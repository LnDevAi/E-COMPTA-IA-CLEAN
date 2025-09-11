import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';

import { DocumentExplorerComponent } from '../document-explorer/document-explorer';

@Component({
  selector: 'app-ged',
  standalone: true,
  imports: [
    CommonModule,
    MatTabsModule,
    MatIconModule,
    MatButtonModule,
    MatSnackBarModule,
    MatDialogModule,
    DocumentExplorerComponent
  ],
  templateUrl: './ged.html',
  styleUrls: ['./ged.scss']
})
export class GedComponent implements OnInit {

  constructor(
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    // Initialisation du composant GED
  }

  openUploadDialog(): void {
    // TODO: Ouvrir le dialog d'upload
    this.snackBar.open('Fonctionnalité d\'upload en cours de développement', 'Fermer', { duration: 3000 });
  }

  async openWorkflowManager(): Promise<void> {
    const { WorkflowManagerComponent } = await import('../workflow-manager/workflow-manager');
    
    const dialogRef = this.dialog.open(WorkflowManagerComponent, {
      width: '95vw',
      height: '95vh',
      maxWidth: '100vw',
      maxHeight: '100vh',
      panelClass: 'workflow-manager-dialog'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        // Actions après fermeture du gestionnaire de workflows si nécessaire
      }
    });
  }
}
