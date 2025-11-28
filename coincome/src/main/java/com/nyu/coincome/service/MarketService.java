package com.nyu.coincome.service;
import java.sql.Timestamp;
public interface MarketService {
    public void upsert(Integer coinId, Double price, Double ret, Timestamp updatedAt);
}
