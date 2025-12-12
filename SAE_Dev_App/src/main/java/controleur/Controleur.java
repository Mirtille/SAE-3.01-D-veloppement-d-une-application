package controleur;

import modele.Priorite;
import modele.SousTache;
import modele.TacheMere;
import vue.VueConsole;

import java.time.LocalDate;
import java.util.Scanner;

public class Controleur {

    private TacheMere modele;
    private VueConsole vue;
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
                    System.out.println(" FIN ");
                    break;
                default:
                    System.out.println("Choix invalide.");
            }
        }
    }

    private void ajouterTache() {
        vue.demanderTitre();
        String titre = scanner.nextLine();
        vue.demanderDateLimite();
        String dateStr = scanner.nextLine();
        LocalDate date;
            date = LocalDate.parse(dateStr);
            System.out.println("Format de date invalide. On utilise la date d'aujourd'hui.");
            date = LocalDate.now();


        vue.demanderPriorite();
        String choixPrio = scanner.nextLine();
        Priorite priorite = null;

        switch (choixPrio) {
            case "1": priorite = Priorite.BASSE;
            break;
            case "2": priorite = Priorite.MOYENNE;
            break;
            case "3": priorite = Priorite.HAUTE;
            break;
            default:
                System.out.println("Priorité invalide. Annulation.");
        }

        SousTache nouvelle = new SousTache(titre, date, priorite);
        modele.ajouterEnfant(nouvelle);
        System.out.println("-> Commande effectuée");
    }
}