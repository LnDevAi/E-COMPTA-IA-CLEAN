import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface SycebnlAccount {
  id: number;
  organizationId: number;
  accountCode: string;
  accountName: string;
  accountDescription?: string;
  accountClass: number;
  accountCategory: 'ASSETS' | 'LIABILITIES' | 'EQUITY' | 'REVENUE' | 'EXPENSES' | 'OFF_BALANCE';
  accountType: 'ASSET' | 'LIABILITY' | 'EQUITY' | 'REVENUE' | 'EXPENSE' | 'OFF_BALANCE';
  parentAccountId?: number;
  accountLevel: number;
  isGroupAccount: boolean;
  openingDebitBalance: number;
  openingCreditBalance: number;
  currentDebitBalance: number;
  currentCreditBalance: number;
  closingDebitBalance: number;
  closingCreditBalance: number;
  isRestricted: boolean;
  restrictionType?: 'DONOR' | 'TIME' | 'PURPOSE' | 'GEOGRAPHIC';
  restrictionDescription?: string;
  donorRestriction?: string;
  timeRestriction?: string;
  purposeRestriction?: string;
  isDepreciable: boolean;
  depreciationMethod?: 'STRAIGHT_LINE' | 'DECLINING_BALANCE' | 'UNITS_OF_PRODUCTION';
  usefulLifeYears?: number;
  depreciationRate?: number;
  accumulatedDepreciation: number;
  isActive: boolean;
  isSystemAccount: boolean;
  requiresApproval: boolean;
  isCashAccount: boolean;
  isBankAccount: boolean;
  createdBy?: string;
  approvedBy?: string;
  approvalDate?: string;
  createdAt: string;
  updatedAt: string;
  parentAccount?: SycebnlAccount;
  children?: SycebnlAccount[];
}

export interface SycebnlOrganization {
  id: number;
  companyId: number;
  organizationName: string;
  legalForm?: string;
  registrationNumber?: string;
  taxIdentificationNumber?: string;
  organizationType: 'ASSOCIATION' | 'FOUNDATION' | 'NGO' | 'COOPERATIVE' | 'OTHER';
  accountingSystem: 'NORMAL' | 'MINIMAL';
  fiscalYearStart?: string;
  fiscalYearEnd?: string;
  baseCurrency: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
  accounts?: SycebnlAccount[];
}

export interface CreateSycebnlAccountRequest {
  organizationId: number;
  accountCode: string;
  accountName: string;
  accountDescription?: string;
  accountClass: number;
  accountCategory: string;
  accountType: string;
  parentAccountId?: number;
  accountLevel: number;
  isGroupAccount: boolean;
  isRestricted?: boolean;
  restrictionType?: string;
  restrictionDescription?: string;
  isDepreciable?: boolean;
  depreciationMethod?: string;
  usefulLifeYears?: number;
  depreciationRate?: number;
  isCashAccount?: boolean;
  isBankAccount?: boolean;
  requiresApproval?: boolean;
}

export interface SycebnlAccountStatistics {
  totalAccounts: number;
  activeAccounts: number;
  inactiveAccounts: number;
  groupAccounts: number;
  detailAccounts: number;
  accountsByClass: { [key: string]: number };
  accountsByCategory: { [key: string]: number };
  totalDebitBalance: number;
  totalCreditBalance: number;
  restrictedAccounts: number;
  depreciableAccounts: number;
}

@Injectable({
  providedIn: 'root'
})
export class SycebnlAccountService {
  private readonly apiUrl = `${environment.apiUrl}/api/sycebnl`;
  private accountsSubject = new BehaviorSubject<SycebnlAccount[]>([]);
  public accounts$ = this.accountsSubject.asObservable();

  constructor(private http: HttpClient) {}

  /**
   * Récupérer tous les comptes SYCEBNL
   */
  getAllSycebnlAccounts(): Observable<SycebnlAccount[]> {
    return this.http.get<{data: SycebnlAccount[], total: number, status: string}>(`${this.apiUrl}/accounts`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            this.accountsSubject.next(response.data);
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des comptes');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des comptes:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les comptes d'une organisation
   */
  getSycebnlAccountsByOrganization(organizationId: number): Observable<SycebnlAccount[]> {
    return this.http.get<{accounts: SycebnlAccount[], total: number, status: string}>(`${this.apiUrl}/organizations/${organizationId}/accounts`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            this.accountsSubject.next(response.accounts);
            return response.accounts;
          }
          throw new Error('Erreur lors de la récupération des comptes');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des comptes:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les comptes par classe
   */
  getSycebnlAccountsByClass(organizationId: number, accountClass: number): Observable<SycebnlAccount[]> {
    return this.http.get<{accounts: SycebnlAccount[], total: number, status: string}>(`${this.apiUrl}/organizations/${organizationId}/accounts/class/${accountClass}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.accounts;
          }
          throw new Error('Erreur lors de la récupération des comptes');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des comptes:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les comptes par catégorie
   */
  getSycebnlAccountsByCategory(organizationId: number, category: string): Observable<SycebnlAccount[]> {
    return this.http.get<{accounts: SycebnlAccount[], total: number, status: string}>(`${this.apiUrl}/organizations/${organizationId}/accounts/category/${category}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.accounts;
          }
          throw new Error('Erreur lors de la récupération des comptes');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des comptes:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les comptes actifs
   */
  getActiveSycebnlAccounts(organizationId: number): Observable<SycebnlAccount[]> {
    return this.http.get<{accounts: SycebnlAccount[], total: number, status: string}>(`${this.apiUrl}/organizations/${organizationId}/accounts/active`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.accounts;
          }
          throw new Error('Erreur lors de la récupération des comptes');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des comptes:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les comptes de groupe
   */
  getGroupSycebnlAccounts(organizationId: number): Observable<SycebnlAccount[]> {
    return this.http.get<{accounts: SycebnlAccount[], total: number, status: string}>(`${this.apiUrl}/organizations/${organizationId}/accounts/groups`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.accounts;
          }
          throw new Error('Erreur lors de la récupération des comptes');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des comptes:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les comptes de détail
   */
  getDetailSycebnlAccounts(organizationId: number): Observable<SycebnlAccount[]> {
    return this.http.get<{accounts: SycebnlAccount[], total: number, status: string}>(`${this.apiUrl}/organizations/${organizationId}/accounts/details`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.accounts;
          }
          throw new Error('Erreur lors de la récupération des comptes');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des comptes:', error);
          throw error;
        })
      );
  }

  /**
   * Créer un nouveau compte SYCEBNL
   */
  createSycebnlAccount(request: CreateSycebnlAccountRequest): Observable<SycebnlAccount> {
    return this.http.post<{message: string, account: SycebnlAccount, status: string}>(`${this.apiUrl}/accounts`, request)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            // Mettre à jour la liste locale
            const currentAccounts = this.accountsSubject.value;
            this.accountsSubject.next([...currentAccounts, response.account]);
            return response.account;
          }
          throw new Error(response.message || 'Erreur lors de la création du compte');
        }),
        catchError(error => {
          console.error('Erreur lors de la création du compte:', error);
          throw error;
        })
      );
  }

  /**
   * Mettre à jour un compte SYCEBNL
   */
  updateSycebnlAccount(accountId: number, request: Partial<CreateSycebnlAccountRequest>): Observable<SycebnlAccount> {
    return this.http.put<{message: string, account: SycebnlAccount, status: string}>(`${this.apiUrl}/accounts/${accountId}`, request)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            // Mettre à jour la liste locale
            const currentAccounts = this.accountsSubject.value;
            const updatedAccounts = currentAccounts.map(account => 
              account.id === accountId ? response.account : account
            );
            this.accountsSubject.next(updatedAccounts);
            return response.account;
          }
          throw new Error(response.message || 'Erreur lors de la mise à jour du compte');
        }),
        catchError(error => {
          console.error('Erreur lors de la mise à jour du compte:', error);
          throw error;
        })
      );
  }

  /**
   * Supprimer un compte SYCEBNL
   */
  deleteSycebnlAccount(accountId: number): Observable<void> {
    return this.http.delete<{message: string, status: string}>(`${this.apiUrl}/accounts/${accountId}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            // Mettre à jour la liste locale
            const currentAccounts = this.accountsSubject.value;
            const updatedAccounts = currentAccounts.filter(account => account.id !== accountId);
            this.accountsSubject.next(updatedAccounts);
            return;
          }
          throw new Error(response.message || 'Erreur lors de la suppression du compte');
        }),
        catchError(error => {
          console.error('Erreur lors de la suppression du compte:', error);
          throw error;
        })
      );
  }

  /**
   * Activer/Désactiver un compte
   */
  toggleSycebnlAccountStatus(accountId: number): Observable<SycebnlAccount> {
    return this.http.put<{message: string, account: SycebnlAccount, status: string}>(`${this.apiUrl}/accounts/${accountId}/toggle-status`, {})
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            // Mettre à jour la liste locale
            const currentAccounts = this.accountsSubject.value;
            const updatedAccounts = currentAccounts.map(account => 
              account.id === accountId ? response.account : account
            );
            this.accountsSubject.next(updatedAccounts);
            return response.account;
          }
          throw new Error(response.message || 'Erreur lors du changement de statut');
        }),
        catchError(error => {
          console.error('Erreur lors du changement de statut:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les statistiques des comptes
   */
  getSycebnlAccountStatistics(organizationId: number): Observable<SycebnlAccountStatistics> {
    return this.http.get<{statistics: SycebnlAccountStatistics, status: string}>(`${this.apiUrl}/organizations/${organizationId}/accounts/statistics`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.statistics;
          }
          throw new Error('Erreur lors de la récupération des statistiques');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des statistiques:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les organisations SYCEBNL
   */
  getSycebnlOrganizations(): Observable<SycebnlOrganization[]> {
    return this.http.get<{organizations: SycebnlOrganization[], total: number, status: string}>(`${this.apiUrl}/organizations`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.organizations;
          }
          throw new Error('Erreur lors de la récupération des organisations');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des organisations:', error);
          throw error;
        })
      );
  }

  /**
   * Obtenir la couleur de la catégorie
   */
  getCategoryColor(category: string): string {
    switch (category) {
      case 'ASSETS': return 'blue';
      case 'LIABILITIES': return 'red';
      case 'EQUITY': return 'green';
      case 'REVENUE': return 'orange';
      case 'EXPENSES': return 'purple';
      case 'OFF_BALANCE': return 'gray';
      default: return 'gray';
    }
  }

  /**
   * Obtenir l'icône de la catégorie
   */
  getCategoryIcon(category: string): string {
    switch (category) {
      case 'ASSETS': return 'account_balance';
      case 'LIABILITIES': return 'credit_card';
      case 'EQUITY': return 'trending_up';
      case 'REVENUE': return 'attach_money';
      case 'EXPENSES': return 'money_off';
      case 'OFF_BALANCE': return 'help';
      default: return 'help';
    }
  }

  /**
   * Obtenir le libellé de la catégorie
   */
  getCategoryLabel(category: string): string {
    switch (category) {
      case 'ASSETS': return 'Actifs';
      case 'LIABILITIES': return 'Passifs';
      case 'EQUITY': return 'Capitaux propres';
      case 'REVENUE': return 'Produits';
      case 'EXPENSES': return 'Charges';
      case 'OFF_BALANCE': return 'Hors bilan';
      default: return category;
    }
  }

  /**
   * Formater le solde
   */
  formatBalance(balance: number, currency: string = 'EUR'): string {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: currency
    }).format(balance);
  }

  /**
   * Vérifier si un compte est équilibré
   */
  isAccountBalanced(account: SycebnlAccount): boolean {
    return account.currentDebitBalance === account.currentCreditBalance;
  }

  /**
   * Obtenir l'indentation pour l'affichage hiérarchique
   */
  getIndentation(level: number): string {
    return '0 0 0 ' + (level * 20) + 'px';
  }
}








