package de.citec.sc.generator.utils.graphextractor;

import java.io.IOException;
import java.net.URISyntaxException;

public class main {

    public static void main(String[] args) {
        String clazz = "Actor";
        GraphExtractor gr = new GraphExtractor(clazz);
        gr.setClazz(clazz);
        try {
            gr.extract(clazz);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
