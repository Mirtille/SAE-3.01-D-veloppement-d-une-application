package modele;

import observateur.Observateur;
import observateur.Sujet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Colonne implements Sujet, Serializable {
    private static final long serialVersionUID = 1L;

    private String nom;
    private List<TacheMere> taches; // Contient les tâches (qui peuvent avoir des sous-tâches)
    private List<Observateur> observateurs;

    public Colonne(String nom) {
        this.nom = nom;
        this.taches = new ArrayList<>();
        this.observateurs = new ArrayList<>();
    }

    public void ajouterTache(TacheMere tache) {
        taches.add(tache);
        notifierObservateurs();
    }

    public void supprimerTache(TacheMere tache) {
        taches.remove(tache);
        notifierObservateurs();
    }

    public List<TacheMere> getTaches() {
        return taches;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
        notifierObservateurs();
    }

    @Override
    public String toString() {
        return nom;
    }

    // --- Implémentation du Pattern Observer ---
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
    public void viderObservateurs() {
        observateurs.clear();
    }
}