import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule, NgIf } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
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

interface BankAccount {
  id: string;
  bankName: string;
  accountNumber: string;
  accountType: string;
  currency: string;
  balance: number;
  isActive: boolean;
  lastSync: Date;
  connectionStatus: 'connected' | 'disconnected' | 'error';
}

interface BankTransaction {
  id: string;
  date: Date;
  description: string;
  amount: number;
  type: 'debit' | 'credit';
  balance: number;
  reference: string;
  category: string;
  isReconciled: boolean;
}

interface ReconciliationResult {
  matched: number;
  unmatched: number;
  total: number;
  percentage: number;
}

@Component({
  selector: 'app-bank-integration',
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
  templateUrl: './bank-integration.html',
  styleUrl: './bank-integration.scss'
})
export class BankIntegration implements OnInit, OnDestroy {
  currentUser: User | null = null;
  isLoading = false;
  private destroy$ = new Subject<void>();

  // Formulaires
  bankAccountForm: FormGroup;
  importForm: FormGroup;

  // Données
  bankAccounts: BankAccount[] = [];
  transactions: BankTransaction[] = [];
  filteredTransactions: MatTableDataSource<BankTransaction> = new MatTableDataSource<BankTransaction>();
  reconciliationResult: ReconciliationResult | null = null;

  // Colonnes du tableau
  displayedColumns: string[] = [
    'date',
    'description',
    'amount',
    'type',
    'balance',
    'reference',
    'category',
    'isReconciled',
    'actions'
  ];

  // Banques supportées
  supportedBanks = [
    { value: 'BNP_PARIBAS', label: 'BNP Paribas' },
    { value: 'CREDIT_AGRICOLE', label: 'Crédit Agricole' },
    { value: 'SOCIETE_GENERALE', label: 'Société Générale' },
    { value: 'LCL', label: 'LCL' },
    { value: 'CAISSE_EPARGNE', label: 'Caisse d\'Épargne' },
    { value: 'BANQUE_POPULAIRE', label: 'Banque Populaire' },
    { value: 'CREDIT_MUTUEL', label: 'Crédit Mutuel' },
    { value: 'HSBC', label: 'HSBC' }
  ];

  // Types de comptes
  accountTypes = [
    { value: 'CHECKING', label: 'Compte courant' },
    { value: 'SAVINGS', label: 'Compte d\'épargne' },
    { value: 'BUSINESS', label: 'Compte professionnel' },
    { value: 'CREDIT', label: 'Compte crédit' }
  ];

  // Devises
  currencies = [
    { value: 'EUR', label: 'Euro (EUR)' },
    { value: 'USD', label: 'Dollar US (USD)' },
    { value: 'GBP', label: 'Livre Sterling (GBP)' },
    { value: 'XOF', label: 'Franc CFA (XOF)' }
  ];

  // Catégories de transactions
  transactionCategories = [
    'Salaire',
    'Vente',
    'Achat',
    'Frais bancaires',
    'Virement',
    'Prélèvement',
    'Chèque',
    'Espèces',
    'Autre'
  ];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private notificationService: NotificationService,
    private loadingService: LoadingService
  ) {
    this.bankAccountForm = this.fb.group({
      bankName: ['', Validators.required],
      accountNumber: ['', Validators.required],
      accountType: ['', Validators.required],
      currency: ['EUR', Validators.required],
      isActive: [true]
    });

    this.importForm = this.fb.group({
      bankAccount: ['', Validators.required],
      startDate: [new Date(), Validators.required],
      endDate: [new Date(), Validators.required],
      autoReconcile: [true]
    });
  }

  ngOnInit(): void {
    this.setupUserSubscription();
    this.loadBankAccounts();
    this.loadTransactions();
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

  private loadBankAccounts(): void {
    // Simulation de chargement des comptes bancaires
    this.bankAccounts = [
      {
        id: '1',
        bankName: 'BNP Paribas',
        accountNumber: '****1234',
        accountType: 'CHECKING',
        currency: 'EUR',
        balance: 125000,
        isActive: true,
        lastSync: new Date(),
        connectionStatus: 'connected'
      },
      {
        id: '2',
        bankName: 'Crédit Agricole',
        accountNumber: '****5678',
        accountType: 'SAVINGS',
        currency: 'EUR',
        balance: 45000,
        isActive: true,
        lastSync: new Date(Date.now() - 86400000),
        connectionStatus: 'connected'
      }
    ];
  }

  private loadTransactions(): void {
    // Simulation de chargement des transactions
    this.transactions = [
      {
        id: '1',
        date: new Date(),
        description: 'Virement salaire',
        amount: 3500,
        type: 'credit',
        balance: 125000,
        reference: 'VIR001',
        category: 'Salaire',
        isReconciled: true
      },
      {
        id: '2',
        date: new Date(Date.now() - 86400000),
        description: 'Paiement fournisseur',
        amount: 1200,
        type: 'debit',
        balance: 121500,
        reference: 'PAY002',
        category: 'Achat',
        isReconciled: false
      }
    ];
    this.filteredTransactions.data = this.transactions;
  }

  // Gestion des comptes bancaires
  addBankAccount(): void {
    if (this.bankAccountForm.invalid) {
      this.notificationService.showWarning('Veuillez remplir tous les champs obligatoires');
      return;
    }

    const accountData = this.bankAccountForm.value;
    const newAccount: BankAccount = {
      id: Date.now().toString(),
      ...accountData,
      balance: 0,
      lastSync: new Date(),
      connectionStatus: 'disconnected'
    };

    this.bankAccounts.push(newAccount);
    this.bankAccountForm.reset();
    this.bankAccountForm.patchValue({ currency: 'EUR', isActive: true });
    this.notificationService.showSuccess('Compte bancaire ajouté avec succès');
  }

  connectBankAccount(account: BankAccount): void {
    this.isLoading = true;
    this.loadingService.show();

    // Simulation de connexion
    setTimeout(() => {
      account.connectionStatus = 'connected';
      account.lastSync = new Date();
      this.isLoading = false;
      this.loadingService.hide();
      this.notificationService.showSuccess('Compte connecté avec succès');
    }, 2000);
  }

  disconnectBankAccount(account: BankAccount): void {
    account.connectionStatus = 'disconnected';
    this.notificationService.showInfo('Compte déconnecté');
  }

  deleteBankAccount(account: BankAccount): void {
    if (confirm(`Êtes-vous sûr de vouloir supprimer le compte ${account.accountNumber} ?`)) {
      this.bankAccounts = this.bankAccounts.filter(a => a.id !== account.id);
      this.notificationService.showSuccess('Compte supprimé');
    }
  }

  // Import des transactions
  importTransactions(): void {
    if (this.importForm.invalid) {
      this.notificationService.showWarning('Veuillez remplir tous les champs obligatoires');
      return;
    }

    this.isLoading = true;
    this.loadingService.show();

    // Simulation d'import
    setTimeout(() => {
      const newTransactions = this.generateMockTransactions();
      this.transactions = [...this.transactions, ...newTransactions];
      this.filteredTransactions.data = this.transactions;
      
      if (this.importForm.get('autoReconcile')?.value) {
        this.performReconciliation();
      }

      this.isLoading = false;
      this.loadingService.hide();
      this.notificationService.showSuccess(`${newTransactions.length} transactions importées`);
    }, 3000);
  }

  private generateMockTransactions(): BankTransaction[] {
    const transactions: BankTransaction[] = [];
    const startDate = this.importForm.get('startDate')?.value;
    const endDate = this.importForm.get('endDate')?.value;

    for (let i = 0; i < 20; i++) {
      const date = new Date(startDate.getTime() + Math.random() * (endDate.getTime() - startDate.getTime()));
      const amount = Math.floor(Math.random() * 5000) + 100;
      const type = Math.random() > 0.5 ? 'credit' : 'debit';
      
      transactions.push({
        id: `import_${Date.now()}_${i}`,
        date,
        description: `Transaction ${i + 1}`,
        amount,
        type: type as 'debit' | 'credit',
        balance: 0,
        reference: `REF${i + 1}`,
        category: this.transactionCategories[Math.floor(Math.random() * this.transactionCategories.length)],
        isReconciled: false
      });
    }

    return transactions;
  }

  // Réconciliation
  performReconciliation(): void {
    this.isLoading = true;
    this.loadingService.show();

    // Simulation de réconciliation
    setTimeout(() => {
      const total = this.transactions.length;
      const matched = Math.floor(total * 0.85);
      const unmatched = total - matched;

      this.reconciliationResult = {
        matched,
        unmatched,
        total,
        percentage: (matched / total) * 100
      };

      // Marquer les transactions comme réconciliées
      this.transactions.forEach((transaction, index) => {
        if (index < matched) {
          transaction.isReconciled = true;
        }
      });

      this.filteredTransactions.data = this.transactions;
      this.isLoading = false;
      this.loadingService.hide();
      this.notificationService.showSuccess(`Réconciliation terminée: ${matched}/${total} transactions`);
    }, 2000);
  }

  // Actions sur les transactions
  categorizeTransaction(transaction: BankTransaction, category: string): void {
    transaction.category = category;
    this.notificationService.showSuccess('Transaction catégorisée');
  }

  markAsReconciled(transaction: BankTransaction): void {
    transaction.isReconciled = true;
    this.notificationService.showSuccess('Transaction marquée comme réconciliée');
  }

  exportTransactions(): void {
    this.notificationService.showInfo('Export des transactions en cours...');
  }

  // Méthodes utilitaires
  formatCurrency(amount: number, currency: string = 'EUR'): string {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: currency
    }).format(amount);
  }

  formatDate(date: Date): string {
    return new Intl.DateTimeFormat('fr-FR').format(date);
  }

  getConnectionStatusColor(status: string): string {
    switch (status) {
      case 'connected':
        return '#4caf50';
      case 'disconnected':
        return '#ff9800';
      case 'error':
        return '#f44336';
      default:
        return '#666';
    }
  }

  getConnectionStatusLabel(status: string): string {
    switch (status) {
      case 'connected':
        return 'Connecté';
      case 'disconnected':
        return 'Déconnecté';
      case 'error':
        return 'Erreur';
      default:
        return 'Inconnu';
    }
  }

  getTransactionTypeColor(type: string): string {
    return type === 'credit' ? '#4caf50' : '#f44336';
  }

  getTransactionTypeLabel(type: string): string {
    return type === 'credit' ? 'Crédit' : 'Débit';
  }

  getReconciliationPercentage(): number {
    if (!this.reconciliationResult) return 0;
    return this.reconciliationResult.percentage;
  }
}








