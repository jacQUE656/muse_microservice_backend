package com.example.userservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Gateway handles CORS and CSRF completely
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/health", "/error").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()

                        // Admin routes — must come before /api/users/** and anyRequest()
                        .requestMatchers("/api/admin/users/**").hasAuthority("ADMIN")

                        .requestMatchers("/api/users/**").hasAnyAuthority("PREMIUM_USER", "ADMIN", "USER")
                        .anyRequest().hasAnyAuthority("PREMIUM_USER")
                )

                // Purely stateless downstream microservice
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}