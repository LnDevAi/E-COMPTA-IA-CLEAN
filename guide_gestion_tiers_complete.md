# Guide Complet - Gestion des Tiers avec Numérotation OHADA/SYSCOHADA

## 📊 **Système de Numérotation des Tiers**

### ✅ **Plan Comptable OHADA/SYSCOHADA - Classe 4 (Tiers)**

| Type de Tiers | Compte de Base | Description | Exemple |
|---------------|----------------|-------------|---------|
| **Fournisseurs** | 401 | Comptes fournisseurs | 4010001, 4010002 |
| **Clients** | 411 | Comptes clients | 4110001, 4110002 |
| **Personnel** | 421 | Comptes personnel | 4210001, 4210002 |
| **Sécurité Sociale** | 431 | Comptes sécurité sociale | 4310001, 4310002 |
| **État** | 441 | État et autres collectivités | 4410001, 4410002 |
| **Groupe** | 451 | Groupe et associés | 4510001, 4510002 |
| **Dépôts** | 461 | Dépôts et cautionnements reçus | 4610001, 4610002 |
| **Autres** | 471 | Autres créanciers | 4710001, 4710002 |

---

## 🧪 **Tests de la Numérotation des Tiers**

### **1. Informations sur les types de tiers**
```bash
GET http://localhost:8080/api/third-party-numbering/types
```

### **2. Générer un numéro de compte automatiquement**
```bash
POST http://localhost:8080/api/third-party-numbering/generate
Content-Type: application/json

{
    "companyId": 1,
    "type": "CLIENT"
}
```

### **3. Valider un numéro de compte**
```bash
POST http://localhost:8080/api/third-party-numbering/validate
Content-Type: application/json

{
    "companyId": 1,
    "type": "CLIENT",
    "accountNumber": "4110001"
}
```

### **4. Vérifier la disponibilité d'un numéro**
```bash
GET http://localhost:8080/api/third-party-numbering/check-availability?companyId=1&accountNumber=4110001
```

### **5. Informations sur un numéro de compte**
```bash
GET http://localhost:8080/api/third-party-numbering/info?accountNumber=4110001
```

---

## 📋 **Création d'un Tiers - Informations Requises**

### **Informations Obligatoires :**
- **Code tiers** : Code unique (ex: "CLI001", "FOU001")
- **Nom/Raison sociale** : Nom complet du tiers
- **Type** : CLIENT, FOURNISSEUR, PERSONNEL, etc.
- **Pays** : Code pays (ex: "CMR", "FRA")
- **Entreprise** : ID de l'entreprise
- **Standard comptable** : OHADA, IFRS, etc.

### **Informations Optionnelles :**
- **Numéro de compte** : Généré automatiquement si non fourni
- **Catégorie** : Particulier, Entreprise, Association
- **Numéro fiscal** : Numéro d'identification fiscale
- **Numéro de TVA** : Numéro de TVA
- **Adresse complète** : Adresse, ville, code postal
- **Coordonnées** : Téléphone, email, site web
- **Devise préférée** : EUR, XAF, USD, etc.
- **Limite de crédit** : Montant maximum autorisé
- **Conditions de paiement** : Modalités de paiement
- **Délai de paiement** : Nombre de jours
- **Informations bancaires** : IBAN, SWIFT, banque
- **Personne de contact** : Nom, téléphone, email
- **Notes** : Informations additionnelles

---

## 🔧 **Processus de Création d'un Tiers**

### **Étape 1 : Validation des données**
- Vérification de l'unicité du code tiers
- Validation du type de tiers
- Vérification des informations obligatoires

### **Étape 2 : Génération du numéro de compte**
- **Si non fourni** : Génération automatique selon le type
- **Si fourni** : Validation du format et de l'unicité
- Attribution du numéro de sous-compte classe 4

### **Étape 3 : Sauvegarde**
- Création du tiers avec toutes les informations
- Initialisation du solde à zéro
- Horodatage de création

---

## 📊 **Liaison avec les Écritures Comptables**

### **Champ thirdPartyCode dans AccountEntry**
- **Utilisation** : Lier une ligne d'écriture à un tiers spécifique
- **Valeur** : Numéro de compte du tiers (ex: "4110001")
- **Validation** : Vérification de l'existence du tiers

### **Exemple d'écriture avec tiers :**
```json
{
    "entry": {
        "entryDate": "2024-12-26",
        "description": "Vente à crédit - Client ABC",
        "journalType": "VENTES",
        "currency": "XAF",
        "companyId": 1,
        "countryCode": "CMR",
        "accountingStandard": "OHADA"
    },
    "accountEntries": [
        {
            "accountNumber": "4110001",
            "accountName": "Clients",
            "accountType": "DEBIT",
            "amount": 100000,
            "description": "Vente à crédit",
            "thirdPartyCode": "4110001"
        },
        {
            "accountNumber": "701",
            "accountName": "Ventes de marchandises",
            "accountType": "CREDIT",
            "amount": 100000,
            "description": "Vente à crédit"
        }
    ]
}
```

---

## 📈 **Rapports Tiers avec Numérotation**

### **Grand Livre par Tiers**
- **Filtrage** : Par numéro de compte tiers spécifique
- **Contenu** : Toutes les écritures liées au tiers
- **Calcul** : Solde détaillé avec débits/crédits

### **Balance des Tiers**
- **Organisation** : Par type (401, 411, 421, etc.)
- **Tri** : Par numéro de compte
- **Totaux** : Par catégorie de tiers

### **Échéancier**
- **Identification** : Par numéro de compte
- **Suivi** : Échéances par tiers
- **Alertes** : Paiements en retard

---

## ✅ **Validation et Contrôles**

### **Contrôles Automatiques :**
1. **Unicité du code tiers** : Pas de doublon par entreprise
2. **Unicité du numéro de compte** : Pas de doublon par entreprise
3. **Cohérence type/numéro** : Le numéro doit correspondre au type
4. **Format du numéro** : 7 chiffres selon SYSCOHADA
5. **Existence du tiers** : Vérification lors des écritures

### **Validation des Écritures :**
1. **Tiers existant** : Le numéro de compte doit exister
2. **Type cohérent** : Le type de tiers doit correspondre
3. **Solde à jour** : Mise à jour automatique du solde

---

## 🎯 **Avantages du Système**

### **Conformité OHADA/SYSCOHADA**
- Numérotation standardisée
- Plan comptable conforme
- Traçabilité complète

### **Gestion Automatique**
- Génération automatique des numéros
- Validation en temps réel
- Prévention des erreurs

### **Reporting Avancé**
- Grands livres par tiers
- Balance âgée
- Échéanciers
- Analyses de recouvrement

### **Intégration Complète**
- Liaison directe avec les écritures
- Mise à jour automatique des soldes
- Historique complet des opérations

---

## 🚀 **Utilisation Recommandée**

### **1. Création d'un nouveau tiers**
```bash
POST http://localhost:8080/api/third-parties
Content-Type: application/json

{
    "code": "CLI001",
    "name": "Entreprise ABC",
    "type": "CLIENT",
    "countryCode": "CMR",
    "companyId": 1,
    "accountingStandard": "OHADA"
}
```

### **2. Vérification du numéro généré**
- Le système génère automatiquement "4110001"
- Validation de l'unicité
- Attribution du nom de compte "Clients"

### **3. Création d'écritures**
- Utilisation du numéro "4110001" dans `thirdPartyCode`
- Liaison automatique avec le tiers
- Mise à jour du solde

### **4. Génération de rapports**
- Grand livre du tiers "4110001"
- Balance des tiers
- Échéancier et recouvrement

**Le système est maintenant complet et conforme aux standards OHADA/SYSCOHADA !** 🎉




