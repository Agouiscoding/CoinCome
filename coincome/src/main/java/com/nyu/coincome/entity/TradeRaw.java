package com.nyu.coincome.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("TradeRaw")
public class TradeRaw {
    @TableId("RawID")
    private Integer rawId;

    @TableField("BatchID")
    private Integer batchId;

    @TableField("RowNum")
    private Integer rowNum;

    @TableField("RawPayload")
    private String rawPayload;

    @TableField("ParsedAt")
    private Timestamp parsedAt;

    @TableField("Status")
    private String status;

    @TableField("Errormsg")
    private String errormsg;
}
