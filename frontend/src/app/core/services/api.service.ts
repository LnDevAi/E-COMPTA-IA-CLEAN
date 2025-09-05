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
}
