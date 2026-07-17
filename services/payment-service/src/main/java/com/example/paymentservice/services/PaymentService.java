package com.example.paymentservice.services;

import com.example.common_lib.payload.DTO.UserDTO;
import com.example.common_lib.payload.Request.CreatePaymentOrderRequest;
import com.example.common_lib.Response.PaymentLinkResponse;
import com.example.paymentservice.exception.PaymentProcessingException;
import com.example.paymentservice.model.PaymentOrder;

public interface PaymentService {

    PaymentLinkResponse createOrder(
            UserDTO user,
            CreatePaymentOrderRequest request
    ) throws PaymentProcessingException;

    PaymentOrder getPaymentOrderById(String id) throws PaymentProcessingException;

    PaymentOrder getPaymentOrderByPaymentId(String paymentId) throws PaymentProcessingException;

    boolean confirmRazorpayCallback(PaymentOrder paymentOrder, UserDTO user, String razorpayPaymentId, String razorpaySignature) throws PaymentProcessingException;
    boolean confirmStripePayment(PaymentOrder paymentOrder, UserDTO user, String stripeSessionId) throws PaymentProcessingException;
}