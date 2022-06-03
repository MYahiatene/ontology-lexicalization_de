/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.generator.config;

import de.citec.sc.generator.exceptions.ConfigException;
import edu.stanford.nlp.util.Pair;


/**
 * @author elahi
 */

public class ConfigLex {
    private String class_url = null;
    private Integer minimum_entities_per_class = 0;
    private Integer maximum_entities_per_class = 0;
    private Integer minimum_onegram_length = 0;
    private Integer minimum_pattern_count = 0;
    private Integer minimum_anchor_count = 0;
    private Integer minimum_propertyonegram_length = 0;
    private Integer minimum_propertypattern_count = 0;
    private Integer minimum_propertystring_length = 0;
    private Integer maximum_propertystring_length = 0;
    private Integer minimum_supportA = 0;
    private Integer minimum_supportB = 0;
    private Integer minimum_supportAB = 0;

    private String langTag = "en";


    public String getLangTag() {
        return langTag;
    }

    public void setClass_url(String class_url) {
        this.class_url = class_url;
    }

    public void setMinimum_entities_per_class(Integer minimum_entities_per_class) {
        this.minimum_entities_per_class = minimum_entities_per_class;
    }

    public void setMaximum_entities_per_class(Integer maximum_entities_per_class) {
        this.maximum_entities_per_class = maximum_entities_per_class;
    }

    public void setMinimum_onegram_length(Integer minimum_onegram_length) {
        this.minimum_onegram_length = minimum_onegram_length;
    }

    public void setMinimum_pattern_count(Integer minimum_pattern_count) {
        this.minimum_pattern_count = minimum_pattern_count;
    }

    public void setMinimum_anchor_count(Integer minimum_anchor_count) {
        this.minimum_anchor_count = minimum_anchor_count;
    }

    public void setMinimum_propertyonegram_length(Integer minimum_propertyonegram_length) {
        this.minimum_propertyonegram_length = minimum_propertyonegram_length;
    }

    public void setMinimum_propertypattern_count(Integer minimum_propertypattern_count) {
        this.minimum_propertypattern_count = minimum_propertypattern_count;
    }

    public void setMinimum_propertystring_length(Integer minimum_propertystring_length) {
        this.minimum_propertystring_length = minimum_propertystring_length;
    }

    public void setMaximum_propertystring_length(Integer maximum_propertystring_length) {
        this.maximum_propertystring_length = maximum_propertystring_length;
    }

    public Integer getMinimum_supportA() {
        return minimum_supportA;
    }

    public void setMinimum_supportA(Integer minimum_supportA) {
        this.minimum_supportA = minimum_supportA;
    }

    public Integer getMinimum_supportB() {
        return minimum_supportB;
    }

    public void setMinimum_supportB(Integer minimum_supportB) {
        this.minimum_supportB = minimum_supportB;
    }

    public Integer getMinimum_supportAB() {
        return minimum_supportAB;
    }

    public void setMinimum_supportAB(Integer minimum_supportAB) {
        this.minimum_supportAB = minimum_supportAB;
    }

    public void setLangTag(String langTag) {
        this.langTag = langTag;
    }

    public String getClass_url() throws ConfigException {
        Pair<Boolean, String> check = checkClass(class_url);
        if (check.first()) {
            this.class_url = check.second();
            return this.class_url;
        } else {
            throw new ConfigException("");
        }
    }

    public String getClass_url_original() throws ConfigException {
        return this.class_url;
    }

    public Integer getMinimum_entities_per_class() {
        return minimum_entities_per_class;
    }

    public Integer getMaximum_entities_per_class() {
        return maximum_entities_per_class;
    }

    public Integer getMinimum_onegram_length() {
        return minimum_onegram_length;
    }

    public Integer getMinimum_pattern_count() {
        return minimum_pattern_count;
    }

    public Integer getMinimum_anchor_count() {
        return minimum_anchor_count;
    }

    public Integer getMinimum_propertyonegram_length() {
        return minimum_propertyonegram_length;
    }

    public Integer getMinimum_propertypattern_count() {
        return minimum_propertypattern_count;
    }

    public Integer getMinimum_propertystring_length() {
        return minimum_propertystring_length;
    }

    public Integer getMaximum_propertystring_length() {
        return maximum_propertystring_length;
    }

    public Integer getMinimum_supA() {
        return minimum_supportA;
    }

    public Integer getMinimum_supB() {
        return minimum_supportB;
    }

    public Integer getMinimum_supAB() {
        return minimum_supportAB;
    }

    public Pair<Boolean, String> checkClass(String class_url) {
        if (class_url.isEmpty()) {
            return new Pair<>(Boolean.FALSE, class_url);
        } else if (class_url.contains("http:")) {
            class_url = class_url.substring(class_url.lastIndexOf("/") + 1);
            return new Pair<>(Boolean.TRUE, class_url);
        } else if (class_url.contains("dbo:")) {
            String[] info = class_url.split(":");
            class_url = info[1];
            return new Pair<>(Boolean.TRUE, class_url);
        } else {
            return new Pair<>(Boolean.FALSE, class_url);
        }
    }

}
