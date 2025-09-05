export interface User {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  password?: string;
  roles?: string[];
  isActive?: boolean;
  createdAt?: Date;
  updatedAt?: Date;
}

export interface UserProfile {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  fullName: string;
  roles: string[];
  permissions: string[];
  companyId?: number;
  companyName?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  user: UserProfile;
  expiresIn: number;
  refreshToken?: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  companyId?: number;
}

export interface PasswordChangeRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}





