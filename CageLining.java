import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.Collections;

public class CageLining {
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
    public CageLining(String label, ArrayList<Integer> cells){
        this.label = new Label(label);
        this.cells=cells;
        Collections.sort(this.cells);
    }

    public CageLining(ArrayList<Integer> cells){
        this.cells = cells;
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

    public void setPositionX(int N, boolean change){
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

    public void setPositionY(int N){
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
                shape = hero(1,width);
                break;
            case 2:
                shape = hero(2,width);
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
                    shape = hero(3,width);
                }

                if (cells.get(1) == (cells.get(0)+N) && cells.get(2) == (cells.get(1)+N) ){
                    shape = hero(3,width);
                    Rotate rotate = new Rotate();
                    setPositionX(N,true);
                    rotate.setAngle(90);
                    rotate.setPivotX(0);
                    rotate.setPivotY(0);
                    shape.getTransforms().add(rotate);
                }

                if(cells.get(1) == (cells.get(0)+1)) {
                    if (cells.get(2) == (cells.get(0) + N)) {
                        shape = blueRicky(1, width);
                        Rotate rotate = new Rotate();
                        setPositionX(N, true);
                        rotate.setAngle(90);
                        rotate.setPivotX(width/1.54);
                        rotate.setPivotY(width/1.54);
                        shape.getTransforms().add(rotate);

                    } else if (cells.get(2) == (cells.get(1) + N)) {
                        shape = blueRicky(1, width);
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
                        shape = orangeRicky(1, width);
                        setPositionX(N, false);
                    } else if (cells.get(1) == (cells.get(0) + N)) {
                        shape = blueRicky(1, width);
                        setPositionX(N, false);
                    }
                }

            case 4:
                if(cells.size()==4){
                    if(cells.get(1) == cells.get(0)+1 && cells.get(2)==cells.get(0)+N && cells.get(3) == cells.get(2)+1)
                        shape = smashboy(2,width);

                    if(cells.get(1) == cells.get(0) + 1 && cells.get(3) == cells.get(2) + 1 && cells.get(2) == cells.get(1) + 1 && cells.get(3) == cells.get(0) + 3)
                        shape = hero(4,width);

                    if(cells.get(1) == cells.get(0) + N && cells.get(3) == cells.get(2) + N && cells.get(2) == cells.get(1) + N) {
                        shape = hero(4,width);
                        Rotate rotate = new Rotate();
                        setPositionX(N,true);
                        rotate.setAngle(90);
                        rotate.setPivotX(0);
                        rotate.setPivotY(0);
                        shape.getTransforms().add(rotate);
                    }

                    if(cells.get(1) == cells.get(0) + 1 && cells.get(2) == cells.get(1) + N && cells.get(3) == cells.get(2) + N){
                        shape = blueRicky(2,width);
                        Rotate rotate = new Rotate();
                        setPositionX(N, true);
                        rotate.setAngle(90);
                        rotate.setPivotX(width/0.465);
                        rotate.setPivotY(width/0.465);
                        shape.setScaleX(-1);
                        shape.getTransforms().add(rotate);
                    }

                    if(cells.get(1) == cells.get(0)+N && cells.get(2) == cells.get(1)+1 && cells.get(3) == cells.get(1)+N)
                        shape = teeweeRight(width);

                    if (cells.get(2) == cells.get(0)+N && cells.get(1) == cells.get(2)-1 && cells.get(3) == cells.get(2)+N)
                        shape = teeweeleft(width);

                    if (cells.get(2) == cells.get(0)+N && cells.get(1) == cells.get(2)-1 && cells.get(3) == cells.get(2)+1)
                        shape = tee_wee(width);

                    if (cells.get(1) == cells.get(0)+1 && cells.get(2) == cells.get(1)+1 && cells.get(3) == cells.get(1)+N)
                        shape = teeweeUpsideDown(width);
                }
                break;
            case 5:
                if (cells.get(2) == cells.get(0)+N && cells.get(1) == cells.get(2)-1 && cells.get(3) == cells.get(2)+1 && cells.get(4) == cells.get(2) + N)
                    shape = cross(width);

                if(cells.get(1) == cells.get(0) + 1 && cells.get(3) == cells.get(2) + 1 && cells.get(2) == cells.get(1) + 1 && cells.get(3) == cells.get(0) + 3 && cells.get(4) == cells.get(0) + 4)
                    shape = hero(5,width);

                break;
        }
        return shape;
    }

    // SHAPES ARE CHOSEN PER TETRIS NAMES
    public Group blueRicky(int longBranch, int width){
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

    public Group tee_wee(int width) {
        Group teewee = new Group();
        Rectangle h11 = new Rectangle(width, 4);
        h11.relocate(10, 10);
        Rectangle v11 = new Rectangle(4, width);
        v11.relocate(10, 10);
        Rectangle v21 = new Rectangle(4, width);
        v21.relocate(10 + width, 10);
        Rectangle h31 = new Rectangle(width, 4);
        h31.relocate(10 + width, 10 + width);
        Rectangle v31 = new Rectangle(4, width);
        v31.relocate(10 + width * 2, 10 + width);
        Rectangle v41 = new Rectangle(4, width);
        v41.relocate(10 + width*2, 10+width);
        Rectangle h22 = new Rectangle(width*2, 4);
        h22.relocate(10, 10 + width * 2);

        teewee.getChildren().addAll(h11,h31,v11,v21,v31,v41,h22);

        return teewee;
    }


    public Group teeweeRight(int width){
        Group teewee = new Group();
        Rectangle h11 = new Rectangle(width,4);
        h11.relocate(10,10);
        Rectangle v11 = new Rectangle(4,width*3);
        v11.relocate(10,10);
        Rectangle v21 = new Rectangle(4,width);
        v21.relocate(10+width,10);
        Rectangle h31 = new Rectangle(width,4);
        h31.relocate(10+width,10+width);
        Rectangle v31 = new Rectangle(4,width);
        v31.relocate(10+width*2,10+width);
        Rectangle h21 = new Rectangle(width,4);
        h21.relocate(10,10+width*3);
        Rectangle h41 = new Rectangle(width,4);
        h41.relocate(10+width,10+width*2);
        Rectangle v41 = new Rectangle(4,width);
        v41.relocate(10,10+width*2);
        Rectangle v51 = new Rectangle(4,width);
        v51.relocate(10+width,10+width*2);

        teewee.getChildren().addAll(h11,h31,v11,v21,v31,h21,h41,v41,v51);
        return teewee;
    }

    public Group teeweeUpsideDown(int width){
        Group teewee = new Group();
        Rectangle h11 = new Rectangle(width*3,4);
        h11.relocate(10,10);
        Rectangle v11 = new Rectangle(4,width);
        v11.relocate(10,10);
        Rectangle v21 = new Rectangle(4,width);
        v21.relocate(10+width*3,10);
        Rectangle h21 = new Rectangle(width,4);
        h21.relocate(10,10+width);
        Rectangle h31 = new Rectangle(width,4);
        h31.relocate(10+width*2,10+width);
        Rectangle v31 = new Rectangle(4,width);
        v31.relocate(10+width,10+width);
        Rectangle v41 = new Rectangle(4,width);
        v41.relocate(10+width*2,10+width);
        Rectangle h41 = new Rectangle(width,4);
        h41.relocate(10+width,10+width*2);

        teewee.getChildren().addAll(h11,v11,v21,h21,h31,v31,v41,h41);

        return teewee;
    }

    public Group teeweeleft(int width){
        Group teewee = new Group();

        Rectangle h11 = new Rectangle(width,4);
        h11.relocate(10,10);
        Rectangle v11 = new Rectangle(4,width);
        v11.relocate(10,10);
        Rectangle v13 = new Rectangle(4,width*3);
        v13.relocate(10+width,10);
        Rectangle v21 = new Rectangle(4,width);
        v21.relocate(10,10+width*2);
        Rectangle h21 = new Rectangle(width,4);
        h21.relocate(10,10+width*3);

        teewee.getChildren().addAll(h11,v11,v13,v21);

        return teewee;

    }

    // Cross shape
    public Group cross(int width){
        Group cross = new Group();
        Rectangle h11 = new Rectangle(width,4);
        h11.relocate(10,10);
        Rectangle v11 = new Rectangle(4,width);
        v11.relocate(10,10);
        Rectangle v21 = new Rectangle(4,width);
        v21.relocate(10+width,10);
        Rectangle h31 = new Rectangle(width,4);
        h31.relocate(10+width,10+width);
        Rectangle v31 = new Rectangle(4,width);
        v31.relocate(10+width*2,10+width);
        Rectangle h41 = new Rectangle(width,4);
        h41.relocate(10+width,10+width*2);
        Rectangle v41 = new Rectangle(4,width);
        v41.relocate(10,10+width*2);
        Rectangle v51 = new Rectangle(width,4);
        v51.relocate(10,10+width*3);
        Rectangle v61 = new Rectangle(4,width);
        v61.relocate(10+width,10+width*2);

        cross.getChildren().addAll(h11,v11,v21,h31,v31,h41,v41,v51,v61);
        return cross;
    }

    // Lreflected shape
    public Group orangeRicky(int longBranch, int width){
        Group thiccL = new Group();

        Rectangle h11 = new Rectangle(width,4);
        h11.relocate(10,10);
        Rectangle v22 = new Rectangle(4,width*2);
        v22.relocate(10+width,10);
        Rectangle v11 = new Rectangle(4,width);
        v11.relocate(10,10);
        Rectangle v12 = new Rectangle(width,4);
        v12.relocate(10,10+2*width);

        thiccL.getChildren().addAll(h11,v11,v12,v22);

        return thiccL;
    }

    // parameter N defines size of square
    public Group smashboy(int N, int width) {
        Group squareNN = new Group();
        Rectangle rect = new Rectangle(N*width, N*width, Color.TRANSPARENT);
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(4);
        squareNN.getChildren().add(rect);
        return squareNN;
    }

    // parameter N defines number of squares in the line cage
    public Group hero(int N, int width) {
        Group cellLine = new Group();
        Rectangle rect = new Rectangle(N*width, width, Color.TRANSPARENT);
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(4);
        cellLine.getChildren().add(rect);
        return cellLine;
    }

    // parameter N defines number of squares in the line cage in vertical
    public Group vertical_hero(int N, int width){
        Group batonnette = new Group();
        Rectangle rect = new Rectangle(width, N*width, Color.TRANSPARENT);
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(5);
        batonnette.getChildren().add(rect);
        return batonnette;
    }

    public Group Lrotated90(int width){
        Group thiccL = new Group();

        Rectangle h1 = new Rectangle(width*2,5);
        h1.relocate(10,10);
        Rectangle h2 = new Rectangle(width,5);
        h2.relocate(10,10 + width);
        Rectangle h3 = new Rectangle(width,5);
        h3.relocate(10+width,10+width*2);
        Rectangle v1 = new Rectangle(5,width);
        v1.relocate(10,10);
        Rectangle v2 = new Rectangle(5,width*2);
        v2.relocate(10+width*2,10);
        Rectangle v3 = new Rectangle(5,width);
        v3.relocate(10+width,10+width);

        thiccL.getChildren().addAll(h1,h2,h3,v1,v2,v3);
        return thiccL;
    }
}
