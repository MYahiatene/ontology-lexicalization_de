/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.generator.utils;

import de.citec.sc.generator.analyzer.PosAnalyzer;
import de.citec.sc.generator.analyzer.StopWords;
import de.citec.sc.generator.analyzer.TextAnalyzer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * @author elahi
 */
public class StopWordRemoval {
    private final List<String> stopWords;

    public StopWordRemoval() throws IOException {
        this.stopWords = new StopWords().getGermanStopWords();
    }

    public String deleteStopWord(String nGramStr) {
        String tokenStr = "";
        if (nGramStr.contains("_")) {
            String[] tokens = nGramStr.split("_");
            for (String token : tokens) {
                if (this.stopWords.contains(token)) {
                    continue;
                } else {
                    String line = token;
                    tokenStr += line + "_";
                }

            }
            int length = tokenStr.length() - 1;
            if (length > 1) {
                return tokenStr.substring(0, tokenStr.length() - 1);
            } else {
                return tokenStr;
            }

        } else {
            return nGramStr;
        }

    }

    public void main(String[] args) {
        String string = "a_australian";
        String modString = deleteStopWord(string);
        //System.out.println("modString:"+modString);

    }

}
