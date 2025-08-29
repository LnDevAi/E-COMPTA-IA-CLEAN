package com.ecomptaia.repository;

import com.ecomptaia.entity.Account;
import com.ecomptaia.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    
    // Recherche par numéro de compte
    Optional<Account> findByAccountNumber(String accountNumber);
    
    // Recherche par entreprise
    List<Account> findByCompanyOrderByAccountNumberAsc(Company company);
    
    // Recherche par entreprise et type
    List<Account> findByCompanyAndTypeOrderByAccountNumberAsc(Company company, Account.AccountType type);
    
    // Recherche par entreprise et classe de compte
    List<Account> findByCompanyAndAccountClassOrderByAccountNumberAsc(Company company, Account.AccountClass accountClass);
    
    // Recherche des comptes actifs par entreprise
    List<Account> findByCompanyAndIsActiveTrueOrderByAccountNumberAsc(Company company);
    
    // Recherche par nom ou description
    List<Account> findByCompanyAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByAccountNumberAsc(
        Company company, String name, String description);
    
    // Recherche par numéro de compte (pattern)
    List<Account> findByCompanyAndAccountNumberStartingWithOrderByAccountNumberAsc(Company company, String pattern);
    
    // Recherche par devise
    List<Account> findByCompanyAndCurrencyOrderByAccountNumberAsc(Company company, String currency);
    
    // Compter les comptes par entreprise
    long countByCompany(Company company);
    
    // Compter les comptes actifs par entreprise
    long countByCompanyAndIsActiveTrue(Company company);
    
    // Compter les comptes par type et entreprise
    long countByCompanyAndType(Company company, Account.AccountType type);
    
    // Compter les comptes par classe et entreprise
    long countByCompanyAndAccountClass(Company company, Account.AccountClass accountClass);
    
    // Recherche avancée avec critères multiples
    @Query("SELECT a FROM Account a WHERE a.company = :company " +
           "AND (:type IS NULL OR a.type = :type) " +
           "AND (:accountClass IS NULL OR a.accountClass = :accountClass) " +
           "AND (:isActive IS NULL OR a.isActive = :isActive) " +
           "AND (:currency IS NULL OR a.currency = :currency) " +
           "ORDER BY a.accountNumber ASC")
    List<Account> findAccountsWithCriteria(
        @Param("company") Company company,
        @Param("type") Account.AccountType type,
        @Param("accountClass") Account.AccountClass accountClass,
        @Param("isActive") Boolean isActive,
        @Param("currency") String currency
    );
    
    // Recherche par texte (nom, description, numéro)
    @Query("SELECT a FROM Account a WHERE a.company = :company " +
           "AND (LOWER(a.name) LIKE LOWER(CONCAT('%', :texte, '%')) " +
           "OR LOWER(a.description) LIKE LOWER(CONCAT('%', :texte, '%')) " +
           "OR LOWER(a.accountNumber) LIKE LOWER(CONCAT('%', :texte, '%'))) " +
           "ORDER BY a.accountNumber ASC")
    List<Account> findByTexte(@Param("company") Company company, @Param("texte") String texte);
    
    // Vérifier si un numéro de compte existe
    boolean existsByAccountNumberAndCompany(String accountNumber, Company company);
    
    // Trouver le prochain numéro de compte disponible
    @Query("SELECT MAX(CAST(SUBSTRING(a.accountNumber, :startPos) AS integer)) FROM Account a " +
           "WHERE a.company = :company AND a.accountNumber LIKE :pattern")
    Integer findMaxAccountNumberByPattern(@Param("company") Company company, 
                                        @Param("pattern") String pattern, 
                                        @Param("startPos") int startPos);
}




