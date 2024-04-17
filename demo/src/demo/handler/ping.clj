(ns demo.handler.ping
  (:require
   [clojure.pprint]))

(defn ping-handler [req]
  (clojure.pprint/pprint req)
  {:status 200 :body "test"})