# Script de déploiement E-COMPTA-IA avec Podman Desktop
# PowerShell Script pour Windows

Write-Host "🚀 Déploiement E-COMPTA-IA INTERNATIONAL avec Podman Desktop" -ForegroundColor Green
Write-Host "=====================================================" -ForegroundColor Green

# Vérifier que Podman est installé
try {
    $podmanVersion = podman --version
    Write-Host "✅ Podman détecté: $podmanVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Podman n'est pas installé ou pas dans le PATH" -ForegroundColor Red
    Write-Host "Veuillez installer Podman Desktop depuis: https://podman-desktop.io/" -ForegroundColor Yellow
    exit 1
}

# Vérifier que Podman Desktop est démarré
try {
    podman info | Out-Null
    Write-Host "✅ Podman Desktop est démarré" -ForegroundColor Green
} catch {
    Write-Host "❌ Podman Desktop n'est pas démarré" -ForegroundColor Red
    Write-Host "Veuillez démarrer Podman Desktop et réessayer" -ForegroundColor Yellow
    exit 1
}

# Arrêter et supprimer les conteneurs existants
Write-Host "🔄 Nettoyage des conteneurs existants..." -ForegroundColor Yellow
podman-compose down --remove-orphans

# Supprimer les images existantes (optionnel)
Write-Host "🗑️  Suppression des images existantes..." -ForegroundColor Yellow
podman rmi $(podman images -q ecomptaia-*) --force 2>$null

# Construire et démarrer les services
Write-Host "🔨 Construction et démarrage des services..." -ForegroundColor Yellow
podman-compose up --build -d

# Attendre que les services soient prêts
Write-Host "⏳ Attente du démarrage des services..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# Vérifier le statut des services
Write-Host "📊 Statut des services:" -ForegroundColor Cyan
podman-compose ps

# Vérifier la santé des services
Write-Host "🏥 Vérification de la santé des services..." -ForegroundColor Cyan

# Backend
try {
    $backendHealth = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -Method Get -TimeoutSec 10
    Write-Host "✅ Backend: $($backendHealth.status)" -ForegroundColor Green
} catch {
    Write-Host "❌ Backend: Service non accessible" -ForegroundColor Red
}

# Frontend
try {
    $frontendResponse = Invoke-WebRequest -Uri "http://localhost:4200" -Method Get -TimeoutSec 10
    Write-Host "✅ Frontend: Service accessible (HTTP $($frontendResponse.StatusCode))" -ForegroundColor Green
} catch {
    Write-Host "❌ Frontend: Service non accessible" -ForegroundColor Red
}

# Base de données
try {
    $dbHealth = podman exec ecomptaia-postgres pg_isready -U ecomptaia
    Write-Host "✅ Base de données: $dbHealth" -ForegroundColor Green
} catch {
    Write-Host "❌ Base de données: Service non accessible" -ForegroundColor Red
}

# Afficher les URLs d'accès
Write-Host "🌐 URLs d'accès:" -ForegroundColor Cyan
Write-Host "   Frontend: http://localhost:4200" -ForegroundColor White
Write-Host "   Backend API: http://localhost:8080" -ForegroundColor White
Write-Host "   Prometheus: http://localhost:9090" -ForegroundColor White
Write-Host "   Grafana: http://localhost:3000 (admin/admin123)" -ForegroundColor White

Write-Host "🎉 Déploiement terminé!" -ForegroundColor Green
Write-Host "Utilisez 'podman-compose logs -f' pour voir les logs en temps réel" -ForegroundColor Yellow
