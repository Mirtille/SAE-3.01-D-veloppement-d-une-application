package modele;

import java.time.LocalDate;

public class SousTache extends TacheAbstraite {

    public SousTache(String titre, LocalDate dateLimite, Priorite priorite) {
        super(titre, dateLimite, priorite);
    }

    @Override
    public String afficher() {

        return "   -> [Tâche] " + titre + " (" + dateLimite + ") " + " (" + priorite + ")";
    }
}