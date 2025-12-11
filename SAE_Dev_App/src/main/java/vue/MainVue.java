package vue;

import Observateur.Observateur;
import Observateur.Sujet;
import modele.DataManager;
import modele.Tache;
import modele.Priorite;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

// Elle hérite de BorderPane (Layout principal) et implémente Observateur
public class MainVue extends BorderPane implements Observateur {

    // La sous-vue spécifique (celle qu'on a créée au-dessus)
    private VueSemaine vueSemaine;

    // Éléments du formulaire (accessibles pour le Contrôleur)
    private TextField champTitre;
    private ComboBox<Priorite> comboPriorite;
    private DatePicker datePicker;
    private Button boutonAjouter;

    public MainVue() {
        initialiserComposants();

        // --- OBSERVER ---
        // La vue s'abonne au modèle
        DataManager.getInstance().enregistrerObservateur(this);
    }

    private void initialiserComposants() {
        // --- HAUT : Menu + Formulaire ---
        VBox topContainer = new VBox(10);
        topContainer.setPadding(new Insets(10));
        topContainer.setStyle("-fx-background-color: #ddd;");

        // 1. Boutons de navigation (Pour plus tard)
        HBox menu = new HBox(10);
        menu.setAlignment(Pos.CENTER);
        menu.getChildren().addAll(new Button("Vue Liste"), new Button("Vue Kanban"), new Button("Vue Gantt"));

        // 2. Formulaire d'ajout
        HBox formulaire = new HBox(10);
        formulaire.setAlignment(Pos.CENTER);

        champTitre = new TextField();
        champTitre.setPromptText("Titre...");

        comboPriorite = new ComboBox<>();
        comboPriorite.getItems().setAll(Priorite.values());
        comboPriorite.getSelectionModel().select(Priorite.MOYENNE);

        datePicker = new DatePicker();

        boutonAjouter = new Button("Ajouter");

        formulaire.getChildren().addAll(new Label("Nouvelle :"), champTitre, datePicker, comboPriorite, boutonAjouter);

        topContainer.getChildren().addAll(menu, formulaire);
        this.setTop(topContainer);

        // --- CENTRE : La Vue Semaine ---
        vueSemaine = new VueSemaine();
        this.setCenter(vueSemaine);
    }

    // --- MÉTHODE DE L'OBSERVATEUR ---
    // Appelée automatiquement quand le DataManager change !
    @Override
    public void actualiser(Sujet s) {
        // 1. On vide la vue
        vueSemaine.viderContenu();

        // 2. On récupère les données fraîches
        DataManager manager = (DataManager) s;

        // 3. On remplit la vue
        for (Tache t : manager.getTaches()) {
            vueSemaine.ajouterTacheVisuelle(t);
        }
    }

    // Getters pour le contrôleur
    public Button getBoutonAjouter() { return boutonAjouter; }
    public TextField getChampTitre() { return champTitre; }
    public DatePicker getDatePicker() { return datePicker; }
    public ComboBox<Priorite> getComboPriorite() { return comboPriorite; }
}