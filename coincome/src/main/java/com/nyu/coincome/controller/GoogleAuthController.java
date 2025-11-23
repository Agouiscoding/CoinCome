package com.nyu.coincome.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.nyu.coincome.entity.Result;
import com.nyu.coincome.entity.Users;
import com.nyu.coincome.entity.dto.GoogleLoginRequest;
import com.nyu.coincome.entity.dto.UserDTO;
import com.nyu.coincome.service.UserService;
import com.nyu.coincome.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class GoogleAuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/google")
    public Result loginWithGoogle(@RequestBody Map<String, String> body) throws Exception {
        String code = body.get("code");
        String GOOGLE_CLIENT_ID=System.getenv("GOOGLE_CLIENT_ID");
        String GOOGLE_CLIENT_SECRET=System.getenv("GOOGLE_CLIENT_SECRET");

        // 1. 用 code 向 Google 换 token
        String tokenUrl = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", GOOGLE_CLIENT_ID);
        params.add("client_secret", GOOGLE_CLIENT_SECRET);
        params.add("redirect_uri", "http://localhost:5173/auth/google/callback");
        params.add("grant_type", "authorization_code");

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> tokenResponse = restTemplate.postForObject(tokenUrl, params, Map.class);

        String idToken = (String) tokenResponse.get("id_token");

        // 2. 解析 id_token，获取 email / name
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
                .Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                .build();

        GoogleIdToken googleIdToken = verifier.verify(idToken);
        GoogleIdToken.Payload payload = googleIdToken.getPayload();

        String email = payload.getEmail();
        String name = (String) payload.get("name");
        log.info("Google 登录用户 email = {}", email);
        log.info("Google 登录用户 name  = {}", name);

        // 3. 登录或注册用户
        Users user = userService.loginWithGoogle(email, name);

        // 4. 给用户生成 JWT token
        String token = jwtUtil.generateToken(String.valueOf(user.getUserId()));
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());

        // 返回 userDTO + token
        Map<String, Object> result = new HashMap<>();
        result.put("user", dto);
        result.put("token", token);

        log.info("test token: {}", result);


        return Result.success(result);
    }


}
