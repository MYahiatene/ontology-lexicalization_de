package de.citec.sc.generator.utils.graphextractor;

import lombok.AllArgsConstructor;
import lombok.Data;

// LemonEntry	partOfSpeech	gender	writtenFormNominative(singular)	writtenFormNominative (plural)	writtenFormSingular (accusative)	writtenFormSingular (dative)	writtenFormSingular (genetive)	preposition	SyntacticFrame	copulativeArg
// prepositionalAdjunct	sense	reference	domain	range	domain_article	domain_written_singular	domain_written_plural	rangeArticle	range_written_singular	range_written_plural
// question_1	sparql_1	question_2	sparql_2	question_3	sparql_3	question_4	sparql_4	question_5	sparql_5	comment
@AllArgsConstructor
@Data
public class NounPPFrameCSV {

    private final String lemonEntry;
    private final String partOfSpeech;
    private final String gender;
    private final String writtenFormNominativeSingular;
    private final String writtenFormNominativePlural;
    private final String writtenFormSingularAccusative;
    private final String writtenFormSingularDative;
    private final String writtenFormSingularGenitive;
    private final String preposition;
    private final String syntacticFrame;
    private final String copulativeArg;
    private final String prepositionalAdjunct;
    private final String sense;
    private final String reference;
    private final String domain;
    private final String range;
    private final String domainArticle;
    private final String domainWrittenSingular;
    private final String domainWrittenPlural;
    private final String rangeArticle;
    private final String rangeWrittenSingular;
    private final String rangeWrittenPlural;

}
