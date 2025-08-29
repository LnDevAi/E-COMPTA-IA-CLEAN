# 🏗️ **MODULE 16 - GESTION DES IMMOBILISATIONS ET DES STOCKS**

## 📋 **Vue d'ensemble**

Le Module 16 fournit une solution complète pour la gestion des immobilisations et des stocks, incluant :

- **Gestion des immobilisations** : Suivi, dépréciation, maintenance
- **Gestion des stocks** : Inventaire, mouvements, valorisation
- **Méthodes de valorisation** : FIFO, LIFO, Coût moyen, Coût standard
- **Statistiques et rapports** : Tableaux de bord, alertes, analyses

## 🏢 **GESTION DES IMMOBILISATIONS**

### **Types d'immobilisations supportés :**
- 🏢 **BUILDING** - Bâtiments
- 🏭 **MACHINERY** - Machines
- 🚗 **VEHICLE** - Véhicules
- ⚙️ **EQUIPMENT** - Équipements
- 🪑 **FURNITURE** - Mobilier
- 💻 **COMPUTER** - Informatique
- 🖥️ **SOFTWARE** - Logiciels
- 🌍 **LAND** - Terrains
- 🧠 **INTANGIBLE** - Immobilisations incorporelles
- 📦 **OTHER** - Autres

### **Statuts des immobilisations :**
- ✅ **ACTIVE** - En service
- 🔧 **MAINTENANCE** - En maintenance
- 🚫 **RETIRED** - Retiré
- 💰 **SOLD** - Vendu
- ❌ **LOST** - Perdu/Volé
- 🚨 **DAMAGED** - Endommagé
- 🏗️ **UNDER_CONSTRUCTION** - En construction

### **Fonctionnalités :**
- ✅ Création et mise à jour d'immobilisations
- 📊 Calcul automatique de dépréciation
- 📅 Suivi des dates de maintenance
- 🛡️ Gestion des garanties et assurances
- 📍 Localisation et inventaire
- 📈 Statistiques par type et statut

## 📦 **GESTION DES STOCKS**

### **Statuts des produits :**
- ✅ **ACTIVE** - Actif
- ⏸️ **INACTIVE** - Inactif
- 🚫 **DISCONTINUED** - Arrêté
- 📭 **OUT_OF_STOCK** - Rupture de stock
- ⏰ **EXPIRED** - Expiré
- 🚨 **DAMAGED** - Endommagé
- 🔒 **RESERVED** - Réservé

### **Méthodes de valorisation :**
- 📊 **FIFO** - Premier entré, premier sorti
- 📊 **LIFO** - Dernier entré, premier sorti
- 📊 **AVERAGE_COST** - Coût moyen
- 📊 **STANDARD_COST** - Coût standard
- 📊 **SPECIFIC_IDENTIFICATION** - Identification spécifique

### **Types de mouvements :**
- ➕ **IN** - Entrée
- ➖ **OUT** - Sortie
- 🔄 **TRANSFER** - Transfert
- ⚖️ **ADJUSTMENT** - Ajustement
- ↩️ **RETURN** - Retour
- 🚨 **DAMAGE** - Dégâts
- ❌ **LOSS** - Perte
- ⏰ **EXPIRY** - Expiration

## 🚀 **ENDPOINTS API**

### **Tests du module :**
```
GET  /api/asset-inventory/test/base
POST /api/asset-inventory/test/complete
```

### **Immobilisations :**
```
POST   /api/asset-inventory/assets                    # Créer une immobilisation
PUT    /api/asset-inventory/assets/{assetId}          # Mettre à jour
POST   /api/asset-inventory/assets/{assetId}/depreciate # Calculer dépréciation
GET    /api/asset-inventory/assets/{companyId}        # Lister les immobilisations
GET    /api/asset-inventory/assets/{companyId}/statistics # Statistiques
POST   /api/asset-inventory/assets/{companyId}/search # Recherche avancée
```

### **Stocks :**
```
POST   /api/asset-inventory/inventory                 # Créer un produit
PUT    /api/asset-inventory/inventory/{inventoryId}   # Mettre à jour
GET    /api/asset-inventory/inventory/{companyId}     # Lister les produits
GET    /api/asset-inventory/inventory/{companyId}/statistics # Statistiques
POST   /api/asset-inventory/inventory/{companyId}/search # Recherche avancée
```

### **Mouvements :**
```
POST   /api/asset-inventory/movements                 # Créer un mouvement
POST   /api/asset-inventory/movements/{movementId}/approve # Approuver
GET    /api/asset-inventory/movements/{companyId}     # Lister les mouvements
GET    /api/asset-inventory/movements/{companyId}/statistics # Statistiques
POST   /api/asset-inventory/movements/{companyId}/search # Recherche avancée
```

### **Tableau de bord :**
```
GET /api/asset-inventory/{companyId}/dashboard        # Tableau de bord complet
```

## 📊 **EXEMPLES D'UTILISATION**

### **1. Créer une immobilisation :**
```json
POST /api/asset-inventory/assets
{
  "assetCode": "ASSET-2024-001",
  "assetName": "Bâtiment principal",
  "assetType": "BUILDING",
  "purchasePrice": 50000000,
  "companyId": 1,
  "countryCode": "CI",
  "accountingStandard": "OHADA"
}
```

### **2. Créer un produit en stock :**
```json
POST /api/asset-inventory/inventory
{
  "productCode": "PROD-2024-001",
  "productName": "Ordinateurs portables",
  "unit": "pièce",
  "unitPrice": 300000,
  "companyId": 1,
  "countryCode": "CI",
  "accountingStandard": "OHADA"
}
```

### **3. Créer un mouvement de stock :**
```json
POST /api/asset-inventory/movements
{
  "movementCode": "MOV-2024-001",
  "productId": 1,
  "movementType": "IN",
  "quantity": 10,
  "unitPrice": 300000,
  "companyId": 1,
  "countryCode": "CI",
  "accountingStandard": "OHADA"
}
```

### **4. Approuver un mouvement :**
```json
POST /api/asset-inventory/movements/1/approve
{
  "approvedBy": 101
}
```

## 📈 **STATISTIQUES ET RAPPORTS**

### **Statistiques des immobilisations :**
- 📊 Répartition par type
- 📊 Répartition par statut
- 💰 Valeur totale
- 🔢 Nombre total
- 🚨 Alertes (maintenance, garantie, assurance)

### **Statistiques des stocks :**
- 📊 Répartition par catégorie
- 📊 Répartition par statut
- 📊 Répartition par entrepôt
- 💰 Valeur totale
- 📦 Quantité totale
- 🚨 Alertes (rupture, réapprovisionnement, expiration)

### **Statistiques des mouvements :**
- 📊 Répartition par type
- 📊 Répartition par statut
- 📊 Répartition par entrepôt
- 📈 Évolution temporelle
- 🔢 Mouvements en attente

## 🔧 **FONCTIONNALITÉS AVANCÉES**

### **Calcul de dépréciation :**
- 📊 Dépréciation linéaire automatique
- 📅 Calcul basé sur la durée de vie utile
- 💰 Mise à jour de la valeur actuelle

### **Gestion des coûts :**
- 📊 Calcul automatique du coût moyen
- 📊 Support des méthodes FIFO/LIFO
- 💰 Valorisation en temps réel

### **Alertes et notifications :**
- 🚨 Produits en rupture de stock
- 🚨 Produits à réapprovisionner
- ⏰ Produits expirant bientôt
- 🔧 Immobilisations nécessitant une maintenance
- 🛡️ Garanties et assurances expirées

### **Recherche avancée :**
- 🔍 Filtrage par multiples critères
- 📅 Filtrage par période
- 📍 Filtrage par localisation
- 📊 Filtrage par statut

## 🎯 **AVANTAGES DU MODULE**

### **Pour les entreprises :**
- 📊 **Visibilité complète** sur les actifs et stocks
- 💰 **Optimisation des coûts** grâce aux méthodes de valorisation
- 🚨 **Alertes proactives** pour éviter les ruptures
- 📈 **Rapports détaillés** pour la prise de décision
- 🔄 **Traçabilité complète** des mouvements

### **Pour la comptabilité :**
- 📊 **Conformité** aux normes comptables
- 💰 **Valorisation précise** des actifs
- 📈 **Dépréciation automatique** des immobilisations
- 📊 **Rapports réglementaires** facilités

### **Pour les opérations :**
- 📦 **Gestion efficace** des stocks
- 🔄 **Processus d'approbation** des mouvements
- 📍 **Localisation précise** des actifs
- 🚨 **Prévention des pertes** grâce aux alertes

## 🚀 **INTÉGRATION**

Le Module 16 s'intègre parfaitement avec :
- ✅ **Module 15 RH et Paie** - Gestion des employés responsables
- ✅ **Module de comptabilité** - Écritures comptables automatiques
- ✅ **Module de reporting** - Rapports consolidés
- ✅ **Module de sécurité** - Contrôle d'accès et audit

---

**Module 16 - Gestion des Immobilisations et des Stocks**  
*Une solution complète pour la gestion d'actifs et l'optimisation des stocks* 🏗️📦


