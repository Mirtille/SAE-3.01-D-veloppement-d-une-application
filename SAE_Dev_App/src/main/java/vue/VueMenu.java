package vue;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import modele.Projet;
import modele.SingletonTache;

public class VueMenu extends BorderPane {

    // On stocke les vues pour ne pas les recréer à chaque clic
    private VueKanban vueKanban;
    private VueListe vueListe;

    public VueMenu() {
        // 1. Récupération des données
        if (SingletonTache.getInstance().getMesProjets().isEmpty()) {
            // CORRECTION ICI : On crée l'objet Projet et on l'ajoute au Singleton
            Projet nouveauProjet = new Projet("Mon Projet");
            SingletonTache.getInstance().ajouterProjet(nouveauProjet);
        }

        // On récupère le premier projet de la liste
        Projet projetCourant = SingletonTache.getInstance().getMesProjets().get(0);

        // 2. Initialisation des sous-vues
        this.vueKanban = new VueKanban(projetCourant);
        this.vueListe = new VueListe();

        // 3. Création de la barre de menu (En Haut)
        HBox barreMenu = new HBox(15);
        barreMenu.setPadding(new Insets(10, 20, 10, 20));
        barreMenu.setStyle("-fx-background-color: #f4f5f7; -fx-border-color: #dfe1e6; -fx-border-width: 0 0 1 0;");
        barreMenu.setAlignment(Pos.CENTER_LEFT);

        Button btnKanban = new Button("Kanban");
        Button btnListe = new Button("Liste");

        // Style des boutons
        String styleBtn = "-fx-background-color: white; -fx-border-color: #c1c7d0; -fx-border-radius: 3; -fx-background-radius: 3; -fx-cursor: hand;";
        btnKanban.setStyle(styleBtn);
        btnListe.setStyle(styleBtn);

        // Actions : Quand on clique, on change le CENTRE de cette vue
        btnKanban.setOnAction(e -> this.setCenter(vueKanban));
        btnListe.setOnAction(e -> this.setCenter(vueListe));

        barreMenu.getChildren().addAll(btnKanban, btnListe);

        // 4. Assemblage final
        this.setTop(barreMenu);
        this.setCenter(vueKanban); // On affiche le Kanban par défaut
    }
}