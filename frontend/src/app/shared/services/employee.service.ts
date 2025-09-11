import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, catchError, of } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Employee {
  id?: number;
  employeeCode: string;
  firstName: string;
  lastName: string;
  email?: string;
  phone?: string;
  address?: string;
  birthDate?: Date;
  gender?: 'MALE' | 'FEMALE' | 'OTHER';
  nationality?: string;
  idNumber?: string;
  socialSecurityNumber?: string;
  hireDate: Date;
  contractType: 'CDI' | 'PERMANENT' | 'FIXED_TERM' | 'TEMPORARY' | 'INTERNSHIP' | 'FREELANCE';
  employmentStatus: 'ACTIVE' | 'ON_LEAVE' | 'SUSPENDED' | 'TERMINATED' | 'RETIRED';
  department?: string;
  position?: string;
  jobTitle?: string;
  managerId?: number;
  baseSalary?: number;
  salaryCurrency?: string;
  bankAccount?: string;
  emergencyContact?: string;
  emergencyPhone?: string;
  skills?: string;
  certifications?: string;
  performanceRating?: number;
  lastEvaluationDate?: Date;
  nextEvaluationDate?: Date;
  terminationDate?: Date;
  terminationReason?: string;
  notes?: string;
  entrepriseId: number;
  createdAt?: Date;
  updatedAt?: Date;
}

export interface CreateEmployeeRequest {
  employeeCode: string;
  firstName: string;
  lastName: string;
  email?: string;
  phone?: string;
  address?: string;
  birthDate?: Date;
  gender?: string;
  nationality?: string;
  idNumber?: string;
  socialSecurityNumber?: string;
  hireDate: Date;
  contractType: string;
  employmentStatus: string;
  department?: string;
  position?: string;
  jobTitle?: string;
  managerId?: number;
  baseSalary?: number;
  salaryCurrency?: string;
  bankAccount?: string;
  emergencyContact?: string;
  emergencyPhone?: string;
  skills?: string;
  certifications?: string;
  performanceRating?: number;
  lastEvaluationDate?: Date;
  nextEvaluationDate?: Date;
  notes?: string;
  entrepriseId: number;
}

export interface UpdateEmployeeRequest extends Partial<CreateEmployeeRequest> {
  id: number;
}

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {
  private apiUrl = `${environment.apiUrl}/employees`;

  constructor(private http: HttpClient) {}

  /**
   * Obtenir tous les employés
   */
  getAllEmployees(): Observable<Employee[]> {
    return this.http.get<{data: Employee[], status: string}>(this.apiUrl)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des employés');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des employés:', error);
          return of([]);
        })
      );
  }

  /**
   * Obtenir un employé par ID
   */
  getEmployeeById(id: number): Observable<Employee> {
    return this.http.get<{data: Employee, status: string}>(`${this.apiUrl}/${id}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération de l\'employé');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération de l\'employé:', error);
          throw error;
        })
      );
  }

  /**
   * Créer un nouvel employé
   */
  createEmployee(employee: CreateEmployeeRequest): Observable<Employee> {
    return this.http.post<{data: Employee, status: string}>(this.apiUrl, employee)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la création de l\'employé');
        }),
        catchError(error => {
          console.error('Erreur lors de la création de l\'employé:', error);
          throw error;
        })
      );
  }

  /**
   * Mettre à jour un employé
   */
  updateEmployee(employee: UpdateEmployeeRequest): Observable<Employee> {
    return this.http.put<{data: Employee, status: string}>(`${this.apiUrl}/${employee.id}`, employee)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la mise à jour de l\'employé');
        }),
        catchError(error => {
          console.error('Erreur lors de la mise à jour de l\'employé:', error);
          throw error;
        })
      );
  }

  /**
   * Supprimer un employé
   */
  deleteEmployee(id: number): Observable<boolean> {
    return this.http.delete<{message: string, status: string}>(`${this.apiUrl}/${id}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return true;
          }
          throw new Error('Erreur lors de la suppression de l\'employé');
        }),
        catchError(error => {
          console.error('Erreur lors de la suppression de l\'employé:', error);
          return of(false);
        })
      );
  }

  /**
   * Obtenir les employés actifs
   */
  getActiveEmployees(): Observable<Employee[]> {
    return this.http.get<{data: Employee[], status: string}>(`${this.apiUrl}/active`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des employés actifs');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des employés actifs:', error);
          return of([]);
        })
      );
  }

  /**
   * Obtenir les employés par département
   */
  getEmployeesByDepartment(department: string): Observable<Employee[]> {
    return this.http.get<{data: Employee[], status: string}>(`${this.apiUrl}/department/${department}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des employés par département');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des employés par département:', error);
          return of([]);
        })
      );
  }

  /**
   * Obtenir les employés par statut
   */
  getEmployeesByStatus(status: string): Observable<Employee[]> {
    return this.http.get<{data: Employee[], status: string}>(`${this.apiUrl}/status/${status}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des employés par statut');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des employés par statut:', error);
          return of([]);
        })
      );
  }

  /**
   * Obtenir les employés nécessitant une évaluation
   */
  getEmployeesNeedingEvaluation(): Observable<Employee[]> {
    return this.http.get<{data: Employee[], status: string}>(`${this.apiUrl}/needing-evaluation`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des employés nécessitant une évaluation');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des employés nécessitant une évaluation:', error);
          return of([]);
        })
      );
  }

  /**
   * Obtenir les options pour les genres
   */
  getGenders(): Array<{value: string, label: string}> {
    return [
      { value: 'MALE', label: 'Masculin' },
      { value: 'FEMALE', label: 'Féminin' },
      { value: 'OTHER', label: 'Autre' }
    ];
  }

  /**
   * Obtenir les options pour les types de contrats
   */
  getContractTypes(): Array<{value: string, label: string}> {
    return [
      { value: 'CDI', label: 'Contrat à durée indéterminée' },
      { value: 'PERMANENT', label: 'Contrat permanent' },
      { value: 'FIXED_TERM', label: 'Contrat à durée déterminée' },
      { value: 'TEMPORARY', label: 'Contrat temporaire' },
      { value: 'INTERNSHIP', label: 'Stage' },
      { value: 'FREELANCE', label: 'Freelance' }
    ];
  }

  /**
   * Obtenir les options pour les statuts d'emploi
   */
  getEmploymentStatuses(): Array<{value: string, label: string}> {
    return [
      { value: 'ACTIVE', label: 'Actif' },
      { value: 'ON_LEAVE', label: 'En congé' },
      { value: 'SUSPENDED', label: 'Suspendu' },
      { value: 'TERMINATED', label: 'Terminé' },
      { value: 'RETIRED', label: 'Retraité' }
    ];
  }

  /**
   * Obtenir l'icône du genre
   */
  getGenderIcon(gender: string): string {
    switch (gender) {
      case 'MALE': return 'male';
      case 'FEMALE': return 'female';
      case 'OTHER': return 'transgender';
      default: return 'person';
    }
  }

  /**
   * Obtenir l'icône du statut d'emploi
   */
  getEmploymentStatusIcon(status: string): string {
    switch (status) {
      case 'ACTIVE': return 'check_circle';
      case 'ON_LEAVE': return 'pause_circle';
      case 'SUSPENDED': return 'warning';
      case 'TERMINATED': return 'cancel';
      case 'RETIRED': return 'elderly';
      default: return 'help';
    }
  }

  /**
   * Obtenir la couleur du statut d'emploi
   */
  getEmploymentStatusColor(status: string): string {
    switch (status) {
      case 'ACTIVE': return 'green';
      case 'ON_LEAVE': return 'blue';
      case 'SUSPENDED': return 'orange';
      case 'TERMINATED': return 'red';
      case 'RETIRED': return 'purple';
      default: return 'gray';
    }
  }

  /**
   * Obtenir l'icône du type de contrat
   */
  getContractTypeIcon(type: string): string {
    switch (type) {
      case 'CDI': return 'work';
      case 'PERMANENT': return 'work';
      case 'FIXED_TERM': return 'schedule';
      case 'TEMPORARY': return 'access_time';
      case 'INTERNSHIP': return 'school';
      case 'FREELANCE': return 'person';
      default: return 'work';
    }
  }

  /**
   * Formater la devise
   */
  formatCurrency(amount: number, currency: string = 'XOF'): string {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: currency
    }).format(amount);
  }

  /**
   * Formater la date
   */
  formatDate(date: string): string {
    return new Intl.DateTimeFormat('fr-FR').format(new Date(date));
  }

  /**
   * Calculer l'âge
   */
  calculateAge(birthDate: Date): number {
    const today = new Date();
    const birth = new Date(birthDate);
    let age = today.getFullYear() - birth.getFullYear();
    const monthDiff = today.getMonth() - birth.getMonth();
    
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
      age--;
    }
    
    return age;
  }

  /**
   * Calculer les années de service
   */
  calculateYearsOfService(hireDate: Date): number {
    const today = new Date();
    const hire = new Date(hireDate);
    let years = today.getFullYear() - hire.getFullYear();
    const monthDiff = today.getMonth() - hire.getMonth();
    
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < hire.getDate())) {
      years--;
    }
    
    return years;
  }
}








