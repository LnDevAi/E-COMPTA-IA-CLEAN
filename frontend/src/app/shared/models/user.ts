export interface User {
  id: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  isActive: boolean;
  createdAt: Date;
  updatedAt: Date;
  lastLogin?: Date;
  failedLoginAttempts: number;
  accountLocked: boolean;
  passwordExpiresAt?: Date;
  multiFactorEnabled: boolean;
  preferredLanguage: string;
  timezone: string;
  company: Company;
  roles: Role[];
}

export interface Role {
  id: number;
  name: string;
  description?: string;
  permissions: Permission[];
  isActive: boolean;
  createdAt: Date;
  updatedAt: Date;
}

export interface Permission {
  id: number;
  name: string;
  description?: string;
  resource: string;
  action: string;
}

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
}
