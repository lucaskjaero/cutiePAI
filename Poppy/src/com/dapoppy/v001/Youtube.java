package com.dapoppy.v001;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Methods for Youtube related native functions
 */

public class Youtube {
    static final String PATH = "/mem/act/innate/youtube-recommender/";

    public static void makeRecommendation(String query) throws IOException {
        String queryMatch, match = "0", url;
        int queryID = 0, vidNum, recNum, rand;
        // Search through lines of the queries file to find the match
        FileReader queryFile = new FileReader("mem/act/innate/youtube-recommender/queries.txt");
        // Note: no need for first backslash with FileReader
        BufferedReader queryFileReader = new BufferedReader(queryFile);

        // Search through lines of the reaction file to find the match
        while ((queryMatch = queryFileReader.readLine()) != null && match.equals("0")) {
            queryID++; // Track the ID by incrementing each cycle
            if (queryMatch.equals(query)) {
                match = queryFileReader.readLine();
            }else{ // Skip 1 line per cycle if there is no match
                queryFileReader.readLine();
            }
        }

        // If 1st time this query appears, create new entry to queries.txt & related recommendation set...
        if (match.equals("0")) {
            queryID++; // If 1st time, increment queryID to make the next queryID
            Cell.appendFile(PATH, "queries", "\n" + query + "\n");
            Cell.appendFile(PATH, "queries", queryID + "");
            Cell.createFile(PATH + "video-lists/", queryID + "");
            Cell.createFile(PATH + "video-lists/", queryID + "-ban");
            Cell.createFile(PATH + "video-lists/", queryID + "-fav");
            Cell.createFile(PATH + "video-lists/", queryID + "-info");
            Cell.appendFile(PATH + "video-lists/", queryID + "-info", "0\n0");
            fillRecList(query, queryID); // Go through multiple Youtube page to scrap video IDs and fill
        } else {
            // Get number of recommendable videos related to the query
            String filePath = "mem/act/innate/youtube-recommender/video-lists/" + queryID + "-info.txt";
            BufferedReader bf = new BufferedReader(new FileReader(filePath));
            vidNum = Integer.parseInt(bf.readLine());
            recNum = Integer.parseInt(bf.readLine());
            bf.close();

            // OR if number of recommendations made passes half of total number of videos
            // TODO: make the parameter 3 connected to the personality param Conservativeness
            if (recNum > (vidNum / 3)) {
                Cell.appendFile(PATH, "queries", "\n" + query + "\n");
                Cell.appendFile(PATH, "queries", queryID + "");
                fillRecList(query, queryID); // Go through multiple Youtube page to scrap video IDs and fill
            }
        }
        // Again, get number of recommendable videos related to the query
        String filePath = "mem/act/innate/youtube-recommender/video-lists/" + queryID + "-info.txt";
        BufferedReader bf = new BufferedReader(new FileReader(filePath));
        vidNum = Integer.parseInt(bf.readLine());
        bf.close();


        // Open random link from the video list
        String recPath = "mem/act/innate/youtube-recommender/video-lists/" + queryID + ".txt";
        BufferedReader recReader = new BufferedReader(new FileReader(recPath));
        rand = new Random().nextInt(vidNum) + 1;
        for (int i=1; i<rand; i++) {
            recReader.readLine();
        }
        url = "https://www.youtube.com/watch?v=" + recReader.readLine();
        recReader.close();
        editRecInfo(2, queryID, 1); // Increase number of recommendation made (+1)
        // Open the specific url
        try {
            Desktop d = Desktop.getDesktop();
            d.browse(new URI(url));
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void fillRecList(String query, int queryID) throws IOException {
        String ytResult = "https://www.youtube.com/results?sp=";
        int vidNum;
        // YouTube really made it hard for web scrappers. There is no easy way shorten nor maintain the codes below
        collectVids(ytResult + "EgIQAUgA6gMA&q=" + query, queryID); // relevant all time
        collectVids(ytResult + "EgIQAUgU6gMA&q=" + query, queryID); // 2nd page of above
        collectVids(ytResult + "EgIQAUgo6gMA&q=" + query, queryID); // 3rd page of above
//        collectVids(ytResult + "EgQIAxABSADqAwA%253D&q=" + query, queryID); // relevant this week
//        collectVids(ytResult + "EgQIAxABSBTqAwA%253D&q=" + query, queryID); // 2nd page of above
//        collectVids(ytResult + "EgQIAxABSCjqAwA%253D&q=" + query, queryID); // 3rd page of above
        collectVids(ytResult + "EgQIBBABSADqAwA%253D&q=" + query, queryID); // relevant this month
        collectVids(ytResult + "EgQIBBABSBTqAwA%253D&q=" + query, queryID); // 2nd page of above
        collectVids(ytResult + "EgQIBBABSCjqAwA%253D&q=" + query, queryID); // 3rd page of above
        collectVids(ytResult + "CAMSAhABSADqAwA%253D&q=" + query, queryID); // most view all time
        collectVids(ytResult + "CAMSAhABSBTqAwA%253D&q=" + query, queryID); // 2nd page of above
        collectVids(ytResult + "CAMSAhABSCjqAwA%253D&q=" + query, queryID); // 3rd page of above
//        collectVids(ytResult + "CAMSBAgDEAFIAOoDAA%253D%253D&q=" + query, queryID); // most view this week
//        collectVids(ytResult + "CAMSBAgDEAFIFOoDAA%253D%253D&q=" + query, queryID); // 2nd page of above
//        collectVids(ytResult + "CAMSBAgDEAFIKOoDAA%253D%253D&q=" + query, queryID); // 3rd page of above
        collectVids(ytResult + "CAMSBAgEEAFIAOoDAA%253D%253D&q=" + query, queryID); // most view this month
        collectVids(ytResult + "CAMSBAgEEAFIFOoDAA%253D%253D&q=" + query, queryID); // 2nd page of above
        collectVids(ytResult + "CAMSBAgEEAFIKOoDAA%253D%253D&q=" + query, queryID); // 3rd page of above
        // Clean up duplicates
        vidNum = Cell.makeUniqueLinedFile(PATH + "video-lists/", queryID + "");
        // Go through all the links of videos collected and get even more videos.
        // This would add only a few more videos, not worth making the users wait 10x longer for that.
//        FileReader fr = new FileReader(PATH + "video-lists/" + queryID + ".txt");
//        BufferedReader bf = new BufferedReader(fr);
//        String vidID; // Temp line variable
//        while ((vidID = bf.readLine()) != null) {
//            collectVids(ytVideo + vidID, queryID); // Get all related video IDs
//        }
//      vidNum = Cell.makeUniqueLinedFile(PATH + "video-lists/", queryID + ""); // Clean up duplicates again

        editRecInfo(1, queryID, vidNum);
        editRecInfo(2, queryID, 1);
    }

    /**
     *
     * @param option 1: number of video (1st line) ; 2: number of recommendations made (2nd line)
     * @param queryID
     * @param value
     * @throws IOException
     */
    public static void editRecInfo(int option, int queryID, int value) throws IOException {
        String vidNum, recNum, tempStr1, tempStr2;
        // Note: no need for first backslash with FileReader
        String filePath = "mem/act/innate/youtube-recommender/video-lists/" + queryID + "-info.txt";
        FileReader fr = new FileReader(filePath);
        BufferedReader bf = new BufferedReader(fr);
        // Get vidNum and recNum
        tempStr1 = bf.readLine();
        tempStr2 = bf.readLine();
        if (option == 1) {
            vidNum = value + "";
            recNum = tempStr2;
        } else {
            vidNum = tempStr1;
            recNum = Integer.toString((Integer.parseInt(tempStr2) + value));
        }
        bf.close();
        Cell.createFile(PATH + "video-lists/", queryID + "-info");
        Cell.appendFile(PATH + "video-lists/", queryID + "-info", vidNum + "\n" + recNum);
    }

    // Get random String element from a String array
    public static boolean vidExist(String videoID) throws IOException {
        boolean avail1 = true, avail2 = true;
        URL link = new URL("https://www.youtube.com/watch?v=" + videoID);
        Scanner html = new Scanner(link.openStream());
        while(html.hasNext()){
            // TODO: read the 2 lines of text below in different languages
            if (avail2 == true) {
                // Only need to check for this line until found
                if (html.nextLine().contains("This video is unavailable")){ avail2 = false; }
            } else {
                // Check for this line just to make sure
                if (html.nextLine().contains("Sorry about that")){ avail1 = false; }
            }
        }
        avail2 = avail1 == true ? true : false; // If avail1 happens to stay true, change avail2 to true
        return avail2; // If there are videos that have these 2 lines, too bad, no recommendation
    }

    /**
     * Get video IDs from a youtube link
     * @param url e.g. https://www.youtube.com/results?search_query=cat%20funny"
     * @param queryID ID of the input variable
     * @throws IOException
     */
    public static void collectVids(String url, int queryID) throws IOException {
        URL link = new URL(url); // Create URL object
        Scanner html = new Scanner(link.openStream()); // Scanner object that read data stream from URL object
        String fileDir = System.getProperty("user.dir") + PATH + "video-lists/" + queryID + ".txt";
        FileWriter fwriter = new FileWriter(fileDir, true);
        PrintWriter ytWriter = new PrintWriter(fwriter);

        // Match regex to take everything after watch?v=, minus other stuffs
        String pattern = "(?<=watch\\?v=)[^#\\&\\?]*"; // This regex is enough, below is for later
        // String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        while(html.hasNext()){
            Pattern compiledPattern = Pattern.compile(pattern);
            Matcher matcher = compiledPattern.matcher(html.nextLine());
            // When there is a match, write the first 11 chars as a Youtube vid ID
            if(matcher.find()){
                ytWriter.println(matcher.group().substring(0,11));
            }
        }
        ytWriter.close();
    }
}
