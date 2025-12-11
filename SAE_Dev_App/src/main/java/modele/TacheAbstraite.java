package modele;

import java.time.LocalDate;

public abstract class TacheAbstraite {

    protected String titre;
    protected LocalDate dateLimite;
    protected Priorite priorite;

    public TacheAbstraite(String titre, LocalDate dateLimite, Priorite priorite) {
        this.titre = titre;
        this.dateLimite = dateLimite;
        this.priorite = priorite;
    }

    public String getTitre() { return titre; }

    // --- AJOUTE CECI ---
    // Chaque enfant devra coder sa propre façon de s'afficher
    public abstract String afficher();

    // Tu peux garder ou enlever toString, c'est comme tu veux
    @Override
    public String toString() { return titre; }
}