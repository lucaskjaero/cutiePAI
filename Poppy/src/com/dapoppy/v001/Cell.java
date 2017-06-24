package com.dapoppy.v001;

import java.io.*;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This Class contains simple public static methods that are used by other classes
 */
public class Cell {

    // Get random String element from a String array
    public static String randomize(String[] strArray) {
        int index = new Random().nextInt(strArray.length);
        return strArray[index];
    }

    // Clone and return another instance of one string array
    public static String[] clone(String[] strArray) {
        String[] temp = new String[strArray.length];
        for (int i = 0; i < strArray.length; i++) {
            temp[i] = strArray[i];
        }
        return temp;
    }

    // Clone and return another instance of one int array
    public static int[] clone(int[] strArray) {
        int[] temp = new int[strArray.length];
        for (int i = 0; i < strArray.length; i++) {
            temp[i] = strArray[i];
        }
        return temp;
    }

    /**
     * Create a blank text file in a specific path, delete old file
     *
     * @param filePath e.g. /mem/act/innate/youtube-recommender/
     * @param fileName e.g. test
     * @throws IOException
     */
    public static void createFile(String filePath, String fileName) throws IOException {
        // Prep directory to the specific file
        String fileDir = System.getProperty("user.dir") + filePath + fileName + ".txt";
        PrintWriter writer = new PrintWriter(fileDir, "UTF-8");
        writer.close();
    }

    /**
     * Append a line to the text file in a specific path (not used for mass scale writing operations)
     *
     * @param filePath  e.g. /mem/act/innate/youtube-recommender/
     * @param fileName  e.g. test
     * @param inputLine cat
     * @throws IOException
     */
    public static void appendFile(String filePath, String fileName, String inputLine) throws IOException {
        // Prep directory to the specific file
        String fileDir = System.getProperty("user.dir") + filePath + fileName + ".txt";
        FileWriter fwriter = new FileWriter(fileDir, true);
        PrintWriter outputFile = new PrintWriter(fwriter);
        outputFile.print(inputLine);
        outputFile.close();
    }

    /**
     * Strip duplicated lines in a text file
     *
     * @param filePath e.g. /mem/act/innate/youtube-recommender/video-lists/
     * @param fileName e.g. 1
     * @return number of lines for later uses
     * @throws IOException
     */
    public static int makeUniqueLinedFile(String filePath, String fileName) throws IOException {
        // Prep directory to the specific file
        String fileDir = System.getProperty("user.dir") + filePath + fileName + ".txt";
        String line; // Temp line variable
        BufferedReader inputFile = new BufferedReader(new FileReader(fileDir));
        Set<String> lines = new LinkedHashSet<>(50000);
        int lineNumber = 0;

        // Read the file & add lines to the set
        while ((line = inputFile.readLine()) != null) {
            lines.add(line);
        }
        inputFile.close();

        // Rewrite everything into the same file
        BufferedWriter outputFile = new BufferedWriter(new FileWriter(fileDir));
        for (String unique : lines) {
            lineNumber++;
            outputFile.write(unique);
            outputFile.newLine();
        }
        outputFile.close();

        return lineNumber;
    }


    /**
     * Get video IDs from a youtube link
     *
     * @param url e.g. https://www.youtube.com/results?search_query=cat%20funny"
     * @throws IOException
     */
    public static void harvestContent(String url) throws IOException {
        URL link = new URL(url); // Create URL object
        Scanner html = new Scanner(link.openStream()); // Scanner object that read data stream from URL object
        String fileDir = System.getProperty("user.dir") + "/mem/act/innate/web-harvest/basic/content.txt";
        FileWriter fwriter = new FileWriter(fileDir, false);
        PrintWriter ytWriter = new PrintWriter(fwriter);

        while (html.hasNext()) {
            ytWriter.println(html.nextLine());
        }
        ytWriter.close();

    }
}
