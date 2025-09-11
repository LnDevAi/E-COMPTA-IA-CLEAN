import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth';
import { Router } from '@angular/router';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Ajouter le token d'authentification si disponible
  const token = authService.getToken();
  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        // Token expiré ou invalide
        const refreshToken = authService.getRefreshToken();
        if (refreshToken) {
          // Tenter de rafraîchir le token
          return authService.refreshToken().pipe(
            switchMap((response) => {
              if (response.success && response.data) {
                // Retry la requête originale avec le nouveau token
                const newReq = req.clone({
                  setHeaders: {
                    Authorization: `Bearer ${response.data.accessToken}`
                  }
                });
                return next(newReq);
              } else {
                // Refresh échoué, rediriger vers login
                authService.logout().subscribe();
                router.navigate(['/login']);
                return throwError(() => error);
              }
            }),
            catchError(() => {
              // Erreur lors du refresh, rediriger vers login
              authService.logout().subscribe();
              router.navigate(['/login']);
              return throwError(() => error);
            })
          );
        } else {
          // Pas de refresh token, rediriger vers login
          authService.logout().subscribe();
          router.navigate(['/login']);
          return throwError(() => error);
        }
      }
      
      return throwError(() => error);
    })
  );
};
