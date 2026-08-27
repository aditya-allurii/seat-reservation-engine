package com.adii.seatreservationengine.repository;

import com.adii.seatreservationengine.entity.Payment;
import com.adii.seatreservationengine.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    Optional<Payment> findByReservationId(
            Long reservationId
    );

    Optional<Payment> findByPaymentReference(
            String paymentReference
    );

    Optional<Payment> findByReservationIdAndStatus(
            Long reservationId,
            PaymentStatus status
    );

    Optional<Payment> findByStripePaymentIntentId(
            String stripePaymentIntentId
    );
}