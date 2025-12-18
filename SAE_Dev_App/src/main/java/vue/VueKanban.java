package vue;

import controleur.ControleurFX;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import modele.Priorite;
import modele.TacheAbstraite;
import modele.TacheMere;
import observateur.Observateur;
import observateur.Sujet;

import java.time.LocalDate;

public class VueKanban extends VBox implements Observateur {

    private TacheMere projet;
    private HBox containerColonnes;
    private ControleurFX controleur;

    public VueKanban(TacheMere projet) {
        this.projet = projet;
        this.projet.enregistrerObservateur(this);

        this.controleur = new ControleurFX();

        this.setPadding(new Insets(10));
        this.setSpacing(10);
        this.setStyle("-fx-background-color: #0079bf;");

        containerColonnes = new HBox(15);
        ScrollPane scrollH = new ScrollPane(containerColonnes);
        scrollH.setFitToHeight(true);
        scrollH.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(scrollH, Priority.ALWAYS);

        // Bouton pour créer une nouvelle colonne
        Button btnNouvelleColonne = new Button("+ Ajouter une liste");
        btnNouvelleColonne.setStyle("-fx-background-color: rgba(255,255,255,0.3); -fx-text-fill: white; -fx-font-weight: bold;");

        btnNouvelleColonne.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Nouvelle Liste");
            dialog.setHeaderText("Nom de la nouvelle colonne :");

            dialog.showAndWait().ifPresent(nom -> {
                controleur.creerTache(
                        this.projet, // On utilise le projet stocké
                        nom,
                        LocalDate.now(),
                        Priorite.MOYENNE,
                        true
                );
            });
        });

        this.getChildren().addAll(btnNouvelleColonne, scrollH);

        rafraichir();
    }

    private void rafraichir() {
        containerColonnes.getChildren().clear();

        for (TacheAbstraite t : projet.getEnfants()) {
            if (t instanceof TacheMere) {
                VueColonne colonne = new VueColonne((TacheMere) t);
                containerColonnes.getChildren().add(colonne);
            }
        }
    }

    @Override
    public void actualiser(Sujet s) {
        rafraichir();
    }
}