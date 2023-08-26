#!/usr/bin/env python3
import glob
import bz2
import os
import csv
import nltk
import pickle
from alive_progress import alive_bar
from evaluation_utils import merge_csvs_and_add_metrics, create_rank_metric_csvs

from de_properties_map import de_properties_map
from intransitiveFrameMap import intransitiveFrameMap
from qald7_properties import qald7_properties
import umlaute
import pandas as pd
from request import prepare_local_wiktionary_data, get_noun_wiktionary_data_from_dumps, \
    get_verb_wiktionary_data_from_dumps, get_adj_wiktionary_data_from_dumps
from nounMap import nounMap
from transitiveFrameMap import transitiveFrameMap
from headers import nounHeader, transitiveVerbHeader, intransitiveVerbHeader, attributeAdjHeader, gradableAdjHeader

corp = nltk.corpus.ConllCorpusReader('.', 'tiger_release_aug07.corrected.16012013.conll09',
                                     ['ignore', 'words', 'ignore', 'ignore', 'pos'],
                                     encoding='utf-8')

dirs = os.listdir('.')
class_dirs = [directory for directory in dirs if os.path.isdir(directory)]
csv_files = sorted(glob.glob(
    '../results/*.csv.bz2'), key=lambda rule: rule.split('-')[3])

# used to save requests to wiktionary ( dynamic programming)
wiktionary_map = {}
# used to prevent saving same entries in the csv
noun_csv_set = set()
# used to prevent saving same entries in the csv
transitive_csv_set = set()
# used to prevent saving same entries in the csv
intransitive_csv_set = set()
# used to prevent saving same entries in the csv
attribute_adjective_csv_set = set()
# used to prevent saving same entries in the csv
gradable_adjective_csv_set = set()

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


def post_proccess_noun(noun_map, noun_list: list, rank=None, metric=None):
    path = 'csv_results/NounPPFrame.csv' if rank is None and metric is None else f'csv_results/NounPPFrame_{metric}_{rank}.csv'
    domain_range_map_entry = noun_map.get(reference, None)
    if domain_range_map_entry is None:
        return
    domain = domain_range_map_entry[0]
    range = domain_range_map_entry[1]
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
    with open(path, 'a+') as file:
        writer = csv.DictWriter(file, fieldnames=nounHeader)
        if os.stat(path).st_size == 0:
            writer.writeheader()
            return
        if str(noun_row) not in noun_csv_set:
            writer.writerow(noun_row)
            noun_csv_set.add(str(noun_row))


def post_proccess_verb(transitiveFrameMap, intransitiveFrameMap, verb_list, rank=None, metric=None):
    path = 'csv_results/TransitiveFrame.csv' if rank is None and metric is None else f'csv_results/TransitiveFrame_{metric}_{rank}.csv'
    path2 = 'csv_results/IntransitiveFrame.csv' if rank is None and metric is None else f'csv_results/IntransitiveFrame_{metric}_{rank}.csv'
    domain_range_map_entry = None
    if verb_list[-1] == 'transitive':
        domain_range_map_entry = transitiveFrameMap.get(reference, None)
    if verb_list[-1] == 'intransitive':
        domain_range_map_entry = intransitiveFrameMap.get(reference, None)
    written_form_3rd_present, written_form_3rd_past, written_form_3rd_perfect, verb_type = verb_list
    if verb_type == 'transitive':
        if domain_range_map_entry is None:
            return
        domain = domain_range_map_entry[0]
        range = domain_range_map_entry[1]
        with open(path, 'a+') as file:
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
            if os.stat(path).st_size == 0:
                writer.writeheader()
            if str(verb_row) not in transitive_csv_set:
                writer.writerow(verb_row)
                transitive_csv_set.add(str(verb_row))
    if verb_type == "intransitive":
        if domain_range_map_entry is None:
            return
        domain = domain_range_map_entry[0]
        range = domain_range_map_entry[1]
        with open(path2, 'a+') as file:
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
            if os.stat(path2).st_size == 0:
                writer.writeheader()
            if str(verb_row) not in intransitive_csv_set:
                writer.writerow(verb_row)
                intransitive_csv_set.add(str(verb_row))
    if verb_type == 'intransitive/transitive':
        domain_range_map_entry_transitive = transitiveFrameMap.get(reference, None)
        domain_transitive = None
        range_transitive = None
        domain_intransitive = None
        range_intransitive = None
        if domain_range_map_entry_transitive is not None:
            domain_transitive = domain_range_map_entry_transitive[0]
            range_transitive = domain_range_map_entry_transitive[1]
        domain_range_map_entry_intransitive = intransitiveFrameMap.get(reference, None)
        if domain_range_map_entry_intransitive is not None:
            domain_intransitive = domain_range_map_entry_intransitive[0]
            range_intransitive = domain_range_map_entry_intransitive[1]
        with open(path, 'a+') as file, open(path2,
                                            'a+') as file2:
            writer = csv.DictWriter(file, fieldnames=transitiveVerbHeader)
            writer2 = csv.DictWriter(file2, fieldnames=intransitiveVerbHeader)
            verb_row_transitive = {'LemonEntry': word,
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
                                   'domain': domain_transitive,
                                   'range': range_transitive,
                                   'passivePreposition': 'von', }
            verb_row_intransitive = {'LemonEntry': word,
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
                                     'domain': domain_intransitive,
                                     'range': range_intransitive, }
            if os.stat(path).st_size == 0:
                writer.writeheader()
            if str(verb_row_transitive) not in transitive_csv_set and domain_range_map_entry_transitive is not None:
                writer.writerow(verb_row_transitive)
                transitive_csv_set.add(str(verb_row_transitive))
            if os.stat(path2).st_size == 0:
                writer2.writeheader()
            if str(verb_row_intransitive) not in intransitive_csv_set and domain_range_map_entry_intransitive is not None:
                writer2.writerow(verb_row_intransitive)
                intransitive_csv_set.add(str(verb_row_intransitive))


def post_proccess_adj(adj_list, rank=None, metric=None):
    path = 'csv_results/AttributeAdjective.csv' if rank is None and metric is None else f'csv_results/AttributeAdjective_{metric}_{rank}.csv'
    path2 = 'csv_results/GradableAdjective.csv' if rank is None and metric is None else f'csv_results/GradableAdjective_{metric}_{rank}.csv'
    gradable_list, is_attribute = adj_list
    is_gradable, comparative, superlative_singular, superlative_plural = gradable_list
    if is_attribute:
        with open(path, 'a+') as file:
            writer = csv.DictWriter(file, fieldnames=attributeAdjHeader)
            if os.stat(path).st_size == 0:
                writer.writeheader()
            adj_row = {'LemonEntry': word,
                       'partOfSpeech': 'adjective',
                       'writtenForm': word,
                       'SyntacticFrame': 'AdjectiveAttributiveFrame',
                       'copulativeSubject': 'PredSynArg',
                       'attributiveArg': 'AttrSynArg',
                       'sense': '1',
                       'reference': 'owl:Restriction',
                       'owl:onProperty': reference,
                       'owl:hasValue': '',
                       'domain': 'dbo:Person',
                       'range': 'dbo:Person',
                       }
            if str(adj_row) not in attribute_adjective_csv_set:
                writer.writerow(adj_row)
                attribute_adjective_csv_set.add(str(adj_row))
    if is_gradable:
        with open(path2, 'a+') as file:
            writer = csv.DictWriter(file, fieldnames=gradableAdjHeader)
            if os.stat(path2).st_size == 0:
                writer.writeheader()
            adj_row = {'LemonEntry': word,
                       'partOfSpeech': 'adjective',
                       'writtenForm': word,
                       'comparative': comparative,
                       'superlative_singular': superlative_singular,
                       'superlative_plural': superlative_plural,
                       'SyntacticFrame': 'AdjectiveSuperlativeFrame',
                       'predFrame': 'PredSynArg',
                       'sense': '1',
                       'reference': 'oils:CovariantScalar',
                       'oils:boundTo': reference,
                       'oils:degree': 'oils:high',
                       'domain': 'dbo:Person',
                       'range': 'dbo:Person',
                       'preposition': 'in',
                       }
            if str(adj_row) not in gradable_adjective_csv_set:
                writer.writerow(adj_row)
                gradable_adjective_csv_set.add(str(adj_row))


rank_metric_eval_flag = True
metrics = ['Cosine', 'Conviction', 'Lift']
ranks = [100]
if rank_metric_eval_flag:
    for rank in ranks:
        for metric in metrics:
            if os.path.exists(f'csv_results/AttributeAdjective_{metric}_{rank}.csv'):
                os.unlink(f'csv_results/AttributeAdjective_{metric}_{rank}.csv')
            if os.path.exists(f'csv_results/GradableAdjective_{metric}_{rank}.csv'):
                os.unlink(f'csv_results/GradableAdjective_{metric}_{rank}.csv')
            if os.path.exists(f'csv_results/NounPPFrame_{metric}_{rank}.csv'):
                os.unlink(f'csv_results/NounPPFrame_{metric}_{rank}.csv')
            if os.path.exists(f'csv_results/TransitiveFrame_{metric}_{rank}.csv'):
                os.unlink(f'csv_results/TransitiveFrame_{metric}_{rank}.csv')
            if os.path.exists(f'InTransitiveFrame_{metric}_{rank}.csv'):
                os.unlink(f'csv_results/InTransitiveFrame_{metric}_{rank}.csv')
else:
    if os.path.exists('csv_results/AttributeAdjective.csv'):
        os.unlink('csv_results/AttributeAdjective.csv')
    if os.path.exists('csv_results/GradableAdjective.csv'):
        os.unlink('csv_results/GradableAdjective.csv')
    if os.path.exists('csv_results/NounPPFrame.csv'):
        os.unlink('csv_results/NounPPFrame.csv')
    if os.path.exists('csv_results/TransitiveFrame.csv'):
        os.unlink('csv_results/TransitiveFrame.csv')
    if os.path.exists('InTransitiveFrame.csv'):
        os.unlink('csv_results/InTransitiveFrame.csv')

if rank_metric_eval_flag:
    merged_csv = merge_csvs_and_add_metrics()
    print(f'Creating csv files with ranks:{ranks} and with metrics:{metrics}\n')
    csv_pd_metric_arr = create_rank_metric_csvs(merged_csv, ranks, metrics)
    number_of_csv_files = len(csv_pd_metric_arr)
    print("Done.\n")
    print(f'Created in total {number_of_csv_files} pandas dataframes:{csv_pd_metric_arr}\n')
else:
    number_of_csv_files = len(csv_files)

with alive_bar(number_of_csv_files, title='Processing', force_tty=True) as bar:
    if rank_metric_eval_flag:
        for csv_df in csv_pd_metric_arr:
            class_name = csv_df['class'][0] if len(csv_df['class']) > 0 else 'Empty Class Name'
            rule_name = csv_df['ruletype_longname'][0] if len(csv_df['ruletype_longname']) > 0 else 'Empty Rule Name'
            print(f'class: {class_name} rule: {rule_name}')
            csv_df.sort_values('Cosine', inplace=True, ascending=False)
            for index, line in csv_df.iterrows():
                ngram = line['patterntype']
                reference = line['predicate']
                mapped_reference = None
                # TODO: Do it for more properties including converting properties to ontologies
                if 'de.dbpedia.org/property' in reference:
                    mapped_reference = de_properties_map.get(reference, None)
                if mapped_reference is not None:
                    reference = mapped_reference
                if reference not in qald7_properties:
                    continue
                words = replace_stop_words(replace_umlaute(line['linguistic pattern']))
                if len(reference) == 0:
                    continue
                lemmata_pos_word = read_lemma(words)
                if len(lemmata_pos_word) == 0:
                    continue
                word = lemmata_pos_word[0][0]
                pos_tag = lemmata_pos_word[0][1][0][1]
                if pos_tag.startswith('ADJ'):
                    adj_list = get_adj_wiktionary_data_from_dumps(dict_wiktionary_adj, word)
                    if adj_list is None:
                        continue
                    post_proccess_adj(adj_list, rank, metric)
                if pos_tag.startswith('N'):
                    noun_list = get_noun_wiktionary_data_from_dumps(
                        dict_wiktionary_noun, word)
                    if noun_list is None:
                        continue
                    post_proccess_noun(nounMap, noun_list, rank, metric)
                if pos_tag.startswith('V'):
                    verb_list = get_verb_wiktionary_data_from_dumps(
                        dict_wiktionary_verb, word)
                    if verb_list is None:
                        continue
                    post_proccess_verb(transitiveFrameMap, intransitiveFrameMap, verb_list, rank, metric)
            bar()
    else:
        for file in csv_files:
            with bz2.open(file, mode='rt') as f:
                csv_df = pd.read_csv(f)
                class_name = csv_df['class'][0] if len(csv_df['class']) > 0 else 'Empty Class Name'
                rule_name = csv_df['ruletype_longname'][0] if len(
                    csv_df['ruletype_longname']) > 0 else 'Empty Rule Name'
                print(f'class: {class_name} rule: {rule_name}')
                csv_df.sort_values('Cosine', inplace=True, ascending=False)
                for index, line in csv_df.iterrows():
                    ngram = line['patterntype']
                    reference = line['predicate']
                    mapped_reference = None
                    # TODO: Do it for more properties including converting properties to ontologies
                    if 'de.dbpedia.org/property' in reference:
                        mapped_reference = de_properties_map.get(reference, None)
                    if mapped_reference is not None:
                        reference = mapped_reference
                    if reference not in qald7_properties:
                        continue
                    words = replace_stop_words(replace_umlaute(line['linguistic pattern']))
                    if len(reference) == 0:
                        continue
                    lemmata_pos_word = read_lemma(words)
                    if len(lemmata_pos_word) == 0:
                        continue
                    word = lemmata_pos_word[0][0]
                    pos_tag = lemmata_pos_word[0][1][0][1]
                    if pos_tag.startswith('ADJ'):
                        adj_list = get_adj_wiktionary_data_from_dumps(dict_wiktionary_adj, word)
                        if adj_list is None:
                            continue
                        post_proccess_adj(adj_list)
                    if pos_tag.startswith('N'):
                        noun_list = get_noun_wiktionary_data_from_dumps(
                            dict_wiktionary_noun, word)
                        if noun_list is None:
                            continue
                        post_proccess_noun(nounMap, noun_list)
                    if pos_tag.startswith('V'):
                        verb_list = get_verb_wiktionary_data_from_dumps(
                            dict_wiktionary_verb, word)
                        if verb_list is None:
                            continue
                        post_proccess_verb(transitiveFrameMap, intransitiveFrameMap, verb_list)
                bar()
