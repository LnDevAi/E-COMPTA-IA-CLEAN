import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule, NgIf, NgFor } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatMenuModule } from '@angular/material/menu';
import { MatGridListModule } from '@angular/material/grid-list';
import { Subject, takeUntil, interval } from 'rxjs';

import { AuthService } from '../../shared/services/auth';
import { NotificationService } from '../../shared/services/notification';
import { LoadingService } from '../../shared/services/loading';
import { User } from '../../shared/models/user';

interface DashboardWidget {
  id: string;
  title: string;
  value: string | number;
  change: number;
  changeType: 'increase' | 'decrease' | 'neutral';
  icon: string;
  color: string;
  route?: string;
}

interface RecentActivity {
  id: string;
  type: 'transaction' | 'client' | 'invoice' | 'user';
  description: string;
  timestamp: Date;
  amount?: number;
  status: 'success' | 'warning' | 'error' | 'info';
}

interface QuickAction {
  label: string;
  icon: string;
  route: string;
  color: string;
  description: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    NgIf,
    NgFor,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    MatTableModule,
    MatChipsModule,
    MatTooltipModule,
    MatMenuModule,
    MatGridListModule
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class DashboardComponent implements OnInit, OnDestroy {
  currentUser: User | null = null;
  isLoading = false;
  private destroy$ = new Subject<void>();

  // Widgets principaux
  widgets: DashboardWidget[] = [
    {
      id: 'revenue',
      title: 'Chiffre d\'affaires',
      value: '€125,430',
      change: 12.5,
      changeType: 'increase',
      icon: 'trending_up',
      color: 'primary',
      route: '/reports/financial'
    },
    {
      id: 'clients',
      title: 'Clients actifs',
      value: 1247,
      change: 8.2,
      changeType: 'increase',
      icon: 'people',
      color: 'accent',
      route: '/crm/clients'
    },
    {
      id: 'invoices',
      title: 'Factures en attente',
      value: 23,
      change: -5.1,
      changeType: 'decrease',
      icon: 'receipt',
      color: 'warn',
      route: '/billing/invoices'
    },
    {
      id: 'transactions',
      title: 'Transactions du mois',
      value: 2847,
      change: 15.3,
      changeType: 'increase',
      icon: 'swap_horiz',
      color: 'primary',
      route: '/sycebnl/transactions'
    }
  ];

  // Activités récentes
  recentActivities: RecentActivity[] = [
    {
      id: '1',
      type: 'transaction',
      description: 'Nouvelle transaction bancaire - Compte Courant',
      timestamp: new Date(Date.now() - 5 * 60 * 1000),
      amount: 2500,
      status: 'success'
    },
    {
      id: '2',
      type: 'client',
      description: 'Nouveau client ajouté - Entreprise ABC',
      timestamp: new Date(Date.now() - 15 * 60 * 1000),
      status: 'info'
    },
    {
      id: '3',
      type: 'invoice',
      description: 'Facture #INV-2024-001 créée',
      timestamp: new Date(Date.now() - 30 * 60 * 1000),
      amount: 1200,
      status: 'success'
    },
    {
      id: '4',
      type: 'transaction',
      description: 'Échec de synchronisation bancaire',
      timestamp: new Date(Date.now() - 45 * 60 * 1000),
      status: 'error'
    },
    {
      id: '5',
      type: 'user',
      description: 'Connexion utilisateur - Marie Dupont',
      timestamp: new Date(Date.now() - 60 * 60 * 1000),
      status: 'info'
    }
  ];

  // Actions rapides
  quickActions: QuickAction[] = [
    {
      label: 'Nouveau Client',
      icon: 'person_add',
      route: '/crm/clients/new',
      color: 'primary',
      description: 'Ajouter un nouveau client'
    },
    {
      label: 'Créer Facture',
      icon: 'receipt',
      route: '/billing/invoices/new',
      color: 'accent',
      description: 'Créer une nouvelle facture'
    },
    {
      label: 'Transaction',
      icon: 'swap_horiz',
      route: '/sycebnl/transactions/new',
      color: 'primary',
      description: 'Enregistrer une transaction'
    },
    {
      label: 'Rapport',
      icon: 'assessment',
      route: '/reports/financial',
      color: 'accent',
      description: 'Générer un rapport'
    }
  ];

  // Données pour les graphiques (simulées)
  chartData = {
    revenue: {
      labels: ['Jan', 'Fév', 'Mar', 'Avr', 'Mai', 'Jun'],
      data: [85000, 92000, 78000, 105000, 125000, 125430]
    },
    clients: {
      labels: ['Q1', 'Q2', 'Q3', 'Q4'],
      data: [1200, 1150, 1180, 1247]
    }
  };

  constructor(
    private authService: AuthService,
    private notificationService: NotificationService,
    private loadingService: LoadingService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadDashboardData();
    this.setupRealTimeUpdates();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadDashboardData(): void {
    this.isLoading = true;
    this.loadingService.show();

    // Simuler le chargement des données
    setTimeout(() => {
      this.authService.currentUser$
        .pipe(takeUntil(this.destroy$))
        .subscribe(user => {
          this.currentUser = user;
        });

      this.isLoading = false;
      this.loadingService.hide();
    }, 1000);
  }

  private setupRealTimeUpdates(): void {
    // Mise à jour des données toutes les 30 secondes
    interval(30000)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.updateDashboardData();
      });
  }

  private updateDashboardData(): void {
    // Simuler la mise à jour des données
    this.widgets.forEach(widget => {
      if (typeof widget.value === 'number') {
        widget.value = Math.floor(widget.value * (0.95 + Math.random() * 0.1));
      }
    });
  }

  getWidgetIcon(widget: DashboardWidget): string {
    return widget.icon;
  }

  getWidgetColor(widget: DashboardWidget): string {
    return widget.color;
  }

  getChangeIcon(changeType: string): string {
    switch (changeType) {
      case 'increase':
        return 'trending_up';
      case 'decrease':
        return 'trending_down';
      default:
        return 'trending_flat';
    }
  }

  getChangeColor(changeType: string): string {
    switch (changeType) {
      case 'increase':
        return 'green';
      case 'decrease':
        return 'red';
      default:
        return 'gray';
    }
  }

  getActivityIcon(type: string): string {
    switch (type) {
      case 'transaction':
        return 'swap_horiz';
      case 'client':
        return 'person';
      case 'invoice':
        return 'receipt';
      case 'user':
        return 'account_circle';
      default:
        return 'info';
    }
  }

  getActivityColor(status: string): string {
    switch (status) {
      case 'success':
        return 'green';
      case 'warning':
        return 'orange';
      case 'error':
        return 'red';
      case 'info':
        return 'blue';
      default:
        return 'gray';
    }
  }

  formatTimeAgo(timestamp: Date): string {
    const now = new Date();
    const diff = now.getTime() - timestamp.getTime();
    const minutes = Math.floor(diff / 60000);
    const hours = Math.floor(diff / 3600000);
    const days = Math.floor(diff / 86400000);

    if (minutes < 60) {
      return `Il y a ${minutes} min`;
    } else if (hours < 24) {
      return `Il y a ${hours}h`;
    } else {
      return `Il y a ${days} jour${days > 1 ? 's' : ''}`;
    }
  }

  navigateToWidget(widget: DashboardWidget): void {
    if (widget.route) {
      this.router.navigate([widget.route]);
    }
  }

  navigateToQuickAction(action: QuickAction): void {
    this.router.navigate([action.route]);
  }

  refreshDashboard(): void {
    this.loadDashboardData();
    this.notificationService.showInfo('Dashboard actualisé');
  }

  exportDashboard(): void {
    // TODO: Implémenter l'export du dashboard
    this.notificationService.showInfo('Export en cours...');
  }
}
