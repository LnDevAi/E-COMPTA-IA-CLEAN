# GUIDE DE TEST MODULE 16 - POSTMAN

## Configuration Postman
- **Base URL**: `http://localhost:8080`
- **Headers**: `Content-Type: application/json`

## 1. GÉNÉRATION D'ÉCRITURES COMPTABLES

### 1.1 Test Génération Écriture Acquisition Immobilisation
```
POST /api/asset-inventory-advanced/test-asset-acquisition-entry
```

### 1.2 Génération Écriture Acquisition Immobilisation
```
POST /api/asset-inventory-advanced/generate-asset-acquisition-entry
Body:
{
  "assetId": 1,
  "supplierCode": "SUP001",
  "supplierName": "Fournisseur Test",
  "companyId": 1,
  "countryCode": "CMR",
  "accountingStandard": "SYSCOHADA"
}
```

### 1.3 Génération Écriture Dépréciation Immobilisation
```
POST /api/asset-inventory-advanced/generate-asset-depreciation-entry
Body:
{
  "assetId": 1,
  "depreciationAmount": 1000.00,
  "companyId": 1,
  "countryCode": "CMR",
  "accountingStandard": "SYSCOHADA"
}
```

### 1.4 Génération Écriture Mouvement Inventaire
```
POST /api/asset-inventory-advanced/generate-inventory-movement-entry
Body:
{
  "movementId": 1,
  "companyId": 1,
  "countryCode": "CMR",
  "accountingStandard": "SYSCOHADA"
}
```

## 2. ANALYSE D'INVENTAIRE

### 2.1 Test Création Analyse Inventaire
```
POST /api/asset-inventory-advanced/test-inventory-analysis
```

### 2.2 Création Analyse Inventaire
```
POST /api/asset-inventory-advanced/create-inventory-analysis
Body:
{
  "companyId": 1,
  "analysisType": "COMPREHENSIVE",
  "countryCode": "CMR",
  "accountingStandard": "SYSCOHADA"
}
```

### 2.3 Exécution Analyse Inventaire
```
POST /api/asset-inventory-advanced/perform-inventory-analysis/{analysisId}
```

### 2.4 Génération Écritures de Correction
```
POST /api/asset-inventory-advanced/generate-correction-entries/{analysisId}
```

### 2.5 Génération Rapport d'Analyse
```
POST /api/asset-inventory-advanced/generate-analysis-report/{analysisId}
```

### 2.6 Consultation des Analyses
```
GET /api/asset-inventory-advanced/analyses/{companyId}
GET /api/asset-inventory-advanced/analysis/{analysisId}
GET /api/asset-inventory-advanced/analysis-details/{analysisId}
```

## 3. GESTION DES DOCUMENTS

### 3.1 Test Création Document
```
POST /api/asset-inventory-advanced/test-document-creation
```

### 3.2 Création Document
```
POST /api/asset-inventory-advanced/create-document
Body:
{
  "companyId": 1,
  "documentType": "ASSET_PURCHASE",
  "title": "Facture acquisition machine",
  "relatedEntityType": "ASSET",
  "relatedEntityId": 1,
  "relatedEntityCode": "ASSET-001",
  "countryCode": "CMR",
  "accountingStandard": "SYSCOHADA"
}
```

### 3.3 Attachement Fichier
```
POST /api/asset-inventory-advanced/attach-file/{documentId}
Body:
{
  "filePath": "/documents/facture.pdf",
  "fileType": "PDF",
  "fileSize": 1024000,
  "originalFileName": "facture_acquisition.pdf"
}
```

### 3.4 Validation Document
```
POST /api/asset-inventory-advanced/validate-document/{documentId}
Body:
{
  "validatedBy": 1
}
```

### 3.5 Archivage Document
```
POST /api/asset-inventory-advanced/archive-document/{documentId}
Body:
{
  "archivedBy": 1
}
```

### 3.6 Consultation des Documents
```
GET /api/asset-inventory-advanced/documents/{companyId}
GET /api/asset-inventory-advanced/documents/{companyId}/type/{documentType}
GET /api/asset-inventory-advanced/documents/{companyId}/entity/{entityId}/{entityType}
GET /api/asset-inventory-advanced/documents/{companyId}/unreconciled
GET /api/asset-inventory-advanced/documents/{companyId}/with-accounting-entries
```

## 4. STATISTIQUES

### 4.1 Statistiques Documents
```
GET /api/asset-inventory-advanced/document-statistics/{companyId}
```

## 5. ENDPOINTS DE BASE DU MODULE 16

### 5.1 Immobilisations
```
GET /api/asset-inventory/assets/{companyId}
GET /api/asset-inventory/assets/{companyId}/statistics
```

### 5.2 Inventaires
```
GET /api/asset-inventory/inventories/{companyId}
GET /api/asset-inventory/inventories/{companyId}/statistics
```

### 5.3 Mouvements
```
GET /api/asset-inventory/movements/{companyId}
GET /api/asset-inventory/movements/{companyId}/statistics
```

## SÉQUENCE DE TEST RECOMMANDÉE

1. **Tests de base** (endpoints de base du Module 16)
2. **Tests de génération d'écritures comptables**
3. **Tests d'analyse d'inventaire**
4. **Tests de gestion des documents**
5. **Tests de statistiques**

## NOTES IMPORTANTES

- Tous les endpoints sont configurés pour accepter les requêtes CORS
- Les IDs utilisés dans les exemples (1) sont à adapter selon vos données
- Les réponses incluent des codes de statut HTTP appropriés
- Les erreurs sont gérées avec des messages explicites
- Tous les endpoints supportent le standard comptable SYSCOHADA

## CODES DE RÉPONSE ATTENDUS

- **200**: Succès
- **201**: Créé avec succès
- **400**: Erreur de requête
- **404**: Ressource non trouvée
- **500**: Erreur serveur interne


