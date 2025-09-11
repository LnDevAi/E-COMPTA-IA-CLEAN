import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { CommonModule, NgIf } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatChipsModule } from '@angular/material/chips';
import { MatTreeModule } from '@angular/material/tree';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatDividerModule } from '@angular/material/divider';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTableDataSource } from '@angular/material/table';
import { Subject, takeUntil } from 'rxjs';

import { AuthService } from '../../../shared/services/auth';
import { NotificationService } from '../../../shared/services/notification';
import { LoadingService } from '../../../shared/services/loading';
import { AccountService, Account } from '../../../shared/services/account.service';
import { User } from '../../../shared/models/user';

interface AccountTreeNode {
  id: number;
  code: string;
  name: string;
  type: string;
  parentId?: number;
  level: number;
  isActive: boolean;
  balance: number;
  children?: AccountTreeNode[];
}

@Component({
  selector: 'app-chart-of-accounts',
  standalone: true,
  imports: [
    CommonModule,
    NgIf,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatDialogModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    MatChipsModule,
    MatTreeModule,
    MatExpansionModule,
    MatDividerModule,
    MatCheckboxModule
  ],
  templateUrl: './chart-of-accounts.html',
  styleUrl: './chart-of-accounts.scss'
})
export class ChartOfAccounts implements OnInit, OnDestroy {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  currentUser: User | null = null;
  isLoading = false;
  private destroy$ = new Subject<void>();

  // Formulaires
  accountForm: FormGroup;
  searchForm: FormGroup;

  // Données
  accounts: Account[] = [];
  filteredAccounts: MatTableDataSource<Account> = new MatTableDataSource<Account>();
  accountTree: AccountTreeNode[] = [];
  selectedAccounts: Set<number> = new Set();

  // Colonnes du tableau
  displayedColumns: string[] = [
    'select',
    'accountCode',
    'accountName',
    'accountType',
    'parentCode',
    'balance',
    'isActive',
    'actions'
  ];

  // Types de comptes
  accountTypes = [
    { value: 'ASSET', label: 'Actif' },
    { value: 'LIABILITY', label: 'Passif' },
    { value: 'EQUITY', label: 'Capitaux propres' },
    { value: 'REVENUE', label: 'Produits' },
    { value: 'EXPENSE', label: 'Charges' },
    { value: 'COST_OF_SALES', label: 'Coût des ventes' }
  ];

  // Statuts
  statusOptions = [
    { value: true, label: 'Actif' },
    { value: false, label: 'Inactif' }
  ];

  // Filtres
  filterTypes: string[] = [];
  filterStatus: boolean | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private notificationService: NotificationService,
    private loadingService: LoadingService,
    private accountService: AccountService
  ) {
    this.accountForm = this.fb.group({
      code: ['', [Validators.required, Validators.pattern(/^[0-9]+$/)]],
      name: ['', Validators.required],
      type: ['', Validators.required],
      parentId: [null],
      description: [''],
      isActive: [true]
    });

    this.searchForm = this.fb.group({
      searchTerm: [''],
      type: [''],
      status: ['']
    });
  }

  ngOnInit(): void {
    this.setupUserSubscription();
    this.loadAccounts();
    this.setupSearchSubscription();
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

  private loadAccounts(): void {
    this.isLoading = true;
    this.loadingService.show();

    this.accountService.getAllAccounts()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (accounts: Account[]) => {
          this.accounts = accounts;
          this.filteredAccounts.data = accounts;
          this.buildAccountTree();
          this.isLoading = false;
          this.loadingService.hide();
        },
        error: (error: any) => {
          console.error('Erreur lors du chargement des comptes:', error);
          this.isLoading = false;
          this.loadingService.hide();
          this.notificationService.showError('Erreur lors du chargement des comptes');
        }
      });
  }

  private buildAccountTree(): void {
    const accountMap = new Map<number, AccountTreeNode>();
    const rootNodes: AccountTreeNode[] = [];

    // Créer les nœuds
    this.accounts.forEach(account => {
      const node: AccountTreeNode = {
        id: account.id,
        code: account.accountCode,
        name: account.accountName,
        type: account.accountType,
        parentId: account.parentAccountId,
        level: 0,
        isActive: account.isActive,
        balance: account.currentDebitBalance - account.currentCreditBalance,
        children: []
      };
      accountMap.set(account.id, node);
    });

    // Construire l'arbre
    accountMap.forEach(node => {
      if (node.parentId) {
        const parent = accountMap.get(node.parentId);
        if (parent) {
          parent.children!.push(node);
          node.level = parent.level + 1;
        }
      } else {
        rootNodes.push(node);
      }
    });

    this.accountTree = rootNodes;
  }

  private setupSearchSubscription(): void {
    this.searchForm.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.applyFilters();
      });
  }

  // Filtrage et recherche
  applyFilters(): void {
    const searchTerm = this.searchForm.get('searchTerm')?.value?.toLowerCase() || '';
    const typeFilter = this.searchForm.get('type')?.value || '';
    const statusFilter = this.searchForm.get('status')?.value;

    let filtered = this.accounts.filter(account => {
      const matchesSearch = !searchTerm || 
        account.accountCode.toLowerCase().includes(searchTerm) ||
        account.accountName.toLowerCase().includes(searchTerm);

      const matchesType = !typeFilter || account.accountType === typeFilter;
      
      const matchesStatus = statusFilter === '' || 
        (statusFilter === 'true' && account.isActive) ||
        (statusFilter === 'false' && !account.isActive);

      return matchesSearch && matchesType && matchesStatus;
    });

    this.filteredAccounts.data = filtered;
  }

  clearFilters(): void {
    this.searchForm.reset();
    this.filteredAccounts.data = this.accounts;
  }

  // Sélection des comptes
  toggleAccountSelection(accountId: number): void {
    if (this.selectedAccounts.has(accountId)) {
      this.selectedAccounts.delete(accountId);
    } else {
      this.selectedAccounts.add(accountId);
    }
  }

  selectAllAccounts(): void {
    this.selectedAccounts.clear();
    this.filteredAccounts.data.forEach(account => {
      if (account.id) {
        this.selectedAccounts.add(account.id);
      }
    });
  }

  clearSelection(): void {
    this.selectedAccounts.clear();
  }

  // Actions CRUD
  createAccount(): void {
    if (this.accountForm.invalid) {
      this.notificationService.showWarning('Veuillez remplir tous les champs obligatoires');
      return;
    }

    this.isLoading = true;
    this.loadingService.show();

    const accountData = this.accountForm.value;
    this.accountService.createAccount(accountData)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (account: Account) => {
          this.notificationService.showSuccess('Compte créé avec succès');
          this.loadAccounts();
          this.accountForm.reset();
          this.isLoading = false;
          this.loadingService.hide();
        },
        error: (error: any) => {
          console.error('Erreur lors de la création du compte:', error);
          this.isLoading = false;
          this.loadingService.hide();
          this.notificationService.showError('Erreur lors de la création du compte');
        }
      });
  }

  editAccount(account: Account): void {
    this.accountForm.patchValue({
      code: account.accountCode,
      name: account.accountName,
      type: account.accountType,
      parentId: account.parentAccountId,
      description: account.accountDescription,
      isActive: account.isActive
    });
  }

  updateAccount(): void {
    if (this.accountForm.invalid) {
      this.notificationService.showWarning('Veuillez remplir tous les champs obligatoires');
      return;
    }

    // Logique de mise à jour
    this.notificationService.showInfo('Mise à jour du compte en cours...');
  }

  deleteAccount(account: Account): void {
    if (confirm(`Êtes-vous sûr de vouloir supprimer le compte ${account.accountCode} - ${account.accountName} ?`)) {
      this.isLoading = true;
      this.loadingService.show();

      this.accountService.deleteAccount(account.id!)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.notificationService.showSuccess('Compte supprimé avec succès');
            this.loadAccounts();
            this.isLoading = false;
            this.loadingService.hide();
          },
          error: (error: any) => {
            console.error('Erreur lors de la suppression du compte:', error);
            this.isLoading = false;
            this.loadingService.hide();
            this.notificationService.showError('Erreur lors de la suppression du compte');
          }
        });
    }
  }

  // Actions en lot
  activateSelectedAccounts(): void {
    if (this.selectedAccounts.size === 0) {
      this.notificationService.showWarning('Aucun compte sélectionné');
      return;
    }

    this.notificationService.showInfo('Activation des comptes sélectionnés...');
  }

  deactivateSelectedAccounts(): void {
    if (this.selectedAccounts.size === 0) {
      this.notificationService.showWarning('Aucun compte sélectionné');
      return;
    }

    this.notificationService.showInfo('Désactivation des comptes sélectionnés...');
  }

  deleteSelectedAccounts(): void {
    if (this.selectedAccounts.size === 0) {
      this.notificationService.showWarning('Aucun compte sélectionné');
      return;
    }

    if (confirm(`Êtes-vous sûr de vouloir supprimer ${this.selectedAccounts.size} compte(s) ?`)) {
      this.notificationService.showInfo('Suppression des comptes sélectionnés...');
    }
  }

  // Export et import
  exportAccounts(): void {
    this.notificationService.showInfo('Export des comptes en cours...');
  }

  importAccounts(): void {
    this.notificationService.showInfo('Import des comptes en cours...');
  }

  // Méthodes utilitaires
  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR'
    }).format(amount);
  }

  getAccountTypeLabel(type: string): string {
    const accountType = this.accountTypes.find(t => t.value === type);
    return accountType ? accountType.label : type;
  }

  getParentAccountCode(account: Account): string {
    if (!account.parentAccountId) return '-';
    const parent = this.accounts.find(a => a.id === account.parentAccountId);
    return parent ? parent.accountCode : '-';
  }

  getAccountTypeColor(type: string): string {
    const colors: { [key: string]: string } = {
      'ASSET': '#4caf50',
      'LIABILITY': '#f44336',
      'EQUITY': '#2196f3',
      'REVENUE': '#ff9800',
      'EXPENSE': '#9c27b0',
      'COST_OF_SALES': '#607d8b'
    };
    return colors[type] || '#666';
  }

  // Navigation dans l'arbre
  expandAll(): void {
    // Logique pour expandre tous les nœuds
  }

  collapseAll(): void {
    // Logique pour collapser tous les nœuds
  }
}
