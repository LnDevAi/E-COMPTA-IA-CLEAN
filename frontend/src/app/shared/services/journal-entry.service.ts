import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface JournalEntry {
  id: number;
  entryNumber: string;
  entryDate: string;
  description: string;
  journalType: string;
  currency: string;
  totalDebit: number;
  totalCredit: number;
  status: string;
  notes?: string;
  companyId: number;
  countryCode: string;
  accountingStandard: string;
  reference?: string;
  documentNumber?: string;
  createdAt: string;
  updatedAt: string;
  createdBy?: {
    id: number;
    username: string;
    firstName: string;
    lastName: string;
  };
  validatedBy?: {
    id: number;
    username: string;
    firstName: string;
    lastName: string;
  };
  validatedAt?: string;
  isReconciled: boolean;
  isPosted: boolean;
  accountEntries: AccountEntry[];
}

export interface AccountEntry {
  id: number;
  accountNumber: string;
  accountName: string;
  accountType: string;
  amount: number;
  description?: string;
  thirdPartyCode?: string;
  reference?: string;
  documentNumber?: string;
  createdAt: string;
  updatedAt: string;
  companyId: number;
  countryCode: string;
  accountingStandard: string;
  isReconciled: boolean;
  reconciliationReference?: string;
  reconciledAt?: string;
  reconciledBy?: {
    id: number;
    username: string;
    firstName: string;
    lastName: string;
  };
}

export interface CreateJournalEntryRequest {
  entry: {
    entryDate: string;
    description: string;
    journalType: string;
    currency: string;
    countryCode: string;
    accountingStandard: string;
    reference?: string;
    documentNumber?: string;
    notes?: string;
  };
  accountEntries: {
    accountNumber: string;
    accountName: string;
    accountType: string;
    amount: number;
    description?: string;
    thirdPartyCode?: string;
    reference?: string;
    documentNumber?: string;
  }[];
}

export interface JournalEntryStatistics {
  totalEntries: number;
  draftEntries: number;
  postedEntries: number;
  validatedEntries: number;
  cancelledEntries: number;
  totalDebit: number;
  totalCredit: number;
  entriesByType: { [key: string]: number };
  entriesByStatus: { [key: string]: number };
}

@Injectable({
  providedIn: 'root'
})
export class JournalEntryService {
  private readonly apiUrl = `${environment.apiUrl}/api/journal-entries`;
  private journalEntriesSubject = new BehaviorSubject<JournalEntry[]>([]);
  public journalEntries$ = this.journalEntriesSubject.asObservable();

  constructor(private http: HttpClient) {}

  /**
   * Récupérer toutes les écritures
   */
  getAllJournalEntries(): Observable<JournalEntry[]> {
    return this.http.get<{data: JournalEntry[], total: number, status: string}>(this.apiUrl)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            this.journalEntriesSubject.next(response.data);
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des écritures');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des écritures:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les écritures d'une entreprise
   */
  getJournalEntriesByCompany(companyId: number): Observable<JournalEntry[]> {
    return this.http.get<{entries: JournalEntry[], total: number, status: string}>(`${this.apiUrl}/company/${companyId}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            this.journalEntriesSubject.next(response.entries);
            return response.entries;
          }
          throw new Error('Erreur lors de la récupération des écritures');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des écritures:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les écritures par statut
   */
  getJournalEntriesByStatus(companyId: number, status: string): Observable<JournalEntry[]> {
    return this.http.get<{entries: JournalEntry[], total: number, status: string}>(`${this.apiUrl}/company/${companyId}/status/${status}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.entries;
          }
          throw new Error('Erreur lors de la récupération des écritures');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des écritures:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les écritures par période
   */
  getJournalEntriesByDateRange(companyId: number, startDate: string, endDate: string): Observable<JournalEntry[]> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);

    return this.http.get<{entries: JournalEntry[], total: number, status: string}>(`${this.apiUrl}/company/${companyId}/period`, { params })
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.entries;
          }
          throw new Error('Erreur lors de la récupération des écritures');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des écritures:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les écritures comptabilisées
   */
  getPostedJournalEntries(companyId: number): Observable<JournalEntry[]> {
    return this.http.get<{entries: JournalEntry[], total: number, status: string}>(`${this.apiUrl}/company/${companyId}/posted`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.entries;
          }
          throw new Error('Erreur lors de la récupération des écritures');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des écritures:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les brouillons
   */
  getDraftJournalEntries(companyId: number): Observable<JournalEntry[]> {
    return this.http.get<{entries: JournalEntry[], total: number, status: string}>(`${this.apiUrl}/company/${companyId}/drafts`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.entries;
          }
          throw new Error('Erreur lors de la récupération des écritures');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des écritures:', error);
          throw error;
        })
      );
  }

  /**
   * Créer une nouvelle écriture
   */
  createJournalEntry(request: CreateJournalEntryRequest): Observable<JournalEntry> {
    return this.http.post<{message: string, entry: JournalEntry, status: string}>(this.apiUrl, request)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            // Mettre à jour la liste locale
            const currentEntries = this.journalEntriesSubject.value;
            this.journalEntriesSubject.next([...currentEntries, response.entry]);
            return response.entry;
          }
          throw new Error(response.message || 'Erreur lors de la création de l\'écriture');
        }),
        catchError(error => {
          console.error('Erreur lors de la création de l\'écriture:', error);
          throw error;
        })
      );
  }

  /**
   * Valider une écriture
   */
  validateJournalEntry(entryId: number, validatedBy: string): Observable<JournalEntry> {
    return this.http.put<{message: string, entry: JournalEntry, status: string}>(`${this.apiUrl}/${entryId}/validate`, { validatedBy })
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            // Mettre à jour la liste locale
            const currentEntries = this.journalEntriesSubject.value;
            const updatedEntries = currentEntries.map(entry => 
              entry.id === entryId ? response.entry : entry
            );
            this.journalEntriesSubject.next(updatedEntries);
            return response.entry;
          }
          throw new Error(response.message || 'Erreur lors de la validation de l\'écriture');
        }),
        catchError(error => {
          console.error('Erreur lors de la validation de l\'écriture:', error);
          throw error;
        })
      );
  }

  /**
   * Annuler une écriture
   */
  cancelJournalEntry(entryId: number): Observable<JournalEntry> {
    return this.http.put<{message: string, entry: JournalEntry, status: string}>(`${this.apiUrl}/${entryId}/cancel`, {})
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            // Mettre à jour la liste locale
            const currentEntries = this.journalEntriesSubject.value;
            const updatedEntries = currentEntries.map(entry => 
              entry.id === entryId ? response.entry : entry
            );
            this.journalEntriesSubject.next(updatedEntries);
            return response.entry;
          }
          throw new Error(response.message || 'Erreur lors de l\'annulation de l\'écriture');
        }),
        catchError(error => {
          console.error('Erreur lors de l\'annulation de l\'écriture:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les statistiques des écritures
   */
  getJournalEntryStatistics(companyId: number): Observable<JournalEntryStatistics> {
    return this.http.get<{statistics: JournalEntryStatistics, status: string}>(`${this.apiUrl}/company/${companyId}/statistics`)
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
   * Vérifier si une écriture est équilibrée
   */
  isJournalEntryBalanced(entry: JournalEntry): boolean {
    return entry.totalDebit === entry.totalCredit;
  }

  /**
   * Calculer le total des débits d'une écriture
   */
  calculateTotalDebit(accountEntries: AccountEntry[]): number {
    return accountEntries
      .filter(entry => entry.accountType === 'DEBIT')
      .reduce((total, entry) => total + entry.amount, 0);
  }

  /**
   * Calculer le total des crédits d'une écriture
   */
  calculateTotalCredit(accountEntries: AccountEntry[]): number {
    return accountEntries
      .filter(entry => entry.accountType === 'CREDIT')
      .reduce((total, entry) => total + entry.amount, 0);
  }

  /**
   * Formater le numéro d'écriture
   */
  formatEntryNumber(entryNumber: string): string {
    return entryNumber.padStart(6, '0');
  }

  /**
   * Obtenir la couleur du statut
   */
  getStatusColor(status: string): string {
    switch (status.toUpperCase()) {
      case 'BROUILLON': return 'gray';
      case 'VALIDÉ': return 'green';
      case 'ANNULE': return 'red';
      case 'COMPTABILISÉ': return 'blue';
      default: return 'gray';
    }
  }

  /**
   * Obtenir l'icône du statut
   */
  getStatusIcon(status: string): string {
    switch (status.toUpperCase()) {
      case 'BROUILLON': return 'edit';
      case 'VALIDÉ': return 'check_circle';
      case 'ANNULE': return 'cancel';
      case 'COMPTABILISÉ': return 'check';
      default: return 'help';
    }
  }

  /**
   * Formater la devise
   */
  formatCurrency(amount: number, currency: string = 'EUR'): string {
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
