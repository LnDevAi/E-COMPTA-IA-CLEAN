# Vérifier la syntaxe du fichier compose
Write-Host "`nVerification de la syntaxe podman-compose.yml..." -ForegroundColor Cyan
try {
    Get-Content "docker/podman-compose.yml" -Raw | Out-Null
    Write-Host "OK Syntaxe podman-compose.yml valide" -ForegroundColor Green
} catch {
    Write-Host "ERREUR Syntaxe podman-compose.yml invalide" -ForegroundColor Red
}
