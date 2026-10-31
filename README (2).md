# Task Management System

A full-stack task management application built with **Spring Boot** and
**React**.\
The project provides JWT-based authentication, role-based authorization,
user management, task management, filtering, pagination, validation, and
centralized error handling.

## Overview

Task Management System contains both the backend API and the frontend
application in a single repository.

The system supports two roles:

-   **USER** --- manages their own profile and tasks.
-   **ADMIN** --- manages users and can view/manage tasks across users.

Authentication is handled with **JWT**. When a logged-in user changes
their own username, the backend returns a new JWT and the frontend
replaces the old token, allowing the user to stay logged in.

## Tech Stack

### Backend

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
-   Bean Validation

### Frontend

-   React
-   Vite
-   JavaScript
-   React Router
-   CSS
-   Fetch/API integration
-   JWT authentication

## Project Structure

``` text
task-management-system/
├── backend/
│   ├── .mvn/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/
│   │   │   │   ├── config/
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   ├── entity/
│   │   │   │   ├── Enum/
│   │   │   │   ├── exception/
│   │   │   │   ├── handler/
│   │   │   │   ├── mapper/
│   │   │   │   ├── repository/
│   │   │   │   ├── security/
│   │   │   │   ├── service/
│   │   │   │   ├── specifications/
│   │   │   │   └── starter/
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── test/
│   ├── pom.xml
│   ├── mvnw
│   └── mvnw.cmd
│
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── context/
│   │   ├── lib/
│   │   ├── pages/
│   │   ├── App.jsx
│   │   ├── main.jsx
│   │   └── styles.css
│   ├── .env.example
│   ├── index.html
│   ├── package.json
│   ├── package-lock.json
│   └── README.md
│
├── .gitignore
└── README.md
```

## Features

### Authentication & Security

-   User registration
-   User login
-   JWT-based authentication
-   Spring Security
-   Role-based authorization with `ADMIN` and `USER`
-   Protected frontend routes
-   Automatic JWT refresh when the current user changes their username
-   Password encryption
-   Authorization checks for user-owned resources

### User Management

-   View current profile
-   Update username and email
-   Delete account
-   Admin user management
-   Admin can create users
-   Admin can update users
-   Admin can delete users
-   Duplicate username/email validation

### Task Management

-   Create tasks
-   View tasks
-   Update tasks
-   Delete tasks
-   Task ownership validation
-   Task statuses:
    -   `TODO`
    -   `IN_PROGRESS`
    -   `DONE`
-   Pagination
-   Sorting
-   Filtering by user, status, and title
-   Admin can manage tasks across users

### Backend

-   Layered architecture
-   DTO mapping
-   Repository pattern
-   JPA Specifications
-   Bean Validation
-   Custom exceptions
-   Global exception handling
-   Clean JSON API responses

### Frontend

-   Login and registration pages
-   Dashboard
-   Profile management
-   Task management interface
-   Admin users panel
-   Admin task filtering
-   Form validation and backend error messages
-   Persistent JWT authentication

## Backend Architecture

The backend follows a layered architecture:

-   **Controller Layer** --- handles HTTP requests and responses.
-   **Service Layer** --- contains business logic.
-   **Repository Layer** --- handles database operations.
-   **Security Layer** --- handles JWT authentication and authorization.
-   **Entity Layer** --- contains database models.
-   **DTO Layer** --- contains request and response models.
-   **Mapper Layer** --- converts entities to DTOs.
-   **Specification Layer** --- provides dynamic task filtering.
-   **Exception Handler** --- provides centralized API error handling.

## Prerequisites

Before running the project locally, install:

-   Java 17+
-   Maven, or use the included Maven Wrapper
-   MySQL
-   Node.js
-   npm

## Database Configuration

Create a MySQL database for the application and configure the backend
in:

``` text
backend/src/main/resources/application.yml
```

Do not commit real database passwords or JWT secrets to GitHub. For
deployment, use environment variables for sensitive configuration.

## Running the Project Locally

### 1. Clone the repository

``` bash
git clone https://github.com/caviding/task-management-system.git
cd task-management-system
```

### 2. Run the backend

Open a terminal in the backend directory:

``` bash
cd backend
```

On Windows:

``` bash
mvnw.cmd spring-boot:run
```

Or, if Maven is installed globally:

``` bash
mvn spring-boot:run
```

The backend runs by default at:

``` text
http://localhost:8080
```

### 3. Run the frontend

Open another terminal from the repository root:

``` bash
cd frontend
npm install
npm run dev
```

Vite will display the local frontend URL, normally:

``` text
http://localhost:5173
```

Keep both the backend and frontend running while using the application
locally.

## Frontend Environment

The frontend includes:

``` text
frontend/.env.example
```

Use it as the template for frontend environment configuration.

For local development, the API should point to the Spring Boot backend
running on port `8080`.

For production deployment, change the API URL to the deployed backend
URL.

## Main API Endpoints

### Authentication

  Method   Endpoint           Description
  -------- ------------------ ------------------------------
  POST     `/auth/register`   Register a user
  POST     `/auth/login`      Authenticate and receive JWT

### Users

  Method   Endpoint        Description
  -------- --------------- --------------------------
  GET      `/users/me`     Get current user profile
  GET      `/users`        Get users (Admin)
  GET      `/users/{id}`   Get user by ID
  PUT      `/users/{id}`   Update user
  DELETE   `/users/{id}`   Delete user

### Tasks

  Method   Endpoint               Description
  -------- ---------------------- --------------------
  GET      `/tasks`               Get tasks
  GET      `/tasks/{id}`          Get task by ID
  POST     `/tasks`               Create task
  PUT      `/tasks/{id}`          Update task
  PATCH    `/tasks/{id}/status`   Update task status
  DELETE   `/tasks/{id}`          Delete task

Task endpoints support authorization rules so normal users operate on
their own tasks while administrators have additional management
capabilities.

## Example Task Request

``` json
{
  "title": "Finish Task Management System",
  "description": "Complete and test the full-stack application",
  "status": "TODO"
}
```

## JWT Username Update Flow

The JWT subject contains the username. If the currently authenticated
user changes their own username:

1.  The backend updates the user.
2.  The backend generates a new JWT containing the new username.
3.  The update response includes the new token.
4.  The frontend replaces the old JWT with the new JWT.
5.  The user remains logged in.

If an admin changes another user's username, the admin's JWT is not
replaced.

## Error Handling

Backend exceptions are handled centrally using `@ControllerAdvice`.

The API provides structured error responses for cases such as:

-   User not found
-   Task not found
-   Duplicate username
-   Duplicate email
-   Validation errors
-   Unauthorized or forbidden operations
-   Task ownership mismatch

The frontend displays relevant backend validation and error messages to
the user.

## Development Notes

Generated/build files should not be committed. The root `.gitignore`
should exclude at least:

``` gitignore
frontend/node_modules/
frontend/dist/
frontend/.env

backend/target/
backend/.env
```

Keep secrets such as database passwords and JWT signing keys outside the
repository.

## Future Improvements

-   Swagger / OpenAPI documentation
-   Docker support
-   CI/CD pipeline
-   Production deployment
-   Expanded integration testing
-   Refresh-token authentication
-   Improved responsive UI

## License

MIT License
