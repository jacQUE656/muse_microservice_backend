package com.example.userservice.kafka;

import com.example.common_lib.kafka.KafkaTopics;
import com.example.common_lib.payload.enums.UserRole;
import com.example.common_lib.payload.event.PaymentSuccessEvent;
import com.example.userservice.model.ProcessedPaymentEvent;
import com.example.userservice.model.User;
import com.example.userservice.repository.ProcessedPaymentEventRepository;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentSuccessConsumer {

    private final UserRepository userRepository;
    private final ProcessedPaymentEventRepository processedPaymentEventRepository;

    @KafkaListener(
            topics = KafkaTopics.PAYMENT_SUCCESS,
            groupId = "user-service", // distinct group from "notification-service" — Kafka delivers a full copy of every message to each group, so both consumers fire independently off the same topic
            containerFactory = "paymentSuccessFactory") // see note below — this bean doesn't exist yet in your shown KafkaConsumerConfig
    @Transactional
    public void onPaymentSuccess(PaymentSuccessEvent event) {
        if (processedPaymentEventRepository.existsById(event.getPaymentOrderId())) {
            log.info("Skipping already-processed payment event: {}", event.getPaymentOrderId());
            return;
        }

        User user = userRepository.findById(event.getUserId())
                .orElseThrow(() -> new IllegalStateException(
                        "User not found for premium upgrade: " + event.getUserId()));

        user.setRole(UserRole.PREMIUM_USER);
        user.setPremiumPlan(event.getPlanId());
        user.setPremiumExpiresAt(computeExpiry(user.getPremiumExpiresAt(), event.getBillingCycle()));
        userRepository.save(user);

        processedPaymentEventRepository.save(
                ProcessedPaymentEvent.builder().paymentOrderId(event.getPaymentOrderId()).build());

        log.info("User {} upgraded to {} until {}", user.getId(), UserRole.PREMIUM_USER, user.getPremiumExpiresAt());
    }

    private Instant computeExpiry(Instant currentExpiry, String billingCycle) {
        Instant base = (currentExpiry != null && currentExpiry.isAfter(Instant.now())) ? currentExpiry : Instant.now();
        return "annual".equals(billingCycle) ? base.plus(365, ChronoUnit.DAYS) : base.plus(30, ChronoUnit.DAYS);
    }
}