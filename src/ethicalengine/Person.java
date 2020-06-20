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
        DOCTOR, CEO, CASHIER, POLICE, CRIMINAL, HOMELESS, UNEMPLOYED, UNKNOWN, NONE;

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
     * @param bodytype body type of the person.
     * @param isPregnant pregnancy status of the person.
     */
    public Person(int age, Profession profession, Gender gender, BodyType bodytype,
                  boolean isPregnant){
        super(age, gender, bodytype);
        setAgeCategory();
        setIsPregnant(isPregnant);
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
     * 68 < : Senior
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

    public Profession getProfession() {
        return this.profession;
    }

    public void setProfession(Profession profession) {
        if (this.getAgeCategory() == AgeCategory.ADULT){
            this.profession = profession;
        }
        else{
            this.profession = Profession.NONE;
        }
    }


    public boolean isPregnant(){
        return this.isPregnant;
    }

    /**
     * Method to set the pregnant status of a person.
     * For the sake of this simulation, only an adult female can be pregnant.
     * @param isPregnant Boolean status of pregnancy.
     */
    public void setIsPregnant(boolean isPregnant){
        if (this.getGender() == Gender.FEMALE && this.ageCategory == AgeCategory.ADULT){
            this.isPregnant = isPregnant;
        }
        else {
            this.isPregnant = false;
        }
    }

    public boolean isYou(){
        return this.isYou;
    }

    public void setIsYou(boolean isYou){
        this.isYou = isYou;
    }

    public String toString(){
        // isYou
        if (this.isYou()){
            if (getAgeCategory() == AgeCategory.ADULT){
                // isYou, Adult and pregnant: add profession
                if (isPregnant()){
                    return "you "+getBodyType().toLowercase()+" "+getAgeCategory().toLowercase()+
                            " "+getProfession().toLowercase()+" "+getGender().toLowercase()
                            +" pregnant";
                }
                // isYou, Adult and NOT pregnant: add profession
                else{
                    return "you "+getBodyType().toLowercase()+" "+getAgeCategory().toLowercase()+
                            " "+getProfession().toLowercase()+" "+getGender().toLowercase();
                }
            }
            // isYou, Non Adult
            else{
                // Pregnant non adult
                if (isPregnant()){
                    return "you "+getBodyType().toLowercase()+" "+getAgeCategory().toLowercase()
                            +" "+getGender().toLowercase()+" pregnant";
                }
                // Non-pregnant non adult
                else{
                    return "you "+getBodyType().toLowercase()+" "+getAgeCategory().toLowercase()
                            +" "+getGender().toLowercase();
                }
            }
        }
        // NOT you
        else{
            // NOT you, adult
            if(getAgeCategory() == AgeCategory.ADULT){
                // NOT you, adult and pregnant
                if(isPregnant()){
                    return getBodyType().toLowercase()+" "+getAgeCategory().toLowercase()
                            +" "+getProfession().toLowercase()+" "+getGender().toLowercase()
                            +" pregnant";
                }
                // NOT you, adult and not pregnant
                else{
                    return getBodyType().toLowercase()+" "+getAgeCategory().toLowercase()
                            +" "+getProfession().toLowercase()+" "+getGender().toLowercase();
                }
            }
            // NOT you, non adult
            else{
                // NOT you, non adult and pregnant
                if(isPregnant()){
                    return getBodyType().toLowercase()+" "+getAgeCategory().toLowercase()
                            +" "+getGender().toLowercase()+" pregnant";
                }
                // NOT you, non adult and not pregnant
                else{
                    return getBodyType().toLowercase()+" "+getAgeCategory().toLowercase()
                            +" "+getGender().toLowercase();
                }
            }
        }
    }

    public boolean isPet(){
        return false;
    }

    public String getSpecies(){
        return null;
    }
}
