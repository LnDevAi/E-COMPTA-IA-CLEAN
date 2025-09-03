# ========================================
# E-COMPTA-IA - Script de déploiement Production
# ========================================
# Script PowerShell pour déploiement avec Podman Compose
# Usage: .\deploy-podman-production.ps1

param(
    [string]$Environment = "production",
    [switch]$SkipBackup,
    [switch]$SkipSSL,
    [switch]$Force
)

# Configuration
$BackupDir = ".\backup"
$LogFile = ".\deploy.log"

# Couleurs pour les messages
$Red = "Red"
$Green = "Green"
$Yellow = "Yellow"
$Blue = "Blue"

# Fonction pour afficher les messages
function Write-Info {
    param([string]$Message)
    Write-Host "[INFO] $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss') - $Message" -ForegroundColor $Blue
    Add-Content -Path $LogFile -Value "[INFO] $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss') - $Message"
}

function Write-Success {
    param([string]$Message)
    Write-Host "[SUCCESS] $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss') - $Message" -ForegroundColor $Green
    Add-Content -Path $LogFile -Value "[SUCCESS] $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss') - $Message"
}

function Write-Warning {
    param([string]$Message)
    Write-Host "[WARNING] $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss') - $Message" -ForegroundColor $Yellow
    Add-Content -Path $LogFile -Value "[WARNING] $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss') - $Message"
}

function Write-Error {
    param([string]$Message)
    Write-Host "[ERROR] $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss') - $Message" -ForegroundColor $Red
    Add-Content -Path $LogFile -Value "[ERROR] $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss') - $Message"
}

# Vérifier les prérequis
function Test-Prerequisites {
    Write-Info "Vérification des prérequis..."
    
    # Vérifier Podman
    try {
        $podmanVersion = podman --version
        Write-Success "Podman trouvé: $podmanVersion"
    }
    catch {
        Write-Error "Podman n'est pas installé ou n'est pas dans le PATH"
        exit 1
    }
    
    # Vérifier Podman Compose
    try {
        $composeVersion = podman-compose --version
        Write-Success "Podman Compose trouvé: $composeVersion"
    }
    catch {
        Write-Error "Podman Compose n'est pas installé ou n'est pas dans le PATH"
        exit 1
    }
    
    # Vérifier le fichier .env
    if (-not (Test-Path ".env")) {
        Write-Warning "Fichier .env non trouvé. Copie depuis environment.example..."
        if (Test-Path "environment.example") {
            Copy-Item "environment.example" ".env"
            Write-Success "Fichier .env créé depuis environment.example"
        }
        else {
            Write-Error "Fichier environment.example non trouvé"
            exit 1
        }
    }
    
    # Vérifier les certificats SSL
    if (-not $SkipSSL) {
        if (-not (Test-Path "nginx\ssl\ecomptaia.crt") -or -not (Test-Path "nginx\ssl\ecomptaia.key")) {
            Write-Warning "Certificats SSL non trouvés. Génération des certificats auto-signés..."
            if (Test-Path "scripts\generate-ssl-certificates.sh") {
                bash scripts\generate-ssl-certificates.sh dev
                Write-Success "Certificats SSL générés"
            }
            else {
                Write-Error "Script de génération SSL non trouvé"
                exit 1
            }
        }
    }
}

# Créer une sauvegarde avant déploiement
function New-Backup {
    if ($SkipBackup) {
        Write-Info "Sauvegarde ignorée (paramètre -SkipBackup)"
        return
    }
    
    Write-Info "Création d'une sauvegarde avant déploiement..."
    
    $timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
    $backupFile = "$BackupDir\pre_deploy_backup_$timestamp.sql.gz"
    
    # Créer le répertoire de sauvegarde
    if (-not (Test-Path $BackupDir)) {
        New-Item -ItemType Directory -Path $BackupDir -Force | Out-Null
    }
    
    # Sauvegarder la base de données si elle existe
    try {
        $env:PGPASSWORD = "Ecomptaia2024!"
        pg_dump -h localhost -p 5432 -U ecomptaia_user -d ecomptaia_prod | gzip > $backupFile
        Write-Success "Sauvegarde créée: $backupFile"
    }
    catch {
        Write-Warning "Impossible de créer la sauvegarde (base de données peut-être non démarrée)"
    }
}

# Arrêter les services existants
function Stop-Services {
    Write-Info "Arrêt des services existants..."
    
    try {
        podman-compose -f docker-compose.prod.yml down
        Write-Success "Services arrêtés"
    }
    catch {
        Write-Warning "Aucun service en cours d'exécution ou erreur lors de l'arrêt"
    }
}

# Nettoyer les ressources
function Clear-Resources {
    Write-Info "Nettoyage des ressources..."
    
    # Supprimer les conteneurs arrêtés
    try {
        podman container prune -f
        Write-Success "Conteneurs arrêtés supprimés"
    }
    catch {
        Write-Warning "Erreur lors du nettoyage des conteneurs"
    }
    
    # Supprimer les images non utilisées
    try {
        podman image prune -f
        Write-Success "Images non utilisées supprimées"
    }
    catch {
        Write-Warning "Erreur lors du nettoyage des images"
    }
}

# Construire les images
function New-Images {
    Write-Info "Construction des images Docker..."
    
    try {
        # Construire l'image backend
        Write-Info "Construction de l'image backend..."
        podman build -t ecomptaia-backend:latest ./backend
        
        # Construire l'image frontend
        Write-Info "Construction de l'image frontend..."
        podman build -t ecomptaia-frontend:latest ./frontend
        
        Write-Success "Images construites avec succès"
    }
    catch {
        Write-Error "Erreur lors de la construction des images"
        exit 1
    }
}

# Démarrer les services
function Start-Services {
    Write-Info "Démarrage des services..."
    
    try {
        # Démarrer les services avec Podman Compose
        podman-compose -f docker-compose.prod.yml up -d
        
        Write-Success "Services démarrés"
    }
    catch {
        Write-Error "Erreur lors du démarrage des services"
        exit 1
    }
}

# Vérifier la santé des services
function Test-ServicesHealth {
    Write-Info "Vérification de la santé des services..."
    
    $services = @(
        @{Name="PostgreSQL"; Port=5432; URL="localhost:5432"},
        @{Name="Backend"; Port=8080; URL="http://localhost:8080/api/health"},
        @{Name="Frontend"; Port=80; URL="http://localhost:80"},
        @{Name="Redis"; Port=6379; URL="localhost:6379"}
    )
    
    foreach ($service in $services) {
        Write-Info "Vérification de $($service.Name)..."
        
        try {
            if ($service.Name -eq "Backend") {
                $response = Invoke-WebRequest -Uri $service.URL -TimeoutSec 10
                if ($response.StatusCode -eq 200) {
                    Write-Success "$($service.Name) est opérationnel"
                }
                else {
                    Write-Warning "$($service.Name) répond avec le code $($response.StatusCode)"
                }
            }
            else {
                $tcp = New-Object System.Net.Sockets.TcpClient
                $tcp.ConnectAsync("localhost", $service.Port).Wait(5000) | Out-Null
                if ($tcp.Connected) {
                    Write-Success "$($service.Name) est opérationnel"
                    $tcp.Close()
                }
                else {
                    Write-Warning "$($service.Name) ne répond pas"
                }
            }
        }
        catch {
            Write-Warning "$($service.Name) n'est pas encore prêt"
        }
    }
}

# Configurer les certificats SSL
function Set-SSLCertificates {
    if ($SkipSSL) {
        Write-Info "Configuration SSL ignorée (paramètre -SkipSSL)"
        return
    }
    
    Write-Info "Configuration des certificats SSL..."
    
    # Vérifier que les certificats existent
    if (-not (Test-Path "nginx\ssl\ecomptaia.crt") -or -not (Test-Path "nginx\ssl\ecomptaia.key")) {
        Write-Error "Certificats SSL manquants"
        exit 1
    }
    
    # Redémarrer Nginx pour appliquer les certificats
    try {
        podman restart ecomptaia_nginx_prod
        Write-Success "Certificats SSL appliqués"
    }
    catch {
        Write-Warning "Erreur lors de l'application des certificats SSL"
    }
}

# Afficher les informations de déploiement
function Show-DeploymentInfo {
    Write-Host ""
    Write-Host "==========================================" -ForegroundColor $Green
    Write-Host "DÉPLOIEMENT E-COMPTA-IA TERMINÉ" -ForegroundColor $Green
    Write-Host "==========================================" -ForegroundColor $Green
    Write-Host ""
    Write-Host "URLs d'accès:" -ForegroundColor $Blue
    Write-Host "  Frontend: http://localhost:80" -ForegroundColor $Yellow
    Write-Host "  Backend API: http://localhost:8080/api" -ForegroundColor $Yellow
    Write-Host "  Grafana: http://localhost:3000" -ForegroundColor $Yellow
    Write-Host "  Prometheus: http://localhost:9090" -ForegroundColor $Yellow
    Write-Host "  Kibana: http://localhost:5601" -ForegroundColor $Yellow
    Write-Host ""
    Write-Host "Commandes utiles:" -ForegroundColor $Blue
    Write-Host "  Voir les logs: podman-compose -f docker-compose.prod.yml logs -f" -ForegroundColor $Yellow
    Write-Host "  Arrêter: podman-compose -f docker-compose.prod.yml down" -ForegroundColor $Yellow
    Write-Host "  Redémarrer: podman-compose -f docker-compose.prod.yml restart" -ForegroundColor $Yellow
    Write-Host ""
    Write-Host "Sauvegarde:" -ForegroundColor $Blue
    Write-Host "  Créer: .\scripts\backup.sh" -ForegroundColor $Yellow
    Write-Host "  Restaurer: .\scripts\restore.sh --latest" -ForegroundColor $Yellow
    Write-Host ""
    Write-Host "Logs de déploiement: $LogFile" -ForegroundColor $Blue
    Write-Host ""
}

# Fonction principale
function Main {
    Write-Host "==========================================" -ForegroundColor $Green
    Write-Host "DÉPLOIEMENT E-COMPTA-IA PRODUCTION" -ForegroundColor $Green
    Write-Host "==========================================" -ForegroundColor $Green
    Write-Host ""
    
    # Créer le fichier de log
    New-Item -ItemType File -Path $LogFile -Force | Out-Null
    
    Write-Info "Début du déploiement en mode $Environment"
    
    # Vérifier les prérequis
    Test-Prerequisites
    
    # Créer une sauvegarde
    New-Backup
    
    # Arrêter les services existants
    Stop-Services
    
    # Nettoyer les ressources
    Clear-Resources
    
    # Construire les images
    New-Images
    
    # Démarrer les services
    Start-Services
    
    # Attendre que les services démarrent
    Write-Info "Attente du démarrage des services..."
    Start-Sleep -Seconds 30
    
    # Vérifier la santé des services
    Test-ServicesHealth
    
    # Configurer les certificats SSL
    Set-SSLCertificates
    
    # Afficher les informations
    Show-DeploymentInfo
    
    Write-Success "Déploiement terminé avec succès!"
}

# Exécuter la fonction principale
Main
