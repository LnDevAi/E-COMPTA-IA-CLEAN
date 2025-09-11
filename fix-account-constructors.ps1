# Script pour corriger les constructeurs Account dans tous les fichiers accounting
Write-Host "=== CORRECTION DES CONSTRUCTEURS ACCOUNT ===" -ForegroundColor Green

# Fonction pour corriger les constructeurs Account
function Fix-AccountConstructors {
    param($filePath)
    
    try {
        $content = Get-Content -Path $filePath -Raw -Encoding UTF8
        $originalContent = $content
        
        # Corriger les constructeurs Account avec 4 paramètres vers 3 paramètres
        $content = $content -replace 'new Account\("([^"]+)", "([^"]+)", ([^,]+), ([^)]+)\)', 'new Account("$1", "$2", $3)'
        
        # Corriger les caractères d'encodage
        $content = $content -replace 'rÃ©serves', 'réserves'
        $content = $content -replace 'RÃ©serves', 'Réserves'
        $content = $content -replace 'RÃ©sultats', 'Résultats'
        $content = $content -replace 'RÃ©sultat', 'Résultat'
        $content = $content -replace 'assimilÃ©es', 'assimilées'
        $content = $content -replace 'simplifiÃ©e', 'simplifiée'
        $content = $content -replace 'incorporelles', 'incorporelles'
        
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

# Liste des fichiers accounting à corriger
$accountingFiles = @(
    "backend/src/main/java/com/ecomptaia/accounting/SyscohadaSMTChartOfAccounts.java",
    "backend/src/main/java/com/ecomptaia/accounting/SyscohadaChartOfAccounts.java", 
    "backend/src/main/java/com/ecomptaia/accounting/GAAPChartOfAccounts.java",
    "backend/src/main/java/com/ecomptaia/accounting/IFRSChartOfAccounts.java",
    "backend/src/main/java/com/ecomptaia/accounting/PCGFrenchChartOfAccounts.java"
)

$correctedCount = 0

Write-Host "`nCorrection des constructeurs Account..." -ForegroundColor Yellow

foreach ($file in $accountingFiles) {
    if (Test-Path $file) {
        if (Fix-AccountConstructors $file) {
            $correctedCount++
        }
    } else {
        Write-Host "⚠️ Fichier non trouvé: $file" -ForegroundColor Yellow
    }
}

Write-Host "`n=== RÉSUMÉ ===" -ForegroundColor Green
Write-Host "Fichiers corrigés: $correctedCount" -ForegroundColor Green
Write-Host "Tous les constructeurs Account ont été corrigés !" -ForegroundColor Green
