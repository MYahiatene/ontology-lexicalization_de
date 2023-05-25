import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.exec.http.QueryExecutionHTTP;
import org.apache.jena.sparql.exec.http.QuerySendMode;
import org.apache.jena.sparql.exec.http.UpdateExecutionHTTPBuilder;


import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DBpediaQueryExample {
    static String classes = retrieveAllClasses();


    public static void retrieveClassesTranslation() {
        String sparqlEndpoint = "http://dbpedia.org/sparql";
        List<String> classesTranslation = new ArrayList<>();
        for (String clazz : classes.split(" ")) {
            String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                    + "SELECT DISTINCT ?word ?label\n"
                    + "WHERE {\n"
                    + "  VALUES ?word { " + clazz + " }"
                    + "  ?word rdfs:label ?label .\n"
                    + "  FILTER (lang(?label) = \"de\")\n"
                    + "}";
            QueryExecutionHTTP qexec = QueryExecutionHTTP.newBuilder().queryString(queryString).acceptHeader("application/sparql-results+json").endpoint(sparqlEndpoint).build();
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                String word = solution.get("word").toString();
                String label = solution.getLiteral("label").getLexicalForm();
                // System.out.println("Word: " + word + ", Label: " + label);
                classesTranslation.add(clazz.replace("<http://dbpedia.org/ontology/", "").replace(">", "") + "," + label);
                qexec.close();
            }
        }
        //classesTranslation.forEach(System.out::println);
        Path path = Path.of(System.getProperty("user.dir") + "/input/classes_map_de_new.txt");
        try (FileWriter fw = new FileWriter(path.toFile())) {
            for (String clazz : classesTranslation) {
                fw.write(clazz + "\n");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String retrieveAllClasses() {
        StringBuilder sb = new StringBuilder();
        String sparqlEndpoint = "http://dbpedia.org/sparql";
        String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "SELECT DISTINCT ?class ?instance\n"
                + "WHERE {\n"
                + "  ?class rdf:type rdfs:Class .\n"
                + "  FILTER (!strstarts(str(?class), \"http://dbpedia.org/ontology/Property\"))\n"
                + "  ?instance rdf:type ?class .\n"
                + "}";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlEndpoint, query);
        //((QueryEngineHTTP)qexec).addParam("timeout", "10000"); // optional: set a timeout for the query execution
        ResultSet results = qexec.execSelect();
        while (results.hasNext()) {
            QuerySolution solution = results.nextSolution();
            Resource classResource = solution.getResource("class");
            Resource instanceResource = solution.getResource("instance");
            sb.append("<").append(instanceResource.getURI()).append("> ");
            //System.out.println(instanceResource.getURI());
        }
        qexec.close();
        return sb.toString();
    }

    public static void main(String[] args) {
        // retrieveClassesTranslation();

        //retrieveAllClasses();
        getWordFromWiktionary();
    }


    public static void getWordFromWiktionary() {
        String apiUrl = "https://de.wiktionary.org/w/api.php";
        String action = "query";
        String format = "json";
        String titles = "Hund";
        String prop = "revisions";
        String rvprop = "content";

        // Construct the API query URL
        String queryUrl = apiUrl + "?action=" + action + "&format=" + format +
                "&titles=" + titles + "&prop=" + prop + "&rvprop=" + rvprop;
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
            Map<String, String> contentMap = new HashMap<>();
            for (String s : contentArr) {
                if (s.contains("=")) {
                    String[] keyValArr = s.split("=");
                    if (keyValArr.length == 2) {
                        contentMap.put(keyValArr[0].replace("|", ""), keyValArr[1]);
                    }
                }
            }
            System.out.println(contentMap);
           // System.out.println(content);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

    }

}
