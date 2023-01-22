import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.citec.generator.config.ConfigLex;
import de.citec.sc.generator.analyzer.PosAnalyzer;
import de.citec.sc.generator.analyzer.TextAnalyzer;
import de.citec.sc.generator.utils.PropertyCSV;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.apache.jena.atlas.lib.Chars;

import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Test {
    public static void main(String[] args) throws Exception {
        String text = "grün, gelb, heiß";
        String test = "KÃ\u0083Â¶nigreich";
        //System.out.println(Charset.defaultCharset().displayName());
        //System.out.println(Cha.decode(test, StandardCharsets.US_ASCII));
        System.out.println(
                new String(new String(test.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)
                        .getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
    }
}
