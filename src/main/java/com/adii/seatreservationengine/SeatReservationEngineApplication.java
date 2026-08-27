package com.adii.seatreservationengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SeatReservationEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeatReservationEngineApplication.class, args);
    }

}
