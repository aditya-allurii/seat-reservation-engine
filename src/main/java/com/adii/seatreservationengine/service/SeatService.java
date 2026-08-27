package com.adii.seatreservationengine.service;

import com.adii.seatreservationengine.dto.CreateSeatRequest;
import com.adii.seatreservationengine.dto.SeatResponse;
import com.adii.seatreservationengine.entity.*;
import com.adii.seatreservationengine.exception.IdempotencyKeyConflictException;
import com.adii.seatreservationengine.exception.ReservationAccessDeniedException;
import com.adii.seatreservationengine.exception.SeatNotAvailableException;
import com.adii.seatreservationengine.exception.SeatNotFoundException;
import com.adii.seatreservationengine.mapper.SeatMapper;
import com.adii.seatreservationengine.repository.IdempotencyRecordRepository;
import com.adii.seatreservationengine.repository.ReservationRepository;
import com.adii.seatreservationengine.repository.SeatRepository;
import com.adii.seatreservationengine.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SeatService {

    private final SeatRepository seatRepository;
    private final SeatMapper seatMapper;
    private final IdempotencyRecordRepository idempotencyRecordRepository;
    private final ObjectMapper objectMapper;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    public SeatService(
            SeatRepository seatRepository,
            SeatMapper seatMapper,
            IdempotencyRecordRepository idempotencyRecordRepository,
            ObjectMapper objectMapper,
            ReservationRepository reservationRepository,
            UserRepository userRepository
    ) {
        this.seatRepository = seatRepository;
        this.seatMapper = seatMapper;
        this.idempotencyRecordRepository = idempotencyRecordRepository;
        this.objectMapper = objectMapper;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
    }

    // =========================
    // GET ALL SEATS
    // =========================

    public List<SeatResponse> getAllSeats() {

        return seatRepository.findAll()
                .stream()
                .map(seatMapper::toSeatResponse)
                .toList();
    }

    // =========================
    // CREATE SEAT
    // =========================

    public Seat createSeat(CreateSeatRequest request) {

        Seat seat = new Seat();

        seat.setSeatNumber(
                request.getSeatNumber()
        );

        seat.setStatus(
                SeatStatus.AVAILABLE
        );

        return seatRepository.save(seat);
    }

    // =========================
    // HOLD SEAT
    // =========================

    @Transactional
    public SeatResponse holdSeat(
            Long seatId,
            String idempotencyKey
    ) {

        // 1. Check idempotency key

        Optional<IdempotencyRecord> existing =
                idempotencyRecordRepository.findByKey(
                        idempotencyKey
                );

        // 2. If key was already used

        if (existing.isPresent()) {

            IdempotencyRecord record =
                    existing.get();

            // Same key cannot be used for another seat

            if (!seatId.equals(record.getSeatId())) {

                throw new IdempotencyKeyConflictException(
                        "Idempotency key already used for another seat"
                );
            }

            try {

                return objectMapper.readValue(
                        record.getResponse(),
                        SeatResponse.class
                );

            } catch (Exception e) {

                throw new RuntimeException(
                        "Failed to read idempotent response",
                        e
                );
            }
        }

        // 3. Find and lock seat

        Optional<Seat> seatOptional =
                seatRepository.findByIdWithLock(seatId);

        Seat seat =
                seatOptional.orElseThrow(
                        SeatNotFoundException::new
                );

        // 4. Seat must be AVAILABLE

        if (seat.getStatus() != SeatStatus.AVAILABLE) {

            throw new SeatNotAvailableException();
        }

        // 5. Get logged-in username

        String username =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        // 6. Find user

        User user =
                userRepository.findByUsername(username)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "User not found"
                                )
                        );

        // 7. Hold seat

        LocalDateTime now =
                LocalDateTime.now();

        seat.setStatus(
                SeatStatus.HELD
        );

        seat.setHeldAt(now);

        Seat savedSeat =
                seatRepository.save(seat);

        // 8. Create reservation

        Reservation reservation =
                new Reservation();

        reservation.setSeat(savedSeat);

        reservation.setUser(user);

        reservation.setReservationKey(
                idempotencyKey
        );

        reservation.setStatus(
                ReservationStatus.HELD
        );

        reservation.setCreatedAt(now);

        reservation.setExpiresAt(
                now.plusMinutes(5)
        );

        reservationRepository.save(reservation);

        // 9. Create response

        SeatResponse response =
                seatMapper.toSeatResponse(savedSeat);

        // 10. Save idempotency record

        try {

            IdempotencyRecord record =
                    new IdempotencyRecord();

            record.setKey(idempotencyKey);

            record.setSeatId(seatId);

            record.setStatusCode(200);

            record.setResponse(
                    objectMapper.writeValueAsString(response)
            );

            idempotencyRecordRepository.save(record);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to save idempotency record",
                    e
            );
        }

        // 11. Return response

        return response;
    }

    // =========================
    // RELEASE SEAT
    // =========================

    @Transactional
    public SeatResponse releaseSeat(
            Long seatId,
            String reservationKey
    ) {

        // 1. Find and lock seat

        Seat seat =
                seatRepository.findByIdWithLock(seatId)
                        .orElseThrow(
                                SeatNotFoundException::new
                        );

        // 2. Seat must currently be HELD

        if (seat.getStatus() != SeatStatus.HELD) {

            throw new SeatNotAvailableException();
        }

        // 3. Find ONLY the active HELD reservation

        Reservation reservation =
                reservationRepository
                        .findBySeatIdAndStatus(
                                seatId,
                                ReservationStatus.HELD
                        )
                        .orElseThrow(
                                () -> new SeatNotAvailableException()
                        );

        // 4. Get logged-in username

        String username =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        // 5. Find user

        User user =
                userRepository.findByUsername(username)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "User not found"
                                )
                        );

        // 6. Check ownership

        if (!reservation.getUser().getId()
                .equals(user.getId())) {

            throw new ReservationAccessDeniedException(
                    "You do not own this seat reservation"
            );
        }

        // 7. Check reservation key

        if (!reservationKey.equals(
                reservation.getReservationKey()
        )) {

            throw new IdempotencyKeyConflictException(
                    "Invalid reservation key"
            );
        }

        // 8. Release seat

        seat.setStatus(
                SeatStatus.AVAILABLE
        );

        seat.setHeldAt(null);

        // 9. Cancel reservation

        reservation.setStatus(
                ReservationStatus.CANCELLED
        );

        reservationRepository.save(
                reservation
        );

        // 10. Save seat

        Seat savedSeat =
                seatRepository.save(seat);

        // 11. Return response

        return seatMapper.toSeatResponse(
                savedSeat
        );
    }

    // =========================
    // CONFIRM SEAT
    // =========================

    @Transactional
    public SeatResponse confirmSeat(
            Long seatId,
            String reservationKey
    ) {

        // 1. Find ONLY the active HELD reservation

        Reservation reservation =
                reservationRepository
                        .findBySeatIdAndStatus(
                                seatId,
                                ReservationStatus.HELD
                        )
                        .orElseThrow(
                                () -> new SeatNotAvailableException()
                        );

        // 2. Get logged-in username

        String username =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        // 3. Find user

        User user =
                userRepository.findByUsername(username)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "User not found"
                                )
                        );

        // 4. Check ownership

        if (!reservation.getUser().getId()
                .equals(user.getId())) {

            throw new IdempotencyKeyConflictException(
                    "You do not own this seat reservation"
            );
        }

        // 5. Check reservation key

        if (!reservationKey.equals(
                reservation.getReservationKey()
        )) {

            throw new IdempotencyKeyConflictException(
                    "Invalid reservation key"
            );
        }

        // 6. Lock seat

        Seat seat =
                seatRepository.findByIdWithLock(seatId)
                        .orElseThrow(
                                SeatNotFoundException::new
                        );

        // 7. Seat must be HELD

        if (seat.getStatus() != SeatStatus.HELD) {

            throw new SeatNotAvailableException();
        }

        // 8. Book seat

        seat.setStatus(
                SeatStatus.BOOKED
        );

        seat.setHeldAt(null);

        // 9. Confirm reservation

        reservation.setStatus(
                ReservationStatus.CONFIRMED
        );

        reservationRepository.save(
                reservation
        );

        // 10. Save seat

        Seat savedSeat =
                seatRepository.save(seat);

        // 11. Return response

        return seatMapper.toSeatResponse(
                savedSeat
        );
    }
}