import json
from fastapi import FastAPI
import stanza
import spacy_stanza
from pydantic import BaseModel

# Download the stanza model if necessary
stanza.download("de")

# Initialize the pipeline
nlp = spacy_stanza.load_pipeline("de")


class Text(BaseModel):
    text: str


app = FastAPI()


@app.get("/")
def read_root():
    return "This is a small REST Api for Spacy_Stanza."


@app.post("/text")
async def read_item(text: Text):
    doc = nlp(text.text)
    nlp_list = []
    for token in doc:
        nlp_list.append([token.text, token.lemma_, token.pos_])
    return nlp_list
