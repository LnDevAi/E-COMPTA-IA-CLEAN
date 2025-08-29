package com.ecomptaia.service;

import com.ecomptaia.repository.ThirdPartyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service de numérotation des tiers selon les standards comptables
 */
@Service
public class ThirdPartyNumberingService {

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    /**
     * Générer un numéro de compte tiers automatiquement
     * Selon SYSCOHADA/OHADA :
     * - 401 : Fournisseurs
     * - 411 : Clients
     * - 421 : Personnel
     * - 431 : Sécurité sociale
     * - 441 : État et autres collectivités
     * - 451 : Groupe et associés
     * - 461 : Dépôts et cautionnements reçus
     * - 471 : Autres créanciers
     */
    public String generateThirdPartyAccountNumber(Long companyId, String type) {
        String baseAccount = getBaseAccountForType(type);
        String nextNumber = getNextAvailableNumber(companyId, baseAccount);
        return baseAccount + nextNumber;
    }

    /**
     * Valider un numéro de compte tiers
     */
    public boolean validateThirdPartyAccountNumber(String accountNumber, String type) {
        // Vérifier que le numéro commence par le bon préfixe
        String expectedPrefix = getBaseAccountForType(type);
        if (!accountNumber.startsWith(expectedPrefix)) {
            return false;
        }

        // Vérifier la longueur (7 chiffres au total pour SYSCOHADA)
        if (accountNumber.length() != 7) {
            return false;
        }

        // Vérifier que c'est un nombre
        try {
            Integer.parseInt(accountNumber);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    /**
     * Vérifier si un numéro de compte est disponible
     */
    public boolean isAccountNumberAvailable(Long companyId, String accountNumber) {
        Optional<com.ecomptaia.entity.ThirdParty> existing = 
            thirdPartyRepository.findByAccountNumberAndCompanyId(companyId, accountNumber);
        return existing.isEmpty();
    }

    /**
     * Obtenir le prochain numéro disponible pour un type de tiers
     */
    private String getNextAvailableNumber(Long companyId, String baseAccount) {
        // Récupérer le dernier numéro utilisé pour ce type
        String lastAccountNumber = thirdPartyRepository.findLastAccountNumberByBaseAccount(companyId, baseAccount);
        
        if (lastAccountNumber == null) {
            // Premier tiers de ce type
            return "0001";
        }

        // Extraire le numéro séquentiel (4 derniers chiffres)
        String sequenceNumber = lastAccountNumber.substring(3);
        int nextNumber = Integer.parseInt(sequenceNumber) + 1;
        
        // Formater avec des zéros à gauche
        return String.format("%04d", nextNumber);
    }

    /**
     * Obtenir le compte de base selon le type de tiers
     */
    private String getBaseAccountForType(String type) {
        switch (type.toUpperCase()) {
            case "FOURNISSEUR":
                return "401";
            case "CLIENT":
                return "411";
            case "PERSONNEL":
                return "421";
            case "SECURITE_SOCIALE":
                return "431";
            case "ETAT":
                return "441";
            case "GROUPE":
                return "451";
            case "DEPOT":
                return "461";
            case "AUTRE":
                return "471";
            default:
                throw new IllegalArgumentException("Type de tiers non reconnu: " + type);
        }
    }

    /**
     * Obtenir le nom du compte selon le numéro
     */
    public String getAccountNameByNumber(String accountNumber) {
        String baseAccount = accountNumber.substring(0, 3);
        switch (baseAccount) {
            case "401":
                return "Fournisseurs";
            case "411":
                return "Clients";
            case "421":
                return "Personnel";
            case "431":
                return "Sécurité sociale";
            case "441":
                return "État et autres collectivités";
            case "451":
                return "Groupe et associés";
            case "461":
                return "Dépôts et cautionnements reçus";
            case "471":
                return "Autres créanciers";
            default:
                return "Tiers divers";
        }
    }

    /**
     * Obtenir le type de tiers selon le numéro de compte
     */
    public String getThirdPartyTypeByAccountNumber(String accountNumber) {
        String baseAccount = accountNumber.substring(0, 3);
        switch (baseAccount) {
            case "401":
                return "FOURNISSEUR";
            case "411":
                return "CLIENT";
            case "421":
                return "PERSONNEL";
            case "431":
                return "SECURITE_SOCIALE";
            case "441":
                return "ETAT";
            case "451":
                return "GROUPE";
            case "461":
                return "DEPOT";
            case "471":
                return "AUTRE";
            default:
                return "AUTRE";
        }
    }

    /**
     * Valider la cohérence type/numéro de compte
     */
    public boolean validateTypeAccountNumberConsistency(String type, String accountNumber) {
        String expectedBaseAccount = getBaseAccountForType(type);
        String actualBaseAccount = accountNumber.substring(0, 3);
        return expectedBaseAccount.equals(actualBaseAccount);
    }
}
