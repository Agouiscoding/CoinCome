package com.nyu.coincome.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nyu.coincome.entity.Portfolio;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PortfolioMapper extends BaseMapper<Portfolio> {

    @Select("""
        SELECT
            PortID   AS portId,
            UserID   AS userId,
            RiskLevel,
            OptimizationDate,
            TargetReturn,
            TargetRisk,
            UpdatedAt,
            Note
        FROM Portfolio
        WHERE UserID = #{userId}
        ORDER BY
            CASE 
                WHEN RiskLevel = 'Conservative' THEN 1
                WHEN RiskLevel = 'Neutral'      THEN 2
                WHEN RiskLevel = 'Aggressive'   THEN 3
                ELSE 4
            END,
            PortID
        """)
    List<Portfolio> findByUserId(Integer userId);
}
