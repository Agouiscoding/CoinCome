package com.nyu.coincome.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.sql.Timestamp;
import java.math.BigDecimal;

@Data
@TableName("UserCoinAgg")
public class UserCoinAgg {
    @TableId("UserID")
    private Integer userId;

    @TableField("CoinID")
    private Integer coinId;

    @TableField("QtyTotal")
    private BigDecimal qtyTotal;   // DECIMAL(38,18)

    @TableField("WAvgCost")
    private BigDecimal wAvgCost;   // DECIMAL(28,10)

    @TableField("RealizedPnLTotal")
    private BigDecimal realizedPnlTotal;  // DECIMAL(38,18)

    @TableField("ReturnPct")
    private BigDecimal returnPct;  // DECIMAL(18,8) - 可为 NULL

    @TableField("UpdatedAt")
    private Timestamp updatedAt;   // 自动更新时间戳
}
