package com.adii.seatreservationengine.exception;

public class ReservationAccessDeniedException
        extends RuntimeException {

    public ReservationAccessDeniedException(String message) {
        super(message);
    }
}