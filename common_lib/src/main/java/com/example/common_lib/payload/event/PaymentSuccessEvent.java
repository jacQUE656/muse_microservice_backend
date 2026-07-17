package com.example.common_lib.payload.event;

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
    private String email;      // for EmailService.sendHtml
    private String firstName;  // for template personalization, matches UserRegisteredEvent's pattern
    private String fcmToken;   // for PushNotificationService — may be null/blank, handled the same as elsewhere
    private String planId;
    private String billingCycle;
    private PaymentMethod paymentMethod;
    private Long amount;
    private String currency;
    private Instant paidAt;
}