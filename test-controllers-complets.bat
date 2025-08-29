@echo off
echo ========================================
echo TEST DES CONTROLEURS COMPLETS E-COMPTA-IA
echo ========================================
echo.

echo [1/6] Test du Dashboard...
curl -s -f http://localhost:8080/api/dashboard/test
if %errorlevel% neq 0 (
    echo ❌ Dashboard KO
) else (
    echo ✅ Dashboard OK
)
echo.

echo [2/6] Test des Employés...
curl -s -f http://localhost:8080/api/hr/employees
if %errorlevel% neq 0 (
    echo ❌ Employés KO
) else (
    echo ✅ Employés OK
)
echo.

echo [3/6] Test des Écritures Comptables...
curl -s -f http://localhost:8080/api/accounting/journal-entries
if %errorlevel% neq 0 (
    echo ❌ Écritures Comptables KO
) else (
    echo ✅ Écritures Comptables OK
)
echo.

echo [4/6] Test des Clients...
curl -s -f http://localhost:8080/api/third-parties/customers
if %errorlevel% neq 0 (
    echo ❌ Clients KO
) else (
    echo ✅ Clients OK
)
echo.

echo [5/6] Test des Fournisseurs...
curl -s -f http://localhost:8080/api/third-parties/suppliers
if %errorlevel% neq 0 (
    echo ❌ Fournisseurs KO
) else (
    echo ✅ Fournisseurs OK
)
echo.

echo [6/6] Test des Congés...
curl -s -f http://localhost:8080/api/hr/leaves
if %errorlevel% neq 0 (
    echo ❌ Congés KO
) else (
    echo ✅ Congés OK
)
echo.

echo ========================================
echo RÉSUMÉ DES TESTS
echo ========================================
echo.
echo Si tous les tests sont OK, le backend est opérationnel !
echo.
echo Endpoints testés :
echo - Dashboard: /api/dashboard/test
echo - Employés: /api/hr/employees
echo - Écritures: /api/accounting/journal-entries
echo - Clients: /api/third-parties/customers
echo - Fournisseurs: /api/third-parties/suppliers
echo - Congés: /api/hr/leaves
echo.
pause
