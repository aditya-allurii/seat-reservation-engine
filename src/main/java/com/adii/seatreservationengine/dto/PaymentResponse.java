package com.adii.seatreservationengine.dto;

import com.adii.seatreservationengine.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponse {

    private Long id;
    private BigDecimal amount;
    private PaymentStatus status;
    private String paymentReference;
    private String stripePaymentIntentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long reservationId;
    private String reservationKey;
    private String reservationStatus;

    private Long seatId;
    private String seatNumber;
    private String seatStatus;

    public PaymentResponse() {
    }

    public PaymentResponse(
            Long id,
            BigDecimal amount,
            PaymentStatus status,
            String paymentReference,
            String stripePaymentIntentId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Long reservationId,
            String reservationKey,
            String reservationStatus,
            Long seatId,
            String seatNumber,
            String seatStatus
    ) {
        this.id = id;
        this.amount = amount;
        this.status = status;
        this.paymentReference = paymentReference;
        this.stripePaymentIntentId = stripePaymentIntentId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.reservationId = reservationId;
        this.reservationKey = reservationKey;
        this.reservationStatus = reservationStatus;
        this.seatId = seatId;
        this.seatNumber = seatNumber;
        this.seatStatus = seatStatus;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public String getStripePaymentIntentId() {
        return stripePaymentIntentId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public String getReservationKey() {
        return reservationKey;
    }

    public String getReservationStatus() {
        return reservationStatus;
    }

    public Long getSeatId() {
        return seatId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public String getSeatStatus() {
        return seatStatus;
    }
}