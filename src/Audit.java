import ethicalengine.*;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
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
    private double totalPersons = 0;
    private double totalAge = 0;
    private double averageAge= 0;
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
     * runInteraction is a method that runs the audit when EthicalEngine is run in interactive mode.
     * @param decisions an array of decisions.
     */
    public void runInteraction(EthicalEngine.Decision[] decisions){
        for(int i = 0; i < sceArray.length; i++){
            runCount += 1;
            ethicalengine.Character[] passengers = sceArray[i].getPassengers();
            ethicalengine.Character[] pedestrians = sceArray[i].getPedestrians();
            legalityInsert(sceArray[i], data);


            if(decisions[i].equals(EthicalEngine.Decision.PASSENGERS)){
                runInsert(passengers, pedestrians);
            }
            if(decisions[i].equals(EthicalEngine.Decision.PEDESTRIANS)){
                runInsert(pedestrians, passengers);
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

            legalityInsert(sce, data);

            if(decision.equals(EthicalEngine.Decision.PASSENGERS)){
                runInsert(passengers, pedestrians);
            }
            if(decision.equals(EthicalEngine.Decision.PEDESTRIANS)){
                runInsert(pedestrians, passengers);
            }
        }
    }

    // Calculate survival ratios for characteristics
    // Output key and values to list and sort using comparator decendingly.
    // Moved age to a variable

    /**
     * survivalRatioCalc is a method that calculates the survival ratios for all characteristics.
     * The keys and values from data are output to a list and sorted ina descending order based
     * on the calculated survival ratio.
     */
    private void survivalRatioCalc(){
        String[] bodyType = getEnum(ethicalengine.Character.BodyType.class);
        String[] professions = getEnum(Person.Profession.class);
        String[] genders = getEnum(ethicalengine.Character.Gender.class);
        String[] ageCat = getEnum(Person.AgeCategory.class);

        // Average age
        averageAge = totalAge/totalPersons;


        for(String key : data.keySet()){
            // Legality of crossing
            if(key.equals("red") || key.equals("green")){
                calcData.put(key, data.get(key)/ runCount);

            }
            // Animal Characteristics
            if(speciesList.contains(key)){
                survivorCalcUtil(key);
            }
            if(key.equals("pet")){
                survivorCalcUtil(key);
            }
            // Person Characteristics
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
                // ignore unspecified body type
                if(!ethicalengine.Character.BodyType.valueOf(key.toUpperCase()).equals
                        (ethicalengine.Character.BodyType.UNSPECIFIED)){
                    survivorCalcUtil(key);
                }
            }
            if(Arrays.asList(professions).contains(key.toUpperCase())){
                // Ignore None and Unknown professions
                if(!Person.Profession.valueOf(key.toUpperCase()).equals
                        (Person.Profession.UNKNOWN)){
                    survivorCalcUtil(key);
                }
            }
            if(Arrays.asList(genders).contains(key.toUpperCase())){
                // Ignore Unknown gender
                if(!ethicalengine.Character.Gender.valueOf(key.toUpperCase()).equals(ethicalengine.Character.Gender.UNKNOWN)){
                    survivorCalcUtil(key);
                }
            }
        }
    }


    private void survivorCalcUtil(String key){
        if(survivorData.get(key) == null){
            calcData.put(key, 0.0);
        }
        else{
            calcData.put(key, survivorData.get(key)/data.get(key));
        }
    }


    private static String[] getEnum(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }


    private void runInsert(ethicalengine.Character[] winner, ethicalengine.Character[] loser){
        for(ethicalengine.Character c : winner){
            if(c instanceof Person){
                totalPersons += 1;

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
        for(ethicalengine.Character p : loser){
            if(p instanceof Person){
                totalPersons += 1;
                // Age tally
                ageTally(p);
                personKeyInsert(p, data);
            }
            if(p instanceof Animal){
                animalInsertKey(p, data);
            }
        }
    }


    public void legalityInsert(Scenario sce, Hashtable<String, Double> hTable){
        if(sce.isLegalCrossing()){
            if(hTable.containsKey("green")){
                hTable.put("green", hTable.get("green") + 1);
            }
            else{
                hTable.put("green", 1.0);
            }
        }
        else{
            if(hTable.containsKey("red")){
                hTable.put("red", hTable.get("red") + 1.0);
            }
            else{
                hTable.put("red", 1.0);
            }
        }
    }


    public void personKeyInsert(ethicalengine.Character c, Hashtable<String, Double> hTable){


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



    public void ageTally(ethicalengine.Character c){
        // Age tally
        System.out.println(c.getAge());
        totalAge += c.getAge();
    }

    public void animalInsertKey(ethicalengine.Character c, Hashtable<String, Double> hTable){
        // pet status tally
        if(c.isPet()){
            tallyKeyInsert("pet", hTable);
        }
        // Species tally

        speciesList.add(c.getSpecies());
        tallyKeyInsert(c.getSpecies(), hTable);
    }

    public void tallyKeyInsert(String key, Hashtable<String, Double> hTable){
        if(hTable.containsKey(key)){
            hTable.put(key, hTable.get(key)+1.0);
        }
        else{
            hTable.put(key,1.0);
        }
    }

    public List<Map.Entry<String, Double>> sortedOut(){
        List<Map.Entry<String, Double>> out = new ArrayList<>(calcData.entrySet());


        Collections.sort(out, (Map.Entry<String, Double> entry1,
                               Map.Entry<String, Double> entry2) ->
                entry2.getValue().compareTo(entry1.getValue()));
        return out;
    }

    public void setAuditType(String name){
        this.auditType = name;
    }

    public String getAuditType(){
        return this.auditType;
    }

    public String toString(){
        if (runCount == 0){
            return "no audit available\n";
        }
        else{
            StringBuilder sb = new StringBuilder();

            sb.append("======================================\n" +
                    "# " + auditType + " audit\n" +
                    "======================================\n" +
                    "- % SAVED AFTER " + runCount + " RUNS");


            List<Map.Entry<String, Double>> dataList = sortedOut();

            for(Map.Entry<String, Double> item : dataList){

                if(item.getValue() != 0){
                    sb.append("\n" + item.getKey() + ": " + String.format("%.2f", item.getValue()));
                }

            }

            sb.append("\n--");

            sb.append("\naverage age: " + String.format("%.1f", averageAge) + "\n");

            return sb.toString();
        }
    }

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
            catch (Exception e){
            }
        }
    }





}
