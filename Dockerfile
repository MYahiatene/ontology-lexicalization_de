FROM python:3.11
# Update the repository sources list
RUN apt-get update

# Install compiler and perl stuff
RUN apt-get install --yes \
 build-essential \
 gcc \
 apt-utils \
 perl \
 expat \
 libexpat-dev 

# Install perl modules 
RUN apt-get install -y cpanminus

RUN cpanm CPAN::Meta \
 readline \
 Term::ReadKey \
 YAML \
 Digest::SHA \
 Module::Build \
 ExtUtils::MakeMaker \
 Test::More \
 Data::Stag \
 Config::Simple \
 Statistics::Lite \
 Statistics::Descriptive \
 YAML::Syck \
 URL::Encode \
 Number::Bytes::Human \
 JSON \
 Data::Dumper \
 FileHandle \
 File::Basename \
 utf8 \
 File::Slurp \
 JSON::Parse \
 Term::ReadKey \
 Data::Dumper \
 Text::CSV

# wget installation
# Install wget and install/updates certificates
RUN apt-get update \
 && apt-get install -y -q --no-install-recommends \
    ca-certificates \
 && apt-get clean \
 && rm -r /var/lib/apt/lists/*




USER root
SHELL ["/bin/bash", "-c"]
WORKDIR /app
COPY ./input ./input
COPY ./perl ./perl
COPY ./post_processing ./post_processing
COPY inputLex.json .
COPY lexicalizeAndCreateCSV.sh .
COPY requirements.txt .
RUN mkdir "results"
RUN mkdir "inter"
RUN chmod -R u+x .

RUN python -m pip install -r requirements.txt
RUN  virtualenv venv
RUN . venv/bin/activate
CMD ["./lexicalizeAndCreateCSV.sh"]
