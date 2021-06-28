#!/bin/sh

clojure -X:webly :profile '"ci"'
npm test