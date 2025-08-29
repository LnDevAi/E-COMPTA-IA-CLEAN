@echo off
echo ========================================
echo TEST RAPIDE DES CORRECTIONS
echo ========================================
echo.

echo Test des endpoints corrigés...
echo.

echo 1. Test Écritures Comptables...
curl -s http://localhost:8080/api/accounting/journal-entries | findstr "data"
if %errorlevel% equ 0 (
    echo ✅ Écritures comptables OK
) else (
    echo ❌ Écritures comptables KO
)
echo.

echo 2. Test Employés...
curl -s http://localhost:8080/api/hr/employees | findstr "data"
if %errorlevel% equ 0 (
    echo ✅ Employés OK
) else (
    echo ❌ Employés KO
)
echo.

echo 3. Test Clients...
curl -s http://localhost:8080/api/third-parties/customers | findstr "data"
if %errorlevel% equ 0 (
    echo ✅ Clients OK
) else (
    echo ❌ Clients KO
)
echo.

echo ========================================
echo RÉSUMÉ
echo ========================================
echo.
echo Si tous les tests sont OK, redémarrez l'application :
echo docker-compose -f docker/docker-compose.yml restart backend
echo.
echo Puis testez dans votre navigateur :
echo - http://localhost:4200/accounting/journal-entries
echo - http://localhost:4200/hr/employees  
echo - http://localhost:4200/third-parties/customers
echo.

pause
