import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of } from 'rxjs';

import { DashboardComponent } from './dashboard';
import { AuthService } from '../../shared/services/auth';
import { NotificationService } from '../../shared/services/notification';
import { LoadingService } from '../../shared/services/loading';
import { User } from '../../shared/models/user';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let authService: jasmine.SpyObj<AuthService>;
  let notificationService: jasmine.SpyObj<NotificationService>;
  let loadingService: jasmine.SpyObj<LoadingService>;

  const mockUser: User = {
    id: 1,
    username: 'testuser',
    email: 'test@example.com',
    firstName: 'Test',
    lastName: 'User',
    roles: ['USER'],
    isActive: true,
    createdAt: new Date(),
    updatedAt: new Date()
  };

  beforeEach(async () => {
    const authSpy = jasmine.createSpyObj('AuthService', ['currentUser$', 'logout']);
    const notificationSpy = jasmine.createSpyObj('NotificationService', ['showSuccess', 'showError']);
    const loadingSpy = jasmine.createSpyObj('LoadingService', ['show', 'hide']);

    await TestBed.configureTestingModule({
      imports: [
        DashboardComponent,
        HttpClientTestingModule,
        MatSnackBarModule,
        NoopAnimationsModule
      ],
      providers: [
        { provide: AuthService, useValue: authSpy },
        { provide: NotificationService, useValue: notificationSpy },
        { provide: LoadingService, useValue: loadingSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    notificationService = TestBed.inject(NotificationService) as jasmine.SpyObj<NotificationService>;
    loadingService = TestBed.inject(LoadingService) as jasmine.SpyObj<LoadingService>;

    authService.currentUser$ = of(mockUser);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with user data', () => {
    fixture.detectChanges();
    expect(component.currentUser).toEqual(mockUser);
  });

  it('should load dashboard data on init', () => {
    spyOn(component, 'loadDashboardData');
    component.ngOnInit();
    expect(component.loadDashboardData).toHaveBeenCalled();
  });

  it('should format currency correctly', () => {
    const amount = 1234.56;
    const formatted = component.formatCurrency(amount);
    expect(formatted).toContain('1 234,56');
    expect(formatted).toContain('€');
  });

  it('should format numbers correctly', () => {
    const number = 1234;
    const formatted = component.formatNumber(number);
    expect(formatted).toBe('1 234');
  });

  it('should format percentages correctly', () => {
    const percentage = 12.5;
    const formatted = component.formatPercentage(percentage);
    expect(formatted).toBe('12,5 %');
  });

  it('should get status color correctly', () => {
    expect(component.getStatusColor('active')).toBe('#4caf50');
    expect(component.getStatusColor('inactive')).toBe('#f44336');
    expect(component.getStatusColor('pending')).toBe('#ff9800');
    expect(component.getStatusColor('unknown')).toBe('#666');
  });

  it('should get trend icon correctly', () => {
    expect(component.getTrendIcon('up')).toBe('trending_up');
    expect(component.getTrendIcon('down')).toBe('trending_down');
    expect(component.getTrendIcon('stable')).toBe('trending_flat');
    expect(component.getTrendIcon('unknown')).toBe('trending_flat');
  });

  it('should get trend color correctly', () => {
    expect(component.getTrendColor('up')).toBe('#4caf50');
    expect(component.getTrendColor('down')).toBe('#f44336');
    expect(component.getTrendColor('stable')).toBe('#666');
    expect(component.getTrendColor('unknown')).toBe('#666');
  });

  it('should handle refresh action', () => {
    spyOn(component, 'loadDashboardData');
    component.refreshDashboard();
    expect(component.loadDashboardData).toHaveBeenCalled();
    expect(notificationService.showSuccess).toHaveBeenCalledWith('Données actualisées');
  });

  it('should handle export action', () => {
    component.exportData('excel');
    expect(notificationService.showInfo).toHaveBeenCalledWith('Export Excel en cours...');
  });

  it('should handle logout', () => {
    authService.logout.and.returnValue(of({}));
    component.logout();
    expect(authService.logout).toHaveBeenCalled();
  });

  it('should calculate percentage change correctly', () => {
    const current = 100;
    const previous = 80;
    const change = component.calculatePercentageChange(current, previous);
    expect(change).toBe(25);
  });

  it('should handle zero previous value in percentage change', () => {
    const current = 100;
    const previous = 0;
    const change = component.calculatePercentageChange(current, previous);
    expect(change).toBe(0);
  });

  it('should get quick action icon correctly', () => {
    expect(component.getQuickActionIcon('add_customer')).toBe('person_add');
    expect(component.getQuickActionIcon('create_invoice')).toBe('receipt');
    expect(component.getQuickActionIcon('view_reports')).toBe('assessment');
    expect(component.getQuickActionIcon('manage_inventory')).toBe('inventory');
    expect(component.getQuickActionIcon('unknown')).toBe('help');
  });

  it('should get quick action color correctly', () => {
    expect(component.getQuickActionColor('add_customer')).toBe('#2196f3');
    expect(component.getQuickActionColor('create_invoice')).toBe('#4caf50');
    expect(component.getQuickActionColor('view_reports')).toBe('#ff9800');
    expect(component.getQuickActionColor('manage_inventory')).toBe('#9c27b0');
    expect(component.getQuickActionColor('unknown')).toBe('#666');
  });
});








