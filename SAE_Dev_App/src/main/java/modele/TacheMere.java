package modele;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TacheMere extends TacheAbstraite {

    private List<TacheAbstraite> enfants;

    public TacheMere(String titre, LocalDate dateLimite, Priorite priorite) {
        super(titre, dateLimite, priorite);
        this.enfants = new ArrayList<>();
    }

    public void ajouterEnfant(TacheAbstraite t) {
        enfants.add(t);
        notifierObservateurs();
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