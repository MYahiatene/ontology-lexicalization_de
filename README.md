# Automatic induction of lexicalization for multilingual question answering over linked data

TODO: Add description



added platform support for linux/amd64,linux/arm64

### Prerequisites

about 7gb of disk space

docker version >= 20

## Execute the System

pull the latest image:
`docker pull myahiatene/ontology-lexicalization_de`

You can either run the project for all 354 classes or explicitly defining the classes to be run:

start the container for all classes:
`docker run --rm -v <path on your local machine where your results should be stored>:/app/post_processing/csv_results myahiatene/ontology-lexicalization_de`

Example of 2 classes:
`docker run --rm -v <path on your local machine where your results should be stored>:/app/post_processing/csv_results myahiatene/ontology-lexicalization_de "Person" "Actor"`

## Developers

* **Mokrane Yahiatene**

## Supervisors

* **Dr. Philipp Cimiano**
* **Mohammad Fazleh Elahi**

---


