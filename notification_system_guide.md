# Guide Complet - Système de Notifications en Temps Réel

## 📢 **Système de Notifications Avancé**

### ✅ **Fonctionnalités Principales**

- **Notifications en temps réel** : Création et gestion instantanée
- **Gestion des préférences** : Personnalisation par utilisateur
- **Alertes automatiques** : Échéances, soldes, sécurité
- **Multi-canal** : Email, Push, In-app
- **Statistiques avancées** : Rapports et analyses
- **Expiration automatique** : Nettoyage des anciennes notifications

---

## 🧪 **Tests du Système de Notifications**

### **1. Informations sur le système**
```bash
GET http://localhost:8080/api/notifications/info
```

### **2. Créer une notification simple**
```bash
POST http://localhost:8080/api/notifications
Content-Type: application/json

{
    "userId": 1,
    "companyId": 1,
    "title": "Nouvelle écriture comptable",
    "message": "Une nouvelle écriture a été créée pour le compte 4010001",
    "type": "INFO",
    "category": "ACCOUNTING"
}
```

### **3. Créer une notification avancée**
```bash
POST http://localhost:8080/api/notifications
Content-Type: application/json

{
    "userId": 1,
    "companyId": 1,
    "title": "Échéance de paiement",
    "message": "Le paiement de 50000 XAF est dû le 31/12/2024",
    "type": "ALERT",
    "category": "THIRD_PARTY",
    "priority": "HIGH",
    "sourceModule": "THIRD_PARTY",
    "sourceId": "4110001",
    "actionUrl": "/third-parties/4110001",
    "actionType": "VIEW"
}
```

### **4. Récupérer les notifications d'un utilisateur**
```bash
GET http://localhost:8080/api/notifications/user/1?limit=10
```

### **5. Récupérer les notifications non lues**
```bash
GET http://localhost:8080/api/notifications/user/1/unread
```

### **6. Marquer une notification comme lue**
```bash
PUT http://localhost:8080/api/notifications/1/read
```

### **7. Marquer toutes les notifications comme lues**
```bash
PUT http://localhost:8080/api/notifications/user/1/read-all
```

### **8. Supprimer une notification**
```bash
DELETE http://localhost:8080/api/notifications/1
```

### **9. Statistiques des notifications**
```bash
GET http://localhost:8080/api/notifications/user/1/statistics
```

---

## ⚙️ **Gestion des Préférences**

### **10. Créer des préférences par défaut**
```bash
POST http://localhost:8080/api/notifications/user/1/preferences/default?companyId=1
```

### **11. Récupérer les préférences d'un utilisateur**
```bash
GET http://localhost:8080/api/notifications/user/1/preferences?companyId=1
```

### **12. Mettre à jour une préférence**
```bash
PUT http://localhost:8080/api/notifications/user/1/preferences
Content-Type: application/json

{
    "companyId": 1,
    "category": "ACCOUNTING",
    "type": "ALERT",
    "enabled": true,
    "emailEnabled": true,
    "pushEnabled": false,
    "inAppEnabled": true,
    "frequency": "IMMEDIATE",
    "priority": "HIGH"
}
```

---

## 🚨 **Alertes Automatiques**

### **13. Alerte d'échéance de paiement**
```bash
POST http://localhost:8080/api/notifications/alerts/payment-due
Content-Type: application/json

{
    "userId": 1,
    "companyId": 1,
    "thirdPartyName": "Entreprise ABC",
    "dueDate": "31/12/2024",
    "amount": "50000 XAF"
}
```

### **14. Alerte de solde négatif**
```bash
POST http://localhost:8080/api/notifications/alerts/negative-balance
Content-Type: application/json

{
    "userId": 1,
    "companyId": 1,
    "accountName": "Compte Bancaire Principal",
    "balance": "-25000 XAF"
}
```

### **15. Alerte de sécurité**
```bash
POST http://localhost:8080/api/notifications/alerts/security
Content-Type: application/json

{
    "userId": 1,
    "companyId": 1,
    "event": "Tentative de connexion échouée",
    "details": "5 tentatives de connexion depuis l'IP 192.168.1.100"
}
```

---

## 📊 **Types et Catégories de Notifications**

### **Types de Notifications :**
- **SYSTEM** : Notifications système
- **ALERT** : Alertes importantes
- **REMINDER** : Rappels
- **INFO** : Informations générales
- **WARNING** : Avertissements
- **ERROR** : Erreurs

### **Catégories de Notifications :**
- **ACCOUNTING** : Comptabilité
- **THIRD_PARTY** : Gestion des tiers
- **FINANCIAL** : Financier
- **SECURITY** : Sécurité
- **SYSTEM** : Système

### **Priorités :**
- **LOW** : Faible
- **MEDIUM** : Moyenne
- **HIGH** : Élevée
- **URGENT** : Urgente

### **Fréquences :**
- **IMMEDIATE** : Immédiate
- **DAILY** : Quotidienne
- **WEEKLY** : Hebdomadaire
- **NEVER** : Jamais

---

## 🔧 **Configuration et Personnalisation**

### **Préférences par Défaut :**
Le système crée automatiquement des préférences par défaut pour chaque utilisateur :
- **Toutes les notifications activées**
- **Email, Push et In-app activés**
- **Fréquence immédiate**
- **Priorité moyenne**

### **Gestion des Canaux :**
- **Email** : Notifications par email
- **Push** : Notifications push (à implémenter)
- **In-app** : Notifications dans l'application

### **Expiration Automatique :**
- Les notifications expirées sont automatiquement désactivées
- Nettoyage périodique des anciennes notifications

---

## 📈 **Statistiques et Rapports**

### **Métriques Disponibles :**
- **Total des notifications**
- **Notifications non lues**
- **Notifications urgentes**
- **Répartition par type**
- **Répartition par catégorie**

### **Exemple de Réponse Statistiques :**
```json
{
    "statistics": {
        "totalNotifications": 25,
        "unreadNotifications": 8,
        "urgentNotifications": 2,
        "systemNotifications": 5,
        "alertNotifications": 10,
        "reminderNotifications": 5,
        "infoNotifications": 5,
        "accountingNotifications": 12,
        "thirdPartyNotifications": 8,
        "financialNotifications": 3,
        "securityNotifications": 2
    }
}
```

---

## 🚀 **Intégration avec d'Autres Modules**

### **Intégration avec les Tiers :**
- Alertes d'échéances de paiement
- Notifications de création/modification
- Alertes de soldes

### **Intégration avec la Comptabilité :**
- Notifications de nouvelles écritures
- Alertes de soldes négatifs
- Rappels de validation

### **Intégration avec la Sécurité :**
- Alertes de tentatives de connexion
- Notifications de changements de mot de passe
- Alertes de violations de sécurité

---

## ✅ **Avantages du Système**

### **Flexibilité :**
- Configuration personnalisée par utilisateur
- Multi-canal de diffusion
- Types et catégories extensibles

### **Performance :**
- Notifications asynchrones
- Gestion optimisée de la base de données
- Nettoyage automatique

### **Sécurité :**
- Validation des préférences
- Contrôle d'accès par utilisateur
- Audit des notifications

### **Expérience Utilisateur :**
- Notifications en temps réel
- Interface intuitive
- Actions directes depuis les notifications

**Le système de notifications est maintenant complet et prêt à être utilisé !** 🎉




