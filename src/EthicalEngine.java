import ethicalengine.*;
import ethicalengine.Character;
import java.io.*;
import java.lang.NumberFormatException;
import java.util.*;

/**
 * EthicalEngine is a class that holds the Moral Machine decider and the main program when
 * the Moral Machine program is run.
 * @author Benjamin Tam
 * @author email: ytam2@student.unimelb.edu.au
 * @author studentID: 889835
 */
public class EthicalEngine {

    /**
     * The main method of EthicalEngine that when run, prompts the user for flags.
     * -h or --help : prints out instructions for the user.
     * -i or --interactive : interactive mode for the user.
     * -c or --config (filepath) : allows the user to set a filepath for a config csv file.
     * -r or --results (filepath) : allows user to set a filepath for saving audit results.
     * @param args -
     */
    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        List<String> command = new ArrayList<>(Arrays.asList(args));

        // Help message
        String help = String.format("EthicalEngine - COMP90041 - Final Project\n\n" +
                        "Usage: java EthicalEngine [arguments]\n\n" +
                        "Arguments:\n" +
                        "%17s"+"%36s" +
                        "%15s"+"%43s" +
                        "%18s"+"%40s" +
                        "%22s"+"%37s", "-c or --config", "Optional: path to config file\n",
                "-h or --help", "Print Help (this message) and exit\n", "-r or --results",
                "Optional: path to results log file\n", "-i or --interactive",
                "Optional: launches interactive mode\n");


        // -h or --help
        if(command.contains("-h") || command.contains("--help")){
            System.out.println(help);
            System.exit(0);
        }


        // -i or --interactive mode
        else if(command.contains("-i") || command.contains("--interactive")) {
            pathValidityCheck(command, help);
            // read ascii
            try {
                BufferedReader welcome = new BufferedReader(new FileReader("welcome.ascii"));
                String line;
                while ((line = welcome.readLine()) != null) {
                    System.out.println(line);
                }
                welcome.close();
            } catch (IOException e) {
            }

            boolean consent = false;

            boolean validIn = false;
            while (!validIn) {
                System.out.println("Do you consent to have your decisions saved to a file? " +
                        "(yes/no)");
                String consentInput = keyboard.nextLine();

                try{
                    if (!consentInput.equals("yes") && !consentInput.equals("no")) {
                        throw new ethicalengine.InvalidInputException();
                    }
                    else if (consentInput.equals("yes")) {
                        consent = true;
                        validIn = true;
                    }
                }
                catch (ethicalengine.InvalidInputException e){
                    System.out.println(e.getMessage());
                }
            }


            // import config file if -c exists

            Scenario[] userScenario;


            if (command.contains("-c") || command.contains("--config")) {

                // convert CSV to nested list
                List<List<String>> configData = configReader(command);

                // Build scenarios
                List<Scenario> configSce = configInsertUtil(configData);


                // if user has configs, present 3 scenarios at a time, store info and repeat

                List<List<Scenario>> userScenarioArr = new ArrayList<>();

                int chunk = 3; // chunk size to divide
                for (int i = 0; i < configSce.size(); i += chunk) {
                    userScenarioArr.add(configSce.subList(i, Math.min(configSce.size(), i + chunk)));
                }

                // -i -c config.csv
                boolean continueStatus = true;
                Audit interactiveAudit = new Audit();
                interactiveAudit.setAuditType("User");

                for (int i=0; i<userScenarioArr.size(); i++){
                    if(continueStatus){
                        userScenario =
                                userScenarioArr.get(i).toArray(new Scenario[userScenarioArr.get(i).size()]);

                        // Replace scenarios
                        interactiveAudit.changeScenarios(userScenario);
                        userJudgement(userScenario, keyboard, interactiveAudit);
                        userLogUtil(consent, command, interactiveAudit);


                        // check if user would like to continue if there are still scenarios to
                        // judge.
                        if(i < userScenarioArr.size()-1){
                            System.out.println("Would you like to continue? (yes/no)");
                            String inp = keyboard.nextLine();

                            if(inp.equals("no")){
                                System.exit(0);
                            }
                        }
                        else if(i == userScenarioArr.size()-1){
                            // Try to get it to exit on any press
                            System.out.println("That's all. Press Enter to quit.");
                            try{
                                System.in.read();
                                System.exit(0);
                            }
                            catch(Exception e){}

                        }
                    }
                }
            }

            // Without config file
            else{
                boolean continueStatus = true;
                Audit interactiveAudit = new Audit();
                interactiveAudit.setAuditType("User");

                while(continueStatus){

                    ScenarioGenerator sceGen = new ScenarioGenerator();

                    // Insert random scenarios.
                    userScenario = new Scenario[3];
                    for(int i=0; i<userScenario.length; i++){
                        userScenario[i] = sceGen.generate();
                    }
                    interactiveAudit.changeScenarios(userScenario);
                    userJudgement(userScenario, keyboard, interactiveAudit);
                    userLogUtil(consent, command, interactiveAudit);


                    System.out.println("Would you like to continue? (yes/no)");
                    String inp = keyboard.nextLine();


                    if(inp.equals("no")){
                        System.exit(0);
                    }
                }
            }
        }



        // -c or --config (without interactive mode)
        else if(command.contains("-c") || command.contains("--config")){
            pathValidityCheck(command, help);

            // convert CSV to nested list
            List<List<String>> configData = configReader(command);

            // Build scenarios
            List<Scenario> configSce = configInsertUtil(configData);


            Scenario[] auditConfigSce = configSce.toArray(new Scenario[configSce.size()]);

            Audit configAudit = new Audit(auditConfigSce);
            configAudit.run();

            // Display to console
            System.out.println(configAudit.toString());

            // Save to file
            userLogUtil(true, command, configAudit);


        }



        // No -i, -c  or -h flags, run random scenarios. Save in filepath if -r provided
        else {
            Random r = new Random();
            pathValidityCheck(command, help);

            // Generate between 1 to 10 random scenarios
            int numScenarios = r.nextInt(10)+1;


            // Build scenarios

            Scenario[] randomSceArr = new Scenario[numScenarios];
            ScenarioGenerator randomSceGen = new ScenarioGenerator();

            for(int i = 0; i < numScenarios; i++){
                randomSceArr[i] = randomSceGen.generate();
            }

            Audit randomSceAudit = new Audit(randomSceArr);
            randomSceAudit.run();

            // Display to console
            System.out.println(randomSceAudit.toString());

            // Save to file
            userLogUtil(true, command, randomSceAudit);
        }

    }

    /**
     * pathValidityCheck is a method that checks if a file path set after -r, -results, -c or
     * --config are actual valid paths. If not, then the help message is printed and the program
     * is terminated.
     * @param command the command that the user input separated by spaces as an arraylist.
     * @param help help message.
     */
    public static void pathValidityCheck(List<String> command, String help) {

        List<String> validCommands = new ArrayList<>(Arrays.asList("-h", "--help", "-c",
                "--config", "-i", "--interactive", "-r", "--results"));

        if (command.contains("-r") || command.contains("--results") || command.contains("-c") ||
                command.contains("--config")) {

            int index = 0;
            if (command.indexOf("-r") != -1) {
                index = command.indexOf("-r");
            }
            else if(command.indexOf("--results") != -1){
                index = command.indexOf("-results");
            }
            else if(command.indexOf("-c") != -1){
                index = command.indexOf("-c");
            }
            else if(command.indexOf("--config") != -1){
                index = command.indexOf("--config");
            }
            try{
                if (validCommands.contains(command.get(index + 1))){
                    System.out.println(help);
                    System.exit(0);
                }
            }
            catch (IndexOutOfBoundsException e){
                System.out.println(help);
                System.exit(0);
            }

        }
    }

    /**
     * userJudgement is a method that prompts the user for their choice in whether to save the
     * passengers or the pedestrians in the scenarios given.
     * The choices are then taken into account and an audit is then done on the users choices and
     * the scenarios and finally the statistics of the audit are displayed to the user.
     * @param userScenario scenarios given to the user for them to decide the outcome of.
     * @param keyboard Scanner object.
     * @param interactiveAudit the
     */
    public static void userJudgement(Scenario[] userScenario, Scanner keyboard,
                                     Audit interactiveAudit){

        Decision[] decisions = new Decision[userScenario.length];

        for(int i = 0; i < userScenario.length; i++){
            System.out.println(userScenario[i].toString());
            System.out.println("Who should be saved? (passenger(s) [1] or pedestrian(s) [2])");

            String in = keyboard.nextLine();

            if(in.equals("passenger")|| in.equals("passengers") || in.equals("1")){
                decisions[i] = Decision.PASSENGERS;
            }
            else{
                decisions[i] = Decision.PEDESTRIANS;
            }
        }
        // Audit is run on the decisions made by the user and the scenarios given.
        interactiveAudit.runInteraction(decisions);

        // Statistic is displayed to the user.
        System.out.println(interactiveAudit.toString());
    }

    /**
     * userLogUtil is a method that saves the audit results that have been run in a file
     * depending on whether the user has consented to it.
     * The results are saved in a file path given by the user if provided or are saved in the
     * home directory if the user has not provided the file.
     * @param consent boolean status of whether consent has been provided by the user to save the
     *               results.
     * @param command list containing the command input by the user.
     * @param interactiveAudit the Audit object currently being used in this interactive situation.
     */
    private static void userLogUtil(boolean consent, List<String> command, Audit interactiveAudit){

        // If user consents to save, save results to path provided.
        if(consent){
            if(command.contains("-r") || command.contains("--results")){
                int index;
                if(command.indexOf("-r") == -1){
                    index = command.indexOf("--results");
                }
                else{
                    index = command.indexOf("-r");
                }
                // Print results to path provided
                interactiveAudit.printToFile(command.get(index+1));
            }

            // Else save to home directory
            // if mode is interactive, save to user.log
            // else, save to results.log
            else{
                if(command.contains("-i") || (command.contains("--interactive"))){
                    interactiveAudit.printToFile("user.log");
                }
                else{
                    interactiveAudit.printToFile("results.log");
                }

            }
        }
    }

    /**
     * configReader is a method that reads the csv provided by the user via a filepath if a
     * config is provided, then parses the csv and saves the data into a nested list.
     * The first item in the list is then removed as it only contains the headers.
     * @param command command input by the user.
     * @return nested list with parsed data.
     */
    private static List<List<String>> configReader(List<String> command){
        int index;
        if(command.indexOf("-c") == -1){
            index = command.indexOf("--config");
        }
        else{
            index = command.indexOf("-c");
        }

        // Open and save config data in nested list
        File configFile = new File(command.get(index+1));
        List<List<String>> configData = new ArrayList<>();

        try {
            BufferedReader csv = new BufferedReader(new FileReader(configFile));
            String line;
            while ((line = csv.readLine()) != null) {
                String[] val = line.split(",");
                configData.add(Arrays.asList(val));
            }
            csv.close();
        }
        catch(FileNotFoundException e){
            System.out.println("ERROR: could not find config file.");
            System.exit(0);
        }
        catch(IOException e){
        }

        // Delete first row with headers
        configData.remove(0);
        return configData;
    }


    /**
     * configInsertUtil is a method that takes the configData nested list containing data parsed
     * from the config csv and processes it and converts it into a list of scenarios.
     * @param configData List containing data from the config file.
     * @return List of scenarios processed from the config file.
     */
    private static List<Scenario> configInsertUtil(List<List<String>> configData){

        List<Scenario> configSce = new ArrayList<>();
        int csvIndex = 0;

        for(List<String> csvRow : configData){

            if(csvRow.get(0).substring(0, 3).equals("sce")){

                // Processing legality of crossing for each scenario
                boolean crossingLegality = false;
                if (configData.get(csvIndex).get(0).substring(9, 10).equals("g")) {
                    crossingLegality = true;
                }

                // Initialise list for passengers and pedestrians
                List<ethicalengine.Character> configPasse = new ArrayList<>();
                List<ethicalengine.Character> configPeds = new ArrayList<>();

                csvIndex += 1;
                while(csvIndex < configData.size()){
                    if(configData.get(csvIndex).get(0).substring(0, 3).equals("sce")){
                        break;
                    }
                    try{
                        // Check if data format is valid (10 items in row), if invalid, throw
                        // invalid data format exception.
                        if(configData.get(csvIndex).size() != 10){
                            throw new ethicalengine.InvalidDataFormatException(csvIndex+2);
                        }

                        // Processes character and adds the character into a character arraylist
                        // depending on whether the character is a pedestrian or passenger

                        // Processing Person
                        else if(configData.get(csvIndex).get(0).substring(0,3).equals("per")) {
                            // Passenger
                            if (configData.get(csvIndex).get(9).substring(0, 3).equals(
                                    "pas")) {
                                configPasse.add(addPersonCSV(csvIndex, configData));
                            }
                            // Pedestrian
                            else if (configData.get(csvIndex).get(9).substring(0, 3).equals(
                                    "ped")) {
                                configPeds.add(addPersonCSV(csvIndex, configData));
                            }
                        }
                        // Processing Animal
                        else if(configData.get(csvIndex).get(0).substring(0,3).equals("ani")){

                            // Animal passenger
                            if(configData.get(csvIndex).get(9).substring(0,3).equals(
                                    "pas")){

                                configPasse.add(addAnimalCSV(csvIndex, configData));
                            }
                            // Animal Pedestrian
                            else if(configData.get(csvIndex).get(9).substring(0,3).equals(
                                    "ped")){
                                configPeds.add(addAnimalCSV(csvIndex, configData));
                            }
                        }
                    }
                    catch(ethicalengine.InvalidDataFormatException e){
                        System.out.println(e.getMessage());
                    }
                    finally{
                        csvIndex += 1;
                    }
                }

                // Convert passenger and pedestrian arraylists to arrays
                ethicalengine.Character[] pass =
                        configPasse.toArray(new ethicalengine.Character[configPasse.size()]);
                ethicalengine.Character[] peds =
                        configPeds.toArray(new ethicalengine.Character[configPeds.size()]);

                // Add scenario generated from config file to the scenario array
                configSce.add(new Scenario(pass, peds, crossingLegality));
            }
        }
        return configSce;
    }


    /**
     * addPersonCSV is a helper method that processes the characteristics of a person and returns
     * the new person object when the method is called.
     * @param index the line index where the person's characteristics are stored in the list.
     * @param configData nested list containing all the character's data processed and parsed
     *                   from the CSV file.
     * @return person object.
     */
    private static Person addPersonCSV(int index, List<List<String>> configData){


        // Set defaults for new person
        int age = 0;
        Person.Profession prof = Person.Profession.NONE;
        ethicalengine.Character.BodyType bodyType = Character.BodyType.UNSPECIFIED;
        ethicalengine.Character.Gender gend = Character.Gender.UNKNOWN;
        boolean isPregnant = false;
        boolean isYou = false;

        try{
            // Parse age; throws number format exception if age is not able to be parsed to an
            // integer
            age = Integer.parseInt(configData.get(index).get(2));

            // Parse Profession
            if(!isInEnum(configData.get(index).get(4).toUpperCase(), Person.Profession.class) &&
                    !configData.get(index).get(4).equals("")){
                throw new ethicalengine.InvalidCharacteristicException(index+2);
            }
            else{
                for(Person.Profession p : Person.Profession.values()) {
                    if (p.name().equals(configData.get(index).get(4).toUpperCase())) {
                        prof = p;
                    }
                }
            }

            // Parse Gender
            if(!isInEnum(configData.get(index).get(1).toUpperCase(), ethicalengine.Character.Gender.class)){
                throw new ethicalengine.InvalidCharacteristicException(index+2);
            }
            else{
                gend = ethicalengine.Character.Gender.valueOf(configData.get(index).get(1).toUpperCase());
            }

            // Parse Body Type
            if(!isInEnum(configData.get(index).get(3).toUpperCase(), ethicalengine.Character.BodyType.class)){
                throw new ethicalengine.InvalidCharacteristicException(index+2);
            }
            else{
                bodyType = ethicalengine.Character.BodyType.valueOf(configData.get(index).get(3).toUpperCase());
            }

            // Parse Pregnancy status
            if(!configData.get(index).get(5).toUpperCase().equals("TRUE") &&
                    !configData.get(index).get(5).toUpperCase().equals("FALSE")){
                throw new ethicalengine.InvalidCharacteristicException(index+2);
            }
            else{
                if(configData.get(index).get(5).toUpperCase().equals("TRUE")){
                    isPregnant = true;
                }
            }

            // Parse isYou status
            if(!configData.get(index).get(6).toUpperCase().equals("TRUE") &&
                    !configData.get(index).get(6).toUpperCase().equals("FALSE")){
                throw new ethicalengine.InvalidCharacteristicException(index+2);
            }
            else{
                if(configData.get(index).get(6).toUpperCase().equals("TRUE")){
                    isYou = true;
                }
            }

        }
        catch(NumberFormatException e){
            System.out.println("WARNING: invalid number format in config " +
                    "file in line " + index + 2);
        }catch(ethicalengine.InvalidCharacteristicException e){
            System.out.println(e.getMessage());
        }


        // Create new person object from parsed data.
        Person pers = new Person(age, prof, gend, bodyType, isPregnant);
        pers.setAsYou(isYou);
        return pers;
    }

    /**
     * addAnimalCSV is a helper method that processes the characteristics of an animal and returns
     * the new animal object when the method is called.
     * @param index the line index where the animal's characteristics are stored in the list.
     * @param configData nested list containing all the character's data processed and parsed
     *                   from the CSV file.
     * @return animal object.
     */
    private static Animal addAnimalCSV(int index, List<List<String>> configData) {

        // Set default values for new animal object.
        int age = 0;
        String species = "DEFAULT";
        ethicalengine.Character.Gender gend = Character.Gender.UNKNOWN;
        ethicalengine.Character.BodyType bodyType = Character.BodyType.UNSPECIFIED;
        boolean isPet = false;


        try{

            // Parse age
            age = Integer.parseInt(configData.get(index).get(2));

            // Parse Species
            if(configData.get(index).get(7).getClass() != String.class){
                throw new ethicalengine.InvalidCharacteristicException(index+2);
            }
            else{
                species = configData.get(index).get(7);
            }

            // Parse Gender
            if(!isInEnum(configData.get(index).get(1).toUpperCase(), ethicalengine.Character.Gender.class)){
                throw new ethicalengine.InvalidCharacteristicException(index+2);
            }
            else{
                gend =
                        ethicalengine.Character.Gender.valueOf(configData.get(index).get(1).toUpperCase());
            }

            // Parse isPet
            if(!configData.get(index).get(8).toUpperCase().equals("TRUE") &&
                    !configData.get(index).get(8).toUpperCase().equals("FALSE")){
                throw new ethicalengine.InvalidCharacteristicException(index+2);
            }
            else{
                if(configData.get(index).get(8).toUpperCase().equals("TRUE")){
                    isPet = true;
                }
            }

        }
        catch(NumberFormatException e){
            System.out.println("WARNING: invalid number format in config " +
                    "file in line " + index + 2);
        }
        catch(ethicalengine.InvalidCharacteristicException e){
            System.out.println(e.getMessage());
        }

        // Create new animal object
        Animal animal = new Animal(species, age, gend, bodyType, isPet);
        return animal;

    }

    /**
     * isInEnum is a helper method that returns a boolean to represent if a string value is a
     * value in the enum class.
     * @param value string value to check if it is in the enum class.
     * @param eClass the Enum class.
     * @param <E> Enum
     * @return boolean whether the value is in the enum.
     */
    private static <E extends Enum<E>> boolean isInEnum(String value, Class<E> eClass) {
        for (E e : eClass.getEnumConstants()) {
            if(e.name().equals(value.toUpperCase())){
                return true;
            }
        }
        return false;
    }


    /**
     * Enumerated values of decisions in this simulation.
     */
    public enum Decision {
        PASSENGERS, PEDESTRIANS;

        public String toLowercase(){
            return name().toLowerCase();
        }
    }


    /**
     * decide is a method that takes in data from the passengers and pedestrians and adds or
     * removes weights based on various factors and outputs a decision on which party will survive.
     * @param scenario scenario provided that the model has to decide on.
     * @return returns a decision, either PASSENGERS or PEDESTRIANS.
     */
    public static Decision decide(Scenario scenario){

        // Initialise weights for each side.
        int passWeight = 0;
        int pedWeight = 0;

        ethicalengine.Character[] passengers = scenario.getPassengers();
        ethicalengine.Character[] pedestrians = scenario.getPedestrians();
        boolean isLegalCrossing = scenario.isLegalCrossing();


        // If road crossing is legal, +1 weight for each pedestrian
        // else, a penalty of 0.5 weight will be given to each pedestrian.
        if (isLegalCrossing){
            pedWeight += pedestrians.length;
        }
        else{
            pedWeight -= (pedestrians.length * 0.5);
        }

        // Add weights for each characteristic needed for passengers
        for (int i = 0; i < passengers.length; i++){
            if(passengers[i].getBodyType() == ethicalengine.Character.BodyType.AVERAGE
                    || passengers[i].getBodyType() == ethicalengine.Character.BodyType.ATHLETIC){
                passWeight += 2;
            }

            // Female & Pregnant
            if(passengers[i].getGender() == ethicalengine.Character.Gender.FEMALE
                    && passengers[i].isPregnant()){
                passWeight += 3;
            }

            // isYou
            if(passengers[i].isYou()){
                passWeight += 1;
            }

            // Preference to babies or children
            if(passengers[i].getAgeCategory() == Person.AgeCategory.BABY ||
                    passengers[i].getAgeCategory() == Person.AgeCategory.CHILD){
                passWeight += 2;
            }

            // Preference to Doctors, Negative preference to criminals
            if(passengers[i].getProfession() == Person.Profession.DOCTOR){
                passWeight += 1;
            }
            if(passengers[i].getProfession() == Person.Profession.CRIMINAL){
                passWeight -= 2;
            }

            // Preference if animal is pet
            if(passengers[i].isPet()){
                passWeight += 1;
            }
        }

        for (int i = 0; i < pedestrians.length; i++){

            if(pedestrians[i].getBodyType() == ethicalengine.Character.BodyType.AVERAGE
                    || pedestrians[i].getBodyType() == ethicalengine.Character.BodyType.ATHLETIC){
                pedWeight += 2;
            }
            // Female & Pregnant
            if(pedestrians[i].getGender() == ethicalengine.Character.Gender.FEMALE
                    && pedestrians[i].isPregnant()){
                pedWeight += 3;
            }
            // isYou
            if(pedestrians[i].isYou()){
                pedWeight += 1;
            }

            // Preference to babies or children
            if(pedestrians[i].getAgeCategory() == Person.AgeCategory.BABY ||
                    pedestrians[i].getAgeCategory() == Person.AgeCategory.CHILD){
                passWeight += 2;
            }

            // Preference to Doctors, Negative preference to criminals
            if(pedestrians[i].getProfession() == Person.Profession.DOCTOR){
                pedWeight += 1;
            }
            if(pedestrians[i].getProfession() == Person.Profession.CRIMINAL){
                pedWeight -= 2;
            }

            // Preference if animal is a pet
            if(pedestrians[i].isPet()){
                pedWeight += 1;
            }
        }

        if(passWeight == pedWeight){
            Random r = new Random();
            boolean randBool = r.nextBoolean();
            if (randBool){
                return Decision.PASSENGERS;
            }
            else{
                return Decision.PEDESTRIANS;
            }
        }
        if(passWeight > pedWeight){
            return Decision.PASSENGERS;
        }
        else{
            return Decision.PEDESTRIANS;
        }

    }

}
