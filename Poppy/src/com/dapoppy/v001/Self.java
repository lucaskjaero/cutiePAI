package com.dapoppy.v001;

import java.io.IOException;
import java.util.Calendar;
import java.util.Scanner;

/**
 * Main method with 2 interfaces: CMD and GUI.
 * GUI interface is enabled for end user.
 * CMD interface is for development purposes.
 */
public class Self {

    public static void main(String[] args) throws IOException {
        Boolean meditate = false; // false for GUI interface, true for CMD interface
        String userOS = "win"; // Default user's OS to win

        // Detect Mac users TODO: consider other OS later on as well
        if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0) {
            userOS = "mac";
        }

        if (!meditate) {
            new Body(userOS);
        } else {
            // Declare variables
            Boolean inMeditation = true; // reference for command-line interface
            Boolean innerVoice = true; // true for extra insights in CMD interface
            String input = "";
            String currentTopic = "d";
            String uContext = ""; // unordered, untrimmed, non-alphabetized context
            String oContext = ""; // ordered, trimmed, alphabetized context

            // Initialize new objects
            Lip say = new Lip();
            Scanner read = new Scanner(System.in);

            // Initial greetings
            console(say.sayHello());
            console(say.sayGreeting());

            //Cell.harvestContent("https://www.reddit.com/r/leagueoflegends/");
            //Idea.macExecute();

            // Test new functions here

//            Cell.createFile("/mem/act/innate/youtube-recommender/", "test");

//            int tempNum = Cell.makeUniqueFile("/mem/act/innate/youtube-recommender/lists/", "1");
//            console(tempNum);

//            ArrayList<String> vids = Youtube.getVids("https://www.youtube.com/results?search_query=cat%20funny");
//            for (String value : vids) {
//                System.out.println(value);
//            }


            // Main Loop, Poppy keeps talking
            while (inMeditation) {
                input = read.nextLine().toLowerCase(); // get user input
                uContext = Ear.clearChat(input); // unordered pattern is used 2nd
                oContext = Ear.analyzeChat(uContext); // ordered pattern is the result of analyzed uPattern
                inMeditation = input.equals("bb") ? false : true; // quick quit if user enter 'bb'

                if (innerVoice) {
                    console("unordered : " + uContext);
                    console("ordered : " + oContext);
//                    console("2-word-phrases: " + display(Head.getPhrases(uContext, 2)));
//                    console("3-word-phrases: " + display(Head.getPhrases(uContext, 3)));
                }

                // Create new Idea object for pattern-reaction matching
                Idea currentThought = new Idea(uContext, currentTopic);
                // Chat response of the reaction
                console((currentThought.getChatResponse()));
                // Action of the reaction
                currentThought.takeAction();
            }

//        console(say.goodbye());
//        console(say.parting());
        }
    }

    /**-------------------------------------------------------------------------------
     * The part below is for simple private static methods that helps with development
     **-----------------------------------------------------------------------------*/

    // Shorten output statements in command line
    private static void console(Object line) {
        System.out.println("Inner Voice: " + line);
    }

    // Return printable list of all elements in the array phrases[]
    private static String display(String[] phrases)
    {
        String str = "";
        for(int i = 0; i < phrases.length; i++){
            str += phrases[i]+ " | ";
        }
        return str;
    }

    /**-----------------------------------------------------------------------------------
     * The part below is for experimental methods to be moved later to appropriate Classes
     **---------------------------------------------------------------------------------*/

    /**
     * Get the time of the day as a string
     *
     * @param option Default allows 3, Four allows "night"
     * @return Literal time of the day
     */
    public static String timeOfDay(String option) {
        String time = "";
        final int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        if (option == "default") {
            if (currentHour >= 0 && currentHour < 12)
                time = "morning";
            else if (currentHour >= 12 && currentHour < 18)
                time = "afternoon";
            else
                time = "evening";
        }
        return time;
    }

}