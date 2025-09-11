import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatMenuModule } from '@angular/material/menu';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDividerModule } from '@angular/material/divider';
import { MatBadgeModule } from '@angular/material/badge';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { Subject, takeUntil, debounceTime, distinctUntilChanged } from 'rxjs';

import { AuthService } from '../../../shared/services/auth';
import { NotificationService } from '../../../shared/services/notification';
import { LoadingService } from '../../../shared/services/loading';
import { CrmCustomerService, CrmCustomer } from '../../../shared/services/crm-customer.service';
import { User } from '../../../shared/models/user';

// Utilisation de CrmCustomer pour les opportunités

@Component({
  selector: 'app-opportunities',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatChipsModule,
    MatTooltipModule,
    MatMenuModule,
    MatDialogModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatCheckboxModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatDividerModule,
    MatBadgeModule,
    MatProgressBarModule
  ],
  templateUrl: './opportunities.html',
  styleUrl: './opportunities.scss'
})
export class Opportunities implements OnInit, OnDestroy {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  currentUser: User | null = null;
  isLoading = false;
  private destroy$ = new Subject<void>();

  // Données
  opportunities: CrmCustomer[] = [];
  filteredOpportunities: MatTableDataSource<CrmCustomer> = new MatTableDataSource<CrmCustomer>();
  
  // Colonnes du tableau
  displayedColumns: string[] = [
    'select',
    'thirdParty',
    'customerSegment',
    'source',
    'leadStatus',
    'totalRevenueGenerated',
    'leadScore',
    'assignedTo',
    'actions'
  ];

  // Filtres
  filterForm: FormGroup;
  filterExpanded = false;
  
  // Sélection
  selectedOpportunities: Set<number> = new Set();
  selectAll = false;

  // Pagination et tri
  pageSize = 25;
  pageSizeOptions = [10, 25, 50, 100];

  // Sources disponibles
  sources = [
    'Site web', 'Réseaux sociaux', 'Recommandation', 'Salon professionnel',
    'Publicité', 'Email marketing', 'Téléphone', 'LinkedIn', 'Google Ads', 'Autre'
  ];

  // Étapes disponibles (utilise les statuts de CrmCustomer)
  stages = [
    { value: 'QUALIFIED', label: 'Qualifiée', color: 'orange' },
    { value: 'PROPOSAL', label: 'Proposition', color: 'purple' },
    { value: 'NEGOTIATION', label: 'Négociation', color: 'amber' },
    { value: 'CLOSED_WON', label: 'Gagnée', color: 'green' },
    { value: 'CLOSED_LOST', label: 'Perdue', color: 'red' }
  ];

  // Priorités disponibles
  priorities = [
    { value: 'low', label: 'Faible', color: 'gray' },
    { value: 'medium', label: 'Moyenne', color: 'orange' },
    { value: 'high', label: 'Élevée', color: 'red' }
  ];

  // Utilisateurs assignés
  assignedUsers = [
    'Jean Dupont', 'Marie Martin', 'Pierre Durand', 'Sophie Bernard', 'Lucas Moreau'
  ];

  // Produits disponibles
  products = [
    'Logiciel ERP', 'Solution CRM', 'Service de conseil', 'Formation',
    'Support technique', 'Intégration', 'Maintenance', 'Autre'
  ];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private notificationService: NotificationService,
    private loadingService: LoadingService,
    private crmCustomerService: CrmCustomerService,
    private router: Router,
    private dialog: MatDialog
  ) {
    this.filterForm = this.fb.group({
      search: [''],
      stage: [''],
      priority: [''],
      source: [''],
      assignedTo: [''],
      dateRange: this.fb.group({
        start: [null],
        end: [null]
      })
    });
  }

  ngOnInit(): void {
    this.loadOpportunities();
    this.setupFilterSubscription();
    this.setupUserSubscription();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadOpportunities(): void {
    this.isLoading = true;
    this.loadingService.show();

    this.crmCustomerService.getAllCrmCustomers()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (customers) => {
          // Filtrer pour ne garder que les opportunités (clients qualifiés)
          this.opportunities = customers.filter(customer => 
            customer.leadStatus === 'QUALIFIED' || 
            customer.leadStatus === 'PROPOSAL' || 
            customer.leadStatus === 'NEGOTIATION' ||
            customer.leadStatus === 'CLOSED_WON'
          );
          
          this.filteredOpportunities.data = this.opportunities;
          this.filteredOpportunities.paginator = this.paginator;
          this.filteredOpportunities.sort = this.sort;
          
          this.isLoading = false;
          this.loadingService.hide();
          
          this.notificationService.showSuccess(`${this.opportunities.length} opportunités chargées`);
        },
        error: (error) => {
          console.error('Erreur lors du chargement des opportunités:', error);
          this.isLoading = false;
          this.loadingService.hide();
          this.notificationService.showError('Erreur lors du chargement des opportunités');
        }
      });
  }


  private setupFilterSubscription(): void {
    this.filterForm.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        takeUntil(this.destroy$)
      )
      .subscribe(() => {
        this.applyFilters();
      });
  }

  private setupUserSubscription(): void {
    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        this.currentUser = user;
      });
  }

  private applyFilters(): void {
    const filters = this.filterForm.value;
    let filtered = [...this.opportunities];

    if (filters.search) {
      const searchTerm = filters.search.toLowerCase();
      filtered = filtered.filter(opportunity =>
        opportunity.thirdParty?.name?.toLowerCase().includes(searchTerm) ||
        opportunity.thirdParty?.email?.toLowerCase().includes(searchTerm) ||
        opportunity.thirdParty?.phone?.includes(searchTerm) ||
        opportunity.source?.toLowerCase().includes(searchTerm)
      );
    }

    if (filters.stage) {
      filtered = filtered.filter(opportunity => opportunity.leadStatus === filters.stage);
    }

    if (filters.source) {
      filtered = filtered.filter(opportunity => opportunity.source === filters.source);
    }

    if (filters.assignedTo) {
      filtered = filtered.filter(opportunity => opportunity.assignedUser?.username === filters.assignedTo);
    }

    if (filters.dateRange.start) {
      filtered = filtered.filter(opportunity => opportunity.createdAt >= filters.dateRange.start);
    }
    if (filters.dateRange.end) {
      filtered = filtered.filter(opportunity => opportunity.createdAt <= filters.dateRange.end);
    }

    this.filteredOpportunities.data = filtered;
  }

  // Gestion de la sélection
  toggleSelectAll(): void {
    if (this.selectAll) {
      this.selectedOpportunities.clear();
    } else {
      this.filteredOpportunities.data.forEach(opportunity => {
        this.selectedOpportunities.add(opportunity.id);
      });
    }
    this.selectAll = !this.selectAll;
  }

  toggleOpportunitySelection(opportunityId: number): void {
    if (this.selectedOpportunities.has(opportunityId)) {
      this.selectedOpportunities.delete(opportunityId);
    } else {
      this.selectedOpportunities.add(opportunityId);
    }
    this.updateSelectAllState();
  }

  private updateSelectAllState(): void {
    const totalOpportunities = this.filteredOpportunities.data.length;
    const selectedCount = this.selectedOpportunities.size;
    this.selectAll = totalOpportunities > 0 && selectedCount === totalOpportunities;
  }

  // Actions
  addOpportunity(): void {
    this.router.navigate(['/crm/opportunities/new']);
  }

  editOpportunity(opportunity: CrmCustomer): void {
    this.router.navigate(['/crm/opportunities/edit', opportunity.id]);
  }

  viewOpportunity(opportunity: CrmCustomer): void {
    this.router.navigate(['/crm/opportunities/view', opportunity.id]);
  }

  closeOpportunity(opportunity: CrmCustomer): void {
    this.notificationService.showInfo(`Clôture de l'opportunité ${opportunity.thirdParty?.name}`);
  }

  deleteOpportunity(opportunity: CrmCustomer): void {
    this.notificationService.showInfo(`Suppression de ${opportunity.thirdParty?.name}`);
  }

  deleteSelectedOpportunities(): void {
    if (this.selectedOpportunities.size === 0) {
      this.notificationService.showWarning('Aucune opportunité sélectionnée');
      return;
    }
    this.notificationService.showInfo(`${this.selectedOpportunities.size} opportunités sélectionnées pour suppression`);
  }

  exportOpportunities(): void {
    this.notificationService.showInfo('Export des opportunités en cours...');
  }

  clearFilters(): void {
    this.filterForm.reset();
    this.applyFilters();
  }

  toggleFilterPanel(): void {
    this.filterExpanded = !this.filterExpanded;
  }

  getStageColor(stage: string): string {
    const stageObj = this.stages.find(s => s.value === stage);
    return stageObj ? stageObj.color : 'gray';
  }

  getStageIcon(stage: string): string {
    switch (stage) {
      case 'prospecting': return 'search';
      case 'qualification': return 'verified';
      case 'proposal': return 'description';
      case 'negotiation': return 'handshake';
      case 'closed-won': return 'check_circle';
      case 'closed-lost': return 'cancel';
      default: return 'help';
    }
  }

  getPriorityColor(priority: string): string {
    const priorityObj = this.priorities.find(p => p.value === priority);
    return priorityObj ? priorityObj.color : 'gray';
  }

  getPriorityIcon(priority: string): string {
    switch (priority) {
      case 'low': return 'keyboard_arrow_down';
      case 'medium': return 'remove';
      case 'high': return 'keyboard_arrow_up';
      default: return 'help';
    }
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR'
    }).format(amount);
  }

  formatDate(date: Date): string {
    return new Intl.DateTimeFormat('fr-FR').format(date);
  }

  formatPercentage(value: number): string {
    return `${value}%`;
  }

  // Méthodes utilitaires pour les segments et statuts
  getSegmentIcon(segment: string): string {
    switch (segment) {
      case 'PREMIUM': return 'diamond';
      case 'GOLD': return 'star';
      case 'SILVER': return 'star_half';
      case 'BRONZE': return 'star_border';
      case 'NEW': return 'fiber_new';
      case 'AT_RISK': return 'warning';
      case 'CHURNED': return 'cancel';
      default: return 'business';
    }
  }

  getSegmentLabel(segment: string): string {
    switch (segment) {
      case 'PREMIUM': return 'Premium';
      case 'GOLD': return 'Or';
      case 'SILVER': return 'Argent';
      case 'BRONZE': return 'Bronze';
      case 'NEW': return 'Nouveau';
      case 'AT_RISK': return 'À risque';
      case 'CHURNED': return 'Perdu';
      default: return segment;
    }
  }

  getStageLabel(stage: string): string {
    const stageObj = this.stages.find(s => s.value === stage);
    return stageObj ? stageObj.label : stage;
  }
}