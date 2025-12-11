package fr.iut.sae.vue;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import modele.Priorite; // Vérifie ton import
import modele.Tache;    // Vérifie ton import

public class AccueilVue extends VBox {

    // On déclare les composants en attributs pour pouvoir y accéder
    private TextField champTitre;
    private ComboBox<Priorite> comboPriorite;
    private DatePicker datePicker;
    private Button boutonAjouter;
    private ListView<Tache> listeTaches;

    public AccueilVue() {
        // 1. Configuration de la boîte principale (this)
        this.setSpacing(20);
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.TOP_CENTER);

        // 2. Création du Titre
        Label titreApp = new Label("Gestionnaire de Tâches (Java Pur)");
        titreApp.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // 3. Création du Formulaire
        HBox formulaire = new HBox(10); // Espacement horizontal de 10
        formulaire.setAlignment(Pos.CENTER);

        champTitre = new TextField();
        champTitre.setPromptText("Titre de la tâche");

        comboPriorite = new ComboBox<>();
        // On remplit direct la combo ici ou dans le contrôleur (ici c'est plus simple pour l'affichage)
        comboPriorite.getItems().setAll(Priorite.values());
        comboPriorite.setPromptText("Priorité");

        datePicker = new DatePicker();

        boutonAjouter = new Button("Ajouter");
        boutonAjouter.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        // On ajoute tout dans la HBox
        formulaire.getChildren().addAll(champTitre, comboPriorite, datePicker, boutonAjouter);

        // 4. Création de la Liste
        listeTaches = new ListView<>();

        // 5. Ajout final à la vue (this)
        this.getChildren().addAll(titreApp, formulaire, new Label("Mes Tâches :"), listeTaches);
    }

    // --- GETTERS (Pour que le Contrôleur puisse agir dessus) ---
    public TextField getChampTitre() { return champTitre; }
    public ComboBox<Priorite> getComboPriorite() { return comboPriorite; }
    public DatePicker getDatePicker() { return datePicker; }
    public Button getBoutonAjouter() { return boutonAjouter; }
    public ListView<Tache> getListeTaches() { return listeTaches; }
}