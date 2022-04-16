#!/usr/bin/perl -w
# vi:si:ts=4:sw=4
use strict;
use warnings;
use YAML::Syck qw(LoadFile DumpFile Load Dump);
use IO::Uncompress::Bunzip2 '$Bunzip2Error';
use URL::Encode qw(url_encode_utf8);
use Number::Bytes::Human qw(format_bytes);
use Text::CSV;
use JSON;
use Data::Dumper;
use FileHandle;
use File::Basename;
use utf8;
binmode STDOUT, ':utf8';
use File::Slurp;
use JSON::Parse ':all';
use Term::ReadKey;
use Data::Dumper;
use FindBin;
use Storable;
use JSON;
use File::Slurp;
my $e= "http://de.dbpedia.org/resource/Die_Hosen_des_Ritters_von_Bredow";
if ( $e =~ m/http:\/\/(.*)dbpedia.org\/resource\/(.*)/){
    print 'hello';
}