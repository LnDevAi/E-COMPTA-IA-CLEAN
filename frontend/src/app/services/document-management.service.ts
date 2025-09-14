import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface GedDocument {
  id: number;
  fileName: string;
  fileType: string;
  description: string;
  companyId: number;
  uploadedBy: number;
  fileSize: number;
  filePath: string;
  uploadDate: string;
  isActive: boolean;
  status: string;
}

export interface DocumentVersion {
  id: number;
  documentId: number;
  versionName: string;
  description: string;
  createdBy: number;
  createdAt: string;
  isActive: boolean;
}

export interface DocumentWorkflow {
  id: number;
  documentId: number;
  workflowName: string;
  description: string;
  companyId: number;
  status: string;
  approvedBy?: number;
  approvalDate?: string;
  comments?: string;
  createdAt: string;
  isActive: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class DocumentManagementService {
  private apiUrl = `${environment.apiUrl}/api/document-management`;

  constructor(private http: HttpClient) { }

  /**
   * Uploader un document
   */
  uploadDocument(file: File, fileName: string, fileType: string, description: string, 
                companyId: number, userId: number): Observable<GedDocument> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('fileName', fileName);
    formData.append('fileType', fileType);
    formData.append('description', description);
    formData.append('companyId', companyId.toString());
    formData.append('userId', userId.toString());

    return this.http.post<GedDocument>(`${this.apiUrl}/upload`, formData);
  }

  /**
   * Créer une nouvelle version d'un document
   */
  createDocumentVersion(documentId: number, versionName: string, description: string, userId: number): Observable<DocumentVersion> {
    return this.http.post<DocumentVersion>(`${this.apiUrl}/versions`, {
      documentId,
      versionName,
      description,
      userId
    });
  }

  /**
   * Créer un workflow d'approbation pour un document
   */
  createDocumentWorkflow(documentId: number, workflowName: string, description: string, companyId: number): Observable<DocumentWorkflow> {
    return this.http.post<DocumentWorkflow>(`${this.apiUrl}/workflows`, {
      documentId,
      workflowName,
      description,
      companyId
    });
  }

  /**
   * Approuver un document
   */
  approveDocument(workflowId: number, approverId: number, comments: string): Observable<DocumentWorkflow> {
    return this.http.put<DocumentWorkflow>(`${this.apiUrl}/workflows/${workflowId}/approve`, {
      approverId,
      comments
    });
  }

  /**
   * Rejeter un document
   */
  rejectDocument(workflowId: number, approverId: number, reason: string): Observable<DocumentWorkflow> {
    return this.http.put<DocumentWorkflow>(`${this.apiUrl}/workflows/${workflowId}/reject`, {
      approverId,
      reason
    });
  }

  /**
   * Obtenir tous les documents d'une entreprise
   */
  getCompanyDocuments(companyId: number): Observable<GedDocument[]> {
    return this.http.get<GedDocument[]>(`${this.apiUrl}/documents/company/${companyId}`);
  }

  /**
   * Obtenir les versions d'un document
   */
  getDocumentVersions(documentId: number): Observable<DocumentVersion[]> {
    return this.http.get<DocumentVersion[]>(`${this.apiUrl}/versions/document/${documentId}`);
  }

  /**
   * Obtenir les workflows d'un document
   */
  getDocumentWorkflows(documentId: number): Observable<DocumentWorkflow[]> {
    return this.http.get<DocumentWorkflow[]>(`${this.apiUrl}/workflows/document/${documentId}`);
  }

  /**
   * Rechercher des documents par nom
   */
  searchDocumentsByName(fileName: string, companyId: number): Observable<GedDocument[]> {
    return this.http.get<GedDocument[]>(`${this.apiUrl}/documents/search?fileName=${fileName}&companyId=${companyId}`);
  }

  /**
   * Rechercher des documents par type
   */
  searchDocumentsByType(fileType: string, companyId: number): Observable<GedDocument[]> {
    return this.http.get<GedDocument[]>(`${this.apiUrl}/documents/search?fileType=${fileType}&companyId=${companyId}`);
  }

  /**
   * Supprimer un document
   */
  deleteDocument(documentId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/documents/${documentId}`);
  }

  /**
   * Mettre à jour les métadonnées d'un document
   */
  updateDocumentMetadata(documentId: number, fileName: string, description: string): Observable<GedDocument> {
    return this.http.put<GedDocument>(`${this.apiUrl}/documents/${documentId}/metadata`, {
      fileName,
      description
    });
  }

  /**
   * Obtenir les statistiques des documents
   */
  getDocumentStatistics(companyId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/statistics/company/${companyId}`);
  }

  /**
   * Télécharger un document
   */
  downloadDocument(documentId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/documents/${documentId}/download`, {
      responseType: 'blob'
    });
  }

  /**
   * Prévisualiser un document
   */
  previewDocument(documentId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/documents/${documentId}/preview`, {
      responseType: 'blob'
    });
  }

  /**
   * Obtenir les documents en attente d'approbation
   */
  getPendingDocuments(companyId: number): Observable<DocumentWorkflow[]> {
    return this.http.get<DocumentWorkflow[]>(`${this.apiUrl}/workflows/pending?companyId=${companyId}`);
  }

  /**
   * Obtenir les documents approuvés
   */
  getApprovedDocuments(companyId: number): Observable<DocumentWorkflow[]> {
    return this.http.get<DocumentWorkflow[]>(`${this.apiUrl}/workflows/approved?companyId=${companyId}`);
  }

  /**
   * Obtenir les documents rejetés
   */
  getRejectedDocuments(companyId: number): Observable<DocumentWorkflow[]> {
    return this.http.get<DocumentWorkflow[]>(`${this.apiUrl}/workflows/rejected?companyId=${companyId}`);
  }
}

