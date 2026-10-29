# CivicShield

CivicShield is a Spring Boot 3 / Java 17 community road-hazard reporting dashboard. The web UI is served from `src/main/resources/static`; REST endpoints are under `/api`.

## Dashboard improvements in this revision

- Search now covers report title, location, category, report code, description, and reporter name.
- Added a hazard-category filter alongside status and priority filters. Filters combine, and **Clear filters** resets all of them.
- Dashboard statistics are calculated from the successful `/api/reports` response, avoiding a broken dashboard when the separate stats endpoint is temporarily unavailable.
- The last successful refresh time is displayed; failed refreshes show a useful error and do not claim the refresh succeeded.
- Analytics continue to use the live report list.

## Run locally

Requirements: Java 17 and Maven 3.9+.

The application uses Spring Data JPA and PostgreSQL. Set the following environment variables before running: `DATABASE_URL` (a JDBC URL such as `jdbc:postgresql://localhost:5432/civicshield`), `DATABASE_USERNAME`, and `DATABASE_PASSWORD`. Then run `mvn spring-boot:run`, or build with `mvn -DskipTests package` and run the resulting JAR. The server listens on `PORT` or 8080.

## Deploy to Render

The included `render.yaml` defines the web service, but does not provision a PostgreSQL database. Ensure a Render PostgreSQL database exists and the web service has `DATABASE_URL`, `DATABASE_USERNAME`, and `DATABASE_PASSWORD` set to the correct values. `DATABASE_URL` must be in JDBC format for Spring Boot (`jdbc:postgresql://...`), not a raw `postgres://...` URL. Keep credentials in Render environment settings, not in source control.

## API endpoints

- `GET /api/reports` — list reports newest first
- `POST /api/reports` — create a report
- `PATCH /api/reports/{id}/status` — advance/update status
- `DELETE /api/reports/{id}` — delete a report
- `GET /api/stats` — server-side summary statistics

The dashboard's status action advances through Submitted → Verified → In Progress → Resolved. This prototype currently does not include authentication/role-based authorization for status changes or deletion; add authorization before exposing administrative operations to the public.
