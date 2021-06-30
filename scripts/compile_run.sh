#!/bin/sh

clojure -X:webly :profile '"npm-install"'
clojure -X:webly :profile '"release"'
clojure -X:webly :profile '"jetty"'