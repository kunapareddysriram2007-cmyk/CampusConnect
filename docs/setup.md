# Development setup

## Requirements

- Node.js 26.13.0 LTS with npm
- Java 17
- Docker Desktop with Docker Compose
- Git

## Environment file and credentials

The root `.env` is consumed automatically by Docker Compose. It is **not** automatically loaded by PowerShell or by a separately launched Spring Boot process. `.env` is ignored by Git; verify this before creating it:

```powershell
git check-ignore -v .env
Copy-Item .env.example .env
```

Edit `.env` locally. Set `SPRING_DATASOURCE_PASSWORD` to the same value as `MYSQL_PASSWORD`; the root and app users must have different passwords. Compose maps host port `3307` to MySQL container port `3306` to avoid conflict with a native MySQL service on the standard host port. These are local-only values. Never commit `.env`, and do not put real credentials in `.env.example`.

## Start the database

At the repository root:

```powershell
docker compose up -d --wait mysql
docker compose ps
```

Wait for the `mysql` service health status to show `healthy` before starting the backend.

## Start the backend

Open a PowerShell terminal at the repository root and import the `.env` key-value pairs into this process before launching Maven:

```powershell
Get-Content .env |
  Where-Object { $_ -match '^\s*[^#\s][^=]*=' } |
  ForEach-Object {
    $name, $value = $_.Split('=', 2)
    Set-Item -Path "Env:$($name.Trim())" -Value $value.Trim()
  }
.\backend\mvnw.cmd -f backend\pom.xml spring-boot:run
```

The import handles blank lines and comments and splits each setting at its first `=`. Keep `.env` values unquoted, as shown in `.env.example`. This configures the `SPRING_DATASOURCE_*` and `SERVER_PORT` variables for the Maven process without persisting them in Windows user or machine settings.

Health is available at <http://localhost:8080/actuator/health>.

## Start the frontend

Open another terminal:

```powershell
cd frontend
npm ci
npm run dev
```

Open the local URL printed by Vite (normally <http://localhost:5173>). Frontend code should call relative `/api/...` URLs; Vite forwards them to `http://localhost:8080`. This avoids a broad cross-origin policy for local development. The backend connects to Compose MySQL at `localhost:3307`.

## Run checks

Frontend:

```powershell
cd frontend
npm ci
npm test
npm run build
```

Backend:

```powershell
cd backend
.\mvnw.cmd verify
```

Backend integration tests start MySQL 8.4 containers through Testcontainers. Docker must be running. H2 is not used as a substitute for MySQL.

## Database ownership

Flyway is the sole schema migration authority. Put SQL migrations in `backend/src/main/resources/db/migration/` using versioned names such as `V1__foundation.sql`. Hibernate validates the resulting schema. Spring SQL initialization is disabled. Spring Session JDBC and its tables are deferred until the authentication phase; when added, their schema must also be managed by Flyway and automatic session schema initialization must remain disabled.

The current `V1__foundation_baseline.sql` establishes the Flyway migration baseline only. Application tables will be introduced by later versioned migrations alongside their corresponding JPA entities.

## Backend package organization

The backend will remain a modular monolith under `com.campusconnect`. As application modules are introduced, organize them by domain under `user`, `event`, `club`, and `registration`; cross-cutting code belongs in `shared`. Each module should keep its related API, application, domain, and persistence code together where needed. These are planned boundaries only; no empty future packages are created in the foundation.
