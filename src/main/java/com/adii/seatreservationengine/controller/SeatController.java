package com.adii.seatreservationengine.controller;


import com.adii.seatreservationengine.dto.CreateSeatRequest;
import com.adii.seatreservationengine.dto.SeatResponse;
import com.adii.seatreservationengine.entity.Seat;
import com.adii.seatreservationengine.service.SeatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService){
        this.seatService = seatService;
    }

    @GetMapping("/api/seats")
    public List<SeatResponse> getAllSeats() {
        return seatService.getAllSeats();
    }

    @PostMapping("/api/seats")
    public Seat createSeat(@RequestBody CreateSeatRequest request) {

        return seatService.createSeat((request));
    }

    @PostMapping("/api/seats/{id}/hold")
    public SeatResponse holdSeat(
            @PathVariable Long id,
            @RequestHeader("Idempotency-Key") String idempotencyKey
    ) {
        return seatService.holdSeat(id, idempotencyKey);
    }

    @PostMapping("/api/seats/{id}/release")
    public SeatResponse releaseSeat(
            @PathVariable Long id,
            @RequestHeader("Reservation-Key") String reservationKey
    ) {
        return seatService.releaseSeat(id, reservationKey);
    }

    @PostMapping("/api/seats/{id}/confirm")
    public SeatResponse confirmSeat(
            @PathVariable Long id,
            @RequestHeader("Reservation-Key") String reservationKey
    ) {
        return seatService.confirmSeat(id, reservationKey);
    }
}
