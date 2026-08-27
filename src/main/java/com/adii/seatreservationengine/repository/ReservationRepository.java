package com.adii.seatreservationengine.repository;

import com.adii.seatreservationengine.entity.Reservation;
import com.adii.seatreservationengine.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByReservationKey(
            String reservationKey
    );

    Optional<Reservation> findBySeatIdAndStatus(
            Long seatId,
            ReservationStatus status
    );
}