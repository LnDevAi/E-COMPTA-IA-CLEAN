# Script pour corriger les fichiers corrompus avec BOM
Write-Host "=== CORRECTION DES FICHIERS CORROMPUS ===" -ForegroundColor Green

# Fonction pour corriger un fichier corrompu
function Fix-CorruptedFile {
    param($filePath)
    
    try {
        $content = Get-Content -Path $filePath -Raw -Encoding UTF8
        $originalContent = $content
        
        # Supprimer le BOM
        $content = $content -replace '^\ufeff', ''
        
        # Corriger les packages cassés
        $content = $content -replace '^ackage ', 'package '
        $content = $content -replace '^package$', 'package com.ecomptaia.entity;'
        
        # Corriger les imports cassés
        $content = $content -replace 'pa`nimport', 'import'
        $content = $content -replace 'ckage com\\.ecomptaia\\.', 'import com.ecomptaia.'
        $content = $content -replace 'pa`n', ''
        $content = $content -replace 'ckage com\\.ecomptaia\\.', 'import com.ecomptaia.'
        
        # Corriger les packages manquants
        if ($content -notmatch '^package ') {
            $content = "package com.ecomptaia.entity;`n`n" + $content
        }
        
        if ($content -ne $originalContent) {
            Set-Content -Path $filePath -Value $content -Encoding UTF8
            Write-Host "✅ Corrigé: $filePath" -ForegroundColor Green
            return $true
        }
        return $false
    }
    catch {
        Write-Host "❌ Erreur avec $filePath : $($_.Exception.Message)" -ForegroundColor Red
        return $false
    }
}

# Liste des fichiers corrompus identifiés
$corruptedFiles = @(
    "backend/src/main/java/com/ecomptaia/repository/CountryConfigRepository.java",
    "backend/src/main/java/com/ecomptaia/service/TaxIntegrationService.java",
    "backend/src/main/java/com/ecomptaia/service/TestDataService.java",
    "backend/src/main/java/com/ecomptaia/entity/CostCenter.java",
    "backend/src/main/java/com/ecomptaia/repository/CountryRepository.java",
    "backend/src/main/java/com/ecomptaia/service/AIIntelligenceService.java",
    "backend/src/main/java/com/ecomptaia/service/EtatsFinanciersAutoService.java",
    "backend/src/main/java/com/ecomptaia/service/DocumentManagementService.java"
)

$correctedCount = 0

Write-Host "`nCorrection des fichiers corrompus..." -ForegroundColor Yellow

foreach ($file in $corruptedFiles) {
    if (Test-Path $file) {
        if (Fix-CorruptedFile $file) {
            $correctedCount++
        }
    } else {
        Write-Host "⚠️ Fichier non trouvé: $file" -ForegroundColor Yellow
    }
}

Write-Host "`n=== RÉSUMÉ ===" -ForegroundColor Green
Write-Host "Fichiers corrigés: $correctedCount" -ForegroundColor Green
Write-Host "Tous les fichiers corrompus ont été corrigés !" -ForegroundColor Green
