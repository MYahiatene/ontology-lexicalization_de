import glob
import bz2
import os
import csv
import nltk
import pickle
from qald7_properties import qald7_properties
import umlaute
import pandas as pd
import ijson
from request import get_wiktionary_data
from nounMap import nounMap
from transitiveFrameMap import transitiveFrameMap
from intransitiveFrameMap import intransitiveFrameMap
from headers import nounHeader, transitiveVerbHeader, intransitiveVerbHeader, attributeAdjHeader, gradableAdjHeader

corp = nltk.corpus.ConllCorpusReader('.', 'tiger_release_aug07.corrected.16012013.conll09',
                                     ['ignore', 'words', 'ignore', 'ignore', 'pos'],
                                     encoding='utf-8')

dirs = os.listdir(
    '/Users/myahiatene/Desktop/Bachelorarbeit/ontology-lexicalization_de/results_all_classes_csv_files/results_all_classes_latest')
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


def search_wiktionary_records(pos: str, word: str):
    wiktionary_records = None
    if pos.startswith('N'):
        wiktionary_records = records_noun
    elif pos.startswith('V'):
        wiktionary_records = records_verb
    elif pos.startswith('ADJ'):
        wiktionary_records = records_adj
    if wiktionary_records is None:
        return None
    for record in wiktionary_records:
        x = record['word']
        if x == word:
            return record


records_noun = ijson.items(
    open(
        '/Users/myahiatene/results_all_classes_csv_files/results_all_classes/kaikki_dot_org-dictionary-German-by-pos-noun.json',
        "rb"),
    '', multiple_values=True)

records_verb = ijson.items(
    open(
        '/Users/myahiatene/results_all_classes_csv_files/results_all_classes/kaikki_dot_org-dictionary-German-by-pos-verb.json',
        "rb"),
    '', multiple_values=True)

records_adj = ijson.items(
    open(
        '/Users/myahiatene/results_all_classes_csv_files/results_all_classes/kaikki_dot_org-dictionary-German-by-pos-adj.json',
        "rb"),
    '', multiple_values=True)

noun_df = pd.DataFrame
verb_transitive_df = pd.DataFrame
verb_intransitive_df = pd.DataFrame
adj_df = pd.DataFrame

lemmata_pos = []
if os.path.exists('adj_test.csv'):
    os.unlink('adj_test.csv')
if os.path.exists('noun_test.csv'):
    os.unlink('noun_test.csv')
if os.path.exists('verb_test.csv'):
    os.unlink('verb_test.csv')
for directory in class_dirs:
    csv_files = glob.glob(
        '/Users/myahiatene/results_all_classes_csv_files/results_all_classes/%s/results/*.csv.bz2' % directory)
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
            word = lemmata_pos_word[0][0]
            pos_tag = lemmata_pos_word[0][1][0][1]
            if pos_tag.startswith('ADJ'):
                with open('adj_test.csv', 'a') as file:
                    writer = csv.DictWriter(file, fieldnames=['LemonEntry', 'partOfSpeech', 'writtenForm', 'reference'])
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
                        with open('noun_test.csv', 'a') as file:
                            writer = csv.DictWriter(file, fieldnames=nounHeader)
                            writer.writerow(noun_row)
                    if pos == 'verb':
                        if wiktionary_data[4] == "transitive":
                            domain = transitiveFrameMap[reference][0]
                            range = transitiveFrameMap[reference][1]
                            with open('verb_test_transitive.csv', 'a') as file:
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
                                writer.writerow(verb_row)
                        if wiktionary_data[4] == "intransitive":
                            domain = intransitiveFrameMap[reference][0]
                            range = intransitiveFrameMap[reference][1]
                            with open('verb_test_intransitive.csv', 'a') as file:
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
                                writer.writerow(verb_row)
                except Exception:
                    continue


"""ADJA
ADJD
ADV
APPR
APPRART
APPO
APZR
ART
CARD
FM
ITJ
KOUI
KOUS
KON
KOKOM
NN
NE
PDAT
PDS
PIAT
PIS
PPER
PPOSAT
PPOSS
PRELS
PRELAT
PRF
PWS
PWAT
PWAV
PROAV
PTKZU
PTKNEG
PTKVZ
PTKANT
PTKA
TRUNC
VVFIN
VAFIN
VMFIN
VVINF
VAINF
VMINF
VVIMP
VAIMP
VVPP
VAPP
VMPP
VVIZU
XY
$,
$(
$.
attributives Adjektiv
adverbiales ODER
prädikatives Adjektiv
Adverb
Präposition; Zirkumposition links
Präposition mit Artikel
Postposition
Zirkumposition rechts
bestimmter ODER
unbestimmter Artikel
Kardinalzahl
Fremdsprachliches Material
Interjektion
unterordnende Konjunktion mit (zu-)Infinitiv
unterordnende Konjunktion
nebenordnende Konjunktion und, oder, aber
Vergleichskonjunktion als, wie
normales Nomen
Eigennamen
attribuierendes Demonstrativpronomen
substituierendes Demonstrativpronomen
attribuierendes Indefinitpronomen
substituierendes Indefinitpronomen
(nicht-reflexives) Personalpronomen
attribuierendes Possessivpronomen
substituierendes Possessivpronomen
substituierendes Relativpronomen
attribuierendes Relativpronomen
Reflexivpronomen
substituierendes Interrogativpronomen
attribuierendes Interrogativpronomen
adverbiales Interrogativpronomen
Pronominaladverb
"zu" vor Infinitiv
Negationspartikel nicht
abgetrennter Verbzusatz/Verbpartikel
Antwortpartikel
Partikel "am" o. "zu" vor Adjektiv o. Adverb
abgetrenntes Kompositionserstglied
finites Vollverb
finites Voll- oder Kopulaverb
finites Modalverb
infinites Vollverb
infinites Hilfsverb oder Kopulaverb
infinites Modalverb
Vollverb im Imperativ
Kopulaverb im Imperativ
partizipiales Vollverb (Partizip II)
partizipiales Hilfs-/Kopulaverb (Partizip II)
partizipiales Modalverb (Partizip II)
Vollverb/Partikelverb im "zu"-Infinitiv
Nichtwort, Sonderzeichen, Kürzel
Komma
sonstige satzinterne Interpunktion
satzbeendende Interpunktion
der schlaue/ADJA Mitarbeiter
er spricht schnell/ADJD
Sein Sprechen ist schnell/ADJD
Bald/ADV schon/ADV kommt sie wohl/ADV
nach/APPR Berlin; ohne/APPR Hund
zum/APPRART Streichen; zur/APPRART Sache
ihm zuliebe/APPO; der Sache wegen/APPO
von mir aus/APZR
Der/ART Mann schenkt die/ART Rose
einer/ART unerwarteten Frau
zwei/CARD Männer im Jahre 1994/CARD
Er sagte:" Hasta/FM luego/FM, amigos/FM ."
Mhm/ITJ, ach/ITJ, tja/ITJ, dann halt nicht.
Sie kommt, um/KOUI zu arbeiten
Anstatt/KOUI anzufangen, geht sie wieder
Emma wartet, weil/ob/solange/dass/KOUS sie stiehlt
Sie und/oder/KON Emma kommen und/KON streichen
blauer als/KOKOM er; blau wie/KOKOM er
am Tage/NN dem Mann/NN den Schlaf/NN
die Emma/NE dem Hans/NE sein HSV/NE
Jene/PDAT Männer sprachen dieses/PDAT lockere Spanisch
Denen/PDS war dies/PDS nicht übelzunehmen
Manche/PIAT Rose währt einige/PIAT Tage
Manche/PIS verzeiht niemandem/PIS
Er/PPER schenkt sie/PPER ihr/PPER
Unsere/PPOSAT Wand ist rosa
Meiner/PPOSS schlägt deinen/PPOSS
die Mannschaft, der/PRELS du nacheiferst
die Mannschaft, deren/PRELAT Aura du verehrst
Erinnere dich/PRF, wie er sich/PRF ereiferte
Wer/PWS hat wen/PWS gestohlen?
Wessen/PWAT Hund wurde gestohlen?
Warum/PWAV schneidest du die Rose?
Deswegen/PROAV sprechen wir darüber/PROAV
Ich versuche zu/PTKZU verschlafen
Nicht/PTKNEG schlecht, wie du nicht/PTKNEG hinsiehst
Pass auf/PTKVZ und hör weg/PTKVZ!
ja; nein; danke; bitte
Zu/PTKA teure Rosen welken am/PTKA schnellsten
Mallorca liegt zwischen An-/TRUNC und Abreise
Wir passen/VVFIN auf und hören/VVFIN
Sie ist/VAFIN blumig. Du hast/VAFIN weggehört.
Sie sollte/VMFIN passen
Wir wollen weghören/VVINF.
Sie soll rot geworden sein/VAINF
Er hat nicht schlafen können/VMFIN.
Pass/VVIMP auf und hör/VVIMP weg!
Sei/VAIMP wach!
Wir haben verschlafen/VVPP.
Das ist verdrängt worden/VAPP
Sie hat spielen gedurft/VVPP
Wir planen wegzuhören/VVIZU
Es enthält viel D2XW3/XY
,
( ) u.a.
. ? ! ; """
'''import bz2
import os
import shutil
import glob
import bz2

csv_files = glob.glob(
  '/Users/myahiatene/Desktop/Bachelorarbeit/ontology-lexicalization_de/results_all_classes_processed/result_Actor/results/rules-predict_l_for_[o|s]_given_p-dbo-Actor-100-10000-4-5-5-5-5.csv.bz2')

print(csv_files)
for file in csv_files:
  with bz2.open(file, mode='rt', encoding='utf-8') as f:
'''

'''import os
import shutil
import pandas as pd
from pandas.errors import EmptyDataError
import json

path = '/Users/myahiatene/Desktop/results_all_classes_processed/'
subDir = [directory for directory in os.listdir(path) if os.path.isdir(path + directory)]


def read_csv(class_dir, word_type_dir, skip_header):
  no_rows_skipped = 0
  if skip_header:
    no_rows_skipped = 1
  file_path = path + class_dir + word_type_dir
  try:
    return pd.read_csv(file_path, skiprows=no_rows_skipped)
  except (FileNotFoundError, EmptyDataError):
    pass


dump_file_path = "/Users/myahiatene/Desktop/results_all_classes_processed/kaikki.org-dictionary-German.json"
'''
# download link https://kaikki.org/dictionary/German/index.html

"""lst = []
count = 0
with open(dump_file_path, encoding="utf-8") as f:
  for line in f:
    count += 1
    if count == 10000:
      break
    data = json.loads(line)
    lst.append(data)

print(json.loads(json.dumps(lst[80], indent=2, sort_keys=True, ensure_ascii=False)))
"""
"""import ijson

with open(dump_file_path, "rb") as f:
  for k in ijson.items(f, '', multiple_values=True):
    print(k)
"""
# with open(dump_file_path, "rb") as f:
#  for record in ijson.items(f, "item", multiple_values=True):
#    print(record[0])
# df = pd.read_json("/Users/myahiatene/Desktop/results_all_classes_processed/kaikki.org-dictionary-German.json",
#                  lines=True)

"""for dir in subDir:
  directoryPath = path + dir + "/"

  new_file_path = directoryPath + "new_" + dir

  new_result_directory = directoryPath + "new_" + dir + "/"
  try:
    os.mkdir(new_result_directory)
  except FileExistsError as e:
    None
    # print(e)
  old_result_class_jsonpath = directoryPath + dir + ".json"

  new_result_class_jsonpath = new_file_path + ".json"

  new_result_class_noun_jsonpath = new_file_path + "_noun.json"

  new_result_class_verb_jsonpath = new_file_path + "_verb.json"

  new_result_class_adj_jsonpath = new_file_path + "_adj.json"

  new_result_class_noun_csvpath = new_file_path + "_noun.csv"

  new_result_class_verbtrans_csvpath = new_file_path + "_verb_transitive.csv"

  new_result_class_verbintrans_csvpath = new_file_path + "_verb_intransitive.csv"

  new_result_class_adjattr_csvpath = new_file_path + "_adj_attribute.csv"

  new_result_class_adjgradable_csvpath = new_file_path + "_adj_gradable.csv"

  fileArr = [old_result_class_jsonpath, new_result_class_jsonpath, new_result_class_noun_jsonpath,
             new_result_class_verb_jsonpath,
             new_result_class_adj_jsonpath, new_result_class_noun_csvpath, new_result_class_verbtrans_csvpath,
             new_result_class_verbintrans_csvpath
    , new_result_class_adjattr_csvpath, new_result_class_adjgradable_csvpath]
  for file in fileArr:
    arrLen = len(file.split('/'))
    print(file)
    destPath = '/'.join(file.split('/')[0:-2]) + "/" + dir + "/new_" + dir + "/" + file.split('/')[-1]
    print(destPath)
    try:
      shutil.copy(file, destPath)
    except Exception:
      None"""

'''for dir in subDir:
  srcDir = path + dir + "/" + "new_" + dir + "/"
  destDir = '/Users/myahiatene/Desktop/results_all_classes_processed/new_results_all_classes' + "/" + srcDir.split('/')[
    -2]
  # print(srcDir + " - " + destDir)
  try:
    shutil.copytree(srcDir, destDir)
  except Exception as e:
    print(e)
'''
'''import ijson

noun_path = '/Users/myahiatene/Desktop/Bachelorarbeit/ontology-lexicalization_de/results_all_classes_processed/kaikki_dot_org-dictionary-German-by-pos-noun.json'

with open(noun_path, "rb") as f:
  for record in ijson.items(f, "", multiple_values=True):
    if record['word'] == 'Haus':
      print(record)
'''
