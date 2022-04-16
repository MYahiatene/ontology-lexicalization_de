#!/usr/bin/perl -w
# vi:si:ts=4:sw=4
use strict;
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

my $user_data;
my $test= "Hallo";
$user_data->{test_user}->{1256489043}->{STATUS}     =  "RUN";
$user_data->{test_user}->{1256489043}->{MEM}        =  "51591";
$user_data->{test_user}->{1256489043}->{RUN_TIME}   =  "41410";
$user_data->{test_user}->{1256489043}->{PROJ_NAME}  =  "unkown";
$user_data->{test_user}->{1256489043}->{GROUP}      =  "default";
$user_data->{test_user}->{1256489043}->{DATE}       =  "Aug 17 05:23";

$user_data->{test_user_2}->{528562752}->{STATUS}     =  "RUN";
$user_data->{test_user_2}->{528562752}->{MEM}        =  "591";
$user_data->{test_user_2}->{528562752}->{RUN_TIME}   =  "46410";
$user_data->{test_user_2}->{528562752}->{PROJ_NAME}  =  "unkown";
$user_data->{test_user_2}->{528562752}->{GROUP}      =  "default";
$user_data->{test_user_2}->{528562752}->{DATE}       =  "Aug 17 05:23";

store (\$user_data, 'perl/temp_jobs.txt') or die "can't store data to $!";
my $data = retrieve('perl/temp_jobs.txt');
my $json= encode_json $user_data;
print  encode_json $json;


#print "Hash 1\n";
#print Dumper \$user_data;

#print "Hash2\n";
#print Dumper \$data;