#!/bin/sh

echo "cleaning artefacts"

rm target/ -r -f
rm .shadow-cljs/ -r -f
rm node_modules -r -f
rm .cpcache -r -f

rm .webly -r -f

rm package.json -f
rm package-lock.json -f
rm karma.conf.js -f
rm shadow-cljs.edn -f



