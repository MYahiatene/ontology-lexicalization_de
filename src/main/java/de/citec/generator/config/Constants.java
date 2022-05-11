/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.generator.config;

/**
 * @author elahi
 */
public interface Constants {

    String UNDERSCORE = "UNDERSCORE";
    String perlDir = "perl/";
    String scriptName = "experiment.pl";
    String processData = "processData/";
    String appDir = System.getProperty("user.dir") + "/";
    String interDir = appDir + "inter/";
    String resultDir = appDir + "results/";
    String defaultResult = "{\"@graph\" : [],\"@context\":{}}";

}
