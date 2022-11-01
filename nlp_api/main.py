from fastapi import FastAPI
import stanza
from pydantic import BaseModel

# Download the stanza model if necessary

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
    stanza.download(lang)
    global nlp
    nlp = stanza.Pipeline(lang, processors=["tokenize", "mwt", "lemma", "pos"])
    return "Language {lang} downloaded and initialized.".format(lang=lang)


@app.post("/text")
async def read_item(text: Text):
    doc = nlp(text.text)
    nlp_list = []
    for sentence in doc.sentences:
        for word in sentence.words:
            nlp_list.append([word.text, word.lemma, word.upos])
    return nlp_list
