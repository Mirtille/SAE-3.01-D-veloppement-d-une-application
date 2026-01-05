package modele;

import observateur.Observateur;
import observateur.Sujet;
import java.util.ArrayList;
import java.util.List;

public class SingletonTache implements Sujet {

    private static SingletonTache instance;

    // CHANGEMENT ICI : Liste de Projet
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
}