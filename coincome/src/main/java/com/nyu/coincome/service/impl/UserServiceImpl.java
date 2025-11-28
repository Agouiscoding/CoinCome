package com.nyu.coincome.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nyu.coincome.entity.Users;
import com.nyu.coincome.mapper.UsersMapper;
import com.nyu.coincome.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UsersMapper usersMapper;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    //sign in function
    @Override
    public Users signin(String username, String rawPassword) {
        Users user = usersMapper.findByUsername(username);
        if (user == null) {
            return null; // user not exists
        }
        // compare the password(BCrypt)
        boolean match = BCrypt.checkpw(rawPassword, user.getPasswordHash());
        if (!match) {
            return null; // wrong password
        }
        return user; // sign in success
    }

    @Override
    public Integer isAdmin(int userId) {
        return usersMapper.isAdmin(userId);
    }

    //log in or sign up with google
    @Override
    public Users loginWithGoogle(String email, String name) {
        // 根据 email 查找用户
        LambdaQueryWrapper<Users> query = new LambdaQueryWrapper<>();
        query.eq(Users::getEmail, email);
        Users user = usersMapper.selectOne(query);

        if (user == null) {
            // 创建新用户
            user = new Users();
            user.setUsername(name);
            user.setEmail(email);
            user.setPasswordHash("GOOGLE_USER"); // 不需要密码
            usersMapper.insert(user);
        }

        return user;
    }

    //sign up function
    @Override
    public Users signup(String username, String email, String rawPassword) {
        String hashed = encoder.encode(rawPassword);
        Users newUser = new Users();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPasswordHash(hashed);
        usersMapper.insert(newUser);
        return newUser;
    }
}
