package com.adii.seatreservationengine.exception;

import com.adii.seatreservationengine.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ReservationAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleReservationAccessDenied(
            ReservationAccessDeniedException ex) {

        ErrorResponse error =
                new ErrorResponse(
                        HttpStatus.FORBIDDEN.value(),
                        ex.getMessage()
                );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(error);
    }

    // Seat is already booked/held
    @ExceptionHandler(SeatNotAvailableException.class)
    public ResponseEntity<ErrorResponse> handleSeatNotAvailableException(
            SeatNotAvailableException ex) {

        ErrorResponse error =
                new ErrorResponse(
                        HttpStatus.CONFLICT.value(),
                        "Seat is not available"
                );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    // Seat ID does not exist
    @ExceptionHandler(SeatNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSeatNotFoundException(
            SeatNotFoundException ex) {

        ErrorResponse error =
                new ErrorResponse(
                        HttpStatus.NOT_FOUND.value(),
                        "Seat not found"
                );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    // Duplicate/conflicting idempotency key
    @ExceptionHandler(IdempotencyKeyConflictException.class)
    public ResponseEntity<ErrorResponse> handleIdempotencyConflict(
            IdempotencyKeyConflictException ex) {

        ErrorResponse error =
                new ErrorResponse(
                        HttpStatus.CONFLICT.value(),
                        ex.getMessage()
                );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    // Required request header is missing
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestHeader(
            MissingRequestHeaderException ex) {

        ErrorResponse error =
                new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Required request header is missing: "
                                + ex.getHeaderName()
                );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    // Invalid arguments, for example: User not found
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex) {

        ErrorResponse error =
                new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage()
                );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    // Invalid application state, for example:
    // Reservation is not active
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(
            IllegalStateException ex) {

        ErrorResponse error =
                new ErrorResponse(
                        HttpStatus.CONFLICT.value(),
                        ex.getMessage()
                );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }
}