import ethicalengine.*;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Audit is a class that holds audit segment of the Moral Machine program.
 * The audit class takes a number of scenarios and utilises the decide() method in EthicalEngine
 * to decide the outcome of the scenario. Then summarises the results for each characteristic as
 * a "statistic of projected survival".
 *
 * @author Benjamin Tam
 * @author email: ytam2@student.unimelb.edu.au
 * @author studentID: 889835
 */

public class Audit {
    private String auditType = "Unspecified";
    private Scenario[] sceArray;
    private Hashtable<String, Double> data = new Hashtable<String, Double>();
    private Hashtable<String, Double> survivorData = new Hashtable<String, Double>();
    private Hashtable<String, Double> calcData = new Hashtable<String, Double>();
    private ArrayList<String> speciesList = new ArrayList<>();
    private double totalPersonSurvivors = 0;
    private double totalAgeSurvivors = 0.0;
    private double averageAge= 0.0;
    private int runCount = 0;


    /**
     * Empty Constructor for Audit object.
     */
    public Audit(){
    }
    /**
     * Constructor for Audit object that takes an array of Scenarios.
     * @param scenarios array of scenarios.
     */
    public Audit(Scenario[] scenarios){
        sceArray = scenarios;
    }


    /**
     * changeScenarios is a method that changes the scenario array with a an array of scenarios
     * that is provided to the method.
     * @param newSces new scenario array.
     */
    public void changeScenarios(Scenario[] newSces){
        sceArray = newSces;
    }

    /**
     * run is a method that runs the audit when EthicalEngine is run when not in interactive mode.
     */
    public void run(){
        runUtil();
        survivalRatioCalc();
    }

    /**
     * run is a method that runs the audit on runs number of random scenarios.
     * @param runs number of runs
     */
    public void run(int runs){

        // Build Scenarios
        sceArray = new Scenario[runs];
        ScenarioGenerator randomSceGen = new ScenarioGenerator();
        for(int i = 0; i < sceArray.length; i++){
            sceArray[i] = randomSceGen.generate();
        }

        // Conduct Audit
        runUtil();
        survivalRatioCalc();
    }

    /**
     * runInteraction is a method that runs the audit when EthicalEngine is run in interactive mode.
     * @param decisions an array of decisions.
     */
    public void runInteraction(EthicalEngine.Decision[] decisions){
        for(int i = 0; i < sceArray.length; i++){
            runCount += 1;
            ethicalengine.Character[] passengers = sceArray[i].getPassengers();
            ethicalengine.Character[] pedestrians = sceArray[i].getPedestrians();

            if(decisions[i].equals(EthicalEngine.Decision.PASSENGERS)){
                runInsert(passengers, pedestrians, sceArray[i].isLegalCrossing());
            }
            if(decisions[i].equals(EthicalEngine.Decision.PEDESTRIANS)){
                runInsert(pedestrians, passengers, sceArray[i].isLegalCrossing());
            }
        }
        survivalRatioCalc();
    }


    /**
     * runUtil is a method that aids the run method in tallying the run count and tallying up the
     * data needed to calculate the audit calculations.
     */
    private void runUtil(){
        for(Scenario sce : sceArray){
            runCount += 1;
            EthicalEngine.Decision decision = EthicalEngine.decide(sce);
            ethicalengine.Character[] passengers = sce.getPassengers();
            ethicalengine.Character[] pedestrians = sce.getPedestrians();

            if(decision.equals(EthicalEngine.Decision.PASSENGERS)){
                runInsert(passengers, pedestrians, sce.isLegalCrossing());
            }
            if(decision.equals(EthicalEngine.Decision.PEDESTRIANS)){
                runInsert(pedestrians, passengers, sce.isLegalCrossing());
            }
        }
    }

    /**
     * survivalRatioCalc is a method that calculates the survival ratios for all characteristics.
     * The keys and values from data are output to a list.
     */
    private void survivalRatioCalc(){
        String[] bodyType = getEnum(ethicalengine.Character.BodyType.class);
        String[] professions = getEnum(Person.Profession.class);
        String[] genders = getEnum(ethicalengine.Character.Gender.class);
        String[] ageCat = getEnum(Person.AgeCategory.class);

        // Average age calculation
        averageAge = totalAgeSurvivors/totalPersonSurvivors;


        // Legality of crossing tally
        survivorCalcUtil("green");
        survivorCalcUtil("red");

        for(String key : data.keySet()){

            // Animal Characteristics
            if(key.equals("animal")){
                survivorCalcUtil(key);
            }
            if(speciesList.contains(key)){
                survivorCalcUtil(key);
            }
            if(key.equals("pet")){
                survivorCalcUtil(key);
            }

            // Person Characteristics
            if(key.equals("person")){
                survivorCalcUtil(key);
            }
            if(key.equals("you")){
                survivorCalcUtil(key);
            }
            if(key.equals("pregnant")){
                survivorCalcUtil(key);
            }
            if(Arrays.asList(ageCat).contains(key.toUpperCase())){
                survivorCalcUtil(key);
            }
            if(Arrays.asList(bodyType).contains(key.toUpperCase())){
                // Ignore unspecified body type
                if(!ethicalengine.Character.BodyType.valueOf(key.toUpperCase()).equals
                        (ethicalengine.Character.BodyType.UNSPECIFIED)){
                    survivorCalcUtil(key);
                }
            }
            if(Arrays.asList(professions).contains(key.toUpperCase())){
                    survivorCalcUtil(key);
            }
            if(Arrays.asList(genders).contains(key.toUpperCase())){
                // Ignore Unknown gender
                if(!ethicalengine.Character.Gender.valueOf(key.toUpperCase()).equals
                        (ethicalengine.Character.Gender.UNKNOWN)){
                    survivorCalcUtil(key);
                }
            }
        }
    }


    /**
     * survivorCalcUtil is a helper method that assists survivorCalc in the calculation of the
     * survival rate for each characteristic by dividing the survivors characteristics by the total
     * number tallied for both survivors and those that did not survive.
     * If the the number of survivors that have the characteristic is 0, the result is 0.
     * @param key value to be calculated.
     */
    private void survivorCalcUtil(String key){
        if(survivorData.get(key) == null){
            calcData.put(key, 0.0);
        }
        else{
            calcData.put(key, survivorData.get(key)/data.get(key));
        }
    }


    /**
     * getEnum is a helper method that returns the enumerated values of an enum as a string array.
     * @param e an enum class.
     * @return string array containing the enum values of the enum class as strings.
     */
    private static String[] getEnum(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }


    /**
     * runInsert is a helper method that inserts the tally of each characteristics of each
     * character into the data hash table.
     * The method also tallies the characteristics of the survivors(winners) into the
     * survivorData hash table.
     * @param winner character array containing the survivors for the scenario.
     * @param loser character array containing the non-survivors for the scenario.
     * @param legality boolean legality status
     */
    private void runInsert(ethicalengine.Character[] winner, ethicalengine.Character[] loser,
                           boolean legality){

        // Add characteristics for each winner
        for(ethicalengine.Character c : winner){

            // Add red and green tally
            legalityInsert(legality, survivorData);
            legalityInsert(legality, data);

            if(c instanceof Person){
                totalPersonSurvivors += 1;

                // Age tally
                ageTally(c);
                personKeyInsert(c, data);
                personKeyInsert(c, survivorData);
            }
            if(c instanceof Animal){
                animalInsertKey(c, data);
                animalInsertKey(c, survivorData);
            }
        }

        // Add characteristics for each loser
        for(ethicalengine.Character p : loser){

            legalityInsert(legality, data);

            if(p instanceof Person){
                personKeyInsert(p, data);
            }
            if(p instanceof Animal){
                animalInsertKey(p, data);
            }
        }
    }

    /**
     * legalityInsert is a helper method that tallies the legality of the road crossing in the
     * a hashtable (data).
     * @param legality boolean representing legality of crossing
     * @param hTable data hashtable.
     */
    private void legalityInsert(boolean legality, Hashtable<String, Double> hTable){

        if(legality){
            tallyKeyInsert("green", hTable);
        }
        else{
            tallyKeyInsert("red", hTable);
        }
    }

    /**
     * personKeyInsert is a helper method that aids in the tallying up of characteristics of a
     * Person in both data and survivorData hashtables.
     * @param c person object that is being tallied.
     * @param hTable data or survivorData hashtable.
     */
    private void personKeyInsert(ethicalengine.Character c, Hashtable<String, Double> hTable){

        tallyKeyInsert("person", hTable);
        // You tally
        if(c.isYou()){
            tallyKeyInsert("you", hTable);
        }
        // Pregnant tally
        if(c.isPregnant()){
            tallyKeyInsert("pregnant", hTable);
        }
        // Gender tally
        tallyKeyInsert(c.getGender().toLowercase(), hTable);
        // Age category tally
        tallyKeyInsert(c.getAgeCategory().toLowercase(), hTable);
        // Body type tally
        tallyKeyInsert(c.getBodyType().toLowercase(), hTable);
        // Profession tally
        if(c.getAgeCategory().equals(Person.AgeCategory.ADULT)){
            tallyKeyInsert(c.getProfession().toLowercase(), hTable);
        }
    }


    /**
     * ageTally is a helper method that aids in tallying up the ages of the people in the scenarios.
     * @param c person object.
     */
    private void ageTally(ethicalengine.Character c){
        totalAgeSurvivors += c.getAge();
    }

    /**
     * animalInsertKey is a helper method that aids in tallying up the characteristics of an
     * animal object in the data and survivorData hash table.
     * @param c animal object.
     * @param hTable data or survivorData hash table.
     */
    private void animalInsertKey(ethicalengine.Character c, Hashtable<String, Double> hTable){

        tallyKeyInsert("animal", hTable);
        // Pet status tally
        if(c.isPet()){
            tallyKeyInsert("pet", hTable);
        }
        // Species tally
        speciesList.add(c.getSpecies());
        tallyKeyInsert(c.getSpecies(), hTable);
    }

    /**
     * tallyKeyInsert is a helper method that aids in tallying up the number of characteristics
     * (keys) for each character object in the animalInsertKey or personInsertKey methods.
     * @param key characteristic key as a string.
     * @param hTable data or survivorData hash table.
     */
    private void tallyKeyInsert(String key, Hashtable<String, Double> hTable){
        if(hTable.containsKey(key)){
            hTable.put(key, hTable.get(key)+1.0);
        }
        else{
            hTable.put(key,1.0);
        }
    }

    /**
     * sortedOut is a helper method that sorts a nested List in descending order based on the
     * second value, which is the survival ratio when used in the toString method.
     * @return sorted List in descending order based on second value in nested list.
     */
    private List<Map.Entry<String, Double>> sortedOut(){
        List<Map.Entry<String, Double>> out = new ArrayList<>(calcData.entrySet());

        // Sort by values in descending order
        Collections.sort(out, (Map.Entry<String, Double> entry1,
                               Map.Entry<String, Double> entry2) ->
                entry2.getValue().compareTo(entry1.getValue()));
        return out;
    }

    /**
     * setAuditType is a method that sets the type of audit that is being conducted.
     * @param name string value of audit type.
     */
    public void setAuditType(String name){
        this.auditType = name;
    }

    /**
     * getAuditType is a method that gets the audit type.
     * @return audit type.
     */
    public String getAuditType(){
        return this.auditType;
    }

    /**
     * toString is a method that outputs the audit results with the audit details: audit type,
     * run count, characteristic survival ratio in descending order and average age.
     * @return audit results.
     */
    public String toString(){

        DecimalFormat format = new DecimalFormat("##0.0");
        format.setRoundingMode(RoundingMode.DOWN);

        if (runCount == 0){
            return "no audit available\n";
        }
        else{
            StringBuilder sb = new StringBuilder();

            sb.append("======================================\n" +
                    "# " + auditType + " Audit\n" +
                    "======================================\n" +
                    "- % SAVED AFTER " + runCount + " RUNS");


            List<Map.Entry<String, Double>> dataList = sortedOut();

            for(Map.Entry<String, Double> item : dataList){
                String val = format.format(item.getValue());
                sb.append("\n" + item.getKey() + ": " + val);
            }

            sb.append("\n--");
            sb.append("\naverage age: " + format.format(averageAge));
            return sb.toString();
        }
    }

    /**
     * printToFile is a method that saves/ appends the audit results from toString in a file.
     * If a filepath is provided after -r or --results and it exists, the output is appended to
     * the file.
     * If the filepath's directory does not exist, an error is printed.
     * @param filepath filepath where log is saved/
     */
    public void printToFile(String filepath){


        File file = new File(filepath);


        if(file.getParentFile() != null){
            System.out.println("ERROR: could not print results. Target directory does not exist.");
        }
        else{
            try{
                BufferedWriter bw = new BufferedWriter(new FileWriter(filepath, true));
                bw.write(this.toString());
                bw.flush();
            }
            catch (IOException e){
            }
        }
    }
}
