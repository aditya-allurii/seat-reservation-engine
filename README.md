# 🎟️ Seat Reservation Engine

A backend seat reservation system built with **Java and Spring Boot**.

The system is designed to handle **concurrent seat reservations safely** while providing authentication, temporary seat holds, idempotent requests, reservation expiration, and Stripe payment integration.

---

## 🏗️ System Architecture

```text
                    ┌──────────────┐
                    │    Client    │
                    │   / Postman  │
                    └──────┬───────┘
                           │
                           ▼
                  ┌─────────────────┐
                  │ Spring Security │
                  │   + JWT Filter  │
                  └────────┬────────┘
                           │
                           ▼
                  ┌─────────────────┐
                  │   Controller    │
                  │     Layer       │
                  └────────┬────────┘
                           │
                           ▼
                  ┌─────────────────┐
                  │     Service     │
                  │      Layer      │
                  └──────┬─────┬────┘
                         │     │
                 ┌───────┘     └────────┐
                 ▼                      ▼
        ┌─────────────────┐     ┌──────────────┐
        │   Repository    │     │    Stripe    │
        │      Layer      │     │   Payments   │
        └────────┬────────┘     └──────┬───────┘
                 │                     │
                 ▼                     ▼
        ┌─────────────────┐     ┌──────────────┐
        │      MySQL      │     │    Webhook   │
        │    Database     │     │    Events    │
        └─────────────────┘     └──────────────┘
