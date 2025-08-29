# 🧪 **GUIDE DE TEST POSTMAN - E-COMPTA-IA**

## 📋 **FICHIERS CRÉÉS**

1. **`E-COMPTA-IA-COMPLETE-TESTS.postman_collection.json`** - Collection complète
2. **`E-COMPTA-IA-ENVIRONMENT.postman_environment.json`** - Variables d'environnement
3. **`POSTMAN_TEST_GUIDE.md`** - Ce guide

## 🚀 **IMPORT DANS POSTMAN**

### **Étape 1 : Importer la Collection**
1. Ouvrir Postman
2. Cliquer sur **Import**
3. Sélectionner `E-COMPTA-IA-COMPLETE-TESTS.postman_collection.json`
4. Cliquer **Import**

### **Étape 2 : Importer l'Environnement**
1. Cliquer sur **Import**
2. Sélectionner `E-COMPTA-IA-ENVIRONMENT.postman_environment.json`
3. Cliquer **Import**
4. Sélectionner l'environnement **"E-COMPTA-IA Environment"** dans le dropdown

## 🔧 **CONFIGURATION PRÉALABLE**

### **Variables d'Environnement**
- `base_url` : `http://localhost:8080`
- `auth_token` : (auto-rempli après login)
- `company_id` : `1`
- `test_username` : `testuser`
- `test_password` : `TestPassword123!`
- `test_email` : `test@ecomptaia.com`

## 📊 **ORDRE DE TEST RECOMMANDÉ**

### **1. Health & System**
```
✅ Health Check - Vérifier que le backend répond
```

### **2. Authentication**
```
✅ Register User - Créer un utilisateur test
✅ Login User - Se connecter (récupère le token automatiquement)
✅ Auth Test - Tester l'endpoint d'authentification
✅ User Count - Compter les utilisateurs
```

### **3. Dashboard**
```
✅ Dashboard Test - Test basique du dashboard
✅ Financial KPIs - Indicateurs financiers
✅ Operational Metrics - Métriques opérationnelles
✅ Analytics Overview - Vue d'ensemble analytique
✅ Performance Indicators - Indicateurs de performance
```

### **4. Accounting**
```
✅ Get Chart of Accounts - Plan comptable
✅ Create Account - Créer un compte
✅ Get Journal Entries - Écritures comptables
✅ Create Journal Entry - Créer une écriture
```

### **5. Third Parties**
```
✅ Get All Third Parties - Liste des tiers
✅ Create Third Party - Créer un tiers
```

### **6. Reports**
```
✅ Balance Sheet - Bilan comptable
✅ Income Statement - Compte de résultat
✅ Cash Flow Statement - Tableau de flux de trésorerie
```

### **7. AI Features**
```
✅ AI Analysis - Analyse IA
✅ AI Recommendations - Recommandations IA
```

### **8. HR Management**
```
✅ Get All Employees - Liste des employés
✅ Create Employee - Créer un employé
```

### **9. Asset Management**
```
✅ Get All Assets - Liste des actifs
✅ Create Asset - Créer un actif
```

### **10. Document Management**
```
✅ Get All Documents - Liste des documents
```

## 🎯 **TESTS AUTOMATISÉS**

### **Scripts de Test Inclus**
- **Auto-extraction du token** après login/register
- **Validation des réponses** HTTP
- **Gestion des erreurs** automatique

### **Codes de Réponse Attendus**
- `200` : Succès
- `201` : Créé avec succès
- `400` : Erreur de validation
- `401` : Non autorisé
- `403` : Accès interdit
- `404` : Ressource non trouvée
- `500` : Erreur serveur

## 🔍 **VÉRIFICATIONS À EFFECTUER**

### **Pour chaque endpoint :**
1. **Status Code** correct
2. **Response Time** < 2000ms
3. **Content-Type** : `application/json`
4. **Structure de réponse** cohérente

### **Données de Test**
- Utilisateur : `testuser` / `TestPassword123!`
- Email : `test@ecomptaia.com`
- Company ID : `1`
- Montants : En FCFA (devise locale)

## 🚨 **DÉPANNAGE**

### **Si le backend ne répond pas :**
```bash
# Vérifier les conteneurs Docker
docker-compose -f docker/docker-compose.yml ps

# Redémarrer si nécessaire
docker-compose -f docker/docker-compose.yml up -d backend
```

### **Si erreur 401/403 :**
1. Vérifier que le token est bien récupéré après login
2. Relancer "Login User" pour rafraîchir le token
3. Vérifier que l'environnement est bien sélectionné

### **Si erreur 500 :**
1. Vérifier les logs backend : `docker logs ecomptaia-backend`
2. Vérifier la connexion à la base de données
3. Vérifier que PostgreSQL est démarré

## 📈 **RÉSULTATS ATTENDUS**

### **Endpoints Publics (sans auth)**
- ✅ `/api/health`
- ✅ `/api/auth/register`
- ✅ `/api/auth/login`
- ✅ `/api/auth/test`
- ✅ `/api/auth/users/count`

### **Endpoints Protégés (avec auth)**
- ✅ Tous les endpoints `/api/dashboard/**`
- ✅ Tous les endpoints `/api/accounting/**`
- ✅ Tous les endpoints `/api/third-parties/**`
- ✅ Tous les endpoints `/api/reports/**`
- ✅ Tous les endpoints `/api/ai/**`
- ✅ Tous les endpoints `/api/hr/**`
- ✅ Tous les endpoints `/api/assets/**`
- ✅ Tous les endpoints `/api/documents/**`

## 🎉 **VALIDATION COMPLÈTE**

Une fois tous les tests passés avec succès :
1. **Backend** ✅ Fonctionnel
2. **Base de données** ✅ Connectée
3. **Authentification** ✅ Opérationnelle
4. **API** ✅ Complètement testée
5. **Prêt pour le frontend** ✅

---

**🔥 STACK E-COMPTA-IA ENTIÈREMENT VALIDÉE ! 🔥**
