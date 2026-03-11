# NewQuizApplication

> **A robust, production-ready backend service for quiz applications built with Spring Boot 3, Java 21, JWT authentication, and PostgreSQL.**

This is the backend component of a full-stack quiz platform. It exposes a secure REST API that handles quiz lifecycle management, question banks, automated scoring, and role-based user access — ready to be consumed by any frontend (React, Angular, mobile apps, etc.).

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Database Setup](#database-setup)
- [Build & Run](#build--run)
- [API Documentation](#api-documentation)
  - [Authentication Endpoints](#authentication-endpoints)
  - [Admin – Question Endpoints](#admin--question-endpoints)
  - [User – Question Endpoints](#user--question-endpoints)
  - [Admin – Quiz Endpoints](#admin--quiz-endpoints)
  - [User – Quiz Endpoints](#user--quiz-endpoints)
- [Project Structure](#project-structure)
- [Database Schema](#database-schema)
- [Authentication & Authorization](#authentication--authorization)
- [Error Handling](#error-handling)
- [Testing](#testing)
- [Deployment](#deployment)
- [Contributing](#contributing)
- [License](#license)
- [Support & Contact](#support--contact)

---

## Overview

**NewQuizApplication** is the backend service powering a full-stack quiz platform. It provides a comprehensive REST API that enables:

- Administrators to build question banks, create category-based quizzes, and manage content.
- Users to browse quizzes, submit answers, and instantly receive their scores.
- Secure, stateless communication via JSON Web Tokens (JWT).

This service is designed to integrate with the [FullStackQuizApplication](https://github.com/KUNALM17/FullStackQuizApplication) frontend but is fully decoupled and can serve any client.

---

## Features

- ✅ **Quiz Management** – Create, retrieve, and delete quizzes; auto-populate quizzes with random questions by category.
- ✅ **Question Bank** – Add questions with four options, a correct answer, category, and difficulty level.
- ✅ **Answer Submission & Automated Scoring** – Users submit answers and receive an instant numeric score.
- ✅ **JWT Authentication** – Stateless, token-based authentication with 24-hour token expiry.
- ✅ **Role-Based Authorization** – `ADMIN` and `USER` roles with separate endpoint access policies.
- ✅ **Automatic Role Seeding** – `ADMIN` and `USER` roles are created in the database on first startup.
- ✅ **PostgreSQL Integration** – Production-grade relational database with JPA/Hibernate ORM.
- ✅ **BCrypt Password Encoding** – All passwords are securely hashed before storage.
- ✅ **DTO Pattern** – `QuestionWrapper` DTO exposes only question content to users (no answer leakage).

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.3 |
| Security | Spring Security + JWT (JJWT 0.11.5) |
| ORM | Spring Data JPA / Hibernate |
| Database | PostgreSQL |
| Build Tool | Maven |
| Utilities | Lombok |

---

## Prerequisites

Before you begin, ensure you have the following installed:

- **Java 21** (JDK) – [Download](https://adoptium.net/)
- **Maven 3.8+** – [Download](https://maven.apache.org/download.cgi) *(or use the included `mvnw` wrapper)*
- **PostgreSQL 13+** – [Download](https://www.postgresql.org/download/)
- **Git** – [Download](https://git-scm.com/)

Verify your installations:

```bash
java -version        # Should print: openjdk 21...
mvn -version         # Should print: Apache Maven 3.x...
psql --version       # Should print: psql (PostgreSQL) 13...
```

---

## Installation

### 1. Clone the Repository

```bash
git clone https://github.com/KUNALM17/NewQuizApplication.git
cd NewQuizApplication
```

### 2. Install Dependencies

```bash
./mvnw dependency:resolve
```

*(On Windows use `mvnw.cmd dependency:resolve`)*

---

## Configuration

Edit `src/main/resources/application.properties` to match your local environment:

```properties
# Application name
spring.application.name=questiondb

# ─── Database ──────────────────────────────────────────────────────────────────
spring.datasource.url=jdbc:postgresql://localhost:5432/NewQuizAppdb
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
spring.datasource.driver-class-name=org.postgresql.Driver

# ─── JPA / Hibernate ───────────────────────────────────────────────────────────
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.hibernate.ddl-auto=update        # 'create' on first run, then change to 'update'

# ─── JWT ───────────────────────────────────────────────────────────────────────
# Use a strong secret (≥256-bit for HS256)
jwt.secret=YOUR_STRONG_SECRET_KEY_AT_LEAST_256_BITS_LONG
jwt.expiration-ms=86400000                  # 24 hours in milliseconds

# ─── Logging ───────────────────────────────────────────────────────────────────
logging.level.org.springframework=INFO
spring.jackson.serialization.FAIL_ON_EMPTY_BEANS=false
```

> ⚠️ **Security Note:** Never commit real credentials to version control. Use environment variables or a secrets manager in production.

---

## Database Setup

### 1. Create the Database

Connect to PostgreSQL and create the application database:

```sql
CREATE DATABASE "NewQuizAppdb";
```

### 2. Schema Creation

Hibernate is configured with `ddl-auto=update`, which automatically creates or updates all tables on application startup. No manual schema migration is required for development.

The following tables will be created automatically:

| Table | Description |
|---|---|
| `question` | Stores all quiz questions |
| `quiz` | Stores quiz metadata |
| `quiz_question` | Many-to-many join: quizzes ↔ questions |
| `users` | Registered application users |
| `roles` | Available roles (`ADMIN`, `USER`) |
| `user_roles` | Many-to-many join: users ↔ roles |

### 3. Role Seeding

The `DataInitializer` component automatically inserts the `ADMIN` and `USER` roles into the `roles` table on the first application startup — no manual seeding required.

---

## Build & Run

### Run in Development Mode

```bash
./mvnw spring-boot:run
```

The API will start at `http://localhost:8080`.

### Build a Production JAR

```bash
./mvnw clean package -DskipTests
```

The executable JAR is generated at `target/demo-0.0.1-SNAPSHOT.jar`.

### Run the JAR

```bash
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

---

## API Documentation

All endpoints return JSON. Protected endpoints require a Bearer token in the `Authorization` header:

```
Authorization: Bearer <your_jwt_token>
```

---

### Authentication Endpoints

Base path: `/auth`

| Method | Endpoint | Access | Request Body | Response | Description |
|--------|----------|--------|--------------|----------|-------------|
| `POST` | `/auth/register` | Public | `{ "username", "password", "email" }` | `String` | Register a new user with `USER` role |
| `POST` | `/auth/admin/register` | `ADMIN` | `{ "username", "password", "role", "email" }` | `String` | Register a user with a specified role |
| `POST` | `/auth/login` | Public | `{ "username", "password" }` | `{ "token": "..." }` | Log in and receive a JWT |

**Register example:**

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"secret123","email":"john@example.com"}'
```

**Login example:**

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"secret123"}'
# Response: {"token":"eyJhbGciOi..."}
```

---

### Admin – Question Endpoints

Base path: `/admin/question` | Access: **`ADMIN` only**

| Method | Endpoint | Path Param | Request Body | Response | Description |
|--------|----------|-----------|--------------|----------|-------------|
| `GET` | `/admin/question/allQuestions` | – | – | `List<Question>` | Retrieve all questions |
| `GET` | `/admin/question/category/{category}` | `category` | – | `List<Question>` | Get questions by category |
| `GET` | `/admin/question/id/{id}` | `id` | – | `Question` | Get a question by ID |
| `POST` | `/admin/question/addQuestions` | – | `Question` JSON | `String` | Add a new question |

**Add question example:**

```bash
curl -X POST http://localhost:8080/admin/question/addQuestions \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "question_title": "What is the capital of France?",
    "option1": "Berlin",
    "option2": "Madrid",
    "option3": "Paris",
    "option4": "Rome",
    "right_answer": "Paris",
    "difficultylevel": "Easy",
    "category": "Geography"
  }'
```

---

### User – Question Endpoints

Base path: `/user/question` | Access: **`USER` or `ADMIN`**

| Method | Endpoint | Path Param | Response | Description |
|--------|----------|-----------|----------|-------------|
| `GET` | `/user/question/allQuestions` | – | `List<Question>` | Retrieve all questions |
| `GET` | `/user/question/category/{category}` | `category` | `List<Question>` | Get questions by category |
| `GET` | `/user/question/id/{id}` | `id` | `Question` | Get a question by ID |

---

### Admin – Quiz Endpoints

Access: **`ADMIN` only**

| Method | Endpoint | Parameters | Response | Description |
|--------|----------|-----------|----------|-------------|
| `POST` | `/admin/quiz/create` | Query: `category`, `numQ`, `title` | `String` | Create a quiz with random questions |
| `GET` | `/admin/quiz/all` | – | `List<Quiz>` | Retrieve all quizzes |
| `DELETE` | `/admin/quiz/delete/{id}` | Path: `id` | `String` | Delete a specific quiz |
| `DELETE` | `/admin/quiz/delete/all` | – | `String` | Delete all quizzes |

**Create quiz example:**

```bash
curl -X POST "http://localhost:8080/admin/quiz/create?category=Geography&numQ=5&title=Geography+Quiz" \
  -H "Authorization: Bearer <token>"
```

---

### User – Quiz Endpoints

Access: **`USER` or `ADMIN`**

| Method | Endpoint | Parameters | Request Body | Response | Description |
|--------|----------|-----------|--------------|----------|-------------|
| `GET` | `/user/quiz/get/{id}` | Path: `id` | – | `List<QuestionWrapper>` | Fetch quiz questions (answers hidden) |
| `POST` | `/user/quiz/submit/{id}` | Path: `id` | `List<Response>` | `Integer` (score) | Submit answers and get score |

**Fetch quiz questions example:**

```bash
curl http://localhost:8080/user/quiz/get/1 \
  -H "Authorization: Bearer <token>"
```

**Submit quiz answers example:**

```bash
curl -X POST http://localhost:8080/user/quiz/submit/1 \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '[{"id":1,"response":"Paris"},{"id":2,"response":"Tokyo"}]'
# Response: 1  (number of correct answers)
```

---

## Project Structure

```
NewQuizApplication/
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── NewQuizApplication.java          # Application entry point
│   │   │   │
│   │   │   ├── Controller/                      # REST controllers (quiz domain)
│   │   │   │   ├── QuestionController.java      # Admin question management
│   │   │   │   ├── QuizController.java          # Quiz creation, retrieval, scoring
│   │   │   │   └── UserQuestionController.java  # Read-only question access for users
│   │   │   │
│   │   │   ├── Model/                           # JPA entities & DTOs
│   │   │   │   ├── Question.java                # Question entity
│   │   │   │   ├── Quiz.java                    # Quiz entity
│   │   │   │   ├── QuestionWrapper.java         # DTO (question without answer)
│   │   │   │   └── Response.java                # DTO (user's answer submission)
│   │   │   │
│   │   │   ├── Service/                         # Business logic layer
│   │   │   │   ├── QuestionService.java
│   │   │   │   └── QuizService.java
│   │   │   │
│   │   │   ├── Dao/                             # Data access layer (repositories)
│   │   │   │   ├── QuestionDao.java
│   │   │   │   └── QuizDao.java
│   │   │   │
│   │   │   └── security/                        # Security subsystem
│   │   │       ├── controller/
│   │   │       │   └── AuthController.java      # Register & login endpoints
│   │   │       ├── model/
│   │   │       │   ├── User.java                # User entity
│   │   │       │   └── Role.java                # Role entity
│   │   │       ├── service/
│   │   │       │   └── CustomUserDetailsService.java
│   │   │       ├── jwt/
│   │   │       │   ├── JwtUtil.java             # Token generation & validation
│   │   │       │   └── JwtRequestFilter.java    # Per-request JWT validation filter
│   │   │       ├── repo/
│   │   │       │   ├── UserRepository.java
│   │   │       │   └── RoleRepository.java
│   │   │       └── config/
│   │   │           ├── SecurityConfig.java      # Security filter chain & access rules
│   │   │           └── DataInitializer.java     # Seeds roles on startup
│   │   │
│   │   └── resources/
│   │       └── application.properties           # App configuration
│   │
│   └── test/
│       └── java/com/example/demo/
│           └── NewQuizApplicationTests.java     # Integration tests
│
├── pom.xml                                      # Maven build descriptor
├── mvnw / mvnw.cmd                              # Maven wrapper scripts
└── README.md
```

---

## Database Schema

```
┌──────────────────────┐        ┌──────────────────────────────────┐
│        quiz          │        │            question               │
├──────────────────────┤        ├──────────────────────────────────┤
│ id          PK INT   │        │ id             PK INT            │
│ title       VARCHAR  │        │ question_title  VARCHAR          │
└────────┬─────────────┘        │ option1         VARCHAR          │
         │                      │ option2         VARCHAR          │
         │  ┌───────────────────│ option3         VARCHAR          │
         │  │ quiz_question     │ option4         VARCHAR          │
         │  ├───────────────────│ right_answer    VARCHAR          │
         │  │ quiz_id    FK INT │ difficultylevel VARCHAR          │
         └──┤ question_id FK INT│ category        VARCHAR          │
            └───────────────────└──────────────────────────────────┘

┌─────────────────────────┐        ┌──────────────────────┐
│          users          │        │        roles         │
├─────────────────────────┤        ├──────────────────────┤
│ id        PK  BIGINT    │        │ role_name  PK VARCHAR│
│ username  UNIQUE VARCHAR│        │   (ADMIN / USER)     │
│ password  VARCHAR       │        └──────────┬───────────┘
│ email     VARCHAR       │                   │
└────────┬────────────────┘        ┌──────────┴───────────┐
         │                         │      user_roles      │
         │  ┌──────────────────────├──────────────────────┤
         └──┤ user_id     FK BIGINT│                      │
            │ role_name   FK VARCHAR                      │
            └──────────────────────┘
```

---

## Authentication & Authorization

The application uses **stateless JWT-based authentication**.

### How It Works

1. **Register** – `POST /auth/register` creates a user with the `USER` role (BCrypt-hashed password).
2. **Login** – `POST /auth/login` validates credentials and returns a signed JWT valid for **24 hours**.
3. **Access Protected Resources** – Include the token in the `Authorization` header as `Bearer <token>`.
4. **Token Validation** – `JwtRequestFilter` intercepts every request, validates the token, and populates the Spring Security context.

### Access Control Matrix

| Endpoint Pattern | `ADMIN` | `USER` | Public |
|---|:---:|:---:|:---:|
| `POST /auth/register` | ✅ | ✅ | ✅ |
| `POST /auth/login` | ✅ | ✅ | ✅ |
| `POST /auth/admin/**` | ✅ | ❌ | ❌ |
| `/admin/**` | ✅ | ❌ | ❌ |
| `/user/**` | ✅ | ✅ | ❌ |

---

## Error Handling

The application returns standard HTTP status codes:

| Status Code | Meaning | Typical Cause |
|---|---|---|
| `200 OK` | Success | Request processed successfully |
| `201 Created` | Resource created | New question/quiz added |
| `400 Bad Request` | Invalid input | Malformed JSON or missing fields |
| `401 Unauthorized` | Missing/invalid token | No `Authorization` header or expired JWT |
| `403 Forbidden` | Insufficient permissions | A `USER` accessing an `/admin/**` endpoint |
| `404 Not Found` | Resource not found | Quiz or question ID does not exist |
| `500 Internal Server Error` | Server error | Unexpected application error |

---

## Testing

### Run All Tests

```bash
./mvnw test
```

### Run a Specific Test Class

```bash
./mvnw test -Dtest=NewQuizApplicationTests
```

### Current Test Coverage

The project currently includes a Spring Boot context load test that verifies the entire application context starts correctly. To add coverage, create test classes under `src/test/java/com/example/demo/` following the Spring Boot testing conventions:

```java
@SpringBootTest
@AutoConfigureMockMvc
class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnUnauthorizedWithoutToken() throws Exception {
        mockMvc.perform(get("/admin/quiz/all"))
               .andExpect(status().isUnauthorized());
    }
}
```

---

## Deployment

### Option 1: Run the JAR Directly

```bash
# Build
./mvnw clean package -DskipTests

# Run with external config
java -jar target/demo-0.0.1-SNAPSHOT.jar \
  --spring.datasource.url=jdbc:postgresql://<host>:5432/NewQuizAppdb \
  --spring.datasource.username=<user> \
  --spring.datasource.password=<pass> \
  --jwt.secret=<strong-secret>
```

### Option 2: Docker

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
# Build image
docker build -t newquizapplication .

# Run container
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/NewQuizAppdb \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=yourpassword \
  -e JWT_SECRET=yourStrongSecret \
  newquizapplication
```

### Option 3: Docker Compose (with PostgreSQL)

```yaml
services:
  db:
    image: postgres:15
    environment:
      POSTGRES_DB: NewQuizAppdb
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: yourpassword
    ports:
      - "5432:5432"

  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/NewQuizAppdb
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: yourpassword
      JWT_SECRET: yourStrongSecret
    depends_on:
      - db
```

```bash
docker-compose up --build
```

---

## Contributing

Contributions are welcome! Please follow these steps:

1. **Fork** the repository.
2. **Create a feature branch**: `git checkout -b feature/your-feature-name`
3. **Commit your changes**: `git commit -m 'Add: brief description of change'`
4. **Push to your branch**: `git push origin feature/your-feature-name`
5. **Open a Pull Request** against the `main` branch.

### Guidelines

- Follow existing code style and package conventions.
- Write or update tests for any changed behaviour.
- Do not commit credentials, `.env` files, or `target/` artifacts.
- Use clear, descriptive commit messages in the imperative mood.

---

## License

This project is licensed under the **MIT License**.

```
MIT License

Copyright (c) 2026 Kunal M

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## Support & Contact

- 🐛 **Bug Reports & Feature Requests**: [Open a GitHub Issue](https://github.com/KUNALM17/NewQuizApplication/issues)
- 💬 **Discussions**: [GitHub Discussions](https://github.com/KUNALM17/NewQuizApplication/discussions)
- 👤 **Author**: [KUNALM17](https://github.com/KUNALM17)

---

*Built with ❤️ using Spring Boot 3 & Java 21*

