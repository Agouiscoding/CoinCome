package com.nyu.coincome.mapper;

import com.nyu.coincome.entity.dto.PortfolioCoinWeightDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PortfolioWeightMapper {

    @Select("""
        SELECT
            w.PortID   AS portId,
            w.CoinID   AS coinId,
            c.CoinName AS coinSymbol,
            w.Weight   AS weight
        FROM PortfolioWeight w
        LEFT JOIN Coin c ON w.CoinID = c.CoinID
        WHERE w.PortID = #{portId}
        ORDER BY w.WeightID
        """)
    @Results(id = "portfolioWeightMap", value = {
            @Result(column = "coinId", property = "coinId"),
            @Result(column = "coinSymbol", property = "coinSymbol"),
            @Result(column = "weight", property = "weight")
    })
    List<PortfolioCoinWeightDTO> findWeightsByPortId(Integer portId);
}
