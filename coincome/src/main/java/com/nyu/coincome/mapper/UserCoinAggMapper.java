package com.nyu.coincome.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nyu.coincome.entity.UserCoinAgg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserCoinAggMapper extends BaseMapper<UserCoinAgg> {
    //根据userId返回UserCoinAgg数据
    @Select("SELECT * FROM UserCoinAgg WHERE UserId=#{userId}")
    List<UserCoinAgg> findUserCoinAgg(Integer userId);
}
