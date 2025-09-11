import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map, tap } from 'rxjs/operators';

export interface GedDocument {
  id?: number;
  documentCode: string;
  title: string;
  description?: string;
  fileName: string;
  filePath: string;
  fileSize?: number;
  fileType?: string;
  mimeType?: string;
  documentType: DocumentType;
  category?: string;
  tags?: string;
  version: number;
  isCurrentVersion: boolean;
  parentDocumentId?: number;
  status: DocumentStatus;
  securityLevel: SecurityLevel;
  expiryDate?: string;
  isArchived: boolean;
  archiveDate?: string;
  retentionPeriodYears?: number;
  moduleReference?: string;
  entityReferenceId?: number;
  entityReferenceType?: string;
  ecritureId?: number;
  companyId: number;
  countryCode: string;
  accountingStandard: string;
  createdAt: string;
  updatedAt?: string;
  createdBy?: number;
  updatedBy?: number;
  approvedBy?: number;
  approvedAt?: string;
  checksum?: string;
  isEncrypted: boolean;
}

export enum DocumentType {
  INVOICE = 'INVOICE',
  RECEIPT = 'RECEIPT',
  CONTRACT = 'CONTRACT',
  REPORT = 'REPORT',
  POLICY = 'POLICY',
  PROCEDURE = 'PROCEDURE',
  MANUAL = 'MANUAL',
  CERTIFICATE = 'CERTIFICATE',
  LICENSE = 'LICENSE',
  INSURANCE = 'INSURANCE',
  TAX_DOCUMENT = 'TAX_DOCUMENT',
  LEGAL_DOCUMENT = 'LEGAL_DOCUMENT',
  HR_DOCUMENT = 'HR_DOCUMENT',
  ASSET_DOCUMENT = 'ASSET_DOCUMENT',
  INVENTORY_DOCUMENT = 'INVENTORY_DOCUMENT',
  FINANCIAL_STATEMENT = 'FINANCIAL_STATEMENT',
  AUDIT_REPORT = 'AUDIT_REPORT',
  COMPLIANCE_DOCUMENT = 'COMPLIANCE_DOCUMENT',
  OTHER = 'OTHER'
}

export enum DocumentStatus {
  DRAFT = 'DRAFT',
  PENDING_APPROVAL = 'PENDING_APPROVAL',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  ARCHIVED = 'ARCHIVED',
  EXPIRED = 'EXPIRED',
  DELETED = 'DELETED'
}

export enum SecurityLevel {
  PUBLIC = 'PUBLIC',
  INTERNAL = 'INTERNAL',
  CONFIDENTIAL = 'CONFIDENTIAL',
  RESTRICTED = 'RESTRICTED',
  SECRET = 'SECRET'
}

export interface DocumentFilter {
  documentType?: DocumentType;
  status?: DocumentStatus;
  securityLevel?: SecurityLevel;
  category?: string;
  moduleReference?: string;
  searchTerm?: string;
  createdBy?: number;
  dateFrom?: string;
  dateTo?: string;
}

export interface DocumentStats {
  totalDocuments: number;
  documentsByType: { [key: string]: number };
  documentsByStatus: { [key: string]: number };
  documentsBySecurityLevel: { [key: string]: number };
  totalSize: number;
  recentDocuments: GedDocument[];
}

@Injectable({
  providedIn: 'root'
})
export class DocumentManagementService {
  private readonly apiUrl = '/api/document-management';
  private documentsSubject = new BehaviorSubject<GedDocument[]>([]);
  public documents$ = this.documentsSubject.asObservable();

  constructor(private http: HttpClient) {}

  // ==================== GESTION DES DOCUMENTS ====================

  /**
   * Récupérer tous les documents
   */
  getAllDocuments(): Observable<GedDocument[]> {
    return this.http.get<{documents: GedDocument[], total: number, status: string}>(`${this.apiUrl}/documents`)
      .pipe(
        map(response => response.documents),
        tap(documents => this.documentsSubject.next(documents))
      );
  }

  /**
   * Récupérer un document par ID
   */
  getDocumentById(id: number): Observable<GedDocument> {
    return this.http.get<GedDocument>(`${this.apiUrl}/documents/${id}`);
  }

  /**
   * Créer un nouveau document
   */
  createDocument(document: GedDocument): Observable<GedDocument> {
    return this.http.post<GedDocument>(`${this.apiUrl}/documents`, document)
      .pipe(
        tap(newDocument => {
          const currentDocuments = this.documentsSubject.value;
          this.documentsSubject.next([...currentDocuments, newDocument]);
        })
      );
  }

  /**
   * Mettre à jour un document
   */
  updateDocument(id: number, document: GedDocument): Observable<GedDocument> {
    return this.http.put<GedDocument>(`${this.apiUrl}/documents/${id}`, document)
      .pipe(
        tap(updatedDocument => {
          const currentDocuments = this.documentsSubject.value;
          const index = currentDocuments.findIndex(d => d.id === id);
          if (index !== -1) {
            currentDocuments[index] = updatedDocument;
            this.documentsSubject.next([...currentDocuments]);
          }
        })
      );
  }

  /**
   * Supprimer un document
   */
  deleteDocument(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/documents/${id}`)
      .pipe(
        tap(() => {
          const currentDocuments = this.documentsSubject.value;
          this.documentsSubject.next(currentDocuments.filter(d => d.id !== id));
        })
      );
  }

  /**
   * Rechercher des documents
   */
  searchDocuments(searchTerm: string, companyId: number): Observable<GedDocument[]> {
    const params = new HttpParams()
      .set('searchTerm', searchTerm)
      .set('companyId', companyId.toString());
    
    return this.http.get<GedDocument[]>(`${this.apiUrl}/documents/search`, { params });
  }

  /**
   * Filtrer les documents
   */
  filterDocuments(filter: DocumentFilter, companyId: number): Observable<GedDocument[]> {
    let params = new HttpParams().set('companyId', companyId.toString());
    
    if (filter.documentType) params = params.set('documentType', filter.documentType);
    if (filter.status) params = params.set('status', filter.status);
    if (filter.securityLevel) params = params.set('securityLevel', filter.securityLevel);
    if (filter.category) params = params.set('category', filter.category);
    if (filter.moduleReference) params = params.set('moduleReference', filter.moduleReference);
    if (filter.searchTerm) params = params.set('searchTerm', filter.searchTerm);
    if (filter.createdBy) params = params.set('createdBy', filter.createdBy.toString());
    if (filter.dateFrom) params = params.set('dateFrom', filter.dateFrom);
    if (filter.dateTo) params = params.set('dateTo', filter.dateTo);
    
    return this.http.get<GedDocument[]>(`${this.apiUrl}/documents/filter`, { params });
  }

  // ==================== UPLOAD DE DOCUMENTS ====================

  /**
   * Uploader un fichier
   */
  uploadDocument(file: File, documentData: Partial<GedDocument>): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('documentData', JSON.stringify(documentData));
    
    return this.http.post(`${this.apiUrl}/documents/upload`, formData);
  }

  /**
   * Télécharger un document
   */
  downloadDocument(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/documents/${id}/download`, {
      responseType: 'blob'
    });
  }

  /**
   * Prévisualiser un document
   */
  previewDocument(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/documents/${id}/preview`, {
      responseType: 'blob'
    });
  }

  // ==================== STATISTIQUES ====================

  /**
   * Récupérer les statistiques des documents
   */
  getDocumentStats(companyId: number): Observable<DocumentStats> {
    return this.http.get<DocumentStats>(`${this.apiUrl}/documents/stats/${companyId}`);
  }

  /**
   * Récupérer les documents récents
   */
  getRecentDocuments(companyId: number, limit: number = 10): Observable<GedDocument[]> {
    const params = new HttpParams()
      .set('companyId', companyId.toString())
      .set('limit', limit.toString());
    
    return this.http.get<GedDocument[]>(`${this.apiUrl}/documents/recent`, { params });
  }

  // ==================== GESTION DES VERSIONS ====================

  /**
   * Récupérer les versions d'un document
   */
  getDocumentVersions(documentId: number): Observable<GedDocument[]> {
    return this.http.get<GedDocument[]>(`${this.apiUrl}/documents/${documentId}/versions`);
  }

  /**
   * Créer une nouvelle version d'un document
   */
  createDocumentVersion(documentId: number, file: File): Observable<GedDocument> {
    const formData = new FormData();
    formData.append('file', file);
    
    return this.http.post<GedDocument>(`${this.apiUrl}/documents/${documentId}/versions`, formData);
  }

  /**
   * Restaurer une version d'un document
   */
  restoreDocumentVersion(documentId: number, versionId: number): Observable<GedDocument> {
    return this.http.post<GedDocument>(`${this.apiUrl}/documents/${documentId}/versions/${versionId}/restore`, {});
  }

  // ==================== GESTION DES WORKFLOWS ====================

  /**
   * Récupérer les workflows disponibles
   */
  getWorkflows(companyId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/workflow-management/workflows?companyId=${companyId}`);
  }
  
  /**
   * Récupérer les workflows actifs par type de document
   */
  getActiveWorkflowsByDocumentType(companyId: number, documentType: DocumentType): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/workflow-management/workflows/active?companyId=${companyId}&documentType=${documentType}`);
  }
  
  /**
   * Récupérer les approbations en attente pour un utilisateur
   */
  getPendingApprovalsForUser(userId: number, companyId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/workflow-management/approvals/pending?userId=${userId}&companyId=${companyId}`);
  }
  
  /**
   * Récupérer les approbations d'un document
   */
  getDocumentApprovals(documentId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/workflow-management/approvals/document/${documentId}`);
  }
  
  /**
   * Initialiser un workflow pour un document
   */
  initializeDocumentWorkflow(documentId: number, companyId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/workflow-management/workflows/initialize`, {
      documentId,
      companyId
    });
  }
  
  /**
   * Approuver un document
   */
  approveDocument(approvalId: number, comments: string, approverId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/workflow-management/approvals/${approvalId}/approve`, {
      comments,
      approverId
    });
  }
  
  /**
   * Rejeter un document
   */
  rejectDocument(approvalId: number, comments: string, approverId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/workflow-management/approvals/${approvalId}/reject`, {
      comments,
      approverId
    });
  }
  
  /**
   * Annuler une approbation
   */
  cancelApproval(approvalId: number, comments: string, approverId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/workflow-management/approvals/${approvalId}/cancel`, {
      comments,
      approverId
    });
  }
  
  /**
   * Récupérer les statistiques des workflows
   */
  getWorkflowStatistics(companyId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/workflow-management/statistics?companyId=${companyId}`);
  }
  
  /**
   * Récupérer les documents en attente d'approbation
   */
  getDocumentsPendingApproval(companyId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/workflow-management/documents/pending-approval?companyId=${companyId}`);
  }

  // ==================== RECHERCHE ELASTICSEARCH ====================

  /**
   * Recherche full-text dans tous les documents
   */
  searchDocuments(query: string, page: number = 0, size: number = 20): Observable<any> {
    return this.http.get(`${this.apiUrl}/search/full-text?query=${encodeURIComponent(query)}&page=${page}&size=${size}`);
  }

  /**
   * Recherche full-text dans les documents d'une entreprise
   */
  searchDocumentsByCompany(companyId: number, query: string, page: number = 0, size: number = 20): Observable<any> {
    return this.http.get(`${this.apiUrl}/search/full-text/company/${companyId}?query=${encodeURIComponent(query)}&page=${page}&size=${size}`);
  }

  /**
   * Recherche par type de document
   */
  searchByDocumentType(companyId: number, documentType: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/search/by-type/${companyId}?documentType=${documentType}`);
  }

  /**
   * Recherche par catégorie
   */
  searchByCategory(companyId: number, category: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/search/by-category/${companyId}?category=${category}`);
  }

  /**
   * Recherche par statut
   */
  searchByStatus(companyId: number, status: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/search/by-status/${companyId}?status=${status}`);
  }

  /**
   * Recherche par niveau de sécurité
   */
  searchBySecurityLevel(companyId: number, securityLevel: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/search/by-security-level/${companyId}?securityLevel=${securityLevel}`);
  }

  /**
   * Recherche par créateur
   */
  searchByCreator(companyId: number, createdBy: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/search/by-creator/${companyId}?createdBy=${createdBy}`);
  }

  /**
   * Recherche par période
   */
  searchByDateRange(companyId: number, startDate: string, endDate: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/search/by-date-range/${companyId}?startDate=${startDate}&endDate=${endDate}`);
  }

  /**
   * Recherche par tags
   */
  searchByTags(companyId: number, tag: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/search/by-tags/${companyId}?tag=${tag}`);
  }

  /**
   * Recherche par nom de fichier
   */
  searchByFileName(companyId: number, fileName: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/search/by-filename/${companyId}?fileName=${fileName}`);
  }

  /**
   * Recherche par code de document
   */
  searchByDocumentCode(companyId: number, documentCode: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/search/by-document-code/${companyId}?documentCode=${documentCode}`);
  }

  /**
   * Recherche de documents récents
   */
  searchRecentDocuments(companyId: number, daysBack: number = 7): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/search/recent/${companyId}?daysBack=${daysBack}`);
  }

  /**
   * Recherche de documents expirés
   */
  searchExpiredDocuments(companyId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/search/expired/${companyId}`);
  }

  /**
   * Recherche de documents expirant bientôt
   */
  searchDocumentsExpiringSoon(companyId: number, daysAhead: number = 30): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/search/expiring-soon/${companyId}?daysAhead=${daysAhead}`);
  }

  /**
   * Reconstruire l'index de recherche
   */
  rebuildSearchIndex(): Observable<any> {
    return this.http.post(`${this.apiUrl}/search/rebuild-index`, {});
  }

  /**
   * Vérifier la santé de l'index de recherche
   */
  getSearchIndexHealth(): Observable<any> {
    return this.http.get(`${this.apiUrl}/search/health`);
  }

  // ==================== VERSIONING AVANCÉ ====================

  /**
   * Créer une nouvelle version d'un document
   */
  createNewVersion(documentId: number, file: File, versionType: string, changeSummary: string, changeDetails: string, createdBy: number): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('versionType', versionType);
    formData.append('changeSummary', changeSummary);
    formData.append('changeDetails', changeDetails);
    formData.append('createdBy', createdBy.toString());

    return this.http.post(`${this.apiUrl}/document-versioning/documents/${documentId}/versions`, formData);
  }

  /**
   * Créer une version majeure
   */
  createMajorVersion(documentId: number, file: File, changeSummary: string, changeDetails: string, createdBy: number): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('changeSummary', changeSummary);
    formData.append('changeDetails', changeDetails);
    formData.append('createdBy', createdBy.toString());

    return this.http.post(`${this.apiUrl}/document-versioning/documents/${documentId}/versions/major`, formData);
  }

  /**
   * Créer une version mineure
   */
  createMinorVersion(documentId: number, file: File, changeSummary: string, changeDetails: string, createdBy: number): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('changeSummary', changeSummary);
    formData.append('changeDetails', changeDetails);
    formData.append('createdBy', createdBy.toString());

    return this.http.post(`${this.apiUrl}/document-versioning/documents/${documentId}/versions/minor`, formData);
  }

  /**
   * Créer un correctif
   */
  createPatchVersion(documentId: number, file: File, changeSummary: string, changeDetails: string, createdBy: number): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('changeSummary', changeSummary);
    formData.append('changeDetails', changeDetails);
    formData.append('createdBy', createdBy.toString());

    return this.http.post(`${this.apiUrl}/document-versioning/documents/${documentId}/versions/patch`, formData);
  }

  /**
   * Créer une version de brouillon (auto-save)
   */
  createDraftVersion(documentId: number, file: File, createdBy: number, autoSaveIntervalMinutes: number = 5): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('createdBy', createdBy.toString());
    formData.append('autoSaveIntervalMinutes', autoSaveIntervalMinutes.toString());

    return this.http.post(`${this.apiUrl}/document-versioning/documents/${documentId}/versions/draft`, formData);
  }

  /**
   * Approuver une version
   */
  approveVersion(versionId: number, approvedBy: number, approvalNotes: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/document-versioning/versions/${versionId}/approve`, null, {
      params: {
        approvedBy: approvedBy.toString(),
        approvalNotes: approvalNotes
      }
    });
  }

  /**
   * Rejeter une version
   */
  rejectVersion(versionId: number, rejectedBy: number, rejectionReason: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/document-versioning/versions/${versionId}/reject`, null, {
      params: {
        rejectedBy: rejectedBy.toString(),
        rejectionReason: rejectionReason
      }
    });
  }

  /**
   * Archiver une version
   */
  archiveVersion(versionId: number, archivedBy: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/document-versioning/versions/${versionId}/archive`, null, {
      params: {
        archivedBy: archivedBy.toString()
      }
    });
  }

  /**
   * Restaurer une version archivée
   */
  restoreVersion(versionId: number, restoredBy: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/document-versioning/versions/${versionId}/restore`, null, {
      params: {
        restoredBy: restoredBy.toString()
      }
    });
  }

  /**
   * Supprimer une version
   */
  deleteVersion(versionId: number, deletedBy: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/document-versioning/versions/${versionId}`, {
      params: {
        deletedBy: deletedBy.toString()
      }
    });
  }

  /**
   * Récupérer toutes les versions d'un document
   */
  getDocumentVersions(documentId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/document-versioning/documents/${documentId}/versions`);
  }

  /**
   * Récupérer la version actuelle d'un document
   */
  getCurrentVersion(documentId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/document-versioning/documents/${documentId}/versions/current`);
  }

  /**
   * Récupérer une version spécifique
   */
  getVersion(documentId: number, versionNumber: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/document-versioning/documents/${documentId}/versions/${versionNumber}`);
  }

  /**
   * Récupérer la dernière version d'un document
   */
  getLatestVersion(documentId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/document-versioning/documents/${documentId}/versions/latest`);
  }

  /**
   * Récupérer les versions par type
   */
  getVersionsByType(documentId: number, versionType: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/document-versioning/documents/${documentId}/versions/type/${versionType}`);
  }

  /**
   * Récupérer les versions en attente d'approbation
   */
  getPendingApprovalVersions(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/document-versioning/versions/pending-approval`);
  }

  /**
   * Récupérer les versions approuvées
   */
  getApprovedVersions(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/document-versioning/versions/approved`);
  }

  /**
   * Récupérer les versions archivées
   */
  getArchivedVersions(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/document-versioning/versions/archived`);
  }

  /**
   * Récupérer les versions expirées
   */
  getExpiredVersions(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/document-versioning/versions/expired`);
  }

  /**
   * Récupérer les versions expirant bientôt
   */
  getVersionsExpiringSoon(daysAhead: number = 30): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/document-versioning/versions/expiring-soon?daysAhead=${daysAhead}`);
  }

  /**
   * Créer une nouvelle branche
   */
  createBranch(documentId: number, branchName: string, file: File, createdBy: number): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('createdBy', createdBy.toString());

    return this.http.post(`${this.apiUrl}/document-versioning/documents/${documentId}/branches/${branchName}`, formData);
  }

  /**
   * Fusionner une branche
   */
  mergeBranch(sourceVersionId: number, targetDocumentId: number, mergedFile: File, createdBy: number): Observable<any> {
    const formData = new FormData();
    formData.append('targetDocumentId', targetDocumentId.toString());
    formData.append('file', mergedFile);
    formData.append('createdBy', createdBy.toString());

    return this.http.post(`${this.apiUrl}/document-versioning/versions/${sourceVersionId}/merge`, formData);
  }

  /**
   * Récupérer les versions d'une branche
   */
  getBranchVersions(documentId: number, branchName: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/document-versioning/documents/${documentId}/branches/${branchName}/versions`);
  }

  /**
   * Signer numériquement une version
   */
  signVersion(versionId: number, digitalSignature: string, signatureAlgorithm: string, certificateChain: string, signedBy: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/document-versioning/versions/${versionId}/sign`, null, {
      params: {
        digitalSignature,
        signatureAlgorithm,
        certificateChain,
        signedBy: signedBy.toString()
      }
    });
  }

  /**
   * Vérifier la signature numérique d'une version
   */
  verifyDigitalSignature(versionId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/document-versioning/versions/${versionId}/verify-signature`);
  }

  /**
   * Appliquer un filigrane à une version
   */
  applyWatermark(versionId: number, watermarkText: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/document-versioning/versions/${versionId}/watermark`, null, {
      params: {
        watermarkText
      }
    });
  }

  /**
   * Supprimer le filigrane d'une version
   */
  removeWatermark(versionId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/document-versioning/versions/${versionId}/watermark`);
  }

  /**
   * Compresser une version
   */
  compressVersion(versionId: number, compressionAlgorithm: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/document-versioning/versions/${versionId}/compress`, null, {
      params: {
        compressionAlgorithm
      }
    });
  }

  /**
   * Activer l'auto-save pour une version
   */
  enableAutoSave(versionId: number, intervalMinutes: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/document-versioning/versions/${versionId}/auto-save/enable`, null, {
      params: {
        intervalMinutes: intervalMinutes.toString()
      }
    });
  }

  /**
   * Désactiver l'auto-save pour une version
   */
  disableAutoSave(versionId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/document-versioning/versions/${versionId}/auto-save/disable`, null);
  }

  /**
   * Traiter les auto-saves en attente
   */
  processAutoSaves(): Observable<any> {
    return this.http.post(`${this.apiUrl}/document-versioning/auto-save/process`, null);
  }

  /**
   * Récupérer les statistiques de versioning pour un document
   */
  getVersioningStatistics(documentId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/document-versioning/documents/${documentId}/statistics`);
  }

  /**
   * Récupérer les versions les plus accédées
   */
  getMostAccessedVersions(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/document-versioning/versions/most-accessed`);
  }

  /**
   * Récupérer les versions les plus téléchargées
   */
  getMostDownloadedVersions(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/document-versioning/versions/most-downloaded`);
  }

  /**
   * Démarrer un workflow pour un document
   */
  startWorkflow(documentId: number, workflowId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/documents/${documentId}/workflow/${workflowId}/start`, {});
  }

  /**
   * Approuver un document
   */
  approveDocument(documentId: number, comments?: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/documents/${documentId}/approve`, { comments });
  }

  /**
   * Rejeter un document
   */
  rejectDocument(documentId: number, comments: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/documents/${documentId}/reject`, { comments });
  }

  // ==================== UTILITAIRES ====================

  /**
   * Formater la taille de fichier
   */
  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  /**
   * Obtenir l'icône pour un type de document
   */
  getDocumentTypeIcon(documentType: DocumentType): string {
    const icons: { [key in DocumentType]: string } = {
      [DocumentType.INVOICE]: 'receipt',
      [DocumentType.RECEIPT]: 'receipt_long',
      [DocumentType.CONTRACT]: 'description',
      [DocumentType.REPORT]: 'assessment',
      [DocumentType.POLICY]: 'policy',
      [DocumentType.PROCEDURE]: 'rule',
      [DocumentType.MANUAL]: 'menu_book',
      [DocumentType.CERTIFICATE]: 'verified',
      [DocumentType.LICENSE]: 'card_membership',
      [DocumentType.INSURANCE]: 'security',
      [DocumentType.TAX_DOCUMENT]: 'account_balance',
      [DocumentType.LEGAL_DOCUMENT]: 'gavel',
      [DocumentType.HR_DOCUMENT]: 'people',
      [DocumentType.ASSET_DOCUMENT]: 'inventory',
      [DocumentType.INVENTORY_DOCUMENT]: 'warehouse',
      [DocumentType.FINANCIAL_STATEMENT]: 'account_balance_wallet',
      [DocumentType.AUDIT_REPORT]: 'search',
      [DocumentType.COMPLIANCE_DOCUMENT]: 'verified_user',
      [DocumentType.OTHER]: 'insert_drive_file'
    };
    
    return icons[documentType] || 'insert_drive_file';
  }

  /**
   * Obtenir la couleur pour un statut de document
   */
  getStatusColor(status: DocumentStatus): string {
    const colors: { [key in DocumentStatus]: string } = {
      [DocumentStatus.DRAFT]: 'grey',
      [DocumentStatus.PENDING_APPROVAL]: 'orange',
      [DocumentStatus.APPROVED]: 'green',
      [DocumentStatus.REJECTED]: 'red',
      [DocumentStatus.ARCHIVED]: 'blue-grey',
      [DocumentStatus.EXPIRED]: 'red',
      [DocumentStatus.DELETED]: 'red'
    };
    
    return colors[status] || 'grey';
  }

  /**
   * Obtenir la couleur pour un niveau de sécurité
   */
  getSecurityLevelColor(securityLevel: SecurityLevel): string {
    const colors: { [key in SecurityLevel]: string } = {
      [SecurityLevel.PUBLIC]: 'green',
      [SecurityLevel.INTERNAL]: 'blue',
      [SecurityLevel.CONFIDENTIAL]: 'orange',
      [SecurityLevel.RESTRICTED]: 'red',
      [SecurityLevel.SECRET]: 'purple'
    };
    
    return colors[securityLevel] || 'grey';
  }
}
