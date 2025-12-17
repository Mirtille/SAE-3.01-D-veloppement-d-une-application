package controleur;

import modele.*;
import java.time.LocalDate;

public class ControleurFX {

    private ModeleTache modele;

    public ControleurFX() {
        this.modele = ModeleTache.getInstance();
    }

    // --- C'EST CETTE MÉTHODE QUI MANQUAIT ---
    public void creerNouveauProjet(String nom) {
        modele.creerNouveauProjet(nom);
    }

    /**
     * Crée une tâche ou une colonne (dossier).
     * @param parent Le dossier/projet parent.
     * @param titre Titre de la tâche.
     * @param date Date limite.
     * @param priorite Priorité.
     * @param estDossier True si c'est une colonne/liste, False si c'est une tâche.
     */
    public void creerTache(TacheMere parent, String titre, LocalDate date, Priorite priorite, boolean estDossier) {
        modele.creerEtAjouterTache(parent, titre, date, priorite, estDossier);
    }

    public void modifierTache(TacheAbstraite tache, String nouveauTitre, LocalDate nouvelleDate, Priorite nouvellePrio) {
        if (tache != null && nouveauTitre != null && !nouveauTitre.isEmpty()) {
            tache.setTitre(nouveauTitre);
            tache.setDateLimite(nouvelleDate);
            tache.setPriorite(nouvellePrio);
        }
    }

    public void supprimerTache(TacheAbstraite tacheASupprimer) {
        if (tacheASupprimer == null) return;

        // On cherche le parent depuis le premier projet disponible pour effectuer la suppression
        // (Idéalement, on pourrait passer le projet racine en paramètre, mais cela fonctionne pour l'instant)
        if (!SingletonTache.getInstance().getMesProjets().isEmpty()) {
            TacheMere racine = SingletonTache.getInstance().getMesProjets().get(0);
            trouverEtSupprimer(racine, tacheASupprimer);
        }
    }

    // Méthode récursive pour trouver et supprimer une tâche dans l'arbre
    private boolean trouverEtSupprimer(TacheMere parent, TacheAbstraite cible) {
        if (parent.getEnfants().contains(cible)) {
            parent.supprimerEnfant(cible);
            return true;
        }

        for (TacheAbstraite enfant : parent.getEnfants()) {
            if (enfant instanceof TacheMere) {
                boolean resultat = trouverEtSupprimer((TacheMere) enfant, cible);
                if (resultat) return true;
            }
        }
        return false;
    }

    public void archiverTache(TacheAbstraite tache) {
        // À implémenter plus tard
    }
}