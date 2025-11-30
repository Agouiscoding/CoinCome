package com.nyu.coincome.entity.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
public class UserPortfolioDTO {

    private Integer portId;
    private Integer userId;

    private String riskLevel;           // Conservative / Neutral / Aggressive
    private Timestamp optimizationDate;
    private BigDecimal targetReturn;
    private BigDecimal targetRisk;
    private String note;

    // 当前这个推荐组合里的所有币及其权重
    private List<PortfolioCoinWeightDTO> weights;
}
