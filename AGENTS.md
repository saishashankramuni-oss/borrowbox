# BorrowBox Agent Notes

## Deployment

- BorrowBox is a single Spring Boot application: the deployable backend also serves the website from `backend/src/main/resources/static`. Do not assume `frontend/` is an independently deployable app.
- Use Java 25 and Maven. From `backend/`, run `mvn clean verify` to build and test, then `mvn package` and start `target/borrowbox-backend-1.0.0.jar` with `java -jar`.
- No Docker, compose, or cloud-provider deployment manifests currently exist. Ask which hosting provider is intended before adding provider-specific infrastructure.
- Configure a persistent MySQL 8 database for production with `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DB_DRIVER=com.mysql.cj.jdbc.Driver`, and `DB_DIALECT=org.hibernate.dialect.MySQLDialect`. The default H2 database is in-memory and loses data on restart.
- Set `JWT_SECRET` through the host's secret manager to a strong, unique value; never use the checked-in fallback or demo values. `.env.example` is a reference, not a file Spring Boot loads automatically.
- Set `UPLOAD_DIR` to persistent storage. The default `uploads` directory is local to the process and is unsuitable for ephemeral instances or multiple replicas without shared/object storage.
- `application.yml` enables the H2 console and defaults the server to port 8080. Disable the console in production and configure the port expected by the host (Spring Boot's `SERVER_PORT` environment variable overrides `server.port`; `PORT` alone is not wired here).
- Review `V2__seed_data.sql` before any public deployment: it inserts demo accounts and data with documented demo credentials. Do not change an already-applied Flyway migration; add a new migration for follow-up database changes.
- For detailed local setup and tests, see [README.md](README.md). For component boundaries, see [docs/architecture.md](docs/architecture.md); API details are in [docs/api-documentation.md](docs/api-documentation.md).