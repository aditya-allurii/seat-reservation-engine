package com.adii.seatreservationengine.controller;

import com.adii.seatreservationengine.entity.Payment;
import com.adii.seatreservationengine.service.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class StripeWebhookController {

    private final PaymentService paymentService;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    public StripeWebhookController(
            PaymentService paymentService
    ) {
        this.paymentService = paymentService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature
    ) {

        try {

            // 1. Verify Stripe signature
            Event event = Webhook.constructEvent(
                    payload,
                    signature,
                    webhookSecret
            );

            System.out.println(
                    "STRIPE EVENT TYPE = "
                            + event.getType()
            );

            // 2. Handle successful payment
            if ("payment_intent.succeeded"
                    .equals(event.getType())) {

                PaymentIntent paymentIntent =
                        (PaymentIntent) event
                                .getDataObjectDeserializer()
                                .getObject()
                                .orElseThrow(() ->
                                        new IllegalStateException(
                                                "Unable to deserialize PaymentIntent"
                                        )
                                );

                System.out.println(
                        "STRIPE PAYMENT INTENT ID = "
                                + paymentIntent.getId()
                );

                // 3. Update our database
                Payment payment =
                        paymentService.handleSuccessfulPayment(
                                paymentIntent.getId()
                        );

                System.out.println(
                        "PAYMENT SUCCESSFULLY PROCESSED"
                );

                System.out.println(
                        "OUR PAYMENT ID = "
                                + payment.getId()
                );
            }

            return ResponseEntity.ok(
                    "Webhook processed"
            );

        } catch (SignatureVerificationException e) {

            System.out.println(
                    "INVALID STRIPE WEBHOOK SIGNATURE"
            );

            return ResponseEntity.badRequest()
                    .body("Invalid webhook signature");

        } catch (Exception e) {

            System.out.println(
                    "WEBHOOK PROCESSING ERROR = "
                            + e.getMessage()
            );

            return ResponseEntity.internalServerError()
                    .body("Webhook processing failed");
        }
    }
}