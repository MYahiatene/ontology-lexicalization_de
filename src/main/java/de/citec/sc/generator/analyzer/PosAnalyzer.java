/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.generator.analyzer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.citec.generator.config.ConfigLemon;
import de.citec.generator.config.ConfigLex;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.simple.Token;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import edu.stanford.nlp.util.Sets;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.buf.Utf8Decoder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * @author elahi
 */
public class PosAnalyzer implements TextAnalyzer {


    @JsonIgnore
    private static String stanfordModelFile = resources + "stanford-postagger-2015-12-09/models/german-ud.tagger";
    //@JsonIgnore
    //private static MaxentTagger taggerModel = new MaxentTagger(stanfordModelFile);
    @JsonIgnore
    private Integer numberOfSentences = 0;
    @JsonIgnore
    private Boolean flag = false;
    @JsonIgnore

    private String fullPosTag = null;
    @JsonIgnore
    List<String> germanStopwords;
    @JsonIgnore
    private StanfordCoreNLP nlp;
    /*
        static {
            taggerModel = new MaxentTagger(stanfordModelFile);
        }
    */
    private Set<String> words = new HashSet<>();
    private Set<String> adjectives = new HashSet<>();
    private Set<String> nouns = new HashSet<>();
    private Set<String> verbs = new HashSet<>();
    private Set<String> pronouns = new HashSet<>();


    private String inputText = null;
    private List<String> stopWords;

    public PosAnalyzer(String inputText, String analysisType, Integer numberOfSentences) throws Exception {
        this.numberOfSentences = numberOfSentences;
        this.inputText = inputText;
        BufferedReader reader = new BufferedReader(new StringReader(inputText));
        //this.nlp = new StanfordCoreNLP("german");
        if (analysisType.contains(POS_TAGGER_WORDS)) {
            // posTaggerWords(reader, nlp);
            posTaggerWords(reader, null);
        }
        stopWords = new StopWords().getGermanStopWords();

    }

    private void posTaggerWords(BufferedReader reader, StanfordCoreNLP nlp) throws Exception {
        //reader.readLine();
        // Todo: replace stanford java api with fastapi spacy_stanza rest service
        String docLines = reader.lines().collect(Collectors.toList()).get(0);
        //String docLines = reader.lines().collect(Collectors.joining("."));
        docLines = !docLines.startsWith("\"") ? "\"" + docLines : docLines;
        docLines = !docLines.endsWith("\"") ? docLines + "\"" : docLines;
        docLines = "{\"text\":" + docLines + "}";
        try {
            List<List<String>> senList = sendToNLPPipeline(docLines);
            //CoreDocument doc = nlp.processToCoreDocument(docLines);
            Map<Integer, Map<String, Set<String>>> sentencePosTags = new HashMap<>();
            Map<Integer, Set<String>> sentenceWords = new HashMap<>();
            // List<List<CoreLabel>> tSentences = doc.sentences().stream().map(CoreSentence::tokens).collect(Collectors.toList());
            Integer index = 1;
            Set<String> wordsofSentence = new HashSet<>();
            Map<String, Set<String>> posTaggers = new HashMap<>();
            for (List<String> stringList : senList) {
                TaggedWord taggedWord = new TaggedWord(stringList.get(1), stringList.get(2));
                String word = taggedWord.word();
                word = this.modifyWord(word);
                if (isStopWord(word)) {
                    continue;
                }
                if (taggedWord.tag().startsWith(TextAnalyzer.ADJECTIVE)
                        || taggedWord.tag().startsWith(TextAnalyzer.NOUN)
                        || taggedWord.tag().startsWith(TextAnalyzer.VERB)) {
                    posTaggers = this.populateValues(taggedWord.tag(), word, posTaggers);
                }
                wordsofSentence.add(word);

            }
            sentenceWords.put(index, wordsofSentence);
            sentencePosTags.put(index, posTaggers);
            sentenwisePosSeperated(sentenceWords, sentencePosTags);
          /*  for (List<CoreLabel> sentence : tSentences) {
                index++;
                Set<String> wordsofSentence = new HashSet<>();
                Map<String, Set<String>> posTaggers = new HashMap<>();
                List<TaggedWord> tSentence = sentence.stream().map(e -> new TaggedWord(e.word(), e.tag())).collect(Collectors.toList());
                for (TaggedWord taggedWord : tSentence) {
                    String word = taggedWord.word();
                    word = this.modifyWord(word);
                    if (isStopWord(word)) {
                        continue;
                    }
                    if (taggedWord.tag().startsWith(TextAnalyzer.ADJECTIVE)
                            || taggedWord.tag().startsWith(TextAnalyzer.NOUN)
                            || taggedWord.tag().startsWith(TextAnalyzer.VERB)) {
                        posTaggers = this.populateValues(taggedWord.tag(), word, posTaggers);
                    }
                    wordsofSentence.add(word);
                }
                sentenceWords.put(index, wordsofSentence);
                sentencePosTags.put(index, posTaggers);
            }

            sentenwisePosSeperated(sentenceWords, sentencePosTags);*/
        } catch (Exception e) {
        }
    }

    private List<List<String>> sendToNLPPipeline(String text) {
        RestTemplate template = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request =
                new HttpEntity<>(text, headers);
        //ResponseEntity<List> res = template.postForEntity("http://nlp/text", request, List.class);
        ResponseEntity<List> res = template.postForEntity("http://0.0.0.0:80/text", request, List.class);
        Logger.getLogger(this.getClass().getName()).log(Level.WARNING, ((List<List<String>>) res.getBody()).toString());
        return (List<List<String>>) res.getBody();
    }

    private boolean isStopWord(String word) throws URISyntaxException, IOException {
        word = word.trim().toLowerCase();
        this.stopWords = new StopWords().getGermanStopWords();
        return this.stopWords.contains(word);
    }

    // todo: überprüfe ob richtiges Model(tagger)
    public Boolean posTaggerText(String inputText) throws Exception {
        byte[] inputTextBytes = inputText.getBytes();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(inputTextBytes)));
        Sentence sen = new Sentence(reader.readLine());
        List<TaggedWord> tSentence = new ArrayList<>();
        for (Token token : sen.tokens()) {
            TaggedWord tWord = new TaggedWord(token.word(), token.posTag());
            tSentence.add(tWord);
        }
        this.fullPosTag = this.setTaggs(tSentence);
        return true;
    }

    private String getSentenceFromWordListTagged(List<TaggedWord> tSentence) {
        String str = "";
        for (TaggedWord taggedWord : tSentence) {
            String line = taggedWord + " ";
            str += line;
        }
        str = StringUtils.substring(str, 0, str.length() - 1);
        return str;
    }


    private void sentenwisePosSeperated(Map<Integer, Set<String>> sentenceWords, Map<Integer, Map<String, Set<String>>> sentencePosTags) {
        for (Integer number : sentenceWords.keySet()) {
            Map<String, Set<String>> temp = sentencePosTags.get(number);
            if (temp != null) {
                Set<String> set = sentenceWords.get(number);
                words.addAll(set);
            }
            for (String posTag : temp.keySet()) {
                Set<String> set = temp.get(posTag);
                if (posTag.contains(TextAnalyzer.NOUN)) {
                    nouns.addAll(set);
                } else if (posTag.contains(TextAnalyzer.ADJECTIVE)) {
                    adjectives.addAll(set);
                } else if (posTag.contains(TextAnalyzer.VERB)) {
                    verbs.addAll(set);
                } else if (posTag.contains(TextAnalyzer.PRONOUN)) {
                    pronouns.addAll(set);
                }
            }

            number++;
            if (number == numberOfSentences) {
                break;
            }
        }

    }

    private Map<String, Set<String>> populateValues(String key, String value, Map<String, Set<String>> posTaggers) {
        Set<String> words = new HashSet<String>();
        if (posTaggers.containsKey(key)) {
            words = posTaggers.get(key);
        }
        words.add(value);
        posTaggers.put(key, words);

        return posTaggers;
    }

    public String getText() {
        return inputText;
    }

    public String getFullPosTag() {
        return fullPosTag;
    }

    public Set<String> getWords() {
        return words;
    }

    public Set<String> getAdjectives() {
        return adjectives;
    }

    public Set<String> getNouns() {
        return nouns;
    }

    public Set<String> getVerbs() {
        return verbs;
    }

    public Set<String> getPronouns() {
        return pronouns;
    }

    public Boolean getPosTagFound() {
        return flag;
    }


    private String setTaggs(List<TaggedWord> tSentence) {
        String str = "";
        for (TaggedWord taggedWord : tSentence) {
            String line = taggedWord.tag() + "_";
            str += line;
        }
        return StringUtils.substring(str, 0, str.length() - 1);
    }

    private String modifyWord(String word) {
        return word.toLowerCase().trim().strip();
    }


}
