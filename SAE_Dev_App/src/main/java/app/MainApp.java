import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Chargement du FXML
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/vue.fxml"));

        // Création de la scène (fenêtre)
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);

        stage.setTitle("SAE Gestion de Tâches - Itération 1");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}