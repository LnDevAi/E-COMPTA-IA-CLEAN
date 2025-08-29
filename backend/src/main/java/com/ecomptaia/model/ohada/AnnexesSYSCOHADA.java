package com.ecomptaia.model.ohada;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Modèle des Annexes conforme aux standards OHADA/SYSCOHADA
 */
public class AnnexesSYSCOHADA {
    
    // NOTES PRINCIPALES
    private Map<String, NoteAnnexe> notes = new HashMap<>();
    
    public AnnexesSYSCOHADA() {
        initialiserNotes();
    }
    
    private void initialiserNotes() {
        // Note 1 - Règles d'évaluation et de présentation
        notes.put("1", new NoteAnnexe("1", "Règles d'évaluation et de présentation", 
            "Présentation des méthodes comptables utilisées"));
        
        // Note 2 - Changements de méthodes comptables
        notes.put("2", new NoteAnnexe("2", "Changements de méthodes comptables", 
            "Description des changements et de leur impact"));
        
        // Note 3 - Immobilisations
        notes.put("3A", new NoteAnnexe("3A", "Immobilisations brutes", 
            "Détail des immobilisations par catégorie"));
        notes.put("3B", new NoteAnnexe("3B", "Amortissements cumulés", 
            "Détail des amortissements par catégorie"));
        notes.put("3C", new NoteAnnexe("3C", "Amortissements de l'exercice", 
            "Détail des dotations aux amortissements"));
        
        // Note 4 - Provisions
        notes.put("4", new NoteAnnexe("4", "Provisions", 
            "Détail des provisions pour risques et charges"));
        
        // Note 5 - Emprunts et dettes
        notes.put("5", new NoteAnnexe("5", "Emprunts et dettes", 
            "Échéancier des emprunts et dettes financières"));
        
        // Note 6 - Stocks
        notes.put("6", new NoteAnnexe("6", "Stocks", 
            "Détail des stocks par catégorie"));
        
        // Note 7 - Créances et dettes
        notes.put("7", new NoteAnnexe("7", "Créances et dettes", 
            "Échéancier des créances et dettes"));
        
        // Note 8 - Engagements
        notes.put("8", new NoteAnnexe("8", "Engagements", 
            "Engagements hors bilan"));
        
        // Note 9 - Résultat
        notes.put("9", new NoteAnnexe("9", "Résultat", 
            "Analyse du résultat de l'exercice"));
        
        // Note 10 - Personnel
        notes.put("10", new NoteAnnexe("10", "Personnel", 
            "Effectifs et charges de personnel"));
        
        // Note 11 - Impôts
        notes.put("11", new NoteAnnexe("11", "Impôts", 
            "Détail des impôts et taxes"));
        
        // Note 12 - Événements postérieurs
        notes.put("12", new NoteAnnexe("12", "Événements postérieurs", 
            "Événements survenus après la clôture"));
        
        // Note 13 - Informations sectorielles
        notes.put("13", new NoteAnnexe("13", "Informations sectorielles", 
            "Analyse par secteur d'activité"));
        
        // Note 14 - Transactions avec les parties liées
        notes.put("14", new NoteAnnexe("14", "Transactions avec les parties liées", 
            "Détail des transactions significatives"));
        
        // Note 15 - Instruments financiers
        notes.put("15", new NoteAnnexe("15", "Instruments financiers", 
            "Détail des instruments financiers"));
        
        // Note 16 - Capital et réserves
        notes.put("16", new NoteAnnexe("16", "Capital et réserves", 
            "Mouvements du capital et des réserves"));
        
        // Note 17 - Subventions
        notes.put("17", new NoteAnnexe("17", "Subventions", 
            "Détail des subventions reçues"));
        
        // Note 18 - Autres informations
        notes.put("18", new NoteAnnexe("18", "Autres informations", 
            "Informations complémentaires"));
    }
    
    public void genererNote3A(Map<String, Object> balance) {
        NoteAnnexe note = notes.get("3A");
        note.setContenu("Immobilisations brutes au " + java.time.LocalDate.now());
        note.setDonnees(balance);
    }
    
    public void genererNote3C(Map<String, Object> balance) {
        NoteAnnexe note = notes.get("3C");
        note.setContenu("Dotations aux amortissements de l'exercice");
        note.setDonnees(balance);
    }
    
    public void genererNote6(Map<String, Object> balance) {
        NoteAnnexe note = notes.get("6");
        note.setContenu("Détail des stocks par catégorie");
        note.setDonnees(balance);
    }
    
    public void genererNote7(Map<String, Object> balance) {
        NoteAnnexe note = notes.get("7");
        note.setContenu("Échéancier des créances et dettes");
        note.setDonnees(balance);
    }
    
    public void genererNote27A(Map<String, Object> balance) {
        NoteAnnexe note = notes.get("10");
        note.setContenu("Charges de personnel détaillées");
        note.setDonnees(balance);
    }
    
    // Getters
    public Map<String, NoteAnnexe> getNotes() { return notes; }
    
    public NoteAnnexe getNote(String numero) {
        return notes.get(numero);
    }
}





