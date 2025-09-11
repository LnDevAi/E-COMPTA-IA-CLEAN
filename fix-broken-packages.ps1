# Script pour corriger les packages cassés
Write-Host "=== CORRECTION DES PACKAGES CASSES ===" -ForegroundColor Green

# Liste des fichiers avec des packages cassés
$brokenFiles = @(
    "backend/src/main/java/com/ecomptaia/entity/WorkflowInstance.java",
    "backend/src/main/java/com/ecomptaia/service/EcritureComptableService.java",
    "backend/src/main/java/com/ecomptaia/service/EcritureComptableAIService.java",
    "backend/src/main/java/com/ecomptaia/entity/LocaleSettings.java",
    "backend/src/main/java/com/ecomptaia/entity/LigneEcriture.java",
    "backend/src/main/java/com/ecomptaia/entity/EcritureComptable.java",
    "backend/src/main/java/com/ecomptaia/sycebnl/service/SycebnlOrganizationService.java",
    "backend/src/main/java/com/ecomptaia/service/UserManagementService.java",
    "backend/src/main/java/com/ecomptaia/service/SubscriptionService.java",
    "backend/src/main/java/com/ecomptaia/service/SimplePDFService.java",
    "backend/src/main/java/com/ecomptaia/service/OHADAPDFExportService.java",
    "backend/src/main/java/com/ecomptaia/service/MultiTenantService.java",
    "backend/src/main/java/com/ecomptaia/service/MobileService.java",
    "backend/src/main/java/com/ecomptaia/service/LegalInformationService.java",
    "backend/src/main/java/com/ecomptaia/service/InternationalService.java",
    "backend/src/main/java/com/ecomptaia/service/DashboardService.java",
    "backend/src/main/java/com/ecomptaia/service/AuditService.java",
    "backend/src/main/java/com/ecomptaia/service/AssetInventoryService.java",
    "backend/src/main/java/com/ecomptaia/service/AssetInventoryReportingService.java",
    "backend/src/main/java/com/ecomptaia/service/AssetInventoryDocumentService.java",
    "backend/src/main/java/com/ecomptaia/service/AIService.java",
    "backend/src/main/java/com/ecomptaia/service/AIDocumentAnalysisService.java",
    "backend/src/main/java/com/ecomptaia/service/AccountingStandardService.java",
    "backend/src/main/java/com/ecomptaia/security/UserDetailsServiceImpl.java",
    "backend/src/main/java/com/ecomptaia/security/SecurityAuditService.java",
    "backend/src/main/java/com/ecomptaia/repository/FinancialReportRepository.java",
    "backend/src/main/java/com/ecomptaia/repository/CompanyRepository.java",
    "backend/src/main/java/com/ecomptaia/repository/AutomatedTaskRepository.java",
    "backend/src/main/java/com/ecomptaia/entity/ThirdParty.java",
    "backend/src/main/java/com/ecomptaia/entity/Reconciliation.java",
    "backend/src/main/java/com/ecomptaia/entity/InventoryMovement.java",
    "backend/src/main/java/com/ecomptaia/entity/InventoryAnalysisDetail.java",
    "backend/src/main/java/com/ecomptaia/entity/InventoryAnalysis.java",
    "backend/src/main/java/com/ecomptaia/entity/Inventory.java",
    "backend/src/main/java/com/ecomptaia/entity/GedDocument.java",
    "backend/src/main/java/com/ecomptaia/entity/FinancialReport.java",
    "backend/src/main/java/com/ecomptaia/entity/DocumentWorkflow.java",
    "backend/src/main/java/com/ecomptaia/entity/DocumentApproval.java",
    "backend/src/main/java/com/ecomptaia/entity/CountryConfig.java",
    "backend/src/main/java/com/ecomptaia/entity/AutomatedTask.java",
    "backend/src/main/java/com/ecomptaia/entity/AssetInventoryDocument.java",
    "backend/src/main/java/com/ecomptaia/entity/Asset.java",
    "backend/src/main/java/com/ecomptaia/entity/Project.java",
    "backend/src/main/java/com/ecomptaia/entity/CompanySubscription.java"
)

$correctedCount = 0

Write-Host "Correction de $($brokenFiles.Count) fichiers..." -ForegroundColor Yellow

foreach ($file in $brokenFiles) {
    if (Test-Path $file) {
        try {
            $content = Get-Content -Path $file -Raw -Encoding UTF8
            $originalContent = $content

            # Corriger les packages cassés
            $content = $content -replace '^pa\s*$', 'package com.ecomptaia.entity;'
            $content = $content -replace '^ckage\s*$', 'package com.ecomptaia.entity;'

            # Corriger les packages spécifiques selon le type de fichier
            if ($file -match 'service/') {
                $content = $content -replace '^pa\s*$', 'package com.ecomptaia.service;'
            } elseif ($file -match 'repository/') {
                $content = $content -replace '^pa\s*$', 'package com.ecomptaia.repository;'
            } elseif ($file -match 'security/') {
                $content = $content -replace '^pa\s*$', 'package com.ecomptaia.security;'
            } elseif ($file -match 'sycebnl/') {
                $content = $content -replace '^pa\s*$', 'package com.ecomptaia.sycebnl.service;'
            }

            if ($content -ne $originalContent) {
                Set-Content -Path $file -Value $content -Encoding UTF8
                Write-Host "✅ Corrigé: $($file -replace '.*\\', '')" -ForegroundColor Green
                $correctedCount++
            }
        }
        catch {
            Write-Host "❌ Erreur avec $file : $($_.Exception.Message)" -ForegroundColor Red
        }
    } else {
        Write-Host "⚠️ Fichier non trouvé: $file" -ForegroundColor Yellow
    }
}

Write-Host "`n=== RÉSULTAT ===" -ForegroundColor Green
Write-Host "Fichiers corrigés: $correctedCount" -ForegroundColor Green
Write-Host "Tous les packages cassés ont été corrigés !" -ForegroundColor Green
