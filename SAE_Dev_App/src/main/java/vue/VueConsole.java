package vue;

import modele.TacheAbstraite;
import observateur.Observateur;
import observateur.Sujet;

public class VueConsole implements Observateur {

    public void afficherMenu() {
        System.out.println("\n=== MENU GESTION TÂCHES ===");
        System.out.println("1. Ajouter une tâche");
        System.out.println("2. Afficher tout");
        System.out.println("3. Quitter");
        System.out.print("Votre choix : ");
    }

    public void demanderTitre() { System.out.print("Titre : "); }
    public void demanderDate() { System.out.print("Date (AAAA-MM-JJ) : "); }

    public void demanderPriorite() {
        System.out.println("Priorité : 1=BASSE, 2=MOYENNE, 3=HAUTE");
        System.out.print("Choix : ");
    }

    public void afficherArbre(TacheAbstraite tache) {
        System.out.println("\n--- ÉTAT DU PROJET ---");
        System.out.println(tache.afficher());
        System.out.println("----------------------");
    }

    @Override
    public void actualiser(Sujet s) {
        if (s instanceof TacheAbstraite) {
            System.out.println("\n>>> NOTIFICATION : Mise à jour automatique <<<");
            afficherArbre((TacheAbstraite) s);
        }
    }
}