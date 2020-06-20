package ethicalengine;

/**
 * InvalidInputException is an exception that is thrown when an invalid input is given when
 * a user inputs their decision to consent to their data being logged.
 * @author Benjamin Tam
 * @author email: ytam2@student.unimelb.edu.au
 * @author studentID: 889835
 */

public class InvalidInputException extends Exception {

    /**
     * Constructor for InvalidInputException.
     */
    public InvalidInputException(){
        super("Invalid response. Do you consent to have your decisions saved to a file? (yes/no)");
    }
}
