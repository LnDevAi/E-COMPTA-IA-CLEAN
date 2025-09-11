# Script pour corriger les fichiers avec le plus d'erreurs
Write-Host "=== CORRECTION DES FICHIERS AVEC LE PLUS D'ERREURS ===" -ForegroundColor Green

# Fonction pour corriger un fichier avec beaucoup d'erreurs
function Fix-HighestErrorFile {
    param($filePath)
    
    try {
        $content = Get-Content -Path $filePath -Raw -Encoding UTF8
        $originalContent = $content
        
        # Corriger les packages cassés - TOUS les cas possibles
        $content = $content -replace '^ackage ', 'package '
        $content = $content -replace '^package$', 'package com.ecomptaia.entity;'
        
        # Corriger les imports cassés - TOUS les cas possibles
        $content = $content -replace 'pa`nimport', 'import'
        $content = $content -replace 'ckage com\\.ecomptaia\\.', 'import com.ecomptaia.'
        $content = $content -replace 'pa`n', ''
        $content = $content -replace 'ckage com\\.ecomptaia\\.', 'import com.ecomptaia.'
        
        # Corriger les packages manquants
        if ($content -notmatch '^package ') {
            $content = "package com.ecomptaia.entity;`n`n" + $content
        }
        
        # Corriger les imports spécifiques
        $content = $content -replace 'import com\\.ecomptaia\\.entity\\.Company;', 'import com.ecomptaia.security.entity.Company;'
        $content = $content -replace 'import com\\.ecomptaia\\.entity\\.User;', 'import com.ecomptaia.security.entity.User;'
        
        # Corriger les constructeurs Account
        $content = $content -replace 'new Account\("([^"]+)", "([^"]+)", ([^,]+), ([^)]+)\)', 'new Account("$1", "$2", $3)'
        
        # Corriger les caractères d'encodage
        $content = $content -replace 'rÃ©serves', 'réserves'
        $content = $content -replace 'RÃ©serves', 'Réserves'
        $content = $content -replace 'RÃ©sultats', 'Résultats'
        $content = $content -replace 'RÃ©sultat', 'Résultat'
        $content = $content -replace 'assimilÃ©es', 'assimilées'
        $content = $content -replace 'simplifiÃ©e', 'simplifiée'
        
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

# Liste des fichiers avec le plus d'erreurs (basé sur le dernier rapport)
$highestErrorFiles = @(
    "backend/src/main/java/com/ecomptaia/accounting/PCGFrenchChartOfAccounts.java",  # 75+ erreurs
    "backend/src/main/java/com/ecomptaia/accounting/GAAPChartOfAccounts.java",      # 55+ erreurs
    "backend/src/main/java/com/ecomptaia/accounting/SyscohadaChartOfAccounts.java", # 53+ erreurs
    "backend/src/main/java/com/ecomptaia/accounting/SyscohadaSMTChartOfAccounts.java", # 48+ erreurs
    "backend/src/main/java/com/ecomptaia/accounting/IFRSChartOfAccounts.java",      # 43+ erreurs
    "backend/src/main/java/com/ecomptaia/controller/PieceJustificativeComptableController.java", # 52+ erreurs
    "backend/src/main/java/com/ecomptaia/sycebnl/service/GenerationEcritureService.java", # 27+ erreurs
    "backend/src/main/java/com/ecomptaia/service/AIIntelligenceService.java",       # 18+ erreurs
    "backend/src/main/java/com/ecomptaia/entity/EcritureComptable.java",            # 4+ erreurs de syntaxe
    "backend/src/main/java/com/ecomptaia/entity/LigneEcriture.java",                # 2+ erreurs de syntaxe
    "backend/src/main/java/com/ecomptaia/entity/WorkflowInstance.java",             # 4+ erreurs de syntaxe
    "backend/src/main/java/com/ecomptaia/service/EcritureComptableService.java",    # 4+ erreurs de syntaxe
    "backend/src/main/java/com/ecomptaia/service/EcritureComptableAIService.java",  # 4+ erreurs de syntaxe
    "backend/src/main/java/com/ecomptaia/sycebnl/service/SycebnlOrganizationService.java" # 3+ erreurs de syntaxe
)

$correctedCount = 0

Write-Host "`nCorrection des fichiers avec le plus d'erreurs..." -ForegroundColor Yellow

foreach ($file in $highestErrorFiles) {
    if (Test-Path $file) {
        if (Fix-HighestErrorFile $file) {
            $correctedCount++
        }
    } else {
        Write-Host "⚠️ Fichier non trouvé: $file" -ForegroundColor Yellow
    }
}

Write-Host "`n=== RÉSUMÉ ===" -ForegroundColor Green
Write-Host "Fichiers corrigés: $correctedCount" -ForegroundColor Green
Write-Host "Tous les fichiers à haut nombre d'erreurs ont été traités !" -ForegroundColor Green
