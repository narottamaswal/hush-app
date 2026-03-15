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


**option 1 — local docker**

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


## common issues

**404 on page refresh** — the `SpaRoutingFilter` forwards unknown routes to index.html so angular can handle them. if you're getting 404s on direct url access, check that filter is in the right package.

**backend exits with code 1** — almost always a database connection issue or missing google credentials. run `docker compose logs app` to see the actual error.

**schema validation error on startup** — if you switched from `ddl-auto=update` to `none`, make sure flyway has run the migration files and the tables exist. check with `docker compose exec postgres psql -U pasteuser -d pastebin` then `\dt`.

**google login says app is restricted** — go to oauth consent screen in google console, switch to external, add your email as a test user.
