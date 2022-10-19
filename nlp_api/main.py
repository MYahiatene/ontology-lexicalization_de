from fastapi import FastAPI
import stanza
# import spacy_stanza
from pydantic import BaseModel

# Initialize the pipeline
nlp = None


class Text(BaseModel):
    text: str


app = FastAPI()


@app.get("/")
def info():
    return "This is a small REST Api for stanza nlp."


@app.get("/{lang}")
async def read_root(lang: str) -> str:
    global nlp
    nlp = stanza.Pipeline(lang, processors='tokenize,pos,lemma', verbose=True)
    return "Language {lang} downloaded and initialized.".format(lang=lang)


@app.post("/text")
async def read_item(text: Text):
    doc = nlp(text.text)
    return [[word.text, word.lemma, word.pos]
            for sent in doc.sentences for word in sent.words]
