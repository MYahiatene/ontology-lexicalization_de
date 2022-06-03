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
    //Todo: Negra Corpus Pos Tags for german
    //TODO: Make it for several languages, check pos tags for other languages
    //TODO: read available postags from file
/*
ADJA    attributives Adjektiv                   [das] große [Haus]
ADJD    adverbiales oder                        [er fährt] schnell
        prädikatives Adjektiv                   [er ist] schnell

ADV     Adverb                                  schon, bald, doch

APPR    Präposition; Zirkumposition links       in [der Stadt], ohne [mich]
APPRART Präposition mit Artikel                 im [Haus], zur [Sache]
APPO    Postposition                            [ihm] zufolge, [der Sache] wegen
APZR    Zirkumposition rechts                   [von jetzt] an

ART     bestimmter oder                         der, die, das,
        unbestimmter Artikel                    ein, eine, ...

CARD    Kardinalzahl                            zwei [Männer], [im Jahre] 1994

FM      Fremdsprachliches Material              [Er hat das mit ``]
                                                A big fish ['' übersetzt]

ITJ     Interjektion                            mhm, ach, tja

ORD     Ordinalzahl                             [der] neunte [August]

KOUI    unterordnende Konjunktion               um [zu leben],
        mit ``zu'' und Infinitiv                anstatt [zu fragen]
KOUS    unterordnende Konjunktion               weil, daß, damit,
        mit Satz                                wenn, ob
KON     nebenordnende Konjunktion               und, oder, aber
KOKOM   Vergleichskonjunktion                   als, wie

NN      normales Nomen                          Tisch, Herr, [das] Reisen
NE      Eigennamen                              Hans, Hamburg, HSV

PDS     substituierendes Demonstrativ-          dieser, jener
        pronomen
PDAT    attribuierendes Demonstrativ-           jener [Mensch]
        pronomen

PIS     substituierendes Indefinit-             keiner, viele, man, niemand
        pronomen
PIAT    attribuierendes Indefinit-              kein [Mensch],
        pronomen ohne Determiner                irgendein [Glas]
PIDAT   attribuierendes Indefinit-              [ein] wenig [Wasser],
        pronomen mit Determiner                 [die] beiden [Brüder]

PPER    irreflexives Personalpronomen           ich, er, ihm, mich, dir

PPOSS   substituierendes Possessiv-             meins, deiner
        pronomen
PPOSAT  attribuierendes Possessivpronomen       mein [Buch], deine [Mutter]

PRELS   substituierendes Relativpronomen        [der Hund ,] der
PRELAT  attribuierendes Relativpronomen         [der Mann ,] dessen [Hund]

PRF     reflexives Personalpronomen             sich, einander, dich, mir

PWS     substituierendes                        wer, was
        Interrogativpronomen
PWAT    attribuierendes                         welche [Farbe],
        Interrogativpronomen                    wessen [Hut]
PWAV    adverbiales Interrogativ-               warum, wo, wann,
        oder Relativpronomen                    worüber, wobei

PAV     Pronominaladverb                        dafür, dabei, deswegen, trotzdem

PTKZU   ``zu'' vor Infinitiv                    zu [gehen]
PTKNEG  Negationspartikel                       nicht
PTKVZ   abgetrennter Verbzusatz                 [er kommt] an, [er fährt] rad
PTKANT  Antwortpartikel                         ja, nein, danke, bitte
PTKA    Partikel bei Adjektiv                   am [schönsten],
        oder Adverb                             zu [schnell]

SGML    SGML Markup

SPELL   Buchstabierfolge                        S-C-H-W-E-I-K-L

TRUNC   Kompositions-Erstglied                  An- [und Abreise]

VVFIN   finites Verb, voll                      [du] gehst, [wir] kommen [an]
VVIMP   Imperativ, voll                         komm [!]
VVINF   Infinitiv, voll                         gehen, ankommen
VVIZU   Infinitiv mit ``zu'', voll              anzukommen, loszulassen
VVPP    Partizip Perfekt, voll                  gegangen, angekommen
VAFIN   finites Verb, aux                       [du] bist, [wir] werden
VAIMP   Imperativ, aux                          sei [ruhig !]
VAINF   Infinitiv, aux                          werden, sein
VAPP    Partizip Perfekt, aux                   gewesen
VMFIN   finites Verb, modal                     dürfen
VMINF   Infinitiv, modal                        wollen
VMPP    Partizip Perfekt, modal                 gekonnt, [er hat gehen] können

XY      Nichtwort, Sonderzeichen                3:7, H2O,
        enthaltend                              D2XW3

\$,     Komma                                   ,
\$.     Satzbeendende Interpunktion             . ? ! ; :
\$(     sonstige Satzzeichen; satzintern        - [,]()
 */
    String POS_TAGGER_WORDS = "POS_TAGGER_WORDS";
    String ADJECTIVE = "ADJ";
    String NOUN = "N";
    String VERB = "V";
    String PRONOUN = "P";

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
