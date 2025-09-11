# Script de prévention des BOM - à exécuter avant chaque modification
Write-Host "=== PRÉVENTION DES BOM ===" -ForegroundColor Green

# Vérifier si des BOM existent
$javaFiles = Get-ChildItem -Path "backend/src" -Filter "*.java" -Recurse
$bomFiles = @()

foreach ($file in $javaFiles) {
    $content = Get-Content -Path $file.FullName -Raw -Encoding UTF8
    if ($content -match '^\ufeff') {
        $bomFiles += $file.FullName
    }
}

if ($bomFiles.Count -gt 0) {
    Write-Host "⚠️ ATTENTION: $($bomFiles.Count) fichiers ont des BOM !" -ForegroundColor Red
    Write-Host "Fichiers problématiques:" -ForegroundColor Red
    foreach ($file in $bomFiles) {
        Write-Host "  - $file" -ForegroundColor Red
    }
    Write-Host "`nExécutez 'fix-bom-permanent.ps1' pour les corriger." -ForegroundColor Yellow
    exit 1
} else {
    Write-Host "✅ Aucun BOM détecté - vous pouvez continuer !" -ForegroundColor Green
    exit 0
}
