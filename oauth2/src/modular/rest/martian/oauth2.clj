(ns modular.rest.martian.oauth2
  (:require
   [clojure.pprint :refer [print-table]]
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   ;[clj-http.client :as http]
   [martian.core :as martian]
   [martian.interceptors :as interceptors]
   [martian.clj-http :as martian-http]
   [modular.config :refer [get-in-config]]
   [modular.oauth2.token.store :refer [load-token]]))

; token-prefix could be:
; "Token: "
; "Bearer "

(defn add-authentication-header [provider]
  {:name ::add-authentication-header
   :enter (fn [ctx]
            (let [token-prefix (get-in-config [:oauth2 provider :token-prefix])
                  token (load-token provider)
                  access-token (:access-token token)]
              (debug "provider " provider "config: " (get-in-config [:oauth2 provider]))
              (debug "provider " provider "prefix: " token-prefix)
              (assoc-in ctx
                        [:request :headers "Authorization"]
                        (str token-prefix access-token))))})

(defn interceptors [provider]
  {:interceptors
   (concat
    martian/default-interceptors
    [(add-authentication-header provider)
     interceptors/default-encode-body
     interceptors/default-coerce-response
     martian-http/perform-request])})

(defn martian-oauth2 [provider base-url endpoints]
  (let [m (martian-http/bootstrap base-url endpoints (interceptors provider))]
    m))
