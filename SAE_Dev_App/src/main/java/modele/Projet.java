package modele;

import observateur.Observateur;
import observateur.Sujet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Projet implements Sujet, Serializable {
    private static final long serialVersionUID = 1L;

    private String nom;
    private List<Colonne> colonnes;
    private List<Observateur> observateurs;

    public Projet(String nom) {
        this.nom = nom;
        this.colonnes = new ArrayList<>();
        this.observateurs = new ArrayList<>();

        ajouterColonne(new Colonne("A Faire"));
        ajouterColonne(new Colonne("En Cours"));
        ajouterColonne(new Colonne("Terminé"));
    }

    public void ajouterColonne(Colonne c) {
        colonnes.add(c);
        notifierObservateurs();
    }

    public void supprimerColonne(Colonne c) {
        colonnes.remove(c);
        notifierObservateurs();
    }

    public List<Colonne> getColonnes() {
        return colonnes;
    }

    public String getNom() {
        return nom;
    }

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