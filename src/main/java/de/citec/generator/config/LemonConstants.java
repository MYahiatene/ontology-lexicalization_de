/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.generator.config;

import static de.citec.sc.lemon.vocabularies.LEXINFO.preposition;

/**
 *
 * @author elahi
 */
public interface LemonConstants {

    String lexinfo = "http://www.lexinfo.net/ontology/2.0/lexinfo#";
    String lemon = "http://lemon-model.net/lemon#";
    String lexinfo_adjective = lexinfo + "/" + "adjective";
    String lexinfo_verb = lexinfo + "/" + "verb";
    String lexinfo_noun = lexinfo + "/" + "noun";
    String AdjectivePredicateFrame = "AdjectivePredicateFrame";
    String attributiveArg = "attributiveArg";
    String AttrSynArg = "AttrSynArg";
    String copulativeSubject = "copulativeSubject";
    String PredSynArg = "PredSynArg";
    String TransitiveFrame="TransitiveFrame";
    String IntransitivePPFrame="IntransitivePPFrame";
    String  prepositionalAdjunct="prepositionalAdjunct";
    String object="object";
    String subject="subject";
    String subjOfProp="subjOfProp";
    String objOfProp="objOfProp";
    String  NounPPFrame="NounPPFrame";
    String preposition="on";
    String directObject="directObject";
}
