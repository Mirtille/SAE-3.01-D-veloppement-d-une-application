package vue;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import modele.Projet;
import modele.SingletonTache;

public class VueMenu extends BorderPane {

    private VueKanban vueKanban;
    private VueListe vueListe;
    private VueGantt vueGantt; // NOUVEAU

    public VueMenu() {

        if (SingletonTache.getInstance().getMesProjets().isEmpty()) {
            Projet nouveauProjet = new Projet("Projet");
            SingletonTache.getInstance().ajouterProjet(nouveauProjet);
        }

        Projet projetCourant = SingletonTache.getInstance().getMesProjets().get(0);

        this.vueKanban = new VueKanban();
        this.vueListe = new VueListe();
        this.vueGantt = new VueGantt(projetCourant); // NOUVEAU

        HBox barreMenu = new HBox(15);
        barreMenu.setPadding(new Insets(10, 20, 10, 20));
        barreMenu.setStyle("-fx-background-color: #f4f5f7; -fx-border-color: #dfe1e6; -fx-border-width: 0 0 1 0;");
        barreMenu.setAlignment(Pos.CENTER_LEFT);

        Button btnKanban = new Button("Kanban");
        Button btnListe = new Button("Liste");
        Button btnGantt = new Button("Gantt"); // NOUVEAU

        String styleBtn = "-fx-background-color: white; -fx-border-color: #c1c7d0; -fx-border-radius: 3; -fx-background-radius: 3; -fx-cursor: hand;";
        btnKanban.setStyle(styleBtn);
        btnListe.setStyle(styleBtn);
        btnGantt.setStyle(styleBtn);

        btnKanban.setOnAction(e -> this.setCenter(vueKanban));
        btnListe.setOnAction(e -> this.setCenter(vueListe));
        btnGantt.setOnAction(e -> this.setCenter(vueGantt)); // NOUVEAU

        barreMenu.getChildren().addAll(btnKanban, btnListe, btnGantt);

        this.setTop(barreMenu);
        this.setCenter(vueKanban);
    }
}