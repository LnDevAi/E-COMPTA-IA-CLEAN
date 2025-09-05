# Script de test complet du backend E-COMPTA-IA
# Teste tous les endpoints et fonctionnalités

Write-Host "🚀 DÉMARRAGE DES TESTS COMPLETS DU BACKEND E-COMPTA-IA" -ForegroundColor Green
Write-Host "=================================================" -ForegroundColor Green

# Configuration
$BASE_URL = "http://localhost:8082"
$API_URL = "$BASE_URL/api"

# Fonction de test
function Test-Endpoint {
    param(
        [string]$Url,
        [string]$Method = "GET",
        [string]$Description
    )
    
    Write-Host "`n📋 Test: $Description" -ForegroundColor Yellow
    Write-Host "   URL: $Url" -ForegroundColor Gray
    
    try {
        $response = Invoke-RestMethod -Uri $Url -Method $Method -TimeoutSec 10
        Write-Host "   ✅ SUCCESS" -ForegroundColor Green
        Write-Host "   Response: $($response | ConvertTo-Json -Compress)" -ForegroundColor Gray
        return $true
    }
    catch {
        Write-Host "   ❌ FAILED" -ForegroundColor Red
        Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
        return $false
    }
}

# Attendre que le backend soit prêt
Write-Host "`n⏳ Attente du démarrage du backend..." -ForegroundColor Blue
Start-Sleep -Seconds 5

# Tests de base
Write-Host "`n🔍 TESTS DE BASE" -ForegroundColor Cyan
$basicTests = @(
    @{Url="$API_URL/test"; Description="Test de base du backend"},
    @{Url="$API_URL/test/health"; Description="Test de santé"},
    @{Url="$API_URL/test/endpoints"; Description="Test des endpoints disponibles"},
    @{Url="$API_URL/test/database"; Description="Test de la base de données"}
)

$basicResults = @()
foreach ($test in $basicTests) {
    $result = Test-Endpoint -Url $test.Url -Description $test.Description
    $basicResults += $result
}

# Tests d'authentification
Write-Host "`n🔐 TESTS D'AUTHENTIFICATION" -ForegroundColor Cyan
$authTests = @(
    @{Url="$API_URL/auth/test"; Description="Test endpoint d'authentification"}
)

$authResults = @()
foreach ($test in $authTests) {
    $result = Test-Endpoint -Url $test.Url -Description $test.Description
    $authResults += $result
}

# Tests des modules principaux
Write-Host "`n📊 TESTS DES MODULES PRINCIPAUX" -ForegroundColor Cyan
$moduleTests = @(
    @{Url="$API_URL/dashboard/test"; Description="Test du module Dashboard"},
    @{Url="$API_URL/accounting/test"; Description="Test du module Comptabilité"},
    @{Url="$API_URL/hr/test"; Description="Test du module RH"},
    @{Url="$API_URL/third-parties/test"; Description="Test du module Tiers"},
    @{Url="$API_URL/assets/test"; Description="Test du module Actifs"}
)

$moduleResults = @()
foreach ($test in $moduleTests) {
    $result = Test-Endpoint -Url $test.Url -Description $test.Description
    $moduleResults += $result
}

# Résumé des résultats
Write-Host "`n📈 RÉSUMÉ DES TESTS" -ForegroundColor Magenta
Write-Host "===================" -ForegroundColor Magenta

$totalBasic = $basicResults.Count
$passedBasic = ($basicResults | Where-Object { $_ -eq $true }).Count
$totalAuth = $authResults.Count
$passedAuth = ($authResults | Where-Object { $_ -eq $true }).Count
$totalModule = $moduleResults.Count
$passedModule = ($moduleResults | Where-Object { $_ -eq $true }).Count

Write-Host "Tests de base: $passedBasic/$totalBasic" -ForegroundColor $(if($passedBasic -eq $totalBasic) {"Green"} else {"Yellow"})
Write-Host "Tests d'authentification: $passedAuth/$totalAuth" -ForegroundColor $(if($passedAuth -eq $totalAuth) {"Green"} else {"Yellow"})
Write-Host "Tests des modules: $passedModule/$totalModule" -ForegroundColor $(if($passedModule -eq $totalModule) {"Green"} else {"Yellow"})

$totalTests = $totalBasic + $totalAuth + $totalModule
$totalPassed = $passedBasic + $passedAuth + $passedModule

Write-Host "`n🎯 RÉSULTAT GLOBAL: $totalPassed/$totalTests" -ForegroundColor $(if($totalPassed -eq $totalTests) {"Green"} else {"Yellow"})

if ($totalPassed -eq $totalTests) {
    Write-Host "`n🎉 TOUS LES TESTS SONT PASSÉS !" -ForegroundColor Green
    Write-Host "Le backend E-COMPTA-IA est opérationnel !" -ForegroundColor Green
} else {
    Write-Host "`n⚠️ CERTAINS TESTS ONT ÉCHOUÉ" -ForegroundColor Yellow
    Write-Host "Vérifiez les erreurs ci-dessus." -ForegroundColor Yellow
}

Write-Host "`n🏁 Tests terminés." -ForegroundColor Blue
