package com.nyu.coincome.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("Admin")
public class Admin {

    @TableId("UserID")
    private Integer userId;        // 主键

    @TableField("CreatedAt")
    private Timestamp createdAt;   // 创建时间
}