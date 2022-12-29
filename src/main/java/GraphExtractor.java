
import com.github.jsonldjava.utils.Obj;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GraphExtractor {

    public static void main(String[] args) {
        List<JSONObject> noun = new ArrayList<>();
        List<JSONObject> adj = new ArrayList<>();
        List<JSONObject> verb = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(System.getProperty("user.dir") + "/result.json")) {
            Object obj = jsonParser.parse(reader);
            JSONArray arr = (JSONArray) ((JSONObject) obj).get("@graph");
            String POS = "partOfSpeech";
            String NOUN = "http://www.lexinfo.net/ontology/2.0/lexinfo#/noun";
            String ADJ = "http://www.lexinfo.net/ontology/2.0/lexinfo#/adjective";
            String VERB = "http://www.lexinfo.net/ontology/2.0/lexinfo#/verb";
            int n = arr.size();
            List<JSONObject> l = IntStream.range(1, n).mapToObj(i -> (JSONObject) arr.get(i)).collect(Collectors.toList());
            l.forEach(e -> {
                String jsonObj = (String) e.get(POS);
                JSONObject lemonJson = new JSONObject();
                lemonJson.put("label", e.get("label"));
                lemonJson.put(POS, e.get(POS));
                if (jsonObj != null && jsonObj.equals(NOUN)) {

                    lemonJson.put("reference", l.stream().filter(x -> x.get("@id").equals("http://localhost:8080/" + e.get("label") + "#Sense1")).findFirst().get().get("reference"));
                    noun.add(lemonJson);
                }
                if (jsonObj != null && jsonObj.equals(VERB)) {

                    lemonJson.put("reference", l.stream().filter(x -> x.get("@id").equals("http://localhost:8080/" + e.get("label") + "#Sense1")).findFirst().get().get("reference"));
                    verb.add(lemonJson);
                }
                if (jsonObj != null && jsonObj.equals(ADJ)) {
                    String adjRef = (String) l.stream().filter(x -> x.get("@id").equals("http://localhost:8080/" + e.get("label") + "#Sense1")).findFirst().get().get("reference");
                    lemonJson.put("hasValue", l.stream().filter(x -> x.get("@id").equals(adjRef)).findFirst().get().get("hasValue"));
                    lemonJson.put("onProperty", l.stream().filter(x -> x.get("@id").equals(adjRef)).findFirst().get().get("onProperty"));
                    adj.add(lemonJson);
                }
            });


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }


}
