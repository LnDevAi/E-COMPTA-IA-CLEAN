package com.ecomptaia.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

/**
 * Service de gestion de la fiscalité et des charges sociales
 */
@Service
public class TaxAndSocialService {

    /**
     * Calcul des taxes sur les salaires par pays
     */
    public Map<String, Object> calculateSalaryTaxes(
            String country, BigDecimal grossSalary, String employeeType, 
            LocalDate calculationDate, boolean hasChildren, int childrenCount) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> taxCalculation = new HashMap<>();
            
            switch (country.toUpperCase()) {
                case "FRANCE":
                    taxCalculation = calculateFrenchSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                // Pays OHADA
                case "CAMEROUN":
                    taxCalculation = calculateCameroonianSalaryTaxes(grossSalary, employeeType);
                    break;
                case "SENEGAL":
                    taxCalculation = calculateSenegaleseSalaryTaxes(grossSalary, employeeType);
                    break;
                case "COTE D'IVOIRE":
                    taxCalculation = calculateIvorianSalaryTaxes(grossSalary, employeeType);
                    break;
                case "BENIN":
                    taxCalculation = calculateBenineseSalaryTaxes(grossSalary, employeeType);
                    break;
                case "BURKINA FASO":
                    taxCalculation = calculateBurkinabeSalaryTaxes(grossSalary, employeeType);
                    break;
                case "CENTRAFRIQUE":
                case "REPUBLIQUE CENTRAFRICAINE":
                    taxCalculation = calculateCentralAfricanSalaryTaxes(grossSalary, employeeType);
                    break;
                case "COMORES":
                    taxCalculation = calculateComorianSalaryTaxes(grossSalary, employeeType);
                    break;
                case "CONGO":
                case "REPUBLIQUE DU CONGO":
                    taxCalculation = calculateCongoleseSalaryTaxes(grossSalary, employeeType);
                    break;
                case "GABON":
                    taxCalculation = calculateGaboneseSalaryTaxes(grossSalary, employeeType);
                    break;
                case "GUINEE":
                case "REPUBLIQUE DE GUINEE":
                    taxCalculation = calculateGuineanSalaryTaxes(grossSalary, employeeType);
                    break;
                case "GUINEE BISSAU":
                    taxCalculation = calculateGuineaBissauSalaryTaxes(grossSalary, employeeType);
                    break;
                case "GUINEE EQUATORIALE":
                    taxCalculation = calculateEquatorialGuineanSalaryTaxes(grossSalary, employeeType);
                    break;
                case "MALI":
                    taxCalculation = calculateMalianSalaryTaxes(grossSalary, employeeType);
                    break;
                case "NIGER":
                    taxCalculation = calculateNigerienSalaryTaxes(grossSalary, employeeType);
                    break;
                case "RD CONGO":
                case "REPUBLIQUE DEMOCRATIQUE DU CONGO":
                    taxCalculation = calculateDRCSalaryTaxes(grossSalary, employeeType);
                    break;
                case "TCHAD":
                    taxCalculation = calculateChadianSalaryTaxes(grossSalary, employeeType);
                    break;
                case "TOGO":
                    taxCalculation = calculateTogoleseSalaryTaxes(grossSalary, employeeType);
                    break;
                // Autres pays internationaux
                case "ETATS UNIS":
                case "USA":
                case "UNITED STATES":
                    taxCalculation = calculateUSSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                case "CANADA":
                    taxCalculation = calculateCanadianSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                case "ALLEMAGNE":
                case "GERMANY":
                    taxCalculation = calculateGermanSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                case "BELGIQUE":
                case "BELGIUM":
                    taxCalculation = calculateBelgianSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                case "SUISSE":
                case "SWITZERLAND":
                    taxCalculation = calculateSwissSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                case "LUXEMBOURG":
                    taxCalculation = calculateLuxembourgSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                case "MAROC":
                case "MOROCCO":
                    taxCalculation = calculateMoroccanSalaryTaxes(grossSalary, employeeType);
                    break;
                case "TUNISIE":
                case "TUNISIA":
                    taxCalculation = calculateTunisianSalaryTaxes(grossSalary, employeeType);
                    break;
                case "ALGERIE":
                case "ALGERIA":
                    taxCalculation = calculateAlgerianSalaryTaxes(grossSalary, employeeType);
                    break;
                case "EGYPTE":
                case "EGYPT":
                    taxCalculation = calculateEgyptianSalaryTaxes(grossSalary, employeeType);
                    break;
                case "NIGERIA":
                    taxCalculation = calculateNigerianSalaryTaxes(grossSalary, employeeType);
                    break;
                case "GHANA":
                    taxCalculation = calculateGhanaianSalaryTaxes(grossSalary, employeeType);
                    break;
                case "KENYA":
                    taxCalculation = calculateKenyanSalaryTaxes(grossSalary, employeeType);
                    break;
                case "AFRIQUE DU SUD":
                case "SOUTH AFRICA":
                    taxCalculation = calculateSouthAfricanSalaryTaxes(grossSalary, employeeType);
                    break;
                case "ROYAUME UNI":
                case "UK":
                case "UNITED KINGDOM":
                    taxCalculation = calculateUKSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                case "ESPAGNE":
                case "SPAIN":
                    taxCalculation = calculateSpanishSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                case "ITALIE":
                case "ITALY":
                    taxCalculation = calculateItalianSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                case "PORTUGAL":
                    taxCalculation = calculatePortugueseSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                case "PAYS BAS":
                case "NETHERLANDS":
                    taxCalculation = calculateDutchSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                case "DANEMARK":
                case "DENMARK":
                    taxCalculation = calculateDanishSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                case "SUEDE":
                case "SWEDEN":
                    taxCalculation = calculateSwedishSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                case "NORVEGE":
                case "NORWAY":
                    taxCalculation = calculateNorwegianSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                case "FINLANDE":
                case "FINLAND":
                    taxCalculation = calculateFinnishSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                case "AUTRICHE":
                case "AUSTRIA":
                    taxCalculation = calculateAustrianSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                case "POLOGNE":
                case "POLAND":
                    taxCalculation = calculatePolishSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                case "REPUBLIQUE TCHEQUE":
                case "CZECH REPUBLIC":
                    taxCalculation = calculateCzechSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                case "HONGRIE":
                case "HUNGARY":
                    taxCalculation = calculateHungarianSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                case "ROUMANIE":
                case "ROMANIA":
                    taxCalculation = calculateRomanianSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                case "BULGARIE":
                case "BULGARIA":
                    taxCalculation = calculateBulgarianSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                case "GRECE":
                case "GREECE":
                    taxCalculation = calculateGreekSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                case "CHYPRE":
                case "CYPRUS":
                    taxCalculation = calculateCypriotSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                case "MALTE":
                case "MALTA":
                    taxCalculation = calculateMalteseSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                case "SLOVENIE":
                case "SLOVENIA":
                    taxCalculation = calculateSlovenianSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                case "SLOVAQUIE":
                case "SLOVAKIA":
                    taxCalculation = calculateSlovakSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                case "CROATIE":
                case "CROATIA":
                    taxCalculation = calculateCroatianSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                case "LITUANIE":
                case "LITHUANIA":
                    taxCalculation = calculateLithuanianSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                case "LETTONIE":
                case "LATVIA":
                    taxCalculation = calculateLatvianSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                case "ESTONIE":
                case "ESTONIA":
                    taxCalculation = calculateEstonianSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                case "IRLANDE":
                case "IRELAND":
                    taxCalculation = calculateIrishSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
                    break;
                default:
                    taxCalculation = calculateGenericSalaryTaxes(grossSalary, employeeType);
            }
            
            result.put("country", country);
            result.put("grossSalary", grossSalary);
            result.put("calculationDate", calculationDate);
            result.put("taxCalculation", taxCalculation);
            result.put("status", "SUCCESS");
            
        } catch (Exception e) {
            result.put("error", "Erreur lors du calcul: " + e.getMessage());
            result.put("status", "ERROR");
        }
        
        return result;
    }

    /**
     * Calcul des charges sociales par pays
     */
    public Map<String, Object> calculateSocialCharges(
            String country, BigDecimal grossSalary, String employeeType, 
            LocalDate calculationDate, String businessSector) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> socialCalculation = new HashMap<>();
            
            switch (country.toUpperCase()) {
                case "FRANCE":
                    socialCalculation = calculateFrenchSocialCharges(grossSalary, employeeType, businessSector);
                    break;
                case "CAMEROUN":
                    socialCalculation = calculateCameroonianSocialCharges(grossSalary, employeeType);
                    break;
                case "SENEGAL":
                    socialCalculation = calculateSenegaleseSocialCharges(grossSalary, employeeType);
                    break;
                case "COTE D'IVOIRE":
                    socialCalculation = calculateIvorianSocialCharges(grossSalary, employeeType);
                    break;
                default:
                    socialCalculation = calculateGenericSocialCharges(grossSalary, employeeType);
            }
            
            result.put("country", country);
            result.put("grossSalary", grossSalary);
            result.put("calculationDate", calculationDate);
            result.put("socialCalculation", socialCalculation);
            result.put("status", "SUCCESS");
            
        } catch (Exception e) {
            result.put("error", "Erreur lors du calcul: " + e.getMessage());
            result.put("status", "ERROR");
        }
        
        return result;
    }

    /**
     * Calcul de la TVA par pays
     */
    public Map<String, Object> calculateVAT(
            String country, BigDecimal amount, String vatType, 
            LocalDate transactionDate, String businessSector) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> vatCalculation = new HashMap<>();
            
            switch (country.toUpperCase()) {
                case "FRANCE":
                    vatCalculation = calculateFrenchVAT(amount, vatType, businessSector);
                    break;
                case "CAMEROUN":
                    vatCalculation = calculateCameroonianVAT(amount, vatType);
                    break;
                case "SENEGAL":
                    vatCalculation = calculateSenegaleseVAT(amount, vatType);
                    break;
                case "COTE D'IVOIRE":
                    vatCalculation = calculateIvorianVAT(amount, vatType);
                    break;
                default:
                    vatCalculation = calculateGenericVAT(amount, vatType);
            }
            
            result.put("country", country);
            result.put("amount", amount);
            result.put("vatType", vatType);
            result.put("transactionDate", transactionDate);
            result.put("vatCalculation", vatCalculation);
            result.put("status", "SUCCESS");
            
        } catch (Exception e) {
            result.put("error", "Erreur lors du calcul: " + e.getMessage());
            result.put("status", "ERROR");
        }
        
        return result;
    }

    /**
     * Génération de déclarations fiscales
     */
    public Map<String, Object> generateTaxDeclaration(
            String country, String declarationType, YearMonth period, 
            Long companyId, Map<String, Object> financialData) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> declaration = new HashMap<>();
            
            switch (country.toUpperCase()) {
                case "FRANCE":
                    declaration = generateFrenchTaxDeclaration(declarationType, period, financialData);
                    break;
                case "CAMEROUN":
                    declaration = generateCameroonianTaxDeclaration(declarationType, period, financialData);
                    break;
                case "SENEGAL":
                    declaration = generateSenegaleseTaxDeclaration(declarationType, period, financialData);
                    break;
                case "COTE D'IVOIRE":
                    declaration = generateIvorianTaxDeclaration(declarationType, period, financialData);
                    break;
                default:
                    declaration = generateGenericTaxDeclaration(declarationType, period, financialData);
            }
            
            result.put("country", country);
            result.put("declarationType", declarationType);
            result.put("period", period);
            result.put("companyId", companyId);
            result.put("declaration", declaration);
            result.put("status", "SUCCESS");
            
        } catch (Exception e) {
            result.put("error", "Erreur lors de la génération: " + e.getMessage());
            result.put("status", "ERROR");
        }
        
        return result;
    }

    /**
     * Validation de conformité fiscale
     */
    public Map<String, Object> validateTaxCompliance(
            String country, Long companyId, YearMonth period, 
            Map<String, Object> taxData) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<String> warnings = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            List<String> recommendations = new ArrayList<>();
            
            // Validation spécifique par pays
            switch (country.toUpperCase()) {
                case "FRANCE":
                    validateFrenchCompliance(taxData, warnings, errors, recommendations);
                    break;
                case "CAMEROUN":
                    validateCameroonianCompliance(taxData, warnings, errors, recommendations);
                    break;
                case "SENEGAL":
                    validateSenegaleseCompliance(taxData, warnings, errors, recommendations);
                    break;
                case "COTE D'IVOIRE":
                    validateIvorianCompliance(taxData, warnings, errors, recommendations);
                    break;
                default:
                    validateGenericCompliance(taxData, warnings, errors, recommendations);
            }
            
            result.put("country", country);
            result.put("companyId", companyId);
            result.put("period", period);
            result.put("warnings", warnings);
            result.put("errors", errors);
            result.put("recommendations", recommendations);
            result.put("isCompliant", errors.isEmpty());
            result.put("validationDate", LocalDate.now());
            result.put("status", "SUCCESS");
            
        } catch (Exception e) {
            result.put("error", "Erreur lors de la validation: " + e.getMessage());
            result.put("status", "ERROR");
        }
        
        return result;
    }

    // Méthodes de calcul spécifiques par pays - FRANCE
    private Map<String, Object> calculateFrenchSalaryTaxes(
            BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        
        Map<String, Object> calculation = new HashMap<>();
        
        // Barème 2024 simplifié
        BigDecimal taxableIncome = grossSalary.multiply(new BigDecimal("0.9")); // Abattement 10%
        
        BigDecimal taxAmount = BigDecimal.ZERO;
        if (taxableIncome.compareTo(new BigDecimal("11294")) <= 0) {
            taxAmount = BigDecimal.ZERO;
        } else if (taxableIncome.compareTo(new BigDecimal("28797")) <= 0) {
            taxAmount = taxableIncome.subtract(new BigDecimal("11294"))
                    .multiply(new BigDecimal("0.11"));
        } else if (taxableIncome.compareTo(new BigDecimal("82341")) <= 0) {
            taxAmount = new BigDecimal("1925.33")
                    .add(taxableIncome.subtract(new BigDecimal("28797"))
                            .multiply(new BigDecimal("0.30")));
        } else {
            taxAmount = new BigDecimal("18048.33")
                    .add(taxableIncome.subtract(new BigDecimal("82341"))
                            .multiply(new BigDecimal("0.41")));
        }
        
        // Quotient familial pour enfants
        if (hasChildren && childrenCount > 0) {
            BigDecimal familyQuotient = new BigDecimal(1 + (childrenCount * 0.5));
            taxAmount = taxAmount.divide(familyQuotient, 2, RoundingMode.HALF_UP);
        }
        
        calculation.put("taxableIncome", taxableIncome);
        calculation.put("taxAmount", taxAmount);
        calculation.put("netSalary", grossSalary.subtract(taxAmount));
        calculation.put("taxRate", taxAmount.divide(grossSalary, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100")));
        
        return calculation;
    }

    private Map<String, Object> calculateFrenchSocialCharges(
            BigDecimal grossSalary, String employeeType, String businessSector) {
        
        Map<String, Object> calculation = new HashMap<>();
        
        // Charges sociales employeur (environ 42%)
        BigDecimal employerCharges = grossSalary.multiply(new BigDecimal("0.42"));
        
        // Charges sociales salarié (environ 22%)
        BigDecimal employeeCharges = grossSalary.multiply(new BigDecimal("0.22"));
        
        // Charges totales
        BigDecimal totalCharges = employerCharges.add(employeeCharges);
        
        calculation.put("employerCharges", employerCharges);
        calculation.put("employeeCharges", employeeCharges);
        calculation.put("totalCharges", totalCharges);
        calculation.put("netSalary", grossSalary.subtract(employeeCharges));
        calculation.put("totalCost", grossSalary.add(employerCharges));
        
        return calculation;
    }

    private Map<String, Object> calculateFrenchVAT(
            BigDecimal amount, String vatType, String businessSector) {
        
        Map<String, Object> calculation = new HashMap<>();
        
        BigDecimal vatRate = BigDecimal.ZERO;
        switch (vatType.toUpperCase()) {
            case "STANDARD":
                vatRate = new BigDecimal("0.20"); // 20%
                break;
            case "REDUCED":
                vatRate = new BigDecimal("0.10"); // 10%
                break;
            case "SUPER_REDUCED":
                vatRate = new BigDecimal("0.055"); // 5.5%
                break;
            case "ZERO":
                vatRate = BigDecimal.ZERO;
                break;
        }
        
        BigDecimal vatAmount = amount.multiply(vatRate);
        BigDecimal totalWithVAT = amount.add(vatAmount);
        
        calculation.put("amount", amount);
        calculation.put("vatRate", vatRate.multiply(new BigDecimal("100")));
        calculation.put("vatAmount", vatAmount);
        calculation.put("totalWithVAT", totalWithVAT);
        
        return calculation;
    }

    // Méthodes de calcul spécifiques par pays - CAMEROUN
    private Map<String, Object> calculateCameroonianSalaryTaxes(
            BigDecimal grossSalary, String employeeType) {
        
        Map<String, Object> calculation = new HashMap<>();
        
        // Barème fiscal camerounais simplifié
        BigDecimal taxableIncome = grossSalary.multiply(new BigDecimal("0.8")); // Abattement 20%
        
        BigDecimal taxAmount = BigDecimal.ZERO;
        if (taxableIncome.compareTo(new BigDecimal("500000")) <= 0) {
            taxAmount = taxableIncome.multiply(new BigDecimal("0.10"));
        } else if (taxableIncome.compareTo(new BigDecimal("1500000")) <= 0) {
            taxAmount = new BigDecimal("50000")
                    .add(taxableIncome.subtract(new BigDecimal("500000"))
                            .multiply(new BigDecimal("0.15")));
        } else {
            taxAmount = new BigDecimal("200000")
                    .add(taxableIncome.subtract(new BigDecimal("1500000"))
                            .multiply(new BigDecimal("0.25")));
        }
        
        calculation.put("taxableIncome", taxableIncome);
        calculation.put("taxAmount", taxAmount);
        calculation.put("netSalary", grossSalary.subtract(taxAmount));
        calculation.put("taxRate", taxAmount.divide(grossSalary, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100")));
        
        return calculation;
    }

    private Map<String, Object> calculateCameroonianSocialCharges(
            BigDecimal grossSalary, String employeeType) {
        
        Map<String, Object> calculation = new HashMap<>();
        
        // Charges sociales CNPS (Cameroun)
        BigDecimal employerCharges = grossSalary.multiply(new BigDecimal("0.15")); // 15%
        BigDecimal employeeCharges = grossSalary.multiply(new BigDecimal("0.05")); // 5%
        
        BigDecimal totalCharges = employerCharges.add(employeeCharges);
        
        calculation.put("employerCharges", employerCharges);
        calculation.put("employeeCharges", employeeCharges);
        calculation.put("totalCharges", totalCharges);
        calculation.put("netSalary", grossSalary.subtract(employeeCharges));
        calculation.put("totalCost", grossSalary.add(employerCharges));
        
        return calculation;
    }

    private Map<String, Object> calculateCameroonianVAT(
            BigDecimal amount, String vatType) {
        
        Map<String, Object> calculation = new HashMap<>();
        
        BigDecimal vatRate = new BigDecimal("0.195"); // 19.5% TVA Cameroun
        BigDecimal vatAmount = amount.multiply(vatRate);
        BigDecimal totalWithVAT = amount.add(vatAmount);
        
        calculation.put("amount", amount);
        calculation.put("vatRate", vatRate.multiply(new BigDecimal("100")));
        calculation.put("vatAmount", vatAmount);
        calculation.put("totalWithVAT", totalWithVAT);
        
        return calculation;
    }

    // Méthodes génériques pour les autres pays
    private Map<String, Object> calculateGenericSalaryTaxes(
            BigDecimal grossSalary, String employeeType) {
        
        Map<String, Object> calculation = new HashMap<>();
        
        // Calcul générique (15% du salaire brut)
        BigDecimal taxAmount = grossSalary.multiply(new BigDecimal("0.15"));
        
        calculation.put("taxableIncome", grossSalary);
        calculation.put("taxAmount", taxAmount);
        calculation.put("netSalary", grossSalary.subtract(taxAmount));
        calculation.put("taxRate", new BigDecimal("15"));
        
        return calculation;
    }

    private Map<String, Object> calculateGenericSocialCharges(
            BigDecimal grossSalary, String employeeType) {
        
        Map<String, Object> calculation = new HashMap<>();
        
        // Charges génériques (20% employeur + 10% salarié)
        BigDecimal employerCharges = grossSalary.multiply(new BigDecimal("0.20"));
        BigDecimal employeeCharges = grossSalary.multiply(new BigDecimal("0.10"));
        
        calculation.put("employerCharges", employerCharges);
        calculation.put("employeeCharges", employeeCharges);
        calculation.put("totalCharges", employerCharges.add(employeeCharges));
        calculation.put("netSalary", grossSalary.subtract(employeeCharges));
        calculation.put("totalCost", grossSalary.add(employerCharges));
        
        return calculation;
    }

    private Map<String, Object> calculateGenericVAT(
            BigDecimal amount, String vatType) {
        
        Map<String, Object> calculation = new HashMap<>();
        
        BigDecimal vatRate = new BigDecimal("0.15"); // 15% par défaut
        BigDecimal vatAmount = amount.multiply(vatRate);
        BigDecimal totalWithVAT = amount.add(vatAmount);
        
        calculation.put("amount", amount);
        calculation.put("vatRate", vatRate.multiply(new BigDecimal("100")));
        calculation.put("vatAmount", vatAmount);
        calculation.put("totalWithVAT", totalWithVAT);
        
        return calculation;
    }

    // Méthodes pour Sénégal et Côte d'Ivoire (similaires au Cameroun)
    private Map<String, Object> calculateSenegaleseSalaryTaxes(
            BigDecimal grossSalary, String employeeType) {
        return calculateCameroonianSalaryTaxes(grossSalary, employeeType);
    }

    private Map<String, Object> calculateSenegaleseSocialCharges(
            BigDecimal grossSalary, String employeeType) {
        return calculateCameroonianSocialCharges(grossSalary, employeeType);
    }

    private Map<String, Object> calculateSenegaleseVAT(
            BigDecimal amount, String vatType) {
        return calculateCameroonianVAT(amount, vatType);
    }

    private Map<String, Object> calculateIvorianSalaryTaxes(
            BigDecimal grossSalary, String employeeType) {
        return calculateCameroonianSalaryTaxes(grossSalary, employeeType);
    }

    private Map<String, Object> calculateIvorianSocialCharges(
            BigDecimal grossSalary, String employeeType) {
        return calculateCameroonianSocialCharges(grossSalary, employeeType);
    }

    private Map<String, Object> calculateIvorianVAT(
            BigDecimal amount, String vatType) {
        return calculateCameroonianVAT(amount, vatType);
    }

    // Méthodes de génération de déclarations
    private Map<String, Object> generateFrenchTaxDeclaration(
            String declarationType, YearMonth period, Map<String, Object> financialData) {
        
        Map<String, Object> declaration = new HashMap<>();
        declaration.put("declarationType", declarationType);
        declaration.put("period", period);
        declaration.put("country", "FRANCE");
        declaration.put("generatedDate", LocalDate.now());
        declaration.put("status", "DRAFT");
        
        return declaration;
    }

    private Map<String, Object> generateCameroonianTaxDeclaration(
            String declarationType, YearMonth period, Map<String, Object> financialData) {
        
        Map<String, Object> declaration = new HashMap<>();
        declaration.put("declarationType", declarationType);
        declaration.put("period", period);
        declaration.put("country", "CAMEROUN");
        declaration.put("generatedDate", LocalDate.now());
        declaration.put("status", "DRAFT");
        
        return declaration;
    }

    private Map<String, Object> generateSenegaleseTaxDeclaration(
            String declarationType, YearMonth period, Map<String, Object> financialData) {
        return generateCameroonianTaxDeclaration(declarationType, period, financialData);
    }

    private Map<String, Object> generateIvorianTaxDeclaration(
            String declarationType, YearMonth period, Map<String, Object> financialData) {
        return generateCameroonianTaxDeclaration(declarationType, period, financialData);
    }

    private Map<String, Object> generateGenericTaxDeclaration(
            String declarationType, YearMonth period, Map<String, Object> financialData) {
        
        Map<String, Object> declaration = new HashMap<>();
        declaration.put("declarationType", declarationType);
        declaration.put("period", period);
        declaration.put("country", "GENERIC");
        declaration.put("generatedDate", LocalDate.now());
        declaration.put("status", "DRAFT");
        
        return declaration;
    }

    // Méthodes de validation de conformité
    private void validateFrenchCompliance(
            Map<String, Object> taxData, List<String> warnings, 
            List<String> errors, List<String> recommendations) {
        
        // Validation spécifique France
        recommendations.add("Vérifier la déclaration TVA mensuelle");
        recommendations.add("Contrôler les charges sociales URSSAF");
        recommendations.add("Valider la déclaration fiscale annuelle");
    }

    private void validateCameroonianCompliance(
            Map<String, Object> taxData, List<String> warnings, 
            List<String> errors, List<String> recommendations) {
        
        // Validation spécifique Cameroun
        recommendations.add("Vérifier la déclaration TVA trimestrielle");
        recommendations.add("Contrôler les charges CNPS");
        recommendations.add("Valider la déclaration fiscale annuelle");
    }

    private void validateSenegaleseCompliance(
            Map<String, Object> taxData, List<String> warnings, 
            List<String> errors, List<String> recommendations) {
        validateCameroonianCompliance(taxData, warnings, errors, recommendations);
    }

    private void validateIvorianCompliance(
            Map<String, Object> taxData, List<String> warnings, 
            List<String> errors, List<String> recommendations) {
        validateCameroonianCompliance(taxData, warnings, errors, recommendations);
    }

    private void validateGenericCompliance(
            Map<String, Object> taxData, List<String> warnings, 
            List<String> errors, List<String> recommendations) {
        
        // Validation générique
        recommendations.add("Vérifier la conformité fiscale locale");
        recommendations.add("Contrôler les obligations sociales");
        recommendations.add("Valider les déclarations périodiques");
    }

    // ===== MÉTHODES DE CALCUL POUR TOUS LES PAYS OHADA =====

    // Bénin
    private Map<String, Object> calculateBenineseSalaryTaxes(BigDecimal grossSalary, String employeeType) {
        Map<String, Object> calculation = new HashMap<>();
        
        // Barème fiscal béninois simplifié
        BigDecimal taxableIncome = grossSalary.multiply(new BigDecimal("0.8")); // Abattement 20%
        
        BigDecimal taxAmount = BigDecimal.ZERO;
        if (taxableIncome.compareTo(new BigDecimal("300000")) <= 0) {
            taxAmount = taxableIncome.multiply(new BigDecimal("0.10"));
        } else if (taxableIncome.compareTo(new BigDecimal("1000000")) <= 0) {
            taxAmount = new BigDecimal("30000")
                    .add(taxableIncome.subtract(new BigDecimal("300000"))
                            .multiply(new BigDecimal("0.15")));
        } else {
            taxAmount = new BigDecimal("135000")
                    .add(taxableIncome.subtract(new BigDecimal("1000000"))
                            .multiply(new BigDecimal("0.25")));
        }
        
        calculation.put("taxableIncome", taxableIncome);
        calculation.put("taxAmount", taxAmount);
        calculation.put("netSalary", grossSalary.subtract(taxAmount));
        calculation.put("taxRate", taxAmount.divide(grossSalary, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100")));
        
        return calculation;
    }

    // Burkina Faso
    private Map<String, Object> calculateBurkinabeSalaryTaxes(BigDecimal grossSalary, String employeeType) {
        Map<String, Object> calculation = new HashMap<>();
        
        BigDecimal taxableIncome = grossSalary.multiply(new BigDecimal("0.8"));
        
        BigDecimal taxAmount = BigDecimal.ZERO;
        if (taxableIncome.compareTo(new BigDecimal("400000")) <= 0) {
            taxAmount = taxableIncome.multiply(new BigDecimal("0.10"));
        } else if (taxableIncome.compareTo(new BigDecimal("1200000")) <= 0) {
            taxAmount = new BigDecimal("40000")
                    .add(taxableIncome.subtract(new BigDecimal("400000"))
                            .multiply(new BigDecimal("0.15")));
        } else {
            taxAmount = new BigDecimal("160000")
                    .add(taxableIncome.subtract(new BigDecimal("1200000"))
                            .multiply(new BigDecimal("0.25")));
        }
        
        calculation.put("taxableIncome", taxableIncome);
        calculation.put("taxAmount", taxAmount);
        calculation.put("netSalary", grossSalary.subtract(taxAmount));
        calculation.put("taxRate", taxAmount.divide(grossSalary, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100")));
        
        return calculation;
    }

    // République Centrafricaine
    private Map<String, Object> calculateCentralAfricanSalaryTaxes(BigDecimal grossSalary, String employeeType) {
        Map<String, Object> calculation = new HashMap<>();
        
        BigDecimal taxableIncome = grossSalary.multiply(new BigDecimal("0.8"));
        
        BigDecimal taxAmount = BigDecimal.ZERO;
        if (taxableIncome.compareTo(new BigDecimal("500000")) <= 0) {
            taxAmount = taxableIncome.multiply(new BigDecimal("0.10"));
        } else if (taxableIncome.compareTo(new BigDecimal("1500000")) <= 0) {
            taxAmount = new BigDecimal("50000")
                    .add(taxableIncome.subtract(new BigDecimal("500000"))
                            .multiply(new BigDecimal("0.15")));
        } else {
            taxAmount = new BigDecimal("200000")
                    .add(taxableIncome.subtract(new BigDecimal("1500000"))
                            .multiply(new BigDecimal("0.25")));
        }
        
        calculation.put("taxableIncome", taxableIncome);
        calculation.put("taxAmount", taxAmount);
        calculation.put("netSalary", grossSalary.subtract(taxAmount));
        calculation.put("taxRate", taxAmount.divide(grossSalary, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100")));
        
        return calculation;
    }

    // Comores
    private Map<String, Object> calculateComorianSalaryTaxes(BigDecimal grossSalary, String employeeType) {
        Map<String, Object> calculation = new HashMap<>();
        
        BigDecimal taxableIncome = grossSalary.multiply(new BigDecimal("0.8"));
        
        BigDecimal taxAmount = BigDecimal.ZERO;
        if (taxableIncome.compareTo(new BigDecimal("200000")) <= 0) {
            taxAmount = taxableIncome.multiply(new BigDecimal("0.10"));
        } else if (taxableIncome.compareTo(new BigDecimal("800000")) <= 0) {
            taxAmount = new BigDecimal("20000")
                    .add(taxableIncome.subtract(new BigDecimal("200000"))
                            .multiply(new BigDecimal("0.15")));
        } else {
            taxAmount = new BigDecimal("110000")
                    .add(taxableIncome.subtract(new BigDecimal("800000"))
                            .multiply(new BigDecimal("0.25")));
        }
        
        calculation.put("taxableIncome", taxableIncome);
        calculation.put("taxAmount", taxAmount);
        calculation.put("netSalary", grossSalary.subtract(taxAmount));
        calculation.put("taxRate", taxAmount.divide(grossSalary, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100")));
        
        return calculation;
    }

    // Congo
    private Map<String, Object> calculateCongoleseSalaryTaxes(BigDecimal grossSalary, String employeeType) {
        Map<String, Object> calculation = new HashMap<>();
        
        BigDecimal taxableIncome = grossSalary.multiply(new BigDecimal("0.8"));
        
        BigDecimal taxAmount = BigDecimal.ZERO;
        if (taxableIncome.compareTo(new BigDecimal("600000")) <= 0) {
            taxAmount = taxableIncome.multiply(new BigDecimal("0.10"));
        } else if (taxableIncome.compareTo(new BigDecimal("1800000")) <= 0) {
            taxAmount = new BigDecimal("60000")
                    .add(taxableIncome.subtract(new BigDecimal("600000"))
                            .multiply(new BigDecimal("0.15")));
        } else {
            taxAmount = new BigDecimal("240000")
                    .add(taxableIncome.subtract(new BigDecimal("1800000"))
                            .multiply(new BigDecimal("0.25")));
        }
        
        calculation.put("taxableIncome", taxableIncome);
        calculation.put("taxAmount", taxAmount);
        calculation.put("netSalary", grossSalary.subtract(taxAmount));
        calculation.put("taxRate", taxAmount.divide(grossSalary, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100")));
        
        return calculation;
    }

    // Gabon
    private Map<String, Object> calculateGaboneseSalaryTaxes(BigDecimal grossSalary, String employeeType) {
        Map<String, Object> calculation = new HashMap<>();
        
        BigDecimal taxableIncome = grossSalary.multiply(new BigDecimal("0.8"));
        
        BigDecimal taxAmount = BigDecimal.ZERO;
        if (taxableIncome.compareTo(new BigDecimal("800000")) <= 0) {
            taxAmount = taxableIncome.multiply(new BigDecimal("0.10"));
        } else if (taxableIncome.compareTo(new BigDecimal("2400000")) <= 0) {
            taxAmount = new BigDecimal("80000")
                    .add(taxableIncome.subtract(new BigDecimal("800000"))
                            .multiply(new BigDecimal("0.15")));
        } else {
            taxAmount = new BigDecimal("320000")
                    .add(taxableIncome.subtract(new BigDecimal("2400000"))
                            .multiply(new BigDecimal("0.25")));
        }
        
        calculation.put("taxableIncome", taxableIncome);
        calculation.put("taxAmount", taxAmount);
        calculation.put("netSalary", grossSalary.subtract(taxAmount));
        calculation.put("taxRate", taxAmount.divide(grossSalary, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100")));
        
        return calculation;
    }

    // Guinée
    private Map<String, Object> calculateGuineanSalaryTaxes(BigDecimal grossSalary, String employeeType) {
        Map<String, Object> calculation = new HashMap<>();
        
        BigDecimal taxableIncome = grossSalary.multiply(new BigDecimal("0.8"));
        
        BigDecimal taxAmount = BigDecimal.ZERO;
        if (taxableIncome.compareTo(new BigDecimal("300000")) <= 0) {
            taxAmount = taxableIncome.multiply(new BigDecimal("0.10"));
        } else if (taxableIncome.compareTo(new BigDecimal("1000000")) <= 0) {
            taxAmount = new BigDecimal("30000")
                    .add(taxableIncome.subtract(new BigDecimal("300000"))
                            .multiply(new BigDecimal("0.15")));
        } else {
            taxAmount = new BigDecimal("135000")
                    .add(taxableIncome.subtract(new BigDecimal("1000000"))
                            .multiply(new BigDecimal("0.25")));
        }
        
        calculation.put("taxableIncome", taxableIncome);
        calculation.put("taxAmount", taxAmount);
        calculation.put("netSalary", grossSalary.subtract(taxAmount));
        calculation.put("taxRate", taxAmount.divide(grossSalary, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100")));
        
        return calculation;
    }

    // Guinée-Bissau
    private Map<String, Object> calculateGuineaBissauSalaryTaxes(BigDecimal grossSalary, String employeeType) {
        Map<String, Object> calculation = new HashMap<>();
        
        BigDecimal taxableIncome = grossSalary.multiply(new BigDecimal("0.8"));
        
        BigDecimal taxAmount = BigDecimal.ZERO;
        if (taxableIncome.compareTo(new BigDecimal("250000")) <= 0) {
            taxAmount = taxableIncome.multiply(new BigDecimal("0.10"));
        } else if (taxableIncome.compareTo(new BigDecimal("800000")) <= 0) {
            taxAmount = new BigDecimal("25000")
                    .add(taxableIncome.subtract(new BigDecimal("250000"))
                            .multiply(new BigDecimal("0.15")));
        } else {
            taxAmount = new BigDecimal("107500")
                    .add(taxableIncome.subtract(new BigDecimal("800000"))
                            .multiply(new BigDecimal("0.25")));
        }
        
        calculation.put("taxableIncome", taxableIncome);
        calculation.put("taxAmount", taxAmount);
        calculation.put("netSalary", grossSalary.subtract(taxAmount));
        calculation.put("taxRate", taxAmount.divide(grossSalary, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100")));
        
        return calculation;
    }

    // Guinée Équatoriale
    private Map<String, Object> calculateEquatorialGuineanSalaryTaxes(BigDecimal grossSalary, String employeeType) {
        Map<String, Object> calculation = new HashMap<>();
        
        BigDecimal taxableIncome = grossSalary.multiply(new BigDecimal("0.8"));
        
        BigDecimal taxAmount = BigDecimal.ZERO;
        if (taxableIncome.compareTo(new BigDecimal("1000000")) <= 0) {
            taxAmount = taxableIncome.multiply(new BigDecimal("0.10"));
        } else if (taxableIncome.compareTo(new BigDecimal("3000000")) <= 0) {
            taxAmount = new BigDecimal("100000")
                    .add(taxableIncome.subtract(new BigDecimal("1000000"))
                            .multiply(new BigDecimal("0.15")));
        } else {
            taxAmount = new BigDecimal("400000")
                    .add(taxableIncome.subtract(new BigDecimal("3000000"))
                            .multiply(new BigDecimal("0.25")));
        }
        
        calculation.put("taxableIncome", taxableIncome);
        calculation.put("taxAmount", taxAmount);
        calculation.put("netSalary", grossSalary.subtract(taxAmount));
        calculation.put("taxRate", taxAmount.divide(grossSalary, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100")));
        
        return calculation;
    }

    // Mali
    private Map<String, Object> calculateMalianSalaryTaxes(BigDecimal grossSalary, String employeeType) {
        Map<String, Object> calculation = new HashMap<>();
        
        BigDecimal taxableIncome = grossSalary.multiply(new BigDecimal("0.8"));
        
        BigDecimal taxAmount = BigDecimal.ZERO;
        if (taxableIncome.compareTo(new BigDecimal("400000")) <= 0) {
            taxAmount = taxableIncome.multiply(new BigDecimal("0.10"));
        } else if (taxableIncome.compareTo(new BigDecimal("1200000")) <= 0) {
            taxAmount = new BigDecimal("40000")
                    .add(taxableIncome.subtract(new BigDecimal("400000"))
                            .multiply(new BigDecimal("0.15")));
        } else {
            taxAmount = new BigDecimal("160000")
                    .add(taxableIncome.subtract(new BigDecimal("1200000"))
                            .multiply(new BigDecimal("0.25")));
        }
        
        calculation.put("taxableIncome", taxableIncome);
        calculation.put("taxAmount", taxAmount);
        calculation.put("netSalary", grossSalary.subtract(taxAmount));
        calculation.put("taxRate", taxAmount.divide(grossSalary, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100")));
        
        return calculation;
    }

    // Niger
    private Map<String, Object> calculateNigerienSalaryTaxes(BigDecimal grossSalary, String employeeType) {
        Map<String, Object> calculation = new HashMap<>();
        
        BigDecimal taxableIncome = grossSalary.multiply(new BigDecimal("0.8"));
        
        BigDecimal taxAmount = BigDecimal.ZERO;
        if (taxableIncome.compareTo(new BigDecimal("300000")) <= 0) {
            taxAmount = taxableIncome.multiply(new BigDecimal("0.10"));
        } else if (taxableIncome.compareTo(new BigDecimal("1000000")) <= 0) {
            taxAmount = new BigDecimal("30000")
                    .add(taxableIncome.subtract(new BigDecimal("300000"))
                            .multiply(new BigDecimal("0.15")));
        } else {
            taxAmount = new BigDecimal("135000")
                    .add(taxableIncome.subtract(new BigDecimal("1000000"))
                            .multiply(new BigDecimal("0.25")));
        }
        
        calculation.put("taxableIncome", taxableIncome);
        calculation.put("taxAmount", taxAmount);
        calculation.put("netSalary", grossSalary.subtract(taxAmount));
        calculation.put("taxRate", taxAmount.divide(grossSalary, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100")));
        
        return calculation;
    }

    // RD Congo
    private Map<String, Object> calculateDRCSalaryTaxes(BigDecimal grossSalary, String employeeType) {
        Map<String, Object> calculation = new HashMap<>();
        
        BigDecimal taxableIncome = grossSalary.multiply(new BigDecimal("0.8"));
        
        BigDecimal taxAmount = BigDecimal.ZERO;
        if (taxableIncome.compareTo(new BigDecimal("400000")) <= 0) {
            taxAmount = taxableIncome.multiply(new BigDecimal("0.10"));
        } else if (taxableIncome.compareTo(new BigDecimal("1200000")) <= 0) {
            taxAmount = new BigDecimal("40000")
                    .add(taxableIncome.subtract(new BigDecimal("400000"))
                            .multiply(new BigDecimal("0.15")));
        } else {
            taxAmount = new BigDecimal("160000")
                    .add(taxableIncome.subtract(new BigDecimal("1200000"))
                            .multiply(new BigDecimal("0.25")));
        }
        
        calculation.put("taxableIncome", taxableIncome);
        calculation.put("taxAmount", taxAmount);
        calculation.put("netSalary", grossSalary.subtract(taxAmount));
        calculation.put("taxRate", taxAmount.divide(grossSalary, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100")));
        
        return calculation;
    }

    // Tchad
    private Map<String, Object> calculateChadianSalaryTaxes(BigDecimal grossSalary, String employeeType) {
        Map<String, Object> calculation = new HashMap<>();
        
        BigDecimal taxableIncome = grossSalary.multiply(new BigDecimal("0.8"));
        
        BigDecimal taxAmount = BigDecimal.ZERO;
        if (taxableIncome.compareTo(new BigDecimal("300000")) <= 0) {
            taxAmount = taxableIncome.multiply(new BigDecimal("0.10"));
        } else if (taxableIncome.compareTo(new BigDecimal("1000000")) <= 0) {
            taxAmount = new BigDecimal("30000")
                    .add(taxableIncome.subtract(new BigDecimal("300000"))
                            .multiply(new BigDecimal("0.15")));
        } else {
            taxAmount = new BigDecimal("135000")
                    .add(taxableIncome.subtract(new BigDecimal("1000000"))
                            .multiply(new BigDecimal("0.25")));
        }
        
        calculation.put("taxableIncome", taxableIncome);
        calculation.put("taxAmount", taxAmount);
        calculation.put("netSalary", grossSalary.subtract(taxAmount));
        calculation.put("taxRate", taxAmount.divide(grossSalary, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100")));
        
        return calculation;
    }

    // Togo
    private Map<String, Object> calculateTogoleseSalaryTaxes(BigDecimal grossSalary, String employeeType) {
        Map<String, Object> calculation = new HashMap<>();
        
        BigDecimal taxableIncome = grossSalary.multiply(new BigDecimal("0.8"));
        
        BigDecimal taxAmount = BigDecimal.ZERO;
        if (taxableIncome.compareTo(new BigDecimal("300000")) <= 0) {
            taxAmount = taxableIncome.multiply(new BigDecimal("0.10"));
        } else if (taxableIncome.compareTo(new BigDecimal("1000000")) <= 0) {
            taxAmount = new BigDecimal("30000")
                    .add(taxableIncome.subtract(new BigDecimal("300000"))
                            .multiply(new BigDecimal("0.15")));
        } else {
            taxAmount = new BigDecimal("135000")
                    .add(taxableIncome.subtract(new BigDecimal("1000000"))
                            .multiply(new BigDecimal("0.25")));
        }
        
        calculation.put("taxableIncome", taxableIncome);
        calculation.put("taxAmount", taxAmount);
        calculation.put("netSalary", grossSalary.subtract(taxAmount));
        calculation.put("taxRate", taxAmount.divide(grossSalary, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100")));
        
        return calculation;
    }

    // ===== MÉTHODES DE CALCUL POUR LES AUTRES PAYS INTERNATIONAUX =====

    // États-Unis
    private Map<String, Object> calculateUSSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        Map<String, Object> calculation = new HashMap<>();
        
        // Barème fiscal US simplifié 2024
        BigDecimal taxableIncome = grossSalary;
        
        BigDecimal taxAmount = BigDecimal.ZERO;
        if (taxableIncome.compareTo(new BigDecimal("11600")) <= 0) {
            taxAmount = taxableIncome.multiply(new BigDecimal("0.10"));
        } else if (taxableIncome.compareTo(new BigDecimal("47150")) <= 0) {
            taxAmount = new BigDecimal("1160")
                    .add(taxableIncome.subtract(new BigDecimal("11600"))
                            .multiply(new BigDecimal("0.12")));
        } else if (taxableIncome.compareTo(new BigDecimal("100525")) <= 0) {
            taxAmount = new BigDecimal("5428")
                    .add(taxableIncome.subtract(new BigDecimal("47150"))
                            .multiply(new BigDecimal("0.22")));
        } else if (taxableIncome.compareTo(new BigDecimal("191950")) <= 0) {
            taxAmount = new BigDecimal("17196")
                    .add(taxableIncome.subtract(new BigDecimal("100525"))
                            .multiply(new BigDecimal("0.24")));
        } else if (taxableIncome.compareTo(new BigDecimal("243725")) <= 0) {
            taxAmount = new BigDecimal("39148")
                    .add(taxableIncome.subtract(new BigDecimal("191950"))
                            .multiply(new BigDecimal("0.32")));
        } else if (taxableIncome.compareTo(new BigDecimal("609350")) <= 0) {
            taxAmount = new BigDecimal("56044")
                    .add(taxableIncome.subtract(new BigDecimal("243725"))
                            .multiply(new BigDecimal("0.35")));
        } else {
            taxAmount = new BigDecimal("183647")
                    .add(taxableIncome.subtract(new BigDecimal("609350"))
                            .multiply(new BigDecimal("0.37")));
        }
        
        // Child Tax Credit
        if (hasChildren && childrenCount > 0) {
            BigDecimal childCredit = new BigDecimal(childrenCount).multiply(new BigDecimal("2000"));
            taxAmount = taxAmount.subtract(childCredit).max(BigDecimal.ZERO);
        }
        
        calculation.put("taxableIncome", taxableIncome);
        calculation.put("taxAmount", taxAmount);
        calculation.put("netSalary", grossSalary.subtract(taxAmount));
        calculation.put("taxRate", taxAmount.divide(grossSalary, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100")));
        
        return calculation;
    }

    // Canada
    private Map<String, Object> calculateCanadianSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        Map<String, Object> calculation = new HashMap<>();
        
        // Barème fiscal canadien simplifié 2024
        BigDecimal taxableIncome = grossSalary;
        
        BigDecimal taxAmount = BigDecimal.ZERO;
        if (taxableIncome.compareTo(new BigDecimal("15000")) <= 0) {
            taxAmount = taxableIncome.multiply(new BigDecimal("0.15"));
        } else if (taxableIncome.compareTo(new BigDecimal("53359")) <= 0) {
            taxAmount = new BigDecimal("2250")
                    .add(taxableIncome.subtract(new BigDecimal("15000"))
                            .multiply(new BigDecimal("0.205")));
        } else if (taxableIncome.compareTo(new BigDecimal("106717")) <= 0) {
            taxAmount = new BigDecimal("10025")
                    .add(taxableIncome.subtract(new BigDecimal("53359"))
                            .multiply(new BigDecimal("0.26")));
        } else if (taxableIncome.compareTo(new BigDecimal("165430")) <= 0) {
            taxAmount = new BigDecimal("23892")
                    .add(taxableIncome.subtract(new BigDecimal("106717"))
                            .multiply(new BigDecimal("0.29")));
        } else {
            taxAmount = new BigDecimal("40947")
                    .add(taxableIncome.subtract(new BigDecimal("165430"))
                            .multiply(new BigDecimal("0.33")));
        }
        
        // Canada Child Benefit
        if (hasChildren && childrenCount > 0) {
            BigDecimal childBenefit = new BigDecimal(childrenCount).multiply(new BigDecimal("6000"));
            taxAmount = taxAmount.subtract(childBenefit).max(BigDecimal.ZERO);
        }
        
        calculation.put("taxableIncome", taxableIncome);
        calculation.put("taxAmount", taxAmount);
        calculation.put("netSalary", grossSalary.subtract(taxAmount));
        calculation.put("taxRate", taxAmount.divide(grossSalary, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100")));
        
        return calculation;
    }

    // Allemagne
    private Map<String, Object> calculateGermanSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        Map<String, Object> calculation = new HashMap<>();
        
        // Barème fiscal allemand simplifié 2024
        BigDecimal taxableIncome = grossSalary.multiply(new BigDecimal("0.9"));
        
        BigDecimal taxAmount = BigDecimal.ZERO;
        if (taxableIncome.compareTo(new BigDecimal("10908")) <= 0) {
            taxAmount = BigDecimal.ZERO;
        } else if (taxableIncome.compareTo(new BigDecimal("62809")) <= 0) {
            taxAmount = taxableIncome.subtract(new BigDecimal("10908"))
                    .multiply(new BigDecimal("0.14"));
        } else if (taxableIncome.compareTo(new BigDecimal("277825")) <= 0) {
            taxAmount = new BigDecimal("7264")
                    .add(taxableIncome.subtract(new BigDecimal("62809"))
                            .multiply(new BigDecimal("0.42")));
        } else {
            taxAmount = new BigDecimal("97632")
                    .add(taxableIncome.subtract(new BigDecimal("277825"))
                            .multiply(new BigDecimal("0.45")));
        }
        
        // Kindergeld pour enfants
        if (hasChildren && childrenCount > 0) {
            BigDecimal kindergeld = new BigDecimal(childrenCount).multiply(new BigDecimal("250"));
            taxAmount = taxAmount.subtract(kindergeld).max(BigDecimal.ZERO);
        }
        
        calculation.put("taxableIncome", taxableIncome);
        calculation.put("taxAmount", taxAmount);
        calculation.put("netSalary", grossSalary.subtract(taxAmount));
        calculation.put("taxRate", taxAmount.divide(grossSalary, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100")));
        
        return calculation;
    }

    // Méthodes pour les autres pays européens (simplifiées)
    private Map<String, Object> calculateBelgianSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        return calculateGermanSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
    }

    private Map<String, Object> calculateSwissSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        return calculateGermanSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
    }

    private Map<String, Object> calculateLuxembourgSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        return calculateGermanSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
    }

    // Méthodes pour les pays africains non-OHADA
    private Map<String, Object> calculateMoroccanSalaryTaxes(BigDecimal grossSalary, String employeeType) {
        return calculateCameroonianSalaryTaxes(grossSalary, employeeType);
    }

    private Map<String, Object> calculateTunisianSalaryTaxes(BigDecimal grossSalary, String employeeType) {
        return calculateCameroonianSalaryTaxes(grossSalary, employeeType);
    }

    private Map<String, Object> calculateAlgerianSalaryTaxes(BigDecimal grossSalary, String employeeType) {
        return calculateCameroonianSalaryTaxes(grossSalary, employeeType);
    }

    private Map<String, Object> calculateEgyptianSalaryTaxes(BigDecimal grossSalary, String employeeType) {
        return calculateCameroonianSalaryTaxes(grossSalary, employeeType);
    }

    private Map<String, Object> calculateNigerianSalaryTaxes(BigDecimal grossSalary, String employeeType) {
        return calculateCameroonianSalaryTaxes(grossSalary, employeeType);
    }

    private Map<String, Object> calculateGhanaianSalaryTaxes(BigDecimal grossSalary, String employeeType) {
        return calculateCameroonianSalaryTaxes(grossSalary, employeeType);
    }

    private Map<String, Object> calculateKenyanSalaryTaxes(BigDecimal grossSalary, String employeeType) {
        return calculateCameroonianSalaryTaxes(grossSalary, employeeType);
    }

    private Map<String, Object> calculateSouthAfricanSalaryTaxes(BigDecimal grossSalary, String employeeType) {
        return calculateCameroonianSalaryTaxes(grossSalary, employeeType);
    }

    // Méthodes pour les autres pays européens (simplifiées)
    private Map<String, Object> calculateUKSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        return calculateGermanSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
    }

    private Map<String, Object> calculateSpanishSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        return calculateGermanSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
    }

    private Map<String, Object> calculateItalianSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        return calculateGermanSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
    }

    private Map<String, Object> calculatePortugueseSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        return calculateGermanSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
    }

    private Map<String, Object> calculateDutchSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        return calculateGermanSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
    }

    private Map<String, Object> calculateDanishSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        return calculateGermanSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
    }

    private Map<String, Object> calculateSwedishSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        return calculateGermanSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
    }

    private Map<String, Object> calculateNorwegianSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        return calculateGermanSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
    }

    private Map<String, Object> calculateFinnishSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        return calculateGermanSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
    }

    private Map<String, Object> calculateAustrianSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        return calculateGermanSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
    }

    private Map<String, Object> calculatePolishSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        return calculateGermanSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
    }

    private Map<String, Object> calculateCzechSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        return calculateGermanSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
    }

    private Map<String, Object> calculateHungarianSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        return calculateGermanSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
    }

    private Map<String, Object> calculateRomanianSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        return calculateGermanSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
    }

    private Map<String, Object> calculateBulgarianSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        return calculateGermanSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
    }

    private Map<String, Object> calculateGreekSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        return calculateGermanSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
    }

    private Map<String, Object> calculateCypriotSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        return calculateGermanSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
    }

    private Map<String, Object> calculateMalteseSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        return calculateGermanSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
    }

    private Map<String, Object> calculateSlovenianSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        return calculateGermanSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
    }

    private Map<String, Object> calculateSlovakSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        return calculateGermanSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
    }

    private Map<String, Object> calculateCroatianSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        return calculateGermanSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
    }

    private Map<String, Object> calculateLithuanianSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        return calculateGermanSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
    }

    private Map<String, Object> calculateLatvianSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        return calculateGermanSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
    }

    private Map<String, Object> calculateEstonianSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        return calculateGermanSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
    }

    private Map<String, Object> calculateIrishSalaryTaxes(BigDecimal grossSalary, String employeeType, boolean hasChildren, int childrenCount) {
        return calculateGermanSalaryTaxes(grossSalary, employeeType, hasChildren, childrenCount);
    }
}
