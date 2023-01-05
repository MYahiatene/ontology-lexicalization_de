package de.citec.sc.generator.utils;

import com.github.jsonldjava.utils.Obj;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GraphExtractor {
    public static void main(String[] args) throws IOException {
        //extract();
        String className = "test/";
        String workingDirectory = System.getProperty("user.dir");
        Path source = Paths.get(workingDirectory + "/results/");
        Path destination = Paths.get(workingDirectory + "/results_all_classes/" + "result_" + className+"/results/" );
        if(!Files.isDirectory(destination)){
        Files.createDirectories(destination);}
        List<String> files = Arrays.asList("/result_noun.json", "/result_adj.json", "/result_verb.json");

        Files.list(source).forEach(src -> {
            try {
                 Files.copy(src, Paths.get(destination+"/"+ src.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }


        });


       for (String f : files) {
            try {
                Files.copy(Paths.get(workingDirectory + f),
                        Paths.get(workingDirectory + "/results_all_classes/" + "result_" + className + "/" + f), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                System.err.println("Error copying " + f);
            }
        }
    }

    public static void extract() {
        List<JSONObject> noun = new ArrayList<>();
        List<JSONObject> adj = new ArrayList<>();
        List<JSONObject> verb = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(System.getProperty("user.dir") + "/result.json", StandardCharsets.ISO_8859_1)) {
            Object obj = jsonParser.parse(reader);
            JSONArray arr = (JSONArray) ((JSONObject) obj).get("@graph");
            String POS = "partOfSpeech";
            String NOUN = "http://www.lexinfo.net/ontology/2.0/lexinfo#/noun";
            String ADJ = "http://www.lexinfo.net/ontology/2.0/lexinfo#/adjective";
            String VERB = "http://www.lexinfo.net/ontology/2.0/lexinfo#/verb";
            int n = arr == null ? 0 : arr.size();
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

        try {
            Files.writeString(Paths.get(System.getProperty("user.dir") + "/result_noun.json"), JSONArray.toJSONString(noun).replace("\\\\", ""), StandardCharsets.ISO_8859_1);
            Files.writeString(Paths.get(System.getProperty("user.dir") + "/result_verb.json"), JSONArray.toJSONString(verb).replace("\\\\", ""), StandardCharsets.ISO_8859_1);
            Files.writeString(Paths.get(System.getProperty("user.dir") + "/result_adj.json"), JSONArray.toJSONString(adj).replace("\\\\", ""), StandardCharsets.ISO_8859_1);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
