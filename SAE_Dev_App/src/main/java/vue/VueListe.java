package vue;

import controleur.ControleurFX;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

        this.racine = ModeleTache.getInstance().getRacine();
        this.controleur = new ControleurFX();

        racine.enregistrerObservateur(this);

        Label titre = new Label("Projet : " + racine.getTitre());

        // ---- FORMULAIRE ----
        TextField champTitre = new TextField();
        champTitre.setPromptText("Titre de la tâche");

        DatePicker datePicker = new DatePicker(LocalDate.now());

        ComboBox<Priorite> prioriteBox = new ComboBox<>();
        prioriteBox.getItems().addAll(Priorite.values());
        prioriteBox.setValue(Priorite.MOYENNE);

        Button btnAjouter = new Button("Ajouter");

        btnAjouter.setOnAction(e -> {
            controleur.creerTache(
                    champTitre.getText(),
                    datePicker.getValue(),
                    prioriteBox.getValue()
            );
            champTitre.clear();
        });

        HBox formulaire = new HBox(10, champTitre, datePicker, prioriteBox, btnAjouter);

        // ---- LISTE ----
        items = FXCollections.observableArrayList();
        liste = new ListView<>(items);

        liste.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(TacheAbstraite item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.afficher());
            }
        });

        // ---- SUPPRESSION ----
        Button btnSupprimer = new Button("Supprimer la tâche sélectionnée");

        btnSupprimer.setOnAction(e ->
                controleur.supprimerTache(liste.getSelectionModel().getSelectedItem())
        );

        this.setSpacing(15);
        this.getChildren().addAll(titre, formulaire, liste, btnSupprimer);

        rafraichir();
    }

    private void rafraichir() {
        items.clear();
        items.addAll((TacheAbstraite) racine.getEnfants());
    }

    @Override
    public void actualiser(Sujet s) {
        if (s instanceof TacheMere) {
            rafraichir();
        }
    }
}
