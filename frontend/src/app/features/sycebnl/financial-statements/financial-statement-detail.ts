import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatMenuModule } from '@angular/material/menu';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTableModule } from '@angular/material/table';
import { Subject, takeUntil } from 'rxjs';

import { AuthService } from '../../../shared/services/auth';
import { NotificationService } from '../../../shared/services/notification';
import { LoadingService } from '../../../shared/services/loading';
import { 
  SycebnlFinancialStatementsService, 
  EtatFinancierSycebnl,
  NoteAnnexeSycebnl 
} from '../../../shared/services/sycebnl-financial-statements.service';
import { User } from '../../../shared/models/user';

@Component({
  selector: 'app-financial-statement-detail',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatDividerModule,
    MatChipsModule,
    MatTooltipModule,
    MatMenuModule,
    MatTabsModule,
    MatTableModule
  ],
  templateUrl: './financial-statement-detail.html',
  styleUrl: './financial-statement-detail.scss'
})
export class FinancialStatementDetail implements OnInit, OnDestroy {
  currentUser: User | null = null;
  isLoading = false;
  private destroy$ = new Subject<void>();

  // Données
  financialStatement: EtatFinancierSycebnl | null = null;
  notesAnnexes: NoteAnnexeSycebnl[] = [];
  statementData: any = null;

  // Colonnes du tableau des données
  displayedColumns: string[] = ['code', 'libelle', 'montant'];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private notificationService: NotificationService,
    private loadingService: LoadingService,
    private financialStatementsService: SycebnlFinancialStatementsService
  ) {}

  ngOnInit(): void {
    this.setupUserSubscription();
    this.loadFinancialStatement();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private setupUserSubscription(): void {
    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        this.currentUser = user;
      });
  }

  private loadFinancialStatement(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.notificationService.showError('ID de l\'état financier manquant');
      this.router.navigate(['/sycebnl/financial-statements']);
      return;
    }

    this.isLoading = true;
    this.loadingService.show();

    this.financialStatementsService.getFinancialStatementById(+id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (statement) => {
          this.financialStatement = statement;
          this.parseStatementData();
          this.loadNotesAnnexes(+id);
          this.isLoading = false;
          this.loadingService.hide();
        },
        error: (error) => {
          console.error('Erreur lors du chargement de l\'état financier:', error);
          this.isLoading = false;
          this.loadingService.hide();
          this.notificationService.showError('Erreur lors du chargement de l\'état financier');
          this.router.navigate(['/sycebnl/financial-statements']);
        }
      });
  }

  private loadNotesAnnexes(statementId: number): void {
    this.financialStatementsService.getFinancialStatementNotes(statementId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (notes) => {
          this.notesAnnexes = notes;
        },
        error: (error) => {
          console.error('Erreur lors du chargement des notes annexes:', error);
        }
      });
  }

  private parseStatementData(): void {
    if (this.financialStatement?.donneesJson) {
      try {
        this.statementData = JSON.parse(this.financialStatement.donneesJson);
      } catch (error) {
        console.error('Erreur lors du parsing des données JSON:', error);
        this.statementData = null;
      }
    }
  }

  // Actions
  validateStatement(): void {
    if (!this.financialStatement?.id) return;

    this.loadingService.show();
    this.financialStatementsService.validateFinancialStatement(this.financialStatement.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          this.loadingService.hide();
          if (result.valide) {
            this.notificationService.showSuccess('État financier validé avec succès');
            this.loadFinancialStatement(); // Recharger pour mettre à jour le statut
          } else {
            this.notificationService.showWarning(`Validation échouée: ${result.erreurs.join(', ')}`);
          }
        },
        error: (error) => {
          this.loadingService.hide();
          this.notificationService.showError('Erreur lors de la validation');
        }
      });
  }

  exportStatement(format: string): void {
    if (!this.financialStatement?.id) return;

    this.loadingService.show();
    this.financialStatementsService.exportFinancialStatement(this.financialStatement.id, format)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (blob) => {
          this.loadingService.hide();
          const filename = `${this.financialStatement?.typeEtat}_${this.financialStatement?.typeSysteme}_${this.financialStatement?.dateArrete}.${format.toLowerCase()}`;
          this.financialStatementsService.downloadFile(blob, filename);
          this.notificationService.showSuccess(`Export ${format} réussi`);
        },
        error: (error) => {
          this.loadingService.hide();
          this.notificationService.showError('Erreur lors de l\'export');
        }
      });
  }

  editStatement(): void {
    if (this.financialStatement?.id) {
      this.router.navigate(['/sycebnl/financial-statements/edit', this.financialStatement.id]);
    }
  }

  goBack(): void {
    this.router.navigate(['/sycebnl/financial-statements']);
  }

  // Méthodes utilitaires
  getFinancialStatementTypeIcon(type: string): string {
    return this.financialStatementsService.getFinancialStatementTypeIcon(type);
  }

  getFinancialStatementTypeColor(type: string): string {
    switch (type) {
      case 'BILAN': return 'blue';
      case 'COMPTE_RESULTAT': return 'green';
      case 'TABLEAU_FLUX': return 'purple';
      case 'RECETTES_DEPENSES': return 'orange';
      case 'SITUATION_TRESORERIE': return 'teal';
      case 'ANNEXES': return 'brown';
      default: return 'gray';
    }
  }

  getSystemTypeColor(system: string): string {
    return system === 'NORMAL' ? 'green' : 'orange';
  }

  getStatusColor(status: string): string {
    return this.financialStatementsService.getStatusColor(status);
  }

  getStatusIcon(status: string): string {
    return this.financialStatementsService.getStatusIcon(status);
  }

  formatAmount(amount: number | undefined): string {
    if (amount === undefined || amount === null) return '-';
    return this.financialStatementsService.formatAmount(amount);
  }

  formatDate(date: string): string {
    return this.financialStatementsService.formatDate(date);
  }

  getEquilibreIcon(equilibre: boolean): string {
    return equilibre ? 'check_circle' : 'cancel';
  }

  getEquilibreColor(equilibre: boolean): string {
    return equilibre ? 'green' : 'red';
  }

  // Méthodes pour le tableau des données
  getStatementDataArray(): any[] {
    if (!this.statementData) return [];
    
    return Object.entries(this.statementData).map(([key, value]: [string, any]) => ({
      code: key,
      libelle: value.libelle || key,
      montant: value.solde || 0
    }));
  }
}






