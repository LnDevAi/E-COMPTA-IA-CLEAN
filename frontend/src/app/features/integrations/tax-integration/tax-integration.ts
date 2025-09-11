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
import { MatTableDataSource } from '@angular/material/table';
import { Subject, takeUntil } from 'rxjs';

import { AuthService } from '../../../shared/services/auth';
import { NotificationService } from '../../../shared/services/notification';
import { LoadingService } from '../../../shared/services/loading';
import { User } from '../../../shared/models/user';

interface TaxDeclaration {
  id: string;
  period: string;
  type: string;
  status: 'draft' | 'submitted' | 'approved' | 'rejected';
  dueDate: Date;
  submittedDate?: Date;
  amount: number;
  penalty?: number;
  interest?: number;
  totalAmount: number;
}

interface TaxRate {
  id: string;
  name: string;
  rate: number;
  type: 'percentage' | 'fixed';
  category: string;
  isActive: boolean;
  effectiveDate: Date;
  endDate?: Date;
}

interface TaxCalculation {
  id: string;
  description: string;
  baseAmount: number;
  taxRate: number;
  taxAmount: number;
  category: string;
  period: string;
}

@Component({
  selector: 'app-tax-integration',
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
    MatChipsModule
  ],
  templateUrl: './tax-integration.html',
  styleUrl: './tax-integration.scss'
})
export class TaxIntegration implements OnInit, OnDestroy {
  currentUser: User | null = null;
  isLoading = false;
  private destroy$ = new Subject<void>();

  // Formulaires
  taxRateForm: FormGroup;
  declarationForm: FormGroup;
  calculationForm: FormGroup;

  // Données
  taxDeclarations: TaxDeclaration[] = [];
  taxRates: TaxRate[] = [];
  taxCalculations: TaxCalculation[] = [];
  
  declarationsDataSource = new MatTableDataSource<TaxDeclaration>();
  ratesDataSource = new MatTableDataSource<TaxRate>();
  calculationsDataSource = new MatTableDataSource<TaxCalculation>();

  // Colonnes des tableaux
  declarationsColumns: string[] = [
    'period', 'type', 'status', 'dueDate', 'amount', 'penalty', 'totalAmount', 'actions'
  ];
  ratesColumns: string[] = [
    'name', 'rate', 'type', 'category', 'effectiveDate', 'isActive', 'actions'
  ];
  calculationsColumns: string[] = [
    'description', 'baseAmount', 'taxRate', 'taxAmount', 'category', 'period', 'actions'
  ];

  // Types de déclarations
  declarationTypes = [
    { value: 'VAT', label: 'TVA' },
    { value: 'INCOME_TAX', label: 'Impôt sur le revenu' },
    { value: 'CORPORATE_TAX', label: 'Impôt sur les sociétés' },
    { value: 'PAYROLL_TAX', label: 'Taxes sur les salaires' },
    { value: 'PROPERTY_TAX', label: 'Taxe foncière' },
    { value: 'BUSINESS_TAX', label: 'Taxe professionnelle' }
  ];

  // Catégories de taxes
  taxCategories = [
    'TVA',
    'Impôt sur le revenu',
    'Impôt sur les sociétés',
    'Taxes sur les salaires',
    'Taxe foncière',
    'Taxe professionnelle',
    'Autres'
  ];

  // Périodes
  periods = [
    '2024-Q1',
    '2024-Q2',
    '2024-Q3',
    '2024-Q4',
    '2024-Année',
    '2025-Q1',
    '2025-Q2'
  ];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private notificationService: NotificationService,
    private loadingService: LoadingService
  ) {
    this.taxRateForm = this.fb.group({
      name: ['', Validators.required],
      rate: [0, [Validators.required, Validators.min(0)]],
      type: ['percentage', Validators.required],
      category: ['', Validators.required],
      isActive: [true],
      effectiveDate: [new Date(), Validators.required],
      endDate: [null]
    });

    this.declarationForm = this.fb.group({
      period: ['', Validators.required],
      type: ['', Validators.required],
      dueDate: [new Date(), Validators.required],
      amount: [0, [Validators.required, Validators.min(0)]]
    });

    this.calculationForm = this.fb.group({
      description: ['', Validators.required],
      baseAmount: [0, [Validators.required, Validators.min(0)]],
      taxRate: [0, [Validators.required, Validators.min(0)]],
      category: ['', Validators.required],
      period: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.setupUserSubscription();
    this.loadTaxData();
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

  private loadTaxData(): void {
    this.isLoading = true;
    this.loadingService.show();

    // Simulation de chargement des données
    setTimeout(() => {
      this.initializeTaxDeclarations();
      this.initializeTaxRates();
      this.initializeTaxCalculations();
      this.isLoading = false;
      this.loadingService.hide();
    }, 2000);
  }

  private initializeTaxDeclarations(): void {
    this.taxDeclarations = [
      {
        id: '1',
        period: '2024-Q3',
        type: 'VAT',
        status: 'submitted',
        dueDate: new Date('2024-10-31'),
        submittedDate: new Date('2024-10-25'),
        amount: 15000,
        totalAmount: 15000
      },
      {
        id: '2',
        period: '2024-Q2',
        type: 'INCOME_TAX',
        status: 'approved',
        dueDate: new Date('2024-07-31'),
        submittedDate: new Date('2024-07-20'),
        amount: 25000,
        penalty: 500,
        interest: 200,
        totalAmount: 25700
      },
      {
        id: '3',
        period: '2024-Q4',
        type: 'VAT',
        status: 'draft',
        dueDate: new Date('2025-01-31'),
        amount: 18000,
        totalAmount: 18000
      }
    ];
    this.declarationsDataSource.data = this.taxDeclarations;
  }

  private initializeTaxRates(): void {
    this.taxRates = [
      {
        id: '1',
        name: 'TVA Standard',
        rate: 20,
        type: 'percentage',
        category: 'TVA',
        isActive: true,
        effectiveDate: new Date('2024-01-01')
      },
      {
        id: '2',
        name: 'TVA Réduite',
        rate: 5.5,
        type: 'percentage',
        category: 'TVA',
        isActive: true,
        effectiveDate: new Date('2024-01-01')
      },
      {
        id: '3',
        name: 'Impôt sur les sociétés',
        rate: 25,
        type: 'percentage',
        category: 'Impôt sur les sociétés',
        isActive: true,
        effectiveDate: new Date('2024-01-01')
      }
    ];
    this.ratesDataSource.data = this.taxRates;
  }

  private initializeTaxCalculations(): void {
    this.taxCalculations = [
      {
        id: '1',
        description: 'Ventes Q3 2024',
        baseAmount: 75000,
        taxRate: 20,
        taxAmount: 15000,
        category: 'TVA',
        period: '2024-Q3'
      },
      {
        id: '2',
        description: 'Bénéfices 2024',
        baseAmount: 100000,
        taxRate: 25,
        taxAmount: 25000,
        category: 'Impôt sur les sociétés',
        period: '2024-Année'
      }
    ];
    this.calculationsDataSource.data = this.taxCalculations;
  }

  // Gestion des taux de taxes
  addTaxRate(): void {
    if (this.taxRateForm.invalid) {
      this.notificationService.showWarning('Veuillez remplir tous les champs obligatoires');
      return;
    }

    const rateData = this.taxRateForm.value;
    const newRate: TaxRate = {
      id: Date.now().toString(),
      ...rateData
    };

    this.taxRates.push(newRate);
    this.ratesDataSource.data = this.taxRates;
    this.taxRateForm.reset();
    this.taxRateForm.patchValue({ type: 'percentage', isActive: true, effectiveDate: new Date() });
    this.notificationService.showSuccess('Taux de taxe ajouté avec succès');
  }

  updateTaxRate(rate: TaxRate): void {
    const index = this.taxRates.findIndex(r => r.id === rate.id);
    if (index !== -1) {
      this.taxRates[index] = rate;
      this.ratesDataSource.data = this.taxRates;
      this.notificationService.showSuccess('Taux de taxe mis à jour');
    }
  }

  deleteTaxRate(rate: TaxRate): void {
    if (confirm(`Êtes-vous sûr de vouloir supprimer le taux ${rate.name} ?`)) {
      this.taxRates = this.taxRates.filter(r => r.id !== rate.id);
      this.ratesDataSource.data = this.taxRates;
      this.notificationService.showSuccess('Taux de taxe supprimé');
    }
  }

  // Gestion des déclarations
  addDeclaration(): void {
    if (this.declarationForm.invalid) {
      this.notificationService.showWarning('Veuillez remplir tous les champs obligatoires');
      return;
    }

    const declarationData = this.declarationForm.value;
    const newDeclaration: TaxDeclaration = {
      id: Date.now().toString(),
      ...declarationData,
      status: 'draft',
      totalAmount: declarationData.amount
    };

    this.taxDeclarations.push(newDeclaration);
    this.declarationsDataSource.data = this.taxDeclarations;
    this.declarationForm.reset();
    this.declarationForm.patchValue({ dueDate: new Date() });
    this.notificationService.showSuccess('Déclaration créée avec succès');
  }

  submitDeclaration(declaration: TaxDeclaration): void {
    declaration.status = 'submitted';
    declaration.submittedDate = new Date();
    this.declarationsDataSource.data = this.taxDeclarations;
    this.notificationService.showSuccess('Déclaration soumise avec succès');
  }

  approveDeclaration(declaration: TaxDeclaration): void {
    declaration.status = 'approved';
    this.declarationsDataSource.data = this.taxDeclarations;
    this.notificationService.showSuccess('Déclaration approuvée');
  }

  rejectDeclaration(declaration: TaxDeclaration): void {
    declaration.status = 'rejected';
    this.declarationsDataSource.data = this.taxDeclarations;
    this.notificationService.showSuccess('Déclaration rejetée');
  }

  // Calculs de taxes
  calculateTax(): void {
    if (this.calculationForm.invalid) {
      this.notificationService.showWarning('Veuillez remplir tous les champs obligatoires');
      return;
    }

    const calculationData = this.calculationForm.value;
    const taxAmount = (calculationData.baseAmount * calculationData.taxRate) / 100;
    
    const newCalculation: TaxCalculation = {
      id: Date.now().toString(),
      ...calculationData,
      taxAmount
    };

    this.taxCalculations.push(newCalculation);
    this.calculationsDataSource.data = this.taxCalculations;
    this.calculationForm.reset();
    this.notificationService.showSuccess('Calcul de taxe effectué');
  }

  // Actions
  exportDeclarations(): void {
    this.notificationService.showInfo('Export des déclarations en cours...');
  }

  generateTaxReport(): void {
    this.notificationService.showInfo('Génération du rapport fiscal en cours...');
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

  formatPercentage(rate: number): string {
    return `${rate}%`;
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'draft':
        return '#ff9800';
      case 'submitted':
        return '#2196f3';
      case 'approved':
        return '#4caf50';
      case 'rejected':
        return '#f44336';
      default:
        return '#666';
    }
  }

  getStatusLabel(status: string): string {
    switch (status) {
      case 'draft':
        return 'Brouillon';
      case 'submitted':
        return 'Soumis';
      case 'approved':
        return 'Approuvé';
      case 'rejected':
        return 'Rejeté';
      default:
        return 'Inconnu';
    }
  }

  getTypeLabel(type: string): string {
    const declarationType = this.declarationTypes.find(t => t.value === type);
    return declarationType?.label || type;
  }

  isOverdue(declaration: TaxDeclaration): boolean {
    return declaration.dueDate < new Date() && declaration.status !== 'approved';
  }

  getDaysUntilDue(declaration: TaxDeclaration): number {
    const today = new Date();
    const dueDate = new Date(declaration.dueDate);
    const diffTime = dueDate.getTime() - today.getTime();
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  }
}








