# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Copier le pom.xml parent et le module gateway-service
COPY pom.xml .
COPY gateway-service/pom.xml ./gateway-service/

# Télécharger les dépendances
RUN mvn dependency:go-offline -pl gateway-service -am 2>/dev/null || true

# Copier le code source
COPY gateway-service/src ./gateway-service/src

# Builder le service
RUN mvn clean package -pl gateway-service -am -DskipTests -q

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copier le JAR compilé
COPY --from=builder /build/gateway-service/target/gateway-service-*.jar app.jar

# Exposer le port
EXPOSE 8888

# Healthcheck
HEALTHCHECK --interval=10s --timeout=5s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8888/actuator/health || exit 1

# Lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]

