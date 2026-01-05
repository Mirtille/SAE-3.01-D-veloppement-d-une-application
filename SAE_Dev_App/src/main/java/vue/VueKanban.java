package vue;

import controleur.ControleurFX;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import modele.Colonne;
import modele.Projet;
import observateur.Observateur;
import observateur.Sujet;

public class VueKanban extends VBox implements Observateur {

    private Projet projet; // Utilisation de la nouvelle classe Projet
    private HBox containerColonnes;
    private ControleurFX controleur;

    public VueKanban(Projet projet) {
        this.projet = projet;
        // On écoute le projet : si une colonne est ajoutée/supprimée, on actualise
        this.projet.enregistrerObservateur(this);

        this.controleur = new ControleurFX();

        // Style général du Kanban (Fond bleu type Trello)
        this.setPadding(new Insets(10));
        this.setSpacing(10);
        this.setStyle("-fx-background-color: #0079bf;");

        // Conteneur horizontal pour les colonnes (scrollable)
        containerColonnes = new HBox(15);
        ScrollPane scrollH = new ScrollPane(containerColonnes);
        scrollH.setFitToHeight(true);
        scrollH.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        // Permet au scrollpane de prendre toute la place disponible
        VBox.setVgrow(scrollH, Priority.ALWAYS);

        // Bouton pour créer une nouvelle colonne
        Button btnNouvelleColonne = new Button("+ Ajouter une liste");
        btnNouvelleColonne.setStyle("-fx-background-color: rgba(255,255,255,0.3); -fx-text-fill: white; -fx-font-weight: bold;");
        btnNouvelleColonne.setPrefHeight(40);

        btnNouvelleColonne.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Nouvelle Liste");
            dialog.setHeaderText("Nom de la nouvelle colonne :");
            dialog.setContentText("Nom :");

            dialog.showAndWait().ifPresent(nom -> {
                if (!nom.trim().isEmpty()) {
                    // Appel au contrôleur pour ajouter une Colonne au Projet
                    controleur.ajouterColonne(this.projet, nom);
                }
            });
        });

        this.getChildren().addAll(btnNouvelleColonne, scrollH);

        rafraichir();
    }

    private void rafraichir() {
        containerColonnes.getChildren().clear();

        // On itère maintenant sur les objets 'Colonne' du Projet
        for (Colonne c : projet.getColonnes()) {
            VueColonne vueColonne = new VueColonne(c);
            containerColonnes.getChildren().add(vueColonne);
        }
    }

    @Override
    public void actualiser(Sujet s) {
        rafraichir();
    }
}