import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

public class MathDoku extends Application {

    private Button undo = new Button("UNDO");
    private Button redo = new Button("REDO");
    private Button clear = new Button("CLEAR");
    private Button load = new Button("LOAD");
    private Button showMistakes = new Button("CHECK");
    private Button backspace = new Button("Backspace");
    private ChoiceBox<String> fontChoiceBox = new ChoiceBox<>();
    private Button solver = new Button("SOLVE");
    private Button hint = new Button("HINT");

    private BorderPane background = new BorderPane();
    private Pane grid = new Pane();
    private Pane pane = new Pane();
    private Pane gridline = new Pane();
    private StackPane dummyPane = new StackPane();
    private VBox keypad = new VBox(10);
    private VBox numericPad = new VBox(10);
    private VBox commands = new VBox(10);
    private HBox actions = new HBox(undo, redo, clear);
    private Scene scene = new Scene(background);

    private Stack<String> movesStack = new Stack<>();
    private Stack<String> redoStack = new Stack<>();
    private Stack<TextField> movesStackText = new Stack<>();
    private Stack<TextField> redoStackText = new Stack<>();

    private int[][] arrayOfInput;
    private ArrayList<String> StringLabels = new ArrayList<>();
    private ArrayList<TextField> textFieldArrayList = new ArrayList<>();
    private ArrayList<Label> labels = new ArrayList<>();
    private ArrayList<Integer> cellPositionArr1D = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> cellPositionArr2D = new ArrayList<>();
    private ArrayList<Rectangle> rectangles = new ArrayList<>();
    private ArrayList<Rectangle> animationRectangles = new ArrayList<>();
    private ArrayList<Boolean> listBoolean = new ArrayList<>();
    private ArrayList<Integer> solutionArrayList = new ArrayList<>();
    private ArrayList<int[][]> listOfPossibleSolutions = new ArrayList<>();

    private AtomicInteger num = new AtomicInteger();
    private int width = 80;
    private int fontTextField = 25;
    private int fontLabel = 15;
    private boolean win = true;
    private boolean full = true;
    private String difficulty;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        keypad.setPadding(new Insets(10));
        keypad.getChildren().add(backspace);
        commands.setPadding(new Insets(10));
        undo.setPadding(new Insets(10));
        redo.setPadding(new Insets(10));
        clear.setPadding(new Insets(10));
        actions.setPadding(new Insets(10));
        actions.setSpacing(20);
        actions.setAlignment(Pos.CENTER);

        HBox.setHgrow(redo, Priority.ALWAYS);
        HBox.setHgrow(clear, Priority.ALWAYS);
        HBox.setHgrow(undo, Priority.ALWAYS);
        HBox.setHgrow(actions, Priority.ALWAYS);
        VBox.setVgrow(actions, Priority.ALWAYS);

        VBox.setVgrow(load, Priority.ALWAYS);
        VBox.setVgrow(showMistakes, Priority.ALWAYS);
        VBox.setVgrow(fontChoiceBox, Priority.ALWAYS);
        VBox.setVgrow(hint, Priority.ALWAYS);
        VBox.setVgrow(solver, Priority.ALWAYS);

        backspace.setPrefSize(80, 80);
        redo.setMaxSize(100, Double.POSITIVE_INFINITY);
        undo.setMaxSize(100, Double.POSITIVE_INFINITY);
        clear.setMaxSize(100, Double.POSITIVE_INFINITY);
        load.setMaxSize(100, Double.POSITIVE_INFINITY);
        showMistakes.setMaxSize(100, Double.POSITIVE_INFINITY);
        fontChoiceBox.setMaxSize(100, Double.POSITIVE_INFINITY);
        solver.setMaxSize(100, Double.POSITIVE_INFINITY);
        hint.setMaxSize(100, Double.POSITIVE_INFINITY);

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

            for (TextField tf : textFieldArrayList)
                tf.setFont(Font.font("Verdana", FontWeight.BOLD, fontTextField));

            for (Label label : labels)
                label.setFont(Font.font("Verdana", FontWeight.BOLD, fontLabel));
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

        // UNDO, REDO, CLEAR FUNCTIONALITIES
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
        });

        clear.setOnAction(e ->{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to clear the board?");
            alert.setTitle("Restart Game Confirmation");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                for (TextField textField: textFieldArrayList)
                    textField.clear();

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
                animationRectangles.clear();

                undo.setDisable(true);
                redo.setDisable(true);
                clear.setDisable(true);
            }
        });

        // Disabling buttons accordingly
        if (movesStack.empty())
            undo.setDisable(true);

        if (redoStack.empty())
            redo.setDisable(true);

        if (movesStack.isEmpty() && redoStack.isEmpty())
            clear.setDisable(true);

        // SOLVER PART 9
        hint.setOnAction(e -> {
            if (solutionArrayList.isEmpty())
                solutionArrayList = solver(false);

            ArrayList<Integer> indexOfAvailableTextField = new ArrayList<>();
            int count=0;
            for (TextField textField : textFieldArrayList) {
                if (textField.getText().equals(""))
                    indexOfAvailableTextField.add(count);
                count++;
            }

            Random rand = new Random();
            int random = rand.nextInt(indexOfAvailableTextField.size());
            int index = indexOfAvailableTextField.get(random);
            int hint = solutionArrayList.get(index);

            textFieldArrayList.get(index).setText(String.valueOf(hint));
            Timeline time = new Timeline(new KeyFrame(Duration.millis(2500), exp -> textFieldArrayList.get(index).clear()));
            time.play();
        });

        solver.setOnAction(e -> solver(true));

        // LOADING GAME OPERATIONS
        // CONTAINS LOADING FROM FILE, TEXT INPUT & RANDOM GAME GENERATOR
        load.setOnAction(event -> {
            List<String> choices = new ArrayList<>();
            choices.add("Load game from a file");
            choices.add("Load game from text input");
            choices.add("Random Game Generator");
            ChoiceDialog<String> dialog = new ChoiceDialog<>("Load game from a file", choices);
            dialog.setTitle("LOAD GAME");
            dialog.setHeaderText("Method of ");
            dialog.setContentText("Please choose method of loading:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(s -> System.out.println("Loading game using method: " + s));

            if (result.equals(Optional.of("Load game from a file"))) {
                textFieldArrayList.clear();
                labels.clear();
                StringLabels.clear();
                cellPositionArr1D.clear();
                cellPositionArr2D.clear();
                rectangles.clear();

                grid.getChildren().clear();
                pane.getChildren().clear();
                gridline.getChildren().clear();

                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open File to Load");
                FileChooser.ExtensionFilter txtFilter = new FileChooser.ExtensionFilter("Text files", "*.txt");
                fileChooser.getExtensionFilters().add(txtFilter);
                File file = fileChooser.showOpenDialog(stage);
                readFile(file, stage);
            } else if (result.equals(Optional.of("Load game from text input"))) {
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
                cancel.setOnAction((ActionEvent e) -> stage2.close());
                buttons.getChildren().addAll(ok, cancel);
                buttons.setAlignment(Pos.CENTER_RIGHT);

                border.setTop(enter);
                border.setCenter(text);
                border.setBottom(buttons);

                Scene scene = new Scene(border, 310, 300);
                stage2.setTitle("LOADING VIA TEXT INPUT");
                stage2.setScene(scene);
                stage2.show();
            } else {
                FlowPane centre = new FlowPane();
                BorderPane randomGamePane = new BorderPane();
                randomGamePane.setPadding(new Insets(10));
                Scene randomGameScene = new Scene(randomGamePane,200,150);
                Stage randomGameStage = new Stage();
                randomGameStage.setScene(randomGameScene);

                Label label = new Label("Select grid size:");
                label.setPadding(new Insets(10));

                RadioButton radioButton2 = new RadioButton("2x2");
                RadioButton radioButton3 = new RadioButton("3x3");
                RadioButton radioButton4 = new RadioButton("4x4");
                RadioButton radioButton5 = new RadioButton("5x5");
                RadioButton radioButton6 = new RadioButton("6x6");
                RadioButton radioButton7 = new RadioButton("7x7");
                RadioButton radioButton8 = new RadioButton("8x8");

                radioButton2.setSelected(true);

                ToggleGroup toggle = new ToggleGroup();
                radioButton2.setToggleGroup(toggle);
                radioButton3.setToggleGroup(toggle);
                radioButton4.setToggleGroup(toggle);
                radioButton5.setToggleGroup(toggle);
                radioButton6.setToggleGroup(toggle);
                radioButton7.setToggleGroup(toggle);
                radioButton8.setToggleGroup(toggle);

                centre.setHgap(5);
                centre.getChildren().addAll(radioButton2,radioButton3,radioButton4,radioButton5,radioButton6,radioButton7, radioButton8);

                ChoiceBox<String> difficultyChoiceBox = new ChoiceBox<>();
                difficultyChoiceBox.setValue("Easy");
                difficultyChoiceBox.getItems().add("Easy");
                difficultyChoiceBox.getItems().add("Normal");
                difficultyChoiceBox.getItems().add("Hard");

                HBox difficultyHBox = new HBox(5);
                Label label1 = new Label ("Select difficulty :");
                difficultyHBox.getChildren().addAll(label1,difficultyChoiceBox);
                difficultyHBox.setAlignment(Pos.CENTER);
                difficultyHBox.setPadding(new Insets(10));
                centre.getChildren().add(difficultyHBox);

                Button generateButton = new Button("Generate");
                generateButton.setOnAction(e -> {
                    textFieldArrayList.clear();
                    labels.clear();
                    StringLabels.clear();
                    cellPositionArr1D.clear();
                    cellPositionArr2D.clear();
                    rectangles.clear();

                    grid.getChildren().clear();
                    pane.getChildren().clear();
                    gridline.getChildren().clear();

                    RadioButton selectedRadioButton = (RadioButton) toggle.getSelectedToggle();
                    String toogleGroupValue = selectedRadioButton.getText();

                    num.set(Integer.parseInt(toogleGroupValue.substring(0, 1)));

                    randomGameStage.close();

                    drawGrid();
                    keypad = keypad();

                    difficulty = difficultyChoiceBox.getValue();

                    switch (difficulty) {
                        case "Easy":
                        case "Normal":
                        case "Hard":
                            generateRandom();
                            break;
                    }

                    arrayOfInput = new int[num.get()][num.get()];

                    for (int i = 0; i < num.get(); i++) {
                        for (int j = 0; j < num.get(); j++)
                            arrayOfInput[i][j] = 0;
                    }
                });
                Button cancelButton = new Button("Cancel");
                cancelButton.setOnAction(e -> randomGameStage.close());

                HBox hBox = new HBox(generateButton,cancelButton);
                hBox.setAlignment(Pos.CENTER_RIGHT);
                hBox.setSpacing(10);

                randomGamePane.setTop(label);
                randomGamePane.setCenter(centre);
                randomGamePane.setBottom(hBox);

                randomGameStage.setMinWidth(350);
                randomGameStage.setMinHeight(180);
                randomGameStage.setTitle("Random Game Generator");
                randomGameStage.show();
            }
        });

        num.set(6);
        drawGrid();
        arrayOfInput = new int[num.get()][num.get()];

        dummyPane.getChildren().addAll(gridline, pane, grid);
        commands.getChildren().addAll(load, showMistakes, fontChoiceBox, hint, solver);
        keypad = keypad();

        dummyPane.setAlignment(Pos.CENTER);
        dummyPane.setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        grid.setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        pane.setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        gridline.setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

        background.setPadding(new Insets(10));

        background.setLeft(commands);
        background.setTop(actions);
        background.setRight(keypad);
        background.setCenter(dummyPane);

        stage.setMinWidth((num.get() * width) + 240);
        stage.setMinHeight((num.get() * width) + 120);

        stage.setScene(scene);
        stage.setTitle("MATHDOKU");
        stage.sizeToScene();
        stage.show();
    }

    private VBox keypad() {
        numericPad.getChildren().clear();
        keypad.getChildren().clear();
        keypad.getChildren().add(backspace);
        for (int i = 1; i <= num.get(); i++) {
            Button button = new Button(Integer.toString(i));
            button.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
            button.setPrefSize(50, 50);
            button.setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
            button.setFocusTraversable(false);
            button.setOnAction(evt -> {
                Node fo = scene.getFocusOwner();
                if (fo instanceof TextInputControl)
                    ((TextInputControl) fo).replaceSelection(button.getText());
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
                rectangles.add(rect);

                StackPane cell = new StackPane();
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

                    if (movesStack.empty())
                        undo.setDisable(true);
                    else
                        undo.setDisable(false);

                    if (redoStack.empty())
                        redo.setDisable(true);
                    else
                        redo.setDisable(false);

                    if (movesStack.isEmpty() && redoStack.isEmpty())
                        clear.setDisable(true);
                    else
                        clear.setDisable(false);

                    /*
                    System.out.println("Actions: " + movesStack);
                    System.out.println("Undone actions: " + redoStack);
                    System.out.println(Arrays.deepToString(arrayOfInput));
                     */

                    if (textFieldArrayList.size() == num.get() && textFieldArrayList.get(finalRow + finalCol * num.get()).getText() == null)
                        full = false;
                    if (isArrayInputFull()) {
                        cageMistake();
                        checkDuplicate();
                        winningAnimation();
                    }
                });

                text.setOnKeyPressed(e ->{
                    if (e.getCode()==KeyCode.BACK_SPACE) {
                        Node fo = scene.getFocusOwner();
                        if (fo instanceof TextInputControl)
                            ((TextInputControl) fo).clear();
                        movesStack.push(null);
                    }
                });

                text.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
                text.setAlignment(Pos.CENTER);
                text.setMinSize(70,60);
                text.setStyle("-fx-text-box-border: transparent; -fx-background-color: transparent");

                cell.getChildren().add(text);
                cell.relocate(j * width, (i * width)-10);
                grid.getChildren().add(cell);

                // Creating rectangles for cage mistakes
                for (int col = 0; col < num.get(); col++) {
                    for (int row = 0; row < num.get(); row++) {
                        Rectangle rectangle = new Rectangle(width, width);
                        rectangle.setStroke(Color.BLACK);
                        rectangle.setFill(Color.TRANSPARENT);
                        animationRectangles.add(rectangle);
                        rectangle.relocate(row * width, col * width);
                        pane.getChildren().add(rectangle);
                    }
                }
            }
        }
        Rectangle bigrect = new Rectangle(width * num.get(), width * num.get());
        bigrect.setFill(Color.TRANSPARENT);
        bigrect.setStroke(Color.BLACK);
        bigrect.setStrokeWidth(4);
        bigrect.relocate(1,1);
        gridline.getChildren().add(bigrect);
    }

    //Code to read into the file
    private void readFile (File file, Stage stage){
        ArrayList<Integer> allGridNumbers = new ArrayList<>();
        boolean containDuplicateGridNumber = false;

        String fileName = file.getPath();
        boolean fileFound = true;
        Reader reader = null;

        String thisLine = "";
        String label;
        String gridNumbersString;
        int indexOfComma;

        try {
            reader = new Reader(fileName);
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

                StringLabels.add(label);

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
                CageLining cage = new CageLining(StringLabels.get(count), gridNumbers);
                Group group = cage.getShape(width, num.get());
                if (!(group == null)) {
                    group.relocate(cage.getXPositionToDraw() * width, cage.getYPositionToDraw() * width);
                    Label cageLabel = cage.getLabel();
                    labels.add(cageLabel);
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
        String target;
        int result = 0;
        String operation;

        // Populates 2D arraylist of cell positions
        cellPositionArr2D.add(new ArrayList<>());
        for (Integer i : cellPositionArr1D){
            if (i != 0){
                cellPositionArr2D.get(arrayRow).add(arrayCol, i);
                arrayCol++;
            } else {
                arrayRow++;
                cellPositionArr2D.add(new ArrayList<>());
                arrayCol = 0;
            }
        }

        // checks if correct values entered in cell
        int row = 0;
        boolean nullpointer = false;
        for (String label : StringLabels){
            if (label.length() == 1) {
                target = label;
                operation = "";
            } else {
                target = label.substring(0,(label.length() - 1));
                operation = label.substring(label.length()-1);
            }
            switch (operation) {
                case "+":
                    for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                        if (!textFieldArrayList.get(cellPositionArr2D.get(row).get(i) - 1).getText().equals(""))
                            result = result + Integer.parseInt(textFieldArrayList.get(cellPositionArr2D.get(row).get(i) - 1).getText());
                        else
                            nullpointer = true;
                    }
                    if ((result != Integer.parseInt(target)) || nullpointer) {
                        for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                            rectangles.get(cellPositionArr2D.get(row).get(i) - 1).setFill(Color.RED);
                            rectangles.get(cellPositionArr2D.get(row).get(i) - 1).setStroke(Color.BLACK);
                            rectangles.get(cellPositionArr2D.get(row).get(i) - 1).setStrokeWidth(0.5);
                            FadeTransition fadeTransition = new FadeTransition(Duration.millis(3000), rectangles.get(cellPositionArr2D.get(row).get(i) - 1));
                            fadeTransition.setFromValue(0.75);
                            fadeTransition.setToValue(0);
                            fadeTransition.setAutoReverse(true);
                            fadeTransition.play();
                            win = false;
                        }
                    } else {
                        for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                            rectangles.get(cellPositionArr2D.get(row).get(i) - 1).setFill(Color.PALEGREEN);
                            rectangles.get(cellPositionArr2D.get(row).get(i) - 1).setStroke(Color.BLACK);
                            rectangles.get(cellPositionArr2D.get(row).get(i) - 1).setStrokeWidth(0.5);
                            FadeTransition fadeTransition = new FadeTransition(Duration.millis(3000), rectangles.get(cellPositionArr2D.get(row).get(i) - 1));
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
                    if (result != Integer.parseInt(target)) {
                        for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                            rectangles.get(cellPositionArr2D.get(row).get(i) - 1).setFill(Color.RED);
                            rectangles.get(cellPositionArr2D.get(row).get(i) - 1).setStroke(Color.BLACK);
                            rectangles.get(cellPositionArr2D.get(row).get(i) - 1).setStrokeWidth(0.5);
                            FadeTransition fadeTransition = new FadeTransition(Duration.millis(3000), rectangles.get(cellPositionArr2D.get(row).get(i) - 1));
                            fadeTransition.setFromValue(0.75);
                            fadeTransition.setToValue(0);
                            fadeTransition.setAutoReverse(true);
                            fadeTransition.play();
                            win = false;
                        }
                    } else {
                        for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                            rectangles.get(cellPositionArr2D.get(row).get(i) - 1).setFill(Color.PALEGREEN);
                            rectangles.get(cellPositionArr2D.get(row).get(i) - 1).setStroke(Color.BLACK);
                            rectangles.get(cellPositionArr2D.get(row).get(i) - 1).setStrokeWidth(0.5);
                            FadeTransition fadeTransition = new FadeTransition(Duration.millis(3000), rectangles.get(cellPositionArr2D.get(row).get(i) - 1));
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
                case "*":
                    result = 1;
                    for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                        if (!textFieldArrayList.get(cellPositionArr2D.get(row).get(i) - 1).getText().equals("")) {
                            result = result * Integer.parseInt(textFieldArrayList.get(cellPositionArr2D.get(row).get(i) - 1).getText());
                        }
                    }
                    if (result != Integer.parseInt(target)) {
                        for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                            rectangles.get(cellPositionArr2D.get(row).get(i) - 1).setFill(Color.RED);
                            rectangles.get(cellPositionArr2D.get(row).get(i) - 1).setStroke(Color.BLACK);
                            rectangles.get(cellPositionArr2D.get(row).get(i) - 1).setStrokeWidth(0.5);
                            FadeTransition fadeTransition = new FadeTransition(Duration.millis(3000), rectangles.get(cellPositionArr2D.get(row).get(i) - 1));
                            fadeTransition.setFromValue(0.75);
                            fadeTransition.setToValue(0);
                            fadeTransition.setAutoReverse(true);
                            fadeTransition.play();
                            win = false;
                        }
                    } else {
                        for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                            rectangles.get(cellPositionArr2D.get(row).get(i) - 1).setFill(Color.PALEGREEN);
                            rectangles.get(cellPositionArr2D.get(row).get(i) - 1).setStroke(Color.BLACK);
                            rectangles.get(cellPositionArr2D.get(row).get(i) - 1).setStrokeWidth(0.5);
                            FadeTransition fadeTransition = new FadeTransition(Duration.millis(3000), rectangles.get(cellPositionArr2D.get(row).get(i) - 1));
                            fadeTransition.setFromValue(0.75);
                            fadeTransition.setToValue(0);
                            fadeTransition.setAutoReverse(true);
                            fadeTransition.play();
                            win = true;
                        }
                    }
                    result = 0;
                    break;
                case "÷":
                case "/":
                    ArrayList<Integer> sorted = new ArrayList<>();
                    result = 1;
                    for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                        if (!textFieldArrayList.get(cellPositionArr2D.get(row).get(i) - 1).getText().equals("")) {
                            sorted.add(Integer.parseInt(textFieldArrayList.get(cellPositionArr2D.get(row).get(i) - 1).getText()));
                        }
                    }
                    Collections.sort(sorted);
                    for (Integer number : sorted) {
                        result = number / result;
                    }
                    if (result != Integer.parseInt(target)) {

                        for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                            rectangles.get(cellPositionArr2D.get(row).get(i) - 1).setFill(Color.RED);
                            rectangles.get(cellPositionArr2D.get(row).get(i) - 1).setStroke(Color.BLACK);
                            rectangles.get(cellPositionArr2D.get(row).get(i) - 1).setStrokeWidth(0.5);
                            FadeTransition fadeTransition = new FadeTransition(Duration.millis(3000), rectangles.get(cellPositionArr2D.get(row).get(i) - 1));
                            fadeTransition.setFromValue(0.75);
                            fadeTransition.setToValue(0);
                            fadeTransition.setAutoReverse(true);
                            fadeTransition.play();
                            win = false;
                        }
                    } else {
                        for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                            rectangles.get(cellPositionArr2D.get(row).get(i) - 1).setFill(Color.PALEGREEN);
                            rectangles.get(cellPositionArr2D.get(row).get(i) - 1).setStroke(Color.BLACK);
                            rectangles.get(cellPositionArr2D.get(row).get(i) - 1).setStrokeWidth(0.5);
                            FadeTransition fadeTransition = new FadeTransition(Duration.millis(3000), rectangles.get(cellPositionArr2D.get(row).get(i) - 1));
                            fadeTransition.setFromValue(0.75);
                            fadeTransition.setToValue(0);
                            fadeTransition.setAutoReverse(true);
                            fadeTransition.play();
                            win = true;
                        }
                    }
                    result = 0;
                    break;
                default:
                    result = 0;
                    for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                        if (!textFieldArrayList.get(cellPositionArr2D.get(row).get(i) - 1).getText().equals("")) {
                            result = Integer.parseInt(textFieldArrayList.get(cellPositionArr2D.get(row).get(i) - 1).getText());
                        }
                    }
                    if (result != Integer.parseInt(target)) {
                        for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                            rectangles.get(cellPositionArr2D.get(row).get(i) - 1).setFill(Color.RED);
                            rectangles.get(cellPositionArr2D.get(row).get(i) - 1).setStroke(Color.BLACK);
                            rectangles.get(cellPositionArr2D.get(row).get(i) - 1).setStrokeWidth(0.5);
                            FadeTransition fadeTransition = new FadeTransition(Duration.millis(3000), rectangles.get(cellPositionArr2D.get(row).get(i) - 1));
                            fadeTransition.setFromValue(0.75);
                            fadeTransition.setToValue(0);
                            fadeTransition.setAutoReverse(true);
                            fadeTransition.play();
                            win = false;
                        }
                    } else {
                        for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                            rectangles.get(cellPositionArr2D.get(row).get(i) - 1).setFill(Color.PALEGREEN);
                            rectangles.get(cellPositionArr2D.get(row).get(i) - 1).setStroke(Color.BLACK);
                            rectangles.get(cellPositionArr2D.get(row).get(i) - 1).setStrokeWidth(0.5);
                            FadeTransition fadeTransition = new FadeTransition(Duration.millis(3000), rectangles.get(cellPositionArr2D.get(row).get(i) - 1));
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
            row++;
        }
        cellPositionArr2D.clear();
    }

    private boolean isArrayInputFull() {
        for (int i = 0; i < num.get(); i++) {
            for (int j = 0; j < num.get(); j++) {
                if (arrayOfInput[i][j] == 0)
                    return false;
            }
        }
        return true;
    }

    private void animation() {
        for (Rectangle rec : animationRectangles){
            Color[] palette = new Color[] { Color.RED, Color.BLUE, Color.PALETURQUOISE, Color.AZURE, Color.YELLOW};
            Random rng = new Random();
            rec.setOpacity(0.5);
            Timeline time = new Timeline(new KeyFrame(Duration.millis(100), e -> rec.setFill(palette[rng.nextInt(palette.length)])));
            time.setCycleCount(Animation.INDEFINITE);
            time.setAutoReverse(false);
            time.play();
        }
    }

    private void winningAnimation() {
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
            animation();
        }
    }

    private ArrayList<ArrayList<Integer>> permutations(){

        int fact = 1;
        for (int i = 1; i <= num.get(); i++) {
            fact = fact * i;
        }

        ArrayList<ArrayList<Integer>> allperm = new ArrayList<>();
        ArrayList<Integer> arr = new ArrayList<>();
        for (int i = 0; i < num.get(); i++) {
            arr.add(i + 1);
        }

        allperm.add(arr);
        while (allperm.size() < fact) {

            ArrayList<Integer> temporary = new ArrayList<>(arr);
            Collections.shuffle(temporary);

            boolean bool = false;
            for (ArrayList<Integer> perm : allperm) {
                if (temporary.equals(perm)) {
                    bool = true;
                    break;
                }
            }

            if (!bool) {
                allperm.add(temporary);
            }

        }

        return allperm;
    }
    
    private int[][] matrix(ArrayList<ArrayList<Integer>> permutation){

        ArrayList<ArrayList<Integer>> matrix = new ArrayList<>();

        int fact = 1;
        for (int i = 1; i <= num.get(); i++) {
            fact = fact * i;
        }

        while (matrix.size() < num.get()){
            ArrayList<Integer> row;
            Random rand = new Random();
            int random = rand.nextInt(fact);
            row = permutation.get(random);
            boolean bool = false;
            for(ArrayList<Integer> arr : matrix){
                if(arr.equals(row)){
                    bool = true;
                    break;
                }
            }

            outer: for(ArrayList<Integer> arr : matrix){
                for(int i = 0; i < num.get(); i++){
                    if(arr.get(i).equals(row.get(i))){
                        bool = true;
                        break outer;
                    }
                }
            }

            if (!bool){
                matrix.add(row);
            }
        }

        int[][] solarr = new int[num.get()][num.get()];

        for (int i = 0; i < matrix.size(); i++) {
            for (int j = 0; j < matrix.get(i).size(); j++) {
                solarr[i][j] = matrix.get(i).get(j);
            }
        }
        return solarr;

    }

    private boolean cageMistake(ArrayList<Integer> solutions) {
        boolean correct = true;
        int arrayRow = 0;
        int arrayCol = 0;
        String target;
        int result = 0;
        String operation;

        // Populates 2D arraylist of cell positions
        cellPositionArr2D.add(new ArrayList<>());
        for (Integer i : cellPositionArr1D){
            if (i != 0){
                cellPositionArr2D.get(arrayRow).add(arrayCol, i);
                arrayCol++;
            } else {
                arrayRow++;
                cellPositionArr2D.add(new ArrayList<>());
                arrayCol = 0;
            }
        }

        for (Label ignored : labels) {
            listBoolean.add(false);
        }

        // checks if correct values entered in cell
        int row = 0;
        int count = 0;
        boolean nullpointer = false;
        for (String label : StringLabels){
            target = label.substring(0,(label.length() - 1));
            operation = label.substring(label.length()-1);
            switch (operation) {
                case "+":
                    for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                        if (!solutions.get(cellPositionArr2D.get(row).get(i) - 1).equals(0)) {
                            result = result + solutions.get(cellPositionArr2D.get(row).get(i) - 1);
                        } else {
                            nullpointer = true;
                        }
                    }
                    if ((result != Integer.parseInt(target)) || nullpointer) {
                        listBoolean.set(count, false);
                        count++;
                    } else {
                        listBoolean.set(count, true);
                        count++;
                    }
                    nullpointer = false;
                    result = 0;
                    break;
                case "-": {
                    ArrayList<Integer> sorted = new ArrayList<>();
                    for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                        if (!solutions.get(cellPositionArr2D.get(row).get(i) - 1).equals(0)) {
                            sorted.add(solutions.get(cellPositionArr2D.get(row).get(i) - 1));
                        }
                    }
                    Collections.sort(sorted);
                    for (Integer number : sorted) {
                        result = number - result;
                    }
                    if (result != Integer.parseInt(target)) {
                        listBoolean.set(count, false);
                        count++;
                    } else {
                        listBoolean.set(count, true);
                        count++;
                    }
                    result = 0;
                    break;
                }
                case "x":
                case "*":
                    result = 1;
                    for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                        if (!solutions.get(cellPositionArr2D.get(row).get(i) - 1).equals(0)) {
                            result = result * (solutions.get(cellPositionArr2D.get(row).get(i) - 1));
                        }
                    }
                    if (result != Integer.parseInt(target)) {
                        listBoolean.set(count, false);
                        count++;
                    }  else {
                        listBoolean.set(count, true);
                        count++;
                    }
                    result = 0;
                    break;
                case "÷":
                case "/":{
                    ArrayList<Integer> sorted = new ArrayList<>();
                    result = 1;
                    for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                        if (!solutions.get(cellPositionArr2D.get(row).get(i) - 1).equals(0)) {
                            sorted.add(solutions.get(cellPositionArr2D.get(row).get(i) - 1));
                        }
                    }
                    Collections.sort(sorted);
                    for (Integer number : sorted) {
                        result = number / result;
                    }
                    if (result != Integer.parseInt(target)) {
                        listBoolean.set(count, false);
                        count++;
                    } else {
                        listBoolean.set(count, true);
                        count++;
                    }
                    result = 0;
                    break;
                }
                default:{
                    result = solutions.get(cellPositionArr2D.get(row).get(0) - 1);
                    if (result != Integer.parseInt(operation)) {
                        listBoolean.set(count, false);
                        count++;
                    } else {
                        listBoolean.set(count, true);
                        count++;
                    }
                    result = 0;
                    break;
                }
            }
            row++;
        }
        cellPositionArr2D.clear();
        for(Boolean bool : listBoolean){
            if (!bool) {
                correct = false;
                break;
            }
        }
        listBoolean.clear();
        return correct;
    }

    private ArrayList<Integer> solver(Boolean output){

        boolean solved = false;
        long startTime = System.nanoTime();
        int[][] solutionArray;
        ArrayList<ArrayList<Integer>> allpermu = permutations();
        ArrayList<Integer> solutionArrayList = new ArrayList<>();
        int trials = 0;

        while(!solved || trials < Math.pow(num.get(),num.get())){

            boolean cellCorrect = false;
            solutionArray = matrix(allpermu);


            for (int i = 0; i < num.get(); i++){
                for (int j = 0; j < num.get(); j++){
                    solutionArrayList.add(solutionArray[i][j]);
                }
            }

            if (!listOfPossibleSolutions.contains(solutionArray)) {
                cellCorrect = cageMistake(solutionArrayList);
                listOfPossibleSolutions.add(solutionArray);
                trials++;
            }


            if (cellCorrect){
                solved = true;
                if (output) {
                    for (int count = 0; count < textFieldArrayList.size(); count++) {
                        textFieldArrayList.get(count).setText(String.valueOf(solutionArrayList.get(count)));
                    }
                }
            } else {
                System.out.println(solutionArrayList);
                solutionArrayList.clear();
            }
        }
        System.out.println("Solved");
        long endTime = System.nanoTime();
        System.out.println("Took " + ((endTime - startTime)*Math.pow(10,-9)) + " s");
        return solutionArrayList;
    }

    private void generateRandom(){
        ArrayList<ArrayList<Integer>> permutations = permutations();
        ArrayList<Integer> solList = new ArrayList<>();
        int[][] solution = matrix(permutations);

        for(int i = 0; i < num.get(); i++){
            for(int j = 0; j < num.get(); j++)
                solList.add(solution[i][j]);
        }

        ArrayList<String> operation = new ArrayList<>();
        operation.add("+");
        operation.add("x");
        operation.add("-");
        operation.add("÷");

        ArrayList<Boolean> availableCells = new ArrayList<>();
        for (int i = 0; i < 150; i++)
            availableCells.add(true);

        for (int i = 0; i < num.get()*num.get(); i++){
            ArrayList<Integer> cellPosition = new ArrayList<>();
            Boolean rightAvailable = false;
            Boolean bottomAvailable = false;
            int labelresult = 0;
            String op;
            int xPosi = i % num.get();
            int yPosi = i / num.get();

            if (availableCells.get(i)){
                if (i < (num.get()*num.get())-1)
                    rightAvailable = availableCells.get(i + 1);

                if (i < (num.get()*num.get())-num.get())
                    bottomAvailable = availableCells.get(i + num.get());

                if (((i+1) % num.get()) == 0)
                    rightAvailable = false;

                switch (rightAvailable + "-" + bottomAvailable){
                    case "false-false": {
                        // 1x1 square
                        Label label = new Label();
                        label.setText(String.valueOf(solList.get(i)));
                        label.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
                        label.relocate(xPosi * width + 5, yPosi * width + 5);
                        labels.add(label);
                        StringLabels.add(label.getText());

                        CageLining cage = new CageLining(label.getText(), cellPosition);
                        Group group = cage.hero(1,width);
                        group.relocate(xPosi * width,yPosi * width);
                        pane.getChildren().addAll(group,label);
                        availableCells.set(i, false);
                        cellPositionArr1D.add(i+1);
                        cellPositionArr1D.add(0);
                        System.out.println("1x1 Square");
                        break;
                    }
                    case "true-false": {
                        // cell line horizontal length 2,3,4
                        Random rand = new Random();
                        int length = rand.nextInt(2);
                        length = length + 2;
                        int spaceLeft = num.get() - (i % num.get());
                        if (spaceLeft < length)
                            length = num.get() - (i % num.get());

                        for(int j = 1; j <= spaceLeft; j++){
                            if(!availableCells.get(i + j)){
                                length = j;
                                break;
                            }
                        }
                        CageLining cage = new CageLining(cellPosition);
                        Group group = cage.hero(length,width);
                        group.relocate(xPosi*width,yPosi*width);
                        pane.getChildren().addAll(group);
                        Random randop = new Random();

                        if (length>2)
                            op = operation.get(randop.nextInt(2));
                        else
                            op = operation.get(randop.nextInt(4));

                        for(int j = 0; j < length; j++){
                            availableCells.set(i + j, false);
                            cellPositionArr1D.add(i+j+1);
                            cellPosition.add(solList.get(i + j));
                        }

                        cellPositionArr1D.add(0);

                        Collections.sort(cellPosition);
                        System.out.println(cellPosition);
                        switch (op) {
                            case "+":
                                for (int num : cellPosition) {
                                    labelresult = labelresult + num;
                                }
                                break;
                            case "-":
                                if (cellPosition.size()==2) {
                                    Collections.sort(cellPosition);
                                    for (Integer number : cellPosition)
                                        labelresult = number - labelresult;
                                } else if (cellPosition.size() == 3) {
                                    cellPosition.sort(Collections.reverseOrder());
                                    int num1 = cellPosition.get(0);
                                    int num2 = cellPosition.get(1);
                                    int num3 = cellPosition.get(2);

                                    labelresult = num1 - num2 - num3;
                                }
                                break;
                            case "x":
                                labelresult = 1;
                                for (int num : cellPosition)
                                    labelresult = labelresult * num;
                                break;
                            case "÷":
                                labelresult = 1;
                                if(cellPosition.size() == 2){
                                    Collections.sort(cellPosition);
                                    for (Integer number : cellPosition)
                                        labelresult = number / labelresult;
                                } else if (cellPosition.size() == 3) {
                                    cellPosition.sort(Collections.reverseOrder());
                                    int num1 = cellPosition.get(0);
                                    int num2 = cellPosition.get(1);
                                    int num3 = cellPosition.get(2);

                                    labelresult = num1 / num2 / num3;
                                }
                                break;
                        }
                        Label label = new Label();
                        label.setText(String.valueOf(labelresult) + op);
                        label.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
                        label.relocate(xPosi * width + 5, yPosi * width + 5);
                        labels.add(label);
                        StringLabels.add(label.getText());
                        pane.getChildren().addAll(label);
                        System.out.println("cell line of length " + length);
                        break;
                    }
                    case "false-true": {
                        // cell line vertical length 2,3,4
                        Random random = new Random();
                        int probability = random.nextInt(2);
                        if (probability == 0) {
                            Random rand = new Random();
                            int length = rand.nextInt(2);
                            length = length + 2;
                            if (num.get() - (i / num.get()) < length)
                                length = num.get() - (i / num.get());
                            CageLining cage = new CageLining(cellPosition);
                            Group group = cage.vertical_hero(length, width);
                            group.relocate(xPosi * width, yPosi * width);
                            pane.getChildren().addAll(group);

                            Random randop = new Random();
                            if (length>2)
                                op = operation.get(randop.nextInt(2));
                            else
                                op = operation.get(randop.nextInt(4));

                            for (int j = 0; j < length; j++) {
                                availableCells.set(i + num.get() * j, false);
                                cellPositionArr1D.add(i+num.get()*j+1);
                                cellPosition.add(solList.get(i + num.get() * j));
                            }

                            cellPositionArr1D.add(0);
                            Collections.sort(cellPosition);
                            System.out.println(cellPosition);

                            switch (op) {
                                case "+":
                                    for (int num : cellPosition)
                                        labelresult = labelresult + num;
                                    break;
                                case "-":
                                    if (cellPosition.size() == 1) {
                                        labelresult = cellPosition.get(0);
                                        break;
                                    }
                                    if(cellPosition.size()==2){
                                        Collections.sort(cellPosition);
                                        for (Integer number : cellPosition)
                                            labelresult = number - labelresult;
                                    } else if (cellPosition.size() == 3) {
                                        cellPosition.sort(Collections.reverseOrder());
                                        int num1 = cellPosition.get(0);
                                        int num2 = cellPosition.get(1);
                                        int num3 = cellPosition.get(2);

                                        labelresult = num1 - num2 - num3;
                                    }
                                    break;
                                case "x":
                                    labelresult = 1;
                                    for (int num : cellPosition)
                                        labelresult = labelresult * num;
                                    break;
                                case "÷":
                                    labelresult = 1;
                                    if (cellPosition.size() == 1) {
                                        labelresult = cellPosition.get(0);
                                        break;
                                    }
                                    if(cellPosition.size()==2){
                                        Collections.sort(cellPosition);
                                        for (Integer number : cellPosition)
                                            labelresult = number / labelresult;
                                    } else if (cellPosition.size() == 3) {
                                        cellPosition.sort(Collections.reverseOrder());
                                        int num1 = cellPosition.get(0);
                                        int num2 = cellPosition.get(1);
                                        int num3 = cellPosition.get(2);

                                        labelresult = num1 / num2 / num3;
                                    }
                                    break;
                            }

                            Label label = new Label();
                            label.setText(String.valueOf(labelresult) + op);
                            label.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
                            label.relocate(xPosi * width + 5, yPosi * width + 5);
                            labels.add(label);
                            StringLabels.add(label.getText());
                            pane.getChildren().addAll(label);
                            System.out.println("vertical cell line of length " + length);
                            break;
                        } else if (probability == 1){
                            Label label = new Label();
                            label.setText(String.valueOf(solList.get(i)));
                            label.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
                            label.relocate(xPosi * width + 5, yPosi * width + 5);
                            labels.add(label);
                            StringLabels.add(label.getText());

                            CageLining cage = new CageLining(label.getText(), cellPosition);
                            Group group = cage.hero(1,width);
                            group.relocate(xPosi*width,yPosi*width);
                            pane.getChildren().addAll(group,label);
                            availableCells.set(i, false);
                            cellPositionArr1D.add(i+1);
                            cellPositionArr1D.add(0);
                            System.out.println("1x1 Square");
                            break;
                        }
                    }
                    case "true-true": {
                        // both L
                        // square 2x2
                        // cell line all
                        Random random = new Random();
                        int probability=0;

                        switch (difficulty) {
                            case "Easy":
                                probability = random.nextInt(3);
                                break;
                            case "Normal":
                                probability = random.nextInt(5);
                                break;
                            case "Hard":
                                probability = random.nextInt(6);
                                break;
                        }

                        if (probability == 0){
                            Random rand = new Random();
                            int length = rand.nextInt(2);
                            length = length + 2;
                            int spaceLeft = num.get() - (i % num.get());
                            if (spaceLeft < length)
                                length = num.get() - (i % num.get());

                            for (int j = 1; j <= spaceLeft; j++){
                                if (!availableCells.get(i + j)){
                                    length = j;
                                    break;
                                }
                            }

                            CageLining cage = new CageLining(cellPosition);
                            Group group = cage.hero(length,width);
                            group.relocate(xPosi*width,yPosi*width);
                            pane.getChildren().addAll(group);

                            Random randop = new Random();
                            if (length > 2)
                                op = operation.get(randop.nextInt(2));
                            else
                                op = operation.get(randop.nextInt(4));

                            for(int j = 0; j < length; j++){
                                availableCells.set(i + j, false);
                                cellPositionArr1D.add(i+j+1);
                                cellPosition.add(solList.get(i + j));
                            }
                            cellPositionArr1D.add(0);
                            Collections.sort(cellPosition);
                            System.out.println(cellPosition);
                            switch (op) {
                                case "+":
                                    for (int num : cellPosition)
                                        labelresult = labelresult + num;
                                    break;
                                case "-":
                                    if (cellPosition.size() == 1) {
                                        labelresult = cellPosition.get(0);
                                        break;
                                    }
                                    if(cellPosition.size()==2){
                                        Collections.sort(cellPosition);
                                        for (Integer number : cellPosition)
                                            labelresult = number - labelresult;
                                    } else if (cellPosition.size() == 3) {
                                        cellPosition.sort(Collections.reverseOrder());
                                        int num1 = cellPosition.get(0);
                                        int num2 = cellPosition.get(1);
                                        int num3 = cellPosition.get(2);

                                        labelresult = num1 - num2 - num3;
                                    }
                                    break;
                                case "x":
                                    labelresult = 1;
                                    for (int num : cellPosition)
                                        labelresult = labelresult * num;
                                    break;
                                case "÷":
                                    labelresult = 1;
                                    if (cellPosition.size() == 1) {
                                        labelresult = cellPosition.get(0);
                                        break;
                                    }
                                    if(cellPosition.size()==2){
                                        Collections.sort(cellPosition);
                                        for (Integer number : cellPosition)
                                            labelresult = number / labelresult;
                                    } else if (cellPosition.size() == 3) {
                                        cellPosition.sort(Collections.reverseOrder());
                                        int num1 = cellPosition.get(0);
                                        int num2 = cellPosition.get(1);
                                        int num3 = cellPosition.get(2);

                                        labelresult = num1 / num2 / num3;
                                    }
                                    break;
                            }

                            Label label = new Label();
                            label.setText(String.valueOf(labelresult) + op);
                            label.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
                            label.relocate(xPosi * width + 5, yPosi * width + 5);
                            labels.add(label);
                            StringLabels.add(label.getText());
                            pane.getChildren().addAll(label);
                            System.out.println("cell line of length " + length);
                            break;

                        } else if (probability == 1){
                            Random rand = new Random();
                            int length = rand.nextInt(2);
                            length = length + 2;

                            if (num.get() - (i / num.get()) < length)
                                length = num.get() - (i / num.get());

                            CageLining cage = new CageLining(cellPosition);
                            Group group = cage.vertical_hero(length, width);
                            group.relocate(xPosi*width,yPosi*width);
                            pane.getChildren().addAll(group);

                            Random randop = new Random();
                            if (length > 2)
                                op = operation.get(randop.nextInt(2));
                            else
                                op = operation.get(randop.nextInt(4));

                            for (int j = 0; j < length; j++) {
                                availableCells.set(i + (num.get() * j), false);
                                cellPositionArr1D.add(i+(num.get()*j)+1);
                                cellPosition.add(solList.get(i + num.get() * j));
                            }
                            cellPositionArr1D.add(0);
                            Collections.sort(cellPosition);
                            System.out.println(cellPosition);
                            switch (op) {
                                case "+":
                                    for (int num : cellPosition)
                                        labelresult = labelresult + num;
                                    break;
                                case "-":
                                    if (cellPosition.size() == 1) {
                                        labelresult = cellPosition.get(0);
                                        break;
                                    }
                                    if(cellPosition.size()==2){
                                        Collections.sort(cellPosition);
                                        for (Integer number : cellPosition)
                                            labelresult = number - labelresult;
                                    } else if (cellPosition.size() == 3) {
                                        cellPosition.sort(Collections.reverseOrder());
                                        int num1 = cellPosition.get(0);
                                        int num2 = cellPosition.get(1);
                                        int num3 = cellPosition.get(2);

                                        labelresult = num1 - num2 - num3;
                                    }
                                    break;
                                case "x":
                                    labelresult = 1;
                                    for (int num : cellPosition)
                                        labelresult = labelresult * num;
                                    break;
                                case "÷":
                                    labelresult = 1;
                                    if (cellPosition.size() == 1) {
                                        labelresult = cellPosition.get(0);
                                        break;
                                    }
                                    if(cellPosition.size()==2){
                                        Collections.sort(cellPosition);
                                        for (Integer number : cellPosition)
                                            labelresult = number / labelresult;
                                    } else if (cellPosition.size() == 3) {
                                        cellPosition.sort(Collections.reverseOrder());
                                        int num1 = cellPosition.get(0);
                                        int num2 = cellPosition.get(1);
                                        int num3 = cellPosition.get(2);

                                        labelresult = num1 / num2 / num3;
                                    }
                                    break;
                            }

                            Label label = new Label();
                            label.setText(String.valueOf(labelresult) + op);
                            label.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
                            label.relocate(xPosi * width + 5, yPosi * width + 5);
                            labels.add(label);
                            StringLabels.add(label.getText());
                            pane.getChildren().addAll(label);
                            System.out.println("vertical cell line of length " + length);
                            break;
                        }else if (probability == 4){
                            CageLining cage = new CageLining(cellPosition);
                            Group group = cage.blueRicky(1,width);
                            group.relocate(xPosi*width,yPosi*width);
                            pane.getChildren().addAll(group);

                            Random randop = new Random();
                            op = operation.get(randop.nextInt(2));

                            availableCells.set(i,false);
                            availableCells.set(i + num.get(),false);
                            availableCells.set(i + num.get() + 1,false);

                            cellPositionArr1D.add(i+1);
                            cellPositionArr1D.add(i+num.get()+1);
                            cellPositionArr1D.add(i+num.get()+1+1);

                            cellPositionArr1D.add(0);

                            cellPosition.add(solList.get(i));
                            cellPosition.add(solList.get(i + num.get()));
                            cellPosition.add(solList.get(i + num.get() + 1));

                            Collections.sort(cellPosition);
                            System.out.println(cellPosition);
                            switch (op) {
                                case "+":
                                    for (int num : cellPosition)
                                        labelresult = labelresult + num;
                                    break;
                                case "-":
                                    if (cellPosition.size() == 1) {
                                        labelresult = cellPosition.get(0);
                                        break;
                                    }
                                    if(cellPosition.size()==2){
                                        Collections.sort(cellPosition);
                                        for (Integer number : cellPosition)
                                            labelresult = number - labelresult;
                                    } else if (cellPosition.size() == 3) {
                                        cellPosition.sort(Collections.reverseOrder());
                                        int num1 = cellPosition.get(0);
                                        int num2 = cellPosition.get(1);
                                        int num3 = cellPosition.get(2);

                                        labelresult = num1 - num2 - num3;
                                    }
                                    break;
                                case "x":
                                    labelresult = 1;
                                    for (int num : cellPosition)
                                        labelresult = labelresult * num;
                                    break;
                                case "÷":
                                    labelresult = 1;
                                    if (cellPosition.size() == 1) {
                                        labelresult = cellPosition.get(0);
                                        break;
                                    }
                                    if(cellPosition.size()==2){
                                        Collections.sort(cellPosition);
                                        for (Integer number : cellPosition)
                                            labelresult = number / labelresult;
                                    } else if (cellPosition.size() == 3) {
                                        cellPosition.sort(Collections.reverseOrder());
                                        int num1 = cellPosition.get(0);
                                        int num2 = cellPosition.get(1);
                                        int num3 = cellPosition.get(2);

                                        labelresult = num1 / num2 / num3;
                                    }
                                    break;
                            }

                            Label label = new Label();
                            label.setText(String.valueOf(labelresult) + op);
                            label.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
                            label.relocate(xPosi * width + 5, yPosi * width + 5);
                            labels.add(label);
                            StringLabels.add(label.getText());
                            pane.getChildren().addAll(label);
                            System.out.println("L");
                            break;
                        } else if (probability == 3){
                            CageLining cage = new CageLining(cellPosition);
                            Group group = cage.Lrotated90(width);
                            group.relocate(xPosi*width,yPosi*width);
                            pane.getChildren().addAll(group);

                            Random randop = new Random();
                            op = operation.get(randop.nextInt(2));

                            availableCells.set(i,false);
                            availableCells.set(i + 1,false);
                            availableCells.set(i + num.get() + 1,false);

                            cellPositionArr1D.add(i+1);
                            cellPositionArr1D.add(i+1+1);
                            cellPositionArr1D.add(i+num.get()+1+1);

                            cellPositionArr1D.add(0);

                            cellPosition.add(solList.get(i));
                            cellPosition.add(solList.get(i + 1));
                            cellPosition.add(solList.get(i + num.get() + 1));

                            Collections.sort(cellPosition);
                            System.out.println(cellPosition);
                            switch (op) {
                                case "+":
                                    for (int num : cellPosition)
                                        labelresult = labelresult + num;
                                    break;
                                case "-":
                                    if (cellPosition.size() == 1) {
                                        labelresult = cellPosition.get(0);
                                        break;
                                    }
                                    if(cellPosition.size()==2){
                                        Collections.sort(cellPosition);
                                        for (Integer number : cellPosition)
                                            labelresult = number - labelresult;
                                    } else if (cellPosition.size() == 3) {
                                        cellPosition.sort(Collections.reverseOrder());
                                        int num1 = cellPosition.get(0);
                                        int num2 = cellPosition.get(1);
                                        int num3 = cellPosition.get(2);

                                        labelresult = num1 - num2 - num3;
                                    }
                                    break;
                                case "x":
                                    labelresult = 1;
                                    for (int num : cellPosition)
                                        labelresult = labelresult * num;
                                    break;
                                case "÷":
                                    labelresult = 1;
                                    if (cellPosition.size() == 1) {
                                        labelresult = cellPosition.get(0);
                                        break;
                                    }
                                    if(cellPosition.size()==2){
                                        Collections.sort(cellPosition);
                                        for (Integer number : cellPosition)
                                            labelresult = number / labelresult;
                                    } else if (cellPosition.size() == 3) {
                                        cellPosition.sort(Collections.reverseOrder());
                                        int num1 = cellPosition.get(0);
                                        int num2 = cellPosition.get(1);
                                        int num3 = cellPosition.get(2);

                                        labelresult = num1 / num2 / num3;
                                    }
                                    break;
                            }

                            Label label = new Label();
                            label.setText(String.valueOf(labelresult) + op);
                            label.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
                            label.relocate(xPosi * width + 5, yPosi * width + 5);
                            labels.add(label);
                            StringLabels.add(label.getText());
                            pane.getChildren().addAll(label);
                            System.out.println("L");
                            break;
                        } else if (probability == 2){
                            Label label = new Label();
                            label.setText(String.valueOf(solList.get(i)));
                            label.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
                            label.relocate(xPosi * width + 5, yPosi * width + 5);
                            labels.add(label);
                            StringLabels.add(label.getText());

                            CageLining cage = new CageLining(cellPosition);
                            Group group = cage.hero(1, width);
                            group.relocate(xPosi*width,yPosi*width);
                            pane.getChildren().addAll(group,label);
                            availableCells.set(i, false);
                            cellPositionArr1D.add(i+1);
                            cellPositionArr1D.add(0);
                            System.out.println("1x1 Square");
                            break;
                        }else if (probability == 5){
                            CageLining cage = new CageLining(cellPosition);
                            Group group = cage.smashboy(2,width);
                            group.relocate(xPosi*width,yPosi*width);
                            pane.getChildren().addAll(group);

                            Random randop = new Random();
                            op = operation.get(randop.nextInt(2));

                            availableCells.set(i,false);
                            availableCells.set(i + 1,false);
                            availableCells.set(i + num.get(),false);
                            availableCells.set(i + num.get() + 1,false);

                            cellPositionArr1D.add(i+1);
                            cellPositionArr1D.add(i+1+1);
                            cellPositionArr1D.add(i+num.get()+1);
                            cellPositionArr1D.add(i+num.get()+1+1);
                            cellPositionArr1D.add(0);

                            cellPosition.add(solList.get(i));
                            cellPosition.add(solList.get(i + 1));
                            cellPosition.add(solList.get(i + num.get()));
                            cellPosition.add(solList.get(i + num.get() + 1));

                            Collections.sort(cellPosition);
                            System.out.println(cellPosition);
                            switch (op) {
                                case "+":
                                    for (int num : cellPosition)
                                        labelresult = labelresult + num;
                                    break;
                                case "-":
                                    if (cellPosition.size() == 1) {
                                        labelresult = cellPosition.get(0);
                                        break;
                                    }
                                    if(cellPosition.size()==2){
                                        Collections.sort(cellPosition);
                                        for (Integer number : cellPosition)
                                            labelresult = number - labelresult;
                                    } else if (cellPosition.size() == 3) {
                                        cellPosition.sort(Collections.reverseOrder());
                                        int num1 = cellPosition.get(0);
                                        int num2 = cellPosition.get(1);
                                        int num3 = cellPosition.get(2);

                                        labelresult = num1 - num2 - num3;
                                    }
                                    break;
                                case "x":
                                    labelresult = 1;
                                    for (int num : cellPosition)
                                        labelresult = labelresult * num;
                                    break;
                                case "÷":
                                    labelresult = 1;
                                    if (cellPosition.size() == 1) {
                                        labelresult = cellPosition.get(0);
                                        break;
                                    }
                                    if(cellPosition.size()==2){
                                        Collections.sort(cellPosition);
                                        for (Integer number : cellPosition)
                                            labelresult = number / labelresult;
                                    } else if (cellPosition.size() == 3) {
                                        cellPosition.sort(Collections.reverseOrder());
                                        int num1 = cellPosition.get(0);
                                        int num2 = cellPosition.get(1);
                                        int num3 = cellPosition.get(2);

                                        labelresult = num1 / num2 / num3;
                                    }
                                    break;
                            }
                            Label label = new Label();
                            label.setText(String.valueOf(labelresult) + op);
                            label.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
                            label.relocate(xPosi * width + 5, yPosi * width + 5);
                            labels.add(label);
                            StringLabels.add(label.getText());
                            pane.getChildren().addAll(label);
                            System.out.println("L");
                            break;
                        }
                    }
                }
                cellPosition.clear();
            }
        }
    }
}
