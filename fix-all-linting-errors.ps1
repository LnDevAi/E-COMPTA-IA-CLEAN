# Script pour corriger TOUTES les erreurs de linting
Write-Host "CORRECTION MASSIVE DES ERREURS DE LINTING" -ForegroundColor Green

# 1. Supprimer tous les BOM des fichiers Java
Write-Host "1. Suppression des BOM..." -ForegroundColor Yellow
Get-ChildItem -Path "backend/src" -Recurse -Filter "*.java" | ForEach-Object {
    $content = Get-Content $_.FullName -Raw -Encoding UTF8
    if ($content.StartsWith([char]0xFEFF)) {
        $content = $content.Substring(1)
        Set-Content $_.FullName -Value $content -Encoding UTF8 -NoNewline
        Write-Host "  BOM supprime: $($_.Name)" -ForegroundColor Green
    }
}

# 2. Corriger les packages cassés
Write-Host "2. Correction des packages cassés..." -ForegroundColor Yellow
Get-ChildItem -Path "backend/src" -Recurse -Filter "*.java" | ForEach-Object {
    $content = Get-Content $_.FullName -Raw -Encoding UTF8
    $modified = $false

    # Corriger les packages malformés
    $content = $content -replace 'pa\s+ckage\s+', 'package '
    $content = $content -replace 'pa\s*ckage\s+', 'package '
    $content = $content -replace 'package\s+pa\s*ckage\s+', 'package '

    # Corriger les imports cassés
    $content = $content -replace 'im\s*port\s+', 'import '
    $content = $content -replace 'im\s*port\s*;', 'import;'

    # Corriger les classes cassées
    $content = $content -replace 'cl\s*ass\s+', 'class '
    $content = $content -replace 'pu\s*blic\s+', 'public '
    $content = $content -replace 'pr\s*ivate\s+', 'private '
    $content = $content -replace 'pro\s*tected\s+', 'protected '

    if ($content -ne (Get-Content $_.FullName -Raw -Encoding UTF8)) {
        Set-Content $_.FullName -Value $content -Encoding UTF8 -NoNewline
        Write-Host "  Package corrige: $($_.Name)" -ForegroundColor Green
        $modified = $true
    }
}

# 3. Corriger les annotations @Query malformées
Write-Host "3. Correction des annotations @Query..." -ForegroundColor Yellow
Get-ChildItem -Path "backend/src" -Recurse -Filter "*.java" | ForEach-Object {
    $content = Get-Content $_.FullName -Raw -Encoding UTF8
    $modified = $false

    # Corriger les annotations cassées
    $content = $content -replace '@Q\s*uery\s*\(', '@Query('
    $content = $content -replace '@Q\s*uery\s*\(', '@Query('
    $content = $content -replace '@Q\s*uery\s*\(', '@Query('

    if ($content -ne (Get-Content $_.FullName -Raw -Encoding UTF8)) {
        Set-Content $_.FullName -Value $content -Encoding UTF8 -NoNewline
        Write-Host "  @Query corrige: $($_.Name)" -ForegroundColor Green
        $modified = $true
    }
}

# 4. Corriger les imports manquants
Write-Host "4. Correction des imports manquants..." -ForegroundColor Yellow
Get-ChildItem -Path "backend/src" -Recurse -Filter "*.java" | ForEach-Object {
    $content = Get-Content $_.FullName -Raw -Encoding UTF8
    $modified = $false

    # Ajouter les imports manquants courants
    if ($content -match 'Company' -and $content -notmatch 'import.*Company') {
        $content = $content -replace '(package.*?;)', "`$1`nimport com.ecomptaia.entity.Company;"
        $modified = $true
    }

    if ($content -match 'User' -and $content -notmatch 'import.*User') {
        $content = $content -replace '(package.*?;)', "`$1`nimport com.ecomptaia.entity.User;"
        $modified = $true
    }

    if ($modified) {
        Set-Content $_.FullName -Value $content -Encoding UTF8 -NoNewline
        Write-Host "  Imports ajoutes: $($_.Name)" -ForegroundColor Green
    }
}

Write-Host "CORRECTION TERMINEE !" -ForegroundColor Green
Write-Host "Verifiez maintenant le panel d'erreurs." -ForegroundColor Cyan
