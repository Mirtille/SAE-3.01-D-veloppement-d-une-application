package modele;

import java.time.LocalDate;

public class Modele {

    // L'instance unique (Singleton)
    private static Modele instance;

    // La donnée principale (La racine de ton composite)
    private TacheMere racine;

    // Constructeur privé
    private Modele() {
        // Initialisation de la racine
        this.racine = new TacheMere("Projet Global", LocalDate.now(), Priorite.MOYENNE);
    }

    // Point d'accès unique
    public static Modele getInstance() {
        if (instance == null) {
            instance = new Modele();
        }
        return instance;
    }

    // Accesseur pour récupérer les données
    public TacheMere getRacine() {
        return racine;
    }
}