# 🎟️ Seat Reservation Engine

Short project description

## 🏗️ Architecture

        Client
          │
          ▼
    ┌─────────────┐
    │ Controller  │
    └──────┬──────┘
           ▼
    ┌─────────────┐
    │   Service   │
    └──────┬──────┘
           │
     ┌─────┴──────┐
     ▼            ▼
 Repository     Stripe
     │            │
     ▼            ▼
   MySQL       Webhook

## 🔄 Reservation Flow

AVAILABLE
    ↓
  HELD
    ↓
Payment
  ↙   ↘
Success Timeout
 ↓       ↓
BOOKED  EXPIRED
          ↓
      AVAILABLE

## ✨ Features

- JWT authentication
- Seat reservation
- Pessimistic locking
- Idempotency
- Reservation expiration
- Ownership validation
- Stripe PaymentIntent
- Stripe webhooks
- DTOs and mappers
- Global exception handling

## 🛠️ Tech Stack

Java
Spring Boot
Spring Data JPA / Hibernate
Spring Security
JWT
MySQL
Stripe
Maven

## 📡 API Endpoints

Authentication
...

Seats
...

Payments
...

## 🗄️ Database Design

User
 ↓
Reservation
 ↓
Seat

Reservation
 ↓
Payment

## 🚀 Running Locally

...

## 🔐 Environment Variables

DB_PASSWORD
STRIPE_SECRET_KEY
STRIPE_WEBHOOK_SECRET
JWT_SECRET

## 🧪 Testing

...

## 👨‍💻 Author
Aditya Alluri
