import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface CountryConfig {
  code: string;
  name: string;
  nativeName: string;
  isActive: boolean;
  currency: {
    code: string;
    symbol: string;
    name: string;
    decimalPlaces: number;
  };
  accountingStandards: {
    primary: string;
    secondary?: string[];
    framework: string;
    version: string;
  };
  fiscalYear: {
    startMonth: number;
    endMonth: number;
    startDay: number;
  };
  compliance: {
    regulatoryBody: string;
    auditRequirements: string[];
    reportingFrequency: 'ANNUAL' | 'SEMI_ANNUAL' | 'QUARTERLY' | 'MONTHLY';
    filingDeadline: string;
  };
  localization: {
    language: string;
    dateFormat: string;
    numberFormat: string;
    timeZone: string;
  };
  organizationTypes: Array<{
    code: string;
    name: string;
    localName: string;
    description: string;
    legalRequirements: string[];
  }>;
  accountingSystems: Array<{
    code: string;
    name: string;
    localName: string;
    description: string;
    criteria: string[];
    thresholds: {
      revenue?: number;
      assets?: number;
      employees?: number;
    };
  }>;
  taxRegimes: Array<{
    code: string;
    name: string;
    description: string;
    rates: {
      corporate?: number;
      vat?: number;
      withholding?: number;
    };
  }>;
  reportingRequirements: {
    balanceSheet: boolean;
    incomeStatement: boolean;
    cashFlow: boolean;
    notes: boolean;
    auditReport: boolean;
    managementReport: boolean;
  };
}

export interface RegionConfig {
  code: string;
  name: string;
  countries: string[];
  commonStandards: string[];
  sharedRegulations: string[];
}

@Injectable({
  providedIn: 'root'
})
export class InternationalConfigService {
  private currentCountrySubject = new BehaviorSubject<string>('SN'); // Sénégal par défaut
  private currentRegionSubject = new BehaviorSubject<string>('OHADA');
  
  public currentCountry$ = this.currentCountrySubject.asObservable();
  public currentRegion$ = this.currentRegionSubject.asObservable();

  // Configuration des pays supportés
  private countryConfigs: Map<string, CountryConfig> = new Map([
    ['SN', {
      code: 'SN',
      name: 'Sénégal',
      nativeName: 'Sénégal',
      isActive: true,
      currency: {
        code: 'XOF',
        symbol: 'FCFA',
        name: 'Franc CFA',
        decimalPlaces: 0
      },
      accountingStandards: {
        primary: 'OHADA',
        secondary: ['IFRS'],
        framework: 'SYSCOA',
        version: '2022'
      },
      fiscalYear: {
        startMonth: 1,
        endMonth: 12,
        startDay: 1
      },
      compliance: {
        regulatoryBody: 'OHADA',
        auditRequirements: ['Audit légal', 'Certification comptable'],
        reportingFrequency: 'ANNUAL',
        filingDeadline: '31-03'
      },
      localization: {
        language: 'fr',
        dateFormat: 'DD/MM/YYYY',
        numberFormat: 'fr-FR',
        timeZone: 'Africa/Dakar'
      },
      organizationTypes: [
        {
          code: 'NGO',
          name: 'NGO',
          localName: 'ONG',
          description: 'Organisation Non Gouvernementale',
          legalRequirements: ['Enregistrement', 'Statuts', 'Conseil d\'administration']
        },
        {
          code: 'ASSOCIATION',
          name: 'Association',
          localName: 'Association',
          description: 'Association à but non lucratif',
          legalRequirements: ['Déclaration', 'Statuts', 'Assemblée générale']
        },
        {
          code: 'FOUNDATION',
          name: 'Foundation',
          localName: 'Fondation',
          description: 'Fondation d\'utilité publique',
          legalRequirements: ['Autorisation', 'Statuts', 'Conseil de surveillance']
        }
      ],
      accountingSystems: [
        {
          code: 'NORMAL',
          name: 'Normal System',
          localName: 'Système Normal',
          description: 'Système comptable normal OHADA',
          criteria: ['Chiffre d\'affaires > 100M XOF', 'Actif > 50M XOF', 'Employés > 10'],
          thresholds: {
            revenue: 100000000,
            assets: 50000000,
            employees: 10
          }
        },
        {
          code: 'MINIMAL',
          name: 'Minimal System',
          localName: 'Système Minimal',
          description: 'Système minimal de trésorerie',
          criteria: ['Chiffre d\'affaires < 100M XOF', 'Actif < 50M XOF', 'Employés < 10'],
          thresholds: {
            revenue: 100000000,
            assets: 50000000,
            employees: 10
          }
        }
      ],
      taxRegimes: [
        {
          code: 'NON_PROFIT',
          name: 'Non-Profit',
          localName: 'Sans but lucratif',
          description: 'Régime fiscal des organisations à but non lucratif',
          rates: {
            corporate: 0,
            vat: 0,
            withholding: 0
          }
        }
      ],
      reportingRequirements: {
        balanceSheet: true,
        incomeStatement: true,
        cashFlow: true,
        notes: true,
        auditReport: true,
        managementReport: true
      }
    }],
    ['CI', {
      code: 'CI',
      name: 'Côte d\'Ivoire',
      nativeName: 'Côte d\'Ivoire',
      isActive: true,
      currency: {
        code: 'XOF',
        symbol: 'FCFA',
        name: 'Franc CFA',
        decimalPlaces: 0
      },
      accountingStandards: {
        primary: 'OHADA',
        secondary: ['IFRS'],
        framework: 'SYSCOA',
        version: '2022'
      },
      fiscalYear: {
        startMonth: 1,
        endMonth: 12,
        startDay: 1
      },
      compliance: {
        regulatoryBody: 'OHADA',
        auditRequirements: ['Audit légal', 'Certification comptable'],
        reportingFrequency: 'ANNUAL',
        filingDeadline: '31-03'
      },
      localization: {
        language: 'fr',
        dateFormat: 'DD/MM/YYYY',
        numberFormat: 'fr-FR',
        timeZone: 'Africa/Abidjan'
      },
      organizationTypes: [
        {
          code: 'NGO',
          name: 'NGO',
          localName: 'ONG',
          description: 'Organisation Non Gouvernementale',
          legalRequirements: ['Enregistrement', 'Statuts', 'Conseil d\'administration']
        },
        {
          code: 'ASSOCIATION',
          name: 'Association',
          localName: 'Association',
          description: 'Association à but non lucratif',
          legalRequirements: ['Déclaration', 'Statuts', 'Assemblée générale']
        }
      ],
      accountingSystems: [
        {
          code: 'NORMAL',
          name: 'Normal System',
          localName: 'Système Normal',
          description: 'Système comptable normal OHADA',
          criteria: ['Chiffre d\'affaires > 100M XOF', 'Actif > 50M XOF', 'Employés > 10'],
          thresholds: {
            revenue: 100000000,
            assets: 50000000,
            employees: 10
          }
        },
        {
          code: 'MINIMAL',
          name: 'Minimal System',
          localName: 'Système Minimal',
          description: 'Système minimal de trésorerie',
          criteria: ['Chiffre d\'affaires < 100M XOF', 'Actif < 50M XOF', 'Employés < 10'],
          thresholds: {
            revenue: 100000000,
            assets: 50000000,
            employees: 10
          }
        }
      ],
      taxRegimes: [
        {
          code: 'NON_PROFIT',
          name: 'Non-Profit',
          localName: 'Sans but lucratif',
          description: 'Régime fiscal des organisations à but non lucratif',
          rates: {
            corporate: 0,
            vat: 0,
            withholding: 0
          }
        }
      ],
      reportingRequirements: {
        balanceSheet: true,
        incomeStatement: true,
        cashFlow: true,
        notes: true,
        auditReport: true,
        managementReport: true
      }
    }],
    ['FR', {
      code: 'FR',
      name: 'France',
      nativeName: 'France',
      isActive: true,
      currency: {
        code: 'EUR',
        symbol: '€',
        name: 'Euro',
        decimalPlaces: 2
      },
      accountingStandards: {
        primary: 'PCG',
        secondary: ['IFRS'],
        framework: 'Plan Comptable Général',
        version: '2022'
      },
      fiscalYear: {
        startMonth: 1,
        endMonth: 12,
        startDay: 1
      },
      compliance: {
        regulatoryBody: 'ACPR',
        auditRequirements: ['Commissaire aux comptes', 'Certification'],
        reportingFrequency: 'ANNUAL',
        filingDeadline: '31-05'
      },
      localization: {
        language: 'fr',
        dateFormat: 'DD/MM/YYYY',
        numberFormat: 'fr-FR',
        timeZone: 'Europe/Paris'
      },
      organizationTypes: [
        {
          code: 'ASSOCIATION',
          name: 'Association',
          localName: 'Association',
          description: 'Association loi 1901',
          legalRequirements: ['Déclaration préfecture', 'Statuts', 'Assemblée générale']
        },
        {
          code: 'FOUNDATION',
          name: 'Foundation',
          localName: 'Fondation',
          description: 'Fondation reconnue d\'utilité publique',
          legalRequirements: ['Décret', 'Statuts', 'Conseil d\'administration']
        }
      ],
      accountingSystems: [
        {
          code: 'NORMAL',
          name: 'Normal System',
          localName: 'Système Normal',
          description: 'Système comptable normal français',
          criteria: ['Chiffre d\'affaires > 8M EUR', 'Actif > 4M EUR', 'Employés > 50'],
          thresholds: {
            revenue: 8000000,
            assets: 4000000,
            employees: 50
          }
        },
        {
          code: 'SIMPLIFIED',
          name: 'Simplified System',
          localName: 'Système Simplifié',
          description: 'Système comptable simplifié',
          criteria: ['Chiffre d\'affaires < 8M EUR', 'Actif < 4M EUR', 'Employés < 50'],
          thresholds: {
            revenue: 8000000,
            assets: 4000000,
            employees: 50
          }
        }
      ],
      taxRegimes: [
        {
          code: 'NON_PROFIT',
          name: 'Non-Profit',
          localName: 'Sans but lucratif',
          description: 'Régime fiscal des associations',
          rates: {
            corporate: 0,
            vat: 0,
            withholding: 0
          }
        }
      ],
      reportingRequirements: {
        balanceSheet: true,
        incomeStatement: true,
        cashFlow: false,
        notes: true,
        auditReport: true,
        managementReport: true
      }
    }],
    ['US', {
      code: 'US',
      name: 'United States',
      nativeName: 'United States',
      isActive: true,
      currency: {
        code: 'USD',
        symbol: '$',
        name: 'US Dollar',
        decimalPlaces: 2
      },
      accountingStandards: {
        primary: 'GAAP',
        secondary: ['IFRS'],
        framework: 'US Generally Accepted Accounting Principles',
        version: '2022'
      },
      fiscalYear: {
        startMonth: 1,
        endMonth: 12,
        startDay: 1
      },
      compliance: {
        regulatoryBody: 'IRS',
        auditRequirements: ['CPA Audit', 'Form 990'],
        reportingFrequency: 'ANNUAL',
        filingDeadline: '15-05'
      },
      localization: {
        language: 'en',
        dateFormat: 'MM/DD/YYYY',
        numberFormat: 'en-US',
        timeZone: 'America/New_York'
      },
      organizationTypes: [
        {
          code: 'NGO',
          name: 'NGO',
          localName: 'Non-Governmental Organization',
          description: '501(c)(3) tax-exempt organization',
          legalRequirements: ['IRS Registration', 'Bylaws', 'Board of Directors']
        },
        {
          code: 'FOUNDATION',
          name: 'Foundation',
          localName: 'Foundation',
          description: 'Private or public foundation',
          legalRequirements: ['IRS Registration', 'Articles of Incorporation', 'Board of Trustees']
        }
      ],
      accountingSystems: [
        {
          code: 'NORMAL',
          name: 'Full System',
          localName: 'Full Accounting System',
          description: 'Full GAAP accounting system',
          criteria: ['Revenue > $1M', 'Assets > $500K', 'Employees > 10'],
          thresholds: {
            revenue: 1000000,
            assets: 500000,
            employees: 10
          }
        },
        {
          code: 'CASH',
          name: 'Cash Basis',
          localName: 'Cash Basis Accounting',
          description: 'Cash basis accounting for small organizations',
          criteria: ['Revenue < $1M', 'Assets < $500K', 'Employees < 10'],
          thresholds: {
            revenue: 1000000,
            assets: 500000,
            employees: 10
          }
        }
      ],
      taxRegimes: [
        {
          code: 'TAX_EXEMPT',
          name: 'Tax Exempt',
          localName: 'Tax Exempt',
          description: '501(c)(3) tax-exempt status',
          rates: {
            corporate: 0,
            vat: 0,
            withholding: 0
          }
        }
      ],
      reportingRequirements: {
        balanceSheet: true,
        incomeStatement: true,
        cashFlow: true,
        notes: true,
        auditReport: true,
        managementReport: true
      }
    }]
  ]);

  // Configuration des régions
  private regionConfigs: Map<string, RegionConfig> = new Map([
    ['OHADA', {
      code: 'OHADA',
      name: 'OHADA',
      countries: ['SN', 'CI', 'BF', 'ML', 'NE', 'TD', 'CM', 'CF', 'CG', 'CD', 'GA', 'GQ', 'BJ', 'TG'],
      commonStandards: ['SYSCOA', 'OHADA Uniform Act'],
      sharedRegulations: ['Audit requirements', 'Filing deadlines', 'Tax regimes']
    }],
    ['EU', {
      code: 'EU',
      name: 'European Union',
      countries: ['FR', 'DE', 'IT', 'ES', 'NL', 'BE', 'LU', 'AT', 'PT', 'IE', 'FI', 'GR'],
      commonStandards: ['IFRS', 'EU Accounting Directive'],
      sharedRegulations: ['EU regulations', 'Cross-border reporting', 'VAT rules']
    }],
    ['NORTH_AMERICA', {
      code: 'NORTH_AMERICA',
      name: 'North America',
      countries: ['US', 'CA', 'MX'],
      commonStandards: ['GAAP', 'IFRS'],
      sharedRegulations: ['Tax treaties', 'Cross-border compliance', 'Audit standards']
    }]
  ]);

  constructor() {
    // Initialiser avec le pays par défaut
    this.setCurrentCountry('SN');
  }

  /**
   * Obtenir la configuration du pays actuel
   */
  getCurrentCountryConfig(): CountryConfig {
    const countryCode = this.currentCountrySubject.value;
    return this.countryConfigs.get(countryCode) || this.countryConfigs.get('SN')!;
  }

  /**
   * Obtenir la configuration d'un pays spécifique
   */
  getCountryConfig(countryCode: string): CountryConfig | undefined {
    return this.countryConfigs.get(countryCode);
  }

  /**
   * Obtenir toutes les configurations de pays disponibles
   */
  getAllCountryConfigs(): CountryConfig[] {
    return Array.from(this.countryConfigs.values());
  }

  /**
   * Obtenir la configuration de la région actuelle
   */
  getCurrentRegionConfig(): RegionConfig {
    const regionCode = this.currentRegionSubject.value;
    return this.regionConfigs.get(regionCode) || this.regionConfigs.get('OHADA')!;
  }

  /**
   * Obtenir la configuration d'une région spécifique
   */
  getRegionConfig(regionCode: string): RegionConfig | undefined {
    return this.regionConfigs.get(regionCode);
  }

  /**
   * Obtenir toutes les configurations de régions disponibles
   */
  getAllRegionConfigs(): RegionConfig[] {
    return Array.from(this.regionConfigs.values());
  }

  /**
   * Définir le pays actuel
   */
  setCurrentCountry(countryCode: string): void {
    if (this.countryConfigs.has(countryCode)) {
      this.currentCountrySubject.next(countryCode);
      
      // Mettre à jour la région automatiquement
      const region = this.getRegionForCountry(countryCode);
      if (region) {
        this.currentRegionSubject.next(region);
      }
    }
  }

  /**
   * Définir la région actuelle
   */
  setCurrentRegion(regionCode: string): void {
    if (this.regionConfigs.has(regionCode)) {
      this.currentRegionSubject.next(regionCode);
    }
  }

  /**
   * Obtenir la région pour un pays donné
   */
  private getRegionForCountry(countryCode: string): string | undefined {
    for (const [regionCode, regionConfig] of this.regionConfigs) {
      if (regionConfig.countries.includes(countryCode)) {
        return regionCode;
      }
    }
    return undefined;
  }

  /**
   * Obtenir les types d'organisations pour le pays actuel
   */
  getCurrentOrganizationTypes(): Array<{code: string, name: string, localName: string, description: string, legalRequirements: string[]}> {
    return this.getCurrentCountryConfig().organizationTypes;
  }

  /**
   * Obtenir les systèmes comptables pour le pays actuel
   */
  getCurrentAccountingSystems(): Array<{code: string, name: string, localName: string, description: string, criteria: string[], thresholds: any}> {
    return this.getCurrentCountryConfig().accountingSystems;
  }

  /**
   * Obtenir les régimes fiscaux pour le pays actuel
   */
  getCurrentTaxRegimes(): Array<{code: string, name: string, description: string, rates: any}> {
    return this.getCurrentCountryConfig().taxRegimes;
  }

  /**
   * Déterminer le système comptable approprié basé sur les critères
   */
  determineAccountingSystem(revenue: number, assets: number, employees: number): string {
    const config = this.getCurrentCountryConfig();
    const systems = config.accountingSystems;
    
    for (const system of systems) {
      const thresholds = system.thresholds;
      if (thresholds.revenue && revenue > thresholds.revenue) continue;
      if (thresholds.assets && assets > thresholds.assets) continue;
      if (thresholds.employees && employees > thresholds.employees) continue;
      
      return system.code;
    }
    
    // Par défaut, retourner le système normal
    return 'NORMAL';
  }

  /**
   * Formater la devise selon le pays actuel
   */
  formatCurrency(amount: number, currencyCode?: string): string {
    const config = this.getCurrentCountryConfig();
    const currency = currencyCode ? 
      this.getCurrencyConfig(currencyCode) : 
      config.currency;
    
    if (!currency) return amount.toString();
    
    return new Intl.NumberFormat(config.localization.numberFormat, {
      style: 'currency',
      currency: currency.code,
      minimumFractionDigits: currency.decimalPlaces,
      maximumFractionDigits: currency.decimalPlaces
    }).format(amount);
  }

  /**
   * Obtenir la configuration d'une devise
   */
  private getCurrencyConfig(currencyCode: string): any {
    for (const config of this.countryConfigs.values()) {
      if (config.currency.code === currencyCode) {
        return config.currency;
      }
    }
    return null;
  }

  /**
   * Formater la date selon le pays actuel
   */
  formatDate(date: Date | string): string {
    const config = this.getCurrentCountryConfig();
    const dateObj = typeof date === 'string' ? new Date(date) : date;
    
    return new Intl.DateTimeFormat(config.localization.numberFormat, {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    }).format(dateObj);
  }

  /**
   * Obtenir les exigences de conformité pour le pays actuel
   */
  getCurrentComplianceRequirements(): any {
    return this.getCurrentCountryConfig().compliance;
  }

  /**
   * Obtenir les exigences de reporting pour le pays actuel
   */
  getCurrentReportingRequirements(): any {
    return this.getCurrentCountryConfig().reportingRequirements;
  }

  /**
   * Vérifier si un pays est supporté
   */
  isCountrySupported(countryCode: string): boolean {
    return this.countryConfigs.has(countryCode);
  }

  /**
   * Obtenir les pays d'une région
   */
  getCountriesInRegion(regionCode: string): string[] {
    const region = this.regionConfigs.get(regionCode);
    return region ? region.countries : [];
  }

  /**
   * Obtenir les standards communs d'une région
   */
  getCommonStandardsInRegion(regionCode: string): string[] {
    const region = this.regionConfigs.get(regionCode);
    return region ? region.commonStandards : [];
  }
}
