import com.apicatalog.rdf.Rdf;
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
import de.citec.sc.lemon.io.LexiconSerialization;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFWriterBuilder;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Filter;
import java.util.stream.Collectors;

import static de.citec.generator.config.Constants.UNDERSCORE;
import static de.citec.generator.config.Constants.scriptName;

public class Test {
    public static void main(String[] args) throws Exception {

        ConfigLemon lemon = new ObjectMapper().readValue(new File("/home/mokrane/BachelorArbeit/ontology-lexicalization_de/inputLemon.json"), ConfigLemon.class);
        Lexicon turtleLexicon = new Lexicon(lemon.getUri_basic());
        new ProcessCsv("/home/mokrane/BachelorArbeit/ontology-lexicalization_de/src/main/java/", "test", lemon);
        LexiconSerialization serializer = new LexiconSerialization();
        Model model = ModelFactory.createDefaultModel();
        serializer.serialize(turtleLexicon, model);
        System.out.println("lemon creating ends!! ");
        String filePath = System.getProperty("user.dir") + "/test_tmp.json";
        String input= "im US-ReprÃ¤sentantenhaus";

        try( FileOutputStream fos = new FileOutputStream(filePath)){
            fos.write(RDFWriterBuilder.create().source(model)
                    .lang(Lang.JSONLD).asString() .getBytes(StandardCharsets.ISO_8859_1));}
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    static class Test2 {
        public static void main(String[] args) throws IOException {
            String input = "im US-ReprÃ¤sentantenhaus";
            //StringWriter stringWriter = new StringWriter();
            //model.write(new PrintWriter(System.out,StandardCharset.UTF_8));
            StringWriter stringWriter = new StringWriter();
            String filePath = System.getProperty("user.dir") + "/test_tmp.json";
            System.out.println(filePath);
            PrintWriter pw = new PrintWriter(filePath, StandardCharsets.UTF_8);
            //model.write(new PrintWriter(System.out));
            //DatasetGraph g = DatasetFactory.wrap(model).asDatasetGraph();
            //JsonLDWriteContext ctx = new JsonLDWriteContext();
            //model.write(stringWriter,"JSON-LD");
            //ByteArrayOutputStream out = new ByteArrayOutputStream(input);
            //OutputStreamWriter out =new OutputStreamWriter(new FileOutputStream("temp.txt"), StandardCharsets.UTF_8);
            //Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
            //OutputStreamWriter osw = new OutputStreamWriter(out, StandardCharsets.ISO_8859_1);
            //OutputStreamWriter osw = new OutputStreamWriter(fos,
            //       StandardCharsets.UTF_8);
            //model.write(osw, "JSON-LD");

            //RDFDataMgr.write(osw, model, type);
            //String jsonLDString = stringWriter.toString();
            //stringWriter.close();
            //return jsonLDString;
            //System.out.println("Test: "+out);
        }
    }
}
