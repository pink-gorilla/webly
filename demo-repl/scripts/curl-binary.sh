#!/bin/sh
curl  http://localhost:8000/api/bindata \
     -v \
     -O /tmp/blog.bin

#      -H 'Accept: application/transit+json' \