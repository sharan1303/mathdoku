import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Win extends Application {
    private BorderPane background = new BorderPane();
    private Pane grid = new Pane();
    private Pane pane = new Pane();
    private Pane gridline = new Pane();
    private GridPane dummyPane = new GridPane();
    private Scene scene = new Scene(background);
    private ArrayList<TextField> textFieldArrayList = new ArrayList<>();
    private ArrayList<Rectangle> rectangleArrayList = new ArrayList<>();
    private ArrayList<Rectangle> rgbArraylist = new ArrayList<>();
    private AtomicInteger num = new AtomicInteger();
    private int width = 80;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        num.set(7);
        drawGrid();
        rgb();
        dummyPane.getChildren().addAll(gridline, pane, grid);
        background.setPadding(new Insets(10));
        background.setCenter(dummyPane);

        // Add the Scene to the Stage
        stage.setScene(scene);
        // Set the Title of the Stage
        stage.setTitle("MATHDOKU");

        stage.sizeToScene();
        // Display the Stage
        stage.show();
    }

    private void drawGrid() {
        // grid.setMaxSize(num.get() * width,num.get() * width);
        grid.setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

        for (int i = 0; i < num.get(); i++) {
            for (int j = 0; j < num.get(); j++) {
                // Grid
                Rectangle rect = new Rectangle(width, width);
                rect.setStroke(Color.BLACK);
                rect.setFill(Color.WHITE);
                rect.relocate(j * width, i * width);
                gridline.getChildren().add(rect);
                rgbArraylist.add(rect);

                BorderPane cell = new BorderPane();
                cell.setPrefSize(width, width);

                // Textfield
                TextField text = new TextField();
                textFieldArrayList.add(text);
                text.textProperty().addListener((observableValue, oldValue, newValue) -> {
                    if ((newValue.length()>1) || !"123456789".contains(newValue)){
                        text.setText(oldValue);
                    }
                });

                text.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
                text.setAlignment(Pos.CENTER);
                text.setMinSize(70,60);
                text.setStyle("-fx-text-box-border: transparent; -fx-background-color: transparent");

                cell.setBottom(text);
                cell.relocate(j * width, (i * width)-10);
                grid.getChildren().add(cell);

                // Creating rectangles for cage mistakes
                for (int col = 0; col < num.get(); col++) {
                    for (int row = 0; row < num.get(); row++) {
                        Rectangle rectangle = new Rectangle(width, width);
                        rectangle.setStroke(Color.BLACK);
                        rectangle.setFill(Color.TRANSPARENT);
                        rectangleArrayList.add(rectangle);
                        rectangle.relocate(row * width, col * width);
                        pane.getChildren().add(rectangle);
                    }
                }
            }
        }
    }

    private void rgb() {
        for (Rectangle rec : rgbArraylist) {
            Color[] palette = new Color[] { Color.RED, Color.NAVY, Color.PALETURQUOISE};
            Random rng = new Random();
            rec.setOpacity(0.5);
            Timeline time = new Timeline(new KeyFrame(Duration.millis(100), e -> rec.setFill(palette[rng.nextInt(palette.length)])));
            time.setCycleCount(Animation.INDEFINITE);
            time.setAutoReverse(false);
            time.play();
        }
    }
}
