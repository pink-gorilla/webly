(ns demo
  (:require
   [clojure.pprint :refer [print-table]]
   [taoensso.timbre :as timbre :refer [info error]]
   [modular.config :refer [load-config!]]
   [rest.google :refer [google]]
   [rest.github :refer [github]]
   [rest.xero :refer [xero]]))


(load-config! "demo-config.edn")

(defn make-requests [{:keys [provider]}]
  ; cli entry poinit
  (case provider
    :xero (xero)
    :google (google)
    :github (github)
    ;
    ))














