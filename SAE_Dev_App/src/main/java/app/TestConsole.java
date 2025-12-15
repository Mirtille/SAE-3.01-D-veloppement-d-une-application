package app;

import controleur.Controleur;
import modele.Modele;
import vue.VueConsole;

public class TestConsole {
    public static void main(String[] args) {
        // 1. La Vue
        VueConsole vue = new VueConsole();

        // 2. ABONNEMENT
        // On récupère la racine via le DataManager pour l'abonner
        Modele.getInstance().getRacine().enregistrerObservateur(vue);

        // 3. Le Contrôleur (plus besoin de lui passer la racine)
        Controleur controleur = new Controleur(vue);

        // 4. Action
        controleur.demarrer();
    }
}