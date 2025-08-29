@echo off
echo ========================================
echo VÉRIFICATION DES LOGS BACKEND E-COMPTA-IA
echo ========================================
echo.

echo [1/3] Vérification du statut des conteneurs...
cd docker
docker-compose ps
cd ..
echo.

echo [2/3] Logs du backend (dernières 50 lignes)...
cd docker
docker-compose logs --tail=50 backend
cd ..
echo.

echo [3/3] Test rapide des endpoints problématiques...
echo.

echo Test détaillé des Écritures Comptables...
curl -v http://localhost:8080/api/accounting/journal-entries
echo.
echo.

echo Test détaillé des Clients...
curl -v http://localhost:8080/api/third-parties/customers
echo.
echo.

echo Test détaillé des Fournisseurs...
curl -v http://localhost:8080/api/third-parties/suppliers
echo.

echo ========================================
echo FIN DE LA VÉRIFICATION
echo ========================================
pause
