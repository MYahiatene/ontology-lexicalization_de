/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.generator.core;

import de.citec.sc.generator.analyzer.PosAnalyzer;
import de.citec.sc.generator.analyzer.StopWords;
import de.citec.sc.generator.analyzer.TextAnalyzer;

import static de.citec.sc.generator.analyzer.TextAnalyzer.POS_TAGGER_WORDS;

import de.citec.sc.generator.utils.Pair;
import de.citec.sc.generator.utils.PropertyCSV;
import de.citec.sc.generator.utils.StopWordRemoval;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.HashSet;

import de.citec.generator.config.PredictionPatterns;

/**
 * @author elahi
 */
//,Comparator
public class LineInfo implements PredictionPatterns {

    private String line = null;
    private String subject = "e";
    private String predicate = "p";
    private String object = "o";
    private String subjectOriginal = "e";
    private String predicateOriginal = "p";
    private String objectOriginal = "o";
    private String posTag = null;
    private String fullPosTag = null;
    private String rule = null;
    private String word = null;
    private String wordOriginal = null;
    private String className = null;
    private String predictionRule = null;
    private String subjectObjectOrder = null;
    private Boolean validFlag = false;
    private Integer nGramNumber = 0;
    private static String http = "http://dbpedia.org/resource/";
    private static String ONTOLOGY = "ontology";
    private static String PROPERTY = "property";
    private String checkedAssociationRule = null;
    private Double checkedAssociationRuleValue = null;


    private Map<String, Double> probabilityValue = new TreeMap<String, Double>();
    private PosAnalyzer analyzer = null;
    public static String CHECK_THRESOLD_VALUE = "CHECK_THRESOLD_VALUE";

    //supA=93, supB=115, supAB=93, condBA=1, condAB=0.808, AllConf=0.808, Coherence=0.447, Cosine=0.899, Kulczynski=0.904, MaxConf=1, IR=0.191


    public LineInfo() {

    }

    public LineInfo(Integer index, String[] row, String prediction, String interestingness, PropertyCSV propertyCSV) throws Exception {
        String string = "";
        if (row.length < propertyCSV.getStringIndex()) {
            this.validFlag = false;
            //LOGGER.log(Level.INFO, "line No ::" + index + " line does not work!!!!!!!!!!");
            return;
        }

        try {
            string = row[propertyCSV.getStringIndex()];
            //string=string.replace(",", "$");
            //System.out.println("string::"+string);
        } catch (ArrayIndexOutOfBoundsException ex) {
            validFlag = false;
            return;
        }
        this.line = string;
        if (line.contains("http://www.w3.org/2001/XMLSchema#integer")) {
            line = "http://www.w3.org/2001/XMLSchema#integer";
        }

        this.className = setClassName(row[propertyCSV.getClassNameIndex()]);
        this.setSubject(row[propertyCSV.getSubjectIndex()]);
        this.setProperty(row[propertyCSV.getPredicateIndex()]);
        this.setObject(row[propertyCSV.getObjectIndex()]);
        if (!isKBValid()) {
            this.validFlag = false;
            return;
        }

        this.wordOriginal = row[propertyCSV.getLinguisticPatternIndex()];


        if (wordOriginal != null) {
            this.validFlag = true;
        }
        this.nGramNumber = this.setNGram(row, propertyCSV.getPatterntypeIndex());

        if (this.validFlag) {
            String str = this.processWords(this.wordOriginal);
            this.getPosTag(str);
            this.setRule();
            this.setProbabilityValue(index, interestingness, row, propertyCSV);
        }
    }


    private Integer setNGram(String[] row, Integer patterntypeIndex) {
        String patternType = row[patterntypeIndex];
        return 5;

    }

    private void setProbabilityValue(Integer index, String interestingness, String[] row, PropertyCSV propertyCSV) {
        Double givenSupA, givenSupB, givenSupAB, givenCondAB, givenCondBA, givenAllConf, givenCoherence, givenCosine, givenIR, givenKulczynski, givenMaxConf;

        try {
            givenSupA = Double.parseDouble(row[propertyCSV.getSupAIndex()]);
            givenSupB = Double.parseDouble(row[propertyCSV.getSupBIndex()]);
            givenSupAB = Double.parseDouble(row[propertyCSV.getSupABIndex()]);
            givenCondAB = Double.parseDouble(row[propertyCSV.getCondABIndex()]);
            givenCondBA = Double.parseDouble(row[propertyCSV.getCondBAIndex()]);
            givenAllConf = Double.parseDouble(row[propertyCSV.getAllConfIndex()]);
            givenCoherence = Double.parseDouble(row[propertyCSV.getCoherenceIndex()]);
            givenCosine = Double.parseDouble(row[propertyCSV.getCosineIndex()]);
            givenIR = Double.parseDouble(row[propertyCSV.getIRIndex()]);
            givenKulczynski = Double.parseDouble(row[propertyCSV.getKulczynskiIndex()]);
            givenMaxConf = Double.parseDouble(row[propertyCSV.getMaxConfIndex()]);
            this.probabilityValue.put(supA, givenSupA);
            this.probabilityValue.put(supB, givenSupB);
            this.probabilityValue.put(supAB, givenSupAB);
            this.probabilityValue.put(condAB, givenCondAB);
            this.probabilityValue.put(condBA, givenCondBA);
            if (interestingness.contains(AllConf)) {
                this.probabilityValue.put(AllConf, givenAllConf);
            } else if (interestingness.contains(Cosine)) {
                this.probabilityValue.put(Cosine, givenCosine);
            } else if (interestingness.contains(Coherence)) {
                this.probabilityValue.put(Coherence, givenCoherence);
            } else if (interestingness.contains(Kulczynski)) {
                this.probabilityValue.put(Kulczynski, givenKulczynski);
            } else if (interestingness.contains(MaxConf)) {
                this.probabilityValue.put(MaxConf, givenMaxConf);
            } else if (interestingness.contains(IR)) {
                this.probabilityValue.put(IR, givenIR);
            }
            this.checkedAssociationRule = interestingness;
            this.checkedAssociationRuleValue = Double.valueOf(this.probabilityValue.get(interestingness));
        } catch (Exception ex) {
            this.validFlag = false;
        }

    }


    private void setRule() {
        String str = "[" + this.line.replace("|", "]");
        str = StringUtils.substringBetween(str, "[", "]");
        this.rule = str;
    }


    private void setObject(String object) {
        if (object.isEmpty()) {
            this.objectOriginal = "o";
            this.object = "o";
        } else {
            this.objectOriginal = object;
            this.object = object;
        }
    }

    private void setProperty(String property) {
        if (property.isEmpty()) {
            this.predicateOriginal = "p";
            this.predicate = "p";
        } else {
            this.predicateOriginal = property;
            this.predicate = property;
        }
    }

    private void setSubject(String subject) {
        if (subject.isEmpty()) {
            this.subjectOriginal = "e";
            this.subject = "e";
        } else {
            this.subjectOriginal = subject;
            this.subject = subject;
        }

    }


    private String processWords(String nGram) throws Exception {
        StringTokenizer st = new StringTokenizer(nGram);
        String str = "";
        while (st.hasMoreTokens()) {
            String tokenStr = st.nextToken();
            if (this.isStopWord(tokenStr)) {
                continue;
            }

            String line = tokenStr + "_";
            str += line;
        }
        str = str.replace("_", " ");
        str = str.trim().stripTrailing();

        return str;
    }

    private void getPosTag(String word) throws Exception {
        analyzer = new PosAnalyzer(word, POS_TAGGER_WORDS, 5);
        if (!analyzer.getNouns().isEmpty()) {
            this.posTag = PosAnalyzer.NOUN;
        } else if (!analyzer.getAdjectives().isEmpty()) {
            this.posTag = PosAnalyzer.ADJECTIVE;
        } else if (!analyzer.getVerbs().isEmpty()) {
            this.posTag = PosAnalyzer.VERB;
        } else {
            this.posTag = PosAnalyzer.NOUN;
        }

        if (analyzer.posTaggerText(word)) {
            this.fullPosTag = analyzer.getFullPosTag();
        }
        this.word = word.trim().strip();
    }

    private String correct(String string) {
        return string.trim().strip();
    }


    public String getPosTag() {
        return posTag;
    }

    public Integer getnGramNumber() {
        return nGramNumber;
    }

    public PosAnalyzer getAnalyzer() {
        return analyzer;
    }

    public String getRule() {
        return rule;
    }

    public String getWord() {
        return word;
    }

    public Double getProbabilityValue(String key) {
        return probabilityValue.get(key);
    }

    public String getLine() {
        return line;
    }

    public String getSubject() {
        return subject;
    }

    public String getPredicate() {
        return predicate;
    }

    public String getObject() {
        return object;
    }

    public Map<String, Double> getProbabilityValue() {
        return probabilityValue;
    }

    public String getWordOriginal() {
        return wordOriginal;
    }

    public String getClassName() {
        return className;
    }

    public Boolean getValidFlag() {
        return validFlag;
    }

    public String getPartOfSpeech() {
        return this.posTag;
    }

    private Boolean isStopWord(String tokenStr) throws IOException {
        StopWords stopWords = new StopWords();
        tokenStr = tokenStr.toLowerCase().trim().strip();
        if (stopWords.getGermanStopWords().contains(tokenStr)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "LineInfo{" + ", subjectOriginal=" + subjectOriginal + ", predicateOriginal=" + predicateOriginal + ", objectOriginal=" + objectOriginal + ", posTag=" + posTag + ", word=" + word + ", wordOriginal=" + wordOriginal + ", className=" + className + ", predictionRule=" + predictionRule + ", subjectObjectOrder=" + subjectObjectOrder + ", validFlag=" + validFlag + ", nGramNumber=" + nGramNumber;
    }


    private String setClassName(String className) {
        String prefix = null;
        if (className.contains("/")) {
            className = className.replace("http://dbpedia.org/ontology/", "");
        }

        return className;
    }

    public String getCheckedAssociationRule() {
        return checkedAssociationRule;
    }

    public String getSubjectOriginal() {
        return subjectOriginal;
    }

    public String getPredicateOriginal() {
        return predicateOriginal;
    }

    public String getObjectOriginal() {
        return objectOriginal;
    }


    private boolean isKBValid() {
        if (this.object != null) {
            if (this.object.strip().trim().contains("http://www.w3.org/2001/XMLSchema#date")) {
                return false;
            }
        }
        if (this.predicate != null) {
            if (this.predicate.strip().trim().contains("date")) {
                return false;
            }
        }
        return true;
    }

    public String getFullPosTag() {
        return fullPosTag;
    }


    public String getPreposition() {
        return "of";
    }


    public Double getCheckedAssociationRuleValue() {
        return checkedAssociationRuleValue;
    }


}
