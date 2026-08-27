package com.adii.seatreservationengine.controller;

import com.adii.seatreservationengine.dto.PaymentResponse;
import com.adii.seatreservationengine.entity.Payment;
import com.adii.seatreservationengine.mapper.PaymentMapper;
import com.adii.seatreservationengine.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    public PaymentController(
            PaymentService paymentService,
            PaymentMapper paymentMapper
    ) {
        this.paymentService = paymentService;
        this.paymentMapper = paymentMapper;
    }

    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestParam Long reservationId,
            @RequestParam BigDecimal amount
    ) {

        Payment payment =
                paymentService.createPayment(
                        reservationId,
                        amount
                );

        PaymentResponse response =
                paymentMapper.toResponse(payment);

        return ResponseEntity.ok(response);
    }
}