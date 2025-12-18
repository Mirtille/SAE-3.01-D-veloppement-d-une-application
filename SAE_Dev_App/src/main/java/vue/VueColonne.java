package vue;

import controleur.ControleurFX;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import modele.Priorite;
import modele.TacheAbstraite;
import modele.TacheMere;
import observateur.Observateur;
import observateur.Sujet;

import java.time.LocalDate;

public class VueColonne extends VBox implements Observateur {

    private TacheMere colonneDonnees;
    private ControleurFX controleur;
    private VBox containerCartes;

    public VueColonne(TacheMere colonneDonnees) {
        this.colonneDonnees = colonneDonnees;
        this.controleur = new ControleurFX();

        this.colonneDonnees.enregistrerObservateur(this);

        // Style Colonne
        this.setMinWidth(280);
        this.setMaxWidth(280);
        this.setStyle("-fx-background-color: #ebecf0; -fx-background-radius: 8;");
        this.setPadding(new Insets(10));
        this.setSpacing(10);

        Label lblTitre = new Label(colonneDonnees.getTitre());
        lblTitre.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #172b4d;");

        containerCartes = new VBox(10);
        ScrollPane scroll = new ScrollPane(containerCartes);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        // --- BOUTON AJOUTER UNE CARTE ---
        Button btnAjouterCarte = new Button("+ Ajouter une tache");
        btnAjouterCarte.setMaxWidth(Double.MAX_VALUE);

        // Bouton Supprimer
        Button btnSuppr = new Button("supprimer ×");
        btnSuppr.setStyle("-fx-background-color: transparent; -fx-text-fill: #eb5a46; -fx-font-size: 18px; -fx-cursor: hand; -fx-padding: 0 5 0 5;");
        btnSuppr.setTooltip(new Tooltip("Supprimer"));
        btnSuppr.setOnAction(e -> controleur.supprimerTache(colonneDonnees));

        btnAjouterCarte.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Nouvelle Carte");
            dialog.setHeaderText("Ajouter une tâche dans : " + colonneDonnees.getTitre());
            dialog.setContentText("Titre :");

            dialog.showAndWait().ifPresent(titre -> {
                // MODIFICATION IMPORTANTE ICI :
                // paramètre 'estDossier' à TRUE.
                // La carte sera une TacheMere, donc elle pourra contenir des sous-tâches !
                controleur.creerTache(
                        colonneDonnees,
                        titre,
                        LocalDate.now(),
                        Priorite.MOYENNE,
                        true // <--- C'est un conteneur (Carte avec sous-tâches)
                );
            });
        });

        this.getChildren().addAll(lblTitre, scroll, btnAjouterCarte, btnSuppr);

        rafraichir();
    }

    private void rafraichir() {
        containerCartes.getChildren().clear();
        for (TacheAbstraite t : colonneDonnees.getEnfants()) {
            // On ajoute la carte visuelle pour chaque enfant de la colonne
            containerCartes.getChildren().add(new VueCarte(t, controleur));
        }
    }

    @Override
    public void actualiser(Sujet s) {
        rafraichir();
    }
}