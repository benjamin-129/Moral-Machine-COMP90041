package ethicalengine;
import java.util.Random;

public class ScenarioGenerator {

    private Random r;
    private int passengerCountMinimum = 1;
    private int passengerCountMaximum = 5;
    private int pedestrianCountMinimum = 1;
    private int pedestrianCountMaximum = 5;

    // random seed
    public ScenarioGenerator(){
        this.r = new Random();
    }

    public ScenarioGenerator(long seed){
        this.r = new Random(seed);
    }

    public ScenarioGenerator(long seed, int passengerCountMinimum, int passengerCountMaximum,
                             int pedestrianCountMinimum, int pedestrianCountMaximum){
        this.r = new Random(seed);
        this.passengerCountMinimum = passengerCountMinimum;
        this.passengerCountMaximum = passengerCountMaximum;
        this.pedestrianCountMinimum = pedestrianCountMinimum;
        this. pedestrianCountMaximum = pedestrianCountMaximum;
    }

    public void setPassengerCountMinimum(int passengerCountMinimum) {
        this.passengerCountMinimum = passengerCountMinimum;
    }

    public void setPassengerCountMaximum(int passengerCountMaximum) {
        this.passengerCountMaximum = passengerCountMaximum;
    }

    public void setPedestrianCountMinimum(int pedestrianCountMinimum) {
        this.pedestrianCountMinimum = pedestrianCountMinimum;
    }

    public void setPedestrianCountmaximum(int pedestrianCountMaximum) {
        this.pedestrianCountMaximum = pedestrianCountMaximum;
    }


    public Person getRandomPerson(){
        int randBodytype = r.nextInt(Character.BodyType.values().length);
        int randAge = r.nextInt(118); // Oldest person alive is 117 years old
        int randProf = r.nextInt(Person.Profession.values().length - 1); // Ignore NONE enum
        int randGend = r.nextInt(Character.Gender.values().length);
        boolean randPreg = r.nextBoolean();

        Person p = new Person(randAge, Person.Profession.values()[randProf],
                Character.Gender.values()[randGend], Character.BodyType.values()[randBodytype],
                randPreg);

        return p;
    }

    public Animal getRandomAnimal(){
        String[] species = new String[] {"cat", "dog", "bird", "hamster", "rabbit"};

        int randGend = r.nextInt(Character.Gender.values().length-1);
        int randAge = r.nextInt(121); // Oldest pet is 120 years old
        int randBodytype = r.nextInt(Character.BodyType.values().length);
        int randSpecies = r.nextInt(species.length);
        boolean randIsPet = r.nextBoolean();

        Animal a = new Animal(species[randSpecies], randAge, Character.Gender.values()[randGend],
                Character.BodyType.values()[randBodytype], randIsPet);


        return a;
    }

    public Scenario generate(){
        boolean isLegalCrossing = r.nextBoolean();
        Character[] passengers = new Character[r.nextInt(passengerCountMaximum
                - passengerCountMinimum) + passengerCountMinimum];
        Character[] pedestrians = new Character[r.nextInt(pedestrianCountMaximum
                - pedestrianCountMinimum) + pedestrianCountMinimum];


        // More likely to have People around on a street instead of animals:
        // Humans 80% chance, Animals 20% chance
        // Possible to only have animals in car as its a self driving car
        for(int i = 0; i < passengers.length; i++){
            int odds = r.nextInt(101);
            if (odds > 19){
                passengers[i] = getRandomPerson();
            }
            else{
                passengers[i] = getRandomAnimal();
            }
        }

        for(int i = 0; i < pedestrians.length; i++){
            int odds = r.nextInt(101);
            if (odds > 19){
                pedestrians[i] = getRandomPerson();
            }
            else{
                pedestrians[i] = getRandomAnimal();
            }
        }

        Scenario genScenario = new Scenario(passengers, pedestrians, isLegalCrossing);

        return genScenario;
    }

}

