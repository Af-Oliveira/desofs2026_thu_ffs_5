# Build stage: compile the Spring Boot application
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY mvnw pom.xml ./
COPY .mvn/ .mvn/
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B
COPY src/ src/
RUN ./mvnw -B package -DskipTests

# Runtime stage: minimal JRE Alpine image
FROM eclipse-temurin:17-jre-alpine AS runtime
RUN addgroup -S vendnet && adduser -S vendnet -G vendnet
RUN mkdir -p /var/vendnet /var/vendnet/logs /var/vendnet/backups /var/vendnet/reports \
    && chown -R vendnet:vendnet /var/vendnet
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN chown vendnet:vendnet app.jar
USER vendnet:vendnet
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --retries=3 --start-period=60s \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-XX:+UseZGC", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
