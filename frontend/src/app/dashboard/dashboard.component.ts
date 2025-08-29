import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ApiService } from '../core/services/api';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatGridListModule
  ],
  template: `
    <div class="dashboard-container">
      <h1>Tableau de Bord E-COMPTA-IA</h1>
      
      <!-- Loading indicator -->
      <div *ngIf="isLoading" class="loading-container">
        <mat-card>
          <mat-card-content>
            <p>Chargement des données...</p>
          </mat-card-content>
        </mat-card>
      </div>

      <!-- Error message -->
      <div *ngIf="error && !isLoading" class="error-container">
        <mat-card>
          <mat-card-content>
            <p class="error-message">{{ error }}</p>
            <p class="error-details">Vérifiez que le backend est démarré et accessible.</p>
            <button mat-raised-button color="primary" (click)="refreshDashboard()">
              <mat-icon>refresh</mat-icon>
              Réessayer
            </button>
          </mat-card-content>
        </mat-card>
      </div>
      
      <div class="dashboard-stats" *ngIf="!isLoading && !error && dashboardData">
        <div class="stat-card">
          <h3>Chiffre d'Affaires</h3>
          <p class="stat-value">{{ dashboardData.revenue || 0 | number:'1.0-0' }} FCFA</p>
          <span class="stat-change positive">+{{ dashboardData.revenueGrowth || 12.5 }}%</span>
        </div>
        
        <div class="stat-card">
          <h3>Dépenses</h3>
          <p class="stat-value">{{ dashboardData.expenses || 0 | number:'1.0-0' }} FCFA</p>
          <span class="stat-change negative">+{{ dashboardData.expensesGrowth || 8.2 }}%</span>
        </div>
        
        <div class="stat-card">
          <h3>Bénéfice Net</h3>
          <p class="stat-value">{{ dashboardData.profit || 0 | number:'1.0-0' }} FCFA</p>
          <span class="stat-change positive">+{{ dashboardData.profitGrowth || 18.7 }}%</span>
        </div>
        
        <div class="stat-card">
          <h3>Employés</h3>
          <p class="stat-value">{{ dashboardData.employees || 0 }}</p>
          <span class="stat-change positive">+{{ dashboardData.employeesGrowth || 3 }}</span>
        </div>
      </div>

      <div class="dashboard-charts" *ngIf="!isLoading && !error && dashboardData">
        <div class="chart-container">
          <h3>Évolution du CA</h3>
          <div class="chart-placeholder">
            [Graphique des revenus mensuels]
          </div>
        </div>
        
        <div class="chart-container">
          <h3>Répartition des Dépenses</h3>
          <div class="chart-placeholder">
            [Graphique en secteurs]
          </div>
        </div>
      </div>

      <div class="recent-activities" *ngIf="!isLoading && !error && dashboardData">
        <h3>Activités Récentes</h3>
        <ul>
          <li>Nouvelle facture créée - Client ABC Corp</li>
          <li>Paiement reçu - 150,000 FCFA</li>
          <li>Rapport mensuel généré</li>
          <li>Nouvel employé ajouté - Marie Dupont</li>
        </ul>
      </div>
    </div>
  `,
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  dashboardData: any = null;
  isLoading = true;
  error: string | null = null;

  constructor(
    private apiService: ApiService,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.isLoading = true;
    this.error = null;
    this.dashboardData = null;

    // Test de connexion au backend
    this.apiService.getDashboardTest().subscribe({
      next: (response) => {
        console.log('Dashboard test response:', response);
        this.dashboardData = response.data || {};
        this.isLoading = false;
        this.snackBar.open('Données chargées avec succès', 'Fermer', {
          duration: 3000,
          horizontalPosition: 'end',
          verticalPosition: 'top'
        });
      },
      error: (error) => {
        console.error('Dashboard error:', error);
        this.error = 'Impossible de charger les données du dashboard. Vérifiez la connexion au backend.';
        this.isLoading = false;
        
        // Fallback vers les données mockées
        this.loadMockData();
        
        this.snackBar.open('Erreur de connexion au backend', 'Fermer', {
          duration: 5000,
          horizontalPosition: 'end',
          verticalPosition: 'top',
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  private loadMockData(): void {
    this.dashboardData = {
      revenue: 2450000,
      expenses: 1850000,
      profit: 600000,
      employees: 45,
      revenueGrowth: 12.5,
      expensesGrowth: 8.2,
      profitGrowth: 18.7,
      employeesGrowth: 3
    };
  }

  refreshDashboard(): void {
    this.loadDashboardData();
  }
}


