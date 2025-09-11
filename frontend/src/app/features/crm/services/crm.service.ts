import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CrmCustomer } from '../models/crm-customer.model';

@Injectable({
  providedIn: 'root'
})
export class CrmService {
  private apiUrl = 'http://localhost:8080/api/crm';

  constructor(private http: HttpClient) { }

  getCustomers(): Observable<CrmCustomer[]> {
    return this.http.get<CrmCustomer[]>(`${this.apiUrl}/customers`);
  }

  getCustomerById(id: number): Observable<CrmCustomer> {
    return this.http.get<CrmCustomer>(`${this.apiUrl}/customers/${id}`);
  }

  createCustomer(customer: CrmCustomer): Observable<CrmCustomer> {
    return this.http.post<CrmCustomer>(`${this.apiUrl}/customers`, customer);
  }

  updateCustomer(id: number, customer: CrmCustomer): Observable<CrmCustomer> {
    return this.http.put<CrmCustomer>(`${this.apiUrl}/customers/${id}`, customer);
  }

  deleteCustomer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/customers/${id}`);
  }
}
