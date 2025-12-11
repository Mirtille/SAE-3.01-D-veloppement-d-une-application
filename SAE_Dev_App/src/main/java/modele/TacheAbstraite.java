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

    public abstract String afficher();

    @Override
    public String toString() { return titre; }
}