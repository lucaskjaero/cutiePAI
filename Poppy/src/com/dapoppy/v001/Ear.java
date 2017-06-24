package com.dapoppy.v001;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * This Class includes puclic static methods that process user input into cleaner versions.
 */
public class Ear
{
    // https://www.tutorialspoint.com/java/java_regular_expressions.htm
    // http://stackoverflow.com/questions/7938033/string-pattern-matching-in-java
    // http://javadevnotes.com/java-split-string-into-arraylist-examples
    public static String getChatPatterns()
    {
        return "";
    }

    /**
     * Combine several processing methods into one
     * @param chat Text input of user
     * @return chat pattern
     * @throws IOException
     */
    public static String analyzeChat (String chat) throws IOException{
        return alphabetizeWords(removeStopWords(chat));
    }

    /**
     *
     * @param chat Text input of user
     * @return alphabetical pattern
     */
    public static String alphabetizeWords (String chat) {
        String[] words = chat.split("\\s+");
        Arrays.sort(words);
        StringBuilder sortedChat = new StringBuilder();
        for(String word:words){
            sortedChat.append(word);
            sortedChat.append(" ");
        }
        return sortedChat.toString().trim();
    }

    /**
     * Remove special characters from a string
     * @param chat Text input of user
     * @return Text without special characters
     */
    public static String clearChat(String chat){
        // Regex matches everything that is not a letter in any language & not a separator (whitespace, linebreak etc.)
        return chat.replaceAll("[^\\p{L}\\p{Z}]","");
    }

    /**
     * Remove stop words (references from Google) from a string for better search performance
     * @param chat Text input of user
     * @return Text without stop words
     */
    public static String removeStopWords(String chat) throws IOException{
        // get the regular expression command from stop-words.txt file
        final FileReader regexFile = new FileReader(System.getProperty("user.dir") + "/mem/stop-words.txt");
        final Scanner regexRead = new Scanner(regexFile);

        // process chat input
        while (regexRead.hasNextLine()) {
            // this regular expression replaces each word only (surrounded by space)
            String regex = "\\s*\\b" + regexRead.nextLine() + "\\b\\s*";
            chat = chat.replaceAll(regex, " ");
        }

        return chat.trim(); // return chat with leading & trailing spaces stripped
    }
}
