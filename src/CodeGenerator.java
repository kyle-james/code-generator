import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Properties;

public class CodeGenerator {
    public static void main(String[] args) throws IOException{

        // Creates paths and files.
        Path path_code_in = Paths.get("code_in.txt");
        File file_code_in = new File("code_in.txt");

        Path path_lines_in = Paths.get("lines_in.txt");
        File file_lines_in = new File("lines_in.txt");

        Path path_config = Paths.get("app.config");
        File file_config = new File("app.config");

        Path path_code_out = Paths.get("code_out.txt");
        File file_code_out = new File("code_out.txt");

        // Creating headers (Auto generated content for auto generated text files)
        String header_code_in =
                "#This code will be printed for each line in lines_in.txt\n" +
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
                "],";

        String header_lines_in =
                "#All Data was gathered via the 'random page' button on wikipedia. I clicked until.\n" +
                "#I stumbled on a page of a living person with enough info to fill in the example fields.\n" +
                "$People Famous Enough to Have a Wikipedia Page\n" +
                "Hannah Barbera,1957-07-07,Marketing/Animation,20.00,2022-07-20\n" +
                "Jeff Bezos,1964-01-12,Software/Business,5.02,2022-07-19\n" +
                "Jeff Goldblum,1952-10-22,Arts/Acting,20.80,2022-07-17\n" +
                "Dolly Parton,1946-01-19,Performing/Philanthropy,50.90,2022-07-02\n" +
                "Natasha Trethewey,1966-04-26,Writing/Poetry,20.00,2022-07-04\n" +
                "Rachita Ram,1992-10-03,Acting/Singing,15.50,2022-06-30\n" +
                "Justin Tredeau,1971-12-25,Politician/Scholar,3.00,2022-06-12\n" +
                "Liam McIntyre,1982-02-08,Actor/Media,55.30,2022-08-30\n" +
                "Steve Turner,1962-10-24,Trades/Unions,48.33,2022-03-03\n" +
                "René Haselbacher,1977-09-15,Racing/Bicycling,10.00,2022-04-20\n" +
                "Sabine Heitling,1987-07-02,Athletics/Racing,20.65,2022-07-30";

        String header_config =
                "#!! IF YOU HAVE ISSUES WITH THIS FILE, DELETE IT. A NEW ONE WILL BE GENERATED !!\n" +
                "#Start a line with # if you wish for it to not be processed.\n" +
                "#Start a line with $ if you wish to reference that on other lines.\n" +
                "#   Best practise is to put them at the top.\n" +
                "#Please indicate your separator: (eg: ',', '/', '|' sans ''. Default: ,)\n" +
                "separator=,\n" +
                "variable_indicator=@\n" +
                "#Please indicate the total amount of values per line. There should be one less separator (see above) than the value set here, per line. (Eg: Name, StreetAddress, City, State, Zip = 5. Default: 13)\n" +
                "total_values=5\n" +
                "booleans=[1,2,3,4]";

        // Generate files if they don't exist.
        if(!file_code_in.exists() && !file_code_in.isDirectory()) {
            try {
                Files.write(path_code_in, Collections.singleton(header_code_in), StandardCharsets.UTF_8);
                System.out.println("Created file: code_in.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(!file_lines_in.exists() && !file_lines_in.isDirectory()) {
            try {
                Files.write(path_lines_in, Collections.singleton(header_lines_in), StandardCharsets.UTF_8);
                System.out.println("Created file: lines_in.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(!file_config.exists() && !file_config.isDirectory()) {
            try {
                Files.write(path_config, Collections.singleton(header_config), StandardCharsets.UTF_8);
                System.out.println("Created file: app.config");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Load config.

        Properties config = new Properties();
        try {
            config.load(new FileInputStream("app.config"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        String saved_word = "";
        int count = 1;


        StringBuilder full_generated_code = new StringBuilder();

        for(int i = 0; i < (Files.readAllLines(Paths.get("lines_in.txt"))).size(); i++) {
            if(!Files.readAllLines(Paths.get("lines_in.txt")).get(i).startsWith("#") && !Files.readAllLines(Paths.get("lines_in.txt")).get(i).startsWith("$")) {

                String temp_line = Files.readAllLines(Paths.get("lines_in.txt")).get(i);

                int total_values = Integer.parseInt(config.getProperty("total_values"));

                // GET COMMAS
                int[] commas = new int[total_values];

                for(int j = 0; j < commas.length ; j++) {
                    if(j == 0) {
                        commas[j] = temp_line.indexOf(config.getProperty("separator"));
                    }
                    else if(j == commas.length - 1) {
                        commas[j] = temp_line.lastIndexOf(config.getProperty("separator"));
                    }
                    else {
                        commas[j] = temp_line.indexOf(config.getProperty("separator"), (commas[j-1]+1));
                    }
                }

                // GET VALUES
                String[] values = new String[total_values];

                for(int j = 0; j < values.length; j++) {
                    if(j == 0) {
                        values[0] = temp_line.substring(0, commas[0]);
                    }
                    else if(j == values.length - 1){
                        values[j] = temp_line.substring(commas[j]+1);
                    }
                    else {
                        values[j] = temp_line.substring(commas[j-1]+1, commas[j]);
                    }
                    if(values[j].equals("t")) {
                        values[j] = "true";
                    }
                    if(values[j].equals("f")) {
                        values[j] = "false";
                    }
                }

                int j_count = 0;
                for(int j = 0; j < (Files.readAllLines(Paths.get("code_in.txt"))).size(); j++) {
                    if(!Files.readAllLines(Paths.get("code_in.txt")).get(j).startsWith("#") && !Files.readAllLines(Paths.get("code_in.txt")).get(j).startsWith("$")) {
                        String temp_code = Files.readAllLines(Paths.get("code_in.txt")).get(j);

                        if(temp_code.contains(config.getProperty("variable_indicator") + "#" + config.getProperty("variable_indicator"))) {
                            int first_at = temp_code.indexOf(config.getProperty("variable_indicator"));
                            int second_at = temp_code.indexOf(config.getProperty("variable_indicator"), first_at+1);

                            String new_code = temp_code;

                            new_code = new_code.substring(0,first_at) + count + new_code.substring(second_at+1);
                            System.out.println(new_code);
                            full_generated_code.append(new_code).append("\n");

                        }
                        else if(temp_code.contains(config.getProperty("variable_indicator") + "$" + config.getProperty("variable_indicator"))) {
                            int first_at = temp_code.indexOf(config.getProperty("variable_indicator"));
                            int second_at = temp_code.indexOf(config.getProperty("variable_indicator"), first_at+1);

                            String new_code = temp_code;

                            new_code = new_code.substring(0,first_at) + saved_word + new_code.substring(second_at+1);
                            System.out.println(new_code);
                            full_generated_code.append(new_code).append("\n");

                        }
                        else if(temp_code.contains(config.getProperty("variable_indicator"))) {

                            int first_at = temp_code.indexOf(config.getProperty("variable_indicator"));
                            int second_at = temp_code.indexOf(config.getProperty("variable_indicator"), first_at+1);

                            String new_code = temp_code;

                            new_code = new_code.substring(0,first_at) + values[j_count] + new_code.substring(second_at+1);
                            System.out.println(new_code);

                            j_count ++;
                            full_generated_code.append(new_code).append("\n");
                        }
                        else {
                            System.out.println(temp_code);
                            full_generated_code.append(temp_code).append("\n");
                        }
                    }
                }
                count++;
            }
            else if(Files.readAllLines(Paths.get("lines_in.txt")).get(i).startsWith("$")) {
                saved_word = Files.readAllLines(Paths.get("lines_in.txt")).get(i).substring(1);
            }
        }
        if(!file_code_out.exists() && !file_code_out.isDirectory()) {
            try {
                Files.write(path_code_out, Collections.singleton(full_generated_code.toString()), StandardCharsets.UTF_8);
                System.out.println("Created file: code_out.txt. ");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            Files.write(path_code_out, Collections.singleton(full_generated_code.toString()), StandardCharsets.UTF_8);
        }
    }
}