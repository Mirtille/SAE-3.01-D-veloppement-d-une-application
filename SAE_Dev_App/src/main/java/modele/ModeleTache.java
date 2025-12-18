package modele;

import java.time.LocalDate;

public class ModeleTache {

    private static ModeleTache instance;

    private ModeleTache() {}

    public static ModeleTache getInstance() {
        if (instance == null) instance = new ModeleTache();
        return instance;
    }

    // 1. MÉTHODE POUR CRÉER UN PROJET (Nouveau !)
    public void creerNouveauProjet(String nomProjet) {
        if (nomProjet != null && !nomProjet.isEmpty()) {
            TacheMere nouveau = new TacheMere(nomProjet, LocalDate.now(), Priorite.MOYENNE);
            // On l'ajoute au Singleton
            SingletonTache.getInstance().ajouterProjet(nouveau);
        }
    }

    // 2. MÉTHODE POUR CRÉER UNE TÂCHE (Dans un projet existant)
    public void creerEtAjouterTache(TacheMere parent, String titre, LocalDate date, Priorite prio, boolean estDossier) {
        if (titre == null || titre.isEmpty()) return;

        // Sécurité : Si pas de parent, on ne peut rien faire (il faut choisir un projet
        if (parent == null) return;

        TacheAbstraite tache;
        if (estDossier) {
            tache = new TacheMere(titre, date, prio);
        } else {
            tache = new SousTache(titre, date, prio);
        }

        parent.ajouterEnfant(tache);
        // On notifie le projet concerné pour qu'il mette à jour sa liste de tâches
        parent.notifierObservateurs();
    }
}