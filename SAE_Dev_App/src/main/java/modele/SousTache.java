package modele;

import java.io.Serializable;
import java.time.LocalDate;

public class SousTache extends TacheAbstraite implements Serializable {
    private static final long serialVersionUID = 1L;

    public SousTache(String titre,LocalDate dateDebut, LocalDate dateFin, Priorite priorite) {
        super(titre,dateDebut, dateFin, priorite);
    }

    @Override
    public String afficher() {
        return "   -> [Tâche] " + titre + " (" + priorite + " - " + dateLimite + ")";
    }
}