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
import { MatBadgeModule } from '@angular/material/badge';
import { MatExpansionModule } from '@angular/material/expansion';
import { Subject, takeUntil, debounceTime, distinctUntilChanged } from 'rxjs';

import { AuthService } from '../../../shared/services/auth';
import { NotificationService } from '../../../shared/services/notification';
import { LoadingService } from '../../../shared/services/loading';
import { AccountService, Account } from '../../../shared/services/account.service';
import { User } from '../../../shared/models/user';

@Component({
  selector: 'app-accounts',
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
    MatExpansionModule
  ],
  templateUrl: './accounts.html',
  styleUrl: './accounts.scss'
})
export class AccountsComponent implements OnInit, OnDestroy {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  currentUser: User | null = null;
  isLoading = false;
  private destroy$ = new Subject<void>();

  // Données
  accounts: Account[] = [];
  filteredAccounts: MatTableDataSource<Account> = new MatTableDataSource<Account>();
  
  // Colonnes du tableau
  displayedColumns: string[] = [
    'select',
    'accountCode',
    'accountName',
    'accountType',
    'accountCategory',
    'accountLevel',
    'isGroupAccount',
    'currentDebitBalance',
    'currentCreditBalance',
    'isActive',
    'actions'
  ];

  // Filtres
  filterForm: FormGroup;
  filterExpanded = false;
  
  // Sélection
  selectedAccounts: Set<number> = new Set();
  selectAll = false;

  // Pagination et tri
  pageSize = 25;
  pageSizeOptions = [10, 25, 50, 100];

  // Types de comptes
  accountTypes = [
    { value: 'ASSET', label: 'Actif', color: 'blue' },
    { value: 'LIABILITY', label: 'Passif', color: 'red' },
    { value: 'EQUITY', label: 'Capitaux propres', color: 'green' },
    { value: 'REVENUE', label: 'Produit', color: 'orange' },
    { value: 'EXPENSE', label: 'Charge', color: 'purple' },
    { value: 'OFF_BALANCE', label: 'Hors bilan', color: 'gray' }
  ];

  // Catégories de comptes
  accountCategories = [
    { value: 'CURRENT_ASSET', label: 'Actif circulant', color: 'lightblue' },
    { value: 'FIXED_ASSET', label: 'Actif immobilisé', color: 'blue' },
    { value: 'CURRENT_LIABILITY', label: 'Passif circulant', color: 'lightcoral' },
    { value: 'LONG_TERM_LIABILITY', label: 'Passif immobilisé', color: 'red' },
    { value: 'EQUITY', label: 'Capitaux propres', color: 'green' },
    { value: 'REVENUE', label: 'Produit', color: 'orange' },
    { value: 'EXPENSE', label: 'Charge', color: 'purple' },
    { value: 'OFF_BALANCE', label: 'Hors bilan', color: 'gray' }
  ];

  // Devises
  currencies = ['EUR', 'USD', 'CHF', 'GBP'];

  // Comptes parents
  parentAccounts: Account[] = [];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private notificationService: NotificationService,
    private loadingService: LoadingService,
    private accountService: AccountService,
    private router: Router,
    private dialog: MatDialog
  ) {
    this.filterForm = this.fb.group({
      search: [''],
      accountType: [''],
      accountCategory: [''],
      currency: [''],
      isActive: [''],
      isGroupAccount: ['']
    });
  }

  ngOnInit(): void {
    this.loadAccounts();
    this.setupFilterSubscription();
    this.setupUserSubscription();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadAccounts(): void {
    this.isLoading = true;
    this.loadingService.show();

    // Récupérer les comptes depuis l'API
    this.accountService.getAllAccounts()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (accounts) => {
          this.accounts = accounts;
          this.parentAccounts = this.accounts.filter(acc => acc.accountLevel === 1);
          this.filteredAccounts.data = this.accounts;
          this.filteredAccounts.paginator = this.paginator;
          this.filteredAccounts.sort = this.sort;
          
          this.isLoading = false;
          this.loadingService.hide();
          
          this.notificationService.showSuccess(`${accounts.length} comptes chargés`);
        },
        error: (error) => {
          console.error('Erreur lors du chargement des comptes:', error);
          this.isLoading = false;
          this.loadingService.hide();
          this.notificationService.showError('Erreur lors du chargement des comptes');
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
    let filtered = [...this.accounts];

    if (filters.search) {
      const searchTerm = filters.search.toLowerCase();
      filtered = filtered.filter(account =>
        account.accountCode.toLowerCase().includes(searchTerm) ||
        account.accountName.toLowerCase().includes(searchTerm) ||
        account.accountDescription?.toLowerCase().includes(searchTerm)
      );
    }

    if (filters.accountType) {
      filtered = filtered.filter(account => account.accountType === filters.accountType);
    }

    if (filters.accountCategory) {
      filtered = filtered.filter(account => account.accountCategory === filters.accountCategory);
    }

    if (filters.isActive !== '') {
      filtered = filtered.filter(account => account.isActive === filters.isActive);
    }

    if (filters.isGroupAccount !== '') {
      filtered = filtered.filter(account => account.isGroupAccount === filters.isGroupAccount);
    }

    this.filteredAccounts.data = filtered;
  }

  // Gestion de la sélection
  toggleSelectAll(): void {
    if (this.selectAll) {
      this.selectedAccounts.clear();
    } else {
      this.filteredAccounts.data.forEach(account => {
        this.selectedAccounts.add(account.id);
      });
    }
    this.selectAll = !this.selectAll;
  }

  toggleAccountSelection(accountId: number): void {
    if (this.selectedAccounts.has(accountId)) {
      this.selectedAccounts.delete(accountId);
    } else {
      this.selectedAccounts.add(accountId);
    }
    this.updateSelectAllState();
  }

  private updateSelectAllState(): void {
    const totalAccounts = this.filteredAccounts.data.length;
    const selectedCount = this.selectedAccounts.size;
    this.selectAll = totalAccounts > 0 && selectedCount === totalAccounts;
  }

  // Actions
  addAccount(): void {
    this.router.navigate(['/accounting/accounts/new']);
  }

  editAccount(account: Account): void {
    this.router.navigate(['/accounting/accounts/edit', account.id]);
  }

  viewAccount(account: Account): void {
    this.router.navigate(['/accounting/accounts/view', account.id]);
  }

  deleteAccount(account: Account): void {
    this.accountService.deleteAccount(account.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.notificationService.showSuccess(`Compte ${account.accountCode} supprimé avec succès`);
          this.loadAccounts(); // Recharger la liste
        },
        error: (error) => {
          console.error('Erreur lors de la suppression:', error);
          this.notificationService.showError('Erreur lors de la suppression du compte');
        }
      });
  }

  toggleAccountStatus(account: Account): void {
    this.accountService.toggleAccountStatus(account.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (updatedAccount) => {
          this.notificationService.showSuccess(`Statut du compte ${account.accountCode} modifié avec succès`);
          // Mettre à jour la liste locale
          const index = this.accounts.findIndex(a => a.id === account.id);
          if (index !== -1) {
            this.accounts[index] = updatedAccount;
            this.filteredAccounts.data = [...this.accounts];
          }
        },
        error: (error) => {
          console.error('Erreur lors du changement de statut:', error);
          this.notificationService.showError('Erreur lors du changement de statut');
        }
      });
  }

  deleteSelectedAccounts(): void {
    if (this.selectedAccounts.size === 0) {
      this.notificationService.showWarning('Aucun compte sélectionné');
      return;
    }

    const selectedIds = Array.from(this.selectedAccounts);
    let completed = 0;
    let errors = 0;

    selectedIds.forEach(accountId => {
      this.accountService.deleteAccount(accountId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            completed++;
            if (completed + errors === selectedIds.length) {
              if (errors === 0) {
                this.notificationService.showSuccess(`${completed} comptes supprimés avec succès`);
                this.loadAccounts(); // Recharger la liste
              } else {
                this.notificationService.showWarning(`${completed} comptes supprimés, ${errors} erreurs`);
              }
            }
          },
          error: (error) => {
            console.error('Erreur lors de la suppression:', error);
            errors++;
            if (completed + errors === selectedIds.length) {
              this.notificationService.showWarning(`${completed} comptes supprimés, ${errors} erreurs`);
            }
          }
        });
    });
  }

  exportAccounts(): void {
    this.notificationService.showInfo('Export des comptes en cours...');
    // TODO: Implémenter l'export
  }

  importAccounts(): void {
    this.notificationService.showInfo('Import des comptes en cours...');
    // TODO: Implémenter l'import
  }

  clearFilters(): void {
    this.filterForm.reset();
    this.applyFilters();
  }

  toggleFilterPanel(): void {
    this.filterExpanded = !this.filterExpanded;
  }

  getAccountTypeColor(type: string): string {
    return this.accountService.getTypeColor(type);
  }

  getAccountTypeIcon(type: string): string {
    return this.accountService.getTypeIcon(type);
  }

  getAccountTypeLabel(type: string): string {
    return this.accountService.getTypeLabel(type);
  }

  getAccountCategoryColor(category: string): string {
    return this.accountService.getCategoryColor(category);
  }

  getAccountCategoryIcon(category: string): string {
    return this.accountService.getCategoryIcon(category);
  }

  getAccountCategoryLabel(category: string): string {
    return this.accountService.getCategoryLabel(category);
  }

  formatBalance(balance: number, currency: string = 'EUR'): string {
    return this.accountService.formatBalance(balance, currency);
  }

  formatDate(date: string): string {
    return new Intl.DateTimeFormat('fr-FR').format(new Date(date));
  }

  isAccountBalanced(account: Account): boolean {
    return this.accountService.isAccountBalanced(account);
  }

  getIndentation(level: number): string {
    return this.accountService.getIndentation(level);
  }

  getAccountStatusColor(isActive: boolean): string {
    return isActive ? 'green' : 'red';
  }

  getAccountStatusIcon(isActive: boolean): string {
    return isActive ? 'check_circle' : 'cancel';
  }

  getAccountStatusLabel(isActive: boolean): string {
    return isActive ? 'Actif' : 'Inactif';
  }
}