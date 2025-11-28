package com.nyu.coincome.service.impl;
import java.sql.Timestamp;

import com.nyu.coincome.entity.MarketData;
import com.nyu.coincome.mapper.MarketDataMapper;
import com.nyu.coincome.mapper.UsersMapper;
import com.nyu.coincome.service.MarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MarketServiceImpl implements MarketService {
    @Autowired
    private MarketDataMapper marketDataMapper;
    @Override
    public void upsert(Integer coinId, Double price, Double ret, Timestamp updatedAt) {

        MarketData existing = marketDataMapper.findByCoinId(coinId);

        if (existing == null) {
            // 插入
            MarketData md = new MarketData();
            md.setCoinId(coinId);
            md.setPrice(price);
            md.setReturnValue(ret);
            md.setIntervalType("minutely");
            md.setUpdatedAt(updatedAt);
            marketDataMapper.insert(md);
        } else {
            // 更新
            existing.setPrice(price);
            existing.setReturnValue(ret);
            existing.setIntervalType("minutely");
            existing.setUpdatedAt(updatedAt);
            marketDataMapper.updateById(existing);
        }
    }
}
