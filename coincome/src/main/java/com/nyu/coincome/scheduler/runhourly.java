package com.nyu.coincome.scheduler;
import com.nyu.coincome.entity.Coin;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyu.coincome.mapper.CoinMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class runhourly implements ApplicationRunner {
    private final CoinMapper coinMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public runhourly(CoinMapper coinMapper) {
        this.coinMapper = coinMapper;
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("Running 48h initial Python fetch...");
        try {
            //从数据库读取 coin 列表
            List<Coin> coins = coinMapper.findAllCoins();
            if (coins.isEmpty()) {
                System.err.println("No coins found in DB.");
                return;
            }

            List<Map<String, Object>> simpleCoinList = coins.stream()
                    .map(c -> {
                        Map<String, Object> m = new HashMap<>();
                        m.put("coin_id", c.getCoinId());      // 或 c.getCoinId()
                        m.put("cg_id", c.getCgId());      // 确认字段是 cgId
                        m.put("symbol", c.getCoinName());   // 确认字段是 symbol
                        return m;
                    })
                    .collect(Collectors.toList());
            //转成 JSON 字符串
            String coinJson = objectMapper.writeValueAsString(simpleCoinList);
            System.out.println("Coin JSON for Python: " + coinJson);

            // 4. Python 脚本路径
            Path scriptPath = Paths.get(System.getProperty("user.dir"))
                    .getParent()
                    .resolve("python")
                    .resolve("CoinHourly.py");

            // 5. 启动 Python 进程
            ProcessBuilder pb = new ProcessBuilder(
                    "python",
                    scriptPath.toString()
            );
            pb.redirectErrorStream(true); // 合并 stderr 和 stdout

            Process process = pb.start();

            // 6. 把 JSON 写入 Python stdin
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(process.getOutputStream())
            )) {
                writer.write(coinJson);
                writer.flush();
            }

            // 7. 读取 Python 输出
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            )) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[Python] " + line);
                }
            }

            int exitCode = process.waitFor();
            System.out.println("Python script exited with code: " + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Python run failed: " + e.getMessage());
        }

        System.out.println("Initial 48h fetch completed.");
    }
}
