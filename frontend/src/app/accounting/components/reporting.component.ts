import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { ApiService } from '../../core/services/api.service';

@Component({
  selector: 'app-accounting-reporting',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatTableModule, MatSnackBarModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">Reporting comptable</h1>
        <div>
          <button mat-raised-button color="primary" (click)="loadTrialBalance()">Balance générale</button>
          <button mat-raised-button color="accent" (click)="loadGeneralLedger()">Grand Livre</button>
        </div>
      </div>

      <mat-card *ngIf="trialBalance">
        <mat-card-title>Balance générale</mat-card-title>
        <mat-card-content>
          <pre>{{ trialBalance | json }}</pre>
        </mat-card-content>
      </mat-card>

      <mat-card *ngIf="generalLedger">
        <mat-card-title>Grand Livre</mat-card-title>
        <mat-card-content>
          <pre>{{ generalLedger | json }}</pre>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .page-container { padding: 20px; }
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
    .page-title { margin: 0; }
    pre { white-space: pre-wrap; word-wrap: break-word; }
  `]
})
export class ReportingComponent implements OnInit {
  trialBalance: any | null = null;
  generalLedger: any | null = null;

  constructor(private api: ApiService, private snackBar: MatSnackBar) {}

  ngOnInit(): void {}

  loadTrialBalance(): void {
    const companyId = 1;
    const asOfDate = new Date().toISOString().slice(0, 10);
    this.api.getTrialBalance(companyId, asOfDate).subscribe({
      next: (data: any) => {
        this.trialBalance = data;
        this.snackBar.open('Balance chargée', 'Fermer', { duration: 3000 });
      },
      error: (_: any) => this.snackBar.open('Erreur chargement balance', 'Fermer', { duration: 4000, panelClass: ['error-snackbar'] })
    });
  }

  loadGeneralLedger(): void {
    const companyId = 1;
    const asOfDate = new Date().toISOString().slice(0, 10);
    this.api.getGeneralLedger(companyId, asOfDate).subscribe({
      next: (data: any) => {
        this.generalLedger = data;
        this.snackBar.open('Grand Livre chargé', 'Fermer', { duration: 3000 });
      },
      error: (_: any) => this.snackBar.open('Erreur chargement Grand Livre', 'Fermer', { duration: 4000, panelClass: ['error-snackbar'] })
    });
  }
}