package controleur;

import modele.Modele;
import modele.Priorite;
import modele.SousTache;
import modele.TacheAbstraite;
import modele.TacheMere;

import java.time.LocalDate;

public class ControleurFX {

    private TacheMere racine;

    public ControleurFX() {
        this.racine = Modele.getInstance().getRacine();
    }

    public void creerTache(String titre, LocalDate date, Priorite priorite) {
        SousTache tache = new SousTache(titre, date, priorite);
        racine.ajouterEnfant(tache);
    }

    public void supprimerTache(TacheAbstraite tache) {
        if (tache != null) {
            racine.supprimerEnfant(tache);
        }
    }
}
