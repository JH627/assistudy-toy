# syntax=docker/dockerfile:1
# ── Build stage ──────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace

COPY gradlew .
COPY gradle gradle
COPY settings.gradle .
COPY build.gradle .

# Copy only build configs for all subprojects (dependency resolution layer)
COPY shared/build.gradle shared/build.gradle
COPY homework-service/build.gradle homework-service/build.gradle
COPY webrtc-service/build.gradle webrtc-service/build.gradle
COPY user-service/build.gradle user-service/build.gradle
COPY common-service/build.gradle common-service/build.gradle
COPY log-send-service/build.gradle log-send-service/build.gradle
COPY log-process-service/build.gradle log-process-service/build.gradle
COPY apigateway-service/build.gradle apigateway-service/build.gradle
COPY discovery-service/build.gradle discovery-service/build.gradle

# Pre-download dependencies — cached until any build.gradle changes
RUN --mount=type=cache,target=/root/.gradle \
    chmod +x gradlew && ./gradlew :webrtc-service:dependencies --no-daemon

# Copy source and build
COPY shared/src shared/src
COPY webrtc-service/src webrtc-service/src

RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew :webrtc-service:bootJar -x test --no-daemon

# ── Runtime stage ─────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /workspace/webrtc-service/build/libs/*.jar app.jar

EXPOSE 8087

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
