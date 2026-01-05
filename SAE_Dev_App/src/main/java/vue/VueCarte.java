package vue;

import controleur.ControleurFX;
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
    private Label lblSousTaches;

    public VueCarte(TacheAbstraite tache, ControleurFX controleur) {
        this.tache = tache;
        this.controleur = controleur;

        this.tache.enregistrerObservateur(this);

        this.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 8; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 3, 0, 0, 1);");
        this.setPadding(new Insets(10));
        this.setSpacing(8);

        lblTitre = new Label(tache.getTitre());
        lblTitre.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #172b4d;");
        lblTitre.setWrapText(true);
        lblTitre.setMaxWidth(Double.MAX_VALUE);

        // Bouton Modifier
        Button btnModifier = new Button("modif ✎");
        btnModifier.setStyle("-fx-background-color: transparent; -fx-text-fill: #5e6c84; -fx-font-size: 14px; -fx-cursor: hand;");
        btnModifier.setTooltip(new Tooltip("Modifier"));
        btnModifier.setOnAction(e -> ouvrirPopUpModification(this.tache));

        // Bouton Supprimer
        Button btnSuppr = new Button("×");
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

        lblInfo = new Label();
        mettreAJourInfos();
        lblInfo.setStyle("-fx-text-fill: #5e6c84; -fx-font-size: 11px;");

        this.getChildren().addAll(entete, lblInfo);

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

    @Override
    public void actualiser(Sujet s) {
        // Correction : On s'assure d'être dans le thread JavaFX pour la mise à jour UI
        javafx.application.Platform.runLater(() -> {
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

    private void ouvrirFenetreSousTaches() {
        if (!(tache instanceof TacheMere)) return;
        TacheMere maTacheConteneur = (TacheMere) tache;

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Sous-tâches : " + tache.getTitre());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        ListView<TacheAbstraite> listeVisuelle = new ListView<>();

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

                    Button btnDel = new Button("x");
                    btnDel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    btnDel.setOnAction(e -> {
                        controleur.supprimerTache(item);
                        getListView().getItems().remove(item);
                        // On force la notification du parent
                        maTacheConteneur.notifierObservateurs();
                    });

                    Button btnModifier = new Button("mod ✎");
                    btnModifier.setStyle("-fx-background-color: transparent; -fx-text-fill: #5e6c84; -fx-font-size: 14px; -fx-cursor: hand;");

                    btnModifier.setOnAction(e -> ouvrirPopUpModification(item));

                    HBox cellLayout = new HBox(10, text, btnDel, btnModifier);
                    cellLayout.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(cellLayout);
                }
            }
        });

        listeVisuelle.getItems().addAll(maTacheConteneur.getEnfants());

        TextField champSousTache = new TextField();
        champSousTache.setPromptText("Nouvelle sous-tâche...");
        Button btnAjouter = new Button("Ajouter");
        btnAjouter.setDefaultButton(true);

        btnAjouter.setOnAction(e -> {
            // --- CORRECTION ICI ---
            // On utilise creerSousTache au lieu de creerTache
            // Et on retire le "false" à la fin
            controleur.creerSousTache(
                    maTacheConteneur,
                    champSousTache.getText(),
                    LocalDate.now(),
                    Priorite.MOYENNE
            );

            listeVisuelle.getItems().clear();
            listeVisuelle.getItems().addAll(maTacheConteneur.getEnfants());
            champSousTache.clear();

            // Mise à jour visuelle de la carte parente
            actualiser(tache);
        });

        HBox ajoutBox = new HBox(10, champSousTache, btnAjouter);
        layout.getChildren().addAll(new Label("Liste des tâches :"), listeVisuelle, ajoutBox);

        popup.setScene(new Scene(layout, 350, 450));
        popup.showAndWait();
    }
}