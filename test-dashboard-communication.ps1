# Script de test pour la communication Dashboard Frontend-Backend
# E-COMPTA-IA - Module Dashboard

Write-Host "🚀 TEST DE COMMUNICATION DASHBOARD FRONTEND-BACKEND" -ForegroundColor Green
Write-Host "=================================================" -ForegroundColor Green

# Configuration
$BACKEND_URL = "http://localhost:8080"
$FRONTEND_URL = "http://localhost:4200"
$COMPANY_ID = 1

Write-Host "`n📋 CONFIGURATION:" -ForegroundColor Yellow
Write-Host "Backend URL: $BACKEND_URL" -ForegroundColor White
Write-Host "Frontend URL: $FRONTEND_URL" -ForegroundColor White
Write-Host "Company ID: $COMPANY_ID" -ForegroundColor White

# Fonction pour tester un endpoint
function Test-Endpoint {
    param(
        [string]$Name,
        [string]$Url,
        [string]$Method = "GET"
    )
    
    Write-Host "`n🔍 Test: $Name" -ForegroundColor Cyan
    Write-Host "URL: $Url" -ForegroundColor Gray
    
    try {
        $response = Invoke-RestMethod -Uri $Url -Method $Method -TimeoutSec 10
        Write-Host "✅ SUCCESS" -ForegroundColor Green
        Write-Host "Response: $($response | ConvertTo-Json -Depth 2)" -ForegroundColor White
        return $true
    }
    catch {
        Write-Host "❌ ERROR: $($_.Exception.Message)" -ForegroundColor Red
        return $false
    }
}

# Tests des endpoints backend
Write-Host "`n🔧 TESTS BACKEND:" -ForegroundColor Yellow

$backendTests = @(
    @{ Name = "Health Check"; Url = "$BACKEND_URL/health" },
    @{ Name = "Test Endpoint"; Url = "$BACKEND_URL/api/test" },
    @{ Name = "Dashboard Test"; Url = "$BACKEND_URL/api/dashboard/test" },
    @{ Name = "Financial KPIs"; Url = "$BACKEND_URL/api/dashboard/financial/kpis?companyId=$COMPANY_ID" },
    @{ Name = "Operational Metrics"; Url = "$BACKEND_URL/api/dashboard/operational/metrics?companyId=$COMPANY_ID" },
    @{ Name = "System Performance"; Url = "$BACKEND_URL/api/dashboard/performance/system" },
    @{ Name = "Financial Dashboard"; Url = "$BACKEND_URL/api/dashboard/financial?companyId=$COMPANY_ID" },
    @{ Name = "System Overview"; Url = "$BACKEND_URL/api/system/overview" }
)

$backendSuccess = 0
$backendTotal = $backendTests.Count

foreach ($test in $backendTests) {
    if (Test-Endpoint -Name $test.Name -Url $test.Url) {
        $backendSuccess++
    }
    Start-Sleep -Seconds 1
}

# Tests des endpoints frontend
Write-Host "`n🌐 TESTS FRONTEND:" -ForegroundColor Yellow

$frontendTests = @(
    @{ Name = "Frontend Home"; Url = "$FRONTEND_URL" },
    @{ Name = "Dashboard Page"; Url = "$FRONTEND_URL/dashboard" }
)

$frontendSuccess = 0
$frontendTotal = $frontendTests.Count

foreach ($test in $frontendTests) {
    if (Test-Endpoint -Name $test.Name -Url $test.Url) {
        $frontendSuccess++
    }
    Start-Sleep -Seconds 1
}

# Résumé des tests
Write-Host "`n📊 RÉSUMÉ DES TESTS:" -ForegroundColor Yellow
Write-Host "===================" -ForegroundColor Yellow

Write-Host "`n🔧 Backend:" -ForegroundColor Cyan
Write-Host "  Succès: $backendSuccess/$backendTotal" -ForegroundColor White
Write-Host "  Taux de réussite: $([math]::Round(($backendSuccess / $backendTotal) * 100, 1))%" -ForegroundColor White

Write-Host "`n🌐 Frontend:" -ForegroundColor Cyan
Write-Host "  Succès: $frontendSuccess/$frontendTotal" -ForegroundColor White
Write-Host "  Taux de réussite: $([math]::Round(($frontendSuccess / $frontendTotal) * 100, 1))%" -ForegroundColor White

$totalSuccess = $backendSuccess + $frontendSuccess
$totalTests = $backendTotal + $frontendTotal

Write-Host "`n🎯 TOTAL:" -ForegroundColor Green
Write-Host "  Succès: $totalSuccess/$totalTests" -ForegroundColor White
Write-Host "  Taux de réussite global: $([math]::Round(($totalSuccess / $totalTests) * 100, 1))%" -ForegroundColor White

# Recommandations
Write-Host "`n💡 RECOMMANDATIONS:" -ForegroundColor Yellow
Write-Host "===================" -ForegroundColor Yellow

if ($backendSuccess -eq $backendTotal) {
    Write-Host "✅ Backend: Tous les endpoints fonctionnent correctement" -ForegroundColor Green
} else {
    Write-Host "⚠️  Backend: Certains endpoints ont des problèmes" -ForegroundColor Yellow
    Write-Host "   - Vérifiez que le backend est démarré" -ForegroundColor White
    Write-Host "   - Vérifiez les logs du backend" -ForegroundColor White
}

if ($frontendSuccess -eq $frontendTotal) {
    Write-Host "✅ Frontend: L'application est accessible" -ForegroundColor Green
} else {
    Write-Host "⚠️  Frontend: L'application n'est pas accessible" -ForegroundColor Yellow
    Write-Host "   - Vérifiez que le frontend est démarré (ng serve)" -ForegroundColor White
    Write-Host "   - Vérifiez le port 4200" -ForegroundColor White
}

if ($totalSuccess -eq $totalTests) {
    Write-Host "`n🎉 EXCELLENT! La communication frontend-backend fonctionne parfaitement!" -ForegroundColor Green
    Write-Host "   Vous pouvez maintenant tester l'interface utilisateur sur $FRONTEND_URL/dashboard" -ForegroundColor White
} else {
    Write-Host "`nDes corrections sont necessaires avant de pouvoir tester l'interface utilisateur" -ForegroundColor Yellow
}

Write-Host "`nTest termine!" -ForegroundColor Green
