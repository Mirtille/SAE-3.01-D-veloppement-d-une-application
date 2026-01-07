package modele;

import observateur.Observateur;
import observateur.Sujet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SingletonTache implements Sujet, Serializable {
    private static final long serialVersionUID = 1L;

    private static SingletonTache instance;

    private List<Projet> mesProjets;
    private List<Observateur> observateurs;

    private SingletonTache() {
        this.mesProjets = new ArrayList<>();
        this.observateurs = new ArrayList<>();
        // Projet par défaut
        mesProjets.add(new Projet("Mon Premier Projet"));
    }

    public static synchronized SingletonTache getInstance() {
        if (instance == null) {
            instance = new SingletonTache();
        }
        return instance;
    }

    public List<Projet> getMesProjets() {
        return mesProjets;
    }

    public void ajouterProjet(Projet projet) {
        mesProjets.add(projet);
        notifierObservateurs();
    }

    @Override
    public void enregistrerObservateur(Observateur o) { observateurs.add(o); }

    @Override
    public void supprimerObservateur(Observateur o) { observateurs.remove(o); }

    @Override
    public void notifierObservateurs() {
        for (Observateur o : observateurs) o.actualiser(this);
    }
    public void viderObservateurs() {
        observateurs.clear();
    }
    public void reinitialiserAvecProjets(List<Projet> projets) {
        this.mesProjets.clear();
        this.mesProjets.addAll(projets);
        notifierObservateurs();
    }
}