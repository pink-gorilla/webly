(ns webly.app.routes-resolve
  (:require
   [clojure.string]
   [taoensso.timbre :refer [debug info error]]))

(defn get-handler-backend-symbol [s]
  (try
    (info "resolving handler symbol: " s)
    (requiring-resolve s)
    (catch Exception ex
      (error "api-handler-symbol resolve exception: " ex)
      (throw ex))))

