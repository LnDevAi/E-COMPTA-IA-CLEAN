# Script de déploiement complet E-COMPTA-IA avec Podman Desktop
# PowerShell Script pour Windows - Version Production
# =====================================================

param(
    [string]$Environment = "production",
    [switch]$SkipBuild = $false,
    [switch]$SkipTests = $false,
    [switch]$Force = $false,
    [switch]$Help = $false
)

if ($Help) {
    Write-Host "Usage: .\deploy-podman-complete.ps1 [OPTIONS]" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Options:" -ForegroundColor Yellow
    Write-Host "  -Environment <env>    Environnement (production|staging|development) [default: production]"
    Write-Host "  -SkipBuild           Ignorer la construction des images"
    Write-Host "  -SkipTests           Ignorer les tests de sante"
    Write-Host "  -Force               Forcer le redemarrage meme si les services sont en cours"
    Write-Host "  -Help                Afficher cette aide"
    Write-Host ""
    Write-Host "Exemples:" -ForegroundColor Green
    Write-Host "  .\deploy-podman-complete.ps1"
    Write-Host "  .\deploy-podman-complete.ps1 -Environment staging"
    Write-Host "  .\deploy-podman-complete.ps1 -SkipBuild -Force"
    exit 0
}

# Configuration des couleurs et styles
$ErrorActionPreference = "Stop"
$ProgressPreference = "SilentlyContinue"

function Write-Header {
    param([string]$Message)
    Write-Host ""
    Write-Host "===============================================" -ForegroundColor Cyan
    Write-Host "  $Message" -ForegroundColor White
    Write-Host "===============================================" -ForegroundColor Cyan
}

function Write-Success {
    param([string]$Message)
    Write-Host "✓ $Message" -ForegroundColor Green
}

function Write-Warning {
    param([string]$Message)
    Write-Host "⚠ $Message" -ForegroundColor Yellow
}

function Write-Error {
    param([string]$Message)
    Write-Host "✗ $Message" -ForegroundColor Red
}

function Write-Info {
    param([string]$Message)
    Write-Host "ℹ $Message" -ForegroundColor Blue
}

# Début du script
Write-Header "DEPLOIEMENT E-COMPTA-IA INTERNATIONAL"
Write-Info "Environnement: $Environment"
Write-Info "Date: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')"

# 1. Vérifications préliminaires
Write-Header "VERIFICATIONS PRELIMINAIRES"

# Vérifier Podman
try {
    $podmanVersion = podman --version 2>$null
    if ($podmanVersion) {
        Write-Success "Podman detecte: $podmanVersion"
    } else {
        throw "Podman non trouve"
    }
} catch {
    Write-Error "Podman n'est pas installe ou pas dans le PATH"
    Write-Info "Veuillez installer Podman Desktop depuis: https://podman-desktop.io/"
    exit 1
}

# Vérifier Podman Desktop
try {
    podman info | Out-Null
    Write-Success "Podman Desktop est demarre"
} catch {
    Write-Error "Podman Desktop n'est pas demarre"
    Write-Info "Veuillez demarrer Podman Desktop et reessayer"
    exit 1
}

# Vérifier les fichiers de configuration
$requiredFiles = @(
    "docker-compose-podman.yml",
    "env.podman",
    "backend/Dockerfile",
    "frontend/Dockerfile"
)

foreach ($file in $requiredFiles) {
    if (Test-Path $file) {
        Write-Success "Fichier trouve: $file"
    } else {
        Write-Error "Fichier manquant: $file"
        exit 1
    }
}

# 2. Préparation de l'environnement
Write-Header "PREPARATION DE L'ENVIRONNEMENT"

# Charger les variables d'environnement
if (Test-Path "env.podman") {
    Get-Content "env.podman" | ForEach-Object {
        if ($_ -match "^([^#][^=]+)=(.*)$") {
            [Environment]::SetEnvironmentVariable($matches[1], $matches[2], "Process")
        }
    }
    Write-Success "Variables d'environnement chargees"
}

# Créer les répertoires de données
$dataDirs = @("data/postgres", "data/redis", "data/logs", "backup")
foreach ($dir in $dataDirs) {
    if (-not (Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
        Write-Success "Repertoire cree: $dir"
    }
}

# 3. Nettoyage des ressources existantes
Write-Header "NETTOYAGE DES RESSOURCES EXISTANTES"

if ($Force) {
    Write-Info "Arret force des services existants..."
    podman-compose -f docker-compose-podman.yml down --remove-orphans --volumes 2>$null
    Write-Success "Services arretes"
    
    Write-Info "Suppression des images existantes..."
    $images = podman images -q ecomptaia-* 2>$null
    if ($images) {
        podman rmi $images --force 2>$null
        Write-Success "Images supprimees"
    }
} else {
    Write-Info "Arret des services existants..."
    podman-compose -f docker-compose-podman.yml down --remove-orphans 2>$null
    Write-Success "Services arretes"
}

# 4. Construction des images
if (-not $SkipBuild) {
    Write-Header "CONSTRUCTION DES IMAGES"
    
    Write-Info "Construction des images Docker..."
    podman-compose -f docker-compose-podman.yml build --no-cache
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Images construites avec succes"
    } else {
        Write-Error "Echec de la construction des images"
        exit 1
    }
}

# 5. Démarrage des services
Write-Header "DEMARRAGE DES SERVICES"

Write-Info "Demarrage des services..."
podman-compose -f docker-compose-podman.yml up -d
if ($LASTEXITCODE -eq 0) {
    Write-Success "Services demarres"
} else {
    Write-Error "Echec du demarrage des services"
    exit 1
}

# 6. Attente du démarrage
Write-Header "ATTENTE DU DEMARRAGE"

Write-Info "Attente du demarrage des services (60 secondes)..."
Start-Sleep -Seconds 60

# 7. Vérification de la santé des services
if (-not $SkipTests) {
    Write-Header "VERIFICATION DE LA SANTE DES SERVICES"
    
    # Vérifier PostgreSQL
    try {
        $dbHealth = podman exec ecomptaia-postgres pg_isready -U ecomptaia_user -d ecomptaia_db 2>$null
        if ($dbHealth -match "accepting connections") {
            Write-Success "PostgreSQL: Service accessible"
        } else {
            throw "PostgreSQL non accessible"
        }
    } catch {
        Write-Error "PostgreSQL: Service non accessible"
    }
    
    # Vérifier Redis
    try {
        $redisHealth = podman exec ecomptaia-redis redis-cli ping 2>$null
        if ($redisHealth -eq "PONG") {
            Write-Success "Redis: Service accessible"
        } else {
            throw "Redis non accessible"
        }
    } catch {
        Write-Error "Redis: Service non accessible"
    }
    
    # Vérifier Backend
    try {
        $backendHealth = Invoke-RestMethod -Uri "http://localhost:8080/api/actuator/health" -Method Get -TimeoutSec 10 2>$null
        if ($backendHealth.status -eq "UP") {
            Write-Success "Backend: Service accessible (Status: $($backendHealth.status))"
        } else {
            throw "Backend non accessible"
        }
    } catch {
        Write-Error "Backend: Service non accessible"
    }
    
    # Vérifier Frontend
    try {
        $frontendResponse = Invoke-WebRequest -Uri "http://localhost:4200" -Method Get -TimeoutSec 10 2>$null
        if ($frontendResponse.StatusCode -eq 200) {
            Write-Success "Frontend: Service accessible (HTTP $($frontendResponse.StatusCode))"
        } else {
            throw "Frontend non accessible"
        }
    } catch {
        Write-Error "Frontend: Service non accessible"
    }
}

# 8. Affichage du statut final
Write-Header "STATUT FINAL DES SERVICES"

Write-Info "Statut des conteneurs:"
podman-compose -f docker-compose-podman.yml ps

Write-Header "URLS D'ACCES"
Write-Host "🌐 Frontend:     http://localhost:4200" -ForegroundColor White
Write-Host "🔧 Backend API:  http://localhost:8080/api" -ForegroundColor White
Write-Host "📊 Health Check: http://localhost:8080/api/actuator/health" -ForegroundColor White
Write-Host "📈 Metrics:      http://localhost:8080/api/actuator/prometheus" -ForegroundColor White
Write-Host "🗄️  PostgreSQL:   localhost:5432" -ForegroundColor White
Write-Host "⚡ Redis:        localhost:6379" -ForegroundColor White

Write-Header "COMMANDES UTILES"
Write-Host "📋 Voir les logs:     podman-compose -f docker-compose-podman.yml logs -f" -ForegroundColor Yellow
Write-Host "🔄 Redemarrer:        podman-compose -f docker-compose-podman.yml restart" -ForegroundColor Yellow
Write-Host "⏹️  Arreter:           podman-compose -f docker-compose-podman.yml down" -ForegroundColor Yellow
Write-Host "🧹 Nettoyer:          podman-compose -f docker-compose-podman.yml down --volumes --rmi all" -ForegroundColor Yellow

Write-Header 'DEPLOIEMENT TERMINE'
Write-Success 'E-COMPTA-IA INTERNATIONAL deployment completed successfully!'
Write-Info 'Consultez les logs pour plus d''informations: podman-compose -f docker-compose-podman.yml logs -f'
