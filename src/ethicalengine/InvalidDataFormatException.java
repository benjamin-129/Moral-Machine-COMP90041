package ethicalengine;

/**
 * InvalidDataFormatException is an exception that is thrown when a row of data from a config file
 * does not have 10 values.
 * @author Benjamin Tam
 * @author email: ytam2@student.unimelb.edu.au
 * @author studentID: 889835
 */
public class InvalidDataFormatException extends Exception {

    /**
     * Constructor for InvalidDataFormatException
     * @param line is an int that states the line number of the csv file.
     */
    public InvalidDataFormatException(int line){
        super("WARNING: invalid data format in config file in line " + line);
    }

}
