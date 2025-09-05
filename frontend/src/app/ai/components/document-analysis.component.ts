import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-document-analysis',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">Intelligence Artificielle</h1>
      </div>
      <mat-card>
        <mat-card-content>
          <p>Module d'intelligence artificielle en cours de développement...</p>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: []
})
export class DocumentAnalysisComponent {}





