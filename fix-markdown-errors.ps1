# Script PowerShell pour corriger automatiquement les erreurs Markdown
# Corrige les problèmes de formatage dans tous les fichiers .md

Write-Host "🔧 Correction automatique des erreurs Markdown..." -ForegroundColor Green

# Fonction pour corriger un fichier Markdown
function Fix-MarkdownFile {
    param([string]$FilePath)
    
    if (-not (Test-Path $FilePath)) {
        Write-Host "❌ Fichier non trouvé: $FilePath" -ForegroundColor Red
        return
    }
    
    Write-Host "📝 Correction de: $FilePath" -ForegroundColor Yellow
    
    $content = Get-Content $FilePath -Raw -Encoding UTF8
    $originalContent = $content
    
    # 1. Corriger les espaces en fin de ligne (MD009)
    $content = $content -replace ' +$', ''
    
    # 2. Corriger les lignes multiples vides (MD012) - garder seulement une ligne vide
    $content = $content -replace '\n\n\n+', "`n`n"
    
    # 3. Ajouter des lignes vides autour des titres (MD022)
    $content = $content -replace '(\n|^)(#{1,6}[^#\n].*?)(\n[^#\n])', '$1$2`n$3'
    $content = $content -replace '(\n[^#\n].*?)(\n#{1,6}[^#\n])', '$1`n$2'
    
    # 4. Ajouter des lignes vides autour des listes (MD032)
    $content = $content -replace '(\n|^)([ \t]*[-*+][ \t].*?)(\n[^-*+\s])', '$1$2`n$3'
    $content = $content -replace '(\n[^-*+\s].*?)(\n[ \t]*[-*+][ \t])', '$1`n$2'
    
    # 5. Ajouter des lignes vides autour des blocs de code (MD031)
    $content = $content -replace '(\n|^)(```.*?```)(\n[^`\n])', '$1$2`n$3'
    $content = $content -replace '(\n[^`\n].*?)(\n```)', '$1`n$2'
    
    # 6. Corriger la ponctuation en fin de titre (MD026)
    $content = $content -replace '(#{1,6}[^#\n]*?)[:;.,!?]+(\n|$)', '$1$2'
    
    # 7. Ajouter un titre de niveau 1 au début si manquant (MD041)
    if ($content -notmatch '^# ') {
        $firstLine = ($content -split "`n")[0]
        if ($firstLine -notmatch '^#{1,6}') {
            $content = "# $firstLine`n`n" + ($content -split "`n", 2)[1]
        }
    }
    
    # 8. Ajouter une nouvelle ligne à la fin (MD047)
    if ($content -notmatch '\n$') {
        $content += "`n"
    }
    
    # 9. Corriger les URLs nues (MD034) - les entourer de < >
    $content = $content -replace '(?<!\])\(https?://[^\s\)]+\)', '<$1>'
    
    # 10. Corriger les blocs de code sans langage (MD040)
    $content = $content -replace '(\n|^)```(\n|$)', '$1```text$2'
    
    # Sauvegarder seulement si des changements ont été faits
    if ($content -ne $originalContent) {
        Set-Content $FilePath -Value $content -Encoding UTF8 -NoNewline
        Write-Host "✅ Corrigé: $FilePath" -ForegroundColor Green
    } else {
        Write-Host "ℹ️  Aucun changement nécessaire: $FilePath" -ForegroundColor Blue
    }
}

# Liste des fichiers Markdown à corriger
$markdownFiles = @(
    "README-PODMAN-DEPLOYMENT.md",
    "DEPLOYMENT.md", 
    "GUIDE_DEPLOIEMENT_DOCKER.md",
    "backend/docs/PIECE_JUSTIFICATIVE_SYCEBNL.md",
    "backend/docs/IMPLEMENTATION_COMPLETE_SYCEBNL.md",
    "docs/FONCTIONNALITE_PIECE_JUSTIFICATIVE_COMPTABLE.md",
    "smt_financial_statements_module.md",
    "subscription_system_module.md",
    "docs/bonnes ressources/cursor_analyse_du_backend_pour_l_authen.md",
    "FRONTEND_ANALYSIS_AND_PLAN.md",
    "backend/GOVERNMENT_PLATFORMS_README.md",
    "docs/CRM/MODULE_CRM_DGITALMARKETING_SMS_cursor_instructions_complete.md",
    "docs/CRM/crm_digital_marketing_doc.md"
)

# Corriger chaque fichier
foreach ($file in $markdownFiles) {
    Fix-MarkdownFile $file
}

Write-Host "`n🎉 Correction terminée !" -ForegroundColor Green
Write-Host "Vérifiez maintenant les erreurs avec: mvn clean compile" -ForegroundColor Cyan

