@echo off
echo ========================================
echo TEST MODULE 16 - FONCTIONNALITES AVANCEES
echo ========================================
echo.

echo [1/8] Test des endpoints de generation d'ecritures comptables...
echo.

echo --- Test generation ecriture acquisition immobilisation ---
curl -X POST http://localhost:8080/api/asset-inventory-advanced/test-asset-acquisition-entry -H "Content-Type: application/json"
echo.
echo.

echo --- Test generation ecriture depreciation immobilisation ---
curl -X POST http://localhost:8080/api/asset-inventory-advanced/generate-asset-depreciation-entry -H "Content-Type: application/json" -d "{\"assetId\": 1, \"depreciationAmount\": 1000.00, \"companyId\": 1, \"countryCode\": \"CMR\", \"accountingStandard\": \"SYSCOHADA\"}"
echo.
echo.

echo --- Test generation ecriture mouvement inventaire ---
curl -X POST http://localhost:8080/api/asset-inventory-advanced/generate-inventory-movement-entry -H "Content-Type: application/json" -d "{\"movementId\": 1, \"companyId\": 1, \"countryCode\": \"CMR\", \"accountingStandard\": \"SYSCOHADA\"}"
echo.
echo.

echo [2/8] Test des endpoints d'analyse d'inventaire...
echo.

echo --- Test creation analyse inventaire ---
curl -X POST http://localhost:8080/api/asset-inventory-advanced/test-inventory-analysis -H "Content-Type: application/json"
echo.
echo.

echo --- Test creation analyse inventaire personnalisee ---
curl -X POST http://localhost:8080/api/asset-inventory-advanced/create-inventory-analysis -H "Content-Type: application/json" -d "{\"companyId\": 1, \"analysisType\": \"COMPREHENSIVE\", \"countryCode\": \"CMR\", \"accountingStandard\": \"SYSCOHADA\"}"
echo.
echo.

echo --- Test execution analyse inventaire (ID: 1) ---
curl -X POST http://localhost:8080/api/asset-inventory-advanced/perform-inventory-analysis/1 -H "Content-Type: application/json"
echo.
echo.

echo --- Test generation ecritures de correction (ID: 1) ---
curl -X POST http://localhost:8080/api/asset-inventory-advanced/generate-correction-entries/1 -H "Content-Type: application/json"
echo.
echo.

echo --- Test generation rapport d'analyse (ID: 1) ---
curl -X POST http://localhost:8080/api/asset-inventory-advanced/generate-analysis-report/1 -H "Content-Type: application/json"
echo.
echo.

echo [3/8] Test des endpoints de consultation des analyses...
echo.

echo --- Test recuperation analyses par entreprise ---
curl -X GET http://localhost:8080/api/asset-inventory-advanced/analyses/1
echo.
echo.

echo --- Test recuperation analyse par ID ---
curl -X GET http://localhost:8080/api/asset-inventory-advanced/analysis/1
echo.
echo.

echo --- Test recuperation details d'analyse ---
curl -X GET http://localhost:8080/api/asset-inventory-advanced/analysis-details/1
echo.
echo.

echo [4/8] Test des endpoints de gestion des documents...
echo.

echo --- Test creation document ---
curl -X POST http://localhost:8080/api/asset-inventory-advanced/test-document-creation -H "Content-Type: application/json"
echo.
echo.

echo --- Test creation document personnalise ---
curl -X POST http://localhost:8080/api/asset-inventory-advanced/create-document -H "Content-Type: application/json" -d "{\"companyId\": 1, \"documentType\": \"ASSET_PURCHASE\", \"title\": \"Facture acquisition machine\", \"relatedEntityType\": \"ASSET\", \"relatedEntityId\": 1, \"relatedEntityCode\": \"ASSET-001\", \"countryCode\": \"CMR\", \"accountingStandard\": \"SYSCOHADA\"}"
echo.
echo.

echo --- Test attachement fichier au document (ID: 1) ---
curl -X POST http://localhost:8080/api/asset-inventory-advanced/attach-file/1 -H "Content-Type: application/json" -d "{\"filePath\": \"/documents/facture.pdf\", \"fileType\": \"PDF\", \"fileSize\": 1024000, \"originalFileName\": \"facture_acquisition.pdf\"}"
echo.
echo.

echo --- Test validation document (ID: 1) ---
curl -X POST http://localhost:8080/api/asset-inventory-advanced/validate-document/1 -H "Content-Type: application/json" -d "{\"validatedBy\": 1}"
echo.
echo.

echo --- Test archivage document (ID: 1) ---
curl -X POST http://localhost:8080/api/asset-inventory-advanced/archive-document/1 -H "Content-Type: application/json" -d "{\"archivedBy\": 1}"
echo.
echo.

echo [5/8] Test des endpoints de consultation des documents...
echo.

echo --- Test recuperation documents par entreprise ---
curl -X GET http://localhost:8080/api/asset-inventory-advanced/documents/1
echo.
echo.

echo --- Test recuperation documents par type ---
curl -X GET http://localhost:8080/api/asset-inventory-advanced/documents/1/type/ASSET_PURCHASE
echo.
echo.

echo --- Test recuperation documents par entite ---
curl -X GET http://localhost:8080/api/asset-inventory-advanced/documents/1/entity/1/ASSET
echo.
echo.

echo --- Test recuperation documents non rapproches ---
curl -X GET http://localhost:8080/api/asset-inventory-advanced/documents/1/unreconciled
echo.
echo.

echo --- Test recuperation documents avec ecritures comptables ---
curl -X GET http://localhost:8080/api/asset-inventory-advanced/documents/1/with-accounting-entries
echo.
echo.

echo [6/8] Test des endpoints de statistiques...
echo.

echo --- Test statistiques documents ---
curl -X GET http://localhost:8080/api/asset-inventory-advanced/document-statistics/1
echo.
echo.

echo [7/8] Test des endpoints de base du Module 16...
echo.

echo --- Test recuperation immobilisations ---
curl -X GET http://localhost:8080/api/asset-inventory/assets/1
echo.
echo.

echo --- Test recuperation inventaires ---
curl -X GET http://localhost:8080/api/asset-inventory/inventories/1
echo.
echo.

echo --- Test recuperation mouvements ---
curl -X GET http://localhost:8080/api/asset-inventory/movements/1
echo.
echo.

echo [8/8] Test des endpoints de statistiques du Module 16...
echo.

echo --- Test statistiques immobilisations ---
curl -X GET http://localhost:8080/api/asset-inventory/assets/1/statistics
echo.
echo.

echo --- Test statistiques inventaires ---
curl -X GET http://localhost:8080/api/asset-inventory/inventories/1/statistics
echo.
echo.

echo --- Test statistiques mouvements ---
curl -X GET http://localhost:8080/api/asset-inventory/movements/1/statistics
echo.
echo.

echo ========================================
echo FIN DES TESTS MODULE 16 AVANCE
echo ========================================
echo.
echo Tests termines. Verifiez les reponses ci-dessus.
echo.
pause


