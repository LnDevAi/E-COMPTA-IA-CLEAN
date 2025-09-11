ackage com.ecomptaia.repository;

import com.ecomptaia.entity.BalanceComptable;
import com.ecomptaia.entity.SoldeCompte;
import com.ecomptaia.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface SoldeCompteRepository extends JpaRepository<SoldeCompte, Long> {
    
    /**
     * Trouver les soldes par balance
     */
    List<SoldeCompte> findByBalanceOrderByNumeroCompte(BalanceComptable balance);
    
    /**
     * Trouver les soldes par compte
     */
    List<SoldeCompte> findByCompteOrderByBalanceDateBalanceDesc(Account compte);
    
    /**
     * Trouver un solde par balance et compte
     */
    Optional<SoldeCompte> findByBalanceAndCompte(BalanceComptable balance, Account compte);
    
    /**
     * Trouver les soldes par numéro de compte
     */
    List<SoldeCompte> findByNumeroCompteOrderByBalanceDateBalanceDesc(String numeroCompte);
    
    /**
     * Trouver les soldes par classe de compte
     */
    List<SoldeCompte> findByClasseCompteOrderByNumeroCompte(Integer classeCompte);
    
    /**
     * Trouver les soldes par nature de compte
     */
    List<SoldeCompte> findByNatureCompteOrderByNumeroCompte(String natureCompte);
    
    /**
     * Trouver les soldes par type de compte
     */
    List<SoldeCompte> findByTypeCompteOrderByNumeroCompte(String typeCompte);
    
    /**
     * Trouver les soldes avec des mouvements
     */
    @Query("SELECT s FROM SoldeCompte s WHERE s.nombreMouvements > 0 ORDER BY s.numeroCompte")
    List<SoldeCompte> findWithMouvements();
    
    /**
     * Trouver les soldes sans mouvements
     */
    @Query("SELECT s FROM SoldeCompte s WHERE s.nombreMouvements = 0 ORDER BY s.numeroCompte")
    List<SoldeCompte> findWithoutMouvements();
    
    /**
     * Trouver les soldes avec un solde final
     */
    @Query("SELECT s FROM SoldeCompte s WHERE s.soldeFinal != 0 ORDER BY s.numeroCompte")
    List<SoldeCompte> findWithSolde();
    
    /**
     * Trouver les soldes sans solde final
     */
    @Query("SELECT s FROM SoldeCompte s WHERE s.soldeFinal = 0 ORDER BY s.numeroCompte")
    List<SoldeCompte> findWithoutSolde();
    
    /**
     * Trouver les soldes par sens (DEBIT, CREDIT, NUL)
     */
    List<SoldeCompte> findBySensSoldeOrderByNumeroCompte(String sensSolde);
    
    /**
     * Trouver les soldes débiteurs
     */
    @Query("SELECT s FROM SoldeCompte s WHERE s.sensSolde = 'DEBIT' ORDER BY s.soldeFinal DESC")
    List<SoldeCompte> findSoldesDebiteurs();
    
    /**
     * Trouver les soldes créditeurs
     */
    @Query("SELECT s FROM SoldeCompte s WHERE s.sensSolde = 'CREDIT' ORDER BY s.soldeFinal DESC")
    List<SoldeCompte> findSoldesCrediteurs();
    
    /**
     * Trouver les soldes nuls
     */
    @Query("SELECT s FROM SoldeCompte s WHERE s.sensSolde = 'NUL' ORDER BY s.numeroCompte")
    List<SoldeCompte> findSoldesNuls();
    
    /**
     * Trouver les soldes par balance et classe
     */
    @Query("SELECT s FROM SoldeCompte s WHERE s.balance = :balance AND s.classeCompte = :classe ORDER BY s.numeroCompte")
    List<SoldeCompte> findByBalanceAndClasse(@Param("balance") BalanceComptable balance, @Param("classe") Integer classe);
    
    /**
     * Trouver les soldes par balance et nature
     */
    @Query("SELECT s FROM SoldeCompte s WHERE s.balance = :balance AND s.natureCompte = :nature ORDER BY s.numeroCompte")
    List<SoldeCompte> findByBalanceAndNature(@Param("balance") BalanceComptable balance, @Param("nature") String nature);
    
    /**
     * Calculer le total des soldes débiteurs pour une balance
     */
    @Query("SELECT COALESCE(SUM(s.soldeFinDebit), 0) FROM SoldeCompte s WHERE s.balance = :balance")
    BigDecimal calculateTotalDebitByBalance(@Param("balance") BalanceComptable balance);
    
    /**
     * Calculer le total des soldes créditeurs pour une balance
     */
    @Query("SELECT COALESCE(SUM(s.soldeFinCredit), 0) FROM SoldeCompte s WHERE s.balance = :balance")
    BigDecimal calculateTotalCreditByBalance(@Param("balance") BalanceComptable balance);
    
    /**
     * Calculer le total des mouvements débiteurs pour une balance
     */
    @Query("SELECT COALESCE(SUM(s.mouvementDebit), 0) FROM SoldeCompte s WHERE s.balance = :balance")
    BigDecimal calculateTotalMouvementDebitByBalance(@Param("balance") BalanceComptable balance);
    
    /**
     * Calculer le total des mouvements créditeurs pour une balance
     */
    @Query("SELECT COALESCE(SUM(s.mouvementCredit), 0) FROM SoldeCompte s WHERE s.balance = :balance")
    BigDecimal calculateTotalMouvementCreditByBalance(@Param("balance") BalanceComptable balance);
    
    /**
     * Compter les comptes avec des mouvements pour une balance
     */
    @Query("SELECT COUNT(s) FROM SoldeCompte s WHERE s.balance = :balance AND s.nombreMouvements > 0")
    long countComptesWithMouvementsByBalance(@Param("balance") BalanceComptable balance);
    
    /**
     * Compter les comptes avec des soldes pour une balance
     */
    @Query("SELECT COUNT(s) FROM SoldeCompte s WHERE s.balance = :balance AND s.soldeFinal != 0")
    long countComptesWithSoldesByBalance(@Param("balance") BalanceComptable balance);
    
    /**
     * Trouver les soldes par plage de montants
     */
    @Query("SELECT s FROM SoldeCompte s WHERE s.balance = :balance AND s.soldeFinal BETWEEN :montantMin AND :montantMax ORDER BY s.soldeFinal DESC")
    List<SoldeCompte> findByBalanceAndSoldeRange(@Param("balance") BalanceComptable balance, 
                                                @Param("montantMin") BigDecimal montantMin, 
                                                @Param("montantMax") BigDecimal montantMax);
    
    /**
     * Trouver les soldes par nombre de mouvements
     */
    @Query("SELECT s FROM SoldeCompte s WHERE s.balance = :balance AND s.nombreMouvements >= :nombreMin ORDER BY s.nombreMouvements DESC")
    List<SoldeCompte> findByBalanceAndNombreMouvementsMin(@Param("balance") BalanceComptable balance, 
                                                         @Param("nombreMin") Integer nombreMin);
    
    /**
     * Supprimer les soldes d'une balance
     */
    void deleteByBalance(BalanceComptable balance);
    
    /**
     * Trouver les soldes par ordre d'affichage
     */
    List<SoldeCompte> findByBalanceOrderByOrdreAffichageAsc(BalanceComptable balance);
}
