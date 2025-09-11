import { Injectable } from '@angular/core';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';
import { BehaviorSubject, Observable } from 'rxjs';
import { Notification as NotificationModel } from '../models/notification';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notificationsSubject = new BehaviorSubject<NotificationModel[]>([]);
  public notifications$ = this.notificationsSubject.asObservable();

  constructor(private snackBar: MatSnackBar) {}

  // Notifications toast (temporaires)
  showSuccess(message: string, action: string = 'OK', duration: number = 3000): void {
    this.showSnackBar(message, 'success', action, duration);
  }

  showError(message: string, action: string = 'Fermer', duration: number = 5000): void {
    this.showSnackBar(message, 'error', action, duration);
  }

  showWarning(message: string, action: string = 'OK', duration: number = 4000): void {
    this.showSnackBar(message, 'warning', action, duration);
  }

  showInfo(message: string, action: string = 'OK', duration: number = 3000): void {
    this.showSnackBar(message, 'info', action, duration);
  }

  private showSnackBar(message: string, type: 'success' | 'error' | 'warning' | 'info', action: string, duration: number): void {
    const config: MatSnackBarConfig = {
      duration: duration,
      horizontalPosition: 'right',
      verticalPosition: 'top',
      panelClass: [`snackbar-${type}`]
    };

    this.snackBar.open(message, action, config);
  }

  // Notifications persistantes (dans la liste)
  addNotification(notification: Omit<NotificationModel, 'id' | 'createdAt' | 'isRead'>): void {
    const newNotification: NotificationModel = {
      id: this.generateId(),
      ...notification,
      createdAt: new Date(),
      isRead: false
    };

    const currentNotifications = this.notificationsSubject.value;
    this.notificationsSubject.next([newNotification, ...currentNotifications]);
  }

  markAsRead(notificationId: string): void {
    const notifications = this.notificationsSubject.value;
    const updatedNotifications = notifications.map(notification =>
      notification.id === notificationId
        ? { ...notification, isRead: true }
        : notification
    );
    this.notificationsSubject.next(updatedNotifications);
  }

  markAllAsRead(): void {
    const notifications = this.notificationsSubject.value;
    const updatedNotifications = notifications.map(notification =>
      ({ ...notification, isRead: true })
    );
    this.notificationsSubject.next(updatedNotifications);
  }

  removeNotification(notificationId: string): void {
    const notifications = this.notificationsSubject.value;
    const updatedNotifications = notifications.filter(notification => notification.id !== notificationId);
    this.notificationsSubject.next(updatedNotifications);
  }

  clearAllNotifications(): void {
    this.notificationsSubject.next([]);
  }

  getUnreadCount(): Observable<number> {
    return new Observable(observer => {
      this.notifications$.subscribe(notifications => {
        const unreadCount = notifications.filter(n => !n.isRead).length;
        observer.next(unreadCount);
      });
    });
  }

  // Notifications système automatiques
  notifyApiError(error: any): void {
    let message = 'Une erreur est survenue';
    
    if (error?.error?.message) {
      message = error.error.message;
    } else if (error?.message) {
      message = error.message;
    } else if (typeof error === 'string') {
      message = error;
    }

    this.showError(message);
  }

  notifyApiSuccess(message: string = 'Opération réussie'): void {
    this.showSuccess(message);
  }

  notifyValidationError(errors: string[]): void {
    const message = errors.length === 1 
      ? errors[0] 
      : `Plusieurs erreurs de validation:\n${errors.join('\n')}`;
    this.showError(message);
  }

  private generateId(): string {
    return Date.now().toString(36) + Math.random().toString(36).substr(2);
  }
}
