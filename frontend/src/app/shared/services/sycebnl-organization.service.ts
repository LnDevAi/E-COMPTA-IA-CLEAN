import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, catchError, of } from 'rxjs';
import { environment } from '../../../environments/environment';
import { InternationalConfigService } from './international-config.service';
import { AccountingStandardsService } from './accounting-standards.service';
import { CurrencyManagementService } from './currency-management.service';

export interface SycebnlOrganization {
  id?: number;
  company: {
    id: number;
    name: string;
  };
  organizationName: string;
  legalForm?: string;
  registrationNumber?: string;
  taxIdentificationNumber?: string;
  organizationType: 'ASSOCIATION' | 'FOUNDATION' | 'NGO' | 'COOPERATIVE' | 'MUTUAL' | 'RELIGIOUS_ORGANIZATION' | 'EDUCATIONAL_INSTITUTION' | 'HEALTH_INSTITUTION' | 'CULTURAL_ORGANIZATION' | 'SPORTS_ORGANIZATION' | 'PROFESSIONAL_ASSOCIATION' | 'OTHER';
  accountingSystem: 'NORMAL' | 'MINIMAL' | 'TRANSITIONAL';
  fiscalYearStart?: string;
  fiscalYearEnd?: string;
  baseCurrency: string;
  reportingCurrency?: string;
  annualRevenue?: number;
  employeeCount?: number;
  totalAssets?: number;
  meetsNormalSystemCriteria?: boolean;
  legalAddress?: string;
  headquartersAddress?: string;
  phoneNumber?: string;
  email?: string;
  website?: string;
  fundRestrictionPolicy?: string;
  donorRestrictionTracking?: boolean;
  temporarilyRestrictedFunds?: number;
  permanentlyRestrictedFunds?: number;
  unrestrictedFunds?: number;
  ohadaComplianceStatus?: 'COMPLIANT' | 'NON_COMPLIANT' | 'UNDER_REVIEW' | 'PENDING_AUDIT' | 'NEEDS_IMPROVEMENT';
  lastComplianceAudit?: Date;
  nextComplianceAudit?: Date;
  auditorName?: string;
  auditorLicenseNumber?: string;
  missionStatement?: string;
  programAreas?: any;
  geographicScope?: any;
  beneficiaryCount?: number;
  volunteerCount?: number;
  isActive?: boolean;
  createdBy?: string;
  approvedBy?: string;
  approvalDate?: Date;
  createdAt?: Date;
  updatedAt?: Date;
  
  // Nouvelles propriétés internationales
  countryCode?: string;
  regionCode?: string;
  accountingStandard?: string;
  localLanguage?: string;
  timeZone?: string;
  dateFormat?: string;
  numberFormat?: string;
  complianceRequirements?: any;
  reportingRequirements?: any;
  fundTypes?: string[];
  functionalAllocation?: {
    mission: number;
    administration: number;
    fundraising: number;
  };
}

export interface CreateSycebnlOrganizationRequest {
  organizationName: string;
  legalForm?: string;
  registrationNumber?: string;
  taxIdentificationNumber?: string;
  organizationType: string;
  accountingSystem: string;
  fiscalYearStart?: string;
  fiscalYearEnd?: string;
  baseCurrency: string;
  reportingCurrency?: string;
  annualRevenue?: number;
  employeeCount?: number;
  totalAssets?: number;
  legalAddress?: string;
  headquartersAddress?: string;
  phoneNumber?: string;
  email?: string;
  website?: string;
  fundRestrictionPolicy?: string;
  donorRestrictionTracking?: boolean;
  missionStatement?: string;
  programAreas?: any;
  geographicScope?: any;
  beneficiaryCount?: number;
  volunteerCount?: number;
}

export interface UpdateSycebnlOrganizationRequest extends Partial<CreateSycebnlOrganizationRequest> {
  id: number;
}

@Injectable({
  providedIn: 'root'
})
export class SycebnlOrganizationService {
  private apiUrl = `${environment.apiUrl}/sycebnl/organizations`;

  constructor(private http: HttpClient) {}

  /**
   * Obtenir toutes les organisations SYCEBNL
   */
  getAllSycebnlOrganizations(): Observable<SycebnlOrganization[]> {
    return this.http.get<{data: SycebnlOrganization[], status: string}>(this.apiUrl)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des organisations');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des organisations:', error);
          return of([]);
        })
      );
  }

  /**
   * Obtenir une organisation par ID
   */
  getSycebnlOrganizationById(id: number): Observable<SycebnlOrganization> {
    return this.http.get<{data: SycebnlOrganization, status: string}>(`${this.apiUrl}/${id}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération de l\'organisation');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération de l\'organisation:', error);
          throw error;
        })
      );
  }

  /**
   * Créer une nouvelle organisation
   */
  createSycebnlOrganization(organization: CreateSycebnlOrganizationRequest): Observable<SycebnlOrganization> {
    return this.http.post<{data: SycebnlOrganization, status: string}>(this.apiUrl, organization)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la création de l\'organisation');
        }),
        catchError(error => {
          console.error('Erreur lors de la création de l\'organisation:', error);
          throw error;
        })
      );
  }

  /**
   * Mettre à jour une organisation
   */
  updateSycebnlOrganization(organization: UpdateSycebnlOrganizationRequest): Observable<SycebnlOrganization> {
    return this.http.put<{data: SycebnlOrganization, status: string}>(`${this.apiUrl}/${organization.id}`, organization)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la mise à jour de l\'organisation');
        }),
        catchError(error => {
          console.error('Erreur lors de la mise à jour de l\'organisation:', error);
          throw error;
        })
      );
  }

  /**
   * Supprimer une organisation
   */
  deleteSycebnlOrganization(id: number): Observable<boolean> {
    return this.http.delete<{message: string, status: string}>(`${this.apiUrl}/${id}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return true;
          }
          throw new Error('Erreur lors de la suppression de l\'organisation');
        }),
        catchError(error => {
          console.error('Erreur lors de la suppression de l\'organisation:', error);
          return of(false);
        })
      );
  }

  /**
   * Obtenir les organisations par type
   */
  getSycebnlOrganizationsByType(type: string): Observable<SycebnlOrganization[]> {
    return this.http.get<{data: SycebnlOrganization[], status: string}>(`${this.apiUrl}/type/${type}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des organisations par type');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des organisations par type:', error);
          return of([]);
        })
      );
  }

  /**
   * Obtenir les organisations par système comptable
   */
  getSycebnlOrganizationsByAccountingSystem(system: string): Observable<SycebnlOrganization[]> {
    return this.http.get<{data: SycebnlOrganization[], status: string}>(`${this.apiUrl}/accounting-system/${system}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des organisations par système comptable');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des organisations par système comptable:', error);
          return of([]);
        })
      );
  }

  /**
   * Obtenir les organisations conformes OHADA
   */
  getCompliantSycebnlOrganizations(): Observable<SycebnlOrganization[]> {
    return this.http.get<{data: SycebnlOrganization[], status: string}>(`${this.apiUrl}/compliant`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des organisations conformes');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des organisations conformes:', error);
          return of([]);
        })
      );
  }

  /**
   * Obtenir les options pour les types d'organisations
   */
  getOrganizationTypes(): Array<{value: string, label: string}> {
    return [
      { value: 'ASSOCIATION', label: 'Association' },
      { value: 'FOUNDATION', label: 'Fondation' },
      { value: 'NGO', label: 'Organisation Non Gouvernementale' },
      { value: 'COOPERATIVE', label: 'Coopérative' },
      { value: 'MUTUAL', label: 'Mutuelle' },
      { value: 'RELIGIOUS_ORGANIZATION', label: 'Organisation religieuse' },
      { value: 'EDUCATIONAL_INSTITUTION', label: 'Institution éducative' },
      { value: 'HEALTH_INSTITUTION', label: 'Institution de santé' },
      { value: 'CULTURAL_ORGANIZATION', label: 'Organisation culturelle' },
      { value: 'SPORTS_ORGANIZATION', label: 'Organisation sportive' },
      { value: 'PROFESSIONAL_ASSOCIATION', label: 'Association professionnelle' },
      { value: 'OTHER', label: 'Autre' }
    ];
  }

  /**
   * Obtenir les options pour les systèmes comptables
   */
  getAccountingSystems(): Array<{value: string, label: string}> {
    return [
      { value: 'NORMAL', label: 'Système Normal (SN)' },
      { value: 'MINIMAL', label: 'Système Minimal de Trésorerie (SMT)' },
      { value: 'TRANSITIONAL', label: 'Système transitoire' }
    ];
  }

  /**
   * Obtenir les options pour les statuts de conformité
   */
  getComplianceStatuses(): Array<{value: string, label: string}> {
    return [
      { value: 'COMPLIANT', label: 'Conforme' },
      { value: 'NON_COMPLIANT', label: 'Non conforme' },
      { value: 'UNDER_REVIEW', label: 'En cours de révision' },
      { value: 'PENDING_AUDIT', label: 'Audit en attente' },
      { value: 'NEEDS_IMPROVEMENT', label: 'Amélioration nécessaire' }
    ];
  }

  /**
   * Obtenir l'icône du type d'organisation
   */
  getOrganizationTypeIcon(type: string): string {
    switch (type) {
      case 'ASSOCIATION': return 'group';
      case 'FOUNDATION': return 'account_balance';
      case 'NGO': return 'public';
      case 'COOPERATIVE': return 'handshake';
      case 'MUTUAL': return 'support';
      case 'RELIGIOUS_ORGANIZATION': return 'church';
      case 'EDUCATIONAL_INSTITUTION': return 'school';
      case 'HEALTH_INSTITUTION': return 'local_hospital';
      case 'CULTURAL_ORGANIZATION': return 'palette';
      case 'SPORTS_ORGANIZATION': return 'sports';
      case 'PROFESSIONAL_ASSOCIATION': return 'business';
      default: return 'business';
    }
  }

  /**
   * Obtenir l'icône du système comptable
   */
  getAccountingSystemIcon(system: string): string {
    switch (system) {
      case 'NORMAL': return 'account_balance';
      case 'MINIMAL': return 'account_balance_wallet';
      case 'TRANSITIONAL': return 'sync';
      default: return 'account_balance';
    }
  }

  /**
   * Obtenir la couleur du statut de conformité
   */
  getComplianceStatusColor(status: string): string {
    switch (status) {
      case 'COMPLIANT': return 'green';
      case 'NON_COMPLIANT': return 'red';
      case 'UNDER_REVIEW': return 'orange';
      case 'PENDING_AUDIT': return 'blue';
      case 'NEEDS_IMPROVEMENT': return 'yellow';
      default: return 'gray';
    }
  }

  /**
   * Obtenir l'icône du statut de conformité
   */
  getComplianceStatusIcon(status: string): string {
    switch (status) {
      case 'COMPLIANT': return 'check_circle';
      case 'NON_COMPLIANT': return 'cancel';
      case 'UNDER_REVIEW': return 'pending';
      case 'PENDING_AUDIT': return 'schedule';
      case 'NEEDS_IMPROVEMENT': return 'warning';
      default: return 'help';
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
}
