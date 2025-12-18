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

    // Sélecteur de Projet
    private ComboBox<TacheMere> selecteurProjet;
    private Button btnNouveauProjet;

    // La liste des tâches
    private ListView<TacheAbstraite> listeTaches;

    // Le projet actuellement affiché
    private TacheMere projetEnCours;

    // Formulaire d'ajout rapide
    private TextField champTitre;
    private DatePicker datePicker;
    private ComboBox<Priorite> prioriteBox;
    private CheckBox checkDossier;

    public VueListe() {
        this.controleur = new ControleurFX();

        // --- STYLE CSS ---
        // On applique le style global si tu as une classe .root ou .vue-liste
        this.setPadding(new Insets(15));
        this.setSpacing(15);
        // Optionnel : this.getStyleClass().add("vue-liste");

        // 1. Abonnement au Singleton (pour savoir quand on ajoute un PROJET)
        SingletonTache.getInstance().enregistrerObservateur(this);

        // --- ZONE HAUTE : SÉLECTION DE PROJET ---
        Label labelProjet = new Label("Projet actuel :");
        labelProjet.setStyle("-fx-font-weight: bold;");

        selecteurProjet = new ComboBox<>();
        selecteurProjet.setMinWidth(200);

        // Convertisseur pour afficher le titre du projet au lieu de l'objet
        selecteurProjet.setConverter(new StringConverter<>() {
            @Override
            public String toString(TacheMere t) { return t == null ? "Aucun projet" : t.getTitre(); }
            @Override
            public TacheMere fromString(String string) { return null; }
        });

        selecteurProjet.setOnAction(e -> changerProjet(selecteurProjet.getValue()));

        btnNouveauProjet = new Button("+ Nouveau Projet");
        btnNouveauProjet.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Nouveau Projet");
            dialog.setHeaderText("Créer une nouvelle liste de tâches");
            dialog.setContentText("Nom du projet :");
            dialog.showAndWait().ifPresent(nom -> controleur.creerNouveauProjet(nom));
        });

        HBox barreProjet = new HBox(15, labelProjet, selecteurProjet, btnNouveauProjet);
        barreProjet.setAlignment(Pos.CENTER_LEFT);
        barreProjet.setStyle("-fx-background-color: #f4f5f7; -fx-padding: 10; -fx-background-radius: 5;");

        // --- ZONE CENTRALE : LA LISTE INTELLIGENTE ---
        listeTaches = new ListView<>();
        VBox.setVgrow(listeTaches, Priority.ALWAYS);

        // C'EST ICI QUE LA MAGIE OPÈRE (CellFactory)
        listeTaches.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(TacheAbstraite item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // 1. L'icône (Dossier ou Tâche)
                    String icone = (item instanceof TacheMere) ? "📂" : "📄";

                    // 2. Les infos textuelles
                    Label lblTitre = new Label(icone + " " + item.getTitre());
                    lblTitre.getStyleClass().add("titre-carte"); // Utilise ton CSS
                    lblTitre.setMaxWidth(Double.MAX_VALUE);
                    HBox.setHgrow(lblTitre, Priority.ALWAYS);

                    Label lblDetails = new Label(item.getDateLimite() + " (" + item.getPriorite() + ")");
                    lblDetails.setStyle("-fx-text-fill: gray; -fx-font-size: 11px;");

                    // 3. Les Boutons d'action
                    Button btnModif = new Button("✎");
                    btnModif.getStyleClass().add("bouton-modifier"); // CSS
                    btnModif.setOnAction(e -> ouvrirPopUpModification(item));

                    Button btnSuppr = new Button("×");
                    btnSuppr.getStyleClass().add("bouton-supprimer"); // CSS
                    btnSuppr.setOnAction(e -> {
                        controleur.supprimerTache(item);
                        // Pas besoin de rafraichir manuellement ici, l'observer le fera
                    });

                    // 4. Assemblage
                    HBox ligne = new HBox(10, lblTitre, lblDetails, btnModif, btnSuppr);
                    ligne.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(ligne);
                }
            }
        });

        // --- ZONE BASSE : FORMULAIRE AJOUT RAPIDE ---
        champTitre = new TextField();
        champTitre.setPromptText("Nouvelle tâche...");

        datePicker = new DatePicker(LocalDate.now());
        datePicker.setPrefWidth(120);

        prioriteBox = new ComboBox<>();
        prioriteBox.getItems().addAll(Priorite.values());
        prioriteBox.setValue(Priorite.MOYENNE);

        checkDossier = new CheckBox("Est un dossier ?");

        Button btnAjouter = new Button("Ajouter");
        btnAjouter.setDefaultButton(true); // Entrée valide le formulaire
        btnAjouter.setOnAction(e -> {
            controleur.creerTache(
                    projetEnCours,
                    champTitre.getText(),
                    datePicker.getValue(),
                    prioriteBox.getValue(),
                    checkDossier.isSelected()
            );
            champTitre.clear();
            checkDossier.setSelected(false);
        });

        HBox formulaire = new HBox(10, champTitre, datePicker, prioriteBox, checkDossier, btnAjouter);
        formulaire.setAlignment(Pos.CENTER_LEFT);
        formulaire.setPadding(new Insets(10, 0, 0, 0));
        HBox.setHgrow(champTitre, Priority.ALWAYS);

        this.getChildren().addAll(barreProjet, listeTaches, formulaire);

        // Initialisation
        rafraichirListeDesProjets();
        if (!selecteurProjet.getItems().isEmpty()) {
            selecteurProjet.getSelectionModel().selectFirst();
        }
    }

    // --- LOGIQUE METIER ---

    private void changerProjet(TacheMere nouveauProjet) {
        if (nouveauProjet == null) return;

        // Désabonnement ancien
        if (this.projetEnCours != null) {
            this.projetEnCours.supprimerObservateur(this);
        }

        // Abonnement nouveau
        this.projetEnCours = nouveauProjet;
        this.projetEnCours.enregistrerObservateur(this);

        rafraichirListeTaches();
    }

    private void rafraichirListeDesProjets() {
        TacheMere selection = selecteurProjet.getValue();
        selecteurProjet.getItems().clear();
        selecteurProjet.getItems().addAll(SingletonTache.getInstance().getMesProjets());

        if (selection != null && selecteurProjet.getItems().contains(selection)) {
            selecteurProjet.setValue(selection);
        } else if (!selecteurProjet.getItems().isEmpty()) {
            selecteurProjet.getSelectionModel().selectFirst();
        }
    }

    private void rafraichirListeTaches() {
        listeTaches.getItems().clear();
        if (projetEnCours != null) {
            listeTaches.getItems().addAll(projetEnCours.getEnfants());
        }
    }

    @Override
    public void actualiser(Sujet s) {
        Platform.runLater(() -> {
            if (s instanceof SingletonTache) {
                rafraichirListeDesProjets();
            } else if (s == projetEnCours) {
                rafraichirListeTaches();
            }
        });
    }

    // --- POPUP MODIFICATION (Similaire à VueCarte) ---
    private void ouvrirPopUpModification(TacheAbstraite tacheCible) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Modifier : " + tacheCible.getTitre());

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(15);

        TextField txtTitre = new TextField(tacheCible.getTitre());
        DatePicker txtDate = new DatePicker(tacheCible.getDateLimite());
        ComboBox<Priorite> txtPrio = new ComboBox<>();
        txtPrio.getItems().setAll(Priorite.values());
        txtPrio.setValue(tacheCible.getPriorite());

        grid.add(new Label("Titre :"), 0, 0);
        grid.add(txtTitre, 1, 0);
        grid.add(new Label("Date :"), 0, 1);
        grid.add(txtDate, 1, 1);
        grid.add(new Label("Priorité :"), 0, 2);
        grid.add(txtPrio, 1, 2);

        Button btnSave = new Button("Enregistrer");
        btnSave.setOnAction(e -> {
            controleur.modifierTache(tacheCible, txtTitre.getText(), txtDate.getValue(), txtPrio.getValue());
            popup.close();
            // Le rafraichissement se fera via l'observer actualiser()
        });

        grid.add(btnSave, 1, 3);
        popup.setScene(new Scene(grid, 300, 200));
        popup.showAndWait();
    }
}