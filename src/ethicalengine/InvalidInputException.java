package ethicalengine;

public class InvalidInputException extends Exception {
    public InvalidInputException(){
        super("Invalid response. Do you consent to have your decisions saved to a file? (yes/no)");
    }
}
