import javafx.animation.FadeTransition;
import javafx.application.Application;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class v2 extends Application {

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
    private Pane dummyPane = new Pane();
    private Scene scene = new Scene(background);

    private Stack<String> movesStack = new Stack<>();
    private Stack<String> redoStack = new Stack<>();
    private Stack<TextField> movesStackText = new Stack<>();
    private Stack<TextField> redoStackText = new Stack<>();
    private AtomicInteger num = new AtomicInteger();
    private int[][] arrayOfInput;
    private ArrayList<String> labels = new ArrayList<>();
    private ArrayList<TextField> textFieldArrayList = new ArrayList<>();
    private ArrayList<Label> labelArrayList = new ArrayList<>();
    private ArrayList<Integer> cellPositionArr1D = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> cellPositionArr2D = new ArrayList<>();
    private ArrayList<Integer> gridNumbers = new ArrayList<>();
    private ArrayList<Rectangle> rectangleArrayList = new ArrayList<>();

    private int width = 80;
    private int fontTextField = 23;
    private int fontLabel = 15;
    private boolean win = true;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
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
        backspace.setMaxSize(100, Double.POSITIVE_INFINITY);
        VBox.setVgrow(backspace, Priority.ALWAYS);
        fontChoiceBox.setMaxSize(100, Double.POSITIVE_INFINITY);
        VBox.setVgrow(fontChoiceBox, Priority.ALWAYS);

        fontChoiceBox.setValue("Medium");
        fontChoiceBox.getItems().add("Small");
        fontChoiceBox.getItems().add("Medium");
        fontChoiceBox.getItems().add("Large");
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
            if (fo instanceof TextInputControl) {
                ((TextInputControl) fo).clear();
            }
            movesStack.push(null);
        });

        showMistakes.setOnAction(e -> {
            checkDuplicate();
            checkCellMistake();
        });

        undo.setOnAction(e -> {
            if(!movesStackText.isEmpty()) {
                TextField textField = movesStackText.pop();
                redoStackText.push(textField);
                String temp = movesStack.pop();
                if(temp == null){
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

        if (movesStack.empty()) {
            undo.setDisable(true);
        }

        if (redoStack.empty()) {
            redo.setDisable(true);
        }

        if (movesStack.isEmpty() && redoStack.isEmpty()){
            clear.setDisable(true);
        }

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

        VBox numericPad = new VBox();
        for (int r = 0; r < 3; r++) {
            HBox row = new HBox();
            numericPad.getChildren().add(row);
            VBox.setVgrow(row, Priority.ALWAYS);
            for (int c = 0; c < 3; c++) {
                Button button = new Button("" + ((c + 1) + 3 * r));
                button.setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

                button.setFocusTraversable(false);
                button.setOnAction(evt -> {
                    Node fo = scene.getFocusOwner();
                    if (fo instanceof TextInputControl) {
                        ((TextInputControl) fo).replaceSelection(button.getText());
                    }
                });

                row.getChildren().add(button);
                HBox.setHgrow(button, Priority.ALWAYS);
            }
        }

        commands.getChildren().addAll(load, undoredo, clear, showMistakes, numericPad, backspace, fontChoiceBox);

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
                //to display the filechooser GUI
                // TODO use split
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open File to Load");
                FileChooser.ExtensionFilter txtFilter = new FileChooser.ExtensionFilter("Text files", "*.txt");
                fileChooser.getExtensionFilters().add(txtFilter);
                File file = fileChooser.showOpenDialog(stage);
                ArrayList<String> allGridNumbers = new ArrayList<>();
                boolean containDuplicateGridNumber = false;

                //Code to read into the file
                String fileName = file.getPath();
                boolean fileFound=true;
                MathDokuReader reader = null;
                String thisLine = "";
                String label = "";
                String gridNumbersString;
                int indexOfComma;

                try {
                    reader = new MathDokuReader(fileName);
                } catch (FileNotFoundException exception){
                    fileFound = false;
                    System.out.println("The file " + fileName + " cannot be read/opened");
                }

                if (fileFound)
                    thisLine = reader.getLine();
                double max = 0;
                while (thisLine != null){
                    int indexOfSpace = thisLine.indexOf(" ");
                    String division = "÷";
                    if (indexOfSpace >1){
                        label = thisLine.substring(0,indexOfSpace);
                        if (label.contains("�")) {
                            label = label.substring(0,label.length()-1) + division ;
                        }
                        gridNumbersString = thisLine.substring(indexOfSpace+1);

                        labels.add(label);

                        indexOfComma = gridNumbersString.indexOf(",");

                        while (indexOfComma != -1) {
                            if (allGridNumbers.contains(gridNumbersString.substring(0, indexOfComma))){
                                containDuplicateGridNumber = true;
                                break;
                            } else {
                                allGridNumbers.add(gridNumbersString.substring(0, indexOfComma));
                            }
                            gridNumbers.add(Integer.parseInt(gridNumbersString.substring(0, indexOfComma)));
                            cellPositionArr1D.add(Integer.parseInt(gridNumbersString.substring(0, indexOfComma)));
                            gridNumbersString = gridNumbersString.substring(indexOfComma+1);
                            indexOfComma = gridNumbersString.indexOf(",");
                        }
                        if (containDuplicateGridNumber) {
                            pane.getChildren().clear();
                            Alert alert = new Alert(Alert.AlertType.ERROR, "This is not a valid file! Try again!");
                            alert.showAndWait();

                            break;
                        }
                        gridNumbers.add(Integer.parseInt(gridNumbersString));
                        cellPositionArr1D.add(Integer.parseInt(gridNumbersString));
                        cellPositionArr1D.add(0);
                    }
                    if (!(gridNumbers.isEmpty())) {
                        Cage cage = new Cage(label, gridNumbers);
                        Group group = cage.getShape(width, num.get());
                        if(!(group == null)) {
                            group.relocate(cage.getXPositionToDraw() * width, cage.getYPositionToDraw() * width);
                            Label cageLabel = cage.getLabel();
                            labelArrayList.add(cageLabel);
                            cageLabel.setFont(Font.font("Verdana",FontWeight.BOLD, 15));
                            cageLabel.relocate(cage.getPositionXLabel() * width+5,cage.getYPositionToDraw() * width+5);
                            pane.getChildren().addAll(group,cageLabel);
                        }
                    }
                    for (int num: gridNumbers) {
                        if (num > max) {
                            max = num;
                        }
                    }
                    System.out.println(gridNumbers);
                    gridNumbers.clear();
                    assert reader != null;
                    thisLine = reader.getLine();
                }
                num.set((int) Math.sqrt(max));
            } else {
                TextInputDialog textInput = new TextInputDialog("2");
                textInput.setHeaderText("Difficulty of Puzzle");
                textInput.setTitle("Size of grid");
                textInput.setContentText("Please enter number of cells:");
                Optional<String> showAndWait = textInput.showAndWait();
                if (showAndWait.isPresent()) {
                    num.set(Integer.parseInt(showAndWait.get()));
                    arrayOfInput = new int[num.get()][num.get()];
                    drawGrid();
                }
            }
        });

        num.set(6);
        arrayOfInput = new int[num.get()][num.get()];
        drawGrid();

        dummyPane.getChildren().addAll(pane, grid);

        background.setPadding(new Insets(10));
        background.setLeft(commands);
        background.setCenter(dummyPane);


        // Add the Scene to the Stage
        stage.setScene(scene);
        // Set the Title of the Stage
        stage.setTitle("MATHDOKU");
        // Display the Stage
        stage.show();
    }

    private void drawGrid() {
        grid.setMaxSize(num.get() * width,num.get() * width);

        for (int row = 0; row < num.get(); row++) {
            for (int col = 0; col < num.get(); col++) {
                Rectangle rect = new Rectangle(width, width);
                rect.setStroke(Color.BLACK);
                rect.setFill(Color.WHITE);
                rectangleArrayList.add(rect);
                rect.relocate(row * width, col * width);
                pane.getChildren().add(rect);
            }
        }

        for (int row = 0; row < num.get(); row++) {
            for (int col = 0; col < num.get(); col++) {
                BorderPane cell = new BorderPane();
                cell.setPrefSize(width, width);

                // Textfield
                TextField text = new TextField();
                textFieldArrayList.add(text);
                int finalRow = row;
                int finalCol = col;
                text.textProperty().addListener((observableValue, oldValue, newValue) -> {
                    if ((newValue.length()>1) || !"0123456789".contains(newValue)){
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
                cell.relocate(row * width, (col * width)-10);
                /* if (text.getFont().equals(40)) {
                    cell.relocate(row * width, (col * width)-20);
                }

                 */
                grid.getChildren().add(cell);
            }
        }
    }

    private void checkDuplicate(){
        int[] row = new int[num.get()];
        int[] col = new int[num.get()];
        for (int i = 0; i < num.get(); i++){
            for (int j = 0; j < num.get(); j++){
                row[j] = arrayOfInput[i][j];
                col[j] = arrayOfInput[j][i];
            }
            System.out.println("row "  + i);
            System.out.println(Arrays.toString(row));
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
            System.out.println("Col " + i);
            System.out.println(Arrays.toString(col));
        }
    }

    private void checkCellMistake(){
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
            }
            else {
                arrayRow++;
                cellPositionArr2D.add(new ArrayList<Integer>());
                arrayCol = 0;
            }
        }

        // checks if correct values entered in cell
        int row = 0;
        for (String label : labels){
            target = label.substring(0,(label.length() - 1));
            operation = label.substring(label.length()-1);
            System.out.println(operation);
            switch (operation) {
                case "+":
                    for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                        if (!textFieldArrayList.get(cellPositionArr2D.get(row).get(i) - 1).getText().equals("")) {
                            result = result + Integer.parseInt(textFieldArrayList.get(cellPositionArr2D.get(row).get(i) - 1).getText());
                        }
                    }
                    System.out.println(result);
                    if (result != Integer.parseInt(target)) {
                        for (int i = 0; i < cellPositionArr2D.get(row).size(); i++) {
                            rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1).setFill(Color.RED);
                            rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1).setStroke(Color.BLACK);
                            rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1).setStrokeWidth(0.5);
                            FadeTransition fadeTransition = new FadeTransition(Duration.millis(4000), rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1));
                            fadeTransition.setFromValue(0.5);
                            fadeTransition.setToValue(0);
                            fadeTransition.setAutoReverse(true);
                            fadeTransition.play();
                            win = false;
                        }
                    }
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
                            FadeTransition fadeTransition = new FadeTransition(Duration.millis(4000), rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1));
                            fadeTransition.setFromValue(0.5);
                            fadeTransition.setToValue(0);
                            fadeTransition.setAutoReverse(true);
                            fadeTransition.play();
                            win=false;
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
                            FadeTransition fadeTransition = new FadeTransition(Duration.millis(4000), rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1));
                            fadeTransition.setFromValue(0.5);
                            fadeTransition.setToValue(0);
                            fadeTransition.setAutoReverse(true);
                            fadeTransition.play();
                            win=false;
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
                            FadeTransition fadeTransition = new FadeTransition(Duration.millis(4000), rectangleArrayList.get(cellPositionArr2D.get(row).get(i) - 1));
                            fadeTransition.setFromValue(0.5);
                            fadeTransition.setToValue(0);
                            fadeTransition.setAutoReverse(true);
                            fadeTransition.play();
                            win=false;
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

    public void winningAnimation(){
        boolean full = true;
        for (TextField tf: textFieldArrayList) {
            String s = tf.getText();
            if (s == null) {
                full = false;
                break;
            }
        }
        if (win && full) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "CONGRATULATIONS");
            alert.showAndWait();
        }
    }

    // UNUSED METHODS
    // Function returns a L-shaped cage
    // parameter longBranch defines number of cells in the long branch of L
    private Group l_Shape(int longBranch, int width){
        Group lshape = new Group();

        Rectangle h11 = new Rectangle(width,4);
        h11.relocate(10,10);
        Rectangle h22 = new Rectangle(width*longBranch,4);
        h22.relocate(10+width,10+width);
        Rectangle h31 = new Rectangle((longBranch+1)*width,4);
        h31.relocate(10,10+(width*2));
        Rectangle v21 = new Rectangle(4,width*2);
        v21.relocate(10,10);
        Rectangle v11 = new Rectangle(4,width);
        v11.relocate(10+width,10);
        Rectangle v12 = new Rectangle(4,width);
        v12.relocate(10+(width*(longBranch+1)),10+width);

        lshape.getChildren().addAll(h11,h22,h31,v11,v12,v21);

        return lshape;
    }

    // Function returns a square cage
    // parameter N defines size of square cage
    private Rectangle squareNxN(int N, int width) {
        Rectangle rect = new Rectangle(N*width, N*width, Color.TRANSPARENT);
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(4);
        return rect;
    }

    // Function returns a rectangle shaped cage
    // parameter N defines number of squares in the line cage
    private Rectangle cell_line(int N, int width) {
        Rectangle rect = new Rectangle(N*width, width, Color.TRANSPARENT);
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(4);
        return rect;
    }

    //TODO Method including all cages and labelling
    private void cages() {
        int n = 50;
        Rectangle r1 = cell_line(2,n);
        r1.setRotate(90);
        r1.relocate(-25, 25);

        Rectangle r2 = cell_line(2, n);
        r2.relocate(50,0);

        Rectangle r3 = cell_line(2,n);
        r3.relocate(50,50);

        Rectangle r4 = cell_line(2,n);
        r4.setRotate(90);
        r4.relocate(125, 25);

        Rectangle r5 = cell_line(2,n);
        r5.setRotate(90);
        r5.relocate(175, 75);
        
        Rectangle r6 = cell_line(2, n);
        r6.relocate(100,100);

        Rectangle r7 = cell_line(2, n);
        r7.relocate(0,200);

        Rectangle r8 = cell_line(2,n);
        r8.relocate(75,175);
        r8.setRotate(90);

        Rectangle r9 = cell_line(3, n);
        r9.relocate(0,250);

        Rectangle r10 = cell_line(2,n);
        r10.relocate(150,250);

        Rectangle r11 = cell_line(2,n);
        r11.relocate(200,150);

        Rectangle r12 = cell_line(2, n);
        r12.setRotate(90);
        r12.relocate(225, 225);

        Group l1 = l_Shape(2,n);
        l1.setRotate(90);
        l1.relocate(175,25);
        l1.setScaleY(-1);

        Group l2 = l_Shape(1,n);
        l2.relocate(150,150);

        Rectangle s1 = squareNxN(2,50);
        s1.relocate(0,100);

        grid.getChildren().addAll(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, s1, l1, l2);
    }

    private void checkMistakes() {
        ArrayList<Integer> row = new ArrayList<>();
        ArrayList<Integer> col = new ArrayList<>();
        /*
        int[] row = new int[num.get()];
        int[] col = new int[num.get()];

         */
        int width = 50;
        for (int i = 0; i < num.get(); i++) {
            for (int j = 0; j < num.get(); j++) {
                row.add(j, arrayOfInput[i][j]);
                col.add(j, arrayOfInput[j][i]);
                /*
                row[j] = arrayOfInput[i][j];
                col[j] = arrayOfInput[j][i];

                 */
            }

            for (int q = 0; q < num.get(); q++) {
                for (int j = q + 1; j < num.get(); j++) {
                    if (findDuplicates(row) != 0) {
                        // arrayFlags[i][j] = false;
                        System.out.println("Row " + ( i + 1 ) + " has duplicates");
                        Rectangle rect = new Rectangle(num.get() * width, width, Color.RED);
                        // rect.setOpacity(0.5);
                        rect.setStroke(Color.BLACK);
                        rect.relocate(0, i * width);
                        grid.getChildren().add(rect);
                    }

                    if (findDuplicates(col) != 0) {
                        // arrayFlags[i][j] = false;
                        System.out.println("Column " + (j + 1) + " has duplicates");
                        Rectangle rect = new Rectangle(width, num.get() * width, Color.RED);
                        // rect.setOpacity(0.5);
                        rect.setStroke(Color.BLACK);
                        rect.relocate(i * width, 0);
                        grid.getChildren().add(rect);
                    }
                }
            }
        }
    }

    private int findDuplicates (ArrayList<Integer> a) {
        HashSet<Integer> mySet = new HashSet<>(a);
        int dup = 0;
        for (int value : a) {
            if (a.size() != mySet.size()) {
                dup = value;
                break;
            }
        }
        return dup;
    }

}
