# Script de déploiement automatique complet E-COMPTA-IA
# Auteur: Assistant IA
# Date: $(Get-Date)

Write-Host "🚀 DÉPLOIEMENT AUTOMATIQUE E-COMPTA-IA" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

# Fonction pour vérifier si un port est libre
function Test-Port {
    param([int]$Port)
    try {
        $connection = New-Object System.Net.Sockets.TcpClient
        $connection.Connect("localhost", $Port)
        $connection.Close()
        return $false
    }
    catch {
        return $true
    }
}

# Fonction pour attendre qu'un service soit prêt
function Wait-ForService {
    param([string]$ServiceName, [int]$Port, [int]$TimeoutSeconds = 60)
    
    Write-Host "⏳ Attente du service $ServiceName sur le port $Port..." -ForegroundColor Yellow
    
    $elapsed = 0
    while ($elapsed -lt $TimeoutSeconds) {
        if (Test-Port -Port $Port) {
            Start-Sleep -Seconds 2
            $elapsed += 2
        } else {
            Write-Host "✅ $ServiceName est prêt !" -ForegroundColor Green
            return $true
        }
    }
    
    Write-Host "❌ Timeout: $ServiceName n'est pas prêt après $TimeoutSeconds secondes" -ForegroundColor Red
    return $false
}

# Étape 1: Nettoyage
Write-Host "🧹 NETTOYAGE DES CONTAINERS EXISTANTS..." -ForegroundColor Cyan
podman-compose down
podman-compose -f monitoring/podman-compose.yml down
podman system prune -f
podman volume prune -f

# Étape 2: Vérification des ports
Write-Host "🔍 VÉRIFICATION DES PORTS..." -ForegroundColor Cyan
$ports = @(5432, 8080, 4200, 3000, 9090, 8081, 9100, 9187, 9121, 9093)

foreach ($port in $ports) {
    if (-not (Test-Port -Port $port)) {
        Write-Host "⚠️  Port $port est déjà utilisé" -ForegroundColor Yellow
    }
}

# Étape 3: Déploiement de la base de données
Write-Host "🗄️  DÉPLOIEMENT DE POSTGRESQL..." -ForegroundColor Cyan
podman-compose up -d postgres

if (-not (Wait-ForService -ServiceName "PostgreSQL" -Port 5432)) {
    Write-Host "❌ Échec du déploiement de PostgreSQL" -ForegroundColor Red
    exit 1
}

# Étape 4: Déploiement du backend
Write-Host "🔧 DÉPLOIEMENT DU BACKEND SPRING BOOT..." -ForegroundColor Cyan
podman-compose up -d --build backend

if (-not (Wait-ForService -ServiceName "Backend" -Port 8080)) {
    Write-Host "❌ Échec du déploiement du backend" -ForegroundColor Red
    exit 1
}

# Étape 5: Test de l'API backend
Write-Host "🧪 TEST DE L'API BACKEND..." -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/hr/management/test/base" -Method GET -TimeoutSec 10
    Write-Host "✅ API Backend fonctionne: $($response)" -ForegroundColor Green
} catch {
    Write-Host "⚠️  API Backend non accessible: $($_.Exception.Message)" -ForegroundColor Yellow
}

# Étape 6: Déploiement du frontend
Write-Host "🎨 DÉPLOIEMENT DU FRONTEND ANGULAR..." -ForegroundColor Cyan
podman-compose up -d --build frontend

if (-not (Wait-ForService -ServiceName "Frontend" -Port 4200)) {
    Write-Host "❌ Échec du déploiement du frontend" -ForegroundColor Red
    exit 1
}

# Étape 7: Déploiement de nginx
Write-Host "🌐 DÉPLOIEMENT DE NGINX..." -ForegroundColor Cyan
podman-compose up -d nginx

# Étape 8: Déploiement du monitoring
Write-Host "📊 DÉPLOIEMENT DU MONITORING..." -ForegroundColor Cyan
podman-compose -f monitoring/podman-compose.yml up -d

# Étape 9: Vérification finale
Write-Host "🔍 VÉRIFICATION FINALE..." -ForegroundColor Cyan
Start-Sleep -Seconds 10

Write-Host "📋 ÉTAT DES CONTAINERS:" -ForegroundColor Green
podman ps

Write-Host "🌐 URLS D'ACCÈS:" -ForegroundColor Green
Write-Host "  • Frontend: http://localhost:4200" -ForegroundColor White
Write-Host "  • Backend API: http://localhost:8080/api" -ForegroundColor White
Write-Host "  • Nginx Proxy: http://localhost:8080" -ForegroundColor White
Write-Host "  • Grafana: http://localhost:3000" -ForegroundColor White
Write-Host "  • Prometheus: http://localhost:9090" -ForegroundColor White
Write-Host "  • cAdvisor: http://localhost:8081" -ForegroundColor White

Write-Host "✅ DÉPLOIEMENT TERMINÉ !" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green