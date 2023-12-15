# Imports
import os
import sys
from enum import Enum
from nltk.corpus import stopwords
from nltk.stem.snowball import PorterStemmer
from jproperties import Properties
from gensim import corpora
from gensim.models import LsiModel
from gensim.models.coherencemodel import CoherenceModel
import matplotlib.pyplot as plt


class RepositoryCorpus:

    def __init__(self):
        self.repositories = []
        self.dictionary = []

    def add_repository(self, repository):
        self.repositories.append(repository)
        repository_data = []
        for data in repository.data.values():
            repository_data += data
        self.dictionary.append(repository_data)


class Repository:
    def __init__(self, name):
        self.name = name
        self.data = {}

    def add_data(self, name, data):
        self.data[name] = data


def load_properties():
    config = Properties()
    arg_length = len(sys.argv)
    if arg_length < 2:
        raise Exception("Please provide the properties file as an argument")

    with open(sys.argv[1], "rb") as config_file:
        config.load(config_file)

    print("Properties loaded!")
    return config


def preprocess_data(data, stop_words, stemmer):
    data = [i.lower() for i in data]
    stopped_data = [i for i in data if not i in stop_words]
    stemmed_data = [stemmer.stem(i) for i in stopped_data]
    return stemmed_data


def load_repository(folder, stop_words, stemmer, split):
    repository = Repository(folder.name)

    for file in os.scandir(folder):
        with open(file, "r") as f:
            repo_data = []
            filename = os.path.splitext(file.name)[0]
            if (split and not filename.endswith("_split")) or (not split and filename.endswith("_split")):
                continue
            for line in f:
                data = line.strip()
                repo_data.append(data)
            data = preprocess_data(repo_data, stop_words, stemmer)
            repository.add_data(filename, data)
    return repository


def load_repositories(config, split):
    repositories = RepositoryCorpus()
    output_folder = config["output-folder"].data
    stop_words = set(stopwords.words('english')).union(config["stopwords"].data.split(","))
    stemmer = PorterStemmer()

    for folder in os.scandir(output_folder):
        repositories.add_repository(load_repository(folder, stop_words, stemmer, split))
    return repositories


def prepare_corpus(corpus):
    dictionary = corpora.Dictionary(corpus.dictionary)
    matrix = [dictionary.doc2bow(repository) for repository in corpus.dictionary]
    return dictionary, matrix


def main():
    config = load_properties()
    repositories = load_repositories(config, True)
    dictionary, matrix = prepare_corpus(repositories)

    lsa = LsiModel(matrix, id2word=dictionary)
    print(lsa.print_topics())
    print("Done!")


if __name__ == "__main__":
    main()
