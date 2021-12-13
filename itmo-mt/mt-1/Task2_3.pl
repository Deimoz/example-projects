#!/usr/bin/perl
use strict;
use warnings FATAL => 'all';
while (<>) {
    s/\b(a+)\b/argh/i;
    print;
}