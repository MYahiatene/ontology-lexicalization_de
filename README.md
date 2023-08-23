# Automatic induction of lexicalization for multilingual question answering over linked data

TODO: Add description

Pipeline exists of 2 major steps:

- Creating association rules for specified classes with extended LexExMachina (takes about 1 week for all classes)
- Automatic induction of association rules to csv files for nouns verbs and adjectives (takes about 1 hour for all
  classes)

- added platform support for linux/amd64,linux/arm64

## Method 1 (docker)

### Prerequisites

about 7gb of disk space for the docker container 

memory resource allocation inside the docker container >= 6GB (prüfen)

docker version >= 20

## Execute the System

pull the latest image:
`docker pull myahiatene/ontology-lexicalization_de`

You can either run the project for all 354 classes or explicitly defining the classes to be run:

start the container for all classes:
`docker run --rm -v <path on your local machine where your results should be stored>:/app/post_processing/csv_results myahiatene/ontology-lexicalization_de`

Example of 2 classes:
`docker run --rm -v <path on your local machine where your results should be stored>:/app/post_processing/csv_results myahiatene/ontology-lexicalization_de "Person" "Actor"`

## Method 2 (local execution)

Todo: 

## Developers

* **Mokrane Yahiatene**

## Supervisors

* **Dr. Philipp Cimiano**
* **Mohammad Fazleh Elahi**

---


