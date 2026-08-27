package com.adii.seatreservationengine.mapper;

import com.adii.seatreservationengine.dto.SeatResponse;
import com.adii.seatreservationengine.entity.Seat;
import org.springframework.stereotype.Component;

@Component
public class SeatMapper {
    public SeatResponse toSeatResponse(Seat seat) {
        return new SeatResponse(seat.getId(), seat.getSeatNumber(), seat.getStatus(),seat.getHeldAt());
    }
}
