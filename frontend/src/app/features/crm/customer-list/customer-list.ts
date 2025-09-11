import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
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

@Component({
  selector: 'app-customer-list',
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
  templateUrl: './customer-list.html',
  styleUrl: './customer-list.scss'
})
export class CustomerList implements OnInit, OnDestroy {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  currentUser: User | null = null;
  isLoading = false;
  private destroy$ = new Subject<void>();

  // Données
  customers: CrmCustomer[] = [];
  filteredCustomers: MatTableDataSource<CrmCustomer> = new MatTableDataSource<CrmCustomer>();
  
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
  selectedCustomers: Set<number> = new Set();
  selectAll = false;

  // Pagination et tri
  pageSize = 25;
  pageSizeOptions = [10, 25, 50, 100];

  // Segments de clients disponibles
  customerSegments = [
    { value: 'PREMIUM', label: 'Premium', color: 'purple' },
    { value: 'GOLD', label: 'Or', color: 'gold' },
    { value: 'SILVER', label: 'Argent', color: 'silver' },
    { value: 'BRONZE', label: 'Bronze', color: 'brown' },
    { value: 'NEW', label: 'Nouveau', color: 'blue' },
    { value: 'AT_RISK', label: 'À risque', color: 'orange' },
    { value: 'CHURNED', label: 'Perdu', color: 'red' }
  ];

  // Statuts de lead disponibles
  leadStatuses = [
    { value: 'NEW', label: 'Nouveau', color: 'blue' },
    { value: 'CONTACTED', label: 'Contacté', color: 'orange' },
    { value: 'QUALIFIED', label: 'Qualifié', color: 'green' },
    { value: 'PROPOSAL', label: 'Proposition', color: 'purple' },
    { value: 'NEGOTIATION', label: 'Négociation', color: 'amber' },
    { value: 'CLOSED_WON', label: 'Gagné', color: 'green' },
    { value: 'CLOSED_LOST', label: 'Perdu', color: 'red' }
  ];

  // Sources disponibles
  sources = [
    'Site web', 'Réseaux sociaux', 'Recommandation', 'Publicité', 'Email', 'Téléphone', 'Salon', 'Autre'
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
      customerSegment: [''],
      leadStatus: [''],
      source: ['']
    });
  }

  ngOnInit(): void {
    this.loadCustomers();
    this.setupFilterSubscription();
    this.setupUserSubscription();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadCustomers(): void {
    this.isLoading = true;
    this.loadingService.show();

    this.crmCustomerService.getAllCrmCustomers()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (customers: CrmCustomer[]) => {
          this.customers = customers;
          this.filteredCustomers.data = this.customers;
          this.filteredCustomers.paginator = this.paginator;
          this.filteredCustomers.sort = this.sort;
          
          this.isLoading = false;
          this.loadingService.hide();
          
          this.notificationService.showSuccess(`${this.customers.length} clients chargés`);
        },
        error: (error: any) => {
          console.error('Erreur lors du chargement des clients:', error);
          this.isLoading = false;
          this.loadingService.hide();
          this.notificationService.showError('Erreur lors du chargement des clients');
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
    let filtered = [...this.customers];

    if (filters.search) {
      const searchTerm = filters.search.toLowerCase();
      filtered = filtered.filter(customer =>
        customer.thirdParty?.name?.toLowerCase().includes(searchTerm) ||
        customer.thirdParty?.email?.toLowerCase().includes(searchTerm) ||
        customer.thirdParty?.phone?.toLowerCase().includes(searchTerm) ||
        customer.source?.toLowerCase().includes(searchTerm) ||
        customer.leadStatus?.toLowerCase().includes(searchTerm) ||
        customer.assignedUser?.username?.toLowerCase().includes(searchTerm)
      );
    }

    if (filters.customerSegment) {
      filtered = filtered.filter(customer => customer.customerSegment === filters.customerSegment);
    }

    if (filters.leadStatus) {
      filtered = filtered.filter(customer => customer.leadStatus === filters.leadStatus);
    }

    if (filters.source) {
      filtered = filtered.filter(customer => customer.source === filters.source);
    }

    this.filteredCustomers.data = filtered;
  }

  // Sélection
  toggleSelectAll(): void {
    if (this.selectAll) {
      this.selectedCustomers.clear();
    } else {
      this.filteredCustomers.data.forEach(customer => {
        if (customer.id) {
          this.selectedCustomers.add(customer.id);
        }
      });
    }
    this.selectAll = !this.selectAll;
  }

  toggleCustomerSelection(customerId: number | undefined): void {
    if (!customerId) return;
    
    if (this.selectedCustomers.has(customerId)) {
      this.selectedCustomers.delete(customerId);
    } else {
      this.selectedCustomers.add(customerId);
    }
    this.updateSelectAllState();
  }

  private updateSelectAllState(): void {
    const totalCustomers = this.filteredCustomers.data.length;
    const selectedCount = this.selectedCustomers.size;
    this.selectAll = totalCustomers > 0 && selectedCount === totalCustomers;
  }

  // Actions
  addCustomer(): void {
    this.router.navigate(['/crm/customers/new']);
  }

  editCustomer(customer: CrmCustomer): void {
    this.router.navigate(['/crm/customers/edit', customer.id]);
  }

  viewCustomer(customer: CrmCustomer): void {
    this.router.navigate(['/crm/customers/view', customer.id]);
  }

  deleteCustomer(customer: CrmCustomer): void {
    this.notificationService.showInfo(`Suppression de ${customer.thirdParty?.name}`);
  }

  deleteSelectedCustomers(): void {
    if (this.selectedCustomers.size === 0) {
      this.notificationService.showWarning('Aucun client sélectionné');
      return;
    }
    this.notificationService.showInfo(`${this.selectedCustomers.size} clients sélectionnés pour suppression`);
  }

  exportCustomers(): void {
    this.notificationService.showInfo('Export des clients en cours...');
  }

  clearFilters(): void {
    this.filterForm.reset();
    this.applyFilters();
  }

  toggleFilterPanel(): void {
    this.filterExpanded = !this.filterExpanded;
  }

  // Méthodes utilitaires
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

  getStatusIcon(status: string): string {
    switch (status) {
      case 'NEW': return 'fiber_new';
      case 'CONTACTED': return 'phone';
      case 'QUALIFIED': return 'check_circle';
      case 'PROPOSAL': return 'description';
      case 'NEGOTIATION': return 'handshake';
      case 'CLOSED_WON': return 'check_circle';
      case 'CLOSED_LOST': return 'cancel';
      default: return 'help';
    }
  }

  getStatusLabel(status: string): string {
    switch (status) {
      case 'NEW': return 'Nouveau';
      case 'CONTACTED': return 'Contacté';
      case 'QUALIFIED': return 'Qualifié';
      case 'PROPOSAL': return 'Proposition';
      case 'NEGOTIATION': return 'Négociation';
      case 'CLOSED_WON': return 'Gagné';
      case 'CLOSED_LOST': return 'Perdu';
      default: return status;
    }
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR'
    }).format(amount);
  }

  formatDate(date: string): string {
    return new Intl.DateTimeFormat('fr-FR').format(new Date(date));
  }

  formatPercentage(value: number): string {
    return `${value}%`;
  }
}
