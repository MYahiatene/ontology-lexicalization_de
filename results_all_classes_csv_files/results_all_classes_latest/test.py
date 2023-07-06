import requests

response = requests.get('https://de.wiktionary.org/w/api.php?action=parse&page=lächeln&prop=wikitext&format=json')
json_response = response.json()['parse']['wikitext']['*']
transitive_verb_json_arr = json_response.split('\n\n')
'''transitive_verb_json_arr = json_response.split('\n\n')[1].split('\n')
written_form_infinitive = 'query parameter'
written_form_3rd_present = transitive_verb_json_arr[3].split('=')[1]
written_form_3rd_past = transitive_verb_json_arr[4].split('=')[1]
written_form_3rd_perfect = transitive_verb_json_arr[5].split('=')[1]'''

# print(transitive_verb_json_arr)

for el in transitive_verb_json_arr:
    if "{{K|intrans.}}" in el:
        print("found")

# {{K|intrans.}}
