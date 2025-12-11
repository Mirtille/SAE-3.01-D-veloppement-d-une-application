package app;

import controleur.Controleur;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import vue.MainVue;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        // 1. Création de la Vue
        MainVue mainVue = new MainVue();

        // 2. Création du Contrôleur (on lui donne la vue)
        new Controleur(mainVue);

        // 3. Affichage
        Scene scene = new Scene(mainVue, 800, 600);
        stage.setTitle("Gestion de Tâches - Vue Semaine");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}