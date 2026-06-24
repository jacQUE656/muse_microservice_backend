package com.example.common_lib.payload.DTO;

import com.example.common_lib.payload.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private UserRole role;
    private String profileImage;

    @JsonProperty("isEmailVerified")
    private boolean isEmailVerified;

}
