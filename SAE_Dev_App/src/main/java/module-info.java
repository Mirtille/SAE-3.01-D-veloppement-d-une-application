module com.example.sae_dev_app {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens com.example.sae_dev_app to javafx.fxml;
    exports com.example.sae_dev_app;
}