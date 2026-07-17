package com.example.paymentservice.events;

import com.example.common_lib.payload.enums.PaymentMethod;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSuccessEvent {
    private String paymentOrderId;
    private String userId;
    private String planId;        // "individual" | "duo" | "family"
    private String billingCycle;  // "monthly" | "annual"
    private PaymentMethod paymentMethod;
    private Long amount;
    private String currency;
    private Instant paidAt;
}