#!/bin/sh

cd demo

echo "TEST CLJ ..."
clojure -X:test-clj


echo "TEST CLJS ..."
clojure -X:webly:npm-install
clojure -X:webly:ci
npm test

