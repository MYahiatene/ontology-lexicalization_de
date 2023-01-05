import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.citec.generator.config.ConfigLex;
import de.citec.sc.generator.analyzer.PosAnalyzer;
import de.citec.sc.generator.analyzer.TextAnalyzer;
import de.citec.sc.generator.utils.PropertyCSV;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.Properties;

public class Test {
    public static void main(String[] args) throws Exception {
       /* Properties props = new Properties();
        props.setProperty("annotators", "tokenize, mwt, pos");
        props.put("tokenize.language", "de");
        props.put("tokenize.postProcessor","edu.stanford.nlp.international.german.process.GermanTokenizerPostProcessor"
        );
        props.put("pos.model", "edu/stanford/nlp/models/pos-tagger/german-ud.tagger");
        props.put("mwt.mappingFile", "edu/stanford/nlp/models/mwt/german/german-mwt.tsv");*/

        //StanfordCoreNLP nlp = new StanfordCoreNLP(props);
        StanfordCoreNLP nlp = new StanfordCoreNLP("german");
        PosAnalyzer analyzer = new PosAnalyzer("Ich war gestern besoffen und hatte einen großen blauen Fleck und trank Wein.", TextAnalyzer.POS_TAGGER_WORDS, 5,
                new PropertyCSV());
        System.out.println(analyzer.getFullPosTag());
        System.out.println(analyzer.getNouns());
        System.out.println(analyzer.getAdjectives());
        System.out.println(analyzer.getVerbs());
        CoreDocument doc = PosAnalyzer.getNlp().processToCoreDocument("Ich war gestern besoffen und hatte einen großen blauen Fleck und trank Wein.");
        doc.sentences().forEach(e -> e.posTags().forEach(System.out::println));
        // System.out.println(nlp.getProperties());
        //CoreDocument doc = nlp.processToCoreDocument("Ich war gestern besoffen und hatte einen großen blauen Fleck und trank Wein.");
        //doc.sentences().forEach(e -> System.out.println(e.text()));
        //doc.sentences().forEach(e -> System.out.println(e.posTags()));
    }
}
