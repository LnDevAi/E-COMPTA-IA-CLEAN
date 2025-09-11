import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
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
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatChipsModule } from '@angular/material/chips';
import { MatTabsModule } from '@angular/material/tabs';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatDividerModule } from '@angular/material/divider';
import { MatTableDataSource } from '@angular/material/table';
import { Subject, takeUntil } from 'rxjs';

import { AuthService } from '../../../shared/services/auth';
import { NotificationService } from '../../../shared/services/notification';
import { LoadingService } from '../../../shared/services/loading';
import { User } from '../../../shared/models/user';

interface ReportTemplate {
  id: string;
  name: string;
  description: string;
  category: string;
  dataSource: string;
  columns: ReportColumn[];
  filters: ReportFilter[];
  groupBy: string[];
  orderBy: string[];
  chartType?: string;
}

interface ReportColumn {
  field: string;
  label: string;
  type: 'string' | 'number' | 'date' | 'currency' | 'boolean';
  visible: boolean;
  aggregate?: 'sum' | 'avg' | 'count' | 'min' | 'max';
}

interface ReportFilter {
  field: string;
  label: string;
  type: 'string' | 'number' | 'date' | 'select' | 'boolean';
  operator: 'equals' | 'contains' | 'greater' | 'less' | 'between';
  value: any;
  options?: any[];
}

interface ReportData {
  columns: string[];
  rows: any[];
  summary: any;
  totalRows: number;
}

@Component({
  selector: 'app-report-builder',
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
    MatDialogModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    MatChipsModule,
    MatTabsModule,
    MatExpansionModule,
    MatDividerModule
  ],
  templateUrl: './report-builder.html',
  styleUrl: './report-builder.scss'
})
export class ReportBuilder implements OnInit, OnDestroy {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  currentUser: User | null = null;
  isLoading = false;
  private destroy$ = new Subject<void>();

  // Formulaires
  reportForm: FormGroup;
  filterForm: FormGroup;

  // Données
  reportTemplates: ReportTemplate[] = [];
  selectedTemplate: ReportTemplate | null = null;
  reportData: ReportData | null = null;
  dataSource = new MatTableDataSource<any>([]);

  // Colonnes du tableau
  displayedColumns: string[] = [];

  // Catégories de rapports
  reportCategories = [
    { value: 'FINANCIAL', label: 'Financier' },
    { value: 'CRM', label: 'CRM' },
    { value: 'INVENTORY', label: 'Inventaire' },
    { value: 'PAYROLL', label: 'Paie' },
    { value: 'SYCEBNL', label: 'SYCEBNL' },
    { value: 'CUSTOM', label: 'Personnalisé' }
  ];

  // Sources de données
  dataSources = [
    { value: 'ACCOUNTS', label: 'Comptes' },
    { value: 'JOURNAL_ENTRIES', label: 'Écritures comptables' },
    { value: 'CUSTOMERS', label: 'Clients' },
    { value: 'EMPLOYEES', label: 'Employés' },
    { value: 'INVENTORY', label: 'Inventaire' },
    { value: 'TRANSACTIONS', label: 'Transactions' }
  ];

  // Types de graphiques
  chartTypes = [
    { value: 'BAR', label: 'Graphique en barres' },
    { value: 'LINE', label: 'Graphique linéaire' },
    { value: 'PIE', label: 'Graphique en secteurs' },
    { value: 'AREA', label: 'Graphique en aires' },
    { value: 'TABLE', label: 'Tableau uniquement' }
  ];

  // Opérateurs de filtrage
  filterOperators = [
    { value: 'equals', label: 'Égal à' },
    { value: 'contains', label: 'Contient' },
    { value: 'greater', label: 'Supérieur à' },
    { value: 'less', label: 'Inférieur à' },
    { value: 'between', label: 'Entre' }
  ];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private notificationService: NotificationService,
    private loadingService: LoadingService
  ) {
    this.reportForm = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      category: ['', Validators.required],
      dataSource: ['', Validators.required],
      chartType: ['TABLE']
    });

    this.filterForm = this.fb.group({
      field: [''],
      operator: ['equals'],
      value: [''],
      value2: ['']
    });

    this.initializeTemplates();
  }

  ngOnInit(): void {
    this.setupUserSubscription();
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

  private initializeTemplates(): void {
    this.reportTemplates = [
      {
        id: 'financial_summary',
        name: 'Résumé Financier',
        description: 'Vue d\'ensemble des finances de l\'entreprise',
        category: 'FINANCIAL',
        dataSource: 'JOURNAL_ENTRIES',
        columns: [
          { field: 'date', label: 'Date', type: 'date', visible: true },
          { field: 'account', label: 'Compte', type: 'string', visible: true },
          { field: 'debit', label: 'Débit', type: 'currency', visible: true, aggregate: 'sum' },
          { field: 'credit', label: 'Crédit', type: 'currency', visible: true, aggregate: 'sum' },
          { field: 'balance', label: 'Solde', type: 'currency', visible: true }
        ],
        filters: [],
        groupBy: ['account'],
        orderBy: ['date']
      },
      {
        id: 'customer_analysis',
        name: 'Analyse des Clients',
        description: 'Analyse détaillée des clients et prospects',
        category: 'CRM',
        dataSource: 'CUSTOMERS',
        columns: [
          { field: 'name', label: 'Nom', type: 'string', visible: true },
          { field: 'segment', label: 'Segment', type: 'string', visible: true },
          { field: 'status', label: 'Statut', type: 'string', visible: true },
          { field: 'revenue', label: 'Chiffre d\'affaires', type: 'currency', visible: true, aggregate: 'sum' },
          { field: 'lastContact', label: 'Dernier contact', type: 'date', visible: true }
        ],
        filters: [],
        groupBy: ['segment'],
        orderBy: ['revenue']
      },
      {
        id: 'inventory_report',
        name: 'Rapport d\'Inventaire',
        description: 'État des stocks et mouvements',
        category: 'INVENTORY',
        dataSource: 'INVENTORY',
        columns: [
          { field: 'product', label: 'Produit', type: 'string', visible: true },
          { field: 'category', label: 'Catégorie', type: 'string', visible: true },
          { field: 'quantity', label: 'Quantité', type: 'number', visible: true },
          { field: 'value', label: 'Valeur', type: 'currency', visible: true, aggregate: 'sum' },
          { field: 'status', label: 'Statut', type: 'string', visible: true }
        ],
        filters: [],
        groupBy: ['category'],
        orderBy: ['value']
      }
    ];
  }

  // Gestion des templates
  selectTemplate(template: ReportTemplate): void {
    this.selectedTemplate = template;
    this.reportForm.patchValue({
      name: template.name,
      description: template.description,
      category: template.category,
      dataSource: template.dataSource,
      chartType: template.chartType || 'TABLE'
    });
    this.updateDisplayedColumns();
  }

  createNewReport(): void {
    this.selectedTemplate = null;
    this.reportForm.reset();
    this.reportForm.patchValue({ chartType: 'TABLE' });
    this.reportData = null;
    this.dataSource.data = [];
  }

  saveReport(): void {
    if (this.reportForm.invalid) {
      this.notificationService.showWarning('Veuillez remplir tous les champs obligatoires');
      return;
    }

    const reportData = this.reportForm.value;
    if (this.selectedTemplate) {
      // Mise à jour d'un template existant
      this.selectedTemplate.name = reportData.name;
      this.selectedTemplate.description = reportData.description;
      this.selectedTemplate.category = reportData.category;
      this.selectedTemplate.dataSource = reportData.dataSource;
      this.selectedTemplate.chartType = reportData.chartType;
    } else {
      // Création d'un nouveau template
      const newTemplate: ReportTemplate = {
        id: `custom_${Date.now()}`,
        name: reportData.name,
        description: reportData.description,
        category: reportData.category,
        dataSource: reportData.dataSource,
        columns: [],
        filters: [],
        groupBy: [],
        orderBy: [],
        chartType: reportData.chartType
      };
      this.reportTemplates.push(newTemplate);
      this.selectedTemplate = newTemplate;
    }

    this.notificationService.showSuccess('Rapport sauvegardé avec succès');
  }

  // Gestion des colonnes
  addColumn(): void {
    if (!this.selectedTemplate) return;

    const newColumn: ReportColumn = {
      field: '',
      label: '',
      type: 'string',
      visible: true
    };
    this.selectedTemplate.columns.push(newColumn);
    this.updateDisplayedColumns();
  }

  removeColumn(index: number): void {
    if (!this.selectedTemplate) return;
    this.selectedTemplate.columns.splice(index, 1);
    this.updateDisplayedColumns();
  }

  updateDisplayedColumns(): void {
    if (!this.selectedTemplate) {
      this.displayedColumns = [];
      return;
    }
    this.displayedColumns = this.selectedTemplate.columns
      .filter(col => col.visible)
      .map(col => col.field);
  }

  // Gestion des filtres
  addFilter(): void {
    if (!this.selectedTemplate) return;

    const filterData = this.filterForm.value;
    const newFilter: ReportFilter = {
      field: filterData.field,
      label: filterData.field,
      type: 'string',
      operator: filterData.operator,
      value: filterData.value
    };
    this.selectedTemplate.filters.push(newFilter);
    this.filterForm.reset();
  }

  removeFilter(index: number): void {
    if (!this.selectedTemplate) return;
    this.selectedTemplate.filters.splice(index, 1);
  }

  // Génération de rapport
  generateReport(): void {
    if (!this.selectedTemplate) {
      this.notificationService.showWarning('Veuillez sélectionner ou créer un rapport');
      return;
    }

    this.isLoading = true;
    this.loadingService.show();

    // Simulation de génération de données
    setTimeout(() => {
      this.reportData = this.generateMockData();
      this.dataSource.data = this.reportData.rows;
      this.isLoading = false;
      this.loadingService.hide();
      this.notificationService.showSuccess('Rapport généré avec succès');
    }, 2000);
  }

  private generateMockData(): ReportData {
    const columns = this.selectedTemplate!.columns.filter(col => col.visible).map(col => col.field);
    const rows = [];

    // Génération de données mock basées sur le type de rapport
    for (let i = 0; i < 50; i++) {
      const row: any = {};
      columns.forEach(column => {
        switch (column) {
          case 'date':
            row[column] = new Date(2024, Math.floor(Math.random() * 12), Math.floor(Math.random() * 28));
            break;
          case 'account':
            row[column] = `Compte ${i + 1}`;
            break;
          case 'debit':
          case 'credit':
          case 'revenue':
          case 'value':
          case 'balance':
            row[column] = Math.floor(Math.random() * 10000) + 1000;
            break;
          case 'quantity':
            row[column] = Math.floor(Math.random() * 100) + 1;
            break;
          case 'name':
            row[column] = `Client ${i + 1}`;
            break;
          case 'segment':
            row[column] = ['Premium', 'Standard', 'Basic'][Math.floor(Math.random() * 3)];
            break;
          case 'status':
            row[column] = ['Actif', 'Inactif', 'En attente'][Math.floor(Math.random() * 3)];
            break;
          case 'category':
            row[column] = ['Catégorie A', 'Catégorie B', 'Catégorie C'][Math.floor(Math.random() * 3)];
            break;
          case 'product':
            row[column] = `Produit ${i + 1}`;
            break;
          default:
            row[column] = `Valeur ${i + 1}`;
        }
      });
      rows.push(row);
    }

    return {
      columns,
      rows,
      summary: this.calculateSummary(rows),
      totalRows: rows.length
    };
  }

  private calculateSummary(rows: any[]): any {
    const summary: any = {};
    
    this.selectedTemplate!.columns.forEach(col => {
      if (col.aggregate && col.type === 'currency') {
        const values = rows.map(row => row[col.field] || 0);
        switch (col.aggregate) {
          case 'sum':
            summary[col.field] = values.reduce((a, b) => a + b, 0);
            break;
          case 'avg':
            summary[col.field] = values.reduce((a, b) => a + b, 0) / values.length;
            break;
          case 'count':
            summary[col.field] = values.length;
            break;
          case 'min':
            summary[col.field] = Math.min(...values);
            break;
          case 'max':
            summary[col.field] = Math.max(...values);
            break;
        }
      }
    });

    return summary;
  }

  // Export
  exportReport(format: 'excel' | 'pdf' | 'csv'): void {
    if (!this.reportData) {
      this.notificationService.showWarning('Aucun rapport à exporter');
      return;
    }

    this.notificationService.showInfo(`Export ${format.toUpperCase()} en cours...`);
    // Logique d'export à implémenter
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

  getColumnType(field: string): string {
    const column = this.selectedTemplate?.columns.find(col => col.field === field);
    return column?.type || 'string';
  }

  getColumnLabel(field: string): string {
    const column = this.selectedTemplate?.columns.find(col => col.field === field);
    return column?.label || field;
  }

  getSummaryKeys(): string[] {
    if (!this.reportData?.summary) return [];
    return Object.keys(this.reportData.summary);
  }
}
