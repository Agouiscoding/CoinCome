package com.nyu.coincome.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("Users")
public class Users {

    @TableId("UserID")
    private Integer userId;        // 主键

    @TableField("Username")
    private String username;       // 用户名

    @TableField("Email")
    private String email;          // 邮箱

    @TableField("PasswordHash")
    private String passwordHash;   // 密码哈希

    @TableField("CreatedAt")
    private Timestamp createdAt;   // 创建时间

    @TableField("UpdatedAt")
    private Timestamp updatedAt;   // 更新时间
}