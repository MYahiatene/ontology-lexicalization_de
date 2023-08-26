#!/usr/bin/env python3
import pandas as pd
import bz2
import glob
from evaluation import lift, leverage, conv

csv_files = sorted(glob.glob(
    '../results/*.csv.bz2'), key=lambda rule: rule.split('-')[3])


def merge_csvs_and_add_metrics():
    csv_df_list = []
    print('Start merging and adding metrics...\n')
    for file in csv_files:
        with bz2.open(file, mode='rt') as f:
            csv_temp = pd.read_csv(f)
            csv_temp['Lift'] = csv_temp.apply(lambda x: lift(x['condBA'], x['supB']), axis=1)
            csv_temp['Leverage'] = csv_temp.apply(lambda x: leverage(), axis=1)
            csv_temp['Conviction'] = csv_temp.apply(lambda x: conv(x['supB'], x['condBA']), axis=1)
            csv_df_list.append(csv_temp)
    csv_df = pd.concat(csv_df_list, ignore_index=True)
    print("Merging done and metrics added!\n")
    return csv_df


def create_rank_metric_csvs(merged_csv,ranks, metrics):
    csv_results = []
    for metric in metrics:
        for rank in ranks:
            print(f'Extracting rank: {rank} and metric:{metric}\n')
            merged_csv.sort_values(metric, inplace=True, ascending=False)
            csv_results.append(merged_csv.head(rank))
            print(f'Done.\n')
    return csv_results
