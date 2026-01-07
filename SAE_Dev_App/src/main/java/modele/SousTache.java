package modele;

import java.time.LocalDate;

public class SousTache extends TacheAbstraite {

    public SousTache(String titre,LocalDate dateDebut, LocalDate dateFin, Priorite priorite) {
        super(titre,dateDebut, dateFin, priorite);
    }

    @Override
    public String afficher() {
        return "   -> [Tâche] " + titre + " (" + priorite + " - " + dateLimite + ")";
    }
}