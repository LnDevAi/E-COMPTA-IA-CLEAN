# Script pour analyser les erreurs de linting
Write-Host "=== ANALYSE DES ERREURS DE LINTING ===" -ForegroundColor Green

# Compter les fichiers Java
$javaFiles = Get-ChildItem -Path "backend/src" -Filter "*.java" -Recurse
Write-Host "Fichiers Java trouvés: $($javaFiles.Count)" -ForegroundColor Yellow

# Vérifier les erreurs de compilation
Write-Host "`nTest de compilation..." -ForegroundColor Yellow
cd backend
$compileResult = mvn compile -q 2>&1
cd ..

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Compilation réussie" -ForegroundColor Green
} else {
    Write-Host "❌ Erreurs de compilation détectées" -ForegroundColor Red
    Write-Host $compileResult -ForegroundColor Red
}

# Analyser les types d'erreurs courantes
Write-Host "`nAnalyse des erreurs courantes..." -ForegroundColor Yellow

# Vérifier les imports manquants
$importErrors = 0
$packageErrors = 0
$syntaxErrors = 0

foreach ($file in $javaFiles) {
    $content = Get-Content -Path $file.FullName -Raw -Encoding UTF8

    # Vérifier les imports cassés
    if ($content -match 'import\s+[^;]+;\s*$' -and $content -notmatch 'import\s+[a-zA-Z0-9_.]+\.[a-zA-Z0-9_]+;') {
        $importErrors++
    }

    # Vérifier les packages cassés
    if ($content -match '^pa\s*$' -or $content -match '^ckage\s*$') {
        $packageErrors++
    }

    # Vérifier les erreurs de syntaxe
    if ($content -match 'class\s+\w+\s*\{' -and $content -notmatch 'package\s+[a-zA-Z0-9_.]+;') {
        $syntaxErrors++
    }
}

Write-Host "`n=== RÉSUMÉ DES ERREURS ===" -ForegroundColor Green
Write-Host "Erreurs d'imports: $importErrors" -ForegroundColor Yellow
Write-Host "Erreurs de packages: $packageErrors" -ForegroundColor Yellow
Write-Host "Erreurs de syntaxe: $syntaxErrors" -ForegroundColor Yellow

$totalErrors = $importErrors + $packageErrors + $syntaxErrors
Write-Host "Total d'erreurs détectées: $totalErrors" -ForegroundColor Cyan

if ($totalErrors -gt 0) {
    Write-Host "`n🔧 Actions recommandées:" -ForegroundColor Green
    if ($packageErrors -gt 0) {
        Write-Host "1. Corriger les packages cassés (pa/ckage)" -ForegroundColor Yellow
    }
    if ($importErrors -gt 0) {
        Write-Host "2. Corriger les imports manquants" -ForegroundColor Yellow
    }
    if ($syntaxErrors -gt 0) {
        Write-Host "3. Corriger les erreurs de syntaxe" -ForegroundColor Yellow
    }
} else {
    Write-Host "`n✅ Aucune erreur majeure detectee !" -ForegroundColor Green
}
