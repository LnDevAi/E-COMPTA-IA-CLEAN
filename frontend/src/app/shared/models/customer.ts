export interface Customer {
  id: string;
  customerCode: string;
  customerType: CustomerType;
  customerSegment: CustomerSegment;
  acquisitionChannel: string;
  preferredChannels: string[];
  acquisitionDate: Date;
  lastInteractionDate?: Date;
  totalRevenueGenerated: number;
  averageOrderValue: number;
  purchaseFrequency: number;
  paymentBehavior: PaymentBehavior;
  aiScore: number;
  churnProbability: number;
  predictedLifetimeValue: number;
  satisfactionScore?: number;
  companyId: string;
  thirdPartyId: string;
  createdAt: Date;
  updatedAt: Date;
  isActive: boolean;
}

export enum CustomerType {
  INDIVIDUAL = 'INDIVIDUAL',
  BUSINESS = 'BUSINESS',
  GOVERNMENT = 'GOVERNMENT',
  NONPROFIT = 'NONPROFIT'
}

export enum CustomerSegment {
  HIGH_VALUE = 'HIGH_VALUE',
  MEDIUM_VALUE = 'MEDIUM_VALUE',
  LOW_VALUE = 'LOW_VALUE',
  NEW_CUSTOMER = 'NEW_CUSTOMER',
  LOYAL_CUSTOMER = 'LOYAL_CUSTOMER',
  AT_RISK = 'AT_RISK',
  DORMANT = 'DORMANT'
}

export enum PaymentBehavior {
  PROMPT = 'PROMPT',
  AVERAGE = 'AVERAGE',
  SLOW = 'SLOW',
  PROBLEMATIC = 'PROBLEMATIC'
}
