package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import vue.VueMenu; // On importe notre nouvelle classe

public class MainFX extends Application {

    @Override
    public void start(Stage stage) {
        // Tout le travail est fait dans VueMenu !
        VueMenu root = new VueMenu();

        Scene scene = new Scene(root, 1000, 700);
        stage.setTitle("Gestionnaire de Tâches");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}