# 📊 **Module 2 : États Financiers Avancés (SMT + SYSCOHADA + IFRS)**

## 🎯 **Vue d'ensemble**

Le Module 2 implémente un système complet de gestion comptable pour les **Systèmes Minimal de Trésorerie (SMT)** et la génération d'**États Financiers** conformes aux standards internationaux (SYSCOHADA, IFRS, US GAAP, PCG).

### 🌟 **Fonctionnalités principales**

- ✅ **Gestion des entreprises SMT** (micro, petite, moyenne)
- ✅ **Gestion des exercices comptables**
- ✅ **Gestion des comptes de trésorerie** (caisse, banque, mobile money)
- ✅ **Livre des recettes** (ventes, services, autres)
- ✅ **Livre des dépenses** (achats, frais, salaires, impôts)
- ✅ **Génération d'états financiers** (Bilan, Compte de résultat, Flux de trésorerie, Annexes)
- ✅ **Support multi-standards** (SYSCOHADA, IFRS, US GAAP, PCG)
- ✅ **Tableau de bord complet**
- ✅ **Statistiques avancées**

---

## 🏗️ **Architecture du Module**

### 📋 **Entités principales**

#### 1. **EntrepriseSMT**
```java
- id: Long
- nomEntreprise: String
- numeroContribuable: String
- paysCode: String (BF, CI, SN, etc.)
- regimeFiscal: String (MICRO, PETITE, MOYENNE)
- seuilChiffreAffaires: Double
- devise: String (XOF, EUR, USD, etc.)
- statut: String (ACTIVE, SUSPENDU, RADIE)
```

#### 2. **ExerciceSMT**
```java
- id: Long
- entreprise: EntrepriseSMT
- anneeExercice: Integer
- dateDebut: LocalDate
- dateFin: LocalDate
- estCloture: Boolean
- totalRecettes: BigDecimal
- totalDepenses: BigDecimal
- soldeTresorerie: BigDecimal
```

#### 3. **CompteTresorerie**
```java
- id: Long
- entreprise: EntrepriseSMT
- nomCompte: String
- typeCompte: String (CAISSE, BANQUE, MOBILE_MONEY, AUTRE)
- soldeActuel: BigDecimal
- devise: String
```

#### 4. **LivreRecette**
```java
- id: Long
- exercice: ExerciceSMT
- compteTresorerie: CompteTresorerie
- dateRecette: LocalDate
- libelle: String
- typeRecette: String (VENTES, SERVICES, AUTRES_RECETTES)
- montant: BigDecimal
- modePaiement: String (ESPECES, CHEQUE, VIREMENT, MOBILE_MONEY)
```

#### 5. **LivreDepense**
```java
- id: Long
- exercice: ExerciceSMT
- compteTresorerie: CompteTresorerie
- dateDepense: LocalDate
- libelle: String
- typeDepense: String (ACHATS, FRAIS_GENERAUX, SALAIRES, IMPOTS, AUTRES_DEPENSES)
- montant: BigDecimal
- modePaiement: String
```

#### 6. **EtatFinancier**
```java
- id: Long
- exercice: ExerciceSMT
- typeEtat: String (BILAN, COMPTE_RESULTAT, FLUX_TRESORERIE, ANNEXES)
- standardComptable: String (SYSCOHADA, IFRS, US_GAAP, PCG)
- totalActifs: BigDecimal
- totalPassifs: BigDecimal
- totalProduits: BigDecimal
- totalCharges: BigDecimal
- resultatNet: BigDecimal
- donneesJson: String
```

---

## 🚀 **Endpoints API**

### 📊 **Base URL :** `http://localhost:8080/api/smt`

### 🏢 **Gestion des Entreprises SMT**

#### **Créer une entreprise**
```bash
POST /api/smt/entreprises
Content-Type: application/json

{
  "nomEntreprise": "SARL Tech Solutions",
  "numeroContribuable": "BF123456789",
  "paysCode": "BF",
  "regimeFiscal": "PETITE",
  "seuilChiffreAffaires": 50000000.0,
  "adresse": "Ouagadougou, Secteur 15",
  "telephone": "+226 70 12 34 56",
  "email": "contact@techsolutions.bf",
  "representantLegal": "Moussa Ouédraogo",
  "numeroRegistreCommerce": "RC-BF-2024-001",
  "numeroCNSS": "CNSS-BF-001",
  "activitePrincipale": "Services informatiques",
  "devise": "XOF"
}
```

#### **Obtenir toutes les entreprises**
```bash
GET /api/smt/entreprises
```

#### **Obtenir une entreprise par ID**
```bash
GET /api/smt/entreprises/{id}
```

#### **Statistiques des entreprises**
```bash
GET /api/smt/entreprises/statistiques
```

### 📅 **Gestion des Exercices**

#### **Créer un exercice**
```bash
POST /api/smt/exercices
Content-Type: application/json

{
  "entreprise": {
    "id": 1
  },
  "anneeExercice": 2024,
  "dateDebut": "2024-01-01",
  "dateFin": "2024-12-31"
}
```

#### **Obtenir l'exercice en cours**
```bash
GET /api/smt/entreprises/{entrepriseId}/exercice-courant
```

#### **Clôturer un exercice**
```bash
POST /api/smt/exercices/{exerciceId}/cloturer
```

### 💰 **Gestion des Comptes de Trésorerie**

#### **Créer un compte de trésorerie**
```bash
POST /api/smt/comptes-tresorerie
Content-Type: application/json

{
  "entreprise": {
    "id": 1
  },
  "nomCompte": "Caisse principale",
  "typeCompte": "CAISSE",
  "soldeInitial": 500000,
  "devise": "XOF"
}
```

#### **Obtenir les comptes d'une entreprise**
```bash
GET /api/smt/entreprises/{entrepriseId}/comptes-tresorerie
```

### 📈 **Gestion des Recettes**

#### **Enregistrer une recette**
```bash
POST /api/smt/recettes
Content-Type: application/json

{
  "exercice": {
    "id": 1
  },
  "compteTresorerie": {
    "id": 1
  },
  "dateRecette": "2024-01-15",
  "libelle": "Vente services informatiques",
  "typeRecette": "SERVICES",
  "montant": 500000,
  "numeroPiece": "FACT-001",
  "tiers": "Client ABC",
  "modePaiement": "ESPECES"
}
```

#### **Obtenir les recettes d'un exercice**
```bash
GET /api/smt/exercices/{exerciceId}/recettes
```

#### **Statistiques des recettes**
```bash
GET /api/smt/exercices/{exerciceId}/recettes/statistiques
```

### 📉 **Gestion des Dépenses**

#### **Enregistrer une dépense**
```bash
POST /api/smt/depenses
Content-Type: application/json

{
  "exercice": {
    "id": 1
  },
  "compteTresorerie": {
    "id": 1
  },
  "dateDepense": "2024-01-10",
  "libelle": "Achat fournitures bureau",
  "typeDepense": "ACHATS",
  "montant": 150000,
  "numeroPiece": "FACT-FOURN-001",
  "tiers": "Fournisseur XYZ",
  "modePaiement": "ESPECES"
}
```

#### **Obtenir les dépenses d'un exercice**
```bash
GET /api/smt/exercices/{exerciceId}/depenses
```

#### **Statistiques des dépenses**
```bash
GET /api/smt/exercices/{exerciceId}/depenses/statistiques
```

### 📊 **Génération d'États Financiers**

#### **Générer un état financier**
```bash
POST /api/smt/exercices/{exerciceId}/etats-financiers?typeEtat=BILAN&standardComptable=SYSCOHADA
```

#### **Valider un état financier**
```bash
POST /api/smt/etats-financiers/{etatId}/valider
```

#### **Publier un état financier**
```bash
POST /api/smt/etats-financiers/{etatId}/publier
```

### 📋 **Tableau de Bord**

#### **Obtenir le tableau de bord**
```bash
GET /api/smt/entreprises/{entrepriseId}/tableau-bord
```

### 🧪 **Tests et Informations**

#### **Test du module**
```bash
GET /api/smt/test
```

#### **Informations du module**
```bash
GET /api/smt/info
```

---

## 📊 **Types d'États Financiers Supportés**

### 1. **Bilan (Balance Sheet)**
- **Actifs** : Comptes de trésorerie, créances, immobilisations
- **Passifs** : Dettes, provisions, capitaux propres
- **Standards** : SYSCOHADA, IFRS, US GAAP, PCG

### 2. **Compte de Résultat (Income Statement)**
- **Produits** : Recettes d'exploitation, produits financiers
- **Charges** : Dépenses d'exploitation, charges financières
- **Résultat net** : Bénéfice ou perte

### 3. **Flux de Trésorerie (Cash Flow)**
- **Flux d'exploitation** : Activités opérationnelles
- **Flux d'investissement** : Acquisitions d'actifs
- **Flux de financement** : Emprunts, capitaux

### 4. **Annexes (Notes)**
- **Informations complémentaires**
- **Détails des postes**
- **Méthodes comptables**

---

## 🌍 **Standards Comptables Supportés**

### 1. **SYSCOHADA** (Afrique de l'Ouest)
- **Pays** : BF, CI, SN, ML, NE, TG, BJ, etc.
- **Caractéristiques** : Standard harmonisé OHADA
- **Devise** : XOF (Franc CFA)

### 2. **IFRS** (International)
- **Pays** : Monde entier
- **Caractéristiques** : Standards internationaux
- **Devise** : Multi-devises

### 3. **US GAAP** (États-Unis)
- **Pays** : États-Unis
- **Caractéristiques** : Principes comptables américains
- **Devise** : USD

### 4. **PCG** (France)
- **Pays** : France
- **Caractéristiques** : Plan comptable général français
- **Devise** : EUR

---

## 📈 **Statistiques et Rapports**

### **Statistiques par Type**
- Recettes par type (Ventes, Services, Autres)
- Dépenses par type (Achats, Frais, Salaires, Impôts)
- Mouvements par mode de paiement

### **Statistiques Temporelles**
- Évolution mensuelle des recettes/dépenses
- Comparaison inter-exercices
- Tendances saisonnières

### **Statistiques Géographiques**
- Répartition par pays
- Répartition par régime fiscal
- Répartition par devise

---

## 🔧 **Configuration et Installation**

### **Dépendances requises**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

### **Configuration de sécurité**
```java
.requestMatchers("/api/smt/**").permitAll()
```

### **Initialisation des données**
Le module inclut un `SMTDataInitializer` qui crée automatiquement :
- 2 entreprises SMT de test
- Exercices 2023 et 2024
- Comptes de trésorerie (Caisse, Banque, Mobile Money)
- Recettes et dépenses de test
- États financiers de démonstration

---

## 🎯 **Cas d'Usage**

### **1. Petite entreprise OHADA**
- **Régime** : MICRO ou PETITE
- **Standard** : SYSCOHADA
- **Devise** : XOF
- **États** : Bilan simplifié, Compte de résultat

### **2. Entreprise internationale**
- **Régime** : MOYENNE
- **Standard** : IFRS ou US GAAP
- **Devise** : EUR, USD
- **États** : Bilan complet, Flux de trésorerie, Annexes

### **3. Cabinet comptable**
- **Multi-clients** : Gestion de plusieurs entreprises
- **Multi-standards** : Conversion entre standards
- **Reporting** : Rapports consolidés

---

## 🚀 **Fonctionnalités Avancées**

### **1. Calculs Automatiques**
- Solde de trésorerie en temps réel
- Résultat net automatique
- Ratios financiers

### **2. Validation des Données**
- Contrôles de cohérence
- Validation des montants
- Vérification des dates

### **3. Export et Reporting**
- Génération PDF des états
- Export Excel des données
- Rapports personnalisés

### **4. Audit Trail**
- Traçabilité des modifications
- Historique des validations
- Logs de sécurité

---

## 📞 **Support et Maintenance**

### **Logs et Monitoring**
- Logs détaillés des opérations
- Métriques de performance
- Alertes automatiques

### **Sauvegarde et Récupération**
- Sauvegarde automatique des données
- Récupération en cas d'incident
- Versioning des états financiers

### **Mises à jour**
- Évolution des standards comptables
- Nouvelles fonctionnalités
- Corrections de bugs

---

## 🎉 **Conclusion**

Le Module 2 **États Financiers Avancés** offre une solution complète et professionnelle pour la gestion comptable SMT et la génération d'états financiers conformes aux standards internationaux.

**Points forts :**
- ✅ **Conformité OHADA/SYSCOHADA**
- ✅ **Support multi-standards**
- ✅ **Interface REST complète**
- ✅ **Données de test incluses**
- ✅ **Documentation détaillée**
- ✅ **Architecture modulaire**

**Prêt pour la production !** 🚀




