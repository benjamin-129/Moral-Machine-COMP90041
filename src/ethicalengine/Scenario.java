package ethicalengine;

/**
 * Scenario is a class that holds the attributes of each scenario that is judged by the Moral
 * Machine.
 * @author Benjamin Tam
 * @author email: ytam2@student.unimelb.edu.au
 * @author studentID: 889835
 */

public class Scenario {
    private Character[] passengers;
    private Character[] pedestrians;
    private boolean isLegalCrossing;

    /**
     * Empty Constructor
     */
    public Scenario(){}

    /**
     * Constructor for the scenario class that creates a scenario using the parameters below
     * @param passengers an array of Character class objects that represents the passengers in
     *                   the scenario.
     * @param pedestrians an array of Character class objects that represents the pedestrians in
     *                    the scenario.
     * @param isLegalCrossing a boolean that represents the status of the legality of the road
     *                        crossing in the scenario.
     */

    public Scenario(Character[] passengers, Character[] pedestrians, boolean isLegalCrossing){
        this.passengers = passengers;
        this.pedestrians = pedestrians;
        setLegalCrossing(isLegalCrossing);
    }

    /**
     * hasYouInCar is a method that returns a boolean that represents the status of whether the
     * user is a passenger in the car.
     * @return boolean status to represent the users presence as a passenger.
     */

    public boolean hasYouInCar(){
        for(int i=0; i < passengers.length; i++){
            if (passengers[i].isYou()){
                return true;
            }
        }
        return false;
    }

    /**
     * hasYouInLane is a method that returns a boolean that represents the status of whether the
     * user is a pedestrian.
     * @return boolean status to represent the users presence as a pedestrian.
     */
    public boolean hasYouInLane(){
        for(int i=0; i < pedestrians.length; i++){
            if(pedestrians[i].isYou()){
                return true;
            }
        }
        return false;
    }

    /**
     * getPassengers is a method that returns a character array of the passengers in the scenario.
     * @return character array of passengers in the scenario.
     */
    public Character[] getPassengers(){
        return this.passengers;
    }

    /**
     * getPedestrians is a method that returns a character array of the pedestrians in the scenario.
     * @return character array of pedestrians in the scenario.
     */
    public Character[] getPedestrians(){
        return this.pedestrians;
    }

    /**
     * isLegalCrossing is a method that returns a boolean that represents the legality of the
     * road crossing in the scenario.
     * @return boolean of the legality of the road crossing.
     */
    public boolean isLegalCrossing(){
        return this.isLegalCrossing;
    }

    /**
     * setLegalCrossing is a method that sets the status of the legality of the road crossing for
     * the scenario.
     * @param isLegalCrossing boolean that represents the legality of the road crossing.
     */
    public void setLegalCrossing(boolean isLegalCrossing){
        this.isLegalCrossing = isLegalCrossing;
    }

    /**
     * getPassengerCount is a method that returns the number of passengers in the scenario.
     * @return number of passengers in the scenario.
     */
    public int getPassengerCount(){
        return passengers.length;
    }

    /**
     * getPedestrianCount is a method that returns the number of pedestrians in the scenario.
     * @return number of pedestrians in the scenario.
     */
    public int getPedestrianCount(){
        return pedestrians.length;
    }

    /**
     * toString is a method that returns the audit results as a string.
     * @return audit results.
     */
    public String toString(){
        String ynLegal;
        if (isLegalCrossing()){
            ynLegal = "yes";
        }
        else{
            ynLegal = "no";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("======================================\n" +
                "# Scenario\n" + "======================================\n"
                + "Legal Crossing: "+ynLegal + "\nPassengers ("
                +getPassengerCount()+ ")");

        for(int i = 0; i < passengers.length; i++){
            sb.append("\n- " + passengers[i].toString());
        }
        sb.append("\nPedestrians " + "(" + getPedestrianCount()+ ")");
        for(int i = 0; i < pedestrians.length; i++){
            sb.append("\n- " + pedestrians[i].toString());
        }
        return sb.toString();

    }

}
