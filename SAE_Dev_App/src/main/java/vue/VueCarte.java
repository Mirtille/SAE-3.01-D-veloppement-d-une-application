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

public class VueCarte extends VBox implements Observateur {

    private TacheAbstraite tache;
    private ControleurFX controleur;

    private Label lblTitre;
    private Label lblInfo;

    // Conteneurs pour la partie "Sous-tâches"
    private VBox containerSousTaches;
    private HBox zoneAjoutSousTache;

    public VueCarte(TacheAbstraite tache, ControleurFX controleur) {
        this.tache = tache;
        this.controleur = controleur;

        this.tache.enregistrerObservateur(this);

        // --- Style de la Carte ---
        this.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 8; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 3, 0, 0, 1);");
        this.setPadding(new Insets(10));
        this.setSpacing(8);

        // --- ENTÊTE (Titre + Actions Principales) ---
        lblTitre = new Label(tache.getTitre());
        lblTitre.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #172b4d;");
        lblTitre.setWrapText(true);
        lblTitre.setMaxWidth(Double.MAX_VALUE);

        Button btnModifier = new Button("✎");
        btnModifier.setStyle("-fx-background-color: transparent; -fx-text-fill: #5e6c84; -fx-cursor: hand;");
        btnModifier.setTooltip(new Tooltip("Modifier la tâche principale"));
        btnModifier.setOnAction(e -> ouvrirPopUpModification(this.tache));

        Button btnSuppr = new Button("×");
        btnSuppr.setStyle("-fx-background-color: transparent; -fx-text-fill: #eb5a46; -fx-font-weight: bold; -fx-cursor: hand;");
        btnSuppr.setTooltip(new Tooltip("Supprimer la tâche principale"));
        btnSuppr.setOnAction(e -> controleur.supprimerTache(tache));

        HBox actions = new HBox(btnModifier, btnSuppr);
        actions.setAlignment(Pos.TOP_RIGHT);

        HBox entete = new HBox(10);
        entete.setAlignment(Pos.TOP_LEFT);
        entete.getChildren().addAll(lblTitre, actions);
        HBox.setHgrow(lblTitre, Priority.ALWAYS);

        // --- INFOS (Date / Priorité) ---
        lblInfo = new Label();
        lblInfo.setStyle("-fx-text-fill: #5e6c84; -fx-font-size: 11px;");

        this.getChildren().addAll(entete, lblInfo);

        // --- SECTION SOUS-TÂCHES (Seulement si c'est une TacheMere) ---
        if (tache instanceof TacheMere) {
            Separator sep = new Separator();

            // Conteneur visuel des sous-tâches
            containerSousTaches = new VBox(4); // Espacement vertical entre les lignes
            containerSousTaches.setPadding(new Insets(5, 0, 5, 0));

            // Zone d'ajout rapide en bas
            TextField champAjout = new TextField();
            champAjout.setPromptText("Ajouter une étape...");
            champAjout.setStyle("-fx-font-size: 11px; -fx-background-color: #f4f5f7; -fx-background-radius: 3;");

            Button btnAjout = new Button("+");
            btnAjout.setStyle("-fx-font-size: 11px; -fx-background-radius: 3;");

            // Action Ajouter
            Runnable actionAjout = () -> {
                if (!champAjout.getText().trim().isEmpty()) {
                    controleur.creerSousTache((TacheMere) tache, champAjout.getText(), LocalDate.now(), Priorite.MOYENNE);
                    champAjout.clear();
                }
            };

            btnAjout.setOnAction(e -> actionAjout.run());
            champAjout.setOnAction(e -> actionAjout.run()); // Touche Entrée

            zoneAjoutSousTache = new HBox(5, champAjout, btnAjout);
            zoneAjoutSousTache.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(champAjout, Priority.ALWAYS);

            this.getChildren().addAll(sep, containerSousTaches, zoneAjoutSousTache);
        }

        mettreAJourAffichage();
    }

    @Override
    public void actualiser(Sujet s) {
        Platform.runLater(this::mettreAJourAffichage);
    }

    private void mettreAJourAffichage() {
        lblTitre.setText(tache.getTitre());
        lblInfo.setText(tache.getDateLimite() + " • " + tache.getPriorite());

        // Mise à jour de la liste des sous-tâches
        if (tache instanceof TacheMere && containerSousTaches != null) {
            containerSousTaches.getChildren().clear();
            TacheMere tm = (TacheMere) tache;

            for (TacheAbstraite sousTache : tm.getEnfants()) {
                HBox ligne = new HBox(8); // Espacement horizontal
                ligne.setAlignment(Pos.TOP_LEFT); // Alignement en haut pour gérer le multiline
                ligne.setStyle("-fx-padding: 4 0 4 0; -fx-border-color: transparent transparent #f0f0f0 transparent;");

                // Checkbox visuelle (Puce)
                Label puce = new Label("•");
                puce.setStyle("-fx-text-fill: #5e6c84; -fx-font-size: 14px; -fx-padding: -2 0 0 0;");

                // --- CONTENEUR TEXTE (Titre + Détails en dessous) ---
                VBox conteneurTexte = new VBox(2); // 2px d'espace entre titre et détails
                conteneurTexte.setAlignment(Pos.CENTER_LEFT);

                // Titre sous-tâche
                Label lblST = new Label(sousTache.getTitre());
                lblST.setStyle("-fx-font-size: 12px; -fx-text-fill: #172b4d;");
                lblST.setMaxWidth(Double.MAX_VALUE);
                lblST.setWrapText(true);

                // Détails sous-tâche (Date + Priorité)
                Label lblDetails = new Label(sousTache.getDateLimite() + " • " + sousTache.getPriorite());
                lblDetails.setStyle("-fx-font-size: 10px; -fx-text-fill: #97a0af;");

                conteneurTexte.getChildren().addAll(lblST, lblDetails);
                HBox.setHgrow(conteneurTexte, Priority.ALWAYS);

                // --- Boutons d'action ---
                VBox actionsBox = new VBox(0); // Empiler ou aligner les boutons
                actionsBox.setAlignment(Pos.CENTER);

                HBox btnGroup = new HBox(2);

                Button btnModifST = new Button("✎");
                btnModifST.setStyle("-fx-background-color: transparent; -fx-text-fill: #5e6c84; -fx-font-size: 11px; -fx-cursor: hand;");
                btnModifST.setTooltip(new Tooltip("Modifier"));
                btnModifST.setOnAction(e -> ouvrirPopUpModification(sousTache));

                Button btnDelST = new Button("×");
                btnDelST.setStyle("-fx-background-color: transparent; -fx-text-fill: #eb5a46; -fx-font-size: 14px; -fx-padding: 0 5 0 5; -fx-cursor: hand;");
                btnDelST.setTooltip(new Tooltip("Supprimer"));
                btnDelST.setOnAction(e -> controleur.supprimerTache(sousTache));

                btnGroup.getChildren().addAll(btnModifST, btnDelST);

                ligne.getChildren().addAll(puce, conteneurTexte, btnGroup);
                containerSousTaches.getChildren().add(ligne);
            }
        }
    }

    // --- POPUP MODIFICATION (Inchangé) ---
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