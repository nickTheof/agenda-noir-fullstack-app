# Agenda Noir
*Organize, track, and manage your projects effortlessly*
## ⚙️ Tech Stack
### Backend
- Java 21
- Spring Boot
- Spring Security (JWT)
- Spring Data JPA
- Spring Email (Spring Boot Starter Mail)
- Gradle
- Swagger / OpenAPI 3.1
---
### Frontend
- React
- Typescript
- React Router
- React Hook Form
- Zod
- ShadCN
- Tailwind CSS
- Vite

## 🚀 Getting Started

### ✅ Prerequisites

Before you begin, ensure you have the following installed and configured:
- **Docker & Docker Compose**  
  Required for building and running the full stack using containers.

- **Email Service Provider**  
  Required to configure and enable email functionality (e.g., password reset, verification emails).  
  You can use services like:
    - Gmail SMTP
    - MailTrap

  Make sure to collect the following from your provider:
    - SMTP host
    - SMTP port
    - SMTP username (login/email)
    - SMTP password or app-specific password (depending on the provider)

> 💡 These values are set in the `.env` file.

For running backend unit tests, ensure you have the following:
- **Java 21**  
  Required for building and executing the backend and tests.

- **Gradle**  
  You can use the provided wrapper (`./gradlew`) — no need to install Gradle globally.


## Set up instructions

1. **Clone the repository**:
    ```bash
    git clone git@github.com:nickTheof/agenda-noir-fullstack-app.git
    cd agenda-noir-fullstack-app
    ```

2.  **Configure environment variables**:

Create a `.env` file in the **root directory of the project** (alongside your `docker-compose.yml`) based on `.env.example`.

> These variables are injected into the backend container by Docker Compose and then picked up by Spring Boot using `${...}` placeholders in `application.properties`.
   
```env
    # 🛠️ Database Configuration
    MYSQL_ROOT_PASSWORD=          # Root password for MySQL (used during DB setup)
    MYSQL_USER=                   # Non-root MySQL user that the app will use
    MYSQL_PASSWORD=               # Password for the above MySQL user
    MYSQL_DATABASE=projectsdb     # Name of the MySQL database to be used
    
    # Spring Boot Datasource (used by the backend app)
    SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/projectsdb   # JDBC URL pointing to the MySQL service
    SPRING_DATASOURCE_USERNAME=                                # Must match MYSQL_USER
    SPRING_DATASOURCE_PASSWORD=                                # Must match MYSQL_PASSWORD
    SPRING_PROFILES_ACTIVE=dev                                 # Profile to load: typically 'dev', 'prod', or 'test'
    
    # 📧 Email Configuration (for verification and password recovery)
    MAIL_HOST=smtp.example.com     # Your SMTP server (e.g. smtp.gmail.com)
    MAIL_PORT=587                  # SMTP port (commonly 587 for TLS, 465 for SSL)
    MAIL_USERNAME=                 # SMTP login username
    MAIL_PASSWORD=                 # SMTP login password
    
    # 🔐 Security
    JWT_SECRET_KEY=               # Secret key for signing JWT tokens (use a strong random string)
    
    # 👤 Super Admin Bootstrap User
    SUPERUSER_EMAIL=              # Email for the initial admin user (created at first launch)
    SUPERUSER_PASSWORD=           # Password for the admin user
    SUPERUSER_FIRSTNAME=          # First name for the admin user
    SUPERUSER_LASTNAME=           # Last name for the admin user
```

3. **Build the images**:
   Use Docker Compose to build all images of the services.
    ```bash
    docker-compose build
    ```

4. **Start the services**:
   Use Docker Compose to start all services.
    ```bash
    docker-compose up -d
    ```

## 🧰 Services Overview

1. **Frontend - React**
    - Accessible at: [http://localhost:5173](http://localhost:5173)

2. **Backend - Spring Boot API**
    - Accessible at: [http://localhost:8080](http://localhost:8080)
    - Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

3. **Database - MySQL**
    - Port: `3307`
    - Managed via Docker

## 🧪 Running Backend Tests

> ⚠️ **Requires Java 21**

To run unit and integration tests for the backend, make sure Java 21 is installed and available in your environment.

### ✅ Run All Tests

```bash
cd backend
./gradlew test
```

### 📊 View Test Reports
After running tests, you can view the test and coverage reports:
- **JUnit Test Report**:
    Open in browser:
    ```bash
    /backend/build/reports/tests/test/index.html
    ```

- **JaCoCo Code Coverage Report**:
  Open in browser:
    ```bash
    /backend/build/reports/jacoco/test/html/index.html
    ```
  
### 🔗 API Integration Tests

A Postman collection with integration tests for the backend API is included in the `backend/src/main/resources/postman` folder:
You can import this JSON file into Postman to:

- Run predefined integration tests against the running backend API.
- Explore and manually test all available endpoints.
- Verify request/response payloads and headers.

> 💡 Make sure the backend service is running at `http://localhost:8080` before running the tests.

---