#!/bin/bash

for f in *.xml
do
    echo "Processing $f ..."
    cat insert.txt $f > $f.tmp && mv $f.tmp $f
    echo "</movie>" >> $f
done
