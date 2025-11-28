package com.nyu.coincome.controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyu.coincome.entity.*;
import com.nyu.coincome.entity.dto.MarketInfo;
import com.nyu.coincome.entity.dto.SigninRequest;
import com.nyu.coincome.entity.dto.UserDTO;
import com.nyu.coincome.mapper.*;
import com.nyu.coincome.service.UserService;
import com.nyu.coincome.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import java.util.Comparator;
import java.io.FileReader;
import java.math.RoundingMode;
import java.io.File;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@RestController
@CrossOrigin
@RequestMapping("/api/user")
@Slf4j
public class UserController {
    @Value("${file.upload-dir}")
    private String uploadPath;

    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private CoinMapper coinMapper;
    @Autowired
    private ExchangeMapper exchangeMapper;
    @Autowired
    private ImportBatchMapper importBatchMapper;
    @Autowired
    private UserCoinAggMapper userCoinAggMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private MarketDataMapper marketDataMapper;


    //sign in function
    @PostMapping("/signin")
    public Result signin(@RequestBody SigninRequest req){
        log.info("Sign in request：username={}, password={}", req.getUsername(), req.getPassword());
        Users user = userService.signin(req.getUsername(), req.getPassword());
        Integer isAdmin;
        if (user == null) {
            return Result.error("Invalid username or password.");
        }
        // 登录成功 → 生成 token
        String token = jwtUtil.generateToken(String.valueOf(user.getUserId()));

        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());
        isAdmin=userService.isAdmin(user.getUserId());
        dto.setIsAdmin(isAdmin);

        // userDTO + token 返回前端
        Map<String, Object> result = new HashMap<>();
        result.put("user", dto);
        result.put("token", token);

        return Result.success(result);
    }

    @PostMapping("/check-username")
    public Result checkUsername(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        Users user = usersMapper.selectOne(new QueryWrapper<Users>().eq("username", username));
        return Result.success(user != null);
    }

    @PostMapping("/register")
    public Result signup(@RequestBody Users req) {

        // 检查用户名唯一
        Users exists = usersMapper.selectOne(new QueryWrapper<Users>().eq("username", req.getUsername()));
        if (exists != null) {
            return Result.error("Username already exists");
        }

        // 创建新用户
        Users newUser = new Users();
        String username=req.getUsername();
        String email=req.getEmail();
        String password=req.getPasswordHash();
        newUser=userService.signup(username,email,password);
        if (newUser == null) {
            return Result.error("Sign up failed, please try later.");
        }

        return Result.success("Signup success!");
    }

    //test token
//    @GetMapping("/test")
//    public Result test(HttpServletRequest request) {
//        log.info("entry test function");
//
//        Users currentUser = (Users) request.getAttribute("currentUser");
//
//        return Result.success("userID：" + currentUser.getUserId());
//    }

    //获取平台
    @GetMapping("/exchangelist")
    public Result exchangelist(){
        List<Exchange> list = exchangeMapper.selectList(null);
        return Result.success(list);
    }

    //文件上传
    public String sha256(MultipartFile file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(file.getBytes());

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
    @PostMapping("/upload")
    public Result upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("exchangeId") Integer exchangeId,
            @RequestParam(value = "note", required = false) String note,
            HttpServletRequest request
    ) {
        try {
            // 1. 校验文件
            if (file.isEmpty()) {
                return Result.error("No file uploaded");
            }
            // 2. 从 Token 中获取 userId
            Users currentUser = (Users) request.getAttribute("currentUser");
            if (currentUser == null) {
                return Result.error("Invalid token");
            }
            Integer userId = currentUser.getUserId();
            // 3. 检查 exchangeId 是否有效
            if (exchangeId == null) {
                return Result.error("No exchange platform selected");
            }
            log.info("User {} selected exchangeId {}", userId, exchangeId);
            log.info("Note: {}", note);

            // 4. 计算 SHA256
            String checksum = sha256(file);
            log.info("Upload file checksum = {}", checksum);

            // 5. 查数据库是否重复
            int count = importBatchMapper.countByChecksum(checksum,userId);
            if (count > 0) {
                return Result.error("Duplicate file: this file has been uploaded before,please contact support if you think this is an error.");
            }

            // 6. 获取 uploads 目录路径
            Path basePath = Paths.get(uploadPath);
            // 7. 按 userId 建自己的目录
            Path userFolder = basePath.resolve(String.valueOf(userId));
            if (!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
            }
            // 8. 生成文件名
            String originalName = StringUtils.cleanPath(file.getOriginalFilename());
            String ext = "";
            int dotIndex = originalName.lastIndexOf(".");
            if (dotIndex != -1) {
                ext = originalName.substring(dotIndex);
            }
            String baseName = (dotIndex != -1)
                    ? originalName.substring(0, dotIndex)
                    : originalName;
            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss")
                    .format(new java.util.Date());
            String fileName = baseName + "_" + timestamp + ext;
            Path filePath = userFolder.resolve(fileName);
            // 9. 保存文件
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("User {} uploaded file saved at {}", userId, filePath);

            // 10. 把记录插入数据库ImportBatch
            ImportBatch batch = new ImportBatch();
            batch.setUserId(userId);
            batch.setExchangeId(exchangeId);
            batch.setFileAddress(filePath.toString());
            batch.setFileChecksumSHA256(checksum);
            batch.setNote(note);
            importBatchMapper.insert(batch);
            Integer batchId = batch.getBatchId();

            // 11. 调用 XML，把 CSV 导入 TradeRaw
            // 先执行初始化 rownum
            importBatchMapper.resetRowNum();
            importBatchMapper.loadTradeRawFromCsv(filePath.toAbsolutePath().toString(), batchId);

            log.info("TradeRaw loaded for batch {}", batchId);

            // 12. TradeRaw → TransactionRecord
            int inserted = importBatchMapper.insertParsedTransactions(batchId);
            log.info("TransactionRecord inserted rows = {}", inserted);

            // 13. 刷新用户聚合表
            importBatchMapper.callRefreshUserCoinAgg(userId);
            log.info("UserCoinAgg refreshed for user {}", userId);

            return Result.success();

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("File upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/assetinfo")
    public Result assetinfo(HttpServletRequest request){
        log.info("entry assetinfo function");
        // 从 Token 中获取 userId
        Users currentUser = (Users) request.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.error("Invalid token");
        }
        Integer userId = currentUser.getUserId();
        List<UserCoinAgg> list = userCoinAggMapper.findUserCoinAgg(userId);

        // compute three values
        BigDecimal totalValue = BigDecimal.ZERO;  //总收益
        BigDecimal totalCost = BigDecimal.ZERO;    //总成本
        BigDecimal realizedPnl = BigDecimal.ZERO;  //总收益百分比
        int coinCount = list.size();  //币的种类数量

        //首先每一种币的三个数据
        for (UserCoinAgg agg : list) {
            BigDecimal qty = agg.getQtyTotal();                   // 币的数量
            BigDecimal avgCost = agg.getWAvgCost();               // 币的平均成本价
            BigDecimal coinRealizedPnl = agg.getRealizedPnlTotal(); // 币的已实现盈亏
            String Cg_id=coinMapper.findCgId(agg.getCoinId());
            //查找当前价格在MarketData里
            Double cp = marketDataMapper.getprice(agg.getCoinId());
            //BigDecimal currentPrice = getCurrentPrice(Cg_id);     // 币的当前市场价格
            BigDecimal currentPrice = BigDecimal.valueOf(cp);
            log.info("CoinId={}, cg_id={}, current price={}",agg.getCoinId(),Cg_id,currentPrice);

            // 1. 成本 = qty * avgCost
            BigDecimal costForThisCoin = qty.multiply(avgCost);
            totalCost = totalCost.add(costForThisCoin);

            // 2. 市值 = qty * currentPrice
            BigDecimal marketValue = qty.multiply(currentPrice);
            totalValue = totalValue.add(marketValue);

            // 3. 已实现盈亏累加
            realizedPnl = realizedPnl.add(coinRealizedPnl);
        }

        // 综合收益率
        BigDecimal returnPct = BigDecimal.ZERO;
        if (totalCost.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal netProfit = totalValue.subtract(totalCost).add(realizedPnl);
            returnPct = netProfit
                    .divide(totalCost, 6, RoundingMode.HALF_UP)   // 保留 6 位小数
                    .multiply(new BigDecimal("100"));
        }

        log.info("totalValue:{}",totalValue);
        log.info("returnPct:{}",returnPct);
        log.info("coinCount:{}",coinCount);

        // 返回前端
        Map<String, Object> result = new HashMap<>();
        result.put("totalValue", totalValue.doubleValue());
        result.put("returnPct", returnPct.doubleValue());
        result.put("coinCount", coinCount);

        return Result.success(result);
    }

    //current market price
    public BigDecimal getCurrentPrice(String cgId) {
        try {
            // 获取项目根路径
            String projectRoot = System.getProperty("user.dir");
            // 拼接 Python 脚本相对路径：projectRoot/python/CurrentPrice.py
            Path scriptPath = Paths.get(System.getProperty("user.dir"))
                    .getParent()  // 上一级
                    .resolve("python")
                    .resolve("CurrentPrice.py");

            ProcessBuilder pb = new ProcessBuilder(
                    "python",
                    scriptPath.toString(),
                    cgId
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String output = reader.readLine(); // Python 输出 JSON
            //System.out.println("Python raw output = " + output);
            process.waitFor();
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> result = mapper.readValue(output, Map.class);

            Number priceNumber = (Number) result.get("price");
            BigDecimal price = new BigDecimal(priceNumber.toString());
            return new BigDecimal(price.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    @GetMapping("/marketinfo")
    public Result marketinfo(HttpServletRequest request){
        log.info("entry marketinfo function");
        // 从 Token 中获取 userId
        Users currentUser = (Users) request.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.error("Invalid token");
        }
        Integer userId = currentUser.getUserId();
        List<Coin> coins = coinMapper.findAllCoins();
        if (coins.isEmpty()) {
            System.err.println("⚠ No coins found in DB.");
            return Result.error("No coins.");
        }
        List<MarketInfo> resultList = new ArrayList<>();
        //遍历每一个coin
        for (Coin coin: coins) {
            //查找当前价格在MarketData里
            Double cp = marketDataMapper.getprice(coin.getCoinId());
            BigDecimal currentPrice = BigDecimal.valueOf(cp);
            BigDecimal change=get24hChange(coin.getCoinName(),currentPrice);
            //在marketData里查找价格更新时间
            Timestamp updatedAt=marketDataMapper.gettime(coin.getCoinId());

            // 4. 加入 resultList
            resultList.add(new MarketInfo(
                    coin.getCoinId(),
                    coin.getCoinName(),
                    coin.getCgId(),
                    coin.getCoinType(),
                    currentPrice,
                    change,
                    updatedAt
            ));
        }
        return Result.success(resultList);

    }
    public BigDecimal get24hChange(String symbol, BigDecimal currentPrice) {
        // 1. 拼出 24h 前的时间
        LocalDateTime target = LocalDateTime.now().minusHours(24);
        String timestamp = target.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH"));
        String fileName = symbol + "_" + timestamp + ".csv";
        // 2. 构造路径
        String baseDir = System.getProperty("user.dir");
        String filePath = baseDir + "/../python/hours/" + symbol + "/" + fileName;
        File csvFile = new File(filePath);
        if (!csvFile.exists()) {
            log.warn("24h CSV not found: {}", filePath);
            return null;
        }
        // 3. 读取过去的价格
        BigDecimal pastPrice = readPriceFromCsv(csvFile);
        if (pastPrice == null) {
            return null;
        }
        // 4. 计算 return%
        return calculate24hReturn(currentPrice, pastPrice);
    }
    public BigDecimal readPriceFromCsv(File csvFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String header = br.readLine(); // 跳过第一行
            String line = br.readLine();
            if (line != null) {
                String[] parts = line.split(",");
                return new BigDecimal(parts[1].trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    BigDecimal calculate24hReturn(BigDecimal currentPrice, BigDecimal pastPrice) {
        if (pastPrice == null || pastPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentPrice.subtract(pastPrice)
                .divide(pastPrice, 6, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    @GetMapping("/daysPicture")
    public Result daysPcture(HttpServletRequest request){
        // 从 Token 中获取 userId
        Users currentUser = (Users) request.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.error("Invalid token");
        }
        Integer userId = currentUser.getUserId();
        //查询所有币
        List<Coin> coins = coinMapper.findAllCoins();
        if (coins.isEmpty()) {
            System.err.println("⚠ No coins found in DB.");
            return Result.error("No coins.");
        }
        List<Map<String, Object>> resultList = new ArrayList<>();
        //遍历每一个coin
        for (Coin coin: coins) {
            String symbol=coin.getCoinName();
            //去文件夹里拿到这个币的历史价格
            String baseDir = System.getProperty("user.dir");
            String filePath = baseDir + "/../python/days/" + symbol + ".csv";
            File csvFile = new File(filePath);
            if (!csvFile.exists()) {
                log.warn("CSV not found: {}", filePath);
                return Result.error("CSV not found");
            }
            try {
                // 读取 CSV
                List<String[]> rows = new ArrayList<>();
                try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                    String line;
                    br.readLine(); // 跳过 header
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split(",");
                        rows.add(parts);
                    }
                }
                // 按日期排序（CSV 中 MarketDate 在第 4 个字段）
                rows.sort((a, b) -> a[4].compareTo(b[4]));
                // 取最近 7 行
                int from = Math.max(0, rows.size() - 7);
                List<String[]> last7 = rows.subList(from, rows.size());
                // 组装成返回数据
                List<String> dates = new ArrayList<>();
                List<Double> prices = new ArrayList<>();
                for (String[] line : last7) {
                    dates.add(line[4]);                 // MarketDate
                    prices.add(Double.parseDouble(line[1])); // Price
                }
                Map<String, Object> item = new HashMap<>();
                item.put("symbol", symbol);
                item.put("dates", dates);
                item.put("prices", prices);
                resultList.add(item);
            } catch (Exception e) {
                log.error("Error reading CSV for {}: {}", symbol, e.getMessage());
                return Result.error("error");
            }
        }
        return Result.success(resultList);
    }

    @GetMapping("/hoursPicture")
    public Result hoursPcture(HttpServletRequest request){
        // 从 Token 中获取 userId
        Users currentUser = (Users) request.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.error("Invalid token");
        }
        Integer userId = currentUser.getUserId();
        //查询所有币
        List<Coin> coins = coinMapper.findAllCoins();
        if (coins.isEmpty()) {
            System.err.println("⚠ No coins found in DB.");
            return Result.error("No coins.");
        }
        List<Map<String, Object>> resultList = new ArrayList<>();
        //遍历每一个coin
        for (Coin coin: coins) {
            String symbol=coin.getCoinName();
            // hours 文件目录：python/hours/BTC/
            String baseDir = System.getProperty("user.dir");
            String folderPath = baseDir + "/../python/hours/" + symbol + "/";
            File folder = new File(folderPath);
            if (!folder.exists() || !folder.isDirectory()) {
                log.warn("Folder not found: {}", folderPath);
                continue;
            }
            // 4. 目标时间范围：过去 48 小时
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime cutoff = now.minusHours(48);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH");
            // 5. 收集匹配文件
            List<File> matchedFiles = new ArrayList<>();
            for (File file : folder.listFiles()) {
                String name = file.getName(); // BTC_2025-11-27-13.csv
                if (!name.startsWith(symbol + "_")) continue;
                try {
                    String ts = name.replace(symbol + "_", "").replace(".csv", "");
                    LocalDateTime fileTime = LocalDateTime.parse(ts, formatter);
                    if (!fileTime.isBefore(cutoff)) {
                        matchedFiles.add(file);
                    }
                } catch (Exception e) {
                    log.warn("Invalid filename: {}", name);
                }
            }
            // 6. 按时间排序
            matchedFiles.sort(Comparator.comparing(f -> {
                String ts = f.getName().replace(symbol + "_", "").replace(".csv", "");
                return LocalDateTime.parse(ts, formatter);
            }));
            // 7. 解析每个 CSV 里的那一行
            List<String> timestamps = new ArrayList<>();
            List<Double> prices = new ArrayList<>();
            for (File csv : matchedFiles) {
                try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
                    br.readLine(); // 跳 header
                    String dataLine = br.readLine(); // 只有一行数据
                    if (dataLine == null) continue;
                    String[] parts = dataLine.split(",");
                    double price = Double.parseDouble(parts[1]);  // Price 在第二列
                    String marketTime = parts[3];
                    timestamps.add(marketTime);
                    prices.add(price);
                } catch (Exception e) {
                    log.error("Failed to read {}", csv.getName());
                }
            }
            // 8. 添加到返回内容
            Map<String, Object> item = new HashMap<>();
            item.put("symbol", symbol);
            item.put("markettime", timestamps);
            item.put("prices", prices);
            resultList.add(item);
        }
        return Result.success(resultList);
    }




}
