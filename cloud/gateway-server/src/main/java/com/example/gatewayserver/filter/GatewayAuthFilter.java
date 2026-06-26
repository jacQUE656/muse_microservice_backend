package com.example.gatewayserver.filter;

import com.example.gatewayserver.config.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class GatewayAuthFilter extends AbstractGatewayFilterFactory<GatewayAuthFilter.Config> {

    private final JwtUtil jwtUtil;

    public GatewayAuthFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("allowedRoles");
    }

    public static class Config {
        private List<String> allowedRoles;

        public List<String> getAllowedRoles() {
            return allowedRoles;
        }

        public void setAllowedRoles(String allowedRoles) {
            // Parses the clean config strings directly (e.g., "ADMIN,USER")
            this.allowedRoles = Arrays.asList(allowedRoles.split(","));
        }
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            // 🚀 BYPASS CHECK
            if (path.startsWith("/api/auth/")) {
                return chain.filter(exchange);
            }

            // 1. Check for Authorization header presence
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);

            try {
                // 2. Validate token expiration
                if (jwtUtil.isTokenExpired(token)) {
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
                }

                // 3. Extract raw authorities string array directly from your JWT payload
                List<String> authoritiesList = jwtUtil.extractAuthoritiesList(token);

                // 🔒 ROLE CHECK VALIDATION (Raw matching without "ROLE_" prefix logic)
                if (config.getAllowedRoles() != null && !config.getAllowedRoles().isEmpty()) {
                    boolean hasRequiredRole = config.getAllowedRoles().stream()
                            .anyMatch(authoritiesList::contains);

                    if (!hasRequiredRole) {
                        return onError(exchange, HttpStatus.FORBIDDEN);
                    }
                }

                String userId = jwtUtil.extractUserId(token);
                String authoritiesString = String.join(",", authoritiesList);

                // 4. Mutate request & forward headers downstream
                ServerHttpRequest mutatedRequest = request.mutate()
                        .header(HttpHeaders.AUTHORIZATION, authHeader)
                        .header("X-User-Id", userId)
                        .header("X-User-Roles", authoritiesString)
                        .build();

                return chain.filter(exchange.mutate().request(mutatedRequest).build());

            } catch (Exception e) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }
}