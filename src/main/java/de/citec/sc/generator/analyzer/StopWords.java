package de.citec.sc.generator.analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class StopWords {
    private final List<String> germanStopWords=new ArrayList<>();

    public List<String> getGermanStopWords() {
        return germanStopWords;
    }


    public StopWords() throws IOException {
        Path path = Paths.get(System.getProperty("user.dir") + "/input/stopwords-de.txt");
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = reader.readLine();
            while (line != null) {
                germanStopWords.add(line);
                line = reader.readLine();
            }
        }
    }


}
