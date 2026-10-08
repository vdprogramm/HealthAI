# HealthAI – AI-Powered Healthcare Appointment System

A full-stack healthcare appointment booking application with AI-assisted medical specialty recommendations.

The system allows patients to register, log in, describe their symptoms, receive AI-generated specialty recommendations, browse available doctors and appointment schedules, and manage their medical appointments.

The project focuses on backend development, RESTful API design, relational database management, authentication, transaction handling, and integration between Java Spring Boot and a Python FastAPI AI service.

> **Disclaimer:** This is an educational and portfolio project, not a production-ready medical platform. The AI component supports appointment routing and specialty recommendations only. It does not provide medical diagnoses, prescriptions, or professional medical advice.

## 1. Technology Stack

| Layer | Technologies |
|---|---|
| Frontend | React, TypeScript, Vite |
| UI | CSS, Lucide React |
| HTTP Client | Axios |
| Backend | Java 21, Spring Boot 4 |
| Security | Spring Security, JWT, BCrypt |
| Database | PostgreSQL |
| ORM | Spring Data JPA, Hibernate |
| AI Microservice | Python, FastAPI |
| AI Integration | OpenRouter API |
| Communication | RESTful APIs, HTTP/JSON |
| Build Tools | Maven, npm |

## 2. System Architecture

```text
                  React Frontend
                   (Port 5173)
                        |
                        | REST API / JWT
                        v
                Spring Boot Backend
                   (Port 8080)
                        |
              +---------+---------+
              |                   |
              v                   v
          PostgreSQL         FastAPI Service
           Database            (Port 8000)
                                  |
                                  v
                              OpenRouter
                                  |
                                  v
                              AI Model
```

### Backend responsibilities

- Handle patient registration and authentication.
- Manage doctors and appointment schedules.
- Process appointment booking requests.
- Apply database transactions and pessimistic locking.
- Store medical records.
- Communicate with the AI microservice.

### AI service responsibilities

- Receive symptom descriptions.
- Recommend a suitable medical specialty.
- Return a priority level and a brief explanation.
- Provide safety warnings where appropriate.
- Return structured JSON responses.

The AI service does not directly modify the PostgreSQL database.

## 3. Main Features

### 3.1. Patient Authentication

Implemented API functionality:

- Patient registration.
- Patient login.
- Password hashing using BCrypt.
- JWT generation and verification.
- Authentication protection for selected endpoints.

### 3.2. Doctor Management

Backend functionality includes:

- Retrieve doctors.
- Retrieve a doctor by ID.
- Search doctors by specialty.
- Create doctor records.
- Delete doctor records.

The frontend provides a doctor listing page.

### 3.3. Appointment Scheduling

The system supports:

- Creating doctor schedules.
- Retrieving schedules by doctor.
- Booking an appointment.
- Viewing patient appointment history.
- Canceling an appointment.
- Checking whether a schedule has already been booked.

Appointment booking uses a transactional service with pessimistic locking on the selected schedule.

This mechanism is intended to prevent multiple confirmed appointments from being created for the same schedule.

Concurrent booking behavior still requires dedicated integration testing before making a verified concurrency guarantee.

### 3.4. AI-Assisted Specialty Recommendation

Patients can enter symptom descriptions through the frontend.

The request is processed through:

1. React sends symptoms to Spring Boot.
2. Spring Boot forwards the request to FastAPI.
3. FastAPI communicates with OpenRouter.
4. The AI service returns structured triage information.
5. Spring Boot searches for doctors matching the suggested specialty.
6. The frontend displays doctors and their available schedules.

The expected AI response includes:

```json
{
  "chuyen_khoa": "Tim mạch",
  "muc_do_khan_cap": "CAO",
  "giai_thich_ngan": "Các triệu chứng được mô tả cần được đánh giá bởi chuyên khoa phù hợp.",
  "canh_bao": "Nếu triệu chứng nghiêm trọng hoặc xấu đi, hãy tìm hỗ trợ y tế khẩn cấp."
}
```

Supported priority values:

- `THAP`
- `TRUNG_BINH`
- `CAO`
- `CAP_CUU`

AI responses may vary depending on the model and input. The system has not been clinically validated.

### 3.5. Medical Records

Medical records are associated with appointments.

Stored information includes:

- Patient-reported symptoms.
- AI-recommended specialty.
- Doctor-entered diagnosis.
- Doctor-entered prescription information.

The AI component does not automatically generate a medical diagnosis or prescription.

The code includes an appointment flow intended to create a medical record automatically when an AI-assisted appointment is booked.

### 3.6. Frontend

The React frontend includes the following pages:

| Page | Description |
|---|---|
| Login | Patient authentication |
| Register | Patient account registration |
| Dashboard | Main patient dashboard |
| AI Triage | Symptom submission and specialty recommendations |
| Doctors | Doctor listing |
| Appointments | Patient appointment history and cancellation |
| Appointment Detail | Detailed appointment information |
| Medical Record | Medical record information |

The frontend uses Axios to communicate with the Spring Boot backend.

JWT tokens are attached to authenticated requests through an Axios interceptor.

## 4. Database Design

The application uses PostgreSQL with five main business tables.

### Patients

Stores patient account information.

Important fields:

- `id`
- `ho_ten`
- `email`
- `mat_khau`
- `so_dien_thoai`

### Doctors

Stores doctor information.

Important fields:

- `id`
- `ten_bac_si`
- `chuyen_khoa`
- `gia_kham`

### Schedules

Stores appointment time slots.

Important fields:

- `id`
- `doctor_id`
- `ngay_kham`
- `gio_bat_dau`
- `gio_ket_thuc`

### Appointments

Stores patient appointment information.

Important fields:

- `id`
- `patient_id`
- `doctor_id`
- `schedule_id`
- `trang_thai`
- `created_at`

Appointment statuses:

- `PENDING`
- `CONFIRMED`
- `CANCELED`

### Medical Records

Stores medical information associated with appointments.

Important fields:

- `id`
- `appointment_id`
- `trieu_chung_nhap_vao`
- `chuyen_khoa_goi_y_boi_ai`
- `chan_doan_cua_bac_si`
- `don_thuoc`

### Database Relationships

```text
Patients
   |
   | 1:N
   v
Appointments <------ Doctors
   |                    |
   |                    | 1:N
   |                    v
   +---------------- Schedules
   |
   | 1:1
   v
MedicalRecords
```

Each appointment belongs to one patient, one doctor, and one schedule.

Each medical record belongs to one appointment.

## 5. REST API Endpoints

### Authentication

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register patient |
| POST | `/api/auth/login` | Patient login |

### Doctors

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/doctors` | List doctors |
| GET | `/api/doctors/{id}` | Doctor details |
| GET | `/api/doctors/specialty` | Filter by specialty |
| POST | `/api/doctors` | Create doctor |
| DELETE | `/api/doctors/{id}` | Delete doctor |

### Schedules

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/schedules` | List schedules |
| GET | `/api/schedules/{id}` | Schedule details |
| GET | `/api/schedules/doctor/{doctorId}` | Doctor schedules |
| POST | `/api/schedules` | Create schedule |
| DELETE | `/api/schedules/{id}` | Delete schedule |

### Appointments

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/appointments` | Book appointment |
| POST | `/api/appointments/ai` | Book AI-assisted appointment |
| GET | `/api/appointments/me` | Patient appointments |
| PATCH | `/api/appointments/{id}/cancel` | Cancel appointment |

### Medical Records

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/medical-records` | Create medical record |
| GET | `/api/medical-records/appointment/{appointmentId}` | Get medical record |
| PUT | `/api/medical-records/{id}/diagnosis` | Update diagnosis |

### AI Integration

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/ai/triage` | AI-assisted specialty recommendation |

### FastAPI Service

| Method | Endpoint | Description |
|---|---|---|
| GET | `/` | Service information |
| GET | `/health` | Health check |
| POST | `/api/triage` | AI triage processing |

These endpoints describe the application code developed for the project. Their complete integration status should be verified in the local environment.

## 6. Project Structure

The system is organized into three application components.

### Spring Boot Backend

```text
healthcare-api/
└── src/main/java/com/vinh/healthcare/
    ├── config/
    ├── controller/
    ├── dto/
    ├── entity/
    ├── exception/
    ├── repository/
    ├── security/
    ├── service/
    └── HealthcareApiApplication.java
```

### FastAPI AI Service

```text
healthcare-ai/
├── app/
│   ├── main.py
│   ├── config.py
│   ├── schemas.py
│   └── services/
│       └── triage_service.py
├── requirements.txt
└── .env
```

### React Frontend

```text
healthcare-web/
├── src/
│   ├── api/
│   ├── components/
│   ├── pages/
│   ├── types/
│   ├── App.tsx
│   ├── main.tsx
│   └── index.css
├── package.json
└── vite.config.ts
```

## 7. Local Setup

### Prerequisites

- Java 21
- Maven or Maven Wrapper
- PostgreSQL
- Python 3
- Node.js and npm
- OpenRouter API key

### Step 1: Configure PostgreSQL

Create a database:

```sql
CREATE DATABASE healthcare_db;
```

Configure Spring Boot in `application.properties`:

```properties
spring.application.name=healthcare-api

spring.datasource.url=jdbc:postgresql://localhost:5432/healthcare_db
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update

server.port=8080

jwt.secret=${JWT_SECRET}
jwt.expiration=86400000

ai.service.url=http://127.0.0.1:8000
```

Provide the database password and a sufficiently strong Base64-encoded JWT secret through environment variables.

Do not commit real credentials.

### Step 2: Start Spring Boot

From the backend directory:

```powershell
.\mvnw.cmd spring-boot:run
```

Backend URL:

```text
http://localhost:8080
```

### Step 3: Configure FastAPI

Create a `.env` file inside the AI service directory:

```env
OPENROUTER_API_KEY=your_api_key
OPENROUTER_MODEL=your_supported_model
FASTAPI_PORT=8000
```

Install dependencies:

```powershell
python -m venv .venv

.\.venv\Scripts\python.exe -m pip install -r requirements.txt
```

Start the AI service:

```powershell
.\.venv\Scripts\python.exe -m uvicorn app.main:app --reload --port 8000
```

FastAPI documentation:

```text
http://127.0.0.1:8000/docs
```

### Step 4: Start React Frontend

From the frontend directory:

```powershell
npm install
npm run dev
```

Frontend URL, assuming the default Vite port:

```text
http://localhost:5173
```

The frontend expects the backend API at:

```text
http://localhost:8080/api
```

## 8. Example API Requests

### Patient Registration

```http
POST /api/auth/register
Content-Type: application/json
```

```json
{
  "hoTen": "Nguyen Van A",
  "email": "patient@example.com",
  "matKhau": "ExamplePassword123!",
  "soDienThoai": "0900000000"
}
```

### AI Triage

```http
POST /api/ai/triage
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json
```

```json
{
  "trieuChung": "Tôi bị đau đầu và chóng mặt trong vài ngày gần đây"
}
```

### AI-Assisted Appointment Booking

```http
POST /api/appointments/ai
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json
```

```json
{
  "scheduleId": 1,
  "trieuChung": "Tôi bị đau đầu và chóng mặt",
  "chuyenKhoaAi": "Thần kinh"
}
```

The schedule ID must refer to an existing, available schedule.

## 9. Implementation Status

The following status reflects the implementation work described during development, rather than an independent audit of the final repositories.

| Component | Status |
|---|---|
| Database entities and relationships | Code written |
| JWT authentication | Code written |
| Doctor APIs | Code written |
| Schedule APIs | Code written |
| Appointment booking | Code written |
| Pessimistic locking | Implemented in service logic |
| FastAPI AI triage | Successfully tested during development |
| Spring Boot–FastAPI communication | Successfully tested during development |
| Medical record APIs | Code written |
| AI-assisted appointment booking | Code written; end-to-end verification pending |
| React patient pages | Code written |
| Frontend API integration | Connected in code; full verification pending |
| Automated integration tests | Not yet confirmed |
| Production deployment | Not completed |
| Doctor/Admin RBAC | Not implemented |

**Important:** Writing API and frontend code does not automatically mean every feature has been successfully tested. Full end-to-end testing is still required.

## 10. Known Limitations

### Authentication and Authorization

- The current authentication model focuses on patient accounts.
- Dedicated Doctor and Admin authentication has not been implemented.
- Some management endpoints do not yet enforce role-based authorization.
- Medical record access requires stronger ownership and role checks before real-world use.
- JWT storage in browser localStorage is suitable for this development prototype but should be reconsidered for production security.

### Appointment Scheduling

- Pessimistic locking is implemented, but dedicated concurrent integration tests are still required.
- Overlapping schedule intervals are not fully validated.
- Additional database constraints would improve booking consistency.
- Booking cancellation rules require further business validation.

### AI Service

- AI recommendations depend on the external model and API availability.
- Responses are not guaranteed to be medically accurate.
- The system has not undergone clinical evaluation.
- AI output validation and emergency handling need further hardening.
- The AI recommendation submitted during appointment booking can be modified by the client; server-side triage result persistence is a future improvement.

### Medical Records

- Medical records are intended for demonstration using synthetic data.
- Real patient information should not be entered into this prototype.
- Production use would require stricter access controls, audit logging, privacy safeguards, and regulatory review.

### Frontend

- The frontend provides the main patient workflow.
- Doctor and Admin dashboards have not been implemented.
- Some error-handling and loading states can be improved.
- Responsive behavior and cross-browser compatibility require further testing.

## 11. Future Improvements

Potential improvements include:

- Doctor and Admin roles with RBAC.
- Secure medical record ownership checks.
- Automated unit and integration tests.
- Concurrent booking tests.
- Database migration management with Flyway or Liquibase.
- Improved schedule overlap validation.
- Server-side storage of AI triage sessions.
- Better AI output validation and emergency escalation.
- Docker-based local development.
- CI/CD pipelines.
- Production deployment and monitoring.

## 12. Learning Outcomes

This project provides practical experience with:

- Java Spring Boot backend architecture.
- RESTful API development.
- PostgreSQL relational database design.
- Spring Data JPA and Hibernate.
- JWT authentication and password hashing.
- Transaction management.
- Pessimistic locking for booking workflows.
- Python FastAPI microservices.
- External LLM API integration.
- React and TypeScript frontend development.
- Frontend–backend integration.
- Error handling and API testing.

## 13. Project Purpose

HealthAI was developed as a learning and portfolio project to demonstrate backend engineering skills and practical AI service integration.

Its main technical focus is combining transactional appointment booking with an independently running AI-assisted specialty recommendation service.

The project is not intended to replace medical professionals or operate as a real healthcare service without substantial additional engineering, security, testing, and clinical review.

## 14. License

No license has been specified for this project.#   H e a l t h A I  
 