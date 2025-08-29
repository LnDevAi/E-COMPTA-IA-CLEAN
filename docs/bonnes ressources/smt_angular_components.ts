// =====================================================
// COMPOSANTS ANGULAR - SYSTÈME MINIMAL DE TRÉSORERIE
// E COMPTA IA - Interface Utilisateur SMT
// =====================================================

// components/dashboard-smt/dashboard-smt.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject, combineLatest } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import {
  EntrepriseSMTService,
  ExerciceSMTService,
  CompteTresorerieService,
  LivreRecetteService,
  LivreDepenseService,
  EntrepriseSMT,
  ExerciceSMT,
  CompteTresorerie
} from '../../services/smt.services';

@Component({
  selector: 'app-dashboard-smt',
  templateUrl: './dashboard-smt.component.html',
  styleUrls: ['./dashboard-smt.component.scss']
})
export class DashboardSMTComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  entrepriseCourante: EntrepriseSMT | null = null;
  exerciceCourant: ExerciceSMT | null = null;
  comptesTresorerie: CompteTresorerie[] = [];

  // Indicateurs du tableau de bord
  indicateurs = {
    tresorerieDisponible: 0,
    recettesDuMois: 0,
    depensesDuMois: 0,
    soldeNetMois: 0,
    nombreRecettes: 0,
    nombreDepenses: 0,
    evolutionTresorerie: [] as {date: string, solde: number}[]
  };

  loading = true;
  erreur: string | null = null;

  constructor(
    private entrepriseService: EntrepriseSMTService,
    private exerciceService: ExerciceSMTService,
    private compteService: CompteTresorerieService,
    private recetteService: LivreRecetteService,
    private depenseService: LivreDepenseService
  ) {}

  ngOnInit(): void {
    this.chargerDonneesDashboard();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private chargerDonneesDashboard(): void {
    this.loading = true;
    
    // Charger entreprise et exercice courants
    combineLatest([
      this.entrepriseService.obtenirEntrepriseCourante(),
      this.exerciceService.exerciceCourant()
    ]).pipe(takeUntil(this.destroy$))
    .subscribe({
      next: ([entreprise, exercice]) => {
        this.entrepriseCourante = entreprise;
        this.exerciceCourant = exercice;
        
        if (entreprise && exercice) {
          this.chargerIndicateurs();
        } else {
          this.loading = false;
        }
      },
      error: (error) => {
        console.error('Erreur chargement données courantes:', error);
        this.erreur = 'Erreur lors du chargement des données';
        this.loading = false;
      }
    });
  }

  private chargerIndicateurs(): void {
    if (!this.entrepriseCourante || !this.exerciceCourant) return;

    // Charger les comptes de trésorerie
    this.compteService.listerComptesEntreprise(this.entrepriseCourante.id!)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (comptes) => {
          this.comptesTresorerie = comptes.filter(c => c.actif);
          this.calculerTresorerieDisponible();
        },
        error: (error) => console.error('Erreur chargement comptes:', error)
      });

    // Dates du mois courant
    const maintenant = new Date();
    const debutMois = new Date(maintenant.getFullYear(), maintenant.getMonth(), 1);
    const finMois = new Date(maintenant.getFullYear(), maintenant.getMonth() + 1, 0);

    // Statistiques des recettes du mois
    this.recetteService.obtenirStatistiques(this.entrepriseCourante.id!, this.exerciceCourant.id!)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (stats) => {
          this.indicateurs.recettesDuMois = stats.totalRecettes;
          // Compter les recettes du mois courant
          const recettesMoisCourant = stats.evolutionMensuelle.find(
            m => m.mois === `${maintenant.getFullYear()}-${(maintenant.getMonth() + 1).toString().padStart(2, '0')}`
          );
          this.indicateurs.recettesDuMois = recettesMoisCourant?.montant || 0;
          this.calculerSoldeNet();
        },
        error: (error) => console.error('Erreur stats recettes:', error)
      });

    // Statistiques des dépenses du mois
    this.depenseService.obtenirStatistiques(this.entrepriseCourante.id!, this.exerciceCourant.id!)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (stats) => {
          this.indicateurs.depensesDuMois = stats.totalDepenses;
          // Compter les dépenses du mois courant
          const depensesMoisCourant = stats.evolutionMensuelle.find(
            m => m.mois === `${maintenant.getFullYear()}-${(maintenant.getMonth() + 1).toString().padStart(2, '0')}`
          );
          this.indicateurs.depensesDuMois = depensesMoisCourant?.montant || 0;
          this.calculerSoldeNet();
        },
        error: (error) => console.error('Erreur stats dépenses:', error)
      });

    this.loading = false;
  }

  private calculerTresorerieDisponible(): void {
    let total = 0;
    const promises = this.comptesTresorerie.map(compte => 
      this.compteService.calculerSolde(compte.id!).toPromise()
    );

    Promise.all(promises)
      .then(soldes => {
        soldes.forEach(soldeInfo => {
          if (soldeInfo) {
            total += soldeInfo.solde;
          }
        });
        this.indicateurs.tresorerieDisponible = total;
      })
      .catch(error => console.error('Erreur calcul trésorerie:', error));
  }

  private calculerSoldeNet(): void {
    this.indicateurs.soldeNetMois = this.indicateurs.recettesDuMois - this.indicateurs.depensesDuMois;
  }

  formaterMontant(montant: number): string {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: this.entrepriseCourante?.deviseTenueComptes || 'XOF',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(montant);
  }

  obtenirCouleurSolde(montant: number): string {
    if (montant > 0) return 'text-success';
    if (montant < 0) return 'text-danger';
    return 'text-muted';
  }

  rafraichirDonnees(): void {
    this.chargerDonneesDashboard();
  }
}

// components/livre-recettes/livre-recettes.component.ts
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatDialog } from '@angular/material/dialog';
import { 
  LivreRecetteService, 
  EntrepriseSMTService,
  ExerciceSMTService,
  CompteTresorerieService,
  PosteClassificationService,
  ValidationSMTService,
  LivreRecette,
  CompteTresorerie,
  PosteClassificationSMT
} from '../../services/smt.services';

@Component({
  selector: 'app-livre-recettes',
  templateUrl: './livre-recettes.component.html',
  styleUrls: ['./livre-recettes.component.scss']
})
export class LivreRecettesComponent implements OnInit {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  recetteForm: FormGroup;
  dataSource = new MatTableDataSource<LivreRecette>();
  displayedColumns = [
    'numeroOperation', 'dateOperation', 'designationOperation',
    'clientPayeur', 'montant', 'posteRecette', 'valide', 'actions'
  ];

  comptesTresorerie: CompteTresorerie[] = [];
  postesRecettes: PosteClassificationSMT[] = [];
  originesRecettes = [
    'VENTE', 'PRESTATION_SERVICE', 'LOCATION', 'COMMISSION',
    'SUBVENTION', 'PRET', 'APPORT_CAPITAL', 'AUTRE'
  ];
  modesReglement = [
    'ESPECES', 'CHEQUE', 'VIREMENT', 'MOBILE_MONEY', 'CARTE_BANCAIRE'
  ];

  loading = false;
  ajoutEnCours = false;
  erreur: string | null = null;
  entrepriseId: number | null = null;
  exerciceId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private recetteService: LivreRecetteService,
    private entrepriseService: EntrepriseSMTService,
    private exerciceService: ExerciceSMTService,
    private compteService: CompteTresorerieService,
    private posteService: PosteClassificationService,
    private validationService: ValidationSMTService,
    private dialog: MatDialog
  ) {
    this.recetteForm = this.creerFormulaire();
  }

  ngOnInit(): void {
    this.initialiserComposant();
  }

  private initialiserComposant(): void {
    // Charger les données de référence
    Promise.all([
      this.chargerEntrepriseEtExercice(),
      this.chargerComptesEtPostes()
    ]).then(() => {
      this.chargerRecettes();
    });
  }

  private async chargerEntrepriseEtExercice(): Promise<void> {
    try {
      const entreprise = await this.entrepriseService.obtenirEntrepriseCourante().toPromise();
      const exercice = await this.exerciceService.exerciceCourant().toPromise();
      
      if (entreprise && exercice) {
        this.entrepriseId = entreprise.id!;
        this.exerciceId = exercice.id!;
      }
    } catch (error) {
      console.error('Erreur chargement entreprise/exercice:', error);
    }
  }

  private async chargerComptesEtPostes(): Promise<void> {
    if (!this.entrepriseId) return;

    try {
      const [comptes, postesRecettes] = await Promise.all([
        this.compteService.listerComptesEntreprise(this.entrepriseId).toPromise(),
        this.posteService.obtenirPostesRecettes().toPromise()
      ]);

      this.comptesTresorerie = comptes?.filter(c => c.actif) || [];
      this.postesRecettes = postesRecettes || [];
    } catch (error) {
      console.error('Erreur chargement données référence:', error);
    }
  }

  private creerFormulaire(): FormGroup {
    return this.fb.group({
      compteTresorerieId: ['', Validators.required],
      dateOperation: [new Date(), Validators.required],
      designationOperation: ['', [Validators.required, Validators.maxLength(200)]],
      origineRecette: ['', Validators.required],
      clientPayeur: ['', Validators.maxLength(100)],
      modeReglement: ['', Validators.required],
      numeroPiece: [''],
      montant: [0, [Validators.required, Validators.min(1)]],
      posteRecette: ['', Validators.required],
      tvaCollectee: [0, Validators.min(0)],
      observations: ['', Validators.maxLength(500)],
      pieceJustificative: ['']
    });
  }

  chargerRecettes(): void {
    if (!this.entrepriseId || !this.exerciceId) return;

    this.loading = true;
    this.recetteService.listerRecettes({
      entrepriseId: this.entrepriseId,
      exerciceId: this.exerciceId,
      page: 0,
      taille: 100
    }).subscribe({
      next: (response) => {
        this.dataSource.data = response.content;
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
        this.loading = false;
      },
      error: (error) => {
        console.error('Erreur chargement recettes:', error);
        this.erreur = 'Erreur lors du chargement des recettes';
        this.loading = false;
      }
    });
  }

  ajouterRecette(): void {
    if (this.recetteForm.invalid || !this.entrepriseId || !this.exerciceId) {
      this.marquerChampsCommeToques();
      return;
    }

    // Validation métier
    const erreurs = this.validationService.validerRecette(this.recetteForm.value);
    if (erreurs.length > 0) {
      this.erreur = erreurs.join(', ');
      return;
    }

    this.ajoutEnCours = true;
    this.erreur = null;

    const nouvelleRecette: LivreRecette = {
      ...this.recetteForm.value,
      entrepriseId: this.entrepriseId,
      exerciceId: this.exerciceId,
      valide: false
    };

    this.recetteService.ajouterRecette(nouvelleRecette).subscribe({
      next: (recette) => {
        this.dataSource.data = [recette, ...this.dataSource.data];
        this.recetteForm.reset();
        this.ajoutEnCours = false;
        // Notification de succès
      },
      error: (error) => {
        console.error('Erreur ajout recette:', error);
        this.erreur = 'Erreur lors de l\'ajout de la recette';
        this.ajoutEnCours = false;
      }
    });
  }

  validerRecette(recette: LivreRecette): void {
    this.recetteService.validerRecette(recette.id!).subscribe({
      next: (recetteValidee) => {
        const index = this.dataSource.data.findIndex(r => r.id === recette.id);
        if (index >= 0) {
          this.dataSource.data[index] = recetteValidee;
          this.dataSource._updateChangeSubscription();
        }
      },
      error: (error) => {
        console.error('Erreur validation recette:', error);
        this.erreur = 'Erreur lors de la validation';
      }
    });
  }

  supprimerRecette(recette: LivreRecette): void {
    if (recette.valide) {
      this.erreur = 'Impossible de supprimer une recette validée';
      return;
    }

    if (confirm('Êtes-vous sûr de vouloir supprimer cette recette ?')) {
      this.recetteService.supprimerRecette(recette.id!).subscribe({
        next: () => {
          this.dataSource.data = this.dataSource.data.filter(r => r.id !== recette.id);
        },
        error: (error) => {
          console.error('Erreur suppression recette:', error);
          this.erreur = 'Erreur lors de la suppression';
        }
      });
    }
  }

  dupliquerRecette(recette: LivreRecette): void {
    const nouvelleDate = new Date();
    this.recetteService.dupliquerRecette(recette.id!, nouvelleDate).subscribe({
      next: (recetteDupliquee) => {
        this.dataSource.data = [recetteDupliquee, ...this.dataSource.data];
      },
      error: (error) => {
        console.error('Erreur duplication recette:', error);
        this.erreur = 'Erreur lors de la duplication';
      }
    });
  }

  private marquerChampsCommeToques(): void {
    Object.keys(this.recetteForm.controls).forEach(key => {
      this.recetteForm.get(key)?.markAsTouched();
    });
  }

  formaterMontant(montant: number): string {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'XOF',
      minimumFractionDigits: 0
    }).format(montant);
  }

  appliquerFiltre(event: Event): void {
    const valeur = (event.target as HTMLInputElement).value;
    this.dataSource.filter = valeur.trim().toLowerCase();
  }
}

// components/livre-depenses/livre-depenses.component.ts
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { 
  LivreDepenseService, 
  EntrepriseSMTService,
  ExerciceSMTService,
  CompteTresorerieService,
  PosteClassificationService,
  ValidationSMTService,
  LivreDepense,
  CompteTresorerie,
  PosteClassificationSMT
} from '../../services/smt.services';

@Component({
  selector: 'app-livre-depenses',
  templateUrl: './livre-depenses.component.html',
  styleUrls: ['./livre-depenses.component.scss']
})
export class LivreDepensesComponent implements OnInit {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  depenseForm: FormGroup;
  dataSource = new MatTableDataSource<LivreDepense>();
  displayedColumns = [
    'numeroOperation', 'dateOperation', 'designationOperation',
    'beneficiaire', 'montant', 'posteDepense', 'valide', 'actions'
  ];

  comptesTresorerie: CompteTresorerie[] = [];
  postesDepenses: PosteClassificationSMT[] = [];
  naturesDepenses = [
    'ACHAT_MARCHANDISE', 'ACHAT_MATIERE_PREMIERE', 'SERVICE_EXTERIEUR',
    'TRANSPORT', 'LOYER', 'SALAIRE', 'CHARGE_SOCIALE', 'IMPOT',
    'INVESTISSEMENT', 'REMBOURSEMENT_EMPRUNT', 'AUTRE'
  ];
  modesReglement = [
    'ESPECES', 'CHEQUE', 'VIREMENT', 'MOBILE_MONEY', 'CARTE_BANCAIRE'
  ];

  loading = false;
  ajoutEnCours = false;
  erreur: string | null = null;
  entrepriseId: number | null = null;
  exerciceId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private depenseService: LivreDepenseService,
    private entrepriseService: EntrepriseSMTService,
    private exerciceService: ExerciceSMTService,
    private compteService: CompteTresorerieService,
    private posteService: PosteClassificationService,
    private validationService: ValidationSMTService
  ) {
    this.depenseForm = this.creerFormulaire();
  }

  ngOnInit(): void {
    this.initialiserComposant();
  }

  private initialiserComposant(): void {
    Promise.all([
      this.chargerEntrepriseEtExercice(),
      this.chargerComptesEtPostes()
    ]).then(() => {
      this.chargerDepenses();
    });
  }

  private async chargerEntrepriseEtExercice(): Promise<void> {
    try {
      const entreprise = await this.entrepriseService.obtenirEntrepriseCourante().toPromise();
      const exercice = await this.exerciceService.exerciceCourant().toPromise();
      
      if (entreprise && exercice) {
        this.entrepriseId = entreprise.id!;
        this.exerciceId = exercice.id!;
      }
    } catch (error) {
      console.error('Erreur chargement entreprise/exercice:', error);
    }
  }

  private async chargerComptesEtPostes(): Promise<void> {
    if (!this.entrepriseId) return;

    try {
      const [comptes, postesDepenses] = await Promise.all([
        this.compteService.listerComptesEntreprise(this.entrepriseId).toPromise(),
        this.posteService.obtenirPostesDepenses().toPromise()
      ]);

      this.comptesTresorerie = comptes?.filter(c => c.actif) || [];
      this.postesDepenses = postesDepenses || [];
    } catch (error) {
      console.error('Erreur chargement données référence:', error);
    }
  }

  private creerFormulaire(): FormGroup {
    return this.fb.group({
      compteTresorerieId: ['', Validators.required],
      dateOperation: [new Date(), Validators.required],
      designationOperation: ['', [Validators.required, Validators.maxLength(200)]],
      natureDepense: ['', Validators.required],
      beneficiaire: ['', [Validators.required, Validators.maxLength(100)]],
      modeReglement: ['', Validators.required],
      numeroPiece: [''],
      montant: [0, [Validators.required, Validators.min(1)]],
      posteDepense: ['', Validators.required],
      tvaDeductible: [0, Validators.min(0)],
      observations: ['', Validators.maxLength(500)],
      pieceJustificative: ['']
    });
  }

  chargerDepenses(): void {
    if (!this.entrepriseId || !this.exerciceId) return;

    this.loading = true;
    this.depenseService.listerDepenses({
      entrepriseId: this.entrepriseId,
      exerciceId: this.exerciceId,
      page: 0,
      taille: 100
    }).subscribe({
      next: (response) => {
        this.dataSource.data = response.content;
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
        this.loading = false;
      },
      error: (error) => {
        console.error('Erreur chargement dépenses:', error);
        this.erreur = 'Erreur lors du chargement des dépenses';
        this.loading = false;
      }
    });
  }

  ajouterDepense(): void {
    if (this.depenseForm.invalid || !this.entrepriseId || !this.exerciceId) {
      this.marquerChampsCommeToques();
      return;
    }

    // Validation métier
    const erreurs = this.validationService.validerDepense(this.depenseForm.value);
    if (erreurs.length > 0) {
      this.erreur = erreurs.join(', ');
      return;
    }

    this.ajoutEnCours = true;
    this.erreur = null;

    const nouvelleDepense: LivreDepense = {
      ...this.depenseForm.value,
      entrepriseId: this.entrepriseId,
      exerciceId: this.exerciceId,
      valide: false
    };

    this.depenseService.ajouterDepense(nouvelleDepense).subscribe({
      next: (depense) => {
        this.dataSource.data = [depense, ...this.dataSource.data];
        this.depenseForm.reset();
        this.ajoutEnCours = false;
      },
      error: (error) => {
        console.error('Erreur ajout dépense:', error);
        this.erreur = 'Erreur lors de l\'ajout de la dépense';
        this.ajoutEnCours = false;
      }
    });
  }

  validerDepense(depense: LivreDepense): void {
    this.depenseService.validerDepense(depense.id!).subscribe({
      next: (depenseValidee) => {
        const index = this.dataSource.data.findIndex(d => d.id === depense.id);
        if (index >= 0) {
          this.dataSource.data[index] = depenseValidee;
          this.dataSource._updateChangeSubscription();
        }
      },
      error: (error) => {
        console.error('Erreur validation dépense:', error);
        this.erreur = 'Erreur lors de la validation';
      }
    });
  }

  supprimerDepense(depense: LivreDepense): void {
    if (depense.valide) {
      this.erreur = 'Impossible de supprimer une dépense validée';
      return;
    }

    if (confirm('Êtes-vous sûr de vouloir supprimer cette dépense ?')) {
      this.depenseService.supprimerDepense(depense.id!).subscribe({
        next: () => {
          this.dataSource.data = this.dataSource.data.filter(d => d.id !== depense.id);
        },
        error: (error) => {
          console.error('Erreur suppression dépense:', error);
          this.erreur = 'Erreur lors de la suppression';
        }
      });
    }
  }

  private marquerChampsCommeToques(): void {
    Object.keys(this.depenseForm.controls).forEach(key => {
      this.depenseForm.get(key)?.markAsTouched();
    });
  }

  formaterMontant(montant: number): string {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'XOF',
      minimumFractionDigits: 0
    }).format(montant);
  }

  appliquerFiltre(event: Event): void {
    const valeur = (event.target as HTMLInputElement).value;
    this.dataSource.filter = valeur.trim().toLowerCase();
  }
}

// components/etats-smt/etats-smt.component.ts
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { 
  EtatRecettesDepensesService,
  EntrepriseSMTService,
  ExerciceSMTService,
  EtatRecettesDepenses
} from '../../services/smt.services';

@Component({
  selector: 'app-etats-smt',
  templateUrl: './etats-smt.component.html',
  styleUrls: ['./etats-smt.component.scss']
})
export class EtatsSMTComponent implements OnInit {
  parametresForm: FormGroup;
  etatCourant: EtatRecettesDepenses | null = null;
  etatsHistorique: EtatRecettesDepenses[] = [];

  loading = false;
  generationEnCours = false;
  erreur: string | null = null;
  entrepriseId: number | null = null;
  exerciceId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private etatService: EtatRecettesDepensesService,
    private entrepriseService: EntrepriseSMTService,
    private exerciceService: ExerciceSMTService
  ) {
    this.parametresForm = this.creerFormulaire();
  }

  ngOnInit(): void {
    this.initialiserComposant();
  }

  private initialiserComposant(): void {
    this.chargerEntrepriseEtExercice().then(() => {
      this.chargerHistoriqueEtats();
    });
  }

  private async chargerEntrepriseEtExercice(): Promise<void> {
    try {
      const entreprise = await this.entrepriseService.obtenirEntrepriseCourante().toPromise();
      const exercice = await this.exerciceService.exerciceCourant().toPromise();
      
      if (entreprise && exercice) {
        this.entrepriseId = entreprise.id!;
        this.exerciceId = exercice.id!;
        
        // Initialiser les dates avec l'exercice courant
        this.parametresForm.patchValue({
          dateDebut: exercice.dateDebut,
          dateFin: exercice.dateFin
        });
      }
    } catch (error) {
      console.error('Erreur chargement entreprise/exercice:', error);
    }
  }

  private creerFormulaire(): FormGroup {
    return this.fb.group({
      dateDebut: ['', Validators.required],
      dateFin: ['', Validators.required]
    });
  }

  private chargerHistoriqueEtats(): void {
    if (!this.entrepriseId) return;

    this.etatService.listerEtats(this.entrepriseId).subscribe({
      next: (etats) => {
        this.etatsHistorique = etats.sort((a, b) => 
          new Date(b.dateEtablissement || 0).getTime() - new Date(a.dateEtablissement || 0).getTime()
        );
      },
      error: (error) => {
        console.error('Erreur chargement historique:', error);
      }
    });
  }

  genererEtat(): void {
    if (this.parametresForm.invalid || !this.entrepriseId || !this.exerciceId) {
      this.marquerChampsCommeToques();
      return;
    }

    this.generationEnCours = true;
    this.erreur = null;

    const { dateDebut, dateFin } = this.parametresForm.value;

    this.etatService.genererEtat(
      this.entrepriseId,
      this.exerciceId,
      dateDebut,
      dateFin
    ).subscribe({
      next: (etat) => {
        this.etatCourant = etat;
        this.etatsHistorique = [etat, ...this.etatsHistorique];
        this.generationEnCours = false;
      },
      error: (error) => {
        console.error('Erreur génération état:', error);
        this.erreur = 'Erreur lors de la génération de l\'état';
        this.generationEnCours = false;
      }
    });
  }

  validerEtat(): void {
    if (!this.etatCourant) return;

    this.etatService.validerEtat(this.etatCourant.id!).subscribe({
      next: (etatValide) => {
        this.etatCourant = etatValide;
        const index = this.etatsHistorique.findIndex(e => e.id === etatValide.id);
        if (index >= 0) {
          this.etatsHistorique[index] = etatValide;
        }
      },
      error: (error) => {
        console.error('Erreur validation état:', error);
        this.erreur = 'Erreur lors de la validation';
      }
    });
  }

  exporterPDF(): void {
    if (!this.etatCourant) return;

    this.etatService.exporterEtatPDF(this.etatCourant.id!).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `Etat_Recettes_Depenses_${this.formatDate(this.etatCourant!.periodeDebut)}_${this.formatDate(this.etatCourant!.periodeFin)}.pdf`;
        link.click();
        window.URL.revokeObjectURL(url);
      },
      error: (error) => {
        console.error('Erreur export PDF:', error);
        this.erreur = 'Erreur lors de l\'export PDF';
      }
    });
  }

  exporterExcel(): void {
    if (!this.etatCourant) return;

    this.etatService.exporterEtatExcel(this.etatCourant.id!).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `Etat_Recettes_Depenses_${this.formatDate(this.etatCourant!.periodeDebut)}_${this.formatDate(this.etatCourant!.periodeFin)}.xlsx`;
        link.click();
        window.URL.revokeObjectURL(url);
      },
      error: (error) => {
        console.error('Erreur export Excel:', error);
        this.erreur = 'Erreur lors de l\'export Excel';
      }
    });
  }

  chargerEtat(etat: EtatRecettesDepenses): void {
    this.etatCourant = etat;
  }

  private marquerChampsCommeToques(): void {
    Object.keys(this.parametresForm.controls).forEach(key => {
      this.parametresForm.get(key)?.markAsTouched();
    });
  }

  private formatDate(date: Date): string {
    return new Date(date).toLocaleDateString('fr-FR').replace(/\//g, '-');
  }

  formaterMontant(montant: number): string {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'XOF',
      minimumFractionDigits: 0
    }).format(montant);
  }

  obtenirCouleurSolde(montant: number): string {
    if (montant > 0) return 'text-success';
    if (montant < 0) return 'text-danger';
    return 'text-muted';
  }

  obtenirPostesRecettesTableau(): { poste: string, montant: number }[] {
    if (!this.etatCourant?.repartitionRecettes) return [];
    
    return Object.entries(this.etatCourant.repartitionRecettes)
      .map(([poste, montant]) => ({ poste, montant }))
      .sort((a, b) => b.montant - a.montant);
  }

  obtenirPostesDepensesTableau(): { poste: string, montant: number }[] {
    if (!this.etatCourant?.repartitionDepenses) return [];
    
    return Object.entries(this.etatCourant.repartitionDepenses)
      .map(([poste, montant]) => ({ poste, montant }))
      .sort((a, b) => b.montant - a.montant);
  }
}

// components/comptes-tresorerie/comptes-tresorerie.component.ts
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { 
  CompteTresorerieService,
  EntrepriseSMTService,
  CompteTresorerie
} from '../../services/smt.services';

@Component({
  selector: 'app-comptes-tresorerie',
  templateUrl: './comptes-tresorerie.component.html',
  styleUrls: ['./comptes-tresorerie.component.scss']
})
export class ComptesTresorerieComponent implements OnInit {
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  compteForm: FormGroup;
  dataSource = new MatTableDataSource<CompteTresorerie>();
  displayedColumns = ['codeCompte', 'designation', 'typeCompte', 'soldeActuel', 'statut', 'actions'];

  typesComptes = [
    { value: 'CAISSE', label: 'Caisse' },
    { value: 'BANQUE', label: 'Compte Bancaire' },
    { value: 'CCP', label: 'Compte Chèques Postaux' },
    { value: 'MOBILE_MONEY', label: 'Mobile Money' }
  ];

  modeEdition = false;
  compteEnEdition: CompteTresorerie | null = null;
  loading = false;
  erreur: string | null = null;
  entrepriseId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private compteService: CompteTresorerieService,
    private entrepriseService: EntrepriseSMTService,
    private dialog: MatDialog
  ) {
    this.compteForm = this.creerFormulaire();
  }

  ngOnInit(): void {
    this.chargerEntrepriseCourante().then(() => {
      this.chargerComptes();
    });
  }

  private async chargerEntrepriseCourante(): Promise<void> {
    try {
      const entreprise = await this.entrepriseService.obtenirEntrepriseCourante().toPromise();
      if (entreprise) {
        this.entrepriseId = entreprise.id!;
      }
    } catch (error) {
      console.error('Erreur chargement entreprise:', error);
    }
  }

  private creerFormulaire(): FormGroup {
    return this.fb.group({
      codeCompte: ['', [Validators.required, Validators.maxLength(10)]],
      designation: ['', [Validators.required, Validators.maxLength(100)]],
      typeCompte: ['', Validators.required],
      banqueNom: [''],
      numeroCompteBancaire: [''],
      agence: [''],
      soldeOuverture: [0, Validators.required],
      devise: ['XOF', Validators.required],
      dateOuverture: [new Date(), Validators.required]
    });
  }

  chargerComptes(): void {
    if (!this.entrepriseId) return;

    this.loading = true;
    this.compteService.listerComptesEntreprise(this.entrepriseId).subscribe({
      next: (comptes) => {
        // Calculer les soldes actuels
        comptes.forEach(compte => {
          this.compteService.calculerSolde(compte.id!).subscribe({
            next: (soldeInfo) => {
              compte.soldeActuel = soldeInfo.solde;
            }
          });
        });

        this.dataSource.data = comptes;
        this.dataSource.paginator = this.paginator;
        this.loading = false;
      },
      error: (error) => {
        console.error('Erreur chargement comptes:', error);
        this.erreur = 'Erreur lors du chargement des comptes';
        this.loading = false;
      }
    });
  }

  ajouterCompte(): void {
    if (this.compteForm.invalid || !this.entrepriseId) {
      this.marquerChampsCommeToques();
      return;
    }

    const nouveauCompte: CompteTresorerie = {
      ...this.compteForm.value,
      entrepriseId: this.entrepriseId,
      actif: true
    };

    this.compteService.creerCompte(nouveauCompte).subscribe({
      next: (compte) => {
        compte.soldeActuel = compte.soldeOuverture;
        this.dataSource.data = [compte, ...this.dataSource.data];
        this.compteForm.reset();
        this.compteForm.patchValue({ devise: 'XOF', dateOuverture: new Date() });
      },
      error: (error) => {
        console.error('Erreur création compte:', error);
        this.erreur = 'Erreur lors de la création du compte';
      }
    });
  }

  modifierCompte(compte: CompteTresorerie): void {
    this.modeEdition = true;
    this.compteEnEdition = compte;
    this.compteForm.patchValue(compte);
  }

  sauvegarderModifications(): void {
    if (this.compteForm.invalid || !this.compteEnEdition) return;

    this.compteService.mettreAJourCompte(this.compteEnEdition.id!, this.compteForm.value).subscribe({
      next: (compteModifie) => {
        const index = this.dataSource.data.findIndex(c => c.id === compteModifie.id);
        if (index >= 0) {
          this.dataSource.data[index] = compteModifie;
          this.dataSource._updateChangeSubscription();
        }
        this.annulerEdition();
      },
      error: (error) => {
        console.error('Erreur modification compte:', error);
        this.erreur = 'Erreur lors de la modification';
      }
    });
  }

  annulerEdition(): void {
    this.modeEdition = false;
    this.compteEnEdition = null;
    this.compteForm.reset();
    this.compteForm.patchValue({ devise: 'XOF', dateOuverture: new Date() });
  }

  fermerCompte(compte: CompteTresorerie): void {
    if (confirm('Êtes-vous sûr de vouloir fermer ce compte ? Cette action est irréversible.')) {
      this.compteService.fermerCompte(compte.id!, new Date()).subscribe({
        next: () => {
          compte.actif = false;
          compte.dateFermeture = new Date();
          this.dataSource._updateChangeSubscription();
        },
        error: (error) => {
          console.error('Erreur fermeture compte:', error);
          this.erreur = 'Erreur lors de la fermeture du compte';
        }
      });
    }
  }

  onTypeCompteChange(): void {
    const typeCompte = this.compteForm.get('typeCompte')?.value;
    
    if (typeCompte === 'BANQUE' || typeCompte === 'CCP') {
      this.compteForm.get('banqueNom')?.setValidators([Validators.required]);
      this.compteForm.get('numeroCompteBancaire')?.setValidators([Validators.required]);
    } else {
      this.compteForm.get('banqueNom')?.clearValidators();
      this.compteForm.get('numeroCompteBancaire')?.clearValidators();
    }
    
    this.compteForm.get('banqueNom')?.updateValueAndValidity();
    this.compteForm.get('numeroCompteBancaire')?.updateValueAndValidity();
  }

  private marquerChampsCommeToques(): void {
    Object.keys(this.compteForm.controls).forEach(key => {
      this.compteForm.get(key)?.markAsTouched();
    });
  }

  formaterMontant(montant: number): string {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'XOF',
      minimumFractionDigits: 0
    }).format(montant);
  }

  obtenirLibelleTypeCompte(type: string): string {
    const typeObj = this.typesComptes.find(t => t.value === type);
    return typeObj?.label || type;
  }

  appliquerFiltre(event: Event): void {
    const valeur = (event.target as HTMLInputElement).value;
    this.dataSource.filter = valeur.trim().toLowerCase();
  }
}

// =====================================================
// MODULE PRINCIPAL SMT
// =====================================================

// smt.module.ts
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';

// Angular Material
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatTabsModule } from '@angular/material/tabs';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snackbar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';

// Composants
import { DashboardSMTComponent } from './components/dashboard-smt/dashboard-smt.component';
import { LivreRecettesComponent } from './components/livre-recettes/livre-recettes.component';
import { LivreDepensesComponent } from './components/livre-depenses/livre-depenses.component';
import { ComptesTresorerieComponent } from './components/comptes-tresorerie/comptes-tresorerie.component';
import { EtatsSMTComponent } from './components/etats-smt/etats-smt.component';
import { RapprochementsSMTComponent } from './components/rapprochements-smt/rapprochements-smt.component';
import { VirementsSMTComponent } from './components/virements-smt/virements-smt.component';

const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardSMTComponent },
  { path: 'recettes', component: LivreRecettesComponent },
  { path: 'depenses', component: LivreDepensesComponent },
  { path: 'comptes', component: ComptesTresorerieComponent },
  { path: 'virements', component: VirementsSMTComponent },
  { path: 'rapprochements', component: RapprochementsSMTComponent },
  { path: 'etats', component: EtatsSMTComponent }
];

@NgModule({
  declarations: [
    DashboardSMTComponent,
    LivreRecettesComponent,
    LivreDepensesComponent,
    ComptesTresorerieComponent,
    EtatsSMTComponent,
    RapprochementsSMTComponent,
    VirementsSMTComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule.forChild(routes),
    
    // Angular Material
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatTabsModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatCheckboxModule,
    MatDialogModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule
  ],
  providers: []
})
export class SMTModule { }

// =====================================================
// TEMPLATES HTML (Exemples clés)
// =====================================================

/*
<!-- dashboard-smt.component.html -->
<div class="dashboard-smt">
  <div class="dashboard-header">
    <h1>Tableau de Bord - Système Minimal de Trésorerie</h1>
    <button mat-raised-button color="primary" (click)="rafraichirDonnees()">
      <mat-icon>refresh</mat-icon>
      Actualiser
    </button>
  </div>

  <div class="indicateurs-grid" *ngIf="!loading">
    <mat-card class="indicateur-card tresorerie">
      <mat-card-header>
        <mat-card-title>Trésorerie Disponible</mat-card-title>
      </mat-card-header>
      <mat-card-content>
        <div class="montant-principal">
          {{ formaterMontant(indicateurs.tresorerieDisponible) }}
        </div>
        <div class="sous-texte">Tous comptes confondus</div>
      </mat-card-content>
    </mat-card>

    <mat-card class="indicateur-card recettes">
      <mat-card-header>
        <mat-card-title>Recettes du Mois</mat-card-title>
      </mat-card-header>
      <mat-card-content>
        <div class="montant-principal text-success">
          {{ formaterMontant(indicateurs.recettesDuMois) }}
        </div>
        <div class="sous-texte">{{ indicateurs.nombreRecettes }} opérations</div>
      </mat-card-content>
    </mat-card>

    <mat-card class="indicateur-card depenses">
      <mat-card-header>
        <mat-card-title>Dépenses du Mois</mat-card-title>
      </mat-card-header>
      <mat-card-content>
        <div class="montant-principal text-danger">
          {{ formaterMontant(indicateurs.depensesDuMois) }}
        </div>
        <div class="sous-texte">{{ indicateurs.nombreDepenses }} opérations</div>
      </mat-card-content>
    </mat-card>

    <mat-card class="indicateur-card solde-net">
      <mat-card-header>
        <mat-card-title>Solde Net du Mois</mat-card-title>
      </mat-card-header>
      <mat-card-content>
        <div class="montant-principal" [ngClass]="obtenirCouleurSolde(indicateurs.soldeNetMois)">
          {{ formaterMontant(indicateurs.soldeNetMois) }}
        </div>
        <div class="sous-texte">
          <span *ngIf="indicateurs.soldeNetMois > 0" class="text-success">Excédent</span>
          <span *ngIf="indicateurs.soldeNetMois < 0" class="text-danger">Déficit</span>
          <span *ngIf="indicateurs.soldeNetMois === 0" class="text-muted">Équilibre</span>
        </div>
      </mat-card-content>
    </mat-card>
  </div>

  <div class="actions-rapides">
    <h2>Actions Rapides</h2>
    <div class="actions-grid">
      <button mat-raised-button color="primary" routerLink="/recettes">
        <mat-icon>add_circle</mat-icon>
        Nouvelle Recette
      </button>
      <button mat-raised-button color="accent" routerLink="/depenses">
        <mat-icon>remove_circle</mat-icon>
        Nouvelle Dépense
      </button>
      <button mat-raised-button routerLink="/etats">
        <mat-icon>assessment</mat-icon>
        États Financiers
      </button>
      <button mat-raised-button routerLink="/rapprochements">
        <mat-icon>account_balance</mat-icon>
        Rapprochements
      </button>
    </div>
  </div>

  <div *ngIf="loading" class="loading-container">
    <mat-spinner></mat-spinner>
    <p>Chargement des données...</p>
  </div>

  <div *ngIf="erreur" class="erreur-container">
    <mat-icon color="warn">error</mat-icon>
    <p>{{ erreur }}</p>
  </div>
</div>
*/

/*
<!-- livre-recettes.component.html -->
<div class="livre-recettes">
  <div class="page-header">
    <h1>Livre des Recettes</h1>
    <div class="actions-header">
      <button mat-raised-button color="primary" (click)="chargerRecettes()">
        <mat-icon>refresh</mat-icon>
        Actualiser
      </button>
    </div>
  </div>

  <mat-card class="formulaire-card">
    <mat-card-header>
      <mat-card-title>Ajouter une Recette</mat-card-title>
    </mat-card-header>
    <mat-card-content>
      <form [formGroup]="recetteForm" (ngSubmit)="ajouterRecette()">
        <div class="form-row">
          <mat-form-field>
            <mat-label>Compte de Trésorerie</mat-label>
            <mat-select formControlName="compteTresorerieId" required>
              <mat-option *ngFor="let compte of comptesTresorerie" [value]="compte.id">
                {{ compte.designation }} ({{ compte.codeCompte }})
              </mat-option>
            </mat-select>
          </mat-form-field>

          <mat-form-field>
            <mat-label>Date d'Opération</mat-label>
            <input matInput [matDatepicker]="pickerDate" formControlName="dateOperation" required>
            <mat-datepicker-toggle matSuffix [for]="pickerDate"></mat-datepicker-toggle>
            <mat-datepicker #pickerDate></mat-datepicker>
          </mat-form-field>
        </div>

        <mat-form-field class="full-width">
          <mat-label>Désignation de l'Opération</mat-label>
          <input matInput formControlName="designationOperation" maxlength="200" required>
        </mat-form-field>

        <div class="form-row">
          <mat-form-field>
            <mat-label>Origine Recette</mat-label>
            <mat-select formControlName="origineRecette" required>
              <mat-option *ngFor="let origine of originesRecettes" [value]="origine">
                {{ origine }}
              </mat-option>
            </mat-select>
          </mat-form-field>

          <mat-form-field>
            <mat-label>Client/Payeur</mat-label>
            <input matInput formControlName="clientPayeur" maxlength="100">
          </mat-form-field>
        </div>

        <div class="form-row">
          <mat-form-field>
            <mat-label>Mode de Règlement</mat-label>
            <mat-select formControlName="modeReglement" required>
              <mat-option *ngFor="let mode of modesReglement" [value]="mode">
                {{ mode }}
              </mat-option>
            </mat-select>
          </mat-form-field>

          <mat-form-field>
            <mat-label>N° Pièce (Chèque, Référence...)</mat-label>
            <input matInput formControlName="numeroPiece">
          </mat-form-field>
        </div>

        <div class="form-row">
          <mat-form-field>
            <mat-label>Montant (FCFA)</mat-label>
            <input matInput type="number" formControlName="montant" min="1" required>
          </mat-form-field>

          <mat-form-field>
            <mat-label>Poste de Recette</mat-label>
            <mat-select formControlName="posteRecette" required>
              <mat-option *ngFor="let poste of postesRecettes" [value]="poste.codePoste">
                {{ poste.libellePoste }}
              </mat-option>
            </mat-select>
          </mat-form-field>
        </div>

        <mat-form-field class="full-width">
          <mat-label>Observations</mat-label>
          <textarea matInput formControlName="observations" rows="3" maxlength="500"></textarea>
        </mat-form-field>

        <div class="actions-form">
          <button mat-raised-button color="primary" type="submit" 
                  [disabled]="ajoutEnCours || recetteForm.invalid">
            <mat-icon *ngIf="!ajoutEnCours">save</mat-icon>
            <mat-spinner *ngIf="ajoutEnCours" diameter="20"></mat-spinner>
            Enregistrer
          </button>
        </div>
      </form>
    </mat-card-content>
  </mat-card>

  <mat-card class="tableau-card">
    <mat-card-header>
      <mat-card-title>Liste des Recettes</mat-card-title>
      <div class="header-actions">
        <mat-form-field class="filtre-field">
          <mat-label>Rechercher</mat-label>
          <input matInput (keyup)="appliquerFiltre($event)" placeholder="Rechercher...">
          <mat-icon matSuffix>search</mat-icon>
        </mat-form-field>
      </div>
    </mat-card-header>
    <mat-card-content>
      <table mat-table [dataSource]="dataSource" matSort class="table-recettes">
        <ng-container matColumnDef="numeroOperation">
          <th mat-header-cell *matHeaderCellDef mat-sort-header>N° Opération</th>
          <td mat-cell *matCellDef="let recette">{{ recette.numeroOperation }}</td>
        </ng-container>

        <ng-container matColumnDef="dateOperation">
          <th mat-header-cell *matHeaderCellDef mat-sort-header>Date</th>
          <td mat-cell *matCellDef="let recette">{{ recette.dateOperation | date:'dd/MM/yyyy' }}</td>
        </ng-container>

        <ng-container matColumnDef="designationOperation">
          <th mat-header-cell *matHeaderCellDef>Désignation</th>
          <td mat-cell *matCellDef="let recette" class="designation-cell">
            {{ recette.designationOperation }}
          </td>
        </ng-container>

        <ng-container matColumnDef="clientPayeur">
          <th mat-header-cell *matHeaderCellDef>Client/Payeur</th>
          <td mat-cell *matCellDef="let recette">{{ recette.clientPayeur || '-' }}</td>
        </ng-container>

        <ng-container matColumnDef="montant">
          <th mat-header-cell *matHeaderCellDef mat-sort-header>Montant</th>
          <td mat-cell *matCellDef="let recette" class="montant-cell">
            {{ formaterMontant(recette.montant) }}
          </td>
        </ng-container>

        <ng-container matColumnDef="posteRecette">
          <th mat-header-cell *matHeaderCellDef>Poste</th>
          <td mat-cell *matCellDef="let recette">{{ recette.posteRecette }}</td>
        </ng-container>

        <ng-container matColumnDef="valide">
          <th mat-header-cell *matHeaderCellDef>Statut</th>
          <td mat-cell *matCellDef="let recette">
            <mat-icon *ngIf="recette.valide" color="primary">check_circle</mat-icon>
            <mat-icon *ngIf="!recette.valide" color="accent">hourglass_empty</mat-icon>
          </td>
        </ng-container>

        <ng-container matColumnDef="actions">
          <th mat-header-cell *matHeaderCellDef>Actions</th>
          <td mat-cell *matCellDef="let recette">
            <button mat-icon-button *ngIf="!recette.valide" 
                    