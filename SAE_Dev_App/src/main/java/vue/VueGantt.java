package vue;

import controleur.ControleurFX;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modele.*;
import observateur.Observateur;
import observateur.Sujet;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class VueGantt extends VBox implements Observateur {

    private Projet projet;
    private ControleurFX controleur;
    private VBox containerGantt;

    // Paramètres d'affichage
    private final double LARGEUR_JOUR = 50.0;
    private final double HAUTEUR_LIGNE = 40.0;
    private final double LARGEUR_TITRE = 200.0;

    private Set<TacheAbstraite> tachesObservees = new HashSet<>();

    private TextField champTitre;
    private DatePicker dateDebutPicker;
    private DatePicker dateFinPicker;
    private ComboBox<Priorite> prioriteBox;

    public VueGantt(Projet projet) {
        this.projet = projet;
        this.controleur = new ControleurFX();

        this.projet.enregistrerObservateur(this);
        for (Colonne c : projet.getColonnes()) {
            c.enregistrerObservateur(this);
        }

        this.setPadding(new Insets(15));
        this.setSpacing(15);
        this.setStyle("-fx-background-color: white;");

        Label titreVue = new Label("Gantt : " + projet.getNom());
        titreVue.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #172b4d;");

        containerGantt = new VBox();
        ScrollPane scrollPane = new ScrollPane(containerGantt);

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(false);
        scrollPane.setPannable(true);

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        HBox formulaire = creerFormulaireAjout();

        this.getChildren().addAll(titreVue, scrollPane, formulaire);

        rafraichir();
    }

    private void rafraichir() {
        for (TacheAbstraite t : tachesObservees) {
            t.supprimerObservateur(this);
        }
        tachesObservees.clear();

        containerGantt.getChildren().clear();

        List<TacheHierarchique> listeOrdonnee = new ArrayList<>();
        for (Colonne c : projet.getColonnes()) {
            for (TacheMere tm : c.getTaches()) {
                construireListeHierarchique(listeOrdonnee, tm, 0);
            }
        }

        if (listeOrdonnee.isEmpty()) {
            containerGantt.getChildren().add(new Label("pas de tache existante"));
            return;
        }

        LocalDate minDate = listeOrdonnee.stream()
                .map(w -> w.tache.getDateDebut())
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now()).minusDays(2);

        LocalDate maxDate = listeOrdonnee.stream()
                .map(w -> w.tache.getDateLimite())
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now().plusDays(10)).plusDays(5);

        long nbJoursTotal = ChronoUnit.DAYS.between(minDate, maxDate);
        if (nbJoursTotal < 15) nbJoursTotal = 15;

        double largeurGraphiqueReelle = (nbJoursTotal + 1) * LARGEUR_JOUR;

        HBox entete = new HBox();
        entete.setPrefHeight(30);
        entete.setAlignment(Pos.CENTER_LEFT);

        Region espaceTitre = new Region();
        espaceTitre.setMinWidth(LARGEUR_TITRE);
        espaceTitre.setPrefWidth(LARGEUR_TITRE);
        espaceTitre.setStyle("-fx-border-color: #ddd; -fx-border-width: 0 1 1 0;");
        entete.getChildren().add(espaceTitre);

        for (int i = 0; i <= nbJoursTotal; i++) {
            LocalDate jour = minDate.plusDays(i);
            Label lblJour = new Label(jour.getDayOfMonth() + "/" + jour.getMonthValue());
            lblJour.setMinWidth(LARGEUR_JOUR);
            lblJour.setMaxWidth(LARGEUR_JOUR);
            lblJour.setPrefWidth(LARGEUR_JOUR);
            lblJour.setAlignment(Pos.CENTER);
            lblJour.setStyle("-fx-font-size: 10px; -fx-text-fill: #5e6c84; -fx-border-color: #eee; -fx-border-width: 0 0 1 1;");

            if (jour.isEqual(LocalDate.now())) {
                lblJour.setStyle(lblJour.getStyle() + "-fx-background-color: #e6fcff; -fx-font-weight: bold;");
            }
            entete.getChildren().add(lblJour);
        }
        containerGantt.getChildren().add(entete);

        for (TacheHierarchique item : listeOrdonnee) {
            TacheAbstraite tache = item.tache;

            HBox ligne = new HBox();
            ligne.setAlignment(Pos.CENTER_LEFT);
            ligne.setPrefHeight(HAUTEUR_LIGNE);
            ligne.setStyle("-fx-border-color: #f4f5f7; -fx-border-width: 0 0 1 0;");

            Label lblTitre = new Label(tache.getTitre());
            lblTitre.setMinWidth(LARGEUR_TITRE);
            lblTitre.setPrefWidth(LARGEUR_TITRE);
            lblTitre.setMaxWidth(LARGEUR_TITRE);
            lblTitre.setPadding(new Insets(0, 10, 0, 10 + (item.niveau * 20)));

            if (tache instanceof TacheMere && !((TacheMere)tache).getEnfants().isEmpty()) {
                lblTitre.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #172b4d; -fx-border-color: #eee; -fx-border-width: 0 1 0 0;");
            } else {
                lblTitre.setStyle("-fx-font-size: 12px; -fx-text-fill: #172b4d; -fx-border-color: #eee; -fx-border-width: 0 1 0 0;");
            }
            ligne.getChildren().add(lblTitre);

            Pane zoneBarre = new Pane();
            zoneBarre.setMinWidth(largeurGraphiqueReelle);
            zoneBarre.setPrefWidth(largeurGraphiqueReelle);

            long joursDepuisDebut = ChronoUnit.DAYS.between(minDate, tache.getDateDebut());
            long dureeTache = ChronoUnit.DAYS.between(tache.getDateDebut(), tache.getDateLimite());
            if (dureeTache < 1) dureeTache = 1;

            double x = joursDepuisDebut * LARGEUR_JOUR;
            double width = dureeTache * LARGEUR_JOUR;

            Rectangle barre = new Rectangle();
            barre.setX(x);
            barre.setY(HAUTEUR_LIGNE / 2 - 3);
            barre.setWidth(width);
            barre.setHeight(6);
            barre.setArcWidth(6);
            barre.setArcHeight(6);

            Color couleurPrio = switch (tache.getPriorite()) {
                case HAUTE -> Color.RED;
                case MOYENNE -> Color.ORANGE;
                case BASSE -> Color.GREEN;
            };
            barre.setFill(couleurPrio);

            Circle point = new Circle(6);
            point.setCenterX(x + (width / 2));
            point.setCenterY(HAUTEUR_LIGNE / 2);
            point.setFill(Color.WHITE);
            point.setStroke(couleurPrio);
            point.setStrokeWidth(2);
            point.setCursor(javafx.scene.Cursor.HAND);

            point.setOnMouseClicked(e -> ouvrirPopUpDetails(tache));

            zoneBarre.getChildren().addAll(barre, point);
            ligne.getChildren().add(zoneBarre);

            containerGantt.getChildren().add(ligne);
        }
    }


    private void ouvrirPopUpDetails(TacheAbstraite tache) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Details : " + tache.getTitre());

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label lblTitre = new Label(tache.getTitre());
        lblTitre.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        Label lblDates = new Label("Du " + tache.getDateDebut() + " au " + tache.getDateLimite());
        Label lblPrio = new Label("Priorité : " + tache.getPriorite());

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER);

        Button btnModif = new Button("✎ Modifier");
        btnModif.setOnAction(e -> {
            popup.close();
            ouvrirPopUpModification(tache);
        });

        Button btnSousTache = new Button("+ Sous-tâche");
        if (tache instanceof TacheMere) {
            btnSousTache.setOnAction(e -> {
                popup.close();
                ouvrirDialogAjoutSousTache((TacheMere) tache);
            });
        } else {
            btnSousTache.setDisable(true);
        }

        actions.getChildren().addAll(btnModif, btnSousTache);
        layout.getChildren().addAll(lblTitre, lblDates, lblPrio, new Separator(), actions);

        Scene scene = new Scene(layout, 350, 200);
        popup.setScene(scene);
        popup.show();
    }

    private void ouvrirPopUpModification(TacheAbstraite tacheCible) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Modifier");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10); grid.setVgap(15);

        TextField txtTitre = new TextField(tacheCible.getTitre());
        DatePicker dtDebut = new DatePicker(tacheCible.getDateDebut());
        DatePicker dtFin = new DatePicker(tacheCible.getDateLimite());
        ComboBox<Priorite> cbPrio = new ComboBox<>();
        cbPrio.getItems().setAll(Priorite.values());
        cbPrio.setValue(tacheCible.getPriorite());

        grid.add(new Label("Titre :"), 0, 0); grid.add(txtTitre, 1, 0);
        grid.add(new Label("Début :"), 0, 1); grid.add(dtDebut, 1, 1);
        grid.add(new Label("Fin :"), 0, 2);   grid.add(dtFin, 1, 2);
        grid.add(new Label("Prio :"), 0, 3);  grid.add(cbPrio, 1, 3);

        Button btnSave = new Button("Enregistrer");
        btnSave.setOnAction(e -> {
            controleur.modifierTache(tacheCible, txtTitre.getText(), dtDebut.getValue(), dtFin.getValue(), cbPrio.getValue());
            popup.close();
        });
        grid.add(btnSave, 1, 4);

        popup.setScene(new Scene(grid));
        popup.show();
    }

    private void ouvrirDialogAjoutSousTache(TacheMere parent) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle sous tache");
        dialog.setHeaderText("sous tache pour : " + parent.getTitre());

        TextField txtTitre = new TextField();
        DatePicker dtDebut = new DatePicker(LocalDate.now());
        DatePicker dtFin = new DatePicker(LocalDate.now());
        ComboBox<Priorite> cbPrio = new ComboBox<>();
        cbPrio.getItems().setAll(Priorite.values());
        cbPrio.setValue(Priorite.MOYENNE);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.add(new Label("Titre :"), 0, 0); grid.add(txtTitre, 1, 0);
        grid.add(new Label("Début :"), 0, 1); grid.add(dtDebut, 1, 1);
        grid.add(new Label("Fin :"), 0, 2);   grid.add(dtFin, 1, 2);
        grid.add(new Label("Prio :"), 0, 3);  grid.add(cbPrio, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(type -> {
            if (type == ButtonType.OK) {
                controleur.creerSousTache(parent, txtTitre.getText(), dtDebut.getValue(), dtFin.getValue(), cbPrio.getValue());
            }
        });
    }

    private HBox creerFormulaireAjout() {
        champTitre = new TextField();
        champTitre.setPromptText("Nouvelle tache...");
        HBox.setHgrow(champTitre, Priority.ALWAYS);

        dateDebutPicker = new DatePicker(LocalDate.now());
        dateDebutPicker.setPrefWidth(110);
        dateFinPicker = new DatePicker(LocalDate.now().plusDays(1));
        dateFinPicker.setPrefWidth(110);

        prioriteBox = new ComboBox<>();
        prioriteBox.getItems().addAll(Priorite.values());
        prioriteBox.setValue(Priorite.MOYENNE);

        Button btnAjouter = new Button("Ajouter");
        btnAjouter.setStyle("-fx-background-color: #0079bf; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAjouter.setOnAction(e -> {
            if (projet.getColonnes().isEmpty()) {
                controleur.ajouterColonne(projet, "a faire");
            }
            controleur.creerTache(
                    projet.getColonnes().get(0),
                    champTitre.getText(),
                    dateDebutPicker.getValue(),
                    dateFinPicker.getValue(),
                    prioriteBox.getValue()
            );
            champTitre.clear();
        });

        HBox box = new HBox(10, champTitre, dateDebutPicker, dateFinPicker, prioriteBox, btnAjouter);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(10, 0, 0, 0));
        return box;
    }

    private static class TacheHierarchique {
        TacheAbstraite tache;
        int niveau;
        public TacheHierarchique(TacheAbstraite t, int n) { tache = t; niveau = n; }
    }

    private void construireListeHierarchique(List<TacheHierarchique> liste, TacheAbstraite tache, int niveau) {
        tache.enregistrerObservateur(this);
        tachesObservees.add(tache);

        liste.add(new TacheHierarchique(tache, niveau));
        if (tache instanceof TacheMere) {
            List<TacheAbstraite> enfants = new ArrayList<>(((TacheMere) tache).getEnfants());
            enfants.sort(Comparator.comparing(TacheAbstraite::getDateDebut));
            for (TacheAbstraite enfant : enfants) {
                construireListeHierarchique(liste, enfant, niveau + 1);
            }
        }
    }

    @Override
    public void actualiser(Sujet s) {
        Platform.runLater(this::rafraichir);
    }
}