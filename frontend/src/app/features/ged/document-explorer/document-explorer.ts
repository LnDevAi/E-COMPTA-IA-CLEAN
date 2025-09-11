import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSortModule, Sort } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDividerModule } from '@angular/material/divider';
import { Subject, takeUntil, debounceTime, distinctUntilChanged } from 'rxjs';

import { DocumentManagementService, GedDocument, DocumentType, DocumentStatus, SecurityLevel, DocumentFilter } from '../../../shared/services/document-management.service';
import { NgIf, NgFor } from '@angular/common';

@Component({
  selector: 'app-document-explorer',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatDialogModule,
    MatTooltipModule,
    MatCheckboxModule,
    MatDividerModule,
    NgIf,
    NgFor
  ],
  templateUrl: './document-explorer.html',
  styleUrls: ['./document-explorer.scss']
})
export class DocumentExplorerComponent implements OnInit, OnDestroy {
  // Données
  documents: GedDocument[] = [];
  filteredDocuments: GedDocument[] = [];
  selectedDocuments: GedDocument[] = [];
  
  // États
  loading = false;
  error: string | null = null;
  
  // Pagination et tri
  totalItems = 0;
  pageSize = 25;
  pageIndex = 0;
  sortField = 'createdAt';
  sortDirection: 'asc' | 'desc' = 'desc';
  
  // Filtres
  filter: DocumentFilter = {};
  searchTerm = '';
  
  // Colonnes du tableau
  displayedColumns: string[] = [
    'select',
    'title',
    'documentType',
    'status',
    'securityLevel',
    'category',
    'fileSize',
    'createdAt',
    'createdBy',
    'actions'
  ];
  
  // Options pour les filtres
  documentTypes = Object.values(DocumentType);
  documentStatuses = Object.values(DocumentStatus);
  securityLevels = Object.values(SecurityLevel);
  
  // Subject pour la destruction
  private destroy$ = new Subject<void>();
  
  constructor(
    private documentService: DocumentManagementService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}
  
  ngOnInit(): void {
    this.loadDocuments();
    this.setupSearch();
  }
  
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
  
  // ==================== CHARGEMENT DES DONNÉES ====================
  
  loadDocuments(): void {
    this.loading = true;
    this.error = null;
    
    this.documentService.getAllDocuments()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (documents) => {
          this.documents = documents;
          this.applyFilters();
          this.loading = false;
        },
        error: (error) => {
          this.error = 'Erreur lors du chargement des documents';
          this.loading = false;
          this.snackBar.open(this.error, 'Fermer', { duration: 5000 });
        }
      });
  }
  
  private setupSearch(): void {
    // Debounce pour la recherche
    // Note: Dans un vrai projet, on utiliserait un FormControl avec debounce
  }
  
  // ==================== FILTRES ET RECHERCHE ====================
  
  applyFilters(): void {
    let filtered = [...this.documents];
    
    // Recherche textuelle
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(doc => 
        doc.title.toLowerCase().includes(term) ||
        doc.description?.toLowerCase().includes(term) ||
        doc.documentCode.toLowerCase().includes(term) ||
        doc.tags?.toLowerCase().includes(term)
      );
    }
    
    // Filtres par propriétés
    if (this.filter.documentType) {
      filtered = filtered.filter(doc => doc.documentType === this.filter.documentType);
    }
    
    if (this.filter.status) {
      filtered = filtered.filter(doc => doc.status === this.filter.status);
    }
    
    if (this.filter.securityLevel) {
      filtered = filtered.filter(doc => doc.securityLevel === this.filter.securityLevel);
    }
    
    if (this.filter.category) {
      filtered = filtered.filter(doc => doc.category === this.filter.category);
    }
    
    this.filteredDocuments = filtered;
    this.totalItems = filtered.length;
    this.pageIndex = 0; // Reset à la première page
  }
  
  clearFilters(): void {
    this.filter = {};
    this.searchTerm = '';
    this.applyFilters();
  }
  
  // ==================== PAGINATION ET TRI ====================
  
  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
  }
  
  onSortChange(sort: Sort): void {
    this.sortField = sort.active;
    this.sortDirection = sort.direction as 'asc' | 'desc';
    
    // Appliquer le tri
    this.filteredDocuments.sort((a, b) => {
      const aValue = this.getFieldValue(a, this.sortField);
      const bValue = this.getFieldValue(b, this.sortField);
      
      if (aValue < bValue) return this.sortDirection === 'asc' ? -1 : 1;
      if (aValue > bValue) return this.sortDirection === 'asc' ? 1 : -1;
      return 0;
    });
  }
  
  private getFieldValue(document: GedDocument, field: string): any {
    switch (field) {
      case 'title': return document.title;
      case 'documentType': return document.documentType;
      case 'status': return document.status;
      case 'securityLevel': return document.securityLevel;
      case 'category': return document.category || '';
      case 'fileSize': return document.fileSize || 0;
      case 'createdAt': return new Date(document.createdAt);
      case 'createdBy': return document.createdBy || 0;
      default: return '';
    }
  }
  
  // ==================== SÉLECTION ====================
  
  isAllSelected(): boolean {
    return this.selectedDocuments.length === this.getCurrentPageDocuments().length;
  }
  
  isIndeterminate(): boolean {
    return this.selectedDocuments.length > 0 && !this.isAllSelected();
  }
  
  masterToggle(): void {
    if (this.isAllSelected()) {
      this.selectedDocuments = [];
    } else {
      this.selectedDocuments = [...this.getCurrentPageDocuments()];
    }
  }
  
  toggleSelection(document: GedDocument): void {
    const index = this.selectedDocuments.findIndex(d => d.id === document.id);
    if (index > -1) {
      this.selectedDocuments.splice(index, 1);
    } else {
      this.selectedDocuments.push(document);
    }
  }
  
  isSelected(document: GedDocument): boolean {
    return this.selectedDocuments.some(d => d.id === document.id);
  }
  
  getCurrentPageDocuments(): GedDocument[] {
    const start = this.pageIndex * this.pageSize;
    const end = start + this.pageSize;
    return this.filteredDocuments.slice(start, end);
  }
  
  // ==================== ACTIONS ====================
  
  uploadDocument(): void {
    // TODO: Ouvrir le dialog d'upload
    this.snackBar.open('Fonctionnalité d'upload à implémenter', 'Fermer', { duration: 3000 });
  }
  
  async viewDocument(document: GedDocument): Promise<void> {
    const { DocumentViewerComponent } = await import('../document-viewer/document-viewer');
    
    const dialogRef = this.dialog.open(DocumentViewerComponent, {
      width: '90vw',
      height: '90vh',
      maxWidth: '100vw',
      maxHeight: '100vh',
      data: { document },
      panelClass: 'document-viewer-dialog'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        // Actions après fermeture du visualiseur si nécessaire
      }
    });
  }
  
  downloadDocument(document: GedDocument): void {
    this.documentService.downloadDocument(document.id!)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (blob) => {
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = document.fileName;
          link.click();
          window.URL.revokeObjectURL(url);
        },
        error: (error) => {
          this.snackBar.open('Erreur lors du téléchargement', 'Fermer', { duration: 5000 });
        }
      });
  }
  
  editDocument(document: GedDocument): void {
    // TODO: Implémenter l'édition
    this.snackBar.open(`Édition de ${document.title}`, 'Fermer', { duration: 3000 });
  }
  
  deleteDocument(document: GedDocument): void {
    if (confirm(`Êtes-vous sûr de vouloir supprimer le document "${document.title}" ?`)) {
      this.documentService.deleteDocument(document.id!)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.snackBar.open('Document supprimé avec succès', 'Fermer', { duration: 3000 });
            this.loadDocuments();
          },
          error: (error) => {
            this.snackBar.open('Erreur lors de la suppression', 'Fermer', { duration: 5000 });
          }
        });
    }
  }
  
  // ==================== ACTIONS EN LOT ====================
  
  downloadSelected(): void {
    if (this.selectedDocuments.length === 0) {
      this.snackBar.open('Aucun document sélectionné', 'Fermer', { duration: 3000 });
      return;
    }
    
    // TODO: Implémenter le téléchargement en lot
    this.snackBar.open(`Téléchargement de ${this.selectedDocuments.length} documents`, 'Fermer', { duration: 3000 });
  }
  
  deleteSelected(): void {
    if (this.selectedDocuments.length === 0) {
      this.snackBar.open('Aucun document sélectionné', 'Fermer', { duration: 3000 });
      return;
    }
    
    if (confirm(`Êtes-vous sûr de vouloir supprimer ${this.selectedDocuments.length} document(s) ?`)) {
      // TODO: Implémenter la suppression en lot
      this.snackBar.open(`Suppression de ${this.selectedDocuments.length} documents`, 'Fermer', { duration: 3000 });
    }
  }
  
  // ==================== UTILITAIRES ====================
  
  formatFileSize(bytes: number | undefined): string {
    if (!bytes) return '0 Bytes';
    return this.documentService.formatFileSize(bytes);
  }
  
  getDocumentTypeIcon(documentType: DocumentType): string {
    return this.documentService.getDocumentTypeIcon(documentType);
  }
  
  getStatusColor(status: DocumentStatus): string {
    return this.documentService.getStatusColor(status);
  }
  
  getSecurityLevelColor(securityLevel: SecurityLevel): string {
    return this.documentService.getSecurityLevelColor(securityLevel);
  }
  
  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('fr-FR');
  }
}
