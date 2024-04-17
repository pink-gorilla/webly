#!/bin/sh

echo "WEBLY"
cd webly
clojure -M:cljfmt fix
clojure -M:lint src

echo "SPA"
cd ../spa
clojure -M:cljfmt fix
clojure -M:lint src

echo "DEMO"
cd ../demo
clojure -M:cljfmt fix
clojure -M:lint src
