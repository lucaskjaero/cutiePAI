package com.dapoppy.v001;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ProcessBuilder;
import java.util.ArrayList;

/**
 * This Class includes public static and non-static methods that enable "deeper" thinking abilities
 * and more customized functions of Poppy.
 */
public class Head {

    // Compile a .exe file from .ahk file
    public static int finishLesson(String lesson) throws IOException {
        try {
            // get current directory of Poppy
            String dir = System.getProperty("user.dir");
            // compile the .ahk file into .exe file for later use
            ProcessBuilder pp = new ProcessBuilder(dir + "\\mem\\act\\Ahk2Exe.exe", "/in", lesson + "/action.ahk");
            // execute the cmd process
            Process p = pp.start();
            return p.waitFor(); // if everything works, expect return 0
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            return 1;
        }
    }

    // Shorten output statements in command line
    private static void console(Object line) {
        System.out.println("Poppy: " + line);
    }


    /**
     * ------------------------------------------------------
     * The part below is for thorough analyzing of user input
     * *----------------------------------------------------
     */

    // Get phrases that have certain amount of words from a string sentence (usually uContext)
    // TODO: write a check method to check if sentence has >= than phraseSize
    public static String[] getPhrases(String sentence, int phraseSize) {
        String[] words = sentence.split(" ");
        int wordAmount = words.length;
        int phraseAmount = wordAmount - phraseSize + 1;
        String[] phrases = new String[phraseAmount];

        for (int i = 0; i < phraseAmount; i++) {
            phrases[i] = words[i];
            for (int j = i + 1; j < (i + phraseSize); j++) {
                phrases[i] += " " + words[j];
            }
        }
        return phrases;
    }

    // Get a list of similar patterns to user input for later confirmation
    public static ArrayList<String> getSimilarPatterns(String input) throws IOException{
        double goldenLevenshteinVal = 0.618; // 1 divided by 1.618 = 0.618, how cool is that!?
        ArrayList<String> similarPatterns = new ArrayList<>();
        similarPatterns.add("Poppy"); // Add 1 place-holder item to avoid empty list
        String[] tempPattern, patternFiles = {"innate", "learned"};
        String pMatch; // For reference to pattern's match
        String tempInput; // For holding first part of an input containing popvar

        for (int i=0; i<2; i++) {
            FileReader patternFile = new FileReader("mem/pat/" + patternFiles[i] + ".txt");
            BufferedReader inputPatterns = new BufferedReader(patternFile);

            // Search through lines of the pattern file to find closest match to user input
            while ((pMatch = inputPatterns.readLine()) != null) {
                //System.out.println("Similarity of \"" + pMatch + "\" & \"" + input
                //      + "\" is: " + similarity(input, pMatch));
                // Try to look for the input variable "popvar" in the pattern
                if (pMatch.contains("popvar")) {
                    // If input variable exists, check if user input contains first part of the pattern
                    tempPattern = pMatch.split("\\*");
                    // Only consider finding close match with input long enough
                    if (input.length() > tempPattern[0].length() + 3) {
                        // Get the relative first part of the user input
                        tempInput = input.substring(0, tempPattern[0].length() + 3); // Max 3 wrong added char
                        // If there is a close match, fill other variables accordingly
                        if (similarity(tempInput, tempPattern[0]) >= goldenLevenshteinVal) {
                            // Process match from 1st part of official pattern & variable part of user input
                            pMatch = tempPattern[0] + input.substring(tempInput.length() - 3, input.length());
                            similarPatterns.add(pMatch); // Add the match to list of similar patterns
                        }
                    }
                } else if (similarity(input, pMatch) >= goldenLevenshteinVal) {
                    // Basic closest match to fill others if input variable does not exist
                    similarPatterns.add(pMatch);
                }
                // Always skip 2 lines
                for (int j = 0; j < 2; j++) inputPatterns.readLine();
            }
            inputPatterns.close();
        }
        return similarPatterns;
    }

    /**
     * Calculates the similarity between two strings.
     * http://stackoverflow.com/questions/955110/similarity-string-comparison-in-java
     * @param s1 1st string, interchangeable with s2
     * @param s2 2nd string, interchangeable with s1
     * @return a number within 0 and 1 (similarity should be at least above 0.5)
     */
    private static double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0; /* both strings are zero length */
        }

        // TODO: Consider StringUtils to calculate the edit distance:
        /* https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/StringUtils.html
        return (longerLength - StringUtils.getLevenshteinDistance(longer, shorter)) / double) longerLength; */
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
    }

    /**
     * Example implementation of the Levenshtein Edit Distance
     * http://rosettacode.org/wiki/Levenshtein_distance#Java
     * http://richardminerich.com/2012/09/levenshtein-distance-and-the-triangle-inequality/
     * @param s1 1st string, interchangeable with s2
     * @param s2 2nd string, interchangeable with s1
     * @return Levenshtein Distance
     */
    private static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }
}
