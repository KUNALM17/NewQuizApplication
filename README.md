# 🧠 NewQuizApplication

A comprehensive Spring Boot-based quiz application with JWT authentication, role-based access control, and PostgreSQL database integration. This application allows administrators to create and manage questions and quizzes, while users can take quizzes and get scored results.

## 🚀 Features

### 🔐 Authentication & Authorization
- **JWT-based Authentication** - Secure token-based authentication system
- **Role-based Access Control** - Separate permissions for ADMIN and USER roles
- **User Registration** - Public registration with automatic USER role assignment
- **Admin Registration** - Admin-only endpoint to create users with custom roles

### 📝 Question Management
- **Create Questions** - Add multiple-choice questions with 4 options
- **Categorize Questions** - Organize questions by category and difficulty level
- **View Questions** - Get all questions, filter by category, or get specific question by ID
- **Admin Controls** - Full CRUD operations for administrators

### 🎯 Quiz Management
- **Create Quizzes** - Generate quizzes from random questions by category
- **Take Quizzes** - Users can attempt quizzes and get questions without answers
- **Submit Answers** - Automatic scoring system for quiz submissions
- **Quiz Administration** - Admins can view, delete individual quizzes or all quizzes

### 🏗️ Technical Features
- **RESTful API** - Clean REST endpoints for all operations
- **Database Integration** - PostgreSQL with JPA/Hibernate
- **Data Validation** - Comprehensive input validation
- **Error Handling** - Proper HTTP status codes and error messages
- **Security Configuration** - Method-level security with Spring Security

## 🛠️ Technology Stack

- **Backend Framework:** Spring Boot 3.5.3
- **Security:** Spring Security 6.x with JWT
- **Database:** PostgreSQL 
- **ORM:** Spring Data JPA with Hibernate
- **Authentication:** JWT (JSON Web Tokens)
- **Build Tool:** Maven
- **Java Version:** Java 21
- **Additional Libraries:**
  - Lombok (Boilerplate code reduction)
  - JJWT (JWT implementation)
  - BCrypt (Password encryption)

## 🔧 Prerequisites

- Java 17 or higher
- PostgreSQL 12 or higher
- Maven 3.6 or higher
- Git

## ⚙️ Setup & Installation

### 1. Clone the Repository
```bash
git clone https://github.com/KUNALM17/NewQuizApplication.git
cd NewQuizApplication
```

### 2. Database Setup
```sql
-- Create database
CREATE DATABASE NewQuizAppdb;

-- Create user (optional, or use existing postgres user)
CREATE USER quiz_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE NewQuizAppdb TO quiz_user;
```

### 3. Configure Application Properties
Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/NewQuizAppdb
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

jwt.secret=your-256-bit-secret-key
jwt.expiration-ms=86400000
```

### 4. Build and Run
```bash
# Make maven wrapper executable
chmod +x mvnw

# Build the project
./mvnw clean compile

# Run the application
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

## 📡 API Endpoints

### 🔐 Authentication Endpoints
| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/auth/register` | Register new user (USER role) | Public |
| POST | `/auth/login` | Login and get JWT token | Public |
| POST | `/auth/admin/register` | Register user with custom role | Admin Only |

### 👤 User Endpoints
| Method | Endpoint | Description | Required Role |
|--------|----------|-------------|---------------|
| GET | `/user/question/allQuestions` | Get all questions | USER, ADMIN |
| GET | `/user/question/category/{category}` | Get questions by category | USER, ADMIN |
| GET | `/user/question/id/{id}` | Get question by ID | USER, ADMIN |
| GET | `/user/quiz/get/{id}` | Get quiz questions (without answers) | USER, ADMIN |
| POST | `/user/quiz/submit/{id}` | Submit quiz answers and get score | USER, ADMIN |

### 🔧 Admin Endpoints
| Method | Endpoint | Description | Required Role |
|--------|----------|-------------|---------------|
| GET | `/admin/question/allQuestions` | Get all questions | ADMIN |
| GET | `/admin/question/category/{category}` | Get questions by category | ADMIN |
| GET | `/admin/question/id/{id}` | Get question by ID | ADMIN |
| POST | `/admin/question/addQuestions` | Create new question | ADMIN |
| POST | `/admin/quiz/create` | Create new quiz | ADMIN |
| GET | `/admin/quiz/all` | Get all quizzes | ADMIN |
| DELETE | `/admin/quiz/delete/{id}` | Delete specific quiz | ADMIN |
| DELETE | `/admin/quiz/delete/all` | Delete all quizzes | ADMIN |

## 📋 Usage Examples

### Authentication
```bash
# Register a new user
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john_doe","password":"password123","email":"john@example.com"}'

# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john_doe","password":"password123"}'
```

### Creating a Question (Admin)
```bash
curl -X POST http://localhost:8080/admin/question/addQuestions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "question_title": "What is the capital of France?",
    "option1": "London",
    "option2": "Berlin", 
    "option3": "Paris",
    "option4": "Madrid",
    "right_answer": "Paris",
    "difficultylevel": "Easy",
    "category": "Geography"
  }'
```

### Creating a Quiz (Admin)
```bash
curl -X POST "http://localhost:8080/admin/quiz/create?category=Geography&numQ=5&title=Geography%20Quiz" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Taking a Quiz (User)
```bash
# Get quiz questions
curl -X GET http://localhost:8080/user/quiz/get/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Submit answers
curl -X POST http://localhost:8080/user/quiz/submit/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '[
    {"id":1,"response":"Paris"},
    {"id":2,"response":"Berlin"}
  ]'
```

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/com/example/demo/
│   │   ├── Controller/           # REST Controllers
│   │   │   ├── QuestionController.java    # Admin question endpoints
│   │   │   ├── UserQuestionController.java # User question endpoints  
│   │   │   └── QuizController.java        # Quiz management endpoints
│   │   ├── Model/               # Data Models/Entities
│   │   │   ├── Question.java           # Question entity
│   │   │   ├── Quiz.java              # Quiz entity
│   │   │   ├── QuestionWrapper.java   # Question without answer
│   │   │   └── Response.java          # Quiz response model
│   │   ├── Service/             # Business Logic
│   │   │   ├── QuestionService.java   # Question operations
│   │   │   └── QuizService.java       # Quiz operations
│   │   ├── Dao/                 # Data Access Layer
│   │   │   ├── QuestionDao.java       # Question repository
│   │   │   └── QuizDao.java           # Quiz repository
│   │   ├── security/            # Security Configuration
│   │   │   ├── config/               # Security configs
│   │   │   ├── controller/           # Auth controller
│   │   │   ├── jwt/                  # JWT utilities
│   │   │   ├── model/                # User/Role entities
│   │   │   ├── repo/                 # User/Role repositories
│   │   │   └── service/              # User details service
│   │   └── NewQuizApplication.java   # Main application class
│   └── resources/
│       └── application.properties    # Configuration file
└── test/                        # Test classes
```

## 🔒 Security Features

- **JWT Authentication**: Stateless authentication using JSON Web Tokens
- **Password Encryption**: BCrypt hashing for secure password storage  
- **Role-based Authorization**: Method-level security with `@PreAuthorize`
- **CORS Configuration**: Configurable cross-origin resource sharing
- **Session Management**: Stateless session creation policy

## 🗄️ Database Schema

The application uses the following main entities:

- **User**: Stores user credentials and roles
- **Role**: Defines user permissions (USER, ADMIN)
- **Question**: Quiz questions with multiple choice options
- **Quiz**: Collection of questions with title
- **quiz_question**: Join table for Quiz-Question many-to-many relationship

## 🚀 Getting Started

1. **Initial Setup**: Follow the setup instructions above
2. **Create Admin User**: Use the DataInitializer or register via `/auth/admin/register` 
3. **Add Questions**: Use admin endpoints to create questions in different categories
4. **Create Quizzes**: Generate quizzes from your question bank
5. **Test User Flow**: Register users and let them take quizzes

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`) 
5. Open a Pull Request

## 📝 License

This project is open source and available under the [MIT License](LICENSE).

## 👨‍💻 Author

**KUNALM17** - [GitHub Profile](https://github.com/KUNALM17)

