#!/bin/sh

gorillamd="target/node_modules/public/gorillamd"

echo "copying markdown resources.."
mkdir -p  $gorillamd

cp README.md $gorillamd/webly.md
cp README-oauth2.md $gorillamd/webly-oauth2.md