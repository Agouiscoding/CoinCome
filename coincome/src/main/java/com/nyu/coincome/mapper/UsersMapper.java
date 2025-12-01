package com.nyu.coincome.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nyu.coincome.entity.UserCoinAgg;
import com.nyu.coincome.entity.Users;
import com.nyu.coincome.entity.dto.AdminUserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UsersMapper extends BaseMapper<Users> {
    //sign in function, return users from the username.
    @Select("SELECT * FROM Users WHERE Username = #{username}")
    Users findByUsername(String username);


    // 列出所有用户 + 是否 admin
    @Select("""
        SELECT 
            u.UserID      AS userId,
            u.Username    AS username,
            u.Email       AS email,
            u.PasswordHash AS passwordHash,
            u.CreatedAt   AS createdAt,
            u.UpdatedAt   AS updatedAt,
            CASE WHEN a.UserID IS NULL THEN 0 ELSE 1 END AS isAdmin
        FROM Users u
        LEFT JOIN Admin a ON u.UserID = a.UserID
        ORDER BY u.UserID
        """)
    List<AdminUserDTO> findAllWithAdminFlag();

}
