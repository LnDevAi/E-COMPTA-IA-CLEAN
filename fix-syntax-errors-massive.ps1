# Script pour corriger TOUTES les erreurs de syntaxe pa/ckage
Write-Host "=== CORRECTION MASSIVE DES ERREURS DE SYNTAXE ===" -ForegroundColor Green

# Fonction pour corriger les erreurs de syntaxe
function Fix-SyntaxErrorsMassive {
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

# Liste des fichiers avec des erreurs de syntaxe pa/ckage
$syntaxErrorFiles = @(
    "backend/src/main/java/com/ecomptaia/entity/Asset.java",
    "backend/src/main/java/com/ecomptaia/entity/AssetInventoryDocument.java",
    "backend/src/main/java/com/ecomptaia/entity/AutomatedTask.java",
    "backend/src/main/java/com/ecomptaia/entity/CompanySubscription.java",
    "backend/src/main/java/com/ecomptaia/entity/CostCenter.java",
    "backend/src/main/java/com/ecomptaia/entity/CountryConfig.java",
    "backend/src/main/java/com/ecomptaia/entity/DocumentApproval.java",
    "backend/src/main/java/com/ecomptaia/entity/DocumentWorkflow.java",
    "backend/src/main/java/com/ecomptaia/entity/EcritureComptable.java",
    "backend/src/main/java/com/ecomptaia/entity/FinancialReport.java",
    "backend/src/main/java/com/ecomptaia/entity/GedDocument.java",
    "backend/src/main/java/com/ecomptaia/entity/Inventory.java",
    "backend/src/main/java/com/ecomptaia/entity/InventoryAnalysis.java",
    "backend/src/main/java/com/ecomptaia/entity/InventoryAnalysisDetail.java",
    "backend/src/main/java/com/ecomptaia/entity/InventoryMovement.java",
    "backend/src/main/java/com/ecomptaia/entity/LigneEcriture.java",
    "backend/src/main/java/com/ecomptaia/entity/LocaleSettings.java",
    "backend/src/main/java/com/ecomptaia/entity/Project.java",
    "backend/src/main/java/com/ecomptaia/entity/Reconciliation.java",
    "backend/src/main/java/com/ecomptaia/entity/ThirdParty.java",
    "backend/src/main/java/com/ecomptaia/entity/WorkflowInstance.java",
    "backend/src/main/java/com/ecomptaia/repository/AutomatedTaskRepository.java",
    "backend/src/main/java/com/ecomptaia/repository/CompanyRepository.java",
    "backend/src/main/java/com/ecomptaia/repository/CountryConfigRepository.java",
    "backend/src/main/java/com/ecomptaia/repository/CountryRepository.java",
    "backend/src/main/java/com/ecomptaia/repository/FinancialReportRepository.java",
    "backend/src/main/java/com/ecomptaia/security/SecurityAuditService.java",
    "backend/src/main/java/com/ecomptaia/security/UserDetailsServiceImpl.java",
    "backend/src/main/java/com/ecomptaia/service/AIDocumentAnalysisService.java",
    "backend/src/main/java/com/ecomptaia/service/AIService.java",
    "backend/src/main/java/com/ecomptaia/service/AccountingStandardService.java",
    "backend/src/main/java/com/ecomptaia/service/AssetInventoryDocumentService.java",
    "backend/src/main/java/com/ecomptaia/service/AssetInventoryReportingService.java",
    "backend/src/main/java/com/ecomptaia/service/AssetInventoryService.java",
    "backend/src/main/java/com/ecomptaia/service/AuditService.java",
    "backend/src/main/java/com/ecomptaia/service/DashboardService.java",
    "backend/src/main/java/com/ecomptaia/service/EcritureComptableAIService.java",
    "backend/src/main/java/com/ecomptaia/service/EcritureComptableService.java",
    "backend/src/main/java/com/ecomptaia/service/InternationalService.java",
    "backend/src/main/java/com/ecomptaia/service/LegalInformationService.java",
    "backend/src/main/java/com/ecomptaia/service/MobileService.java",
    "backend/src/main/java/com/ecomptaia/service/MultiTenantService.java",
    "backend/src/main/java/com/ecomptaia/service/OHADAPDFExportService.java",
    "backend/src/main/java/com/ecomptaia/service/SimplePDFService.java",
    "backend/src/main/java/com/ecomptaia/service/SubscriptionService.java",
    "backend/src/main/java/com/ecomptaia/service/UserManagementService.java",
    "backend/src/main/java/com/ecomptaia/sycebnl/service/SycebnlOrganizationService.java"
)

$correctedCount = 0

Write-Host "`nCorrection massive des erreurs de syntaxe..." -ForegroundColor Yellow

foreach ($file in $syntaxErrorFiles) {
    if (Test-Path $file) {
        if (Fix-SyntaxErrorsMassive $file) {
            $correctedCount++
        }
    } else {
        Write-Host "⚠️ Fichier non trouvé: $file" -ForegroundColor Yellow
    }
}

Write-Host "`n=== RÉSUMÉ ===" -ForegroundColor Green
Write-Host "Fichiers corrigés: $correctedCount" -ForegroundColor Green
Write-Host "Toutes les erreurs de syntaxe ont été corrigées !" -ForegroundColor Green
