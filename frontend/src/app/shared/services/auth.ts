import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { map, tap, catchError } from 'rxjs/operators';
import {
  LoginRequest,
  LoginResponse,
  RefreshTokenRequest,
  RegisterRequest,
  RegisterResponse,
  PasswordResetRequest,
  PasswordResetResponse,
  ChangePasswordRequest
} from '../models/auth';
import { User } from '../models/user';
import { ApiResponse } from '../models/api-response';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8080/api/auth';
  private readonly TOKEN_KEY = 'access_token';
  private readonly REFRESH_TOKEN_KEY = 'refresh_token';
  private readonly USER_KEY = 'current_user';

  private currentUserSubject = new BehaviorSubject<User | null>(this.getCurrentUser());
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  login(credentials: LoginRequest): Observable<ApiResponse<LoginResponse>> {
    return this.http.post<ApiResponse<LoginResponse>>(`${this.API_URL}/login`, credentials)
      .pipe(
        tap(response => {
          if (response.success && response.data) {
            this.setSession(response.data);
          }
        })
      );
  }

  register(userData: RegisterRequest): Observable<ApiResponse<RegisterResponse>> {
    return this.http.post<ApiResponse<RegisterResponse>>(`${this.API_URL}/register`, userData);
  }

  logout(): Observable<any> {
    return this.http.post(`${this.API_URL}/logout`, {})
      .pipe(
        tap(() => this.clearSession()),
        catchError(() => {
          this.clearSession();
          return of(null);
        })
      );
  }

  refreshToken(): Observable<ApiResponse<LoginResponse>> {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) {
      this.clearSession();
      return of({ success: false, message: 'No refresh token', status: 401, timestamp: new Date() });
    }

    const request: RefreshTokenRequest = { refreshToken };
    return this.http.post<ApiResponse<LoginResponse>>(`${this.API_URL}/refresh`, request)
      .pipe(
        tap(response => {
          if (response.success && response.data) {
            this.setSession(response.data);
          } else {
            this.clearSession();
          }
        }),
        catchError(() => {
          this.clearSession();
          return of({ success: false, message: 'Token refresh failed', status: 401, timestamp: new Date() });
        })
      );
  }

  requestPasswordReset(request: PasswordResetRequest): Observable<ApiResponse<PasswordResetResponse>> {
    return this.http.post<ApiResponse<PasswordResetResponse>>(`${this.API_URL}/forgot-password`, request);
  }

  changePassword(request: ChangePasswordRequest): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.API_URL}/change-password`, request);
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token) return false;
    
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.exp > Date.now() / 1000;
    } catch {
      return false;
    }
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(this.REFRESH_TOKEN_KEY);
  }

  getCurrentUser(): User | null {
    const userStr = localStorage.getItem(this.USER_KEY);
    return userStr ? JSON.parse(userStr) : null;
  }

  hasRole(role: string): boolean {
    const user = this.getCurrentUser();
    return user?.roles?.some(r => r.name === role) || false;
  }

  hasPermission(permission: string): boolean {
    const user = this.getCurrentUser();
    return user?.roles?.some(role => 
      role.permissions?.some(p => p.name === permission)
    ) || false;
  }

  private setSession(authResult: LoginResponse): void {
    localStorage.setItem(this.TOKEN_KEY, authResult.accessToken);
    localStorage.setItem(this.REFRESH_TOKEN_KEY, authResult.refreshToken);
    localStorage.setItem(this.USER_KEY, JSON.stringify(authResult.user));
    this.currentUserSubject.next(authResult.user);
  }

  private clearSession(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.currentUserSubject.next(null);
  }
}
