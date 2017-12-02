package com.company;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

public class wikiInfo {
    private static String wikiURL = "https://en.wikipedia.org/w/api.php?action=query&prop=extracts&format=json&exintro=&titles=";

    public static String getMyWikiArticlesByWord(String word){
        String definition = null;
        try {
            URL url = new URL(wikiURL + word);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            while ((output = br.readLine()) != null) {
                JSONObject obj = new JSONObject(output);
                JSONObject myObj =  obj.getJSONObject("query").getJSONObject("pages");

                Iterator myList = myObj.keys();
                while(myList.hasNext()){
                    String myNextPage = (String) myList.next();
                    definition = obj.getJSONObject("query").getJSONObject("pages").getJSONObject(myNextPage).getString("extract").replaceAll("\\<[^>]*>","");
                    System.out.println("Word: " + word);
                    System.out.println("\tDefinition:" + definition);
                }
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return definition;
        }
    }
}
