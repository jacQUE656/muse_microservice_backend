package com.example.common_lib.payload.DTO;

import com.example.common_lib.payload.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private UserRole role;
    private String fcmToken;
    private String profileImage;
    private LocalDateTime lastLogin;

    @JsonProperty("isEmailVerified")
    private boolean isEmailVerified;

}
