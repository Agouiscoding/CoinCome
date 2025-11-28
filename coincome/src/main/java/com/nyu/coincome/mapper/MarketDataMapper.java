package com.nyu.coincome.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nyu.coincome.entity.Coin;
import com.nyu.coincome.entity.MarketData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.sql.Timestamp;

@Mapper
public interface MarketDataMapper extends BaseMapper<MarketData> {
    // 查某个币是否已经存在记录
    @Select("SELECT * FROM MarketData WHERE CoinID = #{coinId}")
    MarketData findByCoinId(Integer coinId);

    @Select("SELECT Price FROM MarketData WHERE CoinID=#{coinId}")
    Double getprice(Integer coinId);

    @Select("SELECT UpdatedAt FROM MarketData WHERE CoinID = #{coinId}")
    Timestamp gettime(Integer coinId);
}
