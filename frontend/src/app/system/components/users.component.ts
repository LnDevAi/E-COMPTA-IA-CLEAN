import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">Gestion du système</h1>
      </div>
      <mat-card>
        <mat-card-content>
          <p>Module de gestion du système en cours de développement...</p>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: []
})
export class UsersComponent {}



