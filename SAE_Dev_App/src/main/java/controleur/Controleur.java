package controleur;

import modele.DataManager;
import modele.Priorite;
import modele.Tache;
import vue.MainVue;
import javafx.event.ActionEvent;
import java.time.LocalDate;

public class Controleur {

    private MainVue vue;

    public Controleur(MainVue vue) {
        this.vue = vue;
        // On branche l'action du bouton
        this.vue.getBoutonAjouter().setOnAction(this::gererAjoutTache);
    }

    private void gererAjoutTache(ActionEvent e) {
        String titre = vue.getChampTitre().getText();
        LocalDate date = vue.getDatePicker().getValue();
        Priorite prio = vue.getComboPriorite().getValue();

        if (titre != null && !titre.isEmpty() && date != null) {
            // Création
            Tache t = new Tache(titre, date, prio);

            // Ajout au modèle -> Cela déclenchera automatiquement actualiser() dans la vue
            DataManager.getInstance().ajouterTache(t);

            // Reset du champ
            vue.getChampTitre().clear();
        } else {
            System.out.println("Erreur : Titre ou Date manquant");
        }
    }
}