package com.example.userservice.controller;

import com.example.common_lib.Response.AuthResponse;
import com.example.common_lib.payload.DTO.UserDTO;
import com.example.common_lib.payload.Request.LoginRequest;
import com.example.common_lib.payload.Request.RefreshTokenRequest;
import com.example.common_lib.payload.Request.RegisterRequest;
import com.example.userservice.config.JwtService;
import com.example.userservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Authenticate an existing user and issue an Access Token.
     */
    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signIn(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.signIn(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Register a new application user.
     */
    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signUp(@Valid @RequestBody RegisterRequest registerRequest) {
        UserDTO registeredUser = authService.signUp(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    /**
     * Exchange a valid Refresh Token for a pristine Access Token.
     * Expects a JSON object payload format: { "refreshToken": "eyJhbG..." }
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(@Valid @RequestBody RefreshTokenRequest request) {
        // 1. Recover username tied to payload securely via the DTO getter
        String email = jwtService.extractUsername(request.getRefreshToken());

        // 2. Load latest application authorities granted to the user profile
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        // 3. Re-issue fresh access token complete with authority claims safely mapped
        String newAccessToken = jwtService.refreshAccessToken(request.getRefreshToken(), authorities);

        return ResponseEntity.ok(Map.of(
                "accessToken", newAccessToken,
                "tokenType", "Bearer"
        ));
    }
}