# 📋 Task Manager REST API

A production-ready **Spring Boot REST API** for managing tasks with full CRUD operations, status workflow validation, async processing, and a built-in dashboard.

Built as a portfolio project demonstrating:
- ✅ Java OOP (Interfaces, Inheritance, Encapsulation)
- ✅ Collections Framework (HashMap, EnumSet, LinkedHashMap, Streams)
- ✅ Exception Handling (Custom exceptions, Global handler)
- ✅ Multithreading (@Async, CompletableFuture, ThreadPoolTaskExecutor)
- ✅ Spring Boot + Spring Data JPA
- ✅ REST API Design (CRUD + PATCH + filters)
- ✅ Unit Testing (JUnit 5 + Mockito)

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+

### Run Locally
```bash
git clone https://github.com/YOUR_USERNAME/task-manager.git
cd task-manager
mvn spring-boot:run
```

API will be live at: `http://localhost:8080`

### H2 Console (in-browser DB viewer)
`http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:taskdb`
- Username: `sa` | Password: *(empty)*

---

## 📡 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/tasks` | Create a task |
| `GET` | `/api/v1/tasks` | List all tasks |
| `GET` | `/api/v1/tasks/{id}` | Get task by ID |
| `PUT` | `/api/v1/tasks/{id}` | Update task |
| `PATCH` | `/api/v1/tasks/{id}/status?status=IN_PROGRESS` | Update status only |
| `DELETE` | `/api/v1/tasks/{id}` | Delete task |
| `GET` | `/api/v1/tasks?status=TODO` | Filter by status |
| `GET` | `/api/v1/tasks?priority=HIGH` | Filter by priority |
| `GET` | `/api/v1/tasks?assignedTo=alice` | Filter by assignee |
| `GET` | `/api/v1/tasks?search=keyword` | Search tasks |
| `GET` | `/api/v1/tasks/overdue` | Get overdue tasks |
| `GET` | `/api/v1/tasks/dashboard` | Dashboard statistics |
| `GET` | `/api/v1/tasks/async?assignedTo=alice` | Async fetch |

---

## 📦 Sample Requests

### Create a Task
```bash
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Implement login feature",
    "description": "Add JWT-based authentication",
    "priority": "HIGH",
    "assignedTo": "alice",
    "dueDate": "2026-07-01T10:00:00"
  }'
```

### Update Status
```bash
curl -X PATCH "http://localhost:8080/api/v1/tasks/1/status?status=IN_PROGRESS"
```

### Get Dashboard Stats
```bash
curl http://localhost:8080/api/v1/tasks/dashboard
```

---

## 🔁 Task Status Workflow

```
TODO → IN_PROGRESS → IN_REVIEW → DONE
  ↓         ↓           ↓
CANCELLED CANCELLED  CANCELLED
```

Invalid transitions (e.g., DONE → IN_PROGRESS) are rejected with a clear error message.

---

## 🧠 Key Technical Concepts Demonstrated

### OOP
- `TaskService` interface + `TaskServiceImpl` (Abstraction + Polymorphism)
- `Task` entity with encapsulated enums
- `TaskDTO` with inner static classes

### Collections
- `HashMap` and `EnumSet` for status transition map
- `LinkedHashMap` for ordered dashboard stats
- Java Streams for filtering and mapping

### Exception Handling
- `TaskNotFoundException` (custom RuntimeException)
- `InvalidStatusTransitionException`
- `GlobalExceptionHandler` with `@RestControllerAdvice`

### Multithreading
- `@Async` on `getTasksAsync()` method
- Custom `ThreadPoolTaskExecutor` with core=4, max=10
- `CompletableFuture` return type

---

## 🧪 Running Tests
```bash
mvn test
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Data | Spring Data JPA + H2 |
| Build | Maven |
| Testing | JUnit 5 + Mockito |
| Utility | Lombok |

---

## 📁 Project Structure

```
src/
├── main/java/com/autorabit/taskmanager/
│   ├── TaskManagerApplication.java
│   ├── config/
│   │   ├── AsyncConfig.java          # Thread pool config
│   │   └── DataSeeder.java           # Sample data on startup
│   ├── controller/
│   │   └── TaskController.java       # REST endpoints
│   ├── dto/
│   │   ├── ApiResponse.java          # Generic response wrapper
│   │   └── TaskDTO.java              # Request/Response/Summary DTOs
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   ├── InvalidStatusTransitionException.java
│   │   └── TaskNotFoundException.java
│   ├── model/
│   │   └── Task.java                 # JPA entity with enums
│   ├── repository/
│   │   └── TaskRepository.java       # Spring Data JPA repository
│   └── service/
│       ├── TaskService.java          # Interface
│       └── TaskServiceImpl.java      # Implementation
└── test/
    └── TaskServiceTest.java          # JUnit 5 + Mockito unit tests
```
