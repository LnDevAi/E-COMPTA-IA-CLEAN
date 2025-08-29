# PowerShell script: deploy-and-test.ps1
# Build, deploy, and test E-COMPTA-IA INTERNATIONAL (Windows)

$ErrorActionPreference = "Stop"

Write-Host "1. Nettoyage Maven"
Push-Location backend
mvn clean

Write-Host "2. Compilation Maven"
mvn package -DskipTests
Pop-Location

Write-Host "3. Build et lancement Docker Compose"
Push-Location docker
# Arrêt des anciens containers
try {
    docker compose ps | Select-String "ecomptaia-backend" | Out-Null
    docker compose down
} catch {}
# Build et up
docker compose up --build -d
Pop-Location

Write-Host "4. Logs backend"
docker compose -f docker/docker-compose.yml logs backend | Select-Object -Last 50

Write-Host "5. Logs frontend"
docker compose -f docker/docker-compose.yml logs frontend | Select-Object -Last 50

Write-Host "6. Test de communication frontend-backend"
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/test" -UseBasicParsing
    Write-Host $response.StatusCode
    Write-Host $response.Content
} catch {
    Write-Host "Test API backend échoué"
}

Write-Host "Déploiement et test terminés."
