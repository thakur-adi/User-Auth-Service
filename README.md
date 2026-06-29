## User & Auth Service

The service implements JWT-based authentication using Spring Security with refresh token rotation, database-backed token validation, server-side token revocation, and a clean layered architecture. It is designed to explore backend authentication patterns commonly used in modern applications while following clean code and low-level design principles.

> **Note:** This service is part of a larger E-commerce Microservices project.


---

## Highlights

- **Stateful JWT revocation** — tokens are persisted in DB and checked on every request; logout and password reset take effect immediately, not at token expiry
- **Dual-token architecture** — short-lived access token in Authorization header + long-lived refresh token in HttpOnly cookie
- **Global session invalidation** — password reset soft-deletes all tokens across all devices, not just the current session
- **Custom Spring Security filter chain** — OncePerRequestFilter with endpoint-aware token extraction (access vs refresh) wired directly into the security config
- **LLD-compliant design** — service layer built with clean Low-Level Design principles
- **Secrets externalized** — JWT secret and DB credentials never hardcoded, loaded from environment variables at runtime

---

## Architecture Overview

```
Client
  │
  ▼
OncePerRequestFilter (Custom JWT Filter)
  │  ├── Extracts token (Access → Authorization Header / Refresh → HttpOnly Cookie)
  │  ├── Validates JWT signature + claims
  │  ├── Checks token existence + validity in DB (revocation support)
  │  └── Loads claims into Spring Security Context
  │
Spring Security Filter Chain
  │  ├── /user/login      → permitAll()
  │  ├── /user/signup     → permitAll()
  │  └── /**              → authenticated()
  │
Service Layer (LLD-compliant)
  │
Repository Layer (Soft-delete token management)
```
---

## Features

### Authentication
- **JWT-based dual-token system** — Access token in Authorization header, Refresh token in HttpOnly cookie
- **Stateful token validation** — Every token is verified against the database, enabling real-time revocation
- **Soft-delete on logout** — Tokens are marked as deleted rather than purged, preserving audit history
- **Refresh token flow** — Issues a new access + refresh token pair, overwrites existing tokens in DB

### Security
- **BCrypt password hashing** — Passwords never stored in plain text
- **Custom OncePerRequestFilter** — Intercepts requests, validates tokens, and populates the security context
- **AuthenticationEntryPoint** — Returns structured error responses on auth failures instead of default Spring error pages
- **Password reset with global logout** — On successful password reset, all active sessions across all devices are invalidated

### User Management
- Signup and login with email/password
- **View profile** — fetch current user's details from security context
- **Update profile** — modify user details (excluding password) via the same "/profile" endpoint, differentiated by HTTP method
- Password reset via secure validation flow (separate from profile update, logs out all devices)
- Input validation with meaningful error messages (null, empty, invalid format, etc.)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot |
| Security | Spring Security |
| Auth | JWT (custom validator) |
| Password Hashing | BCrypt |
| Database | (MySQL) |
| Build Tool | Maven |

---

## API Endpoints

All endpoints are prefixed with the context path "/user".

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| `POST` | `/user/signup` | No | Register a new user |
| `POST` | `/user/login` | No | Login, returns access + refresh tokens |
| `GET` | `/user/profile` | Access Token | View current user's profile |
| `POST` | `/user/profile` | Access Token | Update profile details (excluding password) |
| `POST` | `/user/reset` | Access Token | Reset password, invalidates all sessions |
| `POST` | `/user/auth/refresh` | Refresh Token (Cookie) | Issue new access + refresh token pair |
| `POST` | `/user/auth/logout` | Refresh Token (Cookie) | Invalidate current session |

---

## Token Flow

```
LOGIN
  └──▶ Generate Access Token (short-lived)
  └──▶ Generate Refresh Token (long-lived, HttpOnly cookie)
  └──▶ Persist both tokens in DB
```


```
AUTHENTICATED REQUEST (/profile,/reset)
  └──▶ Filter extracts Access Token from Authorization header
  └──▶ Validates JWT signature + expiry + Checks token exists in DB and is not soft-deleted
  └──▶ Loads user claims into SecurityContext
```


```
REFRESH
  └──▶ Validate Refresh Token from cookie + Checks token exists in DB and is not soft-deleted
  └──▶ Overwrite both tokens in DB with new pair
  └──▶ Return new Access Token + new Refresh Token cookie
```

```
LOGOUT
  └──▶ Soft-delete current tokens in DB
```

```
PASSWORD RESET
  └──▶ Validate request
  └──▶ Update password (BCrypt)
  └──▶ Soft-delete ALL tokens for the user (logout from every device)
```

---

## Design Decisions

**Why stateful JWTs?**

-> Pure stateless JWTs cannot be revoked before expiry. By persisting tokens in the DB and checking on every request, the service supports immediate revocation on logout, password reset, or suspected compromise — a requirement for any real-world auth system.


**Why an HttpOnly cookie for a refresh token?**

-> Storing the refresh token in an HttpOnly cookie prevents JavaScript access, mitigating XSS-based token theft.


**Why OncePerRequestFilter?**

-> Guarantees the filter runs exactly once per request, regardless of the servlet container, avoiding duplicate processing in forwarded/dispatched requests.

---

## Environment Variables

Sensitive config is externalized via environment variables — never hardcoded. Set the following before running:

| Variable | Description |
|---|---|
| `DATASOURCE_URL` | JDBC connection URL (e.g. "jdbc:mysql://localhost:3306/user_auth_database") |
| `DATASOURCE_USERNAME` | Database username |
| `DATASOURCE_PASSWORD` | Database password |
| `JWT_SECRET_KEY` | Secret key used for signing JWTs |

These are referenced in **application.properties** as:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

jwt.secret=${JWT_SECRET}
```

---

## Getting Started

```bash
# Clone the repository
git clone https://github.com/your-username/user-auth-service.git

# Set required environment variables
export DB_URL=your_db_url
export DB_USERNAME=your_db_user
export DB_PASSWORD=your_db_password
export JWT_SECRET=your_secret_key

# Run the service (context path: /user)
./mvnw spring-boot: run
```

---

## Known Gaps & Roadmap

-  Rate limiting on "/auth/login" and "/auth/signup"
-  Account lockout after N failed login attempts
-  Email verification on signup
-  Structured audit logging (login events, failures, IP tracking)

---
