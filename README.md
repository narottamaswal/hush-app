# hushapp

a simple app to create and share text pastes with optional password protection. you sign in with google, create a paste, get a shareable link. anyone with the link can view it, no account needed on their end.

built with angular 18 on the frontend, spring boot 3 on the backend, postgres for the database. the frontend and backend are served together from one spring boot jar so there's no separate server to manage.

---

## what it does

- sign in with google
- create a paste with a title, content and an optional password
- get a short link like `http://localhost:8080/items/Qnm84666`
- share that link with anyone, they don't need to be logged in
- if you set a password they'll need to enter it to view
- you can edit or delete your own pastes
- my items page lists everything you've created with inline edit and delete

---

## before you start

you'll need these installed locally

- java 17 or higher
- node 18 or higher
- docker (if you want to run everything in containers)
- a google cloud account (free) for oauth
- a postgres database — either local docker or neon.tech

---

## google oauth setup

this is the only slightly annoying step but you only do it once.

1. go to console.cloud.google.com
2. create a new project or use an existing one
3. go to apis and services → credentials → create credentials → oauth client id
4. choose web application as the type
5. under authorised redirect uris add this exactly:
   ```
   http://localhost:8080/login/oauth2/code/google
   ```
6. hit create and copy the client id and client secret somewhere safe

if you get an error saying the app is restricted to organisation users, go to apis and services → oauth consent screen, change user type to external, and add your own gmail as a test user.

---

## database setup

**option 1 — neon (easiest, free, cloud)**

go to neon.tech, create a project, copy the connection string. it looks like this:

```
postgresql://user:password@ep-something.aws.neon.tech/neondb?sslmode=require
```

**option 2 — local docker**

```bash
docker run -d \
  --name local-postgres \
  -e POSTGRES_DB=pastebin \
  -e POSTGRES_USER=pasteuser \
  -e POSTGRES_PASSWORD=pastepass \
  -p 5432:5432 \
  postgres:16-alpine
```

the database tables get created automatically by flyway when the app starts for the first time. you don't need to run any sql manually.

---

## running locally

create `backend/src/main/resources/application-local.properties` and fill in your values:

```properties
spring.datasource.url=jdbc:postgresql://your-host/your-db
spring.datasource.username=your-user
spring.datasource.password=your-password
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=none
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
spring.flyway.baseline-version=0

spring.security.oauth2.client.registration.google.client-id=paste-your-client-id
spring.security.oauth2.client.registration.google.client-secret=paste-your-client-secret
spring.security.oauth2.client.registration.google.scope=openid,profile,email
spring.security.oauth2.client.registration.google.redirect-uri=http://localhost:8080/login/oauth2/code/google

server.servlet.session.cookie.same-site=lax
server.servlet.session.cookie.path=/
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.name=JSESSIONID
```

then build the frontend and drop it into spring's static folder:

```bash
cd frontend
npm install
npm run build -- --configuration production
```

then run the backend:

```bash
cd ../backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

open `http://localhost:8080` and the app should load.

---

## running with docker

create a `.env` file at the project root:

```env
GOOGLE_CLIENT_ID=your-client-id
GOOGLE_CLIENT_SECRET=your-client-secret
```

if you're using neon or any external database, update the datasource url in `application.properties` as well.

then:

```bash
docker compose up --build
```

this builds angular, compiles spring boot, and starts everything. only two containers run — the app and postgres. open `http://localhost:8080`.

---

## project structure

```
hushapp/
├── backend/
│   ├── src/main/java/com/pastebin/app/
│   │   ├── config/          security, oauth handler, spa routing filter
│   │   ├── controller/      auth and items api endpoints
│   │   ├── model/           database entities and dtos
│   │   ├── repository/      database queries
│   │   └── service/         business logic, avatar generation
│   └── src/main/resources/
│       ├── db/migration/    flyway sql scripts
│       └── application.properties
├── frontend/
│   └── src/app/
│       ├── models/          typescript interfaces
│       ├── pages/           create, view, my-items pages
│       └── services/        api calls and auth state
├── Dockerfile               builds frontend + backend into one jar
├── docker-compose.yml
└── .env
```

---

## api endpoints

| method | path | auth required |
|--------|------|---------------|
| get | /api/me | no (returns 401 if not logged in) |
| post | /api/me/avatar | yes |
| get | /api/items/mine | yes |
| post | /api/items | yes |
| get | /api/items/:hash | no |
| get | /api/items/:hash/meta | no |
| post | /api/items/:hash/unlock | no |
| put | /api/items/:hash | yes (owner only) |
| delete | /api/items/:hash | yes (owner only) |

---

## a few things worth knowing

**avatar** — when you log in for the first time it generates an initials-based avatar using your name in java on the server side. it picks a random dark background and a random light text colour and stores it as a base64 png in the database. no external service involved.

**passwords** — paste passwords are hashed with sha-256 before storing. the actual password is never saved in the database.

**hash links** — each paste gets an 8 character random alphanumeric hash like `Qnm84666`. it checks for collisions so no two pastes get the same link.

**flyway** — database schema is managed by flyway migrations in `backend/src/main/resources/db/migration/`. if you ever need to change the schema, add a new file like `V3__your_change.sql` and it runs automatically on next startup. never edit existing migration files.

**same origin** — the frontend is served by spring boot itself, not a separate nginx. this means no cors issues and session cookies just work without any extra config.

---

## common issues

**404 on page refresh** — the `SpaRoutingFilter` forwards unknown routes to index.html so angular can handle them. if you're getting 404s on direct url access, check that filter is in the right package.

**backend exits with code 1** — almost always a database connection issue or missing google credentials. run `docker compose logs app` to see the actual error.

**schema validation error on startup** — if you switched from `ddl-auto=update` to `none`, make sure flyway has run the migration files and the tables exist. check with `docker compose exec postgres psql -U pasteuser -d pastebin` then `\dt`.

**google login says app is restricted** — go to oauth consent screen in google console, switch to external, add your email as a test user.