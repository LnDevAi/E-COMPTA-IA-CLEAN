# Script pour corriger les fichiers avec plus de 50 erreurs
Write-Host "=== CORRECTION DES FICHIERS À HAUT NOMBRE D'ERREURS ===" -ForegroundColor Green

# Fonction pour corriger un fichier avec beaucoup d'erreurs
function Fix-HighErrorFile {
    param($filePath)
    
    try {
        $content = Get-Content -Path $filePath -Raw -Encoding UTF8
        $originalContent = $content
        
        # Corriger les packages cassés - TOUS les cas possibles
        $content = $content -replace '^ackage ', 'package '
        $content = $content -replace '^package$', 'package com.ecomptaia.entity;'
        $content = $content -replace '^package com\\.ecomptaia\\.security\\.entity;', 'package com.ecomptaia.entity;'
        $content = $content -replace '^package com\\.ecomptaia\\.accounting\\.entity;', 'package com.ecomptaia.accounting;'
        
        # Corriger les packages manquants
        if ($content -notmatch '^package ') {
            $content = "package com.ecomptaia.entity;`n`n" + $content
        }
        
        # Corriger les imports cassés
        $content = $content -replace 'pa`nimport', 'import'
        $content = $content -replace 'ckage com\\.ecomptaia\\.', 'import com.ecomptaia.'
        $content = $content -replace 'pa`n', ''
        $content = $content -replace 'ckage com\\.ecomptaia\\.', 'import com.ecomptaia.'
        
        # Corriger les imports spécifiques
        $content = $content -replace 'import com\\.ecomptaia\\.entity\\.Company;', 'import com.ecomptaia.security.entity.Company;'
        $content = $content -replace 'import com\\.ecomptaia\\.entity\\.User;', 'import com.ecomptaia.security.entity.User;'
        
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

# Liste des fichiers avec plus de 50 erreurs (basé sur le dernier rapport)
$highErrorFiles = @(
    "backend/src/main/java/com/ecomptaia/accounting/SyscohadaSMTChartOfAccounts.java",
    "backend/src/main/java/com/ecomptaia/accounting/SyscohadaChartOfAccounts.java", 
    "backend/src/main/java/com/ecomptaia/accounting/GAAPChartOfAccounts.java",
    "backend/src/main/java/com/ecomptaia/accounting/IFRSChartOfAccounts.java",
    "backend/src/main/java/com/ecomptaia/accounting/PCGFrenchChartOfAccounts.java",
    "backend/src/main/java/com/ecomptaia/controller/PieceJustificativeComptableController.java",
    "backend/src/main/java/com/ecomptaia/sycebnl/service/GenerationEcritureService.java",
    "backend/src/main/java/com/ecomptaia/service/AIIntelligenceService.java",
    "backend/src/main/java/com/ecomptaia/service/EcritureComptableService.java",
    "backend/src/main/java/com/ecomptaia/controller/AssetInventoryAdvancedController.java",
    "backend/src/main/java/com/ecomptaia/controller/ConfigurationController.java",
    "backend/src/main/java/com/ecomptaia/controller/NotificationController.java",
    "backend/src/main/java/com/ecomptaia/service/EtatsFinanciersAutoService.java",
    "backend/src/main/java/com/ecomptaia/controller/DatabaseManagementController.java",
    "backend/src/main/java/com/ecomptaia/controller/AccountingJournalEntryController.java"
)

$correctedCount = 0

Write-Host "`nCorrection des fichiers à haut nombre d'erreurs..." -ForegroundColor Yellow

foreach ($file in $highErrorFiles) {
    if (Test-Path $file) {
        if (Fix-HighErrorFile $file) {
            $correctedCount++
        }
    } else {
        Write-Host "⚠️ Fichier non trouvé: $file" -ForegroundColor Yellow
    }
}

Write-Host "`n=== RÉSUMÉ ===" -ForegroundColor Green
Write-Host "Fichiers corrigés: $correctedCount" -ForegroundColor Green
Write-Host "Tous les fichiers à haut nombre d'erreurs ont été traités !" -ForegroundColor Green
