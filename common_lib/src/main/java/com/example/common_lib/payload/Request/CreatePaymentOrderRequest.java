package com.example.common_lib.payload.Request;

import com.example.common_lib.payload.enums.PaymentMethod;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentOrderRequest {

    @NotBlank(message = "planId is required")
    private String planId;

    @NotBlank(message = "billingCycle is required")
    @Pattern(regexp = "monthly|annual", message = "billingCycle must be 'monthly' or 'annual'")
    private String billingCycle;

    @NotNull(message = "paymentMethod is required")
    private PaymentMethod paymentMethod;

    // Required only when paymentMethod == CARD; null/ignored for STRIPE and
    // RAZORPAY, which carry no card data of their own (they're redirects).
    private CardPaymentRequest cardDetails;

    @AssertTrue(message = "cardDetails is required when paymentMethod is CARD")
    private boolean isCardDetailsValidForMethod() {
        if (paymentMethod == PaymentMethod.CARD) {
            return cardDetails != null;
        }
        return true;
    }
}