import ethicalengine.*;
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

    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);

        System.out.print("$ java Ethical Engine ");
        String cmd = keyboard.nextLine();
        List<String> command = new ArrayList<String>(Arrays.asList(cmd.split(" ")));


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
                if (!consentInput.equals("yes") && !consentInput.equals("no")) {
                    System.out.println("Invalid response. Do you consent to have your decisions " +
                            "saved to a file? (yes/no)");
                }
                else if (consentInput.equals("yes")) {
                    consent = true;
                    validIn = true;
                }
                else if (consentInput.equals("no")) {
                    validIn = true;
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
                            try{System.in.read();}
                            catch(Exception e){}

                        }
                    }
                }
            }

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


        interactiveAudit.runInteraction(decisions);

        // Display statistic
        System.out.println(interactiveAudit.toString());
    }

    public static void userLogUtil(boolean consent, List<String> command, Audit interactiveAudit){

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


    public static List<List<String>> configReader(List<String> command){
        int index;
        if(command.indexOf("-c") == -1){
            index = command.indexOf("--config");
        }
        else{
            index = command.indexOf("-c");
        }

        // Open and save config data.
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
        catch(IOException e){
        }

        // Deal with config data.
        // Delete first row with headers
        configData.remove(0);

        return configData;


    }



    public static List<Scenario> configInsertUtil(List<List<String>> configData){


        List<Scenario> configSce = new ArrayList<>();
        int csvIndex = 0;

        for(List<String> csvRow : configData){

            if(csvRow.get(0).substring(0, 3).equals("sce")){

                // Legality of crossing
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

                    // Person
                    else if(configData.get(csvIndex).get(0).substring(0,3).equals("per")) {
                        // Passenger
                        if (configData.get(csvIndex).get(9).substring(0, 3).equals(
                                "pas")) {
                            try {
                                configPasse.add(addPersonCSV(csvIndex, configData));
                            } catch (NumberFormatException e) {
                                System.out.println("WARNING: invalid number format in config " +
                                        "file in line " + csvIndex + 1);
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }

                        }
                        // Pedestrian
                        else if (configData.get(csvIndex).get(9).substring(0, 3).equals(
                                "ped")) {
                            try {
                                configPeds.add(addPersonCSV(csvIndex, configData));
                            } catch (NumberFormatException e) {
                                System.out.println("WARNING: invalid number format in config " +
                                        "file in line " + csvIndex + 1);
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }

                        }
                    }
                    // Animal
                    else if(configData.get(csvIndex).get(0).substring(0,3).equals("ani")){

                        // Animal passenger
                        if(configData.get(csvIndex).get(9).substring(0,3).equals(
                                "pas")){
                            try{
                                configPasse.add(addAnimalCSV(csvIndex, configData));
                            }
                            catch(NumberFormatException e){
                                System.out.println("WARNING: invalid number format in config " +
                                        "file in line " + csvIndex + 1);
                            }
                            catch(Exception e){
                                System.out.println(e.getMessage());
                            }
                        }
                        // Animal Pedestrian
                        else if(configData.get(csvIndex).get(9).substring(0,3).equals(
                                "ped")){

                            try{
                                configPeds.add(addAnimalCSV(csvIndex, configData));
                            }
                            catch(NumberFormatException e){
                                System.out.println("WARNING: invalid number format in config " +
                                        "file in line " + csvIndex);
                            }
                            catch(Exception e){
                                System.out.println(e.getMessage());
                            }
                        }
                    }

                    csvIndex += 1;

                }

                ethicalengine.Character[] pass =
                        configPasse.toArray(new ethicalengine.Character[configPasse.size()]);
                ethicalengine.Character[] peds =
                        configPeds.toArray(new ethicalengine.Character[configPeds.size()]);

                configSce.add(new Scenario(pass, peds, crossingLegality));
            }
        }
        return configSce;
    }




    public static Person addPersonCSV(int index, List<List<String>> configData) throws
            ethicalengine.InvalidDataFormatException, ethicalengine.InvalidCharacteristicException,
            NumberFormatException{

        int age = 0;


        // Check if data format is valid
        if(configData.get(index).size() != 10){
            throw new ethicalengine.InvalidDataFormatException(index+1);
        }
        age = Integer.parseInt(configData.get(index).get(2));
        if(!isInEnum(configData.get(index).get(4).toUpperCase(), Person.Profession.class) &&
                !configData.get(index).get(4).equals("")){
            throw new ethicalengine.InvalidCharacteristicException(index+2);
        }
        if(!isInEnum(configData.get(index).get(1).toUpperCase(), ethicalengine.Character.Gender.class)){
            throw new ethicalengine.InvalidCharacteristicException(index+2);
        }
        if(!isInEnum(configData.get(index).get(3).toUpperCase(), ethicalengine.Character.BodyType.class)){
            System.out.println(configData.get(index).get(3));
            throw new ethicalengine.InvalidCharacteristicException(index+2);
        }

        if(!configData.get(index).get(5).equals("TRUE") &&
                !configData.get(index).get(5).equals("FALSE")){
            throw new ethicalengine.InvalidCharacteristicException(index+2);
        }




        // Profession defining
        Person.Profession prof = Person.Profession.NONE;

        for(Person.Profession p : Person.Profession.values()) {
            if (p.name().equals(configData.get(index).get(4).toUpperCase())) {
                prof = p;
            }
        }

        ethicalengine.Character.BodyType bodyType =
                ethicalengine.Character.BodyType.valueOf(configData.get(index).get(3).toUpperCase());
        ethicalengine.Character.Gender gend =
                ethicalengine.Character.Gender.valueOf(configData.get(index).get(1).toUpperCase());


        boolean isPregnant = false;
        if(configData.get(index).get(5).equals("TRUE")){
            isPregnant = true;
        }
        boolean isYou = false;
        if(configData.get(index).get(6).equals("TRUE")){
            isYou = true;
        }

        Person pers = new Person(age, prof, gend, bodyType, isPregnant);
        pers.setIsYou(isYou);

        return pers;
    }

    public static Animal addAnimalCSV(int index, List<List<String>> configData) throws
            ethicalengine.InvalidDataFormatException, NumberFormatException,
            ethicalengine.InvalidCharacteristicException{

        // Check if data format is valid
        if(configData.get(index).size() != 10){
            throw new ethicalengine.InvalidDataFormatException(index+2);
        }

        if(configData.get(index).get(7).getClass() != String.class){
            throw new ethicalengine.InvalidCharacteristicException(index+2);
        }
        if(!isInEnum(configData.get(index).get(1).toUpperCase(), ethicalengine.Character.Gender.class)){
            throw new ethicalengine.InvalidCharacteristicException(index+2);
        }

        if(!configData.get(index).get(8).equals("TRUE") &&
                !configData.get(index).get(8).equals("FALSE")){
            throw new ethicalengine.InvalidCharacteristicException(index+2);
        }


        String species = configData.get(index).get(7);
        int age = Integer.parseInt(configData.get(index).get(2));
        ethicalengine.Character.Gender gend =
                ethicalengine.Character.Gender.valueOf(configData.get(index).get(1).toUpperCase());
        ethicalengine.Character.BodyType bodyType =
                ethicalengine.Character.BodyType.UNSPECIFIED;
        boolean isPet = false;
        if(configData.get(index).get(8).equals("TRUE")){
            isPet = true;
        }

        Animal animal = new Animal(species, age, gend, bodyType, isPet);
        return animal;

    }

    public static <E extends Enum<E>> boolean isInEnum(String value, Class<E> eClass) {
        for (E e : eClass.getEnumConstants()) {
            if(e.name().equals(value.toUpperCase())){
                return true;
            }
        }
        return false;
    }



    public enum Decision {
        PASSENGERS, PEDESTRIANS;

        public String toLowercase(){
            return name().toLowerCase();
        }
    }


    public static Decision decide(Scenario scenario){

        // Initialise weights for each side.
        int passWeight = 0;
        int pedWeight = 0;

        ethicalengine.Character[] passengers = scenario.getPassengers();
        ethicalengine.Character[] pedestrians = scenario.getPedestrians();
        boolean isLegalCrossing = scenario.isLegalCrossing();


        if (isLegalCrossing){
            pedWeight += pedestrians.length + 1;
        }
        else{
            pedWeight -= (pedestrians.length + 1) / 22;
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
                passWeight -= 1;
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
                pedWeight -= 1;
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
