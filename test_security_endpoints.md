# Test des endpoints POST - Système de Sécurité Avancé

## Prérequis
- Serveur Spring Boot démarré avec : `C:\Users\HP\maven\bin\mvn.cmd spring-boot:run`
- Postman ou curl disponible

## Tests des endpoints POST

### 1. Générer un code MFA
```bash
curl -X POST http://localhost:8080/api/advanced-security/mfa/generate \
  -H "Content-Type: application/json" \
  -d '{"username": "test@example.com"}'
```

### 2. Valider un code MFA
```bash
curl -X POST http://localhost:8080/api/advanced-security/mfa/validate \
  -H "Content-Type: application/json" \
  -d '{"username": "test@example.com", "code": "123456"}'
```

### 3. Créer une session sécurisée
```bash
curl -X POST http://localhost:8080/api/advanced-security/session/create \
  -H "Content-Type: application/json" \
  -d '{"username": "test@example.com", "password": "password123"}'
```

### 4. Valider une session
```bash
curl -X POST http://localhost:8080/api/advanced-security/session/validate \
  -H "Content-Type: application/json" \
  -d '{"sessionToken": "votre_token_de_session"}'
```

### 5. Invalider une session
```bash
curl -X POST http://localhost:8080/api/advanced-security/session/invalidate \
  -H "Content-Type: application/json" \
  -d '{"sessionToken": "votre_token_de_session"}'
```

### 6. Chiffrer des données
```bash
curl -X POST http://localhost:8080/api/advanced-security/encrypt \
  -H "Content-Type: application/json" \
  -d '{"data": "Données sensibles à chiffrer"}'
```

### 7. Déchiffrer des données
```bash
curl -X POST http://localhost:8080/api/advanced-security/decrypt \
  -H "Content-Type: application/json" \
  -d '{"encryptedData": "données_chiffrées_ici"}'
```

### 8. Vérifier la force d'un mot de passe
```bash
curl -X POST http://localhost:8080/api/advanced-security/password/strength \
  -H "Content-Type: application/json" \
  -d '{"password": "Test123!"}'
```

### 9. Analyser les tentatives de connexion
```bash
curl -X POST http://localhost:8080/api/advanced-security/analysis/login-attempts \
  -H "Content-Type: application/json" \
  -d '{"username": "test@example.com"}'
```

### 10. Nettoyer les sessions expirées
```bash
curl -X POST http://localhost:8080/api/advanced-security/cleanup/sessions \
  -H "Content-Type: application/json"
```

## Tests avec Postman

### Configuration Postman
1. **Method** : POST
2. **Headers** : `Content-Type: application/json`
3. **Body** : `raw` avec `JSON`

### Exemples de Body JSON

#### MFA Generate
```json
{
    "username": "test@example.com"
}
```

#### MFA Validate
```json
{
    "username": "test@example.com",
    "code": "123456"
}
```

#### Session Create
```json
{
    "username": "test@example.com",
    "password": "password123"
}
```

#### Encrypt
```json
{
    "data": "Données sensibles à chiffrer"
}
```

#### Password Strength
```json
{
    "password": "Test123!"
}
```

## Ordre de test recommandé

1. **MFA** : Generate → Validate
2. **Chiffrement** : Encrypt → Decrypt (utiliser les données chiffrées retournées)
3. **Password Strength**
4. **Sessions** : Create → Validate → Invalidate (utiliser le token retourné)
5. **Login Analysis**
6. **Cleanup**

## Dépannage

### Erreur 400 Bad Request
- Vérifier que le Content-Type est `application/json`
- Vérifier que le JSON est valide
- Vérifier que tous les champs requis sont présents

### Erreur 500 Internal Server Error
- Vérifier les logs du serveur Spring Boot
- Vérifier que la base de données est accessible
- Vérifier que tous les services sont correctement injectés




