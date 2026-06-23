package com.example.common_lib.payload.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UpdateProfileRequest {
   private String firstname;
    private String lastname;
    private String phoneNumber;
}
