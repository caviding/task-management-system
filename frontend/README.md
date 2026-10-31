# Task Management Frontend

React + Vite frontend matched to the supplied Spring Boot API.

## Run locally
1. Start MySQL and the Spring Boot backend on port 8080.
2. In this folder run `npm install`.
3. Copy `.env.example` to `.env` if the backend URL differs.
4. Run `npm run dev` and open the Vite URL (normally http://localhost:5173).

## Backend mapping used
- POST `/api/v1/auth/register`, POST `/api/v1/auth/login`
- GET `/api/v1/users/me`
- Admin users: GET/POST `/api/v1/users`, PUT/DELETE `/api/v1/users/{id}`
- User tasks: GET `/api/v1/tasks/my-tasks`, POST `/api/v1/tasks`
- Task CRUD: GET/PUT/DELETE `/api/v1/tasks/{id}`, PATCH `/api/v1/tasks/{id}/status`
- Admin tasks: POST `/api/v1/tasks/admin/{userId}`, GET `/api/v1/tasks/search`

The backend `UserResponseDto` does not return `role`, so the UI detects ADMIN by calling the admin-only users endpoint. A 401/403 is treated as a normal non-admin account.

## Important backend note
The supplied backend still has an admin-delete edge case in `TaskServiceImpl.deleteTask`: it removes the task from the currently authenticated user's task list. For an ADMIN deleting another user's task, that list is the admin's list, so the task may remain. The frontend cannot safely repair server-side authorization/data mutation logic; fix the service before relying on admin deletion in production.

## Username update / JWT refresh
The backend may return `{ user, token }` from `PUT /users/{id}` when the currently authenticated user changes their own username. The frontend automatically replaces the JWT before making follow-up authenticated requests, so the user stays signed in. When an admin edits another user's username, no replacement token is expected and the admin session is left unchanged.
