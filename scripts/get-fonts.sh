#!/bin/sh

# https://www.npmjs.com/package/get-google-fonts

node ./node_modules/get-google-fonts/cli.js \
  -i "https://fonts.googleapis.com/css?family=Roboto:400,700&subset=cyrillic" \
  -o "./target/node_modules/public/fonts-google" \
  -p "/r/fonts-google/fonts"
