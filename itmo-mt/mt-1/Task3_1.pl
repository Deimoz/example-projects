#!/usr/bin/perl
use strict;
use warnings FATAL => 'all';
my $text = "";
while (<>) {
    $text = $text . $_;
}
$text =~ s/^\s+//;
$text =~ s/[\ \r\t\f]*\n[\ \r\t\f]*/\n/g;
$text =~ s/[\ \r\t\f]+/ /g;
$text =~ s/([\s]*\n){2,}/\n\n/g;
$text =~ s/\s+$/\n/;
print $text