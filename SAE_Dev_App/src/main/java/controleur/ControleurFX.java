package controleur;

import modele.*;
import java.time.LocalDate;

public class ControleurFX {

    private ModeleTache modele;
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

    public void creerTache(Colonne colonne, String titre, LocalDate dateDebut, LocalDate dateFin, Priorite priorite) {
        if (dateDebut.isBefore(LocalDate.now())) {
            dateDebut = LocalDate.now();
        }
        if (dateFin.isBefore(dateDebut)) {
            dateFin = dateDebut.plusDays(1);
        }
        modele.creerEtAjouterTache(colonne, titre, dateDebut, dateFin, priorite);
    }

    public void creerSousTache(TacheMere parent, String titre, LocalDate dateDebut, LocalDate dateFin, Priorite priorite) {
        if (parent != null) {
            if (dateDebut.isBefore(LocalDate.now())) {
                dateDebut = LocalDate.now();
            }
            if (dateFin.isBefore(LocalDate.now()) || dateFin.isBefore(dateDebut)) {
                dateFin = dateDebut.plusDays(1);
            }
            // ON CRÉE UNE TacheMere (et non plus une SousTache) pour permettre la récursivité
            TacheMere nouvelleSousTache = new TacheMere(titre, dateDebut,dateFin, priorite);
            parent.ajouterEnfant(nouvelleSousTache);
        }
    }

    public void modifierTache(TacheAbstraite tache, String titre,LocalDate dateDebut, LocalDate dateFin, Priorite prio) {
        if (dateDebut.isBefore(LocalDate.now())) {
            dateDebut = LocalDate.now();
        }
        if (dateFin.isBefore(dateDebut)) {
            dateFin = dateDebut.plusDays(1);
        }
        modele.modifierTache(tache, titre, dateDebut, dateFin, prio);
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

        TacheMere tacheAInquerir;
        if (tache instanceof TacheMere) {
            tacheAInquerir = (TacheMere) tache;
        } else {
            // On conserve la date de début lors de la conversion
            tacheAInquerir = new TacheMere(tache.getTitre(), tache.getDateDebut(), tache.getDateLimite(), tache.getPriorite());
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