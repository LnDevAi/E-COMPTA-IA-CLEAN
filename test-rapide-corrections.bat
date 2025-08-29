@echo off
echo ========================================
echo TEST RAPIDE DES CORRECTIONS E-COMPTA-IA
echo ========================================
echo.

echo Test du Dashboard...
curl -s -f http://localhost:8080/api/dashboard/test
if %errorlevel% neq 0 (
    echo ❌ Dashboard KO
) else (
    echo ✅ Dashboard OK
)
echo.

echo Test des Employés...
curl -s -f http://localhost:8080/api/hr/employees
if %errorlevel% neq 0 (
    echo ❌ Employés KO
) else (
    echo ✅ Employés OK
)
echo.

echo Test des Écritures Comptables...
curl -s -f http://localhost:8080/api/accounting/journal-entries
if %errorlevel% neq 0 (
    echo ❌ Écritures Comptables KO
) else (
    echo ✅ Écritures Comptables OK
)
echo.

echo Test des Clients...
curl -s -f http://localhost:8080/api/third-parties/customers
if %errorlevel% neq 0 (
    echo ❌ Clients KO
) else (
    echo ✅ Clients OK
)
echo.

echo Test des Fournisseurs...
curl -s -f http://localhost:8080/api/third-parties/suppliers
if %errorlevel% neq 0 (
    echo ❌ Fournisseurs KO
) else (
    echo ✅ Fournisseurs OK
)
echo.

echo Test des Congés...
curl -s -f http://localhost:8080/api/hr/leaves
if %errorlevel% neq 0 (
    echo ❌ Congés KO
) else (
    echo ✅ Congés OK
)
echo.

echo ========================================
echo RÉSUMÉ
echo ========================================
echo.
echo Test rapide terminé !
echo.
pause
