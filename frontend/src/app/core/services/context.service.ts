import { Injectable } from '@angular/core';

export interface AccountingContext {
  companyId: number;
  countryCode: string; // ISO 3166-1 alpha-2
  accountingStandard: string; // OHADA, IFRS, GAAP
  currency: string; // ISO 4217
}

@Injectable({ providedIn: 'root' })
export class ContextService {
  private readonly STORAGE_KEY = 'ecomptaia_context';

  private context: AccountingContext = this.loadContext();

  getContext(): AccountingContext {
    return this.context;
  }

  setContext(partial: Partial<AccountingContext>): void {
    this.context = { ...this.context, ...partial };
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(this.context));
  }

  private loadContext(): AccountingContext {
    try {
      const stored = localStorage.getItem(this.STORAGE_KEY);
      if (stored) {
        return JSON.parse(stored);
      }
    } catch {}
    return {
      companyId: 1,
      countryCode: 'CI',
      accountingStandard: 'OHADA',
      currency: 'XOF'
    };
  }
}

