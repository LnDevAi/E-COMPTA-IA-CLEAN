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
import { MatTabsModule } from '@angular/material/tabs';
import { Subject, takeUntil, debounceTime, distinctUntilChanged } from 'rxjs';

import { AuthService } from '../../../shared/services/auth';
import { NotificationService } from '../../../shared/services/notification';
import { LoadingService } from '../../../shared/services/loading';
import { 
  SycebnlFinancialStatementsService, 
  EtatFinancierSycebnl 
} from '../../../shared/services/sycebnl-financial-statements.service';
import { User } from '../../../shared/models/user';

@Component({
  selector: 'app-financial-statements-list',
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
    MatProgressBarModule,
    MatTabsModule
  ],
  templateUrl: './financial-statements-list.html',
  styleUrl: './financial-statements-list.scss'
})
export class FinancialStatementsList implements OnInit, OnDestroy {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  currentUser: User | null = null;
  isLoading = false;
  private destroy$ = new Subject<void>();

  // Données
  financialStatements: EtatFinancierSycebnl[] = [];
  filteredFinancialStatements: MatTableDataSource<EtatFinancierSycebnl> = new MatTableDataSource<EtatFinancierSycebnl>();
  
  // Colonnes du tableau
  displayedColumns: string[] = [
    'select',
    'typeEtat',
    'typeSysteme',
    'dateArrete',
    'statut',
    'totalActif',
    'totalPassif',
    'resultatNet',
    'equilibre',
    'actions'
  ];

  // Filtres
  filterForm: FormGroup;
  filterExpanded = false;
  
  // Sélection
  selectedStatements: Set<number> = new Set();
  selectAll = false;

  // Pagination et tri
  pageSize = 25;
  pageSizeOptions = [10, 25, 50, 100];

  // Types d'états financiers disponibles
  financialStatementTypes = [
    { value: 'BILAN', label: 'Bilan', color: 'blue', icon: 'account_balance' },
    { value: 'COMPTE_RESULTAT', label: 'Compte de Résultat', color: 'green', icon: 'trending_up' },
    { value: 'TABLEAU_FLUX', label: 'Tableau des Flux', color: 'purple', icon: 'swap_horiz' },
    { value: 'RECETTES_DEPENSES', label: 'Recettes/Dépenses (SMT)', color: 'orange', icon: 'receipt' },
    { value: 'SITUATION_TRESORERIE', label: 'Situation Trésorerie (SMT)', color: 'teal', icon: 'account_balance_wallet' },
    { value: 'ANNEXES', label: 'Notes Annexes', color: 'brown', icon: 'description' }
  ];

  // Systèmes comptables disponibles
  systemTypes = [
    { value: 'NORMAL', label: 'Système Normal', color: 'green' },
    { value: 'MINIMAL', label: 'Système Minimal', color: 'orange' }
  ];

  // Statuts disponibles
  statuses = [
    { value: 'BROUILLON', label: 'Brouillon', color: 'gray' },
    { value: 'VALIDE', label: 'Validé', color: 'green' },
    { value: 'CLOTURE', label: 'Clôturé', color: 'blue' }
  ];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private notificationService: NotificationService,
    private loadingService: LoadingService,
    private financialStatementsService: SycebnlFinancialStatementsService,
    private router: Router,
    private dialog: MatDialog
  ) {
    this.filterForm = this.fb.group({
      search: [''],
      typeEtat: [''],
      typeSysteme: [''],
      statut: [''],
      dateFrom: [''],
      dateTo: ['']
    });
  }

  ngOnInit(): void {
    this.loadFinancialStatements();
    this.setupFilterSubscription();
    this.setupUserSubscription();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadFinancialStatements(): void {
    this.isLoading = true;
    this.loadingService.show();

    this.financialStatementsService.getAllFinancialStatements()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (statements: EtatFinancierSycebnl[]) => {
          this.financialStatements = statements;
          this.filteredFinancialStatements.data = this.financialStatements;
          this.filteredFinancialStatements.paginator = this.paginator;
          this.filteredFinancialStatements.sort = this.sort;
          
          this.isLoading = false;
          this.loadingService.hide();
          
          this.notificationService.showSuccess(`${this.financialStatements.length} états financiers chargés`);
        },
        error: (error: any) => {
          console.error('Erreur lors du chargement des états financiers:', error);
          this.isLoading = false;
          this.loadingService.hide();
          this.notificationService.showError('Erreur lors du chargement des états financiers');
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
    let filtered = [...this.financialStatements];

    if (filters.search) {
      const searchTerm = filters.search.toLowerCase();
      filtered = filtered.filter(statement =>
        statement.typeEtat?.toLowerCase().includes(searchTerm) ||
        statement.typeSysteme?.toLowerCase().includes(searchTerm) ||
        statement.generePar?.toLowerCase().includes(searchTerm)
      );
    }

    if (filters.typeEtat) {
      filtered = filtered.filter(statement => statement.typeEtat === filters.typeEtat);
    }

    if (filters.typeSysteme) {
      filtered = filtered.filter(statement => statement.typeSysteme === filters.typeSysteme);
    }

    if (filters.statut) {
      filtered = filtered.filter(statement => statement.statut === filters.statut);
    }

    if (filters.dateFrom) {
      filtered = filtered.filter(statement => 
        new Date(statement.dateArrete) >= new Date(filters.dateFrom)
      );
    }

    if (filters.dateTo) {
      filtered = filtered.filter(statement => 
        new Date(statement.dateArrete) <= new Date(filters.dateTo)
      );
    }

    this.filteredFinancialStatements.data = filtered;
  }

  // Sélection
  toggleSelectAll(): void {
    if (this.selectAll) {
      this.selectedStatements.clear();
    } else {
      this.filteredFinancialStatements.data.forEach(statement => {
        if (statement.id) {
          this.selectedStatements.add(statement.id);
        }
      });
    }
    this.selectAll = !this.selectAll;
  }

  toggleStatementSelection(statementId: number | undefined): void {
    if (!statementId) return;
    
    if (this.selectedStatements.has(statementId)) {
      this.selectedStatements.delete(statementId);
    } else {
      this.selectedStatements.add(statementId);
    }
    this.updateSelectAllState();
  }

  private updateSelectAllState(): void {
    const totalStatements = this.filteredFinancialStatements.data.length;
    const selectedCount = this.selectedStatements.size;
    this.selectAll = totalStatements > 0 && selectedCount === totalStatements;
  }

  // Actions
  generateNewStatement(): void {
    this.router.navigate(['/sycebnl/financial-statements/generate']);
  }

  viewStatement(statement: EtatFinancierSycebnl): void {
    this.router.navigate(['/sycebnl/financial-statements/view', statement.id]);
  }

  editStatement(statement: EtatFinancierSycebnl): void {
    this.router.navigate(['/sycebnl/financial-statements/edit', statement.id]);
  }

  validateStatement(statement: EtatFinancierSycebnl): void {
    if (!statement.id) return;

    this.loadingService.show();
    this.financialStatementsService.validateFinancialStatement(statement.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          this.loadingService.hide();
          if (result.valide) {
            this.notificationService.showSuccess('État financier validé avec succès');
            this.loadFinancialStatements(); // Recharger pour mettre à jour le statut
          } else {
            this.notificationService.showWarning(`Validation échouée: ${result.erreurs.join(', ')}`);
          }
        },
        error: (error) => {
          this.loadingService.hide();
          this.notificationService.showError('Erreur lors de la validation');
        }
      });
  }

  exportStatement(statement: EtatFinancierSycebnl, format: string): void {
    if (!statement.id) return;

    this.loadingService.show();
    this.financialStatementsService.exportFinancialStatement(statement.id, format)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (blob) => {
          this.loadingService.hide();
          const filename = `${statement.typeEtat}_${statement.typeSysteme}_${statement.dateArrete}.${format.toLowerCase()}`;
          this.financialStatementsService.downloadFile(blob, filename);
          this.notificationService.showSuccess(`Export ${format} réussi`);
        },
        error: (error) => {
          this.loadingService.hide();
          this.notificationService.showError('Erreur lors de l\'export');
        }
      });
  }

  deleteStatement(statement: EtatFinancierSycebnl): void {
    if (!statement.id) return;

    this.notificationService.showInfo(`Suppression de ${statement.typeEtat} - ${statement.typeSysteme}`);
  }

  deleteSelectedStatements(): void {
    if (this.selectedStatements.size === 0) {
      this.notificationService.showWarning('Aucun état financier sélectionné');
      return;
    }
    this.notificationService.showInfo(`${this.selectedStatements.size} états financiers sélectionnés pour suppression`);
  }

  exportSelectedStatements(format: string): void {
    if (this.selectedStatements.size === 0) {
      this.notificationService.showWarning('Aucun état financier sélectionné');
      return;
    }
    this.notificationService.showInfo(`Export ${format} de ${this.selectedStatements.size} états financiers en cours...`);
  }

  clearFilters(): void {
    this.filterForm.reset();
    this.applyFilters();
  }

  toggleFilterPanel(): void {
    this.filterExpanded = !this.filterExpanded;
  }

  // Méthodes utilitaires
  getFinancialStatementTypeColor(type: string): string {
    const typeObj = this.financialStatementTypes.find(t => t.value === type);
    return typeObj ? typeObj.color : 'gray';
  }

  getFinancialStatementTypeIcon(type: string): string {
    const typeObj = this.financialStatementTypes.find(t => t.value === type);
    return typeObj ? typeObj.icon : 'description';
  }

  getSystemTypeColor(system: string): string {
    const systemObj = this.systemTypes.find(s => s.value === system);
    return systemObj ? systemObj.color : 'gray';
  }

  getStatusColor(status: string): string {
    const statusObj = this.statuses.find(s => s.value === status);
    return statusObj ? statusObj.color : 'gray';
  }

  getStatusIcon(status: string): string {
    switch (status) {
      case 'BROUILLON': return 'edit';
      case 'VALIDE': return 'check_circle';
      case 'CLOTURE': return 'lock';
      default: return 'help';
    }
  }

  formatAmount(amount: number | undefined): string {
    if (amount === undefined || amount === null) return '-';
    return this.financialStatementsService.formatAmount(amount);
  }

  formatDate(date: string): string {
    return this.financialStatementsService.formatDate(date);
  }

  getEquilibreIcon(equilibre: boolean): string {
    return equilibre ? 'check_circle' : 'cancel';
  }

  getEquilibreColor(equilibre: boolean): string {
    return equilibre ? 'green' : 'red';
  }
}






