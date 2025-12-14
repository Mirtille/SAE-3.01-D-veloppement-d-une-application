package controleur;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import modele.Modele; // <--- Changement ici
import modele.Priorite;
import modele.SousTache;
import modele.TacheMere;
import vue.VueConsole;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Controleur implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent actionEvent) {

    }

    private TacheMere tacheRacine; // Je l'appelle 'tacheRacine' pour éviter la confusion avec la classe Modele
    private VueConsole vue;
    private Scanner scanner;

    public Controleur(VueConsole vue) {
        // On récupère la racine via le Singleton "Modele"
        this.tacheRacine = Modele.getInstance().getRacine();
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
                    vue.afficherTache(tacheRacine);
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
        try {
            date = LocalDate.parse(dateStr);
        } catch (Exception e) {
            System.out.println("Format de date invalide (Attendu: AAAA-MM-JJ). On utilise la date d'aujourd'hui.");
            date = LocalDate.now();
        }

        vue.demanderPriorite();
        String choixPrio = scanner.nextLine();
        Priorite priorite = null;

        switch (choixPrio) {
            case "1": priorite = Priorite.BASSE; break;
            case "2": priorite = Priorite.MOYENNE; break;
            case "3": priorite = Priorite.HAUTE; break;
            default:
                System.out.println("Priorité invalide. Par défaut : MOYENNE");
                priorite = Priorite.MOYENNE;
        }

        SousTache nouvelle = new SousTache(titre, date, priorite);
        tacheRacine.ajouterEnfant(nouvelle);
        System.out.println("-> Commande effectuée");
    }
}