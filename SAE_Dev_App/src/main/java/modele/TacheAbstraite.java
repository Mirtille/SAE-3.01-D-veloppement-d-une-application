package modele;

import observateur.Observateur;
import observateur.Sujet;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class TacheAbstraite implements Sujet {

    protected String titre;
    protected LocalDate dateLimite;
    protected Priorite priorite;
    private List<Observateur> observateurs;

    public TacheAbstraite(String titre, LocalDate dateLimite, Priorite priorite) {
        this.titre = titre;
        this.dateLimite = dateLimite;
        this.priorite = priorite;
        this.observateurs = new ArrayList<>();
    }

    public String getTitre() { return titre; }

    public abstract String afficher();

    public void enregistrerObservateur(Observateur o) {
        observateurs.add(o);
    }

    public void supprimerObservateur(Observateur o) {
        observateurs.remove(o);
    }

    public void notifierObservateurs() {
        for (Observateur o : observateurs) {
            o.actualiser(this);
        }
    }

    @Override
    public String toString() { return titre; }
}