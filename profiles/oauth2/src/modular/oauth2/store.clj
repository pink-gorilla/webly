(ns modular.oauth2.store
  (:require
   [taoensso.timbre :refer-macros [info error]]
   [modular.config :as config]
   [modular.persist.protocol :refer [save loadr]]
   [modular.persist.edn] ; side-effects
   ))

;; token storage 

(defn filename-token  [name]
  (when-let [token-path (config/get-in-config [:oauth2 :token-path])]
    (str token-path name ".edn")))

(defn save-token
  [name data]
  (let [filename (filename-token name)]
    (if filename
      (save :edn filename data)
      (error "cannot save token - please set [:oauth2 :token-path] in modular config"))))

(defn load-token [name]
  (let [filename (filename-token name)]
    (if filename
      (loadr :edn filename)
      (do
        (error "cannot load token - please set [:oauth2 :token-path] in modular config")
        nil))))
