package com.example.paymentservice.controller;

import com.example.common_lib.msException.BusinessException;
import com.example.common_lib.payload.DTO.UserDTO;
import com.example.common_lib.payload.Request.CreatePaymentOrderRequest;
import com.example.common_lib.Response.PaymentLinkResponse;
import com.example.common_lib.payload.enums.ErrorCode;
import com.example.paymentservice.exception.PaymentProcessingException;
import com.example.paymentservice.model.PaymentOrder;
import com.example.paymentservice.response.PaymentOrderResponse;
import com.example.paymentservice.services.PaymentService;
import com.example.paymentservice.services.client.UserFeignClient;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final UserFeignClient userFeignClient;

    @PostMapping("/orders")
    public ResponseEntity<PaymentLinkResponse> createOrder(
            @Valid @RequestBody CreatePaymentOrderRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token
    ) throws PaymentProcessingException {
        UserDTO user = resolveCurrentUser(token);
        PaymentLinkResponse response = paymentService.createOrder(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<PaymentOrderResponse> getOrderById(
            @PathVariable String id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token
    ) throws PaymentProcessingException {
        PaymentOrder order = paymentService.getPaymentOrderById(id);
        UserDTO user = resolveCurrentUser(token);
        assertOwnership(order, user);
        return ResponseEntity.ok(PaymentOrderResponse.fromEntity(order));
    }

    @GetMapping("/orders/payment/{paymentId}")
    public ResponseEntity<PaymentOrderResponse> getOrderByPaymentId(
            @PathVariable String paymentId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token
    ) throws PaymentProcessingException {
        PaymentOrder order = paymentService.getPaymentOrderByPaymentId(paymentId);
        UserDTO user = resolveCurrentUser(token);
        assertOwnership(order, user);
        return ResponseEntity.ok(PaymentOrderResponse.fromEntity(order));
    }

    @PostMapping("/orders/{orderId}/confirm/razorpay")
    public ResponseEntity<PaymentOrderResponse> confirmRazorpay(
            @PathVariable String orderId,
            @RequestParam("razorpay_payment_id") String paymentId,
            @RequestParam("razorpay_signature") String signature,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token
    ) throws PaymentProcessingException {
        PaymentOrder order = paymentService.getPaymentOrderById(orderId);
        UserDTO user = resolveCurrentUser(token);
        assertOwnership(order, user);

        boolean confirmed = paymentService.confirmRazorpayCallback(order, user, paymentId, signature);
        if (!confirmed) {
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                    .body(PaymentOrderResponse.fromEntity(order));
        }
        return ResponseEntity.ok(PaymentOrderResponse.fromEntity(order));
    }
    @PostMapping("/orders/{orderId}/confirm/stripe")
    public ResponseEntity<PaymentOrderResponse> confirmStripe(
            @PathVariable String orderId,
            @RequestParam("sessionId") String sessionId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token
    ) throws PaymentProcessingException {
        PaymentOrder order = paymentService.getPaymentOrderById(orderId);
        UserDTO user = resolveCurrentUser(token);
        assertOwnership(order, user);

        boolean confirmed = paymentService.confirmStripePayment(order, user, sessionId);
        if (!confirmed) {
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                    .body(PaymentOrderResponse.fromEntity(order));
        }
        return ResponseEntity.ok(PaymentOrderResponse.fromEntity(order));
    }

    private UserDTO resolveCurrentUser(String token) {
        return userFeignClient.getUserProfileFromJwt(token).getBody();
    }

    private void assertOwnership(PaymentOrder order, UserDTO user) {
        if (!order.getUserId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }
}