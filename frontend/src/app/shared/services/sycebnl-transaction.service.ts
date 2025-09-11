import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, catchError, of } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface SycebnlTransaction {
  id?: number;
  transactionNumber: string;
  transactionDate: Date;
  description: string;
  transactionType: 'INCOME' | 'EXPENSE' | 'TRANSFER' | 'ADJUSTMENT';
  amount: number;
  currency: string;
  accountNumber: string;
  accountName: string;
  thirdPartyName?: string;
  thirdPartyCode?: string;
  reference?: string;
  documentNumber?: string;
  status: 'DRAFT' | 'VALIDATED' | 'CANCELLED' | 'POSTED';
  notes?: string;
  companyId: number;
  countryCode: string;
  accountingStandard: string;
  createdBy?: number;
  validatedBy?: number;
  validatedAt?: Date;
  createdAt?: Date;
  updatedAt?: Date;
  isReconciled?: boolean;
  reconciliationReference?: string;
  reconciledAt?: Date;
  reconciledBy?: number;
}

export interface CreateSycebnlTransactionRequest {
  transactionNumber: string;
  transactionDate: Date;
  description: string;
  transactionType: string;
  amount: number;
  currency: string;
  accountNumber: string;
  accountName: string;
  thirdPartyName?: string;
  thirdPartyCode?: string;
  reference?: string;
  documentNumber?: string;
  notes?: string;
  companyId: number;
  countryCode: string;
  accountingStandard: string;
}

export interface UpdateSycebnlTransactionRequest extends Partial<CreateSycebnlTransactionRequest> {
  id: number;
}

@Injectable({
  providedIn: 'root'
})
export class SycebnlTransactionService {
  private apiUrl = `${environment.apiUrl}/sycebnl/transactions`;

  constructor(private http: HttpClient) {}

  /**
   * Obtenir toutes les transactions SYCEBNL
   */
  getAllSycebnlTransactions(): Observable<SycebnlTransaction[]> {
    return this.http.get<{data: SycebnlTransaction[], status: string}>(this.apiUrl)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des transactions');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des transactions:', error);
          return of([]);
        })
      );
  }

  /**
   * Obtenir une transaction par ID
   */
  getSycebnlTransactionById(id: number): Observable<SycebnlTransaction> {
    return this.http.get<{data: SycebnlTransaction, status: string}>(`${this.apiUrl}/${id}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération de la transaction');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération de la transaction:', error);
          throw error;
        })
      );
  }

  /**
   * Créer une nouvelle transaction
   */
  createSycebnlTransaction(transaction: CreateSycebnlTransactionRequest): Observable<SycebnlTransaction> {
    return this.http.post<{data: SycebnlTransaction, status: string}>(this.apiUrl, transaction)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la création de la transaction');
        }),
        catchError(error => {
          console.error('Erreur lors de la création de la transaction:', error);
          throw error;
        })
      );
  }

  /**
   * Mettre à jour une transaction
   */
  updateSycebnlTransaction(transaction: UpdateSycebnlTransactionRequest): Observable<SycebnlTransaction> {
    return this.http.put<{data: SycebnlTransaction, status: string}>(`${this.apiUrl}/${transaction.id}`, transaction)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la mise à jour de la transaction');
        }),
        catchError(error => {
          console.error('Erreur lors de la mise à jour de la transaction:', error);
          throw error;
        })
      );
  }

  /**
   * Supprimer une transaction
   */
  deleteSycebnlTransaction(id: number): Observable<boolean> {
    return this.http.delete<{message: string, status: string}>(`${this.apiUrl}/${id}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return true;
          }
          throw new Error('Erreur lors de la suppression de la transaction');
        }),
        catchError(error => {
          console.error('Erreur lors de la suppression de la transaction:', error);
          return of(false);
        })
      );
  }

  /**
   * Valider une transaction
   */
  validateSycebnlTransaction(transactionId: number, validatedBy: string): Observable<SycebnlTransaction> {
    return this.http.put<{message: string, transaction: SycebnlTransaction, status: string}>(`${this.apiUrl}/${transactionId}/validate`, { validatedBy })
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.transaction;
          }
          throw new Error('Erreur lors de la validation de la transaction');
        }),
        catchError(error => {
          console.error('Erreur lors de la validation de la transaction:', error);
          throw error;
        })
      );
  }

  /**
   * Annuler une transaction
   */
  cancelSycebnlTransaction(transactionId: number): Observable<SycebnlTransaction> {
    return this.http.put<{message: string, transaction: SycebnlTransaction, status: string}>(`${this.apiUrl}/${transactionId}/cancel`, {})
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.transaction;
          }
          throw new Error('Erreur lors de l\'annulation de la transaction');
        }),
        catchError(error => {
          console.error('Erreur lors de l\'annulation de la transaction:', error);
          throw error;
        })
      );
  }

  /**
   * Obtenir les transactions par type
   */
  getSycebnlTransactionsByType(type: string): Observable<SycebnlTransaction[]> {
    return this.http.get<{data: SycebnlTransaction[], status: string}>(`${this.apiUrl}/type/${type}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des transactions par type');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des transactions par type:', error);
          return of([]);
        })
      );
  }

  /**
   * Obtenir les transactions par statut
   */
  getSycebnlTransactionsByStatus(status: string): Observable<SycebnlTransaction[]> {
    return this.http.get<{data: SycebnlTransaction[], status: string}>(`${this.apiUrl}/status/${status}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des transactions par statut');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des transactions par statut:', error);
          return of([]);
        })
      );
  }

  /**
   * Obtenir les transactions par période
   */
  getSycebnlTransactionsByDateRange(startDate: Date, endDate: Date): Observable<SycebnlTransaction[]> {
    const params = {
      startDate: startDate.toISOString().split('T')[0],
      endDate: endDate.toISOString().split('T')[0]
    };
    
    return this.http.get<{data: SycebnlTransaction[], status: string}>(`${this.apiUrl}/date-range`, { params })
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des transactions par période');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des transactions par période:', error);
          return of([]);
        })
      );
  }

  /**
   * Obtenir les options pour les types de transactions
   */
  getTransactionTypes(): Array<{value: string, label: string}> {
    return [
      { value: 'INCOME', label: 'Recette' },
      { value: 'EXPENSE', label: 'Dépense' },
      { value: 'TRANSFER', label: 'Transfert' },
      { value: 'ADJUSTMENT', label: 'Ajustement' }
    ];
  }

  /**
   * Obtenir les options pour les statuts
   */
  getTransactionStatuses(): Array<{value: string, label: string}> {
    return [
      { value: 'DRAFT', label: 'Brouillon' },
      { value: 'VALIDATED', label: 'Validé' },
      { value: 'CANCELLED', label: 'Annulé' },
      { value: 'POSTED', label: 'Comptabilisé' }
    ];
  }

  /**
   * Obtenir l'icône du type de transaction
   */
  getTransactionTypeIcon(type: string): string {
    switch (type) {
      case 'INCOME': return 'trending_up';
      case 'EXPENSE': return 'trending_down';
      case 'TRANSFER': return 'swap_horiz';
      case 'ADJUSTMENT': return 'tune';
      default: return 'help';
    }
  }

  /**
   * Obtenir la couleur du type de transaction
   */
  getTransactionTypeColor(type: string): string {
    switch (type) {
      case 'INCOME': return 'green';
      case 'EXPENSE': return 'red';
      case 'TRANSFER': return 'blue';
      case 'ADJUSTMENT': return 'orange';
      default: return 'gray';
    }
  }

  /**
   * Obtenir la couleur du statut
   */
  getStatusColor(status: string): string {
    switch (status) {
      case 'DRAFT': return 'gray';
      case 'VALIDATED': return 'green';
      case 'CANCELLED': return 'red';
      case 'POSTED': return 'blue';
      default: return 'gray';
    }
  }

  /**
   * Obtenir l'icône du statut
   */
  getStatusIcon(status: string): string {
    switch (status) {
      case 'DRAFT': return 'edit';
      case 'VALIDATED': return 'check_circle';
      case 'CANCELLED': return 'cancel';
      case 'POSTED': return 'check';
      default: return 'help';
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
}








