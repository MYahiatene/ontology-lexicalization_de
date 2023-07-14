import requests


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


'''
#                       noun case
response = requests.get('https://de.wiktionary.org/w/api.php?action=parse&page=Schwimmer&prop=wikitext&format=json')
print(response.elapsed.total_seconds())
json_response = response.json()['parse']['wikitext']['*']
noun_json_arr = json_response.split('\n\n')[1].split('\n')

#   genus
genus = noun_json_arr[1].split('=')[1]
nominativ_singular = noun_json_arr[2].split('=')[1]
nominativ_plural = noun_json_arr[3].split('=')[1]
akkusativ_singular = noun_json_arr[8].split('=')[1]
dativ_singular = noun_json_arr[6].split('=')[1]
genetiv_singular = noun_json_arr[4].split('=')[1]
'''


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


#               transitiveFrame and intransitive case
'''response = requests.get('https://de.wiktionary.org/w/api.php?action=parse&page=entwickeln&prop=wikitext&format=json')
json_response = response.json()['parse']['wikitext']['*']
transitive_verb_json_arr = json_response.split('\n\n')[1].split('\n')
written_form_infinitive = 'query parameter'
written_form_3rd_present = transitive_verb_json_arr[3].split('=')[1]
written_form_3rd_past = transitive_verb_json_arr[4].split('=')[1]
written_form_3rd_perfect = transitive_verb_json_arr[5].split('=')[1]
'''

'''#               adjective
response = requests.get('https://de.wiktionary.org/w/api.php?action=parse&page=australisch&prop=wikitext&format=json')
json_response = response.json()['parse']['wikitext']['*']
print(json_response)'''
