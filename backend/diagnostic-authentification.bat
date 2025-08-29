@echo off
echo ========================================
echo DIAGNOSTIC AUTHENTIFICATION - E-COMPTA-IA
echo ========================================
echo.

echo [1/8] Test de base de l'application...
curl -X GET "http://localhost:8080/api/test/public" -H "Content-Type: application/json"
echo.
echo.

echo [2/8] Test endpoint register (devrait fonctionner)...
curl -X POST "http://localhost:8080/api/auth/register" -H "Content-Type: application/json" -d "{\"email\": \"test@ecomptaia.com\", \"password\": \"test123\"}"
echo.
echo.

echo [3/8] Test login avec utilisateur inexistant...
curl -X POST "http://localhost:8080/api/auth/login" -H "Content-Type: application/json" -d "{\"email\": \"inexistant@test.com\", \"password\": \"test123\"}"
echo.
echo.

echo [4/8] Test login avec données valides...
curl -X POST "http://localhost:8080/api/auth/login" -H "Content-Type: application/json" -d "{\"email\": \"admin@ecomptaia.com\", \"password\": \"admin123\"}"
echo.
echo.

echo [5/8] Test login avec utilisateur créé...
curl -X POST "http://localhost:8080/api/auth/login" -H "Content-Type: application/json" -d "{\"email\": \"test@ecomptaia.com\", \"password\": \"test123\"}"
echo.
echo.

echo [6/8] Test accès H2 Console...
echo Vérifiez dans votre navigateur: http://localhost:8080/h2-console
echo JDBC URL: jdbc:h2:mem:testdb
echo Username: sa
echo Password: (vide)
echo.

echo [7/8] Test endpoint protégé sans token...
curl -X GET "http://localhost:8080/api/accounting/accounts" -H "Content-Type: application/json"
echo.
echo.

echo [8/8] Test avec token (si disponible)...
echo Si vous avez un token JWT, testez avec:
echo curl -X GET "http://localhost:8080/api/accounting/accounts" -H "Authorization: Bearer VOTRE_TOKEN_JWT"
echo.

echo ========================================
echo DIAGNOSTIC TERMINE
echo ========================================
echo.
echo PROCHAINES ETAPES:
echo 1. Vérifier les logs Spring Boot
echo 2. Accéder à H2 Console pour vérifier les tables
echo 3. Vérifier la configuration SecurityConfig.java
echo 4. Tester avec un utilisateur créé via register
echo.
pause


