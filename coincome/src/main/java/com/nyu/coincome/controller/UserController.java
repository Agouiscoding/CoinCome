package com.nyu.coincome.controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nyu.coincome.entity.Exchange;
import com.nyu.coincome.entity.ImportBatch;
import com.nyu.coincome.entity.Result;
import com.nyu.coincome.entity.Users;
import com.nyu.coincome.entity.dto.SigninRequest;
import com.nyu.coincome.entity.dto.UserDTO;
import com.nyu.coincome.mapper.ExchangeMapper;
import com.nyu.coincome.mapper.ImportBatchMapper;
import com.nyu.coincome.mapper.UsersMapper;
import com.nyu.coincome.service.UserService;
import com.nyu.coincome.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

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
    private ExchangeMapper exchangeMapper;
    @Autowired
    private ImportBatchMapper importBatchMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;


    //sign in function
    @PostMapping("/signin")
    public Result signin(@RequestBody SigninRequest req){
        log.info("Sign in request：username={}, password={}", req.getUsername(), req.getPassword());
        Users user = userService.signin(req.getUsername(), req.getPassword());
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

}
