#!/bin/sh

gorillamd="target/node_modules/public/gorillamd"

echo "copying markdown resources.."
mkdir -p  $gorillamd

cp README.md $gorillamd/webly.md
cp README-oauth2.md $gorillamd/webly-oauth2.md
cp README-analytics.md $gorillamd/webly-ga.md
cp README-developer.md $gorillamd/webly-dev.md

cp README-reagent.md $gorillamd/webly-reagent.md
cp README-tailwind.md $gorillamd/webly-tailwind.md