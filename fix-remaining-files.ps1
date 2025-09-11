# Script simple pour corriger les fichiers restants
Write-Host "=== CORRECTION DES FICHIERS RESTANTS ===" -ForegroundColor Green

# Liste des fichiers à corriger (les plus importants d'abord)
$filesToFix = @(
    "backend/src/main/java/com/ecomptaia/entity/LocaleSettings.java",
    "backend/src/main/java/com/ecomptaia/entity/LigneEcriture.java", 
    "backend/src/main/java/com/ecomptaia/entity/EcritureComptable.java",
    "backend/src/main/java/com/ecomptaia/service/UserManagementService.java",
    "backend/src/main/java/com/ecomptaia/service/SubscriptionService.java",
    "backend/src/main/java/com/ecomptaia/service/AIService.java",
    "backend/src/main/java/com/ecomptaia/entity/ThirdParty.java",
    "backend/src/main/java/com/ecomptaia/entity/Reconciliation.java",
    "backend/src/main/java/com/ecomptaia/entity/Inventory.java",
    "backend/src/main/java/com/ecomptaia/entity/FinancialReport.java"
)

$correctedCount = 0

foreach ($file in $filesToFix) {
    if (Test-Path $file) {
        try {
            $content = Get-Content -Path $file -Raw -Encoding UTF8
            $originalContent = $content
            
            # Supprimer les lignes "pa" et "ckage" isolées
            $content = $content -replace '^pa\s*$', ''
            $content = $content -replace '^ckage\s*$', ''
            
            # Nettoyer les lignes vides multiples
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
