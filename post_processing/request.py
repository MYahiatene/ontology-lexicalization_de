import requests
import pandas as pd
import time


def get_noun_wiktionary_data(word: str):
    #                       noun case
    response = requests.get('https://de.wiktionary.org/w/api.php?action=parse&page=%s&prop=wikitext&format=json' % word)
    json_response = response.json()['parse']['wikitext']['*']
    noun_json_arr = json_response.split('\n\n')[1].split('\n')

    #   genus
    genus = noun_json_arr[1].split('=')[1]
    nominativ_singular = noun_json_arr[2].split('=')[1]
    nominativ_plural = noun_json_arr[3].split('=')[1]
    akkusativ_singular = noun_json_arr[8].split('=')[1]
    dativ_singular = noun_json_arr[6].split('=')[1]
    genetiv_singular = noun_json_arr[4].split('=')[1]
    return ['noun', genus, nominativ_singular, nominativ_plural, akkusativ_singular, dativ_singular, genetiv_singular]


def get_wiktionary_data(word: str, pos: str):
    try:
        if pos.startswith('N'):
            return get_noun_wiktionary_data(word)
        elif pos.startswith('V'):
            return get_verb_wiktionary_data(word)
    except Exception as e:
        return None


'''    elif pos.startswith('ADJ'):
        wiktionary_records = records_adj'''


def get_verb_wiktionary_data(word: str):
    response = requests.get(
        'https://de.wiktionary.org/w/api.php?action=parse&page=%s&prop=wikitext&format=json' % word)
    json_response = response.json()['parse']['wikitext']['*']
    transitive_verb_json_arr = json_response.split('\n\n')[1].split('\n')
    verb_type = "transitive"
    for el in json_response.split('\n\n'):
        if "{{K|intrans.}}" in el:
            verb_type = "intransitive"
    written_form_3rd_present = transitive_verb_json_arr[3].split('=')[1]
    written_form_3rd_past = transitive_verb_json_arr[4].split('=')[1]
    written_form_3rd_perfect = transitive_verb_json_arr[5].split('=')[1]
    return ['verb', written_form_3rd_present, written_form_3rd_past, written_form_3rd_perfect, verb_type]


'''def get_wiktionary_data_from_dumps(word: str, pos: str):
    if pos.startswith('N'):
        return get_noun_wiktionary_data(word)
    if pos.startswith('V'):
        return get_verb_wiktionary_data(word)
    if pos.startswith('ADJ'):
        None
'''


####################### Reading wiktionary from file and then creating a hashmap #################


def prepare_local_wiktionary_data():
    with open('kaikki_dot_org-dictionary-German-by-pos-noun.json', 'r') as file1, open(
            'kaikki_dot_org-dictionary-German-by-pos-verb.json', 'r') as file2, open(
        'kaikki_dot_org-dictionary-German-by-pos-adj.json', 'r') as file3:
        df_noun = pd.read_json(file1, lines=True)
        df2_noun = dict([(i, [x, y]) for i, x, y in zip(df_noun['word'], df_noun['forms'], df_noun['senses'])])
        df_verb = pd.read_json(file2, lines=True)
        df2_verb = dict([(i, [x, y]) for i, x, y in zip(df_verb['word'], df_verb['forms'], df_verb['senses'])])
        df_adj = pd.read_json(file3, lines=True)
        df2_adj = dict([(i, [x, y]) for i, x, y in zip(df_adj['word'], df_adj['forms'], df_adj['senses'])])
    return df2_noun, df2_verb, df2_adj


def get_noun_wiktionary_data_from_dumps(dict_noun, word: str):
    forms_senses_list = dict_noun.get(word, None)
    if forms_senses_list is None:
        return ['', '', '', '', '', '']
    forms = forms_senses_list[0]
    senses = forms_senses_list[1]
    genus = ''
    nominativ_singular = ''
    nominativ_plural = ''
    akkusativ_singular = ''
    dativ_singular = ''
    genetiv_singular = ''
    if type(forms) != float:
        for obj in forms:
            if 'nominative' in obj['tags'] and 'singular' in obj['tags']:
                nominativ_singular = obj['form']
            if 'nominative' in obj['tags'] and 'plural' in obj['tags']:
                nominativ_plural = obj['form']
            if 'accusative' in obj['tags'] and 'singular' in obj['tags']:
                akkusativ_singular = obj['form']
            if 'dative' in obj['tags'] and 'singular' in obj['tags']:
                dativ_singular = obj['form']
            if 'genitive' in obj['tags'] and 'singular' in obj['tags']:
                genetiv_singular = obj['form']
    if type(senses) != float:
        for obj in senses:
            if genus != '':
                break
            tags = obj.get('tags', None)
            if tags is None:
                continue
            for el in tags:
                if el == 'neuter' or el == 'masculine' or el == 'feminine':
                    genus = el
    return [genus, nominativ_singular, nominativ_plural, akkusativ_singular, dativ_singular, genetiv_singular]


# TODO: wen oder was? => transitiv
def get_verb_wiktionary_data_from_dumps(dict_verb, word: str):
    forms_senses_list = dict_verb.get(word, None)
    if forms_senses_list is None:
        return ['', '', '', '']
    forms = forms_senses_list[0]
    senses = forms_senses_list[1]
    written_form_3rd_present = ''
    written_form_3rd_past = ''
    written_form_3rd_perfect = ''
    verb_type = 'transitive'
    if type(forms) != float:
        for obj in forms:
            if 'present' in obj['tags'] and 'singular' in obj['tags'] and 'third-person' in obj['tags']:
                written_form_3rd_present = obj['form']
            if 'preterite' in obj['tags'] and 'singular' in obj['tags'] and 'third-person' in obj['tags']:
                written_form_3rd_past = obj['form']
            if 'perfect' in obj['tags'] and 'singular' in obj['tags'] and 'third-person' in obj['tags']:
                perfect = obj['form'].split(' ')
                written_form_3rd_perfect = perfect[1] if len(perfect) == 2 else perfect[0]
    # TODO: transitive or intransitive classification is relatively heuristic, could be done better
    if type(senses) != float:
        tags= None
        for sense in senses:
            tags = sense.get('tags')
            if tags is not None:
                break
        if tags is None:
            verb_type = 'transitive'
        elif 'intransitive' in tags and 'transitive' in tags:
            verb_type = 'intransitive/transitive'
        elif 'intransitive' in tags:
            verb_type = 'intransitive'
        elif 'transitive' in tags:
            verb_type = 'transitive'
    return [written_form_3rd_present, written_form_3rd_past, written_form_3rd_perfect, verb_type]


# TODO:
def get_adj_wiktionary_data_from_dumps(dict_adj, word: str):
    return dict_adj.get(word)


#     return ['verb', written_form_3rd_present, written_form_3rd_past, written_form_3rd_perfect, verb_type]
# TODO: implement wiktionary data retriever from json dumps
# dict_wiktionary_noun, dict_wiktionary_verb, dict_wiktionary_adj = prepare_local_wiktionary_data()
# tmp1 = get_noun_wiktionary_data_from_dumps(dict_wiktionary_noun,'Flugzeug')
# tmp2 = get_verb_wiktionary_data_from_dumps(dict_wiktionary_verb, 'existieren')
# tmp3 = get_verb_wiktionary_data_from_dumps(dict_wiktionary_verb, 'besitzen')
# tmp4 = dict_wiktionary_adj.get(dict_wiktionary_adj,'hoch')
# tmp5 = dict_wiktionary_adj.get(dict_wiktionary_adj,'deutsch')
# print(dict_wiktionary_verb.get('schwimmen')[1])
# print(dict_wiktionary_verb.get('wählen')[1])
# print(tmp1)
# print('\n')
# print(tmp2)
# print('\n')
# print(tmp3)
# print('\n')
'''print(len(tmp4))
print('\n')
print(tmp1[0])
print('\n')
print(tmp1[1])
print('\n')
print(tmp2[0])
print('\n')
print(tmp2[1])
print('\n')
print(tmp3[0])
print('\n')
print(tmp3[1])
print('\n')
print(tmp4[0])
print('\n')
print(tmp4[1])
print('\n')'''
'''print(tmp5[0])
print('\n')
print(tmp5[1])
print('\n')'''

# get_noun_wiktionary_data_from_dumps('Flugzeug')
'''start = time.time()
# takes about 23 seconds on my machine

end = time.time()
print(end - start)
'''
