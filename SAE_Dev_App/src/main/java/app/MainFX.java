package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import modele.Projet;
import modele.SingletonTache;
import modele.TacheMere;
import vue.VueKanban;
import vue.VueListe;

public class MainFX extends Application {
    /**
    @Override
    public void start(Stage stage) {

        VueListe vue = new VueListe();

        Scene scene = new Scene(vue, 800, 500);

        stage.setTitle("Gestionnaire de tâches - Vue Liste");
        stage.setScene(scene);
        stage.show();
    }
**/

    @Override
    public void start(Stage stage) {
        Projet projet = SingletonTache.getInstance().getMesProjets().getFirst();

        VueKanban kanban = new VueKanban(projet);

        Scene scene = new Scene(kanban, 900, 600);
        stage.setTitle("Mon Kanban Trello");
        stage.setScene(scene);
        stage.show();
    }

    /**
    @Override
    public void start(Stage stage) {
        // On change ici pour utiliser la nouvelle vue
        VueSemaine vue = new VueSemaine();

        Scene scene = new Scene(vue, 800, 600); // Un peu plus large pour l'arbre
        stage.setTitle("Gestionnaire de Tâches - Vue Semaine");
        stage.setScene(scene);
        stage.show();
    }
    **/

    public static void main(String[] args) {
        launch(args);
    }
}