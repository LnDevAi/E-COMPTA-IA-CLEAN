# Script de déploiement E-COMPTA-IA avec Podman Desktop
# PowerShell Script pour Windows

Write-Host "=== Déploiement E-COMPTA-IA INTERNATIONAL avec Podman Desktop ===" -ForegroundColor Green
Write-Host "=====================================================" -ForegroundColor Green

# Vérifier que Podman est installé
try {
    $podmanVersion = podman --version
    Write-Host "OK Podman détecte: $podmanVersion" -ForegroundColor Green
} catch {
    Write-Host "ERREUR Podman n'est pas installe ou pas dans le PATH" -ForegroundColor Red
    Write-Host "Veuillez installer Podman Desktop depuis: https://podman-desktop.io/" -ForegroundColor Yellow
    exit 1
}

# Vérifier que Podman Desktop est démarré
try {
    podman info | Out-Null
    Write-Host "OK Podman Desktop est demarre" -ForegroundColor Green
} catch {
    Write-Host "ERREUR Podman Desktop n'est pas demarre" -ForegroundColor Red
    Write-Host "Veuillez demarrer Podman Desktop et reessayer" -ForegroundColor Yellow
    exit 1
}

# Arrêter et supprimer les conteneurs existants
Write-Host "Nettoyage des conteneurs existants..." -ForegroundColor Yellow
podman-compose -f docker/podman-compose.yml down --remove-orphans

# Supprimer les images existantes (optionnel)
Write-Host "Suppression des images existantes..." -ForegroundColor Yellow
podman rmi $(podman images -q ecomptaia-*) --force 2>$null

# Construire et démarrer les services
Write-Host "Construction et demarrage des services..." -ForegroundColor Yellow
podman-compose -f docker/podman-compose.yml up --build -d

# Attendre que les services soient prêts
Write-Host "Attente du demarrage des services..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# Vérifier le statut des services
Write-Host "Statut des services:" -ForegroundColor Cyan
podman-compose -f docker/podman-compose.yml ps

# Vérifier la santé des services
Write-Host "Verification de la sante des services..." -ForegroundColor Cyan

# Backend
try {
    $backendHealth = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -Method Get -TimeoutSec 10
    Write-Host "OK Backend: $($backendHealth.status)" -ForegroundColor Green
} catch {
    Write-Host "ERREUR Backend: Service non accessible" -ForegroundColor Red
}

# Frontend
try {
    $frontendResponse = Invoke-WebRequest -Uri "http://localhost:4200" -Method Get -TimeoutSec 10
    Write-Host "OK Frontend: Service accessible (HTTP $($frontendResponse.StatusCode))" -ForegroundColor Green
} catch {
    Write-Host "ERREUR Frontend: Service non accessible" -ForegroundColor Red
}

# Base de données
try {
    $dbHealth = podman exec ecomptaia-postgres pg_isready -U ecomptaia_user
    Write-Host "OK Base de donnees: $dbHealth" -ForegroundColor Green
} catch {
    Write-Host "ERREUR Base de donnees: Service non accessible" -ForegroundColor Red
}

# Afficher les URLs d'accès
Write-Host "URLs d'acces:" -ForegroundColor Cyan
Write-Host "   Frontend: http://localhost:4200" -ForegroundColor White
Write-Host "   Backend API: http://localhost:8080" -ForegroundColor White
Write-Host "   Base de donnees: localhost:5432" -ForegroundColor White

Write-Host "Déploiement termine!" -ForegroundColor Green
Write-Host "Utilisez 'podman-compose -f docker/podman-compose.yml logs -f' pour voir les logs en temps reel" -ForegroundColor Yellow
