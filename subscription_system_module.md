# Module 3 : Système de Souscription (Subscription System)

## Vue d'ensemble

Le **Système de Souscription** est un module complet qui gère les plans d'abonnement, les souscriptions des entreprises, les cycles de facturation, et les prix localisés selon la devise du pays client. Il permet de créer un modèle économique flexible et adapté aux différents marchés internationaux.

## Fonctionnalités principales

### 1. Gestion des Plans d'Abonnement
- **Création et gestion de plans** avec différents niveaux de service
- **Prix de base en USD** avec conversion automatique selon la devise locale
- **Cycles de facturation** : Mensuel, Trimestriel, Semestriel, Annuel
- **Limites configurables** : Utilisateurs, entreprises, stockage
- **Statuts de plan** : Actif, Inactif, Déprécié, Bêta

### 2. Gestion des Fonctionnalités par Plan
- **Types de fonctionnalités** : Module, Fonction, Limite, Intégration, Support, Personnalisation
- **Limites d'utilisation** configurables par fonctionnalité
- **Statuts de fonctionnalité** : Actif, Inactif, Déprécié, Bêta
- **Ordre de tri** pour l'affichage

### 3. Gestion des Souscriptions d'Entreprises
- **Souscriptions actives** avec dates de début et de fin
- **Prix localisés** selon la devise du client
- **Taux de change** automatiques via l'API de taux de change
- **Remises** configurables
- **Renouvellement automatique** optionnel
- **Statuts** : Actif, Suspendu, Annulé, Expiré, En attente, Essai

### 4. Suivi de l'Utilisation
- **Enregistrement automatique** de l'utilisation des fonctionnalités
- **Limites d'utilisation** avec alertes
- **Pourcentages d'utilisation** calculés automatiquement
- **Alertes** quand l'utilisation approche ou dépasse les limites

### 5. Gestion des Paiements
- **Enregistrement des paiements** avec différents statuts
- **Conversion automatique** en USD pour les rapports
- **Périodes de facturation** associées
- **Méthodes de paiement** multiples
- **Statuts** : En attente, Complété, Échoué, Annulé, Remboursé

### 6. Prix Localisés
- **Conversion automatique** selon la devise du client
- **Taux de change en temps réel** via l'API externe
- **Prix finaux** calculés avec remises appliquées
- **Support multi-devises** : USD, EUR, XOF, XAF, etc.

## Entités principales

### 1. SubscriptionPlan
```java
@Entity
@Table(name = "subscription_plans")
public class SubscriptionPlan {
    private Long id;
    private String planName;
    private String planCode;
    private String description;
    private BigDecimal basePriceUSD;
    private String currency;
    private BillingCycle billingCycle;
    private Integer maxUsers;
    private Integer maxCompanies;
    private Integer storageLimitGB;
    private Boolean isActive;
    private Boolean isFeatured;
    private Integer sortOrder;
    private PlanStatus status;
    // Relations
    private List<PlanFeature> features;
    private List<CompanySubscription> companySubscriptions;
}
```

### 2. PlanFeature
```java
@Entity
@Table(name = "plan_features")
public class PlanFeature {
    private Long id;
    private SubscriptionPlan subscriptionPlan;
    private String featureCode;
    private String featureName;
    private String description;
    private FeatureType featureType;
    private Boolean isIncluded;
    private Integer limitValue;
    private Integer sortOrder;
    private FeatureStatus status;
}
```

### 3. CompanySubscription
```java
@Entity
@Table(name = "company_subscriptions")
public class CompanySubscription {
    private Long id;
    private Company company;
    private SubscriptionPlan subscriptionPlan;
    private String subscriptionCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private BillingCycle billingCycle;
    private BigDecimal basePriceUSD;
    private BigDecimal localPrice;
    private String localCurrency;
    private BigDecimal exchangeRate;
    private BigDecimal discountPercentage;
    private BigDecimal finalPrice;
    private SubscriptionStatus status;
    private Boolean autoRenew;
    private String paymentMethod;
    private LocalDate lastPaymentDate;
    private LocalDate nextPaymentDate;
    private LocalDate cancellationDate;
    private String cancellationReason;
    // Relations
    private List<SubscriptionUsage> usageRecords;
    private List<SubscriptionPayment> payments;
}
```

### 4. SubscriptionUsage
```java
@Entity
@Table(name = "subscription_usage")
public class SubscriptionUsage {
    private Long id;
    private CompanySubscription companySubscription;
    private String featureCode;
    private String featureName;
    private LocalDate usageDate;
    private Integer usageCount;
    private Integer limitValue;
    private Double usagePercentage;
    private Boolean isOverLimit;
}
```

### 5. SubscriptionPayment
```java
@Entity
@Table(name = "subscription_payments")
public class SubscriptionPayment {
    private Long id;
    private CompanySubscription companySubscription;
    private String paymentCode;
    private LocalDate paymentDate;
    private BigDecimal amount;
    private String currency;
    private BigDecimal exchangeRate;
    private BigDecimal amountUSD;
    private String paymentMethod;
    private String transactionId;
    private PaymentStatus status;
    private LocalDate billingPeriodStart;
    private LocalDate billingPeriodEnd;
}
```

## Endpoints API

### Plans d'Abonnement

#### Créer un plan
```bash
POST /api/subscription/plans
Content-Type: application/json

{
  "planName": "Plan Starter",
  "planCode": "STARTER",
  "description": "Plan de démarrage pour les petites entreprises",
  "basePriceUSD": 29.99,
  "currency": "USD",
  "billingCycle": "MONTHLY",
  "maxUsers": 5,
  "maxCompanies": 2,
  "storageLimitGB": 10,
  "isFeatured": true,
  "sortOrder": 2
}
```

#### Obtenir tous les plans actifs
```bash
GET /api/subscription/plans
```

#### Obtenir un plan par code
```bash
GET /api/subscription/plans/STARTER
```

#### Obtenir les plans avec prix localisés
```bash
GET /api/subscription/plans/localized?currency=XOF
```

#### Obtenir les plans recommandés
```bash
GET /api/subscription/plans/recommended?companyId=1&maxUsers=10&maxCompanies=3
```

### Fonctionnalités

#### Ajouter une fonctionnalité à un plan
```bash
POST /api/subscription/plans/1/features
Content-Type: application/json

{
  "featureCode": "BASIC_ACCOUNTING",
  "featureName": "Comptabilité de base",
  "description": "Fonctionnalité de comptabilité de base",
  "featureType": "MODULE",
  "isIncluded": true,
  "limitValue": 1000,
  "sortOrder": 1
}
```

#### Obtenir les fonctionnalités d'un plan
```bash
GET /api/subscription/plans/1/features
```

#### Obtenir les fonctionnalités actives d'un plan
```bash
GET /api/subscription/plans/1/features/active
```

### Souscriptions

#### Créer une souscription
```bash
POST /api/subscription/subscriptions?companyId=1&planId=2&currency=XOF&discountPercentage=10.0
```

#### Obtenir les souscriptions d'une entreprise
```bash
GET /api/subscription/companies/1/subscriptions
```

#### Obtenir la souscription active d'une entreprise
```bash
GET /api/subscription/companies/1/subscriptions/active
```

#### Renouveler une souscription
```bash
POST /api/subscription/subscriptions/1/renew
```

#### Annuler une souscription
```bash
POST /api/subscription/subscriptions/1/cancel?reason=Coût trop élevé
```

### Utilisation

#### Enregistrer l'utilisation d'une fonctionnalité
```bash
POST /api/subscription/usage?subscriptionId=1&featureCode=BASIC_ACCOUNTING&featureName=Comptabilité de base&usageCount=5
```

#### Obtenir l'utilisation d'une entreprise
```bash
GET /api/subscription/companies/1/usage
```

#### Obtenir les alertes d'utilisation
```bash
GET /api/subscription/usage/alerts
```

### Paiements

#### Enregistrer un paiement
```bash
POST /api/subscription/payments?subscriptionId=1&amount=29990&currency=XOF&paymentMethod=CARTE_BANCAIRE&transactionId=TX123456
```

#### Obtenir les paiements d'une entreprise
```bash
GET /api/subscription/companies/1/payments
```

### Statistiques

#### Statistiques des plans
```bash
GET /api/subscription/statistics/plans
```

#### Statistiques des souscriptions
```bash
GET /api/subscription/statistics/subscriptions
```

#### Statistiques des paiements
```bash
GET /api/subscription/statistics/payments
```

#### Statistiques d'utilisation
```bash
GET /api/subscription/statistics/usage
```

### Vérifications

#### Vérifier la disponibilité d'une fonctionnalité
```bash
GET /api/subscription/check/feature?companyId=1&featureCode=BASIC_ACCOUNTING
```

#### Vérifier les limites d'utilisation
```bash
GET /api/subscription/check/usage-limit?companyId=1&featureCode=BASIC_ACCOUNTING
```

### Test et Information

#### Test du module
```bash
GET /api/subscription/test
```

#### Informations du module
```bash
GET /api/subscription/info
```

## Plans d'Abonnement Préconfigurés

### 1. Plan Gratuit (FREE)
- **Prix** : 0.00 USD
- **Utilisateurs** : 1
- **Entreprises** : 1
- **Stockage** : 1 GB
- **Fonctionnalités** : Comptabilité de base, Rapports de base, Support email

### 2. Plan Starter (STARTER)
- **Prix** : 29.99 USD
- **Utilisateurs** : 5
- **Entreprises** : 2
- **Stockage** : 10 GB
- **Fonctionnalités** : Comptabilité de base, Rapports avancés, Support chat, Export de base

### 3. Plan Professional (PROFESSIONAL)
- **Prix** : 99.99 USD
- **Utilisateurs** : 20
- **Entreprises** : 5
- **Stockage** : 50 GB
- **Fonctionnalités** : Comptabilité avancée, Rapports personnalisés, Support téléphonique, API, IA de base

### 4. Plan Enterprise (ENTERPRISE)
- **Prix** : 299.99 USD
- **Utilisateurs** : 100
- **Entreprises** : 20
- **Stockage** : 200 GB
- **Fonctionnalités** : Comptabilité entreprise, Support prioritaire, IA avancée, Intégrations personnalisées, Marque blanche

### 5. Plan OHADA Premium (OHADA_PREMIUM)
- **Prix** : 199.99 USD
- **Utilisateurs** : 50
- **Entreprises** : 10
- **Stockage** : 100 GB
- **Fonctionnalités** : Conformité OHADA, Standards SYSCOHADA, Comptabilité SMT, Support OHADA spécialisé, Multi-devises, Taxes locales

## Fonctionnalités par Type

### Modules
- **BASIC_ACCOUNTING** : Comptabilité de base
- **ADVANCED_ACCOUNTING** : Comptabilité avancée
- **ENTERPRISE_ACCOUNTING** : Comptabilité entreprise
- **OHADA_COMPLIANCE** : Conformité OHADA
- **SYSCOHADA_STANDARDS** : Standards SYSCOHADA
- **SMT_ACCOUNTING** : Comptabilité SMT

### Fonctions
- **BASIC_REPORTS** : Rapports de base
- **ADVANCED_REPORTS** : Rapports avancés
- **CUSTOM_REPORTS** : Rapports personnalisés
- **OHADA_REPORTS** : Rapports OHADA
- **BASIC_EXPORT** : Export de base
- **ADVANCED_EXPORT** : Export avancé
- **BASIC_AI** : IA de base
- **ADVANCED_AI** : IA avancée
- **OHADA_AI** : IA spécialisée OHADA
- **MULTI_CURRENCY** : Multi-devises
- **LOCAL_TAXES** : Taxes locales

### Limites
- **BASIC_USERS** : Utilisateurs de base
- **BASIC_STORAGE** : Stockage de base

### Support
- **EMAIL_SUPPORT** : Support par email
- **CHAT_SUPPORT** : Support par chat
- **PHONE_SUPPORT** : Support téléphonique
- **PRIORITY_SUPPORT** : Support prioritaire
- **OHADA_SUPPORT** : Support OHADA spécialisé

### Intégrations
- **API_ACCESS** : Accès API
- **CUSTOM_INTEGRATION** : Intégrations personnalisées

### Personnalisation
- **WHITE_LABEL** : Marque blanche

## Exemples d'Utilisation

### 1. Créer une souscription pour une entreprise OHADA
```bash
# Obtenir les plans localisés en XOF
curl -X GET "http://localhost:8080/api/subscription/plans/localized?currency=XOF"

# Créer une souscription OHADA Premium
curl -X POST "http://localhost:8080/api/subscription/subscriptions?companyId=1&planId=5&currency=XOF&discountPercentage=15.0"
```

### 2. Vérifier l'utilisation d'une fonctionnalité
```bash
# Vérifier la disponibilité
curl -X GET "http://localhost:8080/api/subscription/check/feature?companyId=1&featureCode=OHADA_COMPLIANCE"

# Vérifier les limites
curl -X GET "http://localhost:8080/api/subscription/check/usage-limit?companyId=1&featureCode=BASIC_ACCOUNTING"
```

### 3. Enregistrer l'utilisation
```bash
# Enregistrer l'utilisation de la comptabilité
curl -X POST "http://localhost:8080/api/subscription/usage?subscriptionId=1&featureCode=BASIC_ACCOUNTING&featureName=Comptabilité de base&usageCount=10"
```

### 4. Obtenir les statistiques
```bash
# Statistiques des souscriptions
curl -X GET "http://localhost:8080/api/subscription/statistics/subscriptions"

# Statistiques des paiements
curl -X GET "http://localhost:8080/api/subscription/statistics/payments"
```

## Configuration

### 1. Taux de Change
Le module utilise le service `ExchangeRateService` pour obtenir les taux de change en temps réel :
- **API** : exchangerate-api.com
- **Devises supportées** : USD, EUR, XOF, XAF, etc.
- **Cache** : 1 heure pour éviter les appels API excessifs

### 2. Cycles de Facturation
- **MONTHLY** : Mensuel
- **QUARTERLY** : Trimestriel
- **SEMI_ANNUAL** : Semestriel
- **ANNUAL** : Annuel

### 3. Statuts de Souscription
- **ACTIVE** : Actif
- **SUSPENDED** : Suspendu
- **CANCELLED** : Annulé
- **EXPIRED** : Expiré
- **PENDING** : En attente
- **TRIAL** : Essai

### 4. Statuts de Paiement
- **PENDING** : En attente
- **COMPLETED** : Complété
- **FAILED** : Échoué
- **CANCELLED** : Annulé
- **REFUNDED** : Remboursé
- **PARTIALLY_REFUNDED** : Partiellement remboursé

## Sécurité

### 1. Accès aux Endpoints
Tous les endpoints du module sont accessibles sans authentification pour les tests :
```java
.requestMatchers("/api/subscription/**").permitAll()
```

### 2. Validation des Données
- Validation des plans avant création
- Vérification des limites d'utilisation
- Contrôle des dates de souscription
- Validation des montants de paiement

### 3. Intégrité des Données
- Contraintes de clés étrangères
- Validation des statuts
- Calculs automatiques des prix
- Gestion des versions d'entités

## Tests

### 1. Test du Module
```bash
curl -X GET "http://localhost:8080/api/subscription/test"
```

### 2. Informations du Module
```bash
curl -X GET "http://localhost:8080/api/subscription/info"
```

### 3. Test Complet
```bash
# 1. Vérifier les plans
curl -X GET "http://localhost:8080/api/subscription/plans"

# 2. Vérifier les plans localisés
curl -X GET "http://localhost:8080/api/subscription/plans/localized?currency=XOF"

# 3. Vérifier les statistiques
curl -X GET "http://localhost:8080/api/subscription/statistics/plans"
```

## Intégration avec d'Autres Modules

### 1. Module International
- Utilisation des devises locales
- Intégration avec les taux de change
- Support des pays OHADA

### 2. Module SMT
- Plans spécialisés OHADA
- Fonctionnalités SMT
- Conformité aux standards

### 3. Module AI
- Fonctionnalités IA par plan
- Limites d'utilisation IA
- IA spécialisée OHADA

## Évolutions Futures

### 1. Fonctionnalités Planifiées
- **Facturation automatique** : Génération automatique des factures
- **Paiements récurrents** : Gestion des paiements automatiques
- **Promotions** : Codes promo et offres spéciales
- **Upgrade/Downgrade** : Changement de plan en cours d'abonnement
- **Période d'essai** : Gestion des essais gratuits

### 2. Améliorations Techniques
- **Webhooks** : Notifications en temps réel
- **API GraphQL** : Interface de requête plus flexible
- **Microservices** : Séparation en services indépendants
- **Cache Redis** : Amélioration des performances
- **Monitoring** : Métriques et alertes avancées

## Conclusion

Le **Système de Souscription** fournit une base solide pour la monétisation de la plateforme E-COMPTA-IA. Il permet une gestion flexible des plans d'abonnement avec support multi-devises, suivi de l'utilisation, et intégration avec les standards OHADA. Le module est conçu pour évoluer avec les besoins de l'entreprise et supporter une croissance internationale.




