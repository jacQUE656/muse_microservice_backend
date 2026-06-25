package com.example.userservice.config;

import com.example.userservice.config.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

@Component
@RequiredArgsConstructor // Good practice: replaces field @Autowired with constructor injection
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1. Bypass check for authentication/health endpoints
        String servletPath = request.getServletPath();
        if (servletPath.contains("/auth") || servletPath.contains("/health") || servletPath.contains("/error")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extract Authorization Header
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        try {
            // 3. Validate token cryptographically using the Public Key (No DB lookup needed!)
            if (this.jwtService.isTokenValid(jwt) && SecurityContextHolder.getContext().getAuthentication() == null) {

                String username = this.jwtService.extractUsername(jwt);

                // 4. Extract the roles directly out of the JWT JSON array payload
                Collection<? extends GrantedAuthority> authorities = this.jwtService.extractAuthorities(jwt);

                // 5. Build the local security context using token claims data
                final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username, // Principal
                        null,     // Credentials (not needed for stateless)
                        authorities
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            // Ensure any parsing/signature failures don't crash the server, just clear context
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}