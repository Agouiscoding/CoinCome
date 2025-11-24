package com.nyu.coincome.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("ImportBatch")
public class ImportBatch {
    @TableId("BatchID")
    private Integer batchId;

    @TableField("UserID")
    private Integer userId;

    @TableField("ExchangeID")
    private Integer exchangeId;

    @TableField("FileAddress")
    private String fileAddress;

    @TableField("FileChecksumSHA256")
    private String fileChecksumSHA256;

    @TableField("ImportedAt")
    private Timestamp importedAt;

    @TableField("Note")
    private String note;
}
