package ethicalengine;
import java.util.Random;

/**
 * ScenarioGenerator is a class that generates a number of scenarios based on a minimum or
 * maximum number of characters for passengers and pedestrians (with a default of min:1 and max:5)
 * and populates the scenarios with characters with random characteristics.
 *
 * @author Benjamin Tam
 * @author email: ytam2@student.unimelb.edu.au
 * @author studentID: 889835
 */

public class ScenarioGenerator {

    private Random r;
    private int passengerCountMinimum = 1;
    private int passengerCountMaximum = 5;
    private int pedestrianCountMinimum = 1;
    private int pedestrianCountMaximum = 5;


    /**
     * Constructor for the ScenarioGenerator that initialises a Random object with a random seed.
     */
    public ScenarioGenerator(){
        this.r = new Random();
    }

    /**
     * Constructor for the ScenarioGenerator that initialises a Random object with a set seed.
     * @param seed
     */
    public ScenarioGenerator(long seed){
        this.r = new Random(seed);
    }

    /**
     * Constructor for ScenarioGenerator that initialises the object with a set seed for the
     * Random object and set values for passenger and pedestrian minimums and maximums.
     * @param seed seed number for Random object.
     * @param passengerCountMinimum minimum number of passengers.
     * @param passengerCountMaximum maximum number of passengers.
     * @param pedestrianCountMinimum minumum number of pedestrians.
     * @param pedestrianCountMaximum maximum number of pedestrians.
     */
    public ScenarioGenerator(long seed, int passengerCountMinimum, int passengerCountMaximum,
                             int pedestrianCountMinimum, int pedestrianCountMaximum){
        this.r = new Random(seed);
        this.passengerCountMinimum = passengerCountMinimum;
        this.passengerCountMaximum = passengerCountMaximum;
        this.pedestrianCountMinimum = pedestrianCountMinimum;
        this. pedestrianCountMaximum = pedestrianCountMaximum;
    }


    /**
     * setPassengerCountMinimum is a method that sets the minimum number of passengers in the
     * scenario.
     * @param passengerCountMinimum minimum number of passengers.
     */
    public void setPassengerCountMinimum(int passengerCountMinimum) {
        if(passengerCountMinimum < this.passengerCountMaximum){
            this.passengerCountMinimum = passengerCountMinimum;
        }
    }

    /**
     * setPassengerCountMaximum is a method that sets the maximum number of passengers in the
     * scenario.
     * @param passengerCountMaximum maximum number of passengers.
     */
    public void setPassengerCountMaximum(int passengerCountMaximum) {
        this.passengerCountMaximum = passengerCountMaximum;
    }

    /**
     * setPedestrianCountMinimum is a method that sets the minimum number of pedestrians in the
     * scenario.
     * @param pedestrianCountMinimum minumum number of pedestrians.
     */
    public void setPedestrianCountMinimum(int pedestrianCountMinimum) {
        if(pedestrianCountMinimum < this.passengerCountMaximum){
            this.pedestrianCountMinimum = pedestrianCountMinimum;
        }
    }

    /**
     * setPedestrianCountmaximum is a method that sets the maximum number of pedestrians in the
     * scenario.
     * @param pedestrianCountMaximum maximum number of pedestrians.
     */
    public void setPedestrianCountmaximum(int pedestrianCountMaximum) {
        this.pedestrianCountMaximum = pedestrianCountMaximum;
    }


    /**
     * getRandomPerson is a method that creates a Person object with random variables.
     * @return Person object with random variables.
     */
    public Person getRandomPerson(){
        int randBodyType = r.nextInt(Character.BodyType.values().length);
        int randAge = r.nextInt(118); // Oldest person alive is 117 years old
        int randProf = r.nextInt(Person.Profession.values().length - 1); // Ignore NONE enum
        int randGend = r.nextInt(Character.Gender.values().length);
        boolean randPreg = r.nextBoolean();

        Person p = new Person(randAge, Person.Profession.values()[randProf],
                Character.Gender.values()[randGend], Character.BodyType.values()[randBodyType],
                randPreg);

        return p;
    }

    /**
     * getRandomAnimal is a method that creates an Animal object with random variables.
     * @return Animal object with random variables.
     */
    public Animal getRandomAnimal(){
        String[] species = new String[] {"cat", "dog", "bird", "hamster", "rabbit"};
        int randGend = r.nextInt(Character.Gender.values().length-1);
        int randAge = r.nextInt(121); //Oldest pet alive is 120 years old, Tommy the Tortoise
        int randBodyType = r.nextInt(Character.BodyType.values().length);
        int randSpecies = r.nextInt(species.length);
        boolean randIsPet = r.nextBoolean();

        Animal a = new Animal(species[randSpecies], randAge, Character.Gender.values()[randGend],
                Character.BodyType.values()[randBodyType], randIsPet);
        return a;
    }

    /**
     * generate is a method that generates scenarios with passenger and pedestrian numbers based on
     * the bounds set by the user or using the defaults.
     * The scenarios are filled with randomly generated animals or people.
     *
     * @return scenario with randomly generated animals or people.
     */
    public Scenario generate(){
        boolean isLegalCrossing = r.nextBoolean();
        Character[] passengers = new Character[r.nextInt(passengerCountMaximum
                - passengerCountMinimum) + passengerCountMinimum];
        Character[] pedestrians = new Character[r.nextInt(pedestrianCountMaximum
                - pedestrianCountMinimum) + pedestrianCountMinimum];


        // More likely to have People around on a street instead of animals:
        // Humans 80% chance, Animals 20% chance.
        // Possible to only have animals in car as its a self driving car.
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

