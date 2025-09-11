# Script pour corriger plus de fichiers
Write-Host "=== CORRECTION DE PLUS DE FICHIERS ===" -ForegroundColor Green

$filesToFix = @(
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
    "backend/src/main/java/com/ecomptaia/service/AIDocumentAnalysisService.java",
    "backend/src/main/java/com/ecomptaia/service/AccountingStandardService.java",
    "backend/src/main/java/com/ecomptaia/security/UserDetailsServiceImpl.java",
    "backend/src/main/java/com/ecomptaia/security/SecurityAuditService.java"
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
