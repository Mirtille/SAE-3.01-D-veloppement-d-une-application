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
import javafx.util.StringConverter;
import modele.*;
import observateur.Observateur;
import observateur.Sujet;

import java.time.LocalDate;

public class VueListe extends VBox implements Observateur {

    private ControleurFX controleur;

    // Sélecteur de Projet (Type Projet maintenant)
    private ComboBox<Projet> selecteurProjet;
    private Button btnNouveauProjet;

    private ListView<TacheAbstraite> listeTaches;
    private Projet projetEnCours; // Type Projet

    // Formulaire
    private TextField champTitre;
    private DatePicker datePicker;
    private ComboBox<Priorite> prioriteBox;

    public VueListe() {
        this.controleur = new ControleurFX();
        this.setPadding(new Insets(15));
        this.setSpacing(15);

        SingletonTache.getInstance().enregistrerObservateur(this);

        // --- BARRE DE PROJET ---
        Label labelProjet = new Label("Projet actuel :");
        selecteurProjet = new ComboBox<>();
        selecteurProjet.setMinWidth(200);

        // Convertisseur pour afficher le nom du projet proprement
        selecteurProjet.setConverter(new StringConverter<>() {
            @Override
            public String toString(Projet p) { return p == null ? "Aucun projet" : p.getNom(); }
            @Override
            public Projet fromString(String string) { return null; }
        });

        selecteurProjet.setOnAction(e -> changerProjet(selecteurProjet.getValue()));

        btnNouveauProjet = new Button("+ Nouveau Projet");
        btnNouveauProjet.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Nouveau Projet");
            dialog.setHeaderText("Nom du projet :");
            dialog.showAndWait().ifPresent(nom -> controleur.creerNouveauProjet(nom));
        });

        HBox barreProjet = new HBox(15, labelProjet, selecteurProjet, btnNouveauProjet);
        barreProjet.setAlignment(Pos.CENTER_LEFT);

        // --- LISTE ---
        listeTaches = new ListView<>();
        VBox.setVgrow(listeTaches, Priority.ALWAYS);

        // CellFactory pour l'affichage
        listeTaches.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(TacheAbstraite item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String icone = (item instanceof TacheMere) ? "📄" : "↳";
                    Label lblTitre = new Label(icone + " " + item.getTitre());
                    Label lblDetails = new Label(item.getDateLimite() + " (" + item.getPriorite() + ")");
                    lblDetails.setStyle("-fx-text-fill: gray; -fx-font-size: 11px;");

                    Button btnModif = new Button("✎");
                    btnModif.setOnAction(e -> ouvrirPopUpModification(item));

                    HBox ligne = new HBox(10, lblTitre, lblDetails, btnModif);
                    ligne.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(ligne);
                }
            }
        });

        // --- FORMULAIRE ---
        champTitre = new TextField();
        champTitre.setPromptText("Nouvelle tâche...");
        datePicker = new DatePicker(LocalDate.now());
        prioriteBox = new ComboBox<>();
        prioriteBox.getItems().addAll(Priorite.values());
        prioriteBox.setValue(Priorite.MOYENNE);

        Button btnAjouter = new Button("Ajouter");
        btnAjouter.setOnAction(e -> ajouterTacheDepuisFormulaire());

        HBox formulaire = new HBox(10, champTitre, datePicker, prioriteBox, btnAjouter);
        formulaire.setAlignment(Pos.CENTER_LEFT);

        this.getChildren().addAll(barreProjet, listeTaches, formulaire);

        rafraichirListeDesProjets();
    }

    private void ajouterTacheDepuisFormulaire() {
        if (projetEnCours == null || projetEnCours.getColonnes().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez créer un projet et au moins une colonne (via Kanban) avant d'ajouter des tâches.");
            alert.show();
            return;
        }
        // Par défaut, on ajoute dans la première colonne du projet
        Colonne premiereColonne = projetEnCours.getColonnes().get(0);

        controleur.creerTache(
                premiereColonne,
                champTitre.getText(),
                datePicker.getValue(),
                prioriteBox.getValue()
        );
        champTitre.clear();
    }

    private void changerProjet(Projet nouveauProjet) {
        if (nouveauProjet == null) return;
        if (this.projetEnCours != null) this.projetEnCours.supprimerObservateur(this);

        this.projetEnCours = nouveauProjet;
        this.projetEnCours.enregistrerObservateur(this);
        rafraichirListeTaches();
    }

    private void rafraichirListeDesProjets() {
        Projet selection = selecteurProjet.getValue();
        selecteurProjet.getItems().setAll(SingletonTache.getInstance().getMesProjets());
        if (selection != null && selecteurProjet.getItems().contains(selection)) {
            selecteurProjet.setValue(selection);
        } else if (!selecteurProjet.getItems().isEmpty()) {
            selecteurProjet.getSelectionModel().selectFirst();
        }
    }

    private void rafraichirListeTaches() {
        listeTaches.getItems().clear();
        if (projetEnCours != null) {
            // On récupère toutes les tâches de toutes les colonnes pour les afficher
            for (Colonne col : projetEnCours.getColonnes()) {
                listeTaches.getItems().addAll(col.getTaches());
            }
        }
    }

    @Override
    public void actualiser(Sujet s) {
        Platform.runLater(() -> {
            if (s instanceof SingletonTache) rafraichirListeDesProjets();
            else rafraichirListeTaches();
        });
    }

    private void ouvrirPopUpModification(TacheAbstraite tache) {
        // (Code identique à précédemment, inchangé)
        // ...
    }
}