# Test simple de communication Dashboard
Write-Host "TEST DE COMMUNICATION DASHBOARD" -ForegroundColor Green
Write-Host "===============================" -ForegroundColor Green

$BACKEND_URL = "http://localhost:8080"
$FRONTEND_URL = "http://localhost:4200"

Write-Host "`n1. Test Backend Health:" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$BACKEND_URL/health" -TimeoutSec 5
    Write-Host "   ✅ Backend accessible" -ForegroundColor Green
    Write-Host "   Status: $($response.status)" -ForegroundColor White
} catch {
    Write-Host "   ❌ Backend inaccessible: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n2. Test Dashboard Endpoint:" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$BACKEND_URL/api/dashboard/test" -TimeoutSec 5
    Write-Host "   ✅ Dashboard endpoint accessible" -ForegroundColor Green
    Write-Host "   Message: $($response.message)" -ForegroundColor White
} catch {
    Write-Host "   ❌ Dashboard endpoint inaccessible: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n3. Test Financial KPIs:" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$BACKEND_URL/api/dashboard/financial/kpis?companyId=1" -TimeoutSec 5
    Write-Host "   ✅ Financial KPIs accessible" -ForegroundColor Green
    Write-Host "   Revenue: $($response.data.revenue)" -ForegroundColor White
} catch {
    Write-Host "   ❌ Financial KPIs inaccessible: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n4. Test Frontend:" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$FRONTEND_URL" -TimeoutSec 5
    Write-Host "   ✅ Frontend accessible" -ForegroundColor Green
    Write-Host "   Status Code: $($response.StatusCode)" -ForegroundColor White
} catch {
    Write-Host "   ❌ Frontend inaccessible: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`nLIENS A VERIFIER:" -ForegroundColor Cyan
Write-Host "=================" -ForegroundColor Cyan
Write-Host "Backend Health: $BACKEND_URL/health" -ForegroundColor White
Write-Host "Dashboard Test: $BACKEND_URL/api/dashboard/test" -ForegroundColor White
Write-Host "Financial KPIs: $BACKEND_URL/api/dashboard/financial/kpis?companyId=1" -ForegroundColor White
Write-Host "Frontend Home: $FRONTEND_URL" -ForegroundColor White
Write-Host "Dashboard Page: $FRONTEND_URL/dashboard" -ForegroundColor White

Write-Host "`nTest termine!" -ForegroundColor Green
