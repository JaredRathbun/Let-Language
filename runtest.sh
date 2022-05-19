#!/bin/sh

mkdir -p results
for i in $(ls tests/*.let); do
  printf "Processing test %13s . . . " `basename $i .let`
  out="results/"`basename $i .let`".expected"
  java -jar dist/let-lang.jar $i &> $out
  echo "[ DONE ]"
done
