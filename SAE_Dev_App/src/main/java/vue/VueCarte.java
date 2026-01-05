package vue;

import controleur.ControleurFX;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modele.Priorite;
import modele.TacheAbstraite;
import modele.TacheMere;
import observateur.Observateur;
import observateur.Sujet;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class VueCarte extends VBox implements Observateur {

    private TacheAbstraite tache;
    private ControleurFX controleur;

    private Label lblTitre;
    private Label lblInfo;
    private VBox containerSousTaches;

    private Set<TacheAbstraite> sousTachesObservees = new HashSet<>();

    public VueCarte(TacheAbstraite tache, ControleurFX controleur) {
        this.tache = tache;
        this.controleur = controleur;

        this.tache.enregistrerObservateur(this);

        // --- STYLE ---
        this.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 8; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 3, 0, 0, 1);");
        this.setPadding(new Insets(10));
        this.setSpacing(8);

        // --- DRAG SOURCE (Carte Principale) ---
        this.setOnDragDetected(event -> {
            ControleurFX.tacheEnDeplacement = this.tache;
            javafx.scene.input.Dragboard db = this.startDragAndDrop(javafx.scene.input.TransferMode.MOVE);
            javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(tache.getTitre());
            db.setContent(content);
            event.consume();
        });

        this.setOnDragDone(event -> {
            ControleurFX.tacheEnDeplacement = null;
            event.consume();
        });

        // --- DROP TARGET (Recevoir dans cette carte) ---
        if (tache instanceof TacheMere) {

            // 1. Drag Over : Accepter le survol
            this.setOnDragOver(event -> {
                TacheAbstraite source = ControleurFX.tacheEnDeplacement;
                // On accepte si ce n'est pas nous-même et si la source existe
                if (event.getDragboard().hasString() && source != null && source != this.tache) {
                    event.acceptTransferModes(javafx.scene.input.TransferMode.MOVE);
                    this.setStyle("-fx-background-color: #e2e4e6; -fx-background-radius: 8; -fx-border-color: #0079bf; -fx-border-width: 2;");
                }
                event.consume();
            });

            // 2. Drag Exited : Nettoyer le style
            this.setOnDragExited(event -> {
                this.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 3, 0, 0, 1);");
                event.consume();
            });

            // 3. Drag Dropped : Effectuer le déplacement
            this.setOnDragDropped(event -> {
                boolean success = false;
                TacheAbstraite source = ControleurFX.tacheEnDeplacement;
                if (source != null && source != this.tache) {
                    controleur.deplacerVersTacheMere(source, (TacheMere) this.tache);
                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();
            });
        }

        // --- ENTÊTE ---
        lblTitre = new Label(tache.getTitre());
        lblTitre.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #172b4d;");
        lblTitre.setWrapText(true);
        lblTitre.setMaxWidth(Double.MAX_VALUE);

        Button btnModifier = new Button("✎");
        btnModifier.setStyle("-fx-background-color: transparent; -fx-text-fill: #5e6c84; -fx-cursor: hand;");
        btnModifier.setOnAction(e -> ouvrirPopUpModification(this.tache));

        Button btnSuppr = new Button("×");
        btnSuppr.setStyle("-fx-background-color: transparent; -fx-text-fill: #eb5a46; -fx-font-weight: bold; -fx-cursor: hand;");
        btnSuppr.setOnAction(e -> controleur.supprimerTache(tache));

        HBox actions = new HBox(btnModifier, btnSuppr);
        actions.setAlignment(Pos.TOP_RIGHT);

        HBox entete = new HBox(10);
        entete.setAlignment(Pos.TOP_LEFT);
        entete.getChildren().addAll(lblTitre, actions);
        HBox.setHgrow(lblTitre, Priority.ALWAYS);

        // --- INFOS ---
        lblInfo = new Label();
        lblInfo.setStyle("-fx-text-fill: #5e6c84; -fx-font-size: 11px;");
        this.getChildren().addAll(entete, lblInfo);

        // --- INITIALISATION CONTENEUR SOUS-TÂCHES ---
        if (tache instanceof TacheMere) {
            Separator sep = new Separator();
            containerSousTaches = new VBox(4);
            containerSousTaches.setPadding(new Insets(5, 0, 5, 0));

            Button btnAjoutSousTache = new Button("+ Ajouter une sous-tâche");
            btnAjoutSousTache.setMaxWidth(Double.MAX_VALUE);
            btnAjoutSousTache.setStyle("-fx-background-color: #f4f5f7; -fx-text-fill: #172b4d; -fx-font-size: 11px; -fx-cursor: hand;");
            btnAjoutSousTache.setOnAction(e -> ouvrirDialogAjoutSousTache());

            this.getChildren().addAll(sep, containerSousTaches, btnAjoutSousTache);
        }

        mettreAJourAffichage();
    }

    public void detruire() {
        if (tache != null) tache.supprimerObservateur(this);
        for (TacheAbstraite st : sousTachesObservees) {
            st.supprimerObservateur(this);
        }
        sousTachesObservees.clear();
    }

    private void ouvrirDialogAjoutSousTache() {
        if (!(tache instanceof TacheMere)) return;
        TacheMere parent = (TacheMere) tache;
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle Sous-tâche");
        dialog.setHeaderText("Ajouter une étape à : " + parent.getTitre());
        TextField txtTitre = new TextField();
        txtTitre.setPromptText("Titre de l'étape...");
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
        Platform.runLater(txtTitre::requestFocus);
        dialog.showAndWait().ifPresent(type -> {
            if (type == ButtonType.OK && !txtTitre.getText().trim().isEmpty()) {
                controleur.creerSousTache(parent, txtTitre.getText(), datePicker.getValue(), comboPrio.getValue());
            }
        });
    }

    @Override
    public void actualiser(Sujet s) {
        Platform.runLater(this::mettreAJourAffichage);
    }

    private void mettreAJourAffichage() {
        lblTitre.setText(tache.getTitre());
        lblInfo.setText(tache.getDateLimite() + " • " + tache.getPriorite());

        for (TacheAbstraite st : sousTachesObservees) st.supprimerObservateur(this);
        sousTachesObservees.clear();

        if (tache instanceof TacheMere && containerSousTaches != null) {
            containerSousTaches.getChildren().clear();
            TacheMere tm = (TacheMere) tache;

            for (TacheAbstraite sousTache : tm.getEnfants()) {
                sousTache.enregistrerObservateur(this);
                sousTachesObservees.add(sousTache);

                HBox ligne = new HBox(8);
                ligne.setAlignment(Pos.TOP_LEFT);
                ligne.setStyle("-fx-padding: 4 0 4 0; -fx-border-color: transparent transparent #f0f0f0 transparent;");

                // ==========================================
                //  C'EST ICI : RENDRE LA SOUS-TÂCHE DÉPLAÇABLE
                // ==========================================
                ligne.setOnDragDetected(event -> {
                    ControleurFX.tacheEnDeplacement = sousTache;
                    javafx.scene.input.Dragboard db = ligne.startDragAndDrop(javafx.scene.input.TransferMode.MOVE);
                    javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
                    content.putString(sousTache.getTitre());
                    db.setContent(content);
                    event.consume();
                });

                ligne.setOnDragDone(event -> {
                    ControleurFX.tacheEnDeplacement = null;
                    event.consume();
                });
                // ==========================================

                Label puce = new Label("•");
                puce.setStyle("-fx-text-fill: #5e6c84; -fx-font-size: 14px; -fx-padding: -2 0 0 0;");

                VBox conteneurTexte = new VBox(2);
                conteneurTexte.setAlignment(Pos.CENTER_LEFT);

                Label lblST = new Label(sousTache.getTitre());
                lblST.setStyle("-fx-font-size: 12px; -fx-text-fill: #172b4d;");
                lblST.setMaxWidth(Double.MAX_VALUE);
                lblST.setWrapText(true);

                Label lblDetails = new Label(sousTache.getDateLimite() + " • " + sousTache.getPriorite());
                lblDetails.setStyle("-fx-font-size: 10px; -fx-text-fill: #97a0af;");

                conteneurTexte.getChildren().addAll(lblST, lblDetails);
                HBox.setHgrow(conteneurTexte, Priority.ALWAYS);

                HBox btnGroup = new HBox(2);
                Button btnModifST = new Button("✎");
                btnModifST.setStyle("-fx-background-color: transparent; -fx-text-fill: #5e6c84; -fx-font-size: 11px; -fx-cursor: hand;");
                btnModifST.setOnAction(e -> ouvrirPopUpModification(sousTache));

                Button btnDelST = new Button("×");
                btnDelST.setStyle("-fx-background-color: transparent; -fx-text-fill: #eb5a46; -fx-font-size: 14px; -fx-padding: 0 5 0 5; -fx-cursor: hand;");
                btnDelST.setOnAction(e -> controleur.supprimerTache(sousTache));

                btnGroup.getChildren().addAll(btnModifST, btnDelST);
                ligne.getChildren().addAll(puce, conteneurTexte, btnGroup);
                containerSousTaches.getChildren().add(ligne);
            }
        }
    }

    private void ouvrirPopUpModification(TacheAbstraite tacheCible) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Modifier");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(15);
        TextField champTitre = new TextField(tacheCible.getTitre());
        DatePicker champDate = new DatePicker(tacheCible.getDateLimite());
        ComboBox<Priorite> champPriorite = new ComboBox<>();
        champPriorite.getItems().setAll(Priorite.values());
        champPriorite.setValue(tacheCible.getPriorite());
        grid.add(new Label("Titre :"), 0, 0);
        grid.add(champTitre, 1, 0);
        grid.add(new Label("Date :"), 0, 1);
        grid.add(champDate, 1, 1);
        grid.add(new Label("Priorité :"), 0, 2);
        grid.add(champPriorite, 1, 2);
        Button btnValider = new Button("Enregistrer");
        btnValider.setDefaultButton(true);
        btnValider.setOnAction(e -> {
            controleur.modifierTache(tacheCible, champTitre.getText(), champDate.getValue(), champPriorite.getValue());
            popup.close();
        });
        grid.add(btnValider, 1, 3);
        popup.setScene(new Scene(grid, 350, 250));
        popup.showAndWait();
    }
}