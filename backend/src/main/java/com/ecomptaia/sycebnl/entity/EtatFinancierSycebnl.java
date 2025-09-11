ackage com.ecomptaia.sycebnl.entity;

import com.ecomptaia.security.entity.Company;
import com.ecomptaia.entity.ExerciceSMT;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * EntitÃ© pour les Ã©tats financiers SYCEBNL gÃ©nÃ©rÃ©s
 * Supporte les systÃ¨mes Normal et Minimal de TrÃ©sorerie
 */
@Entity
@Table(name = "etats_financiers_sycebnl")
public class EtatFinancierSycebnl {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercice_id", nullable = false)
    private ExerciceSMT exercice;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entite_id", nullable = false)
    private Company entite;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type_systeme", nullable = false, length = 10)
    private TypeSysteme typeSysteme;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type_etat", nullable = false, length = 50)
    private TypeEtat typeEtat;
    
    @Column(name = "date_arrete", nullable = false)
    private LocalDate dateArrete;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", length = 20)
    private StatutEtat statut = StatutEtat.BROUILLON;
    
    @Column(name = "donnees_json", columnDefinition = "jsonb")
    private String donneesJson;
    
    // Totaux calculÃ©s
    @Column(name = "total_actif", precision = 15, scale = 2)
    private BigDecimal totalActif;
    
    @Column(name = "total_passif", precision = 15, scale = 2)
    private BigDecimal totalPassif;
    
    @Column(name = "total_produits", precision = 15, scale = 2)
    private BigDecimal totalProduits;
    
    @Column(name = "total_charges", precision = 15, scale = 2)
    private BigDecimal totalCharges;
    
    @Column(name = "resultat_net", precision = 15, scale = 2)
    private BigDecimal resultatNet;
    
    @Column(name = "equilibre")
    private Boolean equilibre = false;
    
    @Column(name = "genere_par", length = 255)
    private String generePar;
    
    @Column(name = "valide_par", length = 255)
    private String validePar;
    
    @Column(name = "date_validation")
    private LocalDateTime dateValidation;
    
    @Column(name = "date_creation")
    private LocalDateTime dateCreation;
    
    @Column(name = "date_modification")
    private LocalDateTime dateModification;
    
    // Relations
    @OneToMany(mappedBy = "etatFinancier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<NoteAnnexeSycebnl> notesAnnexes;
    
    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }
    
    // Constructeurs
    public EtatFinancierSycebnl() {
    }
    
    public EtatFinancierSycebnl(Long id, ExerciceSMT exercice, Company entite, TypeSysteme typeSysteme, 
                                TypeEtat typeEtat, LocalDate dateArrete, StatutEtat statut, 
                                String donneesJson, BigDecimal totalActif, BigDecimal totalPassif, 
                                BigDecimal totalProduits, BigDecimal totalCharges, BigDecimal resultatNet, 
                                Boolean equilibre, String generePar, String validePar, 
                                LocalDateTime dateValidation, LocalDateTime dateCreation, 
                                LocalDateTime dateModification, List<NoteAnnexeSycebnl> notesAnnexes) {
        this.id = id;
        this.exercice = exercice;
        this.entite = entite;
        this.typeSysteme = typeSysteme;
        this.typeEtat = typeEtat;
        this.dateArrete = dateArrete;
        this.statut = statut;
        this.donneesJson = donneesJson;
        this.totalActif = totalActif;
        this.totalPassif = totalPassif;
        this.totalProduits = totalProduits;
        this.totalCharges = totalCharges;
        this.resultatNet = resultatNet;
        this.equilibre = equilibre;
        this.generePar = generePar;
        this.validePar = validePar;
        this.dateValidation = dateValidation;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
        this.notesAnnexes = notesAnnexes;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public ExerciceSMT getExercice() { return exercice; }
    public void setExercice(ExerciceSMT exercice) { this.exercice = exercice; }
    
    public Company getEntite() { return entite; }
    public void setEntite(Company entite) { this.entite = entite; }
    
    public TypeSysteme getTypeSysteme() { return typeSysteme; }
    public void setTypeSysteme(TypeSysteme typeSysteme) { this.typeSysteme = typeSysteme; }
    
    public TypeEtat getTypeEtat() { return typeEtat; }
    public void setTypeEtat(TypeEtat typeEtat) { this.typeEtat = typeEtat; }
    
    public LocalDate getDateArrete() { return dateArrete; }
    public void setDateArrete(LocalDate dateArrete) { this.dateArrete = dateArrete; }
    
    public StatutEtat getStatut() { return statut; }
    public void setStatut(StatutEtat statut) { this.statut = statut; }
    
    public String getDonneesJson() { return donneesJson; }
    public void setDonneesJson(String donneesJson) { this.donneesJson = donneesJson; }
    
    public BigDecimal getTotalActif() { return totalActif; }
    public void setTotalActif(BigDecimal totalActif) { this.totalActif = totalActif; }
    
    public BigDecimal getTotalPassif() { return totalPassif; }
    public void setTotalPassif(BigDecimal totalPassif) { this.totalPassif = totalPassif; }
    
    public BigDecimal getTotalProduits() { return totalProduits; }
    public void setTotalProduits(BigDecimal totalProduits) { this.totalProduits = totalProduits; }
    
    public BigDecimal getTotalCharges() { return totalCharges; }
    public void setTotalCharges(BigDecimal totalCharges) { this.totalCharges = totalCharges; }
    
    public BigDecimal getResultatNet() { return resultatNet; }
    public void setResultatNet(BigDecimal resultatNet) { this.resultatNet = resultatNet; }
    
    public Boolean getEquilibre() { return equilibre; }
    public void setEquilibre(Boolean equilibre) { this.equilibre = equilibre; }
    
    public String getGenerePar() { return generePar; }
    public void setGenerePar(String generePar) { this.generePar = generePar; }
    
    public String getValidePar() { return validePar; }
    public void setValidePar(String validePar) { this.validePar = validePar; }
    
    public LocalDateTime getDateValidation() { return dateValidation; }
    public void setDateValidation(LocalDateTime dateValidation) { this.dateValidation = dateValidation; }
    
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    
    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }
    
    public List<NoteAnnexeSycebnl> getNotesAnnexes() { return notesAnnexes; }
    public void setNotesAnnexes(List<NoteAnnexeSycebnl> notesAnnexes) { this.notesAnnexes = notesAnnexes; }
    
    // MÃ©thode builder
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private Long id;
        private ExerciceSMT exercice;
        private Company entite;
        private TypeSysteme typeSysteme;
        private TypeEtat typeEtat;
        private LocalDate dateArrete;
        private StatutEtat statut = StatutEtat.BROUILLON;
        private String donneesJson;
        private BigDecimal totalActif;
        private BigDecimal totalPassif;
        private BigDecimal totalProduits;
        private BigDecimal totalCharges;
        private BigDecimal resultatNet;
        private Boolean equilibre = false;
        private String generePar;
        private String validePar;
        private LocalDateTime dateValidation;
        private LocalDateTime dateCreation;
        private LocalDateTime dateModification;
        private List<NoteAnnexeSycebnl> notesAnnexes;
        
        public Builder id(Long id) { this.id = id; return this; }
        public Builder exercice(ExerciceSMT exercice) { this.exercice = exercice; return this; }
        public Builder entite(Company entite) { this.entite = entite; return this; }
        public Builder typeSysteme(TypeSysteme typeSysteme) { this.typeSysteme = typeSysteme; return this; }
        public Builder typeEtat(TypeEtat typeEtat) { this.typeEtat = typeEtat; return this; }
        public Builder dateArrete(LocalDate dateArrete) { this.dateArrete = dateArrete; return this; }
        public Builder statut(StatutEtat statut) { this.statut = statut; return this; }
        public Builder donneesJson(String donneesJson) { this.donneesJson = donneesJson; return this; }
        public Builder totalActif(BigDecimal totalActif) { this.totalActif = totalActif; return this; }
        public Builder totalPassif(BigDecimal totalPassif) { this.totalPassif = totalPassif; return this; }
        public Builder totalProduits(BigDecimal totalProduits) { this.totalProduits = totalProduits; return this; }
        public Builder totalCharges(BigDecimal totalCharges) { this.totalCharges = totalCharges; return this; }
        public Builder resultatNet(BigDecimal resultatNet) { this.resultatNet = resultatNet; return this; }
        public Builder equilibre(Boolean equilibre) { this.equilibre = equilibre; return this; }
        public Builder generePar(String generePar) { this.generePar = generePar; return this; }
        public Builder validePar(String validePar) { this.validePar = validePar; return this; }
        public Builder dateValidation(LocalDateTime dateValidation) { this.dateValidation = dateValidation; return this; }
        public Builder dateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; return this; }
        public Builder dateModification(LocalDateTime dateModification) { this.dateModification = dateModification; return this; }
        public Builder notesAnnexes(List<NoteAnnexeSycebnl> notesAnnexes) { this.notesAnnexes = notesAnnexes; return this; }
        
        public EtatFinancierSycebnl build() {
            return new EtatFinancierSycebnl(id, exercice, entite, typeSysteme, typeEtat, dateArrete, 
                                          statut, donneesJson, totalActif, totalPassif, totalProduits, 
                                          totalCharges, resultatNet, equilibre, generePar, validePar, 
                                          dateValidation, dateCreation, dateModification, notesAnnexes);
        }
    }
    
    // Enums
    public enum TypeSysteme {
        NORMAL, MINIMAL
    }
    
    public enum TypeEtat {
        BILAN, COMPTE_RESULTAT, TABLEAU_FLUX, ANNEXES, RECETTES_DEPENSES, SITUATION_TRESORERIE
    }
    
    public enum StatutEtat {
        BROUILLON, VALIDE, CLOTURE
    }
}

