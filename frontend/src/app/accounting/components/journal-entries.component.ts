import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ApiService } from '../../core/services/api';

@Component({
  selector: 'app-journal-entries',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatTableModule, MatButtonModule, MatIconModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">Écritures comptables</h1>
        <button mat-raised-button color="primary" (click)="loadEntries()">
          <mat-icon>refresh</mat-icon>
          Actualiser
        </button>
      </div>
      
      <!-- Loading -->
      <div *ngIf="isLoading" class="loading-container">
        <mat-card>
          <mat-card-content>
            <p>Chargement des écritures comptables...</p>
          </mat-card-content>
        </mat-card>
      </div>

      <!-- Error -->
      <div *ngIf="error && !isLoading" class="error-container">
        <mat-card>
          <mat-card-content>
            <p class="error-message">{{ error }}</p>
            <button mat-raised-button color="primary" (click)="loadEntries()">
              <mat-icon>refresh</mat-icon>
              Réessayer
            </button>
          </mat-card-content>
        </mat-card>
      </div>

      <!-- Data -->
      <div *ngIf="!isLoading && !error && entries.length > 0">
        <mat-card>
          <mat-card-content>
            <table mat-table [dataSource]="entries" class="entries-table">
              <ng-container matColumnDef="date">
                <th mat-header-cell *matHeaderCellDef>Date</th>
                <td mat-cell *matCellDef="let entry">{{ entry.date | date:'dd/MM/yyyy' }}</td>
              </ng-container>

              <ng-container matColumnDef="reference">
                <th mat-header-cell *matHeaderCellDef>Référence</th>
                <td mat-cell *matCellDef="let entry">{{ entry.reference }}</td>
              </ng-container>

              <ng-container matColumnDef="description">
                <th mat-header-cell *matHeaderCellDef>Description</th>
                <td mat-cell *matCellDef="let entry">{{ entry.description }}</td>
              </ng-container>

              <ng-container matColumnDef="debit">
                <th mat-header-cell *matHeaderCellDef>Débit</th>
                <td mat-cell *matCellDef="let entry">{{ entry.debit | number:'1.0-0' }} FCFA</td>
              </ng-container>

              <ng-container matColumnDef="credit">
                <th mat-header-cell *matHeaderCellDef>Crédit</th>
                <td mat-cell *matCellDef="let entry">{{ entry.credit | number:'1.0-0' }} FCFA</td>
              </ng-container>

              <ng-container matColumnDef="status">
                <th mat-header-cell *matHeaderCellDef>Statut</th>
                <td mat-cell *matCellDef="let entry">
                  <span [class]="'status-' + entry.status.toLowerCase()">{{ entry.status }}</span>
                </td>
              </ng-container>

              <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
              <tr mat-row *matRowDef="let row; columns: displayedColumns;"></tr>
            </table>
          </mat-card-content>
        </mat-card>
      </div>

      <!-- No data -->
      <div *ngIf="!isLoading && !error && entries.length === 0">
        <mat-card>
          <mat-card-content>
            <p>Aucune écriture comptable trouvée.</p>
          </mat-card-content>
        </mat-card>
      </div>
    </div>
  `,
  styles: [`
    .page-container {
      padding: 20px;
    }
    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
    }
    .page-title {
      margin: 0;
      color: #333;
    }
    .entries-table {
      width: 100%;
    }
    .status-validated {
      color: green;
      font-weight: bold;
    }
    .status-pending {
      color: orange;
      font-weight: bold;
    }
    .status-draft {
      color: gray;
      font-weight: bold;
    }
    .loading-container, .error-container {
      text-align: center;
      padding: 40px;
    }
    .error-message {
      color: red;
      margin-bottom: 20px;
    }
  `]
})
export class JournalEntriesComponent implements OnInit {
  entries: any[] = [];
  isLoading = true;
  error: string | null = null;
  displayedColumns: string[] = ['date', 'reference', 'description', 'debit', 'credit', 'status'];

  constructor(
    private apiService: ApiService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadEntries();
  }

  loadEntries(): void {
    this.isLoading = true;
    this.error = null;

    this.apiService.getJournalEntries().subscribe({
      next: (response) => {
        console.log('Journal entries response:', response);
        this.entries = response.data || [];
        this.isLoading = false;
        this.snackBar.open('Écritures comptables chargées avec succès', 'Fermer', {
          duration: 3000
        });
      },
      error: (error) => {
        console.error('Journal entries error:', error);
        this.error = 'Impossible de charger les écritures comptables. Vérifiez la connexion au backend.';
        this.isLoading = false;
        this.snackBar.open('Erreur de connexion au backend', 'Fermer', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }
}


