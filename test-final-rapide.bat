@echo off
echo ========================================
echo TEST FINAL RAPIDE
echo ========================================
echo.

echo Test des endpoints après redémarrage :
echo.

echo 1. Contrôleur simple :
curl -s http://localhost:8080/api/test-simple/accounting
echo.
echo.

echo 2. Écritures comptables :
curl -s http://localhost:8080/api/accounting/journal-entries
echo.
echo.

echo 3. Clients :
curl -s http://localhost:8080/api/third-parties/customers
echo.
echo.

echo 4. Employés :
curl -s http://localhost:8080/api/hr/employees
echo.
echo.

echo ========================================
echo RÉSULTAT
echo ========================================
echo.
echo Si vous voyez des données JSON ci-dessus,
echo alors testez dans votre navigateur :
echo.
echo - http://localhost:4200/accounting/journal-entries
echo - http://localhost:4200/hr/employees
echo - http://localhost:4200/third-parties/customers
echo.

pause
