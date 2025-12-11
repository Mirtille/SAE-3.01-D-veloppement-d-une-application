package vue;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import modele.Tache;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;

public class VueSemaine extends ScrollPane {

    private VBox conteneurPrincipal;
    // Une Map pour retrouver la boite du "Lundi", "Mardi", etc.
    private Map<DayOfWeek, VBox> boitesJours;

    public VueSemaine() {
        this.setFitToWidth(true); // La liste prend toute la largeur dispo
        this.setPadding(new Insets(10));

        conteneurPrincipal = new VBox(15); // Espace vertical entre les jours
        boitesJours = new HashMap<>();

        // On crée une section visuelle pour chaque jour de la semaine
        for (DayOfWeek jour : DayOfWeek.values()) {
            creerSectionJour(jour);
        }

        this.setContent(conteneurPrincipal);
    }

    private void creerSectionJour(DayOfWeek jour) {
        // 1. Le conteneur du jour
        VBox section = new VBox(5);

        // 2. Le Titre (ex: MONDAY)
        Label titreJour = new Label(jour.toString());
        titreJour.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        titreJour.setTextFill(Color.DARKBLUE);

        // 3. La boite qui contiendra les tâches de ce jour
        VBox boiteTaches = new VBox(5);
        boiteTaches.setPadding(new Insets(0, 0, 0, 15)); // Décalage vers la droite
        // Une ligne fine pour séparer
        boiteTaches.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));

        // 4. On stocke la boiteTaches pour pouvoir la remplir plus tard
        boitesJours.put(jour, boiteTaches);

        // 5. On assemble
        section.getChildren().addAll(titreJour, boiteTaches);
        conteneurPrincipal.getChildren().add(section);
    }

    // Vide tout avant de rafraîchir
    public void viderContenu() {
        for (VBox boite : boitesJours.values()) {
            boite.getChildren().clear();
        }
    }

    // Ajoute une tâche visuelle au bon endroit
    public void ajouterTacheVisuelle(Tache t) {
        if (t.getDateLimite() != null) {
            DayOfWeek jour = t.getDateLimite().getDayOfWeek();
            VBox boiteCible = boitesJours.get(jour);

            if (boiteCible != null) {
                // Création de la "Carte" (Label simple pour l'instant)
                Label carte = new Label("• " + t.getTitre() + " (" + t.getPriorite() + ")");
                carte.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 5; -fx-background-radius: 5;");
                carte.setMaxWidth(Double.MAX_VALUE);

                boiteCible.getChildren().add(carte);
            }
        }
    }
}