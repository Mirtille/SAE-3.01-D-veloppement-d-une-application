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

    // Gestion des observateurs factorisée ici
    protected List<Observateur> observateurs;

    public TacheAbstraite(String titre, LocalDate dateLimite, Priorite priorite) {
        this.titre = titre;
        this.dateLimite = dateLimite;
        this.priorite = priorite;
        this.observateurs = new ArrayList<>();
    }

    public String getTitre() { return titre; }

    public abstract String afficher();

    // --- Implémentation Sujet ---
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