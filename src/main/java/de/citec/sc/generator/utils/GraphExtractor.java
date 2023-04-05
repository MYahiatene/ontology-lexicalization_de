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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GraphExtractor {
    private static final String POS = "partOfSpeech";
    private static final String NOUN = "http://www.lexinfo.net/ontology/2.0/lexinfo#/noun";
    private static final String ADJ = "http://www.lexinfo.net/ontology/2.0/lexinfo#/adjective";
    private static final String VERB = "http://www.lexinfo.net/ontology/2.0/lexinfo#/verb";

    /*    public static void main(String[] args) throws IOException {
            //extract();
            String className = "test/";
            String workingDirectory = System.getProperty("user.dir");
            Path source = Paths.get(workingDirectory + "/results/");
            Path destination = Paths.get(workingDirectory + "/results_all_classes/" + "result_" + className + "/results/");
            if (!Files.isDirectory(destination)) {
                Files.createDirectories(destination);
            }
            List<String> files = Arrays.asList("/result.json", "/result_noun.json", "/result_adj.json", "/result_verb.json");

            Files.list(source).forEach(src -> {
                try {
                    Files.copy(src, Paths.get(destination + "/" + src.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
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


        }*/
    public static void main(String[] args) {
/*        List<JSONObject> noun = new ArrayList<>();
        List<JSONObject> adj = new ArrayList<>();
        List<JSONObject> verb = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(System.getProperty("user.dir") + "/result.json", StandardCharsets.UTF_8)) {
            Object obj = jsonParser.parse(reader);
            JSONArray arr = (JSONArray) ((JSONObject) obj).get("@graph");

            int n = arr == null ? 0 : arr.size();
            List<JSONObject> l = IntStream.range(1, n).mapToObj(i -> (JSONObject) arr.get(i)).collect(Collectors.toList());
            //createLemons(l, noun, verb, adj,100);
            System.out.println(l.get(0).get("sense").getClass());
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }*/
        extract();
    }

    public static void extract() {
        List<JSONObject> noun = new ArrayList<>();
        List<JSONObject> adj = new ArrayList<>();
        List<JSONObject> verb = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(System.getProperty("user.dir") + "/result.json", StandardCharsets.UTF_8)) {
            Object obj = jsonParser.parse(reader);
            JSONArray arr = (JSONArray) ((JSONObject) obj).get("@graph");

            int n = arr == null ? 0 : arr.size();
            List<JSONObject> l = IntStream.range(1, n).mapToObj(i -> (JSONObject) arr.get(i)).toList();
            createLemons(l, noun, verb, adj, 100);

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        try {
            Files.writeString(Paths.get(System.getProperty("user.dir") + "/result_noun.json"), JSONArray.toJSONString(noun).replace("\\\\", ""), StandardCharsets.UTF_8);
            Files.writeString(Paths.get(System.getProperty("user.dir") + "/result_verb.json"), JSONArray.toJSONString(verb).replace("\\\\", ""), StandardCharsets.UTF_8);
            Files.writeString(Paths.get(System.getProperty("user.dir") + "/result_adj.json"), JSONArray.toJSONString(adj).replace("\\\\", ""), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private static void createLemons(List<JSONObject> l, List<JSONObject> noun, List<JSONObject> verb, List<JSONObject> adj, long numberOfLemons) {
        l.forEach(e -> {
            String jsonObj = (String) e.get(POS);
            JSONObject lemonJson = new JSONObject();
            lemonJson.put("label", e.get("label"));
            lemonJson.put(POS, e.get(POS));
            // System.out.println(e.get("sense"));
            if (e.get("sense") instanceof String) {
                String sense = (String) e.get("sense");
                if (e.get("sense") != null) {
                    List<Object> senses = l.stream().filter(x -> x.get("@id").equals(sense)).limit(numberOfLemons).map(sense2 -> sense2.get("reference")).toList();
                    List referenceList = senses.stream().map(sense2 -> {
                        Object tmp = l.stream().filter(x -> x.get("@id").equals(sense2)).findFirst().orElse(new JSONObject()).get("reference");
                        if (sense2 == null || tmp == null) {
                            return null;
                        } else
                            return Map.of(sense2, tmp);
                    }).toList();
                    lemonJson.put("references", referenceList);
                    if (jsonObj != null && jsonObj.equals(NOUN)) {
                        noun.add(lemonJson);
                    }
                    if (jsonObj != null && jsonObj.equals(VERB)) {
                        verb.add(lemonJson);
                    }
                    if (jsonObj != null && jsonObj.equals(ADJ)) {
                        List<Object> hasValueList = referenceList.stream().map(reference -> l.stream().filter(x -> x.get("@id").equals(reference)).findFirst().orElse(new JSONObject()).get("hasValue")).toList();
                        List<Object> onPropertyList = referenceList.stream().map(reference -> l.stream().filter(x -> x.get("@id").equals(reference)).findFirst().orElse(new JSONObject()).get("onProperty")).toList();
                        lemonJson.put("hasValueList", hasValueList);
                        lemonJson.put("onPropertyList", onPropertyList);
                        adj.add(lemonJson);
                    }
                }
            } else {
                JSONArray senses = (JSONArray) e.get("sense");
                if (e.get("sense") != null) {
                    List referenceList = senses.stream().map(sense2 -> {
                        Object tmp = l.stream().filter(x -> x.get("@id").equals(sense2)).findFirst().orElse(new JSONObject()).get("reference");
                        if (sense2 == null || tmp == null) {
                            return null;
                        } else
                            return Map.of(sense2, tmp);
                    }).toList();
                    lemonJson.put("references", referenceList);
                    if (jsonObj != null && jsonObj.equals(NOUN)) {
                        noun.add(lemonJson);
                    }
                    if (jsonObj != null && jsonObj.equals(VERB)) {
                        verb.add(lemonJson);
                    }
                    if (jsonObj != null && jsonObj.equals(ADJ)) {
                        List<Object> hasValueList = referenceList.stream().map(reference -> l.stream().filter(x -> x.get("@id").equals(reference)).findFirst().orElse(new JSONObject()).get("hasValue")).toList();
                        List<Object> onPropertyList = referenceList.stream().map(reference -> l.stream().filter(x -> x.get("@id").equals(reference)).findFirst().orElse(new JSONObject()).get("onProperty")).toList();
                        lemonJson.put("hasValueList", hasValueList);
                        lemonJson.put("onPropertyList", onPropertyList);
                        adj.add(lemonJson);
                    }
                }
            }
        });

    }


}
