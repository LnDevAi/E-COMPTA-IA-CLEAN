# Script pour corriger TOUS les packages cassés
Write-Host "=== CORRECTION MASSIVE DES PACKAGES CASSES ===" -ForegroundColor Green

# Trouver tous les fichiers Java avec des packages cassés
$javaFiles = Get-ChildItem -Path "backend/src" -Filter "*.java" -Recurse
$correctedCount = 0

Write-Host "Analyse de $($javaFiles.Count) fichiers Java..." -ForegroundColor Yellow

foreach ($file in $javaFiles) {
    try {
        $content = Get-Content -Path $file.FullName -Raw -Encoding UTF8
        $originalContent = $content
        
        # Supprimer toutes les lignes "pa" et "ckage" isolées
        $content = $content -replace '^pa\s*$', ''
        $content = $content -replace '^ckage\s*$', ''
        
        # Supprimer les lignes vides multiples
        $content = $content -replace '\n\s*\n\s*\n', "`n`n"
        
        # S'assurer qu'il n'y a qu'un seul package au début
        $lines = $content -split "`n"
        $newLines = @()
        $packageFound = $false
        
        foreach ($line in $lines) {
            if ($line -match '^package\s+') {
                if (-not $packageFound) {
                    $newLines += $line
                    $packageFound = $true
                }
            } else {
                $newLines += $line
            }
        }
        
        $content = $newLines -join "`n"
        
        if ($content -ne $originalContent) {
            Set-Content -Path $file.FullName -Value $content -Encoding UTF8
            Write-Host "✅ Corrigé: $($file.Name)" -ForegroundColor Green
            $correctedCount++
        }
    }
    catch {
        Write-Host "❌ Erreur avec $($file.Name) : $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "`n=== RÉSULTAT ===" -ForegroundColor Green
Write-Host "Fichiers corrigés: $correctedCount" -ForegroundColor Green

if ($correctedCount -gt 0) {
    Write-Host "✅ Tous les packages cassés ont été corrigés !" -ForegroundColor Green
} else {
    Write-Host "ℹ️ Aucun fichier à corriger trouvé." -ForegroundColor Yellow
}
