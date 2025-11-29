package com.nyu.coincome.entity.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class TxDTO {
    private Integer txId;
    private Integer userId;

    private Integer exchangeId;
    private String exchangeName;   // from Exchange.ExchangeName

    private Integer coinId;
    private String coinSymbol;     // from Coin.CoinName

    private String txType;         // BUY / SELL
    private BigDecimal quantity;
    private BigDecimal price;

    private Timestamp txTime;
}
