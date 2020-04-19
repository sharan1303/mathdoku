import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MathDokuwithsolver extends Application {

    private Button undo = new Button("Undo");
    private Button redo = new Button("Redo");
    private Button clear = new Button("Clear");
    private Button load = new Button("Load");
    private Button showMistakes = new Button("Show mistakes");
    private Button backspace = new Button("Backspace");
    private ChoiceBox<String> fontChoiceBox = new ChoiceBox<>();

    private BorderPane background = new BorderPane();
    private Pane grid = new Pane();
    private Pane pane = new Pane();
    private Pane gridline = new Pane();
    private StackPane dummyPane = new StackPane();
    private VBox keypad = new VBox(10);
    private VBox numericPad = new VBox(10);
    private Scene scene = new Scene(background);

    private Stack<String> movesStack = new Stack<>();
    private Stack<String> redoStack = new Stack<>();
    private Stack<TextField> movesStackText = new Stack<>();
    private Stack<TextField> redoStackText = new Stack<>();
    private int[][] arrayOfInput;
    private int[][] arraySolution;

    private ArrayList<String> labels = new ArrayList<>();
    private ArrayList<TextField> textFieldArrayList = new ArrayList<>();
    private ArrayList<Label> labelArrayList = new ArrayList<>();
    private ArrayList<Integer> cellPositionArr1D = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> cellPositionArr2D = new ArrayList<>();
    private ArrayList<Rectangle> rectangleArrayList = new ArrayList<>();
    private ArrayList<Rectangle> rgbArraylist = new ArrayList<>();

    private AtomicInteger num = new AtomicInteger();
    private int width = 80;
    private int fontTextField = 25;
    private int fontLabel = 15;
    private boolean win = true;
    private boolean full = true;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        arraySolution = new int[num.get()][num.get()];

        keypad.setPadding(new Insets(20));
        keypad.getChildren().add(backspace);

        VBox commands = new VBox();
        commands.setPadding(new Insets(10));

        HBox undoredo = new HBox(undo, redo);
        HBox.setHgrow(redo, Priority.ALWAYS);
        redo.setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        HBox.setHgrow(undo, Priority.ALWAYS);
        undo.setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        VBox.setVgrow(undoredo, Priority.ALWAYS);

        load.setMaxSize(100, Double.POSITIVE_INFINITY);
        VBox.setVgrow(load, Priority.ALWAYS);
        clear.setMaxSize(100, Double.POSITIVE_INFINITY);
        VBox.setVgrow(clear, Priority.ALWAYS);
        showMistakes.setMaxSize(100, Double.POSITIVE_INFINITY);
        VBox.setVgrow(showMistakes, Priority.ALWAYS);
        fontChoiceBox.setMaxSize(100, Double.POSITIVE_INFINITY);
        VBox.setVgrow(fontChoiceBox, Priority.ALWAYS);

        backspace.setPrefSize(80, 80);

        fontChoiceBox.setValue("Medium");
        fontChoiceBox.getItems().add("Small");
        fontChoiceBox.getItems().add("Medium");
        fontChoiceBox.getItems().add("Large");
        fontChoiceBox.setFocusTraversable(false);
        fontChoiceBox.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.equals("Small")){
                fontTextField = 20;
                fontLabel = 13;
            }
            if (newValue.equals("Medium")){
                fontTextField = 25;
                fontLabel = 15;
            }
            if (newValue.equals("Large")){
                fontTextField = 32;
                fontLabel = 17;
            }
            for (TextField tf : textFieldArrayList){
                tf.setFont(Font.font("Verdana", FontWeight.BOLD, fontTextField));
            }
            for (Label label : labelArrayList){
                label.setFont(Font.font("Verdana", FontWeight.BOLD, fontLabel));
            }
        });

        backspace.setFocusTraversable(false);
        backspace.setOnAction(e -> {
            Node fo = scene.getFocusOwner();
            if (fo instanceof TextInputControl)
                ((TextInputControl) fo).clear();

            movesStack.push(null);
        });

        showMistakes.setOnAction(e -> {
            checkDuplicate();
            cageMistake();
        });

        undo.setOnAction(e -> {
            if(!movesStackText.isEmpty()) {
                TextField textField = movesStackText.pop();
                redoStackText.push(textField);
                String temp = movesStack.pop();
                if (temp == null){
                    textField.setText(movesStack.pop());
                    redoStack.push(null);
                    redoStackText.pop();
                } else {
                    redoStack.push(temp);
                    textField.clear();
                }
                System.out.println(redoStack);
                System.out.println(redoStackText);
            }

            /*
            redoStack.push(movesStack.pop());
            TextField field = movesStackText.pop();
            redoStackText.push(field);
            field.clear();

             */
        });


        redo.setOnAction(e -> {
            if(!redoStack.isEmpty()) {
                String fieldContent = redoStack.pop();
                if(fieldContent == null){
                    movesStack.push(null);
                    TextField textField = redoStackText.pop();
                    textField.setText(redoStack.pop());
                } else {
                    TextField textField = redoStackText.pop();
                    textField.setText(fieldContent);
                }
            }
            /*
            String fieldContent = redoStack.pop();
            movesStack.push(fieldContent);
            TextField field = redoStackText.pop();
            movesStackText.push(field);
            field.setText(fieldContent);
             */
        });

        if (movesStack.empty())
            undo.setDisable(true);

        if (redoStack.empty())
            redo.setDisable(true);

        if (movesStack.isEmpty() && redoStack.isEmpty())
            clear.setDisable(true);

        clear.setOnAction(e ->{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to clear the board?");
            alert.setTitle("Restart Game Confirmation");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // removing any text in all textFields of the gridPane
                for (TextField textField: textFieldArrayList){
                    textField.clear();
                }

                //clearing all stacks
                movesStack.removeAllElements();
                movesStackText.removeAllElements();
                redoStack.removeAllElements();
                redoStackText.removeAllElements();
                for (int i = 0; i < num.get(); i++){
                    for (int j = 0; j < num.get(); j++){
                        arrayOfInput[i][j] = 0;
                    }
                }

                undo.setDisable(true);
                redo.setDisable(true);
                clear.setDisable(true);
            }
        });

        Button showSolution = new Button("Show Solution");
        showSolution.setOnAction(e -> {
            int ll;
            int cnt=0;
            for (Piece piece: problem){
                cnt++;
                for (Point pt: piece.points){
                    ll = pt.value;
                    pt.x = (int) (pt.value-1)/ num.get();
                    pt.y = (pt.value-1) % num.get();

                }
            }
            solveProblem();
            displayInGrid(arraySolution);
        });

        problem = new Vector<>();

        commands.getChildren().addAll(load, undoredo, clear, showMistakes, showSolution, fontChoiceBox);

        load.setOnAction(event -> {
            List<String> choices = new ArrayList<>();
            choices.add("Load from a file");
            choices.add("Load from text input");
            ChoiceDialog<String> dialog = new ChoiceDialog<String>("Load from a file", choices);
            dialog.setTitle("LOAD GAME");
            dialog.setHeaderText("Method of ");
            dialog.setContentText("Please choose method of loading:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(s -> System.out.println("You are loading the game using method: " + s));
            if (result.equals(Optional.of("Load from a file"))) {
                textFieldArrayList.clear();
                labelArrayList.clear();
                labels.clear();
                cellPositionArr1D.clear();
                cellPositionArr2D.clear();
                rectangleArrayList.clear();

                grid.getChildren().clear();
                pane.getChildren().clear();
                gridline.getChildren().clear();

                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open File to Load");
                FileChooser.ExtensionFilter txtFilter = new FileChooser.ExtensionFilter("Text files", "*.txt");
                fileChooser.getExtensionFilters().add(txtFilter);
                File file = fileChooser.showOpenDialog(stage);
                readFile(file, stage);

            } else if (result.equals(Optional.of("Load from text input"))) {
                Stage stage2 = new Stage();
                BorderPane border = new BorderPane();
                border.setPadding(new Insets(10));

                Label enter = new Label("Please input data about cages to load game: ");
                TextArea text = new TextArea();
                text.setPromptText("Format should be: target+operator+ whitespace +cells to form cage separated by comma(s) \rExample: 11+ 1,7");
                text.setFocusTraversable(false);
                text.setWrapText(true);
                text.setFont(Font.font("Verdana", 12));

                HBox buttons = new HBox(10);
                buttons.setPadding(new Insets(10, 0, 0, 0));
                Button ok = new Button("OK");
                ok.setOnAction((ActionEvent e) -> {
                    File f = new File("input.txt");
                    try {
                        f.createNewFile();
                        Writer out = new FileWriter(f);
                        out.write(text.getText());
                        out.close();
                        readFile(f, stage);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    stage2.close();
                });

                Button cancel = new Button("Cancel");
                cancel.setOnAction((ActionEvent e) -> {
                    stage2.close();
                });
                buttons.getChildren().addAll(ok, cancel);
                buttons.setAlignment(Pos.CENTER_RIGHT);

                border.setTop(enter);
                border.setCenter(text);
                border.setBottom(buttons);

                Scene scene = new Scene(border, 310, 300);
                stage2.setTitle("LOADING VIA TEXT INPUT");
                stage2.setScene(scene);
                stage2.show();

                /*
                TextInputDialog textInput = new TextInputDialog();
                textInput.setHeaderText("Difficulty of Puzzle");
                textInput.setTitle("Size of grid");
                textInput.setContentText("Please enter number of cells: ");

                Optional<String> showAndWait = textInput.showAndWait();
                if (showAndWait.isPresent()) {
                    num.set(Integer.parseInt(showAndWait.get()));
                    arrayOfInput = new int[num.get()][num.get()];
                    drawGrid();

                }

                 */

            }
        });

        num.set(6);
        drawGrid();
        arrayOfInput = new int[num.get()][num.get()];

        dummyPane.getChildren().addAll(gridline, pane, grid);

        /*
        //Construct column constraints to resize horizontaly
        ObservableList<ColumnConstraints> colCconstraints = dummyPane.getColumnConstraints();
        colCconstraints.clear();
        for(int col = 0; col < num.get(); col++){
            ColumnConstraints c = new ColumnConstraints();
            c.setHalignment(HPos.CENTER);
            c.setHgrow(Priority.ALWAYS);
            colCconstraints.add(c);
        }

        //Construct row constraints to resize vertically
        ObservableList<RowConstraints> rowCconstraints = dummyPane.getRowConstraints();
        rowCconstraints.clear();
        for(int row = 0; row < num.get(); row++){
            RowConstraints c = new RowConstraints();
            c.setValignment(VPos.CENTER);
            c.setVgrow(Priority.ALWAYS);
            rowCconstraints.add(c);
        }

         */

        dummyPane.setAlignment(Pos.CENTER);
        dummyPane.setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        grid.setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        pane.setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        gridline.setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

        GridPane.setHgrow(dummyPane, Priority.ALWAYS);
        GridPane.setHgrow(grid, Priority.ALWAYS);
        GridPane.setHgrow(gridline, Priority.ALWAYS);
        GridPane.setHgrow(pane, Priority.ALWAYS);
        GridPane.setHgrow(background, Priority.ALWAYS);
        GridPane.setVgrow(background, Priority.ALWAYS);
        GridPane.setVgrow(dummyPane, Priority.ALWAYS);
        GridPane.setVgrow(grid, Priority.ALWAYS);
        GridPane.setVgrow(gridline, Priority.ALWAYS);
        GridPane.setVgrow(pane, Priority.ALWAYS);


        background.setPadding(new Insets(10));
        background.setLeft(commands);
        keypad = keypad();
        background.setRight(keypad);
        background.setCenter(dummyPane);


        stage.setMinWidth((num.get() * width) + 250);

        // Add the Scene to the Stage
        stage.setScene(scene);
        // Set the Title of the Stage
        stage.setTitle("MATHDOKU");

        stage.sizeToScene();
        // Display the Stage
        stage.show();
    }

    private VBox keypad() {
        numericPad.getChildren().clear();
        keypad.getChildren().clear();
        keypad.getChildren().add(backspace);
        for (int i = 1; i <= num.get(); i++) {
            Button button = new Button(Integer.toString(i));
            button.setPrefSize(50, 50);
            button.setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
            button.setFocusTraversable(false);
            button.setOnAction(evt -> {
                Node fo = scene.getFocusOwner();
                if (fo instanceof TextInputControl) {
                    ((TextInputControl) fo).replaceSelection(button.getText());
                }
            });
            numericPad.getChildren().add(button);
            VBox.setVgrow(button, Priority.ALWAYS);
            HBox.setHgrow(button, Priority.ALWAYS);
        }
        keypad.getChildren().add(numericPad);
        return keypad;
    }

    private void drawGrid() {
        grid.setPrefSize(num.get() * width,num.get() * width);

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
                int finalRow = j;
                int finalCol = i;
                text.textProperty().addListener((observableValue, oldValue, newValue) -> {
                    if ((newValue.length()>1) || !"123456789".contains(newValue)){
                        text.setText(oldValue);
                    } else if (newValue.isEmpty()) {
                        arrayOfInput[finalCol][finalRow] = 0;
                    } else {
                        arrayOfInput[finalCol][finalRow] = Integer.parseInt(text.getText());
                        movesStack.push(newValue);
                        movesStackText.push(text);
                    }

                    if (movesStack.empty()) {
                        undo.setDisable(true);
                    } else {
                        undo.setDisable(false);
                    }

                    if (redoStack.empty()) {
                        redo.setDisable(true);
                    } else {
                        redo.setDisable(false);
                    }

                    if (movesStack.isEmpty() && redoStack.isEmpty()){
                        clear.setDisable(true);
                    } else {
                        clear.setDisable(false);
                    }

                    System.out.println("Actions: " + movesStack);
                    System.out.println("Undone actions: " + redoStack);
                    System.out.println(Arrays.deepToString(arrayOfInput));

                    if (textFieldArrayList.size() == num.get() && textFieldArrayList.get(finalRow + finalCol * num.get()).getText() == null)
                        full = false;
                    if (isArrayInputFull()) {
                        cageMistake();
                        checkDuplicate();
                        winningAnimation();
                    }
                });

                text.setOnKeyPressed(e ->{
                    if(e.getCode()==KeyCode.BACK_SPACE) {
                        Node fo = scene.getFocusOwner();
                        if (fo instanceof TextInputControl) {
                            ((TextInputControl) fo).clear();
                        }
                        movesStack.push(null);
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

    //Code to read into the file
    private void readFile (File file, Stage stage){
        ArrayList<Integer> allGridNumbers = new ArrayList<>();
        boolean containDuplicateGridNumber = false;

        String fileName = file.getPath();
        boolean fileFound = true;
        MathDokuReader reader = null;

        String thisLine = "";
        String label = "";
        String gridNumbersString;
        int indexOfComma;

        try {
            reader = new MathDokuReader(fileName);
        } catch (FileNotFoundException exception) {
            fileFound = false;
            System.out.println("The file " + fileName + " cannot be read/opened");
        }

        if (fileFound)
            thisLine = reader.getLine();

        while (thisLine != null) {
            int indexOfSpace = thisLine.indexOf(" ");

            String division = "÷";
            if (indexOfSpace != -1) {
                label = thisLine.substring(0, indexOfSpace);

                if (label.contains("�")) {
                    label = label.substring(0, label.length() - 1) + division;
                }

                gridNumbersString = thisLine.substring(indexOfSpace + 1);

                labels.add(label);

                indexOfComma = gridNumbersString.indexOf(",");

                while (indexOfComma != -1) {
                    if (allGridNumbers.contains(Integer.parseInt(gridNumbersString.substring(0, indexOfComma)))) {
                        containDuplicateGridNumber = true;
                        break;
                    } else {
                        allGridNumbers.add(Integer.parseInt(gridNumbersString.substring(0, indexOfComma)));
                    }
                    cellPositionArr1D.add(Integer.parseInt(gridNumbersString.substring(0, indexOfComma)));
                    gridNumbersString = gridNumbersString.substring(indexOfComma + 1);
                    indexOfComma = gridNumbersString.indexOf(",");
                }

                if (containDuplicateGridNumber) {
                    pane.getChildren().clear();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "This is not a valid file! Try again!");
                    alert.showAndWait();

                    break;
                }

                cellPositionArr1D.add(Integer.parseInt(gridNumbersString));
                allGridNumbers.add(Integer.parseInt(gridNumbersString));
                cellPositionArr1D.add(0);
            }
            assert reader != null;
            thisLine = reader.getLine();
        }
        num.set((int) Math.sqrt(Collections.max(allGridNumbers)));

        int count = 0;
        ArrayList<Integer> gridNumbers = new ArrayList<>();

        for (Integer cellPosition: cellPositionArr1D) {
            if (cellPosition != 0) {
                gridNumbers.add(cellPosition);
            } else {
                Cage cage = new Cage(labels.get(count), gridNumbers);
                Group group = cage.getShape(width, num.get());
                if (!(group == null)) {
                    group.relocate(cage.getXPositionToDraw() * width, cage.getYPositionToDraw() * width);
                    Label cageLabel = cage.getLabel();
                    labelArrayList.add(cageLabel);
                    cageLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
                    cageLabel.relocate(cage.getPositionXLabel() * width + 5, cage.getYPositionToDraw() * width + 5);
                    pane.getChildren().addAll(group, cageLabel);
                }
                gridNumbers.clear();
                count++;
            }
        }
        drawGrid();
        keypad = keypad();
        stage.setMinWidth((num.get() * width) + 250);
    }

    private void checkDuplicate(){
        int[] row = new int[num.get()];
        int[] col = new int[num.get()];
        for (int i = 0; i < num.get(); i++){
            for (int j = 0; j < num.get(); j++){
                row[j] = arrayOfInput[i][j];
                col[j] = arrayOfInput[j][i];
            }
            for (int q = 0; q < num.get(); q++){
                for (int j = q + 1; j < num.get(); j++){
                    if (row[q] == row[j] && row[q] != 0 && row[j] != 0){
                        System.out.println("Duplicate found");
                        Rectangle rec = new Rectangle(width * num.get(), width, Color.RED);
                        rec.relocate(0,i * width);
                        FadeTransition fd = new FadeTransition(Duration.millis(3000), rec);
                        fd.setFromValue(0.75);
                        fd.setToValue(0);
                        fd.setAutoReverse(true);
                        fd.play();
                        pane.getChildren().addAll(rec);
                    }

                    if (col[q] == col[j] && col[q] != 0 && col[j] != 0){
                        System.out.println("Duplicate found");
                        Rectangle rec = new Rectangle(width, width * num.get(), Color.RED);
                        rec.relocate(width * i, 0);
                        FadeTransition fd = new FadeTransition(Duration.millis(3000), rec);
                        fd.setFromValue(0.75);
                        fd.setToValue(0);
                        fd.setAutoReverse(true);
                        fd.play();
                        pane.getChildren().addAll(rec);
                    }
                }
            }
        }
    }

    private void cageMistake() {
        int arrayRow = 0;
        int arrayCol = 0;
        String target = null;
        int result = 0;
        String operation = null;

        // Populates 2D arraylist of cell positions
        cellPositionArr2D.add(new ArrayList<Integer>());
        for (Integer i : cellPositionArr1D){
            if (i != 0){
                cellPositionArr2D.get(arrayRow).add(arrayCol, i);
                arrayCol++;
            } else {
                arrayRow++;
                cellPositionArr2D.add(new ArrayList<Integer>());
                arrayCol = 0;
            }
        }

        // checks if correct values entered in cell
        int row = 0;
        boolean nullpointer = false;
        for (String label : labels){
            target = label.substring(0,(label.length() - 1));
            operation = label.substring(label.length()-1);
            System.out.println(operation);
            switch (operation) {
                case "+":
                    for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                        if (!textFieldArrayList.get(cellPositionArr2D.get(row).get(i) - 1).getText().equals("")) {
                            result = result + Integer.parseInt(textFieldArrayList.get(cellPositionArr2D.get(row).get(i) - 1).getText());
                        } else {
                            nullpointer = true;
                        }
                    }
                    System.out.println(result);
                    if ((result != Integer.parseInt(target)) || nullpointer) {
                        for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                            rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1).setFill(Color.RED);
                            rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1).setStroke(Color.BLACK);
                            rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1).setStrokeWidth(0.5);
                            FadeTransition fadeTransition = new FadeTransition(Duration.millis(3000), rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1));
                            fadeTransition.setFromValue(0.75);
                            fadeTransition.setToValue(0);
                            fadeTransition.setAutoReverse(true);
                            fadeTransition.play();
                            win = false;
                        }
                    } else {
                        for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                            rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1).setFill(Color.PALEGREEN);
                            rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1).setStroke(Color.BLACK);
                            rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1).setStrokeWidth(0.5);
                            FadeTransition fadeTransition = new FadeTransition(Duration.millis(3000), rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1));
                            fadeTransition.setFromValue(0.75);
                            fadeTransition.setToValue(0);
                            fadeTransition.setAutoReverse(true);
                            fadeTransition.play();
                            win = true;
                        }
                    }
                    nullpointer = false;
                    result = 0;
                    break;
                case "-": {
                    ArrayList<Integer> sorted = new ArrayList<>();
                    for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                        if (!textFieldArrayList.get(cellPositionArr2D.get(row).get(i) - 1).getText().equals("")) {
                            sorted.add(Integer.parseInt(textFieldArrayList.get(cellPositionArr2D.get(row).get(i) - 1).getText()));
                        }
                    }
                    Collections.sort(sorted);
                    for (Integer number : sorted) {
                        result = number - result;
                    }
                    System.out.println(result);
                    if (result != Integer.parseInt(target)) {
                        for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                            rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1).setFill(Color.RED);
                            rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1).setStroke(Color.BLACK);
                            rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1).setStrokeWidth(0.5);
                            FadeTransition fadeTransition = new FadeTransition(Duration.millis(3000), rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1));
                            fadeTransition.setFromValue(0.75);
                            fadeTransition.setToValue(0);
                            fadeTransition.setAutoReverse(true);
                            fadeTransition.play();
                            win = false;
                        }
                    }  else {
                        for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                            rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1).setFill(Color.PALEGREEN);
                            rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1).setStroke(Color.BLACK);
                            rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1).setStrokeWidth(0.5);
                            FadeTransition fadeTransition = new FadeTransition(Duration.millis(3000), rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1));
                            fadeTransition.setFromValue(0.75);
                            fadeTransition.setToValue(0);
                            fadeTransition.setAutoReverse(true);
                            fadeTransition.play();
                            win = true;
                        }
                    }
                    result = 0;
                    break;
                }
                case "x":
                    result = 1;
                    for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                        if (!textFieldArrayList.get(cellPositionArr2D.get(row).get(i) - 1).getText().equals("")) {
                            result = result * Integer.parseInt(textFieldArrayList.get(cellPositionArr2D.get(row).get(i) - 1).getText());
                        }
                    }
                    System.out.println(result);
                    if (result != Integer.parseInt(target)) {
                        for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                            rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1).setFill(Color.RED);
                            rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1).setStroke(Color.BLACK);
                            rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1).setStrokeWidth(0.5);
                            FadeTransition fadeTransition = new FadeTransition(Duration.millis(3000), rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1));
                            fadeTransition.setFromValue(0.75);
                            fadeTransition.setToValue(0);
                            fadeTransition.setAutoReverse(true);
                            fadeTransition.play();
                            win=false;
                        }
                    }  else {
                        for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                            rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1).setFill(Color.PALEGREEN);
                            rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1).setStroke(Color.BLACK);
                            rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1).setStrokeWidth(0.5);
                            FadeTransition fadeTransition = new FadeTransition(Duration.millis(3000), rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1));
                            fadeTransition.setFromValue(0.75);
                            fadeTransition.setToValue(0);
                            fadeTransition.setAutoReverse(true);
                            fadeTransition.play();
                            win = true;
                        }
                    }
                    result = 0;
                    break;
                case "÷": {
                    ArrayList<Integer> sorted = new ArrayList<>();
                    result = 1;
                    for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                        if (!textFieldArrayList.get(cellPositionArr2D.get(row).get(i) - 1).getText().equals("")) {
                            sorted.add(Integer.parseInt(textFieldArrayList.get(cellPositionArr2D.get(row).get(i) - 1).getText()));
                        }
                    }
                    System.out.println(sorted);
                    Collections.sort(sorted);
                    System.out.println(sorted);
                    for (Integer number : sorted) {
                        result = number / result;
                    }
                    System.out.println(result);
                    if (result != Integer.parseInt(target)) {
                        for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                            rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1).setFill(Color.RED);
                            rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1).setStroke(Color.BLACK);
                            rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1).setStrokeWidth(0.5);
                            FadeTransition fadeTransition = new FadeTransition(Duration.millis(3000), rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1));
                            fadeTransition.setFromValue(0.75);
                            fadeTransition.setToValue(0);
                            fadeTransition.setAutoReverse(true);
                            fadeTransition.play();
                            win=false;
                        }
                    } else {
                        for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                            rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1).setFill(Color.PALEGREEN);
                            rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1).setStroke(Color.BLACK);
                            rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1).setStrokeWidth(0.5);
                            FadeTransition fadeTransition = new FadeTransition(Duration.millis(3000), rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1));
                            fadeTransition.setFromValue(0.75);
                            fadeTransition.setToValue(0);
                            fadeTransition.setAutoReverse(true);
                            fadeTransition.play();
                            win = true;
                        }
                    }
                    result = 0;
                    break;
                }
            }
            row++;
        }

        System.out.println(cellPositionArr2D);
        System.out.println(labels);
        System.out.println(textFieldArrayList.size());

        cellPositionArr2D.clear();
    }

    private boolean isArrayInputFull() {
        for (int i = 0; i < num.get(); i++) {
            for (int j = 0; j < num.get(); j++) {
                if (arrayOfInput[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private void rgb() {
        for (Rectangle rec : rgbArraylist){
            Color[] palette = new Color[] { Color.RED, Color.BLUE, Color.PALETURQUOISE, Color.AZURE, Color.YELLOW};
            Random rng = new Random();
            rec.setOpacity(0.5);
            Timeline time = new Timeline(new KeyFrame(Duration.millis(100), e -> rec.setFill(palette[rng.nextInt(palette.length)])));
            time.setCycleCount(Animation.INDEFINITE);
            time.setAutoReverse(false);
            time.play();
        }
    }

    private void winningAnimation(){
        for (TextField tf: textFieldArrayList) {
            String s = tf.getText();
            if (s == null) {
                full = false;
                break;
            }
        }
        if (win && full) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "YOU WIN THE GAME!!!!");
            alert.setTitle("AND IT'S A WIN!");
            alert.setHeaderText("CONGRATULATIONS!");
            alert.showAndWait();
            rgb();
        }
    }

    private boolean hasCellMistake() {
        boolean error = false;
        int arrayRow = 0;
        int arrayCol = 0;
        String target = null;
        int result = 0;
        String operation = null;

        // Populates 2D arraylist of cell positions
        cellPositionArr2D.add(new ArrayList<Integer>());
        for (Integer i : cellPositionArr1D){
            if (i != 0){
                cellPositionArr2D.get(arrayRow).add(arrayCol, i);
                arrayCol++;
            } else {
                arrayRow++;
                cellPositionArr2D.add(new ArrayList<Integer>());
                arrayCol = 0;
            }
        }

        // checks if correct values entered in cell
        int row = 0;
        boolean nullpointer = false;
        for (String label : labels){
            target = label.substring(0,(label.length() - 1));
            operation = label.substring(label.length()-1);
            System.out.println(operation);
            switch (operation) {
                case "+":
                    for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                        if (!textFieldArrayList.get(cellPositionArr2D.get(row).get(i) - 1).getText().equals("")) {
                            result = result + Integer.parseInt(textFieldArrayList.get(cellPositionArr2D.get(row).get(i) - 1).getText());
                        } else {
                            nullpointer = true;
                        }
                    }
                    System.out.println(result);
                    if ((result != Integer.parseInt(target)) || nullpointer) {
                        for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                            error = true;
                        }
                    } else {
                        for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                            error = true;
                        }
                    }
                    nullpointer = false;
                    result = 0;
                    break;
                case "-": {
                    ArrayList<Integer> sorted = new ArrayList<>();
                    for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                        if (!textFieldArrayList.get(cellPositionArr2D.get(row).get(i) - 1).getText().equals("")) {
                            sorted.add(Integer.parseInt(textFieldArrayList.get(cellPositionArr2D.get(row).get(i) - 1).getText()));
                        }
                    }
                    Collections.sort(sorted);
                    for (Integer number : sorted) {
                        result = number - result;
                    }
                    System.out.println(result);
                    if (result != Integer.parseInt(target)) {
                        for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                            error = true;
                        }
                    }
                    result = 0;
                    break;
                }
                case "x":
                    result = 1;
                    for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                        if (!textFieldArrayList.get(cellPositionArr2D.get(row).get(i) - 1).getText().equals("")) {
                            result = result * Integer.parseInt(textFieldArrayList.get(cellPositionArr2D.get(row).get(i) - 1).getText());
                        }
                    }
                    System.out.println(result);
                    if (result != Integer.parseInt(target)) {
                        for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                            error = true;
                        }
                    }
                    result = 0;
                    break;
                case "÷": {
                    ArrayList<Integer> sorted = new ArrayList<>();
                    result = 1;
                    for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                        if (!textFieldArrayList.get(cellPositionArr2D.get(row).get(i) - 1).getText().equals("")) {
                            sorted.add(Integer.parseInt(textFieldArrayList.get(cellPositionArr2D.get(row).get(i) - 1).getText()));
                        }
                    }
                    for (Integer number : sorted) {
                        result = number / result;
                    }
                    if (result != Integer.parseInt(target)) {
                        for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                            error = true;
                        }
                    }
                    result = 0;
                    break;
                }
            }
        }

        System.out.println(cellPositionArr2D);
        System.out.println(labels);
        System.out.println(textFieldArrayList.size());

        cellPositionArr2D.clear();

        return error;
    }

    private boolean hasDuplicate() {
        int[] row = new int[num.get()];
        int[] col = new int[num.get()];
        boolean dup = false;
        for (int i = 0; i < num.get(); i++) {
            for (int j = 0; j < num.get(); j++) {
                row[j] = arrayOfInput[i][j];
                col[j] = arrayOfInput[j][i];
            }
            for (int q = 0; q < num.get(); q++) {
                for (int j = q + 1; j < num.get(); j++) {
                    if (row[q] == row[j] && row[q] != 0 && row[j] != 0)
                        dup = true;

                    if (col[q] == col[j] && col[q] != 0 && col[j] != 0)
                        dup = true;
                }
            }
        }
        return dup;
    }

    static class Point {   //one point in matrix
        int value;
        int x;
        int y;

        Point(int zz, int a, int b) {
            value = zz;
            x = a;
            y = b;
        }

        void output() {
            System.out.println("Value " + value + " (" + x + "," + y + ")");
        }
    }

    static class Piece {  //one computation piece in Mathdoku
        Op op;
        int num;
        ArrayList<Point> points;

        Piece(Op o, int n, ArrayList<Point> p) {
            op = o;
            num = n;
            points = p;
        }

        void output() {
            System.out.println("Piece: " + op + "," + num + ",");
            for (Point p : points) {
                p.output();
            }
        }
    }

    enum Op {ADD, SUB, MUL, DIV, NOP}  //operator in one piece

    static Vector<Piece> problem; //a MathDoku problem, consisting of computation pieces


    public void inputInProblem(String result, ArrayList<Integer> listOfPoints) {
        int total;
        String operator;
        Op op = null;

        ArrayList<Point> pts;
        pts = new ArrayList<Point>();

        operator = result.substring(result.length() - 1);
        total = Integer.parseInt(result.substring(0, result.length() - 1));

        for (Integer listOfPoint : listOfPoints) {
            Point point = new Point(listOfPoint, 0, 0);
            pts.add(point);
        }

        switch (operator) {
            case "+":
                op = Op.ADD;
                break;
            case "-":
                op = Op.SUB;
                break;
            case "x":
                op = Op.MUL;
                break;
            case "÷":
                op = Op.DIV;
                break;
            default:
                System.out.println(operator + " is not a correct operator\n");
                assert (false);
        }

        Piece p = new Piece(op, total, pts);
        p.output();
        problem.add(p);
    }

    static HashSet<int[][]> all; //all solutions
    static int num_sol = 0; //number of solutions
    static int[][] cand;  //the current solution candidate under trying, which is a matrix
    static States[][] states;
    static int[] init_vals;
    static Problem_at[][] matchset;

    public void solveProblem() {

        cand = new int[num.get()][num.get()];
        all = new HashSet<int[][]>();

        if(problem != null){ //find the quests that are ready to match against at each point
            problem_opt(problem);
        }

        init_states();

        find_all(0, 0); //find all solutions in this recursive function starting from point (0, 0)

        System.out.println("\n\nTotal solution #: " + num_sol);
    }

    void init_states() {
        states = new States[ num.get()][num.get()];
        for(int i=0; i< num.get(); i++){
            for(int j=0; j< num.get(); j++){
                states[i][j] = new States();
            }
        }

        init_vals = new int[ num.get()];
        for(int i=0; i< num.get(); i++){  //initialize the default available values (i.e., all of them)
            init_vals[i] = 1;
        }

    }

    void solcopy(int[][] s, int[][] t){
        for(int i=0; i< num.get(); i++){
            for(int j=0; j<num.get(); j++)
                t[i][j] = s[i][j];
        }
    }

    void problem_opt(Vector<Piece> prob)
    {
        matchset = new Problem_at[num.get()][num.get()];
        for(int i=0; i< num.get(); i++){
            for(int j=0; j< num.get(); j++){
                matchset[i][j] = new Problem_at(false, null);
            }
        }

        for(Piece piece: prob){
            Point max_point = find_max(piece.points);
            matchset[max_point.x][max_point.y] = new Problem_at(true, piece);

            //System.out.println(max_point.x + " : " + max_point.y);
            //matchset[max_point.x][max_point.y].piece.output();
        }
    }

    void find_all(int s, int t) {
        //        System.out.println("Computing point: (" + s + "," + t + ")  before");
        //        output_states();


        if (!states[s][t].inited) { //only initialize when compute the same point first time

            states[s][t].avails = new int[ num.get()];
            states[s][t].col_avails = new int[ num.get()];
            states[s][t].row_avails = new int[ num.get()];
            states[s][t].inited = true;
        }

        int[] avail = states[s][t].avails;
        int[] col_avail = states[s][t].col_avails;
        int[] row_avail = states[s][t].row_avails;

        int[] last_col_avail, last_row_avail;
        last_col_avail = last_row_avail = init_vals;  //by default, all values are available
        if (s != 0)
            last_col_avail = states[s - 1][t].col_avails; //not first row, inherit col_avails from point of (last_row, same_col)
        if (t != 0)
            last_row_avail = states[s][t - 1].row_avails; //not first col, inherit row_avails from point of (same_row, last_col)

        //only the values avaliable to both column and row directions are avaliable to point (s, t)
        for (int k = 0; k <  num.get(); k++) {
            avail[k] = 0;  //clear it (may save value in last compute of the same point
            if (last_row_avail[k] == 1 && last_col_avail[k] == 1) {
                avail[k] = 1;  //value k+1 is available (setting element k to 1)
            }

            col_avail[k] = last_col_avail[k]; //inherit
            row_avail[k] = last_row_avail[k];
        }

        for (int i = 0; i < num.get(); i++) {
            if (avail[i] == 0) continue;
            cand[s][t] = i + 1;   //fill point(s,t) with a valid value i

            if (problem != null && matchset[s][t].exist) { //enable a piece to match against
                boolean ok = match_piece(cand, matchset[s][t].piece);
                if (!ok) { //does not match, give up this value
                    cand[s][t] = 0;
                    continue;
                }
            }

            col_avail[i] = 0;   //mark value i as unavailable
            row_avail[i] = 0;

            if (t + 1 ==  num.get() && s + 1 != num.get()) {
                find_all(s + 1, 0);  //last column while not last row, wrap around to continue next row
            } else if (t + 1 !=  num.get()) {
                find_all(s, t + 1);   //not last column, continue in next column
            } else {                   //(s,t) is in last column and last row. finish this solution finding
                //once come to this branch, the function will return
                int[][] sol = new int[ num.get()][ num.get()];
                solcopy(cand, sol); //copy the solution
                num_sol++;
                output_one(sol);
                // jes display in grid;
                //displayInGrid(sol);
                populateSolutionArray(sol);
                if (problem != null) {
                    all.add(sol);
                }
            }

            cand[s][t] = 0; //restore zero
            col_avail[i] = 1;  //restore the available value used by last iteration
            row_avail[i] = 1;
        }
    }

    void output_one(int[][] one){

        System.out.printf("\n\nFound a solution: %dx%d\n", num.get(), num.get());
        for(int i=0; i<num.get(); i++){
            for(int j=0; j<num.get(); j++){
                System.out.printf("%2d ", one[i][j]);
            }
            System.out.println(" ");
        }
    }

    static class Problem_at{
        boolean exist;
        Piece piece;
        Problem_at(boolean e, Piece p)
        {
            exist = e;
            piece = p;
        }
    }

    static Point find_max(ArrayList<Point> points)
    {
        Point max = new Point(0,0,0);
        for( Point point: points){
            if( max.x == point.x){
                if( max.y < point.y ) max.y = point.y;
            }else if ( max.x < point.x) {
                max.x = point.x;
                max.y = point.y;
            }
        }
        return max;
    }
    static boolean match_piece(int[][] sol, Piece quest)
    {
        boolean ok = false;
        switch(quest.op){
            case ADD:
                ok = match_add(sol, quest);
                break;
            case SUB:
                ok = match_sub(sol, quest);
                break;
            case MUL:
                ok = match_mul(sol, quest);
                break;
            case DIV:
                ok = match_div(sol, quest);
                break;
            case NOP:
                ok = match_nop(sol, quest);
                break;
            default:
                assert(false);
        }
        return ok;

    }

    static boolean match_add(int[][] sol, Piece quest){
        int sum = 0;
        for(Point p : quest.points){
            sum += sol[p.x][p.y];
        }
        return (sum == quest.num);
    }

    static boolean match_sub(int[][] sol, Piece quest){
        Point p0 = quest.points.get(0);
        Point p1 = quest.points.get(1);

        int v1 = sol[p0.x][p0.y];
        int v2 = sol[p1.x][p1.y];

        return ((v1-v2) == quest.num) || ((v2-v1) == quest.num);
    }

    static boolean match_mul(int[][] sol, Piece quest){
        int prod = 1;
        for(Point p : quest.points){
            prod *= sol[p.x][p.y];
        }
        return (prod == quest.num);
    }

    static boolean match_div(int[][] sol, Piece quest){
        Point p0 = quest.points.get(0);
        Point p1 = quest.points.get(1);

        int v1 = sol[p0.x][p0.y];
        int v2 = sol[p1.x][p1.y];

        return ((v1/v2) == quest.num) || ((v2/v1) == quest.num);
    }

    static boolean match_nop(int[][] sol, Piece quest){
        Point p = quest.points.get(0);
        return (quest.num == sol[p.x][p.y]);
    }

    void displayInGrid(int[][] solu){
        int cnt=1;
        for (TextField textField : textFieldArrayList) {
            int thenumb =  solu[(cnt-1)/num.get()][(cnt-1)% num.get()];
            textField.setText(String.valueOf(thenumb));
            cnt++;
        }
    }

    private void populateSolutionArray(int[][] solu){
        int cnt=1;
        for (int i=0; i<num.get(); i++) {
            for(int j=0; j<num.get(); j++) {
                int thenumb = solu[(cnt - 1) / num.get()][(cnt - 1) %num.get()];
                arraySolution[i][j]=thenumb;
                cnt++;
            }
        }
    }

    static class States{
        int[] avails;       //array recording the available values that can be used by the point
        int[] col_avails;   //array recording the used values in the same column of ths point
        int[] row_avails;   //array recording the used values in the same row of ths point
        boolean inited;
        void output(){
            System.out.println("    row avail:" + Arrays.toString(row_avails));
            System.out.println("    col avail:" + Arrays.toString(col_avails));
            System.out.println("    avail val:" + Arrays.toString(avails));
        }
    }
}
