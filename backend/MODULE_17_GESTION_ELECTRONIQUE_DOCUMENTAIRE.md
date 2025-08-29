# 📄 MODULE 17 - GESTION ÉLECTRONIQUE DOCUMENTAIRE (GED)

## 🎯 **Vue d'ensemble**

Le Module 17 **Gestion Électronique Documentaire (GED)** est un système complet de gestion, stockage, recherche et partage sécurisé de tous les documents de l'entreprise. Il s'intègre parfaitement dans la plateforme E-COMPTA IA et permet une gestion documentaire professionnelle.

---

## 🚀 **FONCTIONNALITÉS PRINCIPALES**

### **1. Gestion des Documents**
- ✅ **Upload et stockage** de documents avec métadonnées
- ✅ **Classification automatique** par type et catégorie
- ✅ **Versioning** et historique des modifications
- ✅ **Gestion des permissions** et niveaux de sécurité
- ✅ **Intégrité des fichiers** avec checksum SHA-256

### **2. Workflows d'Approbation**
- ✅ **Circuits d'approbation** configurables
- ✅ **Niveaux d'approbation** multiples
- ✅ **Auto-approbation** pour certains types
- ✅ **Notifications** et rappels automatiques
- ✅ **Suivi des approbations** en temps réel

### **3. Recherche et Filtrage**
- ✅ **Recherche full-text** (titre, description, tags)
- ✅ **Filtres avancés** par type, statut, sécurité
- ✅ **Recherche par module** de référence
- ✅ **Recherche par entité** liée
- ✅ **Tags et catégorisation**

### **4. Sécurité et Conformité**
- ✅ **Niveaux de sécurité** (Public, Interne, Confidentiel, Restreint, Secret)
- ✅ **Chiffrement** des documents sensibles
- ✅ **Audit trail** complet
- ✅ **Rétention** et archivage automatique
- ✅ **Expiration** des documents

### **5. Intégration Plateforme**
- ✅ **Liaison avec modules** (ASSET, INVENTORY, HR, etc.)
- ✅ **Références d'entités** (ID et type)
- ✅ **Standards comptables** par pays
- ✅ **Multi-entreprises** support

---

## 🏗 **ARCHITECTURE TECHNIQUE**

### **Entités Principales**

#### **1. Document**
```java
@Entity
@Table(name = "documents")
public class Document {
    // Identifiants
    private Long id;
    private String documentCode; // Code unique
    
    // Métadonnées
    private String title;
    private String description;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String fileType;
    private String mimeType;
    
    // Classification
    private DocumentType documentType; // INVOICE, CONTRACT, REPORT, etc.
    private String category;
    private String tags;
    
    // Versioning
    private Integer version;
    private Boolean isCurrentVersion;
    private Long parentDocumentId;
    
    // Statut et sécurité
    private DocumentStatus status; // DRAFT, PENDING_APPROVAL, APPROVED, etc.
    private SecurityLevel securityLevel; // PUBLIC, INTERNAL, CONFIDENTIAL, etc.
    
    // Dates importantes
    private LocalDate expiryDate;
    private Boolean isArchived;
    private LocalDate archiveDate;
    private Integer retentionPeriodYears;
    
    // Intégration plateforme
    private String moduleReference; // Module lié
    private Long entityReferenceId; // ID de l'entité liée
    private String entityReferenceType; // Type d'entité liée
    
    // Contexte entreprise
    private Long companyId;
    private String countryCode;
    private String accountingStandard;
    
    // Audit
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
    private Long approvedBy;
    private LocalDateTime approvedAt;
    
    // Intégrité
    private String checksum; // SHA-256
    private Boolean isEncrypted;
}
```

#### **2. DocumentWorkflow**
```java
@Entity
@Table(name = "document_workflows")
public class DocumentWorkflow {
    private Long id;
    private String workflowCode; // Code unique
    private String workflowName;
    private String description;
    private Document.DocumentType documentType;
    
    // Configuration workflow
    private Boolean isActive;
    private Boolean requiresApproval;
    private Integer approvalLevels;
    private Boolean autoApprove;
    private Boolean autoArchive;
    private Integer archiveDays;
    private Integer retentionYears;
    
    // Contexte
    private Long companyId;
    private String countryCode;
    private String accountingStandard;
    
    // Audit
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
}
```

#### **3. DocumentApproval**
```java
@Entity
@Table(name = "document_approvals")
public class DocumentApproval {
    private Long id;
    private Long documentId;
    private Integer approvalLevel;
    private Long approverId;
    private String approverName;
    
    // Statut approbation
    private ApprovalStatus approvalStatus; // PENDING, APPROVED, REJECTED, etc.
    private LocalDateTime approvalDate;
    private String comments;
    
    // Gestion des délais
    private Boolean isRequired;
    private LocalDateTime dueDate;
    private Boolean isOverdue;
    
    // Notifications
    private Boolean reminderSent;
    private Integer reminderCount;
    private LocalDateTime lastReminderDate;
    
    // Contexte
    private Long companyId;
    private String countryCode;
    private String accountingStandard;
    
    // Audit
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

---

## 🔧 **ENDPOINTS API**

### **Gestion des Documents**

#### **1. Upload de Document**
```http
POST /api/document-management/documents/upload
Content-Type: multipart/form-data

Paramètres:
- file: MultipartFile (obligatoire)
- title: String (obligatoire)
- description: String (optionnel)
- documentType: String (obligatoire) - INVOICE, CONTRACT, REPORT, etc.
- category: String (optionnel)
- tags: String (optionnel)
- securityLevel: String (défaut: INTERNAL) - PUBLIC, INTERNAL, CONFIDENTIAL, etc.
- companyId: Long (obligatoire)
- countryCode: String (obligatoire)
- accountingStandard: String (obligatoire)
- createdBy: Long (obligatoire)
- moduleReference: String (optionnel)
- entityReferenceId: Long (optionnel)
- entityReferenceType: String (optionnel)
```

#### **2. Obtenir un Document**
```http
GET /api/document-management/documents/{documentId}?companyId={companyId}
```

#### **3. Lister tous les Documents**
```http
GET /api/document-management/documents?companyId={companyId}
```

#### **4. Rechercher des Documents**
```http
GET /api/document-management/documents/search?companyId={companyId}&searchTerm={searchTerm}
```

#### **5. Filtrer les Documents**
```http
GET /api/document-management/documents/filter?companyId={companyId}&documentType={type}&status={status}&securityLevel={level}&category={category}&moduleReference={module}
```

#### **6. Mettre à jour un Document**
```http
PUT /api/document-management/documents/{documentId}
Content-Type: application/x-www-form-urlencoded

Paramètres:
- title: String (obligatoire)
- description: String (optionnel)
- category: String (optionnel)
- tags: String (optionnel)
- securityLevel: String (défaut: INTERNAL)
- companyId: Long (obligatoire)
- updatedBy: Long (obligatoire)
```

#### **7. Supprimer un Document**
```http
DELETE /api/document-management/documents/{documentId}?companyId={companyId}
```

### **Gestion des Workflows**

#### **1. Créer un Workflow**
```http
POST /api/document-management/workflows
Content-Type: application/x-www-form-urlencoded

Paramètres:
- workflowCode: String (obligatoire)
- workflowName: String (obligatoire)
- description: String (optionnel)
- documentType: String (obligatoire)
- requiresApproval: Boolean (défaut: true)
- approvalLevels: Integer (défaut: 1)
- autoApprove: Boolean (défaut: false)
- autoArchive: Boolean (défaut: false)
- archiveDays: Integer (défaut: 365)
- retentionYears: Integer (défaut: 7)
- companyId: Long (obligatoire)
- countryCode: String (obligatoire)
- accountingStandard: String (obligatoire)
- createdBy: Long (obligatoire)
```

#### **2. Lister les Workflows**
```http
GET /api/document-management/workflows?companyId={companyId}
```

#### **3. Obtenir un Workflow**
```http
GET /api/document-management/workflows/{workflowCode}?companyId={companyId}
```

### **Gestion des Approbations**

#### **1. Approuver un Document**
```http
POST /api/document-management/documents/{documentId}/approve
Content-Type: application/x-www-form-urlencoded

Paramètres:
- approverId: Long (obligatoire)
- comments: String (optionnel)
- companyId: Long (obligatoire)
```

#### **2. Rejeter un Document**
```http
POST /api/document-management/documents/{documentId}/reject
Content-Type: application/x-www-form-urlencoded

Paramètres:
- approverId: Long (obligatoire)
- comments: String (optionnel)
- companyId: Long (obligatoire)
```

### **Statistiques et Rapports**

#### **1. Statistiques des Documents**
```http
GET /api/document-management/documents/stats?companyId={companyId}
```

#### **2. Documents Expirés**
```http
GET /api/document-management/documents/expired?companyId={companyId}
```

#### **3. Documents à Archiver**
```http
GET /api/document-management/documents/to-archive?companyId={companyId}
```

### **Endpoints de Test**

#### **1. Test Upload**
```http
POST /api/document-management/test/upload
```

#### **2. Test Workflow**
```http
POST /api/document-management/test/workflow
```

#### **3. Test Statistiques**
```http
POST /api/document-management/test/stats
```

---

## 📊 **TYPES DE DOCUMENTS SUPPORTÉS**

### **Types de Documents**
- **INVOICE** - Factures
- **RECEIPT** - Reçus
- **CONTRACT** - Contrats
- **REPORT** - Rapports
- **POLICY** - Politiques
- **PROCEDURE** - Procédures
- **MANUAL** - Manuels
- **CERTIFICATE** - Certificats
- **LICENSE** - Licences
- **INSURANCE** - Assurances
- **TAX_DOCUMENT** - Documents fiscaux
- **LEGAL_DOCUMENT** - Documents légaux
- **HR_DOCUMENT** - Documents RH
- **ASSET_DOCUMENT** - Documents d'immobilisations
- **INVENTORY_DOCUMENT** - Documents d'inventaire
- **FINANCIAL_STATEMENT** - États financiers
- **AUDIT_REPORT** - Rapports d'audit
- **COMPLIANCE_DOCUMENT** - Documents de conformité
- **OTHER** - Autres

### **Statuts de Documents**
- **DRAFT** - Brouillon
- **PENDING_APPROVAL** - En attente d'approbation
- **APPROVED** - Approuvé
- **REJECTED** - Rejeté
- **ARCHIVED** - Archivé
- **EXPIRED** - Expiré
- **DELETED** - Supprimé

### **Niveaux de Sécurité**
- **PUBLIC** - Public
- **INTERNAL** - Interne
- **CONFIDENTIAL** - Confidentiel
- **RESTRICTED** - Restreint
- **SECRET** - Secret

---

## 🔒 **SÉCURITÉ ET CONFORMITÉ**

### **Mesures de Sécurité**
1. **Chiffrement** des documents sensibles
2. **Checksum SHA-256** pour l'intégrité
3. **Niveaux de sécurité** configurables
4. **Audit trail** complet
5. **Permissions** granulaires

### **Conformité**
1. **Rétention** automatique des documents
2. **Archivage** selon les règles métier
3. **Expiration** des documents
4. **Traçabilité** complète
5. **Standards comptables** par pays

---

## 🚀 **UTILISATION PRATIQUE**

### **Exemple d'Upload de Document**
```bash
curl -X POST http://localhost:8080/api/document-management/documents/upload \
  -F "file=@facture.pdf" \
  -F "title=Facture Fournisseur 2024-001" \
  -F "description=Facture pour achat de matériel informatique" \
  -F "documentType=INVOICE" \
  -F "category=Fournisseurs" \
  -F "tags=informatique,matériel,fournisseur" \
  -F "securityLevel=INTERNAL" \
  -F "companyId=1" \
  -F "countryCode=CI" \
  -F "accountingStandard=SYSCOHADA" \
  -F "createdBy=1" \
  -F "moduleReference=ASSET" \
  -F "entityReferenceId=123" \
  -F "entityReferenceType=Asset"
```

### **Exemple de Recherche**
```bash
curl "http://localhost:8080/api/document-management/documents/search?companyId=1&searchTerm=facture"
```

### **Exemple de Statistiques**
```bash
curl "http://localhost:8080/api/document-management/documents/stats?companyId=1"
```

---

## ✅ **AVANTAGES DU MODULE 17**

### **Pour l'Entreprise**
- ✅ **Centralisation** de tous les documents
- ✅ **Sécurité renforcée** avec niveaux d'accès
- ✅ **Conformité** aux standards comptables
- ✅ **Traçabilité** complète des opérations
- ✅ **Archivage automatique** selon les règles

### **Pour les Utilisateurs**
- ✅ **Interface simple** et intuitive
- ✅ **Recherche rapide** et efficace
- ✅ **Workflows automatisés** d'approbation
- ✅ **Notifications** en temps réel
- ✅ **Accès sécurisé** selon les permissions

### **Pour l'Administration**
- ✅ **Gestion centralisée** des workflows
- ✅ **Statistiques détaillées** d'utilisation
- ✅ **Audit trail** complet
- ✅ **Configuration flexible** des règles
- ✅ **Intégration native** avec la plateforme

---

## 🎯 **CONCLUSION**

Le **Module 17 - Gestion Électronique Documentaire** offre une solution complète et professionnelle pour la gestion documentaire dans E-COMPTA IA. Il combine sécurité, conformité et facilité d'utilisation pour répondre aux besoins des entreprises modernes.

**Le module est prêt pour les tests et l'utilisation en production !** 🚀


