#!/bin/bash
# Script de build, déploiement et test complet pour E-COMPTA-IA INTERNATIONAL

set -e

# 1. Nettoyage Maven
cd backend
mvn clean

# 2. Compilation Maven
mvn package -DskipTests
cd ..

# 3. Build et lancement Docker Compose
cd docker
# Arrêt des anciens containers
if docker compose ps | grep ecomptaia-backend; then
  docker compose down
fi
# Build et up
docker compose up --build -d
cd ..

# 4. Vérification des logs backend
echo "--- Logs backend ---"
docker compose logs backend | tail -n 50

# 5. Vérification des logs frontend
echo "--- Logs frontend ---"
docker compose logs frontend | tail -n 50

# 6. Test de communication frontend-backend
curl -i http://localhost:8080/api/auth/test || echo "Test API backend échoué"

# 7. Fin
echo "Déploiement et test terminés."
