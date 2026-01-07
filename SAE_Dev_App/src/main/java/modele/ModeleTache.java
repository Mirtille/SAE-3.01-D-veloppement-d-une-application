package modele;

import java.time.LocalDate;

public class ModeleTache {

    private static ModeleTache instance;

    private ModeleTache() {}

    public static ModeleTache getInstance() {
        if (instance == null) instance = new ModeleTache();
        return instance;
    }

    public void creerNouveauProjet(String nomProjet) {
        if (nomProjet != null && !nomProjet.isEmpty()) {
            Projet nouveau = new Projet(nomProjet);
            SingletonTache.getInstance().ajouterProjet(nouveau);
        }
    }

    public void creerEtAjouterTache(Colonne colonne, String titre, LocalDate dateDebut, LocalDate dateFin, Priorite prio) {
        if (titre == null || titre.isEmpty() || colonne == null) return;

        TacheMere nouvelleTache = new TacheMere(titre,dateDebut , dateFin, prio);
        colonne.ajouterTache(nouvelleTache);
    }

    public void creerEtAjouterSousTache(TacheMere parent, String titre,LocalDate dateDebut, LocalDate dateFin, Priorite prio) {
        if (titre == null || titre.isEmpty() || parent == null) return;

        SousTache sousTache = new SousTache(titre, dateDebut, dateFin, prio);
        parent.ajouterEnfant(sousTache);
    }

    // Modification d'une tâche
    public void modifierTache(TacheAbstraite tache, String titre, LocalDate date, LocalDate dateFin, Priorite prio) {
        if(tache != null) {
            tache.setTitre(titre);
            tache.setDateDebut(date);
            tache.setDateLimite(dateFin);
            tache.setPriorite(prio);
            tache.notifierObservateurs();
        }
    }
}