package vue;

import controleur.ControleurFX;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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

    // La liste des tâches du projet en cours
    private ListView<TacheAbstraite> listeTaches;

    // Le projet actuellement affiché
    private TacheMere projetEnCours;

    // Formulaire
    private TextField champTitre;
    private DatePicker datePicker;
    private ComboBox<Priorite> prioriteBox;
    private CheckBox checkDossier;

    public VueListe() {
        this.setPadding(new Insets(10));
        this.setSpacing(10);
        this.controleur = new ControleurFX();

        // 1. Abonnement au Singleton (pour savoir quand on ajoute un PROJET)
        SingletonTache.getInstance().enregistrerObservateur(this);

        // --- ZONE HAUTE : SÉLECTION DE PROJET ---
        Label labelProjet = new Label("Projet actuel :");
        selecteurProjet = new ComboBox<>();

        // Pour afficher le nom du projet proprement dans la ComboBox
        selecteurProjet.setConverter(new StringConverter<>() {
            @Override
            public String toString(TacheMere t) { return t == null ? "" : t.getTitre(); }
            @Override
            public TacheMere fromString(String string) { return null; }
        });

        // Quand on change de projet dans la liste déroulante
        selecteurProjet.setOnAction(e -> {
            changerProjet(selecteurProjet.getValue());
        });

        btnNouveauProjet = new Button("+ Liste");
        btnNouveauProjet.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog("Nouveau Projet");
            dialog.setHeaderText("Créer une nouvelle liste de tâches");
            dialog.showAndWait().ifPresent(nom -> controleur.creerNouveauProjet(nom));
        });

        HBox barreProjet = new HBox(10, labelProjet, selecteurProjet, btnNouveauProjet);
        barreProjet.setStyle("-fx-background-color: #ddd; -fx-padding: 10;");


        // --- ZONE BASSE : GESTION DES TÂCHES ---
        champTitre = new TextField();
        datePicker = new DatePicker(LocalDate.now());
        prioriteBox = new ComboBox<>();
        prioriteBox.getItems().addAll(Priorite.values());
        prioriteBox.setValue(Priorite.MOYENNE);
        checkDossier = new CheckBox("Dossier");
        Button btnAjouter = new Button("Ajouter Tâche");

        btnAjouter.setOnAction(e -> {
            // On ajoute la tâche DANS le projet en cours
            controleur.creerTache(
                    projetEnCours, // C'est le parent racine de la liste actuelle
                    champTitre.getText(),
                    datePicker.getValue(),
                    prioriteBox.getValue(),
                    checkDossier.isSelected()
            );
            champTitre.clear();
        });

        HBox formulaire = new HBox(5, champTitre, datePicker, prioriteBox, checkDossier, btnAjouter);

        listeTaches = new ListView<>();
        // (Copie ici ta CellFactory pour l'affichage joli)

        this.getChildren().addAll(barreProjet, new Separator(), formulaire, listeTaches);

        // Initialisation : On charge les projets et on sélectionne le premier
        rafraichirListeDesProjets();
        if (!selecteurProjet.getItems().isEmpty()) {
            selecteurProjet.getSelectionModel().selectFirst();
        }
    }

    // Appelé quand on change de sélection dans la ComboBox
    private void changerProjet(TacheMere nouveauProjet) {
        if (nouveauProjet == null) return;

        // 1. On se désabonne de l'ancien projet (s'il y en avait un)
        if (this.projetEnCours != null) {
            this.projetEnCours.supprimerObservateur(this);
        }

        // 2. On change le projet courant
        this.projetEnCours = nouveauProjet;

        // 3. On s'abonne au nouveau (pour voir ses tâches changer)
        this.projetEnCours.enregistrerObservateur(this);

        // 4. On met à jour l'affichage de la liste
        rafraichirListeTaches();
    }

    private void rafraichirListeDesProjets() {
        // Sauvegarde de la sélection actuelle
        TacheMere selection = selecteurProjet.getValue();

        selecteurProjet.getItems().clear();
        selecteurProjet.getItems().addAll(SingletonTache.getInstance().getMesProjets());

        if (selection != null && selecteurProjet.getItems().contains(selection)) {
            selecteurProjet.setValue(selection);
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
        // Cas 1 : C'est le Singleton qui notifie (Ajout d'un PROJET)
        if (s instanceof SingletonTache) {
            rafraichirListeDesProjets();
        }
        // Cas 2 : C'est le Projet en cours qui notifie (Ajout d'une TÂCHE)
        else if (s == projetEnCours) {
            rafraichirListeTaches();
        }
    }
}