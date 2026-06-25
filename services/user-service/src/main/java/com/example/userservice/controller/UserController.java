package com.example.userservice.controller;

import com.example.common_lib.payload.DTO.UserDTO;
import com.example.common_lib.payload.Request.UpdateProfileRequest;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Retrieve the basic User DTO properties (ID, Email) embedded directly within the active token claims.
     */
    @GetMapping("/me/claims")
    public ResponseEntity<UserDTO> getUserClaimsFromJwt(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        UserDTO userDto = userService.getUserFromJwt(token);
        return ResponseEntity.ok(userDto);
    }

    /**
     * Retrieve the fully detailed User database profile context mapped via the email found inside the token.
     */
    @GetMapping("/me/profile")
    public ResponseEntity<UserDTO> getUserProfileFromJwt(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        UserDTO userProfile = userService.getUserProfileFromJwt(token);
        return ResponseEntity.ok(userProfile);
    }

    /**
     * Update profile details and upload an optional multipart avatar picture block.
     * Consumes: MULTIPART_FORM_DATA_VALUE because a raw file binary is transmitted along with fields.
     */
    @PutMapping(value = "/profile/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<UserDTO> updateProfile(
            @PathVariable String id,
            @Valid @RequestPart("request") UpdateProfileRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {
        UserDTO updatedProfile = userService.updateProfile(id, request, file);
        return ResponseEntity.ok(updatedProfile);
    }
}