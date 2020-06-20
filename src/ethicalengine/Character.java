package ethicalengine;


/**
 * Character is an abstract class that defines all characters (people or animals) in the Moral
 * Machine Scenario.
 * @author Benjamin Tam
 * @author email: ytam2@student.unimelb.edu.au
 * @author studentID: 889835
 */
public abstract class Character {
    private int age = 0;
    private Gender gender = Gender.UNKNOWN;
    private BodyType bodyType = BodyType.UNSPECIFIED;


    public enum Gender {
        MALE, FEMALE, UNKNOWN;

        public String toLowercase(){
            return name().toLowerCase();
        }
    }
    public enum BodyType {
        AVERAGE, ATHLETIC, OVERWEIGHT, UNSPECIFIED;

        public String toLowercase(){
            return name().toLowerCase();
        }

    }

    /**
     * Empty class constructor for Characters.
     */
    public Character(){
    }

    /**
     * Class constructor that takes several parameters and creates a character class with the
     * parameters.
     * @param age Age of the character. A character can only be >= than 0.
     * @param gender Gender of the character.
     * @param bodyType Body type of the character.
     */
    public Character(int age, Gender gender, BodyType bodyType){
        if (age >= 0){
            this.age = age;
        }

        this.gender = gender;
        this.bodyType = bodyType;
    }

    /**
     * Copy constructor for the Character class.
     * @param c Other character object.
     */
    public Character(Character c){
        age = c.age;
        gender = c.gender;
        bodyType = c.bodyType;
    }

    /**
     * A method that returns the age of the character.
     * @return An integer that is the age of the character.
     */
    public int getAge() {
        return this.age;
    }

    /**
     * A method that sets the age of the character using an int parameter.
     * @param age Age of the character.
     */
    protected void setAge(int age) {
        if (age>= 0){
            this.age = age;
        }
    }

    /**
     * A method that returns the gender of the character.
     * @return Gender of the character.
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * A method that sets the gender of the character.
     * @param gender Gender of the character.
     */
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    /**
     * A method that returns the body type of the character.
     * @return Body type of the character.
     */
    public BodyType getBodyType() {
        return bodyType;
    }

    /**
     * A method that sets the body type of the character.
     * @param bodyType Body type of the character.
     */
    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    public abstract boolean isPregnant();

    public abstract boolean isYou();

    public abstract Person.AgeCategory getAgeCategory();

    public abstract Person.Profession getProfession();

    public abstract boolean isPet();

    public abstract String getSpecies();
}


