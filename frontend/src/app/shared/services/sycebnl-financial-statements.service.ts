import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, catchError, of } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface EtatFinancierSycebnl {
  id?: number;
  exerciceId: number;
  entiteId: number;
  typeSysteme: 'NORMAL' | 'MINIMAL';
  typeEtat: 'BILAN' | 'COMPTE_RESULTAT' | 'TABLEAU_FLUX' | 'RECETTES_DEPENSES' | 'SITUATION_TRESORERIE' | 'ANNEXES';
  dateArrete: string;
  statut: 'BROUILLON' | 'VALIDE' | 'CLOTURE';
  donneesJson: string;
  totalActif?: number;
  totalPassif?: number;
  totalProduits?: number;
  totalCharges?: number;
  resultatNet?: number;
  equilibre: boolean;
  generePar?: string;
  validePar?: string;
  dateValidation?: string;
  notesAnnexes?: NoteAnnexeSycebnl[];
  dateCreation?: string;
  dateModification?: string;
}

export interface NoteAnnexeSycebnl {
  id?: number;
  exerciceId: number;
  entiteId: number;
  typeSysteme: 'NORMAL' | 'MINIMAL';
  typeNote: string;
  numeroNote: string;
  titreNote: string;
  contenuNote: string;
  ordreAffichage: number;
  dateCreation?: string;
  dateModification?: string;
}

export interface MappingComptesPostes {
  id?: number;
  paysCode: string;
  standardComptable: string;
  typeSysteme: 'NORMAL' | 'MINIMAL';
  typeEtat: 'BILAN' | 'COMPTE_RESULTAT' | 'TABLEAU_FLUX' | 'RECETTES_DEPENSES' | 'SITUATION_TRESORERIE' | 'ANNEXES';
  posteCode: string;
  posteLibelle: string;
  comptesPattern: string[];
  signeNormal: 'DEBIT' | 'CREDIT';
  ordreAffichage: number;
  niveau: number;
  estTotal: boolean;
  actif: boolean;
  dateCreation?: string;
  dateModification?: string;
}

export interface CreateEtatFinancierRequest {
  exerciceId: number;
  entiteId: number;
  typeSysteme: string;
  typeEtat: string;
  dateArrete: string;
}

export interface ValidationResult {
  valide: boolean;
  erreurs: string[];
  avertissements: string[];
  nombreErreurs: number;
  nombreAvertissements: number;
  dateValidation: string;
}

export interface ExportRequest {
  format: 'PDF' | 'EXCEL' | 'CSV';
  includeNotes: boolean;
  includeDetails: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class SycebnlFinancialStatementsService {
  private apiUrl = `${environment.apiUrl}/sycebnl/financial-statements`;

  constructor(private http: HttpClient) {}

  /**
   * Obtenir tous les états financiers
   */
  getAllFinancialStatements(): Observable<EtatFinancierSycebnl[]> {
    return this.http.get<{data: EtatFinancierSycebnl[], status: string}>(this.apiUrl)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des états financiers');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des états financiers:', error);
          return of([]);
        })
      );
  }

  /**
   * Obtenir un état financier par ID
   */
  getFinancialStatementById(id: number): Observable<EtatFinancierSycebnl> {
    return this.http.get<{data: EtatFinancierSycebnl, status: string}>(`${this.apiUrl}/${id}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération de l\'état financier');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération de l\'état financier:', error);
          throw error;
        })
      );
  }

  /**
   * Obtenir les états financiers par exercice
   */
  getFinancialStatementsByExercise(exerciceId: number): Observable<EtatFinancierSycebnl[]> {
    return this.http.get<{data: EtatFinancierSycebnl[], status: string}>(`${this.apiUrl}/exercise/${exerciceId}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des états financiers de l\'exercice');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des états financiers de l\'exercice:', error);
          return of([]);
        })
      );
  }

  /**
   * Obtenir les états financiers par entité
   */
  getFinancialStatementsByEntity(entiteId: number): Observable<EtatFinancierSycebnl[]> {
    return this.http.get<{data: EtatFinancierSycebnl[], status: string}>(`${this.apiUrl}/entity/${entiteId}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des états financiers de l\'entité');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des états financiers de l\'entité:', error);
          return of([]);
        })
      );
  }

  /**
   * Générer un état financier
   */
  generateFinancialStatement(request: CreateEtatFinancierRequest): Observable<EtatFinancierSycebnl> {
    return this.http.post<{data: EtatFinancierSycebnl, status: string}>(`${this.apiUrl}/generate`, request)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la génération de l\'état financier');
        }),
        catchError(error => {
          console.error('Erreur lors de la génération de l\'état financier:', error);
          throw error;
        })
      );
  }

  /**
   * Générer tous les états financiers d'un exercice
   */
  generateAllFinancialStatements(exerciceId: number, entiteId: number, typeSysteme: string): Observable<EtatFinancierSycebnl[]> {
    return this.http.post<{data: EtatFinancierSycebnl[], status: string}>(`${this.apiUrl}/generate-all`, {
      exerciceId,
      entiteId,
      typeSysteme
    })
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la génération de tous les états financiers');
        }),
        catchError(error => {
          console.error('Erreur lors de la génération de tous les états financiers:', error);
          throw error;
        })
      );
  }

  /**
   * Valider un état financier
   */
  validateFinancialStatement(id: number): Observable<ValidationResult> {
    return this.http.post<{data: ValidationResult, status: string}>(`${this.apiUrl}/${id}/validate`, {})
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la validation de l\'état financier');
        }),
        catchError(error => {
          console.error('Erreur lors de la validation de l\'état financier:', error);
          throw error;
        })
      );
  }

  /**
   * Valider tous les états financiers d'un exercice
   */
  validateAllFinancialStatements(exerciceId: number): Observable<ValidationResult> {
    return this.http.post<{data: ValidationResult, status: string}>(`${this.apiUrl}/validate-all`, { exerciceId })
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la validation de tous les états financiers');
        }),
        catchError(error => {
          console.error('Erreur lors de la validation de tous les états financiers:', error);
          throw error;
        })
      );
  }

  /**
   * Exporter un état financier
   */
  exportFinancialStatement(id: number, format: string): Observable<Blob> {
    return this.http.post(`${this.apiUrl}/${id}/export`, { format }, { responseType: 'blob' })
      .pipe(
        catchError(error => {
          console.error('Erreur lors de l\'export de l\'état financier:', error);
          throw error;
        })
      );
  }

  /**
   * Exporter tous les états financiers d'un exercice
   */
  exportAllFinancialStatements(exerciceId: number, format: string): Observable<Blob> {
    return this.http.post(`${this.apiUrl}/export-all`, { exerciceId, format }, { responseType: 'blob' })
      .pipe(
        catchError(error => {
          console.error('Erreur lors de l\'export de tous les états financiers:', error);
          throw error;
        })
      );
  }

  /**
   * Obtenir les notes annexes d'un état financier
   */
  getFinancialStatementNotes(id: number): Observable<NoteAnnexeSycebnl[]> {
    return this.http.get<{data: NoteAnnexeSycebnl[], status: string}>(`${this.apiUrl}/${id}/notes`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des notes annexes');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des notes annexes:', error);
          return of([]);
        })
      );
  }

  /**
   * Obtenir les mappings comptes-postes
   */
  getMappingComptesPostes(typeSysteme: string, typeEtat: string): Observable<MappingComptesPostes[]> {
    return this.http.get<{data: MappingComptesPostes[], status: string}>(`${this.apiUrl}/mapping`, {
      params: { typeSysteme, typeEtat }
    })
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des mappings');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des mappings:', error);
          return of([]);
        })
      );
  }

  /**
   * Supprimer un état financier
   */
  deleteFinancialStatement(id: number): Observable<boolean> {
    return this.http.delete<{message: string, status: string}>(`${this.apiUrl}/${id}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return true;
          }
          throw new Error('Erreur lors de la suppression de l\'état financier');
        }),
        catchError(error => {
          console.error('Erreur lors de la suppression de l\'état financier:', error);
          return of(false);
        })
      );
  }

  /**
   * Obtenir les options pour les types d'états financiers
   */
  getFinancialStatementTypes(): Array<{value: string, label: string, description: string}> {
    return [
      { 
        value: 'BILAN', 
        label: 'Bilan', 
        description: 'État de la situation financière à une date donnée' 
      },
      { 
        value: 'COMPTE_RESULTAT', 
        label: 'Compte de Résultat', 
        description: 'Résultat des activités de l\'exercice' 
      },
      { 
        value: 'TABLEAU_FLUX', 
        label: 'Tableau des Flux de Trésorerie', 
        description: 'Mouvements de trésorerie de l\'exercice' 
      },
      { 
        value: 'RECETTES_DEPENSES', 
        label: 'État des Recettes et Dépenses (SMT)', 
        description: 'Recettes et dépenses pour le système minimal' 
      },
      { 
        value: 'SITUATION_TRESORERIE', 
        label: 'Situation de Trésorerie (SMT)', 
        description: 'Situation de trésorerie pour le système minimal' 
      },
      { 
        value: 'ANNEXES', 
        label: 'Notes Annexes', 
        description: 'Informations complémentaires et explicatives' 
      }
    ];
  }

  /**
   * Obtenir les options pour les types de systèmes
   */
  getSystemTypes(): Array<{value: string, label: string, description: string}> {
    return [
      { 
        value: 'NORMAL', 
        label: 'Système Normal (SN)', 
        description: 'Système comptable complet pour les grandes organisations' 
      },
      { 
        value: 'MINIMAL', 
        label: 'Système Minimal de Trésorerie (SMT)', 
        description: 'Système comptable simplifié pour les petites organisations' 
      }
    ];
  }

  /**
   * Obtenir les options pour les statuts
   */
  getStatuses(): Array<{value: string, label: string, color: string}> {
    return [
      { value: 'BROUILLON', label: 'Brouillon', color: 'gray' },
      { value: 'VALIDE', label: 'Validé', color: 'green' },
      { value: 'CLOTURE', label: 'Clôturé', color: 'blue' }
    ];
  }

  /**
   * Obtenir l'icône du type d'état financier
   */
  getFinancialStatementTypeIcon(type: string): string {
    switch (type) {
      case 'BILAN': return 'account_balance';
      case 'COMPTE_RESULTAT': return 'trending_up';
      case 'TABLEAU_FLUX': return 'swap_horiz';
      case 'RECETTES_DEPENSES': return 'receipt';
      case 'SITUATION_TRESORERIE': return 'account_balance_wallet';
      case 'ANNEXES': return 'description';
      default: return 'description';
    }
  }

  /**
   * Obtenir la couleur du statut
   */
  getStatusColor(status: string): string {
    switch (status) {
      case 'BROUILLON': return 'gray';
      case 'VALIDE': return 'green';
      case 'CLOTURE': return 'blue';
      default: return 'gray';
    }
  }

  /**
   * Obtenir l'icône du statut
   */
  getStatusIcon(status: string): string {
    switch (status) {
      case 'BROUILLON': return 'edit';
      case 'VALIDE': return 'check_circle';
      case 'CLOTURE': return 'lock';
      default: return 'help';
    }
  }

  /**
   * Formater un montant
   */
  formatAmount(amount: number, currency: string = 'XOF'): string {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: currency
    }).format(amount);
  }

  /**
   * Formater une date
   */
  formatDate(date: string): string {
    return new Intl.DateTimeFormat('fr-FR').format(new Date(date));
  }

  /**
   * Télécharger un fichier
   */
  downloadFile(blob: Blob, filename: string): void {
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    link.click();
    window.URL.revokeObjectURL(url);
  }
}






