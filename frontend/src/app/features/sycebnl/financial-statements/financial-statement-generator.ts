import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatStepperModule, MatStepper } from '@angular/material/stepper';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatMenuModule } from '@angular/material/menu';
import { Subject, takeUntil } from 'rxjs';

import { AuthService } from '../../../shared/services/auth';
import { NotificationService } from '../../../shared/services/notification';
import { LoadingService } from '../../../shared/services/loading';
import { 
  SycebnlFinancialStatementsService, 
  CreateEtatFinancierRequest,
  EtatFinancierSycebnl 
} from '../../../shared/services/sycebnl-financial-statements.service';
import { SycebnlOrganizationService, SycebnlOrganization } from '../../../shared/services/sycebnl-organization.service';
import { User } from '../../../shared/models/user';

@Component({
  selector: 'app-financial-statement-generator',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatStepperModule,
    MatProgressSpinnerModule,
    MatDividerModule,
    MatChipsModule,
    MatTooltipModule,
    MatExpansionModule,
    MatCheckboxModule,
    MatMenuModule
  ],
  templateUrl: './financial-statement-generator.html',
  styleUrl: './financial-statement-generator.scss'
})
export class FinancialStatementGenerator implements OnInit, OnDestroy {
  currentUser: User | null = null;
  isLoading = false;
  isGenerating = false;
  private destroy$ = new Subject<void>();

  // Formulaires
  basicInfoForm: FormGroup;
  statementSelectionForm: FormGroup;
  generationForm: FormGroup;

  // Données
  organizations: SycebnlOrganization[] = [];
  selectedOrganization: SycebnlOrganization | null = null;
  generatedStatements: EtatFinancierSycebnl[] = [];

  // Options
  systemTypes = [
    { 
      value: 'NORMAL', 
      label: 'Système Normal (SN)', 
      description: 'Système comptable complet pour les grandes organisations',
      icon: 'account_balance',
      color: 'green'
    },
    { 
      value: 'MINIMAL', 
      label: 'Système Minimal de Trésorerie (SMT)', 
      description: 'Système comptable simplifié pour les petites organisations',
      icon: 'account_balance_wallet',
      color: 'orange'
    }
  ];

  financialStatementTypes = {
    NORMAL: [
      { value: 'BILAN', label: 'Bilan', description: 'État de la situation financière', icon: 'account_balance' },
      { value: 'COMPTE_RESULTAT', label: 'Compte de Résultat', description: 'Résultat des activités', icon: 'trending_up' },
      { value: 'TABLEAU_FLUX', label: 'Tableau des Flux de Trésorerie', description: 'Mouvements de trésorerie', icon: 'swap_horiz' },
      { value: 'ANNEXES', label: 'Notes Annexes', description: 'Informations complémentaires', icon: 'description' }
    ],
    MINIMAL: [
      { value: 'BILAN', label: 'Bilan SMT', description: 'État de la situation financière simplifié', icon: 'account_balance' },
      { value: 'RECETTES_DEPENSES', label: 'État des Recettes et Dépenses', description: 'Recettes et dépenses de l\'exercice', icon: 'receipt' },
      { value: 'SITUATION_TRESORERIE', label: 'Situation de Trésorerie', description: 'Situation de trésorerie', icon: 'account_balance_wallet' },
      { value: 'ANNEXES', label: 'Notes Annexes SMT', description: 'Informations complémentaires simplifiées', icon: 'description' }
    ]
  };

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private notificationService: NotificationService,
    private loadingService: LoadingService,
    private financialStatementsService: SycebnlFinancialStatementsService,
    public organizationService: SycebnlOrganizationService,
    private router: Router
  ) {
    this.basicInfoForm = this.fb.group({
      organizationId: ['', Validators.required],
      exerciceId: ['', Validators.required],
      dateArrete: ['', Validators.required]
    });

    this.statementSelectionForm = this.fb.group({
      typeSysteme: ['', Validators.required],
      selectedStatements: [[], Validators.required]
    });

    this.generationForm = this.fb.group({
      generateAll: [false],
      includeNotes: [true],
      validateAfterGeneration: [true]
    });
  }

  ngOnInit(): void {
    this.loadOrganizations();
    this.setupUserSubscription();
    this.setupFormSubscriptions();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadOrganizations(): void {
    this.isLoading = true;
    this.organizationService.getAllSycebnlOrganizations()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (organizations) => {
          this.organizations = organizations;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Erreur lors du chargement des organisations:', error);
          this.isLoading = false;
          this.notificationService.showError('Erreur lors du chargement des organisations');
        }
      });
  }

  private setupUserSubscription(): void {
    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        this.currentUser = user;
      });
  }

  private setupFormSubscriptions(): void {
    // Mise à jour de l'organisation sélectionnée
    this.basicInfoForm.get('organizationId')?.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(organizationId => {
        this.selectedOrganization = this.organizations.find(org => org.id === organizationId) || null;
      });

    // Mise à jour des types d'états disponibles selon le système
    this.statementSelectionForm.get('typeSysteme')?.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(typeSysteme => {
        this.statementSelectionForm.get('selectedStatements')?.setValue([]);
      });
  }

  // Navigation dans le stepper
  goBack(stepper: MatStepper): void {
    stepper.previous();
  }

  goForward(stepper: MatStepper): void {
    stepper.next();
  }

  // Gestion de la sélection des états financiers
  toggleStatementSelection(statementType: string): void {
    const currentSelection = this.statementSelectionForm.get('selectedStatements')?.value || [];
    const index = currentSelection.indexOf(statementType);
    
    if (index > -1) {
      currentSelection.splice(index, 1);
    } else {
      currentSelection.push(statementType);
    }
    
    this.statementSelectionForm.get('selectedStatements')?.setValue(currentSelection);
  }

  isStatementSelected(statementType: string): boolean {
    const currentSelection = this.statementSelectionForm.get('selectedStatements')?.value || [];
    return currentSelection.includes(statementType);
  }

  selectAllStatements(): void {
    const typeSysteme = this.statementSelectionForm.get('typeSysteme')?.value;
    if (typeSysteme) {
      const allStatements = this.financialStatementTypes[typeSysteme as keyof typeof this.financialStatementTypes]
        .map(stmt => stmt.value);
      this.statementSelectionForm.get('selectedStatements')?.setValue(allStatements);
    }
  }

  clearStatementSelection(): void {
    this.statementSelectionForm.get('selectedStatements')?.setValue([]);
  }

  // Génération des états financiers
  generateStatements(stepper: MatStepper): void {
    if (!this.basicInfoForm.valid || !this.statementSelectionForm.valid) {
      this.notificationService.showWarning('Veuillez remplir tous les champs obligatoires');
      return;
    }

    const basicInfo = this.basicInfoForm.value;
    const statementSelection = this.statementSelectionForm.value;
    const generationOptions = this.generationForm.value;

    this.isGenerating = true;
    this.loadingService.show();

    if (generationOptions.generateAll) {
      // Générer tous les états financiers
      this.financialStatementsService.generateAllFinancialStatements(
        basicInfo.exerciceId,
        basicInfo.organizationId,
        statementSelection.typeSysteme
      )
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (statements) => {
            this.generatedStatements = statements;
            this.isGenerating = false;
            this.loadingService.hide();
            this.notificationService.showSuccess(`${statements.length} états financiers générés avec succès`);
            stepper.next();
          },
          error: (error) => {
            console.error('Erreur lors de la génération:', error);
            this.isGenerating = false;
            this.loadingService.hide();
            this.notificationService.showError('Erreur lors de la génération des états financiers');
          }
        });
    } else {
      // Générer les états sélectionnés un par un
      const selectedStatements = statementSelection.selectedStatements;
      this.generateStatementsSequentially(selectedStatements, basicInfo, stepper);
    }
  }

  private generateStatementsSequentially(
    statementTypes: string[], 
    basicInfo: any, 
    stepper: MatStepper, 
    index: number = 0
  ): void {
    if (index >= statementTypes.length) {
      this.isGenerating = false;
      this.loadingService.hide();
      this.notificationService.showSuccess(`${this.generatedStatements.length} états financiers générés avec succès`);
      stepper.next();
      return;
    }

    const request: CreateEtatFinancierRequest = {
      exerciceId: basicInfo.exerciceId,
      entiteId: basicInfo.organizationId,
      typeSysteme: this.statementSelectionForm.get('typeSysteme')?.value,
      typeEtat: statementTypes[index],
      dateArrete: basicInfo.dateArrete
    };

    this.financialStatementsService.generateFinancialStatement(request)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (statement) => {
          this.generatedStatements.push(statement);
          this.generateStatementsSequentially(statementTypes, basicInfo, stepper, index + 1);
        },
        error: (error) => {
          console.error(`Erreur lors de la génération de ${statementTypes[index]}:`, error);
          this.notificationService.showError(`Erreur lors de la génération de ${statementTypes[index]}`);
          this.generateStatementsSequentially(statementTypes, basicInfo, stepper, index + 1);
        }
      });
  }

  // Validation des états générés
  validateGeneratedStatements(): void {
    if (this.generatedStatements.length === 0) {
      this.notificationService.showWarning('Aucun état financier à valider');
      return;
    }

    this.loadingService.show();
    const exerciceId = this.basicInfoForm.get('exerciceId')?.value;

    this.financialStatementsService.validateAllFinancialStatements(exerciceId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          this.loadingService.hide();
          if (result.valide) {
            this.notificationService.showSuccess('Tous les états financiers sont valides');
          } else {
            this.notificationService.showWarning(`Validation terminée avec ${result.nombreErreurs} erreur(s) et ${result.nombreAvertissements} avertissement(s)`);
          }
        },
        error: (error) => {
          this.loadingService.hide();
          this.notificationService.showError('Erreur lors de la validation');
        }
      });
  }

  // Export des états générés
  exportGeneratedStatements(format: string): void {
    if (this.generatedStatements.length === 0) {
      this.notificationService.showWarning('Aucun état financier à exporter');
      return;
    }

    this.loadingService.show();
    const exerciceId = this.basicInfoForm.get('exerciceId')?.value;

    this.financialStatementsService.exportAllFinancialStatements(exerciceId, format)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (blob) => {
          this.loadingService.hide();
          const filename = `etats_financiers_${exerciceId}.${format.toLowerCase()}`;
          this.financialStatementsService.downloadFile(blob, filename);
          this.notificationService.showSuccess(`Export ${format} réussi`);
        },
        error: (error) => {
          this.loadingService.hide();
          this.notificationService.showError('Erreur lors de l\'export');
        }
      });
  }

  // Navigation
  viewStatement(statement: EtatFinancierSycebnl): void {
    this.router.navigate(['/sycebnl/financial-statements/view', statement.id]);
  }

  goToStatementsList(): void {
    this.router.navigate(['/sycebnl/financial-statements']);
  }

  // Méthodes utilitaires
  getAvailableStatements(): any[] {
    const typeSysteme = this.statementSelectionForm.get('typeSysteme')?.value;
    return typeSysteme ? this.financialStatementTypes[typeSysteme as keyof typeof this.financialStatementTypes] : [];
  }

  getSystemTypeIcon(system: string): string {
    const systemObj = this.systemTypes.find(s => s.value === system);
    return systemObj ? systemObj.icon : 'account_balance';
  }

  getSystemTypeColor(system: string): string {
    const systemObj = this.systemTypes.find(s => s.value === system);
    return systemObj ? systemObj.color : 'gray';
  }

  getStatementTypeIcon(type: string): string {
    const availableStatements = this.getAvailableStatements();
    const statementObj = availableStatements.find(s => s.value === type);
    return statementObj ? statementObj.icon : 'description';
  }

  getFinancialStatementTypeColor(type: string): string {
    switch (type) {
      case 'BILAN': return 'blue';
      case 'COMPTE_RESULTAT': return 'green';
      case 'TABLEAU_FLUX': return 'orange';
      case 'RECETTES_DEPENSES': return 'purple';
      case 'SITUATION_TRESORERIE': return 'teal';
      case 'ANNEXES': return 'gray';
      default: return 'gray';
    }
  }

  formatDate(date: string): string {
    return this.financialStatementsService.formatDate(date);
  }

  formatAmount(amount: number | undefined): string {
    if (amount === undefined || amount === null) return '-';
    return this.financialStatementsService.formatAmount(amount);
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'BROUILLON': return 'gray';
      case 'VALIDE': return 'green';
      case 'CLOTURE': return 'blue';
      default: return 'gray';
    }
  }

  getStatusIcon(status: string): string {
    switch (status) {
      case 'BROUILLON': return 'edit';
      case 'VALIDE': return 'check_circle';
      case 'CLOTURE': return 'lock';
      default: return 'help';
    }
  }
}
