
# 🚀 Task Management Backend API

A clean, scalable, and production-ready RESTful API built with Spring
Boot for managing tasks efficiently.

------------------------------------------------------------------------

## 📌 Overview

Task Management Backend is designed using layered architecture and best
practices.\
It provides structured CRUD operations, validation handling, and
centralized exception management.

This API can easily integrate with any frontend framework (React,
Angular, Vue) or mobile application.

------------------------------------------------------------------------

## ⚙️ Tech Stack

-   Java 17
-   Spring Boot
-   Spring Web
-   Spring Data JPA
-   Spring Security
-   JWT
-   Hibernate
-   Maven
-   MySQL
-   Lombok

------------------------------------------------------------------------

## 🏗 Architecture

The project follows layered architecture:

-   **Controller Layer** -- Handles HTTP requests and responses\
-   **Service Layer** -- Contains business logic\
-   **Repository Layer** -- Database operations\
-   **Security Layer** -- Authentication and authorization\
-   **Entity Layer** -- Database models\
-   **DTO Layer** -- Request/Response models\
-   **Global Exception Handling** -- Centralized error responses

------------------------------------------------------------------------

## 📂 Project Structure

    task-management-backend
    │
    ├── src/main/java/com/example/taskmanagement
    │   ├── controller
    │   ├── config
    │   ├── service
    │   ├── repository
    │   ├── security
    │   ├── entity
    │   ├── mapper
    │   ├── Enum
    │   ├── dto
    │   ├── exception
    │   ├── handler
    │   ├── specifications
    │   |── starter
           ├── TaskManagementApplication.java
    │
    ├── src/main/resources
    │   └── application.properties
    │
    ├── pom.xml
    └── README.md

------------------------------------------------------------------------

## ✨ Features

-   Create Task\
-   Update Task\
-   Delete Task\
-   Get All Tasks\
-   Get Task By ID\
-   DTO Mapping\
-   Authentication\
-   Authorization\
-   Pagination & Sorting\
-   Specification\
-   Validation Handling\
-   Custom Exception Handling\
-   Clean JSON Error Responses

------------------------------------------------------------------------

## 🔧 Configuration

### H2 Example

``` properties
spring.datasource.url=jdbc:h2:mem:taskdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
```

### PostgreSQL Example

``` properties
spring.datasource.url=jdbc:postgresql://localhost:5432/taskdb
spring.datasource.username=postgres
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
```

------------------------------------------------------------------------

## ▶️ Running the Project

``` bash
git clone https://github.com/caviding/task-management-backend.git
cd task-management-backend
mvn clean install
mvn spring-boot:run
```

Application runs at:

    http://localhost:8080

------------------------------------------------------------------------

## 📡 API Endpoints

  Method   Endpoint      Description
  -------- ------------- -----------------
  GET      /tasks        Get all tasks
  GET      /tasks/{id}   Get task by ID
  POST     /tasks        Create new task
  PUT      /tasks/{id}   Update task
  DELETE   /tasks/{id}   Delete task

------------------------------------------------------------------------

## 📥 Example Request

``` json
{
  "title": "Finish Backend API",
  "description": "Complete CRUD operations",
  "status": "TODO"
}
```

------------------------------------------------------------------------

## ❌ Error Handling

All exceptions are handled globally using @ControllerAdvice.

Example:

``` json
{
  "timestamp": "2026-02-11T14:35:00",
  "status": 404,
  "error": "Task Not Found",
  "message": "Task with id 10 not found"
}
```

------------------------------------------------------------------------

## 🔮 Future Improvements

-   Swagger / OpenAPI\
-   Unit & Integration Tests\
-   Docker Support\
-   CI/CD

------------------------------------------------------------------------

## 📜 License

MIT License
