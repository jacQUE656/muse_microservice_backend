package com.example.gatewayserver.filter;

import com.example.gatewayserver.config.JwtUtil;
import lombok.extern.slf4j.Slf4j;
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
import java.util.stream.Collectors;

@Slf4j
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
            this.allowedRoles = Arrays.stream(allowedRoles.split(","))
                    .map(String::trim)  // handles accidental spaces in config
                    .collect(Collectors.toList());
        }
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 1. Check for Authorization header
            String authHeader = request.getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Missing or invalid Authorization header — path: {}",
                        request.getURI().getPath());
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);

            try {
                // 2. Validate token expiry
                if (jwtUtil.isTokenExpired(token)) {
                    log.warn("Expired token — path: {}",
                            request.getURI().getPath());
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
                }

                // 3. Extract roles from token
                List<String> authoritiesList =
                        jwtUtil.extractAuthoritiesList(token);

                if (authoritiesList == null || authoritiesList.isEmpty()) {
                    log.warn("Token has no roles — path: {}",
                            request.getURI().getPath());
                    return onError(exchange, HttpStatus.FORBIDDEN);
                }

                // 4. Check role against allowed roles for this route
                if (config.getAllowedRoles() != null
                        && !config.getAllowedRoles().isEmpty()) {

                    boolean hasRequiredRole = config.getAllowedRoles().stream()
                            .anyMatch(authoritiesList::contains);

                    if (!hasRequiredRole) {
                        log.warn("Access denied — user roles: {} required: {} path: {}",
                                authoritiesList,
                                config.getAllowedRoles(),
                                request.getURI().getPath());
                        return onError(exchange, HttpStatus.FORBIDDEN);
                    }
                }

                // 5. Forward user context downstream via headers
                String userId = jwtUtil.extractUserId(token);
                String authoritiesString = String.join(",", authoritiesList);

                ServerHttpRequest mutatedRequest = request.mutate()
                        .header(HttpHeaders.AUTHORIZATION, authHeader)
                        .header("X-User-Id", userId)
                        .header("X-User-Roles", authoritiesString)
                        .build();

                return chain.filter(
                        exchange.mutate().request(mutatedRequest).build());

            } catch (Exception e) {
                log.error("Error processing token — path: {} error: {}",
                        request.getURI().getPath(), e.getMessage());
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