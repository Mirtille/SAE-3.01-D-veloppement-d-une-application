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
        if (tacheASupprimer == null) return;

        // On parcourt tous les projets pour trouver où se cache la tâche
        for (Projet projet : SingletonTache.getInstance().getMesProjets()) {

            // On parcourt toutes les colonnes du projet
            for (Colonne colonne : projet.getColonnes()) {

                // CAS 1 : La tâche est directement dans la colonne (c'est une racine)
                // Note : colonne.getTaches() contient des TacheMere
                if (colonne.getTaches().contains(tacheASupprimer)) {
                    // Le cast est sûr car la liste ne contient que des TacheMere
                    colonne.supprimerTache((TacheMere) tacheASupprimer);
                    return; // On a trouvé et supprimé, on arrête tout
                }

                // CAS 2 : La tâche est une sous-tâche (il faut fouiller dans les enfants)
                for (TacheMere tacheRacine : colonne.getTaches()) {
                    boolean resultat = trouverEtSupprimerRecursif(tacheRacine, tacheASupprimer);
                    if (resultat) return; // Trouvé et supprimé dans les sous-dossiers
                }
            }
        }
    }

    private boolean trouverEtSupprimerRecursif(TacheMere parent, TacheAbstraite cible) {
        // 1. Est-ce que 'cible' est un enfant direct de 'parent' ?
        if (parent.getEnfants().contains(cible)) {
            parent.supprimerEnfant(cible);
            return true;
        }

        // 2. Sinon, on regarde si un des enfants est lui-même un dossier qui contient la cible
        for (TacheAbstraite enfant : parent.getEnfants()) {
            if (enfant instanceof TacheMere) {
                // Appel récursif
                boolean trouve = trouverEtSupprimerRecursif((TacheMere) enfant, cible);
                if (trouve) return true;
            }
        }
        return false;
    }

    public void supprimerColonne(Colonne colonneASupprimer) {
        if (colonneASupprimer == null) return;

        // On cherche dans quel projet se trouve cette colonne
        for (Projet p : SingletonTache.getInstance().getMesProjets()) {
            if (p.getColonnes().contains(colonneASupprimer)) {
                p.supprimerColonne(colonneASupprimer);
                return; // On a trouvé et supprimé, on s'arrête
            }
        }
    }
}