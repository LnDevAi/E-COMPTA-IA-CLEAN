# Script pour corriger toutes les erreurs de compilation Java
Write-Host "=== CORRECTION COMPLÈTE DES ERREURS BACKEND ===" -ForegroundColor Cyan

# Arrêter tous les processus Java
Write-Host "1. Arrêt des processus Java..." -ForegroundColor Yellow
Get-Process -Name "java" -ErrorAction SilentlyContinue | Stop-Process -Force
Start-Sleep -Seconds 3

# Se déplacer dans le répertoire backend
Write-Host "2. Déplacement vers le répertoire backend..." -ForegroundColor Yellow
Set-Location "backend"

# Nettoyer complètement le projet
Write-Host "3. Nettoyage complet du projet..." -ForegroundColor Yellow
Remove-Item -Path "target" -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item -Path "$env:USERPROFILE\.m2\repository\com\ecomptaia" -Recurse -Force -ErrorAction SilentlyContinue

# Vérifier la configuration Maven
Write-Host "4. Vérification de la configuration Maven..." -ForegroundColor Yellow
C:\Users\HP\maven\bin\mvn.cmd --version

# Compiler le projet étape par étape
Write-Host "5. Compilation du projet..." -ForegroundColor Green
Write-Host "   - Nettoyage..." -ForegroundColor Gray
C:\Users\HP\maven\bin\mvn.cmd clean

Write-Host "   - Validation..." -ForegroundColor Gray
C:\Users\HP\maven\bin\mvn.cmd validate

Write-Host "   - Compilation..." -ForegroundColor Gray
C:\Users\HP\maven\bin\mvn.cmd compile

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Compilation réussie !" -ForegroundColor Green
    
    # Tester le démarrage
    Write-Host "6. Test du démarrage Spring Boot..." -ForegroundColor Green
    C:\Users\HP\maven\bin\mvn.cmd spring-boot:run
} else {
    Write-Host "❌ Erreur de compilation. Vérifiez les logs ci-dessus." -ForegroundColor Red
    Write-Host "Veuillez corriger les erreurs avant de continuer." -ForegroundColor Yellow
}

Write-Host "=== FIN DU SCRIPT ===" -ForegroundColor Cyan
