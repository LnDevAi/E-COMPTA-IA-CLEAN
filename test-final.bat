@echo off
echo ========================================
echo TEST FINAL DES ENDPOINTS
echo ========================================
echo.

echo Attente du redémarrage du backend...
timeout /t 45 /nobreak > nul
echo.

echo Test des endpoints corrigés :
echo.

echo 1. Écritures comptables :
curl -s http://localhost:8080/api/accounting/journal-entries
echo.
echo.

echo 2. Employés :
curl -s http://localhost:8080/api/hr/employees
echo.
echo.

echo 3. Clients :
curl -s http://localhost:8080/api/third-parties/customers
echo.
echo.

echo ========================================
echo RÉSULTAT
echo ========================================
echo.
echo Si vous voyez des données JSON ci-dessus, 
echo alors les modules fonctionnent dans le navigateur !
echo.
echo Testez maintenant :
echo - http://localhost:4200/accounting/journal-entries
echo - http://localhost:4200/hr/employees
echo - http://localhost:4200/third-parties/customers
echo.

pause
