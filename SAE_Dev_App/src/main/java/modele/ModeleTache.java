package modele;

import java.time.LocalDate;

public class ModeleTache {

    private static ModeleTache instance;

    private ModeleTache() {}

    public static ModeleTache getInstance() {
        if (instance == null) instance = new ModeleTache();
        return instance;
    }

    // 1. Créer une NOUVELLE LISTE (Nouveau Projet)
    public void creerNouveauProjet(String nom) {
        if (nom != null && !nom.isEmpty()) {
            TacheMere nouveauProjet = new TacheMere(nom, LocalDate.now(), Priorite.MOYENNE);
            SingletonTache.getInstance().ajouterProjet(nouveauProjet);
        }
    }

    // 2. Créer une Tâche DANS une liste existante
    // (Note: 'parent' ne peut plus être null, il faut savoir dans quel projet on est)
    public void creerEtAjouterTache(TacheMere parent, String titre, LocalDate date, Priorite priorite, boolean estUnDossier) {
        if (titre == null || titre.isEmpty() || parent == null) return;

        TacheAbstraite nouvelleTache;
        if (estUnDossier) {
            nouvelleTache = new TacheMere(titre, date, priorite);
        } else {
            nouvelleTache = new SousTache(titre, date, priorite);
        }

        parent.ajouterEnfant(nouvelleTache);
        // On notifie le parent spécifique, pas besoin de tout rafraichir
        parent.notifierObservateurs();
    }
}