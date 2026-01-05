package vue;

import controleur.ControleurFX;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import modele.Colonne;
import modele.Priorite;
import modele.TacheMere;
import observateur.Observateur;
import observateur.Sujet;

import java.time.LocalDate;

public class VueColonne extends VBox implements Observateur {

    private Colonne colonne;
    private ControleurFX controleur;
    private VBox containerCartes;

    public VueColonne(Colonne colonne) {
        this.colonne = colonne;
        // On initialise le contrôleur ici pour pouvoir le passer aux cartes
        this.controleur = new ControleurFX();

        this.colonne.enregistrerObservateur(this);

        // --- Style de la Colonne ---
        this.setMinWidth(280);
        this.setMaxWidth(280);
        this.setStyle("-fx-background-color: #ebecf0; -fx-background-radius: 8;");
        this.setPadding(new Insets(10));
        this.setSpacing(10);

        // Titre de la colonne
        Label lblTitre = new Label(colonne.getNom());
        lblTitre.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #172b4d;");

        containerCartes = new VBox(10);
        ScrollPane scroll = new ScrollPane(containerCartes);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        Button btnAjouterCarte = new Button("+ Ajouter une tâche");
        btnAjouterCarte.setMaxWidth(Double.MAX_VALUE);

        btnAjouterCarte.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Nouvelle Carte");
            dialog.setHeaderText("Ajouter une tâche dans : " + colonne.getNom());
            dialog.setContentText("Titre :");

            dialog.showAndWait().ifPresent(titre -> {
                if (!titre.trim().isEmpty()) {
                    controleur.creerTache(
                            colonne,
                            titre,
                            LocalDate.now(),
                            Priorite.MOYENNE
                    );
                }
            });
        });

        this.getChildren().addAll(lblTitre, scroll, btnAjouterCarte);

        rafraichir();
    }

    private void rafraichir() {
        containerCartes.getChildren().clear();

        // Correction ici : On passe 't' (la tâche) ET 'controleur'
        for (TacheMere t : colonne.getTaches()) {
            containerCartes.getChildren().add(new VueCarte(t, controleur));
        }
    }

    @Override
    public void actualiser(Sujet s) {
        rafraichir();
    }
}