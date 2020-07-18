#!/bin/bash

mkdir -p target/node_modules/public/tailwindcss/dist
cp node_modules/tailwindcss/dist/*.*  target/node_modules/public/tailwindcss/dist

mkdir -p target/node_modules/public/@fortawesome/fontawesome-free/css
cp node_modules/@fortawesome/fontawesome-free/css/*.*  target/node_modules/public/@fortawesome/fontawesome-free/css

mkdir -p target/node_modules/public/@fortawesome/fontawesome-free/webfonts
cp node_modules/@fortawesome/fontawesome-free/webfonts/*  target/node_modules/public/@fortawesome/fontawesome-free/webfonts
