package app;

import controleur.Controleur;
import modele.Modele; // <--- Changement ici
import vue.VueConsole;

public class TestConsole {
    public static void main(String[] args) {
        VueConsole vue = new VueConsole();

        // On abonne la vue directement au Modèle (via le Singleton)
        Modele.getInstance().getRacine().enregistrerObservateur(vue);

        // On lance le contrôleur
        Controleur controleur = new Controleur(vue);
        controleur.demarrer();
    }
}