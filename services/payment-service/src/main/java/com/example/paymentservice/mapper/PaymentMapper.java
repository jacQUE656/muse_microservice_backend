package com.example.paymentservice.mapper;

import com.example.common_lib.payload.DTO.PaymentDto;
import com.example.paymentservice.model.PaymentOrder;
import lombok.*;

@Getter
@Setter
@Builder
public class PaymentMapper {
    public static PaymentDto toPaymentDto(PaymentOrder paymentOrder) {
        if (paymentOrder == null) return null;
        return PaymentDto.builder()
                .id(paymentOrder.getId())
                .paymentMethod(paymentOrder.getPaymentMethod())
                .providerPaymentId(paymentOrder.getProviderPaymentId())
                .providerReferenceId(paymentOrder.getProviderReferenceId())
                .planId(paymentOrder.getPlanId())
                .billingCycle(paymentOrder.getBillingCycle())
                .amount(paymentOrder.getAmount())
                .currency(paymentOrder.getCurrency())
                .userId(paymentOrder.getUserId())
                .createdAt(paymentOrder.getCreatedAt())
                .updatedAt(paymentOrder.getUpdatedAt())
                .version(paymentOrder.getVersion())
                .build();
    }
}
