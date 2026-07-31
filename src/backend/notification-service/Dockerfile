# ---- Build stage ----
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -q -DskipTests dependency:go-offline || true
COPY src ./src
RUN ./mvnw -q clean package -DskipTests

# ---- Run stage ----
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
RUN useradd -ms /bin/bash spring
USER spring
COPY --from=build /app/target/notification-service-*.jar app.jar
EXPOSE 8086
ENV SERVER_PORT=8086
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
