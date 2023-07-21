#!/usr/bin/env python
import glob
import bz2
import os
import csv
import nltk
import pickle
from alive_progress import alive_bar
from qald7_properties import qald7_properties
import umlaute
import pandas as pd
from request import prepare_local_wiktionary_data, get_noun_wiktionary_data_from_dumps, \
    get_verb_wiktionary_data_from_dumps, get_adj_wiktionary_data_from_dumps
from nounMap import nounMap
from verbFrameMap import verbFrameMap
from headers import nounHeader, transitiveVerbHeader, intransitiveVerbHeader

corp = nltk.corpus.ConllCorpusReader('.', 'tiger_release_aug07.corrected.16012013.conll09',
                                     ['ignore', 'words', 'ignore', 'ignore', 'pos'],
                                     encoding='utf-8')

dirs = os.listdir('.')
class_dirs = [directory for directory in dirs if os.path.isdir(directory)]
csv_files = glob.glob(
    '../results/*.csv.bz2')

# used to save requests to wiktionary ( dynamic programming)
wiktionary_map = {}
# used to prevent saving same entries in the csv
noun_csv_set = set()
# used to prevent saving same entries in the csv
transitive_csv_set = set()
# used to prevent saving same entries in the csv
intransitive_csv_set = set()
# used to prevent saving same entries in the csv
adjective_csv_set = set()

noun_df = pd.DataFrame
verb_transitive_df = pd.DataFrame
verb_intransitive_df = pd.DataFrame
adj_df = pd.DataFrame
pd.set_option('display.max_colwidth', None)

# uncomment for local wiktionary data
dict_wiktionary_noun, dict_wiktionary_verb, dict_wiktionary_adj = prepare_local_wiktionary_data()

lemmata_pos = []


def open_tagger_file():
    with open('nltk_german_classifier_data.pickle', 'rb') as f:
        return pickle.load(f, encoding='utf-8')


tagger = open_tagger_file()


def read_lemmata_from_tiger_corpus(tiger_corpus_file, valid_cols_n=15, col_words=1, col_lemmata=2):
    lemmata_mapping = {}
    with open(tiger_corpus_file, encoding='utf-8') as f:
        for line in f:
            parts = line.split()
            if len(parts) == valid_cols_n:
                w, lemma = parts[col_words], parts[col_lemmata]
                if w != lemma and w not in lemmata_mapping and not lemma.startswith('--'):
                    lemmata_mapping[w] = lemma
    return lemmata_mapping


lemmata_mapping = read_lemmata_from_tiger_corpus('tiger_release_aug07.corrected.16012013.conll09')


def replace_stop_words(words):
    with open('stopwords-de.txt', mode='r', encoding='utf-8') as f:
        lines = f.readlines()
        return [word for word in words.split(" ") if word + '\n' not in lines]


def read_lemma(words):
    lemmata = []
    for w in words:
        w_lemma = lemmata_mapping.get(w, w)
        if w_lemma is None:
            w_lemma = w
        pos_tags = tagger.tag([w_lemma])
        if pos_tags[0][1] == 'XY':
            continue
        lemmata.append((w_lemma, tagger.tag([w])))
    return lemmata


def replace_umlaute(word: str) -> str:
    return word.replace(umlaute.u, "ü").replace(umlaute.a, "ä").replace(umlaute.o, "ö").replace(umlaute.U, "Ü").replace(
        umlaute.A, "Ä").replace(umlaute.O, "Ö").replace(umlaute.ss, "ß")


def post_proccess_noun(noun_map, noun_list: list):
    domain = noun_map[reference][0]
    range = noun_map[reference][1]
    genus, nominativ_singular, nominativ_plural, akkusativ_singular, dativ_singular, genetiv_singular = noun_list
    noun_row = {"LemonEntry": word, 'partOfSpeech': 'noun',
                'gender': genus,
                'writtenFormNominative(singular)': nominativ_singular,
                'writtenFormNominative (plural)': nominativ_plural,
                'writtenFormSingular (accusative)':
                    akkusativ_singular,
                'writtenFormSingular (dative)':
                    dativ_singular,
                'writtenFormSingular (genetive)': genetiv_singular,
                'preposition': 'von',
                'SyntacticFrame': 'NounPPFrame',
                'copulativeArg': 'range',
                'prepositionalAdjunct': 'domain',
                'sense': '1',
                'reference': reference,
                'domain': domain,
                'range': range}
    with open('NounPPFrame.csv', 'a') as file:
        writer = csv.DictWriter(file, fieldnames=nounHeader)
        if os.stat('NounPPFrame.csv').st_size == 0:
            writer.writeheader()
            if noun_row not in noun_csv_set:
                writer.writerow(noun_row)
                noun_csv_set.add(noun_row)


def post_proccess_verb(verbFrameMap, verb_list):
    domain = verbFrameMap[reference][0]
    range = verbFrameMap[reference][1]
    written_form_3rd_present, written_form_3rd_past, written_form_3rd_perfect, verb_type = verb_list
    if verb_type == 'transitive':
        with open('TransitiveFrame.csv', 'a') as file:
            writer = csv.DictWriter(file, fieldnames=transitiveVerbHeader)
            verb_row = {'LemonEntry': word,
                        'partOfSpeech': 'verb',
                        'writtenFormInfinitive': word,
                        'writtenForm3rdPresent': written_form_3rd_present,
                        'writtenFormPast': written_form_3rd_past,
                        'writtenFormPerfect': written_form_3rd_perfect,
                        'SyntacticFrame': 'TransitiveFrame',
                        'subject': 'range',
                        'directObject': 'domain',
                        'sense': '1',
                        'reference': reference,
                        'domain': domain,
                        'range': range,
                        'passivePreposition': 'von', }
            if os.stat('TransitiveFrame.csv').st_size == 0:
                writer.writeheader()
            if verb_row not in transitive_csv_set:
                writer.writerow(verb_row)
                transitive_csv_set.add(verb_row)
    if verb_type == "intransitive":
        domain = verbFrameMap[reference][0]
        range = verbFrameMap[reference][1]
        with open('InTransitiveFrame.csv', 'a') as file:
            writer = csv.DictWriter(file, fieldnames=intransitiveVerbHeader)
            verb_row = {'LemonEntry': word,
                        'partOfSpeech': 'verb',
                        'writtenFormInfinitive': word,
                        'writtenFormThridPerson': written_form_3rd_present,
                        'writtenFormPast': written_form_3rd_past,
                        'writtenFormPerfect': written_form_3rd_perfect,
                        'preposition': 'durch',
                        'SyntacticFrame': 'IntransitivePPFrame',
                        'subject': 'domain',
                        'prepositionalAdjunct': 'range',
                        'sense': '1',
                        'reference': reference,
                        'domain': domain,
                        'range': range, }
            if os.stat('InTransitiveFrame.csv').st_size == 0:
                writer.writeheader()
            if verb_row not in intransitive_csv_set:
                writer.writerow(verb_row)
                intransitive_csv_set.add(verb_row)
    if verb_type == 'transitive/intransitive':
        if verb_row not in intransitive_csv_set:
            writer.writerow(verb_row)
            intransitive_csv_set.add(verb_row)
        if verb_row not in transitive_csv_set:
            writer.writerow(verb_row)
            transitive_csv_set.add(verb_row)


def post_proccess_adj(adj_map, adj_list):
    with open('Adjective.csv', 'a') as file:
        writer = csv.DictWriter(file, fieldnames=["class",
                                                  "linguistic pattern",
                                                  "predicate"
                                                  ])
        if os.stat('Adjective.csv').st_size == 0:
            writer.writeheader()
        # TODO: local wiktionary and differentiate between attribute and gradable
        # get_adj_wiktionary_data_from_dumps(dict_wiktionary_adj,words[0])
        adj_row = {"class": line["class"], "linguistic pattern": word,
                   "predicate": reference,
                   }
        # TODO: case for gradable and attribute adjective
        if adj_row not in adjective_csv_set:
            writer.writerow(adj_row)
            adjective_csv_set.add(adj_row)
        writer.writerow(adj_row)


if os.path.exists('Adjective.csv'):
    os.unlink('Adjective.csv')
if os.path.exists('NounPPFrame.csv'):
    os.unlink('NounPPFrame.csv')
if os.path.exists('TransitiveFrame.csv'):
    os.unlink('TransitiveFrame.csv')
if os.path.exists('InTransitiveFrame.csv'):
    os.unlink('InTransitiveFrame.csv')

with alive_bar(len(csv_files), force_tty=True) as bar:
    for count, file in enumerate(csv_files):
        bar(count)
        with bz2.open(file, mode='rt') as f:
            csv_df = pd.read_csv(f)
            csv_df.sort_values('Cosine', inplace=True, ascending=False)
            csv_df = csv_df.loc[csv_df['predicate'].isin(qald7_properties)]
            for index, line in csv_df.iterrows():
                ngram = line['patterntype']
                if len(line['predicate']) == 0:
                    continue
                reference = line['predicate']
                words = line['linguistic pattern']
                words = replace_stop_words(replace_umlaute(words))
                if len(words) > 2:
                    continue
                lemmata_pos_word = read_lemma(words)
                if len(lemmata_pos_word) == 0:
                    continue
                word = lemmata_pos_word[0][0]
                pos_tag = lemmata_pos_word[0][1][0][1]
                if pos_tag.startswith('ADJ'):
                    adj_list = get_adj_wiktionary_data_from_dumps(dict_wiktionary_adj, word)
                    adj_map = {}
                    post_proccess_adj(adj_map, adj_list)
                    try:
                        if pos_tag.startswith('N'):
                            noun_list = get_noun_wiktionary_data_from_dumps(
                                dict_wiktionary_noun, word)
                            post_proccess_noun(nounMap, noun_list)
                        if pos_tag.startswith('V'):
                            verb_list = get_verb_wiktionary_data_from_dumps(
                                dict_wiktionary_verb, word)
                            post_proccess_verb(verbFrameMap, verb_list)
                    except Exception:
                        continue
