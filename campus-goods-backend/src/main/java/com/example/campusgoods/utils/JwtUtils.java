package com.example.campusgoods.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 手写 JWT 工具（HMAC-SHA256，无第三方依赖）
 * 格式：BASE64URL(header).BASE64URL(payload).BASE64URL(signature)
 * payload 含 uid、role、exp（当前毫秒 + 7 天）
 */
public class JwtUtils {

    /** 固定密钥 */
    private static final String SECRET = "campus-goods-jwt-secret-key-2024";

    /** 有效期：7 天（毫秒） */
    private static final long EXPIRE_MS = 7L * 24 * 60 * 60 * 1000;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JwtUtils() {
    }

    /**
     * 生成 token
     */
    public static String create(Long uid, Integer role) {
        try {
            Map<String, Object> payloadMap = new LinkedHashMap<>();
            payloadMap.put("uid", uid);
            payloadMap.put("role", role);
            payloadMap.put("exp", System.currentTimeMillis() + EXPIRE_MS);
            String header = base64Url("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
            String payload = base64Url(MAPPER.writeValueAsString(payloadMap));
            String data = header + "." + payload;
            return data + "." + sign(data);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解析 token，过期或被篡改返回 null
     */
    public static Payload parse(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }
            String data = parts[0] + "." + parts[1];
            if (!sign(data).equals(parts[2])) {
                return null;
            }
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            Payload payload = MAPPER.readValue(payloadJson, Payload.class);
            if (payload.getUid() == null || payload.getExp() == null
                    || System.currentTimeMillis() > payload.getExp()) {
                return null;
            }
            return payload;
        } catch (Exception e) {
            return null;
        }
    }

    /** HMAC-SHA256 签名 */
    private static String sign(String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    /** BASE64URL 编码（去填充） */
    private static String base64Url(String text) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    /** token 载荷：uid / role / exp */
    @Data
    public static class Payload {
        private Long uid;
        private Integer role;
        private Long exp;
    }
}
