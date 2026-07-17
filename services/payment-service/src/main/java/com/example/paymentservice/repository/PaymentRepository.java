package com.example.paymentservice.repository;

import com.example.paymentservice.model.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentOrder , String> {
    Optional<PaymentOrder> findByPaymentLinkId(String paymentLinkId);
    Optional<PaymentOrder> findByProviderPaymentId(String providerPaymentId);
}
