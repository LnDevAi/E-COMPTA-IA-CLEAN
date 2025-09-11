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
import { SycebnlOrganizationService, SycebnlOrganization } from '../../../shared/services/sycebnl-organization.service';
import { User } from '../../../shared/models/user';

@Component({
  selector: 'app-organization-list',
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
  templateUrl: './organization-list.html',
  styleUrl: './organization-list.scss'
})
export class OrganizationList implements OnInit, OnDestroy {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  currentUser: User | null = null;
  isLoading = false;
  private destroy$ = new Subject<void>();

  // Données
  organizations: SycebnlOrganization[] = [];
  filteredOrganizations: MatTableDataSource<SycebnlOrganization> = new MatTableDataSource<SycebnlOrganization>();
  
  // Colonnes du tableau
  displayedColumns: string[] = [
    'select',
    'organizationName',
    'organizationType',
    'accountingSystem',
    'complianceStatus',
    'annualRevenue',
    'employeeCount',
    'actions'
  ];

  // Filtres
  filterForm: FormGroup;
  filterExpanded = false;
  
  // Sélection
  selectedOrganizations: Set<number> = new Set();
  selectAll = false;

  // Pagination et tri
  pageSize = 25;
  pageSizeOptions = [10, 25, 50, 100];

  // Types d'organisations disponibles
  organizationTypes = [
    { value: 'NGO', label: 'ONG', color: 'blue' },
    { value: 'ASSOCIATION', label: 'Association', color: 'green' },
    { value: 'FOUNDATION', label: 'Fondation', color: 'purple' },
    { value: 'COOPERATIVE', label: 'Coopérative', color: 'orange' },
    { value: 'OTHER', label: 'Autre', color: 'gray' }
  ];

  // Systèmes comptables disponibles
  accountingSystems = [
    { value: 'NORMAL', label: 'Normal', color: 'green' },
    { value: 'MINIMAL', label: 'Minimal', color: 'orange' }
  ];

  // Statuts de conformité disponibles
  complianceStatuses = [
    { value: 'COMPLIANT', label: 'Conforme', color: 'green' },
    { value: 'NON_COMPLIANT', label: 'Non conforme', color: 'red' },
    { value: 'PENDING', label: 'En attente', color: 'orange' },
    { value: 'UNDER_REVIEW', label: 'En révision', color: 'blue' }
  ];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private notificationService: NotificationService,
    private loadingService: LoadingService,
    private sycebnlOrganizationService: SycebnlOrganizationService,
    private router: Router,
    private dialog: MatDialog
  ) {
    this.filterForm = this.fb.group({
      search: [''],
      organizationType: [''],
      accountingSystem: [''],
      complianceStatus: ['']
    });
  }

  ngOnInit(): void {
    this.loadOrganizations();
    this.setupFilterSubscription();
    this.setupUserSubscription();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadOrganizations(): void {
    this.isLoading = true;
    this.loadingService.show();

    this.sycebnlOrganizationService.getAllSycebnlOrganizations()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (organizations: SycebnlOrganization[]) => {
          this.organizations = organizations;
          this.filteredOrganizations.data = this.organizations;
          this.filteredOrganizations.paginator = this.paginator;
          this.filteredOrganizations.sort = this.sort;
          
          this.isLoading = false;
          this.loadingService.hide();
          
          this.notificationService.showSuccess(`${this.organizations.length} organisations chargées`);
        },
        error: (error: any) => {
          console.error('Erreur lors du chargement des organisations:', error);
          this.isLoading = false;
          this.loadingService.hide();
          this.notificationService.showError('Erreur lors du chargement des organisations');
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
    let filtered = [...this.organizations];

    if (filters.search) {
      const searchTerm = filters.search.toLowerCase();
      filtered = filtered.filter(organization =>
        organization.organizationName?.toLowerCase().includes(searchTerm) ||
        organization.organizationType?.toLowerCase().includes(searchTerm) ||
        organization.email?.toLowerCase().includes(searchTerm)
      );
    }

    if (filters.organizationType) {
      filtered = filtered.filter(organization => organization.organizationType === filters.organizationType);
    }

    if (filters.accountingSystem) {
      filtered = filtered.filter(organization => organization.accountingSystem === filters.accountingSystem);
    }

    if (filters.complianceStatus) {
      filtered = filtered.filter(organization => organization.ohadaComplianceStatus === filters.complianceStatus);
    }

    this.filteredOrganizations.data = filtered;
  }

  // Sélection
  toggleSelectAll(): void {
    if (this.selectAll) {
      this.selectedOrganizations.clear();
    } else {
      this.filteredOrganizations.data.forEach(organization => {
        if (organization.id) {
          this.selectedOrganizations.add(organization.id);
        }
      });
    }
    this.selectAll = !this.selectAll;
  }

  toggleOrganizationSelection(organizationId: number | undefined): void {
    if (!organizationId) return;
    
    if (this.selectedOrganizations.has(organizationId)) {
      this.selectedOrganizations.delete(organizationId);
    } else {
      this.selectedOrganizations.add(organizationId);
    }
    this.updateSelectAllState();
  }

  private updateSelectAllState(): void {
    const totalOrganizations = this.filteredOrganizations.data.length;
    const selectedCount = this.selectedOrganizations.size;
    this.selectAll = totalOrganizations > 0 && selectedCount === totalOrganizations;
  }

  // Actions
  addOrganization(): void {
    this.router.navigate(['/sycebnl/organizations/new']);
  }

  editOrganization(organization: SycebnlOrganization): void {
    this.router.navigate(['/sycebnl/organizations/edit', organization.id]);
  }

  viewOrganization(organization: SycebnlOrganization): void {
    this.router.navigate(['/sycebnl/organizations/view', organization.id]);
  }

  deleteOrganization(organization: SycebnlOrganization): void {
    this.notificationService.showInfo(`Suppression de ${organization.organizationName}`);
  }

  deleteSelectedOrganizations(): void {
    if (this.selectedOrganizations.size === 0) {
      this.notificationService.showWarning('Aucune organisation sélectionnée');
      return;
    }
    this.notificationService.showInfo(`${this.selectedOrganizations.size} organisations sélectionnées pour suppression`);
  }

  exportOrganizations(): void {
    this.notificationService.showInfo('Export des organisations en cours...');
  }

  clearFilters(): void {
    this.filterForm.reset();
    this.applyFilters();
  }

  toggleFilterPanel(): void {
    this.filterExpanded = !this.filterExpanded;
  }

  // Méthodes utilitaires
  getOrganizationTypeColor(type: string): string {
    const typeObj = this.organizationTypes.find(t => t.value === type);
    return typeObj ? typeObj.color : 'gray';
  }

  getOrganizationTypeIcon(type: string): string {
    switch (type) {
      case 'NGO': return 'volunteer_activism';
      case 'ASSOCIATION': return 'groups';
      case 'FOUNDATION': return 'account_balance';
      case 'COOPERATIVE': return 'handshake';
      case 'OTHER': return 'business';
      default: return 'help';
    }
  }

  getAccountingSystemColor(system: string): string {
    const systemObj = this.accountingSystems.find(s => s.value === system);
    return systemObj ? systemObj.color : 'gray';
  }

  getComplianceStatusColor(status: string): string {
    const statusObj = this.complianceStatuses.find(s => s.value === status);
    return statusObj ? statusObj.color : 'gray';
  }

  getComplianceStatusIcon(status: string): string {
    switch (status) {
      case 'COMPLIANT': return 'check_circle';
      case 'NON_COMPLIANT': return 'cancel';
      case 'PENDING': return 'schedule';
      case 'UNDER_REVIEW': return 'visibility';
      default: return 'help';
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
}
