package com.nyu.coincome.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nyu.coincome.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

    @Select("SELECT COUNT(1) FROM Admin WHERE UserID = #{userId}")
    int countByUserId(@Param("userId") Integer userId);

    default boolean isAdmin(Integer userId) {
        return userId != null && countByUserId(userId) > 0;
    }
}
