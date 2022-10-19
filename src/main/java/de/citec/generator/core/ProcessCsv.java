package de.citec.generator.core;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author elahi
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import de.citec.sc.generator.analyzer.StopWords;
import de.citec.sc.generator.utils.FileFolderUtils;
import de.citec.sc.generator.utils.StopWordRemoval;
import de.citec.sc.generator.utils.PropertyCSV;
import de.citec.sc.generator.utils.CsvFile;
import de.citec.generator.config.LemonConstants;
import de.citec.sc.generator.analyzer.Lemmatizer;
import de.citec.generator.config.ConfigDownload;
import de.citec.generator.config.ConfigLemon;
import de.citec.generator.config.ConfigLex;

import static de.citec.generator.config.Constants.UNDERSCORE;

import java.io.File;
import java.io.IOException;
import java.util.*;

import de.citec.sc.lemon.core.Lexicon;

import java.util.logging.Logger;

import de.citec.generator.config.PredictionPatterns;

/**
 *
 * @author elahi
 */
public class
ProcessCsv implements PredictionPatterns, LemonConstants {

    private Lexicon turtleLexicon = null;
    private Integer rankLimit = 0;
    private Logger LOGGER = Logger.getLogger(ProcessCsv.class.getName());
    //private Lemmatizer lemmatizer = new Lemmatizer();


    public ProcessCsv(String baseDir, String resourceDir, ConfigLemon config) throws Exception {
        this.turtleLexicon = new Lexicon(config.getUri_basic());
        this.rankLimit = config.getRank_limit();
        Set<String> posTag = new HashSet<>();
        // todo: more pos tags for german
        posTag.add("JJ");
        posTag.add("NN");
        posTag.add("VB");
        String outputDir = resourceDir;

        List<String> predictKBGivenLInguistic = new ArrayList<>(Arrays.asList(
                predict_l_for_s_given_po,
                predict_localized_l_for_s_given_po,
                predict_l_for_s_given_p,
                predict_localized_l_for_s_given_p,
                predict_l_for_s_given_o,
                predict_l_for_o_given_sp,
                predict_localized_l_for_o_given_sp,
                predict_l_for_o_given_s,
                predict_l_for_o_given_p,
                predict_localized_l_for_o_given_p,
                predict_p_for_s_given_l,
                predict_o_for_s_given_l,
                predict_p_for_o_given_l,
                predict_po_for_s_given_l,
                predict_s_for_o_given_l,
                predict_po_for_s_given_localized_l,
                predict_p_for_s_given_localized_l,
                predict_p_for_o_given_localized_l,
                predict_sp_for_o_given_localized_l,
                predict_sp_for_o_given_l
        ));

        List<String> interestingness = new ArrayList<>();
        interestingness.add(Cosine);
        for (String prediction : predictKBGivenLInguistic) {
            String inputDir = baseDir + "/";
            for (String inter : interestingness) {
                outputDir = resourceDir + "/" + prediction + "/" + inter + "/";
                FileFolderUtils.createDirectory(outputDir);
                this.generate(inputDir, outputDir, prediction, inter, LOGGER, ".csv");
            }
        }


    }

    public void generate(String rawFileDir, String outputDir, String prediction, String givenInterestingness, Logger givenLOGGER, String fileType) throws Exception {

        List<File> files = FileFolderUtils.getSpecificFiles(rawFileDir, prediction + "-", ".csv");
        if (!files.isEmpty()) {
            createExperimentLinesCsv(outputDir, prediction, givenInterestingness, files);
        } else {
            throw new Exception("NO ontology lexicalization files are found for processing" + ". " + "Run lexicalization process first");
        }
    }

    private void createExperimentLinesCsv(String outputDir, String prediction, String interestingness, List<File> classFiles) throws Exception {

        List<String[]> rows = new ArrayList<>();
        Integer numberOfClass = 0;
        Integer maximumNumberOflines = 300000;

        for (File classFile : classFiles) {
            Map<String, List<LineInfo>> lineLexicon = new TreeMap<>();
            String fileName = classFile.getName();
            CsvFile csvFile = new CsvFile(classFile);
            rows = csvFile.getRowsManual();
            PropertyCSV propertyCSV = new PropertyCSV();
            numberOfClass = numberOfClass + 1;
            String className = classFile.getName().replace("http%3A%2F%2Fdbpedia.org%2Fontology%2F", "");
            Integer index = 0;
            for (String[] row : rows) {

                LineInfo lineInfo = new LineInfo(index, row, prediction, interestingness, propertyCSV);

                if (lineInfo.getLine() != null) {
                    if (lineInfo.getLine().contains("XMLSchema#integer") || lineInfo.getLine().contains("XMLSchema#gYear")) {
                        continue;
                    } else if (lineInfo.getProbabilityValue().isEmpty()) {
                        continue;
                    } else if (!lineInfo.getValidFlag()) {
                        continue;
                    }

                }


                try {
                    String nGram = this.isValidWord(lineInfo.getWord(), lineInfo.getnGramNumber());

                    if (nGram != null) {
                        List<LineInfo> results = new ArrayList<>();
                        if (lineLexicon.containsKey(nGram)) {
                            results = lineLexicon.get(nGram);
                            results.add(lineInfo);
                            lineLexicon.put(nGram, results);
                        } else {
                            results.add(lineInfo);
                            lineLexicon.put(nGram, results);

                        }
                    }

                } catch (Exception ignored) {
                }

            }
            LemonCreator lexicon = new LemonCreator(outputDir, turtleLexicon, rankLimit);
            lexicon.preparePropertyLexicon(prediction, outputDir, className, interestingness, lineLexicon);

        }

    }

    private String isValidWord(String word, Integer nGramNumber) throws IOException {
        String nGram = word;
        StopWordRemoval stopWordRemoval = new StopWordRemoval();
        nGram = nGram.replace("\"", "");
        nGram = nGram.toLowerCase().trim().strip();
        nGram = nGram.replaceAll(" ", "_");
        nGram = stopWordRemoval.deleteStopWord(nGram);
        nGram = nGram.replaceAll("_", UNDERSCORE);
        nGram = nGram.replaceAll("[^A-Za-z0-9]", "");
        nGram = nGram.replace(UNDERSCORE, "_");

        if (nGram.contains("_")) {
            if ((nGram.split("_").length > 2)) {
                return null;
            } else {
                return nGram;
            }
        } else if (nGram.length() > 2) {
            return nGram;
        }

        return null;
    }


    public Lexicon getTurtleLexicon() {
        return turtleLexicon;
    }


}
