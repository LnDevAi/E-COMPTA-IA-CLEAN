# Script PowerShell pour corriger automatiquement les imports Angular manquants
# Révolutionnaire vs TOMPRO - Correction automatique des erreurs

Write-Host "🔧 Correction automatique des imports Angular manquants..." -ForegroundColor Green

# Liste des fichiers TypeScript à corriger
$files = @(
    "frontend/src/app/features/dashboard/dashboard.ts",
    "frontend/src/app/features/crm/clients/clients.ts",
    "frontend/src/app/features/crm/opportunities/opportunities.ts",
    "frontend/src/app/features/sycebnl/transactions/transactions.ts",
    "frontend/src/app/features/accounting/accounts/accounts.ts",
    "frontend/src/app/features/accounting/journal-entries/journal-entries.ts",
    "frontend/src/app/features/crm/prospects/prospects.ts",
    "frontend/src/app/features/sycebnl/organization-list/organization-list.ts",
    "frontend/src/app/features/inventory/inventory-list/inventory-list.ts",
    "frontend/src/app/features/crm/customer-detail/customer-detail.ts",
    "frontend/src/app/features/crm/customer-list/customer-list.ts",
    "frontend/src/app/features/payroll/employee-list/employee-list.ts",
    "frontend/src/app/features/payroll/payroll-calculations/payroll-calculations.ts",
    "frontend/src/app/features/accounting/chart-of-accounts/chart-of-accounts.ts",
    "frontend/src/app/features/sycebnl/organization-detail/organization-detail.ts",
    "frontend/src/app/features/analytics/dashboard-analytics/dashboard-analytics.ts",
    "frontend/src/app/features/integrations/bank-integration/bank-integration.ts",
    "frontend/src/app/features/reporting/report-builder/report-builder.ts",
    "frontend/src/app/features/integrations/tax-integration/tax-integration.ts",
    "frontend/src/app/features/workflow/approval-workflow/approval-workflow.ts",
    "frontend/src/app/shared/components/country-selector/country-selector.ts"
)

$fixedCount = 0
$errorCount = 0

foreach ($file in $files) {
    if (Test-Path $file) {
        Write-Host "🔍 Traitement de: $file" -ForegroundColor Yellow
        
        try {
            $content = Get-Content $file -Raw
            
            # Vérifier si le fichier utilise *ngIf ou *ngFor
            if ($content -match '\*ngIf|\*ngFor') {
                # Vérifier si NgIf et NgFor sont déjà importés
                if ($content -notmatch 'import.*NgIf|import.*NgFor') {
                    # Ajouter les imports manquants
                    if ($content -match 'import.*CommonModule') {
                        # Remplacer l'import CommonModule existant
                        $content = $content -replace 'import \{ CommonModule \}', 'import { CommonModule, NgIf, NgFor }'
                    } else {
                        # Ajouter un nouvel import
                        $content = $content -replace '(import.*from.*@angular/common.*;)', '$1' + "`nimport { NgIf, NgFor } from '@angular/common';"
                    }
                    
                    # Ajouter NgIf et NgFor dans le tableau imports
                    if ($content -match 'imports:\s*\[') {
                        $content = $content -replace '(imports:\s*\[)', '$1`n    NgIf,`n    NgFor,'
                    }
                    
                    # Sauvegarder le fichier modifié
                    Set-Content $file $content -NoNewline
                    Write-Host "✅ Corrigé: $file" -ForegroundColor Green
                    $fixedCount++
                } else {
                    Write-Host "ℹ️  Déjà corrigé: $file" -ForegroundColor Blue
                }
            } else {
                Write-Host "ℹ️  Pas de *ngIf/*ngFor: $file" -ForegroundColor Gray
            }
        }
        catch {
            Write-Host "❌ Erreur lors du traitement de: $file - $($_.Exception.Message)" -ForegroundColor Red
            $errorCount++
        }
    } else {
        Write-Host "⚠️  Fichier non trouvé: $file" -ForegroundColor Yellow
    }
}

Write-Host "`n📊 Résumé de la correction:" -ForegroundColor Cyan
Write-Host "✅ Fichiers corrigés: $fixedCount" -ForegroundColor Green
Write-Host "❌ Erreurs: $errorCount" -ForegroundColor Red
Write-Host "🎯 Total de fichiers traités: $($files.Count)" -ForegroundColor Blue

if ($fixedCount -gt 0) {
    Write-Host "`n🚀 Correction terminée avec succès !" -ForegroundColor Green
    Write-Host "💡 Vous pouvez maintenant compiler le projet sans erreurs d'imports Angular." -ForegroundColor Yellow
} else {
    Write-Host "`nℹ️  Aucune correction nécessaire." -ForegroundColor Blue
}

Write-Host "`n🔧 Script de correction automatique SYCEBNL - Révolutionnaire vs TOMPRO" -ForegroundColor Magenta








