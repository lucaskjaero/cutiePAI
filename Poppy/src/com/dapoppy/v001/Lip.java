package com.dapoppy.v001;

import java.io.*;
import java.util.Scanner;
import java.util.Random;

/**
 * This Class handles conversational response for chat input from the user
 * Created by Trung Tran on 3/13/2017.
 */
public class Lip {
    /**
     * This comes in the beginning to greet user
     * @return
     * @throws IOException
     */
    public static String sayHello() throws IOException {
        return randomPhrase("hello", "amount","phrases");
    }

    /**
     * This usually comes after Hello Phrase in the beginning
     * @return a day-time aware greeting.
     * @throws IOException
     */
    public static String sayGreeting() throws IOException {
        // get current time of day (morning, afternoon or evening)
        String timeOfDay = Self.timeOfDay("default");
        // return corresponding phrase from txt files
        return randomPhrase("greeting", "amount-" + timeOfDay,"phrases-" + timeOfDay);
    }

    // This method to get random phrases is more straight-forward from the pattern-reaction matching one
    public static String randomPhrase(String categoryDir, String amountTxt, String phrasesTxt) throws IOException {
        // declare variables
        int amount, count = 1;
        String thePhrase = "";
        // get current directory of Poppy, set-up files & objects
        String dir = System.getProperty("user.dir");
        FileReader amountFile = new FileReader(dir + "/mem/say/" + categoryDir + "/" + amountTxt + ".txt");
        FileReader phrasesFile = new FileReader(dir + "/mem/say/" + categoryDir + "/" + phrasesTxt + ".txt");
        Scanner readPhrases = new Scanner(phrasesFile);

        // get from amount.txt the number of hello phrases.txt & randomize
        amount = new Random().nextInt(new Scanner(amountFile).nextInt()) + 1;
        while (readPhrases.hasNextLine()) {
            if (count == amount){
                thePhrase = readPhrases.nextLine();
                break;
            }else{
                readPhrases.nextLine();
                count++;
            }
        }
        readPhrases.close();
        return thePhrase;
    }
}
