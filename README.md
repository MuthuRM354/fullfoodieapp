# FoodieApp — Full-Stack Food Delivery Platform

A production-ready food delivery application built with a **Spring Boot microservices** backend and a **React** frontend.

---

## Architecture Overview

```
                        ┌─────────────────┐
                        │   React Frontend │  (Vercel / Netlify)
                        │   Port 3000      │
                        └────────┬─────────┘
                                 │ HTTP
                        ┌────────▼─────────┐
                        │   API Gateway    │  Port 8080
                        │  (reverse proxy) │
                        └────────┬─────────┘
         ┌──────────┬────────────┼────────────┬──────────┐
         ▼          ▼            ▼            ▼          ▼
   user-service  restaurant  order-service  payment  delivery
   :8081         :8082        :8083         :8084    :8085
         ▼          ▼            ▼            ▼          ▼
   notification review-svc  tracking-svc  admin-svc
   :8086        :8088        :8087         :8089
```

Each service owns its own MySQL database. JWT authentication is enforced uniformly across all services with a shared secret.

---

## Tech Stack

| Layer     | Technology                             |
|-----------|----------------------------------------|
| Frontend  | React 18, React Router v6, Axios       |
| Backend   | Spring Boot 3.3.5 (Java 17)            |
| Auth      | JWT (jjwt 0.11.5) — stateless          |
| Database  | MySQL 8.0 (one schema per service)     |
| Hosting   | Vercel (frontend), Render (backend)    |

---

## Local Development Setup

### Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8.0+
- Node.js 18+ and npm
- Git

### 1 — Database

```bash
# Log into MySQL as root
mysql -u root -p

# Run the initialization script (creates 9 databases + app user)
source db/init.sql;
```

> Edit `db/init.sql` first to set a strong password for the `foodieapp` MySQL user.

### 2 — Backend (all services)

Open **9 separate terminals**, one per service. Each service reads its config from `application.properties` and falls back to environment variables for secrets.

```bash
# Terminal 1 — api-gateway (start first)
cd src/backend/api-gateway
mvn spring-boot:run

# Terminal 2 — user-service
cd src/backend/user-service
mvn spring-boot:run

# Terminal 3 — restaurant-service
cd src/backend/restaurant-service
mvn spring-boot:run

# Terminal 4 — order-service
cd src/backend/order-service
mvn spring-boot:run

# Terminal 5 — payment-service
cd src/backend/payment-service
mvn spring-boot:run

# Terminal 6 — delivery-partner-service
cd src/backend/delivery-partner-service
mvn spring-boot:run

# Terminal 7 — notification-service
cd src/backend/notification-service
mvn spring-boot:run

# Terminal 8 — review-service
cd src/backend/review-service
mvn spring-boot:run

# Terminal 9 — tracking-service
cd src/backend/tracking-service
mvn spring-boot:run

# Terminal 10 — admin-service
cd src/backend/admin-service
mvn spring-boot:run
```

Health check — all services should respond:
```bash
curl http://localhost:8080/actuator/health   # gateway
curl http://localhost:8081/actuator/health   # user
curl http://localhost:8082/actuator/health   # restaurant
# ... etc.
```

### 3 — Frontend

```bash
cd frontend

# Delete stale node_modules if present, then install
rm -rf node_modules
npm install

# Start dev server
npm start
```

Open http://localhost:3000 — the app talks to the API gateway on port 8080.

### 4 — Mock data (optional)

Once the backend services above are running, populate the app with demo
restaurants, menus, users, orders, and reviews:

```bash
node scripts/seed-mock-data.mjs
```

It drives the real REST APIs through the gateway (register, create
restaurant, place order, leave review, ...) rather than touching the
database directly, so it works against **any** environment — point it at a
live deployment with `API_BASE_URL`:

```bash
API_BASE_URL=https://your-live-gateway.onrender.com node scripts/seed-mock-data.mjs
```

Creates 3 restaurant owners (with 4 restaurants/menus), 4 customers (with
orders + reviews), and 2 delivery partners. All demo accounts use the
password `Demo@1234`. Safe to re-run — existing users log in instead of
re-registering, though restaurants/menus are not deduplicated (a re-run adds
a fresh batch rather than erroring out).

---

## Environment Variables

### Backend (per service)

Each service supports the following env vars that override `application.properties` defaults:

| Variable            | Description                                                        | Default (dev)                                     |
|---------------------|---------------------------------------------------------------------|---------------------------------------------------|
| `DB_URL`            | JDBC URL for the service's database                                 | `jdbc:mysql://localhost:3306/foodieapp_<name>...` |
| `DB_USERNAME`        | MySQL username                                                      | `root`                                            |
| `DB_PASSWORD`        | MySQL password                                                      | *(empty)*                                         |
| `JWT_SECRET`         | Shared JWT signing secret (≥32 chars), used by every service        | `foodieapp-super-secret-key-for-jwt-minimum-256-bits-long` |
| `INTERNAL_API_KEY`   | Shared secret for trusted server-to-server calls (e.g. order-service → notification-service, notification-service → user-service). Must match across the services that call each other. | `foodieapp-internal-service-key-change-me` |
| `SERVER_PORT`        | HTTP port                                                            | per-service default                               |

> **Production (Render)**: `JWT_SECRET` and `INTERNAL_API_KEY` are defined once in the `shared-secrets` envVarGroup in `render.yaml` and automatically injected into every service with the same generated value — no manual copy/paste between service dashboards needed. Running locally without Render, just export the same value in every terminal, or rely on the matching defaults baked into each `application.properties` (fine for local dev, **do not use the default in production**).

### Notification service — optional real email/SMS

`notification-service` works out of the box in **log-only mode**: notifications are always saved to the database, and email/SMS sends are logged instead of actually delivered until you configure a provider.

| Variable            | Description                                                    |
|---------------------|------------------------------------------------------------------|
| `MAIL_USERNAME`     | Gmail address to send from (requires a Gmail **App Password**, not your normal password — Google Account → Security → 2‑Step Verification → App Passwords) |
| `MAIL_PASSWORD`     | The 16-character Gmail App Password                              |
| `MAIL_FROM`         | From address shown to recipients (defaults to `MAIL_USERNAME`)   |
| `FAST2SMS_API_KEY`  | API key from [fast2sms.com](https://www.fast2sms.com) — free credits on signup, no subscription required |

Set any of these and the corresponding channel switches from log-only to actually sending. Leave them unset and the app keeps working exactly as before (records still persist, nothing fails).

### Frontend

| File                | Variable              | Description                                  |
|---------------------|-----------------------|----------------------------------------------|
| `frontend/.env`     | `REACT_APP_API_URL`   | `http://localhost:8080` (local dev)           |
| `frontend/.env.production` | `REACT_APP_API_URL` | Your deployed api-gateway URL e.g. `https://foodieapp-api-gateway.onrender.com` |

---

## Build Commands

### Backend (each service)

```bash
cd src/backend/<service-name>
mvn clean package -DskipTests
# Output JAR: target/<service-name>-*.jar
java -jar target/<service-name>-*.jar
```

### Frontend

```bash
cd frontend
npm run build
# Output: frontend/build/ (ready to upload to Vercel / Netlify)
```

---

## Deployment Guide

### Frontend → Vercel (recommended)

1. Push the repo to GitHub.
2. Go to [vercel.com](https://vercel.com) → **Add New Project** → import your repo.
3. Set **Root Directory** to `frontend`.
4. Add environment variable:
   - `REACT_APP_API_URL` = `https://<your-api-gateway>.onrender.com`
5. Click **Deploy**. Vercel auto-detects Create React App.

The included `frontend/vercel.json` handles SPA routing rewrites automatically.

**Alternative — Netlify:**
1. Connect repo on netlify.com, root dir = `frontend`.
2. Build command: `npm run build`, publish dir: `build`.
3. Add env var `REACT_APP_API_URL`.
4. `frontend/netlify.toml` is already configured.

---

### Backend → Render

The `render.yaml` at the project root defines all 10 Spring Boot web services (api-gateway + 9 backend services) as a Render Blueprint. It does **not** provision Render's own databases — every service is MySQL/JPA, and Render's free database tier is PostgreSQL, so this blueprint intentionally leaves `DB_URL`/`DB_USERNAME`/`DB_PASSWORD` as `sync: false` placeholders you fill in per-service after connecting an external free MySQL host (see below). `JWT_SECRET` and `INTERNAL_API_KEY` **are** provisioned automatically, via the `shared-secrets` envVarGroup at the top of `render.yaml` — Render generates each value once and injects the same value into every service, so you don't need to copy/paste secrets between service dashboards.

> **Note:** PlanetScale removed its free tier in April 2024 — it's now ~$39/mo minimum for MySQL, so it's no longer the free option this guide originally assumed. **[Aiven for MySQL](https://aiven.io/free-mysql-database)** currently has a genuinely free, no-credit-card, no-time-limit plan (1 GB storage/RAM, single node) and is what's documented below instead. It auto-suspends after a period of inactivity (with email warning) and wakes on the next connection — fine for a low-traffic demo, not for a high-traffic production app.

**Blueprint deploy path:**

1. Sign up at [render.com](https://render.com), push this repo to GitHub.
2. Sign up at [aiven.io](https://aiven.io/free-mysql-database) (no card required) → **Create service** → **MySQL** → **Free** plan → pick a region → create. Wait for it to go green ("Running").
3. On the service's **Overview** page, copy the **Host**, **Port**, **User** (`avnadmin`), and **Password**.
4. Connect with any MySQL client (e.g. `mysql -h <host> -P <port> -u avnadmin -p --ssl-mode=REQUIRED`, or use Aiven's built-in web SQL console) and run `db/init.sql` to create all 9 databases. Skip the `CREATE USER` block in that file — Aiven's `avnadmin` user already has full access, so just use it directly for every service (one Aiven org account only gets one free MySQL *service*, but a service can hold as many databases as you like).
5. Go to **New → Blueprint** in Render, connect the repo. Render reads `render.yaml` and creates all 10 web services in one go.
6. On each of the 9 backend services (not api-gateway, which has no DB) in the Render dashboard, set:
   - `DB_URL` = `jdbc:mysql://<aiven-host>:<aiven-port>/<db-name>?sslMode=REQUIRED&serverTimezone=UTC&allowPublicKeyRetrieval=true` (swap in that service's own database name, e.g. `foodieapp_users` for user-service)
   - `DB_USERNAME` = `avnadmin`
   - `DB_PASSWORD` = (the Aiven password from step 3)
7. (Optional) On `foodieapp-notification-service`, set `MAIL_USERNAME` / `MAIL_PASSWORD` / `MAIL_FROM` for real email, and/or `FAST2SMS_API_KEY` for real SMS. Leave unset to keep log-only mode.
8. Set `CORS_ALLOWED_ORIGINS` on `foodieapp-api-gateway` to your deployed frontend URL once you know it.
9. Services auto-deploy; api-gateway should finish last since it depends on the others being reachable.
10. Update `frontend/.env.production` → `REACT_APP_API_URL=https://<gateway-url>.onrender.com` and redeploy the frontend.

**Manual path (if you'd rather not use the Blueprint UI):** create each Web Service by hand with Runtime: Docker, Dockerfile path: `src/backend/<service-name>/Dockerfile`, Docker build context: `src/backend/<service-name>` — same env vars as above, just set `JWT_SECRET`/`INTERNAL_API_KEY` to the same value on every service yourself. (Render's native runtimes are node/python/go/ruby/rust/elixir — there's no native Java runtime, which is why this blueprint deploys via each service's own Dockerfile instead of `./mvnw`/`java -jar` directly.)

**Local Docker (optional):** each service now has a working multi-stage `Dockerfile` (`docker build -t foodieapp-user-service src/backend/user-service`, etc.) for local container testing. Render's own deploy does not use Docker — it builds directly with each service's Maven wrapper, as described above.

---

## Service Port Reference

| Service                | Port | Database                 |
|------------------------|------|--------------------------|
| api-gateway            | 8080 | —                        |
| user-service           | 8081 | foodieapp_users          |
| restaurant-service     | 8082 | foodieapp_restaurants    |
| order-service          | 8083 | foodieapp_orders         |
| payment-service        | 8084 | foodieapp_payments       |
| delivery-partner-svc   | 8085 | foodieapp_delivery       |
| notification-service   | 8086 | foodieapp_notifications  |
| tracking-service       | 8087 | foodieapp_tracking       |
| review-service         | 8088 | foodieapp_reviews        |
| admin-service          | 8089 | foodieapp_admin          |

---

## API Routes (via Gateway on :8080)

| Prefix              | Routed To            | Auth Required |
|---------------------|----------------------|---------------|
| `/api/auth/**`      | user-service         | No            |
| `/api/users/**`     | user-service         | Yes           |
| `/api/restaurants/**` | restaurant-service | No (browse)   |
| `/api/cart/**`      | order-service        | Yes           |
| `/api/orders/**`    | order-service        | Yes           |
| `/api/payments/**`  | payment-service      | Yes           |
| `/api/delivery/**`  | delivery-svc         | Yes           |
| `/api/notifications/**` | notification-svc | Yes           |
| `/api/reviews/**`   | review-service       | Yes           |
| `/api/tracking/**`  | tracking-service     | Yes           |
| `/api/admin/**`     | admin-service        | ADMIN role    |

---

## Completed Features & Fixes

### Microservices (Spring Boot)

- **user-service**: JWT issue on register/login, `@Transactional` on all mutating service methods, Spring Security stateless config, actuator health endpoint.
- **restaurant-service**: Security config, actuator, transactional service methods.
- **order-service**: Same pattern — security + actuator + transactions.
- **payment-service**: Same pattern.
- **delivery-partner-service**: Assignment, partner, and earnings services all transactional; security config.
- **notification-service**: Mark-read and delete operations transactional.
- **review-service**: Review + rating recalculation transactional.
- **tracking-service**: Location save and tracking update transactional.
- **admin-service** (most complex fixes):
  - 5 empty stub classes in the `service` package (`AdminActivity`, `AdminAudit`, `AdminLevel`, `AdminPermission`, `AdminWorkflow`) were shadowing same-named model entities — overwritten to comment-only files (no class body) so model imports resolve correctly.
  - `SecurityConfig` changed from `permitAll()` to `hasAnyRole("ADMIN","SUPER_ADMIN")`.
  - `JwtUtil` and `JwtAuthFilter` created from scratch (were missing).
  - `GlobalExceptionHandler` added.
- **api-gateway** (built from scratch — all files were empty):
  - Reverse proxy `GatewayController` forwarding all headers.
  - `AuthFilter` validating JWT, with public path whitelist.
  - `RateLimitFilter` — 120 requests/min per IP.
  - `LoggingFilter` + `ErrorFilter`.
  - Port mapping fixed: review=8088, tracking=8087.

### React Frontend

- **AuthContext**: login/register/logout with `localStorage` persistence.
- **CartContext**: full cart CRUD with computed `itemCount` / `total`.
- **Axios instance**: JWT interceptor + 401→redirect.
- **Pages**: Home (restaurant grid + search + cuisine filter), RestaurantDetail (menu + reviews + info), Cart (qty controls + delivery address + order summary), Orders (list + detail panel + cancel + track order + leave review), Profile (editable fields + logout + change password), Login, Register.
- **Additional pages**: Notifications (bell icon, mark-read/delete), TrackOrder (live location polling), Payments (payment history + pay now), AdminDashboard (users/orders/restaurants management), OwnerDashboard (restaurant open/close toggle + incoming orders), DeliveryDashboard (assignments + status updates + earnings summary).
- **Header**: sticky with cart badge count, role-based navigation links (My Restaurant for owners, Deliveries for partners, Admin for admins), notification bell with unread count badge.
- **PrivateRoute / PublicRoute**: React Router v6 wrappers.
- **App.js**: full routing with role-based protected routes, AuthProvider + CartProvider, ToastContainer.

### Inter-Service Wiring

- **Order → Notification**: `OrderNotificationService` sends `POST /api/notifications` to notification-service after order placement and after every status change. Uses `RestTemplate` with 3 s connect / 5 s read timeout; failures are non-critical (logged as `WARN`, never roll back the order).
- **Review → Restaurant rating sync**: `RatingService.recalculate()` calls `PUT /api/restaurants/{id}/rating` on restaurant-service after each review is added or deleted, keeping the restaurant card's star rating live. Authenticated with the shared internal-service-key, same as Order → Notification above.
- **Change password**: `POST /api/auth/change-password` validates the submitted current password against bcrypt before encoding and saving the new one; wired end-to-end to the Profile page change-password form.
- **render.yaml updated**: `NOTIFICATION_SERVICE_URL` added to order-service, `RESTAURANT_SERVICE_URL` added to review-service so cross-service calls resolve correctly in production.

---

## Recent Fixes (Audit Pass)

- **Frontend build**: `node_modules` was corrupted (missing `react-scripts` binary) — reinstalled clean.
- **Dockerfiles**: every service's `Dockerfile` was an IntelliJ-generated placeholder (`FROM ubuntu:latest ... ENTRYPOINT ["top","-b"]`), or empty, or corrupted — replaced with working multi-stage build/run Dockerfiles for all 11 services plus a documentation-only root `Dockerfile` (this repo has no single "whole app" image; Render's actual deploy doesn't use Docker at all — see Deployment Guide).
- **Root `pom.xml`**: was an unused, unrelated leftover (had a stray MongoDB dependency none of the services use, and no `<modules>` section despite looking like a reactor parent). Turned into a proper aggregator POM so the repo opens as one multi-module project in an IDE; each service still builds and deploys fully independently via its own `mvnw`, unaffected by this change.
- **`shared-lib`**: removed 6 dead placeholder classes (`common-dto`/`error-handling` packages, each just `/** Placeholder */ public class X {}`) that shadowed the real `dto`/`exception` classes and were never referenced anywhere. The real utility classes remain.
- **`AdminHierarchy` frontend component**: was built but never imported anywhere. Wired into `AdminDashboard.jsx` as a new "Hierarchy" tab.
- **Notification delivery was silently broken**: `order-service` called `notification-service`'s `POST /api/notifications` with no auth header, but that endpoint requires authentication — every call was failing with a caught-and-logged 401. Added a shared internal-service-key (`INTERNAL_API_KEY`) auth path so trusted backend-to-backend calls succeed without needing a user's JWT.
- **Real email/SMS wired into notification-service** (previously log-only stubs that were never called): Gmail SMTP for email, Fast2SMS for SMS (free-tier, no subscription) — both fall back to log-only automatically if not configured. Notification-service now resolves the recipient's email/phone from user-service by userId (via the internal-service-key) when the caller didn't already include it.
- **Payment gateways were 100% dead code**: `PaymentService.confirmPayment()` never called any of the 6 gateway classes — it just hardcoded `SUCCESS`. Added `GatewayRouter` to actually route CARD/UPI/WALLET payments through a gateway (CASH stays gateway-free, as COD should be), and `AbstractMockGateway` gives realistic simulated behavior (latency, ~6% random decline with a real-sounding reason) instead of always succeeding.
- **Checkout never called payment-service at all**: `Cart.jsx` placed orders but never hit `/api/payments/initiate`. Added a payment method selector and wired checkout to actually initiate (and reflect the result of) a payment.
- **`render.yaml`**: added a `shared-secrets` envVarGroup so `JWT_SECRET` and `INTERNAL_API_KEY` are generated once and auto-shared across all 10 services (previously `JWT_SECRET` required manually copying the gateway's generated value into every other service by hand — an easy way for secrets to drift out of sync). Corrected the header comment, which claimed the blueprint provisions 9 databases when it doesn't.
- **Maven upgraded to 3.9.16** (from 3.9.9) across the root and all 11 services' `.mvn/wrapper/maven-wrapper.properties` — current latest stable release (Maven 4.0.0 is still release-candidate-only).
- **Review → Restaurant rating sync was silently broken**, the same way notification delivery was: `RatingService.syncToRestaurantService()` sent no auth header at all, and restaurant-service had no internal-service-key support in its `JwtAuthFilter` (unlike user-service/notification-service). Every rating sync 401'd and was swallowed by the catch-and-log-warn block, so restaurant cards never reflected real ratings. Fixed by adding internal-key support to restaurant-service and having review-service send it — verified live: a submitted review now actually moves the restaurant's `averageRating`/`totalReviews`.
- **notification-service's `/actuator/health` always reported DOWN**: Spring Boot auto-registers a mail health indicator because `spring.mail.host` is always configured, and it fails whenever `MAIL_USERNAME`/`MAIL_PASSWORD` are unset — the documented default (log-only mode). Since Render's health check hits `/actuator/health` for every service, this would make Render treat the service as perpetually unhealthy whenever real Gmail credentials aren't set. Fixed with `management.health.mail.enabled=false`.
- **Mock data seeding**: added `scripts/seed-mock-data.mjs` — populates realistic demo restaurants/menus/users/orders/reviews via the real REST APIs (works against local or a live deployment). See "Mock data" under Local Development Setup.

---

## Known Limitations (Free Tier)

- Render free services **spin down after 15 min of inactivity** — first request after idle takes ~30s to cold-start.
- Render free databases are **deleted after 90 days** — export data regularly or upgrade.
- Rate limiting is in-memory per gateway instance; a multi-instance deployment needs Redis.
- Payment gateways (Razorpay/PhonePe/Paytm/GooglePay/Cred/BharatPe) are **simulated**, not real integrations — there are no merchant credentials for this project. `AbstractMockGateway` gives realistic behavior (latency, a small random decline rate, proper failure reasons) instead of always returning success, and `PaymentService`/`GatewayRouter` actually route through it based on payment method. To use a real provider, replace the body of one gateway class with real SDK calls.
- Email/SMS in notification-service run in **log-only mode by default** (see the Environment Variables section) — set `MAIL_USERNAME`/`MAIL_PASSWORD` (Gmail SMTP) and/or `FAST2SMS_API_KEY` (free-tier SMS) to send real messages.

---

## License

MIT
