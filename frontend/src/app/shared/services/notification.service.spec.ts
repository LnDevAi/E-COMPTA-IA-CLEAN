import { TestBed } from '@angular/core/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { NotificationService } from './notification';

describe('NotificationService', () => {
  let service: NotificationService;
  let snackBarSpy: jasmine.SpyObj<MatSnackBar>;

  beforeEach(() => {
    const spy = jasmine.createSpyObj('MatSnackBar', ['open']);

    TestBed.configureTestingModule({
      providers: [
        NotificationService,
        { provide: MatSnackBar, useValue: spy }
      ]
    });
    service = TestBed.inject(NotificationService);
    snackBarSpy = TestBed.inject(MatSnackBar) as jasmine.SpyObj<MatSnackBar>;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('showSuccess', () => {
    it('should show success notification', () => {
      const message = 'Operation successful';
      service.showSuccess(message);

      expect(snackBarSpy.open).toHaveBeenCalledWith(
        message,
        'Fermer',
        jasmine.objectContaining({
          duration: 3000,
          panelClass: ['success-snackbar']
        })
      );
    });
  });

  describe('showError', () => {
    it('should show error notification', () => {
      const message = 'Operation failed';
      service.showError(message);

      expect(snackBarSpy.open).toHaveBeenCalledWith(
        message,
        'Fermer',
        jasmine.objectContaining({
          duration: 5000,
          panelClass: ['error-snackbar']
        })
      );
    });
  });

  describe('showWarning', () => {
    it('should show warning notification', () => {
      const message = 'Warning message';
      service.showWarning(message);

      expect(snackBarSpy.open).toHaveBeenCalledWith(
        message,
        'Fermer',
        jasmine.objectContaining({
          duration: 4000,
          panelClass: ['warning-snackbar']
        })
      );
    });
  });

  describe('showInfo', () => {
    it('should show info notification', () => {
      const message = 'Information message';
      service.showInfo(message);

      expect(snackBarSpy.open).toHaveBeenCalledWith(
        message,
        'Fermer',
        jasmine.objectContaining({
          duration: 3000,
          panelClass: ['info-snackbar']
        })
      );
    });
  });

  describe('addNotification', () => {
    it('should add notification to list', () => {
      const notification = {
        id: '1',
        title: 'Test Notification',
        message: 'Test message',
        type: 'success' as const,
        timestamp: new Date(),
        isRead: false
      };

      service.addNotification(notification);

      service.getNotifications().subscribe(notifications => {
        expect(notifications).toContain(notification);
      });
    });
  });

  describe('markAsRead', () => {
    it('should mark notification as read', () => {
      const notification = {
        id: '1',
        title: 'Test Notification',
        message: 'Test message',
        type: 'success' as const,
        timestamp: new Date(),
        isRead: false
      };

      service.addNotification(notification);
      service.markAsRead('1');

      service.getNotifications().subscribe(notifications => {
        const updatedNotification = notifications.find(n => n.id === '1');
        expect(updatedNotification?.isRead).toBe(true);
      });
    });
  });

  describe('clearAll', () => {
    it('should clear all notifications', () => {
      const notification = {
        id: '1',
        title: 'Test Notification',
        message: 'Test message',
        type: 'success' as const,
        timestamp: new Date(),
        isRead: false
      };

      service.addNotification(notification);
      service.clearAll();

      service.getNotifications().subscribe(notifications => {
        expect(notifications.length).toBe(0);
      });
    });
  });
});








