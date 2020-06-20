package ethicalengine;

public class InvalidDataFormatException extends Exception {

    public InvalidDataFormatException(int line){
        super("WARNING: invalid data format in config file in line " + line);
    }

}
