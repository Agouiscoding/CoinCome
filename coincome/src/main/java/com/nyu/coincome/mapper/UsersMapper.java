package com.nyu.coincome.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nyu.coincome.entity.UserCoinAgg;
import com.nyu.coincome.entity.Users;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UsersMapper extends BaseMapper<Users> {
    //sign in function, return users from the username.
    @Select("SELECT * FROM Users WHERE Username = #{username}")
    Users findByUsername(String username);

    @Select("SELECT COUNT(1) FROM Admin WHERE UserId = #{userId}")
    int isAdmin(int userId);

}
