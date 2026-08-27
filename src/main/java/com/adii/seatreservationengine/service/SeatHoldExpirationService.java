package com.adii.seatreservationengine.service;

import com.adii.seatreservationengine.entity.Reservation;
import com.adii.seatreservationengine.entity.ReservationStatus;
import com.adii.seatreservationengine.entity.Seat;
import com.adii.seatreservationengine.entity.SeatStatus;
import com.adii.seatreservationengine.repository.ReservationRepository;
import com.adii.seatreservationengine.repository.SeatRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SeatHoldExpirationService {

    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    public SeatHoldExpirationService(
            SeatRepository seatRepository,
            ReservationRepository reservationRepository
    ) {
        this.seatRepository = seatRepository;
        this.reservationRepository = reservationRepository;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void releaseExpiredSeats() {

        LocalDateTime expirationTime =
                LocalDateTime.now().minusMinutes(5);

        List<Seat> expiredSeats =
                seatRepository.findByStatusAndHeldAtBefore(
                        SeatStatus.HELD,
                        expirationTime
                );

        System.out.println(
                "EXPIRED SEATS FOUND = " + expiredSeats.size()
        );

        for (Seat seat : expiredSeats) {

            System.out.println(
                    "EXPIRING SEAT ID = " + seat.getId()
            );

            // Find only the ACTIVE/HELD reservation
            Optional<Reservation> reservation =
                    reservationRepository.findBySeatIdAndStatus(
                            seat.getId(),
                            ReservationStatus.HELD
                    );

            if (reservation.isPresent()) {

                Reservation expiredReservation =
                        reservation.get();

                expiredReservation.setStatus(
                        ReservationStatus.EXPIRED
                );

                reservationRepository.save(
                        expiredReservation
                );

                System.out.println(
                        "EXPIRING RESERVATION ID = "
                                + expiredReservation.getId()
                );

                System.out.println(
                        "NEW STATUS = "
                                + expiredReservation.getStatus()
                );
            } else {

                System.out.println(
                        "NO ACTIVE RESERVATION FOUND FOR SEAT ID = "
                                + seat.getId()
                );
            }

            // Release the seat
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setHeldAt(null);

            seatRepository.save(seat);
        }
    }
}