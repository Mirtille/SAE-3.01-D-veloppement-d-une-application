package modele;

import observateur.Observateur;
import observateur.Sujet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public abstract class TacheAbstraite implements Sujet, Serializable {
    private static final long serialVersionUID = 1L;
    protected String titre;
    protected LocalDate dateDebut;
    protected LocalDate dateLimite;
    protected Priorite priorite;
    protected Etat etat;

    protected List<Observateur> observateurs;

    public TacheAbstraite(String titre, LocalDate dateDebut, LocalDate dateLimite, Priorite priorite) {
        this.titre = titre;
        this.dateDebut = dateDebut;
        this.dateLimite = dateLimite;
        this.priorite = priorite;
        this.etat = Etat.A_FAIRE; // État par défaut
        this.observateurs = new ArrayList<>();
    }

    public Etat getEtat() {
        return etat;
    }

    public void setEtat(Etat etat) {
        this.etat = etat;
        notifierObservateurs(); // Important pour déplacer la carte d'une colonne à l'autre !
    }

    public String getTitre() { return titre; }

    public abstract String afficher();

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

    public Priorite getPriorite() {
        return priorite;
    }

    public List<TacheAbstraite> getEnfants() {
        return new ArrayList<>();
    }

    public String setTitre(String nouveauTitre) {
        this.titre = nouveauTitre;
        return this.titre;
    }

    public LocalDate setDateLimite(LocalDate nouvelleDate) {
        this.dateLimite = nouvelleDate;
        return this.dateLimite;
    }

    public LocalDate getDateLimite() {
        return dateLimite;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
        notifierObservateurs();
    }

    public Priorite setPriorite(Priorite nouvellePriorite) {
        this.priorite = nouvellePriorite;
        return this.priorite;
    }
    public void viderObservateurs() {
        observateurs.clear();
    }
}