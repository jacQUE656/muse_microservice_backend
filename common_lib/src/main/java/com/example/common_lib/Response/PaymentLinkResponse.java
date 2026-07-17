package com.example.common_lib.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentLinkResponse {
    private String paymentOrderId;
    private String paymentLinkUrl;
    private String paymentLinkId;
}
