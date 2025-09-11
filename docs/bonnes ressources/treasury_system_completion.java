// Continuation of MouvementTresorerieService
@Transactional
public MouvementTresorerieDTO creerMouvement(MouvementTresorerieDTO mouvementDTO) {
    // Validation du compte bancaire
    CompteBancaire compte = compteBancaireRepository.findById(mouvementDTO.getCompteBancaireId())
            .orElseThrow(() -> new EntityNotFoundException("Compte bancaire non trouvé"));
    
    // Calcul du nouveau solde
    BigDecimal montantSigne = calculerMontantSigne(mouvementDTO.getMontant(), mouvementDTO.getSensMouvement());
    BigDecimal nouveauSolde = compte.getSoldeActuel().add(montantSigne);
    
    // Création du mouvement
    MouvementTresorerie mouvement = new MouvementTresorerie();
    mouvement.setDateOperation(mouvementDTO.getDateOperation());
    mouvement.setDateValeur(mouvementDTO.getDateValeur() != null ? mouvementDTO.getDateValeur() : mouvementDTO.getDateOperation());
    mouvement.setReference(genererReference());
    mouvement.setLibelle(mouvementDTO.getLibelle());
    mouvement.setTypeMouvement(mouvementDTO.getTypeMouvement());
    mouvement.setSensMouvement(mouvementDTO.getSensMouvement());
    mouvement.setMontant(mouvementDTO.getMontant());
    mouvement.setTiers(mouvementDTO.getTiers());
    mouvement.setModeReglement(mouvementDTO.getModeReglement());
    mouvement.setNumeroCheque(mouvementDTO.getNumeroCheque());
    mouvement.setDateEcheance(mouvementDTO.getDateEcheance());
    mouvement.setCommentaire(mouvementDTO.getCommentaire());
    mouvement.setCompteBancaire(compte);
    mouvement.setSoldeApresMouvement(nouveauSolde);
    mouvement.setStatut(StatutMouvement.SAISI);
    
    // Sauvegarde du mouvement
    mouvement = mouvementRepository.save(mouvement);
    
    // Mise à jour du solde du compte
    compteBancaireService.mettreAJourSolde(compte.getId(), nouveauSolde);
    
    return convertToDTO(mouvement);
}

@Transactional
public MouvementTresorerieDTO modifierMouvement(Long mouvementId, MouvementTresorerieDTO mouvementDTO) {
    MouvementTresorerie mouvement = mouvementRepository.findById(mouvementId)
            .orElseThrow(() -> new EntityNotFoundException("Mouvement non trouvé"));
    
    if (mouvement.getStatut() == StatutMouvement.COMPTABILISE || mouvement.getStatut() == StatutMouvement.RAPPROCHE) {
        throw new IllegalStateException("Impossible de modifier un mouvement comptabilisé ou rapproché");
    }
    
    // Restaurer l'ancien solde
    BigDecimal ancienMontantSigne = calculerMontantSigne(mouvement.getMontant(), mouvement.getSensMouvement());
    BigDecimal soldeAvantModification = mouvement.getCompteBancaire().getSoldeActuel().subtract(ancienMontantSigne);
    
    // Appliquer le nouveau montant
    BigDecimal nouveauMontantSigne = calculerMontantSigne(mouvementDTO.getMontant(), mouvementDTO.getSensMouvement());
    BigDecimal nouveauSolde = soldeAvantModification.add(nouveauMontantSigne);
    
    // Mise à jour des champs
    mouvement.setLibelle(mouvementDTO.getLibelle());
    mouvement.setMontant(mouvementDTO.getMontant());
    mouvement.setTiers(mouvementDTO.getTiers());
    mouvement.setCommentaire(mouvementDTO.getCommentaire());
    mouvement.setSoldeApresMouvement(nouveauSolde);
    
    mouvement = mouvementRepository.save(mouvement);
    compteBancaireService.mettreAJourSolde(mouvement.getCompteBancaire().getId(), nouveauSolde);
    
    return convertToDTO(mouvement);
}

@Transactional
public void supprimerMouvement(Long mouvementId) {
    MouvementTresorerie mouvement = mouvementRepository.findById(mouvementId)
            .orElseThrow(() -> new EntityNotFoundException("Mouvement non trouvé"));
    
    if (mouvement.getStatut() == StatutMouvement.COMPTABILISE || mouvement.getStatut() == StatutMouvement.RAPPROCHE) {
        throw new IllegalStateException("Impossible de supprimer un mouvement comptabilisé ou rapproché");
    }
    
    // Restaurer le solde
    BigDecimal montantSigne = calculerMontantSigne(mouvement.getMontant(), mouvement.getSensMouvement());
    BigDecimal nouveauSolde = mouvement.getCompteBancaire().getSoldeActuel().subtract(montantSigne);
    
    mouvementRepository.delete(mouvement);
    compteBancaireService.mettreAJourSolde(mouvement.getCompteBancaire().getId(), nouveauSolde);
}

@Transactional
public MouvementTresorerieDTO validerMouvement(Long mouvementId) {
    MouvementTresorerie mouvement = mouvementRepository.findById(mouvementId)
            .orElseThrow(() -> new EntityNotFoundException("Mouvement non trouvé"));
    
    mouvement.setStatut(StatutMouvement.VALIDE);
    mouvement = mouvementRepository.save(mouvement);
    
    return convertToDTO(mouvement);
}

@Transactional(readOnly = true)
public List<MouvementTresorerieDTO> obtenirMouvementsRecents(Long entrepriseId, int limite) {
    List<MouvementTresorerie> mouvements = mouvementRepository.findMouvementsRecents(entrepriseId, limite);
    return mouvements.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
}

private BigDecimal calculerMontantSigne(BigDecimal montant, SensMouvement sens) {
    return sens == SensMouvement.ENTREE ? montant : montant.negate();
}

private String genererReference() {
    return "MVT-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
}

private MouvementTresorerieDTO convertToDTO(MouvementTresorerie mouvement) {
    MouvementTresorerieDTO dto = new MouvementTresorerieDTO();
    dto.setId(mouvement.getId());
    dto.setDateOperation(mouvement.getDateOperation());
    dto.setDateValeur(mouvement.getDateValeur());
    dto.setReference(mouvement.getReference());
    dto.setLibelle(mouvement.getLibelle());
    dto.setTypeMouvement(mouvement.getTypeMouvement());
    dto.setSensMouvement(mouvement.getSensMouvement());
    dto.setMontant(mouvement.getMontant());
    dto.setMontantDevise(mouvement.getMontantDevise());
    dto.setDeviseOrigine(mouvement.getDeviseOrigine());
    dto.setTauxChange(mouvement.getTauxChange());
    dto.setTiers(mouvement.getTiers());
    dto.setNumeroPiece(mouvement.getNumeroPiece());
    dto.setModeReglement(mouvement.getModeReglement());
    dto.setNumeroCheque(mouvement.getNumeroCheque());
    dto.setDateEcheance(mouvement.getDateEcheance());
    dto.setStatut(mouvement.getStatut());
    dto.setSoldeApresMouvement(mouvement.getSoldeApresMouvement());
    dto.setCompteBancaireId(mouvement.getCompteBancaire().getId());
    dto.setNomCompteBancaire(mouvement.getCompteBancaire().getNomBanque());
    dto.setCompteContrepartieId(mouvement.getCompteContrepartie() != null ? mouvement.getCompteContrepartie().getId() : null);
    dto.setCommentaire(mouvement.getCommentaire());
    dto.setRapproche(mouvement.getRapproche());
    dto.setDateRapprochement(mouvement.getDateRapprochement());
    
    return dto;
}
}

@Service
@Transactional
public class PrevisionTresorerieService {

private final PrevisionTresorerieRepository previsionRepository;
private final CompteBancaireRepository compteBancaireRepository;
private final MouvementTresorerieRepository mouvementRepository;

public PrevisionTresorerieService(PrevisionTresorerieRepository previsionRepository,
                                 CompteBancaireRepository compteBancaireRepository,
                                 MouvementTresorerieRepository mouvementRepository) {
    this.previsionRepository = previsionRepository;
    this.compteBancaireRepository = compteBancaireRepository;
    this.mouvementRepository = mouvementRepository;
}

@Transactional(readOnly = true)
public List<PrevisionTresorerieDTO> obtenirPrevisionsPeriode(Long entrepriseId, LocalDate dateDebut, LocalDate dateFin) {
    List<PrevisionTresorerie> previsions = previsionRepository.findByEntrepriseIdAndPeriode(entrepriseId, dateDebut, dateFin);
    return previsions.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
}

@Transactional
public PrevisionTresorerieDTO creerPrevision(PrevisionTresorerieDTO previsionDTO) {
    CompteBancaire compte = compteBancaireRepository.findById(previsionDTO.getCompteBancaireId())
            .orElseThrow(() -> new EntityNotFoundException("Compte bancaire non trouvé"));
    
    PrevisionTresorerie prevision = new PrevisionTresorerie();
    prevision.setLibelle(previsionDTO.getLibelle());
    prevision.setDatePrevisionnelle(previsionDTO.getDatePrevisionnelle());
    prevision.setTypeMouvement(previsionDTO.getTypeMouvement());
    prevision.setSensMouvement(previsionDTO.getSensMouvement());
    prevision.setMontantPrevisionnel(previsionDTO.getMontantPrevisionnel());
    prevision.setTiers(previsionDTO.getTiers());
    prevision.setPeriodicite(previsionDTO.getPeriodicite() != null ? previsionDTO.getPeriodicite() : PeriodiciteRecurrence.PONCTUEL);
    prevision.setDateFinRecurrence(previsionDTO.getDateFinRecurrence());
    prevision.setCompteBancaire(compte);
    prevision.setCommentaire(previsionDTO.getCommentaire());
    
    prevision = previsionRepository.save(prevision);
    
    // Générer les occurrences récurrentes si nécessaire
    if (prevision.getPeriodicite() != PeriodiciteRecurrence.PONCTUEL) {
        genererOccurrencesRecurrentes(prevision);
    }
    
    return convertToDTO(prevision);
}

@Transactional
public PrevisionTresorerieDTO modifierPrevision(Long previsionId, PrevisionTresorerieDTO previsionDTO) {
    PrevisionTresorerie prevision = previsionRepository.findById(previsionId)
            .orElseThrow(() -> new EntityNotFoundException("Prévision non trouvée"));
    
    if (prevision.getStatut() == StatutPrevision.REALISEE) {
        throw new IllegalStateException("Impossible de modifier une prévision réalisée");
    }
    
    prevision.setLibelle(previsionDTO.getLibelle());
    prevision.setDatePrevisionnelle(previsionDTO.getDatePrevisionnelle());
    prevision.setMontantPrevisionnel(previsionDTO.getMontantPrevisionnel());
    prevision.setTiers(previsionDTO.getTiers());
    prevision.setCommentaire(previsionDTO.getCommentaire());
    
    prevision = previsionRepository.save(prevision);
    return convertToDTO(prevision);
}

@Transactional
public PrevisionTresorerieDTO realiserPrevision(Long previsionId, BigDecimal montantRealise) {
    PrevisionTresorerie prevision = previsionRepository.findById(previsionId)
            .orElseThrow(() -> new EntityNotFoundException("Prévision non trouvée"));
    
    prevision.setMontantRealise(montantRealise);
    
    if (montantRealise.compareTo(prevision.getMontantPrevisionnel()) == 0) {
        prevision.setStatut(StatutPrevision.REALISEE);
    } else if (montantRealise.compareTo(BigDecimal.ZERO) > 0) {
        prevision.setStatut(StatutPrevision.PARTIELLEMENT_REALISEE);
    }
    
    prevision = previsionRepository.save(prevision);
    return convertToDTO(prevision);
}

@Transactional(readOnly = true)
public BigDecimal calculerFluxPrevisionnelPeriode(Long entrepriseId, LocalDate dateDebut, LocalDate dateFin) {
    BigDecimal fluxPrevisionnel = previsionRepository.calculerFluxPrevisionnelPeriode(entrepriseId, dateDebut, dateFin);
    return fluxPrevisionnel != null ? fluxPrevisionnel : BigDecimal.ZERO;
}

@Transactional(readOnly = true)
public List<PrevisionTresorerieDTO> obtenirPrevisionsEchues(Long entrepriseId) {
    List<PrevisionTresorerie> previsions = previsionRepository.findPrevisionsEchues(entrepriseId, LocalDate.now());
    return previsions.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
}

private void genererOccurrencesRecurrentes(PrevisionTresorerie previsionOrigine) {
    LocalDate dateActuelle = previsionOrigine.getDatePrevisionnelle();
    LocalDate dateFin = previsionOrigine.getDateFinRecurrence();
    
    if (dateFin == null) {
        dateFin = dateActuelle.plusYears(1); // Par défaut, générer pour 1 an
    }
    
    while (dateActuelle.isBefore(dateFin)) {
        dateActuelle = calculerProchaineOccurrence(dateActuelle, previsionOrigine.getPeriodicite());
        
        if (dateActuelle.isAfter(dateFin)) break;
        
        PrevisionTresorerie nouvellePrevision = new PrevisionTresorerie();
        nouvellePrevision.setLibelle(previsionOrigine.getLibelle());
        nouvellePrevision.setDatePrevisionnelle(dateActuelle);
        nouvellePrevision.setTypeMouvement(previsionOrigine.getTypeMouvement());
        nouvellePrevision.setSensMouvement(previsionOrigine.getSensMouvement());
        nouvellePrevision.setMontantPrevisionnel(previsionOrigine.getMontantPrevisionnel());
        nouvellePrevision.setTiers(previsionOrigine.getTiers());
        nouvellePrevision.setPeriodicite(PeriodiciteRecurrence.PONCTUEL);
        nouvellePrevision.setCompteBancaire(previsionOrigine.getCompteBancaire());
        nouvellePrevision.setCommentaire("Générée automatiquement à partir de la prévision récurrente #" + previsionOrigine.getId());
        
        previsionRepository.save(nouvellePrevision);
    }
}

private LocalDate calculerProchaineOccurrence(LocalDate date, PeriodiciteRecurrence periodicite) {
    switch (periodicite) {
        case QUOTIDIEN: return date.plusDays(1);
        case HEBDOMADAIRE: return date.plusWeeks(1);
        case MENSUEL: return date.plusMonths(1);
        case TRIMESTRIEL: return date.plusMonths(3);
        case SEMESTRIEL: return date.plusMonths(6);
        case ANNUEL: return date.plusYears(1);
        default: return date.plusDays(1);
    }
}

private PrevisionTresorerieDTO convertToDTO(PrevisionTresorerie prevision) {
    PrevisionTresorerieDTO dto = new PrevisionTresorerieDTO();
    dto.setId(prevision.getId());
    dto.setLibelle(prevision.getLibelle());
    dto.setDatePrevisionnelle(prevision.getDatePrevisionnelle());
    dto.setTypeMouvement(prevision.getTypeMouvement());
    dto.setSensMouvement(prevision.getSensMouvement());
    dto.setMontantPrevisionnel(prevision.getMontantPrevisionnel());
    dto.setMontantRealise(prevision.getMontantRealise());
    dto.setTiers(prevision.getTiers());
    dto.setStatut(prevision.getStatut());
    dto.setPeriodicite(prevision.getPeriodicite());
    dto.setDateFinRecurrence(prevision.getDateFinRecurrence());
    dto.setCompteBancaireId(prevision.getCompteBancaire().getId());
    dto.setNomCompteBancaire(prevision.getCompteBancaire().getNomBanque());
    dto.setCommentaire(prevision.getCommentaire());
    
    return dto;
}
}

@Service
@Transactional(readOnly = true)
public class TableauBordTresorerieService {

private final CompteBancaireRepository compteBancaireRepository;
private final MouvementTresorerieRepository mouvementRepository;
private final PrevisionTresorerieRepository previsionRepository;

public TableauBordTresorerieService(CompteBancaireRepository compteBancaireRepository,
                                   MouvementTresorerieRepository mouvementRepository,
                                   PrevisionTresorerieRepository previsionRepository) {
    this.compteBancaireRepository = compteBancaireRepository;
    this.mouvementRepository = mouvementRepository;
    this.previsionRepository = previsionRepository;
}

public TableauBordTresorerieDTO genererTableauBord(Long entrepriseId) {
    TableauBordTresorerieDTO tableauBord = new TableauBordTresorerieDTO();
    
    // Soldes actuels
    BigDecimal soldeTotalActuel = compteBancaireRepository.calculerSoldeTotalEntreprise(entrepriseId);
    tableauBord.setSoldeTotalActuel(soldeTotalActuel != null ? soldeTotalActuel : BigDecimal.ZERO);
    
    // Calcul du solde disponible (incluant découverts autorisés)
    List<CompteBancaire> comptes = compteBancaireRepository.findByEntrepriseIdAndActifTrue(entrepriseId);
    BigDecimal soldeTotalDisponible = comptes.stream()
            .map(CompteBancaire::getSoldeDisponible)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    tableauBord.setSoldeTotalDisponible(soldeTotalDisponible);
    
    // Total découvert
    List<CompteBancaire> comptesEnDecouvert = compteBancaireRepository.findComptesEnDecouvert(entrepriseId);
    BigDecimal totalDecouvert = comptesEnDecouvert.stream()
            .map(CompteBancaire::getSoldeActuel)
            .filter(solde -> solde.compareTo(BigDecimal.ZERO) < 0)
            .map(BigDecimal::abs)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    tableauBord.setTotalDecouvert(totalDecouvert);
    
    // Prévision de fin de période (30 jours)
    LocalDate dateFin = LocalDate.now().plusDays(30);
    BigDecimal fluxPrevisionnel = previsionRepository.calculerFluxPrevisionnelPeriode(
            entrepriseId, LocalDate.now(), dateFin);
    BigDecimal previsionSoldeFin = soldeTotalActuel.add(fluxPrevisionnel != null ? fluxPrevisionnel : BigDecimal.ZERO);
    tableauBord.setPrevisionSoldeFin(previsionSoldeFin);
    
    // Évolution des soldes (7 derniers jours)
    tableauBord.setEvolutionSoldes(calculerEvolutionSoldes(entrepriseId, 7));
    
    // Mouvements récents
    List<MouvementTresorerie> mouvementsRecents = mouvementRepository.findMouvementsRecents(entrepriseId, 10);
    tableauBord.setMouvementsRecents(mouvementsRecents.stream()
            .map(this::convertMouvementToDTO)
            .collect(Collectors.toList()));
    
    // Prévisions prochaines
    List<PrevisionTresorerie> previsionsProchaines = previsionRepository.findByEntrepriseIdAndPeriode(
            entrepriseId, LocalDate.now(), LocalDate.now().plusDays(30));
    tableauBord.setPrevisionsProchaines(previsionsProchaines.stream()
            .map(this::convertPrevisionToDTO)
            .collect(Collectors.toList()));
    
    // Répartition par type de mouvement
    tableauBord.setRepartitionParType(calculerRepartitionParType(entrepriseId));
    
    // Alertes
    tableauBord.setAlertes(genererAlertes(entrepriseId, comptes));
    
    return tableauBord;
}

private List<SoldePeriodeDTO> calculerEvolutionSoldes(Long entrepriseId, int nombreJours) {
    List<SoldePeriodeDTO> evolution = new ArrayList<>();
    LocalDate dateActuelle = LocalDate.now();
    
    for (int i = nombreJours - 1; i >= 0; i--) {
        LocalDate date = dateActuelle.minusDays(i);
        
        // Calcul approximatif du solde à cette date
        // Dans un vrai système, il faudrait calculer le solde historique exact
        BigDecimal soldeJour = compteBancaireRepository.calculerSoldeTotalEntreprise(entrepriseId);
        
        SoldePeriodeDTO soldeDTO = new SoldePeriodeDTO(date, soldeJour);
        if (!evolution.isEmpty()) {
            BigDecimal variation = soldeJour.subtract(evolution.get(evolution.size() - 1).getSolde());
            soldeDTO.setVariation(variation);
        }
        
        evolution.add(soldeDTO);
    }
    
    return evolution;
}

private Map<String, BigDecimal> calculerRepartitionParType(Long entrepriseId) {
    Map<String, BigDecimal> repartition = new HashMap<>();
    
    LocalDate dateDebut = LocalDate.now().minusMonths(1);
    LocalDate dateFin = LocalDate.now();
    
    List<MouvementTresorerie> mouvements = mouvementRepository.findByEntrepriseIdAndPeriode(
            entrepriseId, dateDebut, dateFin);
    
    for (MouvementTresorerie mouvement : mouvements) {
        String type = mouvement.getTypeMouvement().getLibelle();
        BigDecimal montant = mouvement.getSensMouvement() == SensMouvement.ENTREE ? 
                mouvement.getMontant() : mouvement.getMontant().negate();
        
        repartition.merge(type, montant, BigDecimal::add);
    }
    
    return repartition;
}

private List<AlerteTresorerieDTO> genererAlertes(Long entrepriseId, List<CompteBancaire> comptes) {
    List<AlerteTresorerieDTO> alertes = new ArrayList<>();
    
    // Alertes pour comptes en découvert
    for (CompteBancaire compte : comptes) {
        if (compte.estEnDecouvert()) {
            AlerteTresorerieDTO alerte = new AlerteTresorerieDTO(
                    "DECOUVERT",
                    compte.depasseDecouvertAutorise() ? "CRITIQUE" : "ATTENTION",
                    "Le compte " + compte.getNumeroCompte() + " est en découvert"
            );
            alerte.setMontant(compte.getSoldeActuel().abs());
            alertes.add(alerte);
        }
    }
    
    // Alertes pour prévisions échues
    List<PrevisionTresorerie> previsionsEchues = previsionRepository.findPrevisionsEchues(
            entrepriseId, LocalDate.now());
    
    if (!previsionsEchues.isEmpty()) {
        AlerteTresorerieDTO alerte = new AlerteTresorerieDTO(
                "PREVISIONS_ECHUES",
                "INFO",
                previsionsEchues.size() + " prévision(s) non réalisée(s)"
        );
        alertes.add(alerte);
    }
    
    return alertes;
}

private MouvementTresorerieDTO convertMouvementToDTO(MouvementTresorerie mouvement) {
    // Utiliser la même méthode que dans MouvementTresorerieService
    MouvementTresorerieDTO dto = new MouvementTresorerieDTO();
    dto.setId(mouvement.getId());
    dto.setDateOperation(mouvement.getDateOperation());
    dto.setLibelle(mouvement.getLibelle());
    dto.setTypeMouvement(mouvement.getTypeMouvement());
    dto.setSensMouvement(mouvement.getSensMouvement());
    dto.setMontant(mouvement.getMontant());
    dto.setTiers(mouvement.getTiers());
    dto.setStatut(mouvement.getStatut());
    dto.setNomCompteBancaire(mouvement.getCompteBancaire().getNomBanque());
    return dto;
}

private PrevisionTresorerieDTO convertPrevisionToDTO(PrevisionTresorerie prevision) {
    // Utiliser la même méthode que dans PrevisionTresorerieService
    PrevisionTresorerieDTO dto = new PrevisionTresorerieDTO();
    dto.setId(prevision.getId());
    dto.setLibelle(prevision.getLibelle());
    dto.setDatePrevisionnelle(prevision.getDatePrevisionnelle());
    dto.setTypeMouvement(prevision.getTypeMouvement());
    dto.setSensMouvement(prevision.getSensMouvement());
    dto.setMontantPrevisionnel(prevision.getMontantPrevisionnel());
    dto.setMontantRealise(prevision.getMontantRealise());
    dto.setTiers(prevision.getTiers());
    dto.setStatut(prevision.getStatut());
    dto.setNomCompteBancaire(prevision.getCompteBancaire().getNomBanque());
    return dto;
}
}

// ========================================
// CONTROLLERS REST
// ========================================

@RestController
@RequestMapping("/api/mouvements-tresorerie")
@CrossOrigin(origins = "*")
public class MouvementTresorerieController {

    private final MouvementTresorerieService mouvementService;

    public MouvementTresorerieController(MouvementTresorerieService mouvementService) {
        this.mouvementService = mouvementService;
    }

    @GetMapping("/compte/{compteId}")
    public ResponseEntity<List<MouvementTresorerieDTO>> obtenirMouvementsCompte(@PathVariable Long compteId) {
        List<MouvementTresorerieDTO> mouvements = mouvementService.obtenirMouvementsCompte(compteId);
        return ResponseEntity.ok(mouvements);
    }

    @GetMapping("/entreprise/{entrepriseId}")
    public ResponseEntity<List<MouvementTresorerieDTO>> obtenirMouvementsPeriode(
            @PathVariable Long entrepriseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        List<MouvementTresorerieDTO> mouvements = mouvementService.obtenirMouvementsPeriode(entrepriseId, dateDebut, dateFin);
        return ResponseEntity.ok(mouvements);
    }

    @PostMapping
    public ResponseEntity<MouvementTresorerieDTO> creerMouvement(@RequestBody @Valid MouvementTresorerieDTO mouvementDTO) {
        MouvementTresorerieDTO nouveauMouvement = mouvementService.creerMouvement(mouvementDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouveauMouvement);
    }

    @PutMapping("/{mouvementId}")
    public ResponseEntity<MouvementTresorerieDTO> modifierMouvement(@PathVariable Long mouvementId,
                                                                   @RequestBody @Valid MouvementTresorerieDTO mouvementDTO) {
        MouvementTresorerieDTO mouvementModifie = mouvementService.modifierMouvement(mouvementId, mouvementDTO);
        return ResponseEntity.ok(mouvementModifie);
    }

    @DeleteMapping("/{mouvementId}")
    public ResponseEntity<Void> supprimerMouvement(@PathVariable Long mouvementId) {
        mouvementService.supprimerMouvement(mouvementId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{mouvementId}/valider")
    public ResponseEntity<MouvementTresorerieDTO> validerMouvement(@PathVariable Long mouvementId) {
        MouvementTresorerieDTO mouvementValide = mouvementService.validerMouvement(mouvementId);
        return ResponseEntity.ok(mouvementValide);
    }

    @GetMapping("/entreprise/{entrepriseId}/recents")
    public ResponseEntity<List<MouvementTresorerieDTO>> obtenirMouvementsRecents(
            @PathVariable Long entrepriseId,
            @RequestParam(defaultValue = "10") int limite) {
        List<MouvementTresorerieDTO> mouvements = mouvementService.obtenirMouvementsRecents(entrepriseId, limite);
        return ResponseEntity.ok(mouvements);
    }
}

@RestController
@RequestMapping("/api/previsions-tresorerie")
@CrossOrigin(origins = "*")
public class PrevisionTresorerieController {

    private final PrevisionTresorerieService previsionService;

    public PrevisionTresorerieController(PrevisionTresorerieService previsionService) {
        this.previsionService = previsionService;
    }

    @GetMapping("/entreprise/{entrepriseId}")
    public ResponseEntity<List<PrevisionTresorerieDTO>> obtenirPrevisionsPeriode(
            @PathVariable Long entrepriseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        List<PrevisionTresorerieDTO> previsions = previsionService.obtenirPrevisionsPeriode(entrepriseId, dateDebut, dateFin);
        return ResponseEntity.ok(previsions);
    }

    @PostMapping
    public ResponseEntity<PrevisionTresorerieDTO> creerPrevision(@RequestBody @Valid PrevisionTresorerieDTO previsionDTO) {
        PrevisionTresorerieDTO nouvellePrevision = previsionService.creerPrevision(previsionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouvellePrevision);
    }

    @PutMapping("/{previsionId}")
    public ResponseEntity<PrevisionTresorerieDTO> modifierPrevision(@PathVariable Long previsionId,
                                                                   @RequestBody @Valid PrevisionTresorerieDTO previsionDTO) {
        PrevisionTresorerieDTO previsionModifiee = previsionService.modifierPrevision(previsionId, previsionDTO);
        return ResponseEntity.ok(previsionModifiee);
    }

    @PutMapping("/{previsionId}/realiser")
    public ResponseEntity<PrevisionTresorerieDTO> realiserPrevision(@PathVariable Long previsionId,
                                                                   @RequestParam BigDecimal montantRealise) {
        PrevisionTresorerieDTO previsionRealisee = previsionService.realiserPrevision(previsionId, montantRealise);
        return ResponseEntity.ok(previsionRealisee);
    }

    @GetMapping("/entreprise/{entrepriseId}/flux-previsionnel")
    public ResponseEntity<BigDecimal> calculerFluxPrevisionnelPeriode(
            @PathVariable Long entrepriseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        BigDecimal fluxPrevisionnel = previsionService.calculerFluxPrevisionnelPeriode(entrepriseId, dateDebut, dateFin);
        return ResponseEntity.ok(fluxPrevisionnel);
    }

    @GetMapping("/entreprise/{entrepriseId}/echues")
    public ResponseEntity<List<PrevisionTresorerieDTO>> obtenirPrevisionsEchues(@PathVariable Long entrepriseId) {
        List<PrevisionTresorerieDTO> previsions = previsionService.obtenirPrevisionsEchues(entrepriseId);
        return ResponseEntity.ok(previsions);
    }
}

@RestController
@RequestMapping("/api/tableau-bord")
@CrossOrigin(origins = "*")
public class TableauBordTresorerieController {

    private final TableauBordTresorerieService tableauBordService;

    public TableauBordTresorerieController(TableauBordTresorerieService tableauBordService) {
        this.tableauBordService = tableauBordService;
    }

    @GetMapping("/entreprise/{entrepriseId}")
    public ResponseEntity<TableauBordTresorerieDTO> obtenirTableauBord(@PathVariable Long entrepriseId) {
        TableauBordTresorerieDTO tableauBord = tableauBordService.genererTableauBord(entrepriseId);
        return ResponseEntity.ok(tableauBord);
    }
}

// ========================================
// EXCEPTION HANDLERS
// ========================================

@ControllerAdvice
public class TresorerieExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("ENTITY_NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        ErrorResponse error = new ErrorResponse("ILLEGAL_STATE", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        
        ErrorResponse error = new ErrorResponse("VALIDATION_ERROR", "Erreurs de validation: " + String.join(", ", errors));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse("INTERNAL_ERROR", "Une erreur interne s'est produite");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    public static class ErrorResponse {
        private String code;
        private String message;
        private LocalDateTime timestamp;

        public ErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
            this.timestamp = LocalDateTime.now();
        }

        // Getters et setters
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
}

// ========================================
// CONFIGURATION ET UTILITAIRES
// ========================================

@Configuration
@EnableJpaRepositories(basePackages = "com.entreprise.tresorerie.repository")
@EntityScan(basePackages = "com.entreprise.tresorerie.entity")
public class TresorerieConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
}

// ========================================
// SCRIPTS SQL POUR LA BASE DE DONNÉES
// ========================================

/*
-- Création des tables principales

CREATE TABLE entreprise (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(255) NOT NULL,
    siret VARCHAR(14),
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE compte_bancaire (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    numero_compte VARCHAR(50) NOT NULL UNIQUE,
    nom_banque VARCHAR(255) NOT NULL,
    type_compte VARCHAR(50) NOT NULL,
    devise_compte VARCHAR(3) NOT NULL DEFAULT 'EUR',
    solde_initial DECIMAL(15,2) DEFAULT 0.00,
    solde_actuel DECIMAL(15,2) DEFAULT 0.00,
    decouvert_autorise DECIMAL(15,2) DEFAULT 0.00,
    taux_agios DECIMAL(5,2) DEFAULT 0.00,
    actif BOOLEAN DEFAULT TRUE,
    entreprise_id BIGINT NOT NULL,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (entreprise_id) REFERENCES entreprise(id)
);

CREATE TABLE mouvement_tresorerie (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    date_operation DATE NOT NULL,
    date_valeur DATE NOT NULL,
    reference VARCHAR(50) NOT NULL,
    libelle VARCHAR(255) NOT NULL,
    type_mouvement VARCHAR(50) NOT NULL,
    sens_mouvement VARCHAR(10) NOT NULL,
    montant DECIMAL(15,2) NOT NULL,
    montant_devise DECIMAL(15,2),
    devise_origine VARCHAR(3),
    taux_change DECIMAL(10,6),
    tiers VARCHAR(255) NOT NULL,
    numero_piece VARCHAR(50),
    mode_reglement VARCHAR(50),
    numero_cheque VARCHAR(50),
    date_echeance DATE,
    statut VARCHAR(50) NOT NULL DEFAULT 'SAISI',
    solde_apres_mouvement DECIMAL(15,2),
    compte_bancaire_id BIGINT NOT NULL,
    compte_contrepartie_id BIGINT,
    prevision_id BIGINT,
    commentaire TEXT,
    rapproche BOOLEAN DEFAULT FALSE,
    date_rapprochement DATE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cree_par VARCHAR(255),
    FOREIGN KEY (compte_bancaire_id) REFERENCES compte_bancaire(id),
    FOREIGN KEY (compte_contrepartie_id) REFERENCES compte_bancaire(id),
    INDEX idx_mouvement_date_operation (date_operation),
    INDEX idx_mouvement_compte (compte_bancaire_id)
);

CREATE TABLE prevision_tresorerie (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    libelle VARCHAR(255) NOT NULL,
    date_previsionnelle DATE NOT NULL,
    type_mouvement VARCHAR(50) NOT NULL,
    sens_mouvement VARCHAR(10) NOT NULL,
    montant_previsionnel DECIMAL(15,2) NOT NULL,
    montant_realise DECIMAL(15,2) DEFAULT 0.00,
    tiers VARCHAR(255) NOT NULL,
    statut VARCHAR(50) NOT NULL DEFAULT 'PREVUE',
    periodicite VARCHAR(20) NOT NULL DEFAULT 'PONCTUEL',
    date_fin_recurrence DATE,
    compte_bancaire_id BIGINT NOT NULL,
    commentaire TEXT,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cree_par VARCHAR(255),
    FOREIGN KEY (compte_bancaire_id) REFERENCES compte_bancaire(id),
    INDEX idx_prevision_date (date_previsionnelle),
    INDEX idx_prevision_compte (compte_bancaire_id)
);

CREATE TABLE rapprochement_bancaire (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    date_rapprochement DATE NOT NULL,
    date_releve DATE NOT NULL,
    solde_releve DECIMAL(15,2) NOT NULL,
    solde_comptable DECIMAL(15,2) NOT NULL,
    solde_rapproche DECIMAL(15,2),
    ecart DECIMAL(15,2),
    statut VARCHAR(50) NOT NULL DEFAULT 'EN_COURS',
    compte_bancaire_id BIGINT NOT NULL,
    commentaire TEXT,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cree_par VARCHAR(255),
    FOREIGN KEY (compte_bancaire_id) REFERENCES compte_bancaire(id)
);

-- Ajout de la liaison avec la table prévision
ALTER TABLE mouvement_tresorerie 
ADD CONSTRAINT fk_mouvement_prevision 
FOREIGN KEY (prevision_id) REFERENCES prevision_tresorerie(id);

-- Index pour améliorer les performances
CREATE INDEX idx_compte_entreprise ON compte_bancaire(entreprise_id);
CREATE INDEX idx_mouvement_statut ON mouvement_tresorerie(statut);
CREATE INDEX idx_prevision_statut ON prevision_tresorerie(statut);

-- Données de test
INSERT INTO entreprise (nom, siret) VALUES 
('Entreprise Demo', '12345678901234');

INSERT INTO compte_bancaire (numero_compte, nom_banque, type_compte, devise_compte, solde_initial, solde_actuel, decouvert_autorise, entreprise_id) VALUES 
('FR76 1234 5678 9012 3456 7890 123', 'Banque Populaire', 'COURANT', 'EUR', 10000.00, 10000.00, 5000.00, 1),
('FR76 9876 5432 1098 7654 3210 987', 'Crédit Agricole', 'EPARGNE', 'EUR', 50000.00, 50000.00, 0.00, 1);
*/

// ========================================
// TESTS UNITAIRES (EXEMPLES)
// ========================================

/*
@SpringBootTest
@Transactional
public class MouvementTresorerieServiceTest {

    @Autowired
    private MouvementTresorerieService mouvementService;
    
    @Autowired
    private CompteBancaireRepository compteBancaireRepository;
    
    @Test
    public void testCreerMouvement() {
        // Arrange
        CompteBancaire compte = new CompteBancaire();
        compte.setNumeroCompte("TEST123");
        compte.setNomBanque("Banque Test");
        compte.setTypeCompte("COURANT");
        compte.setDeviseCompte("EUR");
        compte.setSoldeActuel(BigDecimal.valueOf(1000));
        compte = compteBancaireRepository.save(compte);
        
        MouvementTresorerieDTO mouvementDTO = new MouvementTresorerieDTO();
        mouvementDTO.setDateOperation(LocalDate.now());
        mouvementDTO.setLibelle("Test mouvement");
        mouvementDTO.setTypeMouvement(TypeMouvement.RECETTE_CLIENTS);
        mouvementDTO.setSensMouvement(SensMouvement.ENTREE);
        mouvementDTO.setMontant(BigDecimal.valueOf(500));
        mouvementDTO.setTiers("Client Test");
        mouvementDTO.setCompteBancaireId(compte.getId());
        
        // Act
        MouvementTresorerieDTO result = mouvementService.creerMouvement(mouvementDTO);
        
        // Assert
        assertNotNull(result.getId());
        assertEquals(BigDecimal.valueOf(500), result.getMontant());
        assertEquals(BigDecimal.valueOf(1500), result.getSoldeApresMouvement());
    }
}
*/comptes-bancaires")
@CrossOrigin(origins = "*")
public class CompteBancaireController {

private final CompteBancaireService compteBancaireService;

public CompteBancaireController(CompteBancaireService compteBancaireService) {
    this.compteBancaireService = compteBancaireService;
}

@GetMapping("/entreprise/{entrepriseId}")
public ResponseEntity<List<CompteBancaireDTO>> obtenirComptesEntreprise(@PathVariable Long entrepriseId) {
    List<CompteBancaireDTO> comptes = compteBancaireService.obtenirComptesEntreprise(entrepriseId);
    return ResponseEntity.ok(comptes);
}

@GetMapping("/{compteId}")
public ResponseEntity<CompteBancaireDTO> obtenirCompte(@PathVariable Long compteId) {
    CompteBancaireDTO compte = compteBancaireService.obtenirCompteParId(compteId);
    return ResponseEntity.ok(compte);
}

@PostMapping("/entreprise/{entrepriseId}")
public ResponseEntity<CompteBancaireDTO> creerCompte(@PathVariable Long entrepriseId, 
                                                    @RequestBody @Valid CompteBancaireDTO compteDTO) {
    CompteBancaireDTO nouveauCompte = compteBancaireService.creerCompte(compteDTO, entrepriseId);
    return ResponseEntity.status(HttpStatus.CREATED).body(nouveauCompte);
}

@PutMapping("/{compteId}")
public ResponseEntity<CompteBancaireDTO> modifierCompte(@PathVariable Long compteId, 
                                                       @RequestBody @Valid CompteBancaireDTO compteDTO) {
    CompteBancaireDTO compteModifie = compteBancaireService.modifierCompte(compteId, compteDTO);
    return ResponseEntity.ok(compteModifie);
}

@DeleteMapping("/{compteId}")
public ResponseEntity<Void> supprimerCompte(@PathVariable Long compteId) {
    compteBancaireService.supprimerCompte(compteId);
    return ResponseEntity.noContent().build();
}

@GetMapping("/entreprise/{entrepriseId}/solde-total")
public ResponseEntity<BigDecimal> obtenirSoldeTotalEntreprise(@PathVariable Long entrepriseId) {
    BigDecimal soldeTotal = compteBancaireService.calculerSoldeTotalEntreprise(entrepriseId);
    return ResponseEntity.ok(soldeTotal);
}

@GetMapping("/entreprise/{entrepriseId}/decouvert")
public ResponseEntity<List<CompteBancaireDTO>> obtenirComptesEnDecouvert(@PathVariable Long entrepriseId) {
    List<CompteBancaireDTO> comptes = compteBancaireService.obtenirComptesEnDecouvert(entrepriseId);
    return ResponseEntity.ok(comptes);
}
}

@RestController
@RequestMapping("/api/