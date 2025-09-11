import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth';
import { User } from '../models/user';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('login', () => {
    it('should login successfully with valid credentials', () => {
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

      const mockResponse = {
        accessToken: 'mock-access-token',
        refreshToken: 'mock-refresh-token',
        user: mockUser
      };

      service.login('testuser', 'password').subscribe(response => {
        expect(response.user).toEqual(mockUser);
        expect(response.accessToken).toBe('mock-access-token');
      });

      const req = httpMock.expectOne('/api/auth/login');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({ username: 'testuser', password: 'password' });
      req.flush(mockResponse);
    });

    it('should handle login error', () => {
      service.login('testuser', 'wrongpassword').subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(401);
        }
      });

      const req = httpMock.expectOne('/api/auth/login');
      req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });
    });
  });

  describe('register', () => {
    it('should register successfully with valid data', () => {
      const mockUser: User = {
        id: 1,
        username: 'newuser',
        email: 'new@example.com',
        firstName: 'New',
        lastName: 'User',
        roles: ['USER'],
        isActive: true,
        createdAt: new Date(),
        updatedAt: new Date()
      };

      const registrationData = {
        username: 'newuser',
        email: 'new@example.com',
        password: 'password123',
        firstName: 'New',
        lastName: 'User'
      };

      service.register(registrationData).subscribe(user => {
        expect(user).toEqual(mockUser);
      });

      const req = httpMock.expectOne('/api/auth/register');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(registrationData);
      req.flush(mockUser);
    });
  });

  describe('logout', () => {
    it('should logout successfully', () => {
      service.logout().subscribe(response => {
        expect(response).toBeTruthy();
      });

      const req = httpMock.expectOne('/api/auth/logout');
      expect(req.request.method).toBe('POST');
      req.flush({});
    });
  });

  describe('refreshToken', () => {
    it('should refresh token successfully', () => {
      const mockResponse = {
        accessToken: 'new-access-token',
        refreshToken: 'new-refresh-token'
      };

      service.refreshToken().subscribe(response => {
        expect(response.accessToken).toBe('new-access-token');
      });

      const req = httpMock.expectOne('/api/auth/refresh');
      expect(req.request.method).toBe('POST');
      req.flush(mockResponse);
    });
  });

  describe('getCurrentUser', () => {
    it('should return current user', () => {
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

      service.getCurrentUser().subscribe(user => {
        expect(user).toEqual(mockUser);
      });

      const req = httpMock.expectOne('/api/auth/me');
      expect(req.request.method).toBe('GET');
      req.flush(mockUser);
    });
  });
});








