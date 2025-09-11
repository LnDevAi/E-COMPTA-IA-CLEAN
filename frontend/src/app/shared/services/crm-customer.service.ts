import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface CrmCustomer {
  id: number;
  companyId: number;
  thirdPartyId?: number;
  aiScore?: number;
  churnProbability?: number;
  lifetimeValuePredicted?: number;
  customerSegment: 'PREMIUM' | 'GOLD' | 'SILVER' | 'BRONZE' | 'NEW' | 'AT_RISK' | 'CHURNED';
  paymentBehavior: 'EXCELLENT' | 'GOOD' | 'AVERAGE' | 'POOR' | 'DELINQUENT';
  satisfactionScore?: number;
  avgPaymentDelay?: number;
  totalRevenueGenerated: number;
  totalOrdersCount: number;
  avgOrderValue: number;
  lastOrderDate?: string;
  lastContactDate?: string;
  nextContactDate?: string;
  preferredContactMethod: 'EMAIL' | 'PHONE' | 'SMS' | 'WHATSAPP' | 'LINKEDIN';
  preferredContactTime?: string;
  timezone?: string;
  language?: string;
  source: string;
  leadScore?: number;
  leadStatus: 'NEW' | 'CONTACTED' | 'QUALIFIED' | 'PROPOSAL' | 'NEGOTIATION' | 'CLOSED_WON' | 'CLOSED_LOST';
  assignedTo?: number;
  tags: string[];
  notes?: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
  // Relations
  thirdParty?: {
    id: number;
    name: string;
    email: string;
    phone: string;
    address: string;
    city: string;
    postalCode: string;
    country: string;
  };
  assignedUser?: {
    id: number;
    username: string;
    firstName: string;
    lastName: string;
    email: string;
  };
  orders?: CrmOrder[];
  interactions?: CrmInteraction[];
}

export interface CrmOrder {
  id: number;
  customerId: number;
  orderNumber: string;
  orderDate: string;
  totalAmount: number;
  currency: string;
  status: 'PENDING' | 'CONFIRMED' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED' | 'REFUNDED';
  paymentStatus: 'PENDING' | 'PAID' | 'PARTIAL' | 'OVERDUE' | 'CANCELLED';
  items: CrmOrderItem[];
  createdAt: string;
  updatedAt: string;
}

export interface CrmOrderItem {
  id: number;
  orderId: number;
  productName: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
  currency: string;
}

export interface CrmInteraction {
  id: number;
  customerId: number;
  interactionType: 'EMAIL' | 'PHONE' | 'MEETING' | 'CHAT' | 'SMS' | 'LINKEDIN' | 'OTHER';
  subject: string;
  description: string;
  outcome: 'POSITIVE' | 'NEUTRAL' | 'NEGATIVE' | 'FOLLOW_UP_REQUIRED';
  nextAction?: string;
  nextActionDate?: string;
  createdBy: number;
  createdAt: string;
  updatedAt: string;
  createdByUser?: {
    id: number;
    username: string;
    firstName: string;
    lastName: string;
  };
}

export interface CreateCrmCustomerRequest {
  thirdPartyId?: number;
  customerSegment: string;
  paymentBehavior: string;
  satisfactionScore?: number;
  preferredContactMethod: string;
  preferredContactTime?: string;
  timezone?: string;
  language?: string;
  source: string;
  leadScore?: number;
  leadStatus: string;
  assignedTo?: number;
  tags: string[];
  notes?: string;
}

export interface CrmCustomerStatistics {
  totalCustomers: number;
  activeCustomers: number;
  inactiveCustomers: number;
  customersBySegment: { [key: string]: number };
  customersByStatus: { [key: string]: number };
  customersBySource: { [key: string]: number };
  totalRevenue: number;
  avgOrderValue: number;
  avgSatisfactionScore: number;
  churnRate: number;
  newCustomersThisMonth: number;
  customersAtRisk: number;
}

@Injectable({
  providedIn: 'root'
})
export class CrmCustomerService {
  private readonly apiUrl = `${environment.apiUrl}/api/crm`;
  private customersSubject = new BehaviorSubject<CrmCustomer[]>([]);
  public customers$ = this.customersSubject.asObservable();

  constructor(private http: HttpClient) {}

  /**
   * Récupérer tous les clients CRM
   */
  getAllCrmCustomers(): Observable<CrmCustomer[]> {
    return this.http.get<{data: CrmCustomer[], total: number, status: string}>(`${this.apiUrl}/customers`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            this.customersSubject.next(response.data);
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des clients');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des clients:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer un client par ID
   */
  getCrmCustomerById(customerId: number): Observable<CrmCustomer> {
    return this.http.get<{customer: CrmCustomer, status: string}>(`${this.apiUrl}/customers/${customerId}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.customer;
          }
          throw new Error('Erreur lors de la récupération du client');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération du client:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les clients d'une entreprise
   */
  getCrmCustomersByCompany(companyId: number): Observable<CrmCustomer[]> {
    return this.http.get<{customers: CrmCustomer[], total: number, status: string}>(`${this.apiUrl}/companies/${companyId}/customers`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            this.customersSubject.next(response.customers);
            return response.customers;
          }
          throw new Error('Erreur lors de la récupération des clients');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des clients:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les clients par segment
   */
  getCrmCustomersBySegment(companyId: number, segment: string): Observable<CrmCustomer[]> {
    return this.http.get<{customers: CrmCustomer[], total: number, status: string}>(`${this.apiUrl}/companies/${companyId}/customers/segment/${segment}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.customers;
          }
          throw new Error('Erreur lors de la récupération des clients');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des clients:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les clients par statut
   */
  getCrmCustomersByStatus(companyId: number, status: string): Observable<CrmCustomer[]> {
    return this.http.get<{customers: CrmCustomer[], total: number, status: string}>(`${this.apiUrl}/companies/${companyId}/customers/status/${status}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.customers;
          }
          throw new Error('Erreur lors de la récupération des clients');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des clients:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les clients actifs
   */
  getActiveCrmCustomers(companyId: number): Observable<CrmCustomer[]> {
    return this.http.get<{customers: CrmCustomer[], total: number, status: string}>(`${this.apiUrl}/companies/${companyId}/customers/active`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.customers;
          }
          throw new Error('Erreur lors de la récupération des clients');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des clients:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les clients à risque
   */
  getAtRiskCrmCustomers(companyId: number): Observable<CrmCustomer[]> {
    return this.http.get<{customers: CrmCustomer[], total: number, status: string}>(`${this.apiUrl}/companies/${companyId}/customers/at-risk`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.customers;
          }
          throw new Error('Erreur lors de la récupération des clients');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des clients:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les clients premium
   */
  getPremiumCrmCustomers(companyId: number): Observable<CrmCustomer[]> {
    return this.http.get<{customers: CrmCustomer[], total: number, status: string}>(`${this.apiUrl}/companies/${companyId}/customers/premium`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.customers;
          }
          throw new Error('Erreur lors de la récupération des clients');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des clients:', error);
          throw error;
        })
      );
  }

  /**
   * Créer un nouveau client CRM
   */
  createCrmCustomer(request: CreateCrmCustomerRequest): Observable<CrmCustomer> {
    return this.http.post<{message: string, customer: CrmCustomer, status: string}>(`${this.apiUrl}/customers`, request)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            // Mettre à jour la liste locale
            const currentCustomers = this.customersSubject.value;
            this.customersSubject.next([...currentCustomers, response.customer]);
            return response.customer;
          }
          throw new Error(response.message || 'Erreur lors de la création du client');
        }),
        catchError(error => {
          console.error('Erreur lors de la création du client:', error);
          throw error;
        })
      );
  }

  /**
   * Mettre à jour un client CRM
   */
  updateCrmCustomer(customerId: number, request: Partial<CreateCrmCustomerRequest>): Observable<CrmCustomer> {
    return this.http.put<{message: string, customer: CrmCustomer, status: string}>(`${this.apiUrl}/customers/${customerId}`, request)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            // Mettre à jour la liste locale
            const currentCustomers = this.customersSubject.value;
            const updatedCustomers = currentCustomers.map(customer => 
              customer.id === customerId ? response.customer : customer
            );
            this.customersSubject.next(updatedCustomers);
            return response.customer;
          }
          throw new Error(response.message || 'Erreur lors de la mise à jour du client');
        }),
        catchError(error => {
          console.error('Erreur lors de la mise à jour du client:', error);
          throw error;
        })
      );
  }

  /**
   * Supprimer un client CRM
   */
  deleteCrmCustomer(customerId: number): Observable<void> {
    return this.http.delete<{message: string, status: string}>(`${this.apiUrl}/customers/${customerId}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            // Mettre à jour la liste locale
            const currentCustomers = this.customersSubject.value;
            const updatedCustomers = currentCustomers.filter(customer => customer.id !== customerId);
            this.customersSubject.next(updatedCustomers);
            return;
          }
          throw new Error(response.message || 'Erreur lors de la suppression du client');
        }),
        catchError(error => {
          console.error('Erreur lors de la suppression du client:', error);
          throw error;
        })
      );
  }

  /**
   * Activer/Désactiver un client
   */
  toggleCrmCustomerStatus(customerId: number): Observable<CrmCustomer> {
    return this.http.put<{message: string, customer: CrmCustomer, status: string}>(`${this.apiUrl}/customers/${customerId}/toggle-status`, {})
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            // Mettre à jour la liste locale
            const currentCustomers = this.customersSubject.value;
            const updatedCustomers = currentCustomers.map(customer => 
              customer.id === customerId ? response.customer : customer
            );
            this.customersSubject.next(updatedCustomers);
            return response.customer;
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
   * Récupérer les commandes d'un client
   */
  getCrmCustomerOrders(customerId: number): Observable<CrmOrder[]> {
    return this.http.get<{orders: CrmOrder[], total: number, status: string}>(`${this.apiUrl}/customers/${customerId}/orders`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.orders;
          }
          throw new Error('Erreur lors de la récupération des commandes');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des commandes:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les interactions d'un client
   */
  getCrmCustomerInteractions(customerId: number): Observable<CrmInteraction[]> {
    return this.http.get<{interactions: CrmInteraction[], total: number, status: string}>(`${this.apiUrl}/customers/${customerId}/interactions`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.interactions;
          }
          throw new Error('Erreur lors de la récupération des interactions');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des interactions:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les statistiques des clients
   */
  getCrmCustomerStatistics(companyId: number): Observable<CrmCustomerStatistics> {
    return this.http.get<{statistics: CrmCustomerStatistics, status: string}>(`${this.apiUrl}/companies/${companyId}/customers/statistics`)
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
   * Obtenir la couleur du segment
   */
  getSegmentColor(segment: string): string {
    switch (segment) {
      case 'PREMIUM': return 'purple';
      case 'GOLD': return 'gold';
      case 'SILVER': return 'silver';
      case 'BRONZE': return 'brown';
      case 'NEW': return 'green';
      case 'AT_RISK': return 'orange';
      case 'CHURNED': return 'red';
      default: return 'gray';
    }
  }

  /**
   * Obtenir l'icône du segment
   */
  getSegmentIcon(segment: string): string {
    switch (segment) {
      case 'PREMIUM': return 'diamond';
      case 'GOLD': return 'star';
      case 'SILVER': return 'star_half';
      case 'BRONZE': return 'star_border';
      case 'NEW': return 'fiber_new';
      case 'AT_RISK': return 'warning';
      case 'CHURNED': return 'cancel';
      default: return 'person';
    }
  }

  /**
   * Obtenir le libellé du segment
   */
  getSegmentLabel(segment: string): string {
    switch (segment) {
      case 'PREMIUM': return 'Premium';
      case 'GOLD': return 'Or';
      case 'SILVER': return 'Argent';
      case 'BRONZE': return 'Bronze';
      case 'NEW': return 'Nouveau';
      case 'AT_RISK': return 'À risque';
      case 'CHURNED': return 'Perdu';
      default: return segment;
    }
  }

  /**
   * Obtenir la couleur du statut
   */
  getStatusColor(status: string): string {
    switch (status) {
      case 'NEW': return 'blue';
      case 'CONTACTED': return 'orange';
      case 'QUALIFIED': return 'green';
      case 'PROPOSAL': return 'purple';
      case 'NEGOTIATION': return 'amber';
      case 'CLOSED_WON': return 'green';
      case 'CLOSED_LOST': return 'red';
      default: return 'gray';
    }
  }

  /**
   * Obtenir l'icône du statut
   */
  getStatusIcon(status: string): string {
    switch (status) {
      case 'NEW': return 'fiber_new';
      case 'CONTACTED': return 'phone';
      case 'QUALIFIED': return 'verified';
      case 'PROPOSAL': return 'description';
      case 'NEGOTIATION': return 'handshake';
      case 'CLOSED_WON': return 'check_circle';
      case 'CLOSED_LOST': return 'cancel';
      default: return 'help';
    }
  }

  /**
   * Formater la devise
   */
  formatCurrency(amount: number, currency: string = 'EUR'): string {
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
   * Calculer le score de risque
   */
  calculateRiskScore(customer: CrmCustomer): number {
    let score = 0;
    
    // Score de base basé sur le segment
    switch (customer.customerSegment) {
      case 'PREMIUM': score += 10; break;
      case 'GOLD': score += 8; break;
      case 'SILVER': score += 6; break;
      case 'BRONZE': score += 4; break;
      case 'NEW': score += 2; break;
      case 'AT_RISK': score += 1; break;
      case 'CHURNED': score += 0; break;
    }
    
    // Ajustement basé sur le comportement de paiement
    switch (customer.paymentBehavior) {
      case 'EXCELLENT': score += 5; break;
      case 'GOOD': score += 3; break;
      case 'AVERAGE': score += 1; break;
      case 'POOR': score -= 2; break;
      case 'DELINQUENT': score -= 5; break;
    }
    
    // Ajustement basé sur le score de satisfaction
    if (customer.satisfactionScore) {
      if (customer.satisfactionScore >= 8) score += 3;
      else if (customer.satisfactionScore >= 6) score += 1;
      else if (customer.satisfactionScore >= 4) score -= 1;
      else score -= 3;
    }
    
    return Math.max(0, Math.min(10, score));
  }
}
