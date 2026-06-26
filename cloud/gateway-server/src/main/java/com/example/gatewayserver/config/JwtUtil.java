package com.example.gatewayserver.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private final PublicKey publicKey;

    public JwtUtil(@Value("${JWT_PUBLIC_KEY_CONTENT:}") String publicKeyEnv) throws Exception {
        this.publicKey = resolvePublicKey(publicKeyEnv, "keys/public.pem");
    }

    private PublicKey resolvePublicKey(String env, String fallbackPath) throws Exception {
        String rawKeyContent;

        if (env != null && !env.isBlank()) {
            rawKeyContent = env;
        } else {
            try (InputStream is = getClass().getClassLoader().getResourceAsStream(fallbackPath)) {
                if (is == null) {
                    throw new IllegalStateException("CRITICAL: Public key not found in env or classpath: " + fallbackPath);
                }
                rawKeyContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        }

        return parsePublicKey(rawKeyContent);
    }

    private PublicKey parsePublicKey(String pemContent) throws Exception {
        String cleanKey = pemContent
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(cleanKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUserId(String token) {
        return extractAllClaims(token).get("userId", String.class);
    }

    /**
     * FIX: Replaced the old extractAuthorities method.
     * Reads the token payload safely as a generic List to match the updated JSON Array format.
     */
    @SuppressWarnings("unchecked")
    public List<String> extractAuthoritiesList(String token) {
        Claims claims = extractAllClaims(token);
        List<String> authorities = claims.get("authorities", List.class);
        return authorities != null ? authorities : Collections.emptyList();
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractAllClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}