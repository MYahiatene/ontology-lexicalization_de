import os
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

for dir in subDir:
  srcDir = path + dir + "/" + "new_" + dir + "/"
  destDir = '/Users/myahiatene/Desktop/results_all_classes_processed/new_results_all_classes' + "/" + srcDir.split('/')[
    -2]
  # print(srcDir + " - " + destDir)
  try:
    shutil.copytree(srcDir, destDir)
  except Exception as e:
    print(e)
