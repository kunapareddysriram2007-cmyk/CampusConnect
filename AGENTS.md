# CampusConnect contributor guidance

- Keep this application a modular monolith. Do not add microservices.
- Do not add business features until specifically requested.
- Use Java 17 and the Spring Boot version pinned in `backend/pom.xml`.
- Use Flyway for all database schema changes. Keep Hibernate at `ddl-auto: validate`; never use Hibernate schema generation to manage schema.
- Do not commit `.env`, credentials, or secrets. `.env.example` must contain placeholders only.
- Keep frontend API calls on relative `/api` paths and use the Vite development proxy.
- Run frontend tests/build and `backend/mvnw verify` for relevant changes. Backend integration tests require Docker and MySQL Testcontainers.
