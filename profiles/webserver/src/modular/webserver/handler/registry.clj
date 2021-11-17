(ns modular.webserver.handler.registry
  (:require
   [clojure.string]
   [taoensso.timbre :refer [debug info error]]))

(defonce handler-registry
  (atom {}))

(defn add-ring-handler [key handler]
  (swap! handler-registry assoc key handler))