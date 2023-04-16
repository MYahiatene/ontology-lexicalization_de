/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.generator.restservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.citec.generator.config.ConfigDownload;
import de.citec.generator.config.ConfigLemon;
import de.citec.generator.config.ConfigLex;
import de.citec.generator.config.Constants;
import de.citec.generator.core.PerlQuery;
import de.citec.generator.core.ProcessCsv;
import de.citec.generator.results.ResultDownload;
import de.citec.generator.results.ResultLex;
import de.citec.sc.generator.exceptions.ConfigException;
import de.citec.sc.generator.exceptions.PerlException;
import de.citec.sc.generator.utils.FileFolderUtils;
import de.citec.sc.generator.utils.graphextractor.GraphExtractor;
import de.citec.sc.generator.utils.ProgressSingleton;
import de.citec.sc.lemon.core.Lexicon;
import de.citec.sc.lemon.io.LexiconSerialization;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.*;
import org.springframework.web.client.RestTemplate;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

/**
 * @author elahi
 */
public class ResponseTransfer implements Constants {
    public ResultLex lexicalization(ConfigLex config) {
        String className = null;
        try {
            FileFolderUtils.delete(new File(interDir));
            FileFolderUtils.delete(new File(resultDir));
            String class_url_original = config.getClass_url_original();
            String class_url = config.getClass_url();
            config.setClass_url(class_url_original);
            String configStr = new ObjectMapper().writeValueAsString(config);
            String langTag = config.getLangTag();
            PerlQuery perlQuery = new PerlQuery(perlDir, scriptName, class_url, langTag, configStr);
            Boolean flag = perlQuery.getProcessSuccessFlag();
            System.out.println("Lexicalization process successfuly ended!!");
            return new ResultLex(className, flag);

        } catch (ConfigException ex) {
            Logger.getLogger(ResponseTransfer.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(ex.getMessage());
            return new ResultLex(className, "Configuration file is correct.");

        } catch (PerlException ex) {
            Logger.getLogger(ResponseTransfer.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(ex.getMessage());
            return new ResultLex(className, false);

        } catch (IOException ex) {
            Logger.getLogger(ResponseTransfer.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("writing to file failed!!" + ex.getMessage());
            return new ResultLex(className, false);

        } catch (Exception ex) {
            Logger.getLogger(ResponseTransfer.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("System output process does not work!!" + ex.getMessage());
            return new ResultLex(className, false);
        }

    }

    public String createLemon(ConfigLemon config, String clazz) {
        try {
            ProgressSingleton.getInstance();
            String resourceDir = resultDir + processData;
            Lexicon turtleLexicon = new ProcessCsv(resultDir, resourceDir, config).getTurtleLexicon();
            LexiconSerialization serializer = new LexiconSerialization();
            Model model = ModelFactory.createDefaultModel();
            serializer.serialize(turtleLexicon, model);
            String filePostfix = "_" + clazz + ".json";
            System.out.println("lemon creating ends!! ");
            String filePath = System.getProperty("user.dir") + "/result" + filePostfix;
            String result = this.writeJsonLDtoString(model, filePath);
            return result;
        } catch (JsonProcessingException ex) {
            Logger.getLogger(ResponseTransfer.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Json creation fails!!" + ex.getMessage());
            return defaultResult;

        } catch (IOException ex) {
            Logger.getLogger(ResponseTransfer.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("writing to file failed!!" + ex.getMessage());
            return defaultResult;

        } catch (Exception ex) {
            Logger.getLogger(ResponseTransfer.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("System output process does not work!!" + ex.getMessage());
            return defaultResult;
        }

    }


    private void initializeLang() {
        // todo: lang independent
        RestTemplate template = new RestTemplate();
        template.getForEntity("http://nlp/de", String.class);
        //template.getForEntity("http://0.0.0.0:80/de", String.class);
    }


    private String writeJsonLDtoString(Model model, String fileName) throws IOException {

        String modelToString = RDFWriterBuilder.create().source(model)
                .lang(Lang.JSONLD).asString();
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            //todo: check encoding probably model or serializer in the step before breaks encoding
            fos.write(modelToString.getBytes(ISO_8859_1));
        } catch (IOException e) {
            Logger.getLogger(ResponseTransfer.class.getName()).log(Level.SEVERE, null, e);
        }
        //todo: check encoding probably model or serializer in the step before breaks encoding
        return new String(modelToString.getBytes(ISO_8859_1));
    }


    public ResultDownload downloadData(ConfigDownload conf) {
        URL url;
        System.out.println(conf);
        try {
            url = new URL(conf.getUri_abstract());
            Path path = Paths.get("/home/elahi/a-teanga/dockerTest/ontology-lexicalization/app/");
            Files.copy(url.openStream(), path);
            return new ResultDownload(conf.getUri_abstract(), "successfully download the file!");
        } catch (MalformedURLException ex) {
            Logger.getLogger(ResponseTransfer.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(ex.getMessage());
            return new ResultDownload(conf.getUri_abstract(), "failed to download turtle files!");
        } catch (FileAlreadyExistsException ex) {
            Logger.getLogger(ResponseTransfer.class.getName()).log(Level.SEVERE, null, ex);
            return new ResultDownload(conf.getUri_abstract(), "File already exists!!");
        } catch (IOException ex) {
            Logger.getLogger(ResponseTransfer.class.getName()).log(Level.SEVERE, null, ex);
            return new ResultDownload(conf.getUri_abstract(), "failed to download turtle files!");
        }

    }

    String searchLemon(ConfigDownload conf) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    void lexicalizationAndLemonCreate() throws IOException, URISyntaxException {
        List<String> classesList = Arrays.asList(/*"http://dbpedia.org/ontology/Place",
                "http://dbpedia.org/ontology/Director",
                "http://dbpedia.org/ontology/Actor", "http://dbpedia.org/ontology/Politician",
                "http://dbpedia.org/ontology/City",*/"http://dbpedia.org/ontology/Director");
        ConfigLex lex = new ObjectMapper().readValue(new File(System.getProperty("user.dir") + "/inputLex.json"), ConfigLex.class);
        ConfigLemon lemon = new ObjectMapper().readValue(new File(System.getProperty("user.dir") + "/inputLemon.json"), ConfigLemon.class);
        for (String cl : classesList) {
            GraphExtractor extractor = new GraphExtractor(cl);
            lex.setClass_url(cl);
            lexicalization(lex);
            String className = cl.split("/")[4];
            createLemon(lemon, className);
            copyFilesToResultsFolder(className);
            extractor.extract(className);
        }
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        String cl = "http://dbpedia.org/ontology/Director";
        String className = cl.split("/")[4];
        System.out.println(className);
        GraphExtractor extractor = new GraphExtractor(className);
        extractor.setClazz(className);
        copyFilesToResultsFolder(className);
        extractor.extract("Director");
    }

    static void copyFilesToResultsFolder(String className) throws IOException {
        String workingDirectory = System.getProperty("user.dir");
        Path source = Paths.get(workingDirectory + "/results/");
        Path destination = Paths.get(workingDirectory + "/results_all_classes/" + "result_" + className + "/results/");
        String postfix = "_" + className + ".json";
        if (!Files.isDirectory(destination)) {
            Files.createDirectories(destination);
        }

        Files.list(source).forEach(src -> {
            try {
                Files.copy(src, Paths.get(destination + "/" + src.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }


        });
        String src = workingDirectory + "/result" + postfix;
        String dest = workingDirectory + "/results_all_classes/" + "result_" + className + "/result" + postfix;
        try {
            Files.copy(Paths.get(src), Paths.get(dest), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(String.format("Couldn't copy files from %s to %s", src, dest));
        }

    }

}
