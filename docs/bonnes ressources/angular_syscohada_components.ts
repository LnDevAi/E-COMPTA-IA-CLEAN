// services/balance-comptable.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface SoldeCompte {
  numeroCompte: string;
  libelleCompte: string;
  debitExercicePrecedent: number;
  creditExercicePrecedent: number;
  debitMouvements: number;
  creditMouvements: number;
  debitSoldeFinal: number;
  creditSoldeFinal: number;
}

export interface BalanceComptable {
  exercice: string;
  dateDebut: Date;
  dateFin: Date;
  comptes: SoldeCompte[];
}

@Injectable({
  providedIn: 'root'
})
export class BalanceComptableService {
  private apiUrl = 'api/balance-comptable';

  constructor(private http: HttpClient) {}

  obtenirBalance(dateDebut: string, dateFin: string): Observable<BalanceComptable> {
    return this.http.get<BalanceComptable>(`${this.apiUrl}`, {
      params: { dateDebut, dateFin }
    });
  }

  obtenirBalanceComparative(dateDebut: string, dateFin: string): Observable<{
    exerciceCourant: BalanceComptable;
    exercicePrecedent: BalanceComptable;
  }> {
    return this.http.get<any>(`${this.apiUrl}/comparative`, {
      params: { dateDebut, dateFin }
    });
  }
}

// services/etats-financiers-auto.service.ts
import { Injectable } from '@angular/core';
import { Observable, forkJoin, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { BalanceComptableService, BalanceComptable, SoldeCompte } from './balance-comptable.service';

export interface EtatFinancierAuto {
  bilan: BilanSYSCOHADA;
  compteResultat: CompteResultatSYSCOHADA;
  tableauFlux: TableauFluxSYSCOHADA;
  annexes: AnnexesSYSCOHADA;
}

@Injectable({
  providedIn: 'root'
})
export class EtatsFinanciersAutoService {
  
  // Mapping des comptes vers les postes SYSCOHADA
  private mappingBilan = {
    // ACTIF IMMOBILISÉ
    'AD': ['21%'], // Immobilisations incorporelles
    'AF': ['211%', '212%', '213%'], // Brevets, licences, logiciels
    'AG': ['214%', '215%'], // Fonds commercial
    'AI': ['22%', '23%', '24%'], // Immobilisations corporelles
    'AJ': ['221%'], // Terrains
    'AK': ['222%'], // Bâtiments
    'AL': ['223%', '224%'], // Aménagements
    'AM': ['231%', '232%', '233%', '234%'], // Matériel
    'AN': ['245%'], // Matériel de transport
    'AP': ['238%'], // Avances sur immobilisations
    'AQ': ['26%', '27%'], // Immobilisations financières
    
    // ACTIF CIRCULANT
    'BA': ['85%'], // Actif circulant HAO
    'BB': ['31%', '32%', '33%', '34%', '35%', '36%', '37%', '38%'], // Stocks
    'BG': ['40%', '41%', '42%', '43%', '44%', '45%', '46%', '47%', '48%'], // Créances
    'BI': ['411%', '416%', '418%'], // Clients
    'BJ': ['425%', '427%', '428%', '444%', '445%', '446%', '447%', '448%'], // Autres créances
    'BS': ['51%', '52%', '53%', '54%', '57%', '58%'], // Trésorerie actif
    'BU': ['476%'], // Écart de conversion actif
    
    // PASSIF - CAPITAUX PROPRES
    'CA': ['101%', '104%', '105%'], // Capital
    'CB': ['109%'], // Capital non appelé
    'CD': ['105%'], // Écarts de réévaluation
    'CE': ['111%', '112%', '113%'], // Réserves
    'CF': ['121%'], // Report à nouveau
    'CG': ['130%'], // Résultat
    'CH': ['141%', '142%'], // Subventions
    'CJ': ['151%', '152%', '153%'], // Provisions réglementées
    
    // PASSIF - DETTES FINANCIÈRES
    'DA': ['16%', '17%'], // Emprunts et dettes financières
    'DC': ['191%', '192%', '193%', '194%', '195%', '196%', '197%', '198%'], // Provisions
    
    // PASSIF CIRCULANT
    'DH': ['86%'], // Dettes circulantes HAO
    'DI': ['419%'], // Clients avances reçues
    'DJ': ['401%', '403%', '408%'], // Fournisseurs
    'DK': ['421%', '422%', '423%', '424%', '425%', '426%', '427%', '428%'], // Dettes fiscales et sociales
    'DM': ['444%', '445%', '446%', '447%', '448%'], // Autres dettes
    'DN': ['499%'], // Provisions court terme
    'DR': ['565%', '566%'], // Trésorerie passif
    'DV': ['477%'] // Écart de conversion passif
  };

  private mappingCompteResultat = {
    // PRODUITS
    'TA': ['701%'], // Ventes marchandises
    'TB': ['702%', '703%'], // Ventes produits fabriqués
    'TC': ['704%', '705%', '706%'], // Services vendus
    'TD': ['707%', '708%'], // Produits accessoires
    'TF': ['721%', '722%'], // Production immobilisée
    'TH': ['754%', '758%'], // Autres produits
    'TI': ['781%', '791%'], // Transferts de charges
    'TK': ['771%', '772%', '773%', '774%', '776%', '777%'], // Revenus financiers
    'TN': ['775%'], // Produits cessions
    'TO': ['84%'], // Produits HAO
    
    // CHARGES
    'RA': ['601%'], // Achats marchandises
    'RB': ['6031%'], // Variation stocks marchandises
    'RC': ['602%'], // Achats matières premières
    'RD': ['6032%'], // Variation stocks matières
    'RE': ['604%', '605%', '606%', '607%', '608%'], // Autres achats
    'RF': ['6033%', '6034%'], // Variation autres stocks
    'RG': ['61%'], // Transports
    'RH': ['62%'], // Services extérieurs
    'RI': ['63%'], // Impôts et taxes
    'RJ': ['65%'], // Autres charges
    'RK': ['66%'], // Charges personnel
    'RL': ['681%', '691%'], // Dotations amortissements
    'RM': ['671%', '672%', '673%', '674%', '675%', '676%', '677%', '678%'], // Frais financiers
    'RO': ['675%'], // Valeurs comptables cessions
    'RP': ['87%'], // Charges HAO
    'RS': ['891%', '892%'] // Impôts sur résultat
  };

  constructor(private balanceService: BalanceComptableService) {}

  genererEtatsFinanciers(dateDebut: string, dateFin: string): Observable<EtatFinancierAuto> {
    return forkJoin({
      balanceCourante: this.balanceService.obtenirBalance(dateDebut, dateFin),
      balanceComparative: this.balanceService.obtenirBalanceComparative(dateDebut, dateFin)
    }).pipe(
      map(({ balanceCourante, balanceComparative }) => {
        const bilan = this.genererBilanAuto(balanceCourante, balanceComparative.exercicePrecedent);
        const compteResultat = this.genererCompteResultatAuto(balanceCourante);
        const tableauFlux = this.genererTableauFluxAuto(balanceCourante, balanceComparative.exercicePrecedent);
        const annexes = this.genererAnnexesAuto(balanceCourante);

        return {
          bilan,
          compteResultat,
          tableauFlux,
          annexes
        };
      })
    );
  }

  private genererBilanAuto(balanceCourante: BalanceComptable, balancePrecedente: BalanceComptable): BilanSYSCOHADA {
    const bilan = new BilanSYSCOHADA();

    // Calcul automatique des postes du bilan
    Object.entries(this.mappingBilan).forEach(([poste, comptes]) => {
      const valeurExerciceCourant = this.calculerSoldePostes(balanceCourante.comptes, comptes);
      const valeurExercicePrecedent = this.calculerSoldePostes(balancePrecedente.comptes, comptes);
      
      bilan.affecterValeur(poste, valeurExerciceCourant, valeurExercicePrecedent);
    });

    // Calcul des totaux automatiques
    bilan.calculerTotaux();
    
    return bilan;
  }

  private genererCompteResultatAuto(balance: BalanceComptable): CompteResultatSYSCOHADA {
    const compteResultat = new CompteResultatSYSCOHADA();

    Object.entries(this.mappingCompteResultat).forEach(([poste, comptes]) => {
      const valeur = this.calculerSoldePostes(balance.comptes, comptes);
      compteResultat.affecterValeur(poste, valeur);
    });

    // Calcul des soldes intermédiaires automatiques
    compteResultat.calculerSoldesIntermediaires();
    
    return compteResultat;
  }

  private genererTableauFluxAuto(balanceCourante: BalanceComptable, balancePrecedente: BalanceComptable): TableauFluxSYSCOHADA {
    const tableauFlux = new TableauFluxSYSCOHADA();
    
    // Calcul de la CAFG
    const resultatNet = this.calculerSoldePostes(balanceCourante.comptes, ['130%']);
    const dotationsAmort = this.calculerSoldePostes(balanceCourante.comptes, ['681%', '691%']);
    const cafg = resultatNet + dotationsAmort;
    
    tableauFlux.affecterValeur('FA', cafg);
    
    // Variations des postes de l'actif et du passif circulant
    const variationStocks = this.calculerVariation(balanceCourante, balancePrecedente, ['31%', '32%', '33%', '34%', '35%', '36%', '37%', '38%']);
    const variationCreances = this.calculerVariation(balanceCourante, balancePrecedente, ['40%', '41%', '42%', '43%', '44%']);
    const variationDettes = this.calculerVariation(balanceCourante, balancePrecedente, ['401%', '421%', '422%', '423%', '424%']);
    
    tableauFlux.affecterValeur('FC', -variationStocks);
    tableauFlux.affecterValeur('FD', -variationCreances);
    tableauFlux.affecterValeur('FE', variationDettes);
    
    // Flux d'investissement et de financement
    const acquisitionsImmob = this.calculerAcquisitionsImmobilisations(balanceCourante, balancePrecedente);
    const cessionsImmob = this.calculerCessionsImmobilisations(balanceCourante, balancePrecedente);
    
    tableauFlux.affecterValeur('FG', -acquisitionsImmob);
    tableauFlux.affecterValeur('FI', cessionsImmob);
    
    tableauFlux.calculerTotaux();
    
    return tableauFlux;
  }

  private genererAnnexesAuto(balance: BalanceComptable): AnnexesSYSCOHADA {
    const annexes = new AnnexesSYSCOHADA();
    
    // Génération automatique des notes principales
    annexes.genererNote3A(balance); // Immobilisations brutes
    annexes.genererNote3C(balance); // Amortissements
    annexes.genererNote6(balance);  // Stocks
    annexes.genererNote7(balance);  // Clients
    annexes.genererNote27A(balance); // Charges de personnel
    
    return annexes;
  }

  private calculerSoldePostes(comptes: SoldeCompte[], patterns: string[]): number {
    return comptes
      .filter(compte => this.correspondAuPattern(compte.numeroCompte, patterns))
      .reduce((total, compte) => {
        const solde = compte.debitSoldeFinal - compte.creditSoldeFinal;
        return total + solde;
      }, 0);
  }

  private correspondAuPattern(numeroCompte: string, patterns: string[]): boolean {
    return patterns.some(pattern => {
      const regex = new RegExp('^' + pattern.replace('%', '.*'));
      return regex.test(numeroCompte);
    });
  }

  private calculerVariation(balanceCourante: BalanceComptable, balancePrecedente: BalanceComptable, patterns: string[]): number {
    const soldeCourant = this.calculerSoldePostes(balanceCourante.comptes, patterns);
    const soldePrecedent = this.calculerSoldePostes(balancePrecedente.comptes, patterns);
    return soldeCourant - soldePrecedent;
  }

  private calculerAcquisitionsImmobilisations(balanceCourante: BalanceComptable, balancePrecedente: BalanceComptable): number {
    const immobCourantes = this.calculerSoldePostes(balanceCourante.comptes, ['21%', '22%', '23%', '24%']);
    const immobPrecedentes = this.calculerSoldePostes(balancePrecedente.comptes, ['21%', '22%', '23%', '24%']);
    return Math.max(0, immobCourantes - immobPrecedentes);
  }

  private calculerCessionsImmobilisations(balanceCourante: BalanceComptable, balancePrecedente: BalanceComptable): number {
    // Calcul basé sur les produits de cession
    return this.calculerSoldePostes(balanceCourante.comptes, ['775%']);
  }
}

// models/bilan-syscohada.model.ts
export class BilanSYSCOHADA {
  private postes: Map<string, PosteBilan> = new Map();

  constructor() {
    this.initialiserStructure();
  }

  private initialiserStructure(): void {
    const structure = [
      // ACTIF IMMOBILISÉ
      { code: 'AD', libelle: 'IMMOBILISATIONS INCORPORELLES', niveau: 1 },
      { code: 'AF', libelle: 'Brevets, licences, logiciels et droits similaires', niveau: 2 },
      { code: 'AG', libelle: 'Fonds commercial et droit au bail', niveau: 2 },
      { code: 'AH', libelle: 'Autres immobilisations incorporelles', niveau: 2 },
      { code: 'AI', libelle: 'IMMOBILISATIONS CORPORELLES', niveau: 1 },
      { code: 'AJ', libelle: 'Terrains', niveau: 2 },
      { code: 'AK', libelle: 'Bâtiments', niveau: 2 },
      { code: 'AL', libelle: 'Aménagements, agencements et installations', niveau: 2 },
      { code: 'AM', libelle: 'Matériel, mobilier et actifs biologiques', niveau: 2 },
      { code: 'AN', libelle: 'Matériel de transport', niveau: 2 },
      { code: 'AP', libelle: 'Avances et acomptes versés sur immobilisations', niveau: 2 },
      { code: 'AQ', libelle: 'IMMOBILISATIONS FINANCIÈRES', niveau: 1 },
      { code: 'AR', libelle: 'Titres de participation', niveau: 2 },
      { code: 'AS', libelle: 'Autres immobilisations financières', niveau: 2 },
      { code: 'AZ', libelle: 'TOTAL ACTIF IMMOBILISÉ', niveau: 0, isTotal: true },
      
      // ACTIF CIRCULANT
      { code: 'BA', libelle: 'Actif circulant HAO', niveau: 1 },
      { code: 'BB', libelle: 'STOCKS ET EN-COURS', niveau: 1 },
      { code: 'BC', libelle: 'Marchandises', niveau: 2 },
      { code: 'BD', libelle: 'Matières premières et autres approvisionnements', niveau: 2 },
      { code: 'BE', libelle: 'En-cours', niveau: 2 },
      { code: 'BF', libelle: 'Produits fabriqués', niveau: 2 },
      { code: 'BG', libelle: 'CRÉANCES ET EMPLOIS ASSIMILÉS', niveau: 1 },
      { code: 'BH', libelle: 'Fournisseurs, avances versées', niveau: 2 },
      { code: 'BI', libelle: 'Clients', niveau: 2 },
      { code: 'BJ', libelle: 'Autres créances', niveau: 2 },
      { code: 'BK', libelle: 'TOTAL ACTIF CIRCULANT', niveau: 0, isTotal: true },
      
      // TRÉSORERIE ACTIF
      { code: 'BQ', libelle: 'Titres de placement', niveau: 2 },
      { code: 'BR', libelle: 'Valeurs à encaisser', niveau: 2 },
      { code: 'BS', libelle: 'Banques, chèques postaux, caisse et assimilés', niveau: 2 },
      { code: 'BT', libelle: 'TOTAL TRÉSORERIE-ACTIF', niveau: 0, isTotal: true },
      
      // ÉCART DE CONVERSION
      { code: 'BU', libelle: 'Écart de conversion-Actif (perte latente)', niveau: 1 },
      { code: 'BZ', libelle: 'TOTAL GÉNÉRAL ACTIF', niveau: 0, isTotal: true, isGrandTotal: true },
      
      // PASSIF - CAPITAUX PROPRES
      { code: 'CA', libelle: 'CAPITAL', niveau: 1 },
      { code: 'CB', libelle: 'Apporteurs, capital non appelé (-)', niveau: 2 },
      { code: 'CC', libelle: 'Primes et réserves', niveau: 1 },
      { code: 'CD', libelle: 'Écarts de réévaluation', niveau: 2 },
      { code: 'CE', libelle: 'Réserves indisponibles', niveau: 2 },
      { code: 'CF', libelle: 'Réserves libres', niveau: 2 },
      { code: 'CG', libelle: 'Report à nouveau (+ ou -)', niveau: 2 },
      { code: 'CH', libelle: 'Résultat net de l\'exercice (bénéfice + ou perte -)', niveau: 2 },
      { code: 'CI', libelle: 'Autres capitaux propres', niveau: 1 },
      { code: 'CJ', libelle: 'Subventions d\'investissement', niveau: 2 },
      { code: 'CK', libelle: 'Provisions réglementées', niveau: 2 },
      { code: 'CP', libelle: 'TOTAL CAPITAUX PROPRES ET RESSOURCES ASSIMILÉES', niveau: 0, isTotal: true },
      
      // DETTES FINANCIÈRES
      { code: 'DA', libelle: 'EMPRUNTS ET DETTES FINANCIÈRES DIVERSES', niveau: 1 },
      { code: 'DB', libelle: 'Emprunts', niveau: 2 },
      { code: 'DC', libelle: 'Dettes de crédit-bail et contrats assimilés', niveau: 2 },
      { code: 'DD', libelle: 'Dettes financières diverses', niveau: 2 },
      { code: 'DE', libelle: 'PROVISIONS POUR RISQUES ET CHARGES', niveau: 1 },
      { code: 'DF', libelle: 'TOTAL DETTES FINANCIÈRES ET RESSOURCES ASSIMILÉES', niveau: 0, isTotal: true },
      { code: 'DG', libelle: 'TOTAL RESSOURCES STABLES', niveau: 0, isTotal: true },
      
      // PASSIF CIRCULANT
      { code: 'DH', libelle: 'Dettes circulantes HAO', niveau: 1 },
      { code: 'DI', libelle: 'CLIENTS, AVANCES REÇUES', niveau: 1 },
      { code: 'DJ', libelle: 'FOURNISSEURS D\'EXPLOITATION', niveau: 1 },
      { code: 'DK', libelle: 'DETTES FISCALES ET SOCIALES', niveau: 1 },
      { code: 'DL', libelle: 'Dettes fiscales', niveau: 2 },
      { code: 'DM', libelle: 'Dettes sociales', niveau: 2 },
      { code: 'DN', libelle: 'AUTRES DETTES', niveau: 1 },
      { code: 'DO', libelle: 'PROVISIONS POUR RISQUES À COURT TERME', niveau: 1 },
      { code: 'DP', libelle: 'TOTAL PASSIF CIRCULANT', niveau: 0, isTotal: true },
      
      // TRÉSORERIE PASSIF
      { code: 'DQ', libelle: 'Banques, crédits d\'escompte', niveau: 2 },
      { code: 'DR', libelle: 'Banques, établissements financiers et crédits de trésorerie', niveau: 2 },
      { code: 'DS', libelle: 'Banques, découverts', niveau: 2 },
      { code: 'DT', libelle: 'TOTAL TRÉSORERIE-PASSIF', niveau: 0, isTotal: true },
      
      // ÉCART DE CONVERSION
      { code: 'DV', libelle: 'Écart de conversion-Passif (gain latent)', niveau: 1 },
      { code: 'DZ', libelle: 'TOTAL GÉNÉRAL PASSIF', niveau: 0, isTotal: true, isGrandTotal: true }
    ];

    structure.forEach(item => {
      this.postes.set(item.code, new PosteBilan(
        item.code,
        item.libelle,
        item.niveau,
        item.isTotal || false,
        item.isGrandTotal || false
      ));
    });
  }

  affecterValeur(code: string, valeurCourante: number, valeurPrecedente: number = 0): void {
    const poste = this.postes.get(code);
    if (poste) {
      poste.valeurExerciceCourant = valeurCourante;
      poste.valeurExercicePrecedent = valeurPrecedente;
    }
  }

  calculerTotaux(): void {
    // Calcul TOTAL ACTIF IMMOBILISÉ (AZ)
    const totalImmobilise = this.calculerSommePostes(['AD', 'AI', 'AQ']);
    this.affecterValeur('AZ', totalImmobilise);

    // Calcul TOTAL ACTIF CIRCULANT (BK)
    const totalCirculant = this.calculerSommePostes(['BA', 'BB', 'BG']);
    this.affecterValeur('BK', totalCirculant);

    // Calcul TOTAL TRÉSORERIE-ACTIF (BT)
    const totalTresorerieActif = this.calculerSommePostes(['BQ', 'BR', 'BS']);
    this.affecterValeur('BT', totalTresorerieActif);

    // Calcul TOTAL GÉNÉRAL ACTIF (BZ)
    const totalGeneralActif = this.calculerSommePostes(['AZ', 'BK', 'BT', 'BU']);
    this.affecterValeur('BZ', totalGeneralActif);

    // Calcul TOTAL CAPITAUX PROPRES (CP)
    const totalCapitaux = this.calculerSommePostes(['CA', 'CC', 'CI']) - this.obtenirValeur('CB');
    this.affecterValeur('CP', totalCapitaux);

    // Calcul TOTAL DETTES FINANCIÈRES (DF)
    const totalDettesFinancieres = this.calculerSommePostes(['DA', 'DE']);
    this.affecterValeur('DF', totalDettesFinancieres);

    // Calcul TOTAL RESSOURCES STABLES (DG)
    const totalRessourcesStables = this.calculerSommePostes(['CP', 'DF']);
    this.affecterValeur('DG', totalRessourcesStables);

    // Calcul TOTAL PASSIF CIRCULANT (DP)
    const totalPassifCirculant = this.calculerSommePostes(['DH', 'DI', 'DJ', 'DK', 'DN', 'DO']);
    this.affecterValeur('DP', totalPassifCirculant);

    // Calcul TOTAL TRÉSORERIE-PASSIF (DT)
    const totalTresoreriePassif = this.calculerSommePostes(['DQ', 'DR', 'DS']);
    this.affecterValeur('DT', totalTresoreriePassif);

    // Calcul TOTAL GÉNÉRAL PASSIF (DZ)
    const totalGeneralPassif = this.calculerSommePostes(['DG', 'DP', 'DT', 'DV']);
    this.affecterValeur('DZ', totalGeneralPassif);
  }

  private calculerSommePostes(codes: string[]): number {
    return codes.reduce((total, code) => total + this.obtenirValeur(code), 0);
  }

  private obtenirValeur(code: string): number {
    const poste = this.postes.get(code);
    return poste ? poste.valeurExerciceCourant : 0;
  }

  obtenirPoste(code: string): PosteBilan | undefined {
    return this.postes.get(code);
  }

  obtenirTousLesPostes(): PosteBilan[] {
    return Array.from(this.postes.values());
  }

  verifierEquilibre(): boolean {
    const totalActif = this.obtenirValeur('BZ');
    const totalPassif = this.obtenirValeur('DZ');
    return Math.abs(totalActif - totalPassif) < 0.01;
  }
}

export class PosteBilan {
  constructor(
    public code: string,
    public libelle: string,
    public niveau: number,
    public isTotal: boolean = false,
    public isGrandTotal: boolean = false,
    public valeurExerciceCourant: number = 0,
    public valeurExercicePrecedent: number = 0
  ) {}

  get estSignificatif(): boolean {
    return Math.abs(this.valeurExerciceCourant) > 0 || Math.abs(this.valeurExercicePrecedent) > 0;
  }
}

// models/compte-resultat-syscohada.model.ts
export class CompteResultatSYSCOHADA {
  private postes: Map<string, PosteCompteResultat> = new Map();

  constructor() {
    this.initialiserStructure();
  }

  private initialiserStructure(): void {
    const structure = [
      // PRODUITS D'EXPLOITATION
      { code: 'TA', libelle: 'Ventes de marchandises', type: 'produit' },
      { code: 'RA', libelle: 'Achats de marchandises', type: 'charge' },
      { code: 'RB', libelle: 'Variation stocks de marchandises', type: 'charge' },
      { code: 'XA', libelle: 'MARGE COMMERCIALE (TA - RA - RB)', type: 'solde', isTotal: true },
      
      { code: 'TB', libelle: 'Ventes de produits fabriqués', type: 'produit' },
      { code: 'TC', libelle: 'Travaux, services vendus', type: 'produit' },
      { code: 'TD', libelle: 'Produits accessoires', type: 'produit' },
      { code: 'XB', libelle: 'CHIFFRE D\'AFFAIRES (TB + TC + TD)', type: 'solde', isTotal: true },
      
      { code: 'TE', libelle: 'Production stockée (ou destockage)', type: 'produit' },
      { code: 'TF', libelle: 'Production immobilisée', type: 'produit' },
      { code: 'RC', libelle: 'Achats de matières premières et fournitures liées', type: 'charge' },
      { code: 'RD', libelle: 'Variation stocks de matières premières', type: 'charge' },
      { code: 'RE', libelle: 'Autres achats', type: 'charge' },
      { code: 'RF', libelle: 'Variation stocks autres approvisionnements', type: 'charge' },
      { code: 'RG', libelle: 'Transports', type: 'charge' },
      { code: 'RH', libelle: 'Services extérieurs', type: 'charge' },
      { code: 'RI', libelle: 'Impôts et taxes', type: 'charge' },
      { code: 'RJ', libelle: 'Autres charges', type: 'charge' },
      { code: 'XC', libelle: 'VALEUR AJOUTÉE (XA + XB + TE + TF - RC à RJ)', type: 'solde', isTotal: true },
      
      { code: 'TH', libelle: 'Subventions d\'exploitation', type: 'produit' },
      { code: 'RK', libelle: 'Charges de personnel', type: 'charge' },
      { code: 'XD', libelle: 'EXCÉDENT BRUT D\'EXPLOITATION (XC + TH - RK)', type: 'solde', isTotal: true },
      
      { code: 'TI', libelle: 'Reprises d\'amortissements et de provisions', type: 'produit' },
      { code: 'TJ', libelle: 'Transferts de charges', type: 'produit' },
      { code: 'RL', libelle: 'Dotations aux amortissements et aux provisions', type: 'charge' },
      { code: 'XE', libelle: 'RÉSULTAT D\'EXPLOITATION (XD + TI + TJ - RL)', type: 'solde', isTotal: true },
      
      // RÉSULTAT FINANCIER
      { code: 'TK', libelle: 'Revenus financiers et produits assimilés', type: 'produit' },
      { code: 'RM', libelle: 'Frais financiers et charges assimilées', type: 'charge' },
      { code: 'XF', libelle: 'RÉSULTAT FINANCIER (TK - RM)', type: 'solde', isTotal: true },
      
      { code: 'XG', libelle: 'RÉSULTAT DES ACTIVITÉS ORDINAIRES (XE + XF)', type: 'solde', isTotal: true },
      
      // HORS ACTIVITÉS ORDINAIRES (HAO)
      { code: 'TN', libelle: 'Produits des cessions d\'immobilisations', type: 'produit' },
      { code: 'TO', libelle: 'Produits HAO', type: 'produit' },
      { code: 'TP', libelle: 'Reprises HAO', type: 'produit' },
      { code: 'TQ', libelle: 'Transferts de charges HAO', type: 'produit' },
      { code: 'RO', libelle: 'Valeurs comptables des cessions d\'immobilisations', type: 'charge' },
      { code: 'RP', libelle: 'Charges HAO', type: 'charge' },
      { code: 'RQ', libelle: 'Dotations HAO', type: 'charge' },
      { code: 'XH', libelle: 'RÉSULTAT HORS ACTIVITÉS ORDINAIRES (TN + TO + TP + TQ - RO - RP - RQ)', type: 'solde', isTotal: true },
      
      // RÉSULTAT NET
      { code: 'RS', libelle: 'Participation des travailleurs', type: 'charge' },
      { code: 'RT', libelle: 'Impôts sur le résultat', type: 'charge' },
      { code: 'XI', libelle: 'RÉSULTAT NET (XG + XH - RS - RT)', type: 'solde', isTotal: true, isGrandTotal: true }
    ];

    structure.forEach(item => {
      this.postes.set(item.code, new PosteCompteResultat(
        item.code,
        item.libelle,
        item.type as 'produit' | 'charge' | 'solde',
        item.isTotal || false,
        item.isGrandTotal || false
      ));
    });
  }

  affecterValeur(code: string, valeur: number): void {
    const poste = this.postes.get(code);
    if (poste) {
      poste.valeur = valeur;
    }
  }

  calculerSoldesIntermediaires(): void {
    // Marge commerciale
    const margeCommerciale = this.obtenirValeur('TA') - this.obtenirValeur('RA') - this.obtenirValeur('RB');
    this.affecterValeur('XA', margeCommerciale);

    // Chiffre d'affaires
    const chiffreAffaires = this.obtenirValeur('TB') + this.obtenirValeur('TC') + this.obtenirValeur('TD');
    this.affecterValeur('XB', chiffreAffaires);

    // Valeur ajoutée
    const valeurAjoutee = margeCommerciale + chiffreAffaires + this.obtenirValeur('TE') + this.obtenirValeur('TF')
      - this.obtenirValeur('RC') - this.obtenirValeur('RD') - this.obtenirValeur('RE') - this.obtenirValeur('RF')
      - this.obtenirValeur('RG') - this.obtenirValeur('RH') - this.obtenirValeur('RI') - this.obtenirValeur('RJ');
    this.affecterValeur('XC', valeurAjoutee);

    // Excédent brut d'exploitation
    const ebe = valeurAjoutee + this.obtenirValeur('TH') - this.obtenirValeur('RK');
    this.affecterValeur('XD', ebe);

    // Résultat d'exploitation
    const resultatExploitation = ebe + this.obtenirValeur('TI') + this.obtenirValeur('TJ') - this.obtenirValeur('RL');
    this.affecterValeur('XE', resultatExploitation);

    // Résultat financier
    const resultatFinancier = this.obtenirValeur('TK') - this.obtenirValeur('RM');
    this.affecterValeur('XF', resultatFinancier);

    // Résultat des activités ordinaires
    const resultatActivitesOrdinaires = resultatExploitation + resultatFinancier;
    this.affecterValeur('XG', resultatActivitesOrdinaires);

    // Résultat HAO
    const resultatHAO = this.obtenirValeur('TN') + this.obtenirValeur('TO') + this.obtenirValeur('TP') + this.obtenirValeur('TQ')
      - this.obtenirValeur('RO') - this.obtenirValeur('RP') - this.obtenirValeur('RQ');
    this.affecterValeur('XH', resultatHAO);

    // Résultat net
    const resultatNet = resultatActivitesOrdinaires + resultatHAO - this.obtenirValeur('RS') - this.obtenirValeur('RT');
    this.affecterValeur('XI', resultatNet);
  }

  private obtenirValeur(code: string): number {
    const poste = this.postes.get(code);
    return poste ? poste.valeur : 0;
  }

  obtenirPoste(code: string): PosteCompteResultat | undefined {
    return this.postes.get(code);
  }

  obtenirTousLesPostes(): PosteCompteResultat[] {
    return Array.from(this.postes.values());
  }

  obtenirProduitsExploitation(): PosteCompteResultat[] {
    return this.obtenirTousLesPostes().filter(p => p.type === 'produit' && !p.code.startsWith('TK') && !p.code.startsWith('TN'));
  }

  obtenirChargesExploitation(): PosteCompteResultat[] {
    return this.obtenirTousLesPostes().filter(p => p.type === 'charge' && !p.code.startsWith('RM') && !p.code.startsWith('RO'));
  }
}

export class PosteCompteResultat {
  constructor(
    public code: string,
    public libelle: string,
    public type: 'produit' | 'charge' | 'solde',
    public isTotal: boolean = false,
    public isGrandTotal: boolean = false,
    public valeur: number = 0
  ) {}

  get estSignificatif(): boolean {
    return Math.abs(this.valeur) > 0;
  }
}

// models/tableau-flux-syscohada.model.ts
export class TableauFluxSYSCOHADA {
  private postes: Map<string, PosteFlux> = new Map();

  constructor() {
    this.initialiserStructure();
  }

  private initialiserStructure(): void {
    const structure = [
      { code: 'ZA', libelle: 'Trésorerie nette au début d\'exercice', section: 'debut' },
      
      // FLUX DE TRÉSORERIE PROVENANT DES ACTIVITÉS OPÉRATIONNELLES
      { code: 'FA', libelle: 'Capacité d\'Autofinancement Globale (CAFG)', section: 'operationnel' },
      { code: 'FB', libelle: '- Variations de l\'actif circulant HAO', section: 'operationnel' },
      { code: 'FC', libelle: '- Variation des stocks', section: 'operationnel' },
      { code: 'FD', libelle: '- Variation des créances et emplois assimilés', section: 'operationnel' },
      { code: 'FE', libelle: '+ Variation du passif circulant', section: 'operationnel' },
      { code: 'ZB', libelle: 'FLUX DE TRÉSORERIE PROVENANT DES ACTIVITÉS OPÉRATIONNELLES', section: 'operationnel', isTotal: true },
      
      // FLUX DE TRÉSORERIE PROVENANT DES OPÉRATIONS D'INVESTISSEMENT
      { code: 'FF', libelle: '- Décaissements liés aux acquisitions d\'immobilisations incorporelles', section: 'investissement' },
      { code: 'FG', libelle: '- Décaissements liés aux acquisitions d\'immobilisations corporelles', section: 'investissement' },
      { code: 'FH', libelle: '- Décaissements liés aux acquisitions d\'immobilisations financières', section: 'investissement' },
      { code: 'FI', libelle: '+ Encaissements liés aux cessions d\'immobilisations incorporelles et corporelles', section: 'investissement' },
      { code: 'FJ', libelle: '+ Encaissements liés aux cessions d\'immobilisations financières', section: 'investissement' },
      { code: 'ZC', libelle: 'FLUX DE TRÉSORERIE PROVENANT DES OPÉRATIONS D\'INVESTISSEMENT', section: 'investissement', isTotal: true },
      
      // FLUX DE TRÉSORERIE PROVENANT DES CAPITAUX PROPRES
      { code: 'FK', libelle: '+ Augmentations de capital par apports nouveaux', section: 'capitaux' },
      { code: 'FL', libelle: '+ Subventions d\'investissement reçues', section: 'capitaux' },
      { code: 'FM', libelle: '- Prélèvements sur le capital', section: 'capitaux' },
      { code: 'FN', libelle: '- Distribution de dividendes', section: 'capitaux' },
      { code: 'ZD', libelle: 'FLUX DE TRÉSORERIE PROVENANT DES CAPITAUX PROPRES', section: 'capitaux', isTotal: true },
      
      // FLUX DE TRÉSORERIE PROVENANT DES CAPITAUX ÉTRANGERS
      { code: 'FO', libelle: '+ Emprunts', section: 'emprunts' },
      { code: 'FP', libelle: '+ Autres dettes financières', section: 'emprunts' },
      { code: 'FQ', libelle: '- Remboursements des emprunts et autres dettes financières', section: 'emprunts' },
      { code: 'ZE', libelle: 'FLUX DE TRÉSORERIE PROVENANT DES CAPITAUX ÉTRANGERS', section: 'emprunts', isTotal: true },
      
      { code: 'ZF', libelle: 'FLUX DE TRÉSORERIE PROVENANT DES ACTIVITÉS DE FINANCEMENT (ZD + ZE)', section: 'financement', isTotal: true },
      { code: 'ZG', libelle: 'VARIATION DE LA TRÉSORERIE NETTE DE LA PÉRIODE (ZB + ZC + ZF)', section: 'variation', isTotal: true },
      { code: 'ZH', libelle: 'Trésorerie nette à la fin d\'exercice (ZA + ZG)', section: 'fin', isTotal: true, isGrandTotal: true }
    ];

    structure.forEach(item => {
      this.postes.set(item.code, new PosteFlux(
        item.code,
        item.libelle,
        item.section,
        item.isTotal || false,
        item.isGrandTotal || false
      ));
    });
  }

  affecterValeur(code: string, valeur: number): void {
    const poste = this.postes.get(code);
    if (poste) {
      poste.valeur = valeur;
    }
  }

  calculerTotaux(): void {
    // Flux opérationnels
    const fluxOperationnels = this.obtenirValeur('FA') + this.obtenirValeur('FB') + this.obtenirValeur('FC') 
      + this.obtenirValeur('FD') + this.obtenirValeur('FE');
    this.affecterValeur('ZB', fluxOperationnels);

    // Flux d'investissement
    const fluxInvestissement = this.obtenirValeur('FF') + this.obtenirValeur('FG') + this.obtenirValeur('FH') 
      + this.obtenirValeur('FI') + this.obtenirValeur('FJ');
    this.affecterValeur('ZC', fluxInvestissement);

    // Flux capitaux propres
    const fluxCapitaux = this.obtenirValeur('FK') + this.obtenirValeur('FL') + this.obtenirValeur('FM') + this.obtenirValeur('FN');
    this.affecterValeur('ZD', fluxCapitaux);

    // Flux capitaux étrangers
    const fluxEmprunts = this.obtenirValeur('FO') + this.obtenirValeur('FP') + this.obtenirValeur('FQ');
    this.affecterValeur('ZE', fluxEmprunts);

    // Flux de financement
    const fluxFinancement = fluxCapitaux + fluxEmprunts;
    this.affecterValeur('ZF', fluxFinancement);

    // Variation de trésorerie
    const variationTresorerie = fluxOperationnels + fluxInvestissement + fluxFinancement;
    this.affecterValeur('ZG', variationTresorerie);

    // Trésorerie fin
    const tresorerieFin = this.obtenirValeur('ZA') + variationTresorerie;
    this.affecterValeur('ZH', tresorerieFin);
  }

  private obtenirValeur(code: string): number {
    const poste = this.postes.get(code);
    return poste ? poste.valeur : 0;
  }

  obtenirPoste(code: string): PosteFlux | undefined {
    return this.postes.get(code);
  }

  obtenirPostesParSection(section: string): PosteFlux[] {
    return Array.from(this.postes.values()).filter(p => p.section === section);
  }

  verifierCoherence(): boolean {
    const calculVariation = this.obtenirValeur('ZB') + this.obtenirValeur('ZC') + this.obtenirValeur('ZF');
    const variationAffichee = this.obtenirValeur('ZG');
    return Math.abs(calculVariation - variationAffichee) < 0.01;
  }
}

export class PosteFlux {
  constructor(
    public code: string,
    public libelle: string,
    public section: string,
    public isTotal: boolean = false,
    public isGrandTotal: boolean = false,
    public valeur: number = 0
  ) {}

  get estSignificatif(): boolean {
    return Math.abs(this.valeur) > 0;
  }
}

// models/annexes-syscohada.model.ts
export class AnnexesSYSCOHADA {
  private notes: Map<string, NoteAnnexe> = new Map();

  constructor() {
    this.initialiserNotes();
  }

  private initialiserNotes(): void {
    const notesStructure = [
      { code: 'NOTE1', titre: 'Dettes garanties par des sûretés réelles', obligatoire: true },
      { code: 'NOTE2', titre: 'Informations obligatoires', obligatoire: true },
      { code: 'NOTE3A', titre: 'Immobilisations brutes', obligatoire: true },
      { code: 'NOTE3C', titre: 'Immobilisations : amortissements', obligatoire: true },
      { code: 'NOTE3D', titre: 'Immobilisations : plus et moins-values de cession', obligatoire: false },
      { code: 'NOTE4', titre: 'Immobilisations financières', obligatoire: false },
      { code: 'NOTE6', titre: 'Stocks et en-cours', obligatoire: true },
      { code: 'NOTE7', titre: 'Clients', obligatoire: true },
      { code: 'NOTE27A', titre: 'Charges de personnel', obligatoire: true },
      { code: 'NOTE34', titre: 'Fiche de synthèse des principaux indicateurs financiers', obligatoire: true }
    ];

    notesStructure.forEach(note => {
      this.notes.set(note.code, new NoteAnnexe(note.code, note.titre, note.obligatoire));
    });
  }

  genererNote3A(balance: BalanceComptable): void {
    const note = this.notes.get('NOTE3A');
    if (!note) return;

    const tableauImmob = new TableauImmobilisations();
    
    // Immobilisations incorporelles
    tableauImmob.ajouterLigne('IMMOBILISATIONS INCORPORELLES', '', '', '', '', true);
    tableauImmob.ajouterLigne('Frais d\'établissement', this.getSoldeCompte(balance, '201'), '0', '0', '0');
    tableauImmob.ajouterLigne('Frais de recherche et développement', this.getSoldeCompte(balance, '202'), '0', '0', '0');
    tableauImmob.ajouterLigne('Brevets, licences, logiciels', this.getSoldeCompte(balance, '211'), '0', '0', '0');
    tableauImmob.ajouterLigne('Fonds commercial', this.getSoldeCompte(balance, '214'), '0', '0', '0');
    
    // Immobilisations corporelles
    tableauImmob.ajouterLigne('IMMOBILISATIONS CORPORELLES', '', '', '', '', true);
    tableauImmob.ajouterLigne('Terrains', this.getSoldeCompte(balance, '221'), '0', '0', '0');
    tableauImmob.ajouterLigne('Bâtiments', this.getSoldeCompte(balance, '222'), '0', '0', '0');
    tableauImmob.ajouterLigne('Matériel et outillage', this.getSoldeCompte(balance, '231'), '0', '0', '0');
    tableauImmob.ajouterLigne('Matériel de transport', this.getSoldeCompte(balance, '245'), '0', '0', '0');
    
    note.contenu = tableauImmob;
  }

  genererNote3C(balance: BalanceComptable): void {
    const note = this.notes.get('NOTE3C');
    if (!note) return;

    const tableauAmort = new TableauAmortissements();
    
    tableauAmort.ajouterLigne('Frais d\'établissement', 
      this.getSoldeCompte(balance, '2801'), 
      this.getMouvementDebit(balance, '2801'),
      this.getMouvementCredit(balance, '2801'));
    
    tableauAmort.ajouterLigne('Brevets, licences, logiciels',
      this.getSoldeCompte(balance, '2811'),
      this.getMouvementDebit(balance, '2811'),
      this.getMouvementCredit(balance, '2811'));
    
    tableauAmort.ajouterLigne('Bâtiments',
      this.getSoldeCompte(balance, '2822'),
      this.getMouvementDebit(balance, '2822'),
      this.getMouvementCredit(balance, '2822'));
    
    note.contenu = tableauAmort;
  }

  genererNote6(balance: BalanceComptable): void {
    const note = this.notes.get('NOTE6');
    if (!note) return;

    const tableauStocks = new TableauStocks();
    
    tableauStocks.ajouterLigne('Marchandises', this.getSoldeCompte(balance, '31'));
    tableauStocks.ajouterLigne('Matières premières', this.getSoldeCompte(balance, '321'));
    tableauStocks.ajouterLigne('Autres approvisionnements', this.getSoldeCompte(balance, '322'));
    tableauStocks.ajouterLigne('Produits en cours', this.getSoldeCompte(balance, '331'));
    tableauStocks.ajouterLigne('Produits finis', this.getSoldeCompte(balance, '36'));
    
    note.contenu = tableauStocks;
  }

  genererNote7(balance: BalanceComptable): void {
    const note = this.notes.get('NOTE7');
    if (!note) return;

    const tableauClients = new TableauClients();
    
    tableauClients.ajouterLigne('Clients', this.getSoldeCompte(balance, '411'));
    tableauClients.ajouterLigne('Clients-Effets à recevoir', this.getSoldeCompte(balance, '413'));
    tableauClients.ajouterLigne('Clients douteux', this.getSoldeCompte(balance, '416'));
    tableauClients.ajouterLigne('Créances sur travaux non facturés', this.getSoldeCompte(balance, '418'));
    
    const provisionsClients = this.getSoldeCompte(balance, '491');
    tableauClients.ajouterProvision(provisionsClients);
    
    note.contenu = tableauClients;
  }

  genererNote27A(balance: BalanceComptable): void {
    const note = this.notes.get('NOTE27A');
    if (!note) return;

    const tableauPersonnel = new TableauChargesPersonnel();
    
    tableauPersonnel.ajouterLigne('Appointements et salaires', this.getSoldeCompte(balance, '661'));
    tableauPersonnel.ajouterLigne('Primes et gratifications', this.getSoldeCompte(balance, '662'));
    tableauPersonnel.ajouterLigne('Indemnités et avantages divers', this.getSoldeCompte(balance, '663'));
    tableauPersonnel.ajouterLigne('Charges sociales sur rémunérations', this.getSoldeCompte(balance, '664'));
    tableauPersonnel.ajouterLigne('Charges sociales diverses', this.getSoldeCompte(balance, '665'));
    tableauPersonnel.ajouterLigne('Rémunérations transférées de charges', this.getSoldeCompte(balance, '667'));
    
    note.contenu = tableauPersonnel;
  }

  private getSoldeCompte(balance: BalanceComptable, numeroCompte: string): string {
    const compte = balance.comptes.find(c => c.numeroCompte.startsWith(numeroCompte));
    if (!compte) return '0';
    
    const solde = compte.debitSoldeFinal - compte.creditSoldeFinal;
    return Math.abs(solde).toLocaleString('fr-FR', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
  }

  private getMouvementDebit(balance: BalanceComptable, numeroCompte: string): string {
    const compte = balance.comptes.find(c => c.numeroCompte.startsWith(numeroCompte));
    if (!compte) return '0';
    
    return compte.debitMouvements.toLocaleString('fr-FR', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
  }

  private getMouvementCredit(balance: BalanceComptable, numeroCompte: string): string {
    const compte = balance.comptes.find(c => c.numeroCompte.startsWith(numeroCompte));
    if (!compte) return '0';
    
    return compte.creditMouvements.toLocaleString('fr-FR', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
  }

  obtenirNote(code: string): NoteAnnexe | undefined {
    return this.notes.get(code);
  }

  obtenirToutesLesNotes(): NoteAnnexe[] {
    return Array.from(this.notes.values());
  }
}

export class NoteAnnexe {
  constructor(
    public code: string,
    public titre: string,
    public obligatoire: boolean,
    public contenu: any = null
  ) {}
}

// Classes pour les tableaux des annexes
export class TableauImmobilisations {
  lignes: LigneImmobilisation[] = [];

  ajouterLigne(libelle: string, valeurBrute: string, augmentations: string, 
               diminutions: string, valeurBruteFin: string, isTotal: boolean = false): void {
    this.lignes.push(new LigneImmobilisation(libelle, valeurBrute, augmentations, diminutions, valeurBruteFin, isTotal));
  }
}

export class LigneImmobilisation {
  constructor(
    public libelle: string,
    public valeurBrute: string,
    public augmentations: string,
    public diminutions: string,
    public valeurBruteFin: string,
    public isTotal: boolean = false
  ) {}
}

export class TableauAmortissements {
  lignes: LigneAmortissement[] = [];

  ajouterLigne(libelle: string, amortissementDebut: string, 
               dotations: string, amortissementFin: string): void {
    this.lignes.push(new LigneAmortissement(libelle, amortissementDebut, dotations, amortissementFin));
  }
}

export class LigneAmortissement {
  constructor(
    public libelle: string,
    public amortissementDebut: string,
    public dotations: string,
    public amortissementFin: string
  ) {}
}

export class TableauStocks {
  lignes: LigneStock[] = [];

  ajouterLigne(libelle: string, valeur: string): void {
    this.lignes.push(new LigneStock(libelle, valeur));
  }
}

export class LigneStock {
  constructor(
    public libelle: string,
    public valeur: string
  ) {}
}

export class TableauClients {
  lignes: LigneClient[] = [];
  provisions: string = '0';

  ajouterLigne(libelle: string, valeur: string): void {
    this.lignes.push(new LigneClient(libelle, valeur));
  }

  ajouterProvision(montant: string): void {
    this.provisions = montant;
  }
}

export class LigneClient {
  constructor(
    public libelle: string,
    public valeur: string
  ) {}
}

export class TableauChargesPersonnel {
  lignes: LigneChargePersonnel[] = [];

  ajouterLigne(libelle: string, montant: string): void {
    this.lignes.push(new LigneChargePersonnel(libelle, montant));
  }
}

export class LigneChargePersonnel {
  constructor(
    public libelle: string,
    public montant: string
  ) {}
}

// component/etats-financiers-auto.component.ts
@Component({
  selector: 'app-etats-financiers-auto',
  templateUrl: './etats-financiers-auto.component.html',
  styleUrls: ['./etats-financiers-auto.component.css']
})
export class EtatsFinanciersAutoComponent implements OnInit {
  etatsForm: FormGroup;
  etatsData: EtatFinancierAuto | null = null;
  loading = false;
  error: string | null = null;
  selectedEtat: string = 'bilan';

  constructor(
    private fb: FormBuilder,
    private etatsFinanciersAutoService: EtatsFinanciersAutoService
  ) {
    this.etatsForm = this.fb.group({
      dateDebut: ['', Validators.required],
      dateFin: ['', Validators.required],
      exercicePrecedent: [false]
    });
  }

  ngOnInit(): void {}

  genererEtatsFinanciers(): void {
    if (this.etatsForm.valid) {
      this.loading = true;
      this.error = null;

      const dateDebut = this.etatsForm.get('dateDebut')?.value;
      const dateFin = this.etatsForm.get('dateFin')?.value;

      this.etatsFinanciersAutoService.genererEtatsFinanciers(dateDebut, dateFin).subscribe({
        next: (data) => {
          this.etatsData = data;
          this.loading = false;
        },
        error: (error) => {
          this.error = 'Erreur lors de la génération des états financiers: ' + error.message;
          this.loading = false;
        }
      });
    }
  }

  changerEtat(etat: string): void {
    this.selectedEtat = etat;
  }

  exporterToutPDF(): void {
    if (this.etatsData) {
      // Implémentation de l'export complet en PDF
      console.log('Export complet PDF des états financiers');
    }
  }

  exporterEtatPDF(etat: string): void {
    if (this.etatsData) {
      // Implémentation de l'export spécifique
      console.log(`Export PDF de l'état: ${etat}`);
    }
  }

  verifierCoherenceEtats(): boolean {
    if (!this.etatsData) return false;
    
    // Vérification de l'équilibre du bilan
    const equilibreBilan = this.etatsData.bilan.verifierEquilibre();
    
    // Vérification de la cohérence du tableau des flux
    const coherenceFlux = this.etatsData.tableauFlux.verifierCoherence();
    
    // Vérification résultat net identique entre bilan et compte de résultat
    const resultatBilan = this.etatsData.bilan.obtenirPoste('CG')?.valeurExerciceCourant || 0;
    const resultatCR = this.etatsData.compteResultat.obtenirPoste('XI')?.valeur || 0;
    const coherenceResultat = Math.abs(resultatBilan - resultatCR) < 0.01;
    
    return equilibreBilan && coherenceFlux && coherenceResultat;
  }
}

// Templates HTML avec design conforme SYSCOHADA

// bilan-syscohada.component.html
/*
<div class="etats-financiers-container">
  <div class="header-etat">
    <h2 class="titre-etat">BILAN AU {{ dateArrete | date:'dd/MM/yyyy' }}</h2>
    <div class="info-entreprise">
      <p><strong>{{ nomEntreprise }}</strong></p>
      <p>{{ adresseEntreprise }}</p>
      <p>Exercice du {{ dateDebut | date:'dd/MM/yyyy' }} au {{ dateFin | date:'dd/MM/yyyy' }}</p>
    </div>
  </div>

  <form [formGroup]="bilanForm" (ngSubmit)="genererBilan()" class="form-etat">
    <div class="form-group">
      <label for="dateArrete">Date d'arrêté :</label>
      <input type="date" id="dateArrete" formControlName="dateArrete" class="form-control">
    </div>
    <button type="submit" class="btn btn-primary" [disabled]="loading">
      {{ loading ? 'Génération...' : 'Générer le Bilan' }}
    </button>
  </form>

  <div *ngIf="error" class="alert alert-danger">{{ error }}</div>

  <div *ngIf="bilanData" class="bilan-syscohada">
    <table class="table-bilan">
      <thead>
        <tr class="header-row">
          <th colspan="2" class="actif-header">ACTIF</th>
          <th class="montant-header">EXERCICE AU<br>{{ dateFin | date:'dd/MM/yyyy' }}</th>
          <th class="montant-header">EXERCICE AU<br>{{ dateFinPrecedent | date:'dd/MM/yyyy' }}</th>
        </tr>
        <tr class="sous-header">
          <th class="ref-col">REF</th>
          <th class="libelle-col">LIBELLÉ</th>
          <th class="montant-col">MONTANT</th>
          <th class="montant-col">MONTANT</th>
        </tr>
      </thead>
      <tbody>
        <!-- ACTIF IMMOBILISÉ -->
        <tr class="section-header">
          <td colspan="4" class="section-title">ACTIF IMMOBILISÉ</td>
        </tr>
        <tr *ngFor="let poste of getPostesActifImmobilise()" 
            [class.total-row]="poste.isTotal"
            [class.niveau-1]="poste.niveau === 1"
            [class.niveau-2]="poste.niveau === 2">
          <td class="ref">{{ poste.code }}</td>
          <td class="libelle" [class.indent-niveau-2]="poste.niveau === 2">{{ poste.libelle }}</td>
          <td class="montant">{{ formatMontant(poste.valeurExerciceCourant) }}</td>
          <td class="montant">{{ formatMontant(poste.valeurExercicePrecedent) }}</td>
        </tr>

        <!-- ACTIF CIRCULANT -->
        <tr class="section-header">
          <td colspan="4" class="section-title">ACTIF CIRCULANT</td>
        </tr>
        <tr *ngFor="let poste of getPostesActifCirculant()" 
            [class.total-row]="poste.isTotal"
            [class.niveau-1]="poste.niveau === 1"
            [class.niveau-2]="poste.niveau === 2">
          <td class="ref">{{ poste.code }}</td>
          <td class="libelle" [class.indent-niveau-2]="poste.niveau === 2">{{ poste.libelle }}</td>
          <td class="montant">{{ formatMontant(poste.valeurExerciceCourant) }}</td>
          <td class="montant">{{ formatMontant(poste.valeurExercicePrecedent) }}</td>
        </tr>

        <!-- TRÉSORERIE ACTIF -->
        <tr class="section-header">
          <td colspan="4" class="section-title">TRÉSORERIE-ACTIF</td>
        </tr>
        <tr *ngFor="let poste of getPostesTresorerieActif()" 
            [class.total-row]="poste.isTotal">
          <td class="ref">{{ poste.code }}</td>
          <td class="libelle">{{ poste.libelle }}</td>
          <td class="montant">{{ formatMontant(poste.valeurExerciceCourant) }}</td>
          <td class="montant">{{ formatMontant(poste.valeurExercicePrecedent) }}</td>
        </tr>

        <!-- ÉCART DE CONVERSION ACTIF -->
        <tr *ngFor="let poste of getPostesEcartConversionActif()">
          <td class="ref">{{ poste.code }}</td>
          <td class="libelle">{{ poste.libelle }}</td>
          <td class="montant">{{ formatMontant(poste.valeurExerciceCourant) }}</td>
          <td class="montant">{{ formatMontant(poste.valeurExercicePrecedent) }}</td>
        </tr>

        <!-- TOTAL GÉNÉRAL ACTIF -->
        <tr class="grand-total-row">
          <td class="ref">BZ</td>
          <td class="libelle"><strong>TOTAL GÉNÉRAL ACTIF</strong></td>
          <td class="montant"><strong>{{ formatMontant(calculerTotalActif()) }}</strong></td>
          <td class="montant"><strong>{{ formatMontant(calculerTotalActifPrecedent()) }}</strong></td>
        </tr>
      </tbody>
    </table>

    <table class="table-bilan passif-table">
      <thead>
        <tr class="header-row">
          <th colspan="2" class="passif-header">PASSIF</th>
          <th class="montant-header">EXERCICE AU<br>{{ dateFin | date:'dd/MM/yyyy' }}</th>
          <th class="montant-header">EXERCICE AU<br>{{ dateFinPrecedent | date:'dd/MM/yyyy' }}</th>
        </tr>
        <tr class="sous-header">
          <th class="ref-col">REF</th>
          <th class="libelle-col">LIBELLÉ</th>
          <th class="montant-col">MONTANT</th>
          <th class="montant-col">MONTANT</th>
        </tr>
      </thead>
      <tbody>
        <!-- CAPITAUX PROPRES -->
        <tr class="section-header">
          <td colspan="4" class="section-title">CAPITAUX PROPRES ET RESSOURCES ASSIMILÉES</td>
        </tr>
        <tr *ngFor="let poste of getPostesCapitauxPropres()" 
            [class.total-row]="poste.isTotal"
            [class.niveau-1]="poste.niveau === 1"
            [class.niveau-2]="poste.niveau === 2">
          <td class="ref">{{ poste.code }}</td>
          <td class="libelle" [class.indent-niveau-2]="poste.niveau === 2">{{ poste.libelle }}</td>
          <td class="montant">{{ formatMontant(poste.valeurExerciceCourant) }}</td>
          <td class="montant">{{ formatMontant(poste.valeurExercicePrecedent) }}</td>
        </tr>

        <!-- DETTES FINANCIÈRES -->
        <tr class="section-header">
          <td colspan="4" class="section-title">DETTES FINANCIÈRES ET RESSOURCES ASSIMILÉES</td>
        </tr>
        <tr *ngFor="let poste of getPostesDettesFinancieres()" 
            [class.total-row]="poste.isTotal">
          <td class="ref">{{ poste.code }}</td>
          <td class="libelle">{{ poste.libelle }}</td>
          <td class="montant">{{ formatMontant(poste.valeurExerciceCourant) }}</td>
          <td class="montant">{{ formatMontant(poste.valeurExercicePrecedent) }}</td>
        </tr>

        <!-- TOTAL RESSOURCES STABLES -->
        <tr class="total-intermediaire-row">
          <td class="ref">DG</td>
          <td class="libelle"><strong>TOTAL RESSOURCES STABLES</strong></td>
          <td class="montant"><strong>{{ formatMontant(calculerTotalRessourcesStables()) }}</strong></td>
          <td class="montant"><strong>{{ formatMontant(calculerTotalRessourcesStablesPrecedent()) }}</strong></td>
        </tr>

        <!-- PASSIF CIRCULANT -->
        <tr class="section-header">
          <td colspan="4" class="section-title">PASSIF CIRCULANT</td>
        </tr>
        <tr *ngFor="let poste of getPostesPassifCirculant()" 
            [class.total-row]="poste.isTotal">
          <td class="ref">{{ poste.code }}</td>
          <td class="libelle">{{ poste.libelle }}</td>
          <td class="montant">{{ formatMontant(poste.valeurExerciceCourant) }}</td>
          <td class="montant">{{ formatMontant(poste.valeurExercicePrecedent) }}</td>
        </tr>

        <!-- TRÉSORERIE PASSIF -->
        <tr class="section-header">
          <td colspan="4" class="section-title">TRÉSORERIE-PASSIF</td>
        </tr>
        <tr *ngFor="let poste of getPostesTresoreriePassif()" 
            [class.total-row]="poste.isTotal">
          <td class="ref">{{ poste.code }}</td>
          <td class="libelle">{{ poste.libelle }}</td>
          <td class="montant">{{ formatMontant(poste.valeurExerciceCourant) }}</td>
          <td class="montant">{{ formatMontant(poste.valeurExercicePrecedent) }}</td>
        </tr>

        <!-- ÉCART DE CONVERSION PASSIF -->
        <tr *ngFor="let poste of getPostesEcartConversionPassif()">
          <td class="ref">{{ poste.code }}</td>
          <td class="libelle">{{ poste.libelle }}</td>
          <td class="montant">{{ formatMontant(poste.valeurExerciceCourant) }}</td>
          <td class="montant">{{ formatMontant(poste.valeurExercicePrecedent) }}</td>
        </tr>

        <!-- TOTAL GÉNÉRAL PASSIF -->
        <tr class="grand-total-row">
          <td class="ref">DZ</td>
          <td class="libelle"><strong>TOTAL GÉNÉRAL PASSIF</strong></td>
          <td class="montant"><strong>{{ formatMontant(calculerTotalPassif()) }}</strong></td>
          <td class="montant"><strong>{{ formatMontant(calculerTotalPassifPrecedent()) }}</strong></td>
        </tr>
      </tbody>
    </table>

    <div class="controle-equilibre" [class.equilibre]="isEquilibre()" [class.desequilibre]="!isEquilibre()">
      <p><strong>Contrôle d'équilibre : {{ isEquilibre() ? 'ÉQUILIBRÉ' : 'DÉSÉQUILIBRÉ' }}</strong></p>
      <p>Total Actif : {{ formatMontant(calculerTotalActif()) }}</p>
      <p>Total Passif : {{ formatMontant(calculerTotalPassif()) }}</p>
      <p>Écart : {{ formatMontant(Math.abs(calculerTotalActif() - calculerTotalPassif())) }}</p>
    </div>

    <div class="actions-export">
      <button (click)="exporterPDF()" class="btn btn-secondary">
        <i class="fa fa-file-pdf"></i> Exporter en PDF
      </button>
    </div>
  </div>
</div>
*/

// compte-resultat-syscohada.component.html
/*
<div class="etats-financiers-container">
  <div class="header-etat">
    <h2 class="titre-etat">COMPTE DE RÉSULTAT</h2>
    <div class="info-entreprise">
      <p><strong>{{ nomEntreprise }}</strong></p>
      <p>{{ adresseEntreprise }}</p>
      <p>Exercice du {{ dateDebut | date:'dd/MM/yyyy' }} au {{ dateFin | date:'dd/MM/yyyy' }}</p>
    </div>
  </div>

  <form [formGroup]="compteResultatForm" (ngSubmit)="genererCompteResultat()" class="form-etat">
    <div class="form-group">
      <label for="dateDebut">Date de début :</label>
      <input type="date" id="dateDebut" formControlName="dateDebut" class="form-control">
    </div>
    <div class="form-group">
      <label for="dateFin">Date de fin :</label>
      <input type="date" id="dateFin" formControlName="dateFin" class="form-control">
    </div>
    <button type="submit" class="btn btn-primary" [disabled]="loading">
      {{ loading ? 'Génération...' : 'Générer le Compte de Résultat' }}
    </button>
  </form>

  <div *ngIf="error" class="alert alert-danger">{{ error }}</div>

  <div *ngIf="compteResultatData" class="compte-resultat-syscohada">
    <table class="table-compte-resultat">
      <thead>
        <tr class="header-row">
          <th class="ref-col">REF</th>
          <th class="libelle-col">LIBELLÉ</th>
          <th class="exercice-col">EXERCICE AU<br>{{ dateFin | date:'dd/MM/yyyy' }}</th>
          <th class="exercice-col">EXERCICE AU<br>{{ dateFinPrecedent | date:'dd/MM/yyyy' }}</th>
        </tr>
      </thead>
      <tbody>
        <!-- ACTIVITÉS D'EXPLOITATION -->
        <tr class="section-header">
          <td colspan="4" class="section-title">ACTIVITÉS D'EXPLOITATION</td>
        </tr>

        <!-- Ventes et marge commerciale -->
        <tr>
          <td class="ref">TA</td>
          <td class="libelle">Ventes de marchandises</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('TA')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('TA')) }}</td>
        </tr>
        <tr>
          <td class="ref">RA</td>
          <td class="libelle">Achats de marchandises</td>
          <td class="montant charge">{{ formatMontant(obtenirValeurPoste('RA')) }}</td>
          <td class="montant charge">{{ formatMontant(obtenirValeurPostePrecedent('RA')) }}</td>
        </tr>
        <tr>
          <td class="ref">RB</td>
          <td class="libelle">Variation stocks de marchandises</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('RB')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('RB')) }}</td>
        </tr>
        <tr class="solde-row">
          <td class="ref">XA</td>
          <td class="libelle"><strong>MARGE COMMERCIALE (TA - RA - RB)</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPoste('XA')) }}</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPostePrecedent('XA')) }}</strong></td>
        </tr>

        <!-- Chiffre d'affaires -->
        <tr>
          <td class="ref">TB</td>
          <td class="libelle">Ventes de produits fabriqués</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('TB')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('TB')) }}</td>
        </tr>
        <tr>
          <td class="ref">TC</td>
          <td class="libelle">Travaux, services vendus</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('TC')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('TC')) }}</td>
        </tr>
        <tr>
          <td class="ref">TD</td>
          <td class="libelle">Produits accessoires</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('TD')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('TD')) }}</td>
        </tr>
        <tr class="solde-row">
          <td class="ref">XB</td>
          <td class="libelle"><strong>CHIFFRE D'AFFAIRES (TB + TC + TD)</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPoste('XB')) }}</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPostePrecedent('XB')) }}</strong></td>
        </tr>

        <!-- Production et consommations -->
        <tr>
          <td class="ref">TE</td>
          <td class="libelle">Production stockée (ou destockage)</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('TE')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('TE')) }}</td>
        </tr>
        <tr>
          <td class="ref">TF</td>
          <td class="libelle">Production immobilisée</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('TF')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('TF')) }}</td>
        </tr>

        <!-- Consommations de l'exercice -->
        <tr>
          <td class="ref">RC</td>
          <td class="libelle">Achats de matières premières et fournitures liées</td>
          <td class="montant charge">{{ formatMontant(obtenirValeurPoste('RC')) }}</td>
          <td class="montant charge">{{ formatMontant(obtenirValeurPostePrecedent('RC')) }}</td>
        </tr>
        <tr>
          <td class="ref">RD</td>
          <td class="libelle">Variation stocks de matières premières</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('RD')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('RD')) }}</td>
        </tr>
        <tr>
          <td class="ref">RE</td>
          <td class="libelle">Autres achats</td>
          <td class="montant charge">{{ formatMontant(obtenirValeurPoste('RE')) }}</td>
          <td class="montant charge">{{ formatMontant(obtenirValeurPostePrecedent('RE')) }}</td>
        </tr>
        <tr>
          <td class="ref">RF</td>
          <td class="libelle">Variation stocks autres approvisionnements</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('RF')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('RF')) }}</td>
        </tr>
        <tr>
          <td class="ref">RG</td>
          <td class="libelle">Transports</td>
          <td class="montant charge">{{ formatMontant(obtenirValeurPoste('RG')) }}</td>
          <td class="montant charge">{{ formatMontant(obtenirValeurPostePrecedent('RG')) }}</td>
        </tr>
        <tr>
          <td class="ref">RH</td>
          <td class="libelle">Services extérieurs</td>
          <td class="montant charge">{{ formatMontant(obtenirValeurPoste('RH')) }}</td>
          <td class="montant charge">{{ formatMontant(obtenirValeurPostePrecedent('RH')) }}</td>
        </tr>
        <tr>
          <td class="ref">RI</td>
          <td class="libelle">Impôts et taxes</td>
          <td class="montant charge">{{ formatMontant(obtenirValeurPoste('RI')) }}</td>
          <td class="montant charge">{{ formatMontant(obtenirValeurPostePrecedent('RI')) }}</td>
        </tr>
        <tr>
          <td class="ref">RJ</td>
          <td class="libelle">Autres charges</td>
          <td class="montant charge">{{ formatMontant(obtenirValeurPoste('RJ')) }}</td>
          <td class="montant charge">{{ formatMontant(obtenirValeurPostePrecedent('RJ')) }}</td>
        </tr>

        <!-- Valeur ajoutée -->
        <tr class="solde-row important">
          <td class="ref">XC</td>
          <td class="libelle"><strong>VALEUR AJOUTÉE (XA + XB + TE + TF - RC à RJ)</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPoste('XC')) }}</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPostePrecedent('XC')) }}</strong></td>
        </tr>

        <!-- Subventions et charges de personnel -->
        <tr>
          <td class="ref">TH</td>
          <td class="libelle">Subventions d'exploitation</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('TH')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('TH')) }}</td>
        </tr>
        <tr>
          <td class="ref">RK</td>
          <td class="libelle">Charges de personnel</td>
          <td class="montant charge">{{ formatMontant(obtenirValeurPoste('RK')) }}</td>
          <td class="montant charge">{{ formatMontant(obtenirValeurPostePrecedent('RK')) }}</td>
        </tr>

        <!-- Excédent brut d'exploitation -->
        <tr class="solde-row important">
          <td class="ref">XD</td>
          <td class="libelle"><strong>EXCÉDENT BRUT D'EXPLOITATION (XC + TH - RK)</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPoste('XD')) }}</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPostePrecedent('XD')) }}</strong></td>
        </tr>

        <!-- Autres produits et charges d'exploitation -->
        <tr>
          <td class="ref">TI</td>
          <td class="libelle">Reprises d'amortissements et de provisions</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('TI')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('TI')) }}</td>
        </tr>
        <tr>
          <td class="ref">TJ</td>
          <td class="libelle">Transferts de charges</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('TJ')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('TJ')) }}</td>
        </tr>
        <tr>
          <td class="ref">RL</td>
          <td class="libelle">Dotations aux amortissements et aux provisions</td>
          <td class="montant charge">{{ formatMontant(obtenirValeurPoste('RL')) }}</td>
          <td class="montant charge">{{ formatMontant(obtenirValeurPostePrecedent('RL')) }}</td>
        </tr>

        <!-- Résultat d'exploitation -->
        <tr class="solde-row important">
          <td class="ref">XE</td>
          <td class="libelle"><strong>RÉSULTAT D'EXPLOITATION (XD + TI + TJ - RL)</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPoste('XE')) }}</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPostePrecedent('XE')) }}</strong></td>
        </tr>

        <!-- ACTIVITÉS FINANCIÈRES -->
        <tr class="section-header">
          <td colspan="4" class="section-title">ACTIVITÉS FINANCIÈRES</td>
        </tr>
        <tr>
          <td class="ref">TK</td>
          <td class="libelle">Revenus financiers et produits assimilés</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('TK')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('TK')) }}</td>
        </tr>
        <tr>
          <td class="ref">RM</td>
          <td class="libelle">Frais financiers et charges assimilées</td>
          <td class="montant charge">{{ formatMontant(obtenirValeurPoste('RM')) }}</td>
          <td class="montant charge">{{ formatMontant(obtenirValeurPostePrecedent('RM')) }}</td>
        </tr>
        <tr class="solde-row">
          <td class="ref">XF</td>
          <td class="libelle"><strong>RÉSULTAT FINANCIER (TK - RM)</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPoste('XF')) }}</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPostePrecedent('XF')) }}</strong></td>
        </tr>

        <!-- Résultat des activités ordinaires -->
        <tr class="solde-row important">
          <td class="ref">XG</td>
          <td class="libelle"><strong>RÉSULTAT DES ACTIVITÉS ORDINAIRES (XE + XF)</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPoste('XG')) }}</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPostePrecedent('XG')) }}</strong></td>
        </tr>

        <!-- HORS ACTIVITÉS ORDINAIRES -->
        <tr class="section-header">
          <td colspan="4" class="section-title">HORS ACTIVITÉS ORDINAIRES (HAO)</td>
        </tr>
        <tr>
          <td class="ref">TN</td>
          <td class="libelle">Produits des cessions d'immobilisations</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('TN')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('TN')) }}</td>
        </tr>
        <tr>
          <td class="ref">TO</td>
          <td class="libelle">Produits HAO</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('TO')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('TO')) }}</td>
        </tr>
        <tr>
          <td class="ref">TP</td>
          <td class="libelle">Reprises HAO</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('TP')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('TP')) }}</td>
        </tr>
        <tr>
          <td class="ref">TQ</td>
          <td class="libelle">Transferts de charges HAO</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('TQ')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('TQ')) }}</td>
        </tr>
        <tr>
          <td class="ref">RO</td>
          <td class="libelle">Valeurs comptables des cessions d'immobilisations</td>
          <td class="montant charge">{{ formatMontant(obtenirValeurPoste('RO')) }}</td>
          <td class="montant charge">{{ formatMontant(obtenirValeurPostePrecedent('RO')) }}</td>
        </tr>
        <tr>
          <td class="ref">RP</td>
          <td class="libelle">Charges HAO</td>
          <td class="montant charge">{{ formatMontant(obtenirValeurPoste('RP')) }}</td>
          <td class="montant charge">{{ formatMontant(obtenirValeurPostePrecedent('RP')) }}</td>
        </tr>
        <tr>
          <td class="ref">RQ</td>
          <td class="libelle">Dotations HAO</td>
          <td class="montant charge">{{ formatMontant(obtenirValeurPoste('RQ')) }}</td>
          <td class="montant charge">{{ formatMontant(obtenirValeurPostePrecedent('RQ')) }}</td>
        </tr>
        <tr class="solde-row">
          <td class="ref">XH</td>
          <td class="libelle"><strong>RÉSULTAT HORS ACTIVITÉS ORDINAIRES (TN + TO + TP + TQ - RO - RP - RQ)</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPoste('XH')) }}</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPostePrecedent('XH')) }}</strong></td>
        </tr>

        <!-- Participation et impôts -->
        <tr>
          <td class="ref">RS</td>
          <td class="libelle">Participation des travailleurs</td>
          <td class="montant charge">{{ formatMontant(obtenirValeurPoste('RS')) }}</td>
          <td class="montant charge">{{ formatMontant(obtenirValeurPostePrecedent('RS')) }}</td>
        </tr>
        <tr>
          <td class="ref">RT</td>
          <td class="libelle">Impôts sur le résultat</td>
          <td class="montant charge">{{ formatMontant(obtenirValeurPoste('RT')) }}</td>
          <td class="montant charge">{{ formatMontant(obtenirValeurPostePrecedent('RT')) }}</td>
        </tr>

        <!-- RÉSULTAT NET -->
        <tr class="resultat-net-row">
          <td class="ref">XI</td>
          <td class="libelle"><strong>RÉSULTAT NET DE L'EXERCICE (Bénéfice + ou Perte -) (XG + XH - RS - RT)</strong></td>
          <td class="montant resultat-net"><strong>{{ formatMontant(obtenirValeurPoste('XI')) }}</strong></td>
          <td class="montant resultat-net"><strong>{{ formatMontant(obtenirValeurPostePrecedent('XI')) }}</strong></td>
        </tr>
      </tbody>
    </table>

    <div class="resume-indicateurs">
      <h3>RÉSUMÉ DES SOLDES INTERMÉDIAIRES</h3>
      <table class="table-resume">
        <tr>
          <td>Marge commerciale (XA)</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('XA')) }}</td>
        </tr>
        <tr>
          <td>Chiffre d'affaires (XB)</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('XB')) }}</td>
        </tr>
        <tr>
          <td>Valeur ajoutée (XC)</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('XC')) }}</td>
        </tr>
        <tr>
          <td>Excédent brut d'exploitation (XD)</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('XD')) }}</td>
        </tr>
        <tr>
          <td>Résultat d'exploitation (XE)</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('XE')) }}</td>
        </tr>
        <tr>
          <td>Résultat financier (XF)</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('XF')) }}</td>
        </tr>
        <tr>
          <td>Résultat des activités ordinaires (XG)</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('XG')) }}</td>
        </tr>
        <tr>
          <td>Résultat HAO (XH)</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('XH')) }}</td>
        </tr>
        <tr class="total-row">
          <td><strong>RÉSULTAT NET (XI)</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPoste('XI')) }}</strong></td>
        </tr>
      </table>
    </div>

    <div class="actions-export">
      <button (click)="exporterPDF()" class="btn btn-secondary">
        <i class="fa fa-file-pdf"></i> Exporter en PDF
      </button>
    </div>
  </div>
</div>
*/

// tableau-flux-tresorerie.component.html
/*
<div class="etats-financiers-container">
  <div class="header-etat">
    <h2 class="titre-etat">TABLEAU DES FLUX DE TRÉSORERIE</h2>
    <div class="info-entreprise">
      <p><strong>{{ nomEntreprise }}</strong></p>
      <p>{{ adresseEntreprise }}</p>
      <p>Exercice du {{ dateDebut | date:'dd/MM/yyyy' }} au {{ dateFin | date:'dd/MM/yyyy' }}</p>
    </div>
  </div>

  <form [formGroup]="tableauFluxForm" (ngSubmit)="genererTableauFlux()" class="form-etat">
    <div class="form-group">
      <label for="dateDebut">Date de début :</label>
      <input type="date" id="dateDebut" formControlName="dateDebut" class="form-control">
    </div>
    <div class="form-group">
      <label for="dateFin">Date de fin :</label>
      <input type="date" id="dateFin" formControlName="dateFin" class="form-control">
    </div>
    <button type="submit" class="btn btn-primary" [disabled]="loading">
      {{ loading ? 'Génération...' : 'Générer le Tableau des Flux' }}
    </button>
  </form>

  <div *ngIf="error" class="alert alert-danger">{{ error }}</div>

  <div *ngIf="tableauFluxData" class="tableau-flux-syscohada">
    <table class="table-flux">
      <thead>
        <tr class="header-row">
          <th class="ref-col">REF</th>
          <th class="libelle-col">LIBELLÉ</th>
          <th class="exercice-col">EXERCICE AU<br>{{ dateFin | date:'dd/MM/yyyy' }}</th>
          <th class="exercice-col">EXERCICE AU<br>{{ dateFinPrecedent | date:'dd/MM/yyyy' }}</th>
        </tr>
      </thead>
      <tbody>
        <!-- TRÉSORERIE DÉBUT -->
        <tr class="tresorerie-debut">
          <td class="ref">ZA</td>
          <td class="libelle"><strong>Trésorerie nette au début d'exercice</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPoste('ZA')) }}</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPostePrecedent('ZA')) }}</strong></td>
        </tr>

        <!-- FLUX OPÉRATIONNELS -->
        <tr class="section-header">
          <td colspan="4" class="section-title">FLUX DE TRÉSORERIE PROVENANT DES ACTIVITÉS OPÉRATIONNELLES</td>
        </tr>
        <tr>
          <td class="ref">FA</td>
          <td class="libelle">Capacité d'Autofinancement Globale (CAFG)</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('FA')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('FA')) }}</td>
        </tr>
        <tr>
          <td class="ref">FB</td>
          <td class="libelle">- Variations de l'actif circulant HAO</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('FB')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('FB')) }}</td>
        </tr>
        <tr>
          <td class="ref">FC</td>
          <td class="libelle">- Variation des stocks</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('FC')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('FC')) }}</td>
        </tr>
        <tr>
          <td class="ref">FD</td>
          <td class="libelle">- Variation des créances et emplois assimilés</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('FD')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('FD')) }}</td>
        </tr>
        <tr>
          <td class="ref">FE</td>
          <td class="libelle">+ Variation du passif circulant</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('FE')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('FE')) }}</td>
        </tr>
        <tr class="total-section">
          <td class="ref">ZB</td>
          <td class="libelle"><strong>FLUX DE TRÉSORERIE PROVENANT DES ACTIVITÉS OPÉRATIONNELLES</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPoste('ZB')) }}</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPostePrecedent('ZB')) }}</strong></td>
        </tr>

        <!-- FLUX D'INVESTISSEMENT -->
        <tr class="section-header">
          <td colspan="4" class="section-title">FLUX DE TRÉSORERIE PROVENANT DES OPÉRATIONS D'INVESTISSEMENT</td>
        </tr>
        <tr>
          <td class="ref">FF</td>
          <td class="libelle">- Décaissements liés aux acquisitions d'immobilisations incorporelles</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('FF')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('FF')) }}</td>
        </tr>
        <tr>
          <td class="ref">FG</td>
          <td class="libelle">- Décaissements liés aux acquisitions d'immobilisations corporelles</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('FG')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('FG')) }}</td>
        </tr>
        <tr>
          <td class="ref">FH</td>
          <td class="libelle">- Décaissements liés aux acquisitions d'immobilisations financières</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('FH')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('FH')) }}</td>
        </tr>
        <tr>
          <td class="ref">FI</td>
          <td class="libelle">+ Encaissements liés aux cessions d'immobilisations incorporelles et corporelles</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('FI')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('FI')) }}</td>
        </tr>
        <tr>
          <td class="ref">FJ</td>
          <td class="libelle">+ Encaissements liés aux cessions d'immobilisations financières</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('FJ')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('FJ')) }}</td>
        </tr>
        <tr class="total-section">
          <td class="ref">ZC</td>
          <td class="libelle"><strong>FLUX DE TRÉSORERIE PROVENANT DES OPÉRATIONS D'INVESTISSEMENT</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPoste('ZC')) }}</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPostePrecedent('ZC')) }}</strong></td>
        </tr>

        <!-- FLUX CAPITAUX PROPRES -->
        <tr class="section-header">
          <td colspan="4" class="section-title">FLUX DE TRÉSORERIE PROVENANT DES CAPITAUX PROPRES</td>
        </tr>
        <tr>
          <td class="ref">FK</td>
          <td class="libelle">+ Augmentations de capital par apports nouveaux</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('FK')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('FK')) }}</td>
        </tr>
        <tr>
          <td class="ref">FL</td>
          <td class="libelle">+ Subventions d'investissement reçues</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('FL')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('FL')) }}</td>
        </tr>
        <tr>
          <td class="ref">FM</td>
          <td class="libelle">- Prélèvements sur le capital</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('FM')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('FM')) }}</td>
        </tr>
        <tr>
          <td class="ref">FN</td>
          <td class="libelle">- Distribution de dividendes</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('FN')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('FN')) }}</td>
        </tr>
        <tr class="total-section">
          <td class="ref">ZD</td>
          <td class="libelle"><strong>FLUX DE TRÉSORERIE PROVENANT DES CAPITAUX PROPRES</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPoste('ZD')) }}</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPostePrecedent('ZD')) }}</strong></td>
        </tr>

        <!-- FLUX CAPITAUX ÉTRANGERS -->
        <tr class="section-header">
          <td colspan="4" class="section-title">FLUX DE TRÉSORERIE PROVENANT DES CAPITAUX ÉTRANGERS</td>
        </tr>
        <tr>
          <td class="ref">FO</td>
          <td class="libelle">+ Emprunts</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('FO')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('FO')) }}</td>
        </tr>
        <tr>
          <td class="ref">FP</td>
          <td class="libelle">+ Autres dettes financières</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('FP')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('FP')) }}</td>
        </tr>
        <tr>
          <td class="ref">FQ</td>
          <td class="libelle">- Remboursements des emprunts et autres dettes financières</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('FQ')) }}</td>
          <td class="montant">{{ formatMontant(obtenirValeurPostePrecedent('FQ')) }}</td>
        </tr>
        <tr class="total-section">
          <td class="ref">ZE</td>
          <td class="libelle"><strong>FLUX DE TRÉSORERIE PROVENANT DES CAPITAUX ÉTRANGERS</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPoste('ZE')) }}</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPostePrecedent('ZE')) }}</strong></td>
        </tr>

        <!-- TOTAL FINANCEMENT -->
        <tr class="total-section important">
          <td class="ref">ZF</td>
          <td class="libelle"><strong>FLUX DE TRÉSORERIE PROVENANT DES ACTIVITÉS DE FINANCEMENT (ZD + ZE)</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPoste('ZF')) }}</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPostePrecedent('ZF')) }}</strong></td>
        </tr>

        <!-- VARIATION TRÉSORERIE -->
        <tr class="variation-tresorerie">
          <td class="ref">ZG</td>
          <td class="libelle"><strong>VARIATION DE LA TRÉSORERIE NETTE DE LA PÉRIODE (ZB + ZC + ZF)</strong></td>
          <td class="montant variation"><strong>{{ formatMontant(obtenirValeurPoste('ZG')) }}</strong></td>
          <td class="montant variation"><strong>{{ formatMontant(obtenirValeurPostePrecedent('ZG')) }}</strong></td>
        </tr>

        <!-- TRÉSORERIE FIN -->
        <tr class="tresorerie-fin">
          <td class="ref">ZH</td>
          <td class="libelle"><strong>Trésorerie nette à la fin d'exercice (ZA + ZG)</strong></td>
          <td class="montant final"><strong>{{ formatMontant(obtenirValeurPoste('ZH')) }}</strong></td>
          <td class="montant final"><strong>{{ formatMontant(obtenirValeurPostePrecedent('ZH')) }}</strong></td>
        </tr>
      </tbody>
    </table>

    <div class="controle-coherence" [class.coherent]="verifierCoherence()" [class.incoherent]="!verifierCoherence()">
      <h3>CONTRÔLE DE COHÉRENCE</h3>
      <p><strong>Statut : {{ verifierCoherence() ? 'COHÉRENT' : 'INCOHÉRENT' }}</strong></p>
      <table class="table-controle">
        <tr>
          <td>Flux opérationnels (ZB)</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('ZB')) }}</td>
        </tr>
        <tr>
          <td>Flux d'investissement (ZC)</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('ZC')) }}</td>
        </tr>
        <tr>
          <td>Flux de financement (ZF)</td>
          <td class="montant">{{ formatMontant(obtenirValeurPoste('ZF')) }}</td>
        </tr>
        <tr class="total-row">
          <td><strong>Total calculé</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPoste('ZB') + obtenirValeurPoste('ZC') + obtenirValeurPoste('ZF')) }}</strong></td>
        </tr>
        <tr>
          <td><strong>Variation affichée (ZG)</strong></td>
          <td class="montant"><strong>{{ formatMontant(obtenirValeurPoste('ZG')) }}</strong></td>
        </tr>
        <tr>
          <td><strong>Écart</strong></td>
          <td class="montant"><strong>{{ formatMontant(Math.abs((obtenirValeurPoste('ZB') + obtenirValeurPoste('ZC') + obtenirValeurPoste('ZF')) - obtenirValeurPoste('ZG'))) }}</strong></td>
        </tr>
      </table>
    </div>

    <div class="actions-export">
      <button (click)="exporterPDF()" class="btn btn-secondary">
        <i class="fa fa-file-pdf"></i> Exporter en PDF
      </button>
    </div>
  </div>
</div>
*/

// CSS pour le design conforme SYSCOHADA
// etats-financiers.component.css
/*
.etats-financiers-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Times New Roman', serif;
  color: #000;
  background: #fff;
}

.header-etat {
  text-align: center;
  margin-bottom: 30px;
  border-bottom: 2px solid #000;
  padding-bottom: 15px;
}

.titre-etat {
  font-size: 18px;
  font-weight: bold;
  margin: 0 0 10px 0;
  text-transform: uppercase;
}

.info-entreprise {
  font-size: 12px;
  line-height: 1.4;
}

.info-entreprise p {
  margin: 2px 0;
}

.form-etat {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 5px;
  margin-bottom: 20px;
  display: flex;
  gap: 15px;
  align-items: end;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  font-weight: bold;
  margin-bottom: 5px;
  font-size: 12px;
}

.form-control {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 3px;
  font-size: 12px;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 3px;
  cursor: pointer;
  font-size: 12px;
  font-weight: bold;
}

.btn-primary {
  background: #007bff;
  color: white;
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.alert {
  padding: 15px;
  border-radius: 5px;
  margin-bottom: 20px;
}

.alert-danger {
  background: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

/* Tables SYSCOHADA */
.table-bilan,
.table-compte-resultat,
.table-flux {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
  font-size: 11px;
  border: 2px solid #000;
}

.table-bilan th,
.table-bilan td,
.table-compte-resultat th,
.table-compte-resultat td,
.table-flux th,
.table-flux td {
  border: 1px solid #000;
  padding: 4px 6px;
  text-align: left;
  vertical-align: middle;
}

.header-row th {
  background: #000;
  color: #fff;
  font-weight: bold;
  text-align: center;
  padding: 8px;
}

.actif-header,
.passif-header {
  font-size: 14px;
  font-weight: bold;
}

.sous-header th {
  background: #e9ecef;
  font-weight: bold;
  text-align: center;
  font-size: 10px;
}

.ref-col {
  width: 50px;
  text-align: center;
}

.libelle-col {
  width: auto;
  min-width: 300px;
}

.montant-col,
.exercice-col {
  width: 120px;
  text-align: center;
}

.section-header td {
  background: #f8f9fa;
  font-weight: bold;
  text-align: center;
  font-size: 12px;
  padding: 8px;
}

.section-title {
  font-weight: bold;
  text-transform: uppercase;
}

.ref {
  text-align: center;
  font-weight: bold;
  font-size: 10px;
}

.libelle {
  font-size: 11px;
}

.indent-niveau-2 {
  padding-left: 20px;
}

.montant {
  text-align: right;
  font-family: 'Courier New', monospace;
  font-size: 10px;
  white-space: nowrap;
}

.montant.charge::before {
  content: '-';
}

.niveau-1 {
  font-weight: bold;
}

.niveau-2 {
  font-style: italic;
}

.total-row {
  background: #f0f0f0;
  font-weight: bold;
}

.total-row .libelle,
.total-row .montant {
  font-weight: bold;
}

.grand-total-row {
  background: #000;
  color: #fff;
  font-weight: bold;
}

.grand-total-row .libelle,
.grand-total-row .montant {
  font-weight: bold;
}

.solde-row {
  background: #e3f2fd;
  font-weight: bold;
}

.solde-row.important {
  background: #bbdefb;
}

.resultat-net-row {
  background: #000;
  color: #fff;
  font-weight: bold;
}

.resultat-net {
  font-weight: bold;
  font-size: 12px;
}

.total-section {
  background: #f5f5f5;
  font-weight: bold;
}

.total-section.important {
  background: #e8f5e8;
}

.tresorerie-debut,
.tresorerie-fin {
  background: #fff3cd;
  font-weight: bold;
}

.variation-tresorerie {
  background: #d4edda;
  font-weight: bold;
}

.variation {
  font-weight: bold;
  color: #155724;
}

.final {
  font-weight: bold;
  color: #721c24;
}

/* Contrôles et résumés */
.controle-equilibre,
.controle-coherence {
  margin: 20px 0;
  padding: 15px;
  border-radius: 5px;
  font-size: 12px;
}

.equilibre {
  background: #d4edda;
  border: 1px solid #c3e6cb;
  color: #155724;
}

.desequilibre,
.incoherent {
  background: #f8d7da;
  border: 1px solid #f5c6cb;
  color: #721c24;
}

.coherent {
  background: #d4edda;
  border: 1px solid #c3e6cb;
  color: #155724;
}

.resume-indicateurs {
  margin: 30px 0;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 5px;
}

.resume-indicateurs h3 {
  margin: 0 0 15px 0;
  font-size: 14px;
  text-align: center;
  text-transform: uppercase;
}

.table-resume,
.table-controle {
  width: 100%;
  border-collapse: collapse;
  font-size: 11px;
}

.table-resume td,
.table-controle td {
  padding: 5px 10px;
  border-bottom: 1px solid #ddd;
}

.table-resume .montant,
.table-controle .montant {
  text-align: right;
  font-family: 'Courier New', monospace;
  font-weight: bold;
}

.actions-export {
  text-align: center;
  margin-top: 30px;
  padding: 20px;
  border-top: 2px solid #000;
}

/* Responsive */
@media print {
  .etats-financiers-container {
    max-width: none;
    margin: 0;
    padding: 0;
  }
  
  .form-etat,
  .actions-export {
    display: none;
  }
  
  .table-bilan,
  .table-compte-resultat,
  .table-flux {
    page-break-inside: avoid;
  }
  
  .header-etat {
    page-break-after: avoid;
  }
}

@media (max-width: 768px) {
  .form-etat {
    flex-direction: column;
  }
  
  .table-bilan,
  .table-compte-resultat,
  .table-flux {
    font-size: 9px;
  }
  
  .montant-col,
  .exercice-col {
    width: 80px;
  }
}

/* Styles spécifiques pour les annexes */
.notes-annexes-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Times New Roman', serif;
}

.notes-selection {
  margin-bottom: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 5px;
}

.notes-selection h3 {
  margin: 0 0 15px 0;
  font-size: 14px;
}

.notes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 10px;
}

.note-button {
  padding: 10px;
  border: 1px solid #ccc;
  background: #fff;
  cursor: pointer;
  border-radius: 3px;
  font-size: 11px;
  text-align: left;
  transition: all 0.2s;
}

.note-button:hover {
  background: #e9ecef;
}

.note-button.active {
  background: #007bff;
  color: #fff;
  border-color: #007bff;
}

.note-button.obligatoire {
  border-left: 4px solid #dc3545;
}

.note-contenu {
  margin-top: 20px;
  padding: 20px;
  border: 1px solid #ddd;
  background: #fff;
}

.note-titre {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 15px;
  text-transform: uppercase;
  border-bottom: 1px solid #000;
  padding-bottom: 5px;
}

.tableau-annexe {
  width: 100%;
  border-collapse: collapse;
  margin: 15px 0;
  font-size: 11px;
  border: 1px solid #000;
}

.tableau-annexe th,
.tableau-annexe td {
  border: 1px solid #000;
  padding: 5px 8px;
  text-align: left;
}

.tableau-annexe th {
  background: #f0f0f0;
  font-weight: bold;
  text-align: center;
}

.tableau-annexe .montant-annexe {
  text-align: right;
  font-family: 'Courier New', monospace;
}

.tableau-annexe .total-annexe {
  background: #f8f9fa;
  font-weight: bold;
}

/* Animation de chargement */
.loading-spinner {
  display: inline-block;
  width: 20px;
  height: 20px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #007bff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text {
  display: flex;
  align-items: center;
  gap: 10px;
  justify-content: center;
  padding: 20px;
  font-style: italic;
  color: #6c757d;
}

/* Indicateurs visuels pour les montants */
.montant.negatif {
  color: #dc3545;
  font-weight: bold;
}

.montant.positif {
  color: #28a745;
  font-weight: bold;
}

.montant.zero {
  color: #6c757d;
  font-style: italic;
}

/* États financiers consolidés */
.etats-consolides-container {
  display: grid;
  grid-template-columns: 250px 1fr;
  gap: 20px;
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.navigation-etats {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 5px;
  height: fit-content;
  position: sticky;
  top: 20px;
}

.navigation-etats h3 {
  margin: 0 0 15px 0;
  font-size: 14px;
  text-transform: uppercase;
}

.nav-etat {
  display: block;
  width: 100%;
  padding: 10px 15px;
  margin-bottom: 5px;
  border: 1px solid #ddd;
  background: #fff;
  color: #333;
  text-decoration: none;
  border-radius: 3px;
  font-size: 12px;
  transition: all 0.2s;
}

.nav-etat:hover {
  background: #e9ecef;
  text-decoration: none;
  color: #333;
}

.nav-etat.active {
  background: #007bff;
  color: #fff;
  border-color: #007bff;
}

.contenu-etats {
  background: #fff;
  border-radius: 5px;
  overflow: hidden;
}

.onglet-etat {
  display: none;
}

.onglet-etat.active {
  display: block;
}

/* Validation et contrôles */
.controles-validation {
  margin: 20px 0;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 5px;
}

.controles-validation h4 {
  margin: 0 0 15px 0;
  font-size: 13px;
  text-transform: uppercase;
}

.controle-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
  font-size: 11px;
}

.controle-item:last-child {
  border-bottom: none;
}

.controle-statut {
  padding: 2px 8px;
  border-radius: 3px;
  font-size: 10px;
  font-weight: bold;
}

.controle-statut.ok {
  background: #d4edda;
  color: #155724;
}

.controle-statut.erreur {
  background: #f8d7da;
  color: #721c24;
}

.controle-statut.attention {
  background: #fff3cd;
  color: #856404;
}

/* Tooltips et aide */
.tooltip-info {
  position: relative;
  display: inline-block;
  cursor: help;
}

.tooltip-info::after {
  content: '?';
  display: inline-block;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #007bff;
  color: white;
  text-align: center;
  font-size: 10px;
  line-height: 16px;
  margin-left: 5px;
}

.tooltip-info:hover::before {
  content: attr(data-tooltip);
  position: absolute;
  bottom: 100%;
  left: 50%;
  transform: translateX(-50%);
  background: #333;
  color: white;
  padding: 8px 12px;
  border-radius: 4px;
  white-space: nowrap;
  font-size: 11px;
  z-index: 1000;
  margin-bottom: 5px;
}

/* Styles pour l'impression */
@page {
  size: A4;
  margin: 1cm;
}

@media print {
  body {
    font-size: 10px;
    line-height: 1.2;
  }
  
  .no-print {
    display: none !important;
  }
  
  .page-break {
    page-break-before: always;
  }
  
  .keep-together {
    page-break-inside: avoid;
  }
  
  .table-bilan,
  .table-compte-resultat,
  .table-flux,
  .tableau-annexe {
    border: 2px solid #000 !important;
  }
  
  .table-bilan th,
  .table-bilan td,
  .table-compte-resultat th,
  .table-compte-resultat td,
  .table-flux th,
  .table-flux td,
  .tableau-annexe th,
  .tableau-annexe td {
    border: 1px solid #000 !important;
  }
}
*/

// Méthodes utilitaires dans les composants TypeScript

// Ajout des méthodes utilitaires dans BilanSyscohadaComponent
formatMontant(montant: number): string {
  if (montant === 0) return '-';
  return Math.abs(montant).toLocaleString('fr-FR', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  });
}

getPostesActifImmobilise(): PosteBilan[] {
  return this.bilanStructure.actif.immobilise;
}

getPostesActifCirculant(): PosteBilan[] {
  return this.bilanStructure.actif.circulant;
}

getPostesTresorerieActif(): PosteBilan[] {
  return this.bilanStructure.actif.tresorerie;
}

getPostesEcartConversionActif(): PosteBilan[] {
  return this.bilanStructure.actif.conversion;
}

getPostesCapitauxPropres(): PosteBilan[] {
  return this.bilanStructure.passif.capitaux;
}

getPostesDettesFinancieres(): PosteBilan[] {
  return this.bilanStructure.passif.dettesFinancieres;
}

getPostesPassifCirculant(): PosteBilan[] {
  return this.bilanStructure.passif.passifCirculant;
}

getPostesTresoreriePassif(): PosteBilan[] {
  return this.bilanStructure.passif.tresorerie;
}

getPostesEcartConversionPassif(): PosteBilan[] {
  return this.bilanStructure.passif.conversion;
}

calculerTotalActifPrecedent(): number {
  const totalImmobilise = this.bilanStructure.actif.immobilise
    .find(item => item.code === 'AZ')?.valeurExercicePrecedent || 0;
  const totalCirculant = this.bilanStructure.actif.circulant
    .find(item => item.code === 'BK')?.valeurExercicePrecedent || 0;
  const totalTresorerie = this.bilanStructure.actif.tresorerie
    .find(item => item.code === 'BT')?.valeurExercicePrecedent || 0;
  const ecartConversion = this.bilanStructure.actif.conversion[0].valeurExercicePrecedent || 0;

  return totalImmobilise + totalCirculant + totalTresorerie + ecartConversion;
}

calculerTotalPassifPrecedent(): number {
  const totalCapitaux = this.bilanStructure.passif.capitaux
    .find(item => item.code === 'CP')?.valeurExercicePrecedent || 0;
  const totalDettes = this.bilanStructure.passif.dettesFinancieres
    .find(item => item.code === 'DD')?.valeurExercicePrecedent || 0;
  const totalCirculant = this.bilanStructure.passif.passifCirculant
    .find(item => item.code === 'DP')?.valeurExercicePrecedent || 0;
  const totalTresorerie = this.bilanStructure.passif.tresorerie
    .find(item => item.code === 'DT')?.valeurExercicePrecedent || 0;
  const ecartConversion = this.bilanStructure.passif.conversion[0].valeurExercicePrecedent || 0;

  return totalCapitaux + totalDettes + totalCirculant + totalTresorerie + ecartConversion;
}

calculerTotalRessourcesStables(): number {
  const totalCapitaux = this.bilanStructure.passif.capitaux
    .find(item => item.code === 'CP')?.valeurExerciceCourant || 0;
  const totalDettes = this.bilanStructure.passif.dettesFinancieres
    .find(item => item.code === 'DD')?.valeurExerciceCourant || 0;
  return totalCapitaux + totalDettes;
}

calculerTotalRessourcesStablesPrecedent(): number {
  const totalCapitaux = this.bilanStructure.passif.capitaux
    .find(item => item.code === 'CP')?.valeurExercicePrecedent || 0;
  const totalDettes = this.bilanStructure.passif.dettesFinancieres
    .find(item => item.code === 'DD')?.valeurExercicePrecedent || 0;
  return totalCapitaux + totalDettes;
}

// Méthodes pour CompteResultatSyscohadaComponent
obtenirValeurPoste(code: string): number {
  const sections = [
    this.compteResultatStructure.produits,
    this.compteResultatStructure.charges,
    this.compteResultatStructure.soldesIntermediaires
  ];
  
  for (const section of sections) {
    const poste = section.find(p => p.code === code);
    if (poste) return poste.valeur;
  }
  return 0;
}

obtenirValeurPostePrecedent(code: string): number {
  // Implémentation pour obtenir les valeurs de l'exercice précédent
  // À adapter selon la structure des données
  return 0;
}

// Service pour la génération des PDFs conformes SYSCOHADA
@Injectable({
  providedIn: 'root'
})
export class PDFGeneratorService {
  
  constructor() {}

  genererBilanPDF(bilanData: BilanSYSCOHADA, infoEntreprise: any): void {
    // Implémentation avec jsPDF ou autre bibliothèque
    // Respect du format officiel SYSCOHADA
    console.log('Génération PDF du bilan avec format SYSCOHADA');
  }

  genererCompteResultatPDF(crData: CompteResultatSYSCOHADA, infoEntreprise: any): void {
    // Implémentation pour le compte de résultat
    console.log('Génération PDF du compte de résultat');
  }

  genererTableauFluxPDF(fluxData: TableauFluxSYSCOHADA, infoEntreprise: any): void {
    // Implémentation pour le tableau des flux
    console.log('Génération PDF du tableau des flux');
  }

  genererAnnexesPDF(annexesData: AnnexesSYSCOHADA, infoEntreprise: any): void {
    // Implémentation pour les annexes
    console.log('Génération PDF des annexes');
  }

  genererLiasseComplete(etatsData: EtatFinancierAuto, infoEntreprise: any): void {
    // Génération d'un PDF complet avec tous les états
    console.log('Génération de la liasse complète');
  }
}

// Module Angular pour intégrer tous les composants
@NgModule({
  declarations: [
    BilanSyscohadaComponent,
    CompteResultatSyscohadaComponent,
    TableauFluxTresorerieComponent,
    NotesAnnexesComponent,
    EtatsFinanciersAutoComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    HttpClientModule
  ],
  providers: [
    EtatsFinanciersService,
    EtatsFinanciersAutoService,
    BalanceComptableService,
    PDFGeneratorService
  ]
})
export class EtatsFinanciersSyscohadaModule { }