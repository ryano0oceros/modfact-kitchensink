# Maven files staging
FROM alpine:3.18 as maven-files
WORKDIR /staging
COPY .mvn .mvn/
COPY mvnw .
COPY pom.xml .

# Build stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace/app
COPY --from=maven-files . .
COPY src src
RUN chmod +x mvnw
RUN ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# Run stage
FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
COPY --from=build /workspace/app/src/main/resources/application.yml /app/
ENTRYPOINT ["java","-cp","app:app/lib/*","io.quarkus.runner.GeneratedMain"] 