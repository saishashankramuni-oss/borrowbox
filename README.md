# BorrowBox — Rent Smart. Own Less.
## Complete Full-Stack Equipment Rental Marketplace

BorrowBox is a modern, full-stack online equipment rental marketplace where equipment owners can list gear (cameras, laptops, projectors, audio equipment, tools, gaming consoles, drones, etc.) and renters can discover, check date availability, book, and manage the entire rental lifecycle.

---

## 🌟 Key Features

1. **Double-Booking & Concurrency Prevention**: Database pessimistic write locking (`@Lock(LockModeType.PESSIMISTIC_WRITE)`) and inclusive date overlap validation (`requestedStart <= existingEnd AND requestedEnd >= existingStart`) prevent simultaneous conflicting reservations.
2. **Server-Side Pricing Engine**: Java `BigDecimal` calculations for daily rental subtotal, 2% service fees, refundable security deposits, and total amount due.
3. **Full Booking Lifecycle State Machine**: Enforces strict status transitions: `PENDING` &rarr; `APPROVED` / `REJECTED` &rarr; `ACTIVE` &rarr; `RETURN_REQUESTED` &rarr; `COMPLETED` / `CANCELLED` / `DISPUTED`.
4. **Condition Inspection Reports**: Side-by-side comparison of equipment condition before handover and after return with photo evidence.
5. **Role-Based Access Control**: Renter, Owner, and Protected Admin Dashboards with JWT bearer token authentication and BCrypt password hashing.
6. **In-App Notifications & Wishlist**: Database-backed notification alerts and user saved wishlist items.
7. **Verified Reviews System**: Allows reviews only after a completed rental with duplicate review prevention.

---

## 🛠️ Technology Stack

- **Frontend**: HTML5, CSS3 (Vanilla design system, HSL color tokens, dark/light surface cards), Vanilla JavaScript (ES6+ Fetch API), Lucide Icons SVG.
- **Backend**: Java 25 LTS, Spring Boot 3.5.16, Maven, Spring Data JPA, Spring Security, Bean Validation, JWT (`jjwt 0.12.5`), BCrypt.
- **Database**: MySQL 8.x (with H2 embedded fallback support for zero-config local runs), Flyway DB Migrations (`V1__schema.sql`, `V2__seed_data.sql`).

---

## 🔑 Documented Demo User Accounts

All demo user accounts use the password: **`Password123!`**

| Account Role | Email Address | Phone Number | Description |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin@borrowbox.com` | `+91 98765 00001` | Full access to Admin Dashboard, users, disputes, and overview metrics |
| **Equipment Owner 1** | `rahul.owner@borrowbox.com` | `+91 98765 11111` | Owner of Sony A7 IV, MacBook Pro 16", Canon EOS R6, etc. |
| **Equipment Owner 2** | `priya.owner@borrowbox.com` | `+91 98765 22222` | Owner of PS5, DJI Mini 4 Pro, Dell XPS 15, etc. |
| **Renter 1** | `alex.renter@borrowbox.com` | `+91 98765 33333` | Renter with active and completed booking records |
| **Renter 2** | `sneha.renter@borrowbox.com` | `+91 98765 44444` | Renter with active PS5 booking and review history |

---

## 🚀 Quick Setup & Installation Guide (Windows)

### 1. Prerequisites
- **Java Development Kit (JDK)**: Java 25 LTS or higher.
- **Apache Maven**: Version 3.8+ (Verified: Maven 3.9 installed).
- **MySQL Server 8.x** (Optional - system falls back automatically to H2 MySQL compatibility mode if MySQL server is offline).

### 2. Database Setup (MySQL 8.x)
Open MySQL Workbench or MySQL CLI:
```sql
CREATE DATABASE borrowbox_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Optionally set environment variables in Command Prompt or PowerShell:
```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/borrowbox_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_mysql_password"
```

### 3. Build & Run Backend Application
Open a terminal in the project `backend` folder:
```powershell
mvn clean spring-boot:run
```
Spring Boot will start on port `8080` and Flyway will automatically execute database migrations (`V1__schema.sql` and `V2__seed_data.sql`).

### 4. Accessing the Website & Frontend
- **Integrated Server**: The frontend static files are served directly at [http://localhost:8080](http://localhost:8080)
- **H2 Console** (when using H2 mode): [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (JDBC URL: `jdbc:h2:mem:borrowbox_db`, User: `sa`, Password: *blank*)

---

## 🧪 Running Automated Unit & Concurrency Tests

To run the JUnit 5 tests verifying double-booking prevention, pricing calculations, and authentication:
```powershell
cd backend
mvn test
```
The following tests will run:
- `PricingEngineTest`: Verifies `BigDecimal` subtotal, service fee, deposit, and total calculations.
- `BookingConcurrencyTest`: Verifies that overlapping booking requests are blocked and throw `BookingConflictException`.
- `BookingAccessControlTest`: Verifies role-based access security constraints.

---

## 🌐 Cloud Production Deployment (Netlify + Render + Railway)

BorrowBox is prepared for modern cloud hosting with independent tier scaling:
- **Frontend**: Deployed to **Netlify** (CDN edge static hosting + `/api/*` and `/uploads/*` proxy). Configuration in [netlify.toml](file:///s:/BORROWBOX/netlify.toml) and [frontend/_redirects](file:///s:/BORROWBOX/frontend/_redirects).
- **Backend**: Deployed to **Render** as a Docker web service with persistent disk at `/app/uploads`. Configuration in [render.yaml](file:///s:/BORROWBOX/render.yaml) and [Dockerfile](file:///s:/BORROWBOX/Dockerfile).
- **Database**: Managed **MySQL 8.x** on **Railway** with automatic zero-downtime Flyway migrations (`V1__schema.sql`, `V2__seed_data.sql`).

For step-by-step instructions with exact environment variables and GitHub steps, see:
👉 **[Production Deployment Guide](docs/deployment-guide.md)**

### Production Health Check Endpoints
- Public Liveness Probe: `GET /health` or `GET /api/v1/health`
- Spring Boot Actuator: `GET /actuator/health`

## 📁 Project Folder Structure

```
BORROWBOX/
├── frontend/                  # Responsive Vanilla JS Frontend
│   ├── index.html             # Home page with hero search & featured listings
│   ├── explore.html           # Marketplace search, filters & pagination
│   ├── equipment-details.html # Item details & live booking pricing widget
│   ├── login.html             # Login page with 1-click demo presets
│   ├── register.html          # Sign up form
│   ├── renter-dashboard.html  # Renter dashboard & booking tabs
│   ├── owner-dashboard.html   # Owner dashboard & handover/return actions
│   ├── add-equipment.html     # Multi-section listing creation form
│   ├── edit-equipment.html    # Listing edit form
│   ├── booking-details.html   # Status timeline & side-by-side condition reports
│   ├── wishlist.html          # Saved wishlist gear
│   ├── notifications.html     # In-app notification center
│   ├── profile.html           # Account profile settings
│   ├── admin-dashboard.html   # Admin moderation & disputes queue
│   ├── about.html             # Platform mission & policies
│   ├── contact.html           # Support contact form
│   ├── css/                   # Stylesheets (main.css, components.css, pages.css)
│   └── js/                    # JS Modules (api.js, auth.js, ui.js, navbar.js)
├── backend/                   # Spring Boot 3.x Java REST Service
│   ├── pom.xml                # Maven Dependencies
│   └── src/
│       ├── main/
│       │   ├── java/com/borrowbox/ # Entities, DTOs, Repositories, Services, Controllers
│       │   └── resources/
│       │       ├── application.yml
│       │       └── db/migration/   # Flyway V1 schema & V2 seed migrations
│       └── test/java/com/borrowbox/ # JUnit Tests
├── database/                  # Standalone DDL & Seed SQL Scripts
│   ├── schema.sql
│   └── seed-data.sql
├── docs/                      # Documentation
│   ├── architecture.md        # Concurrency & System Architecture
│   └── api-documentation.md   # REST API Specs
├── .env.example
├── .gitignore
└── README.md
```
