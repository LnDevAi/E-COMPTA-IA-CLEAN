# Script de déploiement automatique du monitoring E-COMPTA-IA
# Compatible Podman Desktop - Version PowerShell

param(
    [string]$ComposeFile = "podman-compose.yml",
    [switch]$Force
)

# Configuration
$NetworkName = "ecomptaia-monitoring"

Write-Host "🚀 DÉPLOIEMENT MONITORING E-COMPTA-IA" -ForegroundColor Blue
Write-Host "==================================" -ForegroundColor Blue

# Vérification des prérequis
Write-Host "📋 Vérification des prérequis..." -ForegroundColor Yellow

if (-not (Get-Command podman -ErrorAction SilentlyContinue)) {
    Write-Host "❌ Podman n'est pas installé" -ForegroundColor Red
    exit 1
}

if (-not (Get-Command podman-compose -ErrorAction SilentlyContinue)) {
    Write-Host "❌ podman-compose n'est pas installé" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Podman et podman-compose disponibles" -ForegroundColor Green

# Création des volumes Podman
Write-Host "📦 Création des volumes..." -ForegroundColor Yellow

try {
    podman volume create prometheus_data 2>$null
    Write-Host "✅ Volume prometheus_data créé" -ForegroundColor Green
} catch {
    Write-Host "ℹ️ Volume prometheus_data existe déjà" -ForegroundColor Cyan
}

try {
    podman volume create grafana_data 2>$null
    Write-Host "✅ Volume grafana_data créé" -ForegroundColor Green
} catch {
    Write-Host "ℹ️ Volume grafana_data existe déjà" -ForegroundColor Cyan
}

try {
    podman volume create alertmanager_data 2>$null
    Write-Host "✅ Volume alertmanager_data créé" -ForegroundColor Green
} catch {
    Write-Host "ℹ️ Volume alertmanager_data existe déjà" -ForegroundColor Cyan
}

# Création du réseau
Write-Host "�� Création du réseau..." -ForegroundColor Yellow

if (-not (podman network exists $NetworkName)) {
    podman network create $NetworkName
    Write-Host "✅ Réseau $NetworkName créé" -ForegroundColor Green
} else {
    Write-Host "✅ Réseau $NetworkName existe déjà" -ForegroundColor Green
}

# Vérification des ports disponibles
Write-Host "🔍 Vérification des ports..." -ForegroundColor Yellow

$Ports = @(9090, 3000, 9100, 9187, 9121, 9093, 8080)
foreach ($port in $Ports) {
    try {
        $connection = Test-NetConnection -ComputerName localhost -Port $port -WarningAction SilentlyContinue
        if ($connection.TcpTestSucceeded) {
            Write-Host "⚠️ Port $port est déjà utilisé" -ForegroundColor Yellow
        } else {
            Write-Host "✅ Port $port disponible" -ForegroundColor Green
        }
    } catch {
        Write-Host "✅ Port $port disponible" -ForegroundColor Green
    }
}

# Déploiement des services
Write-Host "🚀 Déploiement des services..." -ForegroundColor Yellow

try {
    podman-compose -f $ComposeFile down 2>$null
} catch {
    # Ignore les erreurs si les services ne sont pas démarrés
}

podman-compose -f $ComposeFile up -d

Write-Host "✅ Services déployés" -ForegroundColor Green

# Attente du démarrage
Write-Host "⏳ Attente du démarrage des services..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# Vérification de la santé des services
Write-Host "�� Vérification de la santé des services..." -ForegroundColor Yellow

$Services = @(
    @{Name="prometheus"; Port=9090},
    @{Name="grafana"; Port=3000},
    @{Name="node-exporter"; Port=9100},
    @{Name="postgres-exporter"; Port=9187},
    @{Name="redis-exporter"; Port=9121},
    @{Name="alertmanager"; Port=9093},
    @{Name="cadvisor"; Port=8080}
)

foreach ($service in $Services) {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:$($service.Port)" -TimeoutSec 5 -ErrorAction Stop
        if ($response.StatusCode -eq 200) {
            Write-Host "✅ $($service.Name) est opérationnel (port $($service.Port))" -ForegroundColor Green
        } else {
            Write-Host "⚠️ $($service.Name) répond avec le code $($response.StatusCode) (port $($service.Port))" -ForegroundColor Yellow
        }
    } catch {
        Write-Host "❌ $($service.Name) n'est pas accessible (port $($service.Port))" -ForegroundColor Red
    }
}

# Configuration initiale Grafana
Write-Host "🎨 Configuration initiale Grafana..." -ForegroundColor Yellow

# Attendre que Grafana soit prêt
Write-Host "Attente que Grafana soit prêt..."
for ($i = 1; $i -le 60; $i++) {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:3000/api/health" -TimeoutSec 5 -ErrorAction Stop
        if ($response.StatusCode -eq 200) {
            Write-Host "✅ Grafana est prêt" -ForegroundColor Green
            break
        }
    } catch {
        # Continue d'attendre
    }
    Write-Host "Tentative $i/60..."
    Start-Sleep -Seconds 5
}

# Affichage des URLs d'accès
Write-Host "🌐 URLs d'accès au monitoring:" -ForegroundColor Blue
Write-Host "==================================" -ForegroundColor Blue
Write-Host "📊 Prometheus: http://localhost:9090" -ForegroundColor Green
Write-Host "🎨 Grafana: http://localhost:3000 (admin/admin123)" -ForegroundColor Green
Write-Host "📈 Node Exporter: http://localhost:9100" -ForegroundColor Green
Write-Host "��️ Postgres Exporter: http://localhost:9187" -ForegroundColor Green
Write-Host "🔴 Redis Exporter: http://localhost:9121" -ForegroundColor Green
Write-Host "�� Alertmanager: http://localhost:9093" -ForegroundColor Green
Write-Host "📊 cAdvisor: http://localhost:8080" -ForegroundColor Green

Write-Host ""
Write-Host "�� Commandes utiles:" -ForegroundColor Blue
Write-Host "==================================" -ForegroundColor Blue
Write-Host "Voir les logs: podman-compose -f $ComposeFile logs -f" -ForegroundColor Yellow
Write-Host "Arrêter: podman-compose -f $ComposeFile down" -ForegroundColor Yellow
Write-Host "Redémarrer: podman-compose -f $ComposeFile restart" -ForegroundColor Yellow
Write-Host "Status: podman-compose -f $ComposeFile ps" -ForegroundColor Yellow

Write-Host ""
Write-Host "🎉 Déploiement du monitoring terminé avec succès !" -ForegroundColor Green
Write-Host "⚠️ N
'oubliez pas de configurer les variables d'environnement" -ForegroundColor Yellow
# Script terminé