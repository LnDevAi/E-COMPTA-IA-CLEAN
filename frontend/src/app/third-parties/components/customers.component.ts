import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ApiService } from '../../core/services/api';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatTableModule, MatButtonModule, MatIconModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">Gestion des clients</h1>
        <button mat-raised-button color="primary" (click)="loadCustomers()">
          <mat-icon>refresh</mat-icon>
          Actualiser
        </button>
      </div>
      
      <!-- Loading -->
      <div *ngIf="isLoading" class="loading-container">
        <mat-card>
          <mat-card-content>
            <p>Chargement des clients...</p>
          </mat-card-content>
        </mat-card>
      </div>

      <!-- Error -->
      <div *ngIf="error && !isLoading" class="error-container">
        <mat-card>
          <mat-card-content>
            <p class="error-message">{{ error }}</p>
            <button mat-raised-button color="primary" (click)="loadCustomers()">
              <mat-icon>refresh</mat-icon>
              Réessayer
            </button>
          </mat-card-content>
        </mat-card>
      </div>

      <!-- Data -->
      <div *ngIf="!isLoading && !error && customers.length > 0">
        <mat-card>
          <mat-card-content>
            <table mat-table [dataSource]="customers" class="customers-table">
              <ng-container matColumnDef="name">
                <th mat-header-cell *matHeaderCellDef>Nom</th>
                <td mat-cell *matCellDef="let customer">{{ customer.name }}</td>
              </ng-container>

              <ng-container matColumnDef="email">
                <th mat-header-cell *matHeaderCellDef>Email</th>
                <td mat-cell *matCellDef="let customer">{{ customer.email }}</td>
              </ng-container>

              <ng-container matColumnDef="phone">
                <th mat-header-cell *matHeaderCellDef>Téléphone</th>
                <td mat-cell *matCellDef="let customer">{{ customer.phone }}</td>
              </ng-container>

              <ng-container matColumnDef="address">
                <th mat-header-cell *matHeaderCellDef>Adresse</th>
                <td mat-cell *matCellDef="let customer">{{ customer.address }}</td>
              </ng-container>

              <ng-container matColumnDef="totalRevenue">
                <th mat-header-cell *matHeaderCellDef>CA Total</th>
                <td mat-cell *matCellDef="let customer">{{ customer.totalRevenue | number:'1.0-0' }} FCFA</td>
              </ng-container>

              <ng-container matColumnDef="status">
                <th mat-header-cell *matHeaderCellDef>Statut</th>
                <td mat-cell *matCellDef="let customer">
                  <span [class]="'status-' + customer.status.toLowerCase()">{{ customer.status }}</span>
                </td>
              </ng-container>

              <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
              <tr mat-row *matRowDef="let row; columns: displayedColumns;"></tr>
            </table>
          </mat-card-content>
        </mat-card>
      </div>

      <!-- No data -->
      <div *ngIf="!isLoading && !error && customers.length === 0">
        <mat-card>
          <mat-card-content>
            <p>Aucun client trouvé.</p>
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
    .customers-table {
      width: 100%;
    }
    .status-active {
      color: green;
      font-weight: bold;
    }
    .status-inactive {
      color: red;
      font-weight: bold;
    }
    .status-prospect {
      color: orange;
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
export class CustomersComponent implements OnInit {
  customers: any[] = [];
  isLoading = true;
  error: string | null = null;
  displayedColumns: string[] = ['name', 'email', 'phone', 'address', 'totalRevenue', 'status'];

  constructor(
    private apiService: ApiService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadCustomers();
  }

  loadCustomers(): void {
    this.isLoading = true;
    this.error = null;

    this.apiService.getCustomers().subscribe({
      next: (response) => {
        console.log('Customers response:', response);
        this.customers = response.data || [];
        this.isLoading = false;
        this.snackBar.open('Clients chargés avec succès', 'Fermer', {
          duration: 3000
        });
      },
      error: (error) => {
        console.error('Customers error:', error);
        this.error = 'Impossible de charger les clients. Vérifiez la connexion au backend.';
        this.isLoading = false;
        this.snackBar.open('Erreur de connexion au backend', 'Fermer', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }
}
