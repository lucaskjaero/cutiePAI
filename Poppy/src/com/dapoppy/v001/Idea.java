package com.dapoppy.v001;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * The Idea class includes simple methods for matching user input to related patterns and reactions.
 */
public class Idea {
    private String userInput, currentTopic;
    private String[] topics;
    private int[] reactions;
    // Raw variables of main/chosen reaction
    private String[] chatResponses, actionDir, gifResponses, nextPatterns;
    private String originReaction;
    private String inputVar = "";

    // Constructor for Idea object
    public Idea(String userInput, String currentTopic) throws IOException {
        try {
            this.userInput = userInput;
            this.currentTopic = currentTopic;
            findPattern(userInput);
            setMainReaction(getReaction());
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    // Overload construction method to clone another Idea object
    public Idea(Idea origin) throws IOException {
        this.userInput = origin.userInput;
        this.currentTopic = origin.currentTopic;
        this.topics = Cell.clone(origin.topics);
        this.reactions = new int[origin.reactions.length];
        this.chatResponses = Cell.clone(origin.chatResponses);
        this.actionDir = Cell.clone(origin.actionDir);
        this.gifResponses = Cell.clone(origin.gifResponses);
        this.nextPatterns = Cell.clone(origin.nextPatterns);
        this.originReaction = origin.originReaction;
        this.inputVar = origin.inputVar;
        try {
            findPattern(userInput);
            setMainReaction(getReaction());
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    // Get a random chat response from all responses
    public String getChatResponse() {
        return Cell.randomize(chatResponses);
    }

    // Get a random gif response from all responses
    public String getGifResponses() {
        return Cell.randomize(gifResponses);
    }

    public String[] getNextPatterns() {
        return nextPatterns;
    }

    public String getOriginReaction() {
        return originReaction;
    }

    public String getInputVar() {
        return inputVar;
    }

    public Integer originReactionID() {
        return Integer.parseInt(originReaction);
    }

    // Execute related action of a reaction
    public void takeAction() throws IOException {
        int actionID = getReaction(); // Get the relevant reaction ID
        // Do nothing if actionDir has only 1 element of value "n"
        if (!actionDir[0].equals("n")) {
            // Assign actionType base on the ID of reaction
            String actionType = actionID > 0 ? "learned" : "innate";
            // Generate file path name base on object of this class
            String filePath = "mem/act/" + actionType + "/" + actionDir[0] + "/" + actionDir[1];
            // Check if a .exe file exist & execute accordingly
            File f = new File(filePath + ".exe");
            if (f.exists() && !f.isDirectory()) {
                execute(actionType, actionDir[0], actionDir[1]);
            } else { // Wait until the .exe file is generated
                int cont = Head.finishLesson(filePath);
                if (cont == 0) // If 0 is returned, .exe file is good to go
                    execute(actionType, actionDir[0], actionDir[1]);
            }
        }
        // A lot of if-else to call native functions
        if (actionID == -1000) {
            if (inputVar.equals("you") || inputVar.equals("yourself") || inputVar.equals("you poppy")) {
                inputVar = "im+poppy"; // Simply replace these inputs above with Im Poppy
            }
            Youtube.makeRecommendation((inputVar.trim().replace(" ", "+")));
        }
    }

    /**
     * Execute an action .exe file.
     *
     * @param actionType
     * @param actionSet
     * @param actionCase
     * @throws IOException
     */
    private static void execute(String actionType, String actionSet, String actionCase) throws IOException {
        // get current directory of Poppy
        String dir = System.getProperty("user.dir");
        // build up the cmd process.
        ProcessBuilder p = new ProcessBuilder(dir + "\\mem\\act\\" + actionType + "\\"
                + actionSet + "\\" + actionCase + "\\" + "action.exe");
        // execute the cmd process
        p.start();
    }

    /*

http://eastmanreference.com/complete-list-of-applescript-key-codes/
http://www.mactricksandtips.com/2013/04/insert-text-with-a-keystroke-in-any-application.html

Run multiple line apple script


 */
    public static void macExecute() throws IOException {
        Runtime runtime = Runtime.getRuntime();
        String applescriptCommand =  "tell app \"iTunes\"\n" +
                "activate\n" +
                "set sound volume to 40\n" +
                "set EQ enabled to true\n" +
                "play\n" +
                "end tell";

        String[] args = { "osascript", "-e", applescriptCommand };
        Process process = runtime.exec(args);
    }


    // Choose 1 reaction and return it
    public int getReaction() {
        int react = reactions[0]; // default reaction is always the first
        // Compare topics of each reaction & change react if there is a match
        for (int i = 0; i < reactions.length; i++) {
            if (topics[i] == currentTopic) {
                react = reactions[i];
            }
        }
        return react;
    }

    // Set up the variables of a specific reaction
    private void setMainReaction(int reactionID) throws IOException {
        String reactionMatch, reactionString = "i:" + reactionID;
        boolean match = false;
        String fileName = reactionID > 0 ? "learned" : "innate";

        FileReader reactionFile = new FileReader("mem/act/" + fileName + "-reactions.txt");
        BufferedReader inputReactions = new BufferedReader(reactionFile);

        // Search through lines of the reaction file to find the match
        while ((reactionMatch = inputReactions.readLine()) != null && match == false) {
            if (reactionMatch.equals(reactionString)) {
                // When there is a match, read the next lines and put into variables
                chatResponses = inputReactions.readLine().split("--");
                // TODO: handle multiple input variables
                // If input variable exists, look for place-holder "popvar" & replace with inputVar
                if (inputVar == null) { // Do nothing if inputVar equals null
                } else {
                    // Modify the chat response with dynamic variable
                    for (int i = 0; i < chatResponses.length; i++) {
                        if (inputVar.equals("you") || inputVar.equals("yourself") || inputVar.equals("you poppy")) {
                            chatResponses[i] = chatResponses[i].replace("$popvar", "me");
                        } else {
                            chatResponses[i] = chatResponses[i].replace("$popvar", inputVar);
                        }
                    }
                }
                nextPatterns = inputReactions.readLine().split(",");
                gifResponses = inputReactions.readLine().split(",");
                originReaction = inputReactions.readLine();
                actionDir = inputReactions.readLine().split("/");
                match = true;
            } else {
                // Jump 5 lines each search cycle
                for (int i = 0; i < 5; i++)
                    inputReactions.readLine();
            }
        }
    }

    // Go through more than 1 pattern file (innate.txt and learned.txt) with several input variations
    private void findPattern(String input) throws IOException {
        // Start finding a match in innate patterns and get related reaction IDs
        boolean match = matchPattern(input, "innate");
        //  If there is no match in innate patterns, repeat with learned patterns
        if (match == false) {
            match = matchPattern(input, "learned");
        }
        // TODO: get uContext and oContext as input
        // If at the end there is no match, default reaction is called using a different input
        if (match == false) {
            match = matchPattern("pls act cannot find", "innate");
        }
    }

    // Straight forward method to set-up the arrays of topics and reactions related to a pattern
    private boolean matchPattern(String input, String fileName) throws IOException {
        String patternMatch, topicMatch = "", reactionMatch = "";
        String[] tempPattern;
        boolean match = false;

        FileReader patternFile = new FileReader("mem/pat/" + fileName + ".txt");
        BufferedReader inputPatterns = new BufferedReader(patternFile);

        // Search through lines of the pattern file to find the match
        while ((patternMatch = inputPatterns.readLine()) != null && match == false) {
            // Try to look for the input variable "popvar" in the pattern
            if (patternMatch.contains("popvar")) {
                // If there input variable exists, check if user input contains first part of the pattern
                tempPattern = patternMatch.split("\\*");
                // If there is a match, fill other variables accordingly
                if (input.contains(tempPattern[0])) {
                    topicMatch = inputPatterns.readLine();
                    reactionMatch = inputPatterns.readLine();
                    // TODO: multiple input variables in between contextual words (complex scenarios)
                    inputVar = input.replace(tempPattern[0], "").trim().toLowerCase();
                    match = true;
                } else {
                    // Jump 2 lines each search cycle
                    for (int i = 0; i < 2; i++)
                        inputPatterns.readLine();
                }
            } else if (patternMatch.equals(input)) {
                // Basic match to fill others if input variable does not exist
                topicMatch = inputPatterns.readLine();
                reactionMatch = inputPatterns.readLine();
                match = true;
            } else {
                // Jump 2 lines each search cycle
                for (int i = 0; i < 2; i++)
                    inputPatterns.readLine();
            }
        }
        // Set instance variables reactions and topics if a match is found
        if (match == true) {
            // Split the string variables into arrays
            String[] reactionArray = reactionMatch.split(",");
            topics = topicMatch.split(","); // set String array topics

            // Parse reactionArray to integer array reactions
            reactions = new int[reactionArray.length];
            for (int i = 0; i < reactionArray.length; i++) {
                reactions[i] = Integer.parseInt(reactionArray[i]);
            }
        }
        return match;
    }


    /**-------------------------------------------------------------
     * The part below is for actions that call native Java functions
     **-----------------------------------------------------------*/

    // TODO: a sleep function to refresh and reorganize patterns, reactions, etc.


}