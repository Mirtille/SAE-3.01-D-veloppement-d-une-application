package controleur;

import modele.*;
import java.time.LocalDate;

public class ControleurFX {

    private ModeleTache modele;

    // Variable pour le Drag & Drop
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
        // Une tâche racine est toujours une TacheMere
        modele.creerEtAjouterTache(colonne, titre, date, priorite);
    }

    // --- MODIFICATION MAJEURE ICI ---
    public void creerSousTache(TacheMere parent, String titre, LocalDate date, Priorite priorite) {
        if (parent != null) {
            // ON CRÉE UNE TacheMere (et non plus une SousTache) pour permettre la récursivité
            TacheMere nouvelleSousTache = new TacheMere(titre, date, priorite);
            parent.ajouterEnfant(nouvelleSousTache);
        }
    }

    public void modifierTache(TacheAbstraite tache, String titre, LocalDate date, Priorite prio) {
        modele.modifierTache(tache, titre, date, prio);
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

    public void supprimerTache(TacheAbstraite tacheASupprimer) {
        if (tacheASupprimer == null) return;

        for (Projet projet : SingletonTache.getInstance().getMesProjets()) {
            for (Colonne colonne : projet.getColonnes()) {
                if (colonne.getTaches().contains(tacheASupprimer)) {
                    colonne.supprimerTache((TacheMere) tacheASupprimer);
                    return;
                }
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

    public void deplacerTache(TacheAbstraite tache, Colonne colonneDestination) {
        if (tache == null || colonneDestination == null) return;

        supprimerTache(tache);

        // On s'assure que c'est une TacheMere avant de l'ajouter à la colonne
        TacheMere tacheAInquerir;
        if (tache instanceof TacheMere) {
            tacheAInquerir = (TacheMere) tache;
        } else {
            tacheAInquerir = new TacheMere(tache.getTitre(), tache.getDateLimite(), tache.getPriorite());
        }
        colonneDestination.ajouterTache(tacheAInquerir);
    }

    public void deplacerVersTacheMere(TacheAbstraite tacheADeplacer, TacheMere nouveauParent) {
        if (tacheADeplacer == null || nouveauParent == null) return;
        if (tacheADeplacer == nouveauParent) return;

        supprimerTache(tacheADeplacer);
        nouveauParent.ajouterEnfant(tacheADeplacer);
        nouveauParent.notifierObservateurs();
    }
}