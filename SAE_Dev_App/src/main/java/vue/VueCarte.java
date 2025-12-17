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

    // Éléments graphiques à mettre à jour
    private Label lblTitre;
    private Label lblInfo;
    private Label lblSousTaches;

    public VueCarte(TacheAbstraite tache, ControleurFX controleur) {
        this.tache = tache;
        this.controleur = controleur;

        // ABONNEMENT INDISPENSABLE
        this.tache.enregistrerObservateur(this);

        // --- STYLE ---
        this.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 8; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 3, 0, 0, 1);");
        this.setPadding(new Insets(10));
        this.setSpacing(8);

        // --- ENTÊTE ---
        lblTitre = new Label(tache.getTitre());
        lblTitre.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #172b4d;");
        lblTitre.setWrapText(true);
        lblTitre.setMaxWidth(Double.MAX_VALUE);

        // Bouton Modifier
        Button btnModifier = new Button("✎");
        btnModifier.setStyle("-fx-background-color: transparent; -fx-text-fill: #5e6c84; -fx-font-size: 14px; -fx-cursor: hand;");
        btnModifier.setTooltip(new Tooltip("Modifier"));
        btnModifier.setOnAction(e -> ouvrirPopUpModification());

        // Bouton Supprimer
        Button btnSuppr = new Button("supprimer ×");
        btnSuppr.setStyle("-fx-background-color: transparent; -fx-text-fill: #eb5a46; -fx-font-size: 18px; -fx-cursor: hand; -fx-padding: 0 5 0 5;");
        btnSuppr.setTooltip(new Tooltip("Supprimer"));
        btnSuppr.setOnAction(e -> controleur.supprimerTache(tache));

        HBox actions = new HBox(0, btnModifier, btnSuppr);
        actions.setAlignment(Pos.TOP_RIGHT);
        actions.setMinWidth(60);

        HBox entete = new HBox(10);
        entete.setAlignment(Pos.TOP_LEFT);
        entete.getChildren().addAll(lblTitre, actions);
        HBox.setHgrow(lblTitre, Priority.ALWAYS);

        // --- INFOS ---
        lblInfo = new Label();
        mettreAJourInfos();
        lblInfo.setStyle("-fx-text-fill: #5e6c84; -fx-font-size: 11px;");

        this.getChildren().addAll(entete, lblInfo);

        // --- SOUS-TÂCHES (Seulement si c'est un dossier) ---
        if (tache instanceof TacheMere) {
            lblSousTaches = new Label();
            mettreAJourCompteurSousTaches();
            lblSousTaches.setStyle("-fx-text-fill: #172b4d; -fx-font-size: 11px; -fx-font-style: italic;");

            Button btnVoirSousTaches = new Button("Gérer sous-tâches");
            btnVoirSousTaches.setMaxWidth(Double.MAX_VALUE);
            btnVoirSousTaches.setStyle("-fx-background-color: #f4f5f7; -fx-text-fill: #172b4d; -fx-cursor: hand;");
            btnVoirSousTaches.setOnAction(e -> ouvrirFenetreSousTaches());

            this.getChildren().addAll(lblSousTaches, btnVoirSousTaches);
        }
    }

    // --- MISE A JOUR (Observer) ---
    @Override
    public void actualiser(Sujet s) {
        // Platform.runLater force la mise à jour sur le Thread graphique (Règle le bug d'affichage)
        Platform.runLater(() -> {
            lblTitre.setText(tache.getTitre());
            mettreAJourInfos();
            if (tache instanceof TacheMere) {
                mettreAJourCompteurSousTaches();
            }
        });
    }

    private void mettreAJourInfos() {
        lblInfo.setText(tache.getDateLimite() + " • " + tache.getPriorite());
    }

    private void mettreAJourCompteurSousTaches() {
        if (lblSousTaches != null && tache instanceof TacheMere) {
            int nb = ((TacheMere) tache).getEnfants().size();
            lblSousTaches.setText(nb + " sous-tâche(s)");
        }
    }

    // --- POPUP MODIFICATION ---
    private void ouvrirPopUpModification() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Modifier");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(15);

        TextField champTitre = new TextField(tache.getTitre());
        DatePicker champDate = new DatePicker(tache.getDateLimite());
        ComboBox<Priorite> champPriorite = new ComboBox<>();
        champPriorite.getItems().setAll(Priorite.values());
        champPriorite.setValue(tache.getPriorite());

        grid.add(new Label("Titre :"), 0, 0);
        grid.add(champTitre, 1, 0);
        grid.add(new Label("Date :"), 0, 1);
        grid.add(champDate, 1, 1);
        grid.add(new Label("Priorité :"), 0, 2);
        grid.add(champPriorite, 1, 2);

        Button btnValider = new Button("Enregistrer");
        btnValider.setDefaultButton(true);
        btnValider.setOnAction(e -> {
            controleur.modifierTache(tache, champTitre.getText(), champDate.getValue(), champPriorite.getValue());
            popup.close();
        });

        grid.add(btnValider, 1, 3);
        popup.setScene(new Scene(grid, 350, 250));
        popup.showAndWait();
    }

    // --- POPUP SOUS-TÂCHES (CORRIGÉ POUR SUPPRESSION) ---
    private void ouvrirFenetreSousTaches() {
        if (!(tache instanceof TacheMere)) return;
        TacheMere maTacheConteneur = (TacheMere) tache;

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Sous-tâches : " + tache.getTitre());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        // NOUVEAU : Une liste d'OBJETS TacheAbstraite, pas de String
        ListView<TacheAbstraite> listeVisuelle = new ListView<>();

        // Affichage personnalisé : Texte + Bouton Supprimer
        listeVisuelle.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(TacheAbstraite item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label text = new Label(item.getTitre() + " (" + item.getPriorite() + ")");
                    HBox.setHgrow(text, Priority.ALWAYS);
                    text.setMaxWidth(Double.MAX_VALUE);

                    Button btnDel = new Button("supprimer x");
                    btnDel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    btnDel.setOnAction(e -> {
                        // Action de suppression
                        controleur.supprimerTache(item);
                        // On rafraichit la liste visuelle immédiatement
                        getListView().getItems().remove(item);
                        actualiser(tache); // Met à jour le compteur sur la carte parente
                    });

                    HBox cellLayout = new HBox(10, text, btnDel);
                    cellLayout.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(cellLayout);
                }
            }
        });

        // Chargement initial
        listeVisuelle.getItems().addAll(maTacheConteneur.getEnfants());

        // Formulaire Ajout
        TextField champSousTache = new TextField();
        champSousTache.setPromptText("Nouvelle sous-tâche...");
        Button btnAjouter = new Button("Ajouter");
        btnAjouter.setDefaultButton(true);

        btnAjouter.setOnAction(e -> {
            controleur.creerTache(maTacheConteneur, champSousTache.getText(), LocalDate.now(), Priorite.MOYENNE, false);
            // On rafraichit la liste visuelle en rechargeant tout depuis le modèle
            listeVisuelle.getItems().clear();
            listeVisuelle.getItems().addAll(maTacheConteneur.getEnfants());
            champSousTache.clear();
            actualiser(tache);
        });

        HBox ajoutBox = new HBox(10, champSousTache, btnAjouter);
        layout.getChildren().addAll(new Label("Liste des tâches :"), listeVisuelle, ajoutBox);

        popup.setScene(new Scene(layout, 350, 450));
        popup.showAndWait();
    }
}