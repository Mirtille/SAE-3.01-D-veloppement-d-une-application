/**
package app;

import controleur.Controleur;
import modele.ModeleTache;
import vue.VueConsole;

public class TestConsole {
    public static void main(String[] args) {
        // 1. Création de la vue
        VueConsole vue = new VueConsole();

        // 2. Abonnement au modèle (via Singleton)
        ModeleTache.getInstance().getRacine().enregistrerObservateur(vue);

        // 3. Lancement du contrôleur
        Controleur controleur = new Controleur(vue);
        controleur.demarrer();
    }
}
**/