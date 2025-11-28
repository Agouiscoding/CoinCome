package com.nyu.coincome.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nyu.coincome.entity.Coin;
import com.nyu.coincome.entity.Users;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CoinMapper extends BaseMapper<Coin> {
    //根据coinId寻找Cg_id
    @Select("SELECT Cg_id FROM Coin WHERE CoinId=#{coinId}")
    String findCgId(Integer coinId);

    @Select("SELECT CoinID, CoinName, Cg_id, CoinType, UpdatedAt FROM `Coin`")
    @Results(id = "coinMap", value = {
            @Result(column = "CoinID", property = "coinId"),
            @Result(column = "CoinName", property = "coinName"),
            @Result(column = "Cg_id", property = "cgId"),
            @Result(column = "CoinType", property = "coinType"),
            @Result(column = "UpdatedAt", property = "updatedAt")
    })
    List<Coin> findAllCoins();
}
