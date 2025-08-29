@echo off
echo ========================================
echo TEST BACKEND CORRIGÉ
echo ========================================
echo.

echo 1. Test du backend (attente 60 secondes)...
timeout /t 60 /nobreak > nul
echo.

echo 2. Test de santé du backend...
curl -s http://localhost:8080/api/health
if %errorlevel% neq 0 (
    echo ❌ Backend non accessible
    goto :end
) else (
    echo ✅ Backend accessible
)
echo.

echo 3. Test des endpoints corrigés...
echo.

echo Test Écritures Comptables...
curl -s http://localhost:8080/api/accounting/journal-entries
echo.
echo.

echo Test Employés...
curl -s http://localhost:8080/api/hr/employees
echo.
echo.

echo Test Clients...
curl -s http://localhost:8080/api/third-parties/customers
echo.
echo.

echo ========================================
echo RÉSULTAT
echo ========================================
echo.
echo 🎉 MAINTENANT TESTEZ DANS VOTRE NAVIGATEUR :
echo.
echo ✅ Dashboard: http://localhost:4200/dashboard
echo ✅ Comptabilité: http://localhost:4200/accounting/journal-entries
echo ✅ RH & Paie: http://localhost:4200/hr/employees
echo ✅ Tiers: http://localhost:4200/third-parties/customers
echo.
echo LES MODULES NE DEVRAIENT PLUS AFFICHER "EN DÉVELOPPEMENT" !
echo.

:end
pause
