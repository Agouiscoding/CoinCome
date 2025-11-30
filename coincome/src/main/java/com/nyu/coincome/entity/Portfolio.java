package com.nyu.coincome.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@TableName("Portfolio")
public class Portfolio {
    @TableId("PortID")
    private Integer PortID;

    @TableField("UserID")
    private Integer UserID;

    @TableField("RiskLevel")
    private String RiskLevel;

    @TableField("OptimizationDate")
    private Timestamp OptimizationDate;

    @TableField("TargetReturn")
    private BigDecimal TargetReturn;

    @TableField("TargetRisk")
    private BigDecimal TargetRisk;

    @TableField("UpdatedAt")
    private Timestamp UpdatedAt;

    @TableField("Note")
    private String Note;

    public Integer getPortId() {
        return PortID;
    }


    public Integer getUserId() {
        return UserID;
    }
}
