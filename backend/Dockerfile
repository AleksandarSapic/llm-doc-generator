# Stage 1: build
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build
# Copy pom.xml first so dependency resolution is cached independently of source changes
COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline
COPY src ./src
RUN mvn -B -ntp -DskipTests package

# Stage 2: extract layered JAR
FROM builder AS extractor
RUN java -Djarmode=layertools -jar target/llm-doc-generator-0.0.1-SNAPSHOT.jar \
    extract --destination /extracted

# Stage 3: runtime
FROM eclipse-temurin:21-jre-jammy

# Install curl for HEALTHCHECK (single layer, clean up apt cache)
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Non-root user (uid 1001; 1000 is often taken by base images)
RUN groupadd --gid 1001 appgroup \
    && useradd --uid 1001 --gid appgroup --shell /bin/false --no-create-home appuser

WORKDIR /app

# Copy layers in order of change frequency (most stable first → best cache reuse)
COPY --from=extractor --chown=appuser:appgroup /extracted/dependencies/ ./
COPY --from=extractor --chown=appuser:appgroup /extracted/spring-boot-loader/ ./
COPY --from=extractor --chown=appuser:appgroup /extracted/snapshot-dependencies/ ./
COPY --from=extractor --chown=appuser:appgroup /extracted/application/ ./

USER appuser

EXPOSE 8080

# 60s start-period: accounts for Flyway migrations + Hibernate schema validation on cold start
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Exec form: JVM is PID 1, receives SIGTERM directly for graceful shutdown
# Note: Spring Boot 3+ launcher is under the .launch. sub-package
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:InitialRAMPercentage=50.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "org.springframework.boot.loader.launch.JarLauncher"]
