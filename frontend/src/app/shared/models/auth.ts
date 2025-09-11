export interface LoginRequest {
  username: string;
  password: string;
  rememberMe?: boolean;
  mfaCode?: string;
}

import { User } from './user';

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  tokenType: string;
  user: User;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  companyName?: string;
  acceptTerms: boolean;
}

export interface RegisterResponse {
  message: string;
  userId: string;
  requiresVerification: boolean;
}

export interface PasswordResetRequest {
  email: string;
}

export interface PasswordResetResponse {
  message: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

// User interface moved to user.ts for consistency
