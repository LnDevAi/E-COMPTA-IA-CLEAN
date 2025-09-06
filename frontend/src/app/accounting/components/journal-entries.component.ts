import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { ApiService } from '../../core/services/api.service';

@Component({
  selector: 'app-journal-entries',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatTableModule, MatButtonModule, MatIconModule, MatSnackBarModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">Écritures comptables</h1>
        <div>
          <button mat-raised-button color="primary" (click)="loadEntries()">
            <mat-icon>refresh</mat-icon>
            Actualiser
          </button>
          <a mat-raised-button color="accent" href="#/accounting/journal-entries/new">
            <mat-icon>add</mat-icon>
            Nouvelle écriture
          </a>
          <a mat-raised-button color="primary" href="#/accounting/reporting">
            <mat-icon>insights</mat-icon>
            Reporting
          </a>
        </div>
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
              <ng-container matColumnDef="entryDate">
                <th mat-header-cell *matHeaderCellDef>Date</th>
                <td mat-cell *matCellDef="let entry">{{ entry.entryDate | date:'dd/MM/yyyy' }}</td>
              </ng-container>

              <ng-container matColumnDef="entryNumber">
                <th mat-header-cell *matHeaderCellDef>Référence</th>
                <td mat-cell *matCellDef="let entry">{{ entry.entryNumber }}</td>
              </ng-container>

              <ng-container matColumnDef="description">
                <th mat-header-cell *matHeaderCellDef>Description</th>
                <td mat-cell *matCellDef="let entry">{{ entry.description }}</td>
              </ng-container>

              <ng-container matColumnDef="totalDebit">
                <th mat-header-cell *matHeaderCellDef>Débit</th>
                <td mat-cell *matCellDef="let entry">{{ entry.totalDebit | number:'1.0-0' }} FCFA</td>
              </ng-container>

              <ng-container matColumnDef="totalCredit">
                <th mat-header-cell *matHeaderCellDef>Crédit</th>
                <td mat-cell *matCellDef="let entry">{{ entry.totalCredit | number:'1.0-0' }} FCFA</td>
              </ng-container>

              <ng-container matColumnDef="status">
                <th mat-header-cell *matHeaderCellDef>Statut</th>
                <td mat-cell *matCellDef="let entry">
                  <span [class]="'status-' + (entry.status || '').toLowerCase()">{{ entry.status }}</span>
                </td>
              </ng-container>

              <ng-container matColumnDef="actions">
                <th mat-header-cell *matHeaderCellDef>Actions</th>
                <td mat-cell *matCellDef="let entry">
                  <button mat-icon-button color="primary" (click)="validate(entry)">
                    <mat-icon>check_circle</mat-icon>
                  </button>
                  <button mat-icon-button color="warn" (click)="remove(entry)">
                    <mat-icon>delete</mat-icon>
                  </button>
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
  displayedColumns: string[] = ['entryDate', 'entryNumber', 'description', 'totalDebit', 'totalCredit', 'status', 'actions'];

  constructor(
    private snackBar: MatSnackBar,
    private apiService: ApiService
  ) {}

  ngOnInit(): void {
    this.loadEntries();
  }

  loadEntries(): void {
    this.isLoading = true;
    this.error = null;

    this.apiService.getJournalEntries().subscribe({
      next: (response: any) => {
        this.entries = response.data || [];
        this.isLoading = false;
        this.snackBar.open('Écritures comptables chargées avec succès', 'Fermer', {
          duration: 3000
        });
      },
      error: (_: any) => {
        this.error = 'Impossible de charger les écritures comptables. Vérifiez la connexion au backend.';
        this.isLoading = false;
        this.snackBar.open('Erreur de connexion au backend', 'Fermer', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  validate(entry: any): void {
    const id = entry.id;
    this.apiService.post(`/api/accounting/journal-entries/${id}/validate`, {}).subscribe({
      next: () => {
        this.snackBar.open('Écriture validée', 'Fermer', { duration: 3000 });
        this.loadEntries();
      },
      error: () => this.snackBar.open('Erreur validation', 'Fermer', { duration: 4000, panelClass: ['error-snackbar'] })
    });
  }

  remove(entry: any): void {
    const id = entry.id;
    this.apiService.delete(`/api/accounting/journal-entries/${id}`).subscribe({
      next: () => {
        this.snackBar.open('Écriture supprimée', 'Fermer', { duration: 3000 });
        this.loadEntries();
      },
      error: () => this.snackBar.open('Erreur suppression', 'Fermer', { duration: 4000, panelClass: ['error-snackbar'] })
    });
  }
}


