package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import modele.GestionnaireSauvegarde;
import vue.VueMenu; // On importe notre nouvelle classe

public class MainFX extends Application {

    @Override
    public void start(Stage stage) {


        GestionnaireSauvegarde.charger();


        VueMenu root = new VueMenu();

        Scene scene = new Scene(root, 1000, 700);
        stage.setTitle("Gestionnaire de Tâches");
        stage.setScene(scene);
        stage.show();


        stage.setOnCloseRequest(event -> {
            GestionnaireSauvegarde.sauvegarder();
            System.out.println("Données sauvegardées automatiquement");
        });
    }
    public static void main(String[] args) {
        launch(args);
    }
}