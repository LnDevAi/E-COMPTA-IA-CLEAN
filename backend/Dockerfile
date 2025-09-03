# Utiliser l'image OpenJDK 17
FROM openjdk:17-jdk-slim

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier pom.xml
COPY pom.xml .

# Copier le code source
COPY src ./src

# Installer Maven et construire l'application
RUN apt-get update && apt-get install -y maven curl

# Construire l'application
RUN mvn clean package -DskipTests

# Exposer le port 8080
EXPOSE 8080

# Commande pour démarrer l'application
CMD ["java", "-jar", "target/ecomptaia-backend-1.0.0.jar"]
