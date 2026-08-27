package com.adii.seatreservationengine.service;

import com.adii.seatreservationengine.entity.*;
import com.adii.seatreservationengine.exception.SeatNotAvailableException;
import com.adii.seatreservationengine.repository.PaymentRepository;
import com.adii.seatreservationengine.repository.ReservationRepository;
import com.adii.seatreservationengine.repository.SeatRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            ReservationRepository reservationRepository,
            SeatRepository seatRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.seatRepository = seatRepository;
    }

    @Transactional
    public Payment createPayment(
            Long reservationId,
            BigDecimal amount
    ) {

        // 1. Find reservation
        Reservation reservation =
                reservationRepository.findById(reservationId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Reservation not found"
                                )
                        );

        // 2. Reservation must be HELD
        if (reservation.getStatus()
                != ReservationStatus.HELD) {

            throw new IllegalStateException(
                    "Reservation is not active"
            );
        }

        // 3. Check if payment already exists
        if (paymentRepository
                .findByReservationId(reservationId)
                .isPresent()) {

            throw new IllegalStateException(
                    "Payment already exists for this reservation"
            );
        }

        try {

            // Stripe uses the smallest currency unit.
            // ₹500 = 50000 paise.

            long amountInSmallestUnit =
                    amount
                            .multiply(BigDecimal.valueOf(100))
                            .longValueExact();

            // 4. Create Stripe PaymentIntent

            PaymentIntentCreateParams params =
                    PaymentIntentCreateParams.builder()
                            .setAmount(amountInSmallestUnit)
                            .setCurrency("inr")
                            .build();

            PaymentIntent paymentIntent =
                    PaymentIntent.create(params);

            // 5. Create local Payment record

            Payment payment =
                    new Payment();

            payment.setReservation(
                    reservation
            );

            payment.setAmount(
                    amount
            );

            payment.setStatus(
                    PaymentStatus.PENDING
            );

            payment.setStripePaymentIntentId(
                    paymentIntent.getId()
            );

            payment.setCreatedAt(
                    LocalDateTime.now()
            );

            payment.setUpdatedAt(
                    LocalDateTime.now()
            );

            // 6. Save local payment

            return paymentRepository.save(
                    payment
            );

        } catch (StripeException e) {

            throw new RuntimeException(
                    "Stripe payment creation failed",
                    e
            );
        }
    }


    @Transactional
    public Payment handleSuccessfulPayment(
            String stripePaymentIntentId
    ) {

        // 1. Find our payment using Stripe PaymentIntent ID

        Payment payment =
                paymentRepository
                        .findByStripePaymentIntentId(
                                stripePaymentIntentId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Payment not found for Stripe PaymentIntent"
                                )
                        );

        // 2. Idempotency
        // If Stripe sends the same webhook again,
        // don't process the payment twice.

        if (payment.getStatus()
                == PaymentStatus.SUCCESS) {

            return payment;
        }

        // 3. Get reservation

        Reservation reservation =
                payment.getReservation();

        // 4. Reservation must still be HELD

        if (reservation.getStatus()
                != ReservationStatus.HELD) {

            throw new IllegalStateException(
                    "Reservation is no longer active"
            );
        }

        // 5. Get seat

        Seat seat =
                reservation.getSeat();

        // 6. Seat must still be HELD

        if (seat.getStatus()
                != SeatStatus.HELD) {

            throw new SeatNotAvailableException();
        }

        // 7. Payment → SUCCESS

        payment.setStatus(
                PaymentStatus.SUCCESS
        );

        payment.setUpdatedAt(
                LocalDateTime.now()
        );

        // 8. Reservation → CONFIRMED

        reservation.setStatus(
                ReservationStatus.CONFIRMED
        );

        // 9. Seat → BOOKED

        seat.setStatus(
                SeatStatus.BOOKED
        );

        // 10. Remove hold timestamp

        seat.setHeldAt(null);

        // 11. Save everything

        reservationRepository.save(
                reservation
        );

        seatRepository.save(
                seat
        );

        return paymentRepository.save(
                payment
        );
    }
}