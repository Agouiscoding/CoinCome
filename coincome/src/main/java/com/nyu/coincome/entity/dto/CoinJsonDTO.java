package com.nyu.coincome.entity.dto;
import lombok.Data;

@Data
public class CoinJsonDTO {
    private String id;      // -> cg_id
    private String symbol;  // -> CoinName (需要大写)
    private String name;    // 不存表，你的表里不需要
}
