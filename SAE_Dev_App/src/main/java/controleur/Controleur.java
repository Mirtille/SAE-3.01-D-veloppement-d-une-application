/**
package controleur;

import modele.ModeleTache;
import modele.Priorite;
import vue.VueConsole;
import java.time.LocalDate;
import java.util.Scanner;

public class Controleur {

    private ModeleTache modele;
    private VueConsole vue;
    private Scanner scanner;

    public Controleur(VueConsole vue) {
        this.modele = ModeleTache.getInstance();
        this.vue = vue;
        this.scanner = new Scanner(System.in);
    }

    public void demarrer() {
        boolean continuer = true;
        while (continuer) {
            vue.afficherMenu();
            String choix = scanner.nextLine();

            switch (choix) {
                case "1":
                    gererAjout();
                    break;
                case "2":
                    vue.afficherArbre(modele.getRacine());
                    break;
                case "3":
                    continuer = false;
                    System.out.println("Au revoir !");
                    break;
                default:
                    System.out.println("Choix invalide.");
            }
        }
    }

    private void gererAjout() {
        vue.demanderTitre();
        String titre = scanner.nextLine();

        vue.demanderDate();
        String dateStr = scanner.nextLine();
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (Exception e) {
            date = LocalDate.now();
            System.out.println("Date invalide, utilisation d'aujourd'hui.");
        }

        vue.demanderPriorite();
        String prioStr = scanner.nextLine();
        Priorite prio = Priorite.MOYENNE;
        switch (prioStr) {
            case "1": prio = Priorite.BASSE; break;
            case "2": prio = Priorite.MOYENNE; break;
            case "3": prio = Priorite.HAUTE; break;
        }

        // Appel au service du Modèle (Singleton)
        modele.creerEtAjouterTache(titre, date, prio);
    }
}
 **/