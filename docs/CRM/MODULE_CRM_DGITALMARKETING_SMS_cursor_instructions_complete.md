## 🔧 INSTRUCTIONS CURSOR CORRIGÉES - ARCHITECTURE MODULE

### 1. Setup Structure Packages Cohérente

```bash
# @cursor: Créer module CRM au même niveau que les autres
cd backend/src/main/java/com/ecomptaia

# Vérifier modules existants
ls -la
# Doit afficher: accounting/, hr/, asset/, subscription/, security/, config/, entity/, exception/

# Créer module CRM au même niveau
mkdir -p crm/{controller,service,repository,entity,dto,config,exception}
mkdir -p crm/channels/{email,sms,social}
mkdir -p crm/integration
mkdir -p crm/automation
mkdir -p crm/analytics
mkdir -p crm/ai

# Vérifier structure finale
ls -la crm/
```

### 2. Frontend Structure Cohérente

```bash
# @cursor: Dans frontend/src/app/
cd frontend/src/app

# Vérifier modules existants
ls -la
# Doit afficher: accounting/, hr/, asset-inventory/, subscription/, shared/, core/

# Créer module CRM au même niveau
ng generate module crm --routing
mkdir -p crm/{components,services,models,guards}
mkdir -p crm/components/{dashboard,customers,campaigns,analytics,integrations}

# Vérifier structure
ls -la crm/
```

### 3. Configuration Application Cohérente

```yaml
# @cursor: Dans application.yml, intégrer section CRM
ecomptaia:
  modules:
    accounting:
      enabled: true
    hr:  
      enabled: true
    asset:
      enabled: true
    subscription:
      enabled: true
    crm:                    # Nouveau module au même niveau
      enabled: true
      digital-marketing:
        email:
          providers:
            sendgrid:
              api-key: ${SENDGRID_API_KEY:}
            mailchimp:
              api-key: ${MAILCHIMP_API_KEY:}
        sms:
          providers:
            twilio:
              account-sid: ${TWILIO_ACCOUNT_SID:}
              auth-token: ${TWILIO_AUTH_TOKEN:}
            orange:
              client-id: ${ORANGE_CLIENT_ID:}
              client-secret: ${ORANGE_CLIENT_SECRET:}
        social:
          providers:
            facebook:
              app-id: ${FACEBOOK_APP_ID:}
              app-secret: ${FACEBOOK_APP_SECRET:}
            linkedin:
              client-id: ${LINKEDIN_CLIENT_ID:}
              client-secret: ${LINKEDIN_CLIENT_SECRET:}
        integrations:
          hubspot:
            api-key: ${HUBSPOT_API_KEY:}
          salesforce:
            client-id: ${SALESFORCE_CLIENT_ID:}
            client-secret: ${SALESFORCE_CLIENT_SECRET:}
        ai:
          openai:
            api-key: ${OPENAI_API_KEY:}
          enabled: true
```

### 4. Génération Entités Alignées

```java
// @cursor: CrmCustomer.java dans crm/entity/
package com.ecomptaia.crm.entity;

import com.ecomptaia.entity.Company;      // Entité partagée existante
import com.ecomptaia.entity.ThirdParty;   // Entité partagée existante
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import com.vladmihalcea.hibernate.type.json.JsonType;

@Entity
@Table(name = "crm_customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrmCustomer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company; // Référence entité Company existante
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "third_party_id")
    private ThirdParty thirdParty; // Référence entité ThirdParty existante
    
    // Champs spécifiques CRM
    @Column(name = "ai_score")
    private Integer aiScore;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "customer_segment")
    private CustomerSegment customerSegment;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_behavior")
    private PaymentBehavior paymentBehavior;
    
    @Column(name = "churn_probability", precision = 5, scale = 4)
    private BigDecimal churnProbability;
    
    @Column(name = "lifetime_value_predicted", precision = 15, scale = 2)
    private BigDecimal lifetimeValuePredicted;
    
    @Column(name = "satisfaction_score")
    private Integer satisfactionScore;
    
    // Préférences communication multi-canal
    @Type(JsonType.class)
    @Column(name = "communication_preferences", columnDefinition = "jsonb")
    private CommunicationPreferences communicationPreferences;
    
    @Column(name = "email_opt_in")
    private Boolean emailOptIn = true;
    
    @Column(name = "sms_opt_in")
    private Boolean smsOptIn = true;
    
    @Type(JsonType.class)
    @Column(name = "social_handles", columnDefinition = "jsonb")
    private Map<String, String> socialHandles = new HashMap<>();
    
    @Type(JsonType.class)
    @Column(name = "external_ids", columnDefinition = "jsonb")
    private Map<String, String> externalIds = new HashMap<>();
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

### 5. Services Intégrés avec Services Existants

```java
// @cursor: CrmCustomerService.java dans crm/service/
package com.ecomptaia.crm.service;

import com.ecomptaia.crm.entity.CrmCustomer;
import com.ecomptaia.crm.repository.CrmCustomerRepository;
import com.ecomptaia.service.ThirdPartyService;        // Service existant
import com.ecomptaia.service.CompanyService;           // Service existant  
import com.ecomptaia.subscription.service.SubscriptionService; // Service existant
import com.ecomptaia.accounting.service.EcritureComptableService; // Service existant
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CrmCustomerService {
    
    private final CrmCustomerRepository crmCustomerRepository;
    private final ThirdPartyService thirdPartyService;           // Injection service existant
    private final CompanyService companyService;                 // Injection service existant
    private final SubscriptionService subscriptionService;       // Injection service existant
    private final EcritureComptableService ecritureService;     // Injection service existant
    private final CustomerIntelligenceService intelligenceService;
    
    /**
     * Création profil CRM à partir ThirdParty existant
     */
    public CrmCustomer createFromThirdParty(UUID thirdPartyId) {
        ThirdParty thirdParty = thirdPartyService.findById(thirdPartyId);
        
        // Vérification si profil existe déjà
        Optional<CrmCustomer> existing = crmCustomerRepository
            .findByThirdPartyId(thirdPartyId);
        if (existing.isPresent()) {
            return existing.get();
        }
        
        // Calcul intelligence initiale basée sur données comptables
        CustomerIntelligence intelligence = intelligenceService
            .calculateFromAccountingData(thirdParty);
        
        CrmCustomer crmCustomer = CrmCustomer.builder()
            .company(thirdParty.getCompany())
            .thirdParty(thirdParty)
            .aiScore(intelligence.getAiScore())
            .customerSegment(intelligence.getSegment())
            .churnProbability(intelligence.getChurnProbability())
            .lifetimeValuePredicted(intelligence.getPredictedLTV())
            .build();
            
        return crmCustomerRepository.save(crmCustomer);
    }
    
    /**
     * Intégration avec système abonnements existant
     */
    public void checkCrmFeatureAccess(UUID companyId, CrmFeature feature) {
        if (!subscriptionService.hasFeatureAccess(companyId, "CRM_" + feature.name())) {
            throw new FeatureNotAvailableException("CRM feature " + feature + " not available for company " + companyId);
        }
    }
    
    /**
     * Mise à jour intelligence basée sur nouvelles écritures comptables
     */
    @EventListener
    public void onAccountingEntryCreated(EcritureComptableCreatedEvent event) {
        // Mise à jour automatique du scoring client quand nouvelle écriture
        if (event.getThirdPartyId() != null) {
            Optional<CrmCustomer> customer = crmCustomerRepository
                .findByThirdPartyId(event.getThirdPartyId());
            
            if (customer.isPresent()) {
                updateCustomerIntelligence(customer.get());
            }
        }
    }
}
```

### 6. Extension Entités Existantes

```java
// @cursor: Extension PlanFeature existant dans subscription/entity/
@Entity
@Table(name = "plan_features")
public class PlanFeature {
    // Champs existants...
    
    // AJOUT: Fonctionnalités CRM
    @Column(name = "crm_enabled")
    private Boolean crmEnabled = false;
    
    @Column(name = "crm_contacts_limit")
    private Integer crmContactsLimit;
    
    @Column(name = "email_campaigns_monthly_limit")
    private Integer emailCampaignsMonthlyLimit;
    
    @Column(name = "sms_monthly_limit")
    private Integer smsMonthlyLimit;
    
    @Column(name = "social_posts_monthly_limit")
    private Integer socialPostsMonthlyLimit;
    
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "plan_crm_features")
    private Set<CrmFeature> enabledCrmFeatures = new HashSet<>();
    
    @Column(name = "external_integrations_limit")
    private Integer externalIntegrationsLimit;
    
    @Column(name = "ai_features_enabled")
    private Boolean aiFeaturesEnabled = false;
    
    // Getters/setters...
}

// @cursor: Enum CrmFeature
public enum CrmFeature {
    CUSTOMER_PROFILES,
    EMAIL_MARKETING,
    SMS_MARKETING,
    SOCIAL_MEDIA_POSTING,
    MARKETING_AUTOMATION,
    CUSTOMER_ANALYTICS,
    AI_SCORING,
    CHURN_PREDICTION,
    EXTERNAL_INTEGRATIONS,
    ADVANCED_REPORTING
}
```

### 7. Contrôleurs Cohérents avec Architecture REST

```java
// @cursor: CrmCustomerController.java dans crm/controller/
package com.ecomptaia.crm.controller;

import com.ecomptaia.controller.BaseController;   // Contrôleur de base existant
import com.ecomptaia.crm.service.CrmCustomerService;
import com.ecomptaia.crm.entity.CrmCustomer;
import com.ecomptaia.crm.dto.CreateCrmCustomerRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/crm/customers")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CrmCustomerController extends BaseController {
    
    private final CrmCustomerService crmCustomerService;
    
    @GetMapping
    public ResponseEntity<List<CrmCustomer>> getCustomers() {
        UUID companyId = getCurrentCompanyId(); // Méthode héritée BaseController
        List<CrmCustomer> customers = crmCustomerService.findByCompanyId(companyId);
        return ResponseEntity.ok(customers);
    }
    
    @PostMapping("/from-third-party/{thirdPartyId}")
    public ResponseEntity<CrmCustomer> createFromThirdParty(@PathVariable UUID thirdPartyId) {
        UUID companyId = getCurrentCompanyId();
        
        // Vérification accès fonctionnalité CRM
        crmCustomerService.checkCrmFeatureAccess(companyId, CrmFeature.CUSTOMER_PROFILES);
        
        CrmCustomer customer = crmCustomerService.createFromThirdParty(thirdPartyId);
        return ResponseEntity.ok(customer);
    }
    
    @PutMapping("/{customerId}/update-intelligence")
    public ResponseEntity<CrmCustomer> updateIntelligence(@PathVariable UUID customerId) {
        UUID companyId = getCurrentCompanyId();
        
        // Vérification accès IA
        crmCustomerService.checkCrmFeatureAccess(companyId, CrmFeature.AI_SCORING);
        
        CrmCustomer customer = crmCustomerService.updateCustomerIntelligence(customerId);
        return ResponseEntity.ok(customer);
    }
}
```

### 8. Frontend Module Intégré

```typescript
// @cursor: crm.module.ts dans frontend/src/app/crm/
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../shared/shared.module';     // Module partagé existant
import { CoreModule } from '../core/core.module';           // Module core existant

import { CrmRoutingModule } from './crm-routing.module';
import { CrmDashboardComponent } from './components/dashboard/crm-dashboard.component';
import { CustomerListComponent } from './components/customers/customer-list.component';
import { CampaignManagerComponent } from './components/campaigns/campaign-manager.component';

@NgModule({
  declarations: [
    CrmDashboardComponent,
    CustomerListComponent,
    CampaignManagerComponent
  ],
  imports: [
    CommonModule,
    SharedModule,    // Réutilise composants UI existants
    CoreModule,      // Réutilise services core existants
    CrmRoutingModule
  ]
})
export class CrmModule { }
```

### 9. Services Angular Intégrés

```typescript
// @cursor: crm-customer.service.ts dans crm/services/
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiService } from '../../core/services/api.service'; // Service existant

@Injectable({
  providedIn: 'root'
})
export class CrmCustomerService extends ApiService {
  
  private readonly CRM_API = '/api/crm/customers';
  
  constructor(http: HttpClient) {
```typescript
// @cursor: crm-customer.service.ts dans crm/services/
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiService } from '../../core/services/api.service'; // Service existant
import { CrmCustomer } from '../models/crm-customer.model';

@Injectable({
  providedIn: 'root'
})
export class CrmCustomerService extends ApiService {
  
  private readonly CRM_API = '/api/crm/customers';
  
  constructor(http: HttpClient) {
    super(http); // Hérite des méthodes communes (auth, error handling, etc.)
  }
  
  /**
   * Récupération clients CRM avec réutilisation logique existante
   */
  getCustomers(): Observable<CrmCustomer[]> {
    return this.get<CrmCustomer[]>(this.CRM_API);
  }
  
  /**
   * Création client CRM depuis ThirdParty existant
   */
  createFromThirdParty(thirdPartyId: string): Observable<CrmCustomer> {
    return this.post<CrmCustomer>(`${this.CRM_API}/from-third-party/${thirdPartyId}`, {});
  }
  
  /**
   * Mise à jour intelligence client
   */
  updateIntelligence(customerId: string): Observable<CrmCustomer> {
    return this.put<CrmCustomer>(`${this.CRM_API}/${customerId}/update-intelligence`, {});
  }
}
```

### 10. Routes Intégrées avec Architecture Existante

```typescript
// @cursor: Dans app-routing.module.ts principal, ajouter route CRM
const routes: Routes = [
  { path: 'accounting', loadChildren: () => import('./accounting/accounting.module').then(m => m.AccountingModule) },
  { path: 'hr', loadChildren: () => import('./hr/hr.module').then(m => m.HrModule) },
  { path: 'asset-inventory', loadChildren: () => import('./asset-inventory/asset-inventory.module').then(m => m.AssetInventoryModule) },
  { path: 'subscription', loadChildren: () => import('./subscription/subscription.module').then(m => m.SubscriptionModule) },
  { path: 'crm', loadChildren: () => import('./crm/crm.module').then(m => m.CrmModule) }, // NOUVEAU
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' }
];
```

---

## 📊 COMMANDES CURSOR SPÉCIFIQUES ARCHITECTURE COHÉRENTE

### Génération Respectant Structure Existante

```bash
# @cursor: Commandes Ctrl+K spécialisées pour architecture cohérente

"Generate CRM module entities that extend and reference existing Company and ThirdParty entities from the main entity package"

"Create CRM services that inject and use existing services like ThirdPartyService, CompanyService, and SubscriptionService"

"Generate CRM controllers that extend BaseController and follow the same REST API patterns as existing modules"

"Create CRM repositories using the same JPA patterns and naming conventions as existing modules"

"Generate CRM DTOs following the same validation and serialization patterns as existing modules"

"Create CRM configuration classes that integrate with existing application configuration structure"

"Generate CRM exception handling that extends existing global exception handling"

"Create Angular CRM module that uses existing SharedModule and CoreModule dependencies"

"Generate CRM services that extend existing ApiService and follow the same HTTP patterns"

"Create CRM components that use existing UI components from SharedModule"
```

### Intégration avec Modules Existants

```bash
# @cursor: Commandes d'intégration

"Extend existing PlanFeature entity to include CRM features without breaking existing functionality"

"Update existing SubscriptionService to include CRM feature checking methods"

"Create event listeners in CRM module that respond to existing accounting events"

"Generate CRM navigation items that integrate with existing sidebar navigation"

"Create CRM dashboard widgets that follow existing dashboard component patterns"

"Update existing user permissions to include CRM role-based access control"

"Generate CRM audit logging that uses existing audit framework"

"Create CRM notification integration with existing notification system"
```

### Migration et Compatibilité

```bash
# @cursor: Scripts de migration

"Generate database migration scripts that add CRM tables without affecting existing schema"

"Create data migration scripts to populate CRM customer profiles from existing ThirdParty data"

"Generate backward compatibility checks for existing API endpoints"

"Create feature flag system for gradual CRM module rollout"

"Generate rollback scripts for CRM module removal if needed"
```

---

## 🧪 TESTS COHÉRENTS AVEC ARCHITECTURE

### Tests Backend Intégrés

```java
// @cursor: CrmIntegrationTest.java dans src/test/java/com/ecomptaia/crm/
package com.ecomptaia.crm;

import com.ecomptaia.crm.service.CrmCustomerService;
import com.ecomptaia.service.ThirdPartyService;
import com.ecomptaia.subscription.service.SubscriptionService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class CrmIntegrationTest {
    
    @Autowired
    private CrmCustomerService crmCustomerService;
    
    @Autowired
    private ThirdPartyService thirdPartyService; // Service existant
    
    @Autowired
    private SubscriptionService subscriptionService; // Service existant
    
    @Test
    void testCrmCustomerCreationFromExistingThirdParty() {
        // Test intégration avec données existantes
        UUID thirdPartyId = createTestThirdParty();
        CrmCustomer customer = crmCustomerService.createFromThirdParty(thirdPartyId);
        
        assertThat(customer).isNotNull();
        assertThat(customer.getThirdParty().getId()).isEqualTo(thirdPartyId);
    }
    
    @Test
    void testCrmFeatureAccessWithExistingSubscription() {
        // Test intégration avec système d'abonnement existant
        UUID companyId = createTestCompany();
        setupCrmSubscription(companyId);
        
        assertDoesNotThrow(() -> 
            crmCustomerService.checkCrmFeatureAccess(companyId, CrmFeature.CUSTOMER_PROFILES)
        );
    }
}
```

### Tests Frontend Cohérents

```typescript
// @cursor: crm-customer.service.spec.ts
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CrmCustomerService } from './crm-customer.service';
import { ApiService } from '../../core/services/api.service'; // Service existant

describe('CrmCustomerService', () => {
  let service: CrmCustomerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CrmCustomerService]
    });
    service = TestBed.inject(CrmCustomerService);
  });

  it('should extend ApiService correctly', () => {
    expect(service).toBeInstanceOf(ApiService);
  });

  it('should create customer from third party', () => {
    // Test intégration avec API existante
  });
});
```

---

## 📦 DÉPLOIEMENT COHÉRENT

### Configuration Docker Intégrée

```dockerfile
# @cursor: Aucune modification Dockerfile nécessaire
# Le module CRM s'intègre naturellement au JAR existant
```

### Variables Environnement Organisées

```bash
# @cursor: .env organisé par modules
# Modules existants
POSTGRES_DB=ecomptaia
POSTGRES_USER=ecomptaia
POSTGRES_PASSWORD=password

# Module CRM (nouveau)
SENDGRID_API_KEY=your_sendgrid_key
TWILIO_ACCOUNT_SID=your_twilio_sid
TWILIO_AUTH_TOKEN=your_twilio_token
FACEBOOK_APP_ID=your_facebook_id
HUBSPOT_API_KEY=your_hubspot_key
OPENAI_API_KEY=your_openai_key
```

---

## ✅ CHECKLIST ARCHITECTURE COHÉRENTE

### Vérifications Structure
- [ ] Module CRM au même niveau que hr/, accounting/, asset/
- [ ] Pas de duplication avec modules existants
- [ ] Réutilisation services existants (ThirdPartyService, CompanyService)
- [ ] Extension entités existantes sans casser compatibilité
- [ ] Contrôleurs suivent patterns REST existants
- [ ] Frontend utilise SharedModule et CoreModule existants

### Vérifications Intégration
- [ ] Events comptables déclenchent actions CRM
- [ ] Système abonnements gère fonctionnalités CRM
- [ ] Navigation intégrée avec sidebar existante
- [ ] Permissions utilisateurs étendues pour CRM
- [ ] Audit trail utilise framework existant
- [ ] Notifications intégrées au système existant

### Vérifications Technique
- [ ] Même patterns JPA que modules existants
- [ ] Même structure DTOs et validation
- [ ] Configuration cohérente avec application.yml
- [ ] Tests suivent même framework que modules existants
- [ ] Déploiement transparent sans modification infrastructure

Cette architecture cohérente garantit que le module CRM-Digital Marketing s'intègre parfaitement à votre plateforme existante sans disruption, tout en ajoutant une valeur business significative.# 🎯 INSTRUCTIONS CURSOR COMPLÈTES
## Module CRM-Digital Marketing E-COMPTA-IA

---

## 📋 PHASE 1: SETUP INITIAL (Jour 1)

### 1. Préparation Environnement
```bash
# @cursor: Ouvrir le projet E-COMPTA-IA dans Cursor
# @cursor: Créer nouvelle branche
git checkout -b feature/crm-digital-marketing-module
git push -u origin feature/crm-digital-marketing-module

# @cursor: Vérifier structure projet
pwd
ls -la backend/
ls -la frontend/
```

### 2. Mise à jour Dependencies
```xml
<!-- @cursor: Dans backend/pom.xml, ajouter après les dépendances existantes -->
<!-- AVANT: Localiser la section <dependencies> existante -->
<!-- AJOUTER: Ces nouvelles dépendances -->

<!-- Email Marketing -->
<dependency>
    <groupId>com.sendgrid</groupId>
    <artifactId>sendgrid-java</artifactId>
    <version>4.9.3</version>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>

<!-- SMS Marketing -->
<dependency>
    <groupId>com.twilio.sdk</groupId>
    <artifactId>twilio</artifactId>
    <version>9.14.1</version>
</dependency>

<!-- Social Media -->
<dependency>
    <groupId>com.restfb</groupId>
    <artifactId>restfb</artifactId>
    <version>2023.8.0</version>
</dependency>

<!-- Cache et Performance -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>

<!-- JSON Processing -->
<dependency>
    <groupId>com.vladmihalcea</groupId>
    <artifactId>hibernate-types-60</artifactId>
    <version>2.21.1</version>
</dependency>

<!-- Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

### 3. Structure Packages Backend
```bash
# @cursor: Créer structure packages dans backend/src/main/java/com/ecomptaia/
mkdir -p crm/entity
mkdir -p crm/repository  
mkdir -p crm/service
mkdir -p crm/controller
mkdir -p crm/dto
mkdir -p crm/config
mkdir -p crm/channels/email
mkdir -p crm/channels/sms
mkdir -p crm/channels/social
mkdir -p crm/integration
mkdir -p crm/automation
mkdir -p crm/analytics
mkdir -p crm/ai

# @cursor: Vérifier création
ls -la src/main/java/com/ecomptaia/crm/
```

---

## 📊 PHASE 2: BASE DE DONNÉES (Jour 2)

### 1. Scripts SQL à Exécuter
```sql
-- @cursor: Se connecter à PostgreSQL
-- @cursor: Utiliser Ctrl+Shift+P -> "Database: Connect"
-- @cursor: Ou utiliser psql en ligne de commande

-- SCRIPT 1: Extensions et fonctions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- SCRIPT 2: Tables principales CRM (copier le SQL complet du document)
-- @cursor: Exécuter tous les CREATE TABLE du document principal

-- SCRIPT 3: Index de performance
CREATE INDEX CONCURRENTLY idx_crm_customer_company ON crm_customer_profiles(company_id);
CREATE INDEX CONCURRENTLY idx_crm_customer_segment ON crm_customer_profiles(customer_segment);
CREATE INDEX CONCURRENTLY idx_crm_customer_ai_score ON crm_customer_profiles(ai_score DESC);
CREATE INDEX CONCURRENTLY idx_marketing_campaigns_company ON marketing_campaigns(company_id);
CREATE INDEX CONCURRENTLY idx_marketing_messages_campaign ON marketing_messages(campaign_id);
CREATE INDEX CONCURRENTLY idx_marketing_messages_channel ON marketing_messages(channel, status);

-- SCRIPT 4: Données initiales
INSERT INTO marketing_providers (provider_type, platform_name, api_credentials, is_active) VALUES
('EMAIL', 'SENDGRID', '{"api_key": ""}', true),
('EMAIL', 'MAILCHIMP', '{"api_key": "", "server": "us1"}', true),
('SMS', 'TWILIO', '{"account_sid": "", "auth_token": ""}', true),
('SMS', 'ORANGE', '{"client_id": "", "client_secret": ""}', true),
('SOCIAL', 'FACEBOOK', '{"app_id": "", "app_secret": ""}', true),
('SOCIAL', 'LINKEDIN', '{"client_id": "", "client_secret": ""}', true),
('CRM', 'HUBSPOT', '{"api_key": ""}', true),
('CRM', 'SALESFORCE', '{"client_id": "", "client_secret": ""}', true);
```

### 2. Vérification Base de Données
```sql
-- @cursor: Vérifier tables créées
SELECT table_name FROM information_schema.tables 
WHERE table_schema = 'public' AND table_name LIKE '%crm%' OR table_name LIKE '%marketing%';

-- @cursor: Vérifier colonnes importantes
\d crm_customer_profiles
\d marketing_campaigns
\d marketing_messages
```

---

## 🔧 PHASE 3: ENTITÉS JAVA (Jour 3)

### 1. Entité CrmCustomerProfile
```java
// @cursor: Créer fichier crm/entity/CrmCustomerProfile.java
package com.ecomptaia.crm.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.vladmihalcea.hibernate.type.json.JsonType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "crm_customer_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class CrmCustomerProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(name = "company_id", nullable = false)
    private UUID companyId;
    
    @Column(name = "third_party_id")
    private UUID thirdPartyId;
    
    // SCORING IA
    @Column(name = "ai_score")
    private Integer aiScore;
    
    @Column(name = "churn_probability", precision = 5, scale = 4)
    private BigDecimal churnProbability;
    
    @Column(name = "lifetime_value_predicted", precision = 15, scale = 2)
    private BigDecimal lifetimeValuePredicted;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "customer_segment")
    private CustomerSegment customerSegment;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_behavior")
    private PaymentBehavior paymentBehavior;
    
    @Column(name = "satisfaction_score")
    private Integer satisfactionScore;
    
    // DONNÉES COMPORTEMENTALES
    @Column(name = "avg_payment_delay")
    private Integer avgPaymentDelay;
    
    @Column(name = "total_revenue_generated", precision = 15, scale = 2)
    private BigDecimal totalRevenueGenerated = BigDecimal.ZERO;
    
    @Column(name = "last_purchase_date")
    private LocalDateTime lastPurchaseDate;
    
    @Column(name = "purchase_frequency", precision = 8, scale = 2)
    private BigDecimal purchaseFrequency;
    
    // PRÉFÉRENCES COMMUNICATION
    @Type(JsonType.class)
    @Column(name = "preferred_channels", columnDefinition = "jsonb")
    private Set<String> preferredChannels;
    
    @Column(name = "email_opt_in")
    private Boolean emailOptIn = true;
    
    @Column(name = "sms_opt_in")
    private Boolean smsOptIn = true;
    
    @Type(JsonType.class)
    @Column(name = "social_media_handles", columnDefinition = "jsonb")
    private Map<String, String> socialMediaHandles = new HashMap<>();
    
    @Column(name = "best_contact_time")
    private String bestContactTime;
    
    @Column(name = "language_preference")
    private String languagePreference = "fr";
    
    @Column(name = "timezone")
    private String timezone;
    
    // INTÉGRATIONS EXTERNES
    @Type(JsonType.class)
    @Column(name = "external_ids", columnDefinition = "jsonb")
    private Map<String, String> externalIds = new HashMap<>();
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

// @cursor: Créer enums dans le même package
public enum CustomerSegment {
    VIP_HIGH_VALUE,
    STRATEGIC_ACCOUNT, 
    GROWING_BUSINESS,
    STABLE_REGULAR,
    OCCASIONAL_BUYER,
    PRICE_SENSITIVE,
    PAYMENT_DELAYED,
    AT_RISK_CHURN
}

public enum PaymentBehavior {
    EARLY_PAYER,
    PROMPT_PAYER,
    REGULAR_DELAY,
    NEGOTIATOR,
    PROBLEMATIC_PAYER,
    CASH_ONLY
}
```

### 2. Commandes Cursor pour Générer Autres Entités
```bash
# @cursor: Utiliser Ctrl+K puis ces prompts:

"Generate MarketingCampaign entity based on the database schema with all JSON fields and relationships"

"Create MarketingMessage entity with proper JPA annotations and enums"

"Generate MarketingProvider entity with encrypted credentials support"

"Create CustomerInteraction entity with sentiment analysis fields"

"Generate MarketingWorkflow entity with JSON workflow steps"

"Create ContentTemplate entity for multi-channel templates"

"Generate MarketingAnalytics entity with performance metrics"

"Create MarketingOptOut entity for unsubscribe management"
```

---

## 🗂 PHASE 4: REPOSITORIES (Jour 4)

### 1. Repository Principal
```java
// @cursor: Créer crm/repository/CrmCustomerRepository.java
package com.ecomptaia.crm.repository;

import com.ecomptaia.crm.entity.CrmCustomerProfile;
import com.ecomptaia.crm.entity.CustomerSegment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CrmCustomerRepository extends JpaRepository<CrmCustomerProfile, UUID> {
    
    // Recherches par entreprise
    Page<CrmCustomerProfile> findByCompanyId(UUID companyId, Pageable pageable);
    
    List<CrmCustomerProfile> findByCompanyIdAndCustomerSegment(UUID companyId, CustomerSegment segment);
    
    // Recherches par score IA
    @Query("SELECT c FROM CrmCustomerProfile c WHERE c.companyId = :companyId AND c.aiScore >= :minScore ORDER BY c.aiScore DESC")
    List<CrmCustomerProfile> findByCompanyIdAndAiScoreGreaterThanEqual(@Param("companyId") UUID companyId, @Param("minScore") Integer minScore);
    
    // Clients à risque de churn
    @Query("SELECT c FROM CrmCustomerProfile c WHERE c.companyId = :companyId AND c.churnProbability > :threshold ORDER BY c.churnProbability DESC")
    List<CrmCustomerProfile> findHighChurnRiskCustomers(@Param("companyId") UUID companyId, @Param("threshold") BigDecimal threshold);
    
    // Clients par valeur prédite
    @Query("SELECT c FROM CrmCustomerProfile c WHERE c.companyId = :companyId AND c.lifetimeValuePredicted >= :minValue ORDER BY c.lifetimeValuePredicted DESC")
    List<CrmCustomerProfile> findHighValueCustomers(@Param("companyId") UUID companyId, @Param("minValue") BigDecimal minValue);
    
    // Opt-in par canal
    List<CrmCustomerProfile> findByCompanyIdAndEmailOptInTrue(UUID companyId);
    List<CrmCustomerProfile> findByCompanyIdAndSmsOptInTrue(UUID companyId);
    
    // Recherche par Third Party
    Optional<CrmCustomerProfile> findByCompanyIdAndThirdPartyId(UUID companyId, UUID thirdPartyId);
    
    // Clients inactifs
    @Query("SELECT c FROM CrmCustomerProfile c WHERE c.companyId = :companyId AND c.lastPurchaseDate < :cutoffDate")
    List<CrmCustomerProfile> findInactiveCustomers(@Param("companyId") UUID companyId, @Param("cutoffDate") LocalDateTime cutoffDate);
    
    // Statistiques
    @Query("SELECT COUNT(c) FROM CrmCustomerProfile c WHERE c.companyId = :companyId AND c.customerSegment = :segment")
    Long countByCompanyIdAndSegment(@Param("companyId") UUID companyId, @Param("segment") CustomerSegment segment);
    
    @Query("SELECT AVG(c.aiScore) FROM CrmCustomerProfile c WHERE c.companyId = :companyId")
    Double getAverageAiScore(@Param("companyId") UUID companyId);
}
```

### 2. Commandes pour Autres Repositories
```bash
# @cursor: Générer repositories avec Ctrl+K:

"Generate MarketingCampaignRepository with custom queries for campaign management"

"Create MarketingMessageRepository with channel and status filtering"

"Generate MarketingProviderRepository with provider type filtering"

"Create CustomerInteractionRepository with date range and sentiment queries"

"Generate MarketingWorkflowRepository with trigger-based queries"

"Create ContentTemplateRepository with category and language filtering"

"Generate MarketingAnalyticsRepository with aggregation queries"
```

---

## ⚙ PHASE 5: SERVICES (Jours 5-6)

### 1. Service Principal CRM
```java
// @cursor: Créer crm/service/CrmCustomerService.java
package com.ecomptaia.crm.service;

import com.ecomptaia.crm.entity.CrmCustomerProfile;
import com.ecomptaia.crm.entity.CustomerSegment;
import com.ecomptaia.crm.repository.CrmCustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CrmCustomerService {
    
    private final CrmCustomerRepository customerRepository;
    private final CustomerIntelligenceService intelligenceService;
    private final ThirdPartyRepository thirdPartyRepository; // Référence à l'existant
    
    /**
     * Création ou mise à jour profil client CRM
     */
    public CrmCustomerProfile createOrUpdateCustomerProfile(UUID companyId, UUID thirdPartyId) {
        // Vérifier si profil existe déjà
        CrmCustomerProfile existingProfile = customerRepository
            .findByCompanyIdAndThirdPartyId(companyId, thirdPartyId)
            .orElse(null);
            
        if (existingProfile != null) {
            return updateCustomerProfile(existingProfile);
        }
        
        // Créer nouveau profil
        CrmCustomerProfile newProfile = CrmCustomerProfile.builder()
            .companyId(companyId)
            .thirdPartyId(thirdPartyId)
            .emailOptIn(true)
            .smsOptIn(true)
            .languagePreference("fr")
            .build();
            
        // Calcul initial du scoring IA
        CustomerIntelligence intelligence = intelligenceService.calculateInitialIntelligence(
            companyId, thirdPartyId
        );
        
        newProfile.setAiScore(intelligence.getAiScore());
        newProfile.setCustomerSegment(intelligence.getSegment());
        newProfile.setChurnProbability(intelligence.getChurnProbability());
        newProfile.setLifetimeValuePredicted(intelligence.getPredictedLTV());
        
        return customerRepository.save(newProfile);
    }
    
    /**
     * Mise à jour scoring IA pour tous les clients
     */
    @Transactional
    public void updateAllCustomerIntelligence(UUID companyId) {
        List<CrmCustomerProfile> customers = customerRepository.findByCompanyId(companyId, Pageable.unpaged()).getContent();
        
        for (CrmCustomerProfile customer : customers) {
            CustomerIntelligence intelligence = intelligenceService.calculateCustomerIntelligence(
                customer.getId()
            );
            
            customer.setAiScore(intelligence.getAiScore());
            customer.setCustomerSegment(intelligence.getSegment());
            customer.setChurnProbability(intelligence.getChurnProbability());
            customer.setLifetimeValuePredicted(intelligence.getPredictedLTV());
            customer.setSatisfactionScore(intelligence.getSatisfactionScore());
            
            customerRepository.save(customer);
        }
        
        log.info("Mise à jour intelligence de {} clients pour company {}", customers.size(), companyId);
    }
    
    /**
     * Segmentation automatique
     */
    public List<CrmCustomerProfile> getCustomersBySegment(UUID companyId, CustomerSegment segment) {
        return customerRepository.findByCompanyIdAndCustomerSegment(companyId, segment);
    }
    
    /**
     * Clients éligibles pour campagne
     */
    public List<CrmCustomerProfile> getEligibleCustomersForCampaign(
            UUID companyId, 
            List<CustomerSegment> targetSegments,
            List<String> requiredChannels
    ) {
        List<CrmCustomerProfile> customers = customerRepository.findByCompanyId(companyId, Pageable.unpaged()).getContent();
        
        return customers.stream()
            .filter(customer -> targetSegments.isEmpty() || targetSegments.contains(customer.getCustomerSegment()))
            .filter(customer -> isEligibleForChannels(customer, requiredChannels))
            .toList();
    }
    
    private boolean isEligibleForChannels(CrmCustomerProfile customer, List<String> channels) {
        for (String channel : channels) {
            switch (channel.toUpperCase()) {
                case "EMAIL":
                    if (!Boolean.TRUE.equals(customer.getEmailOptIn())) return false;
                    break;
                case "SMS":
                    if (!Boolean.TRUE.equals(customer.getSmsOptIn())) return false;
                    break;
            }
        }
        return true;
    }
}
```

### 2. Commandes pour Services Spécialisés
```bash
# @cursor: Générer services avec Ctrl+K:

"Create CustomerIntelligenceService with AI scoring algorithms and churn prediction"

"Generate UnifiedMarketingService for multi-channel campaign management"

"Create EmailMarketingService with SendGrid and Mailchimp integration"

"Generate SmsMarketingService with Twilio and Orange SMS providers"

"Create SocialMediaService with Facebook and LinkedIn API integration"

"Generate MarketingAutomationService with workflow engine"

"Create MarketingAnalyticsService with ROI calculation and attribution"

"Generate ExternalIntegrationService for HubSpot and Salesforce sync"
```

---

## 🌐 PHASE 6: CONTRÔLEURS REST (Jour 7)

### 1. Contrôleur Principal
```java
// @cursor: Créer crm/controller/CrmController.java
package com.ecomptaia.crm.controller;

import com.ecomptaia.crm.dto.*;
import com.ecomptaia.crm.entity.CrmCustomerProfile;
import com.ecomptaia.crm.service.CrmCustomerService;
import com.ecomptaia.crm.service.UnifiedMarketingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/crm-premium")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class CrmController {
    
    private final CrmCustomerService customerService;
    private final UnifiedMarketingService marketingService;
    
    /**
     * Gestion des profils clients
     */
    @GetMapping("/customers")
    public ResponseEntity<Page<CrmCustomerProfile>> getCustomers(Pageable pageable) {
        UUID companyId = getCurrentCompanyId();
        Page<CrmCustomerProfile> customers = customerService.getCustomers(companyId, pageable);
        return ResponseEntity.ok(customers);
    }
    
    @GetMapping("/customers/{customerId}")
    public ResponseEntity<CrmCustomerProfile> getCustomer(@PathVariable UUID customerId) {
        CrmCustomerProfile customer = customerService.getCustomer(customerId);
        return ResponseEntity.ok(customer);
    }
    
    @PostMapping("/customers")
    public ResponseEntity<CrmCustomerProfile> createCustomerProfile(@RequestBody @Valid CreateCustomerProfileRequest request) {
        UUID companyId = getCurrentCompanyId();
        CrmCustomerProfile customer = customerService.createOrUpdateCustomerProfile(companyId, request.getThirdPartyId());
        return ResponseEntity.ok(customer);
    }
    
    @PutMapping("/customers/{customerId}")
    public ResponseEntity<CrmCustomerProfile> updateCustomer(@PathVariable UUID customerId, @RequestBody @Valid UpdateCustomerRequest request) {
        CrmCustomerProfile customer = customerService.updateCustomer(customerId, request);
        return ResponseEntity.ok(customer);
    }
    
    /**
     * Campagnes marketing unifiées
     */
    @PostMapping("/campaigns")
    public ResponseEntity<ApiResponse<MarketingCampaign>> createCampaign(@RequestBody @Valid CreateUnifiedCampaignRequest request) {
        try {
            UUID companyId = getCurrentCompanyId();
            MarketingCampaign campaign = marketingService.createUnifiedCampaign(companyId, request);
            return ResponseEntity.ok(ApiResponse.success(campaign));
        } catch (UsageLimitExceededException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Limite atteinte: " + e.getMessage()));
        }
    }
    
    @PostMapping("/campaigns/{campaignId}/execute")
    public ResponseEntity<ApiResponse<String>> executeCampaign(@PathVariable UUID campaignId) {
        marketingService.executeUnifiedCampaign(campaignId);
        return ResponseEntity.ok(ApiResponse.success("Campagne en cours d'exécution"));
    }
    
    @GetMapping("/campaigns")
    public ResponseEntity<List<MarketingCampaign>> getCampaigns() {
        UUID companyId = getCurrentCompanyId();
        List<MarketingCampaign> campaigns = marketingService.getCampaigns(companyId);
        return ResponseEntity.ok(campaigns);
    }
    
    /**
     * Analytics unifiés
     */
    @GetMapping("/analytics/unified")
    public ResponseEntity<UnifiedMarketingAnalytics> getUnifiedAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        UUID companyId = getCurrentCompanyId();
        UnifiedMarketingAnalytics analytics = marketingService.getUnifiedAnalytics(companyId, from, to);
        return ResponseEntity.ok(analytics);
    }
    
    /**
     * Intelligence client
     */
    @PostMapping("/customers/{customerId}/update-intelligence")
    public ResponseEntity<CustomerIntelligence> updateCustomerIntelligence(@PathVariable UUID customerId) {
        CustomerIntelligence intelligence = customerService.updateCustomerIntelligence(customerId);
        return ResponseEntity.ok(intelligence);
    }
    
    @PostMapping("/intelligence/update-all")
    public ResponseEntity<String> updateAllCustomerIntelligence() {
        UUID companyId = getCurrentCompanyId();
        customerService.updateAllCustomerIntelligence(companyId);
        return ResponseEntity.ok("Mise à jour de l'intelligence client en cours");
    }
    
    private UUID getCurrentCompanyId() {
        // TODO: Récupérer depuis le contexte de sécurité
        return UUID.randomUUID(); // Placeholder
    }
}
```

---

## 📱 PHASE 7: FRONTEND ANGULAR (Jours 8-10)

### 1. Génération Modules
```bash
# @cursor: Dans le répertoire frontend/
cd frontend

# Génération modules principaux
ng generate module crm-digital-marketing --routing
ng generate module crm-digital-marketing/shared

# Génération composants
ng generate component crm-digital-marketing/dashboard
ng generate component crm-digital-marketing/customer-profile
ng generate component crm-digital-marketing/campaign-manager
ng generate component crm-digital-marketing/analytics

# Génération services
ng generate service crm-digital-marketing/services/crm
ng generate service crm-digital-marketing/services/marketing
ng generate service crm-digital-marketing/services/analytics
```

### 2. Configuration Module Principal
```typescript
// @cursor: Mettre à jour crm-digital-marketing.module.ts
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '../shared/material.module';
import { ChartsModule } from 'ng2-charts';

import { CrmDigitalMarketingRoutingModule } from './crm-digital-marketing-routing.module';
import { DashboardComponent } from './dashboard/dashboard.component';
import { CustomerProfileComponent } from './customer-profile/customer-profile.component';
import { CampaignManagerComponent } from './campaign-manager/campaign-manager.component';
import { AnalyticsComponent } from './analytics/analytics.component';

@NgModule({
  declarations: [
    DashboardComponent,
    CustomerProfileComponent, 
    CampaignManagerComponent,
    AnalyticsComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MaterialModule,
    ChartsModule,
    CrmDigitalMarketingRoutingModule
  ]
})
export class CrmDigitalMarketingModule { }
```

### 3. Commandes pour Composants Avancés
```bash
# @cursor: Générer composants avec Ctrl+K:

"Generate Angular dashboard component with charts and KPIs for CRM marketing"

"Create customer profile component with 360° view and interaction timeline"

"Generate unified campaign manager with multi-channel form and preview"

"Create analytics component with revenue attribution and ROI charts"

"Generate workflow builder component with drag-and-drop functionality"

"Create integration manager component for external API configuration"

"Generate template library component with multi-channel templates"
```

---

## 🧪 PHASE 8: TESTS (Jour 11)

### 1. Tests Unitaires Backend
```java
// @cursor: Créer tests dans src/test/java/com/ecomptaia/crm/
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CrmCustomerServiceTest {
    
    @Autowired
    private CrmCustomerService customerService;
    
    @MockBean
    private CrmCustomerRepository customerRepository;
    
    @Test
    void testCreateCustomerProfile() {
        // Given
        UUID companyId = UUID.randomUUID();
        UUID thirdPartyId = UUID.randomUUID();
        
        // When
        CrmCustomerProfile result = customerService.createOrUpdateCustomerProfile(companyId, thirdPartyId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCompanyId()).isEqualTo(companyId);
        assertThat(result.getThirdPartyId()).isEqualTo(thirdPartyId);
    }
    
    @Test
    void testCustomerIntelligenceUpdate() {
        // Test mise à jour intelligence
    }
}
```

### 2. Tests Frontend
```typescript
// @cursor: Tests Angular
describe('CrmDashboardComponent', () => {
  let component: CrmDashboardComponent;
  let fixture: ComponentFixture<CrmDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CrmDashboardComponent],
      imports: [MaterialModule, ReactiveFormsModule]
    }).compileComponents();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load customer data', () => {
    // Test chargement données
  });
});
```

---

## 🚀 PHASE 9: DÉPLOIEMENT (Jour 12)

### 1. Configuration Production
```yaml
# @cursor: Ajouter dans application-prod.yml
crm:
  digital-marketing:
    enabled: true
    
    email:
      sendgrid:
### 1. Configuration Production
```yaml
# @cursor: Ajouter dans application-prod.yml
crm:
  digital-marketing:
    enabled: true
    
    email:
      sendgrid:
        api-key: ${SENDGRID_API_KEY}
      mailchimp:
        api-key: ${MAILCHIMP_API_KEY}
        server: ${MAILCHIMP_SERVER:us1}
    
    sms:
      twilio:
        account-sid: ${TWILIO_ACCOUNT_SID}
        auth-token: ${TWILIO_AUTH_TOKEN}
        phone-number: ${TWILIO_PHONE_NUMBER}
      orange:
        client-id: ${ORANGE_CLIENT_ID}
        client-secret: ${ORANGE_CLIENT_SECRET}
        api-url: https://api.orange.com/sms/admin/v1
    
    social:
      facebook:
        app-id: ${FACEBOOK_APP_ID}
        app-secret: ${FACEBOOK_APP_SECRET}
      linkedin:
        client-id: ${LINKEDIN_CLIENT_ID}
        client-secret: ${LINKEDIN_CLIENT_SECRET}
    
    integrations:
      hubspot:
        api-key: ${HUBSPOT_API_KEY}
      salesforce:
        client-id: ${SALESFORCE_CLIENT_ID}
        client-secret: ${SALESFORCE_CLIENT_SECRET}
    
    ai:
      openai-api-key: ${OPENAI_API_KEY}

spring:
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
```

### 2. Docker Configuration
```dockerfile
# @cursor: Mettre à jour Dockerfile backend
FROM openjdk:17-jdk-slim

# Variables environnement CRM
ENV SENDGRID_API_KEY=""
ENV MAILCHIMP_API_KEY=""
ENV TWILIO_ACCOUNT_SID=""
ENV TWILIO_AUTH_TOKEN=""
ENV FACEBOOK_APP_ID=""
ENV LINKEDIN_CLIENT_ID=""
ENV HUBSPOT_API_KEY=""
ENV OPENAI_API_KEY=""
ENV REDIS_HOST="localhost"
ENV REDIS_PORT="6379"

COPY target/ecomptaia-backend-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### 3. Docker Compose Mise à Jour
```yaml
# @cursor: Ajouter dans docker-compose.yml
services:
  backend:
    build: ./backend
    environment:
      - SENDGRID_API_KEY=${SENDGRID_API_KEY}
      - TWILIO_ACCOUNT_SID=${TWILIO_ACCOUNT_SID}
      - TWILIO_AUTH_TOKEN=${TWILIO_AUTH_TOKEN}
      - FACEBOOK_APP_ID=${FACEBOOK_APP_ID}
      - HUBSPOT_API_KEY=${HUBSPOT_API_KEY}
      - OPENAI_API_KEY=${OPENAI_API_KEY}
      - REDIS_HOST=redis
    depends_on:
      - postgres
      - redis
      
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
      
volumes:
  redis_data:
```

---

## 📋 CHECKLIST DE VALIDATION COMPLÈTE

### Phase 1: Infrastructure (Jours 1-3)
- [ ] Dependencies Maven ajoutées et projet compile
- [ ] Structure packages créée correctement
- [ ] Base de données : toutes les tables créées
- [ ] Index de performance appliqués
- [ ] Données initiales insérées
- [ ] Entités JPA générées et fonctionnelles
- [ ] Enums créés et utilisés correctement

### Phase 2: Services Core (Jours 4-6)
- [ ] Repositories avec requêtes custom fonctionnels
- [ ] Service CrmCustomerService opérationnel
- [ ] Service CustomerIntelligenceService avec scoring IA
- [ ] Service UnifiedMarketingService créé
- [ ] Services canal-spécifiques (Email, SMS, Social)
- [ ] Gestion des événements comptables
- [ ] Cache Redis configuré et fonctionnel

### Phase 3: APIs et Frontend (Jours 7-10)
- [ ] Contrôleurs REST avec sécurité
- [ ] Validation des données d'entrée
- [ ] Gestion des erreurs et exceptions
- [ ] Module Angular CRM créé
- [ ] Composants principaux fonctionnels
- [ ] Services Angular avec appels API
- [ ] Interface utilisateur responsive

### Phase 4: Tests et Déploiement (Jours 11-12)
- [ ] Tests unitaires backend (>80% coverage)
- [ ] Tests intégration services
- [ ] Tests frontend composants
- [ ] Configuration production complète
- [ ] Docker build réussi
- [ ] Déploiement test environnement
- [ ] Validation end-to-end

---

## 🔧 COMMANDES CURSOR AVANCÉES

### Génération Automatique Complète
```bash
# @cursor: Génération massive avec Ctrl+K

"Generate complete CRM digital marketing module with all entities, repositories, services, controllers, DTOs, and Angular components based on the provided database schema"

"Create comprehensive test suite for CRM marketing module with unit tests, integration tests, and Angular component tests"

"Generate all DTOs with validation annotations for CRM marketing APIs including request/response objects"

"Create configuration classes for all external integrations (SendGrid, Twilio, Facebook, HubSpot) with proper encryption"

"Generate exception handling classes and global exception handler for CRM marketing module"

"Create comprehensive logging configuration with structured logging for CRM marketing operations"

"Generate security configuration for CRM marketing endpoints with role-based access control"

"Create database migration scripts for CRM marketing tables with proper constraints and indexes"
```

### Optimisation et Monitoring
```bash
# @cursor: Optimisations avec Ctrl+K

"Add caching strategies with Redis for CRM customer profiles and marketing templates"

"Implement async processing with RabbitMQ for email and SMS campaign execution"

"Add comprehensive monitoring with Micrometer metrics for CRM marketing operations"

"Create circuit breaker patterns for external API calls (SendGrid, Twilio, social media)"

"Implement rate limiting for marketing campaign execution to avoid provider limits"

"Add comprehensive audit logging for all CRM marketing operations with customer data protection"

"Create health checks for all external integrations and marketing providers"

"Implement proper error recovery and retry mechanisms for failed marketing messages"
```

### Sécurité et Conformité
```bash
# @cursor: Sécurité avec Ctrl+K

"Implement GDPR compliance features including data export, deletion, and consent management"

"Add encryption for all sensitive customer data and external API credentials"

"Create audit trail for all customer data access and marketing communications"

"Implement opt-out mechanisms for all marketing channels with legal compliance"

"Add data anonymization features for customer analytics while preserving insights"

"Create security scanning for all external integrations and API calls"

"Implement proper data validation and sanitization for all user inputs"

"Add rate limiting and DDoS protection for CRM marketing endpoints"
```

---

## 🚀 DÉPLOIEMENT FINAL ET MONITORING

### 1. Script de Déploiement Production
```bash
# @cursor: Créer deploy-crm-production.sh
#!/bin/bash

echo "Déploiement Module CRM-Digital Marketing Production"

# Arrêt des services
docker-compose down

# Build backend avec profil production
cd backend
mvn clean package -Pprod -DskipTests
cd ..

# Build frontend avec optimisations
cd frontend
npm run build --prod
cd ..

# Mise à jour base de données
docker-compose exec postgres psql -U ecomptaia -d ecomptaia -f /sql/crm-marketing-prod.sql

# Démarrage avec profil production
docker-compose -f docker-compose.prod.yml up -d

# Vérification santé services
echo "Vérification santé des services..."
sleep 30
curl -f http://localhost:8080/actuator/health/crm-marketing || exit 1

echo "Déploiement CRM-Digital Marketing réussi!"
```

### 2. Monitoring et Alertes
```yaml
# @cursor: Configuration Prometheus pour CRM
# Dans monitoring/prometheus.yml
- job_name: 'crm-marketing'
  static_configs:
    - targets: ['backend:8080']
  metrics_path: '/actuator/prometheus'
  scrape_interval: 15s
  
# Alertes spécifiques CRM
- alert: CRMMarketingDown
  expr: up{job="crm-marketing"} == 0
  for: 1m
  annotations:
    summary: "Module CRM Marketing indisponible"
    
- alert: HighEmailFailureRate
  expr: rate(crm_email_failures_total[5m]) > 0.1
  for: 2m
  annotations:
    summary: "Taux d'échec email élevé"
    
- alert: SMSProviderDown
  expr: crm_sms_provider_health == 0
  for: 1m
  annotations:
    summary: "Fournisseur SMS indisponible"
```

### 3. Documentation Automatique
```bash
# @cursor: Génération documentation avec Ctrl+K

"Generate comprehensive API documentation for CRM marketing module with OpenAPI 3.0 specifications"

"Create user manual for CRM digital marketing features with screenshots and examples"

"Generate technical documentation for developers including architecture diagrams and integration guides"

"Create troubleshooting guide for common CRM marketing issues and solutions"

"Generate performance tuning guide for CRM marketing module with optimization recommendations"
```

---

## 📈 MÉTRIQUES DE SUCCÈS

### KPIs Techniques
- Temps de réponse API < 200ms (95e percentile)
- Disponibilité > 99.9%
- Taux d'erreur < 0.1%
- Couverture tests > 80%

### KPIs Business
- Adoption module CRM > 60% clients
- Revenus additionnels > 50k€/mois
- NPS module > 8/10
- Réduction churn clients > 20%

### Monitoring Continu
- Dashboards Grafana temps réel
- Alertes automatiques Slack/email
- Rapports hebdomadaires performance
- Revues mensuelles roadmap

Ce guide complet vous permet d'implémenter le module CRM-Digital Marketing de A à Z avec Cursor, en garantissant la qualité, la performance et la sécurité nécessaires pour un environnement de production.