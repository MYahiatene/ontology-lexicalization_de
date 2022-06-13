import com.fasterxml.jackson.databind.ObjectMapper;
import de.citec.generator.config.ConfigLemon;
import de.citec.generator.core.LemonCreator;
import de.citec.generator.core.LineInfo;
import de.citec.generator.core.ProcessCsv;
import de.citec.sc.generator.utils.CsvFile;
import de.citec.sc.generator.utils.FileFolderUtils;
import de.citec.sc.generator.utils.PropertyCSV;
import de.citec.sc.generator.utils.StopWordRemoval;
import de.citec.sc.lemon.core.Lexicon;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static de.citec.generator.config.Constants.UNDERSCORE;

public class Test {
    public static void main(String[] args) throws Exception {

        ConfigLemon lemon = new ObjectMapper().readValue(new File("/home/mokrane/BachelorArbeit/ontology-lexicalization_de/inputLemon.json"), ConfigLemon.class);
        Lexicon turtleLexicon = new Lexicon(lemon.getUri_basic());
        //BufferedReader br = FileFolderUtils.getBufferedReaderForCompressedFile(new File("src/main/java/predict_l_for_s_given_po-.csv.bz2"));
        //br.readLine();
        Map<String, List<LineInfo>> lineLexicon = new TreeMap<>();
        ProcessCsv csv = new ProcessCsv("/home/mokrane/BachelorArbeit/ontology-lexicalization_de/src/main/java/", "test", lemon);
        /*List<File> files = new ArrayList<>();
        files.add(new File("src/main/java/predict_l_for_s_given_po-.csv.bz2"));
        File classFile = new File("src/main/java/predict_l_for_s_given_po-.csv.bz2");
        CsvFile csvFile = new CsvFile(new File("src/main/java/predict_l_for_s_given_po-.csv.bz2"));
        List<String[]> rows = csvFile.getRowsManual();
        PropertyCSV propertyCSV = new PropertyCSV();
        int numberOfClass = 1;
        String className = classFile.getName().replace("http%3A%2F%2Fdbpedia.org%2Fontology%2F", "");
        Integer index = 0;
        for (String[] row : rows) {

            LineInfo lineInfo = new LineInfo(index, row, "prediction", "cosine", propertyCSV);

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
                String nGram = isValidWord(lineInfo.getWord(), lineInfo.getnGramNumber());

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
        LemonCreator lexicon = new LemonCreator("src/main/resources", turtleLexicon, 20);
        lexicon.preparePropertyLexicon("test", "src/main/resources", className, "cosine", lineLexicon);
*/
    }

    private static String isValidWord(String word, Integer nGramNumber) throws IOException {
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
}
