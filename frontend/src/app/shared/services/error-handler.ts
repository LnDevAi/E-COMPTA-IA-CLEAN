import { ErrorHandler, Injectable, NgZone } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { NotificationService } from './notification';
import { AuthService } from './auth';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class GlobalErrorHandler implements ErrorHandler {
  constructor(
    private notificationService: NotificationService,
    private authService: AuthService,
    private router: Router,
    private ngZone: NgZone
  ) {}

  handleError(error: any): void {
    console.error('Global Error Handler:', error);

    // Gérer les erreurs HTTP
    if (error instanceof HttpErrorResponse) {
      this.handleHttpError(error);
      return;
    }

    // Gérer les erreurs JavaScript
    if (error instanceof Error) {
      this.handleJavaScriptError(error);
      return;
    }

    // Gérer les erreurs inconnues
    this.handleUnknownError(error);
  }

  private handleHttpError(error: HttpErrorResponse): void {
    let message = 'Une erreur est survenue';
    let shouldRedirect = false;

    switch (error.status) {
      case 400:
        message = this.extractValidationErrors(error) || 'Requête invalide';
        break;
      case 401:
        message = 'Non autorisé. Veuillez vous reconnecter.';
        shouldRedirect = true;
        this.authService.logout().subscribe();
        break;
      case 403:
        message = 'Accès refusé. Vous n\'avez pas les permissions nécessaires.';
        break;
      case 404:
        message = 'Ressource non trouvée';
        break;
      case 409:
        message = 'Conflit de données. La ressource existe déjà.';
        break;
      case 422:
        message = this.extractValidationErrors(error) || 'Données invalides';
        break;
      case 429:
        message = 'Trop de requêtes. Veuillez patienter.';
        break;
      case 500:
        message = 'Erreur interne du serveur';
        break;
      case 502:
        message = 'Service temporairement indisponible';
        break;
      case 503:
        message = 'Service en maintenance';
        break;
      default:
        message = `Erreur ${error.status}: ${error.statusText}`;
    }

    this.notificationService.showError(message);

    if (shouldRedirect) {
      this.ngZone.run(() => {
        this.router.navigate(['/login']);
      });
    }
  }

  private handleJavaScriptError(error: Error): void {
    let message = 'Une erreur JavaScript est survenue';

    // Gérer les erreurs spécifiques
    if (error.name === 'ChunkLoadError') {
      message = 'Erreur de chargement. Veuillez actualiser la page.';
    } else if (error.name === 'TypeError') {
      message = 'Erreur de type de données';
    } else if (error.name === 'ReferenceError') {
      message = 'Erreur de référence';
    } else if (error.message) {
      message = error.message;
    }

    this.notificationService.showError(message);
  }

  private handleUnknownError(error: any): void {
    const message = typeof error === 'string' ? error : 'Une erreur inattendue est survenue';
    this.notificationService.showError(message);
  }

  private extractValidationErrors(error: HttpErrorResponse): string | null {
    if (error.error?.errors && Array.isArray(error.error.errors)) {
      return error.error.errors.join(', ');
    }
    
    if (error.error?.message) {
      return error.error.message;
    }

    if (error.error?.error) {
      return error.error.error;
    }

    return null;
  }
}








