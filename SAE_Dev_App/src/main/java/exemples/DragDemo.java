package exemples;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/*
source : https://stackoverflow.com/questions/55379635/drag-and-drop-listeners-and-handlers-not-working-javafx
 */
public class DragDemo extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        StackPane root = new StackPane();
        Scene sc = new Scene(root, 600, 600);
        stage.setScene(sc);
        stage.show();

        HBox hb = new HBox();
        VBox imageBox = new VBox();
        Node node1 = buildNode("red");
        Node node2 = buildNode("yellow");
        imageBox.getChildren().addAll(node1,node2);

        StackPane displayBox = new StackPane();
        displayBox.setStyle("-fx-border-width:2px;-fx-border-color:black;");
        HBox.setHgrow(displayBox,Priority.ALWAYS);
        hb.getChildren().addAll(imageBox,displayBox);
        root.getChildren().add(hb);

        displayBox.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                if (dragEvent.getGestureSource() != displayBox &&
                        dragEvent.getDragboard().hasString()) {
                    dragEvent.acceptTransferModes(TransferMode.MOVE);
                }
                dragEvent.consume();
            }
        });

        displayBox.setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                if (dragEvent.getGestureSource() != displayBox && dragEvent.getDragboard().hasString()) {
                    displayBox.setStyle("-fx-border-width:2px;-fx-border-color:black;-fx-opacity:.4;-fx-background-color:" + dragEvent.getDragboard().getString());
                }
                dragEvent.consume();
            }
        });

        displayBox.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                if(!dragEvent.isAccepted()) {
                    displayBox.setStyle("-fx-border-width:2px;-fx-border-color:black;");
                    dragEvent.consume();
                }
            }
        });

        displayBox.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                Dragboard db = dragEvent.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    displayBox.setStyle("-fx-border-width:2px;-fx-border-color:black;-fx-background-color: " + db.getString());
                    success = true;
                }
                dragEvent.setDropCompleted(success);
                dragEvent.consume();
            }
        });

    }
    private Node buildNode(String color){
        StackPane node = new StackPane();
        node.setPrefSize(200,200);
        node.setStyle("-fx-background-color:"+color);
        node.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Dragboard db = node.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putImage(node.snapshot(new SnapshotParameters(), null));
                content.putString(color);
                db.setContent(content);
                mouseEvent.consume();
            }
        });
        return node;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}