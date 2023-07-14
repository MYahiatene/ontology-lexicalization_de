#!/usr/bin/python3
import glob
import bz2
import os
import csv
import nltk
import pickle
from qald7_properties import qald7_properties
import umlaute
import pandas as pd
from request import get_wiktionary_data
from nounMap import nounMap
from verbFrameMap import verbFrameMap
from headers import nounHeader, transitiveVerbHeader, intransitiveVerbHeader

corp = nltk.corpus.ConllCorpusReader('.', 'tiger_release_aug07.corrected.16012013.conll09',
                                     ['ignore', 'words', 'ignore', 'ignore', 'pos'],
                                     encoding='utf-8')

dirs = os.listdir('.')
class_dirs = [directory for directory in dirs if os.path.isdir(directory)]

#               Training my tagger from tiger corpus
'''
tagged_sents = list(corp.tagged_sents())
random.shuffle(tagged_sents)

# set a split size: use 90% for training, 10% for testing
split_perc = 0.1
split_size = int(len(tagged_sents) * split_perc)
train_sents, test_sents = tagged_sents[split_size:], tagged_sents[:split_size]

tagger = ClassifierBasedGermanTagger(train=train_sents)

tagger.accuracy(gold=test_sents)
with open('nltk_german_classifier_data.pickle', 'wb') as f:
    pickle.dump(tagger, f, protocol=2)
'''


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
global tagger

with open('nltk_german_classifier_data.pickle', 'rb') as f:
    tagger = pickle.load(f, encoding='utf-8')


def replace_stop_words(words):
    with open('stopwords-de.txt', mode='r', encoding='utf-8') as f:
        lines = f.readlines()
        return [word for word in words if word + '\n' not in lines]


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


noun_df = pd.DataFrame
verb_transitive_df = pd.DataFrame
verb_intransitive_df = pd.DataFrame
adj_df = pd.DataFrame
pd.set_option('display.max_colwidth', None)

lemmata_pos = []
if os.path.exists('AttributeAdjective.csv'):
    os.unlink('AttributeAdjective.csv')
if os.path.exists('NounPPFrame.csv'):
    os.unlink('NounPPFrame.csv')
if os.path.exists('TransitiveFrame.csv'):
    os.unlink('TransitiveFrame.csv')
if os.path.exists('InTransitiveFrame.csv'):
    os.unlink('InTransitiveFrame.csv')

csv_files = glob.glob(
    '../results_classes/*.csv.bz2')

for file in csv_files:
    with bz2.open(file, mode='rt') as f:
        csv_df = pd.read_csv(f)
        csv_df.sort_values('Cosine', inplace=True, ascending=False)
        csv_df = csv_df.loc[csv_df['predicate'].isin(qald7_properties)]
        line = csv_df.head(1)
        if len(line['predicate']) == 0:
            continue
        reference = line['predicate'].values[0]
        words = line['linguistic pattern'].values
        words = replace_stop_words([replace_umlaute(words[0])])
        lemmata_pos_word = read_lemma(words)
        if len(lemmata_pos_word) == 0:
            continue
        word = lemmata_pos_word[0][0]
        pos_tag = lemmata_pos_word[0][1][0][1]
        if pos_tag.startswith('ADJ'):
            with open('AttributeAdjective.csv', 'a') as file:
                writer = csv.DictWriter(file, fieldnames=['LemonEntry', 'partOfSpeech', 'writtenForm', 'reference'])
                if os.stat('AttributeAdjective.csv').st_size == 0:
                    writer.writeheader()
                writer.writerow({'LemonEntry': word, 'partOfSpeech': 'adjective',
                                 'writtenForm': word, 'reference': reference})
        else:
            wiktionary_data = get_wiktionary_data(word, pos_tag)
            if wiktionary_data is None:
                continue
            pos = wiktionary_data[0]
            try:
                if pos == 'noun':
                    domain = nounMap[reference][0]
                    range = nounMap[reference][1]
                    noun_row = {"LemonEntry": word, 'partOfSpeech': 'noun', 'gender': wiktionary_data[1],
                                'writtenFormNominative(singular)': wiktionary_data[2],
                                'writtenFormNominative (plural)': wiktionary_data[3],
                                'writtenFormSingular (accusative)':
                                    wiktionary_data[4],
                                'writtenFormSingular (dative)':
                                    wiktionary_data[5],
                                'writtenFormSingular (genetive)': wiktionary_data[6],
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
                        writer.writerow(noun_row)
                if pos == 'verb':
                    if wiktionary_data[4] == "transitive":
                        domain = verbFrameMap[reference][0]
                        range = verbFrameMap[reference][1]
                        with open('TransitiveFrame.csv', 'a') as file:
                            writer = csv.DictWriter(file, fieldnames=transitiveVerbHeader)
                            verb_row = {'LemonEntry': word,
                                        'partOfSpeech': 'verb',
                                        'writtenFormInfinitive': word,
                                        'writtenForm3rdPresent': wiktionary_data[1],
                                        'writtenFormPast': wiktionary_data[2],
                                        'writtenFormPerfect': wiktionary_data[3],
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
                            writer.writerow(verb_row)
                    if wiktionary_data[4] == "intransitive":
                        domain = verbFrameMap[reference][0]
                        range = verbFrameMap[reference][1]
                        with open('InTransitiveFrame.csv', 'a') as file:
                            writer = csv.DictWriter(file, fieldnames=intransitiveVerbHeader)
                            verb_row = {'LemonEntry': word,
                                        'partOfSpeech': 'verb',
                                        'writtenFormInfinitive': word,
                                        'writtenForm3rdPresent': wiktionary_data[1],
                                        'writtenFormPast': wiktionary_data[2],
                                        'writtenFormPerfect': wiktionary_data[3],
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
                            writer.writerow(verb_row)
            except Exception:
                continue
