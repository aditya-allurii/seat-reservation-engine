package com.adii.seatreservationengine;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SeatConcurrencyTest {

    ExecutorService executor = Executors.newFixedThreadPool(20);

    @Test
    public void testConcurrentHold() throws Exception {

        int numberOfRequests = 20;

        CountDownLatch startSignal = new CountDownLatch(1);

        HttpClient client = HttpClient.newHttpClient();

        List<Callable<Integer>> requests = new ArrayList<>();

        // Create 20 identical requests
        for (int i = 0; i < numberOfRequests; i++) {

            Callable<Integer> request = () -> {

                // Wait until all requests are ready
                startSignal.await();

                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .uri(URI.create(
                                "http://localhost:8080/api/seats/16/hold"
                        ))
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> response =
                        client.send(
                                httpRequest,
                                HttpResponse.BodyHandlers.ofString()
                        );

                return response.statusCode();
            };

            requests.add(request);
        }

        // Submit all 20 requests
        List<Future<Integer>> futures = new ArrayList<>();

        for (Callable<Integer> request : requests) {
            futures.add(executor.submit(request));
        }

        // Release all 20 requests at approximately the same time
        startSignal.countDown();

        int successfulRequests = 0;
        int rejectedRequests = 0;

        // Collect results
        for (Future<Integer> future : futures) {

            int status = future.get();

            System.out.println("Response: " + status);

            if (status == 200) {
                successfulRequests++;
            } else if (status == 409) {
                rejectedRequests++;
            }
        }

        System.out.println("----------------------------");
        System.out.println("Successful requests: " + successfulRequests);
        System.out.println("Rejected requests: " + rejectedRequests);
        System.out.println("----------------------------");

        // Exactly ONE request must succeed
        assertEquals(1, successfulRequests);

        // The remaining 19 must be rejected
        assertEquals(19, rejectedRequests);

        executor.shutdown();
    }
}