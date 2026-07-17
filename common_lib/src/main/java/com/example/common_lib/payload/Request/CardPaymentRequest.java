package com.example.common_lib.payload.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardPaymentRequest {

    @NotBlank(message = "cardToken is required")
    private String cardToken;

    @NotBlank(message = "cardholderName is required")
    private String cardholderName;
}