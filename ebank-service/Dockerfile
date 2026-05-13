# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Copier le pom.xml parent et le module ebank-service
COPY pom.xml .
COPY ebank-service/pom.xml ./ebank-service/

# Télécharger les dépendances
RUN mvn dependency:go-offline -pl ebank-service -am 2>/dev/null || true

# Copier le code source
COPY ebank-service/src ./ebank-service/src

# Builder le service
RUN mvn clean package -pl ebank-service -am -DskipTests -q

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copier le JAR compilé
COPY --from=builder /build/ebank-service/target/ebank-service-*.jar app.jar

# Exposer le port
EXPOSE 8057

# Healthcheck
HEALTHCHECK --interval=10s --timeout=5s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8057/actuator/health || exit 1

# Lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]

