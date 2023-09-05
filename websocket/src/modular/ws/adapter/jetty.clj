(ns modular.ws.adapter.jetty
  (:require
   [taoensso.timbre :as log :refer [debug info infof]]
   [taoensso.sente.server-adapters.jetty9]))

(defn get-sch-adapter []
  (debug "websocket mode: jetty.")
  (require '[taoensso.sente.server-adapters.jetty9])
  taoensso.sente.server-adapters.jetty9/get-sch-adapter)