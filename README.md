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
`docker run myahiatene/ontology-lexicalization_de -v <absolute path of your current working directory>/results_csv:/app/results_csv/`
start the container for explicitly specified classes. Here an example fo 2 classes:
`docker run myahiatene/ontology-lexicalization_de -v <absolute path of repository directory>/results_csv:/app/post_processing/results_csv/ "Actor" "Politician"`

## Developers

* **Mokrane Yahiatene**

## Supervisors

* **Dr. Philipp Cimiano**
* **Mohammad Fazleh Elahi**

---


