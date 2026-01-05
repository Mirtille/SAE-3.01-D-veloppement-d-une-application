package controleur;

import modele.*;
import java.time.LocalDate;

public class ControleurFX {

    private ModeleTache modele;

    // --- 1. VARIABLE POUR LE DRAG & DROP ---
    // Stocke la tâche temporairement pendant le glisser-déposer
    public static TacheAbstraite tacheEnDeplacement = null;

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

    public void creerTache(Colonne colonne, String titre, LocalDate date, Priorite priorite) {
        modele.creerEtAjouterTache(colonne, titre, date, priorite);
    }

    public void creerSousTache(TacheMere parent, String titre, LocalDate date, Priorite priorite) {
        modele.creerEtAjouterSousTache(parent, titre, date, priorite);
    }

    public void modifierTache(TacheAbstraite tache, String titre, LocalDate date, Priorite prio) {
        modele.modifierTache(tache, titre, date, prio);
    }

    public void supprimerTache(TacheAbstraite tacheASupprimer) {
        if (tacheASupprimer == null) return;

        for (Projet projet : SingletonTache.getInstance().getMesProjets()) {
            for (Colonne colonne : projet.getColonnes()) {
                // Cas 1 : Tâche racine dans une colonne
                if (colonne.getTaches().contains(tacheASupprimer)) {
                    colonne.supprimerTache((TacheMere) tacheASupprimer);
                    return;
                }
                // Cas 2 : Sous-tâche (récursif)
                for (TacheMere tacheRacine : colonne.getTaches()) {
                    if (trouverEtSupprimerRecursif(tacheRacine, tacheASupprimer)) return;
                }
            }
        }
    }

    private boolean trouverEtSupprimerRecursif(TacheMere parent, TacheAbstraite cible) {
        if (parent.getEnfants().contains(cible)) {
            parent.supprimerEnfant(cible);
            return true;
        }
        for (TacheAbstraite enfant : parent.getEnfants()) {
            if (enfant instanceof TacheMere) {
                if (trouverEtSupprimerRecursif((TacheMere) enfant, cible)) return true;
            }
        }
        return false;
    }

    public void supprimerColonne(Colonne colonneASupprimer) {
        if (colonneASupprimer == null) return;
        for (Projet p : SingletonTache.getInstance().getMesProjets()) {
            if (p.getColonnes().contains(colonneASupprimer)) {
                p.supprimerColonne(colonneASupprimer);
                return;
            }
        }
    }

    // --- 2. LOGIQUE DE DÉPLACEMENT (DRAG & DROP) ---
    public void deplacerTache(TacheAbstraite tache, Colonne colonneDestination) {
        if (tache == null || colonneDestination == null) return;

        // 1. On retire la tâche de son ancien emplacement
        supprimerTache(tache);

        // 2. On l'ajoute à la nouvelle colonne
        // Si c'était une sous-tâche, on la convertit en TacheMere pour qu'elle puisse aller dans une colonne
        TacheMere tacheAInquerir;
        if (tache instanceof TacheMere) {
            tacheAInquerir = (TacheMere) tache;
        } else {
            // Conversion SousTache -> TacheMere
            tacheAInquerir = new TacheMere(tache.getTitre(), tache.getDateLimite(), tache.getPriorite());
            // Note: On perd les enfants d'une sous-tâche ici car une SousTache n'a pas d'enfants dans ce modèle
        }

        colonneDestination.ajouterTache(tacheAInquerir);
    }
}