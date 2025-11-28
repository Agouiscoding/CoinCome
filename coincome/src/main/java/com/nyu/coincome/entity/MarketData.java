package com.nyu.coincome.entity;
import java.sql.Timestamp;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("MarketData")
public class MarketData {
    @TableId("MarketDataId")
    private Integer marketDataId;

    @TableField("CoinID")
    private Integer coinId;

    @TableField("Price")
    private Double price;

    @TableField(value = "`Return`")
    private Double returnValue;

    @TableField("IntervalType")
    private String intervalType;

    @TableField("UpdatedAt")
    private Timestamp updatedAt;
}
