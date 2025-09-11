import { Component, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { CommonModule, NgIf, NgFor } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatStepperModule } from '@angular/material/stepper';
import { MatBadgeModule } from '@angular/material/badge';
import { MatMenuModule } from '@angular/material/menu';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { Subject, takeUntil, interval } from 'rxjs';

import { AIIntelligenceService, AIInsight, SmartRecommendation, AIWorkflow } from '../../shared/services/ai-intelligence.service';
import { PredictiveAnalyticsService, PredictiveMetric, ScenarioAnalysis, BenchmarkData } from '../../shared/services/predictive-analytics.service';
import { AuthService } from '../../shared/services/auth';
import { NotificationService } from '../../shared/services/notification';
import { LoadingService } from '../../shared/services/loading';

@Component({
  selector: 'app-ai-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    NgIf,
    NgFor,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTabsModule,
    MatChipsModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    MatDialogModule,
    MatExpansionModule,
    MatStepperModule,
    MatBadgeModule,
    MatMenuModule,
    MatSnackBarModule
  ],
  templateUrl: './ai-dashboard.html',
  styleUrl: './ai-dashboard.scss'
})
export class AIDashboardComponent implements OnInit, OnDestroy {
  @ViewChild('insightsContainer') insightsContainer!: ElementRef;

  private destroy$ = new Subject<void>();

  // Données IA
  insights: AIInsight[] = [];
  predictions: PredictiveMetric[] = [];
  scenarios: ScenarioAnalysis[] = [];
  benchmarks: BenchmarkData[] = [];
  recommendations: SmartRecommendation[] = [];
  workflows: AIWorkflow[] = [];

  // État
  isLoading = false;
  selectedTab = 0;
  autoRefresh = true;
  refreshInterval = 30000; // 30 secondes

  // Statistiques
  aiStats: any = {};
  predictiveStats: any = {};

  // Filtres
  insightFilter = 'ALL';
  predictionFilter = 'ALL';
  scenarioFilter = 'ALL';

  constructor(
    private aiService: AIIntelligenceService,
    private predictiveService: PredictiveAnalyticsService,
    private authService: AuthService,
    private notificationService: NotificationService,
    private loadingService: LoadingService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadAIData();
    this.setupAutoRefresh();
    this.loadStats();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadAIData(): void {
    this.isLoading = true;
    this.loadingService.show();

    // Charger les données IA
    this.aiService.insights$.pipe(takeUntil(this.destroy$)).subscribe(insights => {
      this.insights = insights;
    });

    this.aiService.recommendations$.pipe(takeUntil(this.destroy$)).subscribe(recommendations => {
      this.recommendations = recommendations;
    });

    this.aiService.workflows$.pipe(takeUntil(this.destroy$)).subscribe(workflows => {
      this.workflows = workflows;
    });

    // Charger les données prédictives
    this.predictiveService.metrics$.pipe(takeUntil(this.destroy$)).subscribe(predictions => {
      this.predictions = predictions;
    });

    this.predictiveService.scenarios$.pipe(takeUntil(this.destroy$)).subscribe(scenarios => {
      this.scenarios = scenarios;
    });

    this.predictiveService.benchmarks$.pipe(takeUntil(this.destroy$)).subscribe(benchmarks => {
      this.benchmarks = benchmarks;
    });

    this.isLoading = false;
    this.loadingService.hide();
  }

  private setupAutoRefresh(): void {
    if (this.autoRefresh) {
      interval(this.refreshInterval).pipe(takeUntil(this.destroy$)).subscribe(() => {
        this.refreshData();
      });
    }
  }

  private loadStats(): void {
    this.aiService.getAIStats().pipe(takeUntil(this.destroy$)).subscribe(stats => {
      this.aiStats = stats;
    });

    this.predictiveService.getPredictiveStats().pipe(takeUntil(this.destroy$)).subscribe(stats => {
      this.predictiveStats = stats;
    });
  }

  refreshData(): void {
    this.loadAIData();
    this.loadStats();
    this.notificationService.showInfo('Données IA mises à jour');
  }

  toggleAutoRefresh(): void {
    this.autoRefresh = !this.autoRefresh;
    if (this.autoRefresh) {
      this.setupAutoRefresh();
    }
  }

  // Méthodes pour les insights
  getFilteredInsights(): AIInsight[] {
    if (this.insightFilter === 'ALL') {
      return this.insights;
    }
    return this.insights.filter(insight => insight.category === this.insightFilter);
  }

  getInsightIcon(type: string): string {
    const iconMap: { [key: string]: string } = {
      'PREDICTION': 'trending_up',
      'ANOMALY': 'warning',
      'RECOMMENDATION': 'lightbulb',
      'OPTIMIZATION': 'tune',
      'RISK': 'error'
    };
    return iconMap[type] || 'info';
  }

  getInsightColor(type: string): string {
    const colorMap: { [key: string]: string } = {
      'PREDICTION': 'primary',
      'ANOMALY': 'warn',
      'RECOMMENDATION': 'accent',
      'OPTIMIZATION': 'primary',
      'RISK': 'warn'
    };
    return colorMap[type] || 'primary';
  }

  getImpactColor(impact: string): string {
    const colorMap: { [key: string]: string } = {
      'LOW': 'green',
      'MEDIUM': 'orange',
      'HIGH': 'red',
      'CRITICAL': 'darkred'
    };
    return colorMap[impact] || 'gray';
  }

  resolveInsight(insightId: string): void {
    this.aiService.resolveInsight(insightId);
    this.notificationService.showSuccess('Insight résolu');
  }

  // Méthodes pour les prédictions
  getFilteredPredictions(): PredictiveMetric[] {
    if (this.predictionFilter === 'ALL') {
      return this.predictions;
    }
    return this.predictions.filter(prediction => prediction.category === this.predictionFilter);
  }

  getTrendIcon(trend: string): string {
    const iconMap: { [key: string]: string } = {
      'INCREASING': 'trending_up',
      'DECREASING': 'trending_down',
      'STABLE': 'trending_flat',
      'VOLATILE': 'show_chart'
    };
    return iconMap[trend] || 'trending_flat';
  }

  getTrendColor(trend: string, isPositive: boolean): string {
    if (trend === 'INCREASING' && isPositive) return 'green';
    if (trend === 'DECREASING' && !isPositive) return 'green';
    if (trend === 'INCREASING' && !isPositive) return 'red';
    if (trend === 'DECREASING' && isPositive) return 'red';
    return 'gray';
  }

  formatValue(value: number, unit: string): string {
    if (unit === 'FCFA') {
      return new Intl.NumberFormat('fr-FR', {
        style: 'currency',
        currency: 'XOF',
        minimumFractionDigits: 0,
        maximumFractionDigits: 0
      }).format(value);
    }
    if (unit === 'ratio' || unit === 'score') {
      return (value * 100).toFixed(1) + '%';
    }
    return new Intl.NumberFormat('fr-FR').format(value) + ' ' + unit;
  }

  // Méthodes pour les scénarios
  getFilteredScenarios(): ScenarioAnalysis[] {
    if (this.scenarioFilter === 'ALL') {
      return this.scenarios;
    }
    return this.scenarios.filter(scenario => scenario.riskLevel === this.scenarioFilter);
  }

  getRiskColor(riskLevel: string): string {
    const colorMap: { [key: string]: string } = {
      'LOW': 'green',
      'MEDIUM': 'orange',
      'HIGH': 'red',
      'CRITICAL': 'darkred'
    };
    return colorMap[riskLevel] || 'gray';
  }

  getRiskIcon(riskLevel: string): string {
    const iconMap: { [key: string]: string } = {
      'LOW': 'check_circle',
      'MEDIUM': 'warning',
      'HIGH': 'error',
      'CRITICAL': 'dangerous'
    };
    return iconMap[riskLevel] || 'help';
  }

  // Méthodes pour les benchmarks
  getBenchmarkColor(percentile: number): string {
    if (percentile >= 80) return 'green';
    if (percentile >= 60) return 'orange';
    return 'red';
  }

  getBenchmarkIcon(percentile: number): string {
    if (percentile >= 80) return 'star';
    if (percentile >= 60) return 'star_half';
    return 'star_border';
  }

  // Méthodes pour les recommandations
  getRecommendationIcon(category: string): string {
    const iconMap: { [key: string]: string } = {
      'BUDGET': 'account_balance',
      'COMPLIANCE': 'gavel',
      'EFFICIENCY': 'speed',
      'GROWTH': 'trending_up',
      'RISK_MANAGEMENT': 'security'
    };
    return iconMap[category] || 'lightbulb';
  }

  getEffortColor(effort: string): string {
    const colorMap: { [key: string]: string } = {
      'LOW': 'green',
      'MEDIUM': 'orange',
      'HIGH': 'red'
    };
    return colorMap[effort] || 'gray';
  }

  // Méthodes pour les workflows
  getWorkflowStatus(workflow: AIWorkflow): string {
    if (!workflow.isActive) return 'Inactif';
    if (workflow.nextExecution && workflow.nextExecution > new Date()) {
      return 'Programmé';
    }
    return 'Actif';
  }

  getWorkflowStatusColor(workflow: AIWorkflow): string {
    if (!workflow.isActive) return 'gray';
    if (workflow.nextExecution && workflow.nextExecution > new Date()) {
      return 'blue';
    }
    return 'green';
  }

  executeWorkflow(workflowId: string): void {
    this.aiService.executeWorkflow(workflowId, {}).subscribe(success => {
      if (success) {
        this.notificationService.showSuccess('Workflow exécuté avec succès');
      } else {
        this.notificationService.showError('Erreur lors de l\'exécution du workflow');
      }
    });
  }

  // Méthodes utilitaires
  getConfidenceColor(confidence: number): string {
    if (confidence >= 80) return 'green';
    if (confidence >= 60) return 'orange';
    return 'red';
  }

  getPriorityColor(priority: number): string {
    if (priority >= 8) return 'red';
    if (priority >= 6) return 'orange';
    if (priority >= 4) return 'yellow';
    return 'green';
  }

  formatDate(date: Date): string {
    return new Intl.DateTimeFormat('fr-FR', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    }).format(date);
  }

  formatTimeAgo(date: Date): string {
    const now = new Date();
    const diff = now.getTime() - date.getTime();
    const minutes = Math.floor(diff / 60000);
    const hours = Math.floor(diff / 3600000);
    const days = Math.floor(diff / 86400000);

    if (minutes < 60) return `Il y a ${minutes} min`;
    if (hours < 24) return `Il y a ${hours}h`;
    return `Il y a ${days} jour${days > 1 ? 's' : ''}`;
  }

  // Actions
  exportData(format: 'PDF' | 'EXCEL' | 'CSV'): void {
    this.predictiveService.exportPredictiveData(format).subscribe(success => {
      if (success) {
        this.notificationService.showSuccess(`Export ${format} généré avec succès`);
      } else {
        this.notificationService.showError('Erreur lors de l\'export');
      }
    });
  }

  generateNewPrediction(metricId: string): void {
    this.predictiveService.generatePrediction(metricId, '6 mois').subscribe(prediction => {
      this.notificationService.showSuccess('Nouvelle prédiction générée');
    });
  }

  openInsightDetails(insight: AIInsight): void {
    // TODO: Ouvrir un dialog avec les détails de l'insight
    console.log('Détails de l\'insight:', insight);
  }

  openRecommendationDetails(recommendation: SmartRecommendation): void {
    // TODO: Ouvrir un dialog avec les détails de la recommandation
    console.log('Détails de la recommandation:', recommendation);
  }

  // Navigation
  onTabChange(index: number): void {
    this.selectedTab = index;
  }

  // Filtres
  onInsightFilterChange(filter: string): void {
    this.insightFilter = filter;
  }

  onPredictionFilterChange(filter: string): void {
    this.predictionFilter = filter;
  }

  onScenarioFilterChange(filter: string): void {
    this.scenarioFilter = filter;
  }

  // Méthodes utilitaires supplémentaires
  getActionIcon(actionType: string): string {
    const iconMap: { [key: string]: string } = {
      'VALIDATE': 'check_circle',
      'NOTIFY': 'notifications',
      'ALERT': 'warning',
      'INVESTIGATE': 'search',
      'ANALYZE': 'analytics',
      'RECOMMEND': 'lightbulb'
    };
    return iconMap[actionType] || 'help';
  }
}
