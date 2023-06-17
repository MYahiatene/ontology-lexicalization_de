package de.citec.sc.generator.utils.graphextractor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class main {

    public static void main(String[] args) throws IOException {
        List<String> classes = Files.list(Path.of("/Users/myahiatene/Desktop/Bachelorarbeit/ontology-lexicalization_de/results_all_classes/")).map(name -> name.toString().split("/")[7].split("_")[1]).collect(Collectors.toList());
        //String clazz = "Organization";
        System.out.println(classes);
        for (String clazz : classes) {
            GraphExtractor gr = new GraphExtractor(clazz);
            gr.setClazz(clazz);
            try {
                gr.extract(clazz);
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
