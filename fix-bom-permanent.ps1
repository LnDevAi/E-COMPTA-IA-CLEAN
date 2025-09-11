# Script permanent pour supprimer TOUS les BOM du projet
Write-Host "=== NETTOYAGE PERMANENT DES BOM ===" -ForegroundColor Green

# Fonction pour supprimer BOM d'un fichier
function Remove-BOM {
    param($filePath)

    try {
        $content = Get-Content -Path $filePath -Raw -Encoding UTF8
        if ($content -match '^\ufeff') {
            $content = $content -replace '^\ufeff', ''
            Set-Content -Path $filePath -Value $content -Encoding UTF8NoBOM
            Write-Host "✅ BOM supprimé: $filePath" -ForegroundColor Green
            return $true
        }
        return $false
    }
    catch {
        Write-Host "❌ Erreur avec $filePath : $($_.Exception.Message)" -ForegroundColor Red
        return $false
    }
}

# Trouver TOUS les fichiers Java
$javaFiles = Get-ChildItem -Path "backend/src" -Filter "*.java" -Recurse

$bomCount = 0
$totalFiles = $javaFiles.Count

Write-Host "`nAnalyse de $totalFiles fichiers Java..." -ForegroundColor Yellow

foreach ($file in $javaFiles) {
    if (Remove-BOM $file.FullName) {
        $bomCount++
    }
}

Write-Host "`n=== RÉSULTAT ===" -ForegroundColor Green
Write-Host "Fichiers avec BOM trouvés: $bomCount" -ForegroundColor Green
Write-Host "Fichiers traités: $totalFiles" -ForegroundColor Green

if ($bomCount -gt 0) {
    Write-Host "`n⚠️ ATTENTION: $bomCount fichiers avaient des BOM !" -ForegroundColor Yellow
    Write-Host "Vérifiez votre éditeur pour éviter de recréer des BOM." -ForegroundColor Yellow
} else {
    Write-Host "`n✅ Aucun BOM détecté - projet propre !" -ForegroundColor Green
}
