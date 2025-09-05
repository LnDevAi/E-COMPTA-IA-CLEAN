# Script pour corriger les erreurs de compilation Java
Write-Host "=== CORRECTION DES ERREURS DE COMPILATION JAVA ===" -ForegroundColor Cyan

# Arrêter tous les processus Java
Write-Host "1. Arrêt des processus Java..." -ForegroundColor Yellow
Get-Process -Name "java" -ErrorAction SilentlyContinue | Stop-Process -Force
Start-Sleep -Seconds 2

# Se déplacer dans le répertoire backend
Write-Host "2. Déplacement vers le répertoire backend..." -ForegroundColor Yellow
Set-Location "backend"

# Nettoyer le cache Maven local
Write-Host "3. Nettoyage du cache Maven..." -ForegroundColor Yellow
Remove-Item -Path "$env:USERPROFILE\.m2\repository\com\ecomptaia" -Recurse -Force -ErrorAction SilentlyContinue

# Nettoyer le répertoire target
Write-Host "4. Nettoyage du répertoire target..." -ForegroundColor Yellow
Remove-Item -Path "target" -Recurse -Force -ErrorAction SilentlyContinue

# Vérifier la configuration Maven
Write-Host "5. Vérification de la configuration Maven..." -ForegroundColor Yellow
C:\Users\HP\maven\bin\mvn.cmd --version

# Compiler le projet
Write-Host "6. Compilation du projet..." -ForegroundColor Green
C:\Users\HP\maven\bin\mvn.cmd clean compile

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Compilation réussie !" -ForegroundColor Green
    
    # Tester le démarrage
    Write-Host "7. Test du démarrage Spring Boot..." -ForegroundColor Green
    C:\Users\HP\maven\bin\mvn.cmd spring-boot:run
} else {
    Write-Host "❌ Erreur de compilation. Vérifiez les logs ci-dessus." -ForegroundColor Red
}

Write-Host "=== FIN DU SCRIPT ===" -ForegroundColor Cyan
