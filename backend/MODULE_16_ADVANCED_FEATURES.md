# Module 16 - Fonctionnalités Avancées : Immobilisations et Stocks

## Vue d'ensemble

Ce document décrit les fonctionnalités avancées ajoutées au Module 16 (Gestion des Immobilisations et des Stocks) pour répondre aux exigences spécifiques :

1. **Génération d'écritures comptables** pour les opérations d'immobilisations et de stocks
2. **Analyse d'inventaire** pour détecter les écarts entre situation comptable et physique
3. **Gestion des documents** pour importer des pièces justificatives
4. **Codification robuste** des actifs et stocks

## 1. Génération d'Écritures Comptables

### 1.1 Écritures d'Acquisition d'Immobilisations

**Endpoint :** `POST /api/asset-inventory-advanced/generate-asset-acquisition-entry`

**Fonctionnalité :** Génère automatiquement les écritures comptables lors de l'acquisition d'un actif.

**Écritures générées :**
- **Débit :** Compte 211 (Immobilisations corporelles) - Montant HT
- **Débit :** Compte 4456 (TVA déductible) - Montant TVA
- **Crédit :** Compte 401 (Fournisseurs) - Montant TTC

**Exemple de requête :**
```json
{
  "assetId": 1,
  "supplierCode": "SUPPLIER_001",
  "supplierName": "Fournisseur ABC"
}
```

### 1.2 Écritures de Dépréciation

**Endpoint :** `POST /api/asset-inventory-advanced/generate-asset-depreciation-entry`

**Fonctionnalité :** Génère les écritures de dépréciation linéaire des actifs.

**Écritures générées :**
- **Débit :** Compte 6811 (Dotations aux amortissements)
- **Crédit :** Compte 2811 (Amortissements cumulés)

**Exemple de requête :**
```json
{
  "assetId": 1,
  "depreciationAmount": 50000
}
```

### 1.3 Écritures de Mouvements de Stocks

**Endpoint :** `POST /api/asset-inventory-advanced/generate-inventory-movement-entry`

**Fonctionnalité :** Génère les écritures pour les entrées et sorties de stocks.

**Écritures pour entrée (IN) :**
- **Débit :** Compte 31 (Stocks de marchandises)
- **Crédit :** Compte 401 (Fournisseurs)

**Écritures pour sortie (OUT) :**
- **Débit :** Compte 607 (Coût des marchandises vendues)
- **Crédit :** Compte 31 (Stocks de marchandises)

**Exemple de requête :**
```json
{
  "movementId": 1
}
```

## 2. Analyse d'Inventaire

### 2.1 Création d'une Analyse

**Endpoint :** `POST /api/asset-inventory-advanced/create-inventory-analysis`

**Fonctionnalité :** Crée une nouvelle session d'analyse d'inventaire.

**Types d'analyse :**
- `ASSET_ANALYSIS` : Analyse des immobilisations uniquement
- `INVENTORY_ANALYSIS` : Analyse des stocks uniquement
- `COMPREHENSIVE` : Analyse complète (actifs + stocks)

**Exemple de requête :**
```json
{
  "companyId": 1,
  "analysisType": "COMPREHENSIVE",
  "countryCode": "CI",
  "accountingStandard": "SYSCOHADA"
}
```

### 2.2 Exécution de l'Analyse

**Endpoint :** `POST /api/asset-inventory-advanced/perform-inventory-analysis/{analysisId}`

**Fonctionnalité :** Compare les valeurs comptables avec les valeurs physiques et détecte les écarts.

**Processus :**
1. Récupération des actifs et stocks de l'entreprise
2. Simulation d'inventaire physique (avec écarts aléatoires pour démonstration)
3. Calcul des écarts (quantité et valeur)
4. Classification des écarts (SURPLUS, SHORTAGE, NONE)
5. Proposition d'actions correctives

**Résultats retournés :**
```json
{
  "analysis": {
    "id": 1,
    "analysisNumber": "ANAL_1234567890",
    "totalAccountingValue": 15000000,
    "totalPhysicalValue": 14250000,
    "totalVariance": -750000,
    "variancePercentage": -5.0,
    "totalItemsAnalyzed": 25,
    "itemsWithVariance": 8
  },
  "details": [
    {
      "itemCode": "ASSET_001",
      "itemName": "Ordinateur portable",
      "itemType": "ASSET",
      "accountingValue": 500000,
      "physicalValue": 475000,
      "valueVariance": -25000,
      "varianceType": "SHORTAGE",
      "proposedAction": "ADJUSTMENT"
    }
  ]
}
```

### 2.3 Génération d'Écritures de Correction

**Endpoint :** `POST /api/asset-inventory-advanced/generate-correction-entries/{analysisId}`

**Fonctionnalité :** Génère automatiquement les écritures comptables pour corriger les écarts détectés.

**Écritures de correction pour actifs :**
- **Débit :** Compte 675 (Charges exceptionnelles) - Perte sur immobilisation
- **Crédit :** Compte 211 (Immobilisations corporelles) - Correction

**Écritures de correction pour stocks :**
- **Débit :** Compte 607 (Achats de marchandises) - Perte sur stock
- **Crédit :** Compte 31 (Stocks de marchandises) - Correction

### 2.4 Génération de Rapports

**Endpoint :** `POST /api/asset-inventory-advanced/generate-analysis-report/{analysisId}`

**Fonctionnalité :** Génère un rapport détaillé de l'analyse d'inventaire.

**Contenu du rapport :**
- Résumé de l'analyse
- Détails des écarts par article
- Statistiques par type d'écart
- Top 10 des plus gros écarts
- Actions recommandées

## 3. Gestion des Documents

### 3.1 Création de Documents

**Endpoint :** `POST /api/asset-inventory-advanced/create-document`

**Fonctionnalité :** Crée un document associé à une opération d'immobilisation ou de stock.

**Types de documents supportés :**
- `ASSET_PURCHASE` : Facture d'acquisition d'immobilisation
- `ASSET_MAINTENANCE` : Bon de maintenance
- `INVENTORY_RECEIPT` : Bon de réception
- `INVENTORY_ISSUE` : Bon de sortie
- `INVENTORY_TRANSFER` : Bon de transfert
- `INVENTORY_ADJUSTMENT` : Bon d'ajustement

**Exemple de requête :**
```json
{
  "companyId": 1,
  "documentType": "ASSET_PURCHASE",
  "title": "Facture d'acquisition d'immobilisation",
  "relatedEntityType": "ASSET",
  "relatedEntityId": 1,
  "relatedEntityCode": "ASSET_001",
  "countryCode": "CI",
  "accountingStandard": "SYSCOHADA"
}
```

### 3.2 Attachement de Fichiers

**Endpoint :** `POST /api/asset-inventory-advanced/attach-file/{documentId}`

**Fonctionnalité :** Associe un fichier (PDF, image, etc.) à un document.

**Exemple de requête :**
```json
{
  "filePath": "/uploads/documents/facture_001.pdf",
  "fileType": "PDF",
  "fileSize": 1024000,
  "originalFileName": "facture_acquisition.pdf"
}
```

### 3.3 Validation de Documents

**Endpoint :** `POST /api/asset-inventory-advanced/validate-document/{documentId}`

**Fonctionnalité :** Valide un document pour l'intégrer dans le processus comptable.

**Exemple de requête :**
```json
{
  "validatedBy": 1
}
```

### 3.4 Consultation des Documents

**Endpoints disponibles :**
- `GET /api/asset-inventory-advanced/documents/{companyId}` - Tous les documents
- `GET /api/asset-inventory-advanced/documents/{companyId}/type/{documentType}` - Par type
- `GET /api/asset-inventory-advanced/documents/{companyId}/entity/{entityId}/{entityType}` - Par entité liée
- `GET /api/asset-inventory-advanced/documents/{companyId}/unreconciled` - Non réconciliés
- `GET /api/asset-inventory-advanced/documents/{companyId}/with-accounting-entries` - Avec écritures comptables

## 4. Codification des Actifs et Stocks

### 4.1 Structure des Codes

**Actifs :** `ASSET_XXX_YYYY` où :
- `ASSET` : Préfixe fixe
- `XXX` : Type d'actif (001, 002, etc.)
- `YYYY` : Numéro séquentiel

**Stocks :** `INV_XXX_YYYY` où :
- `INV` : Préfixe fixe
- `XXX` : Catégorie de produit
- `YYYY` : Numéro séquentiel

**Mouvements :** `MOV_XXX_YYYY` où :
- `MOV` : Préfixe fixe
- `XXX` : Type de mouvement (IN, OUT, TRANS, ADJ)
- `YYYY` : Numéro séquentiel

### 4.2 Gestion Automatique

- Génération automatique des codes lors de la création
- Vérification de l'unicité des codes
- Historisation des changements de codes
- Traçabilité complète des opérations

## 5. Intégration avec le Module Comptabilité

### 5.1 Validation des Écritures

Les écritures générées sont créées avec le statut "BROUILLON" et peuvent être :
- Validées individuellement
- Transférées en lot vers le module comptabilité
- Annulées si nécessaire

### 5.2 Rapprochement

- Association automatique des documents aux écritures
- Traçabilité complète des opérations
- Possibilité de rapprochement manuel

## 6. Endpoints de Test

### 6.1 Test de Génération d'Écriture d'Acquisition

**Endpoint :** `POST /api/asset-inventory-advanced/test-asset-acquisition-entry`

**Fonctionnalité :** Teste la génération d'écriture avec un actif fictif.

### 6.2 Test de Création d'Analyse

**Endpoint :** `POST /api/asset-inventory-advanced/test-inventory-analysis`

**Fonctionnalité :** Teste la création d'une analyse d'inventaire.

### 6.3 Test de Création de Document

**Endpoint :** `POST /api/asset-inventory-advanced/test-document-creation`

**Fonctionnalité :** Teste la création d'un document.

## 7. Avantages des Nouvelles Fonctionnalités

### 7.1 Automatisation
- Génération automatique des écritures comptables
- Détection automatique des écarts d'inventaire
- Propositions automatiques de corrections

### 7.2 Conformité
- Respect des standards comptables SYSCOHADA
- Traçabilité complète des opérations
- Documentation systématique

### 7.3 Efficacité
- Réduction des erreurs manuelles
- Gain de temps sur les tâches répétitives
- Rapports automatisés

### 7.4 Contrôle
- Validation en plusieurs étapes
- Approbation des corrections
- Audit trail complet

## 8. Utilisation Recommandée

### 8.1 Workflow Typique

1. **Création d'actifs/stocks** avec codification automatique
2. **Import des documents** justificatifs
3. **Génération automatique** des écritures comptables
4. **Validation** des écritures et documents
5. **Analyse périodique** d'inventaire
6. **Correction automatique** des écarts détectés
7. **Génération de rapports** pour audit

### 8.2 Bonnes Pratiques

- Valider systématiquement les documents avant génération d'écritures
- Effectuer des analyses d'inventaire régulières
- Réviser les propositions de correction avant application
- Maintenir une documentation complète des opérations

## 9. Sécurité et Permissions

Tous les endpoints sont protégés et nécessitent :
- Authentification utilisateur
- Autorisation appropriée
- Validation des données d'entrée
- Logging des opérations sensibles

## 10. Support et Maintenance

Le module est conçu pour être :
- Facilement extensible
- Bien documenté
- Testé automatiquement
- Maintenu régulièrement

---

**Note :** Cette documentation décrit les fonctionnalités avancées du Module 16. Pour les fonctionnalités de base, se référer au document `MODULE_16_ASSET_INVENTORY.md`.


