package controleur;

import modele.DataManager;
import modele.Tache;

public class Controleur {

    // Méthode appelée quand l'utilisateur veut créer une tâche
    public void creerTache(String titre) {
        System.out.println("[Controleur] Demande de création : " + titre);
        Tache t = new Tache(titre);

        // Le contrôleur modifie le Modèle
        DataManager.getInstance().ajouterTache(t);
    }
}