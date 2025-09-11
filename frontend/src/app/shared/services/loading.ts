import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class LoadingService {
  private loadingSubject = new BehaviorSubject<boolean>(false);
  private loadingCount = 0;

  public loading$: Observable<boolean> = this.loadingSubject.asObservable();

  constructor() {}

  show(): void {
    this.loadingCount++;
    if (this.loadingCount === 1) {
      this.loadingSubject.next(true);
    }
  }

  hide(): void {
    this.loadingCount--;
    if (this.loadingCount <= 0) {
      this.loadingCount = 0;
      this.loadingSubject.next(false);
    }
  }

  isLoading(): boolean {
    return this.loadingSubject.value;
  }

  reset(): void {
    this.loadingCount = 0;
    this.loadingSubject.next(false);
  }

  // Méthodes utilitaires pour les opérations async
  async withLoading<T>(operation: () => Promise<T>): Promise<T> {
    this.show();
    try {
      const result = await operation();
      return result;
    } finally {
      this.hide();
    }
  }

  withLoadingObservable<T>(operation: () => Observable<T>): Observable<T> {
    this.show();
    return operation().pipe(
      tap(() => this.hide()),
      catchError((error) => {
        this.hide();
        throw error;
      })
    );
  }
}








