package controleur;

import modele.*;
import java.time.LocalDate;

public class ControleurFX {

    private ModeleTache modele;
    public static TacheAbstraite tacheEnDeplacement = null;

    public ControleurFX() {
        this.modele = ModeleTache.getInstance();
    }

    // --- GESTION PROJET / COLONNE ---

    public void creerNouveauProjet(String nom) {
        modele.creerNouveauProjet(nom);
    }

    public void ajouterColonne(Projet projet, String nomColonne) {
        if (projet != null && nomColonne != null && !nomColonne.isEmpty()) {
            projet.ajouterColonne(new Colonne(nomColonne));
        }
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

    // --- GESTION TÂCHES (CRÉATION) ---

    public void creerTache(Colonne colonne, String titre, LocalDate dateDebut, LocalDate dateFin, Priorite priorite) {
        // Vérification basique des dates
        if (dateDebut.isBefore(LocalDate.now())) dateDebut = LocalDate.now();
        if (dateFin.isBefore(dateDebut)) dateFin = dateDebut;

        modele.creerEtAjouterTache(colonne, titre, dateDebut, dateFin, priorite);
    }

    public void creerSousTache(TacheMere parent, String titre, LocalDate dateDebut, LocalDate dateFin, Priorite priorite) {
        if (parent != null) {
            // 1. GESTION DES DATES PARENT/ENFANT
            // Si la sous-tâche finit après le parent, on la bloque à la date de fin du parent
            if (dateFin.isAfter(parent.getDateLimite())) {
                dateFin = parent.getDateLimite();
            }
            // Si le début est avant le début du parent (optionnel, mais logique)
            if (dateDebut.isBefore(parent.getDateDebut())) {
                dateDebut = parent.getDateDebut();
            }

            // 2. Vérifications de cohérence standard
            if (dateDebut.isBefore(LocalDate.now())) dateDebut = LocalDate.now();
            if (dateFin.isBefore(dateDebut)) dateFin = dateDebut; // Minimum 1 jour ou même jour

            // 3. Création (On utilise TacheMere pour permettre la récursivité infinie)
            TacheMere nouvelleSousTache = new TacheMere(titre, dateDebut, dateFin, priorite);
            parent.ajouterEnfant(nouvelleSousTache);
        }
    }

    // --- GESTION TÂCHES (MODIFICATION) ---

    public void modifierTache(TacheAbstraite tache, String titre, LocalDate dateDebut, LocalDate dateFin, Priorite prio) {
        // 1. On cherche le parent de cette tâche pour vérifier les contraintes
        TacheMere parent = trouverParent(tache);

        if (parent != null) {
            // RÈGLE D'OR : L'enfant ne peut pas finir après le parent
            if (dateFin.isAfter(parent.getDateLimite())) {
                dateFin = parent.getDateLimite();
            }
            // RÈGLE D'OR : L'enfant ne peut pas commencer avant le parent
            if (dateDebut.isBefore(parent.getDateDebut())) {
                dateDebut = parent.getDateDebut();
            }
        }

        // 2. Cohérence interne
        if (dateFin.isBefore(dateDebut)) {
            dateFin = dateDebut;
        }

        // 3. Application
        modele.modifierTache(tache, titre, dateDebut, dateFin, prio);
    }

    // --- GESTION TÂCHES (SUPPRESSION / DÉPLACEMENT) ---

    public void supprimerTache(TacheAbstraite tacheASupprimer) {
        if (tacheASupprimer == null) return;

        for (Projet projet : SingletonTache.getInstance().getMesProjets()) {
            for (Colonne colonne : projet.getColonnes()) {
                // Cas 1: C'est une tâche racine dans une colonne
                if (colonne.getTaches().contains(tacheASupprimer)) {
                    colonne.supprimerTache((TacheMere) tacheASupprimer);
                    return;
                }
                // Cas 2: C'est une sous-tâche (recherche récursive)
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

        // Conversion en TacheMere si ce n'en est pas une, pour garder la flexibilité
        TacheMere tacheAInquerir;
        if (tache instanceof TacheMere) {
            tacheAInquerir = (TacheMere) tache;
        } else {
            tacheAInquerir = new TacheMere(tache.getTitre(), tache.getDateDebut(), tache.getDateLimite(), tache.getPriorite());
        }
        colonneDestination.ajouterTache(tacheAInquerir);
    }

    public void deplacerVersTacheMere(TacheAbstraite tacheADeplacer, TacheMere nouveauParent) {
        if (tacheADeplacer == null || nouveauParent == null) return;
        if (tacheADeplacer == nouveauParent) return; // Pas de boucle sur soi-même

        // Vérification contrainte date lors du déplacement (Drop)
        if (tacheADeplacer.getDateLimite().isAfter(nouveauParent.getDateLimite())) {
            // On raccourcit la tâche pour qu'elle rentre dans le parent
            tacheADeplacer.setDateLimite(nouveauParent.getDateLimite());
        }

        supprimerTache(tacheADeplacer);
        nouveauParent.ajouterEnfant(tacheADeplacer);
        nouveauParent.notifierObservateurs();
    }

    // --- UTILITAIRE : RECHERCHE DU PARENT ---

    // Cette méthode parcourt tout le projet pour retrouver qui est le père de la tâche 'enfant'
    private TacheMere trouverParent(TacheAbstraite enfant) {
        for (Projet p : SingletonTache.getInstance().getMesProjets()) {
            for (Colonne c : p.getColonnes()) {
                for (TacheMere racine : c.getTaches()) {
                    if (racine == enfant) return null; // C'est une racine, elle n'a pas de parent

                    TacheMere parentTrouve = chercherParentRecursif(racine, enfant);
                    if (parentTrouve != null) return parentTrouve;
                }
            }
        }
        return null;
    }

    private TacheMere chercherParentRecursif(TacheMere parentActuel, TacheAbstraite cible) {
        // Est-ce que ce parent contient directement la cible ?
        if (parentActuel.getEnfants().contains(cible)) {
            return parentActuel;
        }
        // Sinon, on cherche dans les enfants qui sont eux-mêmes des parents potentiels
        for (TacheAbstraite sousTache : parentActuel.getEnfants()) {
            if (sousTache instanceof TacheMere) {
                TacheMere resultat = chercherParentRecursif((TacheMere) sousTache, cible);
                if (resultat != null) return resultat;
            }
        }
        return null;
    }
}