# BUILD

FROM maven:3-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY pom.xml ./pom.xml
COPY common/pom.xml ./common/pom.xml
COPY client/pom.xml ./client/pom.xml
COPY server/pom.xml ./server/pom.xml

RUN --mount=type=cache,target=/root/.m2 mvn -B dependency:go-offline

COPY common ./common
COPY client ./client
COPY server ./server

RUN --mount=type=cache,target=/root/.m2 mvn -B clean package -DskipTests

# RUNTIME

FROM eclipse-temurin:21-jre-alpine AS runtime

RUN addgroup -S quarkus && adduser -S -G quarkus quarkus

WORKDIR /app

COPY --from=build /app/server/target/quarkus-app .

RUN chown -R quarkus:quarkus /app

USER quarkus

ENTRYPOINT ["java", "-jar", "quarkus-run.jar"]
