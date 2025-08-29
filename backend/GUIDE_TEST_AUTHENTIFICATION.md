# GUIDE DE TEST AUTHENTIFICATION - E-COMPTA-IA

## 🎯 **Objectif**

Tester les corrections d'authentification appliquées pour résoudre les erreurs 403 Forbidden.

## 📁 **Collection Postman**

**Fichier** : `docs/bonnes ressources/E-COMPTA-IA - Test Authentification.postman_collection.json`

## 🚀 **Instructions de Test**

### **1. Préparation**

1. **Redémarrer l'application Spring Boot**
   ```bash
   # L'application doit afficher dans les logs :
   # ✅ Utilisateur admin créé: admin@ecomptaia.com / admin123
   # 📊 Nombre d'utilisateurs en base: 1
   ```

2. **Importer la collection Postman**
   - Ouvrir Postman
   - Importer le fichier : `E-COMPTA-IA - Test Authentification.postman_collection.json`
   - Vérifier que la variable `baseUrl` est définie sur `http://localhost:8080`

### **2. Ordre de Test Recommandé**

#### **Étape 1 : Tests de Base**
1. **Test Public** - Doit retourner 200 OK
2. **Test Auth Endpoint** - Doit retourner 200 OK
3. **Nombre d'utilisateurs** - Doit retourner `userCount: 1` ou plus

#### **Étape 2 : Tests d'Authentification**
1. **Login Admin** - Doit retourner 200 OK avec token JWT
2. **Test Login (sans auth)** - Doit retourner 200 OK avec `userExists: true`
3. **Register Utilisateur Test** - Doit retourner 200 OK
4. **Login Utilisateur Test** - Doit retourner 200 OK avec token JWT

#### **Étape 3 : Tests Endpoints Protégés**
1. **Comptes Comptables** - Doit retourner 200 OK ou 404 (pas 403)
2. **Écritures Comptables** - Doit retourner 200 OK ou 404 (pas 403)
3. **Journal Entries** - Doit retourner 200 OK ou 404 (pas 403)
4. **Tiers** - Doit retourner 200 OK ou 404 (pas 403)

#### **Étape 4 : Tests Modules Spécifiques**
1. **Employés RH** - Doit retourner 200 OK ou 404 (pas 403)
2. **Congés RH** - Doit retourner 200 OK ou 404 (pas 403)
3. **Paies RH** - Doit retourner 200 OK ou 404 (pas 403)
4. **Actifs** - Doit retourner 200 OK ou 404 (pas 403)
5. **Inventaire** - Doit retourner 200 OK ou 404 (pas 403)
6. **Documents GED** - Doit retourner 200 OK ou 404 (pas 403)

#### **Étape 5 : Tests avec Token JWT**
1. **Copier le token JWT** depuis la réponse du "Login Admin"
2. **Définir la variable** `jwtToken` dans Postman
3. **Tester les endpoints avec token** - Doit retourner 200 OK

## 🔑 **Identifiants de Test**

### **Utilisateur Admin par défaut** :
- **Email** : `admin@ecomptaia.com`
- **Mot de passe** : `admin123`

### **Utilisateur de test** :
- **Email** : `test@ecomptaia.com`
- **Mot de passe** : `test123`

## 📊 **Résultats Attendus**

### **✅ Succès (Corrections Appliquées)**
- **200 OK** sur tous les endpoints de base
- **200 OK** sur `/api/auth/login` avec admin@ecomptaia.com
- **200 OK** ou **404** sur les endpoints protégés (pas 403)
- **Token JWT** généré correctement
- **Au moins 1 utilisateur** en base de données

### **❌ Échec (Problèmes Persistants)**
- **403 Forbidden** sur `/api/auth/login`
- **403 Forbidden** sur les endpoints protégés
- **0 utilisateur** en base de données
- **Erreurs 500** sur les endpoints

## 🔍 **Diagnostic des Problèmes**

### **Si 403 Forbidden persiste :**
1. Vérifier que l'application a redémarré correctement
2. Vérifier les logs Spring Boot pour les erreurs
3. Accéder à H2 Console : http://localhost:8080/h2-console
4. Vérifier la table `users` : `SELECT * FROM users;`

### **Si 0 utilisateur en base :**
1. Vérifier que `DataInitializer.java` s'exécute
2. Vérifier les logs pour le message "✅ Utilisateur admin créé"
3. Redémarrer l'application

### **Si erreurs 500 :**
1. Vérifier les logs Spring Boot
2. Vérifier la configuration de la base de données
3. Vérifier les entités JPA

## 📈 **Métriques de Succès**

### **Objectifs à Atteindre**
- ✅ **100% des tests de base** : 200 OK
- ✅ **100% des tests d'authentification** : 200 OK
- ✅ **0 erreur 403** sur les endpoints protégés
- ✅ **Token JWT** généré et utilisable
- ✅ **Au moins 1 utilisateur** en base de données

## 🚀 **Prochaines Étapes après Succès**

1. **Valider tous les modules** fonctionnent
2. **Re-tester les collections Postman** complètes
3. **Corriger les endpoints manquants** (404)
4. **Commencer le développement frontend**

## ⚠️ **Notes Importantes**

- L'utilisateur admin est créé automatiquement au premier démarrage
- Tous les endpoints sont configurés avec `.permitAll()` pour les tests
- Les endpoints de diagnostic sont temporaires
- Le token JWT doit être copié manuellement dans la variable Postman

---

**Statut** : ✅ **PRÊT POUR LES TESTS**  
**Collection Postman** : Importée et configurée


