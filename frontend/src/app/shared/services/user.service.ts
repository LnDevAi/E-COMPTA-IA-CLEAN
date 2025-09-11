import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  address?: string;
  city?: string;
  postalCode?: string;
  countryCode?: string;
  department?: string;
  position?: string;
  employeeCode?: string;
  companyId: number;
  gender?: 'MALE' | 'FEMALE' | 'OTHER';
  baseSalary?: number;
  salaryCurrency?: string;
  contractType?: string;
  hireDate?: string;
  terminationDate?: string;
  isActive: boolean;
  isAdmin: boolean;
  lastLoginDate?: string;
  createdAt: string;
  updatedAt: string;
  createdBy?: string;
  updatedBy?: string;
  // Relations
  company?: {
    id: number;
    name: string;
    legalName: string;
    registrationNumber: string;
    taxNumber: string;
    address: string;
    city: string;
    postalCode: string;
    country: string;
    phone: string;
    email: string;
    website?: string;
    industry?: string;
    size?: string;
    isActive: boolean;
  };
  roles?: UserRole[];
  permissions?: UserPermission[];
}

export interface UserRole {
  id: number;
  name: string;
  description: string;
  isActive: boolean;
  permissions: UserPermission[];
}

export interface UserPermission {
  id: number;
  name: string;
  description: string;
  resource: string;
  action: string;
  isActive: boolean;
}

export interface CreateUserRequest {
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  address?: string;
  city?: string;
  postalCode?: string;
  countryCode?: string;
  department?: string;
  position?: string;
  employeeCode?: string;
  companyId: number;
  gender?: string;
  baseSalary?: number;
  salaryCurrency?: string;
  contractType?: string;
  hireDate?: string;
  isAdmin?: boolean;
  roles?: number[];
}

export interface UserStatistics {
  totalUsers: number;
  activeUsers: number;
  inactiveUsers: number;
  adminUsers: number;
  regularUsers: number;
  usersByDepartment: { [key: string]: number };
  usersByPosition: { [key: string]: number };
  usersByContractType: { [key: string]: number };
  avgSalary: number;
  newUsersThisMonth: number;
  usersByGender: { [key: string]: number };
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly apiUrl = `${environment.apiUrl}/api/users`;
  private usersSubject = new BehaviorSubject<User[]>([]);
  public users$ = this.usersSubject.asObservable();

  constructor(private http: HttpClient) {}

  /**
   * Récupérer tous les utilisateurs
   */
  getAllUsers(): Observable<User[]> {
    return this.http.get<{data: User[], total: number, status: string}>(this.apiUrl)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            this.usersSubject.next(response.data);
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des utilisateurs');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des utilisateurs:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les utilisateurs d'une entreprise
   */
  getUsersByCompany(companyId: number): Observable<User[]> {
    return this.http.get<{users: User[], total: number, status: string}>(`${this.apiUrl}/company/${companyId}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            this.usersSubject.next(response.users);
            return response.users;
          }
          throw new Error('Erreur lors de la récupération des utilisateurs');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des utilisateurs:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les utilisateurs actifs
   */
  getActiveUsers(companyId: number): Observable<User[]> {
    return this.http.get<{users: User[], total: number, status: string}>(`${this.apiUrl}/company/${companyId}/active`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.users;
          }
          throw new Error('Erreur lors de la récupération des utilisateurs');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des utilisateurs:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les administrateurs
   */
  getAdminUsers(companyId: number): Observable<User[]> {
    return this.http.get<{users: User[], total: number, status: string}>(`${this.apiUrl}/company/${companyId}/admins`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.users;
          }
          throw new Error('Erreur lors de la récupération des administrateurs');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des administrateurs:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les utilisateurs par département
   */
  getUsersByDepartment(companyId: number, department: string): Observable<User[]> {
    return this.http.get<{users: User[], total: number, status: string}>(`${this.apiUrl}/company/${companyId}/department/${department}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.users;
          }
          throw new Error('Erreur lors de la récupération des utilisateurs');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des utilisateurs:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les utilisateurs par position
   */
  getUsersByPosition(companyId: number, position: string): Observable<User[]> {
    return this.http.get<{users: User[], total: number, status: string}>(`${this.apiUrl}/company/${companyId}/position/${position}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.users;
          }
          throw new Error('Erreur lors de la récupération des utilisateurs');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des utilisateurs:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer un utilisateur par ID
   */
  getUserById(userId: number): Observable<User> {
    return this.http.get<{user: User, status: string}>(`${this.apiUrl}/${userId}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.user;
          }
          throw new Error('Erreur lors de la récupération de l\'utilisateur');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération de l\'utilisateur:', error);
          throw error;
        })
      );
  }

  /**
   * Créer un nouvel utilisateur
   */
  createUser(request: CreateUserRequest): Observable<User> {
    return this.http.post<{message: string, user: User, status: string}>(this.apiUrl, request)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            // Mettre à jour la liste locale
            const currentUsers = this.usersSubject.value;
            this.usersSubject.next([...currentUsers, response.user]);
            return response.user;
          }
          throw new Error(response.message || 'Erreur lors de la création de l\'utilisateur');
        }),
        catchError(error => {
          console.error('Erreur lors de la création de l\'utilisateur:', error);
          throw error;
        })
      );
  }

  /**
   * Mettre à jour un utilisateur
   */
  updateUser(userId: number, request: Partial<CreateUserRequest>): Observable<User> {
    return this.http.put<{message: string, user: User, status: string}>(`${this.apiUrl}/${userId}`, request)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            // Mettre à jour la liste locale
            const currentUsers = this.usersSubject.value;
            const updatedUsers = currentUsers.map(user => 
              user.id === userId ? response.user : user
            );
            this.usersSubject.next(updatedUsers);
            return response.user;
          }
          throw new Error(response.message || 'Erreur lors de la mise à jour de l\'utilisateur');
        }),
        catchError(error => {
          console.error('Erreur lors de la mise à jour de l\'utilisateur:', error);
          throw error;
        })
      );
  }

  /**
   * Supprimer un utilisateur
   */
  deleteUser(userId: number): Observable<void> {
    return this.http.delete<{message: string, status: string}>(`${this.apiUrl}/${userId}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            // Mettre à jour la liste locale
            const currentUsers = this.usersSubject.value;
            const updatedUsers = currentUsers.filter(user => user.id !== userId);
            this.usersSubject.next(updatedUsers);
            return;
          }
          throw new Error(response.message || 'Erreur lors de la suppression de l\'utilisateur');
        }),
        catchError(error => {
          console.error('Erreur lors de la suppression de l\'utilisateur:', error);
          throw error;
        })
      );
  }

  /**
   * Activer/Désactiver un utilisateur
   */
  toggleUserStatus(userId: number): Observable<User> {
    return this.http.put<{message: string, user: User, status: string}>(`${this.apiUrl}/${userId}/toggle-status`, {})
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            // Mettre à jour la liste locale
            const currentUsers = this.usersSubject.value;
            const updatedUsers = currentUsers.map(user => 
              user.id === userId ? response.user : user
            );
            this.usersSubject.next(updatedUsers);
            return response.user;
          }
          throw new Error(response.message || 'Erreur lors du changement de statut');
        }),
        catchError(error => {
          console.error('Erreur lors du changement de statut:', error);
          throw error;
        })
      );
  }

  /**
   * Changer le mot de passe d'un utilisateur
   */
  changeUserPassword(userId: number, newPassword: string): Observable<void> {
    return this.http.put<{message: string, status: string}>(`${this.apiUrl}/${userId}/password`, { newPassword })
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return;
          }
          throw new Error(response.message || 'Erreur lors du changement de mot de passe');
        }),
        catchError(error => {
          console.error('Erreur lors du changement de mot de passe:', error);
          throw error;
        })
      );
  }

  /**
   * Assigner des rôles à un utilisateur
   */
  assignUserRoles(userId: number, roleIds: number[]): Observable<User> {
    return this.http.put<{message: string, user: User, status: string}>(`${this.apiUrl}/${userId}/roles`, { roleIds })
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            // Mettre à jour la liste locale
            const currentUsers = this.usersSubject.value;
            const updatedUsers = currentUsers.map(user => 
              user.id === userId ? response.user : user
            );
            this.usersSubject.next(updatedUsers);
            return response.user;
          }
          throw new Error(response.message || 'Erreur lors de l\'assignation des rôles');
        }),
        catchError(error => {
          console.error('Erreur lors de l\'assignation des rôles:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les rôles disponibles
   */
  getAvailableRoles(): Observable<UserRole[]> {
    return this.http.get<{roles: UserRole[], status: string}>(`${this.apiUrl}/roles`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.roles;
          }
          throw new Error('Erreur lors de la récupération des rôles');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des rôles:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les permissions disponibles
   */
  getAvailablePermissions(): Observable<UserPermission[]> {
    return this.http.get<{permissions: UserPermission[], status: string}>(`${this.apiUrl}/permissions`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.permissions;
          }
          throw new Error('Erreur lors de la récupération des permissions');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des permissions:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les statistiques des utilisateurs
   */
  getUserStatistics(companyId: number): Observable<UserStatistics> {
    return this.http.get<{statistics: UserStatistics, status: string}>(`${this.apiUrl}/company/${companyId}/statistics`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.statistics;
          }
          throw new Error('Erreur lors de la récupération des statistiques');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des statistiques:', error);
          throw error;
        })
      );
  }

  /**
   * Obtenir le nom complet d'un utilisateur
   */
  getFullName(user: User): string {
    return `${user.firstName} ${user.lastName}`;
  }

  /**
   * Obtenir les initiales d'un utilisateur
   */
  getInitials(user: User): string {
    return `${user.firstName.charAt(0)}${user.lastName.charAt(0)}`.toUpperCase();
  }

  /**
   * Vérifier si un utilisateur est administrateur
   */
  isAdmin(user: User): boolean {
    return user.isAdmin;
  }

  /**
   * Vérifier si un utilisateur est actif
   */
  isActive(user: User): boolean {
    return user.isActive;
  }

  /**
   * Formater le salaire
   */
  formatSalary(salary: number, currency: string = 'EUR'): string {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: currency
    }).format(salary);
  }

  /**
   * Formater la date
   */
  formatDate(date: string): string {
    return new Intl.DateTimeFormat('fr-FR').format(new Date(date));
  }

  /**
   * Calculer l'ancienneté d'un utilisateur
   */
  calculateSeniority(user: User): number {
    if (!user.hireDate) return 0;
    
    const hireDate = new Date(user.hireDate);
    const now = new Date();
    const diffTime = Math.abs(now.getTime() - hireDate.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    
    return Math.floor(diffDays / 365); // Années
  }
}








