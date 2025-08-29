@echo off
echo ========================================
echo TEST RAPIDE - DIAGNOSTIC
echo ========================================
echo.

echo 1. Test du backend de base...
curl -s http://localhost:8080/api/health
echo.
echo.

echo 2. Test du contrôleur simple...
curl -s http://localhost:8080/api/test-simple/accounting
echo.
echo.

echo 3. Test des endpoints problématiques...
echo.

echo Test AccountingJournalEntryController :
curl -s http://localhost:8080/api/accounting/journal-entries
echo.
echo.

echo Test CustomerController :
curl -s http://localhost:8080/api/third-parties/customers
echo.
echo.

echo Test EmployeeController :
curl -s http://localhost:8080/api/hr/employees
echo.
echo.

echo ========================================
echo DIAGNOSTIC
echo ========================================
echo.
echo Si le contrôleur simple fonctionne mais pas les autres,
echo alors le problème vient des contrôleurs spécifiques.
echo.
echo Si rien ne fonctionne, le problème vient du backend.
echo.

pause
