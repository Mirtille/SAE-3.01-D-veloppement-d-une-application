package vue;

import controleur.ControleurFX;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import modele.Colonne;
import modele.Projet;
import modele.SingletonTache;
import observateur.Observateur;
import observateur.Sujet;

import java.util.List;

public class VueKanban extends VBox implements Observateur {

    private Projet projetEnCours;
    private HBox containerColonnes;
    private ControleurFX controleur;
    private ComboBox<Projet> selecteurProjet;

    public VueKanban() {
        this.controleur = new ControleurFX();

        SingletonTache.getInstance().enregistrerObservateur(this);

        this.setPadding(new Insets(10));
        this.setSpacing(10);
        this.setStyle("-fx-background-color: #0079bf;");

        HBox barreOutils = new HBox(10);
        barreOutils.setAlignment(Pos.CENTER_LEFT);

        Label labelProjet = new Label("Projet :");
        labelProjet.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

        selecteurProjet = new ComboBox<>();
        selecteurProjet.setMinWidth(150);
        selecteurProjet.setConverter(new StringConverter<>() {
            @Override public String toString(Projet p) { return p == null ? "Aucun" : p.getNom(); }
            @Override public Projet fromString(String s) { return null; }
        });
        // Action au changement de projet
        selecteurProjet.setOnAction(e -> changerProjet(selecteurProjet.getValue()));

        // Bouton "+" pour créer un projet (comme dans VueListe)
        Button btnNouveauProjet = new Button("+");
        btnNouveauProjet.setTooltip(new Tooltip("Créer un nouveau projet"));
        btnNouveauProjet.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Nouveau Projet");
            dialog.setHeaderText("Créer un nouveau projet");
            dialog.setContentText("Nom :");
            dialog.showAndWait().ifPresent(nom -> controleur.creerNouveauProjet(nom));
        });

        // Bouton pour créer une nouvelle colonne (Déplacé dans la barre d'outils)
        Button btnNouvelleColonne = new Button("Ajouter une colonne");
        btnNouvelleColonne.setStyle("-fx-background-color: rgba(255,255,255,0.3); -fx-text-fill: white; -fx-font-weight: bold;");
        btnNouvelleColonne.setOnAction(e -> {
            if (projetEnCours == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez d'abord sélectionner ou créer un projet.");
                alert.show();
                return;
            }
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Nouvelle colonne");
            dialog.setHeaderText("Ajouter une colonne à : " + projetEnCours.getNom());
            dialog.setContentText("Nom :");
            dialog.showAndWait().ifPresent(nom -> {
                if (!nom.trim().isEmpty()) {
                    controleur.ajouterColonne(this.projetEnCours, nom);
                }
            });
        });

        barreOutils.getChildren().addAll(labelProjet, selecteurProjet, btnNouveauProjet, new Separator(javafx.geometry.Orientation.VERTICAL), btnNouvelleColonne);


        // --- CONTENU KANBAN ---
        containerColonnes = new HBox(15);
        ScrollPane scrollH = new ScrollPane(containerColonnes);
        scrollH.setFitToHeight(true);
        scrollH.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(scrollH, Priority.ALWAYS);

        this.getChildren().addAll(barreOutils, scrollH);

        // Initialisation
        rafraichirListeDesProjets();
    }

    // [NOUVEAU] Gestion du changement de projet
    private void changerProjet(Projet p) {
        // Désabonnement de l'ancien projet
        if (this.projetEnCours != null) {
            this.projetEnCours.supprimerObservateur(this);
        }

        this.projetEnCours = p;

        // Abonnement au nouveau projet
        if (this.projetEnCours != null) {
            this.projetEnCours.enregistrerObservateur(this);
        }

        rafraichir();
    }

    // [NOUVEAU] Met à jour la ComboBox avec la liste réelle des projets
    private void rafraichirListeDesProjets() {
        Projet selection = selecteurProjet.getValue();
        List<Projet> projets = SingletonTache.getInstance().getMesProjets();
        selecteurProjet.getItems().setAll(projets);

        if (selection != null && projets.contains(selection)) {
            selecteurProjet.setValue(selection);
        } else if (!projets.isEmpty()) {
            selecteurProjet.getSelectionModel().selectFirst();
            changerProjet(projets.get(0));
        } else {
            changerProjet(null);
        }
    }

    private void rafraichir() {
        containerColonnes.getChildren().clear();

        if (projetEnCours != null) {
            for (Colonne c : projetEnCours.getColonnes()) {
                VueColonne vueColonne = new VueColonne(c);
                containerColonnes.getChildren().add(vueColonne);
            }
        }
    }

    @Override
    public void actualiser(Sujet s) {
        Platform.runLater(() -> {
            // Si c'est le Singleton qui notifie (ex: ajout de projet), on met à jour la liste
            if (s instanceof SingletonTache) {
                rafraichirListeDesProjets();
            }
            // Sinon (c'est le projet en cours qui change), on rafraîchit les colonnes
            else {
                rafraichir();
            }
        });
    }
}