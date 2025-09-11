import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { CommonModule, NgIf } from '@angular/common';
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
import { Subject, takeUntil, debounceTime, distinctUntilChanged } from 'rxjs';

import { AuthService } from '../../../shared/services/auth';
import { NotificationService } from '../../../shared/services/notification';
import { LoadingService } from '../../../shared/services/loading';
import { CrmCustomerService, CrmCustomer } from '../../../shared/services/crm-customer.service';
import { User } from '../../../shared/models/user';

@Component({
  selector: 'app-clients',
  standalone: true,
  imports: [
    CommonModule,
    NgIf,
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
    MatDividerModule
  ],
  templateUrl: './clients.html',
  styleUrl: './clients.scss'
})
export class ClientsComponent implements OnInit, OnDestroy {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  currentUser: User | null = null;
  isLoading = false;
  private destroy$ = new Subject<void>();

  // Données
  clients: CrmCustomer[] = [];
  filteredClients: MatTableDataSource<CrmCustomer> = new MatTableDataSource<CrmCustomer>();
  
  // Colonnes du tableau
  displayedColumns: string[] = [
    'select',
    'thirdParty',
    'customerSegment',
    'paymentBehavior',
    'totalRevenueGenerated',
    'totalOrdersCount',
    'lastOrderDate',
    'isActive',
    'actions'
  ];

  // Filtres
  filterForm: FormGroup;
  filterExpanded = false;
  
  // Sélection
  selectedClients: Set<number> = new Set();
  selectAll = false;

  // Pagination et tri
  pageSize = 25;
  pageSizeOptions = [10, 25, 50, 100];

  // Segments clients
  customerSegments = [
    { value: 'PREMIUM', label: 'Premium', color: 'purple' },
    { value: 'GOLD', label: 'Or', color: 'gold' },
    { value: 'SILVER', label: 'Argent', color: 'silver' },
    { value: 'BRONZE', label: 'Bronze', color: 'brown' },
    { value: 'NEW', label: 'Nouveau', color: 'green' },
    { value: 'AT_RISK', label: 'À risque', color: 'orange' },
    { value: 'CHURNED', label: 'Perdu', color: 'red' }
  ];

  // Comportements de paiement
  paymentBehaviors = [
    { value: 'EXCELLENT', label: 'Excellent', color: 'green' },
    { value: 'GOOD', label: 'Bon', color: 'lightgreen' },
    { value: 'AVERAGE', label: 'Moyen', color: 'orange' },
    { value: 'POOR', label: 'Mauvais', color: 'red' },
    { value: 'DELINQUENT', label: 'Défaillant', color: 'darkred' }
  ];

  // Sources
  sources = [
    'Site web',
    'Réseaux sociaux',
    'Recommandation',
    'Email marketing',
    'Téléphone',
    'Autre'
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
      paymentBehavior: [''],
      source: [''],
      isActive: ['']
    });
  }

  ngOnInit(): void {
    this.loadClients();
    this.setupFilterSubscription();
    this.setupUserSubscription();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadClients(): void {
    this.isLoading = true;
    this.loadingService.show();

    // Récupérer les clients depuis l'API
    this.crmCustomerService.getAllCrmCustomers()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (customers) => {
          this.clients = customers;
          this.filteredClients.data = this.clients;
          this.filteredClients.paginator = this.paginator;
          this.filteredClients.sort = this.sort;
          
          this.isLoading = false;
          this.loadingService.hide();
          
          this.notificationService.showSuccess(`${customers.length} clients chargés`);
        },
        error: (error) => {
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
    let filtered = [...this.clients];

    // Filtre de recherche
    if (filters.search) {
      const searchTerm = filters.search.toLowerCase();
      filtered = filtered.filter(client =>
        client.thirdParty?.name.toLowerCase().includes(searchTerm) ||
        client.thirdParty?.email.toLowerCase().includes(searchTerm) ||
        client.thirdParty?.phone.includes(searchTerm) ||
        client.notes?.toLowerCase().includes(searchTerm)
      );
    }

    // Filtre par segment
    if (filters.customerSegment) {
      filtered = filtered.filter(client => client.customerSegment === filters.customerSegment);
    }

    // Filtre par comportement de paiement
    if (filters.paymentBehavior) {
      filtered = filtered.filter(client => client.paymentBehavior === filters.paymentBehavior);
    }

    // Filtre par source
    if (filters.source) {
      filtered = filtered.filter(client => client.source === filters.source);
    }

    // Filtre par statut actif
    if (filters.isActive !== '') {
      filtered = filtered.filter(client => client.isActive === filters.isActive);
    }

    this.filteredClients.data = filtered;
  }

  // Gestion de la sélection
  toggleSelectAll(): void {
    if (this.selectAll) {
      this.selectedClients.clear();
    } else {
      this.filteredClients.data.forEach(client => {
        this.selectedClients.add(client.id);
      });
    }
    this.selectAll = !this.selectAll;
  }

  toggleClientSelection(clientId: number): void {
    if (this.selectedClients.has(clientId)) {
      this.selectedClients.delete(clientId);
    } else {
      this.selectedClients.add(clientId);
    }
    this.updateSelectAllState();
  }

  private updateSelectAllState(): void {
    const totalClients = this.filteredClients.data.length;
    const selectedCount = this.selectedClients.size;
    this.selectAll = totalClients > 0 && selectedCount === totalClients;
  }

  // Actions
  addClient(): void {
    this.router.navigate(['/crm/clients/new']);
  }

  editClient(client: CrmCustomer): void {
    this.router.navigate(['/crm/clients/edit', client.id]);
  }

  viewClient(client: CrmCustomer): void {
    this.router.navigate(['/crm/clients/view', client.id]);
  }

  deleteClient(client: CrmCustomer): void {
    this.crmCustomerService.deleteCrmCustomer(client.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.notificationService.showSuccess(`Client ${client.thirdParty?.name} supprimé avec succès`);
          this.loadClients(); // Recharger la liste
        },
        error: (error) => {
          console.error('Erreur lors de la suppression:', error);
          this.notificationService.showError('Erreur lors de la suppression du client');
        }
      });
  }

  toggleClientStatus(client: CrmCustomer): void {
    this.crmCustomerService.toggleCrmCustomerStatus(client.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (updatedClient) => {
          this.notificationService.showSuccess(`Statut du client ${client.thirdParty?.name} modifié avec succès`);
          // Mettre à jour la liste locale
          const index = this.clients.findIndex(c => c.id === client.id);
          if (index !== -1) {
            this.clients[index] = updatedClient;
            this.filteredClients.data = [...this.clients];
          }
        },
        error: (error) => {
          console.error('Erreur lors du changement de statut:', error);
          this.notificationService.showError('Erreur lors du changement de statut');
        }
      });
  }

  deleteSelectedClients(): void {
    if (this.selectedClients.size === 0) {
      this.notificationService.showWarning('Aucun client sélectionné');
      return;
    }

    const selectedIds = Array.from(this.selectedClients);
    let completed = 0;
    let errors = 0;

    selectedIds.forEach(clientId => {
      this.crmCustomerService.deleteCrmCustomer(clientId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            completed++;
            if (completed + errors === selectedIds.length) {
              if (errors === 0) {
                this.notificationService.showSuccess(`${completed} clients supprimés avec succès`);
                this.loadClients(); // Recharger la liste
              } else {
                this.notificationService.showWarning(`${completed} clients supprimés, ${errors} erreurs`);
              }
            }
          },
          error: (error) => {
            console.error('Erreur lors de la suppression:', error);
            errors++;
            if (completed + errors === selectedIds.length) {
              this.notificationService.showWarning(`${completed} clients supprimés, ${errors} erreurs`);
            }
          }
        });
    });
  }

  exportClients(): void {
    this.notificationService.showInfo('Export des clients en cours...');
    // TODO: Implémenter l'export
  }

  importClients(): void {
    this.notificationService.showInfo('Import des clients en cours...');
    // TODO: Implémenter l'import
  }

  clearFilters(): void {
    this.filterForm.reset();
    this.applyFilters();
  }

  toggleFilterPanel(): void {
    this.filterExpanded = !this.filterExpanded;
  }

  getSegmentColor(segment: string): string {
    return this.crmCustomerService.getSegmentColor(segment);
  }

  getSegmentIcon(segment: string): string {
    return this.crmCustomerService.getSegmentIcon(segment);
  }

  getSegmentLabel(segment: string): string {
    return this.crmCustomerService.getSegmentLabel(segment);
  }

  getPaymentBehaviorColor(behavior: string): string {
    switch (behavior) {
      case 'EXCELLENT': return 'green';
      case 'GOOD': return 'lightgreen';
      case 'AVERAGE': return 'orange';
      case 'POOR': return 'red';
      case 'DELINQUENT': return 'darkred';
      default: return 'gray';
    }
  }

  getPaymentBehaviorIcon(behavior: string): string {
    switch (behavior) {
      case 'EXCELLENT': return 'star';
      case 'GOOD': return 'thumb_up';
      case 'AVERAGE': return 'thumbs_up_down';
      case 'POOR': return 'thumb_down';
      case 'DELINQUENT': return 'warning';
      default: return 'help';
    }
  }

  getPaymentBehaviorLabel(behavior: string): string {
    switch (behavior) {
      case 'EXCELLENT': return 'Excellent';
      case 'GOOD': return 'Bon';
      case 'AVERAGE': return 'Moyen';
      case 'POOR': return 'Mauvais';
      case 'DELINQUENT': return 'Défaillant';
      default: return behavior;
    }
  }

  formatCurrency(amount: number, currency: string = 'EUR'): string {
    return this.crmCustomerService.formatCurrency(amount, currency);
  }

  formatDate(date: string): string {
    return this.crmCustomerService.formatDate(date);
  }

  getClientStatusColor(isActive: boolean): string {
    return isActive ? 'green' : 'red';
  }

  getClientStatusIcon(isActive: boolean): string {
    return isActive ? 'check_circle' : 'cancel';
  }

  getClientStatusLabel(isActive: boolean): string {
    return isActive ? 'Actif' : 'Inactif';
  }

  calculateRiskScore(client: CrmCustomer): number {
    return this.crmCustomerService.calculateRiskScore(client);
  }
}