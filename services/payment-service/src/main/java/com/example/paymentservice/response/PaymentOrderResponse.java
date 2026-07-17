package com.example.paymentservice.response;

import com.example.common_lib.payload.enums.PaymentMethod;
import com.example.common_lib.payload.enums.PaymentStatus;
import com.example.paymentservice.model.PaymentOrder;
import lombok.*;

import java.time.Instant;

// What the client gets back after creating or fetching a payment order.
//
// Excludes:
//   - userId    -> the client already knows who it is; no need to echo it back.
//   - version   -> optimistic-locking detail, irrelevant to the frontend.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentOrderResponse {

    private String id;
    private Long amount;
    private String currency;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private String planId;
    private String billingCycle;

    private String providerReferenceId;

    private Instant createdAt;

    public static PaymentOrderResponse fromEntity(PaymentOrder order) {
        return PaymentOrderResponse.builder()
                .id(order.getId())
                .amount(order.getAmount())
                .currency(order.getCurrency())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .planId(order.getPlanId())
                .billingCycle(order.getBillingCycle())
                .providerReferenceId(order.getProviderReferenceId())
                .createdAt(order.getCreatedAt())
                .build();
    }
}