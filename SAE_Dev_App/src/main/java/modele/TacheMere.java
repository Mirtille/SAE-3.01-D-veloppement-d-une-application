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
    }

    @Override
    public String afficher() {
        StringBuilder sb = new StringBuilder();
        // 1. On affiche le titre du dossier
        sb.append("[DOSSIER] ").append(titre).append("\n");
        // 2. On boucle sur les enfants (Récursivité)
        for (TacheAbstraite enfant : enfants) {
            // On appelle la méthode afficher() de l'enfant
            // Si l'enfant est une TacheMere, ça relancera ce processus !
            sb.append(enfant.afficher()).append("\n");
        }

        return sb.toString();
    }
}