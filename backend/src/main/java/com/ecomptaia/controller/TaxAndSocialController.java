package com.ecomptaia.controller;

import com.ecomptaia.service.TaxAndSocialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

/**
 * Contrôleur pour la gestion de la fiscalité et des charges sociales
 */
@RestController
@RequestMapping("/api/tax-and-social")
@CrossOrigin(origins = "*")
public class TaxAndSocialController {

    @Autowired
    private TaxAndSocialService taxAndSocialService;

    /**
     * Calcul des taxes sur les salaires
     */
    @PostMapping("/calculate-salary-taxes")
    public ResponseEntity<Map<String, Object>> calculateSalaryTaxes(
            @RequestParam String country,
            @RequestParam BigDecimal grossSalary,
            @RequestParam String employeeType,
            @RequestParam(required = false) String calculationDate,
            @RequestParam(required = false, defaultValue = "false") boolean hasChildren,
            @RequestParam(required = false, defaultValue = "0") int childrenCount) {
        
        LocalDate calcDate = calculationDate != null ? LocalDate.parse(calculationDate) : LocalDate.now();
        
        Map<String, Object> result = taxAndSocialService.calculateSalaryTaxes(
                country, grossSalary, employeeType, calcDate, hasChildren, childrenCount);
        
        return ResponseEntity.ok(result);
    }

    /**
     * Calcul des charges sociales
     */
    @PostMapping("/calculate-social-charges")
    public ResponseEntity<Map<String, Object>> calculateSocialCharges(
            @RequestParam String country,
            @RequestParam BigDecimal grossSalary,
            @RequestParam String employeeType,
            @RequestParam(required = false) String calculationDate,
            @RequestParam(required = false) String businessSector) {
        
        LocalDate calcDate = calculationDate != null ? LocalDate.parse(calculationDate) : LocalDate.now();
        
        Map<String, Object> result = taxAndSocialService.calculateSocialCharges(
                country, grossSalary, employeeType, calcDate, businessSector);
        
        return ResponseEntity.ok(result);
    }

    /**
     * Calcul de la TVA
     */
    @PostMapping("/calculate-vat")
    public ResponseEntity<Map<String, Object>> calculateVAT(
            @RequestParam String country,
            @RequestParam BigDecimal amount,
            @RequestParam String vatType,
            @RequestParam(required = false) String transactionDate,
            @RequestParam(required = false) String businessSector) {
        
        LocalDate transDate = transactionDate != null ? LocalDate.parse(transactionDate) : LocalDate.now();
        
        Map<String, Object> result = taxAndSocialService.calculateVAT(
                country, amount, vatType, transDate, businessSector);
        
        return ResponseEntity.ok(result);
    }

    /**
     * Génération de déclarations fiscales
     */
    @PostMapping("/generate-tax-declaration")
    public ResponseEntity<Map<String, Object>> generateTaxDeclaration(
            @RequestParam String country,
            @RequestParam String declarationType,
            @RequestParam String period,
            @RequestParam Long companyId,
            @RequestBody(required = false) Map<String, Object> financialData) {
        
        YearMonth periodObj = YearMonth.parse(period);
        
        Map<String, Object> result = taxAndSocialService.generateTaxDeclaration(
                country, declarationType, periodObj, companyId, financialData);
        
        return ResponseEntity.ok(result);
    }

    /**
     * Validation de conformité fiscale
     */
    @PostMapping("/validate-tax-compliance")
    public ResponseEntity<Map<String, Object>> validateTaxCompliance(
            @RequestParam String country,
            @RequestParam Long companyId,
            @RequestParam String period,
            @RequestBody(required = false) Map<String, Object> taxData) {
        
        YearMonth periodObj = YearMonth.parse(period);
        
        Map<String, Object> result = taxAndSocialService.validateTaxCompliance(
                country, companyId, periodObj, taxData);
        
        return ResponseEntity.ok(result);
    }

    /**
     * Endpoint de test pour les taxes sur salaires
     */
    @GetMapping("/test-salary-taxes")
    public ResponseEntity<Map<String, Object>> testSalaryTaxes() {
        Map<String, Object> result = Map.of(
                "france", taxAndSocialService.calculateSalaryTaxes(
                        "FRANCE", new BigDecimal("50000"), "CDI", LocalDate.now(), true, 2),
                "cameroun", taxAndSocialService.calculateSalaryTaxes(
                        "CAMEROUN", new BigDecimal("500000"), "CDI", LocalDate.now(), false, 0),
                "senegal", taxAndSocialService.calculateSalaryTaxes(
                        "SENEGAL", new BigDecimal("300000"), "CDI", LocalDate.now(), false, 0),
                "benin", taxAndSocialService.calculateSalaryTaxes(
                        "BENIN", new BigDecimal("250000"), "CDI", LocalDate.now(), false, 0),
                "gabon", taxAndSocialService.calculateSalaryTaxes(
                        "GABON", new BigDecimal("800000"), "CDI", LocalDate.now(), false, 0),
                "usa", taxAndSocialService.calculateSalaryTaxes(
                        "USA", new BigDecimal("80000"), "CDI", LocalDate.now(), true, 2),
                "allemagne", taxAndSocialService.calculateSalaryTaxes(
                        "ALLEMAGNE", new BigDecimal("60000"), "CDI", LocalDate.now(), true, 1),
                "status", "SUCCESS"
        );
        
        return ResponseEntity.ok(result);
    }

    /**
     * Endpoint de test pour les charges sociales
     */
    @GetMapping("/test-social-charges")
    public ResponseEntity<Map<String, Object>> testSocialCharges() {
        Map<String, Object> result = Map.of(
                "france", taxAndSocialService.calculateSocialCharges(
                        "FRANCE", new BigDecimal("50000"), "CDI", LocalDate.now(), "SERVICES"),
                "cameroun", taxAndSocialService.calculateSocialCharges(
                        "CAMEROUN", new BigDecimal("500000"), "CDI", LocalDate.now(), "COMMERCE"),
                "senegal", taxAndSocialService.calculateSocialCharges(
                        "SENEGAL", new BigDecimal("300000"), "CDI", LocalDate.now(), "INDUSTRIE"),
                "status", "SUCCESS"
        );
        
        return ResponseEntity.ok(result);
    }

    /**
     * Endpoint de test pour la TVA
     */
    @GetMapping("/test-vat")
    public ResponseEntity<Map<String, Object>> testVAT() {
        Map<String, Object> result = Map.of(
                "france_standard", taxAndSocialService.calculateVAT(
                        "FRANCE", new BigDecimal("1000"), "STANDARD", LocalDate.now(), "SERVICES"),
                "france_reduced", taxAndSocialService.calculateVAT(
                        "FRANCE", new BigDecimal("1000"), "REDUCED", LocalDate.now(), "SERVICES"),
                "cameroun", taxAndSocialService.calculateVAT(
                        "CAMEROUN", new BigDecimal("100000"), "STANDARD", LocalDate.now(), "COMMERCE"),
                "senegal", taxAndSocialService.calculateVAT(
                        "SENEGAL", new BigDecimal("50000"), "STANDARD", LocalDate.now(), "INDUSTRIE"),
                "status", "SUCCESS"
        );
        
        return ResponseEntity.ok(result);
    }

    /**
     * Endpoint de test pour les déclarations fiscales
     */
    @GetMapping("/test-tax-declarations")
    public ResponseEntity<Map<String, Object>> testTaxDeclarations() {
        Map<String, Object> financialData = Map.of(
                "revenue", new BigDecimal("1000000"),
                "expenses", new BigDecimal("600000"),
                "profit", new BigDecimal("400000")
        );
        
        Map<String, Object> result = Map.of(
                "france_tva", taxAndSocialService.generateTaxDeclaration(
                        "FRANCE", "TVA", YearMonth.of(2024, 12), 1L, financialData),
                "cameroun_tva", taxAndSocialService.generateTaxDeclaration(
                        "CAMEROUN", "TVA", YearMonth.of(2024, 12), 1L, financialData),
                "senegal_tva", taxAndSocialService.generateTaxDeclaration(
                        "SENEGAL", "TVA", YearMonth.of(2024, 12), 1L, financialData),
                "status", "SUCCESS"
        );
        
        return ResponseEntity.ok(result);
    }

    /**
     * Endpoint de test pour la validation de conformité
     */
    @GetMapping("/test-compliance")
    public ResponseEntity<Map<String, Object>> testCompliance() {
        Map<String, Object> taxData = Map.of(
                "tvaCollected", new BigDecimal("200000"),
                "tvaDeductible", new BigDecimal("150000"),
                "tvaToPay", new BigDecimal("50000"),
                "socialCharges", new BigDecimal("100000"),
                "incomeTax", new BigDecimal("80000")
        );
        
        Map<String, Object> result = Map.of(
                "france", taxAndSocialService.validateTaxCompliance(
                        "FRANCE", 1L, YearMonth.of(2024, 12), taxData),
                "cameroun", taxAndSocialService.validateTaxCompliance(
                        "CAMEROUN", 1L, YearMonth.of(2024, 12), taxData),
                "senegal", taxAndSocialService.validateTaxCompliance(
                        "SENEGAL", 1L, YearMonth.of(2024, 12), taxData),
                "status", "SUCCESS"
        );
        
        return ResponseEntity.ok(result);
    }

    /**
     * Récupération des taux de TVA par pays
     */
    @GetMapping("/vat-rates/{country}")
    public ResponseEntity<Map<String, Object>> getVATRates(@PathVariable String country) {
        Map<String, Object> vatRates = new java.util.HashMap<>();
        
        switch (country.toUpperCase()) {
            case "FRANCE":
                vatRates.put("STANDARD", "20%");
                vatRates.put("REDUCED", "10%");
                vatRates.put("SUPER_REDUCED", "5.5%");
                vatRates.put("ZERO", "0%");
                break;
            // Pays OHADA
            case "CAMEROUN":
            case "SENEGAL":
            case "COTE D'IVOIRE":
            case "BENIN":
            case "BURKINA FASO":
            case "CENTRAFRIQUE":
            case "REPUBLIQUE CENTRAFRICAINE":
            case "COMORES":
            case "CONGO":
            case "REPUBLIQUE DU CONGO":
            case "GABON":
            case "GUINEE":
            case "REPUBLIQUE DE GUINEE":
            case "GUINEE BISSAU":
            case "GUINEE EQUATORIALE":
            case "MALI":
            case "NIGER":
            case "RD CONGO":
            case "REPUBLIQUE DEMOCRATIQUE DU CONGO":
            case "TCHAD":
            case "TOGO":
                vatRates.put("STANDARD", "19.5%");
                vatRates.put("REDUCED", "9%");
                vatRates.put("ZERO", "0%");
                break;
            // Autres pays africains
            case "MAROC":
            case "MOROCCO":
                vatRates.put("STANDARD", "20%");
                vatRates.put("REDUCED", "10%");
                vatRates.put("ZERO", "0%");
                break;
            case "TUNISIE":
            case "TUNISIA":
                vatRates.put("STANDARD", "19%");
                vatRates.put("REDUCED", "7%");
                vatRates.put("ZERO", "0%");
                break;
            case "ALGERIE":
            case "ALGERIA":
                vatRates.put("STANDARD", "19%");
                vatRates.put("REDUCED", "9%");
                vatRates.put("ZERO", "0%");
                break;
            case "EGYPTE":
            case "EGYPT":
                vatRates.put("STANDARD", "14%");
                vatRates.put("REDUCED", "5%");
                vatRates.put("ZERO", "0%");
                break;
            case "NIGERIA":
                vatRates.put("STANDARD", "7.5%");
                vatRates.put("REDUCED", "5%");
                vatRates.put("ZERO", "0%");
                break;
            case "GHANA":
                vatRates.put("STANDARD", "12.5%");
                vatRates.put("REDUCED", "3%");
                vatRates.put("ZERO", "0%");
                break;
            case "KENYA":
                vatRates.put("STANDARD", "16%");
                vatRates.put("REDUCED", "8%");
                vatRates.put("ZERO", "0%");
                break;
            case "AFRIQUE DU SUD":
            case "SOUTH AFRICA":
                vatRates.put("STANDARD", "15%");
                vatRates.put("REDUCED", "0%");
                vatRates.put("ZERO", "0%");
                break;
            // Pays européens
            case "ALLEMAGNE":
            case "GERMANY":
                vatRates.put("STANDARD", "19%");
                vatRates.put("REDUCED", "7%");
                vatRates.put("ZERO", "0%");
                break;
            case "BELGIQUE":
            case "BELGIUM":
                vatRates.put("STANDARD", "21%");
                vatRates.put("REDUCED", "12%");
                vatRates.put("SUPER_REDUCED", "6%");
                vatRates.put("ZERO", "0%");
                break;
            case "SUISSE":
            case "SWITZERLAND":
                vatRates.put("STANDARD", "7.7%");
                vatRates.put("REDUCED", "2.5%");
                vatRates.put("ZERO", "0%");
                break;
            case "LUXEMBOURG":
                vatRates.put("STANDARD", "17%");
                vatRates.put("REDUCED", "14%");
                vatRates.put("SUPER_REDUCED", "8%");
                vatRates.put("ZERO", "0%");
                break;
            case "ROYAUME UNI":
            case "UK":
            case "UNITED KINGDOM":
                vatRates.put("STANDARD", "20%");
                vatRates.put("REDUCED", "5%");
                vatRates.put("ZERO", "0%");
                break;
            case "ESPAGNE":
            case "SPAIN":
                vatRates.put("STANDARD", "21%");
                vatRates.put("REDUCED", "10%");
                vatRates.put("SUPER_REDUCED", "4%");
                vatRates.put("ZERO", "0%");
                break;
            case "ITALIE":
            case "ITALY":
                vatRates.put("STANDARD", "22%");
                vatRates.put("REDUCED", "10%");
                vatRates.put("SUPER_REDUCED", "5%");
                vatRates.put("ZERO", "0%");
                break;
            case "PORTUGAL":
                vatRates.put("STANDARD", "23%");
                vatRates.put("REDUCED", "13%");
                vatRates.put("SUPER_REDUCED", "6%");
                vatRates.put("ZERO", "0%");
                break;
            case "PAYS BAS":
            case "NETHERLANDS":
                vatRates.put("STANDARD", "21%");
                vatRates.put("REDUCED", "9%");
                vatRates.put("ZERO", "0%");
                break;
            case "DANEMARK":
            case "DENMARK":
                vatRates.put("STANDARD", "25%");
                vatRates.put("REDUCED", "0%");
                vatRates.put("ZERO", "0%");
                break;
            case "SUEDE":
            case "SWEDEN":
                vatRates.put("STANDARD", "25%");
                vatRates.put("REDUCED", "12%");
                vatRates.put("SUPER_REDUCED", "6%");
                vatRates.put("ZERO", "0%");
                break;
            case "NORVEGE":
            case "NORWAY":
                vatRates.put("STANDARD", "25%");
                vatRates.put("REDUCED", "15%");
                vatRates.put("SUPER_REDUCED", "12%");
                vatRates.put("ZERO", "0%");
                break;
            case "FINLANDE":
            case "FINLAND":
                vatRates.put("STANDARD", "24%");
                vatRates.put("REDUCED", "14%");
                vatRates.put("SUPER_REDUCED", "10%");
                vatRates.put("ZERO", "0%");
                break;
            case "AUTRICHE":
            case "AUSTRIA":
                vatRates.put("STANDARD", "20%");
                vatRates.put("REDUCED", "10%");
                vatRates.put("SUPER_REDUCED", "13%");
                vatRates.put("ZERO", "0%");
                break;
            case "POLOGNE":
            case "POLAND":
                vatRates.put("STANDARD", "23%");
                vatRates.put("REDUCED", "8%");
                vatRates.put("SUPER_REDUCED", "5%");
                vatRates.put("ZERO", "0%");
                break;
            case "REPUBLIQUE TCHEQUE":
            case "CZECH REPUBLIC":
                vatRates.put("STANDARD", "21%");
                vatRates.put("REDUCED", "15%");
                vatRates.put("SUPER_REDUCED", "10%");
                vatRates.put("ZERO", "0%");
                break;
            case "HONGRIE":
            case "HUNGARY":
                vatRates.put("STANDARD", "27%");
                vatRates.put("REDUCED", "18%");
                vatRates.put("SUPER_REDUCED", "5%");
                vatRates.put("ZERO", "0%");
                break;
            case "ROUMANIE":
            case "ROMANIA":
                vatRates.put("STANDARD", "19%");
                vatRates.put("REDUCED", "9%");
                vatRates.put("SUPER_REDUCED", "5%");
                vatRates.put("ZERO", "0%");
                break;
            case "BULGARIE":
            case "BULGARIA":
                vatRates.put("STANDARD", "20%");
                vatRates.put("REDUCED", "9%");
                vatRates.put("ZERO", "0%");
                break;
            case "GRECE":
            case "GREECE":
                vatRates.put("STANDARD", "24%");
                vatRates.put("REDUCED", "13%");
                vatRates.put("SUPER_REDUCED", "6%");
                vatRates.put("ZERO", "0%");
                break;
            case "CHYPRE":
            case "CYPRUS":
                vatRates.put("STANDARD", "19%");
                vatRates.put("REDUCED", "9%");
                vatRates.put("SUPER_REDUCED", "5%");
                vatRates.put("ZERO", "0%");
                break;
            case "MALTE":
            case "MALTA":
                vatRates.put("STANDARD", "18%");
                vatRates.put("REDUCED", "7%");
                vatRates.put("SUPER_REDUCED", "5%");
                vatRates.put("ZERO", "0%");
                break;
            case "SLOVENIE":
            case "SLOVENIA":
                vatRates.put("STANDARD", "22%");
                vatRates.put("REDUCED", "9.5%");
                vatRates.put("ZERO", "0%");
                break;
            case "SLOVAQUIE":
            case "SLOVAKIA":
                vatRates.put("STANDARD", "20%");
                vatRates.put("REDUCED", "10%");
                vatRates.put("ZERO", "0%");
                break;
            case "CROATIE":
            case "CROATIA":
                vatRates.put("STANDARD", "25%");
                vatRates.put("REDUCED", "13%");
                vatRates.put("SUPER_REDUCED", "5%");
                vatRates.put("ZERO", "0%");
                break;
            case "LITUANIE":
            case "LITHUANIA":
                vatRates.put("STANDARD", "21%");
                vatRates.put("REDUCED", "9%");
                vatRates.put("SUPER_REDUCED", "5%");
                vatRates.put("ZERO", "0%");
                break;
            case "LETTONIE":
            case "LATVIA":
                vatRates.put("STANDARD", "21%");
                vatRates.put("REDUCED", "12%");
                vatRates.put("SUPER_REDUCED", "5%");
                vatRates.put("ZERO", "0%");
                break;
            case "ESTONIE":
            case "ESTONIA":
                vatRates.put("STANDARD", "20%");
                vatRates.put("REDUCED", "9%");
                vatRates.put("ZERO", "0%");
                break;
            case "IRLANDE":
            case "IRELAND":
                vatRates.put("STANDARD", "23%");
                vatRates.put("REDUCED", "13.5%");
                vatRates.put("SUPER_REDUCED", "9%");
                vatRates.put("ZERO", "0%");
                break;
            // Amérique du Nord
            case "ETATS UNIS":
            case "USA":
            case "UNITED STATES":
                vatRates.put("STANDARD", "0%"); // Pas de TVA fédérale
                vatRates.put("STATE_TAX", "Variable par état");
                vatRates.put("ZERO", "0%");
                break;
            case "CANADA":
                vatRates.put("STANDARD", "5%"); // GST fédérale
                vatRates.put("PROVINCIAL", "Variable par province");
                vatRates.put("ZERO", "0%");
                break;
            default:
                vatRates.put("STANDARD", "15%");
                vatRates.put("REDUCED", "10%");
                vatRates.put("ZERO", "0%");
        }
        
        Map<String, Object> result = Map.of(
                "country", country,
                "vatRates", vatRates,
                "status", "SUCCESS"
        );
        
        return ResponseEntity.ok(result);
    }

    /**
     * Récupération des barèmes fiscaux par pays
     */
    @GetMapping("/tax-brackets/{country}")
    public ResponseEntity<Map<String, Object>> getTaxBrackets(@PathVariable String country) {
        Map<String, Object> taxBrackets = new java.util.HashMap<>();
        
        switch (country.toUpperCase()) {
            case "FRANCE":
                taxBrackets.put("bracket1", Map.of("min", 0, "max", 11294, "rate", "0%"));
                taxBrackets.put("bracket2", Map.of("min", 11294, "max", 28797, "rate", "11%"));
                taxBrackets.put("bracket3", Map.of("min", 28797, "max", 82341, "rate", "30%"));
                taxBrackets.put("bracket4", Map.of("min", 82341, "max", 177106, "rate", "41%"));
                taxBrackets.put("bracket5", Map.of("min", 177106, "max", null, "rate", "45%"));
                break;
            case "CAMEROUN":
                taxBrackets.put("bracket1", Map.of("min", 0, "max", 500000, "rate", "10%"));
                taxBrackets.put("bracket2", Map.of("min", 500000, "max", 1500000, "rate", "15%"));
                taxBrackets.put("bracket3", Map.of("min", 1500000, "max", null, "rate", "25%"));
                break;
            default:
                taxBrackets.put("bracket1", Map.of("min", 0, "max", null, "rate", "15%"));
        }
        
        Map<String, Object> result = Map.of(
                "country", country,
                "taxBrackets", taxBrackets,
                "status", "SUCCESS"
        );
        
        return ResponseEntity.ok(result);
    }
}
