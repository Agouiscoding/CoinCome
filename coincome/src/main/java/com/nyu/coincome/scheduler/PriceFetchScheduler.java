package com.nyu.coincome.scheduler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyu.coincome.mapper.CoinMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.nyu.coincome.entity.Coin;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import com.nyu.coincome.service.MarketService;
import java.sql.Timestamp;
import java.time.OffsetDateTime;

@Component
public class PriceFetchScheduler {
    @Autowired
    private MarketService marketService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CoinMapper coinMapper;

    public PriceFetchScheduler(CoinMapper coinMapper) {
        this.coinMapper = coinMapper;
    }

    @Scheduled(fixedRate = 180_000)
    public void fetchPricesEvery3Minutes() {

        try {
            // 从数据库查询 coin 列表
            List<Coin> coins = coinMapper.findAllCoins();
            if (coins.isEmpty()) {
                System.err.println("⚠ No coins found in DB.");
                return;
            }

            // 生成 cg_id 参数
            List<String> cgIds = coins.stream()
                    .map(Coin::getCgId)
                    .toList();

//            for (Coin c : coins) {
//                System.out.println("Coin: " + c.getCoinName() + " - cgId=" + c.getCgId());
//            }

            System.out.println(" Coins to fetch: " + cgIds);

            // 拼接 Python 路径
            Path scriptPath = Paths.get(System.getProperty("user.dir"))
                    .getParent()
                    .resolve("python")
                    .resolve("minPrice.py");

            // 4️⃣ 构建 Python 命令
            ProcessBuilder pb = new ProcessBuilder();
            pb.command().add("python");
            pb.command().add(scriptPath.toString());
            pb.command().addAll(cgIds);

            pb.redirectErrorStream(true);
            Process process = pb.start();

            // 读取 Python 输出（JSON 数组）
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String json = reader.readLine();
            String line;
            System.out.println("---- Python Output Start ----");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("---- Python Output End ----");

            if (json != null) {
                List<Map<String, Object>> resultList =
                        objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
                System.out.println("Fetched: " + resultList);

                // 遍历每个币的市场数据
                for (Map<String, Object> item : resultList) {

                    String cgId = (String) item.get("cg_id");   // bitcoin / ethereum ...
                    Double price = Double.valueOf(item.get("Price").toString());
                    Double ret = Double.valueOf(item.get("Return").toString());
                    //Timestamp updatedAt = Timestamp.valueOf(item.get("UpdatedAt").toString());
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime ldt = LocalDateTime.parse(item.get("UpdatedAt").toString(), formatter);
                    Timestamp updatedAt = Timestamp.valueOf(ldt);


                    // 找 coinId
                    Integer coinId = coins.stream()
                            .filter(c -> c.getCgId().equals(cgId))
                            .findFirst()
                            .map(Coin::getCoinId)
                            .orElse(null);

                    if (coinId == null) continue;

                    // --- 插入或更新数据库 ---
                    marketService.upsert(coinId, price, ret, updatedAt);
                }
            }

            process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}