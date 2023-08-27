#!/usr/bin/env python3
import os

import pandas as pd
import bz2
import glob
from evaluation import lift, leverage, conv
from qald7_properties import qald7_properties
from de_properties_map import de_properties_map

csv_files = sorted(glob.glob(
    '../results/*.csv.bz2'), key=lambda rule: rule.split('-')[3])


def checkQald7_and_replace(predicate: str) -> str:
    mapped_predicate = de_properties_map.get(predicate, None)
    if predicate in qald7_properties:
        return predicate
    elif mapped_predicate is not None:
        return mapped_predicate
    else:
        return ''


def merge_csvs_and_add_metrics():
    csv_df_list = []
    print('Start merging and adding metrics...\n')
    if 'merged_rules.csv' not in os.listdir('csv_results') or os.stat('csv_results/merged_rules.csv').st_size == 0:
        for file in csv_files:
            with bz2.open(file, mode='rt') as f:
                csv_temp = pd.read_csv(f)
                csv_temp['predicate'] = csv_temp.apply(lambda x: checkQald7_and_replace(x['predicate']), axis=1)
                csv_temp = csv_temp.drop(csv_temp[csv_temp['predicate'] == ''].index)
                csv_temp['Lift'] = csv_temp.apply(lambda x: lift(x['supA'], x['supAB'], x['supB']), axis=1)
                csv_temp['Leverage'] = csv_temp.apply(lambda x: leverage(x['supAB'], x['supA'], x['supB']), axis=1)
                csv_temp['Conviction'] = csv_temp.apply(lambda x: conv(x['supAB'], x['supA'], x['supB']), axis=1)
                csv_df_list.append(csv_temp)
        csv_df = pd.concat(csv_df_list, ignore_index=True)
        csv_df.to_csv('csv_results/merged_rules.csv')
    else:
        csv_df = pd.read_csv('csv_results/merged_rules.csv')
    print("Merging done and metrics added!\n")
    return csv_df


# ['Cosine', 'Conviction', 'Leverage', 'Lift']

def create_rank_metric_csvs(merged_csv, ranks, metrics):
    csv_results = []
    for metric in metrics:
        for rank in ranks:
            print(f'Extracting rank: {rank} and metric:{metric}\n')
            csv_df = merged_csv.sort_values(metric, inplace=False, ascending=False)
            csv_results.append((csv_df.head(rank), rank, metric))
            print(f'Done.\n')
    return csv_results
