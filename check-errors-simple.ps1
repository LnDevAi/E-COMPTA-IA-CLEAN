# Script simple pour analyser les erreurs
Write-Host "=== ANALYSE DES ERREURS ===" -ForegroundColor Green

# Compter les fichiers Java
$javaFiles = Get-ChildItem -Path "backend/src" -Filter "*.java" -Recurse
Write-Host "Fichiers Java trouves: $($javaFiles.Count)" -ForegroundColor Yellow

# Vérifier les erreurs de compilation
Write-Host "`nTest de compilation..." -ForegroundColor Yellow
cd backend
$compileResult = mvn compile -q 2>&1
cd ..

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Compilation reussie" -ForegroundColor Green
} else {
    Write-Host "❌ Erreurs de compilation detectees" -ForegroundColor Red
}

# Analyser les erreurs courantes
Write-Host "`nAnalyse des erreurs courantes..." -ForegroundColor Yellow

$importErrors = 0
$packageErrors = 0

foreach ($file in $javaFiles) {
    $content = Get-Content -Path $file.FullName -Raw -Encoding UTF8

    # Vérifier les packages cassés
    if ($content -match '^pa\s*$' -or $content -match '^ckage\s*$') {
        $packageErrors++
        Write-Host "Package casse: $($file.Name)" -ForegroundColor Red
    }

    # Vérifier les imports cassés
    if ($content -match 'import\s+[^;]+;\s*$' -and $content -notmatch 'import\s+[a-zA-Z0-9_.]+\.[a-zA-Z0-9_]+;') {
        $importErrors++
    }
}

Write-Host "`n=== RESUME ===" -ForegroundColor Green
Write-Host "Erreurs de packages: $packageErrors" -ForegroundColor Yellow
Write-Host "Erreurs d'imports: $importErrors" -ForegroundColor Yellow

$totalErrors = $importErrors + $packageErrors
Write-Host "Total d'erreurs detectees: $totalErrors" -ForegroundColor Cyan

if ($totalErrors -gt 0) {
    Write-Host "`n🔧 Actions recommandees:" -ForegroundColor Green
    if ($packageErrors -gt 0) {
        Write-Host "1. Corriger les packages casses (pa/ckage)" -ForegroundColor Yellow
    }
    if ($importErrors -gt 0) {
        Write-Host "2. Corriger les imports manquants" -ForegroundColor Yellow
    }
} else {
    Write-Host "`n✅ Aucune erreur majeure detectee !" -ForegroundColor Green
}
