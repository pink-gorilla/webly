(ns demo
  (:require
   [clojure.pprint :refer [print-table]]
   [taoensso.timbre :as timbre :refer [info error]]
   [modular.config :refer [load-config!]]
   ; token info
   [modular.oauth2.token.info :as t]
   [token-info :refer [show]]
   ; rest
   [rest.google :refer [google]]
   [rest.github :refer [github]]
   [rest.xero :refer [xero]]))


(load-config! ["webly/config.edn" "demo-config.edn" "creds.edn"])

(defn make-requests [{:keys [provider]}]
  ; cli entry point
  (case provider
    :xero (xero)
    :google (google)
    :github (github)
    ;
    ))

(defn token-info [{:keys [provider]}]
  ; cli entry point
  (show provider))


(defn tokens-summary [{:keys [providers]}]
  ; cli entry point
  (info "providers list: " providers)
  (-> (t/tokens-summary providers)
      (print-table ))
  (->> (t/tokens-summary-map providers)
      (info "token summary map:" )) 
      
      )












