(ns demo
  (:require
   [clojure.pprint :refer [print-table]]
   [taoensso.timbre :as timbre :refer [info error]]
   [modular.config :refer [load-config! get-in-config]]
   ; token info
   [modular.oauth2.token.info :as t]
   [token-info :refer [show]]
   ; rest
   [rest.google :refer [google]]
   [rest.github :refer [github]]
   [rest.xero :refer [xero]]
   [rest.email :refer [email]]
   [rest.telegram :refer [telegram]]
   ; martian apis:
   [rest.woo :refer [woo]]
   [rest.pets :refer [pets]]
   [rest.wordpress :refer [wordpress]]
   
   ))


(load-config! ["webly/config.edn" "demo-config.edn" "creds.edn"])

(info "oauth2 config: " (get-in-config [:oauth2]))

(defn make-requests [{:keys [provider]}]
  ; cli entry point
  (case provider
    :xero (xero)
    :google (google)
    :github (github)
    :email (email)
    :telegram (telegram)
    :woo (woo)
    :pets (pets)
    :wordpress (wordpress)
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












