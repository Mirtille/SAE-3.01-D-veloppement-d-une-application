package modele;

import java.time.LocalDate;

public class ModeleTache {

    private static ModeleTache instance;
    private TacheMere racine;

    private ModeleTache() {
        this.racine = new TacheMere("Projet Global", LocalDate.now(), Priorite.MOYENNE);
    }

    public static synchronized ModeleTache getInstance() {
        if (instance == null) {
            instance = new ModeleTache();
        }
        return instance;
    }

    public TacheMere getRacine() {
        return racine;
    }

    // Service métier : Créer et Ajouter
    public void creerEtAjouterTache(String titre, LocalDate date, Priorite priorite) {
        if (titre != null && !titre.isEmpty()) {
            SousTache nouvelle = new SousTache(titre, date, priorite);
            racine.ajouterEnfant(nouvelle);
        }
    }
}