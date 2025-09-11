import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { CommonModule, NgIf } from '@angular/common';
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
import { EmployeeService, Employee } from '../../../shared/services/employee.service';
import { User } from '../../../shared/models/user';

@Component({
  selector: 'app-employee-list',
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
    MatDividerModule,
    MatBadgeModule,
    MatProgressBarModule
  ],
  templateUrl: './employee-list.html',
  styleUrl: './employee-list.scss'
})
export class EmployeeList implements OnInit, OnDestroy {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  currentUser: User | null = null;
  isLoading = false;
  private destroy$ = new Subject<void>();

  // Données
  employees: Employee[] = [];
  filteredEmployees: MatTableDataSource<Employee> = new MatTableDataSource<Employee>();
  
  // Colonnes du tableau
  displayedColumns: string[] = [
    'select',
    'employeeCode',
    'fullName',
    'department',
    'position',
    'contractType',
    'employmentStatus',
    'baseSalary',
    'actions'
  ];

  // Filtres
  filterForm: FormGroup;
  filterExpanded = false;
  
  // Sélection
  selectedEmployees: Set<number> = new Set();
  selectAll = false;

  // Pagination et tri
  pageSize = 25;
  pageSizeOptions = [10, 25, 50, 100];

  // Types de contrats disponibles
  contractTypes = [
    { value: 'PERMANENT', label: 'CDI', color: 'green' },
    { value: 'TEMPORARY', label: 'CDD', color: 'orange' },
    { value: 'INTERNSHIP', label: 'Stage', color: 'blue' },
    { value: 'CONSULTANT', label: 'Consultant', color: 'purple' },
    { value: 'FREELANCE', label: 'Freelance', color: 'gray' }
  ];

  // Statuts d'emploi disponibles
  employmentStatuses = [
    { value: 'ACTIVE', label: 'Actif', color: 'green' },
    { value: 'INACTIVE', label: 'Inactif', color: 'gray' },
    { value: 'ON_LEAVE', label: 'En congé', color: 'orange' },
    { value: 'TERMINATED', label: 'Terminé', color: 'red' }
  ];

  // Départements disponibles
  departments = [
    'Direction', 'Ressources Humaines', 'Comptabilité', 'Informatique',
    'Commercial', 'Marketing', 'Production', 'Logistique', 'Autre'
  ];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private notificationService: NotificationService,
    private loadingService: LoadingService,
    private employeeService: EmployeeService,
    private router: Router,
    private dialog: MatDialog
  ) {
    this.filterForm = this.fb.group({
      search: [''],
      department: [''],
      contractType: [''],
      employmentStatus: ['']
    });
  }

  ngOnInit(): void {
    this.loadEmployees();
    this.setupFilterSubscription();
    this.setupUserSubscription();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadEmployees(): void {
    this.isLoading = true;
    this.loadingService.show();

    this.employeeService.getAllEmployees()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (employees: Employee[]) => {
          this.employees = employees;
          this.filteredEmployees.data = this.employees;
          this.filteredEmployees.paginator = this.paginator;
          this.filteredEmployees.sort = this.sort;
          
          this.isLoading = false;
          this.loadingService.hide();
          
          this.notificationService.showSuccess(`${this.employees.length} employés chargés`);
        },
        error: (error: any) => {
          console.error('Erreur lors du chargement des employés:', error);
          this.isLoading = false;
          this.loadingService.hide();
          this.notificationService.showError('Erreur lors du chargement des employés');
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
    let filtered = [...this.employees];

    if (filters.search) {
      const searchTerm = filters.search.toLowerCase();
      filtered = filtered.filter(employee =>
        employee.employeeCode?.toLowerCase().includes(searchTerm) ||
        employee.firstName?.toLowerCase().includes(searchTerm) ||
        employee.lastName?.toLowerCase().includes(searchTerm) ||
        employee.email?.toLowerCase().includes(searchTerm) ||
        employee.department?.toLowerCase().includes(searchTerm) ||
        employee.position?.toLowerCase().includes(searchTerm)
      );
    }

    if (filters.department) {
      filtered = filtered.filter(employee => employee.department === filters.department);
    }

    if (filters.contractType) {
      filtered = filtered.filter(employee => employee.contractType === filters.contractType);
    }

    if (filters.employmentStatus) {
      filtered = filtered.filter(employee => employee.employmentStatus === filters.employmentStatus);
    }

    this.filteredEmployees.data = filtered;
  }

  // Sélection
  toggleSelectAll(): void {
    if (this.selectAll) {
      this.selectedEmployees.clear();
    } else {
      this.filteredEmployees.data.forEach(employee => {
        if (employee.id) {
          this.selectedEmployees.add(employee.id);
        }
      });
    }
    this.selectAll = !this.selectAll;
  }

  toggleEmployeeSelection(employeeId: number | undefined): void {
    if (!employeeId) return;
    
    if (this.selectedEmployees.has(employeeId)) {
      this.selectedEmployees.delete(employeeId);
    } else {
      this.selectedEmployees.add(employeeId);
    }
    this.updateSelectAllState();
  }

  private updateSelectAllState(): void {
    const totalEmployees = this.filteredEmployees.data.length;
    const selectedCount = this.selectedEmployees.size;
    this.selectAll = totalEmployees > 0 && selectedCount === totalEmployees;
  }

  // Actions
  addEmployee(): void {
    this.router.navigate(['/payroll/employees/new']);
  }

  editEmployee(employee: Employee): void {
    this.router.navigate(['/payroll/employees/edit', employee.id]);
  }

  viewEmployee(employee: Employee): void {
    this.router.navigate(['/payroll/employees/view', employee.id]);
  }

  deleteEmployee(employee: Employee): void {
    this.notificationService.showInfo(`Suppression de ${employee.firstName} ${employee.lastName}`);
  }

  deleteSelectedEmployees(): void {
    if (this.selectedEmployees.size === 0) {
      this.notificationService.showWarning('Aucun employé sélectionné');
      return;
    }
    this.notificationService.showInfo(`${this.selectedEmployees.size} employés sélectionnés pour suppression`);
  }

  exportEmployees(): void {
    this.notificationService.showInfo('Export des employés en cours...');
  }

  clearFilters(): void {
    this.filterForm.reset();
    this.applyFilters();
  }

  toggleFilterPanel(): void {
    this.filterExpanded = !this.filterExpanded;
  }

  // Méthodes utilitaires
  getContractTypeColor(type: string): string {
    const typeObj = this.contractTypes.find(t => t.value === type);
    return typeObj ? typeObj.color : 'gray';
  }

  getContractTypeIcon(type: string): string {
    switch (type) {
      case 'PERMANENT': return 'work';
      case 'TEMPORARY': return 'schedule';
      case 'INTERNSHIP': return 'school';
      case 'CONSULTANT': return 'business_center';
      case 'FREELANCE': return 'person';
      default: return 'help';
    }
  }

  getEmploymentStatusColor(status: string): string {
    const statusObj = this.employmentStatuses.find(s => s.value === status);
    return statusObj ? statusObj.color : 'gray';
  }

  getEmploymentStatusIcon(status: string): string {
    switch (status) {
      case 'ACTIVE': return 'check_circle';
      case 'INACTIVE': return 'pause_circle';
      case 'ON_LEAVE': return 'flight_takeoff';
      case 'TERMINATED': return 'cancel';
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

  getFullName(employee: Employee): string {
    return `${employee.firstName} ${employee.lastName}`;
  }
}
