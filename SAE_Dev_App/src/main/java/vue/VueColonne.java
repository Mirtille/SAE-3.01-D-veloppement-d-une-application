package vue;

import controleur.ControleurFX;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import modele.Colonne;
import modele.Priorite;
import modele.TacheAbstraite;
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

        // --- Style ---
        this.setMinWidth(280);
        this.setMaxWidth(280);
        this.setStyle("-fx-background-color: #ebecf0; -fx-background-radius: 8;");
        this.setPadding(new Insets(10));
        this.setSpacing(10);

        // --- EN-TÊTE ---
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label lblTitre = new Label(colonne.getNom());
        lblTitre.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #172b4d;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnSupprColonne = new Button("×");
        btnSupprColonne.setStyle("-fx-background-color: transparent; -fx-text-fill: #6b778c; -fx-font-size: 16px; -fx-cursor: hand; -fx-font-weight: bold;");
        btnSupprColonne.setTooltip(new Tooltip("Supprimer la liste"));
        btnSupprColonne.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer la liste '" + colonne.getNom() + "' et toutes ses tâches ?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) controleur.supprimerColonne(colonne);
            });
        });

        header.getChildren().addAll(lblTitre, spacer, btnSupprColonne);

        // --- CARTES ---
        containerCartes = new VBox(10);
        ScrollPane scroll = new ScrollPane(containerCartes);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        Button btnAjouterCarte = new Button("+ Ajouter une tâche");
        btnAjouterCarte.setMaxWidth(Double.MAX_VALUE);
        btnAjouterCarte.setOnAction(e -> ouvrirDialogAjoutTache());

        this.getChildren().addAll(header, scroll, btnAjouterCarte);

        //DRAG & DROP
        this.setOnDragOver(event -> {
            // Si une tâche est en déplacement, on accepte le survol
            if (event.getDragboard().hasString() && ControleurFX.tacheEnDeplacement != null) {
                event.acceptTransferModes(javafx.scene.input.TransferMode.MOVE);
            }
            event.consume();
        });

        this.setOnDragDropped(event -> {
            boolean success = false;
            TacheAbstraite tachePosee = ControleurFX.tacheEnDeplacement;

            if (tachePosee != null) {
                // Appel au contrôleur pour déplacer la tâche ici
                controleur.deplacerTache(tachePosee, this.colonne);
                success = true;
            }

            event.setDropCompleted(success);
            event.consume();
        });

        rafraichir();
    }

    private void rafraichir() {
        // Nettoyage des anciennes cartes
        for (javafx.scene.Node node : containerCartes.getChildren()) {
            if (node instanceof VueCarte) {
                ((VueCarte) node).detruire();
            }
        }

        containerCartes.getChildren().clear();
        for (TacheMere t : colonne.getTaches()) {
            containerCartes.getChildren().add(new VueCarte(t, controleur));
        }
    }

    private void ouvrirDialogAjoutTache() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle Tâche");
        dialog.setHeaderText("Créer une tâche dans : " + colonne.getNom());

        TextField txtTitre = new TextField();
        txtTitre.setPromptText("Titre de la tâche...");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        ComboBox<Priorite> comboPrio = new ComboBox<>();
        comboPrio.getItems().setAll(Priorite.values());
        comboPrio.setValue(Priorite.MOYENNE);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Titre :"), 0, 0);
        grid.add(txtTitre, 1, 0);
        grid.add(new Label("Date limite :"), 0, 1);
        grid.add(datePicker, 1, 1);
        grid.add(new Label("Priorité :"), 0, 2);
        grid.add(comboPrio, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        javafx.application.Platform.runLater(txtTitre::requestFocus);

        dialog.showAndWait().ifPresent(type -> {
            if (type == ButtonType.OK && !txtTitre.getText().trim().isEmpty()) {
                controleur.creerTache(colonne, txtTitre.getText(), datePicker.getValue(), comboPrio.getValue());
            }
        });
    }

    @Override
    public void actualiser(Sujet s) {
        rafraichir();
    }
}