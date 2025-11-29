package com.nyu.coincome.service;

import com.nyu.coincome.entity.Coin;
import com.nyu.coincome.entity.Exchange;

import java.util.List;

public interface AdminManageService {

    // 交易所相关
    List<Exchange> listExchanges();

    void addExchange(String name, String link);

    // 币种相关
    List<Coin> listCoins();

    void addCoin(String name, String type);
}
