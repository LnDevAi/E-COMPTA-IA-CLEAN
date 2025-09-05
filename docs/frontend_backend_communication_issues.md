# 🔧 Problèmes de Communication Frontend-Backend
## Diagnostic et Solutions pour E-COMPTA-IA

---

## 🚨 PROBLÈMES COURANTS ET SOLUTIONS

### 1. **Problèmes CORS (Cross-Origin Resource Sharing)**

#### Symptômes :
```
Access to XMLHttpRequest at 'http://localhost:8080/api/crm/customers' 
from origin 'http://localhost:4200' has been blocked by CORS policy
```

#### Solutions :

**Backend - Configuration CORS Complète :**
```java
@Configuration
@EnableWebSecurity
public class CorsConfig {
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Origines autorisées (IMPORTANT: adapter selon votre environnement)
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:4200",           // Développement Angular
            "http://localhost:3000",           // React dev (si applicable)
            "https://app.ecomptaia.com",       // Production
            "https://*.ecomptaia.com"          // Sous-domaines
        ));
        
        // Méthodes HTTP autorisées
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"
        ));
        
        // Headers autorisés
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers",
            "X-Company-Id",        // Header custom E-COMPTA-IA
            "X-User-Role"          // Header custom pour rôles
        ));
        
        // Headers exposés au frontend
        configuration.setExposedHeaders(Arrays.asList(
            "X-Total-Count",
            "X-Current-Page",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"
        ));
        
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // Cache preflight 1h
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
        FilterRegistrationBean<CorsFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CorsFilter(corsConfigurationSource()));
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}
```

**Frontend - Service HTTP avec Headers :**
```typescript
// http-interceptor.service.ts
@Injectable()
export class HttpInterceptorService implements HttpInterceptor {
  
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Ajout automatique des headers nécessaires
    const modifiedReq = req.clone({
      setHeaders: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'X-Requested-With': 'XMLHttpRequest'
      },
      // Support des credentials pour CORS
      withCredentials: true
    });
    
    return next.handle(modifiedReq);
  }
}
```

---

### 2. **Problèmes d'Authentification JWT**

#### Symptômes :
```
401 Unauthorized
403 Forbidden  
Token expired
```

#### Solutions :

**Service d'Authentification Frontend :**
```typescript
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly TOKEN_KEY = 'ecomptaia_token';
  private readonly REFRESH_TOKEN_KEY = 'ecomptaia_refresh_token';
  private tokenSubject = new BehaviorSubject<string | null>(null);
  
  constructor(private http: HttpClient, private router: Router) {
    this.loadTokenFromStorage();
  }
  
  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/api/auth/login', credentials)
      .pipe(
        tap(response => {
          this.storeTokens(response.accessToken, response.refreshToken);
          this.tokenSubject.next(response.accessToken);
        }),
        catchError(this.handleAuthError.bind(this))
      );
  }
  
  refreshToken(): Observable<AuthResponse> {
    const refreshToken = localStorage.getItem(this.REFRESH_TOKEN_KEY);
    
    if (!refreshToken) {
      this.logout();
      return throwError('No refresh token available');
    }
    
    return this.http.post<AuthResponse>('/api/auth/refresh', { refreshToken })
      .pipe(
        tap(response => {
          this.storeTokens(response.accessToken, response.refreshToken);
          this.tokenSubject.next(response.accessToken);
        }),
        catchError(error => {
          this.logout();
          return throwError(error);
        })
      );
  }
  
  private handleAuthError(error: HttpErrorResponse): Observable<never> {
    if (error.status === 401) {
      // Token expiré, tentative de refresh
      return this.refreshToken().pipe(
        catchError(() => {
          this.logout();
          return throwError('Authentication failed');
        })
      );
    }
    return throwError(error);
  }
  
  private storeTokens(accessToken: string, refreshToken: string): void {
    localStorage.setItem(this.TOKEN_KEY, accessToken);
    localStorage.setItem(this.REFRESH_TOKEN_KEY, refreshToken);
  }
}
```

**Intercepteur JWT Automatique :**
```typescript
@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  
  constructor(private authService: AuthService) {}
  
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getToken();
    
    if (token && !this.isPublicUrl(req.url)) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }
    
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          // Token invalide, rediriger vers login
          this.authService.logout();
          return throwError(error);
        }
        return throwError(error);
      })
    );
  }
  
  private isPublicUrl(url: string): boolean {
    const publicUrls = ['/api/auth/login', '/api/auth/register', '/api/public'];
    return publicUrls.some(publicUrl => url.includes(publicUrl));
  }
}
```

---

### 3. **Problèmes de Sérialisation JSON**

#### Symptômes :
```
JSON parse error
Could not read document
400 Bad Request - Malformed JSON
```

#### Solutions :

**Backend - Configuration Jackson :**
```java
@Configuration
public class JacksonConfig {
    
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Configuration des dates
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        
        // Gestion des propriétés inconnues
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        // Gestion des valeurs null
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        
        // Support des BigDecimal
        mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
        
        return mapper;
    }
}
```

**Frontend - Service de Sérialisation :**
```typescript
export class SerializationService {
  
  static serializeForBackend(data: any): any {
    if (data === null || data === undefined) {
      return data;
    }
    
    if (data instanceof Date) {
      return data.toISOString();
    }
    
    if (Array.isArray(data)) {
      return data.map(item => this.serializeForBackend(item));
    }
    
    if (typeof data === 'object') {
      const serialized: any = {};
      for (const key in data) {
        if (data.hasOwnProperty(key)) {
          // Convertir les dates string en format ISO
          if (key.includes('Date') || key.includes('At')) {
            const value = data[key];
            if (typeof value === 'string' && value.length > 0) {
              serialized[key] = new Date(value).toISOString();
            } else if (value instanceof Date) {
              serialized[key] = value.toISOString();
            } else {
              serialized[key] = value;
            }
          } else {
            serialized[key] = this.serializeForBackend(data[key]);
          }
        }
      }
      return serialized;
    }
    
    return data;
  }
  
  static deserializeFromBackend(data: any): any {
    if (data === null || data === undefined) {
      return data;
    }
    
    if (typeof data === 'string' && this.isISODateString(data)) {
      return new Date(data);
    }
    
    if (Array.isArray(data)) {
      return data.map(item => this.deserializeFromBackend(item));
    }
    
    if (typeof data === 'object') {
      const deserialized: any = {};
      for (const key in data) {
        if (data.hasOwnProperty(key)) {
          deserialized[key] = this.deserializeFromBackend(data[key]);
        }
      }
      return deserialized;
    }
    
    return data;
  }
  
  private static isISODateString(value: string): boolean {
    return /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}/.test(value);
  }
}
```

---

### 4. **Problèmes de Gestion d'Erreurs**

#### Symptômes :
```
Erreurs non gérées
Messages d'erreur non informatifs
Pas de feedback utilisateur
```

#### Solutions :

**Service de Gestion d'Erreurs Global :**
```typescript
@Injectable({
  providedIn: 'root'
})
export class ErrorHandlerService {
  
  constructor(
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}
  
  handleError(error: HttpErrorResponse): void {
    let errorMessage = 'Une erreur inattendue s\'est produite';
    
    if (error.error instanceof ErrorEvent) {
      // Erreur côté client
      errorMessage = `Erreur: ${error.error.message}`;
    } else {
      // Erreur côté serveur
      switch (error.status) {
        case 400:
          errorMessage = this.handleValidationError(error);
          break;
        case 401:
          errorMessage = 'Authentification requise';
          break;
        case 403:
          errorMessage = 'Accès non autorisé';
          break;
        case 404:
          errorMessage = 'Ressource non trouvée';
          break;
        case 409:
          errorMessage = 'Conflit avec une ressource existante';
          break;
        case 422:
          errorMessage = this.handleBusinessError(error);
          break;
        case 500:
          errorMessage = 'Erreur interne du serveur';
          break;
        case 503:
          errorMessage = 'Service temporairement indisponible';
          break;
        default:
          errorMessage = `Erreur ${error.status}: ${error.message}`;
      }
    }
    
    this.showError(errorMessage);
    console.error('Erreur HTTP complète:', error);
  }
  
  private handleValidationError(error: HttpErrorResponse): string {
    if (error.error?.validationErrors) {
      const validationErrors = error.error.validationErrors;
      const messages = Object.keys(validationErrors)
        .map(field => `${field}: ${validationErrors[field]}`)
        .join('\n');
      return `Erreurs de validation:\n${messages}`;
    }
    return error.error?.message || 'Données invalides';
  }
  
  private handleBusinessError(error: HttpErrorResponse): string {
    return error.error?.message || 'Erreur métier';
  }
  
  private showError(message: string): void {
    if (message.includes('\n')) {
      // Erreurs multiples - afficher dans un dialog
      this.dialog.open(ErrorDialogComponent, {
        data: { message },
        width: '400px'
      });
    } else {
      // Erreur simple - afficher dans une snackbar
      this.snackBar.open(message, 'Fermer', {
        duration: 5000,
        panelClass: ['error-snackbar']
      });
    }
  }
}
```

**Intercepteur de Gestion d'Erreurs :**
```typescript
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  
  constructor(private errorHandler: ErrorHandlerService) {}
  
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorHandler.handleError(error);
        return throwError(error);
      })
    );
  }
}
```

---

### 5. **Problèmes de Proxy de Développement**

#### Symptômes :
```
Cannot GET /api/...
Proxy error
Connection refused
```

#### Solutions :

**Configuration proxy.conf.json :**
```json
{
  "/api/*": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug",
    "headers": {
      "Connection": "keep-alive"
    }
  },
  "/actuator/*": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  }
}
```

**Configuration angular.json :**
```json
{
  "serve": {
    "builder": "@angular-devkit/build-angular:dev-server",
    "options": {
      "proxyConfig": "proxy.conf.json",
      "host": "0.0.0.0",
      "port": 4200
    }
  }
}
```

**Service de Configuration d'Environnement :**
```typescript
export const environment = {
  production: false,
  apiUrl: '', // Vide pour utiliser le proxy en dev
  apiTimeout: 30000,
  retryAttempts: 3
};

@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  
  getApiUrl(): string {
    if (environment.production) {
      return 'https://api.ecomptaia.com';
    }
    return ''; // Proxy en développement
  }
  
  getFullApiUrl(endpoint: string): string {
    const baseUrl = this.getApiUrl();
    return `${baseUrl}/api${endpoint}`;
  }
}
```

---

### 6. **Problèmes de Timeout et Performance**

#### Symptômes :
```
TimeoutError
Request timeout
Slow network responses
```

#### Solutions :

**Service HTTP avec Retry et Timeout :**
```typescript
@Injectable({
  providedIn: 'root'
})
export class ApiService {
  
  constructor(private http: HttpClient) {}
  
  get<T>(url: string, options?: any): Observable<T> {
    return this.http.get<T>(url, {
      ...options,
      timeout: 30000 // 30 secondes
    }).pipe(
      retry(3),
      timeout(30000),
      catchError(this.handleTimeoutError.bind(this))
    );
  }
  
  post<T>(url: string, body: any, options?: any): Observable<T> {
    return this.http.post<T>(url, body, {
      ...options,
      timeout: 45000 // 45 secondes pour POST
    }).pipe(
      retry(2),
      timeout(45000),
      catchError(this.handleTimeoutError.bind(this))
    );
  }
  
  private handleTimeoutError(error: any): Observable<never> {
    if (error.name === 'TimeoutError') {
      console.error('Request timeout:', error);
      return throwError('La requête a pris trop de temps. Veuillez réessayer.');
    }
    return throwError(error);
  }
}
```

**Backend - Configuration Timeout :**
```java
@Configuration
public class WebConfig {
    
    @Bean
    public TomcatServletWebServerFactory tomcatFactory() {
        return new TomcatServletWebServerFactory() {
            @Override
            protected void customizeConnector(Connector connector) {
                super.customizeConnector(connector);
                
                // Timeout de connexion
                connector.setProperty("connectionTimeout", "30000");
                // Timeout de keep-alive
                connector.setProperty("keepAliveTimeout", "60000");
                // Nombre max de threads
                connector.setProperty("maxThreads", "200");
            }
        };
    }
}
```

---

### 7. **Problèmes de Pagination et Tri**

#### Symptômes :
```
Page vide
Tri incorrect
Paramètres ignorés
```

#### Solutions :

**Service de Pagination Générique :**
```typescript
export interface PageRequest {
  page: number;
  size: number;
  sort?: string;
  direction?: 'asc' | 'desc';
  filter?: any;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

@Injectable()
export class PaginationService {
  
  buildPageParams(pageRequest: PageRequest): HttpParams {
    let params = new HttpParams()
      .set('page', pageRequest.page.toString())
      .set('size', pageRequest.size.toString());
    
    if (pageRequest.sort) {
      const sortParam = pageRequest.direction 
        ? `${pageRequest.sort},${pageRequest.direction}`
        : pageRequest.sort;
      params = params.set('sort', sortParam);
    }
    
    if (pageRequest.filter) {
      Object.keys(pageRequest.filter).forEach(key => {
        const value = pageRequest.filter[key];
        if (value !== null && value !== undefined && value !== '') {
          params = params.set(key, value.toString());
        }
      });
    }
    
    return params;
  }
  
  getPagedData<T>(
    endpoint: string, 
    pageRequest: PageRequest
  ): Observable<PageResponse<T>> {
    const params = this.buildPageParams(pageRequest);
    
    return this.http.get<PageResponse<T>>(endpoint, { params })
      .pipe(
        map(response => ({
          ...response,
          // Assurer la cohérence des types
          content: response.content || [],
          totalElements: Number(response.totalElements) || 0,
          totalPages: Number(response.totalPages) || 0
        }))
      );
  }
}
```

---

## 🔍 OUTILS DE DIAGNOSTIC

### 1. **Logger de Requêtes HTTP**

```typescript
@Injectable()
export class HttpLoggerInterceptor implements HttpInterceptor {
  
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const startTime = Date.now();
    
    console.group(`🔍 HTTP ${req.method} ${req.url}`);
    console.log('Headers:', req.headers.keys().map(key => `${key}: ${req.headers.get(key)}`));
    if (req.body) {
      console.log('Body:', req.body);
    }
    
    return next.handle(req).pipe(
      tap(
        event => {
          if (event instanceof HttpResponse) {
            const duration = Date.now() - startTime;
            console.log(`✅ Response (${duration}ms):`, event.status, event.body);
            console.groupEnd();
          }
        },
        error => {
          const duration = Date.now() - startTime;
          console.error(`❌ Error (${duration}ms):`, error);
          console.groupEnd();
        }
      )
    );
  }
}
```

### 2. **Service de Health Check**

```typescript
@Injectable({
  providedIn: 'root'
})
export class HealthCheckService {
  
  constructor(private http: HttpClient) {}
  
  checkBackendHealth(): Observable<HealthStatus> {
    return this.http.get<HealthStatus>('/actuator/health')
      .pipe(
        timeout(5000),
        map(response => ({
          status: 'UP',
          details: response
        })),
        catchError(error => of({
          status: 'DOWN',
          error: error.message
        }))
      );
  }
  
  checkApiEndpoints(): Observable<EndpointStatus[]> {
    const endpoints = [
      '/api/crm/customers',
      '/api/marketing/campaigns',
      '/api/auth/profile'
    ];
    
    return forkJoin(
      endpoints.map(endpoint => 
        this.http.head(endpoint).pipe(
          map(() => ({ endpoint, status: 'UP' })),
          catchError(error => of({ 
            endpoint, 
            status: 'DOWN', 
            error: error.status 
          }))
        )
      )
    );
  }
}
```

---

## ⚡ OPTIMISATIONS RECOMMANDÉES

### 1. **Cache HTTP Client-Side**

```typescript
@Injectable()
export class CacheInterceptor implements HttpInterceptor {
  
  private cache = new Map<string, HttpResponse<any>>();
  
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Cache seulement les GET
    if (req.method !== 'GET') {
      return next.handle(req);
    }
    
    const cachedResponse = this.cache.get(req.url);
    if (cachedResponse) {
      return of(cachedResponse);
    }
    
    return next.handle(req).pipe(
      tap(event => {
        if (event instanceof HttpResponse) {
          this.cache.set(req.url, event);
          
          // Nettoyer le cache après 5 minutes
          setTimeout(() => {
            this.cache.delete(req.url);
          }, 5 * 60 * 1000);
        }
      })
    );
  }
}
```

### 2. **Optimisation des Requêtes**

```typescript
@Injectable()
export class OptimizedApiService {
  
  // Debounce pour les recherches
  searchCustomers(query: string): Observable<CrmCustomer[]> {
    return of(query).pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(q => 
        q.length > 2 
          ? this.http.get<CrmCustomer[]>(`/api/crm/customers/search?q=${q}`)
          : of([])
      )
    );
  }
  
  // Chargement lazy des données
  loadCustomerDetails(id: string): Observable<CrmCustomer> {
    return this.http.get<CrmCustomer>(`/api/crm/customers/${id}`)
      .pipe(
        shareReplay(1), // Partager le résultat
        catchError(error => {
          console.error('Erreur lors du chargement du client:', error);
          return throwError(error);
        })
      );
  }
}
```

---

Cette documentation devrait vous aider à résoudre la plupart des problèmes de communication frontend-backend. Quel problème spécifique rencontrez-vous le plus souvent ?