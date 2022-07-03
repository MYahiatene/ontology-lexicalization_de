#!/usr/bin/env python3
import requests
import json
import os
import shutil

classes = ["Book", "Film", "River", "Person", "Actor", "Organization", "City", "Place", "Language", "Food"]
root_path = os.getcwd()
results_csv_path_src = os.getcwd() + "/results/"

'''def test():
    class_name = 'test'
    os.mkdir(root_path + f"/results_csv/{class_name}", mode=0o777)
    for file_name in os.listdir(results_csv_path_src):
        # construct full file path
        source = results_csv_path_src + file_name
        destination = root_path + f"/results_csv/{class_name}/" + file_name
        # copy only files
        if os.path.isfile(source):
            shutil.copyfile(source, destination)
            print('copied', file_name)
'''


def lexicalization(class_name):
    os.mkdir(root_path + f"/results_csv/{class_name}", mode=0o777)
    url = "http://localhost:8080/lexicalization"
    headers = {'Accept': 'application/json', 'Content-type': 'application/json'}
    data = {f"class_url": f"http://dbpedia.org/ontology/{class_name}", "minimum_entities_per_class": 100,
            "maximum_entities_per_class": 10000,
            "minimum_onegram_length": 4,
            "minimum_pattern_count": 5,
            "minimum_anchor_count": 10,
            "minimum_propertyonegram_length": 4,
            "minimum_propertypattern_count": 5,
            "minimum_propertystring_length": 5,
            "maximum_propertystring_length": 50,
            "minimum_supportA": 5,
            "minimum_supportB": 5,
            "minimum_supportAB": 5,
            "langTag": "de"}
    data = json.dumps(data)
    response = requests.post(url=url, data=data, headers=headers)
    with open(root_path + "/log_classes.txt", 'w+') as f:
        f.write(f"Class: {class_name} , Status Code:{response.status_code}")
    if response.status_code == 200:
        for file_name in os.listdir(results_csv_path_src):
            # construct full file path
            source = results_csv_path_src + file_name
            destination = root_path + f"/results_csv/{class_name}/" + file_name
            # copy only files
            if os.path.isfile(source):
                os.chmod(source, 0o777)
                os.chmod(destination, 0o777)
                shutil.copyfile(source, destination)
                print('copied', file_name)
    return response


if __name__ == '__main__':
    for class_name in classes:
        lexicalization(class_name)
