ackage com.ecomptaia.sycebnl.service;

import com.ecomptaia.sycebnl.entity.AnalyseOCRSycebnl;
import com.ecomptaia.sycebnl.entity.PieceJustificativeSycebnl;
import com.ecomptaia.sycebnl.repository.AnalyseOCRSycebnlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Service pour l'analyse OCR des pièces justificatives SYCEBNL
 */
@Service
@Transactional
public class AnalyseOCRService {
    
    private final AnalyseOCRSycebnlRepository analyseOCRRepository;
    
    @Autowired
    public AnalyseOCRService(AnalyseOCRSycebnlRepository analyseOCRRepository) {
        this.analyseOCRRepository = analyseOCRRepository;
    }
    
    /**
     * Analyse un document avec OCR
     */
    public AnalyseOCRSycebnl analyserDocument(PieceJustificativeSycebnl pieceJustificative) {
        // Créer une nouvelle analyse OCR
        AnalyseOCRSycebnl analyse = new AnalyseOCRSycebnl();
        analyse.setPieceJustificative(pieceJustificative);
        analyse.setStatutAnalyse(AnalyseOCRSycebnl.StatutAnalyse.EN_COURS);
        analyse.setMoteurOCRUtilise("TESSERACT");
        analyse.setVersionMoteur("5.0.0");
        
        try {
            // Simuler l'analyse OCR
            long startTime = System.currentTimeMillis();
            
            // Ici, on devrait utiliser un vrai moteur OCR comme Tesseract, Google Vision API, etc.
            String texteExtrait = simulerAnalyseOCR(pieceJustificative);
            BigDecimal confianceGlobale = calculerConfianceOCR(texteExtrait);
            
            long endTime = System.currentTimeMillis();
            
            // Remplir les résultats
            analyse.setTexteExtrait(texteExtrait);
            analyse.setConfianceGlobale(confianceGlobale);
            analyse.setLangueDetectee("fr");
            analyse.setNombreMots(compterMots(texteExtrait));
            analyse.setNombreLignes(compterLignes(texteExtrait));
            analyse.setTempsTraitementMs(endTime - startTime);
            analyse.setStatutAnalyse(AnalyseOCRSycebnl.StatutAnalyse.TERMINEE);
            
            // Sauvegarder l'analyse
            analyse = analyseOCRRepository.save(analyse);
            
            return analyse;
            
        } catch (Exception e) {
            analyse.setStatutAnalyse(AnalyseOCRSycebnl.StatutAnalyse.ERREUR);
            analyse.setErreurAnalyse(e.getMessage());
            analyseOCRRepository.save(analyse);
            throw new RuntimeException("Erreur lors de l'analyse OCR : " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les analyses OCR d'une pièce justificative
     */
    @Transactional(readOnly = true)
    public java.util.List<AnalyseOCRSycebnl> getAnalysesByPJ(Long pieceJustificativeId) {
        return analyseOCRRepository.findByPieceJustificativeIdOrderByDateAnalyseDesc(pieceJustificativeId);
    }
    
    /**
     * Récupère la dernière analyse OCR d'une pièce justificative
     */
    @Transactional(readOnly = true)
    public java.util.Optional<AnalyseOCRSycebnl> getDerniereAnalyseByPJ(Long pieceJustificativeId) {
        java.util.List<AnalyseOCRSycebnl> analyses = analyseOCRRepository.findDerniereAnalyseParPJ(pieceJustificativeId);
        return analyses.isEmpty() ? java.util.Optional.empty() : java.util.Optional.of(analyses.get(0));
    }
    
    /**
     * Récupère les analyses avec faible confiance
     */
    @Transactional(readOnly = true)
    public java.util.List<AnalyseOCRSycebnl> getAnalysesFaibleConfiance(BigDecimal seuilConfiance) {
        return analyseOCRRepository.findByConfianceFaible(seuilConfiance);
    }
    
    // Méthodes utilitaires privées
    
    private String simulerAnalyseOCR(PieceJustificativeSycebnl pieceJustificative) {
        // Simulation de l'extraction de texte OCR
        // Dans une vraie implémentation, on utiliserait Tesseract, Google Vision API, etc.
        
        StringBuilder texte = new StringBuilder();
        texte.append("FACTURE N° ").append(pieceJustificative.getNumeroPJ()).append("\n");
        texte.append("Date : ").append(pieceJustificative.getDatePiece()).append("\n");
        texte.append("Fournisseur : EXEMPLE FOURNISSEUR SARL\n");
        texte.append("Adresse : 123 Avenue de la République, Dakar\n");
        texte.append("Tél : +221 33 123 45 67\n");
        texte.append("\n");
        texte.append("Désignation                    Quantité    Prix HT    Total HT\n");
        texte.append("------------------------------------------------------------\n");
        texte.append("Prestation de service              1      100,000    100,000\n");
        texte.append("Consultation                       2       50,000    100,000\n");
        texte.append("\n");
        texte.append("Sous-total HT :                   200,000\n");
        texte.append("TVA 18% :                          36,000\n");
        texte.append("Total TTC :                       236,000\n");
        texte.append("\n");
        texte.append("Mode de paiement : Virement bancaire\n");
        texte.append("Échéance : 30 jours\n");
        
        return texte.toString();
    }
    
    private BigDecimal calculerConfianceOCR(String texteExtrait) {
        // Simulation du calcul de confiance
        // Dans une vraie implémentation, on analyserait la qualité de l'OCR
        
        if (texteExtrait == null || texteExtrait.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        // Simulation basée sur la longueur du texte et la présence de mots-clés
        double confiance = 0.7; // Base de 70%
        
        // Bonus pour la présence de mots-clés comptables
        if (texteExtrait.toLowerCase().contains("facture")) confiance += 0.1;
        if (texteExtrait.toLowerCase().contains("total")) confiance += 0.1;
        if (texteExtrait.toLowerCase().contains("tva")) confiance += 0.05;
        if (texteExtrait.toLowerCase().contains("ht")) confiance += 0.05;
        
        // Limiter à 1.0 (100%)
        confiance = Math.min(confiance, 1.0);
        
        return BigDecimal.valueOf(confiance);
    }
    
    private Integer compterMots(String texte) {
        if (texte == null || texte.trim().isEmpty()) {
            return 0;
        }
        return texte.trim().split("\\s+").length;
    }
    
    private Integer compterLignes(String texte) {
        if (texte == null || texte.trim().isEmpty()) {
            return 0;
        }
        return texte.split("\n").length;
    }
}
