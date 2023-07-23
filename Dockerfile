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

WORKDIR /app
COPY ./input /app/
COPY ./perl /app/
COPY ./post_processing /app/
COPY inputLex.json /app/
COPY lexicalizeAndCreateCSV.sh /app/
COPY requirements.txt /app/
RUN chmod +x /app

RUN python3 -m venv /app
RUN source /app/venv/bin/activate
RUN pip3 install -r /app/requirements.txt
CMD ["./lexicalizeAndCreateCSV"]
