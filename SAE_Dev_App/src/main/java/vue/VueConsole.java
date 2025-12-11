package vue;

import modele.TacheAbstraite;

public class VueConsole {

    // Méthode pour afficher le menu principal
    public void afficherMenu() {
        System.out.println("\n=== Test console MVC textuelle ===");
        System.out.println("1. Ajouter une sous-tâche");
        System.out.println("2. Afficher tout l'arbre");
        System.out.println("3. Quitter");
        System.out.print("Votre choix : ");
    }

    public void demanderPriorite() {
        System.out.println("\n=== CHOIX DE PRIORITE ===");
        System.out.println("1. PRIORITE_BASSE");
        System.out.println("2. PRIORITE_MOYENNE");
        System.out.println("3. PRIORITE_HAUTE");
        System.out.print("Votre choix : ");
    }

    // Méthode pour afficher l'état du modèle (la tâche racine)
    public void afficherTache(TacheAbstraite tache) {
        System.out.println("\n--- ÉTAT ACTUEL ---");
        // On utilise la méthode afficher() du composite qui renvoie un String
        System.out.println(tache.afficher());
        System.out.println("-------------------");
    }

    // Méthode pour demander une info
    public void demanderTitre() {
        System.out.print("Entrez le titre de la tâche : ");
    }
}