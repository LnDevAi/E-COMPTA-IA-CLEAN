package com.ecomptaia.security;

import com.ecomptaia.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service d'implémentation de UserDetailsService pour Spring Security
 * Adapté à l'architecture Map existante
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserManagementService userManagementService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Utiliser le service existant pour récupérer les utilisateurs
        List<Map<String, Object>> users = userManagementService.getCompanyUsers(1L); // Company ID par défaut
        
        Map<String, Object> user = users.stream()
                .filter(u -> username.equals(u.get("username")) || username.equals(u.get("email")))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + username));

        return UserPrincipal.createFromMap(user);
    }

    public UserDetails loadUserById(Long id) {
        List<Map<String, Object>> users = userManagementService.getCompanyUsers(1L);
        
        Map<String, Object> user = users.stream()
                .filter(u -> id.equals(u.get("userId")))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'ID: " + id));

        return UserPrincipal.createFromMap(user);
    }
}
