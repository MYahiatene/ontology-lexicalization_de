from fastapi import FastAPI
import stanza
import spacy_stanza
from pydantic import BaseModel
from fastapi.logger import logger
import logging

gunicorn_logger = logging.getLogger('gunicorn.error')
logger.handlers = gunicorn_logger.handlers
if __name__ != "main":
    logger.setLevel(gunicorn_logger.level)
else:
    logger.setLevel(logging.DEBUG)
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
    nlp = spacy_stanza.load_pipeline(lang)
    return "Language {lang} downloaded and initialized.".format(lang=lang)


@app.post("/text")
async def read_item(text: Text):
    doc = nlp(text.text)
    nlp_list = []
    for token in doc:
        nlp_list.append([token.text, token.lemma_, token.pos_])
    logger.error(nlp_list)
    return nlp_list
