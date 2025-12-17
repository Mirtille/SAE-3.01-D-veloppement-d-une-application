package modele;

import java.time.LocalDate;

public class SingletonTache {

    private static SingletonTache instance;
    private ModeleTache racine;

    private SingletonTache() {
        this.racine = new TacheMere("Projet Global", LocalDate.now(), Priorite.MOYENNE);
    }

    public static synchronized SingletonTache getInstance() {
        if (instance == null) {
            instance = new SingletonTache();
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