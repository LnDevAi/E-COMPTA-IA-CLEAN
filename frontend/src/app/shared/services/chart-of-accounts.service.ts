import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, catchError, of } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ChartOfAccounts {
  id?: number;
  accountCode: string;
  accountName: string;
  accountType: 'ASSET' | 'LIABILITY' | 'EQUITY' | 'REVENUE' | 'EXPENSE';
  accountClass: string;
  parentAccountCode?: string;
  accountLevel: number;
  isActive: boolean;
  description?: string;
  normalBalance: 'DEBIT' | 'CREDIT';
  currentDebitBalance: number;
  currentCreditBalance: number;
  openingDebitBalance: number;
  openingCreditBalance: number;
  companyId: number;
  countryCode: string;
  accountingStandard: string;
  createdAt?: Date;
  updatedAt?: Date;
  createdBy?: number;
  updatedBy?: number;
}

export interface CreateChartOfAccountsRequest {
  accountCode: string;
  accountName: string;
  accountType: string;
  accountClass: string;
  parentAccountCode?: string;
  accountLevel: number;
  isActive: boolean;
  description?: string;
  normalBalance: string;
  currentDebitBalance: number;
  currentCreditBalance: number;
  openingDebitBalance: number;
  openingCreditBalance: number;
  companyId: number;
  countryCode: string;
  accountingStandard: string;
}

export interface UpdateChartOfAccountsRequest extends Partial<CreateChartOfAccountsRequest> {
  id: number;
}

@Injectable({
  providedIn: 'root'
})
export class ChartOfAccountsService {
  private apiUrl = `${environment.apiUrl}/chart-of-accounts`;

  constructor(private http: HttpClient) {}

  /**
   * Obtenir tous les comptes du plan comptable
   */
  getAllChartOfAccounts(): Observable<ChartOfAccounts[]> {
    return this.http.get<{data: ChartOfAccounts[], status: string}>(this.apiUrl)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération du plan comptable');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération du plan comptable:', error);
          return of([]);
        })
      );
  }

  /**
   * Obtenir un compte par ID
   */
  getChartOfAccountsById(id: number): Observable<ChartOfAccounts> {
    return this.http.get<{data: ChartOfAccounts, status: string}>(`${this.apiUrl}/${id}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération du compte');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération du compte:', error);
          throw error;
        })
      );
  }

  /**
   * Créer un nouveau compte
   */
  createChartOfAccounts(account: CreateChartOfAccountsRequest): Observable<ChartOfAccounts> {
    return this.http.post<{data: ChartOfAccounts, status: string}>(this.apiUrl, account)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la création du compte');
        }),
        catchError(error => {
          console.error('Erreur lors de la création du compte:', error);
          throw error;
        })
      );
  }

  /**
   * Mettre à jour un compte
   */
  updateChartOfAccounts(account: UpdateChartOfAccountsRequest): Observable<ChartOfAccounts> {
    return this.http.put<{data: ChartOfAccounts, status: string}>(`${this.apiUrl}/${account.id}`, account)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la mise à jour du compte');
        }),
        catchError(error => {
          console.error('Erreur lors de la mise à jour du compte:', error);
          throw error;
        })
      );
  }

  /**
   * Supprimer un compte
   */
  deleteChartOfAccounts(id: number): Observable<boolean> {
    return this.http.delete<{message: string, status: string}>(`${this.apiUrl}/${id}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return true;
          }
          throw new Error('Erreur lors de la suppression du compte');
        }),
        catchError(error => {
          console.error('Erreur lors de la suppression du compte:', error);
          return of(false);
        })
      );
  }

  /**
   * Obtenir les comptes par type
   */
  getChartOfAccountsByType(type: string): Observable<ChartOfAccounts[]> {
    return this.http.get<{data: ChartOfAccounts[], status: string}>(`${this.apiUrl}/type/${type}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des comptes par type');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des comptes par type:', error);
          return of([]);
        })
      );
  }

  /**
   * Obtenir les comptes par classe
   */
  getChartOfAccountsByClass(accountClass: string): Observable<ChartOfAccounts[]> {
    return this.http.get<{data: ChartOfAccounts[], status: string}>(`${this.apiUrl}/class/${accountClass}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des comptes par classe');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des comptes par classe:', error);
          return of([]);
        })
      );
  }

  /**
   * Obtenir les comptes parents (niveau 1)
   */
  getParentAccounts(): Observable<ChartOfAccounts[]> {
    return this.http.get<{data: ChartOfAccounts[], status: string}>(`${this.apiUrl}/parents`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des comptes parents');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des comptes parents:', error);
          return of([]);
        })
      );
  }

  /**
   * Obtenir les comptes enfants d'un compte parent
   */
  getChildAccounts(parentAccountCode: string): Observable<ChartOfAccounts[]> {
    return this.http.get<{data: ChartOfAccounts[], status: string}>(`${this.apiUrl}/children/${parentAccountCode}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des comptes enfants');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des comptes enfants:', error);
          return of([]);
        })
      );
  }

  /**
   * Obtenir les comptes actifs
   */
  getActiveChartOfAccounts(): Observable<ChartOfAccounts[]> {
    return this.http.get<{data: ChartOfAccounts[], status: string}>(`${this.apiUrl}/active`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des comptes actifs');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des comptes actifs:', error);
          return of([]);
        })
      );
  }

  /**
   * Rechercher des comptes
   */
  searchChartOfAccounts(query: string): Observable<ChartOfAccounts[]> {
    return this.http.get<{data: ChartOfAccounts[], status: string}>(`${this.apiUrl}/search`, { params: { q: query } })
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la recherche de comptes');
        }),
        catchError(error => {
          console.error('Erreur lors de la recherche de comptes:', error);
          return of([]);
        })
      );
  }

  /**
   * Obtenir les options pour les types de comptes
   */
  getAccountTypes(): Array<{value: string, label: string}> {
    return [
      { value: 'ASSET', label: 'Actif' },
      { value: 'LIABILITY', label: 'Passif' },
      { value: 'EQUITY', label: 'Capitaux propres' },
      { value: 'REVENUE', label: 'Produit' },
      { value: 'EXPENSE', label: 'Charge' }
    ];
  }

  /**
   * Obtenir les options pour les classes de comptes
   */
  getAccountClasses(): Array<{value: string, label: string}> {
    return [
      { value: 'CURRENT_ASSETS', label: 'Actifs circulants' },
      { value: 'FIXED_ASSETS', label: 'Actifs immobilisés' },
      { value: 'CURRENT_LIABILITIES', label: 'Passifs circulants' },
      { value: 'LONG_TERM_LIABILITIES', label: 'Passifs à long terme' },
      { value: 'SHAREHOLDERS_EQUITY', label: 'Capitaux propres' },
      { value: 'OPERATING_REVENUE', label: 'Produits d\'exploitation' },
      { value: 'NON_OPERATING_REVENUE', label: 'Produits non courants' },
      { value: 'OPERATING_EXPENSES', label: 'Charges d\'exploitation' },
      { value: 'NON_OPERATING_EXPENSES', label: 'Charges non courantes' }
    ];
  }

  /**
   * Obtenir les options pour les soldes normaux
   */
  getNormalBalances(): Array<{value: string, label: string}> {
    return [
      { value: 'DEBIT', label: 'Débit' },
      { value: 'CREDIT', label: 'Crédit' }
    ];
  }

  /**
   * Obtenir l'icône du type de compte
   */
  getAccountTypeIcon(type: string): string {
    switch (type) {
      case 'ASSET': return 'account_balance';
      case 'LIABILITY': return 'account_balance_wallet';
      case 'EQUITY': return 'trending_up';
      case 'REVENUE': return 'trending_up';
      case 'EXPENSE': return 'trending_down';
      default: return 'account_balance';
    }
  }

  /**
   * Obtenir la couleur du type de compte
   */
  getAccountTypeColor(type: string): string {
    switch (type) {
      case 'ASSET': return 'blue';
      case 'LIABILITY': return 'red';
      case 'EQUITY': return 'green';
      case 'REVENUE': return 'green';
      case 'EXPENSE': return 'red';
      default: return 'gray';
    }
  }

  /**
   * Obtenir l'icône de la classe de compte
   */
  getAccountClassIcon(accountClass: string): string {
    switch (accountClass) {
      case 'CURRENT_ASSETS': return 'account_balance';
      case 'FIXED_ASSETS': return 'home';
      case 'CURRENT_LIABILITIES': return 'account_balance_wallet';
      case 'LONG_TERM_LIABILITIES': return 'schedule';
      case 'SHAREHOLDERS_EQUITY': return 'trending_up';
      case 'OPERATING_REVENUE': return 'trending_up';
      case 'NON_OPERATING_REVENUE': return 'trending_up';
      case 'OPERATING_EXPENSES': return 'trending_down';
      case 'NON_OPERATING_EXPENSES': return 'trending_down';
      default: return 'account_balance';
    }
  }

  /**
   * Formater la devise
   */
  formatCurrency(amount: number, currency: string = 'XOF'): string {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: currency
    }).format(amount);
  }

  /**
   * Formater la date
   */
  formatDate(date: string): string {
    return new Intl.DateTimeFormat('fr-FR').format(new Date(date));
  }

  /**
   * Calculer le solde net d'un compte
   */
  calculateNetBalance(debitBalance: number, creditBalance: number, normalBalance: string): number {
    if (normalBalance === 'DEBIT') {
      return debitBalance - creditBalance;
    } else {
      return creditBalance - debitBalance;
    }
  }

  /**
   * Vérifier si un compte est équilibré
   */
  isAccountBalanced(debitBalance: number, creditBalance: number): boolean {
    return Math.abs(debitBalance - creditBalance) < 0.01;
  }

  /**
   * Obtenir le niveau d'indentation pour l'affichage hiérarchique
   */
  getIndentationLevel(accountLevel: number): string {
    return '  '.repeat(accountLevel - 1);
  }
}








