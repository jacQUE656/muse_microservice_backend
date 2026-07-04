package com.example.userservice.service.impl;

import com.example.common_lib.Response.AuthResponse;
import com.example.common_lib.msException.BusinessException;
import com.example.common_lib.payload.DTO.UserDTO;
import com.example.common_lib.payload.Request.LoginRequest;
import com.example.common_lib.payload.Request.RegisterRequest;
import com.example.common_lib.payload.enums.ErrorCode;
import com.example.common_lib.payload.enums.UserRole;
import com.example.userservice.config.JwtService;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.AuthService;
import com.example.userservice.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.common_lib.kafka.KafkaTopics;
import com.example.common_lib.payload.event.UserRegisteredEvent;
import com.example.common_lib.payload.event.EmailVerificationEvent;
import com.example.common_lib.payload.event.PasswordResetEvent;

import java.time.LocalDateTime;

import static com.example.common_lib.payload.enums.ErrorCode.EMAIL_ALREADY_EXISTS;
import static com.example.common_lib.payload.enums.ErrorCode.PASSWORD_MISMATCH;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
private final CustomUserDetailsService customUserDetailsService;

    private void checkUserEmail(String email) {
        final boolean exists = this.userRepository.existsByEmailIgnoreCase(email);
        if (exists){
            throw new BusinessException(EMAIL_ALREADY_EXISTS);
        }
    }
    private void checkPhoneNumber(String phone) {
        final boolean phoneExists = this.userRepository.existsByPhone(phone);
        if (phoneExists){
            throw new BusinessException(ErrorCode.PHONE_ALREADY_EXISTS);
        }

    }

    @Override
    @Transactional
    public AuthResponse signIn(LoginRequest request) {

        Authentication authentication = authenticate(request.getEmail(), request.getPassword());

        // Update user state tracking
        User user = userRepository.findByEmail(request.getEmail());
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String jwt = jwtService.generateToken(authentication, user.getId());

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setUser(UserMapper.toUserDTO(user));
        authResponse.setTitle("Welcome " + user.getFullName());
        authResponse.setMessage("Login Successful");
        return authResponse;
    }

    private Authentication authenticate(String email, String password) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BusinessException(PASSWORD_MISMATCH);
        }

        // Return fully populated token block with authorities mapped from DB
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    @Override
    @Transactional
    public UserDTO signUp(RegisterRequest req) {
        checkPhoneNumber(req.getPhone());
        checkUserEmail(req.getEmail());
        //BUILD  USER
        User user = User.builder()
                .firstName(req.getFirstname())
                .lastName(req.getLastname())
                .email(req.getEmail())
                .phone(req.getPhone())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(UserRole.USER)
                .build();

        //SEND EMAIL
        userRepository.save(user);
        kafkaTemplate.send(KafkaTopics.USER_REGISTERED,
                UserRegisteredEvent.builder()
                        .userId(user.getId())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .fcmToken(user.getFcmToken())
                        .build());
        return UserMapper.toUserDTO(user);
    }

}
