package modele;

import observateur.Observateur;
import observateur.Sujet;
import java.util.ArrayList;
import java.util.List;

public class SingletonTache implements Sujet {

    private static SingletonTache instance;

    // TA LISTE DE PROJETS (Au lieu d'une seule racine)
    private List<TacheMere> mesProjets;

    // Gestion des observateurs (ceux qui regardent la liste des projets)
    private List<Observateur> observateurs;

    private SingletonTache() {
        this.mesProjets = new ArrayList<>();
        this.observateurs = new ArrayList<>();
        // On initialise avec un projet par défaut
        mesProjets.add(new TacheMere("Mon Premier Projet", java.time.LocalDate.now(), Priorite.MOYENNE));
    }

    public static synchronized SingletonTache getInstance() {
        if (instance == null) {
            instance = new SingletonTache();
        }
        return instance;
    }

    public List<TacheMere> getMesProjets() {
        return mesProjets;
    }

    public void ajouterProjet(TacheMere projet) {
        mesProjets.add(projet);
        notifierObservateurs(); // On dit à la VueListe : "La liste des projets a changé !"
    }

    // --- SUJET ---
    @Override
    public void enregistrerObservateur(Observateur o) {
        observateurs.add(o);
    }

    @Override
    public void supprimerObservateur(Observateur o) {
        observateurs.remove(o);
    }

    @Override
    public void notifierObservateurs() {
        for (Observateur o : observateurs) o.actualiser(this);
    }
}