# Zest India Product API

A secure RESTful Product API built with Java and Spring Boot. The application provides Product CRUD operations, product item retrieval, JWT-based authentication, refresh token rotation, role-based authorization, request validation, pagination, centralized exception handling, database indexing, and Docker support.

---

## Features

- Product CRUD operations
- Product item retrieval
- User registration and login
- JWT-based authentication
- Refresh token rotation
- Role-based authorization using USER and ADMIN roles
- Request validation using Jakarta Validation
- Pagination for product listing
- Centralized exception handling
- Database indexing
- CORS configuration
- Swagger/OpenAPI documentation
- Unit testing with JUnit 5 and Mockito
- Integration testing with Spring Boot Test
- H2 in-memory database for testing
- Docker and Docker Compose support
- Environment-based configuration for sensitive credentials

---

## Technology Stack

| Technology | Purpose |
|------------|---------|
| Java 17 | Application development |
| Spring Boot | Backend application framework |
| Spring Data JPA | Data access |
| Hibernate | ORM |
| MySQL | Primary database |
| Spring Security | Authentication and authorization |
| JWT | Token-based authentication |
| Maven | Build and dependency management |
| Jakarta Validation | Request validation |
| Swagger / OpenAPI | API documentation |
| JUnit 5 | Unit testing |
| Mockito | Mocking for unit tests |
| H2 | In-memory database for testing |
| Docker | Application containerization |
| Docker Compose | Multi-container application setup |

---

## Architecture

The application follows a layered architecture that separates HTTP handling, business logic, data access, security, and configuration.

```text
                         Client
                           |
                           v
                    +--------------+
                    | JWT Security |
                    |    Filter    |
                    +--------------+
                           |
                           v
                    +--------------+
                    |  Controller  |
                    +--------------+
                           |
                           v
                    +--------------+
                    |   Service    |
                    +--------------+
                           |
                           v
                    +--------------+
                    |  Repository  |
                    +--------------+
                           |
                           v
                    +--------------+
                    |    MySQL     |
                    +--------------+
```

### Application Layers

**Controller Layer**

Handles HTTP requests, request parameters, validation, and HTTP responses.

**Service Layer**

Contains application and business logic.

**Repository Layer**

Provides database access using Spring Data JPA.

**Entity Layer**

Represents the application's database entities.

**DTO Layer**

Defines request and response objects exchanged through the REST API.

**Security Layer**

Handles JWT validation, authentication, and role-based authorization.

**Exception Layer**

Provides centralized exception handling and standardized error responses.

**Config Layer**

Contains application configuration such as security, CORS, password encoding, and OpenAPI configuration.

---

## Project Structure

```text
product-api/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/zest/productapi/
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       ├── entity/
│   │   │       ├── exception/
│   │   │       ├── repository/
│   │   │       ├── security/
│   │   │       └── service/
│   │   │
│   │   └── resources/
│   │       ├── application.yaml
│   │       └── application-docker.yml
│   │
│   └── test/
│       └── java/
│           └── com/zest/productapi/
│               ├── controller/
│               ├── integration/
│               └── service/
│
├── .gitignore
├── Dockerfile
├── docker-compose.yml
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

---

## API Endpoints

### Authentication APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/users/register` | Register a new user |
| POST | `/api/v1/auth/login` | Authenticate a user |
| POST | `/api/v1/auth/refresh` | Refresh the access token |

### Product APIs

| Method | Endpoint | Description | Authorization |
|--------|----------|-------------|---------------|
| GET | `/api/v1/products` | Get paginated products | USER / ADMIN |
| GET | `/api/v1/products/{id}` | Get product by ID | USER / ADMIN |
| POST | `/api/v1/products` | Create a product | ADMIN |
| PUT | `/api/v1/products/{id}` | Update a product | ADMIN |
| DELETE | `/api/v1/products/{id}` | Delete a product | ADMIN |
| GET | `/api/v1/products/{id}/items` | Get items for a product | USER / ADMIN |

---

## Authentication and Authorization

The application uses JWT-based authentication with refresh token rotation.

### Authentication Flow

```text
User
 |
 | Login
 v
Authentication
 |
 v
Access Token + Refresh Token
 |
 +----------------------+
 |                      |
 v                      v
Access Token        Refresh Token
 |                      |
 v                      v
API Requests         Refresh Request
                        |
                        v
                 Old Token Revoked
                        |
                        v
                 New Tokens Issued
```

### Access Token

After successful login, the client receives an access token.

The access token must be sent with protected API requests using the following HTTP header:

```text
Authorization: Bearer <access-token>
```

### Refresh Token

Refresh tokens are used to obtain new access and refresh tokens when the access token needs to be renewed.

The previous refresh token is revoked during the refresh operation to provide refresh token rotation.

### Roles

The application supports two roles:

- **USER**
    - Can access product GET APIs.
    - Can retrieve product items.

- **ADMIN**
    - Can access product GET APIs.
    - Can retrieve product items.
    - Can create products.
    - Can update products.
    - Can delete products.

---

## Database

The application uses **MySQL** as the primary database.

### Entity Relationship

```text
+-------------+
|   Product   |
+-------------+
| id          |
| productName |
| createdBy   |
| createdOn   |
| modifiedBy  |
| modifiedOn  |
+-------------+
       |
       | 1
       |
       | *
       v
+-------------+
|    Item     |
+-------------+
| id          |
| productId   |
| quantity    |
+-------------+
```

A Product can have multiple Items.

The `Item` entity contains a foreign-key relationship to the Product entity.

### Database Indexing

An index is configured on the `product_id` column of the Item table to improve product-based item retrieval.

---

## Validation

The application uses **Jakarta Validation** to validate incoming request data.

Validation is applied to request DTOs before processing the request.

Examples of validation include:

- Required fields
- Positive quantity values
- Valid request data

Invalid requests are handled by the centralized exception handler.

---

## Exception Handling

The application uses centralized exception handling through a global exception handler.

The API handles common application errors such as:

- Product not found
- Username already exists
- Validation errors
- Unexpected server errors

Errors are returned using a standardized error response structure.

Example:

```json
{
  "status": 404,
  "message": "Product not found with id: 1",
  "timestamp": "2026-01-01T10:00:00"
}
```

---

## Pagination

The product collection API supports pagination using Spring Data `Pageable`.

Example:

```http
GET /api/v1/products?page=0&size=10
```

Pagination allows clients to retrieve products in smaller pages instead of loading the complete collection in a single request.

---

## Testing

The project includes both unit tests and integration tests.

### Testing Technologies

- JUnit 5
- Mockito
- Spring Boot Test
- H2 In-Memory Database

### Test Areas

- Authentication controller
- Product controller
- Item controller
- Authentication service
- Product service
- Item service
- Product API integration flow

### Test Result

The project currently contains **25 automated tests**, all passing successfully.

```text
Tests run: 25
Failures: 0
Errors: 0
Skipped: 0
```

Run the tests with:

### Windows

```powershell
.\mvnw.cmd test
```

### Linux / macOS

```bash
./mvnw test
```

---

## Environment Configuration

Sensitive configuration values are supplied through environment variables instead of being stored directly in the source code.

The application uses:

```text
DB_USERNAME
DB_PASSWORD
JWT_SECRET
```

### Example

Create a `.env` file in the project root:

```text
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password
JWT_SECRET=your_jwt_secret
```

**Do not use real credentials in the example above.**

The `.env` file is excluded through `.gitignore` and should never be committed to the public repository.

### Local Development

For running the application directly from IntelliJ or Maven, configure the required environment variables in the application's run configuration.

The `.env` file is used by Docker Compose for variable substitution; Spring Boot itself does not automatically load a `.env` file.

---

## Running the Application Locally

### Prerequisites

Install the following:

- Java 17 or higher
- MySQL
- Maven (optional because Maven Wrapper is included)

### 1. Configure Environment Variables

Set:

```text
DB_USERNAME
DB_PASSWORD
JWT_SECRET
```

### 2. Build the Application

On Windows:

```powershell
.\mvnw.cmd clean package
```

On Linux / macOS:

```bash
./mvnw clean package
```

### 3. Run the Application

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

On Linux / macOS:

```bash
./mvnw spring-boot:run
```

The application runs on:

```text
http://localhost:8080
```

---

## Docker Setup

The project provides:

- `Dockerfile`
- `docker-compose.yml`

Docker Compose starts the application and MySQL database as separate containers.

```text
                 Docker Compose
                       |
          +------------+------------+
          |                         |
          v                         v
   Product API Container      MySQL Container
          |                         |
          +-----------+-------------+
                      |
                      v
                   Database
```

### 1. Build the Application

The Dockerfile copies the generated JAR from the `target` directory, so build the project first:

Windows:

```powershell
.\mvnw.cmd clean package -DskipTests
```

Linux / macOS:

```bash
./mvnw clean package -DskipTests
```

### 2. Start Docker Containers

```bash
docker compose up --build
```

The API will be available at:

```text
http://localhost:8080
```

### 3. Stop Docker Containers

```bash
docker compose down
```

---

## Swagger / OpenAPI

The project uses Swagger/OpenAPI for API documentation.

After starting the application, open:

```text
http://localhost:8080/swagger-ui/index.html
```

Swagger UI provides an interactive interface for viewing and testing the available API endpoints.

---

## API Usage Examples

### Register User

```http
POST /api/v1/users/register
Content-Type: application/json
```

Request:

```json
{
  "username": "user123",
  "password": "Password@123"
}
```

### Login

```http
POST /api/v1/auth/login
Content-Type: application/json
```

Request:

```json
{
  "username": "user123",
  "password": "Password@123"
}
```

Use the returned access token for protected APIs:

```text
Authorization: Bearer <access-token>
```

### Create Product

```http
POST /api/v1/products
Authorization: Bearer <access-token>
Content-Type: application/json
```

Request:

```json
{
  "productName": "Laptop"
}
```

This operation requires the **ADMIN** role.

---

## Security Considerations

The application implements:

- JWT-based authentication
- Refresh token rotation
- Role-based authorization
- Password encryption
- Jakarta request validation
- CORS configuration
- Environment-based secret management

Sensitive credentials such as database passwords and JWT secrets are not stored directly in the source code.

---

## Build and Run Summary

### Run tests

```powershell
.\mvnw.cmd test
```

### Build application

```powershell
.\mvnw.cmd clean package
```

### Run locally

```powershell
.\mvnw.cmd spring-boot:run
```

### Start with Docker

```powershell
.\mvnw.cmd clean package -DskipTests
docker compose up --build
```

### Stop Docker

```powershell
docker compose down
```

---

## Project Information

**Project:** Zest India Product API  
**Application Type:** RESTful Backend API  
**Java Version:** 17  
**Database:** MySQL  
**Build Tool:** Maven  
**Containerization:** Docker / Docker Compose