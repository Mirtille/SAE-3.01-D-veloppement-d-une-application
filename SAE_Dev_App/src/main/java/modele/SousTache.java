package modele;

import java.time.LocalDate;

public class SousTache extends TacheAbstraite {

    public SousTache(String titre, LocalDate dateLimite, Priorite priorite) {
        super(titre, dateLimite, priorite);
    }

    @Override
    public String afficher() {
        return "   -> [Tâche] >>> TITRE : " + titre + " | DATE LIMITE : (" + dateLimite + ") " + "| PRIORITE (" + priorite + ")";
    }

    public Priorite getPriorite() {
        return priorite;
    }

    public LocalDate getDateLimite() {
        return dateLimite;
    }

    public String getTitre() {
        return titre;
    }
}