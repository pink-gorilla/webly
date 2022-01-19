(ns modular.rest.auth
  (:require
   [clojure.pprint :refer [print-table]]
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [modular.config :refer [get-in-config]]
   [modular.base64 :refer [base64-encode]]
   [clj-http.client :as http]
   [ajax.core :as ajax]
   [modular.config :refer [get-in-config config-atom]]))

(defn auth-header-basic [username password]
  {"Authorization" (str "Basic " (base64-encode (str username ":" password)))})
