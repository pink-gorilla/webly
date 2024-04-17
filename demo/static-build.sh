#!/bin/sh
rm target -r
rm .shadow-cljs -r
clojure -X:webly:npm-install
# clojure -X:webly:release-adv
clojure -X:webly:compile
clojure -X:webly:static
cp node_modules/@icon target/static/r -r