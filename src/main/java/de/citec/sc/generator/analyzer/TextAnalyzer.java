/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.generator.analyzer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.ArrayList;

/**
 * @author elahi
 */
public interface TextAnalyzer {
    //TODO: Make it for several languages, check pos tags for other languages
    //TODO: read available postags from file
/*
ADJ – ADP – ADV – AUX – CCONJ – DET – INTJ – NOUN – NUM – PART – PRON – PROPN – PUNCT – SCONJ – SYM – VERB – X  , MISC?
 */
    String POS_TAGGER_WORDS = "POS_TAGGER_WORDS";
    String ADJECTIVE = "ADJ";
    String NOUN = "NOUN";
    String VERB = "VERB";
    String PRONOUN = "PRON";

    String OBJECT = "object";


    List<String> ENGLISH_STOPWORDS = Arrays.asList("of", "i", "me", "my", "myself", "we", "our", "ours",
            "ourselves", "you", "your", "yours", "yourself",
            "yourselves", "he", "him", "his", "himself", "she",
            "her", "hers", "herself", "it", "its", "itself", "they",
            "them", "their", "theirs", "themselves", "what", "which",
            "who", "whom", "this", "that", "these", "those", "am",
            "is", "are", "was", "were", "be", "been", "being", "have",
            "has", "had", "having", "do", "does", "did", "doing", "a", "an",
            "the", "and", "but", "if", "or", "because", "as", "until", "while",
            "at", "by", "for", "with", "about", "against", "between", "into",
            "through", "during", "before", "after", "above", "below", "to", "from",
            "up", "down", "in", "out", "on", "off", "over", "under", "again",
            "further", "then", "once", "here", "there", "when", "where", "why",
            "how", "all", "any", "both", "each", "few", "more", "most", "other",
            "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than",
            "too", "very", "s", "t", "can", "will", "just", "don", "should", "now", "un", "ein", "und", "il", "est", "ist", " né", "à", "january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december");


    String resources = "src/main/resources/";
    String modelDir = resources + "models/";
    String posTagFile = "de-pos-maxent.bin";
    String lemmaDictionary = "en-lemmatizer.txt";

}
