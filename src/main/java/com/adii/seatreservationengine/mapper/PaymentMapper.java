package com.adii.seatreservationengine.mapper;

import com.adii.seatreservationengine.dto.PaymentResponse;
import com.adii.seatreservationengine.entity.Payment;
import com.adii.seatreservationengine.entity.Reservation;
import com.adii.seatreservationengine.entity.Seat;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment) {

        Reservation reservation =
                payment.getReservation();

        Seat seat =
                reservation.getSeat();

        return new PaymentResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getPaymentReference(),
                payment.getStripePaymentIntentId(),
                payment.getCreatedAt(),
                payment.getUpdatedAt(),

                reservation.getId(),
                reservation.getReservationKey(),
                reservation.getStatus().name(),

                seat.getId(),
                seat.getSeatNumber(),
                seat.getStatus().name()
        );
    }
}