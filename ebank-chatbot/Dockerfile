# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Copier le pom.xml parent et le module ebank-chatbot
COPY pom.xml .
COPY ebank-chatbot/pom.xml ./ebank-chatbot/

# Télécharger les dépendances
RUN mvn dependency:go-offline -pl ebank-chatbot -am 2>/dev/null || true

# Copier le code source
COPY ebank-chatbot/src ./ebank-chatbot/src

# Builder le service
RUN mvn clean package -pl ebank-chatbot -am -DskipTests -q

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copier le JAR compilé
COPY --from=builder /build/ebank-chatbot/target/ebank-chatbot-*.jar app.jar

# Exposer le port
EXPOSE 8058

# Variable d'environnement pour la clé API (à fournir au runtime)
ENV API_KEY=${API_KEY}

# Healthcheck
HEALTHCHECK --interval=10s --timeout=5s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8058/actuator/health || exit 1

# Lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]

