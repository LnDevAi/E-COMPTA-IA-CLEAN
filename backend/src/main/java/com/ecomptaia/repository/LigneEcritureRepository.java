package com.ecomptaia.repository;

import com.ecomptaia.entity.LigneEcriture;
import com.ecomptaia.entity.EcritureComptable;
import com.ecomptaia.accounting.entity.Account;
import com.ecomptaia.entity.ThirdParty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface LigneEcritureRepository extends JpaRepository<LigneEcriture, Long> {
    
    // Recherche par écriture
    List<LigneEcriture> findByEcritureOrderByOrdreAsc(EcritureComptable ecriture);
    
    // Recherche par compte
    @Query("SELECT l FROM LigneEcriture l WHERE l.compte = :compte ORDER BY l.ecriture.dateEcriture DESC")
    List<LigneEcriture> findByCompte(@Param("compte") Account compte);
    
    // Recherche par tiers
    @Query("SELECT l FROM LigneEcriture l WHERE l.tiers = :tiers ORDER BY l.ecriture.dateEcriture DESC")
    List<LigneEcriture> findByTiers(@Param("tiers") ThirdParty tiers);
    
    // Recherche par centre de coût
    @Query("SELECT l FROM LigneEcriture l WHERE l.centreCout.id = :centreCoutId ORDER BY l.ecriture.dateEcriture DESC")
    List<LigneEcriture> findByCentreCoutId(@Param("centreCoutId") Long centreCoutId);
    
    // Recherche par projet
    @Query("SELECT l FROM LigneEcriture l WHERE l.projet.id = :projetId ORDER BY l.ecriture.dateEcriture DESC")
    List<LigneEcriture> findByProjetId(@Param("projetId") Long projetId);
    
    // Recherche par montant
    @Query("SELECT l FROM LigneEcriture l WHERE l.debit = :montant OR l.credit = :montant ORDER BY l.ecriture.dateEcriture DESC")
    List<LigneEcriture> findByMontant(@Param("montant") BigDecimal montant);
    
    // Recherche par plage de montants
    @Query("SELECT l FROM LigneEcriture l WHERE " +
           "l.debit BETWEEN :montantMin AND :montantMax OR " +
           "l.credit BETWEEN :montantMin AND :montantMax " +
           "ORDER BY l.ecriture.dateEcriture DESC")
    List<LigneEcriture> findByPlageMontants(
        @Param("montantMin") BigDecimal montantMin,
        @Param("montantMax") BigDecimal montantMax
    );
    
    // Recherche des lignes en débit
    @Query("SELECT l FROM LigneEcriture l WHERE l.debit > :zero ORDER BY l.ecriture.dateEcriture DESC")
    List<LigneEcriture> findByDebitGreaterThan(@Param("zero") BigDecimal zero);
    
    // Recherche des lignes en crédit
    @Query("SELECT l FROM LigneEcriture l WHERE l.credit > :zero ORDER BY l.ecriture.dateEcriture DESC")
    List<LigneEcriture> findByCreditGreaterThan(@Param("zero") BigDecimal zero);
    
    // Recherche par numéro de compte
    @Query("SELECT l FROM LigneEcriture l WHERE l.compteNumero = :compteNumero ORDER BY l.ecriture.dateEcriture DESC")
    List<LigneEcriture> findByCompteNumero(@Param("compteNumero") String compteNumero);
    
    // Recherche par libellé de compte
    @Query("SELECT l FROM LigneEcriture l WHERE LOWER(l.compteLibelle) LIKE LOWER(CONCAT('%', :compteLibelle, '%')) ORDER BY l.ecriture.dateEcriture DESC")
    List<LigneEcriture> findByCompteLibelleContainingIgnoreCase(@Param("compteLibelle") String compteLibelle);
    
    // Recherche par libellé de ligne
    @Query("SELECT l FROM LigneEcriture l WHERE LOWER(l.libelleLigne) LIKE LOWER(CONCAT('%', :libelleLigne, '%')) ORDER BY l.ecriture.dateEcriture DESC")
    List<LigneEcriture> findByLibelleLigneContainingIgnoreCase(@Param("libelleLigne") String libelleLigne);
    
    // Recherche par écriture et ordre
    List<LigneEcriture> findByEcritureAndOrdreBetweenOrderByOrdreAsc(
        EcritureComptable ecriture, 
        Integer ordreMin, 
        Integer ordreMax
    );
    
    // Recherche des lignes avec analytique
    @Query("SELECT l FROM LigneEcriture l WHERE l.analytique IS NOT NULL AND l.analytique != '' ORDER BY l.ecriture.dateEcriture DESC")
    List<LigneEcriture> findLignesAvecAnalytique();
    
    // Recherche par analytique contenant
    @Query("SELECT l FROM LigneEcriture l WHERE l.analytique LIKE %:analytique% ORDER BY l.ecriture.dateEcriture DESC")
    List<LigneEcriture> findByAnalytiqueContaining(@Param("analytique") String analytique);
    
    // Recherche des lignes par écriture et compte
    @Query("SELECT l FROM LigneEcriture l WHERE l.ecriture = :ecriture AND l.compte = :compte ORDER BY l.ordre ASC")
    List<LigneEcriture> findByEcritureAndCompte(@Param("ecriture") EcritureComptable ecriture, @Param("compte") Account compte);
    
    // Recherche des lignes par écriture et tiers
    @Query("SELECT l FROM LigneEcriture l WHERE l.ecriture = :ecriture AND l.tiers = :tiers ORDER BY l.ordre ASC")
    List<LigneEcriture> findByEcritureAndTiers(@Param("ecriture") EcritureComptable ecriture, @Param("tiers") ThirdParty tiers);
    
    // Recherche des lignes par écriture et montant
    @Query("SELECT l FROM LigneEcriture l WHERE l.ecriture = :ecriture AND (l.debit = :montant OR l.credit = :montant) ORDER BY l.ordre ASC")
    List<LigneEcriture> findByEcritureAndMontant(
        @Param("ecriture") EcritureComptable ecriture, 
        @Param("montant") BigDecimal montant
    );
    
    // Comptage des lignes par écriture
    @Query("SELECT COUNT(l) FROM LigneEcriture l WHERE l.ecriture = :ecriture")
    Long countByEcriture(@Param("ecriture") EcritureComptable ecriture);
    
    // Somme des débits par écriture
    @Query("SELECT SUM(l.debit) FROM LigneEcriture l WHERE l.ecriture = :ecriture")
    BigDecimal sumDebitByEcriture(@Param("ecriture") EcritureComptable ecriture);
    
    // Somme des crédits par écriture
    @Query("SELECT SUM(l.credit) FROM LigneEcriture l WHERE l.ecriture = :ecriture")
    BigDecimal sumCreditByEcriture(@Param("ecriture") EcritureComptable ecriture);
    
    // Recherche des lignes avec montants élevés
    @Query("SELECT l FROM LigneEcriture l WHERE " +
           "l.debit > :seuil OR l.credit > :seuil " +
           "ORDER BY l.ecriture.dateEcriture DESC")
    List<LigneEcriture> findLignesMontantsEleves(@Param("seuil") BigDecimal seuil);
    
    // Recherche des lignes par période
    @Query("SELECT l FROM LigneEcriture l WHERE " +
           "l.ecriture.dateEcriture BETWEEN :dateDebut AND :dateFin " +
           "ORDER BY l.ecriture.dateEcriture DESC")
    List<LigneEcriture> findByPeriode(
        @Param("dateDebut") java.time.LocalDate dateDebut,
        @Param("dateFin") java.time.LocalDate dateFin
    );
    
    // Recherche des lignes par entreprise
    @Query("SELECT l FROM LigneEcriture l WHERE l.ecriture.entreprise.id = :entrepriseId ORDER BY l.ecriture.dateEcriture DESC")
    List<LigneEcriture> findByEntreprise(@Param("entrepriseId") Long entrepriseId);
    
    // Recherche des lignes par exercice
    @Query("SELECT l FROM LigneEcriture l WHERE l.ecriture.exercice.id = :exerciceId ORDER BY l.ecriture.dateEcriture DESC")
    List<LigneEcriture> findByExercice(@Param("exerciceId") Long exerciceId);
    
    // Recherche des lignes par devise
    @Query("SELECT l FROM LigneEcriture l WHERE l.ecriture.devise = :devise ORDER BY l.ecriture.dateEcriture DESC")
    List<LigneEcriture> findByDevise(@Param("devise") String devise);
    
    // Recherche des lignes par type d'écriture
    @Query("SELECT l FROM LigneEcriture l WHERE l.ecriture.typeEcriture = :typeEcriture ORDER BY l.ecriture.dateEcriture DESC")
    List<LigneEcriture> findByTypeEcriture(@Param("typeEcriture") String typeEcriture);
    
    // Recherche des lignes par statut d'écriture
    @Query("SELECT l FROM LigneEcriture l WHERE l.ecriture.statut = :statut ORDER BY l.ecriture.dateEcriture DESC")
    List<LigneEcriture> findByStatutEcriture(@Param("statut") String statut);
    
    // Recherche des lignes par source d'écriture
    @Query("SELECT l FROM LigneEcriture l WHERE l.ecriture.source = :source ORDER BY l.ecriture.dateEcriture DESC")
    List<LigneEcriture> findBySourceEcriture(@Param("source") String source);
    
    // Recherche des lignes par utilisateur
    @Query("SELECT l FROM LigneEcriture l WHERE l.ecriture.utilisateur.id = :utilisateurId ORDER BY l.ecriture.dateEcriture DESC")
    List<LigneEcriture> findByUtilisateur(@Param("utilisateurId") Long utilisateurId);
    
    // Recherche des lignes avec template
    @Query("SELECT l FROM LigneEcriture l WHERE l.ecriture.templateId = :templateId ORDER BY l.ecriture.dateEcriture DESC")
    List<LigneEcriture> findByTemplate(@Param("templateId") String templateId);
    
    // Recherche des lignes par classe de compte
    @Query("SELECT l FROM LigneEcriture l WHERE l.compteNumero LIKE :classeCompte + '%' ORDER BY l.ecriture.dateEcriture DESC")
    List<LigneEcriture> findByClasseCompte(@Param("classeCompte") String classeCompte);
    
    // Recherche des lignes par sous-classe de compte
    @Query("SELECT l FROM LigneEcriture l WHERE l.compteNumero LIKE :sousClasseCompte + '%' ORDER BY l.ecriture.dateEcriture DESC")
    List<LigneEcriture> findBySousClasseCompte(@Param("sousClasseCompte") String sousClasseCompte);
    
    // Supprimer toutes les lignes d'une écriture
    void deleteByEcriture(EcritureComptable ecriture);
    
    // Recherche des lignes avec validation IA faible
    @Query("SELECT l FROM LigneEcriture l WHERE l.ecriture.validationAiConfiance < :seuilConfiance ORDER BY l.ecriture.dateEcriture DESC")
    List<LigneEcriture> findByConfianceIAFaible(@Param("seuilConfiance") Double seuilConfiance);
    
    // Recherche des lignes récentes
    @Query("SELECT l FROM LigneEcriture l WHERE l.ecriture.entreprise.id = :entrepriseId ORDER BY l.ecriture.dateEcriture DESC, l.ecriture.createdAt DESC")
    List<LigneEcriture> findLignesRecentes(@Param("entrepriseId") Long entrepriseId);
    
    // Recherche des lignes par texte (libellé, compte, etc.)
    @Query("SELECT l FROM LigneEcriture l WHERE " +
           "l.ecriture.entreprise.id = :entrepriseId AND " +
           "(" +
           "LOWER(l.libelleLigne) LIKE LOWER(CONCAT('%', :texte, '%')) OR " +
           "LOWER(l.compteLibelle) LIKE LOWER(CONCAT('%', :texte, '%')) OR " +
           "LOWER(l.compteNumero) LIKE LOWER(CONCAT('%', :texte, '%')) OR " +
           "LOWER(l.analytique) LIKE LOWER(CONCAT('%', :texte, '%'))" +
           ") " +
           "ORDER BY l.ecriture.dateEcriture DESC")
    List<LigneEcriture> findByTexte(@Param("entrepriseId") Long entrepriseId, @Param("texte") String texte);
}
