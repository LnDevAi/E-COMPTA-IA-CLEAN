import { Component, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { CommonModule, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTabsModule } from '@angular/material/tabs';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatChipsModule } from '@angular/material/chips';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject, takeUntil } from 'rxjs';

import { AuthService } from '../../../shared/services/auth';
import { NotificationService } from '../../../shared/services/notification';
import { LoadingService } from '../../../shared/services/loading';
import { User } from '../../../shared/models/user';

interface AnalyticsWidget {
  id: string;
  title: string;
  type: 'chart' | 'table' | 'metric' | 'kpi';
  data: any;
  config: any;
  position: { x: number; y: number; w: number; h: number };
}

interface KPI {
  id: string;
  title: string;
  value: number;
  change: number;
  changeType: 'increase' | 'decrease' | 'neutral';
  format: 'currency' | 'number' | 'percentage';
  icon: string;
  color: string;
}

interface ChartData {
  labels: string[];
  datasets: {
    label: string;
    data: number[];
    backgroundColor?: string[];
    borderColor?: string;
    fill?: boolean;
  }[];
}

@Component({
  selector: 'app-dashboard-analytics',
  standalone: true,
  imports: [
    CommonModule,
    NgIf,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    MatFormFieldModule,
    MatTabsModule,
    MatExpansionModule,
    MatDividerModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatTooltipModule,
    MatChipsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule
  ],
  templateUrl: './dashboard-analytics.html',
  styleUrl: './dashboard-analytics.scss'
})
export class DashboardAnalytics implements OnInit, OnDestroy {
  @ViewChild('chartContainer', { static: false }) chartContainer!: ElementRef;

  currentUser: User | null = null;
  isLoading = false;
  private destroy$ = new Subject<void>();

  // Données
  kpis: KPI[] = [];
  widgets: AnalyticsWidget[] = [];
  selectedTimeRange = '30d';
  selectedCategory = 'all';

  // Colonnes du tableau
  displayedColumns: string[] = ['metric', 'current', 'previous', 'change', 'trend'];
  dataSource = new MatTableDataSource<any>([]);

  // Périodes
  timeRanges = [
    { value: '7d', label: '7 derniers jours' },
    { value: '30d', label: '30 derniers jours' },
    { value: '90d', label: '3 derniers mois' },
    { value: '1y', label: '1 an' },
    { value: 'all', label: 'Tout' }
  ];

  // Catégories
  categories = [
    { value: 'all', label: 'Toutes les catégories' },
    { value: 'financial', label: 'Financier' },
    { value: 'crm', label: 'CRM' },
    { value: 'inventory', label: 'Inventaire' },
    { value: 'payroll', label: 'Paie' },
    { value: 'operations', label: 'Opérations' }
  ];

  // Données de graphiques
  revenueChartData: ChartData = {
    labels: [],
    datasets: []
  };

  customerChartData: ChartData = {
    labels: [],
    datasets: []
  };

  inventoryChartData: ChartData = {
    labels: [],
    datasets: []
  };

  constructor(
    private authService: AuthService,
    private notificationService: NotificationService,
    private loadingService: LoadingService
  ) {}

  ngOnInit(): void {
    this.setupUserSubscription();
    this.loadAnalyticsData();
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

  private loadAnalyticsData(): void {
    this.isLoading = true;
    this.loadingService.show();

    // Simulation de chargement des données
    setTimeout(() => {
      this.initializeKPIs();
      this.initializeWidgets();
      this.initializeChartData();
      this.initializeTableData();
      this.isLoading = false;
      this.loadingService.hide();
    }, 2000);
  }

  private initializeKPIs(): void {
    this.kpis = [
      {
        id: 'revenue',
        title: 'Chiffre d\'affaires',
        value: 125000,
        change: 12.5,
        changeType: 'increase',
        format: 'currency',
        icon: 'euro',
        color: '#4caf50'
      },
      {
        id: 'customers',
        title: 'Nouveaux clients',
        value: 45,
        change: 8.2,
        changeType: 'increase',
        format: 'number',
        icon: 'people',
        color: '#2196f3'
      },
      {
        id: 'orders',
        title: 'Commandes',
        value: 234,
        change: -2.1,
        changeType: 'decrease',
        format: 'number',
        icon: 'shopping_cart',
        color: '#ff9800'
      },
      {
        id: 'conversion',
        title: 'Taux de conversion',
        value: 3.2,
        change: 0.5,
        changeType: 'increase',
        format: 'percentage',
        icon: 'trending_up',
        color: '#9c27b0'
      },
      {
        id: 'inventory_value',
        title: 'Valeur du stock',
        value: 89000,
        change: 5.8,
        changeType: 'increase',
        format: 'currency',
        icon: 'inventory',
        color: '#607d8b'
      },
      {
        id: 'employees',
        title: 'Employés actifs',
        value: 156,
        change: 0,
        changeType: 'neutral',
        format: 'number',
        icon: 'person',
        color: '#795548'
      }
    ];
  }

  private initializeWidgets(): void {
    this.widgets = [
      {
        id: 'revenue_trend',
        title: 'Évolution du chiffre d\'affaires',
        type: 'chart',
        data: this.revenueChartData,
        config: { type: 'line', responsive: true },
        position: { x: 0, y: 0, w: 6, h: 4 }
      },
      {
        id: 'customer_segments',
        title: 'Répartition des clients par segment',
        type: 'chart',
        data: this.customerChartData,
        config: { type: 'doughnut', responsive: true },
        position: { x: 6, y: 0, w: 3, h: 4 }
      },
      {
        id: 'inventory_status',
        title: 'État des stocks',
        type: 'chart',
        data: this.inventoryChartData,
        config: { type: 'bar', responsive: true },
        position: { x: 9, y: 0, w: 3, h: 4 }
      },
      {
        id: 'top_products',
        title: 'Top produits',
        type: 'table',
        data: this.dataSource,
        config: { pageSize: 5 },
        position: { x: 0, y: 4, w: 6, h: 4 }
      },
      {
        id: 'recent_activities',
        title: 'Activités récentes',
        type: 'table',
        data: this.dataSource,
        config: { pageSize: 8 },
        position: { x: 6, y: 4, w: 6, h: 4 }
      }
    ];
  }

  private initializeChartData(): void {
    // Données de revenus
    this.revenueChartData = {
      labels: ['Jan', 'Fév', 'Mar', 'Avr', 'Mai', 'Jun', 'Jul', 'Aoû', 'Sep', 'Oct', 'Nov', 'Déc'],
      datasets: [{
        label: 'Chiffre d\'affaires',
        data: [85000, 92000, 78000, 105000, 98000, 112000, 125000, 118000, 135000, 142000, 128000, 125000],
        borderColor: '#4caf50',
        backgroundColor: ['rgba(76, 175, 80, 0.1)'],
        fill: true
      }]
    };

    // Données de clients
    this.customerChartData = {
      labels: ['Premium', 'Standard', 'Basic', 'Prospects'],
      datasets: [{
        label: 'Clients',
        data: [25, 45, 30, 20],
        backgroundColor: ['#4caf50', '#2196f3', '#ff9800', '#9c27b0']
      }]
    };

    // Données d'inventaire
    this.inventoryChartData = {
      labels: ['En stock', 'Stock faible', 'Rupture', 'En commande'],
      datasets: [{
        label: 'Produits',
        data: [120, 25, 8, 15],
        backgroundColor: ['#4caf50', '#ff9800', '#f44336', '#2196f3']
      }]
    };
  }

  private initializeTableData(): void {
    const tableData = [
      { metric: 'Chiffre d\'affaires', current: 125000, previous: 111000, change: 12.6, trend: 'up' },
      { metric: 'Nouveaux clients', current: 45, previous: 42, change: 7.1, trend: 'up' },
      { metric: 'Commandes', current: 234, previous: 239, change: -2.1, trend: 'down' },
      { metric: 'Panier moyen', current: 534, previous: 464, change: 15.1, trend: 'up' },
      { metric: 'Taux de conversion', current: 3.2, previous: 2.7, change: 18.5, trend: 'up' },
      { metric: 'Valeur du stock', current: 89000, previous: 84100, change: 5.8, trend: 'up' },
      { metric: 'Employés actifs', current: 156, previous: 156, change: 0, trend: 'neutral' },
      { metric: 'Coûts opérationnels', current: 78000, previous: 82000, change: -4.9, trend: 'down' }
    ];

    this.dataSource.data = tableData;
  }

  // Actions
  refreshData(): void {
    this.loadAnalyticsData();
    this.notificationService.showInfo('Données actualisées');
  }

  exportData(format: 'excel' | 'pdf' | 'csv'): void {
    this.notificationService.showInfo(`Export ${format.toUpperCase()} en cours...`);
    // Logique d'export à implémenter
  }

  customizeDashboard(): void {
    this.notificationService.showInfo('Mode personnalisation activé');
    // Logique de personnalisation à implémenter
  }

  // Filtres
  onTimeRangeChange(): void {
    this.loadAnalyticsData();
  }

  onCategoryChange(): void {
    this.loadAnalyticsData();
  }

  // Méthodes utilitaires
  formatValue(value: number, format: string): string {
    switch (format) {
      case 'currency':
        return new Intl.NumberFormat('fr-FR', {
          style: 'currency',
          currency: 'EUR'
        }).format(value);
      case 'percentage':
        return `${value.toFixed(1)}%`;
      case 'number':
        return new Intl.NumberFormat('fr-FR').format(value);
      default:
        return value.toString();
    }
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
        return '#4caf50';
      case 'decrease':
        return '#f44336';
      default:
        return '#666';
    }
  }

  getTrendIcon(trend: string): string {
    switch (trend) {
      case 'up':
        return 'arrow_upward';
      case 'down':
        return 'arrow_downward';
      default:
        return 'arrow_forward';
    }
  }

  getTrendColor(trend: string): string {
    switch (trend) {
      case 'up':
        return '#4caf50';
      case 'down':
        return '#f44336';
      default:
        return '#666';
    }
  }

  // Gestion des widgets
  addWidget(): void {
    this.notificationService.showInfo('Ajout de widget en cours...');
  }

  removeWidget(widgetId: string): void {
    this.widgets = this.widgets.filter(w => w.id !== widgetId);
    this.notificationService.showSuccess('Widget supprimé');
  }

  resizeWidget(widgetId: string, size: { w: number; h: number }): void {
    const widget = this.widgets.find(w => w.id === widgetId);
    if (widget) {
      widget.position.w = size.w;
      widget.position.h = size.h;
    }
  }

  moveWidget(widgetId: string, position: { x: number; y: number }): void {
    const widget = this.widgets.find(w => w.id === widgetId);
    if (widget) {
      widget.position.x = position.x;
      widget.position.y = position.y;
    }
  }
}
