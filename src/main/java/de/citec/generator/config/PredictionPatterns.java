/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.generator.config;

import java.util.*;

/**
 * @author elahi
 */
public interface PredictionPatterns {

    String predict_l_for_s_given_po = "predict_l_for_s_given_po";
    String predict_localized_l_for_s_given_po = "predict_localized_l_for_s_given_po";
    String predict_l_for_s_given_p = "predict_l_for_s_given_p";
    String predict_localized_l_for_s_given_p = "predict_localized_l_for_s_given_p";
    String predict_l_for_s_given_o = "predict_l_for_s_given_o";
    String predict_l_for_o_given_sp = "predict_l_for_o_given_sp";
    String predict_localized_l_for_o_given_sp = "predict_localized_l_for_o_given_sp";
    String predict_l_for_o_given_s = "predict_l_for_o_given_s";
    String predict_l_for_o_given_p = "predict_l_for_o_given_p";
    String predict_localized_l_for_o_given_p = "predict_localized_l_for_o_given_p";
    String predict_p_for_s_given_l = "predict_p_for_s_given_l";
    String predict_o_for_s_given_l = "predict_o_for_s_given_l";
    String predict_p_for_o_given_l = "predict_p_for_o_given_l";
    String predict_po_for_s_given_l = "predict_po_for_s_given_l";
    String predict_s_for_o_given_l = "predict_s_for_o_given_l";
    String predict_po_for_s_given_localized_l = "predict_po_for_s_given_localized_l";
    String predict_p_for_s_given_localized_l = "predict_p_for_s_given_localized_l";
    String predict_p_for_o_given_localized_l = "predict_p_for_o_given_localized_l";
    String predict_sp_for_o_given_localized_l = "predict_sp_for_o_given_localized_l";
    String predict_sp_for_o_given_l = "predict_sp_for_o_given_l";

    String supA = "supA";
    String supB = "supB";
    String supAB = "supAB";
    String condAB = "condAB";
    String condBA = "condBA";
    String AllConf = "AllConf";
    String MaxConf = "MaxConf";
    String IR = "IR";
    String Kulczynski = "Kulczynski";
    String Cosine = "Cosine";
    String Coherence = "Coherence";
    LinkedHashSet<String> interestingness = new LinkedHashSet(new ArrayList<String>(Arrays.asList(Cosine, Coherence, AllConf, MaxConf, IR, Kulczynski)));

}
