package com.nyu.coincome.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("Exchange")
public class Exchange {
    @TableId("ExchangeID")
    private Integer exchangeId;

    @TableField("Exchangename")
    private String exchangeName;

    @TableField("ExchangeLink")
    private String exchangeLink;

    @TableField("CreatedAt")
    private Timestamp createdAt;

    @TableField("UpdatedAt")
    private Timestamp updatedAt;
}
