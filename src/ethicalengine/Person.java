package ethicalengine;

/**
 * Child class of character that defines a person in the simulation.
 * @author Benjamin Tam
 * @author email: ytam2@student.unimelb.edu.au
 * @author studentID: 889835
 */
public class Person extends Character {
    private Person.AgeCategory ageCategory;
    private boolean isYou = false;
    private boolean isPregnant = false;
    private Person.Profession profession = Profession.NONE;

    /**
     * Enumerated values of professions that Person objects in the simulation can hold.
     * Default value: NONE.
     */
    public enum Profession {
        DOCTOR, CEO, CASHIER, POLICE, CRIMINAL, HOMELESS, UNEMPLOYED, STUDENT,
        PROFESSOR, UNKNOWN, NONE;

        public String toLowercase(){
            return name().toLowerCase();
        }
    }

    /**
     * Enumerated values of age categories that a Person object can have.
     * Value will depend on the age of the Person.
     */
    public enum AgeCategory {
        BABY, CHILD, ADULT, SENIOR;

        public String toLowercase(){
            return name().toLowerCase();
        }
    }

    /**
     * Constructor for a Person object.
     * @param age age of the person.
     * @param profession profession of the person.
     * @param gender gender of the person.
     * @param bodyType body type of the person.
     * @param isPregnant pregnancy status of the person.
     */
    public Person(int age, Profession profession, Gender gender, BodyType bodyType,
                  boolean isPregnant){
        super(age, gender, bodyType);
        setAgeCategory();
        setAsPregnant(isPregnant);
        setProfession(profession);

    }

    /**
     * Copy constructor for a person object.
     * @param otherPerson other person object.
     */
    public Person(Person otherPerson){
        this.profession = otherPerson.getProfession();
        this.isPregnant = otherPerson.isPregnant();
        this.ageCategory = otherPerson.getAgeCategory();
        this.isYou = otherPerson.isYou;
    }

    /**
     * getAgeCategory is a method that returns the age category of the person.
     * @return age category of the person.
     */
    public AgeCategory getAgeCategory(){
        return this.ageCategory;
    }

    /**
     * setAgeCategory sets the age category of the person based on the person's age.
     * 0-4 : Baby
     * 5-16 : Child
     * 17-68 : Adult
     * Older than 68 : Senior
     */
    private void setAgeCategory() {
        int age = this.getAge();

        if (age >= 0 && age <= 4){
            this.ageCategory = AgeCategory.BABY;
        }
        if (age >= 5 && age <= 16){
            this.ageCategory = AgeCategory.CHILD;
        }
        if (age >= 17 && age <= 68){
            this.ageCategory = AgeCategory.ADULT;
        }
        if (age > 68){
            this.ageCategory = AgeCategory.SENIOR;
        }
    }

    /**
     * getProfession is a method that returns the profession of a Person
     * @return Enumerated value of the Person's profession.
     */
    public Profession getProfession() {
        return this.profession;
    }

    /**
     * setProfession is a method that sets the profession of a person if they are an adult.
     * If this method is used on a person that is not an adult, the profession will be set as NONE.
     * @param profession enumerated value of a profession.
     */
    private void setProfession(Profession profession) {
        if (this.getAgeCategory() == AgeCategory.ADULT){
            this.profession = profession;
        }
        else{
            this.profession = Profession.NONE;
        }
    }

    /**
     * isPregnant is a method that returns a boolean representing the pregnancy status of a person.
     * @return boolean representing the pregenancy status of a person
     */
    public boolean isPregnant(){
        return this.isPregnant;
    }

    /**
     * setAsPregnant is a method to set the pregnant status of a person.
     * Only adult females can be pregnant.
     * @param isPregnant Boolean status of pregnancy.
     */
    public void setAsPregnant(boolean isPregnant){
        if (this.getGender() == Gender.FEMALE && this.ageCategory == AgeCategory.ADULT){
            this.isPregnant = isPregnant;
        }
        else {
            this.isPregnant = false;
        }
    }

    /**
     * isYou is a method that returns a boolean to see if the person is the user.
     * @return boolean to see if the person is the user.
     */
    public boolean isYou(){
        return this.isYou;
    }

    /**
     * setAsYou is a method that sets the isYou status of the person.
     * @param isYou boolean to see if the person is the user.
     */
    public void setAsYou(boolean isYou){
        this.isYou = isYou;
    }

    /**
     * toString is a method that returns a string with information about the person.
     * @return string of information about the person.
     */
    public String toString(){
        // if the Person isYou
        if (this.isYou()){
            if (getAgeCategory() == AgeCategory.ADULT){
                // returns: isYou, Body Type, Adult, Profession, Gender and is Pregnant
                if (isPregnant()){
                    return "you "+getBodyType().toLowercase()+" "+getAgeCategory().toLowercase()+
                            " "+getProfession().toLowercase()+" "+getGender().toLowercase()
                            +" pregnant";
                }
                // returns if not pregnant: isYou, Body Type, Adult, Profession, Gender
                else{
                    return "you "+getBodyType().toLowercase()+" "+getAgeCategory().toLowercase()+
                            " "+getProfession().toLowercase()+" "+getGender().toLowercase();
                }
            }
            // Person isYou and Not Adult
            else{
                return "you "+getBodyType().toLowercase()+" "+getAgeCategory().toLowercase()
                        +" "+getGender().toLowercase();
            }
        }
        // Person is NOT you.
        else{
            // Person is NOT you and adult
            if(getAgeCategory() == AgeCategory.ADULT){
                // returns: Body Type, Adult, Profession, Gender and is Pregnant.
                if(isPregnant()){
                    return getBodyType().toLowercase()+" "+getAgeCategory().toLowercase()
                            +" "+getProfession().toLowercase()+" "+getGender().toLowercase()
                            +" pregnant";
                }
                // returns if not pregnant: Body Type, Adult, Profession, Gender
                else{
                    return getBodyType().toLowercase()+" "+getAgeCategory().toLowercase()
                            +" "+getProfession().toLowercase()+" "+getGender().toLowercase();
                }
            }
            // Person is NOT you & Not Adult
            else{
                // returns if not pregnant: Body Type, Age Category, Gender
                return getBodyType().toLowercase()+" "+getAgeCategory().toLowercase()
                        +" "+getGender().toLowercase();

            }
        }
    }

    /**
     * isPet method returns boolean representing if person is pet, which is false as a person can
     * not be a pet.
     * @return false pet status
     */
    public boolean isPet(){
        return false;
    }

    /**
     * getSpecies method returns the species of the person, but a person does not have a species,
     * hence, returns null.
     * @return null species
     */
    public String getSpecies(){
        return null;
    }
}
