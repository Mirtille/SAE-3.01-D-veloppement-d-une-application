package modele;

import java.time.LocalDate;

public class ModeleTache {

    private static ModeleTache instance;

    private ModeleTache() {}

    public static ModeleTache getInstance() {
        if (instance == null) instance = new ModeleTache();
        return instance;
    }

    // 1. CRÉER UN PROJET (Type Projet et non TacheMere)
    public void creerNouveauProjet(String nomProjet) {
        if (nomProjet != null && !nomProjet.isEmpty()) {
            Projet nouveau = new Projet(nomProjet);
            SingletonTache.getInstance().ajouterProjet(nouveau);
        }
    }

    // 2. CRÉER UNE TÂCHE DANS UNE COLONNE (Niveau 1 : Tâche principale)
    public void creerEtAjouterTache(Colonne colonne, String titre, LocalDate dateDebut, LocalDate dateFin, Priorite prio) {
        if (titre == null || titre.isEmpty() || colonne == null) return;

        // Par défaut, une tâche créée dans une colonne est une TacheMere (peut avoir des sous-tâches)
        TacheMere nouvelleTache = new TacheMere(titre,dateDebut , dateFin, prio);
        colonne.ajouterTache(nouvelleTache);
    }

    // 3. CRÉER UNE SOUS-TÂCHE (Niveau 2 : Enfant d'une TacheMere)
    public void creerEtAjouterSousTache(TacheMere parent, String titre,LocalDate dateDebut, LocalDate dateFin, Priorite prio) {
        if (titre == null || titre.isEmpty() || parent == null) return;

        // Une sous-tâche est ajoutée aux enfants du parent
        SousTache sousTache = new SousTache(titre, dateDebut, dateFin, prio);
        parent.ajouterEnfant(sousTache);
    }

    // Modification d'une tâche (inchangé)
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