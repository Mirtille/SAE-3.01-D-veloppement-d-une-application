package controleur;

import modele.*;
import java.time.LocalDate;

public class ControleurFX {

    private ModeleTache modele;

    public ControleurFX() {
        this.modele = ModeleTache.getInstance();
    }

    public void creerNouveauProjet(String nom) {
        modele.creerNouveauProjet(nom);
    }

    public void ajouterColonne(Projet projet, String nomColonne) {
        if (projet != null && nomColonne != null && !nomColonne.isEmpty()) {
            projet.ajouterColonne(new Colonne(nomColonne));
        }
    }

    // Ajout dans une colonne (depuis VueColonne ou VueListe)
    public void creerTache(Colonne colonne, String titre, LocalDate date, Priorite priorite) {
        modele.creerEtAjouterTache(colonne, titre, date, priorite);
    }

    // Ajout d'une sous-tâche
    public void creerSousTache(TacheMere parent, String titre, LocalDate date, Priorite priorite) {
        modele.creerEtAjouterSousTache(parent, titre, date, priorite);
    }

    public void modifierTache(TacheAbstraite tache, String titre, LocalDate date, Priorite prio) {
        modele.modifierTache(tache, titre, date, prio);
    }

    public void supprimerTache(TacheAbstraite tacheASupprimer) {
        // La suppression nécessite de parcourir les projets/colonnes pour trouver le parent
        // Pour l'instant, on peut simplifier ou gérer ça dans l'itération suivante
        // car cela demande une recherche inverse (Parent de la tâche).
        System.out.println("Suppression à implémenter avec la nouvelle structure Projet/Colonne");
    }
}