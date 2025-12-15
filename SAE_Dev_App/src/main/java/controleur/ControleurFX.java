package controleur;

import modele.ModeleTache;
import modele.Priorite;
import modele.SousTache;
import modele.TacheAbstraite;
import modele.TacheMere;

import java.time.LocalDate;

public class ControleurFX {

    private TacheMere racine;

    public ControleurFX() {
        this.racine = ModeleTache.getInstance().getRacine();
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

    public void modifierTache(TacheAbstraite tache, String nouveauTitre, LocalDate nouvelleDate, Priorite nouvellePriorite) {
        if (tache != null) {
            tache.setTitre(nouveauTitre);
            tache.setDateLimite(nouvelleDate);
            tache.setPriorite(nouvellePriorite);
            // Notifier les observateurs de la modification
            tache.notifierObservateurs();
        }
    }
}
