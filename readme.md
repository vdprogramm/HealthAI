# HealthAI – AI-Powered Healthcare Appointment System

A full-stack healthcare appointment booking application with AI-assisted medical specialty recommendations.

The project demonstrates RESTful API development, PostgreSQL database design, JWT authentication, transaction handling, and integration between Spring Boot and FastAPI.

> **Disclaimer:** This is an educational portfolio project. The AI supports specialty recommendations only and does not replace professional medical diagnosis.

---

## 1. Technology Stack

| Layer | Technologies |
|---|---|
| Frontend | React, TypeScript, Vite |
| Backend | Java 21, Spring Boot 4 |
| Database | PostgreSQL, Spring Data JPA |
| Security | Spring Security, JWT, BCrypt |
| AI Service | Python, FastAPI, OpenRouter |
| API Communication | RESTful API, HTTP/JSON |

## 2. System Architecture

```text
React Frontend (5173)
        |
        v
Spring Boot API (8080)
        |
        +---- PostgreSQL
        |
        +---- FastAPI (8000)
                  |
                  v
               OpenRouter
```

## 3. Main Features

### Authentication
- Patient registration and login
- BCrypt password hashing
- JWT authentication

### Appointment Management
- Browse doctors and schedules
- Book and cancel appointments
- View appointment history
- Pessimistic locking for booking

### AI-Assisted Triage
- Submit symptoms
- Receive specialty recommendations
- Find doctors matching the specialty
- View available appointment schedules

### Medical Records
- Store patient symptoms
- Store AI-recommended specialty
- Record doctor-entered diagnosis and prescription

---

## 4. Implementation Status

| Feature | Status |
|---|---|
| Spring Boot REST APIs | Code written |
| PostgreSQL integration | Code written |
| JWT authentication | Code written |
| FastAPI integration | Tested during development |
| React frontend | Code written |
| End-to-end automated testing | Not yet verified |
| Production deployment | Not completed |

## 5. Known Limitations

- Doctor and Admin RBAC are not yet implemented.
- Medical record authorization requires further work.
- Concurrent booking behavior needs dedicated testing.
- AI recommendations have not been clinically validated.
- The application is not production-ready.
