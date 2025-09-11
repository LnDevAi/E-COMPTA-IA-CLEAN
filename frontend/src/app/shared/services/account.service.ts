import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface Account {
  id: number;
  companyId: number;
  accountCode: string;
  accountName: string;
  accountDescription?: string;
  accountType: 'ASSET' | 'LIABILITY' | 'EQUITY' | 'REVENUE' | 'EXPENSE' | 'OFF_BALANCE';
  accountCategory: 'CURRENT_ASSET' | 'FIXED_ASSET' | 'CURRENT_LIABILITY' | 'LONG_TERM_LIABILITY' | 'EQUITY' | 'REVENUE' | 'EXPENSE' | 'OFF_BALANCE';
  parentAccountId?: number;
  accountLevel: number;
  isGroupAccount: boolean;
  openingDebitBalance: number;
  openingCreditBalance: number;
  currentDebitBalance: number;
  currentCreditBalance: number;
  closingDebitBalance: number;
  closingCreditBalance: number;
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
  // Relations
  parentAccount?: Account;
  children?: Account[];
  company?: {
    id: number;
    name: string;
    legalName: string;
    registrationNumber: string;
    taxNumber: string;
    address: string;
    city: string;
    postalCode: string;
    country: string;
    phone: string;
    email: string;
    website?: string;
    industry?: string;
    size?: string;
    isActive: boolean;
  };
}

export interface CreateAccountRequest {
  companyId: number;
  accountCode: string;
  accountName: string;
  accountDescription?: string;
  accountType: string;
  accountCategory: string;
  parentAccountId?: number;
  accountLevel: number;
  isGroupAccount: boolean;
  isCashAccount?: boolean;
  isBankAccount?: boolean;
  requiresApproval?: boolean;
}

export interface AccountStatistics {
  totalAccounts: number;
  activeAccounts: number;
  inactiveAccounts: number;
  groupAccounts: number;
  detailAccounts: number;
  accountsByType: { [key: string]: number };
  accountsByCategory: { [key: string]: number };
  totalDebitBalance: number;
  totalCreditBalance: number;
  cashAccounts: number;
  bankAccounts: number;
}

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private readonly apiUrl = `${environment.apiUrl}/api/accounts`;
  private accountsSubject = new BehaviorSubject<Account[]>([]);
  public accounts$ = this.accountsSubject.asObservable();

  constructor(private http: HttpClient) {}

  /**
   * Récupérer tous les comptes
   */
  getAllAccounts(): Observable<Account[]> {
    return this.http.get<{data: Account[], total: number, status: string}>(this.apiUrl)
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
   * Récupérer les comptes d'une entreprise
   */
  getAccountsByCompany(companyId: number): Observable<Account[]> {
    return this.http.get<{accounts: Account[], total: number, status: string}>(`${this.apiUrl}/company/${companyId}`)
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
   * Récupérer les comptes par type
   */
  getAccountsByType(companyId: number, accountType: string): Observable<Account[]> {
    return this.http.get<{accounts: Account[], total: number, status: string}>(`${this.apiUrl}/company/${companyId}/type/${accountType}`)
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
  getAccountsByCategory(companyId: number, category: string): Observable<Account[]> {
    return this.http.get<{accounts: Account[], total: number, status: string}>(`${this.apiUrl}/company/${companyId}/category/${category}`)
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
  getActiveAccounts(companyId: number): Observable<Account[]> {
    return this.http.get<{accounts: Account[], total: number, status: string}>(`${this.apiUrl}/company/${companyId}/active`)
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
  getGroupAccounts(companyId: number): Observable<Account[]> {
    return this.http.get<{accounts: Account[], total: number, status: string}>(`${this.apiUrl}/company/${companyId}/groups`)
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
  getDetailAccounts(companyId: number): Observable<Account[]> {
    return this.http.get<{accounts: Account[], total: number, status: string}>(`${this.apiUrl}/company/${companyId}/details`)
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
   * Récupérer les comptes de caisse
   */
  getCashAccounts(companyId: number): Observable<Account[]> {
    return this.http.get<{accounts: Account[], total: number, status: string}>(`${this.apiUrl}/company/${companyId}/cash`)
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
   * Récupérer les comptes bancaires
   */
  getBankAccounts(companyId: number): Observable<Account[]> {
    return this.http.get<{accounts: Account[], total: number, status: string}>(`${this.apiUrl}/company/${companyId}/bank`)
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
   * Créer un nouveau compte
   */
  createAccount(request: CreateAccountRequest): Observable<Account> {
    return this.http.post<{message: string, account: Account, status: string}>(this.apiUrl, request)
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
   * Mettre à jour un compte
   */
  updateAccount(accountId: number, request: Partial<CreateAccountRequest>): Observable<Account> {
    return this.http.put<{message: string, account: Account, status: string}>(`${this.apiUrl}/${accountId}`, request)
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
   * Supprimer un compte
   */
  deleteAccount(accountId: number): Observable<void> {
    return this.http.delete<{message: string, status: string}>(`${this.apiUrl}/${accountId}`)
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
  toggleAccountStatus(accountId: number): Observable<Account> {
    return this.http.put<{message: string, account: Account, status: string}>(`${this.apiUrl}/${accountId}/toggle-status`, {})
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
  getAccountStatistics(companyId: number): Observable<AccountStatistics> {
    return this.http.get<{statistics: AccountStatistics, status: string}>(`${this.apiUrl}/company/${companyId}/statistics`)
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
   * Obtenir la couleur du type
   */
  getTypeColor(type: string): string {
    switch (type) {
      case 'ASSET': return 'blue';
      case 'LIABILITY': return 'red';
      case 'EQUITY': return 'green';
      case 'REVENUE': return 'orange';
      case 'EXPENSE': return 'purple';
      case 'OFF_BALANCE': return 'gray';
      default: return 'gray';
    }
  }

  /**
   * Obtenir l'icône du type
   */
  getTypeIcon(type: string): string {
    switch (type) {
      case 'ASSET': return 'account_balance';
      case 'LIABILITY': return 'credit_card';
      case 'EQUITY': return 'trending_up';
      case 'REVENUE': return 'attach_money';
      case 'EXPENSE': return 'money_off';
      case 'OFF_BALANCE': return 'help';
      default: return 'help';
    }
  }

  /**
   * Obtenir le libellé du type
   */
  getTypeLabel(type: string): string {
    switch (type) {
      case 'ASSET': return 'Actif';
      case 'LIABILITY': return 'Passif';
      case 'EQUITY': return 'Capitaux propres';
      case 'REVENUE': return 'Produit';
      case 'EXPENSE': return 'Charge';
      case 'OFF_BALANCE': return 'Hors bilan';
      default: return type;
    }
  }

  /**
   * Obtenir la couleur de la catégorie
   */
  getCategoryColor(category: string): string {
    switch (category) {
      case 'CURRENT_ASSET': return 'lightblue';
      case 'FIXED_ASSET': return 'blue';
      case 'CURRENT_LIABILITY': return 'lightcoral';
      case 'LONG_TERM_LIABILITY': return 'red';
      case 'EQUITY': return 'green';
      case 'REVENUE': return 'orange';
      case 'EXPENSE': return 'purple';
      case 'OFF_BALANCE': return 'gray';
      default: return 'gray';
    }
  }

  /**
   * Obtenir l'icône de la catégorie
   */
  getCategoryIcon(category: string): string {
    switch (category) {
      case 'CURRENT_ASSET': return 'account_balance_wallet';
      case 'FIXED_ASSET': return 'home';
      case 'CURRENT_LIABILITY': return 'credit_card';
      case 'LONG_TERM_LIABILITY': return 'account_balance';
      case 'EQUITY': return 'trending_up';
      case 'REVENUE': return 'attach_money';
      case 'EXPENSE': return 'money_off';
      case 'OFF_BALANCE': return 'help';
      default: return 'help';
    }
  }

  /**
   * Obtenir le libellé de la catégorie
   */
  getCategoryLabel(category: string): string {
    switch (category) {
      case 'CURRENT_ASSET': return 'Actif circulant';
      case 'FIXED_ASSET': return 'Actif immobilisé';
      case 'CURRENT_LIABILITY': return 'Passif circulant';
      case 'LONG_TERM_LIABILITY': return 'Passif immobilisé';
      case 'EQUITY': return 'Capitaux propres';
      case 'REVENUE': return 'Produit';
      case 'EXPENSE': return 'Charge';
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
  isAccountBalanced(account: Account): boolean {
    return account.currentDebitBalance === account.currentCreditBalance;
  }

  /**
   * Obtenir l'indentation pour l'affichage hiérarchique
   */
  getIndentation(level: number): string {
    return '0 0 0 ' + (level * 20) + 'px';
  }

  /**
   * Construire l'arbre hiérarchique des comptes
   */
  buildAccountTree(accounts: Account[]): Account[] {
    const accountMap = new Map<number, Account>();
    const rootAccounts: Account[] = [];

    // Créer une map des comptes
    accounts.forEach(account => {
      accountMap.set(account.id, { ...account, children: [] });
    });

    // Construire l'arbre
    accounts.forEach(account => {
      const accountWithChildren = accountMap.get(account.id)!;
      
      if (account.parentAccountId) {
        const parent = accountMap.get(account.parentAccountId);
        if (parent) {
          parent.children!.push(accountWithChildren);
        }
      } else {
        rootAccounts.push(accountWithChildren);
      }
    });

    return rootAccounts;
  }

  /**
   * Aplatir l'arbre des comptes
   */
  flattenAccountTree(accounts: Account[], level: number = 0): Account[] {
    let result: Account[] = [];
    
    accounts.forEach(account => {
      const accountWithLevel = { ...account, accountLevel: level };
      result.push(accountWithLevel);
      
      if (account.children && account.children.length > 0) {
        result = result.concat(this.flattenAccountTree(account.children, level + 1));
      }
    });
    
    return result;
  }
}








