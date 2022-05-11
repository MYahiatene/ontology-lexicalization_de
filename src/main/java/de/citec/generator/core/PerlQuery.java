/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.generator.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.citec.generator.config.ConfigLex;
import de.citec.generator.config.Constants;
import de.citec.sc.generator.exceptions.PerlException;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author elahi
 */
public class PerlQuery implements Constants {

    private Boolean processSuccessFlag = false;

    public PerlQuery(String location, String scriptName, String class_url, String langTag) throws PerlException {
        try {
            System.out.println("Reading DBpedia abstract and knowledge graph and corpus based lexicalization!!\n");
            this.runCommandLine(location, scriptName, class_url, langTag);
        } catch (InterruptedException ex) {
            Logger.getLogger(PerlQuery.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            throw new PerlException("Perl script is not working!!" + ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(PerlQuery.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            throw new PerlException("process error exceptions!!" + ex.getMessage());
        }

    }


    public Boolean runCommandLine(String location, String scriptName, String class_url, String langTag) throws IOException, InterruptedException {

        String command = "perl " + location + scriptName + " " + appDir + " " + class_url + " " + langTag;
        Runtime runTime = Runtime.getRuntime();

        Process process = runTime.exec(command);

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        // Read the output from the command
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }
        // Read any errors from the attempted command
        System.out.println("Error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            System.err.println(s);
        }

        if (process.waitFor() == 0) {
            System.err.println("Process terminated ");
            processSuccessFlag = true;
            return true;
        } else {
            return false;
        }

    }

    public Boolean getProcessSuccessFlag() {
        return processSuccessFlag;
    }


}
