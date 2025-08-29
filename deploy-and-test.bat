@echo off
echo ========================================
echo DEPLOIEMENT E-COMPTA-IA DOCKER
echo ========================================

echo.
echo [1/6] Nettoyage des conteneurs existants...
docker-compose -f docker/docker-compose.yml down -v
docker system prune -f

echo.
echo [2/6] Construction des images Docker...
docker-compose -f docker/docker-compose.yml build --no-cache

echo.
echo [3/6] Démarrage des services...
docker-compose -f docker/docker-compose.yml up -d

echo.
echo [4/6] Attente du démarrage des services...
timeout /t 30 /nobreak > nul

echo.
echo [5/6] Vérification de l'état des services...
docker-compose -f docker/docker-compose.yml ps

echo.
echo [6/6] Tests de connectivité...

echo Test PostgreSQL...
docker exec ecomptaia-postgres pg_isready -U ecomptaia_user -d ecomptaia_db

echo Test Backend...
curl -f http://localhost:8080/api/health
if %errorlevel% neq 0 (
    echo ERREUR: Backend non accessible
    exit /b 1
)

echo Test Frontend...
curl -f http://localhost:4200/health
if %errorlevel% neq 0 (
    echo ERREUR: Frontend non accessible
    exit /b 1
)

echo.
echo ========================================
echo DEPLOIEMENT TERMINÉ AVEC SUCCÈS
echo ========================================
echo.
echo URLs d'accès:
echo - Frontend: http://localhost:4200
echo - Backend API: http://localhost:8080
echo - PgAdmin: http://localhost:5050
echo.
echo Logs en temps réel:
echo docker-compose -f docker/docker-compose.yml logs -f
echo.
echo Arrêt des services:
echo docker-compose -f docker/docker-compose.yml down
echo.
pause
