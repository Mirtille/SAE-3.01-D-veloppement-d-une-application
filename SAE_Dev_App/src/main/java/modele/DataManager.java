package modele;

import Observateur.Observateur;
import Observateur.Sujet;
import java.util.ArrayList;
import java.util.List;

public class DataManager implements Sujet {

    // 1. Singleton
    private static DataManager instance;

    // 2. Données
    private List<Tache> mesTaches;

    // 3. Gestion des Observateurs
    private List<Observateur> observateurs;

    private DataManager() {
        this.mesTaches = new ArrayList<>();
        this.observateurs = new ArrayList<>();
    }

    public static DataManager getInstance() {
        if (instance == null) instance = new DataManager();
        return instance;
    }

    // --- Méthodes Métier ---
    public void ajouterTache(Tache t) {
        mesTaches.add(t);
        System.out.println("[MODÈLE] J'ai ajouté : " + t);
        // C'est ICI que la magie opère :
        notifierObservateurs();
    }

    public List<Tache> getTaches() { return mesTaches; }

    // --- Implémentation du Sujet ---
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
        for (Observateur o : observateurs) {
            o.actualiser(this);
        }
    }
}