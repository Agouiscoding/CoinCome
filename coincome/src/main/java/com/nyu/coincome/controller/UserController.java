package com.nyu.coincome.controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nyu.coincome.entity.Result;
import com.nyu.coincome.entity.Users;
import com.nyu.coincome.entity.dto.SigninRequest;
import com.nyu.coincome.entity.dto.UserDTO;
import com.nyu.coincome.mapper.UsersMapper;
import com.nyu.coincome.service.UserService;
import com.nyu.coincome.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/user")
@Slf4j
public class UserController {
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    //sign in function
    @PostMapping("/signin")
    public Result signin(@RequestBody SigninRequest req){
        log.info("Sign in request：username={}, password={}", req.getUsername(), req.getPassword());
        Users user = userService.signin(req.getUsername(), req.getPassword());
        if (user == null) {
            return Result.error("Invalid username or password.");
        }
        // 登录成功 → 生成 token
        String token = jwtUtil.generateToken(String.valueOf(user.getUserId()));

        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());

        // userDTO + token 返回前端
        Map<String, Object> result = new HashMap<>();
        result.put("user", dto);
        result.put("token", token);

        return Result.success(result);
    }

    @PostMapping("/check-username")
    public Result checkUsername(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        Users user = usersMapper.selectOne(new QueryWrapper<Users>().eq("username", username));
        return Result.success(user != null);
    }

    @PostMapping("/register")
    public Result signup(@RequestBody Users req) {

        // 检查用户名唯一
        Users exists = usersMapper.selectOne(new QueryWrapper<Users>().eq("username", req.getUsername()));
        if (exists != null) {
            return Result.error("Username already exists");
        }

        // 创建新用户
        Users newUser = new Users();
        String username=req.getUsername();
        String email=req.getEmail();
        String password=req.getPasswordHash();
        newUser=userService.signup(username,email,password);
        if (newUser == null) {
            return Result.error("Sign up failed, please try later.");
        }

        return Result.success("Signup success!");
    }

    //test token
//    @GetMapping("/test")
//    public Result test(HttpServletRequest request) {
//        log.info("entry test function");
//
//        Users currentUser = (Users) request.getAttribute("currentUser");
//
//        return Result.success("userID：" + currentUser.getUserId());
//    }
}
