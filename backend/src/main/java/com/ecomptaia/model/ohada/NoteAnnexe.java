package com.ecomptaia.model.ohada;

import java.util.Map;

/**
 * Représente une note d'annexe OHADA/SYSCOHADA
 */
public class NoteAnnexe {
    private String numero;
    private String titre;
    private String description;
    private String contenu;
    private Map<String, Object> donnees;
    
    public NoteAnnexe(String numero, String titre, String description) {
        this.numero = numero;
        this.titre = titre;
        this.description = description;
    }
    
    // Getters et Setters
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    
    public Map<String, Object> getDonnees() { return donnees; }
    public void setDonnees(Map<String, Object> donnees) { this.donnees = donnees; }
}






