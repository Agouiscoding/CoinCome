package com.nyu.coincome.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nyu.coincome.entity.Coin;
import com.nyu.coincome.entity.Exchange;
import com.nyu.coincome.mapper.CoinMapper;
import com.nyu.coincome.mapper.ExchangeMapper;
import com.nyu.coincome.service.AdminManageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminManageServiceImpl implements AdminManageService {

    private final ExchangeMapper exchangeMapper;
    private final CoinMapper coinMapper;

    public AdminManageServiceImpl(ExchangeMapper exchangeMapper,
                                  CoinMapper coinMapper) {
        this.exchangeMapper = exchangeMapper;
        this.coinMapper = coinMapper;
    }

    // ====== 交易所 ======
    @Override
    public List<Exchange> listExchanges() {
        return exchangeMapper.selectList(
                new QueryWrapper<Exchange>().orderByAsc("ExchangeID")
        );
    }

    @Override
    public void addExchange(String name, String link) {
        Exchange ex = new Exchange();
        ex.setExchangeName(name);
        ex.setExchangeLink(link);
        exchangeMapper.insert(ex);
    }

    // ====== 币种 ======
    @Override
    public List<Coin> listCoins() {
        return coinMapper.selectList(
                new QueryWrapper<Coin>().orderByAsc("CoinID")
        );
    }

    @Override
    public void addCoin(String name, String type) {
        Coin c = new Coin();
        c.setCoinName(name);
        c.setCoinType(type);
        coinMapper.insert(c);
    }
}
