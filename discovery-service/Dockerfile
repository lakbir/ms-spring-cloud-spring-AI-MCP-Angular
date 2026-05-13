# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Copier le pom.xml parent et le module discovery-service
COPY pom.xml .
COPY discovery-service/pom.xml ./discovery-service/

# Télécharger les dépendances
RUN mvn dependency:go-offline -pl discovery-service -am 2>/dev/null || true

# Copier le code source
COPY discovery-service/src ./discovery-service/src

# Builder le service
RUN mvn clean package -pl discovery-service -am -DskipTests -q

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copier le JAR compilé
COPY --from=builder /build/discovery-service/target/discovery-service-*.jar app.jar

# Exposer le port Eureka
EXPOSE 8761

# Healthcheck
HEALTHCHECK --interval=10s --timeout=5s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8761/actuator/health || exit 1

# Lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]

