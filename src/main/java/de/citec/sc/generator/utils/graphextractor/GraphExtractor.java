package de.citec.sc.generator.utils.graphextractor;

import com.github.jsonldjava.utils.Obj;
import com.opencsv.CSVWriter;
import edu.stanford.nlp.pipeline.CoreDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.jena.base.Sys;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
@AllArgsConstructor
public class GraphExtractor {
    private final String POS = "partOfSpeech";
    private final String NOUN = "http://www.lexinfo.net/ontology/2.0/lexinfo#/noun";
    private final String ADJ = "http://www.lexinfo.net/ontology/2.0/lexinfo#/adjective";
    private final String VERB = "http://www.lexinfo.net/ontology/2.0/lexinfo#/verb";
    private String clazz;

    public void extract(String clazz) throws IOException, URISyntaxException {
        List<JSONObject> noun = new ArrayList<>();
        List<JSONObject> adj = new ArrayList<>();
        List<JSONObject> verb = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        this.clazz = clazz;
        String folder = System.getProperty("user.dir") + "/results_all_classes/result_" + clazz;
        Path classResultCSVDir = Paths.get(folder + "/results_csv/");
        String filePostFix = "_" + clazz + ".json";
        if (clazz.isEmpty()) {
            throw new RuntimeException("Class name empty!");
        }
        String resultFile = folder + "/result" + filePostFix;
        if (Files.notExists(Path.of(resultFile))) {
            throw new FileNotFoundException("Result file " + resultFile + " does not exist!");
        }
        if (Files.notExists(classResultCSVDir)) {
            Files.createDirectories(classResultCSVDir);
        }
        try (FileReader reader = new FileReader(resultFile, StandardCharsets.UTF_8)) {
            Object obj = jsonParser.parse(reader);
            JSONArray arr = (JSONArray) ((JSONObject) obj).get("@graph");

            int n = arr == null ? 0 : arr.size();
            List<JSONObject> l = IntStream.range(1, n).mapToObj(i -> (JSONObject) arr.get(i)).collect(Collectors.toList());
            createLemons(l, noun, verb, adj, 0);
            noun = noun.stream().filter(no -> !((List) no.get("references")).isEmpty()).collect(Collectors.toList());
            verb = verb.stream().filter(ve -> !((List) ve.get("references")).isEmpty()).collect(Collectors.toList());
            adj = adj.stream().filter(ad -> !((List) ad.get("references")).isEmpty()
                    && !((List) ad.get("hasValueList")).isEmpty()
                    && !((List) ad.get("onPropertyList")).isEmpty()).collect(Collectors.toList());
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        try {
            Files.writeString(Paths.get(folder + "/result_noun" + filePostFix), JSONArray.toJSONString(noun).replace("\\\\", ""), StandardCharsets.UTF_8);
            Files.writeString(Paths.get(folder + "/result_verb" + filePostFix), JSONArray.toJSONString(verb).replace("\\\\", ""), StandardCharsets.UTF_8);
            Files.writeString(Paths.get(folder + "/result_adj" + filePostFix), JSONArray.toJSONString(adj).replace("\\\\", ""), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        writeToCsv(noun, verb, adj, clazz);

    }

    private static void writeToCsv(List<JSONObject> noun, List<JSONObject> verb, List<JSONObject> adj, String clazz) throws URISyntaxException, IOException {
        createNounPPFrame(noun, clazz);
        createTransitiveIntransitiveFrame(verb, clazz);
        createGradableAttributeAdjective(adj, clazz);

    }

    //TODO:
    private static void createNounPPFrame(List<JSONObject> noun, String clazz) throws IOException {
        String folder = System.getProperty("user.dir") + "/results_all_classes/result_" + clazz + "/results_csv";
        Path pathNoun = Paths.get(
                folder + "/NounPPFrame.csv"
        );

        try (CSVWriter writer = new CSVWriter(new FileWriter(pathNoun.toString()))) {
            String[] nounPPFrameHeader = {"LemonEntry", "partOfSpeech", "gender", "writtenFormNominative(singular)",
                    "writtenFormNominative (plural)", "writtenFormSingular (accusative)", "writtenFormSingular (dative)",
                    "writtenFormSingular (genetive)", "preposition", "SyntacticFrame", "copulativeArg", "prepositionalAdjunct", "sense", "reference",
                    "domain", "range", "domain_article", "domain_written_singular", "domain_written_plural", "rangeArticle", "range_written_singular",
                    "range_written_plural"
            };
            writer.writeNext(nounPPFrameHeader);

            for (JSONObject n : noun) {
                String label = (String) n.get("label");
                List senses = (List) n.get("references");
                for (Object o : senses) {
                    String senseVal = ((Map) o).values().stream().findFirst().orElseThrow().toString();
                    String senseKey = ((Map) o).keySet().stream().findFirst().orElseThrow().toString().split("/")[3];
                    String[] line = {senseKey, "noun", "masculine", label, label, label, label, label, label, "von", "", "", "", "", "", "", "", "", "", "", ""};
                    writer.writeNext(line);
                }
            }

        }
    }

    //TODO:
    private static void createTransitiveIntransitiveFrame(List<JSONObject> verb, String clazz) throws IOException {
        String folder = System.getProperty("user.dir") + "/results_all_classes/result_" + clazz + "/results_csv";
        Path pathVerb = Paths.get(
                folder + "/TransitiveIntransitiveFrame.csv"
        );

        try (CSVWriter writer = new CSVWriter(new FileWriter(pathVerb.toString()))) {
            String[] transitiveFrameHeader = {"LemonEntry", "partOfSpeech", "writtenFormInfinitive", "writtenForm3rdPresent", "writtenFormPast", "writtenFormPerfect", "SyntacticFrame", "subject", "directObject", "sense", "reference", "domain", "range", "passivePreposition"
            };
            String[] intransitiveFrameHeader = {"LemonEntry", "partOfSpeech", "writtenFormInfinitive", "writtenFormThridPerson", "writtenFormPast", "writtenFormPerfect", "preposition", "SyntacticFrame", "subject", "prepositionalAdjunct", "sense", "reference", "domain", "range", "domainArticle", "domainWrittenSingular", "domainWrittenPlural", "rangeArticle", "rangeWrittenSingular", "rangeWrittenPlural"
            };
            writer.writeNext(transitiveFrameHeader);

            for (JSONObject v : verb) {
                String label = (String) v.get("label");
                List senses = (List) v.get("references");
                for (Object o : senses) {
                    String senseVal = ((Map) o).values().stream().findFirst().orElseThrow().toString();
                    String senseKey = ((Map) o).keySet().stream().findFirst().orElseThrow().toString().split("/")[3];
                    String[] line = {senseKey, "verb", "masculine", label, label, label, label, label, label, "von", "", "", "", "", "", "", "", "", "", "", ""};
                    writer.writeNext(line);
                }
            }

        }
    }

    //TODO:
    private static void createGradableAttributeAdjective(List<JSONObject> adj, String clazz) throws IOException {
        String folder = System.getProperty("user.dir") + "/results_all_classes/result_" + clazz + "/results_csv";
        Path pathVerb = Paths.get(
                folder + "/GradableAttributeAdjective.csv"
        );

        try (CSVWriter writer = new CSVWriter(new FileWriter(pathVerb.toString()))) {
            String[] gradableAdjectiveHeader = {"LemonEntry", "partOfSpeech", "writtenForm", "comparative", "superlative_singular", "superlative_plural", "SyntacticFrame", "predFrame", "sense", "reference", "oils:boundTo", "oils:degree", "domain", "range", "preposition"
            };
            String[] attributeAdjectiveHeader = {"LemonEntry", "partOfSpeech", "writtenForm", "SyntacticFrame", "copulativeSubject", "attributiveArg", "sense", "reference", "owl:onProperty", "owl:hasValue", "domain", "range"
            };
            writer.writeNext(gradableAdjectiveHeader);

            for (JSONObject a : adj) {
                String label = (String) a.get("label");
                List senses = (List) a.get("references");
                for (Object o : senses) {
                    String senseVal = ((Map) o).values().stream().findFirst().orElseThrow().toString();
                    String senseKey = ((Map) o).keySet().stream().findFirst().orElseThrow().toString().split("/")[3];
                    String[] line = {senseKey, "verb", "masculine", label, label, label, label, label, label, "von", "", "", "", "", "", "", "", "", "", "", ""};
                    writer.writeNext(line);
                }
            }

        }
    }


    private void createLemons(List<JSONObject> l, List<JSONObject> noun, List<JSONObject> verb, List<JSONObject> adj, long numberOfLemons) {
        l.forEach(e -> {
            String jsonObj = (String) e.get(POS);
            JSONObject lemonJson = new JSONObject();
            lemonJson.put("label", e.get("label"));
            lemonJson.put(POS, e.get(POS));
            if (e.get("sense") instanceof String) {
                String sense = (String) e.get("sense");
                if (e.get("sense") != null) {
                    List<Object> senses = l.stream().filter(x -> x.get("@id").equals(sense)).limit(numberOfLemons).map(sense2 -> sense2.get("reference")).collect(Collectors.toList());
                    List referenceList = senses.stream().map(sense2 -> {
                        Object tmp = l.stream().filter(x -> x.get("@id").equals(sense2)).findFirst().orElse(new JSONObject()).get("reference");
                        if (sense2 == null || tmp == null) {
                            return null;
                        } else
                            return Map.of(sense2, tmp);
                    }).collect(Collectors.toList());
                    lemonJson.put("references", referenceList);
                    if (jsonObj != null && jsonObj.equals(NOUN)) {
                        noun.add(lemonJson);
                    }
                    if (jsonObj != null && jsonObj.equals(VERB)) {
                        verb.add(lemonJson);
                    }
                    if (jsonObj != null && jsonObj.equals(ADJ)) {
                        List<Object> hasValueList = (List<Object>) referenceList.stream().map(reference -> l.stream().filter(x -> x.get("@id").equals(reference)).findFirst().orElse(new JSONObject()).get("hasValue")).collect(Collectors.toList());
                        List<Object> onPropertyList =(List<Object>) referenceList.stream().map(reference -> l.stream().filter(x -> x.get("@id").equals(reference)).findFirst().orElse(new JSONObject()).get("onProperty")).collect(Collectors.toList());
                        lemonJson.put("hasValueList", hasValueList);
                        lemonJson.put("onPropertyList", onPropertyList);
                        adj.add(lemonJson);
                    }
                }
            } else {
                JSONArray senses = (JSONArray) e.get("sense");
                if (e.get("sense") != null) {
                    List referenceList =(List) senses.stream().map(sense2 -> {
                        Object tmp = l.stream().filter(x -> x.get("@id").equals(sense2)).findFirst().orElse(new JSONObject()).get("reference");
                        if (sense2 == null || tmp == null) {
                            return null;
                        } else
                            return Map.of(sense2, tmp);
                    }).collect(Collectors.toList());
                    lemonJson.put("references", referenceList);
                    if (jsonObj != null && jsonObj.equals(NOUN)) {
                        noun.add(lemonJson);
                    }
                    if (jsonObj != null && jsonObj.equals(VERB)) {
                        verb.add(lemonJson);
                    }
                    if (jsonObj != null && jsonObj.equals(ADJ)) {
                        List<Object> hasValueList = (List<Object>) referenceList.stream().map(reference -> l.stream().filter(x -> x.get("@id").equals(reference)).findFirst().orElse(new JSONObject()).get("hasValue")).collect(Collectors.toList());
                        List<Object> onPropertyList =(List<Object>) referenceList.stream().map(reference -> l.stream().filter(x -> x.get("@id").equals(reference)).findFirst().orElse(new JSONObject()).get("onProperty")).collect(Collectors.toList());
                        lemonJson.put("hasValueList", hasValueList);
                        lemonJson.put("onPropertyList", onPropertyList);
                        adj.add(lemonJson);
                    }
                }
            }
        });

    }


}
