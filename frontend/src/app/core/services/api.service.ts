import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // Test de connexion backend
  testConnection(): Observable<any> {
    return this.http.get(`/api/dashboard/test`);
  }

  // Test de santé backend
  getHealth(): Observable<any> {
    return this.http.get(`/api/health`);
  }

  // Test authentification
  testAuth(): Observable<any> {
    return this.http.get(`/api/auth/test`);
  }

  // Comptabilité - Écritures (journal entries)
  getJournalEntries(): Observable<any> {
    return this.http.get(`/api/accounting/journal-entries`);
  }

  createJournalEntry(entry: any): Observable<any> {
    return this.http.post(`/api/accounting/journal-entries`, entry);
  }

  // utilitaires génériques
  post<T = any>(url: string, body: any): Observable<T> { return this.http.post<T>(url, body); }
  delete<T = any>(url: string): Observable<T> { return this.http.delete<T>(url); }

  // Reporting de base
  getTrialBalance(companyId: number, asOfDate: string): Observable<any> {
    return this.http.get(`/api/reporting/trial-balance`, { params: { companyId, asOfDate } as any });
  }

  getGeneralLedger(companyId: number, asOfDate: string): Observable<any> {
    return this.http.get(`/api/reporting/general-ledger`, { params: { companyId, asOfDate } as any });
  }

  getBalanceSheet(companyId: number, asOfDate: string): Observable<any> {
    return this.http.get(`/api/reporting/balance-sheet`, { params: { companyId, asOfDate } as any });
  }

  getIncomeStatement(companyId: number, startDate: string, endDate: string): Observable<any> {
    return this.http.get(`/api/reporting/income-statement`, { params: { companyId, startDate, endDate } as any });
  }
}
