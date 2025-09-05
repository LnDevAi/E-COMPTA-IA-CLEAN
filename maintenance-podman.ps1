# Script de maintenance E-COMPTA-IA avec Podman Desktop
# PowerShell Script pour Windows - Maintenance et Monitoring
# ========================================================

param(
    [Parameter(Mandatory=$true)]
    [ValidateSet("backup", "restore", "cleanup", "health", "logs", "update", "monitor")]
    [string]$Action,
    
    [string]$BackupFile = "",
    [int]$DaysToKeep = 30,
    [switch]$Force = $false,
    [switch]$Help = $false
)

if ($Help) {
    Write-Host "Usage: .\maintenance-podman.ps1 -Action <ACTION> [OPTIONS]" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Actions disponibles:" -ForegroundColor Yellow
    Write-Host "  backup     - Créer une sauvegarde de la base de données"
    Write-Host "  restore    - Restaurer une sauvegarde de la base de données"
    Write-Host "  cleanup    - Nettoyer les logs et données anciennes"
    Write-Host "  health     - Vérifier la santé de tous les services"
    Write-Host "  logs       - Afficher les logs des services"
    Write-Host "  update     - Mettre à jour les images et redémarrer"
    Write-Host "  monitor    - Afficher les métriques de performance"
    Write-Host ""
    Write-Host "Options:" -ForegroundColor Yellow
    Write-Host "  -BackupFile <file>    Fichier de sauvegarde pour restore"
    Write-Host "  -DaysToKeep <days>    Nombre de jours à conserver (cleanup) [default: 30]"
    Write-Host "  -Force               Forcer l'opération sans confirmation"
    Write-Host "  -Help                Afficher cette aide"
    Write-Host ""
    Write-Host "Exemples:" -ForegroundColor Green
    Write-Host "  .\maintenance-podman.ps1 -Action backup"
    Write-Host "  .\maintenance-podman.ps1 -Action restore -BackupFile backup_20241219.sql"
    Write-Host "  .\maintenance-podman.ps1 -Action cleanup -DaysToKeep 7"
    Write-Host "  .\maintenance-podman.ps1 -Action health"
    exit 0
}

# Configuration des couleurs
$ErrorActionPreference = "Stop"

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

# Vérifier que Podman est disponible
try {
    podman --version | Out-Null
} catch {
    Write-Error "Podman n'est pas installé ou pas dans le PATH"
    exit 1
}

# Créer le répertoire de sauvegarde s'il n'existe pas
if (-not (Test-Path "backup")) {
    New-Item -ItemType Directory -Path "backup" -Force | Out-Null
}

# Fonction de sauvegarde
function Backup-Database {
    Write-Header "SAUVEGARDE DE LA BASE DE DONNÉES"
    
    $timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
    $backupFile = "backup/ecomptaia_backup_$timestamp.sql"
    
    try {
        Write-Info "Création de la sauvegarde: $backupFile"
        
        # Vérifier que PostgreSQL est en cours d'exécution
        $postgresStatus = podman ps --filter "name=ecomptaia-postgres" --format "{{.Status}}"
        if (-not $postgresStatus -or $postgresStatus -notmatch "Up") {
            throw "PostgreSQL n'est pas en cours d'exécution"
        }
        
        # Créer la sauvegarde
        podman exec ecomptaia-postgres pg_dump -U ecomptaia_user -d ecomptaia_db > $backupFile
        
        if (Test-Path $backupFile -and (Get-Item $backupFile).Length -gt 0) {
            Write-Success "Sauvegarde créée avec succès: $backupFile"
            Write-Info "Taille: $((Get-Item $backupFile).Length / 1MB) MB"
        } else {
            throw "La sauvegarde est vide ou n'a pas été créée"
        }
    } catch {
        Write-Error "Échec de la sauvegarde: $_"
        exit 1
    }
}

# Fonction de restauration
function Restore-Database {
    Write-Header "RESTAURATION DE LA BASE DE DONNÉES"
    
    if (-not $BackupFile) {
        Write-Error "Le fichier de sauvegarde doit être spécifié avec -BackupFile"
        exit 1
    }
    
    if (-not (Test-Path $BackupFile)) {
        Write-Error "Le fichier de sauvegarde n'existe pas: $BackupFile"
        exit 1
    }
    
    if (-not $Force) {
        $confirmation = Read-Host "Êtes-vous sûr de vouloir restaurer la base de données ? (oui/non)"
        if ($confirmation -ne "oui") {
            Write-Info "Restauration annulée"
            exit 0
        }
    }
    
    try {
        Write-Info "Restauration depuis: $BackupFile"
        
        # Vérifier que PostgreSQL est en cours d'exécution
        $postgresStatus = podman ps --filter "name=ecomptaia-postgres" --format "{{.Status}}"
        if (-not $postgresStatus -or $postgresStatus -notmatch "Up") {
            throw "PostgreSQL n'est pas en cours d'exécution"
        }
        
        # Restaurer la sauvegarde
        Get-Content $BackupFile | podman exec -i ecomptaia-postgres psql -U ecomptaia_user -d ecomptaia_db
        
        Write-Success "Base de données restaurée avec succès"
    } catch {
        Write-Error "Échec de la restauration: $_"
        exit 1
    }
}

# Fonction de nettoyage
function Remove-OldData {
    Write-Header "NETTOYAGE DU SYSTÈME"
    
    try {
        # Nettoyer les logs anciens
        Write-Info "Nettoyage des logs anciens (plus de $DaysToKeep jours)..."
        
        # Nettoyer les logs de la base de données
        $cleanupQuery = @"
DELETE FROM audit_logs WHERE timestamp < CURRENT_TIMESTAMP - INTERVAL '$DaysToKeep days';
DELETE FROM system_metrics WHERE timestamp < CURRENT_TIMESTAMP - INTERVAL '$DaysToKeep days';
SELECT cleanup_old_audit_logs($DaysToKeep);
SELECT cleanup_old_metrics($DaysToKeep);
"@
        
        $cleanupQuery | podman exec -i ecomptaia-postgres psql -U ecomptaia_user -d ecomptaia_db
        
        # Nettoyer les fichiers de logs
        $logFiles = Get-ChildItem -Path "data/logs" -File -Recurse | Where-Object { $_.LastWriteTime -lt (Get-Date).AddDays(-$DaysToKeep) }
        if ($logFiles) {
            $logFiles | Remove-Item -Force
            Write-Success "Supprimé $($logFiles.Count) fichiers de logs anciens"
        }
        
        # Nettoyer les sauvegardes anciennes
        $oldBackups = Get-ChildItem -Path "backup" -File -Filter "*.sql" | Where-Object { $_.LastWriteTime -lt (Get-Date).AddDays(-$DaysToKeep) }
        if ($oldBackups) {
            if (-not $Force) {
                $confirmation = Read-Host "Supprimer $($oldBackups.Count) sauvegardes anciennes ? (oui/non)"
                if ($confirmation -eq "oui") {
                    $oldBackups | Remove-Item -Force
                    Write-Success "Supprimé $($oldBackups.Count) sauvegardes anciennes"
                }
            } else {
                $oldBackups | Remove-Item -Force
                Write-Success "Supprimé $($oldBackups.Count) sauvegardes anciennes"
            }
        }
        
        # Nettoyer les images Docker inutilisées
        Write-Info "Nettoyage des images Docker inutilisées..."
        podman image prune -f
        podman system prune -f
        
        Write-Success "Nettoyage terminé"
    } catch {
        Write-Error "Échec du nettoyage: $_"
        exit 1
    }
}

# Fonction de vérification de santé
function Test-SystemHealth {
    Write-Header "VÉRIFICATION DE LA SANTÉ DES SERVICES"
    
    $allHealthy = $true
    
    # Vérifier PostgreSQL
    try {
        $dbHealth = podman exec ecomptaia-postgres pg_isready -U ecomptaia_user -d ecomptaia_db 2>$null
        if ($dbHealth -match "accepting connections") {
            Write-Success "PostgreSQL: Service accessible"
        } else {
            Write-Error "PostgreSQL: Service non accessible"
            $allHealthy = $false
        }
    } catch {
        Write-Error "PostgreSQL: Service non accessible"
        $allHealthy = $false
    }
    
    # Vérifier Redis
    try {
        $redisHealth = podman exec ecomptaia-redis redis-cli ping 2>$null
        if ($redisHealth -eq "PONG") {
            Write-Success "Redis: Service accessible"
        } else {
            Write-Error "Redis: Service non accessible"
            $allHealthy = $false
        }
    } catch {
        Write-Error "Redis: Service non accessible"
        $allHealthy = $false
    }
    
    # Vérifier Backend
    try {
        $backendHealth = Invoke-RestMethod -Uri "http://localhost:8080/api/actuator/health" -Method Get -TimeoutSec 10 2>$null
        if ($backendHealth.status -eq "UP") {
            Write-Success "Backend: Service accessible (Status: $($backendHealth.status))"
        } else {
            Write-Error "Backend: Service non accessible"
            $allHealthy = $false
        }
    } catch {
        Write-Error "Backend: Service non accessible"
        $allHealthy = $false
    }
    
    # Vérifier Frontend
    try {
        $frontendResponse = Invoke-WebRequest -Uri "http://localhost:4200" -Method Get -TimeoutSec 10 2>$null
        if ($frontendResponse.StatusCode -eq 200) {
            Write-Success "Frontend: Service accessible (HTTP $($frontendResponse.StatusCode))"
        } else {
            Write-Error "Frontend: Service non accessible"
            $allHealthy = $false
        }
    } catch {
        Write-Error "Frontend: Service non accessible"
        $allHealthy = $false
    }
    
    if ($allHealthy) {
        Write-Success "Tous les services sont en bonne santé"
    } else {
        Write-Warning "Certains services ont des problèmes"
    }
}

# Fonction d'affichage des logs
function Show-Logs {
    Write-Header "LOGS DES SERVICES"
    
    Write-Info "Logs des 50 dernières lignes de chaque service:"
    
    $services = @("postgres", "redis", "backend", "frontend", "nginx")
    
    foreach ($service in $services) {
        Write-Host ""
        Write-Host "=== LOGS $service ===" -ForegroundColor Yellow
        try {
            podman-compose -f docker-compose-podman.yml logs --tail=50 $service
        } catch {
            Write-Error "Impossible de récupérer les logs de $service"
        }
    }
}

# Fonction de mise à jour
function Update-System {
    Write-Header "MISE À JOUR DU SYSTÈME"
    
    if (-not $Force) {
        $confirmation = Read-Host "Êtes-vous sûr de vouloir mettre à jour le système ? (oui/non)"
        if ($confirmation -ne "oui") {
            Write-Info "Mise à jour annulée"
            exit 0
        }
    }
    
    try {
        # Arrêter les services
        Write-Info "Arrêt des services..."
        podman-compose -f docker-compose-podman.yml down
        
        # Construire les nouvelles images
        Write-Info "Construction des nouvelles images..."
        podman-compose -f docker-compose-podman.yml build --no-cache
        
        # Redémarrer les services
        Write-Info "Redémarrage des services..."
        podman-compose -f docker-compose-podman.yml up -d
        
        # Attendre le démarrage
        Write-Info "Attente du démarrage (60 secondes)..."
        Start-Sleep -Seconds 60
        
        # Vérifier la santé
        Test-SystemHealth
        
        Write-Success "Mise à jour terminée"
    } catch {
        Write-Error "Échec de la mise à jour: $_"
        exit 1
    }
}

# Fonction de monitoring
function Show-Monitoring {
    Write-Header "MONITORING DU SYSTÈME"
    
    # Statut des conteneurs
    Write-Info "Statut des conteneurs:"
    podman-compose -f docker-compose-podman.yml ps
    
    # Utilisation des ressources
    Write-Info "Utilisation des ressources:"
    podman stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.MemPerc}}\t{{.NetIO}}\t{{.BlockIO}}"
    
    # Espace disque
    Write-Info "Espace disque:"
    podman system df
    
    # Métriques de la base de données
    Write-Info "Métriques de la base de données:"
    try {
        $dbMetrics = @"
SELECT 
    'Active Connections' as metric,
    count(*) as value
FROM pg_stat_activity 
WHERE state = 'active'
UNION ALL
SELECT 
    'Database Size' as metric,
    pg_size_pretty(pg_database_size('ecomptaia_db')) as value
UNION ALL
SELECT 
    'Cache Hit Ratio' as metric,
    round(100.0 * sum(blks_hit) / (sum(blks_hit) + sum(blks_read)), 2) || '%' as value
FROM pg_stat_database 
WHERE datname = 'ecomptaia_db';
"@
        
        $dbMetrics | podman exec -i ecomptaia-postgres psql -U ecomptaia_user -d ecomptaia_db -t
    } catch {
        Write-Error "Impossible de récupérer les métriques de la base de données"
    }
}

# Exécution de l'action demandée
switch ($Action) {
    "backup" { Backup-Database }
    "restore" { Restore-Database }
    "cleanup" { Remove-OldData }
    "health" { Test-SystemHealth }
    "logs" { Show-Logs }
    "update" { Update-System }
    "monitor" { Show-Monitoring }
    default {
        Write-Error "Action non reconnue: $Action"
        Write-Info "Utilisez -Help pour voir les actions disponibles"
        exit 1
    }
}

Write-Header "MAINTENANCE TERMINÉE"
Write-Success "Action '$Action' exécutée avec succès"
