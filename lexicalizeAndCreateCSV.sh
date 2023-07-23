#!/usr/bin/env bash

export SHELL=$(type -p bash)

startJobForClass() {
  INTER_DIR=./inter
  if [ -d "$INTER_DIR" ]; then
    rm -r "$INTER_DIR"
  fi
  cat <<EOF >inputLex.json
    {"class_url": $1
                 "minimum_entities_per_class": 100,
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
                 "minimum_supportAB":5,
                 "langTag": "de"}
EOF
  ./perl/experiment.pl
}

export -f startJobForClass

if [ -z "$1" ]; then
  while IFS= read -r line; do
    startJobForClass "$line"
  done <./input/classes.txt
fi
cd post_processing || exit
./post_process.py

#TODO: Parallel not working yet cause of perl inter folder and files unique
#if [[ $1 =~ ^[0-9]+$ ]]; then
#  parallel -j "$1" -a ./input/classes.txt startJobForClass {}
#fi
