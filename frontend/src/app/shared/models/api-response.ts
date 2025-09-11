export interface ApiResponse<T = any> {
  success: boolean;
  message: string;
  data?: T;
  errors?: ApiError[];
  timestamp: Date;
  path?: string;
  status: number;
}

export interface ApiError {
  code: string;
  message: string;
  field?: string;
  details?: any;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface PageRequest {
  page: number;
  size: number;
  sort?: string[];
}

export interface SearchCriteria {
  filters: { [key: string]: any };
  search?: string;
  dateFrom?: Date;
  dateTo?: Date;
}
