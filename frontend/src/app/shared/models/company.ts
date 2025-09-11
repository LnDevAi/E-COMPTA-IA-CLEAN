export interface Company {
  id: string;
  name: string;
  businessType: string;
  registrationNumber: string;
  taxId: string;
  email: string;
  phone: string;
  address: string;
  city: string;
  country: string;
  currency: string;
  isActive: boolean;
  createdAt: Date;
  updatedAt: Date;
  subscriptionPlan?: SubscriptionPlan;
  localeSettings?: LocaleSettings;
}

export interface SubscriptionPlan {
  id: number;
  name: string;
  description?: string;
  monthlyPrice: number;
  yearlyPrice: number;
  maxUsers: number;
  features: string[];
  isActive: boolean;
}

export interface LocaleSettings {
  id: number;
  language: string;
  country: string;
  currency: string;
  dateFormat: string;
  timeFormat: string;
  numberFormat: string;
  timezone: string;
}
