package de.citec.sc.generator.utils.graphextractor;

import com.github.jsonldjava.utils.Obj;
import com.opencsv.CSVWriter;
import edu.stanford.nlp.pipeline.CoreDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.text.StringEscapeUtils;
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
        String domainAndRangePath = System.getProperty("user.dir") + "/input/domainAndRange.csv";
        List<List<String>> records = new ArrayList<>();
        Map<String, List<String>> domainAndRange = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(domainAndRangePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                List<String> tmpList = Arrays.asList(values);
                domainAndRange.put(tmpList.get(0), tmpList.subList(1, tmpList.size()));
            }
        }
        System.out.println("DOMAINANDRANGE: " + domainAndRange);
        this.clazz = clazz;
        String folder = System.getProperty("user.dir") + "/results_all_classes/result_" + clazz;
        Path classResultCSVDir = Paths.get(folder + "/results_csv/");
        String filePostFix = "_" + clazz + ".json";
        if (clazz.isEmpty()) {
            throw new RuntimeException("Class name empty!");
        }
        String resultFile = folder + "/result" + filePostFix;
        if (Files.notExists(Path.of(resultFile))) {
            System.out.println("Result file " + resultFile + " does not exist!");
            return;
        }
        if (Files.notExists(classResultCSVDir)) {
            Files.createDirectories(classResultCSVDir);
        }
        try (FileReader reader = new FileReader(resultFile, StandardCharsets.UTF_8)) {
            Object obj = jsonParser.parse(reader);
            JSONArray arr = (JSONArray) ((JSONObject) obj).get("@graph");
            int n = arr == null ? 0 : arr.size();
            List<JSONObject> l = IntStream.range(1, n).mapToObj(i -> (JSONObject) arr.get(i)).collect(Collectors.toList());
            createLemons(l, noun, verb, adj, 100);
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
        //System.out.println("NOUN:" + noun);
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
            CSVExtractor csvExtractor = new CSVExtractor();
            for (JSONObject n : noun) {
                //TODO: check
                if (n == null) {
                    continue;
                }
                String label = (String) n.get("label");
                String labelUpper = label.substring(0, 1).toUpperCase() + label.substring(1);
                Map<String, String> contentMap = csvExtractor.getNounDataFromWiktionary(labelUpper);


                if (n.get("sense") instanceof List) {
                    List senses = (List) n.get("sense");
                    for (Object o : senses) {
                        if (o == null) {
                            continue;
                        }
                        String[] line = {(String) o, "noun", contentMap.get("Genus"), contentMap.get("Nominativ Singular"), contentMap.get("Nominativ Plural"), contentMap.get("Akkusativ Singular"), contentMap.get("Dativ Singular"), contentMap.get("Genetiv Singular"), "von", "NounPPFrame", "range", "domain", "1", (String) n.get("reference"), (String) n.get("reference"), (String) n.get("reference"), "von", "von", "von", "von", "von"};
                        writer.writeNext(line);
                    }
                } else {
                    String sense = (String) n.get("sense");
                    String reference = n.get("reference") instanceof String ? StringEscapeUtils.unescapeJson("http:"+((String) n.get("reference")).split(",")[0].split(":")[2].replace("\"", "")) : (String) ((List) n.get("reference")).get(0);
                    String[] line = {sense, "noun", contentMap.get("Genus"), contentMap.get("Nominativ Singular"), contentMap.get("Nominativ Plural"), contentMap.get("Akkusativ Singular"), contentMap.get("Dativ Singular"), contentMap.get("Genetiv Singular"), "von", "NounPPFrame", "range", "domain", "1", reference, reference, reference, "von", "von", "von", "von", "von"};
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

            CSVExtractor csvExtractor = new CSVExtractor();
            for (JSONObject v : verb) {
                //TODO: check
                if (v == null) {
                    continue;
                }
                String label = (String) v.get("label");
                Map<String, String> contentMap = csvExtractor.getNounDataFromWiktionary(label);
                if (v.get("sense") instanceof List) {
                    List senses = (List) v.get("sense");
                    for (Object o : senses) {
                        if (o == null) {
                            continue;
                        }
                        String[] line = {(String) o, "noun", contentMap.get("Genus"), contentMap.get("Nominativ Singular"), contentMap.get("Nominativ Plural"), contentMap.get("Akkusativ Singular"), contentMap.get("Dativ Singular"), contentMap.get("Genetiv Singular"), "von", "NounPPFrame", "range", "domain", "1", (String) v.get("reference"), (String) v.get("reference"), (String) v.get("reference"), "von", "von", "von", "von", "von"};
                        writer.writeNext(line);
                    }
                } else {
                    String sense = (String) v.get("sense");
                    String reference = v.get("reference") instanceof String ? StringEscapeUtils.unescapeJson("http:"+((String) v.get("reference")).split(",")[0].split(":")[2].replace("\"", "")) : (String) ((List) v.get("reference")).get(0);
                    String[] line = {sense, "noun", contentMap.get("Genus"), contentMap.get("Nominativ Singular"), contentMap.get("Nominativ Plural"), contentMap.get("Akkusativ Singular"), contentMap.get("Dativ Singular"), contentMap.get("Genetiv Singular"), "von", "NounPPFrame", "range", "domain", "1", reference, reference, reference, "von", "von", "von", "von", "von"};
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

            CSVExtractor csvExtractor = new CSVExtractor();
            for (JSONObject a : adj) {
                //TODO: check
                if (a == null) {
                    continue;
                }
                String label = (String) a.get("label");
                String labelUpper = label.substring(0, 1).toUpperCase() + label.substring(1);
                Map<String, String> contentMap = csvExtractor.getNounDataFromWiktionary(labelUpper);
                if (a.get("sense") instanceof List) {
                    List senses = (List) a.get("sense");
                    for (Object o : senses) {
                        if (o == null) {
                            continue;
                        }
                        String[] line = {(String) o, "noun", contentMap.get("Genus"), contentMap.get("Nominativ Singular"), contentMap.get("Nominativ Plural"), contentMap.get("Akkusativ Singular"), contentMap.get("Dativ Singular"), contentMap.get("Genetiv Singular"), "von", "NounPPFrame", "range", "domain", "1", (String) a.get("reference"), (String) a.get("reference"), (String) a.get("reference"), "von", "von", "von", "von", "von"};
                        writer.writeNext(line);
                    }
                } else {
                    String sense = (String) a.get("sense");
                    String reference = a.get("reference") instanceof String ? StringEscapeUtils.unescapeJson("http:"+((String) a.get("reference")).split(",")[0].split(":")[2].replace("\"", "")) : (String) ((List) a.get("reference")).get(0);
                    String[] line = {sense, "noun", contentMap.get("Genus"), contentMap.get("Nominativ Singular"), contentMap.get("Nominativ Plural"), contentMap.get("Akkusativ Singular"), contentMap.get("Dativ Singular"), contentMap.get("Genetiv Singular"), "von", "NounPPFrame", "range", "domain", "1", reference, reference, reference, "von", "von", "von", "von", "von"};
                    writer.writeNext(line);
                }

            }

        }
    }

    //TODO: FIX THIS
    private void createLemons(List<JSONObject> l, List<JSONObject> noun, List<JSONObject> verb, List<JSONObject> adj, long numberOfLemons) {
        l.forEach(e -> {
            if (e.get("sense") instanceof String) {
                String jsonObj = (String) e.get(POS);
                JSONObject lemonJson = new JSONObject();
                lemonJson.put("label", e.get("label"));
                lemonJson.put(POS, e.get(POS));
                String sense = (String) e.get("sense");
                if (e.get("sense") != null) {
                    List<Object> singleReference = l.stream().filter(x -> x.get("@id").equals(sense)).map(obj -> obj.get("reference")).limit(numberOfLemons).collect(Collectors.toList());
                    lemonJson.put("reference", singleReference);
                    lemonJson.put("sense", sense);
                    if (jsonObj != null && jsonObj.equals(NOUN)) {
                        noun.add(lemonJson);
                    }
                    if (jsonObj != null && jsonObj.equals(VERB)) {
                        verb.add(lemonJson);
                    }
                    if (jsonObj != null && jsonObj.equals(ADJ)) {
                        adj.add(lemonJson);
                    }
                }
            } else {
                JSONArray senses = (JSONArray) e.get("sense");
                if (senses != null) {
                    for (Object sense : senses
                    ) {
                        JSONObject obj = new JSONObject();
                        obj.put("sense", sense);
                        String ref = l.stream().filter(el -> el.get("@id").equals(sense)).findFirst().get().toString();
                        obj.put("reference", ref);
                        obj.put("label", e.get("label"));
                        String posTag = (String) e.get(POS);
                        obj.put(POS, posTag);
                        if (posTag != null && posTag.equals(NOUN)) {
                            noun.add(obj);
                        }
                        if (posTag != null && posTag.equals(VERB)) {
                            verb.add(obj);

                        }
                        if (posTag != null && posTag.equals(ADJ)) {
                            adj.add(obj);
                        }
                    }

                }
            }
        });

    }


}
