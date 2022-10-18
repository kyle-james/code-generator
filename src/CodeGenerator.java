import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Properties;

public class CodeGenerator {

    // Creating path and file objects
    public static Path path_code_configuration = Paths.get("code_configuration.txt");
    public static File file_code_configuration = new File("code_configuration.txt");
    public static Path path_input = Paths.get("input.txt");
    public static File file_input = new File("input.txt");
    public static Path path_config = Paths.get("app.config");
    public static File file_config = new File("app.config");
    public static Path path_output = Paths.get("output.txt");
    public static File file_output = new File("output.txt");

    // Creating the frame for message/confirmation popups
    public static JFrame jframe = new JFrame();

    // Creates a 'config' object
    public static Properties config = new Properties();

    public static String saved_word = "";
    public static String variable_indicator = "";
    public static int count = 0;

    // Main class
    public static void main(String[] args) throws IOException {
        try {
            // Checks for the required files. If they aren't there, they will be generated. Empty files do not count. They must NOT exist.
            checkForAndGenerateFiles();

            // Sets the config object to whatever 'app.config' specifies
            loadConfig(config);

            // Gets the "trim_values" value from the config
            boolean trim_values = Boolean.parseBoolean(config.getProperty("trim_values"));

            // Sends a confirmation popup
            int result = initPopup(config, trim_values);

            // Reads and processes the required files for the program to operate
            readFiles(config, result);

            // Safely exits the application to end the process
            System.exit(0);
        }
        catch(Exception e) {
            // Prints the issue
            JOptionPane.showMessageDialog(jframe, e.toString());
        }
    }

    // Checks for and generates the files required for the program to require.
    private static void checkForAndGenerateFiles() {
        // Creating headers (Auto generated content for auto generated text and config files)
        String header_code_in =
            "#This code will be printed for each line in input.txt\n" +
            "#Use @1@ to echo the first value in each line, @2@ for the second value, etc.\n" +
            "#use @$@ for the 'saved keyword'.\n" +
            "#use @#@ for the count (Starting from 1).\n" +
            "[\n" +
            "    'name' => '@1@',\n" +
            "    'personal_id' => @#@,\n" +
            "    'date_of_birth' => '@2@',\n" +
            "    'talents' => '@3@',\n" +
            "    'wage' => '@4@',\n" +
            "    'category' => '@$@',\n" +
            "    'start_date' => '@5@',\n" +
            "    'went_to_post_secondary' => '@6@',\n" +
            "],";

        String header_lines_in =
            "#All Data was gathered via the 'random page' button on wikipedia. I clicked until.\n" +
            "#I stumbled on a page of a living person with enough info to fill in the example fields.\n" +
            "$People Famous Enough to Have a Wikipedia Page\n" +
            "Hannah Barbera,1957-07-07,Marketing/Animation,20.00,2022-07-20,null\n" +
            "Jeff Bezos,1964-01-12,Software/Business,5.02,2022-07-19,t\n" +
            "Jeff Goldblum,1952-10-22,Arts/Acting,20.80,2022-07-17,f\n" +
            "Dolly Parton,1946-01-19,Performing/Philanthropy,50.90,2022-07-02,false\n" +
            "Natasha Trethewey,1966-04-26,Writing/Poetry,20.00,2022-07-04,true\n" +
            "Rachita Ram,1992-10-03,Acting/Singing,15.50,2022-06-30,null\n" +
            "Justin Tredeau,1971-12-25,Politician/Scholar,3.00,2022-06-12,t\n" +
            "Liam McIntyre,1982-02-08,Actor/Media,55.30,2022-08-30,f\n" +
            "Steve Turner,1962-10-24,Trades/Unions,48.33,2022-03-03,f\n" +
            "René Haselbacher,1977-09-15,Racing/Bicycling,10.00,2022-04-20,null\n" +
            "Sabine Heitling,1987-07-02,Athletics/Racing,20.65,2022-07-30,null";

        String header_config =
            "#!! IF YOU HAVE ISSUES WITH THIS FILE, DELETE IT. A NEW ONE WILL BE GENERATED !!\n" +
            "#Start a line with # if you wish for it to not be processed.\n" +
            "#Start a line with $ if you wish to reference that on other lines.\n" +
            "#Best practise is to put them at the top.\n" +
            "#Please indicate your separator: (eg: ',', '/', '|' sans ''. Default: ,)\n" +
            "separator=,\n" +
            "#Please indicate your variable indicator. This is used to wrap variables in the code_configuration.txt file. (Default: @)\n" +
            "variable_indicator=@\n" +
            "#Please indicate the total amount of values per line. There should be one less separator (see above) than the value set here, per line. (Eg: Name, StreetAddress, City, State, Zip = 5. Default: 6)\n" +
            "total_values=6\n" +
            "#Please indicate where you want the counter to start at (Default: 1)\n" +
            "count_start=1\n" +
            "#Please indicate whether or not you want to trim the variables in input.txt (Default: false)\n" +
            "trim_values=false";

        // If 'app.config' exists, and isn't a directory
        if(!file_config.exists() && !file_config.isDirectory()) {
            // Try the following
            try {
                // Creates 'app.config' with the text of 'header_config' added to the top by default.
                Files.write(path_config, Collections.singleton(header_config), StandardCharsets.UTF_8);
                // Sends a popup stating 'app.config' was generated.
                JOptionPane.showMessageDialog(jframe, "Generated File: app.config");
            } 
            // If it fails, it throws an IO Exception
            catch (IOException e) {
                // Prints the issue
                JOptionPane.showMessageDialog(jframe, e.toString());
            }
        }

        // If 'code_configuration.txt' exists, and isn't a directory
        if(!file_code_configuration.exists() && !file_code_configuration.isDirectory()) {
            // Try the following
            try {
                // Creates 'code_configuration.txt' with the text of 'header_code_in' added to the top by default.
                Files.write(path_code_configuration, Collections.singleton(header_code_in), StandardCharsets.UTF_8);
                // Sends a popup stating 'code_configuration.txt' was generated.
                JOptionPane.showMessageDialog(jframe, "Generated File: code_configuration.txt");
            }
            // If it fails, it throws an IO Exception
            catch (IOException e) {
                // Prints the issue
                JOptionPane.showMessageDialog(jframe, e.toString());
            }
        }

        // If 'input.txt' exists, and isn't a directory
        if(!file_input.exists() && !file_input.isDirectory()) {
            // Try the following
            try {
                // Creates 'input.txt' with the text of 'header_lines_in' added to the top by default.
                Files.write(path_input, Collections.singleton(header_lines_in), StandardCharsets.UTF_8);
                // Sends a popup stating 'input.txt' was generated.
                JOptionPane.showMessageDialog(jframe, "Generated File: input.txt");
            } 
            // If it fails, it throws an IO Exception
            catch (IOException ex) {
                // Prints the issue
                JOptionPane.showMessageDialog(jframe, ex.toString());
            }
        }
    }

    // Loads the contents/values for 'app.config' into memory
    private static void loadConfig(Properties config) {
        // Try the following
        try {
            // Loads 'app.config' and set its values to the application
            config.load(new FileInputStream("app.config"));
        }
        // If it fails, it throws an IO Exception
        catch (IOException e) {
            // Prints the issue
            JOptionPane.showMessageDialog(jframe, e.toString());
        }
    }

    // Shows the popup that visually verifies (prints/displays) the configured settings, and confirms whether to run.
    private static int initPopup(Properties config, boolean trim_values) {
        String initMessage =
            "Hello, welcome to my code generator.\n" +
            "Are these settings accurate?\n" +
            "\t*Separator Symbol = " + config.getProperty("separator") + "\n" +
            "\t*Variable Indicator = " + config.getProperty("variable_indicator") + "\n" +
            "\t*Total Amount of Values per Line = " + config.getProperty("total_values") + "\n" +
            "\t*Count Start = " + config.getProperty("count_start") + "\n" +
            "\t*Trim Values? = " + trim_values + "\n";

        // Sends a confirmation popup. Is int value is determined by which button the user clicks.
        return JOptionPane.showConfirmDialog(jframe, initMessage);
    }

    // Reads the files required for the program to execute ('app.config', 'code_configuration.txt', 'input.txt')
    private static void readFiles(Properties config, int result) throws IOException {
        // String builder used to build the formatted code to output to both the console, and 'output.txt'
        StringBuilder full_generated_code = new StringBuilder();

        //assign configured values
        count = Integer.parseInt(config.getProperty("count_start"));
        boolean trim_values = Boolean.parseBoolean(config.getProperty("trim_values"));
        
        // If the confirmation popup option "yes" is clicked
        if(result == 0) {
            // For every line in 'input.txt'
            for (int i = 0; i < (Files.readAllLines(Paths.get("input.txt"))).size(); i++) {
                // If the line doesn't start with '#' or '$'
                if (!Files.readAllLines(Paths.get("input.txt")).get(i).startsWith("#") && !Files.readAllLines(Paths.get("input.txt")).get(i).startsWith("$")) {
                    // Gets current line from 'input.txt'
                    String temp_line = Files.readAllLines(Paths.get("input.txt")).get(i);

                    // Gets the 'total_values' value from the config. This is used to make processing the data significantly easier
                    int total_values = Integer.parseInt(config.getProperty("total_values"));

                    // Creates an array to store the position of the commas of each line. Its size is one less the value of 'total_values' in 'app.config'
                    int[] commas = new int[total_values];

                    // For each of the commas
                    for (int j = 0; j < commas.length; j++) {
                        //If this is the first comma
                        if (j == 0) {
                            // Gets the position of the first comma, and sets the first index in the array to that value
                            commas[0] = temp_line.indexOf(config.getProperty("separator"));
                        }
                        // If above is false, and the last comma
                        else if (j == commas.length - 1) {

                            // Gets the position of the last comma, and sets the last index in the array to that value
                            commas[j] = temp_line.lastIndexOf(config.getProperty("separator"));
                        }
                        else {
                            // Gets the position of the next comma, and sets the relevant index in the array to that
                            // value
                            commas[j] = temp_line.indexOf(config.getProperty("separator"), (commas[j - 1] + 1));
                        }
                    }

                    // Creates an array to store the value of each variable on each line.
                    String[] values = new String[total_values];

                    // If 'trim_values' in 'app.config' == true or t
                    if (trim_values) {
                        // For each value on every line
                        for (int j = 0; j < values.length; j++) {
                            // If this is the first iteration through the loop, set the value of values[0] to whatever text is between position 0, and the position of the first comma (commas[0])
                            if (j == 0) {
                                values[0] = temp_line.substring(0, commas[0]).trim();
                            }
                            // If the above is false, and this is the final iteration through the loop, set the value of values[j] to whatever text follows the position at commas[values.length -1], to the end of the string
                            else if (j == values.length - 1) {
                                values[j] = temp_line.substring(commas[j] + 1).trim();
                            }
                            // If it's not the fist iteration, and it's not the final iteration, set the value of values[j] to whatever text is between the positions of commas[j-1] + 1 and commas[j].
                            else {
                                values[j] = temp_line.substring(commas[j - 1] + 1, commas[j]).trim();
                            }
                            // If the value of this variable is 't', set it to 'true'
                            if (values[j].equals("t")) {
                                values[j] = "true";
                            }
                            // If the value of this variable is 'f', set it to 'false'
                            if (values[j].equals("f")) {
                                values[j] = "false";
                            }
                        }
                    }
                    // If 'trim_values' in 'app.config' == false or f
                    else {
                        // For every variable on each line, execute the following
                        for (int j = 0; j < values.length; j++) {
                            // If this is the first iteration through the loop, set the value of values[0] to whatever text is between position 0, and the position of the first comma (commas[0])
                            if (j == 0) {
                                values[0] = temp_line.substring(0, commas[0]);
                            }
                            // If the above is false, and this is the final iteration through the loop, set the value of values[j] to whatever text follows the position at commas[values.length -1], to the end of the string
                            else if (j == values.length - 1) {
                                values[j] = temp_line.substring(commas[j] + 1);
                            }
                            // If it's not the fist iteration, and it's not the final iteration, set the value of values[j] to whatever text is between the positions of commas[j-1] + 1 and commas[j].
                            else {
                                values[j] = temp_line.substring(commas[j - 1] + 1, commas[j]);
                            }
                            // If the value of this variable is 't', set it to 'true'
                            if (values[j].equals("t")) {
                                values[j] = "true";
                            }
                            // If the value of this variable is 'f', set it to 'false'
                            if (values[j].equals("f")) {
                                values[j] = "false";
                            }
                        }
                    }

                    int j_count = 0;
                    // For every line in 'code_configuration.txt'
                    for (int j = 0; j < (Files.readAllLines(Paths.get("code_configuration.txt"))).size(); j++) {
                        // If the line does not begin with '#' or '$'
                        if (!Files.readAllLines(Paths.get("code_configuration.txt")).get(j).startsWith("#") && !Files.readAllLines(Paths.get("code_configuration.txt")).get(j).startsWith("$")) {
                            // Creates a temporary object to use as reference. the value is set to the line number equivalent to j
                            String temp_code = Files.readAllLines(Paths.get("code_configuration.txt")).get(j);

                            // If the line contains a '#' sandwiched between two 'variable_indicator' symbols (Default: @), set from 'app.config' (Default: @#@)
                            if (temp_code.contains(config.getProperty("variable_indicator") + "#" + config.getProperty("variable_indicator"))) {
                                // Gets the position of the first variable indicator (Default: @)
                                int first_at = temp_code.indexOf(config.getProperty("variable_indicator"));
                                // Gets the position of the second variable indicator (Default: @)
                                int second_at = temp_code.indexOf(config.getProperty("variable_indicator"), first_at + 1);

                                // Creates a new string object to format our data how we want it
                                String new_code = temp_code;
                                // Sets the value of the string between the two variable indicators (Default: @) to count
                                new_code = new_code.substring(0, first_at) + count + new_code.substring(second_at + 1);
                                // Prints the line to console
                                System.out.println(new_code);
                                // Appends an '\n' (new line) to force a new line in the output file
                                full_generated_code.append(new_code).append("\n");
                            }
                            // If above is false and the line contains a '$' sandwiched between two 'variable_indicator'symbols (Default: @), set from 'app.config' (Default: @$@)
                            else if (temp_code.contains(config.getProperty("variable_indicator") + "$" + config.getProperty("variable_indicator"))) {
                                // Gets the position of the first variable indicator (Default: @)
                                int first_at = temp_code.indexOf(config.getProperty("variable_indicator"));
                                // Gets the position of the second variable indicator (Default: @)
                                int second_at = temp_code.indexOf(config.getProperty("variable_indicator"),first_at + 1);

                                // Creates a new string object to format our data how we want it
                                String new_code = temp_code;
                                // Sets the value of the string between the two variable indicators (Default: @) to
                                // saved_word
                                new_code = new_code.substring(0, first_at) + saved_word + new_code.substring(second_at + 1);
                                // Prints the line to console
                                System.out.println(new_code);
                                // Appends an '\n' (new line) to force a new line in the output file
                                full_generated_code.append(new_code).append("\n");
                            }
                            // If the above are false, and the line contains a variable indicator (Default: @)
                            else if (temp_code.contains(config.getProperty("variable_indicator"))) {
                                // Gets the position of the first variable indicator (Default: @)
                                int first_at = temp_code.indexOf(config.getProperty("variable_indicator"));
                                // Gets the position of the second variable indicator (Default: @)
                                int second_at = temp_code.indexOf(config.getProperty("variable_indicator"), first_at + 1);

                                // Creates a new string object to format our data how we want it
                                String new_code = temp_code;
                                // get the contents sandwiched between the two @@ from the line
                                new_code = new_code.substring(0, first_at) + values[j_count] + new_code.substring(second_at + 1);
                                // Prints a new line to the console
                                System.out.println(new_code);
                                // Adds one to j_count
                                j_count++;
                                // Appends a new line to the output file
                                full_generated_code.append(new_code).append("\n");
                            }
                            else {
                                // Prints the line to console
                                System.out.println(temp_code);
                                // Appends a new line to the output file
                                full_generated_code.append(temp_code).append("\n");
                            }
                        }
                    }
                    // Adds 1 to count
                    count++;
                }
                // If the above is false, and the line starts with '$'
                else if (Files.readAllLines(Paths.get("input.txt")).get(i).startsWith("$")) {
                    // Sets saved_word to the line (Default: $People Famous Enough to Have a Wikipedia Page), excluding the first character ($)
                    saved_word = Files.readAllLines(Paths.get("input.txt")).get(i).substring(1);
                }
            }
            // If 'output.txt' exists, and isn't a directory
            if (!file_output.exists() && !file_output.isDirectory()) {
                // Try the following
                try {
                    // Creates 'output.txt' with the text of 'full_generated_code' added to the top by default.
                    Files.write(path_output, Collections.singleton(full_generated_code.toString()), StandardCharsets.UTF_8);
                    // Sends a popup stating 'output.txt' was generated.
                    JOptionPane.showMessageDialog(jframe, "Generated File: 'output.txt'");
                }
                // If it fails, it throws an IO Exception
                catch (IOException e) {
                    // Prints the issue
                    JOptionPane.showMessageDialog(jframe, e.toString());
                }
            }
            else {
                // Creates 'output.txt' with the text of 'full_generated_code' added to the top by default.
                Files.write(path_output, Collections.singleton(full_generated_code.toString()), StandardCharsets.UTF_8);
                // Sends a popup stating the success of the application. prints the settings as a reminder.
                successPopup(config, trim_values);
            }
        }
    }

    // Sends a popup celebrating successful executions
    public static void successPopup(Properties config, boolean trim_values) {
        String initMessage =
            "Actions completed successfully!\n" +
            "For reference, this was your Saved word:\n" +
            "\t" + saved_word + "\n" +
            "This is the total count:\n" +
            "\t" + count + "\n" +
            "These are your selected settings:\n" +
            "\t*Separator Symbol = " + config.getProperty("separator") + "\n" +
            "\t*Variable Indicator = " + config.getProperty("variable_indicator") + "\n" +
            "\t*Total Amount of Values per Line = " + config.getProperty("total_values") + "\n" +
            "\t*Count Start = " + config.getProperty("count_start") + "\n" +
            "\t*Trim Values? = " + trim_values;

        // Sends popup verifying the process was successful,
        JOptionPane.showMessageDialog(jframe, initMessage);
    }
}