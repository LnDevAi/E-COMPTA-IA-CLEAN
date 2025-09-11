import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { InternationalConfigService, CountryConfig } from './international-config.service';

export interface AccountingStandard {
  code: string;
  name: string;
  localName: string;
  description: string;
  version: string;
  effectiveDate: string;
  framework: string;
  applicableCountries: string[];
  chartOfAccounts: ChartOfAccountsConfig;
  financialStatements: FinancialStatementsConfig;
  specificRules: SpecificRulesConfig;
}

export interface ChartOfAccountsConfig {
  structure: {
    classes: AccountClass[];
    subClasses: AccountSubClass[];
    accounts: AccountConfig[];
  };
  numbering: {
    pattern: string;
    length: number;
    separator: string;
  };
  validation: {
    requiredAccounts: string[];
    prohibitedAccounts: string[];
    mandatorySubAccounts: string[];
  };
}

export interface AccountClass {
  code: string;
  name: string;
  localName: string;
  description: string;
  type: 'ASSET' | 'LIABILITY' | 'EQUITY' | 'REVENUE' | 'EXPENSE';
  ngoSpecific: boolean;
  mandatory: boolean;
}

export interface AccountSubClass {
  code: string;
  parentClass: string;
  name: string;
  localName: string;
  description: string;
  ngoSpecific: boolean;
}

export interface AccountConfig {
  code: string;
  parentSubClass: string;
  name: string;
  localName: string;
  description: string;
  type: 'ASSET' | 'LIABILITY' | 'EQUITY' | 'REVENUE' | 'EXPENSE';
  ngoSpecific: boolean;
  mandatory: boolean;
  restrictions: {
    fundRestriction?: boolean;
    projectRestriction?: boolean;
    timeRestriction?: boolean;
  };
  reporting: {
    balanceSheet: boolean;
    incomeStatement: boolean;
    cashFlow: boolean;
    notes: boolean;
  };
}

export interface FinancialStatementsConfig {
  balanceSheet: {
    format: 'SYCEBNL' | 'IFRS' | 'GAAP' | 'PCG';
    structure: BalanceSheetStructure;
    requiredNotes: string[];
  };
  incomeStatement: {
    format: 'SYCEBNL' | 'IFRS' | 'GAAP' | 'PCG';
    structure: IncomeStatementStructure;
    functionalAllocation: boolean;
    requiredNotes: string[];
  };
  cashFlow: {
    required: boolean;
    format: 'DIRECT' | 'INDIRECT';
    structure: CashFlowStructure;
  };
  notes: {
    required: boolean;
    minimumNotes: number;
    ngoSpecificNotes: string[];
  };
}

export interface BalanceSheetStructure {
  assets: {
    currentAssets: string[];
    nonCurrentAssets: string[];
    totalAssets: string;
  };
  liabilities: {
    currentLiabilities: string[];
    nonCurrentLiabilities: string[];
    totalLiabilities: string;
  };
  equity: {
    funds: string[];
    reserves: string[];
    retainedEarnings: string;
    totalEquity: string;
  };
}

export interface IncomeStatementStructure {
  resources: {
    grants: string[];
    donations: string[];
    membershipFees: string[];
    otherResources: string[];
    totalResources: string;
  };
  expenses: {
    missionExpenses: string[];
    administrativeExpenses: string[];
    fundraisingExpenses: string[];
    totalExpenses: string;
  };
  netResult: string;
}

export interface CashFlowStructure {
  operatingActivities: string[];
  investingActivities: string[];
  financingActivities: string[];
  netCashFlow: string;
}

export interface SpecificRulesConfig {
  fundAccounting: {
    required: boolean;
    types: FundType[];
    restrictions: FundRestriction[];
  };
  functionalAllocation: {
    required: boolean;
    minimums: {
      mission: number;
      administration: number;
      fundraising: number;
    };
  };
  depreciation: {
    method: 'STRAIGHT_LINE' | 'DECLINING_BALANCE' | 'UNITS_OF_PRODUCTION';
    rates: DepreciationRate[];
  };
  inventory: {
    valuation: 'FIFO' | 'LIFO' | 'WEIGHTED_AVERAGE' | 'SPECIFIC_IDENTIFICATION';
    writeDown: boolean;
  };
  revenueRecognition: {
    grants: 'CASH' | 'ACCRUAL' | 'CONDITIONAL';
    donations: 'CASH' | 'ACCRUAL';
    services: 'CASH' | 'ACCRUAL';
  };
}

export interface FundType {
  code: string;
  name: string;
  localName: string;
  description: string;
  restriction: 'UNRESTRICTED' | 'TEMPORARILY_RESTRICTED' | 'PERMANENTLY_RESTRICTED';
}

export interface FundRestriction {
  code: string;
  name: string;
  localName: string;
  description: string;
  type: 'PROJECT' | 'TIME' | 'PURPOSE' | 'GEOGRAPHIC';
}

export interface DepreciationRate {
  assetType: string;
  rate: number;
  minimumYears: number;
  maximumYears: number;
}

@Injectable({
  providedIn: 'root'
})
export class AccountingStandardsService {
  private currentStandardSubject = new BehaviorSubject<string>('SYCEBNL');
  public currentStandard$ = this.currentStandardSubject.asObservable();

  // Standards comptables supportés
  private standards: Map<string, AccountingStandard> = new Map([
    ['SYCEBNL', {
      code: 'SYCEBNL',
      name: 'SYCEBNL',
      localName: 'Système Comptable des Entités à But Non Lucratif',
      description: 'Système comptable OHADA pour les ONG et associations',
      version: '2024',
      effectiveDate: '2024-01-01',
      framework: 'OHADA',
      applicableCountries: ['SN', 'CI', 'BF', 'ML', 'NE', 'TD', 'CM', 'CF', 'CG', 'CD', 'GA', 'GQ', 'BJ', 'TG'],
      chartOfAccounts: {
        structure: {
          classes: [
            {
              code: '1',
              name: 'Durable Resources',
              localName: 'Ressources Durables',
              description: 'Fonds associatifs, subventions, emprunts',
              type: 'EQUITY',
              ngoSpecific: true,
              mandatory: true
            },
            {
              code: '2',
              name: 'Fixed Assets',
              localName: 'Immobilisations',
              description: 'Immobilisations corporelles et incorporelles',
              type: 'ASSET',
              ngoSpecific: false,
              mandatory: true
            },
            {
              code: '3',
              name: 'Inventories',
              localName: 'Stocks',
              description: 'Stocks et en-cours',
              type: 'ASSET',
              ngoSpecific: false,
              mandatory: true
            },
            {
              code: '4',
              name: 'Receivables and Payables',
              localName: 'Créances et Dettes',
              description: 'Créances et dettes diverses',
              type: 'ASSET',
              ngoSpecific: false,
              mandatory: true
            },
            {
              code: '5',
              name: 'Financial Accounts',
              localName: 'Comptes Financiers',
              description: 'Banques, caisse, placements',
              type: 'ASSET',
              ngoSpecific: false,
              mandatory: true
            },
            {
              code: '6',
              name: 'Expenses',
              localName: 'Charges',
              description: 'Charges d\'exploitation et financières',
              type: 'EXPENSE',
              ngoSpecific: true,
              mandatory: true
            },
            {
              code: '7',
              name: 'Resources',
              localName: 'Ressources',
              description: 'Ressources d\'exploitation',
              type: 'REVENUE',
              ngoSpecific: true,
              mandatory: true
            }
          ],
          subClasses: [
            {
              code: '10',
              parentClass: '1',
              name: 'Associative Funds',
              localName: 'Fonds Associatifs',
              description: 'Fonds libres et affectés',
              ngoSpecific: true
            },
            {
              code: '74',
              parentClass: '7',
              name: 'Operating Grants',
              localName: 'Subventions d\'Exploitation',
              description: 'Subventions de fonctionnement',
              ngoSpecific: true
            },
            {
              code: '75',
              parentClass: '7',
              name: 'Other Operating Resources',
              localName: 'Autres Ressources d\'Exploitation',
              description: 'Dons, cotisations, legs',
              ngoSpecific: true
            }
          ],
          accounts: [
            {
              code: '102',
              parentSubClass: '10',
              name: 'Free Funds',
              localName: 'Fonds Libres',
              description: 'Fonds sans restriction d\'emploi',
              type: 'EQUITY',
              ngoSpecific: true,
              mandatory: true,
              restrictions: {
                fundRestriction: false,
                projectRestriction: false,
                timeRestriction: false
              },
              reporting: {
                balanceSheet: true,
                incomeStatement: false,
                cashFlow: false,
                notes: true
              }
            },
            {
              code: '103',
              parentSubClass: '10',
              name: 'Restricted Funds',
              localName: 'Fonds Affectés',
              description: 'Fonds avec restriction d\'emploi',
              type: 'EQUITY',
              ngoSpecific: true,
              mandatory: true,
              restrictions: {
                fundRestriction: true,
                projectRestriction: true,
                timeRestriction: true
              },
              reporting: {
                balanceSheet: true,
                incomeStatement: false,
                cashFlow: false,
                notes: true
              }
            },
            {
              code: '7401',
              parentSubClass: '74',
              name: 'State Grants',
              localName: 'Subventions de l\'État',
              description: 'Subventions des pouvoirs publics',
              type: 'REVENUE',
              ngoSpecific: true,
              mandatory: false,
              restrictions: {
                fundRestriction: true,
                projectRestriction: true,
                timeRestriction: false
              },
              reporting: {
                balanceSheet: false,
                incomeStatement: true,
                cashFlow: true,
                notes: true
              }
            },
            {
              code: '7501',
              parentSubClass: '75',
              name: 'Manual Donations',
              localName: 'Dons Manuels',
              description: 'Dons en espèces reçus',
              type: 'REVENUE',
              ngoSpecific: true,
              mandatory: false,
              restrictions: {
                fundRestriction: false,
                projectRestriction: false,
                timeRestriction: false
              },
              reporting: {
                balanceSheet: false,
                incomeStatement: true,
                cashFlow: true,
                notes: true
              }
            }
          ]
        },
        numbering: {
          pattern: 'XXX',
          length: 3,
          separator: ''
        },
        validation: {
          requiredAccounts: ['102', '103', '7401', '7501'],
          prohibitedAccounts: [],
          mandatorySubAccounts: ['102', '103']
        }
      },
      financialStatements: {
        balanceSheet: {
          format: 'SYCEBNL',
          structure: {
            assets: {
              currentAssets: ['3', '4', '5'],
              nonCurrentAssets: ['2'],
              totalAssets: 'TOTAL_ASSETS'
            },
            liabilities: {
              currentLiabilities: ['4'],
              nonCurrentLiabilities: ['1'],
              totalLiabilities: 'TOTAL_LIABILITIES'
            },
            equity: {
              funds: ['102', '103'],
              reserves: ['11'],
              retainedEarnings: '12',
              totalEquity: 'TOTAL_EQUITY'
            }
          },
          requiredNotes: ['FUND_RESTRICTIONS', 'GRANTS_DETAIL', 'FIXED_ASSETS', 'INVESTMENTS']
        },
        incomeStatement: {
          format: 'SYCEBNL',
          structure: {
            resources: {
              grants: ['74'],
              donations: ['7501', '7502', '7503'],
              membershipFees: ['755'],
              otherResources: ['75'],
              totalResources: 'TOTAL_RESOURCES'
            },
            expenses: {
              missionExpenses: ['6'],
              administrativeExpenses: ['6'],
              fundraisingExpenses: ['6'],
              totalExpenses: 'TOTAL_EXPENSES'
            },
            netResult: 'NET_RESULT'
          },
          functionalAllocation: true,
          requiredNotes: ['FUNCTIONAL_ALLOCATION', 'GRANT_DETAILS', 'DONATION_DETAILS']
        },
        cashFlow: {
          required: true,
          format: 'DIRECT',
          structure: {
            operatingActivities: ['OPERATING_CASH_FLOW'],
            investingActivities: ['INVESTING_CASH_FLOW'],
            financingActivities: ['FINANCING_CASH_FLOW'],
            netCashFlow: 'NET_CASH_FLOW'
          }
        },
        notes: {
          required: true,
          minimumNotes: 30,
          ngoSpecificNotes: [
            'FUND_RESTRICTIONS',
            'GRANT_CONDITIONS',
            'DONATION_POLICIES',
            'FUNCTIONAL_ALLOCATION',
            'PROGRAM_EFFECTIVENESS',
            'GOVERNANCE_STRUCTURE',
            'COMPLIANCE_STATUS',
            'AUDIT_INFORMATION'
          ]
        }
      },
      specificRules: {
        fundAccounting: {
          required: true,
          types: [
            {
              code: 'UNRESTRICTED',
              name: 'Unrestricted Funds',
              localName: 'Fonds Libres',
              description: 'Fonds sans restriction d\'emploi',
              restriction: 'UNRESTRICTED'
            },
            {
              code: 'TEMPORARILY_RESTRICTED',
              name: 'Temporarily Restricted Funds',
              localName: 'Fonds Temporairement Affectés',
              description: 'Fonds avec restriction temporelle',
              restriction: 'TEMPORARILY_RESTRICTED'
            },
            {
              code: 'PERMANENTLY_RESTRICTED',
              name: 'Permanently Restricted Funds',
              localName: 'Fonds Permanemment Affectés',
              description: 'Fonds avec restriction permanente',
              restriction: 'PERMANENTLY_RESTRICTED'
            }
          ],
          restrictions: [
            {
              code: 'PROJECT',
              name: 'Project Restriction',
              localName: 'Restriction Projet',
              description: 'Fonds affectés à un projet spécifique',
              type: 'PROJECT'
            },
            {
              code: 'TIME',
              name: 'Time Restriction',
              localName: 'Restriction Temporelle',
              description: 'Fonds avec restriction de temps',
              type: 'TIME'
            },
            {
              code: 'PURPOSE',
              name: 'Purpose Restriction',
              localName: 'Restriction d\'Usage',
              description: 'Fonds avec restriction d\'usage',
              type: 'PURPOSE'
            }
          ]
        },
        functionalAllocation: {
          required: true,
          minimums: {
            mission: 75,
            administration: 20,
            fundraising: 5
          }
        },
        depreciation: {
          method: 'STRAIGHT_LINE',
          rates: [
            {
              assetType: 'BUILDINGS',
              rate: 5,
              minimumYears: 20,
              maximumYears: 20
            },
            {
              assetType: 'EQUIPMENT',
              rate: 20,
              minimumYears: 5,
              maximumYears: 5
            },
            {
              assetType: 'VEHICLES',
              rate: 25,
              minimumYears: 4,
              maximumYears: 4
            }
          ]
        },
        inventory: {
          valuation: 'FIFO',
          writeDown: true
        },
        revenueRecognition: {
          grants: 'CONDITIONAL',
          donations: 'CASH',
          services: 'ACCRUAL'
        }
      }
    }],
    ['IFRS', {
      code: 'IFRS',
      name: 'International Financial Reporting Standards',
      localName: 'Normes Internationales d\'Information Financière',
      description: 'Standards internationaux pour les organisations à but non lucratif',
      version: '2024',
      effectiveDate: '2024-01-01',
      framework: 'IASB',
      applicableCountries: ['US', 'UK', 'AU', 'CA', 'ZA'],
      chartOfAccounts: {
        structure: {
          classes: [
            {
              code: '1',
              name: 'Assets',
              localName: 'Actifs',
              description: 'Assets and resources',
              type: 'ASSET',
              ngoSpecific: false,
              mandatory: true
            },
            {
              code: '2',
              name: 'Liabilities',
              localName: 'Passifs',
              description: 'Liabilities and obligations',
              type: 'LIABILITY',
              ngoSpecific: false,
              mandatory: true
            },
            {
              code: '3',
              name: 'Equity',
              localName: 'Capitaux Propres',
              description: 'Equity and reserves',
              type: 'EQUITY',
              ngoSpecific: true,
              mandatory: true
            },
            {
              code: '4',
              name: 'Revenue',
              localName: 'Produits',
              description: 'Revenue and income',
              type: 'REVENUE',
              ngoSpecific: true,
              mandatory: true
            },
            {
              code: '5',
              name: 'Expenses',
              localName: 'Charges',
              description: 'Expenses and costs',
              type: 'EXPENSE',
              ngoSpecific: true,
              mandatory: true
            }
          ],
          subClasses: [],
          accounts: []
        },
        numbering: {
          pattern: 'XXXX',
          length: 4,
          separator: ''
        },
        validation: {
          requiredAccounts: [],
          prohibitedAccounts: [],
          mandatorySubAccounts: []
        }
      },
      financialStatements: {
        balanceSheet: {
          format: 'IFRS',
          structure: {
            assets: {
              currentAssets: ['1'],
              nonCurrentAssets: ['1'],
              totalAssets: 'TOTAL_ASSETS'
            },
            liabilities: {
              currentLiabilities: ['2'],
              nonCurrentLiabilities: ['2'],
              totalLiabilities: 'TOTAL_LIABILITIES'
            },
            equity: {
              funds: ['3'],
              reserves: ['3'],
              retainedEarnings: '3',
              totalEquity: 'TOTAL_EQUITY'
            }
          },
          requiredNotes: ['ACCOUNTING_POLICIES', 'FUND_RESTRICTIONS', 'RELATED_PARTIES']
        },
        incomeStatement: {
          format: 'IFRS',
          structure: {
            resources: {
              grants: ['4'],
              donations: ['4'],
              membershipFees: ['4'],
              otherResources: ['4'],
              totalResources: 'TOTAL_REVENUE'
            },
            expenses: {
              missionExpenses: ['5'],
              administrativeExpenses: ['5'],
              fundraisingExpenses: ['5'],
              totalExpenses: 'TOTAL_EXPENSES'
            },
            netResult: 'NET_RESULT'
          },
          functionalAllocation: false,
          requiredNotes: ['REVENUE_RECOGNITION', 'EXPENSE_ALLOCATION']
        },
        cashFlow: {
          required: true,
          format: 'INDIRECT',
          structure: {
            operatingActivities: ['OPERATING_CASH_FLOW'],
            investingActivities: ['INVESTING_CASH_FLOW'],
            financingActivities: ['FINANCING_CASH_FLOW'],
            netCashFlow: 'NET_CASH_FLOW'
          }
        },
        notes: {
          required: true,
          minimumNotes: 20,
          ngoSpecificNotes: ['FUND_RESTRICTIONS', 'GRANT_CONDITIONS', 'DONATION_POLICIES']
        }
      },
      specificRules: {
        fundAccounting: {
          required: true,
          types: [
            {
              code: 'UNRESTRICTED',
              name: 'Unrestricted Funds',
              localName: 'Fonds Libres',
              description: 'Unrestricted funds',
              restriction: 'UNRESTRICTED'
            }
          ],
          restrictions: []
        },
        functionalAllocation: {
          required: false,
          minimums: {
            mission: 0,
            administration: 0,
            fundraising: 0
          }
        },
        depreciation: {
          method: 'STRAIGHT_LINE',
          rates: []
        },
        inventory: {
          valuation: 'FIFO',
          writeDown: true
        },
        revenueRecognition: {
          grants: 'ACCRUAL',
          donations: 'CASH',
          services: 'ACCRUAL'
        }
      }
    }]
  ]);

  constructor(private internationalConfig: InternationalConfigService) {
    // Initialiser avec le standard par défaut
    this.setCurrentStandard('SYCEBNL');
  }

  /**
   * Obtenir le standard comptable actuel
   */
  getCurrentStandard(): AccountingStandard {
    const standardCode = this.currentStandardSubject.value;
    return this.standards.get(standardCode) || this.standards.get('SYCEBNL')!;
  }

  /**
   * Obtenir un standard comptable spécifique
   */
  getStandard(standardCode: string): AccountingStandard | undefined {
    return this.standards.get(standardCode);
  }

  /**
   * Obtenir tous les standards disponibles
   */
  getAllStandards(): AccountingStandard[] {
    return Array.from(this.standards.values());
  }

  /**
   * Définir le standard comptable actuel
   */
  setCurrentStandard(standardCode: string): void {
    if (this.standards.has(standardCode)) {
      this.currentStandardSubject.next(standardCode);
    }
  }

  /**
   * Obtenir les standards applicables pour un pays
   */
  getStandardsForCountry(countryCode: string): AccountingStandard[] {
    return Array.from(this.standards.values()).filter(standard =>
      standard.applicableCountries.includes(countryCode)
    );
  }

  /**
   * Obtenir le plan comptable pour le standard actuel
   */
  getCurrentChartOfAccounts(): ChartOfAccountsConfig {
    return this.getCurrentStandard().chartOfAccounts;
  }

  /**
   * Obtenir les états financiers pour le standard actuel
   */
  getCurrentFinancialStatements(): FinancialStatementsConfig {
    return this.getCurrentStandard().financialStatements;
  }

  /**
   * Obtenir les règles spécifiques pour le standard actuel
   */
  getCurrentSpecificRules(): SpecificRulesConfig {
    return this.getCurrentStandard().specificRules;
  }

  /**
   * Vérifier si un compte est valide pour le standard actuel
   */
  isAccountValid(accountCode: string): boolean {
    const chartOfAccounts = this.getCurrentChartOfAccounts();
    const accounts = chartOfAccounts.structure.accounts;
    
    return accounts.some(account => account.code === accountCode);
  }

  /**
   * Obtenir les comptes obligatoires pour le standard actuel
   */
  getRequiredAccounts(): string[] {
    const chartOfAccounts = this.getCurrentChartOfAccounts();
    return chartOfAccounts.validation.requiredAccounts;
  }

  /**
   * Vérifier si la comptabilité par fonds est requise
   */
  isFundAccountingRequired(): boolean {
    return this.getCurrentSpecificRules().fundAccounting.required;
  }

  /**
   * Vérifier si l'allocation fonctionnelle est requise
   */
  isFunctionalAllocationRequired(): boolean {
    return this.getCurrentSpecificRules().functionalAllocation.required;
  }

  /**
   * Obtenir les seuils minimums d'allocation fonctionnelle
   */
  getFunctionalAllocationMinimums(): { mission: number; administration: number; fundraising: number } {
    return this.getCurrentSpecificRules().functionalAllocation.minimums;
  }

  /**
   * Obtenir les types de fonds disponibles
   */
  getFundTypes(): FundType[] {
    return this.getCurrentSpecificRules().fundAccounting.types;
  }

  /**
   * Obtenir les restrictions de fonds disponibles
   */
  getFundRestrictions(): FundRestriction[] {
    return this.getCurrentSpecificRules().fundAccounting.restrictions;
  }

  /**
   * Obtenir la méthode de reconnaissance des revenus
   */
  getRevenueRecognitionMethod(revenueType: 'grants' | 'donations' | 'services'): 'CASH' | 'ACCRUAL' | 'CONDITIONAL' {
    const rules = this.getCurrentSpecificRules();
    return rules.revenueRecognition[revenueType];
  }

  /**
   * Obtenir les notes obligatoires pour le standard actuel
   */
  getRequiredNotes(): string[] {
    const financialStatements = this.getCurrentFinancialStatements();
    return financialStatements.notes.ngoSpecificNotes;
  }

  /**
   * Vérifier si un standard est applicable pour un pays
   */
  isStandardApplicableForCountry(standardCode: string, countryCode: string): boolean {
    const standard = this.standards.get(standardCode);
    return standard ? standard.applicableCountries.includes(countryCode) : false;
  }

  /**
   * Obtenir le standard recommandé pour un pays
   */
  getRecommendedStandardForCountry(countryCode: string): AccountingStandard | undefined {
    const applicableStandards = this.getStandardsForCountry(countryCode);
    
    // Priorité : SYCEBNL pour les pays OHADA, IFRS pour les autres
    if (applicableStandards.some(s => s.code === 'SYCEBNL')) {
      return applicableStandards.find(s => s.code === 'SYCEBNL');
    }
    
    return applicableStandards[0];
  }

  /**
   * Valider la conformité d'une organisation avec le standard actuel
   */
  validateCompliance(organization: any): { compliant: boolean; issues: string[] } {
    const issues: string[] = [];
    const standard = this.getCurrentStandard();
    
    // Vérifier les comptes obligatoires
    const requiredAccounts = this.getRequiredAccounts();
    // TODO: Implémenter la validation des comptes
    
    // Vérifier l'allocation fonctionnelle si requise
    if (this.isFunctionalAllocationRequired()) {
      const minimums = this.getFunctionalAllocationMinimums();
      // TODO: Implémenter la validation de l'allocation fonctionnelle
    }
    
    // Vérifier la comptabilité par fonds si requise
    if (this.isFundAccountingRequired()) {
      // TODO: Implémenter la validation de la comptabilité par fonds
    }
    
    return {
      compliant: issues.length === 0,
      issues: issues
    };
  }
}








