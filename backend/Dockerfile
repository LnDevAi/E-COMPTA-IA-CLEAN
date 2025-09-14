# Stage 1: Build avec Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Définir le répertoire de travail
WORKDIR /app

# Copier les fichiers de dépendances Maven
COPY pom.xml .
COPY src ./src

# Télécharger les dépendances (cache layer)
RUN mvn dependency:go-offline -B

# Construire l'application
RUN mvn clean package -DskipTests -Dmaven.test.skip=true

# Stage 2: Runtime optimisé
FROM eclipse-temurin:17-jre

# Créer un utilisateur non-root pour la sécurité
RUN groupadd -r ecomptaia && useradd -r -g ecomptaia ecomptaia

# Installer les outils nécessaires
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Définir le répertoire de travail
WORKDIR /app

# Copier le JAR depuis le stage de build
COPY --from=build /app/target/ecomptaia-backend-1.0.0.jar app.jar

# Créer le répertoire de logs
RUN mkdir -p /app/logs && chown -R ecomptaia:ecomptaia /app

# Changer vers l'utilisateur non-root
USER ecomptaia

# Exposer le port
EXPOSE 8080

# Configuration JVM optimisée
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/actuator/health || exit 1

# Commande pour démarrer
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
