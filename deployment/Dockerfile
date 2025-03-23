# Maven files staging
FROM alpine:3.18 as maven-files
WORKDIR /staging
COPY .mvn/ .mvn/
COPY mvnw .
RUN chmod +x mvnw
COPY pom.xml .

# Build stage
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace/app
COPY --from=maven-files /staging/ .
COPY src/ src/
COPY config/ config/
RUN ./mvnw install -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl
WORKDIR /app
COPY --from=build /workspace/app/target/quarkus-app/lib/ /app/lib/
COPY --from=build /workspace/app/target/quarkus-app/*.jar /app/
COPY --from=build /workspace/app/target/quarkus-app/app/ /app/app/
COPY --from=build /workspace/app/target/quarkus-app/quarkus/ /app/quarkus/
COPY --from=build /workspace/app/config/ /app/config/
ENTRYPOINT ["java", "-jar", "/app/quarkus-run.jar"] 