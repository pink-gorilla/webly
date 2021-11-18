#!/bin/sh



cd ./certs/live/wien.hoertlehner.com && \
openssl pkcs12 \
    -export \
    -CAfile chain.pem \
    -caname root \
    -in fullchain.pem \
    -inkey privkey.pem \
    -out ../../keystore.p12 \
    -passout pass:password \
    -name wien

chmod a+r ../../keystore.p12


#  -password file:/opt/crickam/keystore.pw
