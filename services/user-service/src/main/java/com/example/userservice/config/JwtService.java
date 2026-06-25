package com.example.userservice.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JwtService {
    private static final String TOKEN_TYPE = "token_type";
    private static final String AUTHORITIES_CLAIM = "authorities";
    private static final String USER_ID_CLAIM = "userId";
    private static final String EMAIL_CLAIM = "email";

    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtService(
            @Value("${JWT_PRIVATE_KEY_CONTENT:}") String privateKeyEnv,
            @Value("${JWT_PUBLIC_KEY_CONTENT:}") String publicKeyEnv,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) throws Exception {
        this.privateKey = resolveKey(true, "keys/private.pem", privateKeyEnv);
        this.publicKey = resolveKey(false, "keys/public.pem", publicKeyEnv);
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    @SuppressWarnings("unchecked")
    private <T> T resolveKey(boolean isPrivate, String path, String env) throws Exception {
        if (env != null && !env.isBlank()) {
            return isPrivate ? (T) KeyUtils.parsePrivateKey(env) : (T) KeyUtils.parsePublicKey(env);
        }
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) throw new Exception("Key file not found in resources: " + path);
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return isPrivate ? (T) KeyUtils.parsePrivateKey(content) : (T) KeyUtils.parsePublicKey(content);
        }
    }

    private String buildToken(String username, Map<String, Object> claims, long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(this.privateKey)
                .compact();
    }

    private List<String> getAuthoritiesList(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    public String generateToken(Authentication auth, String userId) {
        List<String> roles = getAuthoritiesList(auth.getAuthorities());

        Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_TYPE, "ACCESS_TOKEN");
        claims.put(AUTHORITIES_CLAIM, roles);
        claims.put(USER_ID_CLAIM, userId);
        claims.put(EMAIL_CLAIM, auth.getName());

        return buildToken(auth.getName(), claims, this.accessTokenExpiration);
    }

    public String generateRefreshToken(Authentication auth, String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_TYPE, "REFRESH_TOKEN");
        claims.put(USER_ID_CLAIM, userId); // Crucial to keep track of who owns this refresh token

        return buildToken(auth.getName(), claims, this.refreshTokenExpiration);
    }

    public boolean isTokenValid(final String token) {
        try {
            Claims claims = extractClaims(token);
            return "ACCESS_TOKEN".equals(claims.get(TOKEN_TYPE));
        } catch (JwtException | IllegalArgumentException e) {
            // Catches ExpiredJwtException, SignatureException, MalformedJwtException, etc.
            return false;
        }
    }

    public String extractUsername(final String token) {
        return extractClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public Collection<? extends GrantedAuthority> extractAuthorities(String token) {
        Claims claims = extractClaims(token);
        List<String> authorities = claims.get(AUTHORITIES_CLAIM, List.class);
        if (authorities == null) return Collections.emptyList();

        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public Claims extractClaims(final String token) {
        try {
            return Jwts.parser()
                    .verifyWith(this.publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw e; // Rethrow directly to let callers inspect expiration explicitly if needed
        } catch (final JwtException e) {
            throw new RuntimeException("Invalid JWT token signature or payload", e);
        }
    }

    @SuppressWarnings("unchecked")
    public String refreshAccessToken(String refreshToken, Collection<? extends GrantedAuthority> authorities) {
        try {
            Claims claims = extractClaims(refreshToken);

            if ("REFRESH_TOKEN".equals(claims.get(TOKEN_TYPE))) {
                String username = claims.getSubject();
                String userId = claims.get(USER_ID_CLAIM, String.class);

                // Re-build full claims structure so downstream operations in UserServiceImp don't NPE
                Map<String, Object> newClaims = new HashMap<>();
                newClaims.put(TOKEN_TYPE, "ACCESS_TOKEN");
                newClaims.put(USER_ID_CLAIM, userId);
                newClaims.put(EMAIL_CLAIM, username);
                newClaims.put(AUTHORITIES_CLAIM, getAuthoritiesList(authorities));

                return buildToken(username, newClaims, this.accessTokenExpiration);
            }
            throw new RuntimeException("Provided token is not a valid refresh token type");
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Refresh token has expired. Please log in again.", e);
        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token processing status", e);
        }
    }
}