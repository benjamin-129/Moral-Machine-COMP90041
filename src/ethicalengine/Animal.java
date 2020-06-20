package ethicalengine;

/**
 * Child class of character that defines all animals.
 * @author Benjamin Tam
 * @author email: ytam2@student.unimelb.edu.au
 * @author studentID: 889835
 */
public class Animal extends Character {
    private String species;
    boolean isPet = false;

    /**
     * Constructor for the animal class that takes a species parameter and creates an animal object.
     * @param species The species of the animal.
     */
    public Animal(String species){
        setSpecies(species);
    }

    /**
     * Second constructor for the animal class that takes a variety of parameters.
     * @param species The species of the animal.
     * @param age The age of the animal.
     * @param gender The gender of the animal.
     * @param bodyType The bodytype of the animal.
     * @param isPet Whether the animal is a pet.
     */
    public Animal(String species, int age, Gender gender, BodyType bodyType, boolean isPet){
        setSpecies(species);
        this.setAge(age);
        this.setGender(gender);
        this.setBodyType(bodyType);
        this.setPet(isPet);
    }

    /**
     * Copy constructor for the animal class.
     * @param otherAnimal
     */
    public Animal(Animal otherAnimal){
        this.species = otherAnimal.getSpecies();
        this.isPet = otherAnimal.isPet();
    }

    /**
     * A method that returns the species of the animal.
     * @return Species of the animal.
     */
    public String getSpecies() {
        return species;
    }

    /**
     * A method that sets the species of the animal.
     * @param species Species of the animal.
     */
    protected void setSpecies(String species) {
        this.species = species;
    }

    /**
     * A method that sets the pet status of the animal.
     * @param isPet Boolean status of the animals pet status.
     */
    public void setPet(boolean isPet) {
        this.isPet = isPet;
    }

    /**
     * A method that returns the pet status of the animal.
     * @return Boolean status of the pet status of the animal.
     */
    public boolean isPet(){
        return this.isPet;
    }

    /**
     * A method that returns a string output of the animal with it's pet status.
     * @return String output of information of the animal.
     */
    public String toString(){
        if(isPet()){
            return getSpecies()+" is pet";
        }
        else{
            return getSpecies();
        }
    }

    @Override
    public boolean isPregnant() {
        return false;
    }
    @Override
    public boolean isYou() {
        return false;
    }
    @Override
    public Person.AgeCategory getAgeCategory() {
        return null;
    }

    public Person.Profession getProfession(){
        return Person.Profession.NONE;
    }
}