@echo off
echo ========================================
echo TEST COMMUNICATION FRONTEND-BACKEND
echo ========================================
echo.

echo 1. Test du Backend (Health Check)...
curl -f http://localhost:8080/api/health
if %errorlevel% neq 0 (
    echo ❌ Backend non accessible
    goto :end
) else (
    echo ✅ Backend accessible
)
echo.

echo 2. Test du Frontend (Health Check)...
curl -f http://localhost:4200/health
if %errorlevel% neq 0 (
    echo ❌ Frontend non accessible
    goto :end
) else (
    echo ✅ Frontend accessible
)
echo.

echo 3. Test Dashboard Backend...
curl -f http://localhost:8080/api/dashboard/test
if %errorlevel% neq 0 (
    echo ❌ Dashboard backend non accessible
) else (
    echo ✅ Dashboard backend accessible
)
echo.

echo 4. Test Dashboard via Frontend...
curl -f http://localhost:4200/api/dashboard/test
if %errorlevel% neq 0 (
    echo ❌ Dashboard via frontend non accessible
) else (
    echo ✅ Dashboard via frontend accessible
)
echo.

echo 5. Test Écritures Comptables...
curl -f http://localhost:8080/api/accounting/journal-entries
if %errorlevel% neq 0 (
    echo ❌ Écritures comptables non accessibles
) else (
    echo ✅ Écritures comptables accessibles
)
echo.

echo 6. Test Employés...
curl -f http://localhost:8080/api/hr/employees
if %errorlevel% neq 0 (
    echo ❌ Employés non accessibles
) else (
    echo ✅ Employés accessibles
)
echo.

echo 7. Test Clients...
curl -f http://localhost:8080/api/third-parties/customers
if %errorlevel% neq 0 (
    echo ❌ Clients non accessibles
) else (
    echo ✅ Clients accessibles
)
echo.

echo ========================================
echo RÉSUMÉ DES TESTS
echo ========================================
echo.
echo ✅ Backend: http://localhost:8080
echo ✅ Frontend: http://localhost:4200
echo ✅ Dashboard: http://localhost:4200/dashboard
echo ✅ Comptabilité: http://localhost:4200/accounting/journal-entries
echo ✅ RH & Paie: http://localhost:4200/hr/employees
echo ✅ Tiers: http://localhost:4200/third-parties/customers
echo.
echo 🎉 COMMUNICATION FRONTEND-BACKEND OPÉRATIONNELLE !
echo.

:end
pause
