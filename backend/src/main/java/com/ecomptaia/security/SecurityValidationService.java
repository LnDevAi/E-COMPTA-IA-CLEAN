package com.ecomptaia.security;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * Service de validation sécurisée pour E-COMPTA-IA INTERNATIONAL
 * Fournit des méthodes de validation pour sécuriser les entrées utilisateur
 */
@Service
public class SecurityValidationService {

    // Patterns de validation sécurisés
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._-]{3,50}$"
    );
    
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );
    
    private static final Pattern COMPANY_NAME_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9\\s._-]{2,100}$"
    );
    
    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile(
        "^[0-9]{1,20}$"
    );
    
    private static final Pattern AMOUNT_PATTERN = Pattern.compile(
        "^[0-9]+(\\.[0-9]{1,2})?$"
    );

    /**
     * Valide une adresse email
     */
    public boolean isValidEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Valide un nom d'utilisateur
     */
    public boolean isValidUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username.trim()).matches();
    }

    /**
     * Valide un mot de passe selon les critères de sécurité
     */
    public boolean isValidPassword(String password) {
        if (!StringUtils.hasText(password)) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Valide un nom de société
     */
    public boolean isValidCompanyName(String companyName) {
        if (!StringUtils.hasText(companyName)) {
            return false;
        }
        return COMPANY_NAME_PATTERN.matcher(companyName.trim()).matches();
    }

    /**
     * Valide un numéro de compte
     */
    public boolean isValidAccountNumber(String accountNumber) {
        if (!StringUtils.hasText(accountNumber)) {
            return false;
        }
        return ACCOUNT_NUMBER_PATTERN.matcher(accountNumber.trim()).matches();
    }

    /**
     * Valide un montant
     */
    public boolean isValidAmount(String amount) {
        if (!StringUtils.hasText(amount)) {
            return false;
        }
        return AMOUNT_PATTERN.matcher(amount.trim()).matches();
    }

    /**
     * Sanitise une chaîne de caractères pour éviter les injections
     */
    public String sanitizeString(String input) {
        if (!StringUtils.hasText(input)) {
            return "";
        }
        
        return input.trim()
                .replaceAll("[<>\"'&]", "") // Supprime les caractères dangereux
                .replaceAll("\\s+", " ") // Normalise les espaces
                .substring(0, Math.min(input.length(), 1000)); // Limite la longueur
    }

    /**
     * Valide une URL
     */
    public boolean isValidUrl(String url) {
        if (!StringUtils.hasText(url)) {
            return false;
        }
        
        try {
            new java.net.URL(url);
            return url.startsWith("http://") || url.startsWith("https://");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Valide un numéro de téléphone
     */
    public boolean isValidPhoneNumber(String phoneNumber) {
        if (!StringUtils.hasText(phoneNumber)) {
            return false;
        }
        
        // Supprime tous les caractères non numériques
        String cleaned = phoneNumber.replaceAll("[^0-9]", "");
        
        // Vérifie que le numéro a entre 8 et 15 chiffres
        return cleaned.length() >= 8 && cleaned.length() <= 15;
    }

    /**
     * Valide un code pays ISO
     */
    public boolean isValidCountryCode(String countryCode) {
        if (!StringUtils.hasText(countryCode)) {
            return false;
        }
        
        return countryCode.matches("^[A-Z]{2}$");
    }

    /**
     * Valide un code devise ISO
     */
    public boolean isValidCurrencyCode(String currencyCode) {
        if (!StringUtils.hasText(currencyCode)) {
            return false;
        }
        
        return currencyCode.matches("^[A-Z]{3}$");
    }

    /**
     * Valide une date au format ISO
     */
    public boolean isValidISODate(String date) {
        if (!StringUtils.hasText(date)) {
            return false;
        }
        
        try {
            java.time.LocalDate.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Valide un UUID
     */
    public boolean isValidUUID(String uuid) {
        if (!StringUtils.hasText(uuid)) {
            return false;
        }
        
        try {
            java.util.UUID.fromString(uuid);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Valide un ID numérique
     */
    public boolean isValidNumericId(String id) {
        if (!StringUtils.hasText(id)) {
            return false;
        }
        
        try {
            Long.parseLong(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Valide une chaîne pour éviter les injections SQL
     */
    public boolean isSafeForSQL(String input) {
        if (!StringUtils.hasText(input)) {
            return true;
        }
        
        // Liste des mots-clés SQL dangereux
        String[] dangerousKeywords = {
            "SELECT", "INSERT", "UPDATE", "DELETE", "DROP", "CREATE", "ALTER",
            "EXEC", "EXECUTE", "UNION", "SCRIPT", "SCRIPT>", "<SCRIPT",
            "javascript:", "vbscript:", "onload", "onerror", "onclick"
        };
        
        String upperInput = input.toUpperCase();
        for (String keyword : dangerousKeywords) {
            if (upperInput.contains(keyword)) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Valide une chaîne pour éviter les injections XSS
     */
    public boolean isSafeForXSS(String input) {
        if (!StringUtils.hasText(input)) {
            return true;
        }
        
        // Caractères dangereux pour XSS
        String[] dangerousChars = {"<", ">", "\"", "'", "&", "javascript:", "vbscript:"};
        
        for (String dangerousChar : dangerousChars) {
            if (input.contains(dangerousChar)) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Génère un message d'erreur de validation
     */
    public String getValidationErrorMessage(String field, String value) {
        return String.format("Validation failed for field '%s' with value '%s'", field, value);
    }
}

