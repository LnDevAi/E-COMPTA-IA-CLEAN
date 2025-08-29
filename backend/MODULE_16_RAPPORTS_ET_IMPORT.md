# Module 16 - Rapports et Import de Données
## Gestion des Immobilisations et des Stocks

### 📊 Vue d'ensemble

Ce module étend les fonctionnalités du Module 16 avec un système complet de rapports, tableaux de bord et d'import de données pour les immobilisations et stocks.

---

## 🎯 Fonctionnalités Principales

### 1. **Rapports d'Immobilisations**
- Rapport d'état des immobilisations
- Rapport de dépréciation avec plan d'amortissement
- Analyse des immobilisations critiques
- Top 10 des immobilisations par valeur

### 2. **Rapports d'Inventaire**
- Rapport d'état des stocks
- Rapport de mouvements de stock
- Analyse des produits en rupture
- Produits avec stock faible

### 3. **Tableaux de Bord**
- KPIs des immobilisations et stocks
- Alertes et notifications
- Graphiques de distribution
- Vue d'ensemble complète

### 4. **Import de Données**
- Import CSV pour immobilisations
- Import CSV pour inventaires
- Import CSV pour mouvements
- Validation automatique des fichiers
- Templates téléchargeables

---

## 📋 Endpoints Disponibles

### 🔍 RAPPORTS D'IMMOBILISATIONS

#### 1. Rapport d'état des immobilisations
```http
GET /api/asset-inventory-reports/assets/status-report
```
**Paramètres :**
- `companyId` (Long) : ID de l'entreprise
- `countryCode` (String) : Code pays (ex: "CMR")
- `accountingStandard` (String) : Standard comptable (ex: "SYSCOHADA")

**Réponse :**
```json
{
  "reportType": "ASSET_STATUS_REPORT",
  "generatedAt": "2024-01-15T10:30:00",
  "totalAssets": 25,
  "totalPurchaseValue": 15000000,
  "totalCurrentValue": 12000000,
  "totalDepreciation": 3000000,
  "statusDistribution": {
    "ACTIVE": 20,
    "INACTIVE": 5
  },
  "typeDistribution": {
    "MACHINERY": 10,
    "VEHICLE": 5,
    "BUILDING": 10
  },
  "topAssetsByValue": [...],
  "criticalAssets": [...]
}
```

#### 2. Rapport de dépréciation
```http
GET /api/asset-inventory-reports/assets/depreciation-report
```
**Paramètres :** Identiques au rapport d'état

**Réponse :**
```json
{
  "reportType": "DEPRECIATION_REPORT",
  "totalDepreciation": 3000000,
  "totalAnnualDepreciation": 500000,
  "depreciationByType": {
    "MACHINERY": 1500000,
    "VEHICLE": 1000000,
    "BUILDING": 500000
  },
  "depreciationSchedule": [...]
}
```

### 📦 RAPPORTS D'INVENTAIRE

#### 3. Rapport d'état des stocks
```http
GET /api/asset-inventory-reports/inventory/status-report
```
**Paramètres :** Identiques aux rapports d'immobilisations

**Réponse :**
```json
{
  "reportType": "INVENTORY_STATUS_REPORT",
  "totalProducts": 150,
  "totalQuantity": 5000,
  "totalValue": 25000000,
  "statusDistribution": {
    "ACTIVE": 140,
    "INACTIVE": 10
  },
  "categoryDistribution": {
    "MATERIEL": 80,
    "PRODUITS": 70
  },
  "outOfStockProducts": [...],
  "lowStockProducts": [...],
  "topProductsByValue": [...]
}
```

#### 4. Rapport de mouvements de stock
```http
GET /api/asset-inventory-reports/inventory/movement-report
```
**Paramètres :**
- `companyId` (Long) : ID de l'entreprise
- `startDate` (String) : Date de début (format: yyyy-MM-dd)
- `endDate` (String) : Date de fin (format: yyyy-MM-dd)

**Réponse :**
```json
{
  "reportType": "INVENTORY_MOVEMENT_REPORT",
  "period": "2024-01-01 à 2024-01-31",
  "totalMovements": 250,
  "totalInValue": 15000000,
  "totalOutValue": 12000000,
  "netMovement": 3000000,
  "movementTypeCount": {
    "IN": 150,
    "OUT": 100
  },
  "movementsByDate": {...}
}
```

### 📊 TABLEAUX DE BORD

#### 5. Tableau de bord complet
```http
GET /api/asset-inventory-reports/dashboard
```
**Paramètres :** Identiques aux rapports d'immobilisations

**Réponse :**
```json
{
  "dashboardType": "ASSET_INVENTORY_DASHBOARD",
  "assetKPIs": {
    "totalAssets": 25,
    "totalAssetValue": 12000000,
    "totalDepreciation": 3000000,
    "averageAssetValue": 480000
  },
  "inventoryKPIs": {
    "totalProducts": 150,
    "totalInventoryValue": 25000000,
    "totalInventoryItems": 5000,
    "averageProductValue": 166667
  },
  "alerts": {
    "criticalAssets": 3,
    "lowStockItems": 15,
    "criticalAssetsList": [...],
    "lowStockItemsList": [...]
  },
  "charts": {
    "assetTypeDistribution": {...},
    "inventoryStatusDistribution": {...}
  }
}
```

### 📥 IMPORT DE DONNÉES

#### 6. Import d'immobilisations (CSV)
```http
POST /api/asset-inventory-reports/import/assets/csv
```
**Paramètres :**
- `file` (MultipartFile) : Fichier CSV
- `companyId` (Long) : ID de l'entreprise
- `countryCode` (String) : Code pays
- `accountingStandard` (String) : Standard comptable

**Format CSV attendu :**
```csv
assetCode,assetName,assetType,purchasePrice,purchaseDate,depreciationMethod,usefulLife,status,location,supplierCode,supplierName
ASSET001,Ordinateur portable,MACHINERY,500000,2024-01-15,LINEAR,5,ACTIVE,Bureau principal,SUP001,Fournisseur Informatique
```

#### 7. Import d'inventaires (CSV)
```http
POST /api/asset-inventory-reports/import/inventory/csv
```
**Format CSV attendu :**
```csv
productCode,productName,productCategory,unitPrice,quantityOnHand,minimumStock,productStatus,valuationMethod,unitOfMeasure,supplierCode
PROD001,Stylos Bic,MATERIEL,500,100,20,ACTIVE,FIFO,Unité,SUP003,Fournisseur Bureau
```

#### 8. Import de mouvements (CSV)
```http
POST /api/asset-inventory-reports/import/movements/csv
```
**Format CSV attendu :**
```csv
movementCode,productCode,movementType,quantity,unitCost,movementDate,reference,notes
MOV001,PROD001,IN,50,500,2024-01-15,ACH001,Achat initial
```

### 📋 TEMPLATES ET VALIDATION

#### 9. Télécharger les templates
```http
GET /api/asset-inventory-reports/templates/assets
GET /api/asset-inventory-reports/templates/inventory
GET /api/asset-inventory-reports/templates/movements
```

#### 10. Valider un fichier d'import
```http
POST /api/asset-inventory-reports/validate-import
```
**Paramètres :**
- `file` (MultipartFile) : Fichier à valider
- `importType` (String) : Type d'import ("ASSETS", "INVENTORY", "MOVEMENTS")

---

## 🧪 Endpoints de Test

### Test des rapports
```http
POST /api/asset-inventory-reports/test/asset-report
POST /api/asset-inventory-reports/test/inventory-report
POST /api/asset-inventory-reports/test/dashboard
```

---

## 📊 Types de Rapports Disponibles

### 1. **Rapports d'Immobilisations**
- **Rapport d'état** : Vue d'ensemble des immobilisations
- **Rapport de dépréciation** : Plan d'amortissement détaillé
- **Analyse des immobilisations critiques** : Immobilisations nécessitant attention

### 2. **Rapports d'Inventaire**
- **Rapport d'état** : Situation des stocks
- **Rapport de mouvements** : Flux d'entrées/sorties
- **Analyse des ruptures** : Produits en rupture de stock

### 3. **Tableaux de Bord**
- **KPIs** : Indicateurs clés de performance
- **Alertes** : Notifications automatiques
- **Graphiques** : Visualisations des données

---

## 📥 Formats d'Import Supportés

### 1. **Format CSV**
- Séparateur : virgule (,)
- Encodage : UTF-8
- En-tête obligatoire
- Validation automatique

### 2. **Format JSON** (pour immobilisations)
- Structure JSON standard
- Validation des schémas
- Import en lot

### 3. **Validation Automatique**
- Vérification des en-têtes
- Validation des types de données
- Détection des erreurs
- Rapport d'erreurs détaillé

---

## 🔧 Configuration

### Paramètres de Validation
- **Taille maximale de fichier** : 10 MB
- **Formats acceptés** : CSV, JSON
- **Encodage** : UTF-8
- **Séparateur CSV** : virgule

### Paramètres de Rapports
- **Période par défaut** : Année en cours
- **Devise** : XOF (configurable)
- **Standard comptable** : SYSCOHADA (configurable)

---

## 📈 Exemples d'Utilisation

### 1. Générer un rapport d'immobilisations
```bash
curl -X GET "http://localhost:8080/api/asset-inventory-reports/assets/status-report?companyId=1&countryCode=CMR&accountingStandard=SYSCOHADA"
```

### 2. Importer des immobilisations
```bash
curl -X POST "http://localhost:8080/api/asset-inventory-reports/import/assets/csv" \
  -F "file=@assets.csv" \
  -F "companyId=1" \
  -F "countryCode=CMR" \
  -F "accountingStandard=SYSCOHADA"
```

### 3. Télécharger un template
```bash
curl -X GET "http://localhost:8080/api/asset-inventory-reports/templates/assets" \
  -o assets_template.csv
```

---

## 🚀 Avantages

### 1. **Automatisation**
- Import en lot de données
- Validation automatique
- Génération automatique de rapports

### 2. **Flexibilité**
- Multiples formats d'import
- Rapports personnalisables
- Templates téléchargeables

### 3. **Fiabilité**
- Validation stricte des données
- Gestion des erreurs
- Rapports d'import détaillés

### 4. **Performance**
- Traitement optimisé
- Mise en cache des rapports
- Import asynchrone possible

---

## 📝 Notes Techniques

### Architecture
- **Service Layer** : Logique métier séparée
- **Controller Layer** : Exposition des APIs
- **Repository Layer** : Accès aux données
- **Validation Layer** : Contrôle des données

### Sécurité
- Validation des fichiers uploadés
- Sanitisation des données
- Contrôle d'accès par entreprise

### Performance
- Pagination des résultats
- Mise en cache des rapports
- Traitement asynchrone pour gros volumes

---

## 🎯 Prochaines Étapes

### Améliorations Possibles
1. **Export PDF** des rapports
2. **Scheduling** des rapports automatiques
3. **Notifications** par email
4. **API REST** pour intégrations externes
5. **Interface web** pour visualisation

### Intégrations
1. **Système de comptabilité** existant
2. **ERP** externe
3. **Système de facturation**
4. **Gestion des fournisseurs**

---

## ✅ Validation

Le module est maintenant complet avec :
- ✅ Rapports d'immobilisations
- ✅ Rapports d'inventaire
- ✅ Tableaux de bord
- ✅ Import de données (CSV/JSON)
- ✅ Validation automatique
- ✅ Templates téléchargeables
- ✅ Endpoints de test
- ✅ Documentation complète

Le Module 16 est maintenant prêt pour la production avec toutes les fonctionnalités de rapports et d'import ! 🚀


