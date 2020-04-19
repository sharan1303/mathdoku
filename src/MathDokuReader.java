import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MathDokuReader {
    private BufferedReader reader;

    //Constructor for this class
    //@param Name of file
    //Throws exception if the file is not found
    public MathDokuReader(String fileName) throws FileNotFoundException {
        //new BufferReader to read from the file with the given fileName
        reader = new BufferedReader(new FileReader(fileName));
    }

    //Method to return the next line of the contents of the file
    public String getLine(){
        String thisLine = null;
        try {
            //variable to store the line which was read
            thisLine= reader.readLine();
            //checking whether the file has a content at this line
        }
        //catching exception if IO exception has occurred
        catch(IOException exception){
            //Outputting appropriate error message
            System.out.println("End of file reached");
        }
        return thisLine;
    }
}
