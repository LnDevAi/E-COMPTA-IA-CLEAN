import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
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
import { MatStepperModule } from '@angular/material/stepper';
import { MatCardModule } from '@angular/material/card';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatTabsModule } from '@angular/material/tabs';
import { MatBadgeModule } from '@angular/material/badge';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { Subject, takeUntil } from 'rxjs';

import { DocumentManagementService, GedDocument, DocumentType } from '../../../shared/services/document-management.service';
import { NgIf, NgFor } from '@angular/common';

interface WorkflowStep {
  id: number;
  name: string;
  description: string;
  approverRole: string;
  approverId?: number;
  approverName?: string;
  isRequired: boolean;
  order: number;
  estimatedDays: number;
  isCompleted: boolean;
  completedAt?: string;
  completedBy?: number;
  comments?: string;
  status: 'pending' | 'in_progress' | 'completed' | 'rejected' | 'skipped';
}

interface Workflow {
  id: number;
  name: string;
  description: string;
  documentType: DocumentType;
  isActive: boolean;
  steps: WorkflowStep[];
  createdAt: string;
  createdBy: number;
  companyId: number;
  countryCode: string;
  accountingStandard: string;
}

interface WorkflowInstance {
  id: number;
  workflowId: number;
  documentId: number;
  document: GedDocument;
  currentStep: number;
  status: 'pending' | 'in_progress' | 'completed' | 'rejected' | 'cancelled';
  startedAt: string;
  startedBy: number;
  completedAt?: string;
  completedBy?: number;
  steps: WorkflowStep[];
  totalSteps: number;
  completedSteps: number;
  progress: number;
}

interface WorkflowFilter {
  status?: string;
  documentType?: DocumentType;
  approverId?: number;
  dateFrom?: string;
  dateTo?: string;
  searchTerm?: string;
}

@Component({
  selector: 'app-workflow-manager',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
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
    MatStepperModule,
    MatCardModule,
    MatExpansionModule,
    MatTabsModule,
    MatBadgeModule,
    MatDatepickerModule,
    MatNativeDateModule,
    NgIf,
    NgFor
  ],
  templateUrl: './workflow-manager.html',
  styleUrls: ['./workflow-manager.scss']
})
export class WorkflowManagerComponent implements OnInit, OnDestroy {
  // Données
  workflows: Workflow[] = [];
  workflowInstances: WorkflowInstance[] = [];
  filteredInstances: WorkflowInstance[] = [];
  selectedInstances: WorkflowInstance[] = [];
  
  // États
  loading = false;
  error: string | null = null;
  activeTab = 0;
  
  // Pagination et tri
  totalItems = 0;
  pageSize = 25;
  pageIndex = 0;
  sortField = 'startedAt';
  sortDirection: 'asc' | 'desc' = 'desc';
  
  // Filtres
  filter: WorkflowFilter = {};
  
  // Colonnes du tableau
  displayedColumns: string[] = [
    'select',
    'document',
    'workflow',
    'currentStep',
    'progress',
    'status',
    'startedAt',
    'startedBy',
    'actions'
  ];
  
  // Options pour les filtres
  documentTypes = Object.values(DocumentType);
  workflowStatuses = ['pending', 'in_progress', 'completed', 'rejected', 'cancelled'];
  
  // Subject pour la destruction
  private destroy$ = new Subject<void>();
  
  constructor(
    public documentService: DocumentManagementService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
    private fb: FormBuilder
  ) {}
  
  ngOnInit(): void {
    this.loadWorkflows();
    this.loadWorkflowInstances();
  }
  
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
  
  // ==================== CHARGEMENT DES DONNÉES ====================
  
  private loadWorkflows(): void {
    this.loading = true;
    this.error = null;
    
    // Récupérer les workflows depuis l'API
    this.documentService.getWorkflows(1) // TODO: Récupérer l'ID de l'entreprise depuis le contexte
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (workflows) => {
          this.workflows = workflows;
          this.loading = false;
        },
        error: (error) => {
          this.error = 'Erreur lors du chargement des workflows: ' + error.message;
          this.loading = false;
        }
      });
  }
  
  loadWorkflowInstances(): void {
    this.loading = true;
    this.error = null;
    
    // Récupérer les documents en attente d'approbation depuis l'API
    this.documentService.getDocumentsPendingApproval(1) // TODO: Récupérer l'ID de l'entreprise depuis le contexte
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (documents) => {
          // Transformer les données de l'API en instances de workflow
          this.workflowInstances = documents.map(doc => ({
            id: doc.documentId,
            workflowId: 1, // TODO: Récupérer le workflow ID depuis l'API
            documentId: doc.documentId,
            document: {
              id: doc.documentId,
              documentCode: doc.documentCode,
              title: doc.documentTitle,
              fileName: doc.fileName || 'document.pdf',
              filePath: doc.filePath || '/documents/',
              documentType: doc.documentType,
              status: 'PENDING_APPROVAL' as any,
              securityLevel: 'INTERNAL' as any,
              version: 1,
              isCurrentVersion: true,
              isArchived: false,
              companyId: 1,
              countryCode: 'SN',
              accountingStandard: 'SYCEBNL',
              createdAt: doc.createdAt || new Date().toISOString(),
              isEncrypted: false
            },
            currentStep: doc.approvalLevel || 1,
            status: 'in_progress',
            startedAt: doc.createdAt || new Date().toISOString(),
            startedBy: 1,
            steps: [], // TODO: Récupérer les étapes depuis l'API
            totalSteps: 3,
            completedSteps: 0,
            progress: 0
          }));
          
          this.applyFilters();
          this.loading = false;
        },
        error: (error) => {
          this.error = 'Erreur lors du chargement des instances de workflow: ' + error.message;
          this.loading = false;
        }
      });
  }
  
  // ==================== FILTRES ET RECHERCHE ====================
  
  applyFilters(): void {
    let filtered = [...this.workflowInstances];
    
    // Recherche textuelle
    if (this.filter.searchTerm) {
      const term = this.filter.searchTerm.toLowerCase();
      filtered = filtered.filter(instance => 
        instance.document.title.toLowerCase().includes(term) ||
        instance.document.documentCode.toLowerCase().includes(term) ||
        this.getWorkflowName(instance.workflowId).toLowerCase().includes(term)
      );
    }
    
    // Filtres par propriétés
    if (this.filter.status) {
      filtered = filtered.filter(instance => instance.status === this.filter.status);
    }
    
    if (this.filter.documentType) {
      filtered = filtered.filter(instance => instance.document.documentType === this.filter.documentType);
    }
    
    if (this.filter.approverId) {
      filtered = filtered.filter(instance => 
        instance.steps.some(step => step.approverId === this.filter.approverId)
      );
    }
    
    this.filteredInstances = filtered;
    this.totalItems = filtered.length;
    this.pageIndex = 0; // Reset à la première page
  }
  
  clearFilters(): void {
    this.filter = {};
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
    this.filteredInstances.sort((a, b) => {
      const aValue = this.getFieldValue(a, this.sortField);
      const bValue = this.getFieldValue(b, this.sortField);
      
      if (aValue < bValue) return this.sortDirection === 'asc' ? -1 : 1;
      if (aValue > bValue) return this.sortDirection === 'asc' ? 1 : -1;
      return 0;
    });
  }
  
  private getFieldValue(instance: WorkflowInstance, field: string): any {
    switch (field) {
      case 'document': return instance.document.title;
      case 'workflow': return this.getWorkflowName(instance.workflowId);
      case 'currentStep': return instance.currentStep;
      case 'progress': return instance.progress;
      case 'status': return instance.status;
      case 'startedAt': return new Date(instance.startedAt);
      case 'startedBy': return instance.startedBy;
      default: return '';
    }
  }
  
  // ==================== SÉLECTION ====================
  
  isAllSelected(): boolean {
    return this.selectedInstances.length === this.getCurrentPageInstances().length;
  }
  
  isIndeterminate(): boolean {
    return this.selectedInstances.length > 0 && !this.isAllSelected();
  }
  
  masterToggle(): void {
    if (this.isAllSelected()) {
      this.selectedInstances = [];
    } else {
      this.selectedInstances = [...this.getCurrentPageInstances()];
    }
  }
  
  toggleSelection(instance: WorkflowInstance): void {
    const index = this.selectedInstances.findIndex(i => i.id === instance.id);
    if (index > -1) {
      this.selectedInstances.splice(index, 1);
    } else {
      this.selectedInstances.push(instance);
    }
  }
  
  isSelected(instance: WorkflowInstance): boolean {
    return this.selectedInstances.some(i => i.id === instance.id);
  }
  
  getCurrentPageInstances(): WorkflowInstance[] {
    const start = this.pageIndex * this.pageSize;
    const end = start + this.pageSize;
    return this.filteredInstances.slice(start, end);
  }
  
  // ==================== ACTIONS ====================
  
  viewWorkflow(instance: WorkflowInstance): void {
    // TODO: Ouvrir le visualiseur de workflow
    this.snackBar.open(`Visualisation du workflow ${instance.id}`, 'Fermer', { duration: 3000 });
  }
  
  approveStep(instance: WorkflowInstance, step: WorkflowStep): void {
    // Récupérer les approbations du document
    this.documentService.getDocumentApprovals(instance.documentId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (approvals) => {
          const approval = approvals.find(a => a.approvalLevel === step.order);
          if (approval) {
            this.documentService.approveDocument(approval.id, 'Approuvé', 1) // TODO: Récupérer l'ID de l'utilisateur
              .pipe(takeUntil(this.destroy$))
              .subscribe({
                next: () => {
                  this.snackBar.open(`Étape ${step.name} approuvée avec succès`, 'Fermer', { duration: 3000 });
                  this.loadWorkflowInstances(); // Recharger les données
                },
                error: (error) => {
                  this.snackBar.open(`Erreur lors de l'approbation: ${error.message}`, 'Fermer', { duration: 5000 });
                }
              });
          }
        },
        error: (error) => {
          this.snackBar.open(`Erreur lors de la récupération des approbations: ${error.message}`, 'Fermer', { duration: 5000 });
        }
      });
  }
  
  rejectStep(instance: WorkflowInstance, step: WorkflowStep): void {
    // Récupérer les approbations du document
    this.documentService.getDocumentApprovals(instance.documentId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (approvals) => {
          const approval = approvals.find(a => a.approvalLevel === step.order);
          if (approval) {
            this.documentService.rejectDocument(approval.id, 'Rejeté', 1) // TODO: Récupérer l'ID de l'utilisateur
              .pipe(takeUntil(this.destroy$))
              .subscribe({
                next: () => {
                  this.snackBar.open(`Étape ${step.name} rejetée`, 'Fermer', { duration: 3000 });
                  this.loadWorkflowInstances(); // Recharger les données
                },
                error: (error) => {
                  this.snackBar.open(`Erreur lors du rejet: ${error.message}`, 'Fermer', { duration: 5000 });
                }
              });
          }
        },
        error: (error) => {
          this.snackBar.open(`Erreur lors de la récupération des approbations: ${error.message}`, 'Fermer', { duration: 5000 });
        }
      });
  }
  
  cancelWorkflow(instance: WorkflowInstance): void {
    if (confirm(`Êtes-vous sûr de vouloir annuler le workflow "${this.getWorkflowName(instance.workflowId)}" ?`)) {
      // TODO: Implémenter l'annulation
      this.snackBar.open('Workflow annulé', 'Fermer', { duration: 3000 });
    }
  }
  
  // ==================== UTILITAIRES ====================
  
  getWorkflowName(workflowId: number): string {
    const workflow = this.workflows.find(w => w.id === workflowId);
    return workflow ? workflow.name : 'Workflow inconnu';
  }
  
  getStatusColor(status: string): string {
    const colors: { [key: string]: string } = {
      'pending': 'grey',
      'in_progress': 'primary',
      'completed': 'green',
      'rejected': 'warn',
      'cancelled': 'grey'
    };
    return colors[status] || 'grey';
  }
  
  getStepStatusColor(status: string): string {
    const colors: { [key: string]: string } = {
      'pending': 'grey',
      'in_progress': 'primary',
      'completed': 'green',
      'rejected': 'warn',
      'skipped': 'grey'
    };
    return colors[status] || 'grey';
  }
  
  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('fr-FR');
  }
  
  formatProgress(progress: number): string {
    return `${progress}%`;
  }
  
  // ==================== ACTIONS SUPPLÉMENTAIRES ====================
  
  createWorkflow(): void {
    // TODO: Ouvrir le dialog de création de workflow
    this.snackBar.open('Création de workflow en cours de développement', 'Fermer', { duration: 3000 });
  }
  
  editWorkflow(workflow: Workflow): void {
    // TODO: Ouvrir le dialog d'édition de workflow
    this.snackBar.open(`Édition du workflow ${workflow.name}`, 'Fermer', { duration: 3000 });
  }
  
  duplicateWorkflow(workflow: Workflow): void {
    // TODO: Implémenter la duplication
    this.snackBar.open(`Duplication du workflow ${workflow.name}`, 'Fermer', { duration: 3000 });
  }
  
  deleteWorkflow(workflow: Workflow): void {
    if (confirm(`Êtes-vous sûr de vouloir supprimer le workflow "${workflow.name}" ?`)) {
      // TODO: Implémenter la suppression
      this.snackBar.open(`Suppression du workflow ${workflow.name}`, 'Fermer', { duration: 3000 });
    }
  }
  
  approveSelected(): void {
    if (this.selectedInstances.length === 0) {
      this.snackBar.open('Aucun workflow sélectionné', 'Fermer', { duration: 3000 });
      return;
    }
    
    // TODO: Implémenter l'approbation en lot
    this.snackBar.open(`Approbation de ${this.selectedInstances.length} workflow(s)`, 'Fermer', { duration: 3000 });
  }
  
  cancelSelected(): void {
    if (this.selectedInstances.length === 0) {
      this.snackBar.open('Aucun workflow sélectionné', 'Fermer', { duration: 3000 });
      return;
    }
    
    if (confirm(`Êtes-vous sûr de vouloir annuler ${this.selectedInstances.length} workflow(s) ?`)) {
      // TODO: Implémenter l'annulation en lot
      this.snackBar.open(`Annulation de ${this.selectedInstances.length} workflow(s)`, 'Fermer', { duration: 3000 });
    }
  }
  
  // ==================== UTILITAIRES SUPPLÉMENTAIRES ====================
  
  getTotalEstimatedDays(workflow: Workflow): number {
    return workflow.steps.reduce((total, step) => total + step.estimatedDays, 0);
  }
  
  getAverageProcessingTime(): number {
    // Récupérer les statistiques depuis l'API
    this.documentService.getWorkflowStatistics(1) // TODO: Récupérer l'ID de l'entreprise
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (stats) => {
          return stats.averageProcessingTime || 0;
        },
        error: (error) => {
          console.error('Erreur lors de la récupération des statistiques:', error);
          return 0;
        }
      });
    return 0; // Valeur par défaut
  }
  
  getCompletedWorkflows(): number {
    return this.workflowInstances.filter(i => i.status === 'completed').length;
  }
  
  getRejectedWorkflows(): number {
    return this.workflowInstances.filter(i => i.status === 'rejected').length;
  }
  
  // ==================== GETTERS ====================
  
  get canApprove(): boolean {
    return this.selectedInstances.length > 0 && 
           this.selectedInstances.every(i => i.status === 'in_progress');
  }
  
  get canCancel(): boolean {
    return this.selectedInstances.length > 0 && 
           this.selectedInstances.every(i => i.status !== 'completed' && i.status !== 'cancelled');
  }
}
