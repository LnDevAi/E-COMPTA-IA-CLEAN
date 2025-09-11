ackage com.ecomptaia.repository;

import com.ecomptaia.entity.BalanceComptable;
import com.ecomptaia.security.entity.Company;
import com.ecomptaia.entity.FinancialPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BalanceComptableRepository extends JpaRepository<BalanceComptable, Long> {
    
    /**
     * Trouver les balances par entreprise
     */
    List<BalanceComptable> findByCompanyOrderByDateBalanceDesc(Company company);
    
    /**
     * Trouver les balances par exercice
     */
    List<BalanceComptable> findByExerciceOrderByDateBalanceDesc(FinancialPeriod exercice);
    
    /**
     * Trouver les balances par entreprise et exercice
     */
    List<BalanceComptable> findByCompanyAndExerciceOrderByDateBalanceDesc(Company company, FinancialPeriod exercice);
    
    /**
     * Trouver les balances par standard comptable
     */
    List<BalanceComptable> findByStandardComptableOrderByDateBalanceDesc(String standardComptable);
    
    /**
     * Trouver les balances par statut
     */
    List<BalanceComptable> findByStatutOrderByDateBalanceDesc(String statut);
    
    /**
     * Trouver la derniÃ¨re balance d'une entreprise
     */
    @Query("SELECT b FROM BalanceComptable b WHERE b.company = :company ORDER BY b.dateBalance DESC")
    Optional<BalanceComptable> findLatestByCompany(@Param("company") Company company);
    
    /**
     * Trouver la derniÃ¨re balance d'un exercice
     */
    @Query("SELECT b FROM BalanceComptable b WHERE b.exercice = :exercice ORDER BY b.dateBalance DESC")
    Optional<BalanceComptable> findLatestByExercice(@Param("exercice") FinancialPeriod exercice);
    
    /**
     * Trouver les balances dans une pÃ©riode
     */
    @Query("SELECT b FROM BalanceComptable b WHERE b.company = :company AND b.dateBalance BETWEEN :dateDebut AND :dateFin ORDER BY b.dateBalance DESC")
    List<BalanceComptable> findByCompanyAndDateRange(@Param("company") Company company, 
                                                     @Param("dateDebut") LocalDate dateDebut, 
                                                     @Param("dateFin") LocalDate dateFin);
    
    /**
     * Trouver les balances Ã©quilibrÃ©es
     */
    @Query("SELECT b FROM BalanceComptable b WHERE b.equilibre = true ORDER BY b.dateBalance DESC")
    List<BalanceComptable> findEquilibrees();
    
    /**
     * Trouver les balances non Ã©quilibrÃ©es
     */
    @Query("SELECT b FROM BalanceComptable b WHERE b.equilibre = false ORDER BY b.dateBalance DESC")
    List<BalanceComptable> findNonEquilibrees();
    
    /**
     * Compter les balances par entreprise
     */
    long countByCompany(Company company);
    
    /**
     * Compter les balances par exercice
     */
    long countByExercice(FinancialPeriod exercice);
    
    /**
     * Trouver les balances par crÃ©ateur
     */
    List<BalanceComptable> findByCreatedByOrderByDateCreationDesc(Long createdBy);
    
    /**
     * Trouver les balances par validateur
     */
    List<BalanceComptable> findByValidatedByOrderByDateValidationDesc(Long validatedBy);
    
    /**
     * Trouver les balances validÃ©es
     */
    @Query("SELECT b FROM BalanceComptable b WHERE b.statut = 'VALIDATED' ORDER BY b.dateValidation DESC")
    List<BalanceComptable> findValidated();
    
    /**
     * Trouver les balances publiÃ©es
     */
    @Query("SELECT b FROM BalanceComptable b WHERE b.statut = 'PUBLISHED' ORDER BY b.dateModification DESC")
    List<BalanceComptable> findPublished();
    
    /**
     * Trouver les balances en brouillon
     */
    @Query("SELECT b FROM BalanceComptable b WHERE b.statut = 'DRAFT' ORDER BY b.dateCreation DESC")
    List<BalanceComptable> findDrafts();
    
    /**
     * VÃ©rifier l'existence d'une balance pour une date donnÃ©e
     */
    @Query("SELECT COUNT(b) > 0 FROM BalanceComptable b WHERE b.company = :company AND b.dateBalance = :dateBalance")
    boolean existsByCompanyAndDateBalance(@Param("company") Company company, @Param("dateBalance") LocalDate dateBalance);
    
    /**
     * Trouver les balances par devise
     */
    List<BalanceComptable> findByDeviseOrderByDateBalanceDesc(String devise);
    
    /**
     * Trouver les balances avec des totaux supÃ©rieurs Ã  un montant
     */
    @Query("SELECT b FROM BalanceComptable b WHERE b.totalDebit > :montant OR b.totalCredit > :montant ORDER BY b.dateBalance DESC")
    List<BalanceComptable> findByTotalSuperieur(@Param("montant") java.math.BigDecimal montant);
}

