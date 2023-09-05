(ns modular.ws.adapter.undertow
  (:require
   [taoensso.timbre :as log :refer [debug info infof]]
   [taoensso.sente.server-adapters.undertow]))

(defn get-sch-adapter []
  (debug "websocket mode: undertow.")
  (require '[taoensso.sente.server-adapters.undertow])
  taoensso.sente.server-adapters.undertow/get-sch-adapter)