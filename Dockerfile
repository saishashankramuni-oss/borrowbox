# ==============================================================================
# BorrowBox Container Image Build Script
# Single Spring Boot Application serving static HTML/CSS/JS frontend & REST APIs
# ==============================================================================

# Stage 1: Build Stage using Java 25
FROM eclipse-temurin:25-jdk-noble AS builder

WORKDIR /build

# Copy backend pom.xml and source code
COPY backend/pom.xml ./backend/
COPY backend/src ./backend/src

# Ensure frontend assets are updated in backend static resources
COPY frontend/ ./backend/src/main/resources/static/

WORKDIR /build/backend

# Install Maven build tool
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Package application JAR file
RUN mvn clean package -DskipTests

# Stage 2: Production Lightweight Runtime Stage
FROM eclipse-temurin:25-jre-noble

WORKDIR /app

# Install curl for container health check
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Security: Create dedicated non-root application user and persistent upload directory
RUN addgroup --system --gid 1001 borrowbox && \
    adduser --system --uid 1001 --ingroup borrowbox borrowbox && \
    mkdir -p /app/uploads && \
    chown -R borrowbox:borrowbox /app

# Copy compiled JAR from build stage
COPY --from=builder /build/backend/target/borrowbox-backend-1.0.0.jar app.jar
RUN chown borrowbox:borrowbox /app/app.jar

# Run as non-root user
USER borrowbox

EXPOSE 8080

# Production default environment variables
ENV SPRING_PROFILES_ACTIVE=prod \
    SERVER_PORT=8080 \
    UPLOAD_DIR=/app/uploads

# Health check endpoint
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
