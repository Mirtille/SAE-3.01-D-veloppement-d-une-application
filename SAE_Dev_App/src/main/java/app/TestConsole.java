package app;

import controleur.Controleur;
import modele.Priorite;
import modele.TacheMere;
import vue.VueConsole;
import java.time.LocalDate;

public class TestConsole {
    public static void main(String[] args) {
        TacheMere racine = new TacheMere("Projet Global", LocalDate.now(), Priorite.HAUTE);
        VueConsole vue = new VueConsole();
        racine.enregistrerObservateur(vue);
        Controleur controleur = new Controleur(racine, vue);
        controleur.demarrer();
    }
}