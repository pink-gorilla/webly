(ns modular.ws.adapter.httpkit
  (:require
   [taoensso.timbre :as log :refer [debug info infof]]
   [taoensso.sente.server-adapters.http-kit]))

(defn get-sch-adapter []
  (debug "websocket mode: httpkit")
  (require '[taoensso.sente.server-adapters.http-kit])
  taoensso.sente.server-adapters.http-kit/get-sch-adapter)