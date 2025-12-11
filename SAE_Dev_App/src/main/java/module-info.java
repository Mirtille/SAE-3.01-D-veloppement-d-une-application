module com.example.sae_dev_app {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    exports exemples;
    exports controleur;
    exports data;
    exports modele;
    exports app;
    opens exemples to javafx.fxml;
    opens controleur to javafx.fxml;
    opens data to javafx.fxml;
    opens modele to javafx.fxml;
}