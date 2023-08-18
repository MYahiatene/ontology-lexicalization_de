# Automatic induction of lexicalization for multilingual question answering over linked data

TODO: Add description


## Execute the System
added platform support for linux/amd64,linux/arm64
### Prerquisites

about 7gb of disk space

docker version >= 20

### Method 1 (docker)
clone the repository:
`git clone`
change to repository folder:
`cd name`
pull the latest image:
`docker pull myahiatene/ontology-lexicalization_de`
start the container for all classes
`docker run myahiatene/ontology-lexicalization_de -v ./:/app/results_csv/`

`docker run -it --name=ontology-lexicalization_de --rm -v "./:/app/" myahiatene/ontology-lexicalization_de:latest /bin/bash  -c "$(cat ./lexicalizeAndCreateCSV.sh)"`
todo: delete this and use volumes instead`docker cp lexicalization_de:/app/post_processing/csv_results/ .`

### Method 2 

### Prerequisites

pyhton 3.11

perl 5 including dependencies and module

TODO:
chmod u+x lexicalizeAndCreateCSV.sh
./lexicalizeAndCreateCSV.sh
## Developers

* **Mokrane Yahiatene**

## Supervisors

* **Dr. Philipp Cimiano**
* **Mohammad Fazleh Elahi**

---


