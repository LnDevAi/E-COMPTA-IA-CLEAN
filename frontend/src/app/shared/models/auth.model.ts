import { UserProfile, LoginRequest, RegisterRequest, PasswordChangeRequest } from '../interfaces/user.interface';

export interface AuthState {
  user: UserProfile | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  refreshToken: string | null;
  expiresAt: number | null;
}

export interface AuthContext {
  user: UserProfile | null;
  login: (credentials: LoginRequest) => Promise<boolean>;
  logout: () => void;
  register: (userData: RegisterRequest) => Promise<boolean>;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  clearError: () => void;
  refreshToken: () => Promise<boolean>;
  changePassword: (passwordData: PasswordChangeRequest) => Promise<boolean>;
}

export interface JwtPayload {
  sub: string;
  email: string;
  roles: string[];
  companyId?: number;
  iat: number;
  exp: number;
}

export interface TokenInfo {
  token: string;
  expiresAt: number;
  refreshToken?: string;
}

export interface AuthError {
  code: string;
  message: string;
  details?: any;
}
