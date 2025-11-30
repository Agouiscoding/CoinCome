package com.nyu.coincome.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@TableName("PortfolioWeight")
public class PortfolioWeight {
    @TableId("WeightID")
    private Integer WeightID;

    @TableField("PortID")
    private Integer PortID;

    @TableField("CoinID")
    private String CoinID;

    @TableField("Weight")
    private BigDecimal Weight;

    @TableField("UpdatedAt")
    private Timestamp UpdatedAt;
}
