package com.nyu.coincome.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.sql.Timestamp;

@Data
@TableName("Asset")
public class Asset {
    @TableId("AssetID")
    private Integer assetId;

    @TableField("UserID")
    private Integer userId;

    @TableField("ExchangeID")
    private Integer exchangeId;

    @TableField("CoinID")
    private Integer coinId;

    @TableField("Quantity")
    private java.math.BigDecimal quantity;

    @TableField("LastUpdated")
    private Timestamp lastUpdated;

    @TableField("AvgCost")
    private java.math.BigDecimal avgCost;

    @TableField("RealizedPnL")
    private java.math.BigDecimal realizedPnL;
}
