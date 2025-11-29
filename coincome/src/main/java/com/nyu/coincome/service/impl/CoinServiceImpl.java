package com.nyu.coincome.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyu.coincome.entity.Coin;
import com.nyu.coincome.entity.dto.CoinJsonDTO;
import com.nyu.coincome.mapper.CoinMapper;
import com.nyu.coincome.service.CoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CoinServiceImpl implements CoinService {

    private final CoinMapper coinMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ✅ 旧路径版本（可选）
    @Override
    public int importFromJson(String path) throws Exception {
        List<CoinJsonDTO> jsonList = objectMapper.readValue(
                new File(path),
                new TypeReference<List<CoinJsonDTO>>() {}
        );
        return saveCoinList(jsonList);
    }

    // ✅ 新上传文件版本
    @Override
    public int importFromJsonStream(InputStream is) throws Exception {
        List<CoinJsonDTO> jsonList = objectMapper.readValue(
                is,
                new TypeReference<List<CoinJsonDTO>>() {}
        );
        return saveCoinList(jsonList);
    }

    // ✅ 公共逻辑：把 List<CoinJsonDTO> 写入 Coin 表
    private int saveCoinList(List<CoinJsonDTO> jsonList) {
        if (jsonList == null || jsonList.isEmpty()) {
            return 0;
        }

        List<Coin> coinList = new ArrayList<>();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        for (CoinJsonDTO dto : jsonList) {
            if (dto.getId() == null || dto.getSymbol() == null) continue;

            Coin coin = new Coin();
            coin.setCgId(dto.getId());
            coin.setCoinName(dto.getSymbol().toUpperCase()); // symbol → 大写
            coin.setCoinType("Spot");
            coin.setUpdatedAt(now);

            coinList.add(coin);
        }

        int inserted = 0;
        for (Coin c : coinList) {
            try {
                coinMapper.insert(c);  // CoinID 自动递增
                inserted++;
            } catch (Exception e) {
                // 如果有唯一键冲突（重复币），你可以选择忽略
                // log.warn("Insert coin failed: {}", c, e);
            }
        }
        return inserted;
    }
}
