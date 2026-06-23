package com.example.common_lib.payload.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PaymentMethod {
    STRIPE,
    RAZORPAY,
    CASH;

    @JsonCreator
    public static PaymentMethod fromString(String value) {
        if (value == null) {
            return null;
        }
        try {
            // Converts incoming string to uppercase to guarantee a match
            return PaymentMethod.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown payment method: " + value);
        }
    }
}