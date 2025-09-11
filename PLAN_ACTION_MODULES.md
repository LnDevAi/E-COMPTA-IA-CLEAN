# 🎯 PLAN D'ACTION - CORRECTION DES MODULES
## Objectif : Respecter la règle VRAIES DONNÉES - BD - BACKEND - FRONTEND

---

## 📋 **ACTIONS IMMÉDIATES (À FAIRE MAINTENANT)**

### 1. 🔧 **RÉORGANISATION DES FICHIERS EXISTANTS**

#### 1.1 Déplacer les entités d'authentification
```bash
# Créer le dossier security/entity s'il n'existe pas
mkdir -p backend/src/main/java/com/ecomptaia/security/entity

# Déplacer les entités
mv backend/src/main/java/com/ecomptaia/entity/User.java backend/src/main/java/com/ecomptaia/security/entity/
mv backend/src/main/java/com/ecomptaia/entity/Company.java backend/src/main/java/com/ecomptaia/security/entity/
mv backend/src/main/java/com/ecomptaia/entity/Country.java backend/src/main/java/com/ecomptaia/security/entity/
```

#### 1.2 Déplacer les entités comptables
```bash
# Créer le dossier accounting/entity s'il n'existe pas
mkdir -p backend/src/main/java/com/ecomptaia/accounting/entity

# Déplacer les entités comptables
mv backend/src/main/java/com/ecomptaia/accounting/Account.java backend/src/main/java/com/ecomptaia/accounting/entity/
mv backend/src/main/java/com/ecomptaia/accounting/AccountClass.java backend/src/main/java/com/ecomptaia/accounting/entity/
mv backend/src/main/java/com/ecomptaia/accounting/AccountType.java backend/src/main/java/com/ecomptaia/accounting/entity/
mv backend/src/main/java/com/ecomptaia/accounting/ChartOfAccounts.java backend/src/main/java/com/ecomptaia/accounting/entity/
mv backend/src/main/java/com/ecomptaia/accounting/AccountingStandard.java backend/src/main/java/com/ecomptaia/accounting/entity/
```

#### 1.3 Créer les dossiers manquants
```bash
# Créer les dossiers pour les modules manquants
mkdir -p backend/src/main/java/com/ecomptaia/inventory/{entity,controller,service,repository}
mkdir -p backend/src/main/java/com/ecomptaia/payroll/{entity,controller,service,repository}
mkdir -p backend/src/main/java/com/ecomptaia/ged/{entity,controller,service,repository}
mkdir -p backend/src/main/java/com/ecomptaia/integrations/{entity,controller,service,repository}
mkdir -p backend/src/main/java/com/ecomptaia/reporting/{entity,controller,service,repository}
mkdir -p backend/src/main/java/com/ecomptaia/workflow/{entity,controller,service,repository}
```

---

## 🚀 **DÉVELOPPEMENT BACKEND (PRIORITÉ 1)**

### 2. 📦 **MODULE INVENTAIRE**

#### 2.1 Entités à créer
```java
// backend/src/main/java/com/ecomptaia/inventory/entity/InventoryItem.java
@Entity
public class InventoryItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String sku;
    private BigDecimal unitPrice;
    private Integer quantity;
    private String category;
    // ... autres champs
}

// backend/src/main/java/com/ecomptaia/inventory/entity/InventoryCategory.java
@Entity
public class InventoryCategory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    // ... autres champs
}
```

#### 2.2 Repository
```java
// backend/src/main/java/com/ecomptaia/inventory/repository/InventoryItemRepository.java
@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    List<InventoryItem> findByCategory(String category);
    List<InventoryItem> findByNameContainingIgnoreCase(String name);
}
```

#### 2.3 Service
```java
// backend/src/main/java/com/ecomptaia/inventory/service/InventoryService.java
@Service
public class InventoryService {
    @Autowired
    private InventoryItemRepository inventoryItemRepository;
    
    public List<InventoryItem> getAllItems() {
        return inventoryItemRepository.findAll();
    }
    
    public InventoryItem createItem(InventoryItem item) {
        return inventoryItemRepository.save(item);
    }
    // ... autres méthodes
}
```

#### 2.4 Controller
```java
// backend/src/main/java/com/ecomptaia/inventory/controller/InventoryController.java
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;
    
    @GetMapping("/items")
    public ResponseEntity<List<InventoryItem>> getAllItems() {
        return ResponseEntity.ok(inventoryService.getAllItems());
    }
    
    @PostMapping("/items")
    public ResponseEntity<InventoryItem> createItem(@RequestBody InventoryItem item) {
        return ResponseEntity.ok(inventoryService.createItem(item));
    }
    // ... autres endpoints
}
```

### 3. 💰 **MODULE PAIE**

#### 3.1 Entités à créer
```java
// backend/src/main/java/com/ecomptaia/payroll/entity/Employee.java
@Entity
public class Employee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private BigDecimal salary;
    private String position;
    // ... autres champs
}

// backend/src/main/java/com/ecomptaia/payroll/entity/Payroll.java
@Entity
public class Payroll {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Employee employee;
    private BigDecimal grossSalary;
    private BigDecimal netSalary;
    private LocalDate payDate;
    // ... autres champs
}
```

### 4. 📄 **MODULE GED**

#### 4.1 Entités à créer
```java
// backend/src/main/java/com/ecomptaia/ged/entity/Document.java
@Entity
public class Document {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type;
    private String path;
    private LocalDateTime uploadDate;
    // ... autres champs
}
```

---

## 🎨 **DÉVELOPPEMENT FRONTEND (PRIORITÉ 2)**

### 5. 📱 **SERVICES FRONTEND MANQUANTS**

#### 5.1 Service SYCEBNL
```typescript
// frontend/src/app/features/sycebnl/services/sycebnl.service.ts
@Injectable({
  providedIn: 'root'
})
export class SycebnlService {
  private apiUrl = 'http://localhost:8080/api/sycebnl';
  
  constructor(private http: HttpClient) {}
  
  getOrganizations(): Observable<SycebnlOrganization[]> {
    return this.http.get<SycebnlOrganization[]>(`${this.apiUrl}/organizations`);
  }
  
  createOrganization(organization: SycebnlOrganization): Observable<SycebnlOrganization> {
    return this.http.post<SycebnlOrganization>(`${this.apiUrl}/organizations`, organization);
  }
  // ... autres méthodes
}
```

#### 5.2 Service CRM
```typescript
// frontend/src/app/features/crm/services/crm.service.ts
@Injectable({
  providedIn: 'root'
})
export class CrmService {
  private apiUrl = 'http://localhost:8080/api/crm';
  
  constructor(private http: HttpClient) {}
  
  getCustomers(): Observable<CrmCustomer[]> {
    return this.http.get<CrmCustomer[]>(`${this.apiUrl}/customers`);
  }
  
  createCustomer(customer: CrmCustomer): Observable<CrmCustomer> {
    return this.http.post<CrmCustomer>(`${this.apiUrl}/customers`, customer);
  }
  // ... autres méthodes
}
```

### 6. 🏗️ **MODULES ANGULAR**

#### 6.1 Module SYCEBNL
```typescript
// frontend/src/app/features/sycebnl/sycebnl.module.ts
@NgModule({
  declarations: [
    SycebnlComponent,
    OrganizationListComponent,
    OrganizationDetailComponent,
    FinancialStatementsComponent
  ],
  imports: [
    CommonModule,
    SycebnlRoutingModule,
    SharedModule
  ],
  providers: [
    SycebnlService
  ]
})
export class SycebnlModule { }
```

---

## 📊 **DONNÉES DE TEST (PRIORITÉ 3)**

### 7. 🗃️ **CRÉATION DES DONNÉES DE TEST**

#### 7.1 Données d'inventaire
```sql
-- backend/src/main/resources/data-inventory.sql
INSERT INTO inventory_item (name, description, sku, unit_price, quantity, category) VALUES
('Ordinateur Portable', 'Laptop Dell Inspiron 15', 'LAP001', 899.99, 10, 'Informatique'),
('Souris Sans Fil', 'Souris Logitech MX Master', 'MOU001', 79.99, 25, 'Informatique'),
('Clavier Mécanique', 'Clavier Corsair K95', 'KEY001', 199.99, 15, 'Informatique');
```

#### 7.2 Données de paie
```sql
-- backend/src/main/resources/data-payroll.sql
INSERT INTO employee (first_name, last_name, email, salary, position) VALUES
('Jean', 'Dupont', 'jean.dupont@ecomptaia.com', 3500.00, 'Développeur'),
('Marie', 'Martin', 'marie.martin@ecomptaia.com', 4200.00, 'Chef de Projet'),
('Pierre', 'Durand', 'pierre.durand@ecomptaia.com', 2800.00, 'Analyste');
```

---

## ⏰ **CALENDRIER D'EXÉCUTION**

### **JOUR 1 - Réorganisation**
- [ ] Déplacer les entités d'authentification
- [ ] Déplacer les entités comptables
- [ ] Créer les dossiers manquants
- [ ] Tester la compilation

### **JOUR 2-3 - Module Inventaire**
- [ ] Créer les entités
- [ ] Créer les repositories
- [ ] Créer les services
- [ ] Créer les controllers
- [ ] Ajouter les données de test

### **JOUR 4-5 - Module Paie**
- [ ] Créer les entités
- [ ] Créer les repositories
- [ ] Créer les services
- [ ] Créer les controllers
- [ ] Ajouter les données de test

### **JOUR 6-7 - Module GED**
- [ ] Créer les entités
- [ ] Créer les repositories
- [ ] Créer les services
- [ ] Créer les controllers
- [ ] Ajouter les données de test

### **JOUR 8-9 - Services Frontend**
- [ ] Créer les services SYCEBNL
- [ ] Créer les services CRM
- [ ] Créer les services Authentification
- [ ] Créer les services Comptabilité

### **JOUR 10 - Tests et Validation**
- [ ] Tests d'intégration
- [ ] Validation de la règle complète
- [ ] Documentation finale

---

## 🎯 **CRITÈRES DE SUCCÈS**

### **Objectifs quantitatifs :**
- ✅ 10/10 modules avec backend complet
- ✅ 10/10 modules avec frontend complet
- ✅ 10/10 modules avec données de test
- ✅ 100% des modules respectent la règle

### **Objectifs qualitatifs :**
- ✅ Code compilable sans erreurs
- ✅ Tests passent à 100%
- ✅ Documentation à jour
- ✅ Architecture cohérente

---

**Responsable :** Équipe de développement
**Date de début :** Immédiatement
**Date de fin prévue :** 10 jours
**Statut :** En attente de validation
