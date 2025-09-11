import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { ApiResponse, PaginatedResponse, PageRequest, SearchCriteria } from '../models/api-response';
import { AuthService } from './auth';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly BASE_URL = 'http://localhost:8080/api';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  private getHeaders(): HttpHeaders {
    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    const token = this.authService.getToken();
    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }

    return headers;
  }

  private handleError(error: any): Observable<never> {
    console.error('API Error:', error);
    return throwError(error);
  }

  // Generic CRUD operations
  get<T>(endpoint: string, params?: any): Observable<ApiResponse<T>> {
    let httpParams = new HttpParams();
    if (params) {
      Object.keys(params).forEach(key => {
        if (params[key] !== null && params[key] !== undefined) {
          httpParams = httpParams.set(key, params[key].toString());
        }
      });
    }

    return this.http.get<ApiResponse<T>>(`${this.BASE_URL}${endpoint}`, {
      headers: this.getHeaders(),
      params: httpParams
    }).pipe(
      catchError(this.handleError)
    );
  }

  post<T>(endpoint: string, data: any): Observable<ApiResponse<T>> {
    return this.http.post<ApiResponse<T>>(`${this.BASE_URL}${endpoint}`, data, {
      headers: this.getHeaders()
    }).pipe(
      catchError(this.handleError)
    );
  }

  put<T>(endpoint: string, data: any): Observable<ApiResponse<T>> {
    return this.http.put<ApiResponse<T>>(`${this.BASE_URL}${endpoint}`, data, {
      headers: this.getHeaders()
    }).pipe(
      catchError(this.handleError)
    );
  }

  delete<T>(endpoint: string): Observable<ApiResponse<T>> {
    return this.http.delete<ApiResponse<T>>(`${this.BASE_URL}${endpoint}`, {
      headers: this.getHeaders()
    }).pipe(
      catchError(this.handleError)
    );
  }

  // Paginated requests
  getPaginated<T>(endpoint: string, pageRequest: PageRequest, searchCriteria?: SearchCriteria): Observable<ApiResponse<PaginatedResponse<T>>> {
    let params = new HttpParams()
      .set('page', pageRequest.page.toString())
      .set('size', pageRequest.size.toString());

    if (pageRequest.sort) {
      pageRequest.sort.forEach(sort => {
        params = params.append('sort', sort);
      });
    }

    if (searchCriteria) {
      if (searchCriteria.search) {
        params = params.set('search', searchCriteria.search);
      }
      if (searchCriteria.dateFrom) {
        params = params.set('dateFrom', searchCriteria.dateFrom.toISOString());
      }
      if (searchCriteria.dateTo) {
        params = params.set('dateTo', searchCriteria.dateTo.toISOString());
      }
      if (searchCriteria.filters) {
        Object.keys(searchCriteria.filters).forEach(key => {
          if (searchCriteria.filters![key] !== null && searchCriteria.filters![key] !== undefined) {
            params = params.set(key, searchCriteria.filters![key].toString());
          }
        });
      }
    }

    return this.http.get<ApiResponse<PaginatedResponse<T>>>(`${this.BASE_URL}${endpoint}`, {
      headers: this.getHeaders(),
      params
    }).pipe(
      catchError(this.handleError)
    );
  }

  // File upload
  uploadFile(endpoint: string, file: File, additionalData?: any): Observable<ApiResponse<any>> {
    const formData = new FormData();
    formData.append('file', file);

    if (additionalData) {
      Object.keys(additionalData).forEach(key => {
        formData.append(key, additionalData[key]);
      });
    }

    const headers = new HttpHeaders();
    const token = this.authService.getToken();
    if (token) {
      headers.set('Authorization', `Bearer ${token}`);
    }

    return this.http.post<ApiResponse<any>>(`${this.BASE_URL}${endpoint}`, formData, {
      headers
    }).pipe(
      catchError(this.handleError)
    );
  }

  // Download file
  downloadFile(endpoint: string, filename?: string): Observable<Blob> {
    return this.http.get(`${this.BASE_URL}${endpoint}`, {
      headers: this.getHeaders(),
      responseType: 'blob'
    }).pipe(
      map(response => {
        if (filename) {
          const url = window.URL.createObjectURL(response);
          const link = document.createElement('a');
          link.href = url;
          link.download = filename;
          link.click();
          window.URL.revokeObjectURL(url);
        }
        return response;
      }),
      catchError(this.handleError)
    );
  }
}
