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


------------Reservation Flow : ---------------------------------

                    SEAT
                     │
                     ▼
                AVAILABLE
                     │
                Hold Seat
                     │
                     ▼
                   HELD
                  /     \
                 /       \
          Payment        Timeout
             │             │
             ▼             ▼
          SUCCESS       EXPIRED
             │             │
             ▼             ▼
        CONFIRMED       AVAILABLE
             │
             ▼
           BOOKED


-------Concurrency Control----------
The system uses pessimistic write locking when a seat is being reserved.

This prevents two users from successfully booking the same seat at the same time.


User A ────────┐
               │
               ▼
             Seat A1
               🔒
               │
          AVAILABLE
               │
               ▼
              HELD
               │
             Commit
               │
               ▼
User B ────────🔒
               │
               ▼
              HELD
               │
               ▼
          ❌ Rejected



---------Idempotency -----------

Seat reservation requests require an Idempotency-Key.

If the same request is accidentally retried, the system returns the previously stored response instead of creating another reservation.

          Request 1
   │
   ├── Idempotency-Key: ABC123
   ▼
Reservation Created
   │
   ▼
Response Stored


Request 2
   │
   ├── Idempotency-Key: ABC123
   ▼
Existing Response Returned



------Payment Flow---------

Stripe is integrated using PaymentIntent.

Reservation
    │
    │ HELD
    ▼
Create PaymentIntent
    │
    ▼
Payment = PENDING
    │
    ▼
Stripe Payment
    │
    ▼
Stripe Webhook
    │
    │ payment_intent.succeeded
    ▼
Payment = SUCCESS
    │
    ▼
Reservation = CONFIRMED
    │
    ▼
Seat = BOOKED


-------Authentication Flow----------

Login
  │
  ▼
Username + Password
  │
  ▼
JWT Token
  │
  ▼
Authorization: Bearer <JWT>
  │
  ▼
JWT Authentication Filter
  │
  ▼
Authenticated User



#######📡 API Endpoints

-----Authentication----

POST /api/users/register
POST /api/users/login

----Seats-----
GET  /api/seats
POST /api/seats
POST /api/seats/{id}/hold
POST /api/seats/{id}/release
POST /api/seats/{id}/confirm

-----Payments-----

POST /api/payments/create
POST /api/payments/webhook




---Overview:----
src/main/java/com/adii/seatreservationengine
│
├── config
├── controller
├── dto
├── entity
├── exception
├── filter
├── mapper
├── repository
└── service
