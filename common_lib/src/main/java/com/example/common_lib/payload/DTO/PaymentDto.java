package com.example.common_lib.payload.DTO;

import com.example.common_lib.payload.enums.PaymentMethod;
import com.example.common_lib.payload.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {
    private String id;
    private Long amount;
    private String currency;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private String providerReferenceId;
    private String providerPaymentId;
    private String userId;
    private String planId;
    private String billingCycle;
    private Long version;
    private Instant createdAt;
    private Instant updatedAt;

}
