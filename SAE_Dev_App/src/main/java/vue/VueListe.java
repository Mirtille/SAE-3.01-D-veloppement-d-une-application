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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import modele.*;
import observateur.Observateur;
import observateur.Sujet;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class VueListe extends VBox implements Observateur {

    private ControleurFX controleur;

    // --- Composants graphiques ---
    private ComboBox<Projet> selecteurProjet;
    private ScrollPane scrollPane;
    private VBox containerDates;
    private CheckBox checkAfficherSousTaches;

    // --- Données ---
    private Projet projetEnCours;
    private List<Colonne> colonnesObservees = new ArrayList<>();

    // --- Formulaire ajout ---
    private TextField champTitre;
    private DatePicker datePicker;
    private ComboBox<Priorite> prioriteBox;

    public VueListe() {
        this.controleur = new ControleurFX();
        this.setPadding(new Insets(15));
        this.setSpacing(15);
        this.setStyle("-fx-background-color: #ffffff;");

        SingletonTache.getInstance().enregistrerObservateur(this);

        // ============================================
        // 1. BARRE D'OUTILS
        // ============================================
        Label labelProjet = new Label("Projet :");
        labelProjet.setStyle("-fx-font-weight: bold; -fx-text-fill: #172b4d;");

        selecteurProjet = new ComboBox<>();
        selecteurProjet.setMinWidth(150);
        selecteurProjet.setConverter(new StringConverter<>() {
            @Override public String toString(Projet p) { return p == null ? "Aucun" : p.getNom(); }
            @Override public Projet fromString(String s) { return null; }
        });
        selecteurProjet.setOnAction(e -> changerProjet(selecteurProjet.getValue()));

        Button btnNouveauProjet = new Button("+");
        btnNouveauProjet.setTooltip(new Tooltip("Créer un nouveau projet"));
        btnNouveauProjet.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Nouveau Projet");
            dialog.setHeaderText("Créer un nouveau projet");
            dialog.setContentText("Nom :");
            dialog.showAndWait().ifPresent(nom -> controleur.creerNouveauProjet(nom));
        });

        checkAfficherSousTaches = new CheckBox("Voir sous-tâches");
        checkAfficherSousTaches.setStyle("-fx-text-fill: #172b4d;");
        checkAfficherSousTaches.setSelected(true);
        checkAfficherSousTaches.setOnAction(e -> rafraichirContenu());

        HBox barreOutils = new HBox(10, labelProjet, selecteurProjet, btnNouveauProjet, new Separator(javafx.geometry.Orientation.VERTICAL), checkAfficherSousTaches);
        barreOutils.setAlignment(Pos.CENTER_LEFT);

        // ============================================
        // 2. CONTENU (Menus dépliants)
        // ============================================
        containerDates = new VBox(10);
        containerDates.setPadding(new Insets(10));

        scrollPane = new ScrollPane(containerDates);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // ============================================
        // 3. FORMULAIRE D'AJOUT RAPIDE
        // ============================================
        champTitre = new TextField();
        champTitre.setPromptText("Nouvelle tâche...");
        HBox.setHgrow(champTitre, Priority.ALWAYS);

        datePicker = new DatePicker(LocalDate.now());
        datePicker.setPrefWidth(110);

        prioriteBox = new ComboBox<>();
        prioriteBox.getItems().addAll(Priorite.values());
        prioriteBox.setValue(Priorite.MOYENNE);
        prioriteBox.setPrefWidth(100);

        Button btnAjouter = new Button("Ajouter");
        btnAjouter.setStyle("-fx-background-color: #0079bf; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAjouter.setOnAction(e -> ajouterTache());

        HBox formulaire = new HBox(10, champTitre, datePicker, prioriteBox, btnAjouter);
        formulaire.setAlignment(Pos.CENTER_LEFT);
        formulaire.setPadding(new Insets(10, 0, 0, 0));

        this.getChildren().addAll(barreOutils, scrollPane, formulaire);

        rafraichirListeDesProjets();
    }

    // --- GESTION ---

    private void changerProjet(Projet p) {
        if (this.projetEnCours != null) {
            this.projetEnCours.supprimerObservateur(this);
            for (Colonne col : colonnesObservees) col.supprimerObservateur(this);
            colonnesObservees.clear();
        }
        this.projetEnCours = p;
        if (this.projetEnCours != null) {
            this.projetEnCours.enregistrerObservateur(this);
            for (Colonne col : this.projetEnCours.getColonnes()) {
                col.enregistrerObservateur(this);
                colonnesObservees.add(col);
            }
        }
        rafraichirContenu();
    }

    private void rafraichirContenu() {
        containerDates.getChildren().clear();
        if (projetEnCours == null) return;

        List<TacheMere> toutesLesTaches = new ArrayList<>();
        for (Colonne col : projetEnCours.getColonnes()) {
            toutesLesTaches.addAll(col.getTaches());
        }

        if (toutesLesTaches.isEmpty()) {
            Label lblVide = new Label("Aucune tâche dans ce projet.");
            lblVide.setStyle("-fx-text-fill: #6b778c; -fx-font-style: italic;");
            containerDates.getChildren().add(lblVide);
            return;
        }

        Map<LocalDate, List<TacheMere>> mapParDate = toutesLesTaches.stream()
                .collect(Collectors.groupingBy(TacheAbstraite::getDateLimite));

        List<LocalDate> datesTriees = new ArrayList<>(mapParDate.keySet());
        Collections.sort(datesTriees);

        for (LocalDate date : datesTriees) {
            List<TacheMere> tachesDuJour = mapParDate.get(date);
            String titreDate = date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH));

            VBox contenuDate = new VBox(5);
            contenuDate.setPadding(new Insets(10));
            // Fond blanc pour le contenu du menu
            contenuDate.setStyle("-fx-background-color: white;");

            for (TacheMere t : tachesDuJour) {
                construireLigneTacheRecursive(t, contenuDate, 0);
            }

            TitledPane menuDepliant = new TitledPane(titreDate + " (" + tachesDuJour.size() + ")", contenuDate);
            menuDepliant.setStyle("-fx-text-fill: #172b4d;"); // Couleur du titre du menu
            menuDepliant.setExpanded(true);
            containerDates.getChildren().add(menuDepliant);
        }
    }

    private void construireLigneTacheRecursive(TacheAbstraite tache, VBox container, int niveau) {
        HBox ligne = new HBox(10);
        ligne.setAlignment(Pos.CENTER_LEFT);
        ligne.setPadding(new Insets(2, 0, 2, niveau * 25));

        Circle puce = new Circle(4);
        switch (tache.getPriorite()) {
            case HAUTE -> puce.setFill(Color.RED);
            case MOYENNE -> puce.setFill(Color.ORANGE);
            case BASSE -> puce.setFill(Color.GREEN);
        }

        Label lblTitre = new Label(tache.getTitre());
        // CORRECTION VISUELLE : On force la couleur noire pour éviter le blanc sur blanc
        if (niveau == 0) lblTitre.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #172b4d;");
        else lblTitre.setStyle("-fx-font-size: 13px; -fx-text-fill: #172b4d;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnEdit = new Button("✎");
        btnEdit.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: #5e6c84;");
        btnEdit.setOnAction(e -> ouvrirPopUpModification(tache));

        Button btnSuppr = new Button("×");
        btnSuppr.setStyle("-fx-background-color: transparent; -fx-text-fill: #eb5a46; -fx-font-weight: bold; -fx-cursor: hand;");
        btnSuppr.setOnAction(e -> controleur.supprimerTache(tache));

        ligne.getChildren().addAll(puce, lblTitre, spacer, btnEdit, btnSuppr);
        container.getChildren().add(ligne);

        if (checkAfficherSousTaches.isSelected() && tache instanceof TacheMere) {
            TacheMere tm = (TacheMere) tache;
            for (TacheAbstraite enfant : tm.getEnfants()) {
                construireLigneTacheRecursive(enfant, container, niveau + 1);
            }
        }
    }

    private void rafraichirListeDesProjets() {
        Projet selection = selecteurProjet.getValue();
        List<Projet> projets = SingletonTache.getInstance().getMesProjets();
        selecteurProjet.getItems().setAll(projets);

        // Sélection intelligente
        if (selection != null && projets.contains(selection)) {
            selecteurProjet.setValue(selection);
        } else if (!projets.isEmpty()) {
            // Sélectionne automatiquement le premier projet au lancement
            selecteurProjet.getSelectionModel().selectFirst();
            // Force le déclenchement de l'action si le listener ne s'est pas activé
            if (projetEnCours == null) changerProjet(projets.get(0));
        }
    }

    private void ajouterTache() {
        if (projetEnCours == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez créer ou sélectionner un projet.");
            alert.show();
            return;
        }

        // CORRECTION UX : Si aucune colonne n'existe, on en crée une par défaut "À faire"
        if (projetEnCours.getColonnes().isEmpty()) {
            controleur.ajouterColonne(projetEnCours, "À faire");
        }

        // On est maintenant sûr d'avoir au moins une colonne (la 0)
        controleur.creerTache(
                projetEnCours.getColonnes().get(0),
                champTitre.getText(),
                datePicker.getValue(),
                prioriteBox.getValue()
        );
        champTitre.clear();
    }

    private void ouvrirPopUpModification(TacheAbstraite tacheCible) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Modifier");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(15);

        TextField txtTitre = new TextField(tacheCible.getTitre());
        DatePicker dtPicker = new DatePicker(tacheCible.getDateLimite());
        ComboBox<Priorite> cbPrio = new ComboBox<>();
        cbPrio.getItems().setAll(Priorite.values());
        cbPrio.setValue(tacheCible.getPriorite());

        grid.add(new Label("Titre :"), 0, 0); grid.add(txtTitre, 1, 0);
        grid.add(new Label("Date :"), 0, 1); grid.add(dtPicker, 1, 1);
        grid.add(new Label("Prio :"), 0, 2); grid.add(cbPrio, 1, 2);

        Button btnSave = new Button("Enregistrer");

        btnSave.setOnAction(e -> {
            controleur.modifierTache(tacheCible, txtTitre.getText(), dtPicker.getValue(), cbPrio.getValue());
            popup.close();
        });
        grid.add(btnSave, 1, 3);
        popup.setScene(new Scene(grid));
        popup.show();
    }

    @Override
    public void actualiser(Sujet s) {
        Platform.runLater(() -> {
            if (s instanceof SingletonTache) rafraichirListeDesProjets();
            else rafraichirContenu();
        });
    }
}