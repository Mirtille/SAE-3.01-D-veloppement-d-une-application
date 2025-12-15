package vue;

import controleur.ControleurFX;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import modele.*;
import observateur.Observateur;
import observateur.Sujet;

import java.time.LocalDate;

public class VueListe extends VBox implements Observateur {

    private TacheMere racine;
    private ControleurFX controleur;

    private ListView<TacheAbstraite> liste;
    private ObservableList<TacheAbstraite> items;

    public VueListe() {
        // Un peu de marge pour que ce soit joli
        this.setPadding(new Insets(10));
        this.setSpacing(15);

        this.racine = ModeleTache.getInstance().getRacine();
        this.controleur = new ControleurFX();

        racine.enregistrerObservateur(this);

        Label titre = new Label("Projet : " + racine.getTitre());

        TextField champTitre = new TextField();
        champTitre.setPromptText("Titre de la tâche");

        DatePicker datePicker = new DatePicker(LocalDate.now());

        ComboBox<Priorite> prioriteBox = new ComboBox<>();
        prioriteBox.getItems().addAll(Priorite.values());
        prioriteBox.setValue(Priorite.BASSE);

        Button btnAjouter = new Button("Ajouter");
        Button btnModifier = new Button("Modifier la sélection");
        Button btnSupprimer = new Button("Supprimer la sélection");
        Button btnArchiver = new Button("Archiver la sélection");

        btnAjouter.setOnAction(e -> {
            controleur.creerTache(
                    champTitre.getText(),
                    datePicker.getValue(),
                    prioriteBox.getValue()
            );
            champTitre.clear();
        });



        btnModifier.setOnAction(e -> {
            TacheAbstraite tacheSelectionnee = liste.getSelectionModel().getSelectedItem();
            if (tacheSelectionnee != null) {
                controleur.modifierTache(
                        tacheSelectionnee,
                        champTitre.getText(),
                        datePicker.getValue(),
                        prioriteBox.getValue()
                );
                champTitre.clear();
                liste.getSelectionModel().clearSelection(); // On désélectionne après modif
            }
        });

        btnArchiver.setOnAction(e -> {
            controleur.archiverTache(liste.getSelectionModel().getSelectedItem());
        });

        btnSupprimer.setOnAction(e ->
                controleur.supprimerTache(liste.getSelectionModel().getSelectedItem())
        );

        HBox formulaire = new HBox(10, champTitre, datePicker, prioriteBox, btnAjouter, btnModifier);

        items = FXCollections.observableArrayList();
        liste = new ListView<>(items);

        liste.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(TacheAbstraite item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.afficher());
            }
        });

        liste.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                champTitre.setText(newVal.getTitre());
                datePicker.setValue(newVal.getDateLimite());
                prioriteBox.setValue(newVal.getPriorite());
            }
        });

        HBox actions = new HBox(10, btnSupprimer, btnArchiver);

        // Assemblage final
        this.getChildren().addAll(titre, formulaire, liste, actions);

        rafraichir();
    }

    private void rafraichir() {
        items.clear();
        items.addAll(racine.getEnfants());
    }

    @Override
    public void actualiser(Sujet s) {
        // On rafraichit tout le temps pour être sûr d'avoir les dernières infos
        rafraichir();
    }
}