package controleur;

import modele.Priorite;
import modele.SousTache;
import modele.TacheMere;
import vue.VueConsole;
import java.time.LocalDate;
import java.util.Scanner;

public class Controleur {

    private TacheMere modele; // Notre "Racine" (Le Modèle)
    private VueConsole vue;   // L'écran (La Vue)
    private Scanner scanner;

    public Controleur(TacheMere modele, VueConsole vue) {
        this.modele = modele;
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
                    ajouterTache();
                    break;
                case "2":
                    vue.afficherTache(modele);
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

    private void ajouterTache() {

        vue.demanderTitre();
        String titre = scanner.nextLine();

        vue.demanderPriorite();
        String priorite = scanner.nextLine();

        Priorite prioriteSelectionnee = null;

        switch (priorite) {
            case "1":
                prioriteSelectionnee = Priorite.BASSE;;;
                break;
            case "2":
                prioriteSelectionnee = Priorite.MOYENNE;
                break;
            case "3":
                prioriteSelectionnee = Priorite.HAUTE;
                break;
            default:
                System.out.println("Choix invalide.");
        }

        SousTache nouvelle = new SousTache(titre, LocalDate.now(), prioriteSelectionnee);
        modele.ajouterEnfant(nouvelle);

        System.out.println("-> Tâche ajoutée avec succès.");
    }
}