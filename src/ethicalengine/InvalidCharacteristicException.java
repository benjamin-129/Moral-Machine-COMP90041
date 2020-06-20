package ethicalengine;

public class InvalidCharacteristicException extends Exception{

    public InvalidCharacteristicException(int line){
        super("WARNING: invalid characteristic in config file in line " + line);
    }
}
