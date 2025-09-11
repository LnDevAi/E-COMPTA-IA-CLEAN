ackage com.ecomptaia.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Encodeur de mots de passe sécurisé pour E-COMPTA-IA
 * Utilise BCrypt avec un facteur de coût élevé pour la sécurité
 */
@Component
public class EcomptaiaPasswordEncoder implements PasswordEncoder {
    
    private final BCryptPasswordEncoder encoder;
    
    public EcomptaiaPasswordEncoder() {
        // Facteur de coût 12 pour une sécurité maximale
        this.encoder = new BCryptPasswordEncoder(12);
    }
    
    @Override
    public String encode(CharSequence rawPassword) {
        return encoder.encode(rawPassword);
    }
    
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
    
    /**
     * Vérifie la force du mot de passe
     */
    public boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpperCase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLowerCase = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecialChar = password.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(ch) >= 0);
        
        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }
}
