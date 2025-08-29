import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json'
    });
  }

  // ==================== DASHBOARD ENDPOINTS ====================
  getDashboardTest(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/dashboard/test`, { headers: this.getHeaders() });
  }

  getFinancialKPIs(companyId: number = 1): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/dashboard/financial/kpis?companyId=${companyId}`, { headers: this.getHeaders() });
  }

  getOperationalMetrics(companyId: number = 1): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/dashboard/operational/metrics?companyId=${companyId}`, { headers: this.getHeaders() });
  }

  // ==================== COMPTABILITÉ ENDPOINTS ====================
  getJournalEntries(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/accounting/journal-entries`, { headers: this.getHeaders() });
  }

  createJournalEntry(entry: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/api/accounting/journal-entries`, entry, { headers: this.getHeaders() });
  }

  updateJournalEntry(id: number, entry: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/api/accounting/journal-entries/${id}`, entry, { headers: this.getHeaders() });
  }

  deleteJournalEntry(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/api/accounting/journal-entries/${id}`, { headers: this.getHeaders() });
  }

  getChartOfAccounts(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/accounting/chart-of-accounts`, { headers: this.getHeaders() });
  }

  getGeneralLedger(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/accounting/general-ledger`, { headers: this.getHeaders() });
  }

  // ==================== RH & PAIE ENDPOINTS ====================
  getEmployees(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/hr/employees`, { headers: this.getHeaders() });
  }

  createEmployee(employee: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/api/hr/employees`, employee, { headers: this.getHeaders() });
  }

  updateEmployee(id: number, employee: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/api/hr/employees/${id}`, employee, { headers: this.getHeaders() });
  }

  deleteEmployee(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/api/hr/employees/${id}`, { headers: this.getHeaders() });
  }

  getPayroll(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/hr/payroll`, { headers: this.getHeaders() });
  }

  getLeaveRequests(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/hr/leaves`, { headers: this.getHeaders() });
  }

  createLeaveRequest(leave: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/api/hr/leaves`, leave, { headers: this.getHeaders() });
  }

  // ==================== TIERS ENDPOINTS ====================
  getCustomers(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/third-parties/customers`, { headers: this.getHeaders() });
  }

  createCustomer(customer: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/api/third-parties/customers`, customer, { headers: this.getHeaders() });
  }

  updateCustomer(id: number, customer: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/api/third-parties/customers/${id}`, customer, { headers: this.getHeaders() });
  }

  deleteCustomer(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/api/third-parties/customers/${id}`, { headers: this.getHeaders() });
  }

  getSuppliers(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/third-parties/suppliers`, { headers: this.getHeaders() });
  }

  createSupplier(supplier: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/api/third-parties/suppliers`, supplier, { headers: this.getHeaders() });
  }

  // ==================== AUTH ENDPOINTS ====================
  getAuthTest(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/auth/test`, { headers: this.getHeaders() });
  }

  getUserCount(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/auth/users/count`, { headers: this.getHeaders() });
  }

  login(credentials: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/api/auth/login`, credentials, { headers: this.getHeaders() });
  }

  register(user: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/api/auth/register`, user, { headers: this.getHeaders() });
  }

  // ==================== HEALTH CHECK ====================
  getHealth(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/health`, { headers: this.getHeaders() });
  }
}
