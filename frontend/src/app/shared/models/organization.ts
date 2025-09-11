export interface Organization {
  id: string;
  organizationName: string;
  organizationType: OrganizationType;
  registrationNumber: string;
  taxExemptionNumber?: string;
  legalRepresentative: string;
  contactEmail: string;
  contactPhone: string;
  address: string;
  city: string;
  country: string;
  website?: string;
  missionStatement?: string;
  foundingDate: Date;
  memberCount: number;
  annualBudget: number;
  fundingSources: string[];
  complianceRequirements: string[];
  donorRestrictionTracking: boolean;
  grantManagementEnabled: boolean;
  volunteerTrackingEnabled: boolean;
  impactMeasurementEnabled: boolean;
  companyId: string;
  createdAt: Date;
  updatedAt: Date;
  isActive: boolean;
}

export enum OrganizationType {
  ASSOCIATION = 'ASSOCIATION',
  FOUNDATION = 'FOUNDATION',
  NGO = 'NGO',
  COOPERATIVE = 'COOPERATIVE',
  UNION = 'UNION',
  RELIGIOUS = 'RELIGIOUS',
  EDUCATIONAL = 'EDUCATIONAL',
  CULTURAL = 'CULTURAL',
  SPORTS = 'SPORTS',
  HEALTH = 'HEALTH',
  ENVIRONMENTAL = 'ENVIRONMENTAL',
  SOCIAL = 'SOCIAL',
  PROFESSIONAL = 'PROFESSIONAL',
  OTHER = 'OTHER'
}
