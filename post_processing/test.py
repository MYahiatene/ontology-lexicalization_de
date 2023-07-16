import requests
from multiprocessing import Pool


def request_word(word):
    return requests.get('https://de.wiktionary.org/w/api.php?action=parse&page=%s&prop=wikitext&format=json' % word)


if __name__ == '__main__':
    pool = Pool(2)
    result1 = pool.apply(request_word, ["Hund"])  # evaluate "solve1(A)" asynchronously
    result2 = pool.apply(request_word, ["klein"])
    print(result1.json()['parse']['wikitext']['*'])
    print(result2.json()['parse']['wikitext']['*'])
