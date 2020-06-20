package ethicalengine;


public class Scenario {
    private Character[] passengers;
    private Character[] pedestrians;
    private boolean isLegalCrossing;

    public Scenario(Character[] passengers, Character[] pedestrians, boolean isLegalCrossing){
        this.passengers = passengers;
        this.pedestrians = pedestrians;
        setLegalCrossing(isLegalCrossing);
    }

    public boolean hasYouInCar(){
        for(int i=0; i < passengers.length; i++){
            if (passengers[i].isYou()){
                return true;
            }
        }
        return false;
    }

    public boolean hasYouInLane(){
        for(int i=0; i < pedestrians.length; i++){
            if(pedestrians[i].isYou()){
                return true;
            }
        }
        return false;
    }

    public Character[] getPassengers(){
        return this.passengers;
    }

    public Character[] getPedestrians(){
        return this.pedestrians;
    }

    public boolean isLegalCrossing(){
        return this.isLegalCrossing;
    }

    public void setLegalCrossing(boolean isLegalCrossing){
        this.isLegalCrossing = isLegalCrossing;
    }

    public int getPassengerCount(){
        return passengers.length;
    }

    public int getPedestrianCount(){
        return pedestrians.length;
    }

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
