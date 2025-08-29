import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';
import { Router } from '@angular/router';

import { 
  User, 
  UserProfile, 
  LoginRequest, 
  LoginResponse, 
  RegisterRequest, 
  PasswordChangeRequest 
} from '../../shared/interfaces/user.interface';
import { AuthState, JwtPayload, TokenInfo } from '../../shared/models/auth.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = `${environment.apiUrl}/api/auth`;
  private readonly TOKEN_KEY = 'ecomptaia_token';
  private readonly REFRESH_TOKEN_KEY = 'ecomptaia_refresh_token';
  private readonly USER_KEY = 'ecomptaia_user';

  private authStateSubject = new BehaviorSubject<AuthState>({
    user: null,
    token: null,
    isAuthenticated: false,
    isLoading: false,
    error: null,
    refreshToken: null,
    expiresAt: null
  });

  public authState$ = this.authStateSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    this.initializeAuth();
  }

  /**
   * Initialiser l'état d'authentification depuis le localStorage
   */
  private initializeAuth(): void {
    const token = this.getStoredToken();
    const user = this.getStoredUser();
    const refreshToken = this.getStoredRefreshToken();

    if (token && user && !this.isTokenExpired(token)) {
      this.authStateSubject.next({
        user,
        token,
        isAuthenticated: true,
        isLoading: false,
        error: null,
        refreshToken,
        expiresAt: this.getTokenExpiration(token)
      });
    }
  }

  /**
   * Connexion utilisateur
   */
  login(credentials: LoginRequest): Observable<boolean> {
    this.setLoading(true);
    this.clearError();

    return this.http.post<LoginResponse>(`${this.API_URL}/login`, credentials).pipe(
      tap(response => {
        this.handleLoginSuccess(response);
      }),
      map(() => true),
      catchError(error => {
        this.handleLoginError(error);
        return throwError(() => error);
      })
    );
  }

  /**
   * Inscription utilisateur
   */
  register(userData: RegisterRequest): Observable<boolean> {
    this.setLoading(true);
    this.clearError();

    return this.http.post<LoginResponse>(`${this.API_URL}/register`, userData).pipe(
      tap(response => {
        this.handleLoginSuccess(response);
      }),
      map(() => true),
      catchError(error => {
        this.handleLoginError(error);
        return throwError(() => error);
      })
    );
  }

  /**
   * Déconnexion
   */
  logout(): void {
    this.clearAuthData();
    this.router.navigate(['/auth/login']);
  }

  /**
   * Rafraîchir le token
   */
  refreshToken(): Observable<boolean> {
    const refreshToken = this.getStoredRefreshToken();
    
    if (!refreshToken) {
      this.logout();
      return throwError(() => new Error('No refresh token available'));
    }

    return this.http.post<LoginResponse>(`${this.API_URL}/refresh`, { refreshToken }).pipe(
      tap(response => {
        this.handleLoginSuccess(response);
      }),
      map(() => true),
      catchError(error => {
        this.logout();
        return throwError(() => error);
      })
    );
  }

  /**
   * Changer le mot de passe
   */
  changePassword(passwordData: PasswordChangeRequest): Observable<boolean> {
    return this.http.post<{ message: string }>(`${this.API_URL}/change-password`, passwordData).pipe(
      map(() => true),
      catchError(error => {
        this.handleError(error);
        return throwError(() => error);
      })
    );
  }

  /**
   * Vérifier si l'utilisateur a une permission
   */
  hasPermission(permission: string): boolean {
    const user = this.getCurrentUser();
    return user?.permissions?.includes(permission) || false;
  }

  /**
   * Vérifier si l'utilisateur a un rôle
   */
  hasRole(role: string): boolean {
    const user = this.getCurrentUser();
    return user?.roles?.includes(role) || false;
  }

  /**
   * Obtenir l'utilisateur actuel
   */
  getCurrentUser(): UserProfile | null {
    return this.authStateSubject.value.user;
  }

  /**
   * Obtenir le token actuel
   */
  getCurrentToken(): string | null {
    return this.authStateSubject.value.token;
  }

  /**
   * Vérifier si l'utilisateur est authentifié
   */
  isAuthenticated(): boolean {
    return this.authStateSubject.value.isAuthenticated;
  }

  /**
   * Obtenir les headers d'autorisation
   */
  getAuthHeaders(): HttpHeaders {
    const token = this.getCurrentToken();
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  // Méthodes privées

  private handleLoginSuccess(response: LoginResponse): void {
    const user: UserProfile = {
      ...response.user,
      fullName: `${response.user.firstName} ${response.user.lastName}`
    };

    this.storeAuthData(response.token, user, response.refreshToken);
    
    this.authStateSubject.next({
      user,
      token: response.token,
      isAuthenticated: true,
      isLoading: false,
      error: null,
      refreshToken: response.refreshToken || null,
      expiresAt: this.getTokenExpiration(response.token)
    });
  }

  private handleLoginError(error: HttpErrorResponse): void {
    let errorMessage = 'Une erreur est survenue lors de la connexion';
    
    if (error.error?.message) {
      errorMessage = error.error.message;
    } else if (error.status === 401) {
      errorMessage = 'Email ou mot de passe incorrect';
    } else if (error.status === 0) {
      errorMessage = 'Impossible de se connecter au serveur';
    }

    this.authStateSubject.next({
      ...this.authStateSubject.value,
      isLoading: false,
      error: errorMessage
    });
  }

  private handleError(error: HttpErrorResponse): void {
    let errorMessage = 'Une erreur est survenue';
    
    if (error.error?.message) {
      errorMessage = error.error.message;
    }

    this.authStateSubject.next({
      ...this.authStateSubject.value,
      error: errorMessage
    });
  }

  private setLoading(isLoading: boolean): void {
    this.authStateSubject.next({
      ...this.authStateSubject.value,
      isLoading
    });
  }

  private clearError(): void {
    this.authStateSubject.next({
      ...this.authStateSubject.value,
      error: null
    });
  }

  private storeAuthData(token: string, user: UserProfile, refreshToken?: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    if (refreshToken) {
      localStorage.setItem(this.REFRESH_TOKEN_KEY, refreshToken);
    }
  }

  private clearAuthData(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
    
    this.authStateSubject.next({
      user: null,
      token: null,
      isAuthenticated: false,
      isLoading: false,
      error: null,
      refreshToken: null,
      expiresAt: null
    });
  }

  private getStoredToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  private getStoredUser(): UserProfile | null {
    const userStr = localStorage.getItem(this.USER_KEY);
    return userStr ? JSON.parse(userStr) : null;
  }

  private getStoredRefreshToken(): string | null {
    return localStorage.getItem(this.REFRESH_TOKEN_KEY);
  }

  private isTokenExpired(token: string): boolean {
    try {
      const payload = this.decodeToken(token);
      return Date.now() >= payload.exp * 1000;
    } catch {
      return true;
    }
  }

  private getTokenExpiration(token: string): number {
    try {
      const payload = this.decodeToken(token);
      return payload.exp * 1000;
    } catch {
      return 0;
    }
  }

  private decodeToken(token: string): JwtPayload {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
    return JSON.parse(jsonPayload);
  }
}


