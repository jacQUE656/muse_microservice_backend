package com.example.paymentservice.repository;

import com.example.paymentservice.model.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentOrder, String> {
    Optional<PaymentOrder> findByProviderReferenceId(String providerReferenceId);
    Optional<PaymentOrder> findByProviderPaymentId(String providerPaymentId);
}