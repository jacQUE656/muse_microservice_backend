package com.example.paymentservice.services.impl;

import com.example.common_lib.Response.PaymentLinkResponse;
import com.example.common_lib.payload.DTO.UserDTO;
import com.example.common_lib.payload.Request.CreatePaymentOrderRequest;
import com.example.common_lib.payload.enums.PaymentStatus;
import com.example.common_lib.payload.event.PaymentEventProducer;
import com.example.common_lib.payload.event.PaymentSuccessEvent;
import com.example.paymentservice.exception.PaymentProcessingException;
import com.example.paymentservice.model.PaymentOrder;
import com.example.paymentservice.repository.PaymentRepository;
import com.example.paymentservice.services.PaymentService;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer paymentEventProducer;
    private final RazorpayClient razorpayClient;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${razorpay.api.secret}")
    private String razorpayKeySecret;

    @Value("${app.frontend.base-url:http://localhost:5173}")
    private String frontendBaseUrl;

    private static final Map<String, Long> MONTHLY_PRICE_CENTS = Map.of(
            "individual", 999L,
            "duo", 1299L,
            "family", 1699L
    );

    @PostConstruct
    private void initStripe() {
        Stripe.apiKey = stripeApiKey;
    }

    private long resolveAmount(String planId, String billingCycle) throws PaymentProcessingException {
        Long monthly = MONTHLY_PRICE_CENTS.get(planId);
        if (monthly == null) {
            throw new PaymentProcessingException("Unknown planId: " + planId);
        }
        if ("annual".equals(billingCycle)) {
            return Math.round(monthly * 12 * 0.8);
        }
        return monthly;
    }

    @Override
    public PaymentLinkResponse createOrder(UserDTO user, CreatePaymentOrderRequest request) throws PaymentProcessingException {

        long amount = resolveAmount(request.getPlanId(), request.getBillingCycle());

        PaymentOrder order = PaymentOrder.builder()
                .amount(amount)
                .paymentMethod(request.getPaymentMethod())
                .userId(user.getId())
                .planId(request.getPlanId())
                .billingCycle(request.getBillingCycle())
                .build(); // currency defaults to "USD" via @Builder.Default
        order = paymentRepository.save(order);

        try {
            return switch (request.getPaymentMethod()) {
                case RAZORPAY -> {
                    PaymentLink link = createRazorPayPaymentLink(user, amount, order.getCurrency(), order.getId());
                    order.setProviderReferenceId(link.get("id"));
                    paymentRepository.save(order);
                    yield PaymentLinkResponse.builder()
                            .paymentOrderId(order.getId())
                            .paymentLinkUrl(link.get("short_url"))
                            .paymentLinkId(link.get("id"))
                            .build();
                }
                case STRIPE -> {
                    Session session = createStripeCheckoutSession(user, amount, order.getCurrency(), order.getId());
                    // Store the SESSION ID here, not the URL — confirmStripePayment
                    // needs to call Session.retrieve(id), which fails on a URL.
                    order.setProviderReferenceId(session.getId());
                    paymentRepository.save(order);
                    yield PaymentLinkResponse.builder()
                            .paymentOrderId(order.getId())
                            .paymentLinkUrl(session.getUrl())
                            .paymentLinkId(session.getId())
                            .build();
                }
                case CARD -> throw new PaymentProcessingException(
                        "CARD payment method is not yet implemented — requires a tokenization provider decision"
                );
            };
        } catch (RazorpayException | StripeException e) {
            order.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(order);

            throw new PaymentProcessingException("Failed to create payment link" + e);
        } catch (PaymentProcessingException e) {
            order.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(order);
            throw e;
        }
    }

    private PaymentLink createRazorPayPaymentLink(UserDTO user, Long amount, String currency, String orderId) throws RazorpayException {

        JSONObject paymentLinkRequest = new JSONObject();
        paymentLinkRequest.put("amount", amount);
        paymentLinkRequest.put("currency", currency);
        paymentLinkRequest.put("reference_id", orderId);

        JSONObject customer = new JSONObject();
        customer.put("name", user.getFullName());
        customer.put("email", user.getEmail());
        paymentLinkRequest.put("customer", customer);

        JSONObject notify = new JSONObject();
        notify.put("email", true);
        paymentLinkRequest.put("notify", notify);

        paymentLinkRequest.put("reminder_enable", true);

        paymentLinkRequest.put("callback_url", frontendBaseUrl + "/payment-success?orderId=" + orderId);
        paymentLinkRequest.put("callback_method", "get");

        return razorpayClient.paymentLink.create(paymentLinkRequest);
    }

    private Session createStripeCheckoutSession(UserDTO user, Long amount, String currency, String orderId) throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setCustomerEmail(user.getEmail())
                .setSuccessUrl(frontendBaseUrl + "/payment-success?orderId=" + orderId)
                .setCancelUrl(frontendBaseUrl + "/payment/cancel?orderId=" + orderId)
                .putMetadata("orderId", orderId)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(currency.toLowerCase())
                                .setUnitAmount(amount) // already smallest unit — no further scaling
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Subscription")
                                        .build())
                                .build())
                        .build())
                .build();
        return Session.create(params);
    }

    @Override
    public PaymentOrder getPaymentOrderById(String id) throws PaymentProcessingException {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentProcessingException("PaymentOrder not found: " + id));
    }

    @Override
    public PaymentOrder getPaymentOrderByPaymentId(String paymentId) throws PaymentProcessingException {
        return paymentRepository.findByProviderPaymentId(paymentId)
                .orElseThrow(() -> new PaymentProcessingException("PaymentOrder not found for paymentId: " + paymentId));
    }

    // PaymentServiceImpl.java — relevant changes only
    @Override
    public boolean confirmRazorpayCallback(PaymentOrder paymentOrder, UserDTO user, String razorpayPaymentId, String razorpaySignature) throws PaymentProcessingException {
        boolean verified = verifyRazorpaySignature(paymentOrder, razorpayPaymentId, razorpaySignature);
        return finalizeConfirmation(paymentOrder, user, verified, razorpayPaymentId);
    }

    @Override
    public boolean confirmStripePayment(PaymentOrder paymentOrder, UserDTO user, String stripeSessionId) throws PaymentProcessingException {
        boolean verified = verifyStripeSession(paymentOrder, stripeSessionId);
        return finalizeConfirmation(paymentOrder, user, verified, stripeSessionId);
    }

    private boolean finalizeConfirmation(PaymentOrder paymentOrder, UserDTO user, boolean verified, String providerPaymentId) {
        if (!verified) {
            paymentOrder.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(paymentOrder);
            return false;
        }

        paymentOrder.setStatus(PaymentStatus.SUCCESS);
        paymentOrder.setProviderPaymentId(providerPaymentId);
        paymentRepository.save(paymentOrder);

        paymentEventProducer.publishPaymentSuccess(
                PaymentSuccessEvent.builder()
                        .paymentOrderId(paymentOrder.getId())
                        .userId(paymentOrder.getUserId())
                        .email(user.getEmail())
                        .firstName(user.getFirstName()) // ASSUMPTION: UserDTO has getFirstName() — adjust to whatever field it actually exposes
                        .fcmToken(user.getFcmToken())   // ASSUMPTION: same — confirm UserDTO actually carries this
                        .planId(paymentOrder.getPlanId())
                        .billingCycle(paymentOrder.getBillingCycle())
                        .paymentMethod(paymentOrder.getPaymentMethod())
                        .amount(paymentOrder.getAmount())
                        .currency(paymentOrder.getCurrency())
                        .paidAt(Instant.now())
                        .build()
        );
        return true;
    }
    private boolean verifyRazorpaySignature(PaymentOrder order, String paymentId, String signature) throws PaymentProcessingException {
        String payload = String.join("|",
                order.getProviderReferenceId(),
                order.getId(),
                "paid",
                paymentId);

        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(razorpayKeySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] rawHmac = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String expectedSignature = HexFormat.of().formatHex(rawHmac);

            return MessageDigest.isEqual(
                    expectedSignature.getBytes(StandardCharsets.UTF_8),
                    signature.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new PaymentProcessingException("Failed to verify Razorpay signature" + e);
        }
    }

    private boolean verifyStripeSession(PaymentOrder order, String sessionId) throws PaymentProcessingException {
        try {
            Session session = Session.retrieve(sessionId);
            String boundOrderId = session.getMetadata().get("orderId");
            if (!order.getId().equals(boundOrderId)) {
                return false;
            }

            return "paid".equals(session.getPaymentStatus());
        } catch (StripeException e) {
            throw new PaymentProcessingException("Failed to verify Stripe session" + e);
        }
    }
}