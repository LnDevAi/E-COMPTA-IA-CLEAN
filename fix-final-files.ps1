# Script pour corriger les derniers fichiers
Write-Host "=== CORRECTION DES DERNIERS FICHIERS ===" -ForegroundColor Green

$filesToFix = @(
    "backend/src/main/java/com/ecomptaia/repository/FinancialReportRepository.java",
    "backend/src/main/java/com/ecomptaia/repository/CompanyRepository.java",
    "backend/src/main/java/com/ecomptaia/repository/AutomatedTaskRepository.java",
    "backend/src/main/java/com/ecomptaia/entity/InventoryMovement.java",
    "backend/src/main/java/com/ecomptaia/entity/InventoryAnalysisDetail.java",
    "backend/src/main/java/com/ecomptaia/entity/InventoryAnalysis.java",
    "backend/src/main/java/com/ecomptaia/entity/GedDocument.java",
    "backend/src/main/java/com/ecomptaia/entity/DocumentWorkflow.java",
    "backend/src/main/java/com/ecomptaia/entity/DocumentApproval.java",
    "backend/src/main/java/com/ecomptaia/entity/CountryConfig.java",
    "backend/src/main/java/com/ecomptaia/entity/AutomatedTask.java",
    "backend/src/main/java/com/ecomptaia/entity/AssetInventoryDocument.java",
    "backend/src/main/java/com/ecomptaia/entity/Asset.java",
    "backend/src/main/java/com/ecomptaia/entity/Project.java",
    "backend/src/main/java/com/ecomptaia/entity/CompanySubscription.java",
    "backend/src/main/java/com/ecomptaia/sycebnl/service/SycebnlOrganizationService.java",
    "backend/src/main/java/com/ecomptaia/service/EcritureComptableAIService.java"
)

$correctedCount = 0

foreach ($file in $filesToFix) {
    if (Test-Path $file) {
        try {
            $content = Get-Content -Path $file -Raw -Encoding UTF8
            $originalContent = $content
            
            $content = $content -replace '^pa\s*$', ''
            $content = $content -replace '^ckage\s*$', ''
            $content = $content -replace '\n\s*\n\s*\n', "`n`n"
            
            if ($content -ne $originalContent) {
                Set-Content -Path $file -Value $content -Encoding UTF8
                Write-Host "Corrige: $($file -replace '.*\\', '')" -ForegroundColor Green
                $correctedCount++
            }
        }
        catch {
            Write-Host "Erreur avec $file" -ForegroundColor Red
        }
    }
}

Write-Host "`nFichiers corriges: $correctedCount" -ForegroundColor Green
