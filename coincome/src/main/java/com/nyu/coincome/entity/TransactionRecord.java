package com.nyu.coincome.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@TableName("TransactionRecord")
public class TransactionRecord {
    @TableId("TxID")
    private Integer txId;

    @TableField("UserID")
    private Integer userId;

    @TableField("ExchangeID")
    private Integer exchangeId;

    @TableField("CoinID")
    private Integer coinId;

    @TableField("TxType")
    private String txType;

    @TableField("Quantity")
    private BigDecimal quantity;

    @TableField("Price")
    private BigDecimal price;

    @TableField("`return`")
    private BigDecimal returnValue;

    @TableField("TxTime")
    private Timestamp txTime;

    @TableField("UpdatedAt")
    private Timestamp updatedAt;

    @TableField("PrevAvgCost")
    private BigDecimal prevAvgCost;

    @TableField("PrevQty")
    private BigDecimal prevQty;

    @TableField("CostBasisUnit")
    private BigDecimal costBasisUnit;
}
