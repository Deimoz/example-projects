#!/usr/bin/perl
use strict;
use warnings FATAL => 'all';
while (<>) {
    s/([a-zA-Z])(\g1*)\g1/$1/g;
    print;
}