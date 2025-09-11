import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { InternationalConfigService } from './international-config.service';

export interface Currency {
  code: string;
  name: string;
  symbol: string;
  localName: string;
  decimalPlaces: number;
  isActive: boolean;
  isBase: boolean;
  exchangeRate?: number;
  lastUpdated?: Date;
}

export interface ExchangeRate {
  fromCurrency: string;
  toCurrency: string;
  rate: number;
  date: Date;
  source: string;
  isOfficial: boolean;
}

export interface CurrencyConversion {
  amount: number;
  fromCurrency: string;
  toCurrency: string;
  convertedAmount: number;
  rate: number;
  date: Date;
  fees?: number;
}

export interface CurrencyConfig {
  baseCurrency: string;
  reportingCurrency: string;
  allowedCurrencies: string[];
  exchangeRateSource: 'MANUAL' | 'API' | 'BANK';
  updateFrequency: 'DAILY' | 'WEEKLY' | 'MONTHLY' | 'MANUAL';
  roundingMethod: 'ROUND' | 'FLOOR' | 'CEIL';
  precision: number;
}

@Injectable({
  providedIn: 'root'
})
export class CurrencyManagementService {
  private currentConfigSubject = new BehaviorSubject<CurrencyConfig>({
    baseCurrency: 'XOF',
    reportingCurrency: 'XOF',
    allowedCurrencies: ['XOF', 'EUR', 'USD'],
    exchangeRateSource: 'MANUAL',
    updateFrequency: 'DAILY',
    roundingMethod: 'ROUND',
    precision: 2
  });

  private exchangeRatesSubject = new BehaviorSubject<Map<string, ExchangeRate>>(new Map());
  
  public currentConfig$ = this.currentConfigSubject.asObservable();
  public exchangeRates$ = this.exchangeRatesSubject.asObservable();

  // Devises supportées avec leurs configurations
  private currencies: Map<string, Currency> = new Map([
    ['XOF', {
      code: 'XOF',
      name: 'West African CFA Franc',
      symbol: 'FCFA',
      localName: 'Franc CFA',
      decimalPlaces: 0,
      isActive: true,
      isBase: true,
      exchangeRate: 1,
      lastUpdated: new Date()
    }],
    ['EUR', {
      code: 'EUR',
      name: 'Euro',
      symbol: '€',
      localName: 'Euro',
      decimalPlaces: 2,
      isActive: true,
      isBase: false,
      exchangeRate: 0.0015,
      lastUpdated: new Date()
    }],
    ['USD', {
      code: 'USD',
      name: 'US Dollar',
      symbol: '$',
      localName: 'Dollar Américain',
      decimalPlaces: 2,
      isActive: true,
      isBase: false,
      exchangeRate: 0.0017,
      lastUpdated: new Date()
    }],
    ['GBP', {
      code: 'GBP',
      name: 'British Pound',
      symbol: '£',
      localName: 'Livre Sterling',
      decimalPlaces: 2,
      isActive: true,
      isBase: false,
      exchangeRate: 0.0013,
      lastUpdated: new Date()
    }],
    ['CAD', {
      code: 'CAD',
      name: 'Canadian Dollar',
      symbol: 'C$',
      localName: 'Dollar Canadien',
      decimalPlaces: 2,
      isActive: true,
      isBase: false,
      exchangeRate: 0.0023,
      lastUpdated: new Date()
    }],
    ['CHF', {
      code: 'CHF',
      name: 'Swiss Franc',
      symbol: 'CHF',
      localName: 'Franc Suisse',
      decimalPlaces: 2,
      isActive: true,
      isBase: false,
      exchangeRate: 0.0014,
      lastUpdated: new Date()
    }],
    ['JPY', {
      code: 'JPY',
      name: 'Japanese Yen',
      symbol: '¥',
      localName: 'Yen Japonais',
      decimalPlaces: 0,
      isActive: true,
      isBase: false,
      exchangeRate: 0.25,
      lastUpdated: new Date()
    }],
    ['CNY', {
      code: 'CNY',
      name: 'Chinese Yuan',
      symbol: '¥',
      localName: 'Yuan Chinois',
      decimalPlaces: 2,
      isActive: true,
      isBase: false,
      exchangeRate: 0.012,
      lastUpdated: new Date()
    }],
    ['NGN', {
      code: 'NGN',
      name: 'Nigerian Naira',
      symbol: '₦',
      localName: 'Naira Nigérian',
      decimalPlaces: 2,
      isActive: true,
      isBase: false,
      exchangeRate: 2.5,
      lastUpdated: new Date()
    }],
    ['GHS', {
      code: 'GHS',
      name: 'Ghanaian Cedi',
      symbol: '₵',
      localName: 'Cedi Ghanéen',
      decimalPlaces: 2,
      isActive: true,
      isBase: false,
      exchangeRate: 0.025,
      lastUpdated: new Date()
    }],
    ['KES', {
      code: 'KES',
      name: 'Kenyan Shilling',
      symbol: 'KSh',
      localName: 'Shilling Kenyan',
      decimalPlaces: 2,
      isActive: true,
      isBase: false,
      exchangeRate: 0.25,
      lastUpdated: new Date()
    }],
    ['ZAR', {
      code: 'ZAR',
      name: 'South African Rand',
      symbol: 'R',
      localName: 'Rand Sud-Africain',
      decimalPlaces: 2,
      isActive: true,
      isBase: false,
      exchangeRate: 0.03,
      lastUpdated: new Date()
    }],
    ['EGP', {
      code: 'EGP',
      name: 'Egyptian Pound',
      symbol: 'E£',
      localName: 'Livre Égyptienne',
      decimalPlaces: 2,
      isActive: true,
      isBase: false,
      exchangeRate: 0.05,
      lastUpdated: new Date()
    }],
    ['MAD', {
      code: 'MAD',
      name: 'Moroccan Dirham',
      symbol: 'MAD',
      localName: 'Dirham Marocain',
      decimalPlaces: 2,
      isActive: true,
      isBase: false,
      exchangeRate: 0.015,
      lastUpdated: new Date()
    }],
    ['TND', {
      code: 'TND',
      name: 'Tunisian Dinar',
      symbol: 'DT',
      localName: 'Dinar Tunisien',
      decimalPlaces: 3,
      isActive: true,
      isBase: false,
      exchangeRate: 0.005,
      lastUpdated: new Date()
    }]
  ]);

  constructor(private internationalConfig: InternationalConfigService) {
    this.initializeExchangeRates();
    this.setupCountryCurrencyMapping();
  }

  /**
   * Initialiser les taux de change par défaut
   */
  private initializeExchangeRates(): void {
    const rates = new Map<string, ExchangeRate>();
    
    // Taux de change par défaut (à remplacer par des données réelles)
    const defaultRates = [
      { from: 'XOF', to: 'EUR', rate: 0.0015 },
      { from: 'XOF', to: 'USD', rate: 0.0017 },
      { from: 'XOF', to: 'GBP', rate: 0.0013 },
      { from: 'XOF', to: 'CAD', rate: 0.0023 },
      { from: 'XOF', to: 'CHF', rate: 0.0014 },
      { from: 'XOF', to: 'JPY', rate: 0.25 },
      { from: 'XOF', to: 'CNY', rate: 0.012 },
      { from: 'XOF', to: 'NGN', rate: 2.5 },
      { from: 'XOF', to: 'GHS', rate: 0.025 },
      { from: 'XOF', to: 'KES', rate: 0.25 },
      { from: 'XOF', to: 'ZAR', rate: 0.03 },
      { from: 'XOF', to: 'EGP', rate: 0.05 },
      { from: 'XOF', to: 'MAD', rate: 0.015 },
      { from: 'XOF', to: 'TND', rate: 0.005 }
    ];

    defaultRates.forEach(rate => {
      const key = `${rate.from}_${rate.to}`;
      rates.set(key, {
        fromCurrency: rate.from,
        toCurrency: rate.to,
        rate: rate.rate,
        date: new Date(),
        source: 'MANUAL',
        isOfficial: true
      });
    });

    this.exchangeRatesSubject.next(rates);
  }

  /**
   * Configurer la devise par défaut selon le pays
   */
  private setupCountryCurrencyMapping(): void {
    this.internationalConfig.currentCountry$.subscribe(countryCode => {
      const countryConfig = this.internationalConfig.getCountryConfig(countryCode);
      if (countryConfig) {
        this.setBaseCurrency(countryConfig.currency.code);
        this.setReportingCurrency(countryConfig.currency.code);
      }
    });
  }

  /**
   * Obtenir la configuration actuelle
   */
  getCurrentConfig(): CurrencyConfig {
    return this.currentConfigSubject.value;
  }

  /**
   * Mettre à jour la configuration
   */
  updateConfig(config: Partial<CurrencyConfig>): void {
    const currentConfig = this.getCurrentConfig();
    const newConfig = { ...currentConfig, ...config };
    this.currentConfigSubject.next(newConfig);
  }

  /**
   * Définir la devise de base
   */
  setBaseCurrency(currencyCode: string): void {
    if (this.currencies.has(currencyCode)) {
      // Désactiver l'ancienne devise de base
      this.currencies.forEach(currency => {
        if (currency.isBase) {
          currency.isBase = false;
        }
      });
      
      // Activer la nouvelle devise de base
      const newBaseCurrency = this.currencies.get(currencyCode)!;
      newBaseCurrency.isBase = true;
      newBaseCurrency.exchangeRate = 1;
      
      this.updateConfig({ baseCurrency: currencyCode });
    }
  }

  /**
   * Définir la devise de reporting
   */
  setReportingCurrency(currencyCode: string): void {
    if (this.currencies.has(currencyCode)) {
      this.updateConfig({ reportingCurrency: currencyCode });
    }
  }

  /**
   * Obtenir toutes les devises disponibles
   */
  getAllCurrencies(): Currency[] {
    return Array.from(this.currencies.values());
  }

  /**
   * Obtenir les devises actives
   */
  getActiveCurrencies(): Currency[] {
    return Array.from(this.currencies.values()).filter(currency => currency.isActive);
  }

  /**
   * Obtenir une devise spécifique
   */
  getCurrency(currencyCode: string): Currency | undefined {
    return this.currencies.get(currencyCode);
  }

  /**
   * Obtenir la devise de base
   */
  getBaseCurrency(): Currency {
    return Array.from(this.currencies.values()).find(currency => currency.isBase) || this.currencies.get('XOF')!;
  }

  /**
   * Obtenir la devise de reporting
   */
  getReportingCurrency(): Currency {
    const config = this.getCurrentConfig();
    return this.currencies.get(config.reportingCurrency) || this.getBaseCurrency();
  }

  /**
   * Obtenir le taux de change entre deux devises
   */
  getExchangeRate(fromCurrency: string, toCurrency: string): number {
    if (fromCurrency === toCurrency) {
      return 1;
    }

    const rates = this.exchangeRatesSubject.value;
    const key = `${fromCurrency}_${toCurrency}`;
    const reverseKey = `${toCurrency}_${fromCurrency}`;
    
    // Chercher le taux direct
    if (rates.has(key)) {
      return rates.get(key)!.rate;
    }
    
    // Chercher le taux inverse
    if (rates.has(reverseKey)) {
      return 1 / rates.get(reverseKey)!.rate;
    }
    
    // Calculer via la devise de base si nécessaire
    const baseCurrency = this.getBaseCurrency().code;
    if (fromCurrency !== baseCurrency && toCurrency !== baseCurrency) {
      const fromToBase = this.getExchangeRate(fromCurrency, baseCurrency);
      const baseToTo = this.getExchangeRate(baseCurrency, toCurrency);
      return fromToBase * baseToTo;
    }
    
    return 1; // Taux par défaut
  }

  /**
   * Convertir un montant d'une devise à une autre
   */
  convertCurrency(amount: number, fromCurrency: string, toCurrency: string): CurrencyConversion {
    const rate = this.getExchangeRate(fromCurrency, toCurrency);
    const config = this.getCurrentConfig();
    
    let convertedAmount = amount * rate;
    
    // Appliquer la méthode d'arrondi
    switch (config.roundingMethod) {
      case 'FLOOR':
        convertedAmount = Math.floor(convertedAmount * Math.pow(10, config.precision)) / Math.pow(10, config.precision);
        break;
      case 'CEIL':
        convertedAmount = Math.ceil(convertedAmount * Math.pow(10, config.precision)) / Math.pow(10, config.precision);
        break;
      case 'ROUND':
      default:
        convertedAmount = Math.round(convertedAmount * Math.pow(10, config.precision)) / Math.pow(10, config.precision);
        break;
    }
    
    return {
      amount: amount,
      fromCurrency: fromCurrency,
      toCurrency: toCurrency,
      convertedAmount: convertedAmount,
      rate: rate,
      date: new Date()
    };
  }

  /**
   * Formater un montant selon la devise
   */
  formatCurrency(amount: number, currencyCode: string, locale?: string): string {
    const currency = this.currencies.get(currencyCode);
    if (!currency) {
      return amount.toString();
    }

    const config = this.getCurrentConfig();
    const precision = currency.decimalPlaces;
    
    try {
      return new Intl.NumberFormat(locale || 'fr-FR', {
        style: 'currency',
        currency: currencyCode,
        minimumFractionDigits: precision,
        maximumFractionDigits: precision
      }).format(amount);
    } catch (error) {
      // Fallback si la devise n'est pas supportée par Intl
      return `${currency.symbol} ${amount.toFixed(precision)}`;
    }
  }

  /**
   * Mettre à jour un taux de change
   */
  updateExchangeRate(fromCurrency: string, toCurrency: string, rate: number, source: string = 'MANUAL'): void {
    const rates = this.exchangeRatesSubject.value;
    const key = `${fromCurrency}_${toCurrency}`;
    
    rates.set(key, {
      fromCurrency: fromCurrency,
      toCurrency: toCurrency,
      rate: rate,
      date: new Date(),
      source: source,
      isOfficial: source === 'BANK' || source === 'API'
    });
    
    this.exchangeRatesSubject.next(rates);
  }

  /**
   * Obtenir l'historique des taux de change
   */
  getExchangeRateHistory(fromCurrency: string, toCurrency: string, days: number = 30): Observable<ExchangeRate[]> {
    // TODO: Implémenter la récupération de l'historique depuis l'API
    return of([]);
  }

  /**
   * Synchroniser les taux de change avec une source externe
   */
  syncExchangeRates(): Observable<boolean> {
    const config = this.getCurrentConfig();
    
    if (config.exchangeRateSource === 'API') {
      // TODO: Implémenter la synchronisation avec une API externe
      return of(true);
    }
    
    return of(false);
  }

  /**
   * Valider une devise
   */
  isValidCurrency(currencyCode: string): boolean {
    return this.currencies.has(currencyCode);
  }

  /**
   * Obtenir les devises recommandées pour un pays
   */
  getRecommendedCurrenciesForCountry(countryCode: string): Currency[] {
    const countryConfig = this.internationalConfig.getCountryConfig(countryCode);
    if (!countryConfig) {
      return [this.getBaseCurrency()];
    }

    const recommended: Currency[] = [];
    
    // Devise locale du pays
    const localCurrency = this.currencies.get(countryConfig.currency.code);
    if (localCurrency) {
      recommended.push(localCurrency);
    }
    
    // Devises régionales
    const regionConfig = this.internationalConfig.getCurrentRegionConfig();
    if (regionConfig.code === 'OHADA') {
      const xofCurrency = this.currencies.get('XOF');
      if (xofCurrency && !recommended.includes(xofCurrency)) {
        recommended.push(xofCurrency);
      }
    }
    
    // Devises internationales communes
    const commonCurrencies = ['USD', 'EUR'];
    commonCurrencies.forEach(code => {
      const currency = this.currencies.get(code);
      if (currency && !recommended.includes(currency)) {
        recommended.push(currency);
      }
    });
    
    return recommended;
  }

  /**
   * Calculer les frais de conversion
   */
  calculateConversionFees(amount: number, fromCurrency: string, toCurrency: string): number {
    if (fromCurrency === toCurrency) {
      return 0;
    }
    
    // Frais par défaut : 0.5% du montant
    const feeRate = 0.005;
    return amount * feeRate;
  }

  /**
   * Obtenir le résumé des devises
   */
  getCurrencySummary(): { total: number; active: number; base: string; reporting: string } {
    const currencies = this.getAllCurrencies();
    const activeCurrencies = this.getActiveCurrencies();
    const baseCurrency = this.getBaseCurrency();
    const reportingCurrency = this.getReportingCurrency();
    
    return {
      total: currencies.length,
      active: activeCurrencies.length,
      base: baseCurrency.code,
      reporting: reportingCurrency.code
    };
  }
}








