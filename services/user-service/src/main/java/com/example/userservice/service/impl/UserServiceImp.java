package com.example.userservice.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.common_lib.msException.BusinessException;
import com.example.common_lib.payload.DTO.UserDTO;
import com.example.common_lib.payload.Request.UpdateProfileRequest;
import com.example.common_lib.payload.enums.ErrorCode;
import com.example.userservice.config.JwtService;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;
    private final Cloudinary cloudinary;
    private final JwtService jwtService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUser() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return UserMapper.toDTOList(users);
    }

    @Override
    @Transactional
    public Boolean deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        userRepository.delete(user);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserFromJwt(String token) {
        String cleanedToken = extractToken(token);
        Claims claims = jwtService.extractClaims(cleanedToken);

        UserDTO dto = new UserDTO();
        dto.setId(claims.get("userId", String.class));
        dto.setEmail(claims.get("email", String.class));

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserProfileFromJwt(String token) {
        String cleanedToken = extractToken(token);
        String email = jwtService.extractUsername(cleanedToken);

        return getUserByEmail(email);
    }

    private UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return UserMapper.toUserDTO(user);
    }

    @Override
    @Transactional
    public UserDTO updateProfile(String id, UpdateProfileRequest request, MultipartFile file) throws IOException {
        // 1. Fetch user or throw structured exception
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 2. Update basic fields from the DTO
        user.setFirstName(request.getFirstname());
        user.setLastName(request.getLastname());
        user.setPhone(request.getPhoneNumber());

        // 3. Handle Cloudinary Upload
        if (file != null && !file.isEmpty()) {
            @SuppressWarnings("unchecked") // Suppress raw type map warning from Cloudinary SDK
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "image",
                            "folder", "muse_profiles",
                            "public_id", "user_" + id
                    ));

            user.setProfileImage(uploadResult.get("secure_url").toString());
        }

        // 4. Persist changes
        userRepository.save(user);

        // 5. Build and return the Response DTO
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .role(user.getRole())
                .isEmailVerified(user.isEmailVerified())
                .profileImage(user.getProfileImage())
                .build();
    }

    // Helper method to keep your JWT methods DRY
    private String extractToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }
}