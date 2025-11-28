package com.nyu.coincome.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarketInfo {
    private Integer coinId;
    private String coinName;
    private String cgId;
    private String coinType;
    private BigDecimal price;
    private BigDecimal change;
    private Timestamp updatedAt;
}
