package vue;

import modele.TacheAbstraite;
import observateur.Observateur;
import observateur.Sujet;

public class VueConsole implements Observateur {

    public void afficherMenu() {
        System.out.println("\n=== Test console MVC textuelle ===");
        System.out.println("1. Ajouter une sous-tâche");
        System.out.println("2. Afficher tout l'arbre");
        System.out.println("3. Quitter");
        System.out.print("Votre choix : ");
    }

    public void demanderPriorite() {
        System.out.println("\n=== CHOIX DE PRIORITE ===");
        System.out.println("1. BASSE");
        System.out.println("2. MOYENNE");
        System.out.println("3. HAUTE");
        System.out.print("Votre choix : ");
    }

    public void afficherTache(TacheAbstraite tache) {
        System.out.println("\n--- ÉTAT ACTUEL ---");
        System.out.println(tache.afficher());
        System.out.println("-------------------");
    }

    public void demanderDateLimite() {
        System.out.print("Entrez la date limite (AAAA-MM-JJ) : ");
    }

    public void demanderTitre() {
        System.out.print("Entrez le titre de la tâche : ");
    }

    public void actualiser(Sujet s) {
        if (s instanceof TacheAbstraite) {
            System.out.println("\n>>> Mise à jour automatique <<<");
            afficherTache((TacheAbstraite) s);
        }
    }
}