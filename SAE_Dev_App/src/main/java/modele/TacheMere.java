package modele;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TacheMere extends TacheAbstraite implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<TacheAbstraite> enfants;

    public TacheMere(String titre, LocalDate dateDebut, LocalDate dateLimite, Priorite priorite) {
        super(titre, dateDebut, dateLimite, priorite);
        this.enfants = new ArrayList<>();
    }

    public void ajouterEnfant(TacheAbstraite t) {
        enfants.add(t);

        notifierObservateurs();
    }

    public void supprimerEnfant(TacheAbstraite t) {
        enfants.remove(t);
        notifierObservateurs(); // Indispensable pour que la carte disparaisse de l'écran !
    }

    @Override
    public List<TacheAbstraite> getEnfants() {
        return enfants;
    }

    @Override
    public String afficher() {
        StringBuilder sb = new StringBuilder();
        sb.append("[DOSSIER] ").append(titre).append("\n");
        for (TacheAbstraite enfant : enfants) {
            sb.append(enfant.afficher()).append("\n");
        }
        return sb.toString();
    }
}