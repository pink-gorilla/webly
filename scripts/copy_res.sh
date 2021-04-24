#!/bin/sh


cpr () {
  sourcedir="node_modules/$1"
  source="$sourcedir/$2"
  targetdir="target/node_modules/public/$1" 
  if [ -d $sourcedir ]; then
     echo "copying $source ==> $targetdir"
     mkdir -p $targetdir
     cp $source $targetdir
  else 
    echo "ERROR: $sourcedir does not exist."
  fi
}


#mkdir -p target/node_modules/public/tailwindcss/dist
# cp node_modules/tailwindcss/dist/*.*  target/node_modules/public/tailwindcss/dist
cpr "tailwindcss/dist" "*.*" 

#mkdir -p target/node_modules/public/@fortawesome/fontawesome-free/css
# cp node_modules/@fortawesome/fontawesome-free/css/*.*  target/node_modules/public/@fortawesome/fontawesome-free/css
cpr @fortawesome/fontawesome-free/css "*.*"

#mkdir -p target/node_modules/public/@fortawesome/fontawesome-free/webfonts
# cp node_modules/@fortawesome/fontawesome-free/webfonts/*  target/node_modules/public/@fortawesome/fontawesome-free/webfonts
cpr @fortawesome/fontawesome-free/webfonts "*.*"

cpr @icon/fontisto-emoji "fontisto*.*"
cpr @icon/fontisto-emoji/icons "*.svg"
