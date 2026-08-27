package com.adii.seatreservationengine.repository;

import com.adii.seatreservationengine.entity.Seat;
import com.adii.seatreservationengine.entity.SeatStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.id = :seatId")
    Optional<Seat> findByIdWithLock(
            @Param("seatId") Long seatId
    );

    List<Seat> findByStatusAndHeldAtBefore(
            SeatStatus status,
            LocalDateTime time
    );
}