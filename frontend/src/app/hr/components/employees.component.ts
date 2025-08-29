import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ApiService } from '../../core/services/api';

@Component({
  selector: 'app-employees',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatTableModule, MatButtonModule, MatIconModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">Gestion des employés</h1>
        <button mat-raised-button color="primary" (click)="loadEmployees()">
          <mat-icon>refresh</mat-icon>
          Actualiser
        </button>
      </div>
      
      <!-- Loading -->
      <div *ngIf="isLoading" class="loading-container">
        <mat-card>
          <mat-card-content>
            <p>Chargement des employés...</p>
          </mat-card-content>
        </mat-card>
      </div>

      <!-- Error -->
      <div *ngIf="error && !isLoading" class="error-container">
        <mat-card>
          <mat-card-content>
            <p class="error-message">{{ error }}</p>
            <button mat-raised-button color="primary" (click)="loadEmployees()">
              <mat-icon>refresh</mat-icon>
              Réessayer
            </button>
          </mat-card-content>
        </mat-card>
      </div>

      <!-- Data -->
      <div *ngIf="!isLoading && !error && employees.length > 0">
        <mat-card>
          <mat-card-content>
            <table mat-table [dataSource]="employees" class="employees-table">
              <ng-container matColumnDef="name">
                <th mat-header-cell *matHeaderCellDef>Nom</th>
                <td mat-cell *matCellDef="let employee">{{ employee.name }}</td>
              </ng-container>

              <ng-container matColumnDef="position">
                <th mat-header-cell *matHeaderCellDef>Poste</th>
                <td mat-cell *matCellDef="let employee">{{ employee.position }}</td>
              </ng-container>

              <ng-container matColumnDef="department">
                <th mat-header-cell *matHeaderCellDef>Département</th>
                <td mat-cell *matCellDef="let employee">{{ employee.department }}</td>
              </ng-container>

              <ng-container matColumnDef="salary">
                <th mat-header-cell *matHeaderCellDef>Salaire</th>
                <td mat-cell *matCellDef="let employee">{{ employee.salary | number:'1.0-0' }} FCFA</td>
              </ng-container>

              <ng-container matColumnDef="hireDate">
                <th mat-header-cell *matHeaderCellDef>Date d'embauche</th>
                <td mat-cell *matCellDef="let employee">{{ employee.hireDate | date:'dd/MM/yyyy' }}</td>
              </ng-container>

              <ng-container matColumnDef="status">
                <th mat-header-cell *matHeaderCellDef>Statut</th>
                <td mat-cell *matCellDef="let employee">
                  <span [class]="'status-' + employee.status.toLowerCase()">{{ employee.status }}</span>
                </td>
              </ng-container>

              <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
              <tr mat-row *matRowDef="let row; columns: displayedColumns;"></tr>
            </table>
          </mat-card-content>
        </mat-card>
      </div>

      <!-- No data -->
      <div *ngIf="!isLoading && !error && employees.length === 0">
        <mat-card>
          <mat-card-content>
            <p>Aucun employé trouvé.</p>
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
    .employees-table {
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
    .status-on-leave {
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
export class EmployeesComponent implements OnInit {
  employees: any[] = [];
  isLoading = true;
  error: string | null = null;
  displayedColumns: string[] = ['name', 'position', 'department', 'salary', 'hireDate', 'status'];

  constructor(
    private apiService: ApiService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadEmployees();
  }

  loadEmployees(): void {
    this.isLoading = true;
    this.error = null;

    this.apiService.getEmployees().subscribe({
      next: (response) => {
        console.log('Employees response:', response);
        this.employees = response.data || [];
        this.isLoading = false;
        this.snackBar.open('Employés chargés avec succès', 'Fermer', {
          duration: 3000
        });
      },
      error: (error) => {
        console.error('Employees error:', error);
        this.error = 'Impossible de charger les employés. Vérifiez la connexion au backend.';
        this.isLoading = false;
        this.snackBar.open('Erreur de connexion au backend', 'Fermer', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }
}


