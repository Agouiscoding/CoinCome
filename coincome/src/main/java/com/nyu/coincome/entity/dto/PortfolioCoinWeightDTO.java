package com.nyu.coincome.entity.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PortfolioCoinWeightDTO {
    private Integer coinId;
    private String coinSymbol;   // 来自 Coin.CoinName
    private BigDecimal weight;
}
