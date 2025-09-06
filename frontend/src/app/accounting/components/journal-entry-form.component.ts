import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ApiService } from '../../../core/services/api.service';

@Component({
  selector: 'app-journal-entry-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatCardModule, MatButtonModule, MatFormFieldModule, MatInputModule],
  template: `
    <div class="page-container">
      <mat-card>
        <mat-card-title>Créer une écriture comptable</mat-card-title>
        <mat-card-content>
          <form [formGroup]="form" (ngSubmit)="submit()">
            <div class="grid">
              <mat-form-field appearance="outline">
                <mat-label>Numéro</mat-label>
                <input matInput formControlName="entryNumber" placeholder="EC-2025-001">
              </mat-form-field>

              <mat-form-field appearance="outline">
                <mat-label>Date</mat-label>
                <input matInput formControlName="entryDate" placeholder="2025-01-15">
              </mat-form-field>

              <mat-form-field appearance="outline">
                <mat-label>Description</mat-label>
                <input matInput formControlName="description" placeholder="Description">
              </mat-form-field>

              <mat-form-field appearance="outline">
                <mat-label>Journal</mat-label>
                <input matInput formControlName="journalType" placeholder="VENTES">
              </mat-form-field>

              <mat-form-field appearance="outline">
                <mat-label>Devise</mat-label>
                <input matInput formControlName="currency" placeholder="XOF">
              </mat-form-field>

              <mat-form-field appearance="outline">
                <mat-label>Total Débit</mat-label>
                <input matInput formControlName="totalDebit" placeholder="0">
              </mat-form-field>

              <mat-form-field appearance="outline">
                <mat-label>Total Crédit</mat-label>
                <input matInput formControlName="totalCredit" placeholder="0">
              </mat-form-field>
            </div>

            <button mat-raised-button color="primary" type="submit" [disabled]="form.invalid || submitting">Créer</button>
          </form>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .page-container { padding: 20px; }
    .grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }
  `]
})
export class JournalEntryFormComponent {
  submitting = false;
  form = this.fb.group({
    entryNumber: ['', Validators.required],
    entryDate: ['', Validators.required],
    description: ['', Validators.required],
    journalType: ['VENTES', Validators.required],
    currency: ['XOF', Validators.required],
    totalDebit: ['0', Validators.required],
    totalCredit: ['0', Validators.required],
    companyId: [1],
    countryCode: ['CI'],
    accountingStandard: ['OHADA']
  });

  constructor(private fb: FormBuilder, private api: ApiService, private snackBar: MatSnackBar) {}

  submit(): void {
    if (this.form.invalid) return;
    this.submitting = true;
    this.api.post(`/api/accounting/journal-entries`, this.form.value).subscribe({
      next: () => {
        this.snackBar.open('Écriture créée', 'Fermer', { duration: 3000 });
        this.submitting = false;
      },
      error: () => {
        this.snackBar.open('Erreur création écriture', 'Fermer', { duration: 4000, panelClass: ['error-snackbar'] });
        this.submitting = false;
      }
    });
  }
}

