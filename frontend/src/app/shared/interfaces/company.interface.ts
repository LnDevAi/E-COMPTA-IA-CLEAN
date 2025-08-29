export interface Company {
  id?: number;
  name: string;
  legalName?: string;
  registrationNumber?: string;
  taxNumber?: string;
  address?: string;
  city?: string;
  country?: string;
  countryCode?: string;
  phone?: string;
  email?: string;
  website?: string;
  currency?: string;
  accountingStandard?: string;
  fiscalYearStart?: Date;
  fiscalYearEnd?: Date;
  isActive?: boolean;
  createdAt?: Date;
  updatedAt?: Date;
}

export interface CompanySubscription {
  id?: number;
  companyId: number;
  planId: number;
  planName: string;
  status: 'ACTIVE' | 'INACTIVE' | 'EXPIRED' | 'CANCELLED';
  startDate: Date;
  endDate: Date;
  maxUsers: number;
  maxStorage: number;
  features: string[];
  price: number;
  currency: string;
  billingCycle: 'MONTHLY' | 'QUARTERLY' | 'YEARLY';
  autoRenew: boolean;
  createdAt?: Date;
  updatedAt?: Date;
}

export interface CompanyConfig {
  id?: number;
  companyId: number;
  configKey: string;
  configValue: string;
  configType: 'STRING' | 'NUMBER' | 'BOOLEAN' | 'JSON';
  description?: string;
  isSystem?: boolean;
  createdAt?: Date;
  updatedAt?: Date;
}


