package de.citec.sc.generator.utils.graphextractor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CSVExtractor {

    public Map<String, String> getNounDataFromWiktionary(String label) {
        String apiUrl = "https://de.wiktionary.org/w/api.php";
        String action = "query";
        String format = "json";
        String titles = label;
        String prop = "revisions";
        String rvprop = "content";
        Map<String, String> contentMap = new HashMap<>();

        // Construct the API query URL
        String queryUrl = apiUrl + "?action=" + action + "&format=" + format + "&titles=" + titles + "&prop=" + prop + "&rvprop=" + rvprop;
        try {
            // Send the HTTP GET request
            URL url = new URL(queryUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read the API response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse the JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject query = jsonResponse.getJSONObject("query");
            JSONObject pages = query.getJSONObject("pages");
            String pageId = (String) pages.keys().next(); // Get the first (and only) page ID
            JSONObject page = pages.getJSONObject(pageId);
            JSONArray revisions = page.getJSONArray("revisions");
            JSONObject revision = revisions.getJSONObject(0); // Get the first (and latest) revision
            String content = revision.getString("*");
            String[] contentArr = content.split("\n");
            for (String s : contentArr) {
                if (s.contains("=")) {
                    String[] keyValArr = s.split("=");
                    if (keyValArr.length == 2) {
                        contentMap.put(keyValArr[0].replace("|", ""), keyValArr[1]);
                    }
                }
            }
        } catch (IOException | JSONException e) {
            return new HashMap<>();
        }
        return contentMap;
    }

    public static void main(String[] args) {
        CSVExtractor test = new CSVExtractor();
        Map<String, String> lol = test.getNounDataFromWiktionary("hund");
        System.out.println(lol);
    }

}


