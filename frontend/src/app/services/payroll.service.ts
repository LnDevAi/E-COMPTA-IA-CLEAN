import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Employee {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  position: string;
  baseSalary: number;
  salaryCurrency: string;
  companyId: number;
  hireDate: string;
  terminationDate?: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Payroll {
  id: number;
  employeeId: number;
  month: number;
  year: number;
  baseSalary: number;
  grossSalary: number;
  socialCharges: number;
  incomeTax: number;
  netSalary: number;
  currency: string;
  status: string;
  paymentDate?: string;
  createdAt: string;
  updatedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class PayrollService {
  private apiUrl = `${environment.apiUrl}/api/payroll`;

  constructor(private http: HttpClient) { }

  /**
   * Calculer la paie d'un employé
   */
  calculatePayroll(employeeId: number, month: number, year: number): Observable<Payroll> {
    return this.http.post<Payroll>(`${this.apiUrl}/calculate`, {
      employeeId,
      month,
      year
    });
  }

  /**
   * Valider une paie
   */
  validatePayroll(payrollId: number): Observable<Payroll> {
    return this.http.put<Payroll>(`${this.apiUrl}/payrolls/${payrollId}/validate`, {});
  }

  /**
   * Payer une paie
   */
  payPayroll(payrollId: number): Observable<Payroll> {
    return this.http.put<Payroll>(`${this.apiUrl}/payrolls/${payrollId}/pay`, {});
  }

  /**
   * Obtenir les paies d'un employé
   */
  getEmployeePayrolls(employeeId: number): Observable<Payroll[]> {
    return this.http.get<Payroll[]>(`${this.apiUrl}/payrolls/employee/${employeeId}`);
  }

  /**
   * Obtenir les paies d'une période
   */
  getPayrollsByPeriod(month: number, year: number): Observable<Payroll[]> {
    return this.http.get<Payroll[]>(`${this.apiUrl}/payrolls/period/${year}/${month}`);
  }

  /**
   * Obtenir les paies d'une entreprise
   */
  getCompanyPayrolls(companyId: number): Observable<Payroll[]> {
    return this.http.get<Payroll[]>(`${this.apiUrl}/payrolls/company/${companyId}`);
  }

  /**
   * Créer un employé
   */
  createEmployee(firstName: string, lastName: string, email: string, 
                position: string, baseSalary: number, currency: string, companyId: number): Observable<Employee> {
    return this.http.post<Employee>(`${this.apiUrl}/employees`, {
      firstName,
      lastName,
      email,
      position,
      baseSalary,
      currency,
      companyId
    });
  }

  /**
   * Mettre à jour les informations d'un employé
   */
  updateEmployee(employeeId: number, firstName: string, lastName: string, 
                email: string, position: string, baseSalary: number): Observable<Employee> {
    return this.http.put<Employee>(`${this.apiUrl}/employees/${employeeId}`, {
      firstName,
      lastName,
      email,
      position,
      baseSalary
    });
  }

  /**
   * Désactiver un employé
   */
  deactivateEmployee(employeeId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/employees/${employeeId}`);
  }

  /**
   * Obtenir tous les employés d'une entreprise
   */
  getCompanyEmployees(companyId: number): Observable<Employee[]> {
    return this.http.get<Employee[]>(`${this.apiUrl}/employees/company/${companyId}`);
  }

  /**
   * Obtenir les statistiques de paie
   */
  getPayrollStatistics(companyId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/statistics/company/${companyId}`);
  }

  /**
   * Rechercher des employés par nom
   */
  searchEmployeesByName(name: string, companyId: number): Observable<Employee[]> {
    return this.http.get<Employee[]>(`${this.apiUrl}/employees/search?name=${name}&companyId=${companyId}`);
  }

  /**
   * Obtenir les employés par position
   */
  getEmployeesByPosition(position: string, companyId: number): Observable<Employee[]> {
    return this.http.get<Employee[]>(`${this.apiUrl}/employees/position/${position}?companyId=${companyId}`);
  }

  /**
   * Générer le bulletin de paie
   */
  generatePayrollSlip(payrollId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/payrolls/${payrollId}/slip`, {
      responseType: 'blob'
    });
  }

  /**
   * Exporter les paies en Excel
   */
  exportPayrollsToExcel(companyId: number, month: number, year: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/payrolls/export/excel?companyId=${companyId}&month=${month}&year=${year}`, {
      responseType: 'blob'
    });
  }
}

