import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Reader {
    private BufferedReader reader;

    //Constructor for this class
    //@param Name of file
    //Throws exception if the file is not found
    public Reader(String fileName) throws FileNotFoundException {
        //new BufferReader to read from the file with the given fileName
        reader = new BufferedReader(new FileReader(fileName));
    }

    //Method to return the next line of the contents of the file
    public String getLine() {
        String thisLine = null;
        try {
            thisLine = reader.readLine();
        } catch(IOException exception) {
            System.err.println("End of file reached");
        }
        return thisLine;
    }
}
