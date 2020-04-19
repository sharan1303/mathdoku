import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class Keypad extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox keypad = new VBox(10);
        keypad.setPadding(new Insets(20));
        Pane pane = new Pane(keypad);
        Scene scene = new Scene(pane, 200, 300);
        AtomicInteger num = new AtomicInteger(1);

        TextInputDialog textInput = new TextInputDialog();
        textInput.setTitle("Size of grid");
        textInput.setContentText("Please enter number of cells: ");
        Optional<String> showAndWait = textInput.showAndWait();
        showAndWait.ifPresent(s -> num.set(Integer.parseInt(s)));

        for (int i = 1; i <= num.get(); i++) {
            Button button = new Button(Integer.toString(i));
            button.setPrefSize(50,50);
            button.setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
            button.setFocusTraversable(false);
            button.setOnAction(evt -> {
                Node fo = scene.getFocusOwner();
                if (fo instanceof TextInputControl) {
                    ((TextInputControl) fo).replaceSelection(button.getText());
                }
            });
            keypad.getChildren().add(button);
            VBox.setVgrow(button, Priority.ALWAYS);
            HBox.setHgrow(button, Priority.ALWAYS);
        }
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
