import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { CommonModule, NgIf, NgFor } from '@angular/common';
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

// Utilisation de CrmCustomer pour les prospects

@Component({
  selector: 'app-prospects',
  standalone: true,
  imports: [
    CommonModule,
    NgIf,
    NgFor,
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
  templateUrl: './prospects.html',
  styleUrl: './prospects.scss'
})
export class Prospects implements OnInit, OnDestroy {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  currentUser: User | null = null;
  isLoading = false;
  private destroy$ = new Subject<void>();

  // Données
  prospects: CrmCustomer[] = [];
  filteredProspects: MatTableDataSource<CrmCustomer> = new MatTableDataSource<CrmCustomer>();
  
  // Colonnes du tableau
  displayedColumns: string[] = [
    'select',
    'thirdParty',
    'customerSegment',
    'leadSource',
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
  selectedProspects: Set<number> = new Set();
  selectAll = false;

  // Pagination et tri
  pageSize = 25;
  pageSizeOptions = [10, 25, 50, 100];

  // Industries disponibles
  industries = [
    'Technologie', 'Finance', 'Santé', 'Éducation', 'Retail',
    'Manufacturing', 'Services', 'Immobilier', 'Transport', 'Autre'
  ];

  // Sources disponibles
  sources = [
    'Site web', 'Réseaux sociaux', 'Recommandation', 'Salon professionnel',
    'Publicité', 'Email marketing', 'Téléphone', 'LinkedIn', 'Google Ads', 'Autre'
  ];

  // Statuts disponibles (utilise les statuts de CrmCustomer)
  statuses = [
    { value: 'NEW', label: 'Nouveau', color: 'blue' },
    { value: 'CONTACTED', label: 'Contacté', color: 'orange' },
    { value: 'QUALIFIED', label: 'Qualifié', color: 'purple' },
    { value: 'PROPOSAL', label: 'Proposition', color: 'teal' },
    { value: 'NEGOTIATION', label: 'Négociation', color: 'amber' },
    { value: 'CLOSED_WON', label: 'Gagné', color: 'green' },
    { value: 'CLOSED_LOST', label: 'Perdu', color: 'red' }
  ];

  // Priorités disponibles (utilise les priorités de CrmCustomer)
  priorities = [
    { value: 'LOW', label: 'Faible', color: 'gray' },
    { value: 'MEDIUM', label: 'Moyenne', color: 'orange' },
    { value: 'HIGH', label: 'Élevée', color: 'red' }
  ];

  // Utilisateurs assignés
  assignedUsers = [
    'Jean Dupont', 'Marie Martin', 'Pierre Durand', 'Sophie Bernard', 'Lucas Moreau'
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
      status: [''],
      priority: [''],
      industry: [''],
      source: [''],
      assignedTo: [''],
      dateRange: this.fb.group({
        start: [null],
        end: [null]
      })
    });
  }

  ngOnInit(): void {
    this.loadProspects();
    this.setupFilterSubscription();
    this.setupUserSubscription();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadProspects(): void {
    this.isLoading = true;
    this.loadingService.show();

    this.crmCustomerService.getAllCrmCustomers()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (customers) => {
          // Filtrer pour ne garder que les prospects (pas encore clients)
          this.prospects = customers.filter(customer => 
            customer.leadStatus === 'NEW' || 
            customer.leadStatus === 'CONTACTED' || 
            customer.leadStatus === 'QUALIFIED' ||
            customer.leadStatus === 'PROPOSAL' ||
            customer.leadStatus === 'NEGOTIATION'
          );
          
          this.filteredProspects.data = this.prospects;
          this.filteredProspects.paginator = this.paginator;
          this.filteredProspects.sort = this.sort;
          
          this.isLoading = false;
          this.loadingService.hide();
          
          this.notificationService.showSuccess(`${this.prospects.length} prospects chargés`);
        },
        error: (error) => {
          console.error('Erreur lors du chargement des prospects:', error);
          this.isLoading = false;
          this.loadingService.hide();
          this.notificationService.showError('Erreur lors du chargement des prospects');
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
    let filtered = [...this.prospects];

    if (filters.search) {
      const searchTerm = filters.search.toLowerCase();
      filtered = filtered.filter(prospect =>
        prospect.thirdParty?.name?.toLowerCase().includes(searchTerm) ||
        prospect.thirdParty?.email?.toLowerCase().includes(searchTerm) ||
        prospect.thirdParty?.phone?.includes(searchTerm) ||
        prospect.source?.toLowerCase().includes(searchTerm)
      );
    }

    if (filters.status) {
      filtered = filtered.filter(prospect => prospect.leadStatus === filters.status);
    }

    if (filters.source) {
      filtered = filtered.filter(prospect => prospect.source === filters.source);
    }

    if (filters.assignedTo) {
      filtered = filtered.filter(prospect => prospect.assignedUser?.username === filters.assignedTo);
    }

    if (filters.dateRange.start) {
      filtered = filtered.filter(prospect => prospect.createdAt >= filters.dateRange.start);
    }
    if (filters.dateRange.end) {
      filtered = filtered.filter(prospect => prospect.createdAt <= filters.dateRange.end);
    }

    this.filteredProspects.data = filtered;
  }

  // Gestion de la sélection
  toggleSelectAll(): void {
    if (this.selectAll) {
      this.selectedProspects.clear();
    } else {
      this.filteredProspects.data.forEach(prospect => {
        this.selectedProspects.add(prospect.id);
      });
    }
    this.selectAll = !this.selectAll;
  }

  toggleProspectSelection(prospectId: number): void {
    if (this.selectedProspects.has(prospectId)) {
      this.selectedProspects.delete(prospectId);
    } else {
      this.selectedProspects.add(prospectId);
    }
    this.updateSelectAllState();
  }

  private updateSelectAllState(): void {
    const totalProspects = this.filteredProspects.data.length;
    const selectedCount = this.selectedProspects.size;
    this.selectAll = totalProspects > 0 && selectedCount === totalProspects;
  }

  // Actions
  addProspect(): void {
    this.router.navigate(['/crm/prospects/new']);
  }

  editProspect(prospect: CrmCustomer): void {
    this.router.navigate(['/crm/prospects/edit', prospect.id]);
  }

  viewProspect(prospect: CrmCustomer): void {
    this.router.navigate(['/crm/prospects/view', prospect.id]);
  }

  convertToClient(prospect: CrmCustomer): void {
    this.notificationService.showInfo(`Conversion de ${prospect.thirdParty?.name} en client`);
  }

  deleteProspect(prospect: CrmCustomer): void {
    this.notificationService.showInfo(`Suppression de ${prospect.thirdParty?.name}`);
  }

  deleteSelectedProspects(): void {
    if (this.selectedProspects.size === 0) {
      this.notificationService.showWarning('Aucun prospect sélectionné');
      return;
    }
    this.notificationService.showInfo(`${this.selectedProspects.size} prospects sélectionnés pour suppression`);
  }

  exportProspects(): void {
    this.notificationService.showInfo('Export des prospects en cours...');
  }

  clearFilters(): void {
    this.filterForm.reset();
    this.applyFilters();
  }

  toggleFilterPanel(): void {
    this.filterExpanded = !this.filterExpanded;
  }

  getStatusColor(status: string): string {
    const statusObj = this.statuses.find(s => s.value === status);
    return statusObj ? statusObj.color : 'gray';
  }

  getStatusIcon(status: string): string {
    switch (status) {
      case 'NEW': return 'fiber_new';
      case 'CONTACTED': return 'phone';
      case 'QUALIFIED': return 'verified';
      case 'PROPOSAL': return 'description';
      case 'NEGOTIATION': return 'handshake';
      case 'CLOSED_WON': return 'check_circle';
      case 'CLOSED_LOST': return 'cancel';
      default: return 'help';
    }
  }

  getPriorityColor(priority: string): string {
    const priorityObj = this.priorities.find(p => p.value === priority);
    return priorityObj ? priorityObj.color : 'gray';
  }

  getPriorityIcon(priority: string): string {
    switch (priority) {
      case 'LOW': return 'keyboard_arrow_down';
      case 'MEDIUM': return 'remove';
      case 'HIGH': return 'keyboard_arrow_up';
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
      case 'ENTERPRISE': return 'business';
      case 'SMB': return 'store';
      case 'STARTUP': return 'rocket_launch';
      case 'INDIVIDUAL': return 'person';
      default: return 'business';
    }
  }

  getSegmentLabel(segment: string): string {
    switch (segment) {
      case 'ENTERPRISE': return 'Entreprise';
      case 'SMB': return 'PME';
      case 'STARTUP': return 'Startup';
      case 'INDIVIDUAL': return 'Particulier';
      default: return segment;
    }
  }

  getStatusLabel(status: string): string {
    const statusObj = this.statuses.find(s => s.value === status);
    return statusObj ? statusObj.label : status;
  }
}