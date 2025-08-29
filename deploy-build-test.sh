#!/bin/bash
# Script de build, déploiement et test complet pour E-COMPTA-IA INTERNATIONAL

set -e

# 1. Build et lancement Docker Compose
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
cd docker && docker compose logs backend | tail -n 50 | cat && cd ..

# 5. Vérification des logs frontend
echo "--- Logs frontend ---"
cd docker && docker compose logs frontend | tail -n 50 | cat && cd ..

# 3. Test de communication frontend-backend
curl -i http://localhost/api/health || echo "Test API backend échoué"

# 4. Fin
echo "Déploiement et test terminés."
