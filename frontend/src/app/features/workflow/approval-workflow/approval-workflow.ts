import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule, NgIf } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatTabsModule } from '@angular/material/tabs';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatChipsModule } from '@angular/material/chips';
import { MatStepperModule } from '@angular/material/stepper';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableDataSource } from '@angular/material/table';
import { Subject, takeUntil } from 'rxjs';

import { AuthService } from '../../../shared/services/auth';
import { NotificationService } from '../../../shared/services/notification';
import { LoadingService } from '../../../shared/services/loading';
import { User } from '../../../shared/models/user';

interface ApprovalRequest {
  id: string;
  title: string;
  description: string;
  type: 'expense' | 'purchase' | 'payment' | 'contract' | 'budget' | 'other';
  amount?: number;
  requester: string;
  requestDate: Date;
  status: 'pending' | 'approved' | 'rejected' | 'cancelled';
  priority: 'low' | 'medium' | 'high' | 'urgent';
  approvers: Approver[];
  currentStep: number;
  totalSteps: number;
  attachments: string[];
  comments: Comment[];
}

interface Approver {
  id: string;
  name: string;
  role: string;
  status: 'pending' | 'approved' | 'rejected';
  approvalDate?: Date;
  comments?: string;
  order: number;
}

interface Comment {
  id: string;
  author: string;
  content: string;
  date: Date;
  type: 'comment' | 'approval' | 'rejection';
}

interface WorkflowTemplate {
  id: string;
  name: string;
  description: string;
  type: string;
  steps: WorkflowStep[];
  isActive: boolean;
}

interface WorkflowStep {
  id: string;
  name: string;
  approverRole: string;
  isRequired: boolean;
  order: number;
  conditions?: string[];
}

@Component({
  selector: 'app-approval-workflow',
  standalone: true,
  imports: [
    CommonModule,
    NgIf,
    ReactiveFormsModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatCheckboxModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatTabsModule,
    MatDividerModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatTooltipModule,
    MatChipsModule,
    MatStepperModule,
    MatExpansionModule,
    MatProgressBarModule
  ],
  templateUrl: './approval-workflow.html',
  styleUrl: './approval-workflow.scss'
})
export class ApprovalWorkflow implements OnInit, OnDestroy {
  currentUser: User | null = null;
  isLoading = false;
  private destroy$ = new Subject<void>();

  // Formulaires
  requestForm: FormGroup;
  templateForm: FormGroup;
  commentForm: FormGroup;

  // Données
  approvalRequests: ApprovalRequest[] = [];
  workflowTemplates: WorkflowTemplate[] = [];
  selectedRequest: ApprovalRequest | null = null;
  
  requestsDataSource = new MatTableDataSource<ApprovalRequest>();
  templatesDataSource = new MatTableDataSource<WorkflowTemplate>();

  // Colonnes des tableaux
  requestsColumns: string[] = [
    'title', 'type', 'requester', 'amount', 'status', 'priority', 'requestDate', 'actions'
  ];
  templatesColumns: string[] = [
    'name', 'type', 'steps', 'isActive', 'actions'
  ];

  // Types de demandes
  requestTypes = [
    { value: 'expense', label: 'Dépense' },
    { value: 'purchase', label: 'Achat' },
    { value: 'payment', label: 'Paiement' },
    { value: 'contract', label: 'Contrat' },
    { value: 'budget', label: 'Budget' },
    { value: 'other', label: 'Autre' }
  ];

  // Priorités
  priorities = [
    { value: 'low', label: 'Faible' },
    { value: 'medium', label: 'Moyenne' },
    { value: 'high', label: 'Élevée' },
    { value: 'urgent', label: 'Urgente' }
  ];

  // Rôles d'approbateurs
  approverRoles = [
    'Manager',
    'Directeur',
    'Comptable',
    'RH',
    'CEO',
    'CFO',
    'Administrateur'
  ];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private notificationService: NotificationService,
    private loadingService: LoadingService
  ) {
    this.requestForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      type: ['', Validators.required],
      amount: [0],
      priority: ['medium', Validators.required],
      template: ['', Validators.required]
    });

    this.templateForm = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      type: ['', Validators.required],
      isActive: [true]
    });

    this.commentForm = this.fb.group({
      content: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.setupUserSubscription();
    this.loadWorkflowData();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private setupUserSubscription(): void {
    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        this.currentUser = user;
      });
  }

  private loadWorkflowData(): void {
    this.isLoading = true;
    this.loadingService.show();

    // Simulation de chargement des données
    setTimeout(() => {
      this.initializeApprovalRequests();
      this.initializeWorkflowTemplates();
      this.isLoading = false;
      this.loadingService.hide();
    }, 2000);
  }

  private initializeApprovalRequests(): void {
    this.approvalRequests = [
      {
        id: '1',
        title: 'Achat de matériel informatique',
        description: 'Achat de 5 ordinateurs portables pour l\'équipe de développement',
        type: 'purchase',
        amount: 7500,
        requester: 'Jean Dupont',
        requestDate: new Date('2024-10-15'),
        status: 'pending',
        priority: 'medium',
        approvers: [
          {
            id: '1',
            name: 'Marie Martin',
            role: 'Manager',
            status: 'approved',
            approvalDate: new Date('2024-10-16'),
            order: 1
          },
          {
            id: '2',
            name: 'Pierre Durand',
            role: 'Directeur',
            status: 'pending',
            order: 2
          }
        ],
        currentStep: 2,
        totalSteps: 3,
        attachments: ['devis.pdf', 'specifications.docx'],
        comments: [
          {
            id: '1',
            author: 'Marie Martin',
            content: 'Demande approuvée, matériel nécessaire pour l\'équipe',
            date: new Date('2024-10-16'),
            type: 'approval'
          }
        ]
      },
      {
        id: '2',
        title: 'Remboursement de frais de déplacement',
        description: 'Frais de déplacement pour la conférence TechSummit 2024',
        type: 'expense',
        amount: 1200,
        requester: 'Sophie Leroy',
        requestDate: new Date('2024-10-20'),
        status: 'approved',
        priority: 'low',
        approvers: [
          {
            id: '1',
            name: 'Marie Martin',
            role: 'Manager',
            status: 'approved',
            approvalDate: new Date('2024-10-21'),
            order: 1
          }
        ],
        currentStep: 1,
        totalSteps: 1,
        attachments: ['factures.pdf'],
        comments: []
      }
    ];
    this.requestsDataSource.data = this.approvalRequests;
  }

  private initializeWorkflowTemplates(): void {
    this.workflowTemplates = [
      {
        id: '1',
        name: 'Achat standard',
        description: 'Workflow pour les achats de moins de 1000€',
        type: 'purchase',
        steps: [
          {
            id: '1',
            name: 'Validation Manager',
            approverRole: 'Manager',
            isRequired: true,
            order: 1
          }
        ],
        isActive: true
      },
      {
        id: '2',
        name: 'Achat important',
        description: 'Workflow pour les achats de plus de 1000€',
        type: 'purchase',
        steps: [
          {
            id: '1',
            name: 'Validation Manager',
            approverRole: 'Manager',
            isRequired: true,
            order: 1
          },
          {
            id: '2',
            name: 'Validation Directeur',
            approverRole: 'Directeur',
            isRequired: true,
            order: 2
          },
          {
            id: '3',
            name: 'Validation Comptable',
            approverRole: 'Comptable',
            isRequired: true,
            order: 3
          }
        ],
        isActive: true
      }
    ];
    this.templatesDataSource.data = this.workflowTemplates;
  }

  // Gestion des demandes d'approbation
  createRequest(): void {
    if (this.requestForm.invalid) {
      this.notificationService.showWarning('Veuillez remplir tous les champs obligatoires');
      return;
    }

    const requestData = this.requestForm.value;
    const template = this.workflowTemplates.find(t => t.id === requestData.template);
    
    if (!template) {
      this.notificationService.showError('Template de workflow introuvable');
      return;
    }

    const newRequest: ApprovalRequest = {
      id: Date.now().toString(),
      ...requestData,
      requester: this.currentUser?.username || 'Utilisateur',
      requestDate: new Date(),
      status: 'pending',
      approvers: template.steps.map(step => ({
        id: step.id,
        name: `Approbateur ${step.order}`,
        role: step.approverRole,
        status: 'pending',
        order: step.order
      })),
      currentStep: 1,
      totalSteps: template.steps.length,
      attachments: [],
      comments: []
    };

    this.approvalRequests.push(newRequest);
    this.requestsDataSource.data = this.approvalRequests;
    this.requestForm.reset();
    this.requestForm.patchValue({ priority: 'medium' });
    this.notificationService.showSuccess('Demande d\'approbation créée avec succès');
  }

  approveRequest(request: ApprovalRequest): void {
    const currentApprover = request.approvers.find(a => a.order === request.currentStep);
    if (currentApprover) {
      currentApprover.status = 'approved';
      currentApprover.approvalDate = new Date();
      
      if (request.currentStep < request.totalSteps) {
        request.currentStep++;
      } else {
        request.status = 'approved';
      }
      
      this.requestsDataSource.data = this.approvalRequests;
      this.notificationService.showSuccess('Demande approuvée');
    }
  }

  rejectRequest(request: ApprovalRequest): void {
    const currentApprover = request.approvers.find(a => a.order === request.currentStep);
    if (currentApprover) {
      currentApprover.status = 'rejected';
      currentApprover.approvalDate = new Date();
      request.status = 'rejected';
      
      this.requestsDataSource.data = this.approvalRequests;
      this.notificationService.showSuccess('Demande rejetée');
    }
  }

  cancelRequest(request: ApprovalRequest): void {
    request.status = 'cancelled';
    this.requestsDataSource.data = this.approvalRequests;
    this.notificationService.showSuccess('Demande annulée');
  }

  // Gestion des templates
  createTemplate(): void {
    if (this.templateForm.invalid) {
      this.notificationService.showWarning('Veuillez remplir tous les champs obligatoires');
      return;
    }

    const templateData = this.templateForm.value;
    const newTemplate: WorkflowTemplate = {
      id: Date.now().toString(),
      ...templateData,
      steps: []
    };

    this.workflowTemplates.push(newTemplate);
    this.templatesDataSource.data = this.workflowTemplates;
    this.templateForm.reset();
    this.templateForm.patchValue({ isActive: true });
    this.notificationService.showSuccess('Template de workflow créé');
  }

  addStepToTemplate(template: WorkflowTemplate): void {
    const newStep: WorkflowStep = {
      id: Date.now().toString(),
      name: `Étape ${template.steps.length + 1}`,
      approverRole: 'Manager',
      isRequired: true,
      order: template.steps.length + 1
    };
    
    template.steps.push(newStep);
    this.templatesDataSource.data = this.workflowTemplates;
  }

  removeStepFromTemplate(template: WorkflowTemplate, stepId: string): void {
    template.steps = template.steps.filter(s => s.id !== stepId);
    // Réorganiser les ordres
    template.steps.forEach((step, index) => {
      step.order = index + 1;
    });
    this.templatesDataSource.data = this.workflowTemplates;
  }

  // Gestion des commentaires
  addComment(request: ApprovalRequest): void {
    if (this.commentForm.invalid) {
      this.notificationService.showWarning('Veuillez saisir un commentaire');
      return;
    }

    const commentData = this.commentForm.value;
    const newComment: Comment = {
      id: Date.now().toString(),
      author: this.currentUser?.username || 'Utilisateur',
      content: commentData.content,
      date: new Date(),
      type: 'comment'
    };

    request.comments.push(newComment);
    this.commentForm.reset();
    this.notificationService.showSuccess('Commentaire ajouté');
  }

  // Actions
  viewRequestDetails(request: ApprovalRequest): void {
    this.selectedRequest = request;
  }

  exportRequests(): void {
    this.notificationService.showInfo('Export des demandes en cours...');
  }

  // Méthodes utilitaires
  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR'
    }).format(amount);
  }

  formatDate(date: Date): string {
    return new Intl.DateTimeFormat('fr-FR').format(date);
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'pending':
        return '#ff9800';
      case 'approved':
        return '#4caf50';
      case 'rejected':
        return '#f44336';
      case 'cancelled':
        return '#666';
      default:
        return '#666';
    }
  }

  getStatusLabel(status: string): string {
    switch (status) {
      case 'pending':
        return 'En attente';
      case 'approved':
        return 'Approuvé';
      case 'rejected':
        return 'Rejeté';
      case 'cancelled':
        return 'Annulé';
      default:
        return 'Inconnu';
    }
  }

  getPriorityColor(priority: string): string {
    switch (priority) {
      case 'low':
        return '#4caf50';
      case 'medium':
        return '#ff9800';
      case 'high':
        return '#f44336';
      case 'urgent':
        return '#9c27b0';
      default:
        return '#666';
    }
  }

  getPriorityLabel(priority: string): string {
    const priorityObj = this.priorities.find(p => p.value === priority);
    return priorityObj?.label || priority;
  }

  getTypeLabel(type: string): string {
    const typeObj = this.requestTypes.find(t => t.value === type);
    return typeObj?.label || type;
  }

  getProgressPercentage(request: ApprovalRequest): number {
    return (request.currentStep / request.totalSteps) * 100;
  }

  canApprove(request: ApprovalRequest): boolean {
    if (!this.currentUser) return false;
    const currentApprover = request.approvers.find(a => a.order === request.currentStep);
    return currentApprover?.role === 'Manager' || currentApprover?.role === 'Directeur';
  }

  getApprovalProgress(request: ApprovalRequest): string {
    return `${request.currentStep}/${request.totalSteps}`;
  }
}
