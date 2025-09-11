import { Component, OnInit, OnDestroy, Input, Output, EventEmitter, ViewChild, ElementRef } from '@angular/core';
import { CommonModule, NgIf, NgFor } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Subject, takeUntil, debounceTime, distinctUntilChanged } from 'rxjs';

import { InternationalConfigService, CountryConfig, RegionConfig } from '../../services/international-config.service';
import { AccountingStandardsService } from '../../services/accounting-standards.service';
import { CurrencyManagementService } from '../../services/currency-management.service';

@Component({
  selector: 'app-country-selector',
  standalone: true,
  imports: [
    CommonModule,
    NgIf,
    NgFor,
    FormsModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatCardModule,
    MatChipsModule,
    MatTooltipModule,
    MatDialogModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './country-selector.html',
  styleUrl: './country-selector.scss'
})
export class CountrySelectorComponent implements OnInit, OnDestroy {
  @Input() showRegionSelector = true;
  @Input() showCurrencyInfo = true;
  @Input() showStandardsInfo = true;
  @Input() allowMultipleSelection = false;
  @Input() required = false;
  @Input() disabled = false;
  @Input() placeholder = 'Sélectionner un pays';
  
  @Output() countrySelected = new EventEmitter<CountryConfig>();
  @Output() regionSelected = new EventEmitter<RegionConfig>();
  @Output() selectionChanged = new EventEmitter<{country: CountryConfig, region: RegionConfig, standards: string[]}>();

  @ViewChild('searchInput') searchInput!: ElementRef<HTMLInputElement>;

  private destroy$ = new Subject<void>();

  // Données
  countries: CountryConfig[] = [];
  regions: RegionConfig[] = [];
  filteredCountries: CountryConfig[] = [];
  selectedCountry: CountryConfig | null = null;
  selectedRegion: RegionConfig | null = null;
  availableStandards: string[] = [];

  // État
  isLoading = false;
  searchTerm = '';
  showAdvancedOptions = false;

  // Configuration
  currentCountryCode = '';
  currentRegionCode = '';

  constructor(
    private internationalConfig: InternationalConfigService,
    private accountingStandards: AccountingStandardsService,
    private currencyManagement: CurrencyManagementService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadData();
    this.setupSubscriptions();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadData(): void {
    this.isLoading = true;
    
    // Charger les pays et régions
    this.countries = this.internationalConfig.getAllCountryConfigs();
    this.regions = this.internationalConfig.getAllRegionConfigs();
    this.filteredCountries = [...this.countries];
    
    // Charger la sélection actuelle
    this.internationalConfig.currentCountry$.pipe(takeUntil(this.destroy$)).subscribe(countryCode => {
      this.currentCountryCode = countryCode;
      this.selectedCountry = this.countries.find(c => c.code === countryCode) || null;
    });

    this.internationalConfig.currentRegion$.pipe(takeUntil(this.destroy$)).subscribe(regionCode => {
      this.currentRegionCode = regionCode;
      this.selectedRegion = this.regions.find(r => r.code === regionCode) || null;
    });

    this.isLoading = false;
  }

  private setupSubscriptions(): void {
    // Écouter les changements de pays pour mettre à jour les standards disponibles
    this.internationalConfig.currentCountry$.pipe(
      takeUntil(this.destroy$)
    ).subscribe(countryCode => {
      this.updateAvailableStandards(countryCode);
    });
  }

  private updateAvailableStandards(countryCode: string): void {
    const standards = this.accountingStandards.getStandardsForCountry(countryCode);
    this.availableStandards = standards.map(s => s.code);
  }

  onCountryChange(countryCode: string): void {
    if (!countryCode) return;

    const country = this.countries.find(c => c.code === countryCode);
    if (!country) return;

    this.selectedCountry = country;
    this.internationalConfig.setCurrentCountry(countryCode);
    
    // Mettre à jour la région automatiquement
    const region = this.regions.find(r => r.countries.includes(countryCode));
    if (region) {
      this.selectedRegion = region;
      this.internationalConfig.setCurrentRegion(region.code);
    }

    // Mettre à jour les standards disponibles
    this.updateAvailableStandards(countryCode);

    // Émettre les événements
    this.countrySelected.emit(country);
    if (region) {
      this.regionSelected.emit(region);
    }
    this.selectionChanged.emit({
      country: country,
      region: region || this.selectedRegion!,
      standards: this.availableStandards
    });
  }

  onRegionChange(regionCode: string): void {
    if (!regionCode) return;

    const region = this.regions.find(r => r.code === regionCode);
    if (!region) return;

    this.selectedRegion = region;
    this.internationalConfig.setCurrentRegion(regionCode);
    this.regionSelected.emit(region);
  }

  onSearchChange(searchTerm: string): void {
    this.searchTerm = searchTerm;
    this.filterCountries();
  }

  private filterCountries(): void {
    if (!this.searchTerm.trim()) {
      this.filteredCountries = [...this.countries];
      return;
    }

    const term = this.searchTerm.toLowerCase();
    this.filteredCountries = this.countries.filter(country =>
      country.name.toLowerCase().includes(term) ||
      country.nativeName.toLowerCase().includes(term) ||
      country.code.toLowerCase().includes(term)
    );
  }

  clearSelection(): void {
    this.selectedCountry = null;
    this.selectedRegion = null;
    this.searchTerm = '';
    this.filteredCountries = [...this.countries];
    this.availableStandards = [];
  }

  getCountryFlag(countryCode: string): string {
    // Mapping des codes pays vers les emojis de drapeaux
    const flagMap: { [key: string]: string } = {
      'SN': '🇸🇳',
      'CI': '🇨🇮',
      'BF': '🇧🇫',
      'ML': '🇲🇱',
      'NE': '🇳🇪',
      'TD': '🇹🇩',
      'CM': '🇨🇲',
      'CF': '🇨🇫',
      'CG': '🇨🇬',
      'CD': '🇨🇩',
      'GA': '🇬🇦',
      'GQ': '🇬🇶',
      'BJ': '🇧🇯',
      'TG': '🇹🇬',
      'FR': '🇫🇷',
      'US': '🇺🇸',
      'GB': '🇬🇧',
      'DE': '🇩🇪',
      'IT': '🇮🇹',
      'ES': '🇪🇸',
      'CA': '🇨🇦',
      'AU': '🇦🇺',
      'ZA': '🇿🇦',
      'NG': '🇳🇬',
      'GH': '🇬🇭',
      'KE': '🇰🇪',
      'EG': '🇪🇬',
      'MA': '🇲🇦',
      'TN': '🇹🇳'
    };
    
    return flagMap[countryCode] || '🌍';
  }

  getRegionIcon(regionCode: string): string {
    const iconMap: { [key: string]: string } = {
      'OHADA': '🤝',
      'EU': '🇪🇺',
      'NORTH_AMERICA': '🌎',
      'AFRICA': '🌍',
      'ASIA': '🌏'
    };
    
    return iconMap[regionCode] || '🌐';
  }

  getCurrencySymbol(currencyCode: string): string {
    const currency = this.currencyManagement.getCurrency(currencyCode);
    return currency ? currency.symbol : currencyCode;
  }

  getStandardsForCountry(countryCode: string): string[] {
    const standards = this.accountingStandards.getStandardsForCountry(countryCode);
    return standards.map(s => s.code);
  }

  getRecommendedStandard(countryCode: string): string {
    const standard = this.accountingStandards.getRecommendedStandardForCountry(countryCode);
    return standard ? standard.code : 'SYCEBNL';
  }

  isCountryInRegion(countryCode: string, regionCode: string): boolean {
    const region = this.regions.find(r => r.code === regionCode);
    return region ? region.countries.includes(countryCode) : false;
  }

  getCountriesInRegion(regionCode: string): CountryConfig[] {
    const region = this.regions.find(r => r.code === regionCode);
    if (!region) return [];
    
    return this.countries.filter(country => region.countries.includes(country.code));
  }

  getCountryInfo(countryCode: string): string {
    const country = this.countries.find(c => c.code === countryCode);
    if (!country) return '';
    
    return `${country.name} (${country.nativeName}) - ${country.currency.symbol}`;
  }

  getRegionInfo(regionCode: string): string {
    const region = this.regions.find(r => r.code === regionCode);
    if (!region) return '';
    
    return `${region.name} - ${region.countries.length} pays`;
  }

  toggleAdvancedOptions(): void {
    this.showAdvancedOptions = !this.showAdvancedOptions;
  }

  // Méthodes utilitaires pour l'affichage
  getCountryDisplayName(country: CountryConfig): string {
    return `${this.getCountryFlag(country.code)} ${country.name}`;
  }

  getRegionDisplayName(region: RegionConfig): string {
    return `${this.getRegionIcon(region.code)} ${region.name}`;
  }

  getStandardsDisplay(standards: string[]): string {
    return standards.join(', ');
  }

  getCurrencyDisplay(currencyCode: string): string {
    const currency = this.currencyManagement.getCurrency(currencyCode);
    return currency ? `${currency.symbol} ${currency.name}` : currencyCode;
  }

  // Validation
  isSelectionValid(): boolean {
    if (this.required && !this.selectedCountry) {
      return false;
    }
    return true;
  }

  getValidationMessage(): string {
    if (this.required && !this.selectedCountry) {
      return 'Veuillez sélectionner un pays';
    }
    return '';
  }

  // Méthodes pour l'accessibilité
  onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault();
      // Logique pour ouvrir le sélecteur
    }
  }

  focusSearch(): void {
    if (this.searchInput) {
      this.searchInput.nativeElement.focus();
    }
  }

  // Méthodes utilitaires pour les icônes
  getOrganizationTypeIcon(typeCode: string): string {
    const iconMap: { [key: string]: string } = {
      'NGO': 'volunteer_activism',
      'ASSOCIATION': 'groups',
      'FOUNDATION': 'account_balance',
      'COOPERATIVE': 'handshake',
      'MUTUAL': 'support',
      'RELIGIOUS_ORGANIZATION': 'church',
      'EDUCATIONAL_INSTITUTION': 'school',
      'HEALTH_INSTITUTION': 'local_hospital',
      'CULTURAL_ORGANIZATION': 'palette',
      'SPORTS_ORGANIZATION': 'sports',
      'PROFESSIONAL_ASSOCIATION': 'business',
      'OTHER': 'help'
    };
    
    return iconMap[typeCode] || 'business';
  }

  getAccountingSystemIcon(systemCode: string): string {
    const iconMap: { [key: string]: string } = {
      'NORMAL': 'account_balance',
      'MINIMAL': 'account_balance_wallet',
      'TRANSITIONAL': 'sync',
      'SIMPLIFIED': 'account_balance_wallet',
      'CASH': 'payments'
    };
    
    return iconMap[systemCode] || 'account_balance';
  }
}
