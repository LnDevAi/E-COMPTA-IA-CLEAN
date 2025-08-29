@echo off
echo ========================================
echo REDÉMARRAGE ET TEST COMPLET
echo ========================================
echo.

echo 1. Arrêt du backend...
docker-compose -f docker/docker-compose.yml stop backend
echo.

echo 2. Redémarrage du backend...
docker-compose -f docker/docker-compose.yml up -d backend
echo.

echo 3. Attente du démarrage (30 secondes)...
timeout /t 30 /nobreak > nul
echo.

echo 4. Test du backend...
curl -f http://localhost:8080/api/health
if %errorlevel% neq 0 (
    echo ❌ Backend non accessible
    goto :end
) else (
    echo ✅ Backend accessible
)
echo.

echo 5. Test des endpoints corrigés...
echo.

echo Test Écritures Comptables...
curl -s http://localhost:8080/api/accounting/journal-entries | findstr "data"
if %errorlevel% equ 0 (
    echo ✅ Écritures comptables OK
) else (
    echo ❌ Écritures comptables KO
)
echo.

echo Test Employés...
curl -s http://localhost:8080/api/hr/employees | findstr "data"
if %errorlevel% equ 0 (
    echo ✅ Employés OK
) else (
    echo ❌ Employés KO
)
echo.

echo Test Clients...
curl -s http://localhost:8080/api/third-parties/customers | findstr "data"
if %errorlevel% equ 0 (
    echo ✅ Clients OK
) else (
    echo ❌ Clients KO
)
echo.

echo ========================================
echo RÉSULTAT FINAL
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
