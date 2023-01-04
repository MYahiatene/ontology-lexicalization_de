import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.Properties;

public class Test {
    public static void main(String[] args) {
       /* Properties props = new Properties();
        props.setProperty("annotators", "tokenize, mwt, pos");
        props.put("tokenize.language", "de");
        props.put("tokenize.postProcessor","edu.stanford.nlp.international.german.process.GermanTokenizerPostProcessor"
        );
        props.put("pos.model", "edu/stanford/nlp/models/pos-tagger/german-ud.tagger");
        props.put("mwt.mappingFile", "edu/stanford/nlp/models/mwt/german/german-mwt.tsv");*/

        //StanfordCoreNLP nlp = new StanfordCoreNLP(props);
        StanfordCoreNLP nlp = new StanfordCoreNLP("german");

        System.out.println(nlp.getProperties());
        CoreDocument doc = nlp.processToCoreDocument("Ich war gestern besoffen und hatte einen großen blauen Fleck und trank Wein.");
        doc.sentences().forEach(e -> System.out.println(e.text()));
        doc.sentences().forEach(e -> System.out.println(e.posTags()));
    }
}
