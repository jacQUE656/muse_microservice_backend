package com.example.userservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "processed_payment_event")
public class ProcessedPaymentEvent {

    @Id
    private String paymentOrderId; // same id as PaymentOrder.id — natural key, not generated

    @Builder.Default
    private Instant processedAt = Instant.now();
}