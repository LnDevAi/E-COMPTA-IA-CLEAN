# Script de déploiement final E-COMPTA-IA
# Version: FINALE - Plus de tâtonnement !

Write-Host "🚀 DÉPLOIEMENT FINAL E-COMPTA-IA" -ForegroundColor Green
Write-Host "=================================" -ForegroundColor Green

# Fonction pour attendre qu'un service soit prêt
function Wait-ForService {
    param([string]$ServiceName, [int]$Port, [int]$TimeoutSeconds = 120)
    
    Write-Host "⏳ Attente du service $ServiceName sur le port $Port..." -ForegroundColor Yellow
    
    $elapsed = 0
    while ($elapsed -lt $TimeoutSeconds) {
        try {
            $response = Invoke-WebRequest -Uri "http://localhost:$Port" -TimeoutSec 5 -ErrorAction SilentlyContinue
            if ($response.StatusCode -eq 200) {
                Write-Host "✅ $ServiceName est prêt !" -ForegroundColor Green
                return $true
            }
        } catch {
            # Service pas encore prêt
        }
        
        Start-Sleep -Seconds 5
        $elapsed += 5
        Write-Host "   Attente... ($elapsed/$TimeoutSeconds secondes)" -ForegroundColor Gray
    }
    
    Write-Host "❌ Timeout: $ServiceName n'est pas prêt après $TimeoutSeconds secondes" -ForegroundColor Red
    return $false
}

# Étape 1: Nettoyage complet
Write-Host "🧹 NETTOYAGE COMPLET..." -ForegroundColor Cyan
podman-compose down
podman-compose -f monitoring/podman-compose.yml down
podman system prune -f
podman volume prune -f

# Étape 2: Déploiement avec le fichier final
Write-Host "🚀 DÉPLOIEMENT AVEC CONFIGURATION FINALE..." -ForegroundColor Cyan
podman-compose -f docker-compose-final.yml up -d --build

# Étape 3: Attendre que PostgreSQL soit prêt
Write-Host "🗄️  ATTENTE DE POSTGRESQL..." -ForegroundColor Cyan
Start-Sleep -Seconds 15

# Étape 4: Attendre que le backend soit prêt
Write-Host "🔧 ATTENTE DU BACKEND..." -ForegroundColor Cyan
if (-not (Wait-ForService -ServiceName "Backend" -Port 8080)) {
    Write-Host "❌ Échec du déploiement du backend" -ForegroundColor Red
    Write-Host "📋 Logs du backend:" -ForegroundColor Yellow
    podman logs ecomptaia-backend
    exit 1
}

# Étape 5: Attendre que le frontend soit prêt
Write-Host "🎨 ATTENTE DU FRONTEND..." -ForegroundColor Cyan
if (-not (Wait-ForService -ServiceName "Frontend" -Port 4200)) {
    Write-Host "❌ Échec du déploiement du frontend" -ForegroundColor Red
    Write-Host "📋 Logs du frontend:" -ForegroundColor Yellow
    podman logs ecomptaia-frontend
    exit 1
}

# Étape 6: Test de l'API
Write-Host "🧪 TEST DE L'API BACKEND..." -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/hr/management/test/base" -Method GET -TimeoutSec 10
    Write-Host "✅ API Backend fonctionne: $($response)" -ForegroundColor Green
} catch {
    Write-Host "⚠️  API Backend non accessible: $($_.Exception.Message)" -ForegroundColor Yellow
}

# Étape 7: Vérification finale
Write-Host "🔍 VÉRIFICATION FINALE..." -ForegroundColor Cyan
Start-Sleep -Seconds 10

Write-Host "📋 ÉTAT DES CONTAINERS:" -ForegroundColor Green
podman ps

Write-Host "🌐 URLS D'ACCÈS:" -ForegroundColor Green
Write-Host "  • Frontend: http://localhost:4200" -ForegroundColor White
Write-Host "  • Backend API: http://localhost:8080/api" -ForegroundColor White
Write-Host "  • Grafana: http://localhost:3000" -ForegroundColor White
Write-Host "  • Prometheus: http://localhost:9090" -ForegroundColor White
Write-Host "  • cAdvisor: http://localhost:8081" -ForegroundColor White

Write-Host "✅ DÉPLOIEMENT FINAL TERMINÉ !" -ForegroundColor Green
Write-Host "=================================" -ForegroundColor Green
