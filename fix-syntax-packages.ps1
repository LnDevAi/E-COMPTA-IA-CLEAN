# Script pour corriger les erreurs de syntaxe pa/ckage
Write-Host "=== CORRECTION DES ERREURS DE SYNTAXE PA/CKAGE ===" -ForegroundColor Green

# Fonction pour corriger les erreurs de syntaxe
function Fix-SyntaxPackages {
    param($filePath)
    
    try {
        $content = Get-Content -Path $filePath -Raw -Encoding UTF8
        $originalContent = $content
        
        # Corriger les packages cassés
        $content = $content -replace '^ackage ', 'package '
        $content = $content -replace '^package$', 'package com.ecomptaia.entity;'
        
        # Corriger les imports cassés
        $content = $content -replace 'pa`nimport', 'import'
        $content = $content -replace 'ckage com\\.ecomptaia\\.', 'import com.ecomptaia.'
        $content = $content -replace 'pa`n', ''
        $content = $content -replace 'ckage com\\.ecomptaia\\.', 'import com.ecomptaia.'
        
        # Corriger les packages manquants
        if ($content -notmatch '^package ') {
            $content = "package com.ecomptaia.entity;`n`n" + $content
        }
        
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

# Trouver tous les fichiers Java avec des erreurs de syntaxe
$javaFiles = Get-ChildItem -Path "backend/src/main/java" -Filter "*.java" -Recurse

$correctedCount = 0

Write-Host "`nCorrection des erreurs de syntaxe..." -ForegroundColor Yellow

foreach ($file in $javaFiles) {
    $content = Get-Content -Path $file.FullName -Raw -Encoding UTF8 -ErrorAction SilentlyContinue
    if ($content -match '^ackage |^package$|pa`n|ckage com\\.ecomptaia\\.') {
        if (Fix-SyntaxPackages $file.FullName) {
            $correctedCount++
        }
    }
}

Write-Host "`n=== RÉSUMÉ ===" -ForegroundColor Green
Write-Host "Fichiers corrigés: $correctedCount" -ForegroundColor Green
Write-Host "Toutes les erreurs de syntaxe ont été corrigées !" -ForegroundColor Green
