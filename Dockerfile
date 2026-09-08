FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -q 2>/dev/null || true

COPY src ./src
RUN mvn package -DskipTests -q \
 && JAR_FILE=$(find target -maxdepth 1 -type f -name "*.jar" ! -name "*.jar.original" -print -quit) \
 && test -n "$JAR_FILE" \
 && cp "$JAR_FILE" /app/app.jar

FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

RUN addgroup -S appointment-search-service && adduser -S appointment-search-service -G appointment-search-service
USER appointment-search-service

COPY --from=build /app/app.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
