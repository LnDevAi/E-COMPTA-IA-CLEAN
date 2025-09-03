# Script de déploiement E-COMPTA-IA INTERNATIONAL pour Windows
# Utilise Docker Desktop ou Podman

Write-Host "🚀 Déploiement E-COMPTA-IA INTERNATIONAL" -ForegroundColor Green
Write-Host "===============================================" -ForegroundColor Green

# Vérifier si Docker est installé
$dockerVersion = docker --version 2>$null
if ($dockerVersion) {
    Write-Host "✅ Docker détecté: $dockerVersion" -ForegroundColor Green
    $dockerCmd = "docker"
} else {
    # Vérifier si Podman est installé
    $podmanVersion = podman --version 2>$null
    if ($podmanVersion) {
        Write-Host "✅ Podman détecté: $podmanVersion" -ForegroundColor Green
        $dockerCmd = "podman"
    } else {
        Write-Host "❌ Aucun runtime de conteneur détecté!" -ForegroundColor Red
        Write-Host "Veuillez installer Docker Desktop ou Podman" -ForegroundColor Yellow
        exit 1
    }
}

# Arrêter et supprimer les conteneurs existants
Write-Host "🛑 Arrêt des conteneurs existants..." -ForegroundColor Yellow
& $dockerCmd compose -f docker/docker-compose.yml down --remove-orphans

# Nettoyer les images obsolètes
Write-Host "🧹 Nettoyage des images obsolètes..." -ForegroundColor Yellow
& $dockerCmd image prune -f

# Construire et démarrer les services
Write-Host "🔨 Construction des images..." -ForegroundColor Yellow
& $dockerCmd compose -f docker/docker-compose.yml build --no-cache

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Erreur lors de la construction des images" -ForegroundColor Red
    exit 1
}

# Démarrer les services
Write-Host "🚀 Démarrage des services..." -ForegroundColor Yellow
& $dockerCmd compose -f docker/docker-compose.yml up -d

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Erreur lors du démarrage des services" -ForegroundColor Red
    exit 1
}

# Attendre que les services soient prêts
Write-Host "⏳ Attente du démarrage des services..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# Vérifier le statut des services
Write-Host "📊 Statut des services:" -ForegroundColor Yellow
& $dockerCmd compose -f docker/docker-compose.yml ps

# Afficher les logs du backend
Write-Host "📝 Logs du backend (dernières 20 lignes):" -ForegroundColor Yellow
& $dockerCmd compose -f docker/docker-compose.yml logs --tail=20 backend

# Afficher les informations de connexion
Write-Host ""
Write-Host "🌐 Informations de connexion:" -ForegroundColor Green
Write-Host "   Frontend: http://localhost:80" -ForegroundColor Cyan
Write-Host "   Backend API: http://localhost:8080/api" -ForegroundColor Cyan
Write-Host "   H2 Console: http://localhost:8080/h2-console" -ForegroundColor Cyan
Write-Host "   PostgreSQL: localhost:5432" -ForegroundColor Cyan
Write-Host ""
Write-Host "🔑 Comptes par défaut:" -ForegroundColor Green
Write-Host "   Admin: admin@ecomptaia.com / admin123" -ForegroundColor Cyan
Write-Host ""
Write-Host "✅ Déploiement terminé avec succès!" -ForegroundColor Green
Write-Host "Utilisez 'docker compose -f docker/docker-compose.yml logs -f' pour suivre les logs" -ForegroundColor Yellow
