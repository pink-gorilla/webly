(ns webly.app.routes-resolve
  (:require
   [clojure.string]
   [taoensso.timbre :refer [debug info error]]))

(defn resolve-symbol [s]
  (try
    (debug "resolving: " s)
    (requiring-resolve s)
    (catch Exception ex
      (error "Exception in resolving: " s)
      (throw ex))))

(defn get-handler-backend-symbol [handler-symbol]
  (if-let [handler (resolve-symbol handler-symbol)]
    handler
    (do (error "cannot get handler for symbol: " handler-symbol)
        nil)))