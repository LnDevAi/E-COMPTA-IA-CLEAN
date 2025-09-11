# Script pour corriger les documents spécifiques de l'image
Write-Host "Correction des documents specifiques de l'image..." -ForegroundColor Green

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
    
    # 2. Corriger les lignes multiples vides
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
    
    # 8. Corriger les URLs nues
    $content = $content -replace '(?<!\])\(https?://[^\s\)]+\)', '<$1>'
    
    # 9. Corriger les blocs de code sans langage
    $content = $content -replace '(\n|^)```(\n|$)', '$1```text$2'
    
    # Sauvegarder seulement si des changements ont ete faits
    if ($content -ne $originalContent) {
        Set-Content $FilePath -Value $content -Encoding UTF8 -NoNewline
        Write-Host "Corrige: $FilePath" -ForegroundColor Green
    } else {
        Write-Host "Aucun changement necessaire: $FilePath" -ForegroundColor Blue
    }
}

# Documents specifiques de l'image avec leurs compteurs d'erreurs
$documents = @{
    ".github\workflows\frontend-ci.yml" = 1
    "backend\docs\IMPLEMENTATION_COMPLETE_SYCEBNL.md" = 68
    "backend\docs\PIECE_JUSTIFICATIVE_SYCEBNL.md" = 11
    "backend\GOVERNMENT_PLATFORMS_README.md" = 63
    "DEPLOYMENT.md" = 30
    "docs\bonnes ressources\cursor_analyse_du_backend_pour_l_authen.md" = 1001
    "docs\CRM\crm_digital_marketing_doc.md" = 17
    "docs\CRM\MODULE_CRM_DGITALMARKETING_SMS_cursor_instructions_complete.md" = 75
    "docs\FONCTIONNALITE_PIECE_JUSTIFICATIVE_COMPTABLE.md" = 36
    "FRONTEND_ANALYSIS_AND_PLAN.md" = 112
    "GUIDE_DEPLOIEMENT_DOCKER.md" = 48
    "README-PODMAN-DEPLOYMENT.md" = 47
    "smt_financial_statements_module.md" = 107
    "subscription_system_module.md" = 138
}

Write-Host "Documents a corriger (total: $($documents.Count)):" -ForegroundColor Cyan
foreach ($doc in $documents.GetEnumerator()) {
    Write-Host "  - $($doc.Key): $($doc.Value) erreurs" -ForegroundColor White
}

Write-Host "`nDebut de la correction..." -ForegroundColor Yellow

# Corriger chaque document
foreach ($doc in $documents.GetEnumerator()) {
    $filePath = $doc.Key
    $errorCount = $doc.Value
    
    Write-Host "`n[$errorCount erreurs] Correction de: $filePath" -ForegroundColor Magenta
    
    if ($filePath -like "*.md") {
        Fix-MarkdownFile $filePath
    } elseif ($filePath -like "*.yml" -or $filePath -like "*.yaml") {
        # Pour les fichiers YAML, on fait juste une correction basique
        if (Test-Path $filePath) {
            $content = Get-Content $filePath -Raw -Encoding UTF8
            $originalContent = $content
            
            # Corriger les espaces en fin de ligne
            $content = $content -replace ' +$', ''
            
            if ($content -ne $originalContent) {
                Set-Content $filePath -Value $content -Encoding UTF8 -NoNewline
                Write-Host "Corrige (YAML): $filePath" -ForegroundColor Green
            } else {
                Write-Host "Aucun changement necessaire (YAML): $filePath" -ForegroundColor Blue
            }
        } else {
            Write-Host "Fichier YAML non trouve: $filePath" -ForegroundColor Red
        }
    }
}

Write-Host "`nCorrection terminee !" -ForegroundColor Green
Write-Host "Verification des erreurs restantes..." -ForegroundColor Cyan

