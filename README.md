# Student Management System

A full-stack web application for managing student records, built with Spring Boot, Spring Security (JWT), MySQL, and vanilla JavaScript.

## Features

- 🔐 JWT-based authentication with role-based access control (Admin / User)
- 📋 Full CRUD operations for student records
- 🔍 Search, sort, and pagination on the student list
- ✅ Server-side validation with clean, structured error responses
- 🧪 Unit tested service layer (JUnit 5 + Mockito)
- 🎨 Responsive frontend with real-time form validation and toast notifications

## Tech Stack

**Backend:** Java 17, Spring Boot, Spring Security, Spring Data JPA, MySQL, JWT (jjwt)
**Frontend:** HTML, CSS, JavaScript (fetch API, no framework)
**Testing:** JUnit 5, Mockito
**Deployment:** Docker, Render (backend), Railway (MySQL)

## Architecture

- **DTOs** decouple the API layer from database entities
- **Global exception handling** via `@RestControllerAdvice` for consistent error responses
- **Role-based authorization**: Admins can create/edit/delete students; Users have read-only access
- **Stateless JWT authentication**: no server-side sessions

## Getting Started (Local)

### Prerequisites
- Java 17+
- MySQL 8+
- Maven

### Setup
1. Clone this repository
2. Create a MySQL database: `CREATE DATABASE student_db;`
3. Set environment variables: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`
4. Run `mvn spring-boot:run`
5. Open `http://localhost:8081/index.html`

## API Endpoints

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/auth/register` | Public | Register a new user |
| POST | `/api/auth/login` | Public | Login, returns JWT |
| GET | `/api/students` | Authenticated | List students (search/sort/paginate) |
| POST | `/api/students` | Admin only | Create a student |
| PUT | `/api/students/{id}` | Admin only | Update a student |
| DELETE | `/api/students/{id}` | Admin only | Delete a student |

## Live Demo

*(link added after deployment — see below)*