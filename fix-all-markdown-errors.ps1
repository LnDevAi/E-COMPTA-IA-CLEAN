# Script pour corriger automatiquement tous les erreurs Markdown
Write-Host "Correction automatique de tous les fichiers Markdown..." -ForegroundColor Green

# Fonction pour corriger un fichier
function Fix-MarkdownFile {
    param([string]$FilePath)
    
    if (-not (Test-Path $FilePath)) {
        Write-Host "Fichier non trouve: $FilePath" -ForegroundColor Red
        return
    }
    
    Write-Host "Correction de: $FilePath" -ForegroundColor Yellow
    
    $content = Get-Content $FilePath -Raw -Encoding UTF8
    $originalContent = $content
    
    # 1. Corriger les caracteres \n litteraux
    $content = $content -replace '`n`n`n+', "`n`n"
    $content = $content -replace '`n`n', "`n`n"
    $content = $content -replace '`n', "`n"
    
    # 2. Corriger les espaces en fin de ligne
    $content = $content -replace ' +$', ''
    
    # 3. Corriger les lignes multiples vides
    $content = $content -replace '\n\n\n+', "`n`n"
    
    # 4. Ajouter des lignes vides autour des titres
    $content = $content -replace '(\n|^)(#{1,6}[^#\n].*?)(\n[^#\n])', '$1$2`n$3'
    $content = $content -replace '(\n[^#\n].*?)(\n#{1,6}[^#\n])', '$1`n$2'
    
    # 5. Ajouter des lignes vides autour des listes
    $content = $content -replace '(\n|^)([ \t]*[-*+][ \t].*?)(\n[^-*+\s])', '$1$2`n$3'
    $content = $content -replace '(\n[^-*+\s].*?)(\n[ \t]*[-*+][ \t])', '$1`n$2'
    
    # 6. Ajouter des lignes vides autour des blocs de code
    $content = $content -replace '(\n|^)(```.*?```)(\n[^`\n])', '$1$2`n$3'
    $content = $content -replace '(\n[^`\n].*?)(\n```)', '$1`n$2'
    
    # 7. Corriger la ponctuation en fin de titre
    $content = $content -replace '(#{1,6}[^#\n]*?)[:;.,!?]+(\n|$)', '$1$2'
    
    # 8. Ajouter une nouvelle ligne a la fin
    if ($content -notmatch '\n$') {
        $content += "`n"
    }
    
    # Sauvegarder seulement si des changements ont ete faits
    if ($content -ne $originalContent) {
        Set-Content $FilePath -Value $content -Encoding UTF8 -NoNewline
        Write-Host "Corrige: $FilePath" -ForegroundColor Green
    } else {
        Write-Host "Aucun changement necessaire: $FilePath" -ForegroundColor Blue
    }
}

# Liste de tous les fichiers a corriger
$files = @(
    "subscription_system_module.md",
    "FRONTEND_ANALYSIS_AND_PLAN.md", 
    "smt_financial_statements_module.md",
    "backend/GOVERNMENT_PLATFORMS_README.md",
    "backend/docs/IMPLEMENTATION_COMPLETE_SYCEBNL.md",
    "docs/FONCTIONNALITE_PIECE_JUSTIFICATIVE_COMPTABLE.md",
    "backend/docs/PIECE_JUSTIFICATIVE_SYCEBNL.md",
    "GUIDE_DEPLOIEMENT_DOCKER.md",
    "README-PODMAN-DEPLOYMENT.md",
    "DEPLOYMENT.md",
    "docs/CRM/MODULE_CRM_DGITALMARKETING_SMS_cursor_instructions_complete.md",
    "docs/CRM/crm_digital_marketing_doc.md"
)

# Corriger chaque fichier
foreach ($file in $files) {
    Fix-MarkdownFile $file
}

Write-Host "`nCorrection terminee !" -ForegroundColor Green

