/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.generator.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

/**
 * @author elahi
 */
public class FileFolderUtils {

    public static String configDir = "src/main/resources/config/";
    public static String configFileName = "prefix.prop";

    public static void createDirectory(String location) throws IOException {
        Path location_path = Paths.get(location);
        Files.createDirectories(location_path);
    }

    public static List<File> getSpecificFiles(String fileDir, String category, String extension) {
        List<File> selectedFiles = new ArrayList<File>();
        try {
            String[] files = new File(fileDir).list();
            for (String fileName : files) {
                if (fileName.contains(category) && fileName.contains(extension)) {
                    selectedFiles.add(new File(fileDir + fileName));
                }
            }

        } catch (Exception exp) {
            System.err.println("file not found!!" + exp.getMessage());
            return new ArrayList<File>();
        }

        return selectedFiles;
    }
//todo: decode utf8
    public static BufferedReader getBufferedReaderForCompressedFile(File fileIn) throws IOException, CompressorException {
        FileInputStream fin = new FileInputStream(fileIn);
        BufferedInputStream bis = new BufferedInputStream(fin);
        //TODO: Cuts input file if too large. Fix with chunk reading
        CompressorInputStream input = new CompressorStreamFactory(true,Integer.MAX_VALUE-16).createCompressorInputStream(bis);

        BufferedReader br2 = new BufferedReader(new InputStreamReader(input));
        //Skip header
        br2.readLine();
        return br2;
    }

    public static void delete(File dir) throws Exception {
        try {
            File[] files = dir.listFiles();
            for (File file : files) {
                file.delete();
            }
        } catch (Exception ex) {
            throw new Exception("file directory does not exist!!");
        }

    }

}
