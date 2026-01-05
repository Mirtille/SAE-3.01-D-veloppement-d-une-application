package vue;

import controleur.ControleurFX;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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
        this.controleur = new ControleurFX();

        this.colonne.enregistrerObservateur(this);

        // --- Style de la Colonne ---
        this.setMinWidth(280);
        this.setMaxWidth(280);
        this.setStyle("-fx-background-color: #ebecf0; -fx-background-radius: 8;");
        this.setPadding(new Insets(10));
        this.setSpacing(10);

        // --- EN-TÊTE (Titre + Bouton Supprimer) ---
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label lblTitre = new Label(colonne.getNom());
        lblTitre.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #172b4d;");

        // Espace flexible pour pousser le bouton à droite
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Bouton supprimer la colonne
        Button btnSupprColonne = new Button("×");
        btnSupprColonne.setStyle("-fx-background-color: transparent; -fx-text-fill: #6b778c; -fx-font-size: 16px; -fx-cursor: hand; -fx-font-weight: bold;");
        btnSupprColonne.setTooltip(new Tooltip("Supprimer la liste"));

        // ACTION SUPPRESSION
        btnSupprColonne.setOnAction(e -> {
            // Petite confirmation (optionnel mais conseillé)
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer la liste '" + colonne.getNom() + "' et toutes ses tâches ?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    controleur.supprimerColonne(colonne);
                }
            });
        });

        header.getChildren().addAll(lblTitre, spacer, btnSupprColonne);

        // --- CONTENU DES CARTES ---
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

        // On ajoute le header au lieu de juste le label
        this.getChildren().addAll(header, scroll, btnAjouterCarte);

        rafraichir();
    }

    private void rafraichir() {
        containerCartes.getChildren().clear();
        for (TacheMere t : colonne.getTaches()) {
            containerCartes.getChildren().add(new VueCarte(t, controleur));
        }
    }

    @Override
    public void actualiser(Sujet s) {
        rafraichir();
    }
}