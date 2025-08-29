# Utiliser l'image OpenJDK 17
FROM maven:3.9.9-eclipse-temurin-17 AS build

# Définir le répertoire de travail
WORKDIR /build

# Copier le fichier pom.xml
COPY pom.xml .

# Copier le code source
COPY src ./src

# Installer Maven et construire l'application
RUN mvn -q -e -DskipTests dependency:go-offline

# Construire l'application avec le profil de production
RUN mvn -q clean package -DskipTests -Pprod

FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app
RUN apk add --no-cache curl
COPY --from=build /build/target/ecomptaia-backend-1.0.0.jar /app/app.jar
EXPOSE 8080
CMD ["java", "-jar", "-Dspring.profiles.active=prod", "/app/app.jar"]

# Exposer le port 8080
EXPOSE 8080

# Commande pour démarrer l'application avec le profil de production
CMD ["java", "-jar", "-Dspring.profiles.active=prod", "target/ecomptaia-backend-1.0.0.jar"]
