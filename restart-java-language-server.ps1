# Script pour redémarrer le serveur de langage Java et forcer le rafraîchissement
Write-Host "=== REDÉMARRAGE DU SERVEUR DE LANGAGE JAVA ===" -ForegroundColor Green

# Nettoyer les fichiers temporaires Maven
Write-Host "`nNettoyage des fichiers temporaires Maven..." -ForegroundColor Yellow
cd backend
mvn clean -q
cd ..

# Nettoyer les fichiers de cache de l'IDE
Write-Host "`nNettoyage des fichiers de cache..." -ForegroundColor Yellow
if (Test-Path ".vscode") {
    Remove-Item -Path ".vscode" -Recurse -Force -ErrorAction SilentlyContinue
    Write-Host "✅ Cache .vscode supprimé" -ForegroundColor Green
}

if (Test-Path ".idea") {
    Remove-Item -Path ".idea" -Recurse -Force -ErrorAction SilentlyContinue
    Write-Host "✅ Cache .idea supprimé" -ForegroundColor Green
}

# Recompiler le projet
Write-Host "`nRecompilation du projet..." -ForegroundColor Yellow
cd backend
mvn compile -q
cd ..

Write-Host "`n=== REDÉMARRAGE TERMINÉ ===" -ForegroundColor Green
Write-Host "Le serveur de langage Java a été redémarré !" -ForegroundColor Green
Write-Host "Veuillez redémarrer votre IDE pour que les changements prennent effet." -ForegroundColor Yellow
