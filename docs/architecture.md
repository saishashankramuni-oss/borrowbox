# BorrowBox System Architecture

## Overview
**BorrowBox — Rent Smart. Own Less.** is a full-stack equipment rental marketplace platform built using Spring Boot 3.5.16 (Java 25 LTS), MySQL 8.x, and Vanilla JavaScript with responsive CSS3.

```
                  +-----------------------------------+
                  |         Frontend Web UI           |
                  |  (HTML5, Vanilla JS, CSS3, Fetch) |
                  +-----------------+-----------------+
                                    | REST API Calls (JWT Bearer / JSON)
                                    v
                  +-----------------------------------+
                  |      Spring Boot REST Backend     |
                  |  - Spring Security (JWT Auth)     |
                  |  - Concurrency Locking (Pessimistic)
                  |  - Rental Pricing Engine          |
                  |  - File Upload & Storage          |
                  +-----------------+-----------------+
                                    | Spring Data JPA / Flyway
                                    v
                  +-----------------------------------+
                  |         MySQL 8.x Database        |
                  | (Relational Schema & Seed Data)   |
                  +-----------------------------------+
```

## Concurrency Control & Double-Booking Prevention
To eliminate race conditions and double-booking bugs when simultaneous rental requests are made for the same equipment, BorrowBox implements a two-tier strategy:

1. **Pessimistic Locking**: `EquipmentRepository.findByIdWithLock(Long id)` applies `@Lock(LockModeType.PESSIMISTIC_WRITE)` during booking creation inside a `@Transactional` boundary.
2. **Server-Side Inclusive Overlap Query**:
   ```sql
   SELECT COUNT(b) FROM Booking b 
   WHERE b.equipment.id = :equipmentId 
     AND b.bookingStatus IN ('PENDING', 'APPROVED', 'READY_FOR_PICKUP', 'ACTIVE', 'RETURN_REQUESTED')
     AND (:requestedStart <= b.endDate AND :requestedEnd >= b.startDate)
   ```
   If `count > 0`, the transaction is rolled back and a `409 CONFLICT` exception is thrown.

## Database Entities & Entity-Relationship Schema
- `users`: Stores renters, owners, and administrators with BCrypt password hashes.
- `categories`: Marketplace categories (Cameras, Laptops, Projectors, Audio, etc.).
- `equipment`: Equipment listings with daily rates, security deposits, min/max days, and locations.
- `equipment_images`: Photo gallery URLs and display orders.
- `bookings`: Rental transactions with snapshots of daily rates, subtotal, fees, deposit, total amount, and booking status.
- `booking_status_history`: Timestamped audit trail for status machine transitions.
- `condition_reports` & `condition_report_images`: Inspection evidence recorded BEFORE_HANDOVER and AFTER_RETURN.
- `reviews`: Star ratings and reviews posted only after completed rentals.
- `wishlists`: Unique user-equipment saved items.
- `notifications`: Database-backed notification alerts.
- `disputes`: Conflict resolution queue handled by administrators.
