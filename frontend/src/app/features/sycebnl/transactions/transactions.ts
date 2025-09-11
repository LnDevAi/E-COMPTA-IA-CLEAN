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
import { Subject, takeUntil, debounceTime, distinctUntilChanged } from 'rxjs';

import { AuthService } from '../../../shared/services/auth';
import { NotificationService } from '../../../shared/services/notification';
import { LoadingService } from '../../../shared/services/loading';
import { SycebnlTransactionService, SycebnlTransaction } from '../../../shared/services/sycebnl-transaction.service';
import { User } from '../../../shared/models/user';

// Utilisation de SycebnlTransaction pour les transactions

@Component({
  selector: 'app-transactions',
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
    MatBadgeModule
  ],
  templateUrl: './transactions.html',
  styleUrl: './transactions.scss'
})
export class TransactionsComponent implements OnInit, OnDestroy {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  currentUser: User | null = null;
  isLoading = false;
  private destroy$ = new Subject<void>();

  // Données
  transactions: SycebnlTransaction[] = [];
  filteredTransactions: MatTableDataSource<SycebnlTransaction> = new MatTableDataSource<SycebnlTransaction>();
  
  // Colonnes du tableau
  displayedColumns: string[] = [
    'select',
    'transactionNumber',
    'transactionDate',
    'description',
    'accountNumber',
    'thirdPartyName',
    'amount',
    'transactionType',
    'status',
    'actions'
  ];

  // Filtres
  filterForm: FormGroup;
  filterExpanded = false;
  
  // Sélection
  selectedTransactions: Set<number> = new Set();
  selectAll = false;

  // Pagination et tri
  pageSize = 25;
  pageSizeOptions = [10, 25, 50, 100];

  // Types de transactions (utilise les types de SycebnlTransaction)
  transactionTypes = [
    { value: 'INCOME', label: 'Recette', color: 'green' },
    { value: 'EXPENSE', label: 'Dépense', color: 'red' },
    { value: 'TRANSFER', label: 'Virement', color: 'blue' },
    { value: 'ADJUSTMENT', label: 'Ajustement', color: 'orange' }
  ];

  // Statuts (utilise les statuts de SycebnlTransaction)
  statuses = [
    { value: 'DRAFT', label: 'Brouillon', color: 'gray' },
    { value: 'VALIDATED', label: 'Validée', color: 'green' },
    { value: 'CANCELLED', label: 'Annulée', color: 'red' },
    { value: 'POSTED', label: 'Comptabilisée', color: 'blue' }
  ];

  // Catégories
  categories = [
    'Ventes', 'Achats', 'Salaires', 'Frais généraux', 'Investissements',
    'Prêts', 'Épargne', 'Assurance', 'Fiscalité', 'Autre'
  ];

  // Comptes
  accounts = [
    '411000 - Clients', '401000 - Fournisseurs', '512000 - Banque',
    '531000 - Caisse', '701000 - Ventes', '601000 - Achats',
    '641000 - Salaires', '625000 - Frais généraux', '218000 - Matériel'
  ];

  // Devises
  currencies = ['EUR', 'USD', 'CHF', 'GBP'];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private notificationService: NotificationService,
    private loadingService: LoadingService,
    private sycebnlTransactionService: SycebnlTransactionService,
    private router: Router,
    private dialog: MatDialog
  ) {
    this.filterForm = this.fb.group({
      search: [''],
      type: [''],
      status: [''],
      category: [''],
      account: [''],
      dateRange: this.fb.group({
        start: [null],
        end: [null]
      })
    });
  }

  ngOnInit(): void {
    this.loadTransactions();
    this.setupFilterSubscription();
    this.setupUserSubscription();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadTransactions(): void {
    this.isLoading = true;
    this.loadingService.show();

    this.sycebnlTransactionService.getAllSycebnlTransactions()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (transactions: SycebnlTransaction[]) => {
          this.transactions = transactions;
          this.filteredTransactions.data = this.transactions;
          this.filteredTransactions.paginator = this.paginator;
          this.filteredTransactions.sort = this.sort;
          
          this.isLoading = false;
          this.loadingService.hide();
          
          this.notificationService.showSuccess(`${this.transactions.length} transactions chargées`);
        },
        error: (error: any) => {
          console.error('Erreur lors du chargement des transactions:', error);
          this.isLoading = false;
          this.loadingService.hide();
          this.notificationService.showError('Erreur lors du chargement des transactions');
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
    let filtered = [...this.transactions];

    if (filters.search) {
      const searchTerm = filters.search.toLowerCase();
      filtered = filtered.filter(transaction =>
        transaction.transactionNumber.toLowerCase().includes(searchTerm) ||
        transaction.description.toLowerCase().includes(searchTerm) ||
        transaction.reference?.toLowerCase().includes(searchTerm) ||
        transaction.thirdPartyName?.toLowerCase().includes(searchTerm)
      );
    }

    if (filters.type) {
      filtered = filtered.filter(transaction => transaction.transactionType === filters.type);
    }

    if (filters.status) {
      filtered = filtered.filter(transaction => transaction.status === filters.status);
    }

    if (filters.account) {
      filtered = filtered.filter(transaction => 
        transaction.accountNumber.includes(filters.account)
      );
    }

    if (filters.dateRange.start) {
      filtered = filtered.filter(transaction => transaction.transactionDate >= filters.dateRange.start);
    }
    if (filters.dateRange.end) {
      filtered = filtered.filter(transaction => transaction.transactionDate <= filters.dateRange.end);
    }

    this.filteredTransactions.data = filtered;
  }

  // Gestion de la sélection
  toggleSelectAll(): void {
    if (this.selectAll) {
      this.selectedTransactions.clear();
    } else {
      this.filteredTransactions.data.forEach(transaction => {
        if (transaction.id) {
          this.selectedTransactions.add(transaction.id);
        }
      });
    }
    this.selectAll = !this.selectAll;
  }

  toggleTransactionSelection(transactionId: number | undefined): void {
    if (!transactionId) return;
    
    if (this.selectedTransactions.has(transactionId)) {
      this.selectedTransactions.delete(transactionId);
    } else {
      this.selectedTransactions.add(transactionId);
    }
    this.updateSelectAllState();
  }

  private updateSelectAllState(): void {
    const totalTransactions = this.filteredTransactions.data.length;
    const selectedCount = this.selectedTransactions.size;
    this.selectAll = totalTransactions > 0 && selectedCount === totalTransactions;
  }

  // Actions
  addTransaction(): void {
    this.router.navigate(['/sycebnl/transactions/new']);
  }

  editTransaction(transaction: SycebnlTransaction): void {
    this.router.navigate(['/sycebnl/transactions/edit', transaction.id]);
  }

  viewTransaction(transaction: SycebnlTransaction): void {
    this.router.navigate(['/sycebnl/transactions/view', transaction.id]);
  }

  approveTransaction(transaction: SycebnlTransaction): void {
    this.notificationService.showInfo(`Approbation de la transaction ${transaction.transactionNumber}`);
  }

  rejectTransaction(transaction: SycebnlTransaction): void {
    this.notificationService.showInfo(`Rejet de la transaction ${transaction.transactionNumber}`);
  }

  deleteTransaction(transaction: SycebnlTransaction): void {
    this.notificationService.showInfo(`Suppression de ${transaction.transactionNumber}`);
  }

  deleteSelectedTransactions(): void {
    if (this.selectedTransactions.size === 0) {
      this.notificationService.showWarning('Aucune transaction sélectionnée');
      return;
    }
    this.notificationService.showInfo(`${this.selectedTransactions.size} transactions sélectionnées pour suppression`);
  }

  exportTransactions(): void {
    this.notificationService.showInfo('Export des transactions en cours...');
  }

  clearFilters(): void {
    this.filterForm.reset();
    this.applyFilters();
  }

  toggleFilterPanel(): void {
    this.filterExpanded = !this.filterExpanded;
  }

  getTypeColor(type: string): string {
    const typeObj = this.transactionTypes.find(t => t.value === type);
    return typeObj ? typeObj.color : 'gray';
  }


  getStatusColor(status: string): string {
    const statusObj = this.statuses.find(s => s.value === status);
    return statusObj ? statusObj.color : 'gray';
  }

  getStatusIcon(status: string): string {
    switch (status) {
      case 'DRAFT': return 'edit';
      case 'VALIDATED': return 'check_circle';
      case 'CANCELLED': return 'cancel';
      case 'POSTED': return 'done_all';
      default: return 'help';
    }
  }

  getTypeIcon(type: string): string {
    switch (type) {
      case 'INCOME': return 'trending_up';
      case 'EXPENSE': return 'trending_down';
      case 'TRANSFER': return 'swap_horiz';
      case 'ADJUSTMENT': return 'tune';
      default: return 'help';
    }
  }

  formatCurrency(amount: number, currency: string = 'EUR'): string {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: currency
    }).format(amount);
  }

  formatDate(date: Date): string {
    return new Intl.DateTimeFormat('fr-FR').format(date);
  }
}
