#!/bin/sh

# keytool -keysize 2048 -genkey -alias jetty -keyalg RSA -keystore jetty.keystore

certbot certonly \
 -w ./certs \
 -d wien.hoertlehner.com \
 --logs-dir /tmp \
 --config-dir ./certs \
 --work-dir ./certs \
 --standalone

# --webroot \


# https://github.com/DerGuteMoritz/clj-oauth2/issues?q=is%3Aissue+is%3Aclosed

# https://gist.github.com/karanth/8633258

# https://github.com/codecitizen/clj-jwt/blob/master/src/jwt.clj

#https://github.com/riemann/riemann

#  mega geil: jwt claim google
#  https://gist.github.com/arohner/8d94ee5704b1c0c1b206186525d9f7a7

# gute doc.
# https://www.sorcerers-tower.net/articles/configuring-jetty-for-https-with-letsencrypt

#  https://coderwall.com/p/y9w4-g/google-oauth2-in-clojure
