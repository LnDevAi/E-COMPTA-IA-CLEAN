import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

import { ApiService } from '../../core/services/api.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  template: `
    <div class="dashboard-container">
      <h1>E-COMPTA-IA Dashboard</h1>
      
      <!-- Test de connexion -->
      <mat-card class="test-card">
        <mat-card-header>
          <mat-card-title>Test de Connexion Backend</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <div class="test-section">
            <button mat-raised-button color="primary" (click)="testConnection()" [disabled]="loading">
              <mat-icon>wifi</mat-icon>
              Tester Connexion
            </button>
            <div *ngIf="loading" class="loading">
              <mat-spinner diameter="30"></mat-spinner>
              <span>Test en cours...</span>
            </div>
            <div *ngIf="connectionResult" class="result" [class.success]="connectionResult.success" [class.error]="!connectionResult.success">
              <mat-icon>{{ connectionResult.success ? 'check_circle' : 'error' }}</mat-icon>
              <span>{{ connectionResult.message }}</span>
            </div>
          </div>
        </mat-card-content>
      </mat-card>

      <!-- Test de santé -->
      <mat-card class="test-card">
        <mat-card-header>
          <mat-card-title>Test de Santé Backend</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <div class="test-section">
            <button mat-raised-button color="accent" (click)="testHealth()" [disabled]="loading">
              <mat-icon>health_and_safety</mat-icon>
              Tester Santé
            </button>
            <div *ngIf="healthResult" class="result" [class.success]="healthResult.success" [class.error]="!healthResult.success">
              <mat-icon>{{ healthResult.success ? 'check_circle' : 'error' }}</mat-icon>
              <span>{{ healthResult.message }}</span>
            </div>
          </div>
        </mat-card-content>
      </mat-card>

      <!-- Test d'authentification -->
      <mat-card class="test-card">
        <mat-card-header>
          <mat-card-title>Test d'Authentification</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <div class="test-section">
            <button mat-raised-button color="warn" (click)="testAuth()" [disabled]="loading">
              <mat-icon>security</mat-icon>
              Tester Auth
            </button>
            <div *ngIf="authResult" class="result" [class.success]="authResult.success" [class.error]="!authResult.success">
              <mat-icon>{{ authResult.success ? 'check_circle' : 'error' }}</mat-icon>
              <span>{{ authResult.message }}</span>
            </div>
          </div>
        </mat-card-content>
      </mat-card>

      <!-- Résumé des tests -->
      <mat-card class="summary-card" *ngIf="hasResults()">
        <mat-card-header>
          <mat-card-title>Résumé des Tests</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <div class="summary">
            <div class="test-item">
              <mat-icon [class.success]="connectionResult?.success" [class.error]="connectionResult && !connectionResult.success">wifi</mat-icon>
              <span>Connexion: {{ connectionResult?.success ? 'OK' : 'ERREUR' }}</span>
            </div>
            <div class="test-item">
              <mat-icon [class.success]="healthResult?.success" [class.error]="healthResult && !healthResult.success">health_and_safety</mat-icon>
              <span>Santé: {{ healthResult?.success ? 'OK' : 'ERREUR' }}</span>
            </div>
            <div class="test-item">
              <mat-icon [class.success]="authResult?.success" [class.error]="authResult && !authResult.success">security</mat-icon>
              <span>Auth: {{ authResult?.success ? 'OK' : 'ERREUR' }}</span>
            </div>
          </div>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .dashboard-container {
      padding: 20px;
      max-width: 1200px;
      margin: 0 auto;
    }
    
    h1 {
      text-align: center;
      color: #1976d2;
      margin-bottom: 30px;
    }
    
    .test-card {
      margin-bottom: 20px;
    }
    
    .test-section {
      display: flex;
      flex-direction: column;
      gap: 15px;
    }
    
    .loading {
      display: flex;
      align-items: center;
      gap: 10px;
      color: #666;
    }
    
    .result {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 10px;
      border-radius: 4px;
    }
    
    .result.success {
      background-color: #e8f5e8;
      color: #2e7d32;
    }
    
    .result.error {
      background-color: #ffebee;
      color: #c62828;
    }
    
    .summary-card {
      margin-top: 30px;
    }
    
    .summary {
      display: flex;
      flex-direction: column;
      gap: 10px;
    }
    
    .test-item {
      display: flex;
      align-items: center;
      gap: 10px;
    }
    
    .test-item mat-icon.success {
      color: #2e7d32;
    }
    
    .test-item mat-icon.error {
      color: #c62828;
    }
  `]
})
export class DashboardComponent implements OnInit {
  loading = false;
  connectionResult: any = null;
  healthResult: any = null;
  authResult: any = null;

  constructor(
    private apiService: ApiService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    // Auto-test au chargement
    this.testConnection();
  }

  testConnection(): void {
    this.loading = true;
    this.connectionResult = null;
    
    this.apiService.testConnection().subscribe({
      next: (response) => {
        this.connectionResult = {
          success: true,
          message: 'Connexion backend réussie !',
          data: response
        };
        this.loading = false;
        this.showSuccess('Connexion backend OK');
      },
      error: (error) => {
        this.connectionResult = {
          success: false,
          message: `Erreur de connexion: ${error.message || 'Backend non accessible'}`,
          error: error
        };
        this.loading = false;
        this.showError('Connexion backend échouée');
      }
    });
  }

  testHealth(): void {
    this.loading = true;
    this.healthResult = null;
    
    this.apiService.getHealth().subscribe({
      next: (response) => {
        this.healthResult = {
          success: true,
          message: 'Backend en bonne santé !',
          data: response
        };
        this.loading = false;
        this.showSuccess('Santé backend OK');
      },
      error: (error) => {
        this.healthResult = {
          success: false,
          message: `Erreur santé: ${error.message || 'Backend non accessible'}`,
          error: error
        };
        this.loading = false;
        this.showError('Santé backend échouée');
      }
    });
  }

  testAuth(): void {
    this.loading = true;
    this.authResult = null;
    
    this.apiService.testAuth().subscribe({
      next: (response) => {
        this.authResult = {
          success: true,
          message: 'Authentification OK !',
          data: response
        };
        this.loading = false;
        this.showSuccess('Authentification OK');
      },
      error: (error) => {
        this.authResult = {
          success: false,
          message: `Erreur auth: ${error.message || 'Backend non accessible'}`,
          error: error
        };
        this.loading = false;
        this.showError('Authentification échouée');
      }
    });
  }

  hasResults(): boolean {
    return this.connectionResult || this.healthResult || this.authResult;
  }

  private showSuccess(message: string): void {
    this.snackBar.open(message, 'Fermer', {
      duration: 3000,
      panelClass: ['success-snackbar']
    });
  }

  private showError(message: string): void {
    this.snackBar.open(message, 'Fermer', {
      duration: 5000,
      panelClass: ['error-snackbar']
    });
  }
}
