# BorrowBox Production Deployment Guide
## Complete Cloud Setup for Netlify, Render & Railway MySQL

This guide provides end-to-end, production-ready deployment instructions for **BorrowBox**, configured for the target cloud topology:
- **Frontend**: Hosted on [Netlify](https://www.netlify.com/) (Static HTML5/CSS3/Vanilla JS + API & Upload Proxy)
- **Backend**: Hosted on [Render](https://render.com/) (Spring Boot 3 REST API in Docker container / Java 25 runtime)
- **Database**: Hosted on [Railway](https://railway.app/) (Managed MySQL 8.x relational database)

---

## 🏗️ Architecture & Component Boundaries

```
[ Browser / Renter / Owner Client ]
               │
               ▼
   [ Netlify Static CDN Edge ]
     ├── Serves HTML / CSS / JS
     ├── Proxies /api/* ───────┐
     └── Proxies /uploads/* ───┼──────────┐
                               ▼          ▼
                    [ Render Web Service ]
                      ├── Spring Boot 3 / Java REST API
                      ├── Health Check: /health & /actuator/health
                      ├── Persistent Disk Mount: /app/uploads
                      │
                      ▼
               [ Railway MySQL 8 ]
                 ├── Tables, Foreign Keys, Indexes
                 ├── Flyway Schema History (V1 schema, V2 seed)
```

---

## 📋 Status: Pre-Deployment Changes Applied to Workspace

The codebase has already been updated and verified locally:
1. **Health Checks**: Added Spring Boot Actuator (`spring-boot-starter-actuator`) and dedicated `/health` & `/api/v1/health` endpoints returning HTTP 200 OK without requiring authentication.
2. **Environment Variable Configuration**: Fully externalized `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `CORS_ALLOWED_ORIGINS`, `SERVER_PORT` / `PORT`, and `UPLOAD_DIR`.
3. **Database Connection Pooling**: Configured HikariCP pool in `application-prod.yml` (`maximum-pool-size: 10`, `minimum-idle: 5`, `connection-timeout: 20s`, `idle-timeout: 5m`, `max-lifetime: 30m`).
4. **HTTPS-Aware Reverse Proxy**: Configured `server.forward-headers-strategy: native` in `application-prod.yml` so Spring Boot accurately respects `X-Forwarded-Proto` and `X-Forwarded-Port` behind Render's proxy.
5. **Dynamic CORS Security**: Configured `CorsConfigurationSource` in `SecurityConfig.java` to dynamically permit requests from your Netlify domain via `CORS_ALLOWED_ORIGINS`.
6. **Zero Data Loss Migrations**: Configured Flyway with `baseline-on-migrate: true` and Hibernate `ddl-auto: none`, ensuring no existing tables or data are dropped.
7. **Frontend Configuration Layer**: Added `frontend/js/config.js` and updated `api.js` + `ui.js` with automatic environment detection, image URL resolution, and dual-mode operation (Netlify proxy or direct Render domain).
8. **Deployment Manifests**: Added `netlify.toml`, `frontend/_redirects`, `render.yaml`, updated `Dockerfile` with runtime `curl` healthcheck, and prepared `.env.production.example`.
9. **Full Automated Test Validation**: Compiled all 84 Java source files and passed all unit and concurrency integration tests (`BUILD SUCCESS`).

---

## 🚀 Step 1: Provision Railway MySQL 8 Database

1. **Log in to Railway**:
   - Go to [railway.app](https://railway.app/) and log in (or create an account).
2. **Create a New Project**:
   - Click **+ New Project** &rarr; select **Provision MySQL**.
3. **Retrieve Database Connection Variables**:
   - Click on the newly provisioned **MySQL** service card.
   - Go to the **Variables** tab. Railway provides:
     - `MYSQLHOST` (e.g. `junction.proxy.rlwy.net`)
     - `MYSQLPORT` (e.g. `3306` or a random proxy port like `48921`)
     - `MYSQLDATABASE` (e.g. `railway`)
     - `MYSQLUSER` (e.g. `root`)
     - `MYSQLPASSWORD` (e.g. `secretpassword123`)
     - `MYSQL_URL` (e.g. `mysql://root:...@junction.proxy.rlwy.net:48921/railway`)
4. **Construct the JDBC URL for Spring Boot**:
   - Spring Boot and HikariCP require the `jdbc:mysql://` prefix:
     ```
     jdbc:mysql://<MYSQLHOST>:<MYSQLPORT>/<MYSQLDATABASE>?useSSL=true&requireSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
     ```
   - **Example**:
     ```
     jdbc:mysql://junction.proxy.rlwy.net:48921/railway?useSSL=true&requireSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
     ```

> [!IMPORTANT]
> **Data Safety & Migrations**: You do **not** need to manually run SQL scripts on Railway! When the Render backend starts up with this `DB_URL`, Flyway will automatically run `V1__schema.sql` and `V2__seed_data.sql`. It records applied migrations in `flyway_schema_history` and will never drop tables or overwrite data.

---

## 🚀 Step 2: Push Your BorrowBox Code to GitHub

Open PowerShell in your Windows workspace:

```powershell
# Navigate to workspace
cd S:\BORROWBOX

# Check git status
git status

# Add all modified and newly created deployment files
git add .

# Commit changes
git commit -m "feat(devops): configure Netlify, Render, and Railway production deployment"

# If not already connected to GitHub:
# git remote add origin https://github.com/<your-username>/borrowbox.git
# git branch -M main

# Push to your GitHub repository
git push origin main
```

---

## 🚀 Step 3: Deploy Backend to Render

1. **Log in to Render**:
   - Go to [render.com](https://render.com/) and connect your GitHub account.
2. **Create New Web Service**:
   - Click **New +** &rarr; **Web Service**.
   - Select your **borrowbox** repository.
3. **Configure Service Details**:
   - **Name**: `borrowbox-backend`
   - **Region**: Choose the region closest to your Railway database (e.g., `Oregon (US West)` or `Frankfurt (EU)`).
   - **Branch**: `main`
   - **Runtime**: **Docker** (Render will automatically detect `Dockerfile` in the root).
   - **Instance Type**: **Free** or **Starter**.
4. **Add Persistent Disk (Crucial for Images)**:
   - On Render (Starter plan or above), scroll to **Disks** &rarr; **Add Disk**:
     - **Name**: `borrowbox-uploads`
     - **Mount Path**: `/app/uploads`
     - **Size**: `1 GB` (or desired capacity).
   - *(Note: On the free plan without disks, uploads will persist until the instance restarts/spins down. Attaching a persistent disk ensures zero image loss).*
5. **Configure Health Check Path**:
   - Scroll to **Advanced** &rarr; **Health Check Path**:
   - Enter: `/health` (or `/actuator/health`).
6. **Configure Environment Variables**:
   Under **Environment Variables**, add the following key-value pairs:

   | Key | Value / Source | Description |
   | :--- | :--- | :--- |
   | `SPRING_PROFILES_ACTIVE` | `prod` | Activates production profile |
   | `DB_URL` | `jdbc:mysql://<HOST>:<PORT>/<DB>?useSSL=true&requireSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true` | Railway MySQL JDBC URL |
   | `DB_USERNAME` | `<MYSQLUSER>` from Railway | Database username (e.g. `root`) |
   | `DB_PASSWORD` | `<MYSQLPASSWORD>` from Railway | Database password |
   | `DB_DRIVER` | `com.mysql.cj.jdbc.Driver` | MySQL JDBC Driver |
   | `DB_DIALECT` | `org.hibernate.dialect.MySQLDialect` | Hibernate MySQL dialect |
   | `JWT_SECRET` | *(Generate a 64-character hex string)* | 256-bit secret key |
   | `JWT_EXPIRATION` | `86400000` | 24-hour token expiration (ms) |
   | `CORS_ALLOWED_ORIGINS` | `https://<your-app>.netlify.app` | Netlify frontend URL *(update once created)* |
   | `UPLOAD_DIR` | `/app/uploads` | Path to persistent storage |

   *PowerShell snippet to generate a secure random 256-bit JWT Secret on Windows:*
   ```powershell
   -join ((1..32) | ForEach-Object { '{0:x2}' -f (Get-Random -Max 256) })
   ```

7. **Deploy the Web Service**:
   - Click **Create Web Service**.
   - Render will build the Docker container, start Spring Boot, run Flyway migrations against Railway MySQL, and verify `/health`.
   - Once deployed, copy your Render URL (e.g., `https://borrowbox-backend.onrender.com`).

---

## 🚀 Step 4: Deploy Frontend to Netlify

1. **Log in to Netlify**:
   - Go to [netlify.com](https://www.netlify.com/) and sign in with GitHub.
2. **Add New Site**:
   - Click **Add new site** &rarr; **Import an existing project**.
   - Choose **GitHub** and select your `borrowbox` repository.
3. **Configure Build & Deploy Settings**:
   - **Branch to deploy**: `main`
   - **Base directory**: *(leave empty)*
   - **Build command**: *(leave empty — the frontend is vanilla HTML/CSS/JS)*
   - **Publish directory**: `frontend`
4. **Connect Netlify to your Render Backend**:
   There are two straightforward options:

   ### Option A: Via Netlify Rewrites / Proxy (Recommended — Zero CORS Issues)
   1. Open `s:\BORROWBOX\netlify.toml` and `s:\BORROWBOX\frontend\_redirects` in your code.
   2. Replace `https://borrowbox-backend.onrender.com` with your **actual** Render URL.
   3. Commit and push:
      ```powershell
      git add netlify.toml frontend/_redirects
      git commit -m "chore: point Netlify proxy to actual Render backend URL"
      git push origin main
      ```
   4. Netlify will seamlessly proxy all calls from `https://your-site.netlify.app/api/*` and `https://your-site.netlify.app/uploads/*` to Render behind the scenes!

   ### Option B: Via `config.js` Direct Domain
   1. In `frontend/js/config.js`, set:
      ```javascript
      BACKEND_URL: 'https://your-actual-render-subdomain.onrender.com',
      ```
   2. In Render environment variables, ensure `CORS_ALLOWED_ORIGINS` includes your Netlify URL (e.g. `https://your-borrowbox.netlify.app`).
   3. Commit and push to GitHub.

5. **Deploy Site**:
   - Click **Deploy borrowbox**.
   - Netlify will publish your site in seconds and assign a URL (e.g., `https://clever-name-12345.netlify.app`).
   - You can rename your site subdomain in **Site configuration** &rarr; **Change site name** (e.g. `borrowbox-marketplace.netlify.app`).

---

## 🧪 Step 5: Post-Deployment Verification Checklist

Once both Render and Netlify deployments are complete, run through this verification checklist:

1. **Health Check Verification**:
   - Open in browser: `https://<YOUR-RENDER-URL>/health`
   - Expected response:
     ```json
     {
       "service": "BorrowBox Backend",
       "status": "UP",
       "timestamp": "2026-10-10T..."
     }
     ```
   - Open: `https://<YOUR-RENDER-URL>/actuator/health`
   - Expected response: `{"status":"UP"}`

2. **Frontend Loading**:
   - Open your Netlify URL: `https://<YOUR-NETLIFY-URL>`.
   - Confirm that hero banner, category pills, and equipment cards render without console errors.

3. **Authentication Verification**:
   - Go to `https://<YOUR-NETLIFY-URL>/login.html`.
   - Click the **1-Click Demo Login** for Renter (`alex.renter@borrowbox.com` / `Password123!`).
   - Verify you are redirected to the Renter Dashboard and your profile details load.

4. **Equipment Listings & Search**:
   - Navigate to `explore.html`.
   - Filter by category (e.g. "Cameras & Lenses") or price slider.
   - Confirm that items load from the Railway database.

5. **Booking Estimate & Availability**:
   - Click on an item (e.g. Sony Alpha A7 IV).
   - Select start date and end date on `equipment-details.html`.
   - Verify that the live pricing engine calculates subtotal, 2% service fee, and deposit.
   - Click **Request Booking** and verify the booking appears in your dashboard.

6. **File Upload Verification**:
   - Log in as Owner (`rahul.owner@borrowbox.com` / `Password123!`).
   - Navigate to `add-equipment.html`.
   - Upload an equipment photo, complete the form, and submit.
   - Verify that the image appears on the created listing.

---

## 💻 Local Development Workflow (Separate from Production)

Your local development configuration remains completely intact and isolated:
- Running `mvn clean spring-boot:run` in `backend/` defaults to embedded **H2 database** on port `8080` with zero configuration needed.
- Opening the frontend via VS Code / Antigravity Live Server (`http://127.0.0.1:5500`) automatically detects port `5500` and routes API calls to `http://localhost:8080/api/v1`.
- Production credentials and cloud URLs are never hardcoded and are read strictly from environment variables.
