import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatChipsModule } from '@angular/material/chips';
import { MatTableModule } from '@angular/material/table';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { Subject, takeUntil } from 'rxjs';

import { AuthService } from '../../../shared/services/auth';
import { NotificationService } from '../../../shared/services/notification';
import { LoadingService } from '../../../shared/services/loading';
import { PayrollService, Payroll } from '../../../shared/services/payroll.service';
import { EmployeeService, Employee } from '../../../shared/services/employee.service';
import { User } from '../../../shared/models/user';

interface PayrollCalculation {
  employeeId: number;
  employeeName: string;
  baseSalary: number;
  overtimeHours: number;
  overtimeRate: number;
  overtimeAmount: number;
  bonus: number;
  allowance: number;
  commission: number;
  grossSalary: number;
  taxDeduction: number;
  insuranceDeduction: number;
  pensionDeduction: number;
  totalDeductions: number;
  netSalary: number;
}

@Component({
  selector: 'app-payroll-calculations',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatDividerModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatTooltipModule,
    MatChipsModule,
    MatTableModule,
    MatCheckboxModule
  ],
  templateUrl: './payroll-calculations.html',
  styleUrl: './payroll-calculations.scss'
})
export class PayrollCalculations implements OnInit, OnDestroy {
  currentUser: User | null = null;
  isLoading = false;
  private destroy$ = new Subject<void>();

  // Formulaires
  calculationForm: FormGroup;
  payrollForm: FormGroup;

  // Données
  employees: Employee[] = [];
  payrollCalculations: PayrollCalculation[] = [];
  selectedEmployees: Set<number> = new Set();

  // Colonnes du tableau
  displayedColumns: string[] = [
    'employeeName',
    'baseSalary',
    'overtimeAmount',
    'bonus',
    'allowance',
    'grossSalary',
    'totalDeductions',
    'netSalary'
  ];

  // Périodes de paie
  payPeriods = [
    { value: 'MONTHLY', label: 'Mensuel' },
    { value: 'WEEKLY', label: 'Hebdomadaire' },
    { value: 'BIWEEKLY', label: 'Bi-hebdomadaire' },
    { value: 'QUARTERLY', label: 'Trimestriel' }
  ];

  // Méthodes de paiement
  paymentMethods = [
    { value: 'BANK_TRANSFER', label: 'Virement bancaire' },
    { value: 'CHECK', label: 'Chèque' },
    { value: 'CASH', label: 'Espèces' },
    { value: 'MOBILE_MONEY', label: 'Mobile Money' }
  ];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private notificationService: NotificationService,
    private loadingService: LoadingService,
    private payrollService: PayrollService,
    private employeeService: EmployeeService
  ) {
    this.calculationForm = this.fb.group({
      payPeriod: ['MONTHLY', Validators.required],
      payDate: [new Date(), Validators.required],
      startDate: [new Date(), Validators.required],
      endDate: [new Date(), Validators.required]
    });

    this.payrollForm = this.fb.group({
      paymentMethod: ['BANK_TRANSFER', Validators.required],
      paymentDate: [new Date(), Validators.required],
      notes: ['']
    });
  }

  ngOnInit(): void {
    this.setupUserSubscription();
    this.loadEmployees();
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

  private loadEmployees(): void {
    this.isLoading = true;
    this.loadingService.show();

    this.employeeService.getAllEmployees()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (employees: Employee[]) => {
          this.employees = employees;
          this.isLoading = false;
          this.loadingService.hide();
        },
        error: (error: any) => {
          console.error('Erreur lors du chargement des employés:', error);
          this.isLoading = false;
          this.loadingService.hide();
          this.notificationService.showError('Erreur lors du chargement des employés');
        }
      });
  }

  // Calculs de paie
  calculatePayroll(): void {
    if (this.calculationForm.invalid) {
      this.notificationService.showWarning('Veuillez remplir tous les champs obligatoires');
      return;
    }

    this.isLoading = true;
    this.loadingService.show();

    const formValue = this.calculationForm.value;
    this.payrollCalculations = [];

    // Calculer la paie pour chaque employé sélectionné
    this.employees.forEach(employee => {
      if (this.selectedEmployees.has(employee.id!) || this.selectedEmployees.size === 0) {
        const calculation = this.calculateEmployeePayroll(employee, formValue);
        this.payrollCalculations.push(calculation);
      }
    });

    this.isLoading = false;
    this.loadingService.hide();
    this.notificationService.showSuccess(`${this.payrollCalculations.length} calculs de paie effectués`);
  }

  private calculateEmployeePayroll(employee: Employee, period: any): PayrollCalculation {
    const baseSalary = employee.baseSalary || 0;
    const overtimeHours = Math.random() * 20; // Simulation
    const overtimeRate = baseSalary / 160; // Taux horaire basé sur 160h/mois
    const overtimeAmount = overtimeHours * overtimeRate;
    const bonus = Math.random() * 500; // Simulation
    const allowance = Math.random() * 200; // Simulation
    const commission = Math.random() * 300; // Simulation

    const grossSalary = baseSalary + overtimeAmount + bonus + allowance + commission;

    // Calcul des déductions
    const taxDeduction = grossSalary * 0.15; // 15% d'impôt
    const insuranceDeduction = grossSalary * 0.05; // 5% d'assurance
    const pensionDeduction = grossSalary * 0.08; // 8% de pension
    const totalDeductions = taxDeduction + insuranceDeduction + pensionDeduction;

    const netSalary = grossSalary - totalDeductions;

    return {
      employeeId: employee.id!,
      employeeName: `${employee.firstName} ${employee.lastName}`,
      baseSalary,
      overtimeHours: Math.round(overtimeHours * 100) / 100,
      overtimeRate: Math.round(overtimeRate * 100) / 100,
      overtimeAmount: Math.round(overtimeAmount * 100) / 100,
      bonus: Math.round(bonus * 100) / 100,
      allowance: Math.round(allowance * 100) / 100,
      commission: Math.round(commission * 100) / 100,
      grossSalary: Math.round(grossSalary * 100) / 100,
      taxDeduction: Math.round(taxDeduction * 100) / 100,
      insuranceDeduction: Math.round(insuranceDeduction * 100) / 100,
      pensionDeduction: Math.round(pensionDeduction * 100) / 100,
      totalDeductions: Math.round(totalDeductions * 100) / 100,
      netSalary: Math.round(netSalary * 100) / 100
    };
  }

  // Sélection des employés
  toggleEmployeeSelection(employeeId: number): void {
    if (this.selectedEmployees.has(employeeId)) {
      this.selectedEmployees.delete(employeeId);
    } else {
      this.selectedEmployees.add(employeeId);
    }
  }

  selectAllEmployees(): void {
    this.selectedEmployees.clear();
    this.employees.forEach(employee => {
      if (employee.id) {
        this.selectedEmployees.add(employee.id);
      }
    });
  }

  clearSelection(): void {
    this.selectedEmployees.clear();
  }

  // Actions
  generatePayroll(): void {
    if (this.payrollCalculations.length === 0) {
      this.notificationService.showWarning('Veuillez d\'abord calculer la paie');
      return;
    }

    if (this.payrollForm.invalid) {
      this.notificationService.showWarning('Veuillez remplir tous les champs obligatoires');
      return;
    }

    this.isLoading = true;
    this.loadingService.show();

    // Simulation de génération de paie
    setTimeout(() => {
      this.isLoading = false;
      this.loadingService.hide();
      this.notificationService.showSuccess('Paie générée avec succès');
    }, 2000);
  }

  exportPayroll(): void {
    if (this.payrollCalculations.length === 0) {
      this.notificationService.showWarning('Aucun calcul de paie à exporter');
      return;
    }

    this.notificationService.showInfo('Export de la paie en cours...');
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

  getTotalGrossSalary(): number {
    return this.payrollCalculations.reduce((total, calc) => total + calc.grossSalary, 0);
  }

  getTotalDeductions(): number {
    return this.payrollCalculations.reduce((total, calc) => total + calc.totalDeductions, 0);
  }

  getTotalNetSalary(): number {
    return this.payrollCalculations.reduce((total, calc) => total + calc.netSalary, 0);
  }
}
