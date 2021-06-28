#!/bin/sh

clojure -X:webly :profile '"npm-install"'
./scripts/copy_res.sh
./scripts/get-fonts.sh
./scripts/copy-md.sh