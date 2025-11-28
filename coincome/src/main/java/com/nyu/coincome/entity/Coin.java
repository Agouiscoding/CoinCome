package com.nyu.coincome.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName(value = "`Coin`")
public class Coin {
    @TableId("CoinID")
    private Integer coinId;

    @TableField("CoinName")
    private String coinName;

    @TableField(value = "Cg_id")
    private String cgId;

    @TableField(value = "CoinType")
    private String coinType;

    @TableField("UpdatedAt")
    private Timestamp updatedAt;
}
