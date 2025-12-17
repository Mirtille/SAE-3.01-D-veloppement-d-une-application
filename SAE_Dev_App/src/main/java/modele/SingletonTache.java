package modele;

import observateur.Observateur;
import observateur.Sujet;
import java.util.ArrayList;
import java.util.List;

public class SingletonTache implements Sujet { // Il devient Sujet !

    private static SingletonTache instance;

    // Au lieu d'une seule racine, on a une liste de Projets
    private List<TacheMere> mesProjets;
    private List<Observateur> observateurs; // Pour le pattern Observer

    private SingletonTache() {
        this.mesProjets = new ArrayList<>();
        this.observateurs = new ArrayList<>();

        // On crée un projet par défaut pour ne pas arriver sur du vide
        mesProjets.add(new TacheMere("Défaut", java.time.LocalDate.now(), Priorite.MOYENNE));
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

    public void ajouterProjet(TacheMere nouveauProjet) {
        mesProjets.add(nouveauProjet);
        notifierObservateurs(); // On prévient que la liste des projets a changé
    }

    public void supprimerProjet(TacheMere projet) {
        mesProjets.remove(projet);
        notifierObservateurs();
    }

    // --- Implémentation SUJET (Pour la liste des projets) ---
    @Override
    public void enregistrerObservateur(Observateur o) { observateurs.add(o); }
    @Override
    public void supprimerObservateur(Observateur o) { observateurs.remove(o); }
    @Override
    public void notifierObservateurs() {
        for (Observateur o : observateurs) o.actualiser(this);
    }
}