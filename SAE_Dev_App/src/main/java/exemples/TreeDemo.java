package exemples;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
/*
inspiré de : https://brianyoung.blog/2018/08/23/javafx-treeview-drag-drop/
 */
public class TreeDemo extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        TreeItem<Tache> tache0 = new TreeItem<Tache>(new Tache("Tache racine"));
        TreeItem<Tache> tache1 = new TreeItem<Tache>(new Tache("Tache 1"));
        TreeItem<Tache> tache2 = new TreeItem<Tache>(new Tache("Tache 2"));
        TreeItem<Tache> tache3 = new TreeItem<Tache>(new Tache("Tache 3"));
        TreeItem<Tache> tache4 = new TreeItem<Tache>(new Tache("Tache 4"));

        tache0.getChildren().addAll(tache1, tache2, tache3);
        tache0.setExpanded(true);
        tache3.getChildren().addAll(tache4);
        tache3.setExpanded(true);

        TreeView treeView = new TreeView<>(tache0);

        treeView.setCellFactory(new Callback<TreeView<Tache>, TreeCell<Tache>>() {
            @Override
            public TreeCell call(TreeView treeView) {
                TreeCell<Tache> cell = new TreeCell<Tache>() {
                    @Override
                    protected void updateItem(Tache tache, boolean empty) {
                        // une TreeCell peut changer de tâche, donc changer de TreeItem
                        super.updateItem(tache, empty);
                        if (tache != null) {
                            setText(tache.toString());
                            setStyle(" -fx-background-color: grey ; ");
                        } else {
                            setText("");
                            setStyle("");
                        }
                    }
                };
                cell.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        System.out.println("click");
                    }
                });
                return cell;
            }
        });

        Scene scene = new Scene(treeView, 320, 240);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
