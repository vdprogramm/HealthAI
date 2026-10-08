# HealthAI — AI-Assisted Healthcare Appointment Management

> A full-stack healthcare appointment application combining a Java Spring Boot REST API, a Python FastAPI AI service, and a React web application.

**Live Demo:** [https://health-ai-steel.vercel.app](https://health-ai-steel.vercel.app)

## Demo Accounts

Use the following **test-only accounts** to explore the role-based dashboards. Passwords are intentionally **not published in this public repository**. Configure separate, limited-access demo credentials and share them with reviewers privately.

| Role | Login email | Password |
| --- | --- | --- |
| Admin | `admin@healthai.com` | Request demo password privately |
| Doctor 1 — Nguyễn Văn Minh | `doctor1@healthai.com` | Request demo password privately |
| Doctor 2 — Trần Thị Lan | `doctor2@healthai.com` | Request demo password privately |
| Doctor 3 — Lê Quốc Huy | `doctor3@healthai.com` | Request demo password privately |
| Doctor 4 — Phạm Thu Hà | `doctor4@healthai.com` | Request demo password privately |
| Doctor 5 — Hoàng Đức Anh | `doctor5@healthai.com` | Request demo password privately |
| Doctor 6 — Bùi Thị Phương | `doctor6@healthai.com` | Request demo password privately |

> The initializer defines additional Doctor 7 and 8 accounts, but this public demo table intentionally lists Doctor 1–6 as requested. Login availability depends on the deployed database. Set a distinct temporary password for demo users; never publish real Admin or Doctor passwords.

> **Project status:** Portfolio / MVP in development. Features below are based on the current repository; availability and successful operation depend on deployment, database content, and integration testing. This project is a demonstration, **not a medical device**. AI suggestions are not a diagnosis and must not replace professional medical evaluation.

## Overview

HealthAI helps patients describe symptoms, receive an AI-assisted specialty suggestion, find matching doctors, book available appointments, and view their appointment history. Doctors can review their assigned appointments and associated medical records. Administrators manage doctor and schedule information, monitor appointments, and record payments collected **in person** at the clinic.

The system separates the application backend from the AI service:

```text
Patient / Doctor / Admin
          |
     React + Vite
          |
      REST / JWT
          |
  Spring Boot API ---------> PostgreSQL (Neon)
          |
          | HTTP
          v
    FastAPI AI Service
```

## Technology stack

| Layer | Technology |
| --- | --- |
| Web frontend | React 19, TypeScript, Vite, React Router, Axios, Lucide icons |
| Backend | Java 21, Spring Boot, Spring Security, Spring Data JPA, Maven |
| Authentication | JWT, BCrypt password hashing, role-based access control (RBAC) |
| Database | PostgreSQL (hosted on Neon in the deployed environment) |
| AI microservice | Python, FastAPI, Pydantic, HTTPX |
| Hosting | Vercel (web), Render (backend and AI) |

## Roles and functionality

### Patient

- Register and sign in.
- Describe symptoms and request AI-assisted triage.
- Review suggested specialty, severity information, and matching doctors when available.
- Browse doctors and available appointment schedules.
- Book a time slot and view appointment details/history.
- View associated medical record information where available.
- See consultation fees and offline payment status.

### Doctor

- Sign in with a doctor account.
- View appointments assigned to the authenticated doctor.
- Review medical records associated with those appointments.
- See appointment dates, fees, and payment status.

### Admin

- Sign in with an administrator account.
- View doctor listings and dashboard metrics.
- View appointment and offline-payment information.
- Mark eligible appointments as **paid** after payment is received at the clinic.
- Manage doctor/schedule resources through protected backend endpoints.
- View two dashboard visualizations: paid consultation fees by appointment date and appointment counts by status.
- View the total number of registered patients from the database.

**Implementation note:** Not every backend operation has a complete management screen. For example, some doctor/schedule administration workflows still rely on APIs. Doctor medical-record authoring and end-to-end testing should be verified before describing them as complete.

## Appointment and payment workflow

1. A patient signs in and chooses a doctor and an available appointment slot (or starts from AI triage).
2. The backend validates the selected slot and creates an appointment.
3. The consultation fee is captured for the appointment; payment starts as `UNPAID`.
4. The patient attends the clinic and pays **directly at the clinic**.
5. An authenticated Admin confirms collection, updating the payment state to `PAID`.
6. The patient/doctor interfaces can display the updated payment status.

**No online payment gateway is integrated.** The Admin confirmation must only be used after payment is actually received. This is not a substitute for formal accounting or receipt issuance.

For demo data, the Admin-only endpoint `POST /api/schedules/generate-demo` creates upcoming time slots for doctors already present in the database. It skips slots with matching doctor, date, and start time. These are **sample slots**, not real clinical availability. Avoid running demo schedule generation in a real clinic environment.

## Repository structure

```text
HealthAI/
├── healthcare-api/     # Spring Boot REST API, security, JPA, business logic
├── healthcare-ai/      # Python FastAPI AI microservice
└── healthcare-web/     # React + TypeScript frontend
```

## Running locally

### Requirements

- JDK 21 and Maven (or the included Maven wrapper, if present).
- Node.js and npm compatible with the project's Vite version.
- Python 3 and pip.
- A PostgreSQL database.
- Environment variables for credentials and service URLs.

### 1. Clone the repository

```bash
git clone https://github.com/vdprogramm/HealthAI.git
cd HealthAI
```

### 2. Configure and start the backend

The Spring Boot configuration is in `healthcare-api/src/main/resources/application.properties`. Configure database credentials and JWT secrets using environment variables rather than committing secrets.

Common configuration keys:

| Environment variable | Purpose |
| --- | --- |
| `DB_PASSWORD` | PostgreSQL password |
| `JWT_SECRET` | Signing secret for JWTs |
| `AI_SERVICE_URL` | FastAPI base URL (local default: `http://127.0.0.1:8000`) |
| `PORT` | Backend HTTP port (default: `8080`) |
| `ADMIN_PASSWORD` | Password used by the optional Admin initializer |
| `DOCTOR_PASSWORD` | Password used by the optional Doctor initializer |

Set a valid PostgreSQL JDBC URL and username for your local database in your **local, untracked** configuration. The repository's current configuration targets a deployed Neon instance, so **do not run migrations or demo seeds against production by accident**.

```bash
cd healthcare-api
mvn spring-boot:run
```

The API normally listens at `http://localhost:8080/api`.

### 3. Start the AI service

```bash
cd healthcare-ai
python -m venv .venv
# Activate .venv for your operating system
pip install -r requirements.txt
```

Run the FastAPI application with Uvicorn using the module that defines the `app` instance in this directory. Confirm the entry point and any provider-specific environment variables from the AI service code before starting it. The backend's `AI_SERVICE_URL` must point to the running service.

### 4. Start the web app

```bash
cd healthcare-web
npm install
npm run dev
```

Open the URL printed by Vite (commonly `http://localhost:5173`).

**Important:** `healthcare-web/src/api/api.ts` currently contains a deployed Render API base URL. For local full-stack testing, update this configuration to your local backend URL, preferably through a Vite environment variable in a future refactor.

## Selected API endpoints

The following routes exist in the Spring Boot code. Endpoints requiring authentication must include `Authorization: Bearer <JWT>`.

| Method | Endpoint | Access / purpose |
| --- | --- | --- |
| `POST` | `/api/auth/register` | Patient registration |
| `POST` | `/api/auth/login` | Sign in |
| `POST` | `/api/ai/triage` | Authenticated symptom triage |
| `GET` | `/api/doctors` | List doctors |
| `GET` | `/api/schedules` | List schedules |
| `GET` | `/api/schedules/doctor/{doctorId}` | Schedules for a doctor |
| `POST` | `/api/schedules/generate-demo` | Admin: create sample slots |
| `GET` | `/api/admin/dashboard` | Admin: dashboard and patient count |
| `GET` | `/api/admin/appointments` | Admin: list appointments |
| `PATCH` | `/api/admin/appointments/{id}/mark-paid` | Admin: confirm offline payment |
| `GET` | `/api/doctor/me/appointments` | Doctor: own appointments |
| `GET` | `/api/doctor/me/medical-records` | Doctor: related medical records |

The project also contains patient appointment and medical-record endpoints. Check the corresponding controllers for exact request/response schemas before integrating new clients.

### Example: create sample schedules

```http
POST /api/schedules/generate-demo
Authorization: Bearer <ADMIN_JWT>
```

No request body is needed. A successful response returns the number of newly created slots:

```json
{
  "created": 48
}
```

The number above is **illustrative**, not a guaranteed result. If the request takes a long time, check Render logs and existing rows in Neon before retrying; the current implementation may issue many database operations.

## Dashboard analytics

The Admin dashboard uses appointment records to show:

1. **Collected consultation fees:** sums appointments marked `PAID`, grouped by the **scheduled appointment date**. This is **not** revenue grouped by the actual collection timestamp.
2. **Appointment status:** counts of `CONFIRMED`, `PENDING`, and `CANCELED` appointments.

The patient count comes from `PatientRepository.count()`, not from a hard-coded number.

## Security and privacy considerations

- JWT-based authentication and role checks for Admin and Doctor routes.
- BCrypt password hashing for stored account credentials.
- Doctor workspace endpoints resolve the doctor from the authenticated identity.
- Never commit passwords, JWT secrets, database credentials, or actual patient medical information.
- Use test patients and fictional symptoms for public demos.
- Production readiness requires further review of authorization on every patient/medical-record endpoint, input validation, secret rotation, auditing, data retention, and clinical-data privacy obligations.

## Testing and limitations

Before presenting the project as fully complete, validate:

- Backend compilation and tests: `cd healthcare-api && mvn test`.
- Frontend type checking and build: `cd healthcare-web && npm run build`.
- Role-based access with Patient, Doctor, and Admin accounts.
- Appointment availability and prevention of duplicate bookings.
- Payment transitions (`UNPAID` → `PAID`) and cancellation rules.
- AI service downtime/error handling and the full triage-to-booking journey.
- Real database schema updates and deployment behavior on Render/Neon.

Known limitations include reliance on deployed-service configuration in some local defaults, potentially slow bulk demo schedule creation, incomplete Admin CRUD UI, and medical-record workflows that still need verification. Render free-tier services may also take time to wake after inactivity.

## Roadmap

- [ ] Finish and test the doctor's medical-record authoring workflow.
- [ ] Add complete Admin CRUD interfaces for doctors and schedules.
- [ ] Improve availability generation with efficient batch database operations.
- [ ] Track the actual payment collection timestamp and payment audit trail.
- [ ] Add automated API, integration, and access-control tests.
- [ ] Move environment-specific frontend/backend settings into dedicated configuration.
- [ ] Complete security and privacy review before any real-world clinical use.

## Disclaimer

HealthAI is a **software engineering portfolio project**. AI triage output is informational only, may be inaccurate, and must not be used as a standalone medical diagnosis or emergency decision system. For severe or urgent symptoms, seek appropriate professional or emergency medical care.

---

**Maintainer:** [vdprogramm](https://github.com/vdprogramm)  
**License:** No license has been declared in this README; consult the repository for applicable terms.
