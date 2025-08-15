# Gami AI Backend

This is a Spring Boot backend for the Gami AI project.

## Features

- User authentication (JWT)
- Lesson, problem, and submission management
- AI feedback integration
- PostgreSQL database support

## Project Structure

```
gami-ai-be/
├── src/
│   ├── main/
│   │   ├── java/com/project/gamiai/...
│   │   └── resources/application.yaml
│   └── test/
├── pom.xml
├── Dockerfile
├── README.md
```

## Configuration

All configuration is managed in `src/main/resources/application.yaml`.  
You can override sensitive values using environment variables (recommended for deployment):

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `JWT_SECRET`
- `CORS_ALLOWED_ORIGINS`
- etc.

## Build & Run Locally

```sh
./mvnw clean package
java -jar target/gami-ai-be-0.0.1-SNAPSHOT.jar
```

## Docker

Build and run with Docker:

```sh
docker build -t gami-ai-be .
docker run -p 8080:8080 gami-ai-be
```

## Deploy on Railway

1. Push your code to GitHub.
2. Create a new Railway project and select your repo.
3. Railway will build your Dockerfile and run your app.
4. Set environment variables in Railway for secrets and database config.

## API Endpoints

See the controllers in `src/main/java/com/project/gamiai/controller/` for available endpoints.