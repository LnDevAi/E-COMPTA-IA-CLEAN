# Plateformes Gouvernementales et Logiciels Tiers - E-COMPTA-IA

## Vue d'ensemble

Ce module permet l'intégration avec les plateformes gouvernementales (fiscales, sociales, douanes) et les logiciels tiers pour une synchronisation automatique des données comptables et fiscales.

## Fonctionnalités

### 🏛️ Plateformes Gouvernementales

#### Pays Supportés
- **Sénégal (SN)** : DGID, CNSS, DGD
- **France (FR)** : DGFiP, URSSAF, DGDDI
- **États-Unis (US)** : IRS, SSA
- **Burkina Faso (BF)** : DGI, CNSS
- **Côte d'Ivoire (CI)** : DGI, CNPS

#### Types de Plateformes
- **Fiscales** : Déclarations TVA, IS, IR, TSS
- **Sociales** : Déclarations CNSS, URSSAF, SSA
- **Douanes** : Déclarations d'import/export

### 💼 Logiciels Tiers

#### Catégories Supportées
- **Comptabilité** : Sage, Cegid, QuickBooks
- **CRM** : Salesforce, HubSpot
- **RH** : Workday, BambooHR
- **ERP** : NetSuite, Odoo
- **Paiement** : Stripe, PayPal
- **Facturation** : FreshBooks

#### Types de Données Synchronisées
- Comptes comptables
- Écritures comptables
- Clients et fournisseurs
- Factures et paiements
- Employés et paie
- Stocks et commandes

## API Endpoints

### Plateformes Gouvernementales

#### 1. Test de Base
```http
GET /api/government-platform/test
```

#### 2. Plateformes Disponibles
```http
GET /api/government-platform/platforms/{countryCode}
```

#### 3. Test de Connexion
```http
POST /api/government-platform/test-connection
{
  "countryCode": "SN",
  "platformType": "taxPlatform",
  "apiKey": "your_api_key",
  "apiSecret": "your_api_secret"
}
```

#### 4. Soumission Déclaration Fiscale
```http
POST /api/government-platform/submit-tax-declaration
{
  "countryCode": "SN",
  "declarationType": "TVA",
  "period": "2024-01",
  "declarationData": {
    "tvaCollectee": 1000000.0,
    "tvaDeductible": 800000.0,
    "tvaDue": 200000.0
  },
  "companyId": 1
}
```

#### 5. Soumission Déclaration Sociale
```http
POST /api/government-platform/submit-social-declaration
{
  "countryCode": "SN",
  "declarationType": "Déclaration mensuelle",
  "period": "2024-01",
  "declarationData": {
    "nombreEmployes": 50,
    "salaireTotal": 25000000.0,
    "cotisations": 3750000.0
  },
  "companyId": 1
}
```

#### 6. Statut de Déclaration
```http
GET /api/government-platform/declaration-status/{submissionId}
```

#### 7. Notifications Gouvernementales
```http
GET /api/government-platform/notifications/{countryCode}
```

### Logiciels Tiers

#### 1. Logiciels Disponibles
```http
GET /api/government-platform/third-party-software
```

#### 2. Test de Connexion
```http
POST /api/government-platform/test-third-party-connection
{
  "softwareName": "SAGE",
  "apiUrl": "https://api.sage.com",
  "apiKey": "your_api_key",
  "apiSecret": "your_api_secret"
}
```

#### 3. Synchronisation de Données
```http
POST /api/government-platform/sync-third-party-data
{
  "softwareName": "SAGE",
  "dataType": "ACCOUNTS",
  "syncDirection": "BOTH",
  "companyId": 1
}
```

#### 4. Logs de Synchronisation
```http
GET /api/government-platform/sync-logs/{softwareName}
```

#### 5. Configuration d'Intégration
```http
POST /api/government-platform/configure-third-party-integration
{
  "softwareName": "SAGE",
  "integrationType": "REAL_TIME",
  "configuration": {
    "apiKey": "your_api_key",
    "apiSecret": "your_api_secret",
    "syncInterval": "5 minutes"
  },
  "companyId": 1
}
```

#### 6. Statistiques d'Intégration
```http
GET /api/government-platform/integration-statistics
```

#### 7. Test Complet
```http
POST /api/government-platform/test-complete
```

## Configuration

### Plateformes Gouvernementales

Chaque pays est configuré avec ses plateformes spécifiques :

```java
// Exemple pour le Sénégal
Map<String, Object> senegalConfig = new HashMap<>();
senegalConfig.put("taxPlatform", Map.of(
    "name", "Direction Générale des Impôts et Domaines (DGID)",
    "baseUrl", "https://api.dgid.sn",
    "apiVersion", "v1",
    "supportedDeclarations", Arrays.asList("TVA", "IS", "IR", "TSS")
));
```

### Logiciels Tiers

Chaque logiciel est configuré avec ses capacités :

```java
// Exemple pour Sage
Map<String, Object> sageConfig = new HashMap<>();
sageConfig.put("name", "Sage");
sageConfig.put("type", "ACCOUNTING");
sageConfig.put("version", "2024");
sageConfig.put("apiUrl", "https://api.sage.com");
sageConfig.put("apiVersion", "v3");
sageConfig.put("supportedDataTypes", Arrays.asList("ACCOUNTS", "JOURNAL_ENTRIES", "CUSTOMERS", "SUPPLIERS", "INVOICES"));
sageConfig.put("syncModes", Arrays.asList("REAL_TIME", "SCHEDULED", "MANUAL"));
```

## Tests Automatisés

### Exécution des Tests

```bash
# Test complet automatisé
./test-government-platforms.bat

# Tests unitaires
mvn test -Dtest=GovernmentPlatformControllerTest
mvn test -Dtest=ThirdPartySoftwareServiceTest
```

### Tests Disponibles

#### Contrôleur
- ✅ Test de base du module
- ✅ Récupération des plateformes par pays
- ✅ Test de connexion aux plateformes
- ✅ Soumission de déclarations fiscales et sociales
- ✅ Récupération des statuts et notifications
- ✅ Gestion des logiciels tiers
- ✅ Synchronisation et configuration
- ✅ Tests de validation et gestion d'erreurs

#### Services
- ✅ Configuration des plateformes gouvernementales
- ✅ Tests de connexion et soumission
- ✅ Gestion des notifications et statuts
- ✅ Configuration des logiciels tiers
- ✅ Tests de synchronisation et logs
- ✅ Validation des configurations
- ✅ Tests complets et statistiques

## Sécurité

### Authentification
- API Keys et secrets pour les plateformes gouvernementales
- Tokens d'accès pour les logiciels tiers
- Validation des configurations requises

### Validation
- Vérification des types de données supportés
- Validation des paramètres requis
- Gestion des erreurs de connexion

## Monitoring

### Logs de Synchronisation
- Horodatage des opérations
- Statuts de synchronisation
- Gestion des erreurs
- Métriques de performance

### Statistiques
- Nombre de plateformes par pays
- Types de logiciels supportés
- Taux de réussite des synchronisations
- Temps moyen de synchronisation

## Exemples d'Utilisation

### 1. Connexion à une Plateforme Gouvernementale

```javascript
// Test de connexion DGID Sénégal
const response = await fetch('/api/government-platform/test-connection', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    countryCode: 'SN',
    platformType: 'taxPlatform',
    apiKey: 'your_dgid_api_key',
    apiSecret: 'your_dgid_secret'
  })
});
```

### 2. Soumission de Déclaration TVA

```javascript
// Déclaration TVA Sénégal
const response = await fetch('/api/government-platform/submit-tax-declaration', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    countryCode: 'SN',
    declarationType: 'TVA',
    period: '2024-01',
    declarationData: {
      tvaCollectee: 1000000.0,
      tvaDeductible: 800000.0,
      tvaDue: 200000.0
    },
    companyId: 1
  })
});
```

### 3. Synchronisation avec Sage

```javascript
// Synchronisation des comptes avec Sage
const response = await fetch('/api/government-platform/sync-third-party-data', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    softwareName: 'SAGE',
    dataType: 'ACCOUNTS',
    syncDirection: 'BOTH',
    companyId: 1
  })
});
```

## Support

### Pays Supportés
- 🇸🇳 Sénégal : DGID, CNSS, DGD
- 🇫🇷 France : DGFiP, URSSAF, DGDDI
- 🇺🇸 États-Unis : IRS, SSA
- 🇧🇫 Burkina Faso : DGI, CNSS
- 🇨🇮 Côte d'Ivoire : DGI, CNPS

### Logiciels Supportés
- 📊 **Comptabilité** : Sage, Cegid, QuickBooks
- 👥 **CRM** : Salesforce, HubSpot
- 👨‍💼 **RH** : Workday, BambooHR
- 🏢 **ERP** : NetSuite, Odoo
- 💳 **Paiement** : Stripe, PayPal
- 📄 **Facturation** : FreshBooks

## Maintenance

### Ajout d'un Nouveau Pays
1. Ajouter la configuration dans `GovernmentPlatformService`
2. Configurer les plateformes disponibles
3. Ajouter les tests correspondants
4. Mettre à jour la documentation

### Ajout d'un Nouveau Logiciel
1. Ajouter la configuration dans `ThirdPartySoftwareService`
2. Définir les types de données supportés
3. Configurer les modes de synchronisation
4. Ajouter les tests de validation

## Version
- **Version** : 1.0.0
- **Date** : Janvier 2024
- **Statut** : Production Ready


