import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatBadgeModule } from '@angular/material/badge';
import { MatListModule } from '@angular/material/list';
import { Subject, takeUntil } from 'rxjs';

import { AuthService } from '../../../shared/services/auth';
import { NotificationService } from '../../../shared/services/notification';
import { LoadingService } from '../../../shared/services/loading';
import { CrmCustomerService, CrmCustomer } from '../../../shared/services/crm-customer.service';
import { User } from '../../../shared/models/user';

@Component({
  selector: 'app-customer-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTabsModule,
    MatChipsModule,
    MatDividerModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatTooltipModule,
    MatBadgeModule,
    MatListModule
  ],
  templateUrl: './customer-detail.html',
  styleUrl: './customer-detail.scss'
})
export class CustomerDetail implements OnInit, OnDestroy {
  currentUser: User | null = null;
  isLoading = false;
  private destroy$ = new Subject<void>();

  // Données du client
  customer: CrmCustomer | null = null;
  customerId: number | null = null;

  // Onglets
  selectedTabIndex = 0;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private notificationService: NotificationService,
    private loadingService: LoadingService,
    private crmCustomerService: CrmCustomerService
  ) {}

  ngOnInit(): void {
    this.setupUserSubscription();
    this.loadCustomer();
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

  private loadCustomer(): void {
    this.customerId = this.route.snapshot.params['id'];
    
    if (!this.customerId) {
      this.notificationService.showError('ID du client manquant');
      this.router.navigate(['/crm/customers']);
      return;
    }

    this.isLoading = true;
    this.loadingService.show();

    this.crmCustomerService.getCrmCustomerById(this.customerId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (customer: CrmCustomer) => {
          this.customer = customer;
          this.isLoading = false;
          this.loadingService.hide();
        },
        error: (error: any) => {
          console.error('Erreur lors du chargement du client:', error);
          this.isLoading = false;
          this.loadingService.hide();
          this.notificationService.showError('Erreur lors du chargement du client');
          this.router.navigate(['/crm/customers']);
        }
      });
  }

  // Actions
  editCustomer(): void {
    if (this.customer) {
      this.router.navigate(['/crm/customers/edit', this.customer.id]);
    }
  }

  deleteCustomer(): void {
    if (this.customer) {
      this.notificationService.showInfo(`Suppression de ${this.customer.thirdParty?.name}`);
    }
  }

  goBack(): void {
    this.router.navigate(['/crm/customers']);
  }

  // Méthodes utilitaires
  getSegmentIcon(segment: string): string {
    switch (segment) {
      case 'PREMIUM': return 'diamond';
      case 'GOLD': return 'star';
      case 'SILVER': return 'star_half';
      case 'BRONZE': return 'star_border';
      case 'NEW': return 'fiber_new';
      case 'AT_RISK': return 'warning';
      case 'CHURNED': return 'cancel';
      default: return 'business';
    }
  }

  getSegmentLabel(segment: string): string {
    switch (segment) {
      case 'PREMIUM': return 'Premium';
      case 'GOLD': return 'Or';
      case 'SILVER': return 'Argent';
      case 'BRONZE': return 'Bronze';
      case 'NEW': return 'Nouveau';
      case 'AT_RISK': return 'À risque';
      case 'CHURNED': return 'Perdu';
      default: return segment;
    }
  }

  getStatusIcon(status: string): string {
    switch (status) {
      case 'NEW': return 'fiber_new';
      case 'CONTACTED': return 'phone';
      case 'QUALIFIED': return 'check_circle';
      case 'PROPOSAL': return 'description';
      case 'NEGOTIATION': return 'handshake';
      case 'CLOSED_WON': return 'check_circle';
      case 'CLOSED_LOST': return 'cancel';
      default: return 'help';
    }
  }

  getStatusLabel(status: string): string {
    switch (status) {
      case 'NEW': return 'Nouveau';
      case 'CONTACTED': return 'Contacté';
      case 'QUALIFIED': return 'Qualifié';
      case 'PROPOSAL': return 'Proposition';
      case 'NEGOTIATION': return 'Négociation';
      case 'CLOSED_WON': return 'Gagné';
      case 'CLOSED_LOST': return 'Perdu';
      default: return status;
    }
  }

  getPaymentBehaviorIcon(behavior: string): string {
    switch (behavior) {
      case 'EXCELLENT': return 'check_circle';
      case 'GOOD': return 'thumb_up';
      case 'AVERAGE': return 'thumbs_up_down';
      case 'POOR': return 'thumb_down';
      case 'DELINQUENT': return 'warning';
      default: return 'help';
    }
  }

  getPaymentBehaviorLabel(behavior: string): string {
    switch (behavior) {
      case 'EXCELLENT': return 'Excellent';
      case 'GOOD': return 'Bon';
      case 'AVERAGE': return 'Moyen';
      case 'POOR': return 'Mauvais';
      case 'DELINQUENT': return 'Défaillant';
      default: return behavior;
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

  formatPercentage(value: number): string {
    return `${value}%`;
  }
}
