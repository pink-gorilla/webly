#!/bin/sh


cpr () {
  sourcedir="node_modules/$1"
  source="$sourcedir/$2"
  targetdir="../demo-webly/target/node_modules/public/$1" 
  if [ -d $sourcedir ]; then
     echo "copying $source ==> $targetdir"
     mkdir -p $targetdir
     cp $source $targetdir
  else 
    echo "ERROR: $sourcedir does not exist."
  fi
}


