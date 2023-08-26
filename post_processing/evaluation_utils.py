#!/usr/bin/env python3
import os

import pandas as pd
import bz2
import glob
from qald7_properties import qald7_properties
from evaluation import lift, leverage, conv

csv_files = sorted(glob.glob(
    '../results/*.csv.bz2'), key=lambda rule: rule.split('-')[3])


def merge_csvs():
    path = 'csv_results/merged_association_rules.csv'
    if os.stat(path).st_size == 0:
        os.unlink(path)
    csv_df_list = []
    for file in csv_files:
        with bz2.open(file, mode='rt') as f:
            csv_df_list.append(pd.read_csv(f))
    csv_df = pd.concat(csv_df_list, ignore_index=True)
    csv_df.to_csv(path)


def create_rank_metric_csvs(ranks, metrics):
    csv_results = []
    csv_df = pd.read_csv('csv_results/merged_association_rules.csv')
    for index, row in csv_df.iterrows():
        row['Lift'] = lift(row['condBA'], row['supB'])
        row['Leverage'] = leverage()
        row['Conviction'] = conv(row['supB'], row['condBA'])
    for metric in metrics:
        for rank in ranks:
            csv_df_metric = csv_df.sort_values(metric, inplace=True, ascending=False).head(rank)
            csv_results.append(csv_df_metric)
