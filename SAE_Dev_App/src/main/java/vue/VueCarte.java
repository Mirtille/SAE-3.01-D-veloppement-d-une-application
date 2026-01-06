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

    // Set pour gérer les abonnements de TOUTES les sous-tâches (récursif)
    private Set<TacheAbstraite> sousTachesObservees = new HashSet<>();

    public VueCarte(TacheAbstraite tache, ControleurFX controleur) {
        this.tache = tache;
        this.controleur = controleur;
        this.tache.enregistrerObservateur(this);

        // --- STYLE ---
        this.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 3, 0, 0, 1);");
        this.setPadding(new Insets(10));
        this.setSpacing(8);

        // --- DRAG SOURCE (Carte Principale) ---
        configurerDragSource(this, this.tache);

        // --- DROP TARGET (La racine peut recevoir) ---
        if (tache instanceof TacheMere) {
            configurerDropTarget(this, (TacheMere) this.tache);
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

        // --- CONTENEUR SOUS-TÂCHES ---
        if (tache instanceof TacheMere) {
            Separator sep = new Separator();
            containerSousTaches = new VBox(2); // Espacement vertical réduit
            containerSousTaches.setPadding(new Insets(5, 0, 5, 0));

            Button btnAjoutSousTache = new Button("+ Ajouter une sous-tâche");
            btnAjoutSousTache.setMaxWidth(Double.MAX_VALUE);
            btnAjoutSousTache.setStyle("-fx-background-color: #f4f5f7; -fx-text-fill: #172b4d; -fx-font-size: 11px; -fx-cursor: hand;");
            // Ajout à la racine
            btnAjoutSousTache.setOnAction(e -> ouvrirDialogAjoutSousTache((TacheMere) tache));

            this.getChildren().addAll(sep, containerSousTaches, btnAjoutSousTache);
        }

        mettreAJourAffichage();
    }

    // --- MISE A JOUR RÉCURSIVE ---
    private void mettreAJourAffichage() {
        lblTitre.setText(tache.getTitre());
        lblInfo.setText(tache.getDateLimite() + " • " + tache.getPriorite());

        // 1. Nettoyage des anciens observateurs
        for (TacheAbstraite st : sousTachesObservees) st.supprimerObservateur(this);
        sousTachesObservees.clear();

        if (tache instanceof TacheMere && containerSousTaches != null) {
            containerSousTaches.getChildren().clear();
            TacheMere racine = (TacheMere) tache;

            // Appel de la méthode récursive pour construire l'arbre
            for (TacheAbstraite enfant : racine.getEnfants()) {
                construireBrancheRecursive(enfant, containerSousTaches, 0);
            }
        }
    }

    /**
     * Méthode récursive pour afficher une tâche et ses enfants
     */
    private void construireBrancheRecursive(TacheAbstraite tacheCourante, VBox conteneurParent, int niveau) {
        // A. Abonnement aux mises à jour
        tacheCourante.enregistrerObservateur(this);
        sousTachesObservees.add(tacheCourante);

        // B. Création de la ligne visuelle
        HBox ligne = new HBox(8);
        ligne.setAlignment(Pos.TOP_LEFT);
        // Indentation : on décale à gauche en fonction du niveau (0, 1, 2...)
        ligne.setPadding(new Insets(4, 0, 4, niveau * 20));
        ligne.setStyle("-fx-border-color: transparent transparent #f0f0f0 transparent;");

        // Drag & Drop pour cette ligne spécifique
        configurerDragSource(ligne, tacheCourante);

        // Drop Target : Si cette sous-tâche est survolée, elle peut recevoir des enfants !
        if (tacheCourante instanceof TacheMere) {
            configurerDropTarget(ligne, (TacheMere) tacheCourante);
        }

        // Éléments visuels
        Label puce = new Label("↳");
        puce.setStyle("-fx-text-fill: #5e6c84; -fx-font-size: 14px;");

        VBox conteneurTexte = new VBox(2);
        conteneurTexte.setAlignment(Pos.CENTER_LEFT);
        Label lblST = new Label(tacheCourante.getTitre());
        lblST.setStyle("-fx-font-size: 12px; -fx-text-fill: #172b4d;");
        lblST.setWrapText(true);
        Label lblDetails = new Label(tacheCourante.getDateLimite() + " • " + tacheCourante.getPriorite());
        lblDetails.setStyle("-fx-font-size: 10px; -fx-text-fill: #97a0af;");
        conteneurTexte.getChildren().addAll(lblST, lblDetails);
        HBox.setHgrow(conteneurTexte, Priority.ALWAYS);

        // Boutons actions
        HBox btnGroup = new HBox(2);

        // BOUTON "+" : Permet d'ajouter une sous-tâche à CETTE tâche
        Button btnAddSub = new Button("+");
        btnAddSub.setStyle("-fx-background-color: transparent; -fx-text-fill: #0079bf; -fx-font-size: 12px; -fx-font-weight:bold; -fx-cursor: hand;");
        btnAddSub.setTooltip(new Tooltip("Ajouter une sous-tâche ici"));
        // On passe la tacheCourante comme parent
        if (tacheCourante instanceof TacheMere) {
            btnAddSub.setOnAction(e -> ouvrirDialogAjoutSousTache((TacheMere) tacheCourante));
        } else {
            btnAddSub.setDisable(true);
        }

        Button btnModif = new Button("✎");
        btnModif.setStyle("-fx-background-color: transparent; -fx-text-fill: #5e6c84; -fx-font-size: 11px; -fx-cursor: hand;");
        btnModif.setOnAction(e -> ouvrirPopUpModification(tacheCourante));

        Button btnDel = new Button("×");
        btnDel.setStyle("-fx-background-color: transparent; -fx-text-fill: #eb5a46; -fx-font-size: 14px; -fx-cursor: hand;");
        btnDel.setOnAction(e -> controleur.supprimerTache(tacheCourante));

        btnGroup.getChildren().addAll(btnAddSub, btnModif, btnDel);
        ligne.getChildren().addAll(puce, conteneurTexte, btnGroup);

        // C. Ajout de la ligne au parent
        conteneurParent.getChildren().add(ligne);

        // D. RECURSION : Si cette tâche a des enfants, on les affiche en dessous
        if (tacheCourante instanceof TacheMere) {
            TacheMere tm = (TacheMere) tacheCourante;
            if (!tm.getEnfants().isEmpty()) {
                for (TacheAbstraite enfant : tm.getEnfants()) {
                    // On augmente le niveau d'indentation
                    construireBrancheRecursive(enfant, conteneurParent, niveau + 1);
                }
            }
        }
    }

    // --- UTILITAIRES DRAG & DROP FACTORISÉS ---

    private void configurerDragSource(javafx.scene.Node node, TacheAbstraite tacheAssociee) {
        node.setOnDragDetected(event -> {
            ControleurFX.tacheEnDeplacement = tacheAssociee;
            javafx.scene.input.Dragboard db = node.startDragAndDrop(javafx.scene.input.TransferMode.MOVE);
            javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(tacheAssociee.getTitre());
            db.setContent(content);
            event.consume();
        });
        node.setOnDragDone(event -> {
            ControleurFX.tacheEnDeplacement = null;
            event.consume();
        });
    }

    private void configurerDropTarget(javafx.scene.Node node, TacheMere cible) {
        node.setOnDragOver(event -> {
            TacheAbstraite source = ControleurFX.tacheEnDeplacement;
            // On accepte si source != null, source != cible
            if (event.getDragboard().hasString() && source != null && source != cible) {
                event.acceptTransferModes(javafx.scene.input.TransferMode.MOVE);
                node.setStyle("-fx-background-color: #e2e4e6; -fx-border-color: #0079bf; -fx-border-width: 2;");
            }
            event.consume();
        });

        node.setOnDragExited(event -> {
            // Reset style (attention, style par défaut simplifié ici, ajustez selon votre charte)
            node.setStyle("-fx-background-color: transparent;");
            event.consume();
        });

        node.setOnDragDropped(event -> {
            boolean success = false;
            TacheAbstraite source = ControleurFX.tacheEnDeplacement;
            if (source != null && source != cible) {
                controleur.deplacerVersTacheMere(source, cible);
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    // --- LE RESTES DES MÉTHODES (ouvrirDialog, detruire...) ---

    public void detruire() {
        if (tache != null) tache.supprimerObservateur(this);
        for (TacheAbstraite st : sousTachesObservees) st.supprimerObservateur(this);
        sousTachesObservees.clear();
    }

    private void ouvrirDialogAjoutSousTache(TacheMere parent) {
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

    @Override
    public void actualiser(Sujet s) {
        Platform.runLater(this::mettreAJourAffichage);
    }
}