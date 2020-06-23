package ethicalengine;

/**
 * InvalidCharacteristicException is an exception that is thrown when there is an invalid value
 * field.
 * @author Benjamin Tam
 * @author email: ytam2@student.unimelb.edu.au
 * @author studentID: 889835
 */
public class InvalidCharacteristicException extends Exception{

    /**
     * Empty Constructor
     */
    public InvalidCharacteristicException(){}

    /**
     * Constructor for InvalidCharacteristicException
     * @param line line number
     */
    public InvalidCharacteristicException(int line){
        super("WARNING: invalid characteristic in config file in line " + line);
    }
}
