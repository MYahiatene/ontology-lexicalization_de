FROM python:3.8.15-buster

WORKDIR /app
RUN pip install stanza
RUN pip install spacy_stanza
RUN pip install fastapi
RUN pip install "uvicorn[standard]"
RUN pip install pydantic


COPY . .

CMD ["uvicorn","main:app","--host","0.0.0.0", "--port", "80"]
