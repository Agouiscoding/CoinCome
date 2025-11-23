package com.nyu.coincome.utils;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class JwtUtil {

    private static final String SECRET = "sdf9SDF9sdf90SDF90sdf0SDF90sfd9ASDf09sdf0"; // 至少32字节
    private static final long EXPIRATION = 1000 * 60 * 60 * 24; // 24小时

    // 生成 HMAC-SHA256 签名
    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error signing token", e);
        }
    }

    // 生成 token
    public String generateToken(String userId) {
        long exp = System.currentTimeMillis() + EXPIRATION;

        String header = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));

        String payload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(("{\"sub\":\"" + userId + "\",\"exp\":" + exp + "}")
                        .getBytes(StandardCharsets.UTF_8));

        String signature = sign(header + "." + payload);

        return header + "." + payload + "." + signature;
    }

    // 解析 token
    public String getUserId(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return null;

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);

            // 读取 sub
            String userId = payloadJson.split("\"sub\":\"")[1].split("\"")[0];

            // 读取 exp
            long exp = Long.parseLong(payloadJson.split("\"exp\":")[1].replace("}", ""));

            if (System.currentTimeMillis() > exp) {
                return null; // token过期
            }

            return userId;
        } catch (Exception e) {
            return null;
        }
    }
}
