package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import vue.VueListe;

public class MainFX extends Application {

    @Override
    public void start(Stage stage) {

        VueListe vue = new VueListe();

        Scene scene = new Scene(vue, 800, 500);

        stage.setTitle("Gestionnaire de tâches - Vue Liste");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}