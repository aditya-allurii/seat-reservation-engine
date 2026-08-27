package com.adii.seatreservationengine.dto;

import com.adii.seatreservationengine.entity.SeatStatus;

import java.time.LocalDateTime;

public class SeatResponse {

    private Long id;
    private String seatNumber;
    private SeatStatus status;
    private LocalDateTime heldAt;

    public  SeatResponse(Long id, String seatNumber, SeatStatus status, LocalDateTime heldAt) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.status = status;
        this.heldAt = heldAt;
    }

    public long getId() {
        return id;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public  SeatStatus getStatus() {
        return status;
    }

    public LocalDateTime getHeldAt() {
        return heldAt;
    }
}
