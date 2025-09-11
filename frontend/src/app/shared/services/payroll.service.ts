import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, catchError, of } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Employee } from './employee.service';

export interface Payroll {
  id?: number;
  payrollCode: string;
  employee: Employee;
  payPeriod: 'WEEKLY' | 'BIWEEKLY' | 'MONTHLY' | 'QUARTERLY' | 'ANNUAL';
  payDate: Date;
  periodStartDate: Date;
  periodEndDate: Date;
  baseSalary?: number;
  hoursWorked?: number;
  overtimeHours?: number;
  overtimeRate?: number;
  overtimePay?: number;
  bonusAmount?: number;
  bonusDescription?: string;
  allowanceAmount?: number;
  allowanceDescription?: string;
  commissionAmount?: number;
  commissionRate?: number;
  grossSalary?: number;
  incomeTax?: number;
  socialSecurityTax?: number;
  healthInsurance?: number;
  pensionContribution?: number;
  otherDeductions?: number;
  deductionDescription?: string;
  netSalary?: number;
  currency: string;
  exchangeRate?: number;
  payrollStatus: 'DRAFT' | 'PENDING_APPROVAL' | 'APPROVED' | 'PAID' | 'CANCELLED' | 'REJECTED';
  paymentMethod?: 'BANK_TRANSFER' | 'CHECK' | 'CASH' | 'MOBILE_MONEY' | 'OTHER';
  bankTransferReference?: string;
  checkNumber?: string;
  cashAmount?: number;
  notes?: string;
  approvedBy?: number;
  approvedAt?: Date;
  entrepriseId: number;
  createdAt?: Date;
  updatedAt?: Date;
}

export interface CreatePayrollRequest {
  payrollCode: string;
  employeeId: number;
  payPeriod: string;
  payDate: Date;
  periodStartDate: Date;
  periodEndDate: Date;
  baseSalary?: number;
  hoursWorked?: number;
  overtimeHours?: number;
  overtimeRate?: number;
  overtimePay?: number;
  bonusAmount?: number;
  bonusDescription?: string;
  allowanceAmount?: number;
  allowanceDescription?: string;
  commissionAmount?: number;
  commissionRate?: number;
  grossSalary?: number;
  incomeTax?: number;
  socialSecurityTax?: number;
  healthInsurance?: number;
  pensionContribution?: number;
  otherDeductions?: number;
  deductionDescription?: string;
  netSalary?: number;
  currency: string;
  exchangeRate?: number;
  payrollStatus: string;
  paymentMethod?: string;
  bankTransferReference?: string;
  checkNumber?: string;
  cashAmount?: number;
  notes?: string;
  entrepriseId: number;
}

export interface UpdatePayrollRequest extends Partial<CreatePayrollRequest> {
  id: number;
}

@Injectable({
  providedIn: 'root'
})
export class PayrollService {
  private apiUrl = `${environment.apiUrl}/payrolls`;

  constructor(private http: HttpClient) {}

  /**
   * Obtenir toutes les paies
   */
  getAllPayrolls(): Observable<Payroll[]> {
    return this.http.get<{data: Payroll[], status: string}>(this.apiUrl)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des paies');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des paies:', error);
          return of([]);
        })
      );
  }

  /**
   * Obtenir une paie par ID
   */
  getPayrollById(id: number): Observable<Payroll> {
    return this.http.get<{data: Payroll, status: string}>(`${this.apiUrl}/${id}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération de la paie');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération de la paie:', error);
          throw error;
        })
      );
  }

  /**
   * Créer une nouvelle paie
   */
  createPayroll(payroll: CreatePayrollRequest): Observable<Payroll> {
    return this.http.post<{data: Payroll, status: string}>(this.apiUrl, payroll)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la création de la paie');
        }),
        catchError(error => {
          console.error('Erreur lors de la création de la paie:', error);
          throw error;
        })
      );
  }

  /**
   * Mettre à jour une paie
   */
  updatePayroll(payroll: UpdatePayrollRequest): Observable<Payroll> {
    return this.http.put<{data: Payroll, status: string}>(`${this.apiUrl}/${payroll.id}`, payroll)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la mise à jour de la paie');
        }),
        catchError(error => {
          console.error('Erreur lors de la mise à jour de la paie:', error);
          throw error;
        })
      );
  }

  /**
   * Supprimer une paie
   */
  deletePayroll(id: number): Observable<boolean> {
    return this.http.delete<{message: string, status: string}>(`${this.apiUrl}/${id}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return true;
          }
          throw new Error('Erreur lors de la suppression de la paie');
        }),
        catchError(error => {
          console.error('Erreur lors de la suppression de la paie:', error);
          return of(false);
        })
      );
  }

  /**
   * Approuver une paie
   */
  approvePayroll(payrollId: number, approvedBy: number): Observable<Payroll> {
    return this.http.put<{message: string, payroll: Payroll, status: string}>(`${this.apiUrl}/${payrollId}/approve`, { approvedBy })
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.payroll;
          }
          throw new Error('Erreur lors de l\'approbation de la paie');
        }),
        catchError(error => {
          console.error('Erreur lors de l\'approbation de la paie:', error);
          throw error;
        })
      );
  }

  /**
   * Marquer une paie comme payée
   */
  markPayrollAsPaid(payrollId: number): Observable<Payroll> {
    return this.http.put<{message: string, payroll: Payroll, status: string}>(`${this.apiUrl}/${payrollId}/pay`, {})
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.payroll;
          }
          throw new Error('Erreur lors du marquage de la paie comme payée');
        }),
        catchError(error => {
          console.error('Erreur lors du marquage de la paie comme payée:', error);
          throw error;
        })
      );
  }

  /**
   * Obtenir les paies par employé
   */
  getPayrollsByEmployee(employeeId: number): Observable<Payroll[]> {
    return this.http.get<{data: Payroll[], status: string}>(`${this.apiUrl}/employee/${employeeId}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des paies par employé');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des paies par employé:', error);
          return of([]);
        })
      );
  }

  /**
   * Obtenir les paies par statut
   */
  getPayrollsByStatus(status: string): Observable<Payroll[]> {
    return this.http.get<{data: Payroll[], status: string}>(`${this.apiUrl}/status/${status}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des paies par statut');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des paies par statut:', error);
          return of([]);
        })
      );
  }

  /**
   * Obtenir les paies par période
   */
  getPayrollsByPeriod(startDate: Date, endDate: Date): Observable<Payroll[]> {
    const params = {
      startDate: startDate.toISOString().split('T')[0],
      endDate: endDate.toISOString().split('T')[0]
    };
    
    return this.http.get<{data: Payroll[], status: string}>(`${this.apiUrl}/period`, { params })
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des paies par période');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des paies par période:', error);
          return of([]);
        })
      );
  }

  /**
   * Calculer la paie pour un employé
   */
  calculatePayroll(employeeId: number, periodStartDate: Date, periodEndDate: Date): Observable<Payroll> {
    const params = {
      employeeId: employeeId.toString(),
      periodStartDate: periodStartDate.toISOString().split('T')[0],
      periodEndDate: periodEndDate.toISOString().split('T')[0]
    };
    
    return this.http.post<{data: Payroll, status: string}>(`${this.apiUrl}/calculate`, {}, { params })
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors du calcul de la paie');
        }),
        catchError(error => {
          console.error('Erreur lors du calcul de la paie:', error);
          throw error;
        })
      );
  }

  /**
   * Obtenir les options pour les périodes de paie
   */
  getPayPeriods(): Array<{value: string, label: string}> {
    return [
      { value: 'WEEKLY', label: 'Hebdomadaire' },
      { value: 'BIWEEKLY', label: 'Bimensuel' },
      { value: 'MONTHLY', label: 'Mensuel' },
      { value: 'QUARTERLY', label: 'Trimestriel' },
      { value: 'ANNUAL', label: 'Annuel' }
    ];
  }

  /**
   * Obtenir les options pour les statuts de paie
   */
  getPayrollStatuses(): Array<{value: string, label: string}> {
    return [
      { value: 'DRAFT', label: 'Brouillon' },
      { value: 'PENDING_APPROVAL', label: 'En attente d\'approbation' },
      { value: 'APPROVED', label: 'Approuvé' },
      { value: 'PAID', label: 'Payé' },
      { value: 'CANCELLED', label: 'Annulé' },
      { value: 'REJECTED', label: 'Rejeté' }
    ];
  }

  /**
   * Obtenir les options pour les méthodes de paiement
   */
  getPaymentMethods(): Array<{value: string, label: string}> {
    return [
      { value: 'BANK_TRANSFER', label: 'Virement bancaire' },
      { value: 'CHECK', label: 'Chèque' },
      { value: 'CASH', label: 'Espèces' },
      { value: 'MOBILE_MONEY', label: 'Mobile Money' },
      { value: 'OTHER', label: 'Autre' }
    ];
  }

  /**
   * Obtenir l'icône du statut de paie
   */
  getPayrollStatusIcon(status: string): string {
    switch (status) {
      case 'DRAFT': return 'edit';
      case 'PENDING_APPROVAL': return 'pending';
      case 'APPROVED': return 'check_circle';
      case 'PAID': return 'payment';
      case 'CANCELLED': return 'cancel';
      case 'REJECTED': return 'error';
      default: return 'help';
    }
  }

  /**
   * Obtenir la couleur du statut de paie
   */
  getPayrollStatusColor(status: string): string {
    switch (status) {
      case 'DRAFT': return 'gray';
      case 'PENDING_APPROVAL': return 'orange';
      case 'APPROVED': return 'blue';
      case 'PAID': return 'green';
      case 'CANCELLED': return 'red';
      case 'REJECTED': return 'red';
      default: return 'gray';
    }
  }

  /**
   * Obtenir l'icône de la méthode de paiement
   */
  getPaymentMethodIcon(method: string): string {
    switch (method) {
      case 'BANK_TRANSFER': return 'account_balance';
      case 'CHECK': return 'receipt';
      case 'CASH': return 'money';
      case 'MOBILE_MONEY': return 'phone_android';
      case 'OTHER': return 'more_horiz';
      default: return 'payment';
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
   * Calculer le salaire brut
   */
  calculateGrossSalary(baseSalary: number, overtimePay: number = 0, bonusAmount: number = 0, allowanceAmount: number = 0, commissionAmount: number = 0): number {
    return baseSalary + overtimePay + bonusAmount + allowanceAmount + commissionAmount;
  }

  /**
   * Calculer le salaire net
   */
  calculateNetSalary(grossSalary: number, incomeTax: number = 0, socialSecurityTax: number = 0, healthInsurance: number = 0, pensionContribution: number = 0, otherDeductions: number = 0): number {
    const totalDeductions = incomeTax + socialSecurityTax + healthInsurance + pensionContribution + otherDeductions;
    return grossSalary - totalDeductions;
  }
}








