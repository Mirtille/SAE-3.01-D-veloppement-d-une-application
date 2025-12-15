package modele;

import java.time.LocalDate;

public class Modele {

    // 1. Instance unique (Singleton)
    private static Modele instance;

    // 2. La donnée réelle (La racine de ton composite)
    private TacheMere racine;

    // 3. Constructeur privé (personne ne peut faire new DataManager)
    private Modele() {
        // On initialise la racine par défaut
        this.racine = new TacheMere("Projet Global", LocalDate.now(), Priorite.MOYENNE);
    }

    // 4. Point d'accès public
    public static Modele getInstance() {
        if (instance == null) {
            instance = new Modele();
        }
        return instance;
    }

    // 5. Accesseur pour récupérer la racine
    public TacheMere getRacine() {
        return racine;
    }
}