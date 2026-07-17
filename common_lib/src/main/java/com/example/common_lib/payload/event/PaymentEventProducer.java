package com.example.common_lib.payload.event;

import com.example.common_lib.kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

// PaymentEventProducer.java
@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate; // matches KafkaProducerConfig's <String, Object> bean

    public void publishPaymentSuccess(PaymentSuccessEvent event) {
        kafkaTemplate.send(KafkaTopics.PAYMENT_SUCCESS, event.getUserId(), event);
    }
}
