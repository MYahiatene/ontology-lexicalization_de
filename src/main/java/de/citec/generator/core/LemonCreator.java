/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.generator.core;

import de.citec.generator.config.LemonConstants;
import de.citec.sc.generator.analyzer.PosAnalyzer;
import de.citec.sc.generator.analyzer.TextAnalyzer;

import static de.citec.sc.lemon.core.Language.DE;
import static de.citec.sc.lemon.core.Language.EN;

import de.citec.sc.lemon.core.Lexicon;
import de.citec.sc.lemon.core.Provenance;
import de.citec.sc.lemon.core.Reference;
import de.citec.sc.lemon.core.Restriction;
import de.citec.sc.lemon.core.Sense;
import de.citec.sc.lemon.core.SenseArgument;
import de.citec.sc.lemon.core.SimpleReference;
import de.citec.sc.lemon.core.SyntacticArgument;
import de.citec.sc.lemon.core.SyntacticBehaviour;

import java.io.*;
import java.util.*;

import de.citec.generator.config.PredictionPatterns;
import edu.stanford.nlp.util.Pair;

import java.util.regex.Pattern;

/**
 * @author elahi
 */
public class LemonCreator implements PredictionPatterns, LemonConstants, TextAnalyzer {

    private String lexiconDirectory = null;
    private Lexicon turtleLexicon = null;
    private Integer rankLimit = 0;
    private Map<String, List<LexiconUnit>> lexiconPosTaggged = new TreeMap<String, List<LexiconUnit>>();

    public LemonCreator(String outputDir, Lexicon turtleLexicon, Integer rankLimit) throws IOException {
        this.lexiconDirectory = outputDir;
        this.turtleLexicon = turtleLexicon;
        this.rankLimit = rankLimit;
    }

    public void preparePropertyLexicon(String prediction, String directory, String key, String interestingness, Map<String, List<LineInfo>> lineLexicon) throws IOException, Exception {
        Map<String, List<LexiconUnit>> posTaggedLex = new TreeMap<String, List<LexiconUnit>>();
        Integer count = 0, countJJ = 0, countVB = 0;
        for (String word : lineLexicon.keySet()) {
            String postagOfWord = null;
            LinkedHashMap<Integer, List<LineInfo>> kbList = new LinkedHashMap<Integer, List<LineInfo>>();
            Integer index = 0;
            List<LineInfo> LineInfos = lineLexicon.get(word);
            //Collections.sort(LineInfos,new LineInfo());  

            Set<String> duplicateCheck = new HashSet<String>();
            count = count + 1;
            for (LineInfo lineInfo : LineInfos) {
                postagOfWord = lineInfo.getPartOfSpeech();
                String value = null;

                String object = lineInfo.getObject();
                List<LineInfo> pairs = new ArrayList<LineInfo>();
                if (duplicateCheck.contains(object)) {
                    continue;
                }
                if (lineInfo.getProbabilityValue().isEmpty()) {
                    continue;
                } else {
                    value = lineInfo.getProbabilityValue(interestingness).toString();
                }
                pairs.add(lineInfo);
                kbList.put(index, pairs);
                index = index + 1;
                duplicateCheck.add(object);
            }
            LexiconUnit LexiconUnit = new LexiconUnit(count, word, postagOfWord, kbList);
            posTaggedLex = this.setPartsOfSpeech(postagOfWord, LexiconUnit, posTaggedLex);
        }
        this.writeFileLemon(prediction, posTaggedLex);

    }

    private void writeFileLemon(String prediction, Map<String, List<LexiconUnit>> posTaggedLex) {
        String posLexInfo = null, givenPosTag = null;
            createLemonEntry(posTaggedLex, ADJECTIVE);
            createLemonEntry(posTaggedLex, VERB);
            createLemonEntry(posTaggedLex, NOUN);

    }

    private void createLemonEntry(Map<String, List<LexiconUnit>> posTaggedLex, String givenPosTag) {
        for (String postag : posTaggedLex.keySet()) {
            if (!postag.contains(givenPosTag)) {
                continue;
            }
            List<LexiconUnit> lexiconUnts = posTaggedLex.get(postag);
            for (LexiconUnit lexiconUnit : lexiconUnts) {
                LinkedHashMap<Integer, List<LineInfo>> ranks = lexiconUnit.getLineInfos();
                String writtenForm = lexiconUnit.getWord();

                if (!isValidWrittenForm(writtenForm)) {
                    continue;
                } else
                    writtenForm = this.modify(writtenForm);

                //todo: change language specific . check why only adjective is working
                de.citec.sc.lemon.core.LexicalEntry entry = new de.citec.sc.lemon.core.LexicalEntry(DE);
                entry.setCanonicalForm(writtenForm);
                String pos = "";

                if (lexiconUnit.getPartsOfSpeech().equals(TextAnalyzer.NOUN)) {
                    pos = lexinfo_noun;
                }
                if (lexiconUnit.getPartsOfSpeech().equals(TextAnalyzer.VERB)) {
                    pos = lexinfo_verb;
                }
                if (lexiconUnit.getPartsOfSpeech().equals(TextAnalyzer.ADJECTIVE)) {
                    pos = lexinfo_adjective;
                }
                entry.setPOS(pos);
                entry.setURI(this.turtleLexicon.getBaseURI() + writtenForm);
                Set<Sense> senses = new HashSet<>();

                Integer index = 0;
                for (Integer rank : ranks.keySet()) {
                    List<LineInfo> rankLineInfo = ranks.get(rank);
                    index = index + 1;
                    if (index > this.rankLimit) {
                        break;
                    }
                    for (LineInfo lineInfo : rankLineInfo) {
                        Sense sense = null;
                        SyntacticBehaviour behaviour = null;
                        Provenance provenance = null;

                        try {

                            Pair<Boolean, Sense> senseCheck = this.addSenseToEntry(this.turtleLexicon.getBaseURI(), writtenForm, lineInfo, postag);
                            if (senseCheck.first()) {
                                sense = senseCheck.second();
                                senses.add(sense);
                            } else {
                                continue;
                            }

                            behaviour = this.addBehaviourToEntry(sense, writtenForm, postag, lineInfo.getPreposition());
                            provenance = this.addProvinceToEntry();
                            if (sense != null && behaviour != null && provenance != null) {
                                entry.addSyntacticBehaviour(behaviour, sense);
                            }

                        } catch (NullPointerException ex) {
                            System.err.println("either sense or behavior or sense is not !!!" + ex.getMessage());
                        } catch (IOException ex) {
                            System.err.println("No sense is added to the entry!!!" + ex.getMessage());
                        } catch (Exception ex) {
                            System.err.println("No behaviour is added to the entry!!!" + ex.getMessage());
                        }

                    }

                }
                if (!senses.isEmpty()) {
                    turtleLexicon.addEntry(entry);
                }
            }
        }
    }

    private Pair<Boolean, Sense> addSenseToEntry(String baseUri, String writtenForm, LineInfo lineInfo, String posTag) throws FileNotFoundException, IOException {
        Sense sense = new Sense();
        Boolean flag = false;
        if (posTag.contains(ADJECTIVE)) {
            flag = this.isValidReference(lineInfo.getObjectOriginal());

            Reference ref = new Restriction(baseUri + "RestrictionClass" + "_" + writtenForm,
                    lineInfo.getObjectOriginal(),
                    lineInfo.getPredicateOriginal());
            sense.setReference(ref);
        } else if (posTag.contains(NOUN) || posTag.contains(VERB)) {
            flag = this.isValidReference(lineInfo.getObjectOriginal());
            Reference ref = new SimpleReference(lineInfo.getObjectOriginal());
            sense.setReference(ref);
        }

        return new Pair<>(flag, sense);

    }

    public boolean isNumeric(String strNum) {
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    private SyntacticBehaviour addBehaviourToEntry(Sense sense, String writtenForm, String posTag, String preposition) throws FileNotFoundException, IOException {
        return new SyntacticBehaviour();

    }

    private Provenance addProvinceToEntry() {
        return new Provenance();
    }

    private Map<String, List<LexiconUnit>> setPartsOfSpeech(String postagOfWord, LexiconUnit LexiconUnit, Map<String, List<LexiconUnit>> lexicon) {
        List<LexiconUnit> temp = new ArrayList<>();
        if (lexicon.containsKey(postagOfWord)) {
            temp = lexicon.get(postagOfWord);
        }
        temp.add(LexiconUnit);
        lexicon.put(postagOfWord, temp);
        return lexicon;
    }

    public String getOutputDir() {
        return lexiconDirectory;
    }

    private String getFirstTag(String posTag) {
        String firstWord = null;
        if (posTag.contains("_")) {
            String info[] = posTag.split("_");
            firstWord = info[0];
        } else {
            firstWord = posTag;
        }
        return firstWord;
    }

    public String getLexiconDirectory() {
        return lexiconDirectory;
    }

    public Map<String, List<LexiconUnit>> getLexiconPosTaggged() {
        return lexiconPosTaggged;
    }

    private String getPair(LineInfo lineInfo, String predictionRule) throws Exception {
        return lineInfo.getSubject() + " " + lineInfo.getPredicate() + lineInfo.getObject();
    }

    // Todo: why is it fitlering out verbs too? objectOriginal='o' ->false
    private Boolean isValidReference(String objectOriginal) {
        if (objectOriginal.contains("http://www.w3.org/2001/XMLSchema")
                || objectOriginal.contains("http://dbpedia.org/datatype/centimetre")) {
            return false;
        } else if (objectOriginal.contains("http")) {
            return true;
        } else {
            return false;
        }
    }

    private Boolean isValidWrittenForm(String writtenForm) {
        if (this.isNumeric(writtenForm)) {
            return false;
        } else if (writtenForm.equals("also")) {
            return false;
        } else {
            return true;
        }
    }

    private String modify(String writtenForm) {
        if (writtenForm.contains("_")) {
            writtenForm = writtenForm.replace("_", " ");
            return writtenForm;
        }
        return writtenForm;
    }
}
