import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule, NgIf } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatChipsModule } from '@angular/material/chips';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject, takeUntil } from 'rxjs';

import { AuthService } from '../../../shared/services/auth';
import { NotificationService } from '../../../shared/services/notification';
import { LoadingService } from '../../../shared/services/loading';
import { SycebnlOrganizationService, SycebnlOrganization } from '../../../shared/services/sycebnl-organization.service';
import { SycebnlTransactionService, SycebnlTransaction } from '../../../shared/services/sycebnl-transaction.service';
import { User } from '../../../shared/models/user';

@Component({
  selector: 'app-organization-detail',
  standalone: true,
  imports: [
    CommonModule,
    NgIf,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatTooltipModule,
    MatChipsModule,
    MatTabsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule
  ],
  templateUrl: './organization-detail.html',
  styleUrl: './organization-detail.scss'
})
export class OrganizationDetail implements OnInit, OnDestroy {
  currentUser: User | null = null;
  isLoading = false;
  private destroy$ = new Subject<void>();

  // Données
  organization: SycebnlOrganization | null = null;
  transactions: SycebnlTransaction[] = [];
  filteredTransactions: MatTableDataSource<SycebnlTransaction> = new MatTableDataSource<SycebnlTransaction>();

  // Colonnes du tableau des transactions
  displayedColumns: string[] = [
    'transactionDate',
    'transactionType',
    'amount',
    'currency',
    'status',
    'description',
    'actions'
  ];

  // Onglets
  selectedTabIndex = 0;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private notificationService: NotificationService,
    private loadingService: LoadingService,
    private sycebnlOrganizationService: SycebnlOrganizationService,
    private sycebnlTransactionService: SycebnlTransactionService
  ) {}

  ngOnInit(): void {
    this.setupUserSubscription();
    this.loadOrganization();
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

  private loadOrganization(): void {
    const organizationId = this.route.snapshot.params['id'];
    if (!organizationId) {
      this.notificationService.showError('ID d\'organisation manquant');
      this.router.navigate(['/sycebnl/organizations']);
      return;
    }

    this.isLoading = true;
    this.loadingService.show();

    this.sycebnlOrganizationService.getSycebnlOrganizationById(organizationId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (organization: SycebnlOrganization) => {
          this.organization = organization;
          this.loadTransactions(organizationId);
          this.isLoading = false;
          this.loadingService.hide();
        },
        error: (error: any) => {
          console.error('Erreur lors du chargement de l\'organisation:', error);
          this.isLoading = false;
          this.loadingService.hide();
          this.notificationService.showError('Erreur lors du chargement de l\'organisation');
          this.router.navigate(['/sycebnl/organizations']);
        }
      });
  }

  private loadTransactions(organizationId: string): void {
    this.sycebnlTransactionService.getAllSycebnlTransactions()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (transactions: SycebnlTransaction[]) => {
          // Filtrer les transactions par organisation (utiliser companyId)
          const orgTransactions = transactions.filter(t => t.companyId === parseInt(organizationId));
          this.transactions = orgTransactions;
          this.filteredTransactions.data = orgTransactions;
        },
        error: (error: any) => {
          console.error('Erreur lors du chargement des transactions:', error);
          this.notificationService.showError('Erreur lors du chargement des transactions');
        }
      });
  }

  // Actions
  editOrganization(): void {
    if (this.organization) {
      this.router.navigate(['/sycebnl/organizations', this.organization.id, 'edit']);
    }
  }

  deleteOrganization(): void {
    if (!this.organization) return;

    if (confirm(`Êtes-vous sûr de vouloir supprimer l'organisation ${this.organization.organizationName} ?`)) {
      this.isLoading = true;
      this.loadingService.show();

      this.sycebnlOrganizationService.deleteSycebnlOrganization(this.organization.id!)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.notificationService.showSuccess('Organisation supprimée avec succès');
            this.router.navigate(['/sycebnl/organizations']);
            this.isLoading = false;
            this.loadingService.hide();
          },
          error: (error: any) => {
            console.error('Erreur lors de la suppression de l\'organisation:', error);
            this.isLoading = false;
            this.loadingService.hide();
            this.notificationService.showError('Erreur lors de la suppression de l\'organisation');
          }
        });
    }
  }

  viewTransaction(transaction: SycebnlTransaction): void {
    this.router.navigate(['/sycebnl/transactions', transaction.id]);
  }

  createTransaction(): void {
    if (this.organization) {
      this.router.navigate(['/sycebnl/transactions/new'], {
        queryParams: { organizationId: this.organization.id }
      });
    }
  }

  // Méthodes utilitaires
  formatCurrency(amount: number, currency: string = 'EUR'): string {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: currency
    }).format(amount);
  }

  formatDate(date: Date | string): string {
    const dateObj = typeof date === 'string' ? new Date(date) : date;
    return new Intl.DateTimeFormat('fr-FR').format(dateObj);
  }

  getOrganizationStatusColor(status: string): string {
    const colors: { [key: string]: string } = {
      'ACTIVE': 'primary',
      'INACTIVE': 'warn',
      'SUSPENDED': 'accent',
      'PENDING': 'basic'
    };
    return colors[status] || 'basic';
  }

  getTransactionStatusColor(status: string): string {
    const colors: { [key: string]: string } = {
      'PENDING': 'warn',
      'COMPLETED': 'primary',
      'FAILED': 'accent',
      'CANCELLED': 'basic'
    };
    return colors[status] || 'basic';
  }

  getTransactionTypeLabel(type: string): string {
    const types: { [key: string]: string } = {
      'PAYMENT': 'Paiement',
      'TRANSFER': 'Virement',
      'DEPOSIT': 'Dépôt',
      'WITHDRAWAL': 'Retrait',
      'REFUND': 'Remboursement',
      'FEE': 'Frais'
    };
    return types[type] || type;
  }

  getTotalTransactions(): number {
    return this.transactions.length;
  }

  getTotalAmount(): number {
    return this.transactions.reduce((total, transaction) => total + transaction.amount, 0);
  }

  getPendingTransactions(): number {
    return this.transactions.filter(t => t.status === 'DRAFT').length;
  }

  getCompletedTransactions(): number {
    return this.transactions.filter(t => t.status === 'POSTED').length;
  }

  // Navigation
  goBack(): void {
    this.router.navigate(['/sycebnl/organizations']);
  }
}
