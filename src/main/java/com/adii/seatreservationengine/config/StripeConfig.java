package com.adii.seatreservationengine.config;

import com.stripe.Stripe;
import com.stripe.model.Account;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    public StripeConfig(
            @Value("${stripe.secret-key}") String secretKey
    ) {
        Stripe.apiKey = secretKey;

        try {
            Account account = Account.retrieve();

            System.out.println(
                    "SPRING STRIPE ACCOUNT = "
                            + account.getId()
            );

        } catch (Exception e) {
            System.out.println(
                    "STRIPE ACCOUNT CHECK FAILED = "
                            + e.getMessage()
            );
        }
    }
}