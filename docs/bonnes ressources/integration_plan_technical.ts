// =====================================================
// PLAN D'INTÉGRATION TECHNIQUE - E COMPTA IA INTERNATIONAL
// Configuration et Services pour Base de Données Mondiale
// =====================================================

// 1. Configuration Pays dans Spring Boot
// src/main/resources/application-international.yml
/*
spring:
  datasource:
    international:
      url: ${DATABASE_INTERNATIONAL_URL:jdbc:postgresql://localhost:5432/ecomptaia_international}
      username: ${DB_USER:ecomptaia}
      password: ${DB_PASSWORD:secure_password}
  
  jpa:
    properties:
      hibernate:
        default_schema: international
    show-sql: false

app:
  international:
    enabled: true
    auto-update-rates: true
    cache-ttl: 3600 # 1 heure
    priority-countries: [BF, CI, SN, FR, US, GB, DE, CA, AU]
*/

// 2. Entity JPA pour la Configuration Pays
@Entity
@Table(name = "config_application_pays")
public class CountryConfig {
    @Id
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pays_id")
    private Country country;
    
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private CountryConfigData configJson;
    
    private String version;
    
    @CreationTimestamp
    private Timestamp dateCreation;
    
    @UpdateTimestamp
    private Timestamp derniereMaj;
    
    @Enumerated(EnumType.STRING)
    private Status statut;
    
    // Getters/Setters
}

// 3. DTO pour Configuration Pays
public class CountryConfigData {
    private CountryInfo pays;
    private AccountingStandardInfo standardComptable;
    private ExpertOrderInfo ordreExpertsComptables;
    private BusinessCreationInfo creationEntreprise;
    private TaxAdministrationInfo administrationFiscale;
    private SocialSecurityInfo securiteSociale;
    private CentralBankInfo banqueCentrale;
    private List<OfficialApiInfo> apisOfficielles;
    private List<PaymentSystemInfo> systemesPaiement;
    private RegulationInfo reglementations;
    private EconomicIndicatorsInfo indicateursEconomiques;
    
    // Classes imbriquées pour chaque section
    public static class CountryInfo {
        private String code;
        private String nom;
        private String devise;
        private String[] langues;
        private String continent;
    }
    
    public static class AccountingStandardInfo {
        private String nom;
        private String code;
        private String siteOfficiel;
    }
    
    public static class OfficialApiInfo {
        private String organisme;
        private String nomApi;
        private String description;
        private String urlBase;
        private String documentation;
        private String type;
        private String authentification;
        private String rateLimit;
        private String[] categories;
        private boolean environnementTest;
        private String urlSandbox;
    }
}

// 4. Repository pour Configuration Pays
@Repository
public interface CountryConfigRepository extends JpaRepository<CountryConfig, Long> {
    
    @Query("SELECT cc FROM CountryConfig cc JOIN cc.country c WHERE c.codeIso2 = :countryCode AND cc.statut = 'ACTIF'")
    Optional<CountryConfig> findByCountryCode(@Param("countryCode") String countryCode);
    
    @Query("SELECT cc FROM CountryConfig cc JOIN cc.country c WHERE c.continent = :continent AND cc.statut = 'ACTIF'")
    List<CountryConfig> findByContinent(@Param("continent") String continent);
    
    @Query(value = "SELECT export_config_pays(:countryCode)", nativeQuery = true)
    String exportConfigPays(@Param("countryCode") String countryCode);
    
    @Query(value = "SELECT * FROM v_analyse_expansion_mondiale ORDER BY score_total DESC LIMIT :limit", nativeQuery = true)
    List<ExpansionAnalysis> getExpansionPriorities(@Param("limit") int limit);
}

// 5. Service International Principal
@Service
@Transactional
@Slf4j
public class InternationalService {
    
    @Autowired
    private CountryConfigRepository countryConfigRepository;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String CACHE_PREFIX = "country:config:";
    private static final int CACHE_TTL = 3600; // 1 heure
    
    /**
     * Obtenir la configuration complète d'un pays
     */
    public CountryConfigData getCountryConfig(String countryCode) {
        String cacheKey = CACHE_PREFIX + countryCode;
        
        // Essayer le cache d'abord
        CountryConfigData cachedConfig = (CountryConfigData) redisTemplate.opsForValue().get(cacheKey);
        if (cachedConfig != null) {
            return cachedConfig;
        }
        
        // Sinon, charger depuis la base
        CountryConfig config = countryConfigRepository.findByCountryCode(countryCode)
            .orElseThrow(() -> new CountryNotSupportedException("Pays non supporté : " + countryCode));
        
        CountryConfigData configData = config.getConfigJson();
        
        // Mettre en cache
        redisTemplate.opsForValue().set(cacheKey, configData, Duration.ofSeconds(CACHE_TTL));
        
        return configData;
    }
    
    /**
     * Vérifier si un pays est supporté
     */
    public boolean isCountrySupported(String countryCode) {
        return countryConfigRepository.findByCountryCode(countryCode).isPresent();
    }
    
    /**
     * Obtenir la liste des pays supportés
     */
    public List<String> getSupportedCountries() {
        return countryConfigRepository.findAll().stream()
            .map(config -> config.getCountry().getCodeIso2())
            .collect(Collectors.toList());
    }
    
    /**
     * Obtenir les APIs disponibles pour un pays
     */
    public List<OfficialApiInfo> getAvailableApis(String countryCode) {
        CountryConfigData config = getCountryConfig(countryCode);
        return config.getApisOfficielles();
    }
    
    /**
     * Obtenir les systèmes de paiement pour un pays
     */
    public List<PaymentSystemInfo> getPaymentSystems(String countryCode) {
        CountryConfigData config = getCountryConfig(countryCode);
        return config.getSystemesPaiement();
    }
}

// 6. Service d'Expansion Géographique
@Service
@Slf4j
public class ExpansionService {
    
    @Autowired
    private CountryConfigRepository countryConfigRepository;
    
    /**
     * Obtenir les priorités d'expansion
     */
    public List<ExpansionAnalysis> getExpansionPriorities(int limit) {
        return countryConfigRepository.getExpansionPriorities(limit);
    }
    
    /**
     * Analyser le potentiel d'un marché
     */
    public MarketAnalysis analyzeMarket(String countryCode) {
        CountryConfigData config = getCountryConfig(countryCode);
        
        return MarketAnalysis.builder()
            .countryCode(countryCode)
            .digitalMaturityScore(calculateDigitalMaturity(config))
            .economicScore(calculateEconomicScore(config))
            .apiAvailability(config.getApisOfficielles().size())
            .integrationComplexity(assessIntegrationComplexity(config))
            .recommendation(generateRecommendation(config))
            .build();
    }
    
    private int calculateDigitalMaturity(CountryConfigData config) {
        int score = 0;
        
        // Score basé sur les APIs disponibles
        score += config.getApisOfficielles().size() * 5;
        
        // Score basé sur les systèmes de paiement
        score += config.getSystemesPaiement().size() * 3;
        
        // Score basé sur l'infrastructure
        if (config.getIndicateursEconomiques().getPenetrationInternet() > 80) score += 20;
        else if (config.getIndicateursEconomiques().getPenetrationInternet() > 50) score += 15;
        else if (config.getIndicateursEconomiques().getPenetrationInternet() > 25) score += 10;
        
        return Math.min(score, 100);
    }
}

// 7. Controller REST International
@RestController
@RequestMapping("/api/international")
@CrossOrigin(origins = "*")
@Slf4j
public class InternationalController {
    
    @Autowired
    private InternationalService internationalService;
    
    @Autowired
    private ExpansionService expansionService;
    
    /**
     * GET /api/international/countries - Liste des pays supportés
     */
    @GetMapping("/countries")
    public ResponseEntity<ApiResponse<List<String>>> getSupportedCountries() {
        try {
            List<String> countries = internationalService.getSupportedCountries();
            return ResponseEntity.ok(ApiResponse.success(countries));
        } catch (Exception e) {
            log.error("Erreur récupération pays supportés", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur interne"));
        }
    }
    
    /**
     * GET /api/international/countries/{code}/config - Configuration d'un pays
     */
    @GetMapping("/countries/{code}/config")
    public ResponseEntity<ApiResponse<CountryConfigData>> getCountryConfig(@PathVariable String code) {
        try {
            CountryConfigData config = internationalService.getCountryConfig(code.toUpperCase());
            return ResponseEntity.ok(ApiResponse.success(config));
        } catch (CountryNotSupportedException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Pays non supporté : " + code));
        } catch (Exception e) {
            log.error("Erreur récupération config pays : " + code, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur interne"));
        }
    }
    
    /**
     * GET /api/international/countries/{code}/apis - APIs disponibles pour un pays
     */
    @GetMapping("/countries/{code}/apis")
    public ResponseEntity<ApiResponse<List<OfficialApiInfo>>> getCountryApis(@PathVariable String code) {
        try {
            List<OfficialApiInfo> apis = internationalService.getAvailableApis(code.toUpperCase());
            return ResponseEntity.ok(ApiResponse.success(apis));
        } catch (Exception e) {
            log.error("Erreur récupération APIs pays : " + code, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur interne"));
        }
    }
    
    /**
     * GET /api/international/expansion/priorities - Priorités d'expansion
     */
    @GetMapping("/expansion/priorities")
    public ResponseEntity<ApiResponse<List<ExpansionAnalysis>>> getExpansionPriorities(
            @RequestParam(defaultValue = "20") int limit) {
        try {
            List<ExpansionAnalysis> priorities = expansionService.getExpansionPriorities(limit);
            return ResponseEntity.ok(ApiResponse.success(priorities));
        } catch (Exception e) {
            log.error("Erreur récupération priorités expansion", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur interne"));
        }
    }
    
    /**
     * POST /api/international/countries/{code}/validate - Tester intégration pays
     */
    @PostMapping("/countries/{code}/validate")
    public ResponseEntity<ApiResponse<ValidationResult>> validateCountryIntegration(@PathVariable String code) {
        try {
            ValidationResult result = internationalService.validateCountryIntegration(code.toUpperCase());
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            log.error("Erreur validation intégration pays : " + code, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur validation"));
        }
    }
}

// 8. Service de Validation des Intégrations
@Service
@Slf4j
public class CountryValidationService {
    
    @Autowired
    private InternationalService internationalService;
    
    @Autowired
    private RestTemplate restTemplate;
    
    /**
     * Valider l'intégration complète d'un pays
     */
    public ValidationResult validateCountryIntegration(String countryCode) {
        CountryConfigData config = internationalService.getCountryConfig(countryCode);
        ValidationResult result = new ValidationResult(countryCode);
        
        // 1. Tester les APIs officielles
        validateOfficialApis(config.getApisOfficielles(), result);
        
        // 2. Tester les systèmes de paiement  
        validatePaymentSystems(config.getSystemesPaiement(), result);
        
        // 3. Valider la configuration comptable
        validateAccountingStandard(config.getStandardComptable(), result);
        
        // 4. Tester les endpoints métier
        validateBusinessEndpoints(config, result);
        
        return result;
    }
    
    private void validateOfficialApis(List<OfficialApiInfo> apis, ValidationResult result) {
        for (OfficialApiInfo api : apis) {
            try {
                // Test de connexion basique
                if (api.getUrlBase() != null && !api.getUrlBase().isEmpty()) {
                    ResponseEntity<String> response = restTemplate.getForEntity(api.getUrlBase(), String.class);
                    if (response.getStatusCode().is2xxSuccessful()) {
                        result.addSuccess("API " + api.getNomApi() + " accessible");
                    } else {
                        result.addWarning("API " + api.getNomApi() + " retourne : " + response.getStatusCode());
                    }
                }
            } catch (Exception e) {
                result.addError("API " + api.getNomApi() + " inaccessible : " + e.getMessage());
            }
        }
    }
    
    private void validatePaymentSystems(List<PaymentSystemInfo> paymentSystems, ValidationResult result) {
        if (paymentSystems.isEmpty()) {
            result.addWarning("Aucun système de paiement configuré");
            return;
        }
        
        for (PaymentSystemInfo payment : paymentSystems) {
            if (payment.isApiDisponible() && payment.getApiDocumentation() != null) {
                try {
                    // Test de l'API de paiement
                    ResponseEntity<String> response = restTemplate.getForEntity(payment.getApiDocumentation(), String.class);
                    if (response.getStatusCode().is2xxSuccessful()) {
                        result.addSuccess("Système de paiement " + payment.getNom() + " intégrable");
                    }
                } catch (Exception e) {
                    result.addWarning("Documentation paiement " + payment.getNom() + " inaccessible");
                }
            }
        }
    }
}

// 9. Configuration Angular pour International
// src/app/international/international.config.ts
export interface CountryConfig {
  code: string;
  name: string;
  currency: string;
  languages: string[];
  continent: string;
  accountingStandard: AccountingStandard;
  businessCreation: BusinessCreationConfig;
  taxAdministration: TaxAdministrationConfig;
  paymentSystems: PaymentSystemConfig[];
  officialApis: OfficialApiConfig[];
}

export interface AccountingStandard {
  name: string;
  code: string;
  officialSite: string;
}

export interface BusinessCreationConfig {
  platform: string;
  website: string;
  apiAvailable: boolean;
  apiDocumentation?: string;
  supportedTypes: string[];
  averageDelayDays: number;
  minCost: number;
  maxCost: number;
  currency: string;
}

// 10. Service Angular International
// src/app/services/international.service.ts
@Injectable({
  providedIn: 'root'
})
export class InternationalService {
  
  private readonly API_BASE = '/api/international';
  
  constructor(private http: HttpClient) {}
  
  /**
   * Obtenir la liste des pays supportés
   */
  getSupportedCountries(): Observable<ApiResponse<string[]>> {
    return this.http.get<ApiResponse<string[]>>(`${this.API_BASE}/countries`);
  }
  
  /**
   * Obtenir la configuration d'un pays
   */
  getCountryConfig(countryCode: string): Observable<ApiResponse<CountryConfig>> {
    return this.http.get<ApiResponse<CountryConfig>>(`${this.API_BASE}/countries/${countryCode}/config`);
  }
  
  /**
   * Obtenir les APIs disponibles pour un pays
   */
  getCountryApis(countryCode: string): Observable<ApiResponse<OfficialApiConfig[]>> {
    return this.http.get<ApiResponse<OfficialApiConfig[]>>(`${this.API_BASE}/countries/${countryCode}/apis`);
  }
  
  /**
   * Valider l'intégration d'un pays
   */
  validateCountryIntegration(countryCode: string): Observable<ApiResponse<ValidationResult>> {
    return this.http.post<ApiResponse<ValidationResult>>(`${this.API_BASE}/countries/${countryCode}/validate`, {});
  }
  
  /**
   * Obtenir les priorités d'expansion
   */
  getExpansionPriorities(limit: number = 20): Observable<ApiResponse<ExpansionAnalysis[]>> {
    return this.http.get<ApiResponse<ExpansionAnalysis[]>>(`${this.API_BASE}/expansion/priorities?limit=${limit}`);
  }
}

// 11. Composant Angular de Sélection Pays
// src/app/components/country-selector/country-selector.component.ts
@Component({
  selector: 'app-country-selector',
  templateUrl: './country-selector.component.html',
  styleUrls: ['./country-selector.component.scss']
})
export class CountrySelectorComponent implements OnInit {
  
  supportedCountries: string[] = [];
  selectedCountry: string = '';
  countryConfig: CountryConfig | null = null;
  loading = false;
  error: string | null = null;
  
  constructor(
    private internationalService: InternationalService,
    private authService: AuthService
  ) {}
  
  ngOnInit(): void {
    this.loadSupportedCountries();
    this.loadCurrentUserCountry();
  }
  
  loadSupportedCountries(): void {
    this.internationalService.getSupportedCountries().subscribe({
      next: (response) => {
        if (response.success) {
          this.supportedCountries = response.data;
        }
      },
      error: (error) => {
        console.error('Erreur chargement pays supportés:', error);
        this.error = 'Erreur lors du chargement des pays';
      }
    });
  }
  
  loadCurrentUserCountry(): void {
    const currentUser = this.authService.getCurrentUser();
    if (currentUser && currentUser.countryCode) {
      this.selectCountry(currentUser.countryCode);
    }
  }
  
  selectCountry(countryCode: string): void {
    if (!countryCode) return;
    
    this.selectedCountry = countryCode;
    this.loading = true;
    this.error = null;
    
    this.internationalService.getCountryConfig(countryCode).subscribe({
      next: (response) => {
        if (response.success) {
          this.countryConfig = response.data;
          this.saveCountryPreference(countryCode);
        } else {
          this.error = response.message || 'Erreur configuration pays';
        }
        this.loading = false;
      },
      error: (error) => {
        console.error('Erreur chargement config pays:', error);
        this.error = 'Erreur lors du chargement de la configuration';
        this.loading = false;
      }
    });
  }
  
  private saveCountryPreference(countryCode: string): void {
    localStorage.setItem('selectedCountry', countryCode);
    // Mettre à jour le profil utilisateur si nécessaire
    this.authService.updateUserCountry(countryCode);
  }
  
  getCountryFlag(countryCode: string): string {
    return `https://flagcdn.com/w40/${countryCode.toLowerCase()}.png`;
  }
  
  getAccountingStandardInfo(): string {
    if (!this.countryConfig?.accountingStandard) return 'N/A';
    return `${this.countryConfig.accountingStandard.name} (${this.countryConfig.accountingStandard.code})`;
  }
}

// 12. Template HTML pour Sélection Pays
/* src/app/components/country-selector/country-selector.component.html
<div class="country-selector">
  <div class="selector-header">
    <h3>Sélectionner votre pays</h3>
    <p>Choisissez votre pays pour adapter l'interface et les fonctionnalités</p>
  </div>
  
  <div class="country-selection">
    <mat-form-field appearance="outline" class="country-field">
      <mat-label>Pays</mat-label>
      <mat-select [(value)]="selectedCountry" (selectionChange)="selectCountry($event.value)">
        <mat-option *ngFor="let country of supportedCountries" [value]="country">
          <img [src]="getCountryFlag(country)" [alt]="country" class="country-flag">
          {{ country }}
        </mat-option>
      </mat-select>
    </mat-form-field>
  </div>
  
  <div *ngIf="loading" class="loading-container">
    <mat-spinner diameter="40"></mat-spinner>
    <p>Chargement de la configuration...</p>
  </div>
  
  <div *ngIf="error" class="error-container">
    <mat-icon color="warn">error</mat-icon>
    <p>{{ error }}</p>
  </div>
  
  <div *ngIf="countryConfig && !loading" class="country-info">
    <div class="info-card">
      <div class="country-header">
        <img [src]="getCountryFlag(selectedCountry)" [alt]="selectedCountry" class="country-flag-large">
        <div class="country-details">
          <h4>{{ countryConfig.name }}</h4>
          <p>{{ countryConfig.continent }}</p>
          <span class="currency-badge">{{ countryConfig.currency }}</span>
        </div>
      </div>
      
      <div class="config-details">
        <div class="detail-item">
          <mat-icon>account_balance</mat-icon>
          <div class="detail-content">
            <span class="detail-label">Standard Comptable</span>
            <span class="detail-value">{{ getAccountingStandardInfo() }}</span>
          </div>
        </div>
        
        <div class="detail-item" *ngIf="countryConfig.businessCreation">
          <mat-icon>business</mat-icon>
          <div class="detail-content">
            <span class="detail-label">Création Entreprise</span>
            <span class="detail-value">{{ countryConfig.businessCreation.platform }}</span>
            <span class="api-status" *ngIf="countryConfig.businessCreation.apiAvailable">
              <mat-icon color="primary">api</mat-icon> API Disponible
            </span>
          </div>
        </div>
        
        <div class="detail-item" *ngIf="countryConfig.paymentSystems?.length > 0">
          <mat-icon>payment</mat-icon>
          <div class="detail-content">
            <span class="detail-label">Systèmes de Paiement</span>
            <div class="payment-systems">
              <mat-chip-list>
                <mat-chip *ngFor="let payment of countryConfig.paymentSystems.slice(0, 3)" 
                         [class.api-enabled]="payment.apiAvailable">
                  {{ payment.name }}
                  <mat-icon *ngIf="payment.apiAvailable" matChipTrailingIcon>api</mat-icon>
                </mat-chip>
              </mat-chip-list>
            </div>
          </div>
        </div>
      </div>
      
      <div class="actions">
        <button mat-raised-button color="primary" (click)="validateIntegration()" [disabled]="loading">
          <mat-icon>check_circle</mat-icon>
          Tester l'Intégration
        </button>
        <button mat-stroked-button (click)="viewFullConfig()">
          <mat-icon>info</mat-icon>
          Configuration Complète
        </button>
      </div>
    </div>
  </div>
</div>
*/

// 13. Service de Localisation Dynamique
// src/app/services/localization.service.ts
@Injectable({
  providedIn: 'root'
})
export class LocalizationService {
  
  private currentCountry: string = 'BF'; // Par défaut Burkina Faso
  
  constructor(
    private internationalService: InternationalService,
    private translateService: TranslateService
  ) {}
  
  /**
   * Initialiser la localisation pour un pays
   */
  async initializeForCountry(countryCode: string): Promise<void> {
    this.currentCountry = countryCode;
    
    try {
      const configResponse = await this.internationalService.getCountryConfig(countryCode).toPromise();
      if (configResponse?.success) {
        const config = configResponse.data;
        
        // Configurer la langue
        const primaryLanguage = this.mapLanguageCode(config.languages[0]);
        await this.translateService.use(primaryLanguage).toPromise();
        
        // Configurer la devise
        this.configureCurrency(config.currency);
        
        // Configurer les formats de date
        this.configureDateFormats(countryCode);
        
        // Charger les traductions spécifiques au pays
        await this.loadCountrySpecificTranslations(countryCode);
      }
    } catch (error) {
      console.error('Erreur initialisation localisation:', error);
    }
  }
  
  private mapLanguageCode(language: string): string {
    const languageMap: { [key: string]: string } = {
      'Français': 'fr',
      'Anglais': 'en', 
      'English': 'en',
      'Español': 'es',
      'Deutsch': 'de',
      'Português': 'pt'
    };
    
    return languageMap[language] || 'fr';
  }
  
  private configureCurrency(currencyCode: string): void {
    // Configuration globale de la devise
    window.sessionStorage.setItem('defaultCurrency', currencyCode);
  }
  
  /**
   * Formater un montant selon le pays
   */
  formatCurrency(amount: number): string {
    const countryConfig = JSON.parse(sessionStorage.getItem('countryConfig') || '{}');
    const currency = countryConfig.currency || 'XOF';
    
    const formatOptions: Intl.NumberFormatOptions = {
      style: 'currency',
      currency: currency,
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    };
    
    // Configuration spécifique par devise
    switch (currency) {
      case 'XOF':
      case 'XAF':
        return new Intl.NumberFormat('fr-FR', formatOptions).format(amount);
      case 'USD':
        return new Intl.NumberFormat('en-US', formatOptions).format(amount);
      case 'EUR':
        return new Intl.NumberFormat('fr-FR', formatOptions).format(amount);
      case 'GBP':
        return new Intl.NumberFormat('en-GB', formatOptions).format(amount);
      default:
        return new Intl.NumberFormat('fr-FR', formatOptions).format(amount);
    }
  }
  
  /**
   * Obtenir les labels de formulaire selon le pays
   */
  getFieldLabel(fieldKey: string): string {
    const countryLabels = this.getCountrySpecificLabels();
    return countryLabels[fieldKey] || this.getDefaultLabel(fieldKey);
  }
  
  private getCountrySpecificLabels(): { [key: string]: string } {
    // Labels spécifiques selon le pays
    switch (this.currentCountry) {
      case 'BF':
        return {
          'tax_id': 'N° IFU',
          'business_registry': 'RCCM',
          'social_security': 'N° CNSS'
        };
      case 'CI':
        return {
          'tax_id': 'N° CC',
          'business_registry': 'RCCM',
          'social_security': 'N° CNPS'
        };
      case 'FR':
        return {
          'tax_id': 'SIRET',
          'business_registry': 'RCS',
          'social_security': 'N° URSSAF'
        };
      case 'US':
        return {
          'tax_id': 'EIN',
          'business_registry': 'State ID',
          'social_security': 'SSN'
        };
      default:
        return {};
    }
  }
}

// 14. Intercepteur HTTP pour Localisation
// src/app/interceptors/localization.interceptor.ts
@Injectable()
export class LocalizationInterceptor implements HttpInterceptor {
  
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Ajouter le code pays dans les headers pour toutes les requêtes
    const countryCode = localStorage.getItem('selectedCountry') || 'BF';
    const currency = sessionStorage.getItem('defaultCurrency') || 'XOF';
    
    const localizedReq = req.clone({
      setHeaders: {
        'X-Country-Code': countryCode,
        'X-Currency': currency,
        'Accept-Language': this.getAcceptLanguage(countryCode)
      }
    });
    
    return next.handle(localizedReq);
  }
  
  private getAcceptLanguage(countryCode: string): string {
    const languageMap: { [key: string]: string } = {
      'BF': 'fr-BF,fr;q=0.9',
      'CI': 'fr-CI,fr;q=0.9', 
      'SN': 'fr-SN,fr;q=0.9',
      'FR': 'fr-FR,fr;q=0.9',
      'US': 'en-US,en;q=0.9',
      'GB': 'en-GB,en;q=0.9',
      'DE': 'de-DE,de;q=0.9'
    };
    
    return languageMap[countryCode] || 'fr-FR,fr;q=0.9';
  }
}

// 15. Configuration des Tests d'Intégration
// src/test/java/integration/InternationalIntegrationTest.java
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InternationalIntegrationTest {
    
    @Autowired
    private InternationalService internationalService;
    
    @Autowired
    private CountryValidationService validationService;
    
    private static final String[] PRIORITY_COUNTRIES = {"BF", "CI", "SN", "FR", "US", "GB"};
    
    @Test
    @DisplayName("Vérifier que tous les pays prioritaires sont supportés")
    void testPriorityCountriesSupported() {
        List<String> supportedCountries = internationalService.getSupportedCountries();
        
        for (String country : PRIORITY_COUNTRIES) {
            assertTrue(supportedCountries.contains(country), 
                "Le pays prioritaire " + country + " doit être supporté");
        }
    }
    
    @Test 
    @DisplayName("Tester la configuration de chaque pays prioritaire")
    void testPriorityCountriesConfiguration() {
        for (String countryCode : PRIORITY_COUNTRIES) {
            CountryConfigData config = internationalService.getCountryConfig(countryCode);
            
            assertNotNull(config, "Config ne doit pas être null pour " + countryCode);
            assertNotNull(config.getPays(), "Infos pays requises pour " + countryCode);
            assertNotNull(config.getStandardComptable(), "Standard comptable requis pour " + countryCode);
            
            // Tests spécifiques selon le pays
            switch (countryCode) {
                case "BF":
                case "CI": 
                case "SN":
                    assertEquals("SYSCOHADA", config.getStandardComptable().getCode());
                    assertEquals("XOF", config.getPays().getDevise());
                    break;
                case "FR":
                    assertEquals("PCG", config.getStandardComptable().getCode());
                    assertEquals("EUR", config.getPays().getDevise());
                    break;
                case "US":
                    assertEquals("US_GAAP", config.getStandardComptable().getCode());
                    assertEquals("USD", config.getPays().getDevise());
                    break;
            }
        }
    }
    
    @Test
    @DisplayName("Tester les APIs officielles disponibles")
    void testOfficialApisAvailability() {
        for (String countryCode : Arrays.asList("FR", "GB", "US")) {
            List<OfficialApiInfo> apis = internationalService.getAvailableApis(countryCode);
            
            assertFalse(apis.isEmpty(), "Des APIs officielles doivent être disponibles pour " + countryCode);
            
            // Vérifier qu'au moins une API est testable
            boolean hasTestableApi = apis.stream()
                .anyMatch(api -> api.getUrlBase() != null && !api.getUrlBase().isEmpty());
            
            assertTrue(hasTestableApi, "Au moins une API doit être testable pour " + countryCode);
        }
    }
}

/*
🚀 RÉSUMÉ DU PLAN D'INTÉGRATION

✅ BACKEND (Spring Boot)
- Entities JPA pour configuration pays
- Services business internationaux  
- API REST pour frontend
- Validation des intégrations
- Cache Redis pour performance

✅ FRONTEND (Angular)  
- Service international
- Composant sélection pays
- Localisation dynamique
- Intercepteur HTTP

✅ BASE DE DONNÉES
- Structure complète mondiale
- Procédures stockées d'export
- Vues analytiques
- Système de cache

✅ TESTS & VALIDATION
- Tests d'intégration automatisés
- Validation des APIs pays
- Monitoring des endpoints
- Tests de charge internationale

🎯 PROCHAINES ÉTAPES :
1. Importer la base de données
2. Configurer les services Spring Boot
3. Implémenter les composants Angular
4. Tester avec pays prioritaires
5. Déployer en production

🌍 PRÊT POUR CONQUÊTE MONDIALE ! 🚀
*/