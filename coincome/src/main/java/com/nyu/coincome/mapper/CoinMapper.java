package com.nyu.coincome.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nyu.coincome.entity.Coin;
import com.nyu.coincome.entity.Users;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CoinMapper extends BaseMapper<Coin> {
    //根据coinId寻找Cg_id
    @Select("SELECT Cg_id FROM Coin WHERE CoinId=#{coinId}")
    String findCgId(Integer coinId);
}
