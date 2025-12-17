package controleur;

import modele.*;
import java.time.LocalDate;

public class ControleurFX {

    private ModeleTache modeleGestion;

    public ControleurFX() {
        this.modeleGestion = ModeleTache.getInstance();
    }

    public void creerNouveauProjet(String nom) {
        ModeleTache.getInstance().creerNouveauProjet(nom);
    }

    public void creerTache(TacheAbstraite selectionParent, String titre, LocalDate date, Priorite prio, boolean estDossier) {

        TacheMere parent = null;

        // On vérifie si la sélection est bien un conteneur (TacheMere)
        if (selectionParent instanceof TacheMere) {
            parent = (TacheMere) selectionParent;
        }
        // Si c'est une SousTache ou null, le parent restera null
        // et ModeleTache le mettra à la racine automatiquement.

        // Appel au gestionnaire
        modeleGestion.creerEtAjouterTache(parent, titre, date, prio, estDossier);
    }

    // ... tes autres méthodes (modifier, supprimer) ...
}