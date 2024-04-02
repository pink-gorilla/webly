#!/usr/bin/env /home/florian/babashka/bb

(require '[babashka.deps :as deps])
(deps/add-deps
 '{:deps {org.babashka/http-server {:mvn/version "0.1.11"}}})

(require '[babashka.http-server :as http-server])

(println "open http://localhost:8080/static")

(http-server/exec {:port 8080
              :dir "./target"})
