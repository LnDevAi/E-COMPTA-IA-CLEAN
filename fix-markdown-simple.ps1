# Script PowerShell simple pour corriger les erreurs Markdown
Write-Host "Correction automatique des erreurs Markdown..." -ForegroundColor Green

# Fonction pour corriger un fichier Markdown
function Fix-MarkdownFile {
    param([string]$FilePath)
    
    if (-not (Test-Path $FilePath)) {
        Write-Host "Fichier non trouve: $FilePath" -ForegroundColor Red
        return
    }
    
    Write-Host "Correction de: $FilePath" -ForegroundColor Yellow
    
    $content = Get-Content $FilePath -Raw -Encoding UTF8
    $originalContent = $content
    
    # 1. Corriger les espaces en fin de ligne
    $content = $content -replace ' +$', ''
    
    # 2. Corriger les lignes multiples vides - garder seulement une ligne vide
    $content = $content -replace '\n\n\n+', "`n`n"
    
    # 3. Ajouter des lignes vides autour des titres
    $content = $content -replace '(\n|^)(#{1,6}[^#\n].*?)(\n[^#\n])', '$1$2`n$3'
    $content = $content -replace '(\n[^#\n].*?)(\n#{1,6}[^#\n])', '$1`n$2'
    
    # 4. Ajouter des lignes vides autour des listes
    $content = $content -replace '(\n|^)([ \t]*[-*+][ \t].*?)(\n[^-*+\s])', '$1$2`n$3'
    $content = $content -replace '(\n[^-*+\s].*?)(\n[ \t]*[-*+][ \t])', '$1`n$2'
    
    # 5. Ajouter des lignes vides autour des blocs de code
    $content = $content -replace '(\n|^)(```.*?```)(\n[^`\n])', '$1$2`n$3'
    $content = $content -replace '(\n[^`\n].*?)(\n```)', '$1`n$2'
    
    # 6. Corriger la ponctuation en fin de titre
    $content = $content -replace '(#{1,6}[^#\n]*?)[:;.,!?]+(\n|$)', '$1$2'
    
    # 7. Ajouter une nouvelle ligne a la fin
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

# Liste des fichiers Markdown a corriger
$markdownFiles = @(
    "README-PODMAN-DEPLOYMENT.md",
    "DEPLOYMENT.md", 
    "GUIDE_DEPLOIEMENT_DOCKER.md",
    "backend/docs/PIECE_JUSTIFICATIVE_SYCEBNL.md",
    "backend/docs/IMPLEMENTATION_COMPLETE_SYCEBNL.md",
    "docs/FONCTIONNALITE_PIECE_JUSTIFICATIVE_COMPTABLE.md",
    "smt_financial_statements_module.md",
    "subscription_system_module.md",
    "FRONTEND_ANALYSIS_AND_PLAN.md",
    "backend/GOVERNMENT_PLATFORMS_README.md"
)

# Corriger chaque fichier
foreach ($file in $markdownFiles) {
    Fix-MarkdownFile $file
}

Write-Host "Correction terminee !" -ForegroundColor Green

