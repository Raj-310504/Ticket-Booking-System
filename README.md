  # Ticket Booking System

Spring Boot REST API for railway ticket booking with role-based access, schedule search, booking, cancellation, RAC handling, and admin dashboard.

## Tech Stack
- Java 21
- Spring Boot 4.0.3
- Spring Data JPA (Hibernate)
- Spring Security (HTTP Basic, stateless)
- PostgreSQL
- SpringDoc OpenAPI / Swagger UI
- Maven

## Features
- User registration, login, and password change
- Role-based authorization (`USER`, `ADMIN`)
- Station, train, and schedule management (admin)
- Train schedule search (public)
- Ticket booking with preferred coach support
- RAC allocation and auto-promotion on cancellation
- Admin dashboard counters

## Project Structure
- `src/main/java/com/example/TicketBooking/controller` - REST controllers
- `src/main/java/com/example/TicketBooking/service` - business logic
- `src/main/java/com/example/TicketBooking/repository` - JPA repositories (native queries)
- `src/main/java/com/example/TicketBooking/entity` - database entities
- `src/main/resources/application.properties` - app and DB configuration

## Prerequisites
- Java 21+
- PostgreSQL 14+
- Maven (or use Maven Wrapper included in repo)

## Configuration
Update `src/main/resources/application.properties` before running:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ticketBookingDB
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
server.port=8080
```

Current project uses `ddl-auto=update`, so tables are created/updated automatically.

## Run Locally

### Windows
```powershell
.\mvnw.cmd spring-boot:run
```

### Linux/macOS
```bash
./mvnw spring-boot:run
```

Application starts at: `http://localhost:8080`

## API Documentation
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Authentication & Authorization
Security uses HTTP Basic authentication.

Public endpoints:
- `POST /api/users/register`
- `POST /api/users/login`
- `GET /api/trains/**`
- `GET /api/schedules/**`
- Swagger/OpenAPI endpoints

Admin-only endpoints:
- `GET /api/admin/**`
- `POST /api/stations/**`
- `POST /api/trains/**`
- `POST /api/schedules/**`

All other endpoints require authentication.

### Important
`/api/users/login` validates credentials and returns user info, but **does not issue a token**. For protected APIs, send HTTP Basic credentials (`email:password`) in each request.

## Main Endpoints

### Users
- `POST /api/users/register`
- `POST /api/users/login`
- `POST /api/users/change-password`

### Stations
- `POST /api/stations` (ADMIN)

### Trains
- `POST /api/trains` (ADMIN)
- `GET /api/trains`

### Schedules
- `POST /api/schedules` (ADMIN)
- `GET /api/schedules/search?sourceStationId=1&destinationStationId=2&journeyDate=2026-03-10&page=0&size=5`

### Booking
- `POST /api/book`
- `GET /api/book/my-bookings?page=0&size=5`
- `GET /api/book/{pnr}`
- `POST /api/book/{bookingId}/cancel`

### Admin
- `GET /api/admin/dashboard` (ADMIN)

## Sample Requests

### Register User
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Rahul Sharma",
    "email": "rahul@example.com",
    "password": "Rahul@123",
    "phone": "9876543210"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "rahul@example.com",
    "password": "Rahul@123"
  }'
```

### Create Station (ADMIN)
```bash
curl -X POST http://localhost:8080/api/stations \
  -u admin@example.com:Admin@123 \
  -H "Content-Type: application/json" \
  -d '{
    "stationName": "Chennai Central",
    "stationCode": "MAS",
    "city": "Chennai"
  }'
```

### Create Booking (USER)
```bash
curl -X POST http://localhost:8080/api/book \
  -u rahul@example.com:Rahul@123 \
  -H "Content-Type: application/json" \
  -d '{
    "trainScheduleId": 1,
    "journeyDate": "2026-03-10",
    "preferredCoach": "D1",
    "passengers": [
      {"name": "Rahul Sharma", "age": 29, "gender": "MALE"},
      {"name": "Anita Sharma", "age": 27, "gender": "FEMALE"}
    ]
  }'
```

## Admin User Setup
The app properties include admin metadata keys (`app.admin.*`), but there is no automatic admin seeding in current code.

Use one of these options:
1. Register a normal user and promote it in DB:
   ```sql
   UPDATE users SET role = 'ADMIN' WHERE email = 'admin@example.com';
   ```
2. Insert an admin user directly with a BCrypt-hashed password.

## Run Tests
```powershell
.\mvnw.cmd test
```

## Notes
- Database is PostgreSQL-specific for several native queries (`FOR UPDATE SKIP LOCKED`, regex ordering).
- Keep `spring.datasource.password` out of public repos. Prefer environment-specific config for GitHub.

## License
Add your preferred license (MIT/Apache-2.0/etc.) in this repository.
