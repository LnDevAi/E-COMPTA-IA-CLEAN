import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SycebnlOrganization } from '../models/sycebnl-organization.model';

@Injectable({
  providedIn: 'root'
})
export class SycebnlService {
  private apiUrl = 'http://localhost:8080/api/sycebnl';

  constructor(private http: HttpClient) { }

  getOrganizations(): Observable<SycebnlOrganization[]> {
    return this.http.get<SycebnlOrganization[]>(`${this.apiUrl}/organizations`);
  }

  getOrganizationById(id: number): Observable<SycebnlOrganization> {
    return this.http.get<SycebnlOrganization>(`${this.apiUrl}/organizations/${id}`);
  }

  createOrganization(organization: SycebnlOrganization): Observable<SycebnlOrganization> {
    return this.http.post<SycebnlOrganization>(`${this.apiUrl}/organizations`, organization);
  }

  updateOrganization(id: number, organization: SycebnlOrganization): Observable<SycebnlOrganization> {
    return this.http.put<SycebnlOrganization>(`${this.apiUrl}/organizations/${id}`, organization);
  }

  deleteOrganization(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/organizations/${id}`);
  }
}
