import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.Collections;

public class Cage {
    //store the label of the cage
    private Label label;
    //store the cells of the cage
    private ArrayList<Integer> cells;
    //position X of the cage
    private int positionX;
    //position Y of the cage
    private int positionY;
    //position X of the cage
    private int positionXLabel;

    //Constructor for a cage
    public Cage(String label, ArrayList<Integer> cells){
        this.label = new Label(label);
        this.cells=cells;
        Collections.sort(this.cells);
    }

    //getter method of cells
    public ArrayList<Integer> getCells(){
        return cells;
    }

    //getter method for label
    public Label getLabel(){
        return label;
    }

    public int getXPositionToDraw(){
        return positionX;
    }

    public void setXPositionLabel(int positionXLabel,int N){
        if(positionXLabel==N){
            this.positionXLabel = positionXLabel-1;
        } else {
            this.positionXLabel = positionXLabel;
        }
    }

    public int getPositionXLabel(){
        return positionXLabel;
    }

    public int getYPositionToDraw(){
        return positionY;
    }

    private void setPositionX(int N, boolean change){
        if (N != 0) {
            if(!change) {
                positionX = (cells.get(0) % N) - 1;
                if (positionX == -1) {
                    positionX = N-1;
                }
                setXPositionLabel(positionX,N);
            } else if (!(positionX == N)){
                positionX += 1;
            }
        }
    }

    private void setPositionY(int N){
        if (N != 0) {
            positionY = (cells.get(0) / N);
            if (cells.get(0) % N == 0) {
                positionY = positionY - 1;
            }
        }
    }

    public Group getShape(int width, int N){
        setPositionX(N,false);
        setPositionY(N);
        Group shape = null;
        switch (cells.size()){
            case 1:
                shape = cell_line(1,width);
                break;
            case 2:
                shape = cell_line(2,width);
                if (!(cells.get(1) == (cells.get(0)+1))){
                    Rotate rotate = new Rotate();
                    setPositionX(N,true);
                    rotate.setAngle(90);
                    rotate.setPivotX(0);
                    rotate.setPivotY(0);
                    shape.getTransforms().add(rotate);
                }
                break;
            case 3:
                if (cells.get(1) == (cells.get(0)+1) && cells.get(2) == (cells.get(1)+1) ){
                    shape = cell_line(3,width);
                }

                if (cells.get(1) == (cells.get(0)+N) && cells.get(2) == (cells.get(1)+N) ){
                    shape = cell_line(3,width);
                    Rotate rotate = new Rotate();
                    setPositionX(N,true);
                    rotate.setAngle(90);
                    rotate.setPivotX(0);
                    rotate.setPivotY(0);
                    shape.getTransforms().add(rotate);
                }

                if(cells.get(1) == (cells.get(0)+1)) {
                    if (cells.get(2) == (cells.get(0) + N)) {
                        shape = L(1, width);
                        Rotate rotate = new Rotate();
                        setPositionX(N, true);
                        rotate.setAngle(90);
                        rotate.setPivotX(width/1.54);
                        rotate.setPivotY(width/1.54);
                        shape.getTransforms().add(rotate);

                    } else if (cells.get(2) == (cells.get(1) + N)) {
                        shape = L(1, width);
                        Rotate rotate = new Rotate();
                        setPositionX(N, true);
                        rotate.setAngle(180);
                        rotate.setPivotX(width/1.54);
                        rotate.setPivotY(width/0.87);
                        shape.getTransforms().add(rotate);
                    }
                }

                if (cells.get(2) == (cells.get(1)+1)) {
                    if (cells.get(2) == (cells.get(0) + N)) {
                        shape = Lverson2(1, width);
                        Rotate rotate = new Rotate();
                        setPositionX(N, false);
                        rotate.setAngle(270);
                        rotate.setPivotX(width);
                        rotate.setPivotY(width);
                        shape.getTransforms().add(rotate);
                    } else if (cells.get(1) == (cells.get(0) + N)) {
                        shape = L(1, width);
                        setPositionX(N, false);
                    }
                }

            case 4: //TODO remaining cages
                if(cells.size()==4){
                    if(cells.get(1) == cells.get(0)+1 && cells.get(2)==cells.get(0)+N && cells.get(3) == cells.get(2)+1){
                        shape = squareNxN(2,width);
                    }
                    if(cells.get(1) == cells.get(0) + 1 && cells.get(3) == cells.get(2) + 1 && cells.get(2) == cells.get(1) + 1 && cells.get(3) == cells.get(0) + 3) {
                        shape = cell_line(4,width);
                    }
                    if(cells.get(1) == cells.get(0) + N && cells.get(3) == cells.get(2) + N && cells.get(2) == cells.get(1) + N) {
                        shape = cell_line(4,width);
                        Rotate rotate = new Rotate();
                        setPositionX(N,true);
                        rotate.setAngle(90);
                        rotate.setPivotX(0);
                        rotate.setPivotY(0);
                        shape.getTransforms().add(rotate);
                    }
                    if(cells.get(1) == cells.get(0) + 1 && cells.get(2) == cells.get(1) + N && cells.get(3) == cells.get(2) + N){
                        shape = L(2,width);
                        Rotate rotate = new Rotate();
                        setPositionX(N, true);
                        rotate.setAngle(90);
                        rotate.setPivotX(width/0.465);
                        rotate.setPivotY(width/0.465);
                        shape.setScaleX(-1);
                        shape.getTransforms().add(rotate);
                    }
                }
                break;
        }
        return shape;
    }

    private Group L(int longBranch, int width){
        Group thiccL = new Group();

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

        thiccL.getChildren().addAll(h11,h22,h31,v11,v12,v21);
        return thiccL;
    }

    private Group Lreflected(int longBranch, int width){
        Group thiccL = new Group();

        Rectangle h11 = new Rectangle(width,4);
        h11.relocate(10+width,10-width);
        Rectangle h22 = new Rectangle(width*longBranch,4);
        h22.relocate(10,10);
        Rectangle h31 = new Rectangle((longBranch+1)*width,4);
        h31.relocate(10,10+(width));
        Rectangle v21 = new Rectangle(4,width*2);
        v21.relocate(10+width*2,10-width);
        Rectangle v11 = new Rectangle(4,width);
        v11.relocate(10+width,10-width);
        Rectangle v12 = new Rectangle(4,width);
        v12.relocate(10,10);

        thiccL.getChildren().addAll(h11,h22,h31,v11,v12,v21);
        return thiccL;
    }

    private Group Lverson2(int longBranch, int width){
        Group thiccL = new Group();

        Rectangle h11 = new Rectangle(width,4);
        h11.relocate(0,0);
        Rectangle h22 = new Rectangle(width*longBranch,4);
        h22.relocate(width,width);
        Rectangle h31 = new Rectangle((longBranch+1)*width,4);
        h31.relocate(0,(width*2));
        Rectangle v21 = new Rectangle(4,width*2);
        v21.relocate(0,0);
        Rectangle v11 = new Rectangle(4,width);
        v11.relocate(width,0);
        Rectangle v12 = new Rectangle(4,width);
        v12.relocate((width*(longBranch+1)),width);

        h11.setOpacity(0);
        h22.setOpacity(0);
        h31.setOpacity(0);
        v21.setOpacity(0);
        v11.setOpacity(0);
        v12.setOpacity(0);

        thiccL.getChildren().addAll(h11,h22,h31,v11,v12,v21);
        return thiccL;
    }

    // parameter N defines size of square
    private Group squareNxN(int N, int width) {
        Group squareNN = new Group();
        Rectangle rect = new Rectangle(N*width, N*width, Color.TRANSPARENT);
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(4);
        squareNN.getChildren().add(rect);
        return squareNN;
    }

    // parameter N defines number of squares in the line cage
    private Group cell_line(int N, int width) {
        Group cellLine = new Group();
        Rectangle rect = new Rectangle(N*width, width, Color.TRANSPARENT);
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(4);
        cellLine.getChildren().add(rect);
        return cellLine;
    }



}
