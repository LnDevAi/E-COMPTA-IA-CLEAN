package com.ecomptaia.security;

import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

@Service("securityDataValidationService")
public class DataValidationService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[0-9\\s\\-()]{8,20}$");

    public boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public String sanitizeInput(String input) {
        if (input == null) return null;
        return input.replaceAll("[<>\"'&]", "")
                   .replaceAll("javascript:", "")
                   .replaceAll("on\\w+\\s*=", "")
                   .trim();
    }

    public boolean containsSqlInjection(String input) {
        if (input == null) return false;
        String lowerInput = input.toLowerCase();
        String[] sqlKeywords = {
            "select", "insert", "update", "delete", "drop", "create", "alter",
            "union", "exec", "execute", "script", "javascript", "vbscript"
        };
        for (String keyword : sqlKeywords) {
            if (lowerInput.contains(keyword)) return true;
        }
        return false;
    }

    public boolean containsXSS(String input) {
        if (input == null) return false;
        String lowerInput = input.toLowerCase();
        String[] xssPatterns = {
            "<script", "javascript:", "vbscript:", "onload", "onerror",
            "onclick", "onmouseover", "expression(", "url("
        };
        for (String pattern : xssPatterns) {
            if (lowerInput.contains(pattern)) return true;
        }
        return false;
    }

    public String validateAndSanitize(String input, String fieldName) {
        if (input == null) return null;
        if (containsSqlInjection(input)) {
            throw new SecurityException("Contenu suspect détecté dans le champ: " + fieldName);
        }
        if (containsXSS(input)) {
            throw new SecurityException("Contenu XSS détecté dans le champ: " + fieldName);
        }
        return sanitizeInput(input);
    }
}
